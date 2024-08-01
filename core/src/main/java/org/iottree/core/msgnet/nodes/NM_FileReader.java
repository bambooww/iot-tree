package org.iottree.core.msgnet.nodes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNCxtValSty;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.MNBase.DivBlk;
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
	
	String encoding = "UTF-8" ;
	
	OutTP outTP = OutTP.txt ;
	
	int skipFirstLines = 0 ;
	
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
		jo.put("skip_first_lns", this.skipFirstLines) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.pathValSty = MNCxtValSty.valueOf(jo.optString("path_valsty","vt_str")) ;
		this.pathSubV = jo.optString("path_subv") ;
		this.encoding = jo.optString("encoding","utf-8") ;
		this.outTP = OutTP.valueOf(jo.optString("out_tp","txt")) ;
		this.skipFirstLines = jo.optInt("skip_first_lns", 0) ;
	}

	// rt lines
	
	private transient int LINE_CC = 0 ;

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		Object objv = this.pathValSty.RT_getValInCxt(this.pathSubV, this.getBelongTo(), this, msg) ;
		if(objv==null)
		{
			RT_DEBUG_ERR.fire("file_r", "no path be gotten");
			return null ;
		}
		if(!(objv instanceof String))
		{
			RT_DEBUG_ERR.fire("file_r", "path be gotten is not string");
			return null ;
		}
		
		File f = new File((String)objv) ;
		if(!f.exists() || f.isDirectory())
		{
			RT_DEBUG_ERR.fire("file_w", "path "+objv+" is not existed or not file");
			return null ;
		}
		
		RT_DEBUG_ERR.clear("file_r");

		if(outTP==OutTP.msg_per_ln)
		{
			try(FileInputStream fis = new FileInputStream(f) ;
					InputStreamReader isr = new InputStreamReader(fis,this.encoding) ;
					BufferedReader br = new BufferedReader(isr))
			{
				LINE_CC = 0 ;
				String ln ;
				
				while((ln=br.readLine())!=null)
				{
					LINE_CC ++ ;
					if(this.skipFirstLines>0 && LINE_CC <= this.skipFirstLines)
						continue ;
					MNMsg outm = new MNMsg() ;
					outm.asPayload(ln).asHead("path", f.getCanonicalPath()) ;
					this.RT_sendMsgOut(RTOut.createOutAll(outm));
				}
			}
			return null;
		}
		
		String txt = Convert.readFileTxt(f, encoding) ;
		MNMsg outm = new MNMsg() ;
		if(outTP==OutTP.json)
			outm.asPayload(new JSONObject(txt)).asHead("path", f.getCanonicalPath()) ;
		else
			outm.asPayload(txt).asHead("path", f.getCanonicalPath()) ;
		return RTOut.createOutAll(outm);
	}
	
	@Override
	protected void RT_renderDiv(List<DivBlk> divblks)
	{
		if(outTP==OutTP.msg_per_ln)
		{
			StringBuilder divsb = new StringBuilder() ;
			divsb.append("<div class=\"rt_blk\">Read Line CC= "+LINE_CC) ;
			divsb.append("</div>") ;
			divblks.add(new DivBlk("file_r_line_cc",divsb.toString())) ;
		}
		
		super.RT_renderDiv(divblks);
	}
}
