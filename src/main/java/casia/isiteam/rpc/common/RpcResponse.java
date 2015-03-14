package casia.isiteam.rpc.common;

import java.io.Serializable;

public class RpcResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2109117564620632647L;
	private int ID;

	private Throwable exception;
	private Object result;

	public RpcResponse(int ID) {
		this.ID=ID;
	}
	public int getID() {
		return ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

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
