package org.iottree.conn.common.test;

import java.util.Date;

import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TimeServer
{
	private static final ILogger log = LoggerManager.getLogger(TimeServer.class);
	
	
	public void bind(int port) throws InterruptedException
	{
		EventLoopGroup bossg  = new NioEventLoopGroup() ;
		EventLoopGroup workerg  = new NioEventLoopGroup() ;
		
		try
		{
		ServerBootstrap b = new ServerBootstrap() ;
		b.group(bossg, workerg).channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 1024)
			.childHandler(new ChildChannelHandler());
		
			ChannelFuture f = b.bind(port).sync();
		System.out.println("time server bind port="+port) ;
		f.channel().closeFuture().sync() ;
		}
		finally
		{
			bossg.shutdownGracefully();
			workerg.shutdownGracefully();
		}
	}
	
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel>
	{

		@Override
		protected void initChannel(SocketChannel ch) throws Exception
		{
			ch.pipeline().addLast(new TimeServerHandler());
		}
		
	}
	
	

	private class TimeServerHandler extends ChannelInboundHandlerAdapter
	{
		
		public TimeServerHandler()
		{
			
		}
		
		
		
		public void channelRead(ChannelHandlerContext ctx,Object msg)
		{
			ByteBuf buf = (ByteBuf)msg ;
			byte[] req = new byte[buf.readableBytes()];
			buf.readBytes(req) ;
			
			System.out.println("time server recv is:"+new String(req)) ;

			String txt = " "+Convert.toFullYMDHMS(new Date()) ;
			ByteBuf resp = Unpooled.copiedBuffer(txt.getBytes()) ;
			ctx.write(resp) ;
		}
		
		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
		{
			super.channelReadComplete(ctx);
			ctx.flush() ;//write to socketchannel
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
		{
			// TODO Auto-generated method stub
			//super.exceptionCaught(ctx, cause);
			
			System.out.println("unexpected exception="+cause.getMessage());
			ctx.close();
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		new TimeServer().bind(8080);
	}
}
