package org.iottree.core.msgnet.nodes;

import java.io.File;
import java.io.FileFilter;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNCxtValSty;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.json.JSONArray;
import org.json.JSONObject;

public class NM_FileDirMon extends MNNodeMid
{
	static Lan lan = Lan.getLangInPk(NM_FileReader.class) ;
	
	public static enum OutTP
	{
		msg_per_file(0),
		file_array(1);
		
		private final int val ;
		
		OutTP(int v)
		{
			val = v ;
		}
		
		public int getInt()
		{
			return val ;
		}
		
		public static OutTP valueOfInt(int val)
		{
			switch(val)
			{
			case 1:
				return file_array;
			default:
				return msg_per_file;
			}
		}
		
		public String getTitle()
		{
			return lan.g("fm_"+this.name()) ;
		}
	}
	
	public static enum AfterUse
	{
		do_nothing(0),
		mv_to_dir(1),
		del_file(2);
		
		private final int val ;
		
		AfterUse(int v)
		{
			val = v ;
		}
		
		public int getInt()
		{
			return val ;
		}
		
		public String getTitle()
		{
			return lan.g("fm_"+this.name()) ;
		}
		
		public static AfterUse valueOfInt(int val)
		{
			switch(val)
			{
			case 1:
				return mv_to_dir;
			case 2:
				return del_file;
			default:
				return do_nothing;
			}
		}
	}
	
	MNCxtValSty pathValSty = MNCxtValSty.vt_str ;
	
	String pathSubV = null ;
	
	/**
	 * 文件名前缀
	 */
	String fnPrefix = null ;
	
	/**
	 * 文件名后缀
	 */
	String fnSuffix = null ;
	
	OutTP outTP = OutTP.msg_per_file ;
	
	AfterUse afterUse = AfterUse.do_nothing ;
	
	MNCxtValSty mvToDirValSty = MNCxtValSty.vt_str ;
	
	String mvToDirSubV = null ;
	
	private FileFilter fFilter = null ;
	
	@Override
	public int getOutNum()
	{
		return 1;
	}

	@Override
	public String getTP()
	{
		return "file_dir_mon";
	}

	@Override
	public String getTPTitle()
	{
		return g("file_dir_mon");
	}

	@Override
	public String getColor()
	{
		return "#e7b686";
	}

	@Override
	public String getIcon()
	{
		return "\\uf07b";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(pathSubV))
		{
			failedr.append("no dir path set") ;
			return false;
		}
		
		if(this.afterUse==AfterUse.mv_to_dir)
		{
			if(Convert.isNullOrEmpty(this.mvToDirSubV))
			{
				failedr.append("no move to dir path set") ;
				return false;
			}
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("path_valsty", this.pathValSty.name()) ;
		jo.putOpt("path_subv", this.pathSubV) ;
		jo.putOpt("fn_prefix", this.fnPrefix) ;
		jo.putOpt("fn_suffix", this.fnSuffix) ;
		jo.put("out_tp", outTP.getInt()) ;
		jo.put("after_use", this.afterUse.getInt());
		jo.putOpt("mvtodir_valsty", this.mvToDirValSty.name()) ;
		jo.putOpt("mvtodir_subv", this.mvToDirSubV) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.pathValSty = MNCxtValSty.valueOf(jo.optString("path_valsty","vt_str")) ;
		this.pathSubV = jo.optString("path_subv") ;
		this.fnPrefix = jo.optString("fn_prefix") ;
		this.fnSuffix = jo.optString("fn_suffix") ;
		this.outTP = OutTP.valueOfInt(jo.optInt("out_tp",0)) ;
		this.afterUse = AfterUse.valueOfInt(jo.optInt("after_use",0)) ;
		this.mvToDirValSty = MNCxtValSty.valueOf(jo.optString("mvtodir_valsty","vt_str")) ;
		this.mvToDirSubV = jo.optString("mvtodir_subv") ;
		
		fFilter = null ;
	}
	
	private FileFilter getFF()
	{
		if(fFilter!=null)
			return fFilter ;
		
		fFilter = new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if(f.isDirectory())
					return false;
				String fn = f.getName() ;
				if(Convert.isNotNullEmpty(fnPrefix) && !fn.startsWith(fnPrefix))
					return false;
				if(Convert.isNotNullEmpty(fnSuffix) && !fn.endsWith(fnSuffix))
					return false;
				return true;
			}
		} ;
			
		return fFilter ;
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		Object objv = this.pathValSty.RT_getValInCxt(this.pathSubV, this.getBelongTo(), this, msg) ;
		if(objv==null)
		{
			RT_DEBUG_ERR.fire("file_dir_mon", "no path be gotten");
			return null ;
		}
		if(!(objv instanceof String))
		{
			RT_DEBUG_ERR.fire("file_dir_mon", "path be gotten is not string");
			return null ;
		}
		
		File dir = new File((String)objv) ;
		if(!dir.exists() || !dir.isDirectory())
		{
			RT_DEBUG_ERR.fire("file_dir_mon", "path "+objv+" is not existed or not dir");
			return null ;
		}
		
		File mvdir = null ;
		
		if(this.afterUse==AfterUse.mv_to_dir)
		{
			objv = this.mvToDirValSty.RT_getValInCxt(this.mvToDirSubV, this.getBelongTo(), this, msg) ;
			if(objv==null)
			{
				RT_DEBUG_ERR.fire("file_dir_mon", "no move to dir be gotten");
				return null ;
			}
			if(!(objv instanceof String))
			{
				RT_DEBUG_ERR.fire("file_dir_mon", "move to dir be gotten is not string");
				return null ;
			}
			
			mvdir = new File((String)objv) ;
			if(!mvdir.exists() || !mvdir.isDirectory())
			{
				RT_DEBUG_ERR.fire("file_dir_mon", "move to dir "+objv+" is not existed or not dir");
				return null ;
			}
		}

		File[] fs = dir.listFiles(getFF()) ;
		if(outTP==OutTP.msg_per_file)
		{
			for(File f:fs)
			{
				MNMsg outm = new MNMsg() ;
				outm.asPayload(f.getCanonicalPath()) ;
				this.RT_sendMsgOut(RTOut.createOutAll(outm));
				
				doAfterUse(f,mvdir) ;
			}
			return null;
		}
		
		JSONArray jarr = new JSONArray() ;
		for(File f:fs)
		{
			jarr.put(f.getCanonicalPath()) ;
		}
		MNMsg outm = new MNMsg() ;
		outm.asPayload(jarr) ;
		
		this.RT_sendMsgOut(RTOut.createOutAll(outm));
		for(File f:fs)
			doAfterUse(f,mvdir) ;
		
		return null;
	}
	
	private void doAfterUse(File f,File mvdir)
	{
		if(this.afterUse==AfterUse.do_nothing)
			return ;
		
		if(this.afterUse==AfterUse.del_file)
		{
			f.delete() ;
			return ;
		}
		
		if(this.afterUse==AfterUse.mv_to_dir)
		{
			File tarf = new File(mvdir,f.getName()) ;
			f.renameTo(tarf) ;
		}
	}
}
