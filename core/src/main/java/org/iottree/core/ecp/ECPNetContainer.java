package org.iottree.core.ecp;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.json.JSONObject;

public abstract class ECPNetContainer
{
	public abstract String getContainerTp() ;
	
	protected abstract String getContainerId() ;
	
	public final String getContainerUID()
	{
		return this.getContainerTp()+"-"+this.getContainerId() ;
	}
	
	public abstract void saveNet(ECPNet mnn) throws Exception ;
	
	public abstract ECPNet delNet(String net_id) throws Exception ;
	
//	public abstract ECPNet createNewNet(String name,String title,String desc,boolean benable) throws Exception;
//	
//	public abstract ECPNet updateNet(String id,JSONObject jo)  throws Exception;
//	
//	public abstract ECPNet updateNet(String id,String name,String title,String desc,boolean benable) throws Exception;
//	
//	public abstract ECPNet delNet(String id) throws Exception ;
}
