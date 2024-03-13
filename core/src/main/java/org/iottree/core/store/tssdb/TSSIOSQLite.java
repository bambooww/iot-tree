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
import org.iottree.core.store.gdb.IDBSelectCallback;
import org.iottree.core.store.gdb.autofit.DbSql;
import org.iottree.core.store.gdb.autofit.JavaColumnInfo;
import org.iottree.core.store.gdb.autofit.JavaForeignKeyInfo;
import org.iottree.core.store.gdb.autofit.JavaTableInfo;
import org.iottree.core.store.gdb.connpool.DBConnPool;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.XmlVal;

public class TSSIOSQLite extends TSSIO
{
	private static ILogger log = LoggerManager.getLogger(TSSIOSQLite.class);

	// String innerName ;
	// SourceJDBC innersor = null;
	DBConnPool innerPool = null;

	private static final String TN_TAG_MAP = "tss_tag_map";

	private static final String TN_TAG_SEG = "tss_tag_seg";

	private JavaTableInfo jtiTagMap = null;
	private JavaTableInfo jtiTagSegBool = null;
	private JavaTableInfo jtiTagSegInt = null;
	private JavaTableInfo jtiTagSegFloat = null;

	// private DataTable tableTagMap = null ;
	// private DataTable tableTagData = null ;

	public TSSIOSQLite(TSSAdapter adp) // (String innername)
	{
		super(adp) ;
		// this.innerName = innername ;
	}

	@Override
	public boolean initIO(DBConnPool cp, StringBuilder failedr)
	{

		innerPool = cp;//

		try
		{
			jtiTagSegBool = getJTITagSeg(XmlVal.XmlValType.vt_bool);
			jtiTagSegInt = getJTITagSeg(XmlVal.XmlValType.vt_int64);
			jtiTagSegFloat = getJTITagSeg(XmlVal.XmlValType.vt_double);

			createOrUpTagMapTable(innerPool);// createOrUpTable(innerPool,getJTITagMap())
												// ;
			DBUtil.createOrUpTable(innerPool, jtiTagSegBool);
			DBUtil.createOrUpTable(innerPool, jtiTagSegInt);
			DBUtil.createOrUpTable(innerPool, jtiTagSegFloat);
			return true;
		}
		catch ( Exception e)
		{
			e.printStackTrace();
			failedr.append(e.getMessage());
			return false;
		}
	}

	private void createOrUpTagMapTable(DBConnPool cp) throws Exception
	{
		Connection conn = null;
		int tag_maxlen = 500;
		try
		{
			String tablen = TN_TAG_MAP;
			conn = cp.getConnection();
			if (DBUtil.tableExists(conn, cp.getDatabase(), tablen))
			{
				// return getDBTable(conn,tablen);
				return;
			}

			String sql = "create table " + tablen + "(TagIdx INTEGER PRIMARY KEY  AUTOINCREMENT,Tag char(" + tag_maxlen
					+ "))";

			List<String> sqls = Arrays.asList(sql);
			DBUtil.runSqls(conn, sqls);
			// return getDBTable(conn,tablen);
		}
		finally
		{
			if (conn != null)
				cp.free(conn);
		}
	}

	

