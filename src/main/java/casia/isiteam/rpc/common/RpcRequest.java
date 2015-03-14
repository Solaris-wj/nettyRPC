package casia.isiteam.rpc.common;

import java.io.Serializable;
import java.util.List;


public class RpcRequest implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1197672781015016920L;
	
	private int ID;
	private String className;
	private String methodName;
	private Class<?>[] paramsCls;
	private Object[] params;
	
	public RpcRequest(int ID, String className, String methodName, Class<?>[] paramsCls, Object[] params) {
		this.ID=ID;
		this.className=className;
		this.methodName=methodName;
		this.paramsCls=paramsCls;
		this.params=params;
	}
	

	public int getID() {
		return ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Class<?>[] getParamClasses() {
		return paramsCls;
	}
	public void setParamClass(Class<?>[] paramsCls) {
		this.paramsCls = paramsCls;
	}
	public Object[] getParams() {
		return params;
	}
	public void setParams(Object[] params) {
		this.params = params;
	}
	
}
