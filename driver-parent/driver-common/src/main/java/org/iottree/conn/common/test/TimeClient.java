package org.iottree.conn.common.test;

import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TimeClient
{
	private static final ILogger log = LoggerManager.getLogger(TimeClient.class);
	
	
	public void connect(String host,int port) throws InterruptedException
	{
		EventLoopGroup group= new NioEventLoopGroup() ;
		
		try
		{
			Bootstrap b = new Bootstrap() ;
			b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception
				{
					ch.pipeline().addLast(new TimeClientHandler()) ;
				}}) ;
			
			ChannelFuture f = b.connect(host, port).sync();
			System.out.println("time client conn="+host+":"+port);
			f.channel().closeFuture().sync();
		}
		finally
		{
			group.shutdownGracefully();
		}
	}
	private class TimeClientHandler extends ChannelInboundHandlerAdapter
	{
		private ByteBuf firstMsg = null ;
		
		public TimeClientHandler()
		{
			byte[] req = "que time order".getBytes() ;
			firstMsg = Unpooled.buffer(req.length) ;
			firstMsg.writeBytes(req) ;
		}
		
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception
		{
			super.channelActive(ctx);
			ctx.writeAndFlush(firstMsg) ;
		}
		
		public void channelRead(ChannelHandlerContext ctx,Object msg)
		{
			ByteBuf buf = (ByteBuf)msg ;
			byte[] req = new byte[buf.readableBytes()];
			buf.readBytes(req) ;
			
			System.out.println("now is:"+new String(req)) ;

		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
		{
			// TODO Auto-generated method stub
			super.exceptionCaught(ctx, cause);
			
			log.warn("unexpected exception="+cause.getMessage());
			ctx.close();
		}
		
		
		
	}
	
	
	public static void main(String[] args) throws Exception
	{
		new TimeClient().connect("localhost", 8080);
	}
}
