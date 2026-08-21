package org.iottree.core.store.gdb;

/**
 * 
 * @author Jason Zhu
 */
public interface IDBSelectCallback
{
	/**
	 * @param dt
	 * @return
	 * @throws Exception
	 */
	public boolean onFindDataTable(int tableidx,DataTable dt) throws Exception ;
	
	/**
	 * 
	 * 
	 * @param tableidx
	 * @param dt
	 * @param rowidx
	 * @param dr
	 * @return 
	 * @throws Exception
	 */
	public boolean onFindDataRow(int tableidx,DataTable dt,int rowidx,DataRow dr) throws Exception;



}
