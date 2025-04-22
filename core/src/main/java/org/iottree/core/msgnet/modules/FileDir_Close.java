package org.iottree.core.msgnet.modules;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.json.JSONObject;

public class FileDir_Close  extends MNNodeMid
{
	
	@Override
	public int getOutNum()
	{
		return 1;
	}

	@Override
	public String getTP()
	{
		return "file_dir_close";
	}

	@Override
	public String getTPTitle()
	{
		return g("file_dir_close");
	}

	@Override
	public String getColor()
	{
		return "#e7b686";
	}

	@Override
	public String getIcon()
	{
		return "\\uf1c3";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		return null;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
	}


	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		FileDir_M mnm = (FileDir_M)this.getOwnRelatedModule() ;
		FileDir_M.FileItem fi = mnm.getOpenedFile() ;
		if(fi==null)
			return null ;
		mnm.RT_closeFile();
		return RTOut.createOutAll(new MNMsg().asPayload(fi.file.getAbsoluteFile())) ;
	}
}
