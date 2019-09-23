package sensorReadings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
	
	static List<Neighbour> neighbours = new ArrayList<Neighbour>();
	static List<String> measurments = new ArrayList<String>();
	private static String ip;
	private static int port;

	public static void main(String[] args) {
		
		EmulatedSystemClock clock = new EmulatedSystemClock();
		
//		String fileName = "neighbours.json";
//		 
//		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		//File file = new File(classLoader.getResource(fileName).getFile());
		File neighboursFile = new File("C:\\Users\\ebrctnx\\OneDrive - fer.hr\\kolegiji\\RASSUS\\DZ\\DZ2\\RASSUS-dz2\\sensorReadings\\target\\classes\\neighbours.json"); 
		File measurmentsFile = new File("C:\\Users\\ebrctnx\\OneDrive - fer.hr\\kolegiji\\RASSUS\\DZ\\DZ2\\RASSUS-dz2\\sensorReadings\\target\\classes\\mjerenja.csv");
		try (BufferedReader br = new BufferedReader(new FileReader(measurmentsFile))){
			String line;
			while ((line = br.readLine()) != null) {
				measurments.add(line);
				System.out.println(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Scanner scan = new Scanner(System.in);  // Create a Scanner object
	    System.out.println("Enter port:");
	    port = scan.nextInt();
	    scan.close();
	    ip = "localhost";
		
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String jsonData = new String(Files.readAllBytes(neighboursFile.toPath()));
			//System.out.println(jsonData.toString());
			neighbours = objectMapper.readValue(jsonData, new TypeReference<List<Neighbour>>(){});
			Neighbour thisSensor = new Neighbour(ip, port);
			//addSensor(thisSensor, neighboursFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		Server server = new Server();
		Thread t1 = new Thread(server, "t1");
		t1.start();
		Client client = new Client(measurments, neighbours, clock);
		Thread t2 = new Thread(client, "t2");
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t2.start();
	}
	
	private File getFileFromResources(String fileName) {

        ClassLoader classLoader = getClass().getClassLoader();

        String resource = "C:\\Users\\ebrctnx\\OneDrive - fer.hr\\kolegiji\\RASSUS\\DZ\\RASSUS-dz2\\sensorReadings\\target\\classes\\neighbours.json";
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource);
        }

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
