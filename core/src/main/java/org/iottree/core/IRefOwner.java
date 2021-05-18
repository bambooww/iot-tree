package org.iottree.core;

/**
 * node implements this will reference other branch.
 * @author jason.zhu
 *
 */
public interface IRefOwner
{
	/**
	 * get branch to be refered
	 * @return
	 */
	public IRefBranch getRefBranch() ;
}
