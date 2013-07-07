import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import servicediscovery.ServiceDiscoveryLayer;

public class LEDNotificationProxy {
	static String serviceId;
	static ServiceDiscoveryLayer sdl;
	static Socket clientSocket = null;

	public LEDNotificationProxy() {

		sdl = new ServiceDiscoveryLayer(true, false);

	}

	public static void main(String[] args) throws Exception {
		LEDNotificationProxy mainObj = new LEDNotificationProxy();

		String ipPortnumner = mainObj.getArduinoAddress();
		String arduinoIpaddress = ipPortnumner.split(":")[0];
		Integer arduinoPortNum = Integer.valueOf(ipPortnumner.split(":")[1]);
		System.out.println("The Ip ans port number is: " + ipPortnumner
				+ arduinoIpaddress + arduinoPortNum);
		try {
			clientSocket = new Socket(InetAddress.getByName(arduinoIpaddress),
					arduinoPortNum);
			System.out.println("Connected to arduino\n");

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			if (clientSocket.isOutputShutdown())
				e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		sdl.registerApp(mainObj);
		serviceId = sdl.registerNewService("LED");
		sdl.addLocationProperty(serviceId);
		sdl.addLocationValue(serviceId, "MyHome", "one", "Bedroom", null,
				"nearWindow");
		sdl.registerActions(serviceId, "TURNON", "TURNON",
				LEDNotificationProxy.class);
		sdl.registerActions(serviceId, "TURNOFF", "TURNOFF",
				LEDNotificationProxy.class);

	}

	public String getArduinoAddress() {
		byte[] receiveData = new byte[1024];
		DatagramSocket socket;

		try {
			socket = new DatagramSocket(5005);

			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			System.out.println("Listening on : 5005 for arduino");

			socket.receive(receivePacket);
			String receiveMsg = new String(receivePacket.getData(), 0,
					receivePacket.getLength());
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();
			System.out.println("RECEIVED: " + receiveMsg + " IP:"
					+ IPAddress.getHostAddress() + ":" + port);
			return IPAddress.getHostAddress() + ":" + port;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	public void TURNON(Object actionInput, Object srcServiceId) {
		try {
			DataOutputStream outToServer = new DataOutputStream(
					clientSocket.getOutputStream());

			outToServer.writeBytes("TURNON");

		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	public void TURNOFF(Object actionInput, Object srcServiceId) {
		try {
			DataOutputStream outToServer = new DataOutputStream(
					clientSocket.getOutputStream());

			outToServer.writeBytes("TURNOF");

		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

}