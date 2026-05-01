package org.iottree.core.ws;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;

import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.msgnet.MNNet;
import org.iottree.core.msgnet.nodes.NM_WebSocketApi;

public class WSServerMsgNet
{
	public static class SessItem
	{
		private final Session session;
		private final UAPrj prj;
		private final MNNet net;
		private final NM_WebSocketApi wsApi;

		public SessItem(Session s, UAPrj rep, MNNet net, NM_WebSocketApi ws_api)
		{
			this.session = s;
			this.prj = rep;
			this.net = net;
			this.wsApi = ws_api;
		}

		public Session getSession()
		{
			return session;
		}

		public UAPrj getPrj()
		{
			return prj;
		}

		public MNNet getNet()
		{
			return net;
		}

		public NM_WebSocketApi getNode()
		{
			return this.wsApi;
		}

		private void closeSessionOnError(Exception e)
		{
			CloseReason cr = new CloseReason(CloseCodes.CLOSED_ABNORMALLY, e.getMessage());
			try
			{
				session.close(cr);
			}
			catch ( IOException ioe2)
			{
				// Ignore
			}
		}

		public void sendTxt(String txt)
		{
			try
			{
				session.getBasicRemote().sendText(txt);
			}
			catch ( IllegalStateException ise)
			{
				closeSessionOnError(ise);
			}
			catch ( IOException ioe)
			{
				closeSessionOnError(ioe);
			}
		}

		public void sendBin(ByteBuffer bb)
		{
			try
			{
				session.getBasicRemote().sendBinary(bb);
			}
			catch ( IllegalStateException ise)
			{
				closeSessionOnError(ise);
			}
			catch ( IOException ioe)
			{
				closeSessionOnError(ioe);
			}
		}
	}

	private static ConcurrentHashMap<Session, SessItem> sess2item = new ConcurrentHashMap<>();

	public static synchronized void addSessItem(SessItem si)
	{
		sess2item.put(si.getSession(), si);

		// System.out.println(" add session ,num="+sess2item.size()) ;
	}

	public static synchronized void removeSessItem(Session sess)
	{
		sess2item.remove(sess);

		// System.out.println(" remove session ,num="+sess2item.size()) ;
	}
	
	public static SessItem getSessItem(Session sess)
	{
		return sess2item.get(sess) ;
	}

	public static int getSessionNum()
	{
		return sess2item.size();
	}

	public static Collection<SessItem> getSessItems()
	{
		return Collections.unmodifiableCollection(sess2item.values());
	}
	
	
	
	public static void sendTxtOut(NM_WebSocketApi wsapi,String txt)
	{
		for(SessItem si:sess2item.values())
		{
			if(si.wsApi==wsapi)
			{
				si.sendTxt(txt);
			}
		}
	}
	
	public static void sendBinOut(NM_WebSocketApi wsapi,ByteBuffer bb)
	{
		for(SessItem si:sess2item.values())
		{
			if(si.wsApi==wsapi)
			{
				si.sendBin(bb);
			}
		}
	}
}
