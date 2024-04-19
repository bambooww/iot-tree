package org.iottree.core.dict;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.*;

import org.iottree.core.UAManager;
import org.iottree.core.UANode;
import org.iottree.core.cxt.IJSOb;
import org.iottree.core.cxt.JSObMap;
import org.iottree.core.cxt.JsProp;
import org.iottree.core.dict.DataClass.BindStyle;
import org.iottree.core.util.Convert;

public class PrjDataClass extends JSObMap// implements IJSOb
{
	String prjId = null ;
	
	LinkedHashMap<String,DataClass> id2dc = new LinkedHashMap<>() ;
	
	
	public PrjDataClass(String prjid)
	{
		this.prjId= prjid ;
	}
	
	public String getPrjId()
	{
		return prjId ;
	}
	
	public Collection<DataClass> getDataClassAll()
	{
		return id2dc.values();
	}
	
	public DataClass getDataClassById(String id)
	{
		return id2dc.get(id) ;
	}
	
	public DataClass getDataClassByName(String name)
	{
		for(DataClass dc:id2dc.values())
		{
			if(dc.getClassName().equals(name))
				return dc ;
		}
		return null ;
	}

	private File[] getPrjDDFiles(String prjid)
	{
		File  prjdir = UAManager.getPrjFileSubDir(prjid);
		if(!prjdir.exists())
			return null ;
		
		final FilenameFilter ff = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name)
			{
				return name.startsWith("dict_")&&name.endsWith(".xml");
			}} ;
		
		return prjdir.listFiles(ff) ;	
	}
	
	private File getPrjDDFile(String prjid,String classid)
	{
		File  prjdir = UAManager.getPrjFileSubDir(prjid);
		if(!prjdir.exists())
			return null ;
		return new File(prjdir,"dict_"+classid+".xml") ;
	}
	
	private String getClassIdByFile(File f)
	{
		String fn = f.getName() ;
		int len = fn.length() ;
		return fn.substring(5,len-4) ;
	}
	
	public boolean loadAll()
	{
		File[] fs = getPrjDDFiles(prjId) ;
		if(fs==null)
			return false ;
		
		LinkedHashMap<String,DataClass> id2dc = new LinkedHashMap<>() ;
		
		for(File tmpf:fs)
		{
			String cid = getClassIdByFile(tmpf) ;
			try
			{
				DataClass dc = null;
				try(FileInputStream fis = new FileInputStream(tmpf);)
				{
					dc = DictManager.loadDataClass(fis) ;
					dc.classId = cid ;
				}
				id2dc.put(cid, dc) ;
			}
			catch(Exception e)					
			{
				System.out.println("load dd file ["+tmpf.getAbsolutePath()+"] err") ;
				e.printStackTrace();
			}
		}
		
		this.id2dc = id2dc ;
		return true ;
	}
	

	public void saveDataClass(DataClass dc) throws IOException
	{
		String cid = dc.getClassId();
		File dcf = getPrjDDFile(prjId,cid) ;
		if(dcf==null)
			throw new IOException("no DataClass file found!") ;
		
		try(FileOutputStream fos = new FileOutputStream(dcf);
				OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8") ;)
		{
			dc.writeToXml(osw);
			osw.flush();
		}
	}
	
	
	public boolean delDataClass(String cid)
	{
		File dcf = getPrjDDFile(prjId,cid) ;
		if(dcf==null)
			return false;
		
		if(dcf.delete())
		{
			id2dc.remove(cid) ;
		}
		
		return true ;
	}
	
	public DataClass addDataClass(String name,String title,boolean benable,String bind_for,BindStyle bind_s,HashMap<String,String> props) throws IOException
	{
		DataClass dc = DataClass.createNewClass(name, title) ;
		dc.setClassEnable(benable);
		dc.setBindFor(bind_for);
		//dc.setBindMulti(bind_multi);
		dc.setBindStyle(bind_s);
		dc.setExtAttrs(props);
		saveDataClass(dc) ;
		id2dc.put(dc.getClassId(), dc) ;
		return dc ;
	}

	public boolean updateDataClass(String classid,String name,String title,boolean benable,
			String bind_for,BindStyle bind_s,HashMap<String,String> props) throws IOException
	{
		DataClass dc = this.getDataClassById(classid) ;
		if(dc==null)
			return false;
		dc.name = name ;
		dc.title = title ;
		dc.setClassEnable(benable);
		dc.setBindFor(bind_for);
		//dc.setBindMulti(bind_multi);
		dc.setBindStyle(bind_s);
		dc.setExtAttrs(props);
		saveDataClass(dc) ;
		return true;
	}
	
	public DataNode addOrUpdateDataNode(String classid,String name,String title) throws Exception
	{
		DataClass dc = this.getDataClassById(classid) ;
		if(dc==null)
			throw new IOException("no DataClass found") ;
		
		DataNode dn = dc.addOrUpdateDataNode(name, title, -1, null) ;
				
		saveDataClass(dc) ;
		return dn ;
	}
	
	
	public List<DataNode> impDataNodeByTxt(String classid,String txt) throws Exception
	{
		DataClass dc = this.getDataClassById(classid) ;
		if(dc==null)
			throw new IOException("no DataClass found") ;
		
		BufferedReader br = new BufferedReader(new StringReader(txt)) ;
		String ln ;
		ArrayList<DataNode> rets =new ArrayList<>() ;
		while((ln=br.readLine())!=null)
		{
			ln = ln.trim();
			if(Convert.isNullOrEmpty(ln))
				continue ;
			
			
			List<String> ss = Convert.splitStrWith(ln, " \t|") ;
			String name = ss.get(0) ;
			String title = name ;
			if(ss.size()>1)
				title = ss.get(1) ;
			try
			{
				DataNode dn = dc.addOrUpdateDataNode(name, title, -1, null) ;
				if(dn!=null)
					rets.add(dn) ;
			}
			catch(Exception e)
			{
				System.out.println(" warn:"+e.getMessage()) ;
			}
		}
		if(rets.size()>0)
			saveDataClass(dc) ;
		return rets ;
	}
//	
//	public class JSOb
//	{
//		public String get_dd_val(String classn,String dn)
//		{
//			DataClass dc = PrjDataClass.this.getDataClassByName(classn);
//			if(dc==null)
//				return null ;
//			DataNode dn = dc.getNodeByName(dn) ;
//		}
//	}
//	
//	JSOb jsob = new JSOb() ;
//
//	@Override
//	public Object getJSOb()
//	{
//		return jsob;
//	}
//	

	
	@Override
	public Object JS_get(String  key)
	{
		return this.getDataClassByName(key) ;
	}
	
	@Override
	public List<JsProp> JS_props()
	{
		List<JsProp> ss = super.JS_props();
		
		Collection<DataClass> dcs = getDataClassAll() ;
		//List<UANode> subns = this.getSubNodes() ;
		if(dcs!=null)
		{
			for(DataClass dc:dcs)
			{
				ss.add(new JsProp(dc.getClassName(),dc,DataClass.class,true,dc.getClassTitle(),"Data Class "+dc.getClassTitle())) ;
			}
		}
		return ss ;
	}
}
