package casia.isiteam.rpc.server;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import casia.isiteam.rpc.common.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcResponseEncoder extends MessageToByteEncoder<RpcResponse> {

	@Override
	protected void encode(ChannelHandlerContext ctx, RpcResponse msg,
			ByteBuf buf) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
		ObjectOutputStream objectOutput=new ObjectOutputStream(byteArrayOutputStream);		
		objectOutput.writeObject(msg);		
		byte[] dst=byteArrayOutputStream.toByteArray();		
		//buf=Unpooled.copiedBuffer(dst);
		buf.writeBytes(dst);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
	}
}
