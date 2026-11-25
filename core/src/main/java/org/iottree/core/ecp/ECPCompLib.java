package org.iottree.core.ecp;

import java.util.HashMap;
import java.util.LinkedHashMap;
import org.iottree.core.ecp.ECPComp.Head;
import org.iottree.core.util.IdCreator;
import org.json.JSONObject;

public abstract class ECPCompLib extends ECPNetContainer
{
	String lib_id ;
	
	String lib_title ;
	
	String lib_desc ;
	
	private transient LinkedHashMap<String,ECPComp.Head> heads = null ;
	
	private transient HashMap<String,ECPComp> id2comp = new HashMap<>() ;
	
	public ECPCompLib(String lib_id,String title)
	{
		this.lib_id = lib_id ;
		this.lib_title = title ;
	}
	
	@Override
	public String getContainerTp()
	{
		return "comp";
	}
	

	@Override
	public String getContainerId()
	{
		return this.lib_id ;
	}
	
	public final String getLibId()
	{
		return this.lib_id ;
	}
	
	public final String getLibTitle()
	{
		return this.lib_title ;
	}
	
	public final String getLibDesc()
	{
		return this.lib_desc ;
	}

	
	/**
	 * for edit wrapper
	 */
	@Override
	public void saveNet(ECPNet mnn) throws Exception
	{
		ECPNode nd = null;
		for(ECPNode pn : mnn.id2node.values())
		{
			nd = pn ;
			break ;
		}
		if(nd==null || !(nd instanceof ECPComp))
			throw new IllegalArgumentException("not ECPComp in net") ;
		this.saveComp((ECPComp)nd);
	}
	
	@Override
	public ECPNet delNet(String net_id) throws Exception
	{
		throw new Exception("not supported") ;
	}
	
	public final void setLibBasic(String t,String d) throws Exception
	{
		saveLibBasic(t,d) ;
		this.lib_title = t ;
		this.lib_desc = d ;
	}
	
	public final LinkedHashMap<String,ECPComp.Head> listCompHeads()
	{
		if(this.heads!=null)
			return this.heads ;
		
		synchronized(this)
		{
			if(this.heads!=null)
				return this.heads ;
			try
			{
				this.heads = loadCompHeads() ;
//				for(Head h:heads.values())
//					h.uid = this.getContainerUID()+"-"+h.comp_id ;
				return this.heads;
			}
			catch ( Exception e)
			{
				e.printStackTrace();
				return null ;
			}
		}
	}
	
	public ECPComp getCompById(String comp_id)
	{
		ECPComp comp = this.id2comp.get(comp_id) ;
		if(comp!=null)
			return comp ;
		
		synchronized(this)
		{
			comp = this.id2comp.get(comp_id) ;
			if(comp!=null)
				return comp ;
			JSONObject jo;
			try
			{
				jo = this.loadCompById(comp_id);
			}
			catch ( Exception e)
			{
				e.printStackTrace();
				return null ;
			}
			
			if(jo==null)
				return null ;
			comp = new ECPComp(this,comp_id) ;
			if(!comp.fromJO(jo))
				return null ;
			this.id2comp.put(comp_id,comp) ;
			return comp ;
		}
	}
	
	public final ECPComp addNewComp(String title,String desc) throws Exception
	{
		String newid = IdCreator.newSeqId() ;
		ECPComp cp = new ECPComp(this,newid) ;
		cp.setBasic(title,desc);
		saveComp(cp) ;
		return cp ;
	}
	
	/**
	 * 
	 * @param title
	 * @param desc
	 * @param jo other node struct jo
	 * @return
	 * @throws Exception
	 */
	public final ECPComp addNewCompByNodeJO(String title,String desc,JSONObject jo) throws Exception
	{
		String newid = IdCreator.newSeqId() ;
		ECPComp cp = new ECPComp(this,newid) ;
		cp.setBasic(title,desc);
		cp.updateStructByJO(jo) ;
		saveComp(cp) ;
		return cp ;
	}
	
	public final void saveComp(ECPComp comp) throws Exception
	{
		JSONObject cjo = comp.toJO() ;
		this.saveCompById(comp.getId(), cjo);
		this.updateHead(comp) ;
	}
	
	private void updateHead(ECPComp comp) throws Exception
	{
		Head h = comp.getHead() ;
		h.update_dt = System.currentTimeMillis() ;
		this.listCompHeads().put(h.comp_id,h); 
		this.saveCompHeads(this.listCompHeads()) ;
	}
	
	public final ECPComp removeComp(String comp_id) throws Exception
	{
		ECPComp c = this.getCompById(comp_id) ;
		if(c==null)
			return null ;
		this.saveCompById(comp_id, null);
		this.listCompHeads().remove(comp_id) ;
		this.saveCompHeads(this.listCompHeads());
		return c ;
	}
	
	public final ECPComp setCompBasic(String comp_id,String title,String desc) throws Exception
	{
		ECPComp c = this.getCompById(comp_id) ;
		if(c==null)
			return null ;
		c.setTitle(title);
		this.updateHead(c) ;
		return c ;
	}
	
	protected abstract void saveLibBasic(String title,String desc) throws Exception;
	
	protected abstract LinkedHashMap<String,ECPComp.Head> loadCompHeads() throws Exception;
	
	protected abstract void saveCompHeads(LinkedHashMap<String,ECPComp.Head> heads) throws Exception;
	
	protected abstract JSONObject loadCompById(String comp_id) throws Exception;
	
	protected abstract void saveCompById(String comp_id,JSONObject jo) throws Exception;
}

