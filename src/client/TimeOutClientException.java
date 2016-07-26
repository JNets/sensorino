package client;

public class TimeOutClientException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6783869982535181988L;

	/**
	 * 
	 */
	public TimeOutClientException(){
		super();
	}
	
	public TimeOutClientException(String message){
		super(message);
	}

}
