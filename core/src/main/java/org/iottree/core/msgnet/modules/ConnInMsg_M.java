package org.iottree.core.msgnet.modules;

import java.util.HashSet;
import java.util.List;

import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;
import org.iottree.core.UAPrj;
import org.iottree.core.conn.ConnPtMSGNor;
import org.iottree.core.conn.ConnPtMsg;
import org.iottree.core.msgnet.MNModule;
import org.iottree.core.msgnet.MNNet;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class ConnInMsg_M extends MNModule
{
	public static final String TP = "conn_in_msg" ;
	
	String connPtMsgId = null ;
	
	ConnMsgOut_NE nMsgOut = null ;
	ConnMsgIn_NS nMsgIn = null ;
	
	@Override
	public String getTP()
	{
		return TP;
	}

	@Override
	public String getTPTitle()
	{
		return g(TP);
	}

	@Override
	public String getColor()
	{
		return "#007cb7";
	}
	
	@Override
	public String getTitleColor()
	{
		return "#dddddd" ;
	}

	@Override
	public String getIcon()
	{
		return "\\uf0c1";
	}
	
	public String getConnPtMsgId()
	{
		return this.connPtMsgId ;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		ConnPtMsg cpt = this.getConnPt() ;
		if(cpt==null)
		{
			failedr.append("no conn id set or no conn msg with id="+this.connPtMsgId+" found") ;
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("conn_pt_id",this.connPtMsgId) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.connPtMsgId = jo.optString("conn_pt_id","") ;
	}
	
	public String getPmTitle()
	{
		ConnPtMsg cpt = this.getConnPt() ;
		if(cpt==null)
			return null ;
		return cpt.getTitle();
	}
	
	protected void onAfterLoaded()
	{
			List<MNNode> nodes = this.getRelatedNodes() ;
			
			ConnPtMsg ptmsg = getConnPt() ;
			
			ConnMsgOut_NE n_msg_out = null ;
			ConnMsgIn_NS n_msg_in = null ;
			if(nodes!=null)
			{
				for(MNNode n:nodes)
				{
					if(n instanceof ConnMsgOut_NE)
					{
						ConnMsgOut_NE num = (ConnMsgOut_NE)n ;
						
						if(ptmsg!=null)
						{
							n_msg_out =num ;
						}
					}
					
					if(n instanceof ConnMsgIn_NS)
					{
						ConnMsgIn_NS nnn = (ConnMsgIn_NS)n ;
						
						if(ptmsg!=null)
						{
							n_msg_in = nnn ;
						}
					}
				}
			}
			
			nMsgOut = n_msg_out ;
			nMsgIn = n_msg_in ;
	}

	ConnPtMsg getConnPt()
	{
		if(Convert.isNullOrEmpty(connPtMsgId))
		{
			return null ;
		}
		
		UAPrj prj = (UAPrj)this.getBelongTo().getContainer() ;
		for(ConnProvider cp:prj.getConnProviders())
		{
			ConnPt cpt = cp.getConnById(this.connPtMsgId) ;
			if(cpt!=null)
			{
				if(cpt instanceof ConnPtMsg)
					return (ConnPtMsg)cpt ;
				else
					return null ;
			}
		}
		return null ;
	}
	
	
	
	@Override
	public void checkAfterSetParam()
	{
		try
		{
			updateSendRecvNodes() ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
	}
	
	private void updateSendRecvNodes() throws Exception
	{
		List<MNNode> nodes = this.getRelatedNodes() ;
		float me_x = this.getX() ;
		float me_y = this.getY() ;
		
		MNNet net = this.getBelongTo() ;
		
		ConnPtMsg ptmsg = getConnPt() ;
		
		ConnMsgOut_NE n_msg_out = null ;
		ConnMsgIn_NS n_msg_in = null ;
		boolean bdirty = false;
		if(nodes!=null)
		{
			for(MNNode n:nodes)
			{
				if(n instanceof ConnMsgOut_NE)
				{
					ConnMsgOut_NE num = (ConnMsgOut_NE)n ;
					if(ptmsg==null)
					{
						net.delNodeById(n.getId(), false) ;
						bdirty = true ;
					}
					else
					{
						n_msg_out =num ;
					}
				}
				
				if(n instanceof ConnMsgIn_NS)
				{
					ConnMsgIn_NS nnn = (ConnMsgIn_NS)n ;
					if(ptmsg==null)
					{
						net.delNodeById(n.getId(), false) ;
						bdirty = true ;
					}
					else
					{
						n_msg_in = nnn ;
					}
				}
			}
		}
		
		if(ptmsg!=null)
		{
			MNNode sup_in = this.getSupportedNodeByTP(ConnMsgIn_NS.TP);
			MNNode sup_out = this.getSupportedNodeByTP(ConnMsgOut_NE.TP);
			
			if(n_msg_out==null)
			{
				n_msg_out = (ConnMsgOut_NE)net.createNewNodeInModule(this,sup_out,me_x+250, me_y-33,null,false) ;
				//newn.setTitle(dev.getShowTitle()+"["+dev.topic+"]") ;
				//n_msg_out.connPtMsgId = this.connPtMsgId ;
				bdirty = true ;
			}
			
			if(n_msg_in==null)
			{
				n_msg_in = (ConnMsgIn_NS)net.createNewNodeInModule(this,sup_in,me_x-200, me_y-83,null,false) ;
				//n_msg_in.connPtMsgId = this.connPtMsgId ;
				bdirty = true ;
			}
		}
		
		if(bdirty)
			net.save();
		
		nMsgOut = n_msg_out ;
		nMsgIn = n_msg_in ;
	}
	
	public ConnMsgOut_NE getNodeMsgOut()
	{
		return this.nMsgOut ;
	}
	
	public ConnMsgIn_NS getNodeMsgIn()
	{
		return this.nMsgIn ;
	}
}
