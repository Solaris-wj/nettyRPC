package casia.isiteam.rpc.server;

import java.lang.reflect.Method;

import casia.isiteam.rpc.common.RpcRequest;
import casia.isiteam.rpc.common.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

	RpcServer rpcServer;

	public RpcServerHandler(RpcServer rpcServer) {
		this.rpcServer = rpcServer;
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, RpcRequest request)
			throws Exception {

		String className = request.getClassName();
		Class<?> cls = Class.forName(className);
		Class<?>[] paramsCls=request.getParamClasses();
		
		Method method = cls.getMethod(request.getMethodName(), paramsCls);

		Object[] parameters = request.getParams();

		RpcResponse response = new RpcResponse();
		// get handler
		Object object = rpcServer.getRpcObject(className);
		if (object == null) {
			response.setException(new ClassNotFoundException());
		} else {
			try {
				// invoke
				Object result = method.invoke(object, parameters);
				response.setResult(result);
			} catch (Throwable cause) {
				response.setException(cause);
			}
		}
		ctx.writeAndFlush(response);
	}

}
