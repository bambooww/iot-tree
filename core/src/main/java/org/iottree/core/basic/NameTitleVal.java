package org.iottree.core.basic;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.iottree.core.util.Convert;

/**
 * for web input item,dyn render and get input value
 * all use string
 * @author jason.zhu
 *
 */
public class NameTitleVal
{
	String name ;
	
	String title ;
	
	String[] valOpts = null ;
	
	boolean bValMultiLines = false;
	
	String desc = null ;
	
	public NameTitleVal(String n,String t)
	{
		if(Convert.isNullOrEmpty(n))
			throw new IllegalArgumentException("name cannot be null or empty") ;
		this.name = n ;
		this.title = t;
		if(Convert.isNullOrEmpty(this.title))
			this.title = n ;
	}
	
	public NameTitleVal(String n,String t,String[] valopts)
	{
		this(n,t) ;
		this.valOpts = valopts ;
	}
	
	public NameTitleVal(String n,String t,boolean multi_lines)
	{
		this(n,t) ;
		bValMultiLines = multi_lines ;
	}
	
	public String getName()
	{
		return name ;
	}
	
	public String getTitle()
	{
		return title ;
	}
	
	public String[] getValOpts()
	{
		return valOpts ;
	}
	
	public boolean isValMultiLines()
	{
		return bValMultiLines;
	}
	
	public String getDesc()
	{
		return desc ;
	}
	
	public NameTitleVal setDesc(String d)
	{
		this.desc = d ;
		return this ;
	}
	
	
	public void writeToJsonStr(Writer out) throws IOException
	{
		out.write("{\"n\":\""+name+"\",\"t\":\""+title+"\"");
		if(desc!=null)
			out.write(",\"d\":\""+Convert.plainToJsStr(desc)+"\"");
		if(valOpts!=null&&valOpts.length>0)
		{
			out.write(",\"val_opts\":[");
			boolean bf =true ;
			for(String v:valOpts)
			{
				if(bf) bf=false;
				else out.write(",") ;
				out.write("\""+v+"\"");
			}
			out.write("]");
		}
		out.write(",\"multi_ln\":"+bValMultiLines);
		out.write("}");
	}
	
	
	public static void writeToJsonStr(NameTitleVal[] ntvs,Writer out) throws IOException
	{
		out.write("[") ;
		if(ntvs!=null&&ntvs.length>0)
		{
			boolean bf = true ;
			for(NameTitleVal ntv:ntvs)
			{
				if(bf) bf=false;
				else out.write(",") ;
				
				ntv.writeToJsonStr(out) ; 
			}
		}
		out.write("]");
	}

}
