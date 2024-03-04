package org.iottree.core.store.ttsr;

import java.io.File;

import org.iottree.core.Config;
import org.iottree.core.store.gdb.connpool.DBConnPool;
import org.iottree.core.store.gdb.connpool.DBType;

public class RecAdapter4Test extends RecAdapter
{
	RecIO recIO = null ;
	
	@Override
	protected boolean RT_init(StringBuilder failedr)
	{
		getIO() ;
		return true;
	}

	@Override
	protected RecIO getIO()
	{
		if(recIO!=null)
			return recIO ;
		
		synchronized(this)
		{
			if(recIO!=null)
				return recIO ;
			
			recIO = new RecIOSQLite() ;
			StringBuilder failedr = new StringBuilder() ;
			if(!recIO.initIO(getConnPool(),failedr))
				throw new RuntimeException(failedr.toString()) ;
			
			return recIO;
		}
	}

	private DBConnPool getConnPool()
	{
		String url = "jdbc:sqlite:{$$data_db_sqlite}ttsr._test.db";
		String fp =  "../data/tmp/rec_test/" ;
		File f = new File(fp) ;
		if(!f.exists())
			f.mkdirs() ;
		
		url = url.replace("{$$data_db_sqlite}",fp) ;
		System.out.println("db url="+url) ;
		DBConnPool connPool = new DBConnPool(DBType.sqlite, "", "org.sqlite.JDBC", url,null, null,
				null, "0", "10",this.getClass().getClassLoader());
		return connPool ;
	}
}
