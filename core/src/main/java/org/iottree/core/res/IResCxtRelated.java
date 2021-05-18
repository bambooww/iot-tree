package org.iottree.core.res;

import java.util.List;

public interface IResCxtRelated
{
	/**
	 * name of editor which will use res
	 * @return
	 */
	public String getEditorName() ;
	
	public String getEditorId() ;
	
	public List<ResCxt> getResCxts() ;
}
