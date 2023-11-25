package org.iottree.core.store.gdb;

import java.sql.Connection;

import org.iottree.core.store.gdb.connpool.*;

public abstract class DBSelCBCanBreak implements IDBSelectCallback
{
	protected IConnPool connPool = null ;
	
	protected Connection conn = null ;
	
	
	void onStartSelect(IConnPool cp,Connection conn)
	{
		connPool = cp ;
		this.conn = conn ;
	}
	
	public IConnPool getConnPool()
	{
		return connPool ;
	}
	
	public Connection getConn()
	{
		return this.conn ;
	}
	
	/**
	 * ��ѯ�ص���ǿ���жϣ����ж��̣߳�����绹�л�������ӽ����ͷ�
	 */
	public void releaseConn()
	{
		connPool.free(conn);
	}
}
