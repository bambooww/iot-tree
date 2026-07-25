package org.iottree.portal;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.json.JSONArray;
import org.json.JSONObject;

public class Page implements Comparable<Page>
{
	PageCat cat ;
	
	String id = null ;
	
	String name = null ;
	
	String title = null ;
	/**
	 * 页面使用的模板
	 */
	Templet templet = null ;
	
	/**
	 * 模板内容器对应的页面块
	 */
	HashMap<String,PageBlk> name2blks = new HashMap<>() ;
	
	long chgDT = -1 ;
	
	/**
	 * existed page
	 * @param pc
	 * @param id
	 */
	private Page(PageCat pc,String id,long chgdt)
	{
		this.id = id ;
		this.cat = pc ;
		this.chgDT = chgdt ;
	}
	
	/**
	 * new page
	 * @param name
	 * @param temp
	 */
	public Page(PageCat pc,String name,String title,Templet temp)
	{
		this.cat = pc;
		this.id = IdCreator.newSeqId() ;
		this.name = name ;
		this.title = title ;
		this.templet = temp ;
		this.chgDT = System.currentTimeMillis() ;
	}
	
//	public PageCat.Head toHead()
//	{
//		return new PageCat.Head(this.id,this.name, this.title, this.templet.getUID()) ;
//	}
	
	void setBasic(String name,String title,Templet temp)
	{
		this.name = name ;
		this.title = title ;
		this.templet = temp ;
	}
	
	public String getId()
	{
		return this.id ;
	}
	
	public String getPageUID()
	{
		return this.cat.name+"."+this.id ;
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public Templet getTemplet()
	{
		return this.templet ;
	}
	
	public long getChgDT()
	{
		return this.chgDT ;
	}
	
	/**
	 * 根据内部模板块名称，获取模板块
	 * @param blkn
	 * @return
	 */
	public TPageBlk getTempletBlk(String blkn)
	{
		if(this.templet==null)
			return null ;
		TPage tpage = this.templet.getTPage() ;
		if(tpage==null)
			return null ;
		return tpage.getPageBlocks().get(blkn) ;
	}
	
	public PageBlk getPageBlk(String blkn)
	{
		return this.name2blks.get(blkn) ;
	}
	
	public boolean setPageBlk(String blkn,String pblk_tp,JSONObject blk_jo,StringBuilder failedr) throws IOException
	{
		PageBlk pb = PageBlk.transFromJO(this,blkn, pblk_tp,blk_jo) ;
		if(pb==null)
		{
			failedr.append("create PageBlk err");
			return false;
		}
		this.name2blks.put(blkn,pb) ;
		this.cat.setAndSavePage(this);
		return true ;
	}
	
	public JSONObject toListJO()
	{
		JSONObject tmpjo = new JSONObject() ;
		tmpjo.put("cat",this.cat.getName()) ;
		tmpjo.put("cat_title",cat.getTitle()) ;
		tmpjo.put("page_id",this.getId()) ;
		tmpjo.put("page_uid",this.getPageUID()) ;
		tmpjo.putOpt("page_name",this.getName()) ;
		tmpjo.putOpt("page_title",this.getTitle()) ;
		tmpjo.put("templet_uid",this.templet.getUID()) ;
		tmpjo.put("templet_title",this.templet.getTitle()) ;
		return tmpjo;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		//jo.put("uid", this.getPageUID()) ;
		jo.put("id",id) ;
		jo.putOpt("n",name) ;
		jo.putOpt("t", this.title) ;
		jo.put("templet_uid",this.templet.getUID()) ;
		
		JSONArray blks_jarr = new JSONArray() ;
		jo.put("blks",blks_jarr) ;
		for(PageBlk pb:this.name2blks.values())
		{
			blks_jarr.put(pb.toJO()) ;
		}
		return jo ;
	}
	
	public static Page fromJO(PageCat pc,String id,long chgdt,JSONObject jo)
	{
		Page p = new Page(pc,id,chgdt) ;
		p.id = jo.optString("id") ;
		p.name = jo.optString("n") ;
		p.title =  jo.optString("t",p.name) ;
		
		String temp_uid = jo.optString("templet_uid") ;
		if(Convert.isNullOrEmpty(p.id))
			return null ;
		
		p.templet = PortalManager.getInstance().getTempletByUID(temp_uid) ;
		if(p.templet==null)
			return null ;
		
		p.setDetailJO(jo);
		return p ;
	}
	

	boolean setDetailJO(JSONObject jo)
	{
		JSONArray blks_jarr = jo.optJSONArray("blks") ;
		HashMap<String,PageBlk> name2blks = new HashMap<>() ;
		if(blks_jarr!=null)
		{
			int n = blks_jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = blks_jarr.getJSONObject(i);
				PageBlk pb = PageBlk.transFromJO(this, tmpjo) ;
				if(pb==null)
					continue ;
				name2blks.put(pb.getBlkName(),pb) ;
			}
		}
		this.name2blks = name2blks;
		return true;
	}
	

	@Override
	public int compareTo(Page o)
	{
		if(o.chgDT > this.chgDT)
			return 1 ;
		else if(o.chgDT<this.chgDT)
			return -1 ;
		return 0 ;
	}
	

	public void renderTempletSor(Writer out) throws IOException
	{
		Templet temp = this.getTemplet() ;
		if(temp==null)
		{
			out.write("page has no templet found") ;
			return ;
		}
		temp.renderOutSor(out) ;
	}
	
	public void renderPageSetup(Writer out) throws IOException
	{
		Templet temp = this.getTemplet() ;
		if(temp==null)
		{
			out.write("page has no templet found") ;
			return ;
		}
		TPage tpage = temp.getTPage(true) ;
		if(tpage==null)
		{
			out.write("no TPage found in templet") ;
			return ;
		}
		
		for(Object obj : tpage.getContList())
		{
			if(obj instanceof String)
				out.write((String)obj);
			else if(obj instanceof TPageBlk)
			{
				TPageBlk blk = (TPageBlk)obj ;
				String blkn = blk.getBlkName() ;
				if(Convert.isNullOrEmpty(blkn))
					continue ;
				PageBlk pblk = this.name2blks.get(blkn) ;
				blk.renderOutSetup(out,pblk);
			}	
		}
	}
	
	public void RT_renderPage(Writer out) throws IOException
	{
		Templet temp = this.getTemplet() ;
		if(temp==null)
		{
			out.write("page has no templet found") ;
			return ;
		}
		TPage tpage = temp.getTPage(true) ;
		if(tpage==null)
		{
			out.write("no TPage found in templet") ;
			return ;
		}
		
		for(Object obj : tpage.getContList())
		{
			if(obj instanceof String)
			{
				out.write((String)obj);
				continue ;
			}
			if(!(obj instanceof TPageBlk))
				continue ;

			TPageBlk tblk = (TPageBlk)obj ;
			PageBlk pblk = null;
			String blkn = tblk.getBlkName() ;
			if(Convert.isNotNullEmpty(blkn))
			{
				pblk = this.name2blks.get(blkn) ;
			}
			tblk.RT_renderOut(out,this,pblk);
		}
	}
	
	
}
