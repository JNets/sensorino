package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import client.Client;
import client.UnsuportedClientException;

public abstract class Coordinator {
	
	//Constantes
	public static final int ON_RUN = 1;
	public static final int STOPED = 2;
	
	//Puerto tcp del servidor
	private static int tcpPort;
	//Estado del servidor
	private static volatile int status = STOPED;
	//Maximo numero de clientes concurrentes
	private static int maxClients;	
	//tiempo de espera antes de cerrar conexion	
	//Server Socket	
	private static ServerSocket serverSocket;
	//Lista de los sockets online
	private static volatile ArrayList<HttpParser> parsersOnline;		
	
	public static void run(){
		tcpPort = Config.getInt(Config.TCP_PORT);
		maxClients = Config.getInt(Config.MAX_CLIENTS);		
		parsersOnline = new ArrayList<HttpParser>();		
		try{
			serverSocket = new ServerSocket(tcpPort);
			Monitor.println("\n * * * * * * * * * * Sensorino Runing * * * * * * * * * *");
			status = ON_RUN;
		}catch(Exception e){
			Monitor.println("Fail starting Sensorino");
			Monitor.println("Check port configuration");
			Monitor.println("Check Sensorino is current Online");
			return;
		}
		
		try{
			while(status == ON_RUN){
				if(clientsOnline() < maxClients){					
					HttpParser parser = new HttpParser(getNewClient());					
					registerParser(parser);
					parser.start();					
				}
			}			
		}catch(Exception e){
			
		}	
		try{
			for(HttpParser parser:parsersOnline){				
				parser.close();				
			}
			while(clientsOnline() > 0){
				Thread.sleep(100);
			}
			Monitor.println("\nSensorino stoped!\n");
		}catch(Exception e){
			Monitor.println("\nFail stoping Sensorino\n");
		}				
	}
	
	private static int clientsOnline(){
		return parsersOnline.size();
	}
	
	private static void registerParser(HttpParser parser){
		parsersOnline.add(parser);		
		Monitor.println("\nNew Client Connected: " + parser.getClient() +
						"\nOnline CLients: " + String.valueOf(clientsOnline())+ "\n");
	}
	
	public static synchronized Client getNewClient() throws UnsuportedClientException, IOException{		
		return new Client(serverSocket.accept());	
	}
	
	public static void unRegisterParser(HttpParser parser){		
		try{
			parsersOnline.remove(parser);
			Monitor.println("\nClient Disconected: " + parser.getClient() + 
								"\nOnline Clients: " + String.valueOf(clientsOnline())+ "\n");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void stop(){
		status = STOPED;				
		try{
			Socket socket = new Socket("localhost", tcpPort);
			socket.close();			
		}catch(Exception e){
			
		}
	}		
}
