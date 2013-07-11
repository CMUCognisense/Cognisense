import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import servicediscovery.Message;
import servicediscovery.ServiceDiscoveryLayer;

public class DoorBellProxy_2 {
	static String serviceId;
	static ServiceDiscoveryLayer sdl;
	static Socket clientSocket = null;

	public DoorBellProxy_2() {

		sdl = new ServiceDiscoveryLayer(true, false);

	}

	public static void main(String[] args) throws Exception {
		DoorBellProxy_2 mainObj = new DoorBellProxy_2();

		String ipPortnumner = mainObj.getArduinoAddress();
		String arduinoIpaddress = ipPortnumner.split(":")[0];
		Integer arduinoPortNum = Integer.valueOf(ipPortnumner.split(":")[1]);
		System.out.println("The Ip ans port number is: " + ipPortnumner
				+ arduinoIpaddress + arduinoPortNum);
		try {
			clientSocket = new Socket(InetAddress.getByName(arduinoIpaddress),
					arduinoPortNum);
			System.out.println("Connected to arduino\n");
			BufferedReader inFromServer = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));

			while (true) {
				while (clientSocket.getInputStream().read() > 0) {
					String modifiedSentence = inFromServer.readLine();
					System.out.println("FROM SERVER: " + modifiedSentence);
					sdl.registerApp(mainObj);
					serviceId = sdl.registerNewService("DoorbellProxy");
					sdl.addLocationProperty(serviceId);
					sdl.addLocationValue(serviceId, "MyHome", "one", "Bedroom",
							null, "nearWindow");
					sendMessageIndicateDoorbellOn();
				}
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			if (clientSocket.isOutputShutdown())
				e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void sendMessageIndicateDoorbellOn() {

		System.out
				.println("*************Send a DoorBell Message****************!");
		Message message = new Message(serviceId);
		message.addServiceType("LEDConfigurationService");
		message.addTrigger("onDoorbell");
		sdl.sendMessage(message);
	}

	public String getArduinoAddress() {
		byte[] receiveData = new byte[1024];
		DatagramSocket socket;

		try {
			socket = new DatagramSocket(5005);

			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			System.out.println("Listening on : 5005 for DoorbellArduino");

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

}