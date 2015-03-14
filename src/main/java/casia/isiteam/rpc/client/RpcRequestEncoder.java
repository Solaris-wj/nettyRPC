package casia.isiteam.rpc.client;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import casia.isiteam.rpc.common.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcRequestEncoder extends MessageToByteEncoder<RpcRequest> {

	@Override
	protected void encode(ChannelHandlerContext ctx, RpcRequest request, ByteBuf out)
			throws Exception {
		ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream=new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(request);
		byte[] dst=byteArrayOutputStream.toByteArray();
		//out=Unpooled.copiedBuffer(dst);		
		out.writeBytes(dst);
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
	}
}