	private DataTable getDBTable(Connection conn, String tablename) throws Exception
	{
		String sel_sql = "select * from " + tablename + " where 1=0";

		PreparedStatement ps = null;
		ResultSet rs = null;
		try
		{
			ps = conn.prepareStatement(sel_sql);
			rs = ps.executeQuery();
			return DBResult.transResultSetToDataTable(rs, tablename, 0, -1);
		}
		finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch ( Exception ee)
				{
				}
			}

			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch ( Exception ee)
				{
				}
			}
		}
	}

	private JavaTableInfo getJTITagMap() throws Exception
	{
		if (jtiTagMap != null)
			return jtiTagMap;

		int tag_maxlen = 500;

		ArrayList<JavaColumnInfo> norcols = new ArrayList<JavaColumnInfo>();
		JavaColumnInfo pkcol = null;
		ArrayList<JavaForeignKeyInfo> fks = new ArrayList<JavaForeignKeyInfo>();

		pkcol = new JavaColumnInfo("TagIdx", true, XmlVal.XmlValType.vt_int32, -1, false, false, "", false, -1, "",
				false, false);

		norcols.add(new JavaColumnInfo("Tag", false, XmlVal.XmlValType.vt_string, tag_maxlen, false, false, "", false,
				-1, "", false, false));

		jtiTagMap = new JavaTableInfo(TN_TAG_MAP, pkcol, norcols, fks);
		return jtiTagMap;
	}

	private JavaTableInfo getJTITagSeg(XmlVal.XmlValType col_valtp) throws Exception
	{
		String tablen = TSSTagParam.calTableName(TN_TAG_SEG, col_valtp);

		ArrayList<JavaColumnInfo> norcols = new ArrayList<JavaColumnInfo>();
		JavaColumnInfo pkcol = null;
		ArrayList<JavaForeignKeyInfo> fks = new ArrayList<JavaForeignKeyInfo>();

		// pkcol = new JavaColumnInfo("StartDT",false,
		// XmlVal.XmlValType.vt_int64, -1,
		// false, false,"", false,-1, "",false,false);

		norcols.add(new JavaColumnInfo("TagIdx", true, XmlVal.XmlValType.vt_int32, 10, true, false, "TagIdx_idx", false,
				-1, "", false, false));

		norcols.add(new JavaColumnInfo("StartDT", false, XmlVal.XmlValType.vt_int64, -1, true, false, "StartDT_idx",
				false, -1, "", false, false));

		norcols.add(new JavaColumnInfo("EndDT", false, XmlVal.XmlValType.vt_int64, -1, true, false, "EndDT_idx", false,
				-1, "", false, false));

		norcols.add(new JavaColumnInfo("Valid", false, XmlVal.XmlValType.vt_bool, -1, false, false, "", false, -1, "",
				false, false));

		norcols.add(new JavaColumnInfo("Val", false, col_valtp, -1, false, false, "", false, -1, "", false, false));

		return new JavaTableInfo(tablen, pkcol, norcols, fks);
	}

	@Override
	public HashMap<String, Integer> readTag2IdxMap() throws Exception
	{
		Connection conn = null;

		PreparedStatement ps = null;
		// Statement ps = null ;
		ResultSet rs = null;

		String sql = "select * from " + TN_TAG_MAP;
		try
		{
			conn = innerPool.getConnection();

			// ps =
			// conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

			ps = conn.prepareStatement(sql);

			DataTable dt = null;

			rs = ps.executeQuery();
			dt = DBResult.transResultSetToDataTable(TN_TAG_MAP, 0, rs, 0, -1, null);
			HashMap<String, Integer> rets = new HashMap<>();
			for (DataRow dr : dt.getRows())
			{
				String tag = dr.getValueStr("Tag", null);
				int idx = dr.getValueInt32("TagIdx", -1);
				if (Convert.isNullOrEmpty(tag) || idx <= 0)
					continue;
				rets.put(tag, idx);
			}
			return rets;
		}
		finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch ( Exception e)
				{
				}
			}

			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch ( Exception e)
				{
				}
			}
			if (conn != null)
			{
				innerPool.free(conn);
			}
		}
	}

	synchronized protected Integer getOrAddTagIdx(String tag) throws Exception
	{
		HashMap<String, Integer> t2i = this.getTagsMap();
		Integer idx = t2i.get(tag);
		if (idx != null)
			return idx;

		Connection conn = null;

		PreparedStatement ps = null;
		// Statement ps = null ;
		ResultSet rs = null;

		String sql = "insert into " + TN_TAG_MAP + "(Tag) values (?)";
		try
		{
			conn = innerPool.getConnection();

			// ps =
			// conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

			ps = conn.prepareStatement(sql);
			ps.setString(1, tag);

			int rown = ps.executeUpdate();
			if (rown != 1)
				return null;

			ps.close();

			sql = "select last_insert_rowid() from " + TN_TAG_MAP;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next())
			{
				idx = rs.getInt(1);
				t2i.put(tag, idx);
				return idx;
			}
			else
				return null;
		}
		finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch ( Exception e)
				{
				}
			}

			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch ( Exception e)
				{
				}
			}
			if (conn != null)
			{
				innerPool.free(conn);
			}
		}
	}

	// @Override
	// public boolean removeBlocksByTag(String tag)
	// {
	// return false;
	// }

	// @SuppressWarnings("unchecked")
	// @Override
	// public HashMap<Integer,TSSValSeg<Boolean>> loadLastTagsSegBool() throws
	// Exception
	// {
	// HashMap<Integer,TSSValSeg<?>> idx2seg =
	// loadLastTagsSeg(jtiTagSegBool,XmlVal.XmlValType.vt_bool) ;
	// HashMap<Integer,TSSValSeg<Boolean>> rets = new HashMap<>() ;
	// for(Map.Entry<Integer, TSSValSeg<?>> i2s:idx2seg.entrySet())
	// {
	// rets.put(i2s.getKey(), (TSSValSeg<Boolean>)i2s.getValue()) ;
	// }
	// return rets ;
	// }
	//
	// @SuppressWarnings("unchecked")
	// @Override
	// public HashMap<Integer,TSSValSeg<Long>> loadLastTagsSegInt64() throws
	// Exception
	// {
	// HashMap<Integer,TSSValSeg<?>> idx2seg =
	// loadLastTagsSeg(jtiTagSegBool,XmlVal.XmlValType.vt_int64) ;
	// HashMap<Integer,TSSValSeg<Long>> rets = new HashMap<>() ;
	// for(Map.Entry<Integer, TSSValSeg<?>> i2s:idx2seg.entrySet())
	// {
	// rets.put(i2s.getKey(), (TSSValSeg<Long>)i2s.getValue()) ;
	// }
	// return rets ;
	// }
	//
	// @SuppressWarnings("unchecked")
	// @Override
	// public HashMap<Integer,TSSValSeg<Double>> loadLastTagsSegDouble() throws
	// Exception
	// {
	// HashMap<Integer,TSSValSeg<?>> idx2seg =
	// loadLastTagsSeg(jtiTagSegBool,XmlVal.XmlValType.vt_double) ;
	// HashMap<Integer,TSSValSeg<Double>> rets = new HashMap<>() ;
	// for(Map.Entry<Integer, TSSValSeg<?>> i2s:idx2seg.entrySet())
	// {
	// rets.put(i2s.getKey(), (TSSValSeg<Double>)i2s.getValue()) ;
	// }
	// return rets ;
	// }

	private JavaTableInfo getJTIByClass(Class<?> c)
	{
		if (c == Boolean.class)
			return this.jtiTagSegBool;
		if (c == Long.class)
			return this.jtiTagSegInt;
		if (c == Double.class)
			return this.jtiTagSegFloat;
		return null;
	}

	
	// @Override
	// public boolean saveValSeg(TSSTagSegs<?> t,TSSValSeg<?> r) throws
	// Exception
	// {
	//
	// String tag = t.getTag() ;
	// Integer idx = getTagsMap().get(tag) ;
	// if(idx==null)
	// {
	// idx = getOrAddTagIdx(tag) ;
	// System.out.println(tag + " new idx "+idx) ;
	// if(idx==null)
	// throw new Exception("tag ["+tag+"] has no idx found") ;
	// }
	//
	// Connection conn = null ;
	// try
	// {
	// conn = innerPool.getConnection() ;
	//
	// if(r.isNew())
	// {
	// return insertValSeg(conn,t.param,idx,r) ;
	// }
	//
	// return updateValSeg(conn,t.param,idx,r) ;
	// }
	// finally
	// {
	// if(conn!=null)
	// innerPool.free(conn);
	// }
	// }

	private boolean saveValSeg(Connection conn, Integer idx, TSSTagSegs<?> t, TSSValSeg<?> r) throws Exception
	{
		if (r.isNew())
		{
			return insertValSeg(conn, t.param,t, idx, r);
		}

		return updateValSeg(conn, t.param,t, idx, r);
	}

	private void saveTagSegsPK(Connection conn, TSSSavePK pk, Integer idx) throws Exception
	{
		for (int i = 0; i < pk.mem_seg_num; i++)
		{
			TSSValSeg<?> v = pk.segs.memSegs.get(i);
			saveValSeg(conn, idx, pk.segs, v);
		}

		if (pk.last_seg_enddt > 0)
		{
			saveValSeg(conn, idx, pk.segs, pk.segs.lastSeg);
		}
	}

	@Override
	protected int saveTagSegsPKS(List<TSSSavePK> pks) throws Exception
	{
		for (TSSSavePK pk : pks)
		{
			String tag = pk.getTag();
			Integer idx = getTagsMap().get(tag);
			if (idx == null)
			{
				idx = getOrAddTagIdx(tag);

				if (log.isDebugEnabled())
					log.debug(tag + "  new idx " + idx);

				if (idx == null)
					throw new Exception("tag [" + tag + "] has no idx found");
				
				pk.segs.tagIdx = idx ; //must set
			}
		}

		int rown = 0;
		Connection conn = null;
		boolean bautoc = true;
		try
		{
			conn = innerPool.getConnection();
			bautoc = conn.getAutoCommit();
			conn.setAutoCommit(false);
			for (TSSSavePK pk : pks)
			{
				String tag = pk.getTag();
				Integer idx = getTagsMap().get(tag);
				saveTagSegsPK(conn, pk, idx);
				rown += pk.getAffectRowNum();
			}
			conn.commit();
			return rown;
		}
		finally
		{
			if (conn != null)
			{
				conn.setAutoCommit(bautoc);
				innerPool.free(conn);
			}
		}
	}

	private boolean updateValSeg(Connection conn, TSSTagParam pm,TSSTagSegs<?> tagsegs, Integer tagidx, TSSValSeg<?> r) throws Exception
	{
		String sql = "update " + pm.getTableName(TN_TAG_SEG) + " set EndDT=? where TagIdx=? and StartDT=?";
		PreparedStatement ps = null;
		// Connection conn = null ;
		// long st = System.currentTimeMillis() ;
		try
		{
			long enddt = r.getEndDT() ;
			
			ps = conn.prepareStatement(sql);
			ps.setLong(1, enddt);
			ps.setInt(2, tagidx);
			ps.setLong(3, r.getStartDT());

			int rr = ps.executeUpdate();
			if (rr == 1)
			{
				r.setSavedOk();
				
				// this.belongTo.fireTagValSegSaved(false,tagsegs,tagidx, r, enddt);
			}

			return rr == 1;
		}
		finally
		{
			if (ps != null)
				ps.close();
			// if(conn!=null)
			// innerPool.free(conn);
		}
	}

	private boolean insertValSeg(Connection conn, TSSTagParam pm,TSSTagSegs<?> tagsegs, Integer tagidx, TSSValSeg<?> r) throws Exception
	{
		String sql = "insert into " + pm.getTableName(TN_TAG_SEG)
				+ " (TagIdx,StartDT,EndDT,Valid,Val) values (?,?,?,?,?)";
		PreparedStatement ps = null;
		// Connection conn = null ;
		// long st = System.currentTimeMillis() ;
		try
		{
			// conn = innerPool.getConnection() ;
			long enddt = r.getEndDT() ;
			ps = conn.prepareStatement(sql);
			ps.setInt(1, tagidx);
			ps.setLong(2, r.getStartDT());
			ps.setLong(3, r.getEndDT());
			ps.setBoolean(4, r.isValid());
			// if(r.isValid())
			ps.setObject(5, r.val);
			// else
			// ps.setNull(parameterIndex, sqlType);

			int rr = ps.executeUpdate();
			if (rr == 1)
			{
				r.setSavedOk();
				
				// this.belongTo.fireTagValSegSaved(true,tagsegs,tagidx, r,enddt);
			}
			// System.out.println("insert val seg "+tagidx+" "+r.getEndDT()+"
			// cost="+(System.currentTimeMillis()-st)) ;
			return rr == 1;
		}
		finally
		{
			if (ps != null)
				ps.close();
			// if(conn!=null)
			// innerPool.free(conn);
		}
	}

	// - get data support
	
	@SuppressWarnings("unchecked")
	private <T> TSSValSeg<T> transDataRow2ValSeg(DataRow dr,UAVal.ValTP  val_vt)
	{
		long startdt = dr.getValueInt64("StartDT", 0);
		long enddt = dr.getValueInt64("EndDT", 0);
		boolean bvalid = dr.getValueBool("Valid", true);
		
		if (!bvalid)
			return new TSSValSeg<T>(startdt, enddt, false, null, false);
		Object val = dr.getValue("Val");
		if(val==null)
			return new TSSValSeg<T>(startdt, enddt, true, null, false);
		
		if(val instanceof Number)
		{
			Number num = (Number)val ;
			switch(val_vt)
			{
//			case vt_date:
//				val = new Date(num.longValue()) ;
//				break ;
			case vt_double:
				val = num.doubleValue() ;
				break ;
			case vt_float:
				val = num.floatValue() ;
				break ;
			case vt_int64:
				val =  num.longValue() ;
				break ;
			case vt_int32:
				val = num.intValue();
				break ;
			case vt_int16:
				val = num.shortValue() ;
				break ;
			case vt_bool:
				val = num.doubleValue()>0 ;
				break ;
			case vt_byte:
				val = num.byteValue() ;
				break ;
			default:
				break ;
			}
		}
		
		return new TSSValSeg<T>(startdt, enddt, true, (T) val, false);
	}
	
	

	private <T> TSSValSeg<T> readValSegAt(Connection conn,TSSTagSegs<T> ts, long at_dt) throws Exception
	{
		String tag = ts.getTag();
		Integer idx = getTagsMap().get(tag);
		if (idx == null)
			return null;
		String tablen = ts.param.getTableName(TN_TAG_SEG);
		String sql = "select * from " + tablen + " where TagIdx=? and StartDT<=? and (EndDT>? or EndDT=StartDT) order by StartDT";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			ps = conn.prepareStatement(sql);
			ps.setInt(1, idx);
			ps.setLong(2, at_dt);
			ps.setLong(3, at_dt);

			rs = ps.executeQuery();
			// DBResult.transResultSetToDataTable(rs, tablen, 0, -1, cb);
			DataTable dt = DBResult.transResultSetToDataTable(TN_TAG_MAP, 0, rs, 0, -1, null);
			if (dt.getRowNum() == 0)
				return null;
			if (dt.getRowNum() > 1)
				throw new Exception("more than 1 seg found in one time point");
			DataRow dr = dt.getRow(0);
			UAVal.ValTP valtp = ts.param.valTp ;
			
			return transDataRow2ValSeg(dr,valtp) ;
		}
		finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch ( Exception e)
				{
				}
			}

			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch ( Exception e)
				{
				}
			}
		}
	}
	

	private <T> TSSValSeg<T> readValSegPrev(Connection conn,TSSTagSegs<T> ts,TSSValSeg<T> vs) throws Exception
	{
		String tag = ts.getTag();
		Integer idx = getTagsMap().get(tag);
		if (idx == null)
			return null;
		String tablen = ts.param.getTableName(TN_TAG_SEG);
		String sql = "select * from " + tablen + " where TagIdx=? and StartDT<? order by StartDT desc limit 1";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			ps = conn.prepareStatement(sql);
			ps.setInt(1, idx);
			ps.setLong(2, vs.startDT);

			rs = ps.executeQuery();
			// DBResult.transResultSetToDataTable(rs, tablen, 0, -1, cb);
			DataTable dt = DBResult.transResultSetToDataTable(TN_TAG_MAP, 0, rs, 0, -1, null);
			if (dt.getRowNum() == 0)
				return null;
			DataRow dr = dt.getRow(0);
			UAVal.ValTP valtp = ts.param.valTp ;
			
			return transDataRow2ValSeg(dr,valtp) ;
		}
		finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch ( Exception e)
				{
				}
			}

			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch ( Exception e)
				{
				}
			}
		}
	}
	
	public <T> TSSValSeg<T> readValSegNext(TSSTagSegs<T> ts,TSSValSeg<T> vs) throws Exception
	{
		Connection conn =  null ;
		try
		{
			conn = innerPool.getConnection() ;
			return readValSegNext(conn, ts,vs) ;
		}
		finally
		{
			if(conn!=null)
				innerPool.free(conn);
		}
	}
	
	public <T> TSSValSeg<T> readValSegAtOrNext(TSSTagSegs<T> ts,long at_dt) throws Exception
	{
		Connection conn =  null ;
		try
		{
			conn = innerPool.getConnection() ;
			return readValSegAtOrNext(conn, ts,at_dt,true) ;
		}
		finally
		{
			if(conn!=null)
				innerPool.free(conn);
		}
	}
	
	private <T> TSSValSeg<T> readValSegNext(Connection conn,TSSTagSegs<T> ts,TSSValSeg<T> vs) throws Exception
	{
		return readValSegAtOrNext(conn, ts,vs.startDT,false) ;
	}
	
	private <T> TSSValSeg<T> readValSegAtOrNext(Connection conn,TSSTagSegs<T> ts,long at_dt,boolean has_at) throws Exception
	{
		String tag = ts.getTag();
		Integer idx = getTagsMap().get(tag);
		if (idx == null)
			return null;
		String tablen = ts.param.getTableName(TN_TAG_SEG);
		String sql = null;
		if(has_at)
			sql = "select * from " + tablen + " where TagIdx=? and StartDT>=? order by StartDT limit 1";
		else
			sql = "select * from " + tablen + " where TagIdx=? and StartDT>? order by StartDT limit 1";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			ps = conn.prepareStatement(sql);
			ps.setInt(1, idx);
			ps.setLong(2, at_dt);

			rs = ps.executeQuery();
			// DBResult.transResultSetToDataTable(rs, tablen, 0, -1, cb);
			DataTable dt = DBResult.transResultSetToDataTable(TN_TAG_MAP, 0, rs, 0, -1, null);
			if (dt.getRowNum() == 0)
				return null;
			DataRow dr = dt.getRow(0);
			UAVal.ValTP valtp = ts.param.valTp ;
			
			return transDataRow2ValSeg(dr,valtp) ;
		}
		finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch ( Exception e)
				{
				}
			}

			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch ( Exception e)
				{
				}
			}
		}
	}
	
	
	private <T> TSSValSegHit<T> readValSegAt(Connection conn,TSSTagSegs<T> ts,long at_dt,boolean b_prev,boolean b_next) throws Exception
	{
		
		TSSValSeg<T> vs = this.readValSegAt(conn,ts, at_dt) ;
		if(vs==null)
			return null ;
		
		TSSValSeg<T> vs_p = null ;
		if(b_prev)
			vs_p = readValSegPrev(conn,ts,vs) ;

		TSSValSeg<T> vs_n = null ;
		if(b_next)
			vs_n = readValSegNext(conn,ts,vs) ;
		
		return new TSSValSegHit<>(vs_p,vs,vs_n) ;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public <T> HashMap<Integer, TSSValSeg<T>> readTagIdx2MaxValSeg(Class<T> c) throws Exception
	{
		JavaTableInfo jti = getJTIByClass(c);
		if (jti == null)
			throw new IllegalArgumentException("no Table Info found with class=" + c.getCanonicalName());

		String sql = "select max(StartDT) as StartDT,TagIdx,EndDT,Valid,Val from " + jti.getTableName()
				+ " group by TagIdx";

		Connection conn = null;

		PreparedStatement ps = null;
		// Statement ps = null ;
		ResultSet rs = null;
		try
		{
			conn = innerPool.getConnection();

			// ps =
			// conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

			ps = conn.prepareStatement(sql);

			rs = ps.executeQuery();
			DataTable dt = DBResult.transResultSetToDataTable("tt", 0, rs, 0, -1, null);

			HashMap<Integer, TSSValSeg<T>> rets = new HashMap<>();
			for (DataRow dr : dt.getRows())
			{
				long startdt = dr.getValueInt64("StartDT", -1);
				int tagidx = dr.getValueInt32("TagIdx", -1);
				long enddt = dr.getValueInt64("EndDT", -1);
				if (startdt <= 0 || tagidx <= 0 || enddt <= 0)
					continue;
				boolean valid = dr.getValueBool("Valid", false);
				@SuppressWarnings("unchecked")
				T val = (T) dr.getValue("Val");
				TSSValSeg<T> seg = null;
				seg = (TSSValSeg<T>) (new TSSValSeg<T>(startdt, enddt, valid, val, false));

				rets.put(tagidx, seg);
			}
			return rets;
		}
		finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch ( Exception e)
				{
				}
			}

			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch ( Exception e)
				{
				}
			}
			if (conn != null)
			{
				innerPool.free(conn);
			}
		}
	}
	
	@Override
	public <T> HashMap<Integer, TSSValSeg<T>> readTagIdx2MinSeg(Class<T> c) throws Exception
	{
		JavaTableInfo jti = getJTIByClass(c);
		if (jti == null)
			throw new IllegalArgumentException("no Table Info found with class=" + c.getCanonicalName());

		String sql = "select min(StartDT) as StartDT,TagIdx,EndDT,Valid,Val from " + jti.getTableName()
				+ " group by TagIdx";

		Connection conn = null;

		PreparedStatement ps = null;
		// Statement ps = null ;
		ResultSet rs = null;
		try
		{
			conn = innerPool.getConnection();

			// ps =
			// conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

			ps = conn.prepareStatement(sql);

			rs = ps.executeQuery();
			DataTable dt = DBResult.transResultSetToDataTable("tt", 0, rs, 0, -1, null);

			HashMap<Integer, TSSValSeg<T>> rets = new HashMap<>();
			for (DataRow dr : dt.getRows())
			{
				long startdt = dr.getValueInt64("StartDT", -1);
				int tagidx = dr.getValueInt32("TagIdx", -1);
				long enddt = dr.getValueInt64("EndDT", -1);
				if (startdt <= 0 || tagidx <= 0 || enddt <= 0)
					continue;
				boolean valid = dr.getValueBool("Valid", false);
				@SuppressWarnings("unchecked")
				T val = (T) dr.getValue("Val");
				TSSValSeg<T> seg = null;
				seg = (TSSValSeg<T>) (new TSSValSeg<T>(startdt, enddt, valid, val, false));

				rets.put(tagidx, seg);
			}
			return rets;
		}
		finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch ( Exception e)
				{
				}
			}

			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch ( Exception e)
				{
				}
			}
			if (conn != null)
			{
				innerPool.free(conn);
			}
		}
	}

	
	public <T> TSSValSegHit<T> readValSegAt(TSSTagSegs<T> ts,long at_dt,boolean b_prev,boolean b_next) throws Exception
	{
		Connection conn =null ;
		try
		{
			conn = innerPool.getConnection() ;
			return readValSegAt(conn,ts,at_dt,b_prev,b_next) ;
		}
		finally
		{
			if(conn!=null)
				innerPool.free(conn);
		}
	}

	@Override
	public <T> List<TSSValSeg<T>> readValSegs(TSSTagSegs<T> ts, long from_dt, long to_dt,boolean b_desc,int limit_num) throws Exception
	{
		if (from_dt > to_dt)
			return null;

		String tag = ts.getTag();
		Integer idx = getTagsMap().get(tag);
		if (idx == null)
			return null;
		String tablen = ts.param.getTableName(TN_TAG_SEG);
		String sql = "select * from " + tablen + " where TagIdx=? and EndDT>=? and StartDT<? order by StartDT";
		if(b_desc)
			sql += " desc" ;
		if(limit_num>0)
			sql += " limit "+limit_num ;
		
		Connection conn = null;

		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			conn = innerPool.getConnection();

			// ps =
			// conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

			ps = conn.prepareStatement(sql);
			ps.setInt(1, idx);
			ps.setLong(2, from_dt);
			ps.setLong(3, to_dt);

			rs = ps.executeQuery();
			// DBResult.transResultSetToDataTable(rs, tablen, 0, -1, cb);
			DataTable dt = DBResult.transResultSetToDataTable(TN_TAG_MAP, 0, rs, 0, -1, null);
			ArrayList<TSSValSeg<T>> rets = new ArrayList<>();
			
			UAVal.ValTP valtp = ts.param.valTp ;
			for (DataRow dr : dt.getRows())
			{
				TSSValSeg<T> vs = transDataRow2ValSeg(dr,valtp) ;
				rets.add(vs);
			}
			return rets;
		}
		finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch ( Exception e)
				{
				}
			}

			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch ( Exception e)
				{
				}
			}
			if (conn != null)
			{
				innerPool.free(conn);
			}
		}
	}
	
	public <T> List<TSSValSeg<T>> readValSegAt2(TSSTagSegs<T> ts, long at_dt1,long at_dt2) throws Exception
	{
		String tag = ts.getTag();
		Integer idx = getTagsMap().get(tag);
		if (idx == null)
			return null;
		String tablen = ts.param.getTableName(TN_TAG_SEG);
		String sql = "select * from " + tablen +
				" where TagIdx=? and (StartDT<=? and EndDT>? or StartDT<=? and EndDT>?) order by StartDT";

		Connection conn = null;

		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			conn = innerPool.getConnection();

			// ps =
			// conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

			ps = conn.prepareStatement(sql);
			ps.setInt(1, idx);
			ps.setLong(2, at_dt1);
			ps.setLong(3, at_dt1);
			ps.setLong(4, at_dt2);
			ps.setLong(5, at_dt2);

			rs = ps.executeQuery();
			// DBResult.transResultSetToDataTable(rs, tablen, 0, -1, cb);
			DataTable dt = DBResult.transResultSetToDataTable(TN_TAG_MAP, 0, rs, 0, -1, null);
			if (dt.getRowNum() == 0)
				return null;
			if (dt.getRowNum() > 2)
				throw new Exception("more than 1 seg found in one time point");
			
			UAVal.ValTP valtp = ts.param.valTp ;
			
			if(dt.getRowNum()==1)
			{
				DataRow dr = dt.getRow(0);

				TSSValSeg<T> vs = transDataRow2ValSeg(dr,valtp) ;
				return Arrays.asList(vs,vs) ;
			}
			else
			{//2
				DataRow dr1 = dt.getRow(0);
				DataRow dr2 = dt.getRow(1);
				TSSValSeg<T> vs1 = transDataRow2ValSeg(dr1,valtp) ;
				TSSValSeg<T> vs2 = transDataRow2ValSeg(dr2,valtp) ;
				if(vs1.containsDT(at_dt1))
					return Arrays.asList(vs1,vs2) ;
				else
					return Arrays.asList(vs2,vs1) ;
			}
		}
		finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch ( Exception e)
				{
				}
			}

			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch ( Exception e)
				{
				}
			}
			if (conn != null)
			{
				innerPool.free(conn);
			}
		}
	}
	
	public <T> TSSValSeg<T> readValSegAt(TSSTagSegs<T> ts, long at_dt) throws Exception
	{
		Connection conn = null;
		try
		{
			conn = innerPool.getConnection() ;
			return readValSegAt(conn, ts, at_dt) ;
		}
		finally
		{
			if(conn!=null)
				innerPool.free(conn);
		}
	}

	
