package testSensorReadings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import sensorReadings.MainTwo;
import sensorReadings.Neighbour;

public class Test {

	public static void main(String[] args) {
//		String mj1 = "26,991,59,,277,,";
//		String sendString = mj1.split(Measurment.VECTOR_DELIMITER)[MainTwo.CO_POSITION];
//		if (sendString.equals("")) sendString = "0";
//		System.out.println(sendString);
		
		MainTwo main = new MainTwo();
		File neighboursFile = new File(MainTwo.NEIGHBOURS_FILE_PATH);
		List<Neighbour> neighbours = new LinkedList<Neighbour>();
		
		JSONParser jsonParser = new JSONParser();
		try (FileReader reader = new FileReader(neighboursFile)){
			Object obj = jsonParser.parse(reader);
			JSONArray sensorList = (JSONArray) obj;
			for(Object o: sensorList){
			    if ( o instanceof JSONObject ) {
			        neighbours.add(parse((JSONObject)o));
			    }
			}
			for (Neighbour n : neighbours) {
				System.out.println(n.toString());
			}
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		
		try {
			String jsonData = new String(Files.readAllBytes(neighboursFile.toPath()));
			System.out.println(jsonData);
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		
		Neighbour n1 = new Neighbour("localhost", 54444, 0);
		Neighbour n2 = new Neighbour("localhost", 55555, 1);
		
		JSONObject s1 = new JSONObject();
		s1.put("ip", n1.getIp());
		s1.put("port", n1.getPort());
		
		JSONObject s2 = new JSONObject();
		s2.put("ip", n2.getIp());
		s2.put("port", n2.getPort());
		
		JSONArray sensorList = new JSONArray();
		sensorList.add(s1);
		sensorList.add(s2);
		
		try (FileWriter file = new FileWriter(neighboursFile, false)) {
			 
            file.write(sensorList.toJSONString());
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		try {
			String jsonData = new String(Files.readAllBytes(neighboursFile.toPath()));
			System.out.println(jsonData);
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
	}

	private static Neighbour parse(JSONObject o) {
		String ip = (String) o.get("ip");
		long port = (long) o.get("port");
		int position = (int) o.get("position");
		Neighbour n = new Neighbour(ip, port, position);
		return n;
	}

}
