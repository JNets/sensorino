package server;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class Config {
	
	public static final String TCP_PORT = "tcp_port";
	public static final String UDP_PORT = "udp_port";
	public static final String MAX_CLIENTS = "max_clients";
	public static final String TIMEOUT = "timeout";
	public static final String ROOT_PATH = "root_path";
	public static final String API_PATH = "api_path";		
	public static final String MONITOR_LENGTH = "monitor_length"; 
	public static final String SENSORS_FORMAT = "sensors_format";
	public static final String SENSORS_ID = "sensors_id";
	
	
	private static Map<String, String> configMap = new HashMap<String, String>();
	
	static{
		
		
	}
	
	public static void loadConfig() throws Exception {
		FileInputStream in = null;
		char[] buffer = new char[100];		
		in = new FileInputStream("config.ini");
		int c,i = 0;
		while((c = in.read()) != -1){
			if(c != '\n'){
				buffer[i++] = (char) c;
			}else{
				String[] key_value = String.copyValueOf(buffer, 0, i).split("="); 
				configMap.put(key_value[0].trim(), key_value[1].trim());
				i = 0;					
			}
		}
		in.close();
		String rootPath = configMap.get(Config.ROOT_PATH);
		if(rootPath.endsWith("/") || rootPath.endsWith("\\")){				
			configMap.put(Config.ROOT_PATH, rootPath.substring(0, rootPath.length() - 1));
		}								
	}
	
	public static void print(){
		for(String key:configMap.keySet()){
			Monitor.println(key + " = " + configMap.get(key));
		}
	}
	
	public static int getInt(String key){
		return Integer.parseInt(configMap.get(key));
	}
	
	public static String getString(String key){
		return configMap.get(key);
	}
	
}
