package sensorReadings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import sensorReadings.Neighbour;

public class Test {

	public static void main(String[] args) {
		
		String fileName = "neighbours.json";
		 
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		 
		File file = new File(classLoader.getResource(fileName).getFile());
		
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			System.out.println(file.toPath());
			//read json file data to String
			byte[] jsonData = Files.readAllBytes(file.toPath());
			System.out.println(jsonData);
			List<Neighbour> neighbours = Arrays.asList(objectMapper.readValue(jsonData, Neighbour[].class));
			for (Neighbour n : neighbours) {
				System.out.println(n.getPort());
			}
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
