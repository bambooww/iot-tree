package org.iottree.core;

public interface IPropChecker
{
	/**
	 * check prop item value when configration 
	 * @param groupn
	 * @param itemn
	 * @param strv
	 * @param failedr
	 * @return
	 */
	public abstract boolean checkPropValue(String groupn,String itemn,String strv,StringBuilder failedr) ;
}
