package casia.isiteam.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import casia.isiteam.rpc.common.RpcRequest;
import casia.isiteam.rpc.common.RpcResponse;

class Wrapper {
	Integer id;
	RpcResponse response = null;

	public Wrapper(Integer id) {
		this.id = id;
	}

	public void setRpcResponse(RpcResponse response) {
		this.response = response;
	}

	public RpcResponse getRpcResponse() {
		return this.response;
	}
}

public class RpcInvoker implements InvocationHandler {

	EventLoopGroup group = null;
	Bootstrap bootstrap = null;
	RpcClientHandler rpcClientHandler;;
	String host;
	int port;
	Channel channel;
	AtomicInteger requestID = new AtomicInteger(0);

	ConcurrentMap<Integer, Wrapper> responseMap;
	RuntimeException exception = new RuntimeException(
			"socket channel has been closed!");

	public RpcInvoker(String host, int port) {
		this.host = host;
		this.port = port;
		responseMap = new ConcurrentHashMap<>();
		rpcClientHandler = new RpcClientHandler(responseMap);

		try {
			this.channel = connect(host, port);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}

	public void closeChannel() {
		channel.close();
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		String className = method.getDeclaringClass().getName();
		String methodName = method.getName();

		Class<?>[] paramsType = method.getParameterTypes();

		RpcRequest request = new RpcRequest(requestID.getAndIncrement(),
				className, methodName, paramsType, args);

		if (responseMap == null) {
			throw exception;
		}
		Wrapper wrapper = new Wrapper(request.getID());
		responseMap.put(request.getID(), wrapper);
		channel.writeAndFlush(request);
		synchronized (wrapper.id) {
			while (wrapper.response == null) {
				wrapper.id.wait();
			}

			RpcResponse response = wrapper.getRpcResponse();

			if (responseMap == null || response.getException() != null) {
				System.out.println(response);
				System.out.println(response.getException());
				throw response.getException();
			} else {
				responseMap.remove(request.getID());
				return response.getResult();
			}
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
				p.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
						0, Integer.BYTES, 0, Integer.BYTES));
				p.addLast(new RpcResponseDeconder());

				// encoder
				p.addLast(new LengthFieldPrepender(Integer.BYTES));
				p.addLast(new RpcRequestEncoder());

				// p.addLast(new LoggingHandler(LogLevel.INFO));
				// business logic
				p.addLast(rpcClientHandler);

			}
		});

		ChannelFuture f = bootstrap.connect(host, port).sync();

		f.channel().closeFuture().addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				group.shutdownGracefully();
				setAllFailed();
			}
		});

		return f.channel();
	}

	private void setAllFailed() {
		if (responseMap.size() == 0) {
			return;
		}
		Set<Entry<Integer, Wrapper>> set = responseMap.entrySet();
		responseMap = null;
		for (Entry<Integer, Wrapper> entry : set) {
			Integer key = entry.getKey();
			RpcResponse response = new RpcResponse(key);
			response.setException(exception);
			entry.getValue().setRpcResponse(response);
			synchronized (entry.getValue().id) {
				entry.getValue().id.notifyAll();
			}			
		}
	}
}
