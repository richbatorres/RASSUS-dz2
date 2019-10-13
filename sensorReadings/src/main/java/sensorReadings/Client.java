package sensorReadings;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements Runnable {

//	static final int PORT = 10002; // server port
	
//	static List<String> measurments = new ArrayList<String>();
//	static List<Neighbour> neighbours = new ArrayList<Neighbour>();
	private int port;
	private EmulatedSystemClock clock;
	
	public Client(/*List<String> measurments, List<Neighbour> neighbours,*/ EmulatedSystemClock clock, int port) {
		/*Client.measurments = measurments;
		Client.neighbours = neighbours;*/
		this.port = port;
		this.clock = clock;
	}

	public void run() {
		Map<Integer, Integer> vTimestamp = new LinkedHashMap<Integer, Integer>();
		for (Neighbour n : Main.neighbours) {
			vTimestamp.put(Math.toIntExact(n.getPort()), 0);
		}
		byte[] rcvBuf = new byte[256]; // received bytes

        // encode this String into a sequence of bytes using the platform's
        // default charset and store it into a new byte array

        // determine the IP address of a host, given the host's name
        InetAddress address = null;
		try {
			address = InetAddress.getByName("localhost");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        // create a datagram socket and bind it to any available
        // port on the local host
        //DatagramSocket socket = new SimulatedDatagramSocket(0.2, 1, 200, 50); //SOCKET
        DatagramSocket socket = null;
		try {
			socket = new SimpleSimulatedDatagramSocket(0.2, 1000);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} //SOCKET

        while (true) {
        	try {
        		Thread.sleep(1000);
        	} catch (InterruptedException e1) {
        		// TODO Auto-generated catch block
        		e1.printStackTrace();
        	}
        	int redniBroj = (int) (((this.clock.currentTimeMillis()/1000) % 100) + 2);
        	String sendString = Main.measurments.get(redniBroj);
        	sendString += Measurment.SECTION_DELIMITER + clock.currentTimeMillis() + Measurment.SECTION_DELIMITER;
        	Main.vTimestamp.replace(port, Main.vTimestamp.get(port)+1);
        	for (Map.Entry<Integer, Integer> entry : vTimestamp.entrySet()) {
        		sendString += entry.getValue() + Measurment.VECTOR_DELIMITER;
        	}
        	sendString = sendString.substring(0, sendString.length()-1);

        	System.out.print("Client (" + port + ") sends: ");
        	// send each character as a separate datagram packet
        	byte[] sendBuf = sendString.getBytes();// sent bytes

        	// create a datagram packet for sending data
        	DatagramPacket packet;
        	for (Neighbour n : Main.neighbours) {
        		if (n.getPort() != port) {
        			packet = new DatagramPacket(sendBuf, sendBuf.length, address, Math.toIntExact(n.getPort()));
        			// send a datagram packet from this socket
        			try {
        				socket.send(packet);
        			} catch (IOException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			} //SENDTO
        		}
        	}
        	System.out.print(new String(sendBuf));

        	StringBuffer receiveString = new StringBuffer();
        	// create a datagram packet for receiving data
        	DatagramPacket rcvPacket = new DatagramPacket(rcvBuf, rcvBuf.length);
        	try {
        		// receive a datagram packet from this socket
        		socket.receive(rcvPacket); //RECVFROM
        	} catch (SocketTimeoutException e) {
        		break;
        	} catch (IOException ex) {
        		Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        	}
        	// construct a new String by decoding the specified subarray of bytes
        	// using the platform's default charset
        	receiveString.append(new String(rcvPacket.getData(), rcvPacket.getOffset(), rcvPacket.getLength()));
        	System.out.println("Client (" + port + ") received: " + receiveString);
		}
		// close the datagram socket
        socket.close(); //CLOSE
	}

}
