package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UdpPresenter extends Thread {
	private static final int RUN = 1;
	private static final int STOP = 0;
	 
	private int status = RUN;
	private DatagramSocket socket;
	
	public UdpPresenter(int port){	
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {	
			//ignore
		}
	}
	
	public void close(){
		status = STOP;
	}
	
	@Override
	public void run() {
		byte[] buffer = new byte[100];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length); 
		while(status == RUN){
			try{
				socket.receive(packet);
				InetAddress ip = packet.getAddress();
				int clientPort = packet.getPort();
				String message = new String(packet.getData(), 0, packet.getLength());
				Monitor.println("\nUdp message: " + message + "\nIP: " + ip);
				if(message.contains("Hello Sensorino!")){
					DatagramPacket response = new DatagramPacket("Hi! Sensorino is here".getBytes(), 21, ip, clientPort);
					socket.send(response);
				}
			}catch(Exception e){
				Monitor.println("Error");
			}
		}
		socket.close();
	}
	
}
