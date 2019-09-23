package sensorReadings;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements Runnable {

//	static final int PORT = 10002; // server port
	
	static List<String> measurments = new ArrayList<String>();
	static List<Neighbour> neighbours = new ArrayList<Neighbour>();
	private int port;
	
	public Client(List<String> measurments, List<Neighbour> neighbours, EmulatedSystemClock clock, int port) {
		Client.measurments = measurments;
		Client.neighbours = neighbours;
		this.port = port;
	}

	public void run() {
		String sendString = "Any second string...";

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

        System.out.print("Client (" + PORT + ") sends: ");
        // send each character as a separate datagram packet
        for (int i = 0; i < sendString.length(); i++) {
            byte[] sendBuf = new byte[1];// sent bytes
            sendBuf[0] = (byte) sendString.charAt(i);

            // create a datagram packet for sending data
            DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length,
                    address, PORT);

            // send a datagram packet from this socket
            try {
				socket.send(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //SENDTO
            System.out.print(new String(sendBuf));
        }

        StringBuffer receiveString = new StringBuffer();

        while (true) {
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

        }
        System.out.println("Client (" + PORT + ") received: " + receiveString);

        // close the datagram socket
        socket.close(); //CLOSE
	}

}
