package org.iottree.ext.grpc;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.iottree.core.util.*;

import io.grpc.Attributes;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Grpc;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.ServerTransportFilter;

public class ConnStateMonitor extends ServerTransportFilter implements ServerInterceptor
{
	public static class ConnItem
	{
		public String addr ;
		
		public String clientId ;
		
		public long connDT ;
		
		public long activeDT ;
		
		public ConnItem(String addr,String clientid)
		{
			this.addr = addr ;
			this.clientId = clientid ;
			this.activeDT = this.connDT = System.currentTimeMillis() ;
		}
		
		private void fireActive(String clientid)
		{
			this.clientId = clientid ;
			this.activeDT = System.currentTimeMillis() ;
		}
		
		public String toString()
		{
			return this.clientId+"@"+addr +" connected="+Convert.toFullYMDHMS(new Date(this.connDT))
				+" actived="+Convert.toFullYMDHMS(new Date(this.activeDT));
		}
	}
	
	private static final Map<String,ConnItem> ADDR2CONN =  new ConcurrentHashMap<>();
	
	private static final Map<String, Boolean> clientConnections = new ConcurrentHashMap<>();
	
	public static List<ConnItem> listConnItems()
	{
		ArrayList<ConnItem> rets = new ArrayList<>() ;
		rets.addAll(ADDR2CONN.values()) ;
		return rets;
	}
	
	private final List<CallListener> listeners = new CopyOnWriteArrayList<>();

	@Override
	public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
			ServerCallHandler<ReqT, RespT> next)
	{
		String clientId = extractClientId(headers);
		Attributes attrs = call.getAttributes() ;
		String method = call.getMethodDescriptor().getBareMethodName() ;
		String remoteAddress = attrs.get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR).toString();
		String clientKey = clientId + "@" + remoteAddress;

		boolean isNewConnection = !clientConnections.containsKey(clientKey);

		if (isNewConnection)
		{
			clientConnections.put(clientKey, true);
			notifyNew(method,clientId, remoteAddress);
		}
		else
		{
		}

		return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(
				next.startCall(call, headers)) {

			@Override
			public void onCancel()
			{
				clientConnections.remove(clientKey);
				notifyEnd(method,clientId, remoteAddress, "client cancel");
				super.onCancel();
			}

			@Override
			public void onComplete()
			{
				clientConnections.remove(clientKey);
				notifyEnd(method,clientId, remoteAddress, "complete");
				super.onComplete();
			}

			@Override
			public void onHalfClose()
			{
				super.onHalfClose();
			}
		};
	}

	private String extractClientId(Metadata headers)
	{
		String clientId = headers.get(Metadata.Key.of("client-id", Metadata.ASCII_STRING_MARSHALLER));
		return clientId != null ? clientId : "unknown";
	}

	private void notifyNew(String method,String clientId, String remoteAddress)
	{
		ConnItem ci = ADDR2CONN.get(remoteAddress) ;
		if(ci==null)
			return ;
		if(IOTTreeServerImpl.log.isDebugEnabled())
			IOTTreeServerImpl.log.debug("call method "+method+" "+clientId) ;
		ci.fireActive(clientId);
		
		for (CallListener listener : listeners)
		{
			listener.onNewCall(clientId, remoteAddress);
		}
	}

	private void notifyEnd(String method,String clientId, String remoteAddress, String reason)
	{
		ConnItem ci = ADDR2CONN.get(remoteAddress) ;
		if(ci==null)
			return ;
		if(IOTTreeServerImpl.log.isDebugEnabled())
			IOTTreeServerImpl.log.debug("end method "+method+" "+clientId) ;
		ci.fireActive(clientId);
		
		for (CallListener listener : listeners)
		{
			listener.onEndCall(clientId, remoteAddress, reason);
		}
	}

	void addListener(CallListener listener)
	{
		listeners.add(listener);
	}

	void removeListener(CallListener listener)
	{
		listeners.remove(listener);
	}

	public static int getActiveConnections()
	{
		return clientConnections.size();
	}
	
	@Override
	public Attributes transportReady(Attributes transportAttrs)
	{
		if(transportAttrs==null)
			return null ;
		if(IOTTreeServerImpl.log.isDebugEnabled())
			IOTTreeServerImpl.log.debug("transportReady="+transportAttrs.toString());
		
		SocketAddress address = transportAttrs.get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR) ;
		if(address==null)
			return transportAttrs;
		String addr = address.toString() ;
		ADDR2CONN.put(addr,new ConnItem(addr,null)) ;
		return transportAttrs;
	}

	@Override
	public void transportTerminated(Attributes transportAttrs)
	{
		if(transportAttrs==null)
			return ;
		SocketAddress address = transportAttrs.get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR) ;
		if(address==null)
			return ;
		String addr = address.toString() ;
		if(IOTTreeServerImpl.log.isDebugEnabled())
			IOTTreeServerImpl.log.debug("transportTerminated="+transportAttrs.toString());
		ADDR2CONN.remove(addr) ;
	}

	public interface CallListener
	{
		void onNewCall(String clientId, String remoteAddress);

		void onEndCall(String clientId, String remoteAddress, String reason);
	}
	
}
