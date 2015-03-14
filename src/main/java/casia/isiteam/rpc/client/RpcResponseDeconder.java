package casia.isiteam.rpc.client;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class RpcResponseDeconder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		//链接关闭的时候 netty会发送一个空包到decode里。。。
		if(in.readableBytes()==0) {
			return;
		}
		byte[] buf=new byte[in.readableBytes()];
		in.readBytes(buf);

		ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(buf);
		ObjectInputStream objectInputStream=new ObjectInputStream(byteArrayInputStream);
		out.add(objectInputStream.readObject());		
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
	}
}
