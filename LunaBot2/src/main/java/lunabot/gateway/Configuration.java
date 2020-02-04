package lunabot.gateway;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author SirMangler
 *
 * @date 2 Mar 2019
 */
public class Configuration {

	private static HashMap<String, String[]> dataMap;
	final private static Log log = new Log("Configuration");
	
	static File dat = new File(System.getProperty("user.dir")+"/resources/luna.dat");
	
	public static URL getResource(String internalPath) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		return loader.getResource(internalPath);	
	}
	
	public static String getRawFile() throws IOException, URISyntaxException {
		if (dat.exists()) {
			StringBuilder b = new StringBuilder();
			Path p = Paths.get(dat.toURI());
			
			Files.readAllLines(p).forEach(line -> {
				b.append(line+"\r\n");
			});
			
			return b.toString();
		}

		return "File doesn't exist. It will be created automatically as needed, or you can use the template below.";
	}
	
	public static String getVariable(String term) {
		return getData(term)[0];
	}
	
	public static void reloadData() {
		log.info("Reloading Data");
		loadData();
	}
	
	public static void replaceData(String key, String[] data) {
		log.info("Updating: "+key);
		
		dataMap.put(key, data);
		
		updateData();
	}
	
	public static void addData(String key, String[] data) {
		log.info("Updating: "+key);
		
		String[] arr = dataMap.get(key);
		String[] newData = new String[arr.length+data.length];
		
		for (int i = 0; i < arr.length; i++) {
			newData[i] = arr[i];
		}
		
		for (int i = 0; i < data.length; i++) {
			newData[i+arr.length] = data[i];
		}
		
		dataMap.put(key, newData);
		
		updateData();
	}
	
	public static String[] getData(String term) {
		if (dataMap == null) {
			loadData(); //Attempts to load from file.
		}
	
		return dataMap.get(term);
	}
	
	
	public static Tuple getTuple(String term) {
		if (dataMap == null) {
			loadData(); //Attempts to load from file.
		}
		
		String o = getData(term)[0];
		
		if (o == null)
			log.warn(term+" is NULL");
		
		if (o.contains("[]")) {
			String[] split = o.split("[]", 2);
			
			return new Tuple(split[0], split[1]);
		} else {
			log.warn("Malformed Tuple: "+o);
		}
		
		return null;
	}
	
	public static Tuple[] getTuples(String term) {
		if (dataMap == null) {
			loadData(); //Attempts to load from file.
		}
		
		String[] o = getData(term);
		
		if (o == null)
			log.warn(term+" is NULL");
		
		Tuple[] tuples = new Tuple[o.length];
		for (int i = 0; i < o.length; i++) {
			String[] split = o[i].split("[]", 2);
			
			tuples[i] = new Tuple(split[0], split[1]);
		}

		return tuples;
	}

	public static void setTuples(String key, Tuple[] tuples) {
		log.info("Updating: "+key);
		
		if (dataMap == null) {
			loadData(); //Attempts to load from file.
		}
		
		String[] buf = new String[tuples.length];
		
		for (int i = 0; i < tuples.length; i++) {
			buf[i] = tuples[i].a+":"+tuples[i].b;
		}
		
		replaceData(key, buf);
	}
	
	public static void addTuple(String key, String str1, String str2) {
		log.info("Updating: "+key);

		String[] o = getData(key);
		
		if (o == null || o.length == 0) {
			dataMap.put(key, new String[] {str1+"[]"+str2});
		} else {
			String[] arr = getStrings(key);
			String[] newData = new String[arr.length+1];
			
			for (int i = 0; i < arr.length; i++) {	
				if (arr[i] == null) {
					log.warn("Found NULL tuple in tuple list.");
					continue;
				}
				
				newData[i] = arr[i];
			}
			
			newData[arr.length] = str1+"[]"+str2;
			
			dataMap.put(key, newData);
		}
		
		updateData();
	}
	
	public static void removeTuple(String key, String a) {
		log.info("Updating: "+key);
		
		String[] o = getData(key);
		
		if (o == null) {
			return;
		} else {
			String[] arr = getStrings(key);
			String[] newData = new String[arr.length-1];
			
			for (int i = 0; i < arr.length; i++) {
				if (arr[i] == null) {
					log.warn("Found a NULL tuple while trying to removeTuple()");
					continue;
				}
				
				if (arr[i].startsWith(a)) continue;
				
				newData[i] = arr[i];
			}

			dataMap.put(key, newData);
		}

		updateData();
	}
	
	public static void removeTuples(String key) {
		log.info("Updating: "+key);
		
		dataMap.remove(key);
		
		updateData();
	}
	
	public static String[] getStrings(String term) {
		if (dataMap == null) {
			loadData(); //Attempts to load from file.
		}
		
		String[] o = getData(term);
		
		if (o == null)
			log.warn(term+" is NULL");
		
		return o;
	}
	
	private static void updateData() {
		log.info("Updating Data");
		
		try {
			if (!dat.exists()) dat.createNewFile();
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(dat));
			if (dataMap != null) {
				for (String key : dataMap.keySet()) {
					writer.write("%" + key);
					writer.write("\r\n");
					
					String[] o = dataMap.get(key);
					
					if (o.length != 0) {
						for (String line : o) {
							writer.write(line == null ? "null" : line);
							writer.write("\r\n");
						}
					}
					
					writer.write("\r\n");
					writer.flush();
				}
			}
		
			writer.close();
		} catch (IOException e) {
			log.error("Cannot update data!", e);
		}
	}
	
	public static void updateFile(String settings) {
		log.info("Updating Data");
		
		try {
			if (!dat.exists()) dat.createNewFile();
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(dat));
			
			writer.write(settings);
			
			writer.close();
		} catch (IOException e) {
			log.error("Cannot update file!", e);
		}
		
		loadData();
	}
	
	private static void loadData() {
		log.info("Loading Data");
		
		try {
			if (!dat.exists()) dat.createNewFile();
			
			BufferedReader reader = new BufferedReader(new FileReader(dat));

			String key = null;
			List<String> data = new ArrayList<String>();
			dataMap = new HashMap<String, String[]>();
			
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("%")) {
					key = line.replace("%", "").trim();
				} else if (line.equalsIgnoreCase("\r\n") || line.isEmpty()) {
					dataMap.put(key, data.toArray(new String[data.size()]));
					
					key = null;
					data = new ArrayList<String>();
				} else {
					data.add(line);
				}
			}
			
			if (key != null) {
				dataMap.put(key, data.toArray(new String[data.size()]));
			}
			
			reader.close();
		} catch (IOException e) {
			log.error("Cannot load data!", e);
		}
	}
}
