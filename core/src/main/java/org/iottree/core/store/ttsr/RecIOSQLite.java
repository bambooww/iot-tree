package org.iottree.core.store.ttsr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.UATag;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.store.SourceJDBC;
import org.iottree.core.store.StoreManager;
import org.iottree.core.store.gdb.DBResult;
import org.iottree.core.store.gdb.DBUtil;
import org.iottree.core.store.gdb.DataRow;
import org.iottree.core.store.gdb.DataTable;
import org.iottree.core.store.gdb.autofit.DbSql;
import org.iottree.core.store.gdb.autofit.JavaColumnInfo;
import org.iottree.core.store.gdb.autofit.JavaForeignKeyInfo;
import org.iottree.core.store.gdb.autofit.JavaTableInfo;
import org.iottree.core.store.gdb.connpool.DBConnPool;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.iottree.core.util.xmldata.XmlVal;

public class RecIOSQLite extends RecIO
{
	//String innerName ;
	//SourceJDBC innersor = null;
	DBConnPool innerPool = null ;
	

	private static final String TN_TAG_MAP = "rec_tag_map" ;
	
	private static final String TN_TAG_DATA = "rec_tag_data" ;
	

	private JavaTableInfo jtiTagMap = null ;
	private JavaTableInfo jtiTagData = null ;
	
	private DataTable tableTagMap = null ;
	private DataTable tableTagData = null ;
	
	public RecIOSQLite() //(String innername)
	{
		//this.innerName = innername ;
	}
	
	@Override
	protected boolean initIO(DBConnPool cp,StringBuilder failedr)
	{
		
		innerPool = cp;//
		
		try
		{
			tableTagMap = createOrUpTagMapTable(innerPool);// createOrUpTable(innerPool,getJTITagMap()) ;
			tableTagData = DBUtil.createOrUpTable(innerPool,getJTITagData(),true) ;
			return true ;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			failedr.append(e.getMessage()) ;
			return false;
		}
	}
	
	private DataTable createOrUpTagMapTable(DBConnPool cp) throws Exception
	{
		Connection conn =null;
		int tag_maxlen = 500 ;
		try
		{
			String tablen = TN_TAG_MAP ;
			conn = cp.getConnection() ;
			if(DBUtil.tableExists(conn, cp.getDatabase(), tablen))
			{
				return DBUtil.getDBTable(conn,tablen);
			}
			
			String sql ="create table "+tablen +"(TagIdx INTEGER PRIMARY KEY  AUTOINCREMENT,Tag char("+tag_maxlen +"))" ;
			
			List<String> sqls = Arrays.asList(sql) ;
			DBUtil.runSqls(conn, sqls);
			return DBUtil.getDBTable(conn,tablen);
		}
		finally
		{
			if(conn!=null)
				cp.free(conn);
		}
	}
	
//	private DataTable createOrUpTable(DBConnPool cp,JavaTableInfo jti) throws Exception
//	{
//		Connection conn =null;
//		try
//		{
//			String tablen = jti.getTableName() ;
//			conn = cp.getConnection() ;
//			if(DBUtil.tableExists(conn, cp.getDatabase(), tablen))
//			{
//				// TODO check update col length
//				DBUtil.checkAndAlterTable(jti,cp,conn,tablen,null) ;
//				return getDBTable(conn,tablen);
//			}
//			
//			DbSql dbsql = DbSql.getDbSqlByDBType(cp.getDBType()) ;
//			
//			List<String> sqls = dbsql.getCreationSqls(jti);
//			DBUtil.runSqls(conn, sqls);
//			return getDBTable(conn,tablen);
//		}
//		finally
//		{
//			if(conn!=null)
//				cp.free(conn);
//		}
//	}
	
//	private DataTable getDBTable(Connection conn,String tablename)
//			throws Exception
//		{
//				String sel_sql = "select * from "+tablename+" where 1=0" ;
//				
//				PreparedStatement ps = null ;
//				ResultSet rs = null ;
//				try
//				{
//					ps = conn.prepareStatement(sel_sql) ;
//					rs = ps.executeQuery() ;
//					return DBResult.transResultSetToDataTable(rs,tablename,0, -1) ;
//				}
//				finally
//				{
//					if(rs!=null)
//					{
//						try
//						{
//							rs.close() ;
//						}
//						catch(Exception ee) {}
//					}
//						
//					if(ps!=null)
//					{
//						try
//						{
//							ps.close() ;
//						}
//						catch(Exception ee) {}
//					}
//				}
//			}
//		
	
