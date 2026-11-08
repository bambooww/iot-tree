package org.iottree.driver.zigbee.zstack.msg_appcnf;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.zspec.util.BDBCommMode;
import org.iottree.driver.zigbee.zspec.util.BDBCommRemainModes;
import org.iottree.driver.zigbee.zspec.util.BDBCommStatus;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

public class BDBAddInstallCodeSREQ extends MsgRecv
{
	public static final int CMD0 = 0x2F ;
	public static final int CMD1 = 0x04 ;
	
	int install_code_fmt ;
	
	String ieee_addr ;
	
	byte[] install_code ;
	
	public BDBAddInstallCodeSREQ()
	{
		super( CMD0 ,CMD1 );
	}
	
	public BDBAddInstallCodeSREQ(int install_code_fmt,String ieee_addr,byte[] install_code)
	{
		super( CMD0 ,CMD1 );
		this.install_code_fmt = install_code_fmt ;
		this.ieee_addr = ieee_addr ;
		this.install_code = install_code ;
	}
	
	@Override
	public String getTpName()
	{
		return "APP_CNF_BDB_ADD_INSTALLCODE";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 9+(install_code!=null?install_code.length:0);
	}
	
	@Override
	public JSONObject toRecvJO()
	{
		return new JSONObject().put("install_code_fmt",install_code_fmt).putOpt("ieee", ieee_addr)
				.putOpt("install_code", Convert.byteArray2HexStr(install_code, "-"));
	}

}
