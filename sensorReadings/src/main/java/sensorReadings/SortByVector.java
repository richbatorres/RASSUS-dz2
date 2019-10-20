package sensorReadings;

import java.util.Comparator;

public class SortByVector implements Comparator<Measurment>{

	int position;
	
	
	public SortByVector(int position) {
		super();
		this.position = position;
	}


	public int compare(Measurment m1, Measurment m2) {
		if (m1.getvTimestamp().size() > position && m2.getvTimestamp().size() > position) {
			return m1.getvTimestamp().get(position) - m2.getvTimestamp().get(position);
		}else {
			throw new IndexOutOfBoundsException("bar smo našli dže je greška..");
		}
	}

}