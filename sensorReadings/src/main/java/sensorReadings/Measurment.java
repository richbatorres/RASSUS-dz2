package sensorReadings;

import java.util.ArrayList;
import java.util.List;

public class Measurment {

	private long sTimestamp;
	private List<Integer> vTimestamp = new ArrayList<Integer>();
	private List<Integer> values = new ArrayList<Integer>();
	
	static final String SECTION_DELIMITER = ";";
	static final String VECTOR_DELIMITER = ",";
	
	public Measurment(String packet) {
		String[] packetSections = packet.split(SECTION_DELIMITER);
		String[] valuesArray = packetSections[0].split(VECTOR_DELIMITER);
		for (String v : valuesArray) {
			if (v.equals("")) values.add(0);
			else values.add(Integer.parseInt(v));
		}
		this.sTimestamp = Long.parseLong(packetSections[1]);
		String[] vectors = packetSections[2].split(VECTOR_DELIMITER);
		for (String v : vectors) {
			vTimestamp.add(Integer.parseInt(v));
		}
	}
	
	@Override
	public String toString() {
		String returnString = "";
		for (int v : values) {
			returnString += v + VECTOR_DELIMITER;
		}
		returnString = returnString.substring(0, returnString.length()-1);
		returnString += SECTION_DELIMITER + sTimestamp + SECTION_DELIMITER;
		for (int v : vTimestamp) {
			returnString += v + VECTOR_DELIMITER;
		}
		returnString = returnString.substring(0, returnString.length()-1);
		return returnString;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (sTimestamp ^ (sTimestamp >>> 32));
		result = prime * result + ((vTimestamp == null) ? 0 : vTimestamp.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Measurment other = (Measurment) obj;
		if (other.toString().equals(this.toString())) return true;
		else return false;
	}

	public long getsTimestamp() {
		return sTimestamp;
	}

	public void setsTimestamp(long sTimestamp) {
		this.sTimestamp = sTimestamp;
	}

	public List<Integer> getvTimestamp() {
		return vTimestamp;
	}

	public void setvTimestamp(List<Integer> vTimestamp) {
		this.vTimestamp = vTimestamp;
	}

	public List<Integer> getValues() {
		return values;
	}

	public void setValues(List<Integer> values) {
		this.values = values;
	}
	
	
}
