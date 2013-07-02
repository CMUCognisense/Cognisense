package com.androidcommproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class hashMap {
	private static HashMap<String, Integer> hm;

	public hashMap() {
		hm = new HashMap<String, Integer>();

	}

	public void addElement(String key, int value) {
		hm.put(key, value);

	}

	public HashMap<String, Integer> getCurrentHashMap() {
		return hm;
	}

	public void writeToFile() throws IOException {

		File file = new File("values.txt");
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(hm);
		oos.flush();
		oos.close();
		fos.close();

	}

	@SuppressWarnings("unchecked")
	public void readFromFile() throws IOException, ClassNotFoundException{
		File toRead = new File("values.txt");
		if(!toRead.exists()){
			toRead.createNewFile();
		}else{
		FileInputStream fis = new FileInputStream(toRead);
		ObjectInputStream ois = new ObjectInputStream(fis);

		hm = (HashMap<String, Integer>) ois.readObject();

		ois.close();
		fis.close();

		for (Map.Entry<String, Integer> m : hm.entrySet()) {
			System.out.println(m.getKey() + " : " + m.getValue());
		}
		}
	}
}
