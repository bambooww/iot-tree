package org.iottree.core.store.tssdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.UATag;
import org.iottree.core.UAVal;
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
import org.iottree.core.store.tssdb.TSSIO.SavePK;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.iottree.core.util.xmldata.XmlVal;

public class TSSIOSQLite extends TSSIO
{
	//String innerName ;
	//SourceJDBC innersor = null;
	DBConnPool innerPool = null ;
	

	private static final String TN_TAG_MAP = "tss_tag_map" ;
	
	private static final String TN_TAG_DATA = "tss_tag_seg" ;
	

	private JavaTableInfo jtiTagMap = null ;
	private JavaTableInfo jtiTagSegBool = null ;
	private JavaTableInfo jtiTagSegInt = null ;
	private JavaTableInfo jtiTagSegFloat = null ;
	
	//private DataTable tableTagMap = null ;
	//private DataTable tableTagData = null ;
	
	public TSSIOSQLite() //(String innername)
	{
		//this.innerName = innername ;
	}
	
	@Override
	protected boolean initIO(DBConnPool cp,StringBuilder failedr)
	{
		
		innerPool = cp;//
		
		try
		{
			jtiTagSegBool = getJTITagSeg(XmlVal.XmlValType.vt_bool) ;
			jtiTagSegInt = getJTITagSeg(XmlVal.XmlValType.vt_int64) ;
			jtiTagSegFloat = getJTITagSeg(XmlVal.XmlValType.vt_double) ;
			
			createOrUpTagMapTable(innerPool);// createOrUpTable(innerPool,getJTITagMap()) ;
			createOrUpTable(innerPool,jtiTagSegBool) ;
			createOrUpTable(innerPool,jtiTagSegInt) ;
			createOrUpTable(innerPool,jtiTagSegFloat) ;
			return true ;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			failedr.append(e.getMessage()) ;
			return false;
		}
	}
	
	private void createOrUpTagMapTable(DBConnPool cp) throws Exception
	{
		Connection conn =null;
		int tag_maxlen = 500 ;
		try
		{
			String tablen = TN_TAG_MAP ;
			conn = cp.getConnection() ;
			if(DBUtil.tableExists(conn, cp.getDatabase(), tablen))
			{
				//return getDBTable(conn,tablen);
				return ;
			}
			
			String sql ="create table "+tablen +"(TagIdx INTEGER PRIMARY KEY  AUTOINCREMENT,Tag char("+tag_maxlen +"))" ;
			
			List<String> sqls = Arrays.asList(sql) ;
			DBUtil.runSqls(conn, sqls);
			//return getDBTable(conn,tablen);
		}
		finally
		{
			if(conn!=null)
				cp.free(conn);
		}
	}
	
	private void createOrUpTable(DBConnPool cp,JavaTableInfo jti) throws Exception
	{
		Connection conn =null;
		try
		{
			String tablen = jti.getTableName() ;
			conn = cp.getConnection() ;
			if(DBUtil.tableExists(conn, cp.getDatabase(), tablen))
			{
				DBUtil.checkAndAlterTable(jti,cp,conn,tablen,null) ;
				//return getDBTable(conn,tablen);
				return  ;
			}
			
			DbSql dbsql = DbSql.getDbSqlByDBType(cp.getDBType()) ;
			
			List<String> sqls = dbsql.getCreationSqls(jti);
			DBUtil.runSqls(conn, sqls);
			//return getDBTable(conn,tablen);
			return ;
		}
		finally
		{
			if(conn!=null)
				cp.free(conn);
		}
	}
	
	private DataTable getDBTable(Connection conn,String tablename)
			throws Exception
		{
				String sel_sql = "select * from "+tablename+" where 1=0" ;
				
				PreparedStatement ps = null ;
				ResultSet rs = null ;
				try
				{
					ps = conn.prepareStatement(sel_sql) ;
					rs = ps.executeQuery() ;
					return DBResult.transResultSetToDataTable(rs,tablename,0, -1) ;
				}
				finally
				{
					if(rs!=null)
					{
						try
						{
							rs.close() ;
						}
						catch(Exception ee) {}
					}
						
					if(ps!=null)
					{
						try
						{
							ps.close() ;
						}
						catch(Exception ee) {}
					}
				}
			}
		
	
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
	
