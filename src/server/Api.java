package server;
import java.util.HashMap;
import java.util.Map;

public abstract class Api {
	private static Map<String, Resource> apiResourses = new HashMap<String, Resource>();	
	private static String apiPath;	
	
	public static boolean acceptUrl(String url){
		return url.startsWith(apiPath);
	}
	
	public static void setPath(String path){
		apiPath = path;
	}
	
	public synchronized static Resource get(String url) throws Exception{
		String[] url_split = url.split("\\?", 2);
		String resourcePath = url_split[0].substring(apiPath.length());
		String extra = null;
		if(url_split.length == 2){
			extra = url_split[1].replace("%20", " ");
		}
		switch(resourcePath){
		case "/sensors":						
			return new Resource("application/json", Sensors.getJson().getBytes());
		case"/sensors/register":
			return Sensors.readRegister(extra);
		case "/stopSensorinoServer":
			Coordinator.stop();								
			return new Resource("text", "Sersorino Stoped!".getBytes());
		case "/monitor/":			
			return new Resource("text/html", Monitor.htmlMonitor().getBytes());			
		case "/monitor/monitor.js":			
			return new Resource("application/javascript", Monitor.jsMonitor().getBytes());			
		case  "/monitor/updateMonitor":			
			return new Resource("text/html", Monitor.readMessages().getBytes());			
		default:
			return apiResourses.get(resourcePath);
		}
	}
	
	public synchronized static void post(String resourcePath, Resource resourse) throws Exception{
		resourcePath = resourcePath.substring(apiPath.length());
		switch(resourcePath){
		case "/sensors":			
			Sensors.putJson(new String(resourse.getBody()));						
			break;
		default:
			apiResourses.put(resourcePath, resourse);
		}		
	}			
}
