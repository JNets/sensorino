package http;

public class HttpResponse {
	
	private int responseCode;
	private String contentType = "unknow";
	private String protocol;
	private String connection;
	private byte[] body = new byte[0];
	
	public HttpResponse() {
		
	}
	
	public int getResponseCode() {
		return responseCode;
	}
	
	protected void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	protected void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public byte[] getBody() {
		return body;
	}
	
	protected void setBody(byte[] body) {
		this.body = body;
	}
	
	public int getContentLength(){		
		return body.length;
	}

	public String getProtocol() {
		return protocol;
	}

	protected void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getConnection() {
		return connection;
	}

	protected void setConnection(String connection) {
		this.connection = connection;
	}
	
	public boolean isCLoseConnection(){
		return connection.compareTo("close") == 0;
	}
	
	@Override
	public String toString() { 
		String out = String.valueOf(responseCode) + " " + Http.getMessageFromCode(responseCode);
		if(body.length > 0){
			out += ", " + String.valueOf(body.length) + " bytes, " + contentType;
		}
		return out;
	}
}
