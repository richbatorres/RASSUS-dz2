package sensorReadings;

import java.util.Comparator;

public class SortByScalar implements Comparator<Measurment>{

	public int compare(Measurment m1, Measurment m2) {
		return (int) (m1.getsTimestamp() - m2.getsTimestamp());
	}

}
