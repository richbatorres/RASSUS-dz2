package sensorReadings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.text.Position;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MainTwo {
	
	private boolean run = true;
	private Map<Integer, Integer> vTimestamp = new LinkedHashMap<Integer, Integer>();
	private List<Integer> confirmations = new ArrayList<Integer>();
	static List<Neighbour> neighbours = new ArrayList<Neighbour>();
	static List<String> measurmentsGenerator = new ArrayList<String>();
	private List<Measurment> rcvMeasurments = new ArrayList<Measurment>();
	private static String ip;
	private int port;
	EmulatedSystemClock clock;
	private int positionInVTimestamp;

	public static void main(String[] args) {		
		MainTwo main = new MainTwo();
		main.clock = new EmulatedSystemClock();
		//File neighboursFile = new File("C:\\Users\\ebrctnx\\OneDrive - fer.hr\\kolegiji\\RASSUS\\DZ\\DZ2\\RASSUS-dz2\\sensorReadings\\target\\classes\\neighbours.json"); 
		//File measurmentsFile = new File("C:\\Users\\ebrctnx\\OneDrive - fer.hr\\kolegiji\\RASSUS\\DZ\\DZ2\\RASSUS-dz2\\sensorReadings\\target\\classes\\mjerenja.csv");
		File neighboursFile = main.getFileFromResources("neighbours.json");
		File measurmentsFile = main.getFileFromResources("mjerenja.csv");
		
		try (BufferedReader br = new BufferedReader(new FileReader(measurmentsFile))){
			String line;
			while ((line = br.readLine()) != null) {
				measurmentsGenerator.add(line);
				//System.out.println(line);
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		Scanner scan = new Scanner(System.in);  // Create a Scanner object
	    System.out.println("Enter port:");
	    main.setPort(scan.nextInt()); 
	    scan.close();
	    ip = "localhost";
	    
	    ObjectMapper objectMapper = new ObjectMapper();
		try {
			String jsonData = new String(Files.readAllBytes(neighboursFile.toPath()));
			//System.out.println(jsonData.toString());
			neighbours = objectMapper.readValue(jsonData, new TypeReference<List<Neighbour>>(){});
			Neighbour thisSensor = new Neighbour(ip, main.getPort());
			//addSensor(thisSensor, neighboursFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		main.positionInVTimestamp = neighbours.size();
		
		MainTwo.Server server = main.new Server();
		Thread t1 = new Thread(server, "t1");
		t1.start();
		MainTwo.Client client = main.new Client();
		Thread t2 = new Thread(client, "t2");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		t2.start();
		
		while (true) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			printSortedMeasurments();
		}
		
	}

	public class Client implements Runnable {

		//		public Client(EmulatedSystemClock clock) {
		//			this.clock = clock;
		//		}

		public void run(){
			confirmations.clear();
			InetAddress address = null;
			try {
				address = InetAddress.getByName("localhost");
			} catch (UnknownHostException e2) {

				e2.printStackTrace();
			}

			// create a datagram socket and bind it to any available
			// port on the local host
			//DatagramSocket socket = new SimulatedDatagramSocket(0.2, 1, 200, 50); //SOCKET
			DatagramSocket socket = null;
			try {
				socket = new SimpleSimulatedDatagramSocket(0.2, 1000);
			} catch (SocketException e1) {

				e1.printStackTrace();
			} catch (IllegalArgumentException e1) {

				e1.printStackTrace();
			} //SOCKET

			while (run) {

				String sendString = prepareSendingString();

				System.out.print("Client (" + port + ") sends: ");
				// send each character as a separate datagram packet
				byte[] sendBuf = sendString.getBytes();// sent bytes

				// create a datagram packet for sending data
				DatagramPacket packet;
				for (Neighbour n : MainTwo.neighbours) {
					if (n.getPort() != port) {
						packet = new DatagramPacket(sendBuf, sendBuf.length, address, n.getPort());
						// send a datagram packet from this socket
						try {
							socket.send(packet);
						} catch (IOException e) {

							e.printStackTrace();
						} //SENDTO
					}
				}
				System.out.print(new String(sendBuf));
				for (int i = 0; i < 10; i++) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
					for (Neighbour n : neighbours) {
						if (n.getPort() != port) {
							if (!confirmations.contains(n.getPort())) {
								packet = new DatagramPacket(sendBuf, sendBuf.length, address, n.getPort());
								// send a datagram packet from this socket
								try {
									socket.send(packet);
								} catch (IOException e) {
									e.printStackTrace();
								}
							} 
						}
					} 
				}

				//	            	StringBuffer receiveString = new StringBuffer();
				//	            	// create a datagram packet for receiving data
				//	            	DatagramPacket rcvPacket = new DatagramPacket(rcvBuf, rcvBuf.length);
				//	            	socket.set
				//	            	try {
				//	            		// receive a datagram packet from this socket
				//	            		socket.receive(rcvPacket); //RECVFROM
				//	            	} catch (SocketTimeoutException e) {
				//	            		break;
				//	            	} catch (IOException ex) {
				//	            		Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
				//	            	}
				// construct a new String by decoding the specified subarray of bytes
				// using the platform's default charset
				//	            	receiveString.append(new String(rcvPacket.getData(), rcvPacket.getOffset(), rcvPacket.getLength()));
				//	            	System.out.println("Client (" + port + ") received: " + receiveString);
			}
		}
	}
	
	public class Server implements Runnable {
		
		private static final String PORT_REGEX = "^\\d*$";
		
		public Server() {}

		public void run() {
			byte[] rcvBuf = new byte[256]; // received bytes
	        byte[] sendBuf = new byte[256];// sent bytes
	        String rcvStr;

	        // create a UDP socket and bind it to the specified port on the local
	        // host
	        DatagramSocket socket = null;
			try {
				socket = new SimpleSimulatedDatagramSocket(port, 0.2, 1000);
			} catch (SocketException e) {
				
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				
				e.printStackTrace();
			} //SOCKET -> BIND

	        while (true) { //OBRADA ZAHTJEVA
	            // create a DatagramPacket for receiving packets
	            DatagramPacket packet = new DatagramPacket(rcvBuf, rcvBuf.length);

	            // receive packet
	            try {
					socket.receive(packet);
				} catch (IOException e) {
					
					e.printStackTrace();
				} //RECVFROM

	            // construct a new String by decoding the specified subarray of
	            // bytes
	            // using the platform's default charset
	            rcvStr = new String(packet.getData(), packet.getOffset(),
	                    packet.getLength());
	            System.out.println("Server (" + port + ") received: " + rcvStr);
	            
	            if (rcvStr.matches(PORT_REGEX)) {
	            	confirmations.add(Integer.parseInt(rcvStr));
	            }else {
	            	Measurment measurment = new Measurment(rcvStr);
	            	if (!rcvMeasurments.contains(measurment)) {
						rcvMeasurments.add(measurment);
						//confirmations.add(port);
						// encode a String into a sequence of bytes using the platform's
						// default charset
						sendBuf = Integer.toString(port).getBytes();
						System.out.println("Server (" + port + ") sends: " + sendBuf.toString());
						// create a DatagramPacket for sending packets
						DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, packet.getAddress(),
								packet.getPort());
						// send packet
						try {
							socket.send(sendPacket);
						} catch (IOException e) {

							e.printStackTrace();
						} //SENDTO
					}
	            }
	        }
		}

	}

	
	private File getFileFromResources(String fileName) {

        ClassLoader classLoader = getClass().getClassLoader();

        //String resource = "C:\\Users\\ebrctnx\\OneDrive - fer.hr\\kolegiji\\RASSUS\\DZ\\RASSUS-dz2\\sensorReadings\\target\\classes\\neighbours.json";
        URL resource = classLoader.getResource(fileName);
        return new File(resource.getFile());

    }
	
	public static void addSensor(Neighbour sensor, File neighboursFile) {
		try {
			neighbours.add(sensor);
			String newJsonData = "[";
			for (Neighbour n : neighbours) {
				newJsonData += n.toString();
			}
			newJsonData = newJsonData.substring(0, newJsonData.length()-2);
			newJsonData += "]";
			FileWriter fileWriter = new FileWriter(neighboursFile);
			fileWriter.write(newJsonData);
			fileWriter.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}		
	}
	
	public String prepareSendingString() {
		int redniBroj = (int) (((clock.currentTimeMillis()/1000) % 100) + 2);
    	String sendString = MainTwo.measurmentsGenerator.get(redniBroj);
    	sendString += Measurment.SECTION_DELIMITER + clock.currentTimeMillis() + Measurment.SECTION_DELIMITER;
    	vTimestamp.replace(port, vTimestamp.get(port)+1);
    	for (Map.Entry<Integer, Integer> entry : vTimestamp.entrySet()) {
    		sendString += entry.getValue() + Measurment.VECTOR_DELIMITER;
    	}
    	sendString = sendString.substring(0, sendString.length()-1);
    	return sendString;
	}
	
	private static void printSortedMeasurments() {
				
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
