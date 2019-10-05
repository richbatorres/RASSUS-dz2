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

import sensorReadings.Measurment;
import sensorReadings.Neighbour;
import sensorReadings.SortByScalar;
import sensorReadings.SortByVector;

public class Test {

//	 // static member 
//    static int outer_x = 10; 
//      
//    // instance(non-static) member 
//    int outer_y = 20; 
//      
//    // private member 
//    private int outer_private = 30; 
//      
//    // inner class 
//    class InnerClass 
//    { 
//        void display() 
//        { 
//            // can access static member of outer class 
//            System.out.println("outer_x = " + outer_x); 
//              
//            // can also access non-static member of outer class 
//            System.out.println("outer_y = " + outer_y); 
//              
//            // can also access private member of outer class 
//            System.out.println("outer_private = " + outer_private); 
//          
//        } 
//    } 
	
	public static void main(String[] args) {
		List<Measurment> rcvMeasurments = new ArrayList<Measurment>();
		String mj1 = "30,15,,31;250;5,152,3";
		String mj2 = "40,10,,31;240;5,2003,3";
		String mj3 = ",5,8,31;220;5,533,3";
		String mj4 = "50,,10,31;230;5,241,3";
		
		Measurment m1 = new Measurment(mj1);
		Measurment m2 = new Measurment(mj2);
		Measurment m3 = new Measurment(mj3);
		Measurment m4 = new Measurment(mj4);
		
		rcvMeasurments.add(m1);
		rcvMeasurments.add(m2);
		rcvMeasurments.add(m3);
		rcvMeasurments.add(m4);
				
		int[] finalValues = {0, 0, 0, 0};
		int[] antiZeros = {0, 0, 0, 0};
		for (Measurment m : rcvMeasurments) {
			for (int i=0; i<4; i++) {
				if (m.getValues().get(i) != 0) {
					finalValues[i] += m.getValues().get(i);
					antiZeros[i]++;
				}
			}
		}
		System.out.println("Average values from the last 5 seconds are:");
		for (int i=0; i<4; i++) {
			finalValues[i] = finalValues[i]/antiZeros[i];
			System.out.println(finalValues[i]);
		}
		

	}

}
