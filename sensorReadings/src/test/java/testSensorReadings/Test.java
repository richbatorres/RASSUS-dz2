package testSensorReadings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import sensorReadings.MainTwo;
import sensorReadings.Measurment;
import sensorReadings.Neighbour;
import sensorReadings.SortByScalar;
import sensorReadings.SortByVector;

public class Test {

	public static void main(String[] args) {
		String mj1 = "26,991,59,,277,,";
		String sendString = mj1.split(Measurment.VECTOR_DELIMITER)[MainTwo.CO_POSITION];
		if (sendString.equals("")) sendString = "0";
		System.out.println(sendString);
	}

}
