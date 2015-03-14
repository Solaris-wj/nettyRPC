package casia.isiteam.rpc.common;

import java.io.Serializable;
import java.util.List;


public class RpcRequest implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1197672781015016920L;
	String className;
	String methodName;
	Class<?>[] paramsCls;
	Object[] params;
	
	public RpcRequest(String className, String methodName, Class<?>[] paramsCls, Object[] params) {
		this.className=className;
		this.methodName=methodName;
		this.paramsCls=paramsCls;
		this.params=params;
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
