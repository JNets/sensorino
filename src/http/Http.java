package http;

import client.Client;
import client.TimeOutClientException;
import client.UnsuportedClientException;
import server.Resource;

public abstract class Http {
	
	private static int timeOut;
	
	public static void setTimeOut(int timeOut){
		Http.timeOut = timeOut;
	}									
	
	public static HttpRequest readRequest(Client client) throws TimeOutClientException, UnsuportedClientException, BadHttpRequestException {															
		Client.setTimeOut(timeOut);
		boolean readingRequest = false;
		HttpRequest request = new HttpRequest();
		while(client.isOnline()){			
			String line = client.readLine();		
			if(readingRequest){
				if(line.isEmpty()){
					if(request.isPostMethod()){																	
						request.setBody(client.read(request.getContentLength()));										
					}
					return request;
				}else{
					String[] split = line.split(":", 2);
					if(split.length >= 2){
						request.addHeadEntry(split[0].trim(), split[1].trim());
					}else{
						throw new BadHttpRequestException("uncomprensible head");
					}
				}
			}else{
				String[] split = line.split(" ");
				if(split.length == 3){
					if(split[2].startsWith("HTTP") && split[1].startsWith("/")){
						readingRequest = true;
						request.setMethod(split[0]);
						request.setUrl(split[1]);
						request.setProtocol(split[2]);
					}
				}
			}					
		}		
		throw new BadHttpRequestException("Client Disconnected");
	}
	
	public static HttpResponse makeResponse(int responseCode, Resource resource, HttpRequest request){
		HttpResponse response = new HttpResponse();
		response.setProtocol(request.getProtocol());
		response.setConnection(request.getConnection());
		response.setResponseCode(responseCode);
		response.setContentType(resource.getContentType());
		response.setBody(resource.getBody());		
		return response;
	}
	
	public static HttpResponse makeResponse(int responseCode, HttpRequest request){
		HttpResponse response = new HttpResponse();
		response.setProtocol(request.getProtocol());
		response.setConnection(request.getConnection());
		response.setResponseCode(responseCode);		
		return response;
	}
	
	public static HttpResponse makeResponse(int responseCode){
		HttpResponse response = new HttpResponse();
		response.setProtocol("HTTP/1.1");
		response.setConnection("close");
		response.setResponseCode(responseCode);						
		return response;
	}
	
	public static String getMessageFromCode(int responseCode){
		String resultMessage = "Fail";
		switch(responseCode){
		case 200:
			resultMessage = "OK";
			break;
		case 400:
			resultMessage = "Bad Request";
			break;		
		}
		return resultMessage;
	}
	
	public static void sendResponse(Client client, HttpResponse response) throws UnsuportedClientException{
		String resultMessage = getMessageFromCode(response.getResponseCode());		
		String head = 
				response.getProtocol() + " " + String.valueOf(response.getResponseCode()) + " " + resultMessage + "\n" +						
				"Server: Sensorino\n" +
				"Content-Length: " + String.valueOf(response.getContentLength()) + "\n" +
				"Connection: " + response.getConnection() + "\n";
		if(response.getContentLength() > 0){
			head +=	"Content-Type: " + response.getContentType() + "\n\n";
			
		}else{
			head += "\n";
		}
		client.send(head.getBytes());
		client.send(response.getBody());
	}
}
