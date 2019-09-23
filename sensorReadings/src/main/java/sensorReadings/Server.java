package sensorReadings;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server implements Runnable {
	
	static final int PORT = 10001; // server port
	
	public Server() {
		
	}

	public void run() {
		byte[] rcvBuf = new byte[256]; // received bytes
        byte[] sendBuf = new byte[256];// sent bytes
        String rcvStr;

        // create a UDP socket and bind it to the specified port on the local
        // host
        DatagramSocket socket = null;
		try {
			socket = new SimpleSimulatedDatagramSocket(PORT, 0.2, 1000);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //SOCKET -> BIND

        while (true) { //OBRADA ZAHTJEVA
            // create a DatagramPacket for receiving packets
            DatagramPacket packet = new DatagramPacket(rcvBuf, rcvBuf.length);

            // receive packet
            try {
				socket.receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //RECVFROM

            // construct a new String by decoding the specified subarray of
            // bytes
            // using the platform's default charset
            rcvStr = new String(packet.getData(), packet.getOffset(),
                    packet.getLength());
            System.out.println("Server (" + PORT + ") received: " + rcvStr);

            // encode a String into a sequence of bytes using the platform's
            // default charset
            sendBuf = rcvStr.toUpperCase().getBytes();
            System.out.println("Server (" + PORT + ") sends: " + rcvStr.toUpperCase());

            // create a DatagramPacket for sending packets
            DatagramPacket sendPacket = new DatagramPacket(sendBuf,
                    sendBuf.length, packet.getAddress(), packet.getPort());

            // send packet
            try {
				socket.send(sendPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //SENDTO
        }
	}

}
