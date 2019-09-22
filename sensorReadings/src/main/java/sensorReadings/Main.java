package sensorReadings;

import java.io.File;

public class Main {

	public static void main(String[] args) {
		
		String fileName = "neighbours.json";
		 
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		 
		File file = new File(classLoader.getResource(fileName).getFile());
		
		Server server = new Server();
		Thread t1 = new Thread(server, "t1");
		t1.start();
		Client client = new Client();
		Thread t2 = new Thread(client, "t2");
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t2.start();
	}
	
	private File getFileFromResources(String fileName) {

        ClassLoader classLoader = getClass().getClassLoader();

        String resource = "C:\\Users\\ebrctnx\\OneDrive - fer.hr\\kolegiji\\RASSUS\\DZ\\RASSUS-dz1\\sensorProject\\target\\classes\\mjerenja.csv";
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource);
        }

    }

}
