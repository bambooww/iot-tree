package org.iottree.core.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.script.ScriptException;

import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UANode;
import org.iottree.core.ai.AIDevDef;
import org.iottree.core.ai.AIRole;
import org.iottree.core.ai.AIRoleManager;
import org.iottree.core.cxt.JsProp;
import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.cxt.MNContext;
import org.iottree.core.util.Convert;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONArray;
import org.json.JSONObject;

public class NM_AIRole extends MNNodeMid implements IMNRunner
{
	String ai_role_tp = null ;
	
	AIRole ai_role = null ;
	
	long intv_ms = 3000 ;
	
	List<String> related_devids = null ;
	
	boolean b_all_devs = false; //
	
	private transient List<UADev> related_devs = null ;
	
	@Override
	public String getColor()
	{
		return "#ffde80";
	}
	
	@Override
	public String getIcon()
	{
		return "PK_ai_role";
	}

	@Override
	public JSONTemp getInJT()
	{
		return null;
	}

	@Override
	public JSONTemp getOutJT()
	{
		return null;
	}

	@Override
	public int getOutNum()
	{
		return 2;
	}
	
	@Override
	public String getOutColor(int idx)
	{
		if(idx==0)
			return null;
		if(idx==1)
			return "red";
		return null ;
	}

//	@Override
	public String getTP()
	{
		return "ai_role";
	}

	@Override
	public String getTPTitle()
	{
		return g("ai_role");
	}
	
