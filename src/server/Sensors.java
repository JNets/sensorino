package server;

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
			sensors.put(findSensorId(jsonSensor), jsonSensor);
		}else{
			throw new Exception("Invalid Sensor Format");
		}
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
