package server;

import java.util.ArrayList;

public class Monitor {
	private static ArrayList<String> messages = new ArrayList<>();
	private static int messagesLength;
	private static int maxMessagesLength = 5000;
	
	private String message = new String();
	
	public Monitor(){
		
	}
	
	public void addLine(String line){
		message += line + "\n";
	}
	
	public void print(){
		Monitor.print(message);
	}
	
	public void println(){
		Monitor.println(message);
	}
	
	public synchronized static String readMessages(){
		String out = new String();
		for(String message:messages){
			out += message;
		}
		messages.clear();
		messagesLength = 0;
		return out;
	}
	
	public synchronized static void print(String message){
		if(message.contains("GET /api/monitor/")){
			return;
		}
		System.out.print(message);				
		message = message.replace("\n", "<br>");
		while(messagesLength + message.length() > maxMessagesLength){
			if(messages.size() > 0){
				messagesLength -= messages.get(0).length();
				messages.remove(0);
			}else{
				return;
			}
		}
		messagesLength += message.length();
		messages.add(message);
	}
	
	public static void println(String message){
		print(message + "\n");
	}
	
	public static void setMaxLength(int maxLength){
		maxMessagesLength = maxLength;
	}

	public static String htmlMonitor() {
		return	"<!DOCTYPE HTML>\n" +
				"<head>\n" +
					"<script src = \"monitor.js\"></script>\n" +
				"</head>\n" +
				"<body onLoad = \"updateMonitor()\">\n" +
					"<section id = \"monitor\"> </section>\n" +
				"</body>";
	}

	public static String jsMonitor() {
		return	"var xhttp = new XMLHttpRequest();\n" +
				"function updateMonitor()\n" +
				"{\n" +
					"xhttp.onreadystatechange = function(){\n" +
						"if(xhttp.readyState == 4 && xhttp.status == 200){\n" +			
							"document.getElementById(\"monitor\").innerHTML = document.getElementById(\"monitor\").innerHTML + xhttp.responseText;\n" +
							"setTimeout(\"updateMonitor()\",2000);\n" +
						"}\n" +
					"}\n" +
					"xhttp.open(\"GET\", \"/api/monitor/updateMonitor\", true);\n" +
					"xhttp.send();\n" +
				"}\n";
	}
}