	public List<UADev> getRelatedDevs()
	{
		if(this.related_devs!=null)
			return this.related_devs ;
		
		if(this.related_devids==null||this.related_devids.size()<=0)
			return this.related_devs = Arrays.asList() ;
		
		ArrayList<UADev> rets = new ArrayList<>() ;
		if(b_all_devs)
		{
			for(UACh ch:this.getPrj().getChs())
			{
				for(UADev d:ch.getDevs())
				{
					AIDevDef dd = d.getAIDevDef() ;
					if(dd==null)
						continue ;
					rets.add(d) ;
				}
			}
		}
		else
		{
			for(String did:this.related_devids)
			{
				UANode nd = this.getPrj().findNodeById(did) ;
				if(nd==null || !(nd instanceof UADev))
					continue ;
				UADev dev = (UADev)nd ;
				rets.add(dev) ;
			}
		}
		return this.related_devs = rets ;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.ai_role_tp))
		{
			failedr.append("no ai role type set") ;
			return false;
		}
		List<UADev> devs = this.getRelatedDevs() ;
		if(devs==null || devs.size()<=0)
		{
			failedr.append("no device set") ;
			return false;
		}
		return true;
	}
	
	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		jo.putOpt("ai_role_tp", this.ai_role_tp) ;
		jo.put("all_devs", this.b_all_devs) ;
		jo.putOpt("dev_ids", this.related_devids) ;
		if(ai_role!=null)
		{
			JSONObject ai_jo = ai_role.toJO() ;
			jo.put("ai_role", ai_jo) ;
		}
		jo.put("intv_ms", this.intv_ms) ;
		
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.ai_role_tp = jo.getString("ai_role_tp") ;
		this.b_all_devs = jo.optBoolean("all_devs") ;
		JSONArray jarr = jo.optJSONArray("dev_ids") ;
		int n = 0 ;
		if(jarr!=null && (n=jarr.length())>0)
		{
			this.related_devids = new ArrayList<>() ;
			for(int i = 0 ; i < n ; i ++)
			{
				String devid = jarr.getString(i) ;
				this.related_devids.add(devid) ;
			}
		}
		JSONObject ai_jo = jo.optJSONObject("ai_role") ;
		this.ai_role = AIRoleManager.getInstance(this.getPrj()).createAIRole(this.ai_role_tp, this.getId(), ai_jo) ;
		this.intv_ms = jo.optLong("intv_ms",3000) ;
		this.related_devs=null ;
	}
	
	@Override
	protected List<JsProp> JS_getGlobalPropsCxtExts()
	{
		List<UADev> devs = getRelatedDevs() ;
		if(devs==null || devs.size()<=0)
			return null ;
		ArrayList<JsProp> rets = new ArrayList<>() ;
		for(UADev dev:devs)
		{
			String varn = dev.getBelongTo().getName()+"$"+dev.getName() ;
			rets.add(new JsProp(varn,dev,null,true,"use_title",dev.getUseTitle()).asTpTitle("UADev")) ;
		}
		
		return rets ;
	}
	
	//
	
	private MNContext context = null ;
	
	private MNContext getCxt() throws ScriptException
	{
		if(this.context!=null)
			return this.context ;
		this.context = new MNContext(this.getBelongTo(),this);
		List<UADev> devs = getRelatedDevs() ;
		if(devs!=null && devs.size()>0)
		{
			for(UADev dev:devs)
			{
				String varn = dev.getBelongTo().getName()+"$"+dev.getName() ;
				this.context.asNameOb(varn, dev) ;
			}
		}
		return this.context ;
	}
	
	// --------------
	
	private void RT_runInLoop() throws ScriptException
	{
		if(this.ai_role==null)
			return ;
		MNContext cxt = this.getCxt() ;
		this.ai_role.RT_runInLoop(cxt);
	}
	
	private boolean bRun = false;
	
	private Thread procTh = null ;

	
	private Runnable runner = new Runnable()
	{
		public void run()
		{
			try
			{
				while(bRun)
				{
					try
					{
						RT_runInLoop() ;
					}
					catch ( ScriptException e)
					{
						e.printStackTrace();
					}
					
					sleep(NM_AIRole.this.intv_ms) ;
				}
			}
			finally
			{
				procTh = null ;
				bRun = false;
			}
		}
	};
	
	
	private void procModeLastestPri()
	{
		//throw new RuntimeException("not impl") ;
		this.RT_DEBUG_ERR.fire("send_out","not impl");
	}

		
	@Override
	protected void RT_renderDiv(List<DivBlk> divblks)
	{
		StringBuilder divsb = new StringBuilder() ;
		divsb.append("<div class=\"rt_blk\">") ;
		divsb.append("") ;
		divsb.append("</div>") ;
		divblks.add(new DivBlk("memque",divsb.toString())) ;
		
		super.RT_renderDiv(divblks);
	}
	
	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		
		return null;//
	}
	
	@Override
	public synchronized boolean RT_start(StringBuilder failedr)
	{
		if (bRun)
			return true;

		bRun = true;
		procTh = new Thread(runner);
		procTh.start();
		return true;
	}

	@Override
	public synchronized void RT_stop()
	{
		Thread th = procTh;
		if (th != null)
			th.interrupt();
		bRun = false;
		procTh = null;
	}

	@Override
	public boolean RT_isRunning()
	{
		return bRun;
	}

	@Override
	public boolean RT_isSuspendedInRun(StringBuilder reson)
	{
		return false;
	}
	
	/**
	 * false will not support runner
	 * @return
	 */
	public boolean RT_runnerEnabled()
	{
		return true ;
	}
	
	/**
	 * true will not support manual trigger to start
	 * @return
	 */
	public boolean RT_runnerStartInner()
	{
		return false;
	}
	

	@Override
	public String RT_getOutTitle(int idx)
	{
		if(idx==0)
			return null;
		if(idx==1)
			g("warn_or_exceed") ;
		return null ;
	}
	
	@Override
	public String RT_getOutColor(int idx)
	{
		if(idx==0)
			return null;
		if(idx==1)
		{
//			if(System.currentTimeMillis()-RT_lastErrDT<3000)
//				return "red";
//			
//			if(System.currentTimeMillis()-RT_lastWarnDT<3000)
//				return "yellow";
			return null ;
		}
		return null ;
	}

}
