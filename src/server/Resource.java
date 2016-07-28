package server;

import java.io.File;
import java.io.FileInputStream;

import http.HttpRequest;

public class Resource {	 
	
	private static String rootPath;
	
	private String contentType;
	private String dateTime;
	private byte[] body;
	
	public Resource(){
		
	}
	
	public Resource(String contentType, byte[] body){
		this.body = body;
		this.contentType = contentType;
	}
	
	public String getContentType(){
		return contentType;
	}
	
	public byte[] getBody(){
		return body;
	}		
	
	public String getDateTime(){
		return dateTime;
	}	
	
	protected void setContentType(String contentType){
		this.contentType = contentType;
	}
	
	protected void setDateTime(String dateTime){
		this.dateTime = dateTime;
	}
	
	protected void setBody(byte[] body){
		this.body = body;
	}
	
	public synchronized static Resource loadFromUrl(String resoursePath) throws Exception{
		String outContent = "uknow";
		
		int dotIndex = resoursePath.indexOf(".");
		if(dotIndex != -1){
			String ext = resoursePath.substring(dotIndex + 1);
			switch(ext){
				case "js":
					outContent = "application/javascript";
					break;
				case "html":
					outContent = "text/html";
					break;
				case "jpg":
					outContent = "image/jpg";
					break;
				case "htm":
					outContent = "text/html";
					break;				
			}				
		}else if(resoursePath.endsWith("/")){
			outContent = "text/html";
			resoursePath = resoursePath + "index.html";							
		}						
					
		File file = new File(rootPath + resoursePath);			
		byte[] buffer = new byte[(int) file.length()];			
		FileInputStream in = new FileInputStream(file);
		in.read(buffer);							 	
		in.close();								
		Resource resource = new Resource();
		resource.setBody(buffer);
		resource.setContentType(outContent);
		return resource;		
	}			
	
	public static Resource loadFromRequest(HttpRequest request){
		return new Resource(request.getContentType(), request.getBody());
	}
	
	public static void setRootpath(String rootPath){		
		Resource.rootPath = rootPath;
	}
	
	public static String getRootpath(){		
		return rootPath;
	}
}