	private JavaTableInfo getJTITagMap() throws Exception
	{
		if(jtiTagMap!=null)
			return jtiTagMap;
		
		int tag_maxlen = 500 ;
		
		ArrayList<JavaColumnInfo> norcols = new ArrayList<JavaColumnInfo>();
		JavaColumnInfo pkcol = null;
		ArrayList<JavaForeignKeyInfo> fks = new ArrayList<JavaForeignKeyInfo>();

		pkcol = new JavaColumnInfo("TagIdx",true, XmlVal.XmlValType.vt_int32, -1,
				false, false,"", false,-1,"",false,false);
		
		norcols.add(new JavaColumnInfo("Tag",false, XmlVal.XmlValType.vt_string, tag_maxlen,
				false, false,"", false,-1, "",false,false));
		
		jtiTagMap = new JavaTableInfo(TN_TAG_MAP, pkcol, norcols, fks);
		return jtiTagMap;
	}
	
	private JavaTableInfo getJTITagData() throws Exception
	{
		if(jtiTagData!=null)
			return jtiTagData;
		
		ArrayList<JavaColumnInfo> norcols = new ArrayList<JavaColumnInfo>();
		JavaColumnInfo pkcol = null;
		ArrayList<JavaForeignKeyInfo> fks = new ArrayList<JavaForeignKeyInfo>();

//		pkcol = new JavaColumnInfo("StartDT",false, XmlVal.XmlValType.vt_int64, -1,
//				false, false,"", false,-1, "",false,false);
		
		norcols.add(new JavaColumnInfo("TagIdx",true, XmlVal.XmlValType.vt_int32, 10,
				true, false,"TagIdx_idx", false,-1,"",false,false));
		
		norcols.add(new JavaColumnInfo("StartDT",false, XmlVal.XmlValType.vt_int64, -1,
				true, false,"StartDT_idx", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("EndDT",false, XmlVal.XmlValType.vt_int64, -1,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("TagData",false, XmlVal.XmlValType.vt_string, 1024,
				false, false,"", false,-1, "",false,false));
		
		jtiTagData = new JavaTableInfo(TN_TAG_DATA, pkcol, norcols, fks);
		return jtiTagData ;
	}
	
	@Override
	protected HashMap<String,Integer> loadBlockTagsMap() throws Exception
	{
		Connection conn = null;

		PreparedStatement ps = null;
		//Statement ps = null ;
		ResultSet rs = null;
		
		String sql = "select * from "+TN_TAG_MAP;
		try
		{
			conn = innerPool.getConnection();
			
			//ps = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			
			ps = conn.prepareStatement(sql);
			
			DataTable dt = null;

			rs = ps.executeQuery();
			dt = DBResult.transResultSetToDataTable(TN_TAG_MAP,0,rs, 0, -1,null);
			HashMap<String,Integer> rets = new HashMap<>() ;
			for(DataRow dr:dt.getRows())
			{
				String tag = dr.getValueStr("Tag", null) ;
				int idx = dr.getValueInt32("TagIdx", -1) ;
				if(Convert.isNullOrEmpty(tag) || idx<=0)
					continue ;
				rets.put(tag, idx) ;
			}
			return rets ;
		}
		finally
		{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e) {}
			}
			
			if(ps!=null)
			{
				try
				{
					ps.close();
				}
				catch(Exception e) {}
			}
			if (conn != null)
				{
				innerPool.free(conn);
				}
		}
	}
	
