package sensorReadings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import sensorReadings.Neighbour;

public class Test {

	public static void main(String[] args) {
		
		String fileName = "neighbours.json";
		 
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		 
		File file = new File("C:\\Users\\ebrctnx\\OneDrive - fer.hr\\kolegiji\\RASSUS\\DZ\\DZ2\\RASSUS-dz2\\sensorReadings\\target\\classes\\neighbours.json");
		
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			//System.out.println(file.toPath());
			//read json file data to String
			String jsonData = new String(Files.readAllBytes(file.toPath()));
			System.out.println(jsonData.toString());
			List<Neighbour> neighbours = objectMapper.readValue(jsonData, new TypeReference<List<Neighbour>>(){});
			Neighbour ne = new Neighbour("localhost", 53333);
			neighbours.add(ne);
			String newJsonData = "[";
			for (Neighbour n : neighbours) {
				newJsonData += n.toString();
			}
			newJsonData = newJsonData.substring(0, newJsonData.length()-2);
			newJsonData += "]";
//			System.out.println(newJsonData);
			FileWriter fileWriter = new FileWriter(file);
		    fileWriter.write(newJsonData);
		    fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
				
//		JsonNode rootNode = objectMapper.readTree(jsonData);
//		Iterator<JsonNode> elements = rootNode.elements();
//		while(elements.hasNext()){
//			JsonNode phone = elements.next();
//			Neighbour neighbour = phone.as
//		}

	}

}
