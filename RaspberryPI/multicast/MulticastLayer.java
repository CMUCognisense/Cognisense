package multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.crypto.Mac;




/**
 * @author parth
 *
 */
public class MulticastLayer {

	private String deviceID = null;
	private String currentHostIpAddress = null;
	private int TIMEOUT = 200;
	private int MAX_RETRIES;
	HashMap<Integer, HashSet<String>> replyBuckets;
	public boolean DEBUG;
	private SQLiteJDBC db;
	
	public String getDeviceID() {
		return deviceID;
	}
	
	Random rndNumbers;
	private HashMap<String, Integer> requestMap;
	//TODO the size of the hash map needs to be limited
	public MulticastLayer(boolean bool) {
		rndNumbers = new Random(System.currentTimeMillis());
		replyBuckets = new HashMap<Integer, HashSet<String>>();
		requestMap = new HashMap<String, Integer>();
		DEBUG = bool;
		getCurrentEnvironmentNetworkIp();
		db = new SQLiteJDBC();
		setMaxRetries();
		new Thread(new RecvThread()).start();

	}

	private void setMaxRetries() {
		// read from file and set the initial value of the retries
		// TODO 
		MAX_RETRIES = db.query().size();
		if(MAX_RETRIES < 6)
			MAX_RETRIES = 6;
	}

	/**
	 * public method to send a "Message" to all the devices that are to be 
	 * addressed by the same receive message by them implementing an onReceive 
	 * method. This is a blocking call and will take a considerable amount of time to complete. 
	 * Time could vary according to the timeout and the retries used. 
	 * @param MAX_RETRIES
	 * @param TIMEOUT
	 * @param Message
	 * @return
	 */
	public void sendAll(String Message) {
		new Thread(new Sender(Message)).start();
	}
	
	private class Sender implements Runnable {

		String Message;
		int threadSeqNum;
		public Sender(String Message) {
			threadSeqNum = rndNumbers.nextInt();
			this.Message = Message;
		}
		
		@Override
		public void run() {
		
			int retries = 0;
			if(DEBUG)System.out.println("Starting Multicast");
			HashSet<String> set = new HashSet<String>();
			synchronized (replyBuckets) {
				replyBuckets.put(threadSeqNum, set);
				if(DEBUG)System.out.println(replyBuckets.toString());
			}
			

			while(retries < MAX_RETRIES)
			{
				if (!sendPacket("REQ#"+threadSeqNum+"#"+Message))
					if(DEBUG)System.out.println("Couldnot send packet");
				try {
					Thread.sleep(TIMEOUT);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				retries++;

			}
			
			HashSet<String> ret;
			synchronized (replyBuckets) {
				ret = replyBuckets.get(threadSeqNum);
				replyBuckets.remove(threadSeqNum);
				if(DEBUG)System.out.println("Removing entry for send:" + threadSeqNum);
			}
			
			HashSet<String> prevDevices = db.query();
			if(DEBUG)System.out.println("Prev Devices: "+prevDevices.toString());
			Iterator<String> itr = ret.iterator();
			
			while(itr.hasNext())
			{
				String macAddress = itr.next();
				if(!prevDevices.contains(macAddress))
				{
					if(DEBUG)System.out.println("Inserting "+macAddress);
						db.insert(macAddress);
				}
			}
			
			MAX_RETRIES = db.query().size();
			if(MAX_RETRIES < 6)
				MAX_RETRIES = 6;
			
			if(DEBUG)System.out.println("The ACKS are: "+ret.toString());
			if(DEBUG)System.out.println("The MAX_RETRIES is set to:"+MAX_RETRIES);
		}
		
	}
	
