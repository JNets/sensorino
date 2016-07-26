package main;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import http.Http;
import server.Api;
import server.Config;
import server.Coordinator;
import server.Monitor;
import server.Resource;
import server.Sensors;
import server.UdpPresenter;


public class WebServer {
	static final int PUERTO = 10101;
	static ServerSocket serverSocket;
	static Socket socket;
	static DataOutputStream salida;
	static BufferedReader entrada;			
	FileOutputStream out = null;
	
	
	public static void main(String[] args){
		Monitor.println(" * * * * * * * * * * Sensorino server * * * * * * * * * *");
		try{
			Config.loadConfig();
			Monitor.println("Config loaded:");
			Config.print();
		}catch(Exception e){
			Monitor.println("Fail loading Config");
			Monitor.println("Check config.ini");			
		}
		Resource.setRootpath(Config.getString(Config.ROOT_PATH));	
		Monitor.setMaxLength(Config.getInt(Config.MONITOR_LENGTH));					
		Http.setTimeOut(Config.getInt(Config.TIMEOUT));
		Sensors.setFormat(Config.getString(Config.SENSORS_FORMAT));
		Sensors.setFieldId(Config.getString(Config.SENSORS_ID));
		Api.setPath(Config.getString(Config.API_PATH));
		UdpPresenter presenter = new UdpPresenter(Config.getInt(Config.UDP_PORT));
		presenter.start();
		Coordinator.run();
	}	
}
