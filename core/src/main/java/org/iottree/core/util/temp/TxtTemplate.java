package org.iottree.core.util.temp;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * @author Jason Zhu
 */
public class TxtTemplate
{
	public static final String SUB_BEGIN = "__sub_begin" ;
	
	public static final String SUB_END = "__sub_end" ;
	
	public static final String SUB_IDX = "__idx" ;
	
	public static final String SUB_PROP_NAME = "name" ;
	public static final String SUB_PROP_ARRAY = "array" ;
	
	ArrayList<Object> contList = new ArrayList<>() ;
	int id = -1 ;
	
	String name = null ;
	
	boolean bSub = false;
	
	boolean bSubArray = false;
	
	String compatibleName = null ;
	String path = null;
	
	transient ArrayList<TxtBlock> blocks = null ;
	
	transient TxtTemplate parent = null ;
	
	public TxtTemplate(String path, String temp_cont)
	{
		this(-1,path,temp_cont);
	}
	
	public TxtTemplate(int id, String temp_cont)
	{
		this(id,null,temp_cont);
	}
	
	public TxtTemplate(int id,String path, String temp_cont)
	{
		this.id = id ;
		this.path = path;

		if (temp_cont == null || temp_cont.equals(""))
			throw new IllegalArgumentException("temp cont cannot be null!");

		while(temp_cont!=null)
		{
			int sp = temp_cont.indexOf("[#");
			if (sp < 0)
			{
				if(!temp_cont.equals(""))
					contList.add(temp_cont);
				temp_cont = null;
				break ;
			}
			
			int ep = temp_cont.indexOf("#]", sp + 2);
			if (ep < 0)
				throw new IllegalArgumentException(
						"invalid template it must split by string like [#  #]");

			String tmps = temp_cont.substring(0, sp);
			if(!tmps.equals(""))
				contList.add(tmps);
			
			String bn = temp_cont.substring(sp+2,ep).trim();
			TxtBlock bi = new TxtBlock(bn);
			//System.out.println("find block==="+bn);
			contList.add(bi);
			
			temp_cont = temp_cont.substring(ep + 2);
		}
		List<Object> contlist = contList;
		this.contList = new ArrayList<>() ;
		constructSub(this,contlist,0);
	}
	
	private TxtTemplate(TxtTemplate parent,
			String name,boolean b_sub,boolean b_array)
	{
		this.parent = parent ;
		this.name = name ;
		this.bSub = b_sub ;
		this.bSubArray = b_array ;
		//this.contList = contlist ;
	}
	
	private static int constructSub(TxtTemplate cur_temp,List<Object> contlist,int idx)
	{
		//ArrayList<Object> newlist = new ArrayList<>() ;
		int len = contlist.size() ;
		for(int i = idx ; i < len ; i ++)
		{
			Object o = contlist.get(i) ;
			if(o instanceof TxtBlock)
			{
				TxtBlock wpb = (TxtBlock)o;
				String bn = wpb.getBlockName() ;
				if(SUB_BEGIN.equals(bn))
				{//find sub begin
					String name = wpb.getPropVal(SUB_PROP_NAME) ;
					boolean barr = "true".equals(wpb.getPropVal(SUB_PROP_ARRAY)) ;
					TxtTemplate wpt = new TxtTemplate(cur_temp, name, true, barr);
					cur_temp.contList.add(wpt) ;
					i = constructSub(wpt,contlist,i+1) ;
					
					continue ;
				}
				else if(SUB_END.equals(bn))
				{
					//constructSub(cur_temp.parent,contlist,idx+1);
					//continue ;
					return i ;
				}
			}
			
			cur_temp.contList.add(o) ;	
		}
		
		return len ;
	}
	
	public int getId()
	{
		return id ;
	}

	public String getPath()
	{
		return path;
	}
	
	public String getName()
	{
		return this.name ;
	}