//	/**
//	 * 根据某个时间点获取这个时间点对应的seg，并且按照时间顺序，读取下一个seg记录，一起输出
//	 * @param <T>
//	 * @param ts
//	 * @param at_dt
//	 * @return
//	 * @throws Exception
//	 */
//	public <T> TSSValSegHitNext<T> readValSegAtAndNext(TSSTagSegs<T> ts, long at_dt) throws Exception
//	{
//		String tag = ts.getTag();
//		Integer idx = getTagsMap().get(tag);
//		if (idx == null)
//			return null;
//		String tablen = ts.param.getTableName(TN_TAG_SEG);
//		String sql = "select * from " + tablen + " where TagIdx=? and (StartDT<=? and EndDT>? or StartDT>?) order by StartDT limit 2";
//
//		Connection conn = null;
//
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//
//		try
//		{
//			conn = innerPool.getConnection();
//
//			// ps =
//			// conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
//
//			ps = conn.prepareStatement(sql);
//			ps.setInt(1, idx);
//			ps.setLong(2, at_dt);
//			ps.setLong(3, at_dt);
//			ps.setLong(4, at_dt);
//
//			rs = ps.executeQuery();
//			// DBResult.transResultSetToDataTable(rs, tablen, 0, -1, cb);
//			DataTable dt = DBResult.transResultSetToDataTable(TN_TAG_MAP, 0, rs, 0, -1, null);
//			if (dt.getRowNum() <= 0)
//				return null;
//			
//			UAVal.ValTP valtp = ts.param.valTp ;
//			
//			if (dt.getRowNum() == 1)
//			{
//				DataRow dr = dt.getRow(0);
//				TSSValSeg<T> seg = transDataRow2ValSeg(dr,valtp) ;
//				if(seg.containsDT(at_dt))
//					return new TSSValSegHitNext<>(seg,null) ;
//				else
//					return new TSSValSegHitNext<>(null,seg) ;
//			}
//			//	throw new Exception("more than 1 seg found in one time point");
//			DataRow dr1 = dt.getRow(0);
//			TSSValSeg<T> seg1 = transDataRow2ValSeg(dr1,valtp) ;
//			DataRow dr2 = dt.getRow(1);
//			TSSValSeg<T> seg2 = transDataRow2ValSeg(dr2,valtp) ;
//			return new TSSValSegHitNext<>(seg1,seg2) ;
//			
//		}
//		finally
//		{
//			if (rs != null)
//			{
//				try
//				{
//					rs.close();
//				}
//				catch ( Exception e)
//				{
//				}
//			}
//
//			if (ps != null)
//			{
//				try
//				{
//					ps.close();
//				}
//				catch ( Exception e)
//				{
//				}
//			}
//			if (conn != null)
//			{
//				innerPool.free(conn);
//			}
//		}
//	}
//	
//	public <T> TSSValSegHitPrev<T> readValSegAtAndPrev(TSSTagSegs<T> ts, long at_dt) throws Exception
//	{
//		String tag = ts.getTag();
//		Integer idx = getTagsMap().get(tag);
//		if (idx == null)
//			return null;
//		String tablen = ts.param.getTableName(TN_TAG_SEG);
//		String sql = "select * from " + tablen + " where TagIdx=? and (StartDT<=? and EndDT>? or EndDT<=?) order by StartDT desc limit 2";
//
//		Connection conn = null;
//
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//
//		try
//		{
//			conn = innerPool.getConnection();
//
//			// ps =
//			// conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
//
//			ps = conn.prepareStatement(sql);
//			ps.setInt(1, idx);
//			ps.setLong(2, at_dt);
//			ps.setLong(3, at_dt);
//			ps.setLong(4, at_dt);
//
//			rs = ps.executeQuery();
//			// DBResult.transResultSetToDataTable(rs, tablen, 0, -1, cb);
//			DataTable dt = DBResult.transResultSetToDataTable(TN_TAG_MAP, 0, rs, 0, -1, null);
//			if (dt.getRowNum() <= 0)
//				return null;
//			
//			UAVal.ValTP valtp = ts.param.valTp ;
//			
//			if (dt.getRowNum() == 1)
//			{
//				DataRow dr = dt.getRow(0);
//				TSSValSeg<T> seg = transDataRow2ValSeg(dr,valtp) ;
//				if(seg.containsDT(at_dt))
//					return new TSSValSegHitPrev<>(seg,null) ;
//				else
//					return new TSSValSegHitPrev<>(null,seg) ;
//			}
//			//	throw new Exception("more than 1 seg found in one time point");
//			DataRow dr1 = dt.getRow(0);
//			TSSValSeg<T> seg1 = transDataRow2ValSeg(dr1,valtp) ;
//			DataRow dr2 = dt.getRow(1);
//			TSSValSeg<T> seg2 = transDataRow2ValSeg(dr2,valtp) ;
//			return new TSSValSegHitPrev<>(seg1,seg2) ;
//			
//		}
//		finally
//		{
//			if (rs != null)
//			{
//				try
//				{
//					rs.close();
//				}
//				catch ( Exception e)
//				{
//				}
//			}
//
//			if (ps != null)
//			{
//				try
//				{
//					ps.close();
//				}
//				catch ( Exception e)
//				{
//				}
//			}
//			if (conn != null)
//			{
//				innerPool.free(conn);
//			}
//		}
//	}
//	
	
	private class SelectCB<T> implements IDBSelectCallback
	{
		TSSTagSegs<T> ts ;
		IValSegSelectCB<T> vsCB = null ;
		
		SelectCB(TSSTagSegs<T> ts,IValSegSelectCB<T> vs_cb)
		{
			this.ts = ts; 
			this.vsCB = vs_cb ;
		}
		@Override
		public boolean onFindDataTable(int tableidx, DataTable dt) throws Exception
		{
			return true;
		}

		@Override
		public boolean onFindDataRow(int tableidx, DataTable dt, int rowidx, DataRow dr) throws Exception
		{
			TSSValSeg<T> vs = TSSIOSQLite.this.<T>transDataRow2ValSeg(dr,ts.param.valTp) ;
			vsCB.onFindValSeg(rowidx,vs);
			return true;
		}
		
	}
	
	public <T> void iterValSegsFrom(TSSTagSegs<T> ts, long from_dt,IValSegSelectCB<T> cb) throws Exception
	{
		String tag = ts.getTag();
		Integer idx = getTagsMap().get(tag);
		if (idx == null)
			return;
		String tablen = ts.param.getTableName(TN_TAG_SEG);
		String sql = "select * from " + tablen + " where TagIdx=? and StartDT<=? order by StartDT";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			conn = innerPool.getConnection();

			// ps =
			// conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

			ps = conn.prepareStatement(sql);
			ps.setInt(1, idx);
			ps.setLong(2, from_dt);
			
			rs = ps.executeQuery();
			// DBResult.transResultSetToDataTable(rs, tablen, 0, -1, cb);
			SelectCB<T> scb = new SelectCB<>(ts,cb) ;
			DBResult.transResultSetToDataTable(TN_TAG_MAP, 0, rs, 0, -1, scb);
		}
		finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch ( Exception e)
				{
				}
			}

			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch ( Exception e)
				{
				}
			}
			if (conn != null)
			{
				innerPool.free(conn);
			}
		}
	}
	
	public <T> int clearValSegsAll(TSSTagSegs<T> ts) throws Exception
	{
		String tag = ts.getTag();
		Integer idx = getTagsMap().get(tag);
		if (idx == null)
			return -1;
		String tablen = ts.param.getTableName(TN_TAG_SEG);
		String sql = "delete from " + tablen + " where TagIdx=?";

		Connection conn = null;

		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			conn = innerPool.getConnection();

			// ps =
			// conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

			ps = conn.prepareStatement(sql);
			ps.setInt(1, idx);
			
			return ps.executeUpdate() ;
		}
		finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch ( Exception e)
				{
				}
			}

			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch ( Exception e)
				{
				}
			}
			if (conn != null)
			{
				innerPool.free(conn);
			}
		}
	}
}
