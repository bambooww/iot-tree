package org.iottree.core.store.gdb.conf;

import java.util.List;

import org.w3c.dom.Element;


/**
 * 支持一次File文件操作 的运行的配置项<br/>
 * <br/>
 * &lt;File base="#fb" type="write" id="@id" value="@cont"/&gt;<br/>
 * @author Jason Zhu
 */
public class FileItem extends IOperItem
{
	public static enum OperType
	{
		write,
		read,
		delete,
	}
	
	public static FileItem parseFileItem(Gdb g,Func fc,Element sqlele) throws Exception
	{
		FileItem fi = new FileItem();
		
		fi.gdb = g ;
		fi.func = fc ;
		
		fi.parseEle(sqlele);
		
		fi.base = sqlele.getAttribute(Gdb.ATTR_BASE);
		if(fi.base==null||fi.base.equals(""))
			throw new IllegalArgumentException("File Item must has base attribute!");
		
		if(fi.base.startsWith("#"))
		{
			String tmps = g.getAllVarName2Value().get(fi.base);
			if(tmps==null||tmps.equals(""))
				throw new IllegalArgumentException("cannot find Var with name="+fi.base+" which defined in File base=\""+fi.base+"\"");
				
			fi.base = tmps ;
		}
		
		String opert = sqlele.getAttribute(Gdb.ATTR_TYPE);
		fi.operType = OperType.valueOf(opert);
		fi.id_var = sqlele.getAttribute(Gdb.ATTR_ID);
		if(fi.id_var==null||fi.id_var.equals(""))
			throw new IllegalArgumentException("File Item must has attribute "+Gdb.ATTR_ID);
		
		if(!fi.id_var.startsWith("@"))
			throw new IllegalArgumentException("File Item must has attribute "+Gdb.ATTR_ID+" like id=\"@xx\"");
		
		if(fi.operType==OperType.write)
		{
			fi.val_var = sqlele.getAttribute(Gdb.ATTR_VALUE);
			if(fi.val_var==null||fi.val_var.equals(""))
				throw new IllegalArgumentException("File Item with writer oper must has attribute "+Gdb.ATTR_VALUE);
			
			if(!fi.val_var.startsWith("@"))
				throw new IllegalArgumentException("File Item with writer oper must has attribute "+Gdb.ATTR_VALUE+" like value=\"@xx\"");
		}
		return fi ;
	}
	
	private Gdb gdb = null ;
	private Func func = null ;
	
	private String base = null ;
	private OperType operType = null ;
	private String id_var = null ;
	private String val_var = null ;
	
	private FileItem()
	{}
	
	public Gdb getGdb()
	{
		return gdb ;
	}
	
	public Func getFunc()
	{
		return func ;
	}
	
	public String getBase()
	{
		return base ;
	}
	
	public OperType getOperType()
	{
		return operType ;
	}
	
	@Override
	public boolean isChangedData()
	{
		return operType!=OperType.read ;
	}
	
	public String getIdVar()
	{
		return id_var ;
	}
	
	public String getValVar()
	{
		return val_var ;
	}
	
	
}
