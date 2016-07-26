package server;

import client.Client;
import client.UnsuportedClientException;
import http.BadHttpRequestException;
import http.Http;
import http.HttpRequest;
import http.HttpResponse;

public class HttpParser extends Thread {
	
	private static final int FINISHED = 0;
	private static final int RUN = 1;
	private static final int STOPED = 2;	
	
	private volatile int status;			
	private Client client;
	
	public HttpParser(Client client){
		status = RUN;
		this.client = client;
	}	
	
	public String getClient(){
		return client.toString();
	}
	
	protected void close(){
		status = FINISHED;				
	}
	
	@Override
	public void run(){		
		while(status == RUN){
			Monitor log = new Monitor();
			log.addLine(client.toString());
			try{
				HttpRequest request = Http.readRequest(client);				
				log.addLine(request.toString());
				HttpResponse response = null;
				if(Api.acceptUrl(request.getUrl())){
					try{
						switch(request.getMethod()){
						case "GET":						
							response = Http.makeResponse(200, Api.get(request.getUrl()), request);						
							break;
						case "POST":						
							Api.post(request.getUrl(), Resource.loadFromRequest(request));
							response = Http.makeResponse(200, request);						
							break;
						default:
							Resource resource = new Resource("text", "Api Not suported Method".getBytes());
							response = Http.makeResponse(400, resource, request);
						}
					}catch(Exception e){
						Resource resource = new Resource("text", e.getMessage().getBytes());
						log.addLine(e.getMessage());
						response = Http.makeResponse(400, resource, request);					
					}
				}else{
					switch(request.getMethod()){
					case "GET":
						try{						
							response = Http.makeResponse(200, Resource.loadFromUrl(request.getUrl()), request);												
						}catch(Exception e){
							log.addLine(e.getMessage());
							Resource resource = new Resource("text", "File Not Found".getBytes());
							response = Http.makeResponse(400, resource, request);
						}
						break;
					default:
						Resource resource = new Resource("text", "Not suported Method".getBytes());
						response = Http.makeResponse(400, resource, request);					
					}						
				}				
				Http.sendResponse(client, response);
				log.addLine(response.toString());
				log.println();
				if(response.isCLoseConnection()){					
					break;
				}
			}catch(BadHttpRequestException e){				
				log.addLine("Bad Request, " + e.getMessage());
				try {
					HttpResponse response =  Http.makeResponse(400);
					Http.sendResponse(client, response);
					log.addLine(response.toString());
					log.println();
				} catch (UnsuportedClientException e1) {
					log.addLine("Fail response");
					log.println();
					break;					
				}
			}catch(Exception e){				
				log.addLine(e.getMessage());
				log.println();
				break;
			}			
		}		
		client.disconnect();
		Coordinator.unRegisterParser(this);		
		status = STOPED;
	}
}
