package http;

public class BadHttpRequestException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8796574336973336145L;

	public BadHttpRequestException(){
		super();
	}
	
	public BadHttpRequestException(String message){
		super(message);
	}
	
}
