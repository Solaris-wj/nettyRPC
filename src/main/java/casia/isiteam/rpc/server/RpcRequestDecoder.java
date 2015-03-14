package casia.isiteam.rpc.server;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class RpcRequestDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg,
			List<Object> out) throws Exception {
		if(msg.readableBytes()==0) {
			return;
		}
		byte[] buf = new byte[msg.readableBytes()];
		msg.readBytes(buf);

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				buf);
		ObjectInputStream objectInputStream = new ObjectInputStream(
				byteArrayInputStream);
		out.add(objectInputStream.readObject());

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
	}
}
