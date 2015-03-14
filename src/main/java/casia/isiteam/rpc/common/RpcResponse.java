package casia.isiteam.rpc.common;

import java.io.Serializable;

public class RpcResponse implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2109117564620632647L;
	private Throwable exception;
	private Object result;
	
//	public RpcResponse(Throwable exception, Object result) {
//		this.exception=exception;
//		this.result=result;		
//	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
	
}