	public ArrayList<Object> getContList()
	{
		return contList;
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<TxtBlock> getPageBlocks()
	{
		if(blocks!=null)
			return blocks ;
		
		ArrayList<TxtBlock> pbs = new ArrayList<TxtBlock>() ;
		for(Object o : contList)
		{
			if(o instanceof TxtBlock)
				pbs.add((TxtBlock)o) ;
		}
		blocks = pbs ;
		return blocks ;
	}
	
	public void writeOut(Writer w,HashMap<String,String> block2val) throws IOException
	{
		writeOut(w,block2val,false) ;
	}
	
	public void writeOut(Writer w,HashMap<String,String> block2val,boolean emptyout) throws IOException
	{
		for(Object o :contList)
		{
			if(o instanceof String)
			{
				w.write((String)o) ;
				continue ;
			}
			
			if(o instanceof TxtBlock)
			{
				TxtBlock wpb = (TxtBlock)o ;
				String v = block2val.get(wpb.getBlockName()) ;
				if(v!=null)
					w.write(v);
				else
				{
					if(emptyout)
						w.write("[#"+wpb.getBlockName()+"#]");
				}
			}
		}
	}
	
	public void writeOut(Writer w,JSONObject cur_jo)throws IOException
	{
		writeOut(w,cur_jo,false) ;
	}
	
	public void writeOut(Writer w,JSONObject cur_jo,boolean emptyout) throws IOException
	{
		Stack<JSONObject> stk_jos = new Stack<>() ; 
		HashMap<String,String> cur_pm = new HashMap<>();
		writeOut(stk_jos,cur_pm, w, cur_jo,emptyout) ;
	}
	
	
	private static String getBlockValWithStack(TxtBlock wpb,HashMap<String,String> cur_pm,Stack<JSONObject> stk_jos,JSONObject jo)
	{
		String bn = wpb.getBlockName();
		String v = jo.optString(bn,null) ;
		if(v!=null)
			return v;
		if(cur_pm!=null)
		{
			v = cur_pm.get(bn) ;
			if(v!=null)
				return v;
		}
		int slen = 0 ;
		if(stk_jos==null || (slen=stk_jos.size())<=0)
			return null ;
		for(int k = slen-1 ; k >=0 ; k --)
		{
			JSONObject tmpjo = stk_jos.elementAt(k);
			v = tmpjo.optString(bn) ;
			if(v!=null)
				return v ;
		}
		return null ;
	}
	
	private void writeOut(Stack<JSONObject> stk_jos,HashMap<String,String> cur_pm,Writer w,JSONObject cur_jo,boolean emptyout) throws IOException
	{
		for(Object o :contList)
		{
			if(o instanceof String)
			{
				w.write((String)o) ;
				continue ;
			}
			
			if(o instanceof TxtBlock)
			{
				TxtBlock wpb = (TxtBlock)o ;
				String v = getBlockValWithStack(wpb,cur_pm,stk_jos,cur_jo) ;
				if(v!=null)
					w.write(v);
				else
				{
					if(emptyout)
						w.write("[#"+wpb.getBlockName()+"#]");
				}
				continue;
			}
			
			if(o instanceof TxtTemplate)
			{
				TxtTemplate sub = (TxtTemplate)o ;
				String sub_name  = sub.name;
				boolean sub_arr = sub.bSubArray ;
				if(sub_arr)
				{
					JSONArray jarr = cur_jo.optJSONArray(sub_name) ;
					if(jarr==null)
						continue ;
					int sublen = jarr.length() ;
					stk_jos.push(cur_jo) ;
					for(int k = 0 ; k < sublen ; k ++)
					{
						JSONObject subjo = jarr.getJSONObject(k) ;
						cur_pm.put(SUB_IDX, k+"");
						sub.writeOut(stk_jos,cur_pm,w,subjo,emptyout) ;
					}
					stk_jos.pop() ;
				}
				else
				{
					JSONObject subjo = cur_jo.optJSONObject(sub_name) ;
					if(subjo==null)
						continue ;
					stk_jos.push(cur_jo) ;
					sub.writeOut(stk_jos,null,w,subjo,emptyout) ;
					stk_jos.pop() ;
				}
				continue;
			}
		}
	}
	
	public String getContStr(HashMap<String,String> block2val)
	{
		return getContStr(block2val,false) ;
	}
	
	public String getContStr(Map<String,String> block2val,boolean emptyout)
	{
		StringBuilder sb = new StringBuilder() ;
		
		for(Object o :contList)
		{
			if(o instanceof String)
			{
				sb.append((String)o) ;
				continue ;
			}
			
			if(o instanceof TxtBlock)
			{
				TxtBlock wpb = (TxtBlock)o ;
				String v = block2val.get(wpb.getBlockName()) ;
				if(v!=null)
					sb.append(v);
				else
				{
					String defv = wpb.getPropVal("default") ;
					if(defv!=null)
						sb.append(defv) ;
					else
					{
						if(emptyout)
							sb.append("[#"+wpb.getBlockName()+"#]") ;
					}
				}
			}
		}
		
		return sb.toString() ;
	}
	
	
	public String getContStr(JSONObject jo,boolean emptyout) throws IOException
	{
		StringWriter sw = new StringWriter() ;
		this.writeOut(sw, jo, emptyout);
		return sw.toString();
	}
	
	
	private void toFormatString(StringBuffer sb,String indent)
	{
		for(Object o :contList)
		{
			if(o instanceof String)
			{
				sb.append(indent).append("#text").append("\r\n") ;
				continue ;
			}
			
			if(o instanceof TxtBlock)
			{
				sb.append(indent).append(o.toString()).append("\r\n") ;
				continue ;
			}
			
			if(o instanceof TxtTemplate)
			{//sub
				TxtTemplate wpt = (TxtTemplate)o;
				sb.append(indent).append(SUB_BEGIN)
					.append(" name=").append(wpt.name)
					.append(" array=").append(wpt.bSubArray).append("\r\n"); ;
				wpt.toFormatString(sb,indent+"    ") ;
				sb.append(indent).append(SUB_END).append("\r\n");
				continue ;
			}
		}
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer() ;
		toFormatString(sb,"") ;
		return sb.toString() ;
	}
}
