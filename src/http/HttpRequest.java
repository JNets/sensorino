package http;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
	
	public static final String CONTENT_LENGTH = "Content-Length";
	public static final String CONNECTION = "Connection";
	public static final String CONTENT_TYPE = "Content-Type";
	
	private int contentLength = 0;
	private String connection = "Keep-Alive";
	private String method;
	private String protocol;
	private String url;
	private String contentType = "unknow";
	private byte[] body = new byte[0];
	
	private Map<String, String> headMap = new HashMap<String, String>();
	
	public HttpRequest() {
	
	}
	
	public int getContentLength(){
		return contentLength;
	}
	
	public String getProtocol(){
		return protocol;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getConnection(){
		return connection;
	}
	
	public String getContentType(){
		return contentType;
	}
	
	public byte[] getBody(){
		return body;
	}
	
	public boolean isGetMethod(){
		return method.compareTo("POST") == 0;
	}
	
	public boolean isPostMethod(){
		return method.compareTo("POST") == 0;
	}
	
	public String getMethod(){
		return method;
	}
	
	protected void setProtocol(String protocol) throws BadHttpRequestException{
		switch(protocol){
		case "HTTP/1.1":
			break;
		case "HTTP/1.0":
			break;					
		default:
			throw new BadHttpRequestException("Unsuported Protocol");
		}
		this.protocol = protocol;
	}
	
	protected void setUrl(String url){
		this.url = url;
	}
	
	protected void setMethod(String method) throws BadHttpRequestException{
		switch(method){
		case "GET":
			break;
		case "POST":
			break;
		case "PATCH":
			break;
		default:
			throw new BadHttpRequestException("Unsuported Method");
		}
		this.method = method;
	}	
	
	protected void setBody(byte[] body) throws BadHttpRequestException{
		if(body.length != contentLength){
			throw new BadHttpRequestException("Body length error");
		}
		this.body = body;
	}
	
	protected void addHeadEntry(String key, String value) throws BadHttpRequestException{
		try{
			switch(key){
			case CONTENT_LENGTH:
				try{
					contentLength = Integer.parseInt(value);
				}catch(NumberFormatException e){
					throw new BadHttpRequestException("Content-Length value error");
				}
				break;
			case CONNECTION:				
				switch(value){
				case "keep-alive":
					break;
				case "close":
					break;
				default:
					throw new BadHttpRequestException("Connection value error");
				}
				connection = value;
			case CONTENT_TYPE:
				break;
			default:
				headMap.put(key, value);
			}
		}catch(BadHttpRequestException e){
			throw e;
		}
		catch(Exception e){
			throw new BadHttpRequestException("Uncompresible head");
		}
	}
	
	@Override
	public String toString() {
		String out = method + " " + url;
		if(body.length > 0){
			out += ", " + String.valueOf(body.length) + " bytes, " + contentType;			
		}
		return out;
	}
}
