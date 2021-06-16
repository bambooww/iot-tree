package org.iottree.core.res;

import java.util.List;

public interface IResCxt extends IResNode
{
	public static final String PRE_PRJ="p" ;
	
	public static final String PRE_DEVDEF="d" ;
	
	public static final String PRE_COMP="c" ;
	
	public String getResPrefix() ;
	
	//public ResDir getResCxt(String uid);
	
	public boolean isResReadOnly() ;
	//public List<ResCxt> getResCxts() ;
}