	private JavaTableInfo getJTITagSeg(XmlVal.XmlValType col_valtp) throws Exception
	{
		String tablen = TSSTagParam.calTableName(TN_TAG_DATA, col_valtp) ;
		
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
		
		norcols.add(new JavaColumnInfo("Valid",false, XmlVal.XmlValType.vt_bool, -1,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("Val",false, col_valtp, -1,
				false, false,"", false,-1, "",false,false));
		
		return new JavaTableInfo(tablen, pkcol, norcols, fks);
	}
	
	@Override
	protected HashMap<String,Integer> loadTagsMap() throws Exception
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
		HashMap<String,Integer> t2i = this.getTagsMap() ;
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

//	@Override
//	public boolean removeBlocksByTag(String tag)
//	{
//		return false;
//	}
	
//	@SuppressWarnings("unchecked")
//	@Override
//	public HashMap<Integer,TSSValSeg<Boolean>> loadLastTagsSegBool() throws Exception
//	{
//		HashMap<Integer,TSSValSeg<?>> idx2seg = loadLastTagsSeg(jtiTagSegBool,XmlVal.XmlValType.vt_bool) ;
//		HashMap<Integer,TSSValSeg<Boolean>> rets = new HashMap<>() ;
//		for(Map.Entry<Integer, TSSValSeg<?>> i2s:idx2seg.entrySet())
//		{
//			rets.put(i2s.getKey(), (TSSValSeg<Boolean>)i2s.getValue()) ;
//		}
//		return rets ;
//	}
//	
//	@SuppressWarnings("unchecked")
//	@Override
//	public HashMap<Integer,TSSValSeg<Long>> loadLastTagsSegInt64() throws Exception
//	{
//		HashMap<Integer,TSSValSeg<?>> idx2seg = loadLastTagsSeg(jtiTagSegBool,XmlVal.XmlValType.vt_int64) ;
//		HashMap<Integer,TSSValSeg<Long>> rets = new HashMap<>() ;
//		for(Map.Entry<Integer, TSSValSeg<?>> i2s:idx2seg.entrySet())
//		{
//			rets.put(i2s.getKey(), (TSSValSeg<Long>)i2s.getValue()) ;
//		}
//		return rets ;
//	}
//	
//	@SuppressWarnings("unchecked")
//	@Override
//	public HashMap<Integer,TSSValSeg<Double>> loadLastTagsSegDouble() throws Exception
//	{
//		HashMap<Integer,TSSValSeg<?>> idx2seg = loadLastTagsSeg(jtiTagSegBool,XmlVal.XmlValType.vt_double) ;
//		HashMap<Integer,TSSValSeg<Double>> rets = new HashMap<>() ;
//		for(Map.Entry<Integer, TSSValSeg<?>> i2s:idx2seg.entrySet())
//		{
//			rets.put(i2s.getKey(), (TSSValSeg<Double>)i2s.getValue()) ;
//		}
//		return rets ;
//	}
	
	private JavaTableInfo getJTIByClass(Class<?> c)
	{
		if(c==Boolean.class)
			return this.jtiTagSegBool ;
		if(c==Long.class)
			return this.jtiTagSegInt ;
		if(c==Double.class)
			return this.jtiTagSegFloat ;
		return null ;
	}
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public <T> HashMap<Integer,TSSValSeg<T>> loadLastTagsSeg(Class<T> c) throws Exception
	{
		JavaTableInfo jti = getJTIByClass(c) ;
		if(jti==null)
			throw new IllegalArgumentException("no Table Info found with class="+c.getCanonicalName()) ;
		
		String sql = "select max(StartDT) as StartDT,TagIdx,EndDT,Valid,Val from "+jti.getTableName()+" group by TagIdx" ;
		
		Connection conn = null;

		PreparedStatement ps = null;
		//Statement ps = null ;
		ResultSet rs = null;
		try
		{
			conn = innerPool.getConnection();
			
			//ps = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			
			ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();
			DataTable dt = DBResult.transResultSetToDataTable("tt",0,rs, 0, -1,null);
			
			HashMap<Integer,TSSValSeg<T>> rets = new HashMap<>() ;
			for(DataRow dr:dt.getRows())
			{
				long startdt = dr.getValueInt64("StartDT", -1) ;
				int tagidx = dr.getValueInt32("TagIdx", -1) ;
				long enddt = dr.getValueInt64("EndDT", -1) ;
				if(startdt<=0||tagidx<=0||enddt<=0)
					continue ;
				boolean valid = dr.getValueBool("Valid", false) ;
				@SuppressWarnings("unchecked")
				T val = (T)dr.getValue("Val") ;
				TSSValSeg<T> seg = null ;
				seg = (TSSValSeg<T>)(new TSSValSeg<T>(startdt,enddt,valid,val,false));
				
					rets.put(tagidx, seg) ;
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

//	@Override
//	public <T> List<TSSValSeg<T>> loadSegAt(String tag, long at_dt)
//	{
//		return null;
//	}

	@Override
	public boolean saveValSeg(TSSTagSegs<?> t,TSSValSeg<?> r) throws Exception
	{
		
		String tag = t.getTag() ;
		Integer idx = getTagsMap().get(tag) ;
		if(idx==null)
		{
			idx = getOrAddTagIdx(tag) ;
			System.out.println(tag + "  new idx "+idx) ;
			if(idx==null)
				throw new Exception("tag ["+tag+"] has no idx found") ;
		}
		
		Connection conn = null ;
		try
		{
			conn = innerPool.getConnection() ;
			
			if(r.isNew())
			{
				return insertValSeg(conn,t.param,idx,r) ;
			}
	
			return updateValSeg(conn,t.param,idx,r) ;
		}
		finally
		{
			if(conn!=null)
				innerPool.free(conn);
		}
	}
	
	private boolean saveValSeg(Connection conn,Integer idx,TSSTagSegs<?> t,TSSValSeg<?> r) throws Exception
	{
		if(r.isNew())
		{
			return insertValSeg(conn,t.param,idx,r) ;
		}

		return updateValSeg(conn,t.param,idx,r) ;
	}
	
	private void saveTagSegsPK(Connection conn,SavePK pk,Integer idx) throws Exception
	{
		for(int i = 0 ; i < pk.mem_seg_num ; i ++)
		{
			TSSValSeg<?> v = pk.segs.memSegs.get(i) ;
			saveValSeg(conn,idx,pk.segs,v) ;
		}
		
		if(pk.last_seg_enddt>0)
		{
			saveValSeg(conn,idx,pk.segs,pk.segs.lastSeg) ;
		}
	}
	
	@Override
	protected int saveTagSegsPKS(List<SavePK> pks) throws Exception
	{
		for(SavePK pk:pks)
		{
			String tag = pk.getTag() ;
			Integer idx = getTagsMap().get(tag) ;
			if(idx==null)
			{
				idx = getOrAddTagIdx(tag) ;
				System.out.println(tag + "  new idx "+idx) ;
				if(idx==null)
					throw new Exception("tag ["+tag+"] has no idx found") ;
			}
		}
		
		int rown = 0 ;
		Connection conn = null;
		boolean bautoc = true ;
		try
		{
			conn = innerPool.getConnection() ;
			bautoc = conn.getAutoCommit() ;
			conn.setAutoCommit(false);
			for(SavePK pk:pks)
			{
				String tag = pk.getTag() ;
				Integer idx = getTagsMap().get(tag) ;
				saveTagSegsPK(conn,pk,idx) ;
				rown += pk.getAffectRowNum() ;
			}
			conn.commit();
			return rown ;
		}
		finally
		{
			if(conn!=null)
			{
				conn.setAutoCommit(bautoc);
				innerPool.free(conn);
			}
		}
	}
	
	private boolean updateValSeg(Connection conn,TSSTagParam pm,Integer tagidx,TSSValSeg<?> r) throws Exception
	{
		String sql = "update "+pm.getTableName(TN_TAG_DATA)+" set EndDT=? where TagIdx=? and StartDT=?" ;
		PreparedStatement ps = null;
		//Connection conn = null ;
		//long st = System.currentTimeMillis() ;
		try
		{
			//conn = innerPool.getConnection() ;
			ps = conn.prepareStatement(sql) ;
			ps.setLong(1, r.getEndDT());
			ps.setInt(2, tagidx);
			ps.setLong(3, r.getStartDT());
			
			int rr = ps.executeUpdate() ;
			if(rr==1)
			{
				r.setSavedOk();
			}
			
			//System.out.println("update val seg "+tagidx+" "+r.getEndDT()+" cost="+(System.currentTimeMillis()-st)) ;
			return rr==1 ;
		}
		finally
		{
			if(ps!=null)
				ps.close() ;
//			if(conn!=null)
//				innerPool.free(conn);
		}
	}
	
	private boolean insertValSeg(Connection conn,TSSTagParam pm,Integer tagidx,TSSValSeg<?> r) throws Exception
	{
		String sql = "insert into "+pm.getTableName(TN_TAG_DATA)+" (TagIdx,StartDT,EndDT,Valid,Val) values (?,?,?,?,?)" ;
		PreparedStatement ps = null;
		//Connection conn = null ;
		//long st = System.currentTimeMillis() ;
		try
		{
			//conn = innerPool.getConnection() ;
			ps = conn.prepareStatement(sql) ;
			ps.setInt(1,tagidx);
			ps.setLong(2,  r.getStartDT());
			ps.setLong(3, r.getEndDT());
			ps.setBoolean(4, r.isValid());
			//if(r.isValid())
				ps.setObject(5, r.val);
			//else
			//	ps.setNull(parameterIndex, sqlType);
			
			int rr = ps.executeUpdate() ;
			if(rr==1)
			{
				r.setSavedOk();
			}
			//System.out.println("insert val seg "+tagidx+" "+r.getEndDT()+" cost="+(System.currentTimeMillis()-st)) ;
			return rr==1 ;
		}
		finally
		{
			if(ps!=null)
				ps.close() ;
//			if(conn!=null)
//				innerPool.free(conn);
		}
	}

	
}
