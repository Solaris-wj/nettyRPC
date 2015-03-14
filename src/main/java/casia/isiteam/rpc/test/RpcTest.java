package casia.isiteam.rpc.test;

import casia.isiteam.rpc.client.RpcClient;

public class RpcTest {
	
	public static void main(String [] args) {
		String host="localhost";
		int port=8080;
		
		RpcClient rpcClient=new RpcClient(host, port);
		
		AddService addService=rpcClient.create(AddService.class);
		
		addService.sayHello("hello");
		//System.out.println(addService.sayHello());
		//System.out.println(addService.add(10, 20));
		
	}


}
