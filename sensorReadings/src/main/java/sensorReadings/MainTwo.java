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
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MainTwo {
	
	private boolean run = true;
	private Map<Integer, Integer> vTimestamp = new LinkedHashMap<Integer, Integer>();
	private List<Integer> confirmations = new ArrayList<Integer>();
	public List<Neighbour> neighbours = new ArrayList<Neighbour>();
	static List<String> measurmentsGenerator = new ArrayList<String>();
	private List<Measurment> rcvMeasurments = new ArrayList<Measurment>();
	private static String ip;
	private int port;
	EmulatedSystemClock clock;
//	private int positionInVTimestamp;
	
	public final static int CO_POSITION = 3;
	
	public final static String NEIGHBOURS_FILE_PATH = "D:\\Documents\\kolegiji"
			+ "\\RASSUS\\DZ\\DZ2\\RASSUS-dz2\\sensorReadings\\src\\main\\resources\\neighbours.json";
	public final static String MEASURMENTS_FILE_PATH = "D:\\Documents\\kolegiji"
			+ "\\RASSUS\\DZ\\DZ2\\RASSUS-dz2\\sensorReadings\\src\\main\\resources\\mjerenja.csv";
//	public final static String NEIGHBOURS_FILE_PATH = "C:\\Users\\ebrctnx\\OneDrive - fer.hr\\kolegiji"
//			+ "\\RASSUS\\DZ\\DZ2\\RASSUS-dz2\\sensorReadings\\src\\main\\resources\\neighbours.json";
//	public final static String MEASURMENTS_FILE_PATH = "C:\\Users\\ebrctnx\\OneDrive - fer.hr\\kolegiji"
//			+ "\\RASSUS\\DZ\\DZ2\\RASSUS-dz2\\sensorReadings\\src\\main\\resources\\mjerenja.csv";

	public static void main(String[] args) {		
		MainTwo main = new MainTwo();
		main.clock = new EmulatedSystemClock();
		File neighboursFile = new File(NEIGHBOURS_FILE_PATH); 
		File measurmentsFile = new File(MEASURMENTS_FILE_PATH);
		
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
	    updateNeighbours(main.neighbours, neighboursFile, main.vTimestamp);
	    Neighbour sensor = new Neighbour(ip, main.port, main.neighbours.size());
	    
	    try {
			addSensor(sensor, main.neighbours, neighboursFile);
		} catch (ParseException e1) {
			
			e1.printStackTrace();
		}	    
	    
//		main.positionInVTimestamp = main.neighbours.size()-1;
		for (Neighbour n : main.neighbours) {
			if (!main.vTimestamp.containsKey(n.getPositionInVTimestamp()))
				main.vTimestamp.put(n.getPositionInVTimestamp(), 0);
		}
		MainTwo.Server server = main.new Server();
		Thread t1 = new Thread(server, "server");
		t1.start();
		MainTwo.Client client = main.new Client();
		Thread t2 = new Thread(client, "client");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		t2.start();
		
		while (true) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			updateNeighbours(main.neighbours, neighboursFile, main.vTimestamp);
			
			for (Neighbour n : main.neighbours) {
				if (n.getPort() == main.port) {
					printSortedMeasurments(main.rcvMeasurments, n.getPositionInVTimestamp());
					break;
				}
			}
//			printSortedMeasurments(main.rcvMeasurments, sensor.getPositionInVTimestamp());
			printAverageValues(main.rcvMeasurments);
			main.rcvMeasurments.clear();
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

				String sendString = prepareSendingString(port);

//				System.out.println(clock.currentTimeMillis()/1000 + ": Client (" + port + ") sends: ");
				byte[] sendBuf = sendString.getBytes();// sent bytes

				// create a datagram packet for sending data
				DatagramPacket packet;
				for (Neighbour n : neighbours) {
					if (n.getPort() != port) {
						packet = new DatagramPacket(sendBuf, sendBuf.length, address, Math.toIntExact(n.getPort()));
						// send a datagram packet from this socket
						try {
//							System.out.println(clock.currentTimeMillis()/1000 + ": Client (" + port + ") sends to "
//									+ packet.getPort() + ": " + new String(packet.getData()));
							socket.send(packet);
						} catch (IOException e) {

							e.printStackTrace();
						} //SENDTO
					}
				}
				//System.out.println(new String(sendBuf));
				for (int i = 0; i < 10; i++) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
					for (Neighbour n : neighbours) {
						if (n.getPort() != port) {
							if (!confirmations.contains(Math.toIntExact(n.getPort()))) {
								packet = new DatagramPacket(sendBuf, sendBuf.length, address, Math.toIntExact(n.getPort()));
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
//	            System.out.println(clock.currentTimeMillis()/1000 + ": Server (" + port + ") received: " + rcvStr);
	            
	            if (rcvStr.matches(PORT_REGEX)) {
	            	confirmations.add(Integer.parseInt(rcvStr));
	            }else {
	            	Measurment measurment = new Measurment(rcvStr);
//	            	for (Neighbour n : neighbours) {
//						if (n.getPort() == port) {
//			            	measurment.getvTimestamp().set(n.getPositionInVTimestamp(),
//			            			measurment.getvTimestamp().get(n.getPositionInVTimestamp()) + 1);
//							break;
//						}
//					}
	            	if (!rcvMeasurments.contains(measurment)) {						
						for (Neighbour n : neighbours) {
							if (n.getPort() == port) {
								vTimestamp.replace(n.getPositionInVTimestamp(), 
										vTimestamp.get(n.getPositionInVTimestamp()) + 1);
								if (measurment.getvTimestamp().size() > n.getPositionInVTimestamp()) {
									measurment.getvTimestamp().set(n.getPositionInVTimestamp(),
											vTimestamp.get(n.getPositionInVTimestamp()));
									rcvMeasurments.add(measurment);
								}
								break;
							}
						}
						
						//confirmations.add(port);
						// encode a String into a sequence of bytes using the platform's
						// default charset
						sendBuf = Integer.toString(port).getBytes();
//						System.out.println(clock.currentTimeMillis()/1000 + 
//								": Server (" + port + ") sends: " + new String(sendBuf));
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
	
	public static void addSensor(Neighbour sensor, List<Neighbour> neighbours, File neighboursFile) throws ParseException {
		JSONParser jsonParser = new JSONParser();
		JSONArray sensorList = null;
		//updateNeighbours(neighbours, neighboursFile);
		try (FileReader reader = new FileReader(neighboursFile)){
			Object obj = jsonParser.parse(reader);
			sensorList = (JSONArray) obj;
			neighbours.add(sensor);
			JSONObject jo = new JSONObject();
			jo.put("ip", sensor.getIp());
			jo.put("port", sensor.getPort());
			jo.put("position", sensor.getPositionInVTimestamp());
			sensorList.add(jo);						
		} catch (IOException e) {
			e.printStackTrace();
		}try(FileWriter file = new FileWriter(neighboursFile, false)){
			file.write(sensorList.toJSONString());
            file.flush();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	private static void updateNeighbours(List<Neighbour> neighbours, File neighboursFile, Map<Integer, Integer> vTimestamp) {
		JSONParser jsonParser = new JSONParser();
		try (FileReader reader = new FileReader(neighboursFile)){
			Object obj = jsonParser.parse(reader);
			JSONArray sensorList = (JSONArray) obj;
			for(Object o: sensorList){
			    if ( o instanceof JSONObject ) {
			        Neighbour neighbour = parse((JSONObject)o);
			        if (!neighbours.contains(neighbour)) neighbours.add(neighbour);
			    }
			}	
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		for (Neighbour n : neighbours) {
			if (!vTimestamp.containsKey(n.getPositionInVTimestamp())) 
				vTimestamp.put(n.getPositionInVTimestamp(), 0);
		}
	}

	public String prepareSendingString(int port) {
		int redniBroj = (int) (((clock.currentTimeMillis()/1000) % 100) + 1);
    	String sendString = MainTwo.measurmentsGenerator.get(redniBroj).split(Measurment.VECTOR_DELIMITER)[CO_POSITION];
    	if (sendString.equals("")) sendString = "0";
    	sendString += Measurment.SECTION_DELIMITER + clock.currentTimeMillis() + Measurment.SECTION_DELIMITER;
    	for (Neighbour n : neighbours) {
			if (((Long)n.getPort()).intValue() == port) {
				vTimestamp.replace(n.getPositionInVTimestamp(), 
						vTimestamp.get(n.getPositionInVTimestamp()) + 1);
				break;
			}
		}
    	for (Neighbour n : neighbours) {
			if (!vTimestamp.containsKey(n.getPositionInVTimestamp())) 
				vTimestamp.put(n.getPositionInVTimestamp(), 0);
		}
    	for (Map.Entry<Integer, Integer> entry : vTimestamp.entrySet()) {
    		sendString += entry.getValue() + Measurment.VECTOR_DELIMITER;
    	}
    	sendString = sendString.substring(0, sendString.length()-1);
    	return sendString;
	}
	


	private static Neighbour parse(JSONObject o) {
		String ip = (String) o.get("ip");
		long port = (long) o.get("port");
		int positionInVTimestamp = ((Long)o.get("position")).intValue();
		Neighbour n = new Neighbour(ip, port, positionInVTimestamp);
		return n;
	}
	
	private static void printSortedMeasurments(List<Measurment> rcvMeasurments, int positionInVTimestamp) {
		System.out.println("Sorted measurments by scalar values:");
		Collections.sort(rcvMeasurments, new SortByScalar());
		for (Measurment m : rcvMeasurments) {
			System.out.println(m.toString());
		}
		System.out.println("\n------------------------------------------");
		System.out.println("Sorted measurments by vector values:");
//		for (Measurment m : rcvMeasurments) {
//			if (m.getvTimestamp().size() <= positionInVTimestamp) rcvMeasurments.remove(m);
//		}
		Collections.sort(rcvMeasurments, new SortByVector(positionInVTimestamp));
		for (Measurment m : rcvMeasurments) {
			System.out.println(m.toString());
		}
		System.out.println("\n------------------------------------------");
	}

	private static void printAverageValues(List<Measurment> rcvMeasurments) {
//		List<Integer> finalValues = new ArrayList<Integer>();
		int finalValue = 0;
		int antiZeros = 0;
		for (Measurment m : rcvMeasurments) {
			if (m.getValue() != 0) {
				finalValue += m.getValue();
				antiZeros++;
			}
		}
		System.out.println("Average values from the last 5 seconds are:");
		if (antiZeros != 0) finalValue = finalValue / antiZeros;
			System.out.println(finalValue);
		System.out.println("\n------------------------------------------");
		System.out.println("\n------------------------------------------");
		System.out.println("\n------------------------------------------");
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
