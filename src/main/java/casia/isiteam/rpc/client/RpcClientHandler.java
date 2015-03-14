package casia.isiteam.rpc.client;

import casia.isiteam.rpc.common.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

	RpcResponse response=null;
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, RpcResponse response)
			throws Exception {
		this.response=response;
		synchronized (this) {
			this.notifyAll();
		}
		
		//ctx.close();
	}
	
	public RpcResponse getResponse() {
		return response;
	}

}
