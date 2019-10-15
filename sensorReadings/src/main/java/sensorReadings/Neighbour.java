package sensorReadings;

public class Neighbour {

	private String ip;
	private long port;
	private int positionInVTimestamp;
	
	public Neighbour(String ip, long port, int positionInVTimestamp) {
		super();
		this.ip = ip;
		this.port = port;
		this.setPositionInVTimestamp(positionInVTimestamp);
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
	
	public int getPositionInVTimestamp() {
		return positionInVTimestamp;
	}

	public void setPositionInVTimestamp(int positionInVTimestamp) {
		this.positionInVTimestamp = positionInVTimestamp;
	}

	public String toString() {
		return "{\"ip\" : \"" + this.getIp() + "\", \"port\" : " + this.getPort() + "}, ";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (port ^ (port >>> 32));
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
		Neighbour other = (Neighbour) obj;
		if (port != other.port)
			return false;
		return true;
	}
	
	
	
}
