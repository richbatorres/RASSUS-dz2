package testSensorReadings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import sensorReadings.MainTwo;
import sensorReadings.Measurment;
import sensorReadings.Neighbour;

public class Test {

	public static void main(String[] args) {
		List<Measurment> list = new ArrayList<Measurment>();
		Measurment m1 = new Measurment("12;14;0,0");
		Measurment m2 = new Measurment("17;15;0,0");
		list.add(m1);
		list.add(m2);
		m2.getvTimestamp().set(1, 4);
		for (Measurment m : list) {
			System.out.println(m.toString());
		}
		System.out.println("---------------------------");
		list.remove(m2);
		for (Measurment m : list) {
			System.out.println(m.toString());
		}
	}
}
