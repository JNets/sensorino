package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Sensors {
	private static Map<String, String> sensors = new HashMap<String, String>();
	private static Map<String, String> format = new HashMap<String, String>();
	private static String fieldId; 		
	
	protected static String getJson(){
		String jsonSensors = new String("[");
		for(String sensor: sensors.values()){				
			jsonSensors += sensor + ",";
		}		
		jsonSensors += "]";
		jsonSensors = jsonSensors.replace(",]", "]");
		return jsonSensors;
	}
	
	protected static void putJson(String jsonSensor) throws Exception{		
		if(isValidFormat(jsonSensor)){
			String id = findSensorId(jsonSensor);
			addRegister(id, jsonSensor);
			sensors.put(id, jsonSensor);
		}else{
			throw new Exception("Invalid Sensor Format");
		}
	}
	
	private static Map<String, String> parse(String sensor) throws Exception{
		Map<String, String > fields = new HashMap<String, String>();
		String[] properties = sensor.trim().substring(1, sensor.length() - 1).split(",");		
		for(String propertie:properties){
			String[] key_value = propertie.split(":", 2);
			fields.put(key_value[0].trim(), key_value[1].trim());
		}
		return fields;		
	}
	
	protected static void addRegister(String id, String sensor) throws Exception{
		Map<String, String> fields = parse(sensor);
		String registerPath = id.substring(1, id.length() - 1);		
		File register = new File(Resource.getRootpath() + "/" + registerPath + ".csv");
		FileOutputStream out = null;
		if(!register.exists()){
			register.createNewFile();
			out = new FileOutputStream(register, true);
			String columNames = new String();						
			for(String key:fields.keySet()){								
				columNames += key + ";";				
			}
			columNames += "Server Time\n";
			out.write(columNames.getBytes());
		}
		if(out == null){
			out = new FileOutputStream(register, true);
		}
		String newRow = new String();		
		for(String field:fields.values()){						
			newRow += field + ";";			
		}
		Date time = new Date();
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		newRow += timeFormat.format(time) + "\n";
		out.write(newRow.getBytes());
		out.close();
	}
	
	protected static Resource readRegister(String request) throws Exception{
		String id = null; 
		String ti = null; 
		String tf = null;
		Date initTime;
		Date endTime;
		SimpleDateFormat timeFormat;
		try{
			String[] fields = request.split("&");		
			for(String field:fields){
				String[] key_value = field.split("=");
				switch(key_value[0].trim()){
				case "id":
					id = key_value[1].trim();
					break;
				case "ti":
					ti = key_value[1].trim();
					break;
				case "tf":
					tf = key_value[1].trim();
					break;
				default:
				}
			}
			timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			initTime = timeFormat.parse(ti);
			endTime = timeFormat.parse(tf);
		}catch (Exception e) {
			throw new Exception("Invalid Time Format");
		}
		ti = ti.replace(" ", "_");
		tf = tf.replace(" ", "_");
		ti = ti.replace("/", "-");
		tf = tf.replace("/", "-");
		ti = ti.replace(":", "-");
		tf = tf.replace(":", "-");
		String timeRegisterPath = "/" + id + "_" + ti + "_" + tf + ".csv";
		File timeRegister = new File(Resource.getRootpath() + timeRegisterPath);
		if(!timeRegister.exists()){
			timeRegister.createNewFile();
			File register = new File(Resource.getRootpath() + "/" + id + ".csv");
			if(!register.exists()){
				throw new Exception("Sensor not found");
			}
			FileInputStream in = new FileInputStream(register);			
			FileOutputStream out = new FileOutputStream(timeRegister);					
			byte[] buffer = new byte[500];
			int i = 0;
			int j = 0;
			boolean isFirstRow = true;
			while(in.available() > 0){
				byte c = (byte) in.read();
				if(c == ';'){
					j = i;
				}
				if(c == '\n'){
					String rowtime = new String(buffer, 0, i);
					i = 0;
					String row = rowtime.substring(0, j);
					if(isFirstRow){
						isFirstRow = false;
						out.write((row + "\n").getBytes());
					}else{
						String ts = rowtime.substring(j + 1);
						Date serverTime = timeFormat.parse(ts);
						if(serverTime.after(initTime)){
							if(serverTime.after(endTime)){
								break;
							}
							out.write((row + "\n").getBytes());
						}						
					}
				}else{
					buffer[i++] = c;
				}
			}
			in.close();
			out.close();
		}		
		Resource response = new Resource("text/url", timeRegisterPath.getBytes());
		Resource.addToBlackList(timeRegisterPath);
		return response;
	}
	
	public static void setFieldId(String field){
		fieldId = field;
	}
	
	public static void setFormat(String sensorFormat){		
		String[] components = sensorFormat.substring(1, sensorFormat.length() - 1).split(",");
		for(String component: components){
			String[] field_format = component.split(":", 2);
			format.put(field_format[0].trim(), field_format[1].trim());
		}
	}
	
	public static boolean isValidFormat(String jsonSensor){
		String process = jsonSensor;
		process = process.trim();
		if(!process.startsWith("{")){
			return false;
		}
		if(!process.endsWith("}")){
			return false;
		}
		process = process.substring(1, jsonSensor.length() - 1);
		String[] components = process.split(",");
		if(components.length != format.size()){
			return false;
		}
		for(String component:components){
			String[] field_value = component.split(":", 2);
			String field = field_value[0].trim();
			String value = field_value[1].trim();
			if(!format.containsKey(field)){
				return false;
			}
			String valueFormat = format.get(field);
			char formatStart, valueStart;			
			String formatContain, valueContain;
			if(valueFormat.startsWith("\"")){
				if(!value.startsWith("\"") || !value.endsWith("\"")){
					return false;
				}
				formatStart = valueFormat.charAt(1);
				formatContain = valueFormat.substring(2, valueFormat.length() - 1);
				valueStart = value.charAt(1);
				valueContain = value.substring(2, value.length() - 1);
			}else{
				if(value.startsWith("\"") || value.endsWith("\"")){
					return false;
				}
				formatStart = valueFormat.charAt(0);
				formatContain = valueFormat.substring(1, valueFormat.length());
				valueStart = value.charAt(0);
				valueContain = value.substring(1, value.length());
			}
			if(formatStart != '*'){
				if(valueStart >= '0' && valueStart <= '9'){
					if(formatStart != '1'){
						return false;
					}
				}else if(valueStart >= 'a' && valueStart <= 'z'){
					if(formatStart != 'a'){
						return false;
					}
				}else if(valueStart >= 'A' && valueStart <= 'Z'){
					if(formatStart != 'A'){
						return false;
					}
				}else{
					if(valueStart != formatStart){
						return false;
					}
				}
			}
			if(!formatContain.contains("*")){
				for(byte charContain:valueContain.getBytes()){
					if(charContain >= '0' && charContain <= '9'){
						if(!formatContain.contains("1")){
							return false;
						}
					}else if(charContain >= 'a' && charContain <= 'z'){
						if(!formatContain.contains("a")){
							return false;
						}
					}else if(charContain >= 'A' && charContain <= 'Z'){
						if(!formatContain.contains("A")){
							return false;
						}
					}else{
						if(!formatContain.contains(new String(new byte[] {charContain}))){
							return false;
						}
					}
				}
			}
		}
		return true;
	}		
	
	private static String findSensorId(String sensor) throws Exception{
		String[] properties = sensor.trim().substring(1, sensor.length() - 1).split(",");		
		for(String propertie:properties){			
			String[] key_value = propertie.split(":");
			if(key_value[0].trim().matches(fieldId)){
				return key_value[1].trim();			
			}
		}
		throw new Exception("Sensor Id Field not Found");
	}
}
