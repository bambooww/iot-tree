package org.iottree.core.basic.ce;

import java.util.List;

import org.json.JSONObject;

public interface IExchgModule
{
	public String getExchgModuleName() ;
	
	public String getExchgModuleTitle() ;
	
	public List<ExchgObj> listExchgObjs() ;
	
	public boolean formExchgJO(JSONObject jo) ;
}