	synchronized protected Integer getOrAddTagIdx(String tag) throws Exception
	{
		HashMap<String,Integer> t2i = this.getBlockTagsMap() ;
		Integer idx = t2i.get(tag) ;
		if(idx!=null)
			return idx ;
		
		Connection conn = null;

		PreparedStatement ps = null;
		//Statement ps = null ;
		ResultSet rs = null;
		
		String sql = "insert into "+TN_TAG_MAP+"(Tag) values (?)";
		try
		{
			conn = innerPool.getConnection();
			
			//ps = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, tag);

			int rown = ps.executeUpdate() ;
			if(rown!=1)
				return null ;
			
			ps.close();
			
			sql = "select last_insert_rowid() from " + TN_TAG_MAP ;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery() ;
			if(rs.next())
			{
				idx =  rs.getInt(1) ;
				t2i.put(tag, idx);
				return idx ;
			}
			else
				return null ;
		}
		finally
		{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e) {}
			}
			
			if(ps!=null)
			{
				try
				{
					ps.close();
				}
				catch(Exception e) {}
			}
			if (conn != null)
				{
				innerPool.free(conn);
				}
		}
	}

	@Override
	public boolean removeBlocksByTag(String tag)
	{
		return false;
	}

	@Override
	public <T> List<RecBlock<T>> loadBlocksAt(String tag, long at_dt)
	{
		return null;
	}

	@Override
	public boolean saveBlock(RecTag<?> t,RecBlock<?> r) throws Exception
	{
		String tag = t.getName() ;
		Integer idx = getBlockTagsMap().get(tag) ;
		if(idx==null)
		{
			idx = getOrAddTagIdx(tag) ;
			if(idx==null)
				throw new Exception("tag ["+tag+"] has no idx found") ;
		}
		
		if(r.isNew())
		{
			return insertNewBlock(idx,r) ;
		}
		
		if(r.isDirty())
		{
			return updateBlock(idx,r) ;
		}
		
		return false;
	}
	
	private boolean updateBlock(Integer tagidx,RecBlock<?> r) throws Exception
	{
		String sql = "update "+TN_TAG_DATA+" set EndDT=?,TagData=? where TagIdx=? and StartDT=?" ;
		PreparedStatement ps = null;
		Connection conn = null ;
		
		try
		{
			conn = innerPool.getConnection() ;
			ps = conn.prepareStatement(sql) ;
			ps.setLong(1, r.getEndDT());
			ps.setString(2, r.toFormatStr());
			ps.setInt(3, tagidx);
			ps.setLong(4, r.getStartDT());
			
			int rr = ps.executeUpdate() ;
			if(rr==1)
			{
				r.setSavedOk();
			}
			return rr==1 ;
		}
		finally
		{
			if(ps!=null)
				ps.close() ;
			if(conn!=null)
				innerPool.free(conn);
		}
	}
	
	private boolean insertNewBlock(Integer tagidx,RecBlock<?> r) throws Exception
	{
		String sql = "insert into "+TN_TAG_DATA+" (TagIdx,StartDT,EndDT,TagData) values (?,?,?,?)" ;
		PreparedStatement ps = null;
		Connection conn = null ;
		
		try
		{
			conn = innerPool.getConnection() ;
			ps = conn.prepareStatement(sql) ;
			ps.setInt(1,tagidx);
			ps.setLong(2,  r.getStartDT());
			ps.setLong(3, r.getEndDT());
			ps.setString(4, r.toFormatStr());
			
			int rr = ps.executeUpdate() ;
			if(rr==1)
			{
				r.setSavedOk();
			}
			return rr==1 ;
		}
		finally
		{
			if(ps!=null)
				ps.close() ;
			if(conn!=null)
				innerPool.free(conn);
		}
	}

	
}
