package org.trc.exception;

public class BizException extends RuntimeException {
	 
    private static final long serialVersionUID = 1L;
    
    public BizException(String msg) {
        super(msg);
    }
 
    public BizException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
