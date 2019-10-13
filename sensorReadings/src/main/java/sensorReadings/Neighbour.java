package sensorReadings;

public class Neighbour {

	private String ip;
	private long port;
	
	public Neighbour(String ip, long port) {
		super();
		this.ip = ip;
		this.port = port;
	}
	
	public Neighbour() {
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public long getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public String toString() {
		return "{\"ip\" : \"" + this.getIp() + "\", \"port\" : " + this.getPort() + "}, ";
	}
	
}
