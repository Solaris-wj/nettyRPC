package casia.isiteam.rpc.server;

import java.util.HashMap;
import java.util.Map;

import casia.isiteam.rpc.test.AddService;
import casia.isiteam.rpc.test.AddServiceImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class RpcServer {

	String host;
	int port;
	Map<String,Object> rpcObjects=new HashMap<String, Object>();
	public RpcServer(String host, int port) {
		this.host = host;
		this.port = port;
		
	}
	

	/**
	 * 添加 接口 和实现类的对应关系
	 * @param interfaceCls
	 * @param obj
	 */
	public void addRpcImplementor(Class<?> interfaceCls, Object obj) {
		if (!interfaceCls.isInterface()) {
			throw new IllegalArgumentException(interfaceCls.getName()
					+ " is not an interface");
		}
		
		rpcObjects.putIfAbsent(interfaceCls.getName(), obj);
	}
	public Object getRpcObject(String className) {
		return rpcObjects.get(className);
	}
	public void start() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		final RpcServer rpcServer=this;
		try {
			ServerBootstrap sBootstrap = new ServerBootstrap();
			sBootstrap.group(group, workGroup);
			sBootstrap.channel(NioServerSocketChannel.class);
			sBootstrap.option(ChannelOption.TCP_NODELAY, true);

			sBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {

					ChannelPipeline cp = ch.pipeline();

					cp.addLast(new LoggingHandler(LogLevel.INFO));
					
					// decoder
					cp.addLast(new LengthFieldBasedFrameDecoder(
							Integer.MAX_VALUE, 0, Integer.BYTES, 0,
							Integer.BYTES));					
					cp.addLast(new RpcRequestDecoder());

					// encoder
					cp.addLast(new LengthFieldPrepender(Integer.BYTES));
					cp.addLast(new RpcResponseEncoder());

					// business logic
					cp.addLast(new RpcServerHandler(rpcServer));
				}
			});

			ChannelFuture f;

			f = sBootstrap.bind(host, port).sync();

			// 等待服务器关闭，会阻塞线程
			f.channel().closeFuture().sync();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			throw e;
		} finally {
			group.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}
	
	public static void main(String []args) throws Exception {
		String host="localhost";
		int port=8080;
		
		AddService addService=new AddServiceImpl();
		RpcServer rpcServer=new RpcServer(host, port);
		rpcServer.addRpcImplementor(AddService.class, addService);

		rpcServer.start();
		
	}
}
