package casia.isiteam.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import casia.isiteam.rpc.common.RpcRequest;
import casia.isiteam.rpc.common.RpcResponse;

public class RpcInvoker implements InvocationHandler {

	EventLoopGroup group = null;
	Bootstrap bootstrap = null;
	RpcClientHandler rpcClientHandler=new RpcClientHandler();
	String host;
	int port;
	public RpcInvoker(String host,int port) {
		this.host=host;
		this.port=port;
	}
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
	
		String className = method.getDeclaringClass().getName();
		String methodName = method.getName();
		
		Class<?>[] paramsType = method.getParameterTypes();
		
		RpcRequest request = new RpcRequest(className, methodName,
				paramsType, args);

		Channel channel=null;
		RpcResponse response=null;
		try {
			channel=connect(host, port);
			channel.writeAndFlush(request);
			synchronized (rpcClientHandler) {
				rpcClientHandler.wait();
			}			
			response=rpcClientHandler.getResponse();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			channel.close().await();
			close();
		}

		if (response.getException() != null) {
			throw response.getException();
		} else {
			return response.getResult();
		}
	}

	private Channel connect(String host, int port) throws InterruptedException {

		group = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
		bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.group(group);
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline p = ch.pipeline();

				// decoder
				p.addLast(new LoggingHandler(LogLevel.INFO));
				p.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
						0, Integer.BYTES, 0, Integer.BYTES));
				p.addLast(new RpcResponseDeconder());

				// encoder
				p.addLast(new LengthFieldPrepender(Integer.BYTES));
				p.addLast(new RpcRequestEncoder());
				
				//business logic
				p.addLast(rpcClientHandler);

			}
		});

		ChannelFuture f = bootstrap.connect(host, port).sync();

		return f.channel();
	}

	private void close() {
		group.shutdownGracefully();
	}
}
