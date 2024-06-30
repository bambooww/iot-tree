package org.iottree.core.msgnet.nodes;

import java.io.File;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNCxtValSty;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.json.JSONObject;

public class NM_FileReader  extends MNNodeMid
{
	static Lan lan = Lan.getLangInPk(NM_FileReader.class) ;
	
	public static enum OutTP
	{
		txt,json,msg_per_ln;
		
		public String getTitle()
		{
			return lan.g("fr_"+this.name()) ;
		}
	}
	
	MNCxtValSty pathValSty = MNCxtValSty.vt_str ;
	
	String pathSubV = null ;
	
	String encoding = "utf-8" ;
	
	OutTP outTP = OutTP.txt ;
	
	@Override
	public int getOutNum()
	{
		return 1;
	}

	@Override
	public String getTP()
	{
		return "file_r";
	}

	@Override
	public String getTPTitle()
	{
		return g("file_r");
	}

	@Override
	public String getColor()
	{
		return "#e7b686";
	}

	@Override
	public String getIcon()
	{
		return "PK_filer";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(pathSubV))
		{
			failedr.append("no file path set") ;
			return false;
		}
		
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("path_valsty", this.pathValSty.name()) ;
		jo.putOpt("path_subv", this.pathSubV) ;
		jo.put("encoding", this.encoding) ;
		jo.put("out_tp", outTP.name()) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.pathValSty = MNCxtValSty.valueOf(jo.optString("path_valsty","vt_str")) ;
		this.pathSubV = jo.optString("path_subv") ;
		this.encoding = jo.optString("encoding","utf-8") ;
		this.outTP = OutTP.valueOf(jo.optString("out_tp","txt")) ;
	}


	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		Object objv = this.pathValSty.RT_getValInCxt(this.pathSubV, this.getBelongTo(), this, msg) ;
		if(objv==null)
		{
			RT_DEBUG_ERR.fire("file_w", "no path be gotten");
			return null ;
		}
		if(!(objv instanceof String))
		{
			RT_DEBUG_ERR.fire("file_w", "path be gotten is not string");
			return null ;
		}
		
		File f = new File((String)objv) ;
		if(!f.exists() || f.isDirectory())
		{
			RT_DEBUG_ERR.fire("file_w", "path "+objv+" is not existed or not file");
			return null ;
		}
		String txt = Convert.readFileTxt(f, encoding) ;
		MNMsg outm = new MNMsg().asPayload(txt).asHead("path", f.getCanonicalPath()) ;
		return RTOut.createOutAll(outm);
	}
}
