package org.iottree.conn.common.test;

import java.util.Date;

import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
//import io.netty.handler.codec.http.FullHttpRequest;
//import io.netty.handler.codec.http.FullHttpResponse;
//import io.netty.handler.codec.http.HttpObjectAggregator;
//import io.netty.handler.codec.http.HttpServerCodec;
//import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
//import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
//import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
//import io.netty.handler.codec.http.websocketx.WebSocketFrame;
//import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
//import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

public class WebSocketServer
{
//	private static final ILogger log = LoggerManager.getLogger(WebSocketServer.class);
//
//	public void bind(int port) throws InterruptedException
//	{
//		EventLoopGroup bossg = new NioEventLoopGroup();
//		EventLoopGroup workerg = new NioEventLoopGroup();
//
//		try
//		{
//			ServerBootstrap b = new ServerBootstrap();
//			b.group(bossg, workerg).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024)
//					.childHandler(new ChannelInitializer<SocketChannel>() {
//						@Override
//						protected void initChannel(SocketChannel ch) throws Exception
//						{
//							ChannelPipeline pl = ch.pipeline();
//							pl.addLast("http-codec", new HttpServerCodec());
//							pl.addLast("aggregator", new HttpObjectAggregator(65536));
//							pl.addLast("http-chunked", new ChunkedWriteHandler());
//							pl.addLast("handler", new WebSocketServerHandler());
//						}
//					});
//
//			ChannelFuture f = b.bind(port).sync();
//			System.out.println("web socket server bind port=" + port);
//			System.out.println(" browser url http://localhost:"+port+"/");
//			f.channel().closeFuture().sync();
//		}
//		finally
//		{
//			bossg.shutdownGracefully();
//			workerg.shutdownGracefully();
//		}
//	}
//
//	private class WebSocketServerHandler extends SimpleChannelInboundHandler<Object>
//	{
//		private WebSocketServerHandshaker handshaker ;
//		
//		public WebSocketServerHandler()
//		{
//
//		}
//
//
//		@Override
//		protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception
//		{
//			if(msg instanceof FullHttpRequest)
//			{
//				handleHttpReq(ctx,(FullHttpRequest)msg) ;
//			}
//			else if(msg instanceof WebSocketFrame)
//			{
//				handleWebSocketFrame(ctx,(WebSocketFrame)msg) ;
//			}
//		}
//		
//		@Override
//		public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
//		{
//			ctx.flush();
//		}
//		
//		private void handleHttpReq(ChannelHandlerContext ctx, FullHttpRequest req)
//		{
//			if(!req.getDecoderResult().isSuccess()
//					|| (!"websocket".contentEquals(req.headers().get("Upgrade")))
//					)
//			{
//				//sendHttpResponse(ctx,req,new DefaultFullHttpResponse(HTTP_1_1,BAD_REQUEST));
//				return ;
//			}
//			
//			WebSocketServerHandshakerFactory wsFac = new WebSocketServerHandshakerFactory(
//					"ws://localhost:8080/websocket",null,false) ;
//			handshaker = wsFac.newHandshaker(req);
//			if(handshaker==null)
//			{
//				WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
//			}
//			else
//			{
//				handshaker.handshake(ctx.channel(), req);
//			}
//		}
//		
//		private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame sockf)
//		{
//			if(sockf instanceof CloseWebSocketFrame)
//			{
//				ctx.channel().write(new PongWebSocketFrame(sockf.content().retain()));
//				return ;
//			}
//			
//			if(!(sockf instanceof TextWebSocketFrame))
//			{
//				throw new UnsupportedOperationException("not text frame type");
//			}
//			
//			String req_txt = ((TextWebSocketFrame)sockf).text() ;
//			log.info(String.format("%s recved %s", ctx.channel(),req_txt));
//			
//			ctx.channel().write(new TextWebSocketFrame(req_txt+", weclome "+new Date()));
//		}
//		
//		
//		private void sendHttpResponse(ChannelHandlerContext ctx,FullHttpRequest req,FullHttpResponse res)
//		{
////			if(res.getStatus().code()!=200)
////			{
////				ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(),CharsetUtil.UTF_8);
////				res.content().writeBytes(buf);
////				buf.release();
////				sendContentLength(res,res.content().readableBytes());
////			}
////			
////			//
////			ChannelFuture f = ctx.channel().writeAndFlush(res);
////			
////			if(!isKeepAlive(req) || res.getStatus().code()!=200)
////			{
////				
////			}
//		}
//	}
	
	
	

	public static void main(String[] args) throws Exception
	{
		new TimeServer().bind(8080);
	}
}
