package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {	
	
	private Socket socket;
	private InputStream in;
	private OutputStream out;
	private long keepAlive;
	private byte[] buffer = new byte[200];
	private int bufferLength = 0;
	private static int timeOut = 15000;		
	
	public Client(Socket socket) throws UnsuportedClientException{
		try{
			if(socket.isClosed()){
				throw new UnsuportedClientException("Socket is close");
			}
			this.socket = socket;
			in = socket.getInputStream();
			out = socket.getOutputStream();
			keepAlive = System.currentTimeMillis();
		}catch (IOException e) {
			throw new UnsuportedClientException("IO Exception");
		}
	}
	
	public byte[] read() throws UnsuportedClientException {
		if(!isOnline()){
			throw new UnsuportedClientException("Disconnected client");
		}
		try{
			int available = in.available();
			byte[] newRead = new byte[available];				
			
			if (in.read(newRead) > 0){
				keepAlive = System.currentTimeMillis();
			}		
			return newRead;
		}catch(IOException e){
			disconnect();
			throw new UnsuportedClientException("IO Exception");
		}
	}

	public byte[] read(int length) throws UnsuportedClientException, TimeOutClientException {
		if(!isOnline()){
			throw new UnsuportedClientException("Disconnected client");
		}
		
		try{
			bufferLength = 0;
			while((System.currentTimeMillis() - keepAlive) < timeOut){
				if(in.available() > 0){
					int r = in.read();
					keepAlive = System.currentTimeMillis();
					if(r == -1){
						disconnect();
						throw new UnsuportedClientException("End of input found");
					}
					buffer[bufferLength++] = (byte) r;
					if(bufferLength >= length){
						byte[] out = new byte[length];
						System.arraycopy(buffer, 0, out, 0, length);
						return out;
					}
				}
			}
			disconnect();
			throw new TimeOutClientException("Client timeOut");
		}catch(IOException e){
			disconnect();
			throw new UnsuportedClientException("IO Exception");
		}
	}
	
	public String readLine() throws TimeOutClientException, UnsuportedClientException  {
		if(!isOnline()){
			throw new UnsuportedClientException("Disconnected client");
		}
		try{
			bufferLength = 0;
			while((System.currentTimeMillis() - keepAlive) < timeOut){			
				if(in.available() > 0){
					int r = in.read();
					keepAlive = System.currentTimeMillis();
					if(r == -1){
						disconnect();
						throw new UnsuportedClientException("End of input found on read line");
					}
					if(r != '\n' && r != '\r'){
						if(bufferLength >= buffer.length){
							disconnect();
							throw new UnsuportedClientException("buffer full on read line");
						}
						buffer[bufferLength++] = (byte) r;
						
					}else if(r == '\n'){
						String newLine = new String(buffer, 0, bufferLength);
						bufferLength = 0;
						return newLine;
					}
				}			
			}
			disconnect();
			throw new TimeOutClientException("Client timeOut");
		}catch(IOException e){
			disconnect();
			throw new UnsuportedClientException("IO Exception");
		}
	}
	
	public void send(byte[] toSend) throws UnsuportedClientException{
		if(!isOnline()){
			throw new UnsuportedClientException("Disconnected client");
		}
		try{
			out.write(toSend);
		}catch(IOException e){
			disconnect();
			throw new UnsuportedClientException("IO Exception");
		}
	}
	
	public boolean isOnline(){
		if((System.currentTimeMillis() - keepAlive) < timeOut){
			return !socket.isClosed(); 	
		}else{
			disconnect();
			return false;
		}
	}	
	
	public void disconnect(){
		try {
			socket.close();
		} catch (IOException e) {
			//ignore
		}
	}	
	
	public static void setTimeOut(int timeOut){
		Client.timeOut = timeOut;
	}	
	
	@Override
	public String toString() {	
		return socket.getInetAddress().toString();
	}
}