	/**
	 * Private method to send a packet on broadcast address
	 * @param sentence
	 * @return
	 */
	private boolean sendPacket(String sentence) {
		try {
			InetAddress IPAddress = InetAddress.getByAddress(new byte[] {
					(byte) 255, (byte) 255, (byte) 255, (byte) 255 });
			byte[] sendData = sentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData,
					sendData.length, IPAddress, 9876);
			if(DEBUG)System.out.println("Send " + sentence + " to"
					+ sendPacket.getAddress().getHostAddress() + ":" + sendPacket.getPort());
			DatagramSocket sendSocket;

			sendSocket = new DatagramSocket();
			sendSocket.setReuseAddress(true);
			sendSocket.send(sendPacket);
			sendSocket.close();
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	/**
	 * This method sends packet to a specific address
	 * the is given in the argument. 
	 * @param sentence
	 * @param IPAddress
	 * @return
	 */
	private boolean sendPacket(String sentence, InetAddress IPAddress)
	{
		try {
			byte[] sendData = sentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
			if(DEBUG)System.out.println("Send "+sentence + " to" +sendPacket.getAddress().getHostAddress()+ ":"+sendPacket.getPort());
			DatagramSocket sendSocket;

			sendSocket = new DatagramSocket();
			sendSocket.send(sendPacket);
			sendSocket.close();
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private List<MulticastReceive> _listeners = new ArrayList<MulticastReceive>();
	public synchronized void addEventListener(MulticastReceive listener)  {
		_listeners.add(listener);
	}
	public synchronized void removeEventListener(MulticastReceive listener)   {
		_listeners.remove(listener);
	}

	// call this method whenever you want to notify
	//the event listeners of the particular event
	private class FireEvent implements Runnable {

		String message;
		
		public FireEvent(String message) {
			this.message = message;
		}
		
		@Override
		public void run() {
			
			RecvMessageEvent event = new RecvMessageEvent(this,message);
			if(DEBUG) System.out.println("In Fire event list size:"+_listeners.size());
			Iterator<MulticastReceive> i = _listeners.iterator();
			while(i.hasNext())  {
				if(DEBUG) System.out.println("In Fire event loop");
				((MulticastReceive) i.next()).onReceiveMessage(event);
			}
			
		}
		
	}


	public String getCurrentEnvironmentNetworkIp() {
		if (currentHostIpAddress == null) {
			Enumeration<NetworkInterface> netInterfaces = null;
			try {
				netInterfaces = NetworkInterface.getNetworkInterfaces();

				while (netInterfaces.hasMoreElements()) {
					NetworkInterface ni = netInterfaces.nextElement();
					Enumeration<InetAddress> address = ni.getInetAddresses();
					while (address.hasMoreElements()) {
						InetAddress addr = address.nextElement();
						if (!addr.isLoopbackAddress()
								&& addr.isSiteLocalAddress()
								&& !(addr.getHostAddress().indexOf(":") > -1)) {
							currentHostIpAddress = addr.getHostAddress();
							byte[] mac = ni.getHardwareAddress();



							StringBuilder sb = new StringBuilder();
							for (int i = 0; i < mac.length; i++) {
								sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
							}
							deviceID = sb.toString();
							break;
						}
					}
				}
				if (currentHostIpAddress == null) {
					currentHostIpAddress = "127.0.0.1";
				}

			} catch (SocketException e) {
				// log.error("Somehow we have a socket error acquiring the host IP... Using loopback instead...");
				currentHostIpAddress = "127.0.0.1";
			}
		}
		return currentHostIpAddress;
	}

	/**
	 * This is the thread that receives packets all the time
	 * any other thread will communicate with this thread using
	 * the hashmap of reply buckets by adding in a sequence number
	 * and a hashset to store the replies in.
	 * any 
	 * @author parth
	 *
	 */
	private class RecvThread implements Runnable {

		@Override
		public void run() {
			try {
				byte[] receiveData = new byte[1024];
				DatagramSocket socket;

				socket = new DatagramSocket(9876);


				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				if(DEBUG)System.out.println("Listening on :" + currentHostIpAddress
						+ ":9876");
				while(true)
				{
					socket.receive(receivePacket);

					// print out the message
					String receiveMsg = new String(receivePacket.getData(),0,receivePacket.getLength());
					InetAddress IPAddress = receivePacket.getAddress();
					int port = receivePacket.getPort();

					// if message is sent by me only do not bother
					if(IPAddress.getHostAddress().equals(currentHostIpAddress))		
					{
						System.out.println("From me continue");continue;
					}

					if(DEBUG)System.out.println("RECEIVED: "+receiveMsg+" IP:"+ IPAddress.getHostAddress() + ":" + port );

					String[] headers = receiveMsg.split("#");
					// if message has REQ then call upper layer listner and get the reply message.
					if(headers[0].equals("REQ"))
					{
						// get reply message from upper layer if you do not have one. 
						String seqNumIpAdd = headers[1] + IPAddress.getHostAddress();
						if(!requestMap.containsKey(seqNumIpAdd))
						{
							if(DEBUG)System.out.println("Reply is not cached");
							new Thread(new FireEvent(headers[2])).start();
							requestMap.put(seqNumIpAdd, 1);
							
						}
						else 
							if(DEBUG)System.out.println("Reply is cached so do not bother upper layer");
						
						sendPacket("REP#"+headers[1]+"#"+deviceID,IPAddress);
						
					}
					// if message has REP then put in the reply bucket 
					else if(headers[0].equals("REP"))
					{
						HashSet<String> replyBucket;
						synchronized (replyBuckets) {
							replyBucket= replyBuckets.get(Integer.parseInt(headers[1]));
							if(DEBUG)System.out.println(replyBuckets.toString());
						}
						
						if(replyBucket == null)
							continue;
						else
							replyBucket.add(headers[2]);
					}



				}
			} catch (SocketException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}

	}
}
