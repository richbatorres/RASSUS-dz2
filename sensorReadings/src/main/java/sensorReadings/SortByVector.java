package sensorReadings;

import java.util.Comparator;

public class SortByVector implements Comparator<Measurment>{

	int position;
	
	
	public SortByVector(int position) {
		super();
		this.position = position;
	}


	@Override
	public int compare(Measurment m1, Measurment m2) {
		return m1.getvTimestamp().get(position) - m1.getvTimestamp().get(position);
	}

}