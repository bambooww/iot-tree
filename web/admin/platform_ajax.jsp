<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.json.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.core.router.*,
	org.iottree.core.dict.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.core.util.web.*,
	org.iottree.core.station.*
	"%><%!
	static HashSet<String> IGNORE_FN = new HashSet<String>() ;
	static HashSet<String> IGNORE_EXT = new HashSet<String>() ;
	static
	{
		IGNORE_FN.add("CVS") ;
		IGNORE_FN.add(".settings") ;
		IGNORE_FN.add(".gitignore") ;
		
		IGNORE_EXT.add("project") ;
		//IGNORE_EXT.add() ;
		IGNORE_EXT.add("classpath") ;
	}
	
	static class MyFileFilter implements FileFilter
	{
		FileFilter outFF = null ;
		
		public MyFileFilter(FileFilter off)
		{
			outFF = off ;
		}
		
		public boolean accept(File f)
		{
			if(outFF!=null&&!outFF.accept(f))
				return false;
			
			String n = f.getName() ;
			if(IGNORE_FN.contains(n))
				return false;
			
			//if(n.startsWith("."))
			//	return false;
			
			if(f.isDirectory())
			{
				//if(f.getName()
				return true ;
			}
			
			
			int k = n.lastIndexOf(".") ;
			if(k>=0)
			{
				String ext = n.substring(k+1) ;
				if(IGNORE_EXT.contains(ext))
					return false;
			}
			return true ;
		}
	} ;
	
public static List<String> scanDir(File basedir,long modify_after,FileFilter filter)
	{
		ArrayList<String> flist = new ArrayList<String>() ;
		scanDirIn("/",basedir,modify_after,flist,new MyFileFilter(filter)) ;
		return flist ;
	}
	
	static void scanDirIn(String pathbase,File fdir,long modify_after,ArrayList<String> flist,FileFilter filter)
	{
		File[] subfs = fdir.listFiles() ;
		if(subfs==null)
			return ;
		for(File subf:subfs)
		{
			if(!filter.accept(subf))
				continue ;

			if(subf.isDirectory())
			{
				scanDirIn(pathbase+subf.getName()+"/",subf,modify_after,flist,filter) ;
				continue ;
			}
			
			if(subf.lastModified()<modify_after)
				continue ;
			flist.add(pathbase+subf.getName());
		}
	}
	
	public static List<String> scanDirWithDir(File basedir)
	{
		ArrayList<String> flist = new ArrayList<String>() ;
		flist.add("/") ;
		scanDirInWithDir("/",basedir,flist) ;
		return flist ;
	}
	
	static void scanDirInWithDir(String pathbase,File fdir,ArrayList<String> flist)
	{
		File[] subfs = fdir.listFiles() ;
		if(subfs==null)
			return ;
		for(File subf:subfs)
		{
			if(!subf.isDirectory())
				continue ;
			
			String n = subf.getName() ;
			if(IGNORE_FN.contains(n))
				continue ;
			flist.add(pathbase+subf.getName()+"/");
			
			scanDirInWithDir(pathbase+subf.getName()+"/",subf,flist) ;
		}
	}
	
	static class IgnoreFilter implements FileFilter
	{
		List<String> ss = null ;
		public IgnoreFilter(String ignorestr)
		{
			ss = Convert.splitStrWith(ignorestr,",|") ;
		}
		
		public boolean accept(File f)
		{
			if(ss==null)
				return true ;
			if(ss.contains(f.getName()))
				return false;
			return true ;
		}
	}
	
	
	static long calLastMDT(String mdt)
	{
		if(Convert.isNullOrEmpty(mdt))
			return -1;//System.currentTimeMillis() ;
		if(mdt.endsWith("h"))
		{
			int hv = Integer.parseInt(mdt.substring(0,mdt.length()-1)) ;
			return System.currentTimeMillis() - hv*3600000 ;
		}
		if(mdt.endsWith("d"))
		{
			int hv = Integer.parseInt(mdt.substring(0,mdt.length()-1)) ;
			return System.currentTimeMillis() - hv*3600000*24 ;
		}
		
		return -1;//System.currentTimeMillis() ;
	}
	
	private static ArrayList<String> getParamVals(HttpServletRequest request,String pn)
	{
		String[] addfs = request.getParameterValues(pn+"[]") ;
		if(addfs==null)
			return null ;
		//System.out.println(pn+"  "+addfs.length) ;
		ArrayList<String> rets = new ArrayList<>() ;
		for(String f:addfs)
		{
			rets.add(f) ;
		}
		return rets ;
	}
