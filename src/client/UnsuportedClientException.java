package client;

public class UnsuportedClientException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -874607255732426L;

	public UnsuportedClientException(){
		super();
	}
	
	public UnsuportedClientException(String message){
		super(message);
	}
	
}
