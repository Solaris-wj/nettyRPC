package casia.isiteam.rpc.client;

import java.lang.reflect.Proxy;

import casia.isiteam.rpc.test.AddService;

public class RpcClient {
	String host;
	int port;
	RpcInvoker invoker;

	public RpcClient(String host, int port) {
		this.host = host;
		this.port = port;
		this.invoker = new RpcInvoker(host, port);
	}

	public void close() {
		System.err.println("client close called");
		invoker.closeChannel();
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

	public static void main(String[] args) throws InterruptedException {
		String host = "localhost";
		int port = 8080;

		RpcClient rpcClient = new RpcClient(host, port);

		AddService add = rpcClient.create(AddService.class);

		try {

			long start = System.currentTimeMillis();
			int num = 1000;
			for (int i = 0; i != num; ++i) {
				final int n=i;
				new Thread() {
					public void run() {
						System.out.println(add.add(10, 20));
						if(n==500) {
							rpcClient.close();
						}
					}
				}.start();
				

			}
			long end = System.currentTimeMillis();
			System.out.println("time = " + (end - start));

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// int num=20;
		//
		// for(int i =0;i!=num;++i) {
		// final int x=i;
		// new Thread() {
		// public void run() {
		// System.err.println("thread id = " + Thread.currentThread().getId());
		// System.out.println(addService.add(x, 10));
		// }
		// }.start();
		// }

	}

}