%><%
if(!PlatformManager.isInPlatform())
{
	out.println("not platform") ;
	return ;
}
if(!Convert.checkReqEmpty(request, out,"op"))
	return ;

String op = request.getParameter("op");
String prjname = request.getParameter("prj");
String stationid = request.getParameter("stationid") ;

PStation station = null;
if(Convert.isNotNullEmpty(stationid))
{
	station = PlatformManager.getInstance().getStationById(stationid) ;
	if(station==null)
	{
		out.print("no station found") ;
		return ;
	}
}

UAPrj prj = null;
if(Convert.isNotNullEmpty(prjname))
{
	prj = UAManager.getInstance().getPrjByName(prjname) ;
	//if(prj==null)
	//{
	//	out.print("no prj found") ;
	//	return ;
	//}
}

String m  = request.getParameter("m") ;

String fbase = Config.getWebappBase() ;
if(Convert.isNotNullEmpty(m))
{
	fbase = PSCmdDirSyn.calDirBase(m) ;
}

String t = request.getParameter("last_mdt") ;
long last_mdt = calLastMDT(t) ;

String module=  request.getParameter("module") ;
String path = request.getParameter("path") ;

StringBuilder failedr = new StringBuilder() ;
switch(op)
{
case "station_prj_start_stop":
	if(!Convert.checkReqEmpty(request, out, "prj","stationid","start_stop"))
		return ;
	boolean bstart = "true".equals(request.getParameter("start_stop")) ;
	String auto_start = request.getParameter("auto_start") ;
	Boolean bautostart = null ;
	if(Convert.isNotNullEmpty(auto_start))
		bautostart = "true".equals(auto_start) ;
	try
	{
		station.RT_startStopPrj(prjname,bstart,bautostart) ;
		out.print("succ") ;
	}
	catch(Exception e)
	{
		e.printStackTrace();
		out.print(e.getMessage()) ;
	}
	return ;
case "station_reboot":
	if(!Convert.checkReqEmpty(request, out, "stationid"))
		return ;
	try
	{
		if(station.RT_rebootStation(failedr))
			out.print("succ") ;
		else
			out.print(failedr.toString()) ;
	}
	catch(Exception e)
	{
		e.printStackTrace();
		out.print(e.getMessage()) ;
	}
	break ;
case "station_prj_update":
	if(!Convert.checkReqEmpty(request, out, "prj","stationid"))
		return ;
	if(station.RT_updatePrj(prjname, failedr))
		out.print("succ") ;
	else
		out.print(failedr.toString()) ;
	break ;
case "station_syn_dir_diff":
	if(!Convert.checkReqEmpty(request, out, "module","path"))
		return ;
	PSCmdDirSyn.DirDiff diff = station.RT_synDirDiff(module,path,10000,failedr) ;
	
	if(diff!=null)
		diff.toJO().write(out);
	else
		out.print(failedr.toString()) ;
	break ;
case "station_syn_dir_syn":
	if(!Convert.checkReqEmpty(request, out, "module","path"))
		return ;
	ArrayList<String> add_fs = getParamVals(request,"add_fs") ;
	ArrayList<String> update_fs = getParamVals(request,"update_fs") ;
	ArrayList<String> del_fs = getParamVals(request,"del_fs") ;
	
	boolean b = station.RT_synDirSyn(module,path,
			add_fs,update_fs,del_fs,
			10000,failedr) ;
	
	if(b)
		out.print("succ") ;
	else
		out.print(failedr.toString()) ;
	break ;
case "station_syn_dir_syn_mon":
	JSONObject sss = station.RT_getSynDirSynRecved() ;
	sss.write(out) ;
	break ;
case "module_file_list":
	if(!Convert.checkReqEmpty(request, out, "m"))
		return ;
	String f = request.getParameter("f") ;
	IgnoreFilter ignf = null ;
	if(f!=null)
	{
		f = java.net.URLDecoder.decode(f,"UTF-8") ;
		ignf = new IgnoreFilter(f) ;
	}
	//System.out.println(fbase+"/"+m+"/") ;
	List<String> flist = scanDir(new File(fbase+"/"),last_mdt,ignf) ;
	JSONArray jarr = new JSONArray(flist) ;
	jarr.write(out) ;
	break ;
case "module_dir_list":
	if(!Convert.checkReqEmpty(request, out, "m"))
		return ;
	//System.out.println(fbase+"/"+m+"/") ;
	flist = scanDirWithDir(new File(fbase+"/")) ;
	jarr = new JSONArray(flist) ;
	jarr.write(out) ;
	break ;
default:
	out.print("unknown op="+op) ;
	return ;
}%>