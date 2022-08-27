package org.iottree.driver.s7.eth;

import java.io.IOException;

public class S7Exception extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public S7Exception() {
        super();
    }

   
    public S7Exception(String message) {
        super(message);
    }

   
    public S7Exception(String message, Throwable cause) {
        super(message, cause);
    }

    
    public S7Exception(Throwable cause) {
        super(cause);
    }
}
