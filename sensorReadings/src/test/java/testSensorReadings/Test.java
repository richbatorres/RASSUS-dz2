package testSensorReadings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import sensorReadings.Measurment;
import sensorReadings.Neighbour;

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
		String PORT_REGEX = "^\\d*$";
		
		if ("53333".matches(PORT_REGEX)) {
			System.out.println("uspeeeh");
		}else System.out.println("neuspeeeh");

	}

}
