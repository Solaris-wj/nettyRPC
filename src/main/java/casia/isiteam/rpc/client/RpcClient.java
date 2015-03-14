package casia.isiteam.rpc.client;

import java.lang.reflect.Proxy;

public class RpcClient {
	String host;
	int port;
	RpcInvoker invoker;
	public RpcClient(String host,int port) {
		this.host=host;
		this.port=port;
		this.invoker=new RpcInvoker(host,port);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T create(Class<T> cls) {
		if (!cls.isInterface()) {
			throw new IllegalArgumentException(cls.getName()
					+ " is not an interface");
		}
		return (T) Proxy.newProxyInstance(cls.getClassLoader(),
				new Class<?>[] { cls }, invoker);
	}
}
