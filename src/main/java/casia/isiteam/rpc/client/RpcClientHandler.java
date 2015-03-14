package casia.isiteam.rpc.client;

import java.util.concurrent.ConcurrentMap;

import casia.isiteam.rpc.common.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

	
	ConcurrentMap<Integer,Wrapper> requestMap;
	public RpcClientHandler(ConcurrentMap<Integer,Wrapper> requestMap) {
		this.requestMap=requestMap;
	}
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, RpcResponse response)
			throws Exception {
		Wrapper wrapper=requestMap.get(response.getID());
		if(wrapper==null) {
			//FIXME: error handling :no request is waiting for a response
			System.err.println("no request is waiting for a response");
			return ;
		}
		wrapper.setRpcResponse(response);
		synchronized (wrapper.id) {
			wrapper.id.notifyAll();
		}
	}
}
