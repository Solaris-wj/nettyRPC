package casia.isiteam.rpc.test;

public class AddServiceImpl implements AddService {

	public int add(int x, int y) {
		//System.out.println(Thread.currentThread().getId());
		return x+y;
	}
	@Override
	public String sayHello() {
		return "hello";
	}
	@Override
	public void sayHello(String str) {
		System.out.println(str);
		
	}
}
