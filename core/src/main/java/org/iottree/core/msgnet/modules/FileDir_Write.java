package org.iottree.core.msgnet.modules;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.json.JSONObject;

public class FileDir_Write  extends MNNodeMid
{
	static Lan lan = Lan.getLangInPk(FileDir_Write.class) ;
	
	public static enum WriteTP
	{
		bytearray(0),
		payload(1);
		
		public final int val ;
		
		WriteTP(int v)
		{
			val = v ;
		}
		
		public int getVal()
		{
			return this.val ;
		}
		
		public String getTitle()
		{
			return lan.g("file_dir_w_"+this.name()) ;
		}
		
		public static WriteTP fromVal(int v)
		{
			switch(v)
			{
			case 1:
				return payload ;
			default:
				return bytearray ;
			}
		}
	}
	
	WriteTP writeTP = WriteTP.bytearray ;
	
	String appendHex = null ;
	
	String payloadEnc = "UTF-8" ;
	
	@Override
	public int getOutNum()
	{
		return 1;
	}

	@Override
	public String getTP()
	{
		return "file_dir_write";
	}

	@Override
	public String getTPTitle()
	{
		return g("file_dir_write");
	}

	@Override
	public String getColor()
	{
		return "#e7b686";
	}

	@Override
	public String getIcon()
	{
		return "\\uf573";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("write_tp",this.writeTP.getVal()) ;
		jo.putOpt("append_hex",this.appendHex) ;
		jo.putOpt("pld_enc",payloadEnc) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.writeTP = WriteTP.fromVal(jo.optInt("write_tp",0)) ;
		this.appendHex = jo.optString("append_hex") ;
		this.payloadEnc = jo.optString("pld_enc","utf-8") ;
	}


	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		byte[] wbs = null ;
		switch(this.writeTP)
		{
		case payload:
			Object pld = msg.getPayload() ;
			if(pld==null)
				return null;
			String ss = pld.toString() ;
			if(Convert.isNullOrEmpty(ss))
				return null ;
			wbs = ss.getBytes(this.payloadEnc);
			break;
		case bytearray:
			wbs = msg.getBytesArray() ;
			break ;
		default:
			break;
		}
		
		if(wbs==null||wbs.length<=0)
			return null ;
		
		FileDir_M mnm = (FileDir_M)this.getOwnRelatedModule() ;
		FileDir_M.FileItem fi = mnm.getOpenedFile() ;
		if(fi==null)
		{
			RT_DEBUG_WARN.fire("file_dir_w", "no opened file");
			return null ;
		}
		RT_DEBUG_WARN.clear("file_dir_w");
		fi.write(wbs);
		
		JSONObject jo = new JSONObject() ;
		jo.put("filepath",fi.file.getAbsoluteFile());
		jo.put("w_len",wbs.length) ;
		return RTOut.createOutAll(new MNMsg().asPayload(jo)) ;
	}
}
