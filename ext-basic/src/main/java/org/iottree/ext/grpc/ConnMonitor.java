package org.iottree.ext.grpc;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import io.grpc.Attributes;
import io.grpc.Grpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class ConnMonitor
{
	private static final Map<String, ClientConnectionInfo> activeConnections = new ConcurrentHashMap<>();
	private static final List<ConnectionEventListener> listeners = new CopyOnWriteArrayList<>();

	public static class ClientConnectionInfo
	{
		private String clientId;
		private String remoteAddress;
		private long connectTime;
		private long lastActivityTime;
		private int streamCount;
		private StreamObserver<?> mainStream;

	}

	public interface ConnectionEventListener
	{
		void onClientConnected(String clientId, ClientConnectionInfo info);

		void onClientDisconnected(String clientId, ClientConnectionInfo info, String reason);

		void onClientActivity(String clientId);
	}

	public static void registerConnection(String clientId, Attributes attributes)
	{
		ClientConnectionInfo info = new ClientConnectionInfo();
		info.clientId = clientId;
		info.connectTime = System.currentTimeMillis();
		info.lastActivityTime = System.currentTimeMillis();
		info.remoteAddress = getRemoteAddress(attributes);

		activeConnections.put(clientId, info);

		for (ConnectionEventListener listener : listeners)
		{
			listener.onClientConnected(clientId, info);
		}

		System.out.println("客户端连接注册: " + clientId + "，当前活跃连接数: " + activeConnections.size());
	}

	public static void unregisterConnection(String clientId, Status status)
	{
		ClientConnectionInfo info = activeConnections.remove(clientId);
		if (info != null)
		{
			String reason = status.isOk() ? "正常断开" : "异常断开: " + status.getDescription();

			for (ConnectionEventListener listener : listeners)
			{
				listener.onClientDisconnected(clientId, info, reason);
			}

			System.out.println("客户端断开连接: " + clientId + "，原因: " + reason + "，剩余连接数: " + activeConnections.size());
		}
	}

	public static void updateActivity(String clientId)
	{
		ClientConnectionInfo info = activeConnections.get(clientId);
		if (info != null)
		{
			info.lastActivityTime = System.currentTimeMillis();

			for (ConnectionEventListener listener : listeners)
			{
				listener.onClientActivity(clientId);
			}
		}
	}

	public static boolean isClientConnected(String clientId)
	{
		return activeConnections.containsKey(clientId);
	}

	public static int getActiveConnectionCount()
	{
		return activeConnections.size();
	}

	public static List<String> getConnectedClients()
	{
		return new ArrayList<>(activeConnections.keySet());
	}

	private static String getRemoteAddress(Attributes attributes)
	{
		SocketAddress addr = attributes.get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR);
		return addr != null ? addr.toString() : "unknown";
	}
}
