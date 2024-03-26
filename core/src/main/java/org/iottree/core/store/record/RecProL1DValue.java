package org.iottree.core.store.record;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.store.Source;
import org.iottree.core.store.SourceJDBC;
import org.iottree.core.store.StoreManager;
import org.iottree.core.store.gdb.DBResult;
import org.iottree.core.store.gdb.DBUtil;
import org.iottree.core.store.gdb.DataRow;
import org.iottree.core.store.gdb.DataTable;
import org.iottree.core.store.gdb.autofit.JavaColumnInfo;
import org.iottree.core.store.gdb.autofit.JavaForeignKeyInfo;
import org.iottree.core.store.gdb.autofit.JavaTableInfo;
import org.iottree.core.store.gdb.connpool.DBConnPool;
import org.iottree.core.store.gdb.xorm.XORMClass;
import org.iottree.core.store.gdb.xorm.XORMProperty;
import org.iottree.core.store.tssdb.TSSSavePK;
import org.iottree.core.store.tssdb.TSSTagParam;
import org.iottree.core.store.tssdb.TSSTagSegs;
import org.iottree.core.store.tssdb.TSSValPtEval;
import org.iottree.core.store.tssdb.TSSValSeg;
import org.iottree.core.store.tssdb.TSSValSegHit;
import org.iottree.core.ui.IUIProvider;
import org.iottree.core.ui.IUITemp;
import org.iottree.core.ui.UITemp;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ILang;
import org.iottree.core.util.Lan;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.XmlVal;
import org.json.JSONObject;

import kotlin.NotImplementedError;

/**
 * 记录数据处理，累计值间隔差值提取
 * 
 * @author jason.zhu
 *
 */
public class RecProL1DValue extends RecProL1  implements ILang
{
	private static ILogger log = LoggerManager.getLogger(RecProL1DValue.class) ;
	
	public static final String TP = "dvalue" ;
	
	public static final List<RecValStyle> FIT_VAL_STYLES =Arrays.asList(RecValStyle.successive_accumulation) ; 
			
	public static enum ByWay
	{
		second(10,"sec"),  // ,"By Second"
		minute(11,"mi"),  // ,"By Minute"
		hour(12,"h"), // ,"By Hour"
		day(13,"d");  //,"By Day"
		
		// 没有意义
//		week(14,"By Week","w"),
//		month(15,"By Month","m"),
//		year(16,"By Year","y");
		
		private final int val ;
		//private final String title;
		private final String mark ;
		
		ByWay(int v,String mark)
		{
			val = v ;
			//title = tt;
			this.mark = mark ;
		}
		
		public int getVal()
		{
			return val ;
		}
		
		public String getTitle()
		{
			Lan lan = Lan.getLangInPk(RecProL1DValue.class) ;
			return lan.g("by_way_"+mark) ;
			//return title ;
		}
		
		public String getMark()
		{
			return this.mark ;
		}
		
		public static ByWay valOfInt(int i)
		{
			switch(i)
			{
			
			case 10:
				return second;
			case 11:
				return minute;
			case 12:
				return hour;
			case 13:
				return day;
//			case 14:
//				return week;
//			case 15:
//				return month;
//			case 16:
//				return year;
			default:
				return null ;
			}
		}
		
		
		public long[] getStartEndAt(long atpt)
		{
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(atpt);
			long st,et;
				switch(val)
				{
				
				case 10: //second
					cal.set(Calendar.MILLISECOND, 0);
					st = cal.getTimeInMillis() ;
					cal.set(Calendar.MILLISECOND, 999);
					et = cal.getTimeInMillis() ;
					return new long[] {st,et};
				case 11: //min
					cal.set(Calendar.MILLISECOND, 0);
					cal.set(Calendar.SECOND,0) ;
					st = cal.getTimeInMillis() ;
					cal.set(Calendar.MILLISECOND, 999);
					cal.set(Calendar.SECOND, 59);
					et = cal.getTimeInMillis() ;
					return new long[] {st,et};
				case 12: //hour
					cal.set(Calendar.MILLISECOND, 0);
					cal.set(Calendar.SECOND,0) ;
					cal.set(Calendar.MINUTE,0) ;
					st = cal.getTimeInMillis() ;
					cal.set(Calendar.MILLISECOND, 999);
					cal.set(Calendar.SECOND, 59);
					cal.set(Calendar.MINUTE,59) ;
					et = cal.getTimeInMillis() ;
					return new long[] {st,et};
				case 13: //day
					cal.set(Calendar.MILLISECOND, 0);
					cal.set(Calendar.SECOND,0) ;
					cal.set(Calendar.MINUTE,0) ;
					cal.set(Calendar.HOUR_OF_DAY,0) ;
					st = cal.getTimeInMillis() ;
					cal.set(Calendar.MILLISECOND, 999);
					cal.set(Calendar.SECOND, 59);
					cal.set(Calendar.MINUTE,59) ;
					cal.set(Calendar.HOUR_OF_DAY,23) ;
					et = cal.getTimeInMillis() ;
					return new long[] {st,et};
				default:
					return null ;
				}
		}
		
		/**
		 * 计算当前时间，往前回溯一定的记录条数的时间
		 * @param recn
		 * @return
		 */
		public long getPrevDTByRecNum(long curdt,int recn)
		{
			long[] se = getStartEndAt(curdt) ;
			switch(val)
			{
			case 10: //second
				return se[0] - recn*1000 ;
			case 11: //min
				return se[0] - recn*60000 ;
			case 12: //hour
				return se[0] - recn*3600000 ;
			case 13: //day
				return se[0] - recn*86400000;
			default:
				throw new IllegalArgumentException("invalid tp") ;
			}
		}
		
		public long getGapMS()
		{
			switch(val)
			{
			case 10: //second
				return 1000 ;
			case 11: //min
				return 60000 ;
			case 12: //hour
				return 3600000 ;
			case 13: //day
				return 86400000;
			default:
				throw new IllegalArgumentException("invalid tp") ;
			}
		}
		
		private String getDTFmt()
		{
			switch(val)
			{
			case 10: //second
				return "yyyy-MM-dd HH:mm:ss" ;
			case 11: //min
				return "yyyy-MM-dd HH:mm" ;
			case 12: //hour
				return "yyyy-MM-dd HH" ;
			case 13: //day
				return "yyyy-MM-dd" ;
			default:
				throw new IllegalArgumentException("invalid tp") ;
			}
		}
		
		public SimpleDateFormat getDTFormat()
		{
			return new SimpleDateFormat(getDTFmt()) ;
		}
	}
	
	public static abstract class RowOb
	{
		@XORMProperty(name="TagIdx",has_col = true)
		public int tagIdx ;
		
		@XORMProperty(name="DT",has_col = true)
		public long dt ;
		/**
		 * 0-100
		 */
		@XORMProperty(name="Accuracy",has_col = true)
		public int accuracy ;
		
		public abstract Number getVal() ;
		
		
		public Long getValI()
		{
			throw new NotImplementedError() ;
		}
		
		public Double getValF()
		{
			throw new NotImplementedError() ;
		}
	}
	
	@XORMClass(table_name="tt",inherit_parent = true)
	public static class RowObI extends RowOb
	{
		@XORMProperty(name="Val",has_col = true)
		public Long val ;
		
		public Number getVal()
		{
			return val ;
		}
		
		public Long getValI()
		{
			return val ;
		}
		
	}
	
	@XORMClass(table_name="tt",inherit_parent = true)
	public static class RowObF extends RowOb
	{
		@XORMProperty(name="Val",has_col = true)
		public Double val ;
		
		public Number getVal()
		{
			return val ;
		}
		
		public Double getValF()
		{
			return val ;
		}
	}

	ByWay byWay = ByWay.day ;
	
	public ByWay getWay()
	{
		return byWay ;
	}

	@Override
	public String getTp()
	{
		return TP;
	}
	
	protected RecPro newInstance()
	{
		return new RecProL1DValue() ;
	}
	
	public String getTpTitle()
	{
		return g("dvalue_tt") +"-"+this.byWay.getTitle() ;
	}
	
	public String getTpDesc()
	{
		return g("dvalue_desc") ;
	}

	@Override
	public List<RecValStyle> getSupportedValStyle()
	{
		return FIT_VAL_STYLES;
	}
	
	
	@Override
	public JSONObject toJO()
	{
		JSONObject jo = super.toJO() ;
		jo.put("way",byWay.getVal()) ;
		return jo ;
	}
	
	@Override
	public boolean fromJO(JSONObject jo,StringBuilder failedr)
	{
		if(!super.fromJO(jo,failedr))
			return false ;
		
		this.byWay = ByWay.valOfInt(jo.optInt("way", -1)) ;
		if(this.byWay==null)
		{
			failedr.append("no accumWay pn=way") ;
			return false;
		}
		
		
		return true ;
	}

	@Override
	public List<RecShower> getSupportedShowers()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	// --------------------- rt init
	
	private JavaTableInfo jtiI = null ;
	private JavaTableInfo jtiF = null ;
	
	private SourceJDBC sorJDBC = null ;
	private DBConnPool connPool = null ;

	@Override
	protected boolean RT_initSaver(StringBuilder failedr)
	{
		//连接数据源，初始化表格，或读取必要的历史数据以支持继续运行
		
		Source sor = this.getSaverSource() ;
		if(sor==null)
		{
			failedr.append("no saver source found") ;
			return false;
		}
		if(!(sor instanceof SourceJDBC))
		{
			failedr.append("not jdbc source,it may be support later") ;
			return false;
		}

		sorJDBC =  (SourceJDBC)sor ;
		
		//sorJDBC = 
		if(!sorJDBC.checkConn(failedr))
			return false ;
		
		jtiI = getJTI(XmlVal.XmlValType.vt_int64) ;
		jtiF = getJTI(XmlVal.XmlValType.vt_double) ;
		
		connPool = sorJDBC.getConnPool() ;
		try
		{
			DBUtil.createOrUpTable(connPool, jtiI);
			DBUtil.createOrUpTable(connPool, jtiF);
			return true ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			return false;
		}
	}
	
	private HashMap<Integer,RowOb> tag2lastob = null ;

	@Override
	protected boolean RT_initPro(StringBuilder failedr)
	{
		try
		{
			HashMap<Integer,RowOb> t2o = new HashMap<>() ;
			List<RowOb> lastobs = readLastsGroupByTag() ;
			for(RowOb o:lastobs)
			{
				t2o.put(o.tagIdx, o) ;
			}
			this.tag2lastob = t2o ;
			return true;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			failedr.append(ee.getMessage()) ;
			return false;
		}
	}
	
	
	private JavaTableInfo getJTI(XmlVal.XmlValType col_valtp)// throws Exception
	{
		String tablen = null;
		switch(col_valtp)
		{
		case vt_int64:
			tablen = calTableName(byWay.getMark()+"_i") ;
			break ;
		case vt_double:
			tablen = calTableName(byWay.getMark()+"_f") ;
			break ;
		default:
			throw new IllegalArgumentException("unknown val type ,no table found") ;
		}
		
		ArrayList<JavaColumnInfo> norcols = new ArrayList<JavaColumnInfo>();
		JavaColumnInfo pkcol = null;
		ArrayList<JavaForeignKeyInfo> fks = new ArrayList<JavaForeignKeyInfo>();

		// pkcol = new JavaColumnInfo("StartDT",false,
		// XmlVal.XmlValType.vt_int64, -1,
		// false, false,"", false,-1, "",false,false);

		norcols.add(new JavaColumnInfo("TagIdx", true, XmlVal.XmlValType.vt_int32, 10, true, false, "TagIdx_idx", false,
				-1, "", false, false));

		// 记录时间段起始点
		norcols.add(new JavaColumnInfo("DT", false, XmlVal.XmlValType.vt_int64, -1, true, false, "DT_idx",
				false, -1, "", false, false));

		norcols.add(new JavaColumnInfo("Accuracy", false, XmlVal.XmlValType.vt_int16, -1, false, false, "", false, -1, "",
				false, false));

		norcols.add(new JavaColumnInfo("Val", false, col_valtp, -1, false, false, "", false, -1, "", false, false));

		return new JavaTableInfo(tablen, pkcol, norcols, fks);
	}

	// ------------------------------
	
	// rt run
	
	static final int CHECK_PREV_RNUM = 1000 ; 

	@Override
	protected boolean RT_onTagSegsSaved(TSSSavePK savepk)  throws Exception
	{
		if(tag2lastob==null)
			return false;
		
		TSSTagSegs<?> tseg = savepk.getTagSegs() ;
		boolean bfloat = tseg.getValTP().isNumberFloat() ;
		long dt = savepk.getLastSegEndDT();
		long[] sedt = this.byWay.getStartEndAt(dt) ;
		RowOb lastob = tag2lastob.get(tseg.getTagIdx()) ;
		long fromdt = savepk.getFromDT() ;
		//long todt = savepk.getToDT() ;
		long startdt = -1 ;
		if(lastob==null)
		{//first time to record,需要往前回溯
			long prev_dt = byWay.getPrevDTByRecNum(fromdt, CHECK_PREV_RNUM) ;
			TSSValSeg<?> vs = tseg.readValSegAtOrNext(prev_dt) ;
			if(vs==null)
			{
				startdt = savepk.getFromDT()  - byWay.getGapMS();
			}
			else
			{
				startdt = vs.getStartDT() - byWay.getGapMS() ;
			}
		}
		else
		{
			startdt = lastob.dt;// - byWay.getGapMS();
		}
		
		if(startdt>=savepk.getToDT())
			return false;
		
		long st = System.currentTimeMillis() ;
		if(processFromTo(savepk.getTagSegs(),startdt,savepk.getToDT()))
		{
			if(log.isDebugEnabled())
			{
				long et = System.currentTimeMillis() ;
				log.debug("dval process cost from "+startdt+" - "+savepk.getToDT()+" cost="+(et-st)) ;
			}
			
			RowOb rob = null;
			if(bfloat)
				rob = new RowObF() ;
			else
				rob = new RowObI() ;
			rob.dt = savepk.getToDT() ;
			tag2lastob.put(tseg.getTagIdx(), rob) ;
		}
		return true;
	}

	
	private boolean processFromTo(TSSTagSegs<?> tag,long fromdt,long enddt) throws Exception
	{
//		RecTagParam rtp = this.belongTo.getRecTagParam(tag) ;
//		if(rtp==null)
//			return false;
		//TSSAdapterPrj adp = this.belongTo.getTSSAdapterPrj() ;
		
		//String tagp = rtp.getTagPath() ;
		Connection conn = null ;
		boolean bauto = true;
		try
		{
			conn = connPool.getConnection() ;
			bauto = conn.getAutoCommit() ;
			
			//clearRecords(conn,tag) ;
			long curdt = byWay.getStartEndAt(fromdt)[0] ;
			TSSValPtEval<?> curv = null,nextv=null;
			do
			{
				curv = tag.readOrCalValAt(curdt) ;
				if(curv!=null)
					break ;
				
				curdt += byWay.getGapMS() ;
			}while(curdt<enddt) ;
			if(curv==null)
				return false;
			
			JavaTableInfo jti = null ;
			boolean bfloat = tag.getValTP().isNumberFloat(); 
			if(bfloat)
				jti = jtiF ;
			else
				jti = jtiI ;
			
			RowOb rob = this.readLastByTag(bfloat,jti.getTableName(), tag.getTagIdx()) ;
			long lastSavedStartDT = -1 ;
			if(rob!=null)
				lastSavedStartDT = rob.dt ;
			
			conn.setAutoCommit(false);
			
			do
			{
				long nextdt = curdt + byWay.getGapMS() ;
				nextv = tag.readOrCalValAt(nextdt) ;
				if(nextv==null)
					return true;//end

//				if(nextv.getValDouble()<curv.getValDouble())
//				{
//					System.out.println("XX ") ;
//				}
				boolean binsert = curdt>lastSavedStartDT ;
				
				if(log.isDebugEnabled())
				{
					log.debug(" curdt="+curdt+"  lastsaveddt="+lastSavedStartDT+"  insert="+binsert) ;
				}
				
				calDValueAndRecord(conn,binsert,jti,tag,curv,nextv);
				
				curv = nextv ;
				curdt = nextdt ;
			}while(curdt<enddt) ;
			
			conn.commit();
			return true;
		}
		finally
		{
			if(conn!=null)
			{
				conn.setAutoCommit(bauto);
				connPool.free(conn);
			}
		}
	}
	
	private void calDValueAndRecord(Connection conn,boolean binsert,JavaTableInfo jti,TSSTagSegs<?> tag,TSSValPtEval<?> curv,TSSValPtEval<?> nextv) throws Exception
	{
		if(tag.getValTP().isNumberFloat())
		{
			double cv = ((Number)curv.getVal()).doubleValue() ;
			double nv = ((Number)nextv.getVal()).doubleValue() ;
			double dv = nv-cv ;
			insertOrUpdata(conn,binsert,true,jti,tag.getTagIdx(),curv.getDT(),dv,(short)((curv.getEvalRatio()+nextv.getEvalRatio())/2)) ;
		}
		else
		{
			long cv = ((Number)curv.getVal()).longValue() ;
			long nv = ((Number)nextv.getVal()).longValue() ;
			long dv = nv-cv ;
			insertOrUpdata(conn,binsert,false,jti,tag.getTagIdx(),curv.getDT(),dv,(short)((curv.getEvalRatio()+nextv.getEvalRatio())/2)) ;
		}
	}
	
	//
	private int clearRecords(Connection conn,UATag tag) throws Exception
	{
		RecTagParam rtp = this.belongTo.getRecTagParam(tag) ;
		if(rtp==null)
			return -1;
		
		TSSAdapterPrj adp = this.belongTo.getTSSAdapterPrj() ;
		Integer tagidx = adp.getTagsMap().get(rtp.getTagPath()) ;
		if(tagidx==null)
			return -1 ;
		
		String tablen ;
		if(tag.getValTp().isNumberFloat())
			tablen = jtiF.getTableName() ;
		else
			tablen = jtiI.getTableName() ;
		
		String sql = "delete from " + tablen + " where TagIdx=?";

		
		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			conn = connPool.getConnection();
			
			// ps =
			// conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

			ps = conn.prepareStatement(sql);
			ps.setInt(1, tagidx);
			
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
		}
	}
	
	//----------- insert or update
	
	private <T> void insertOrUpdata(Connection conn,boolean binsert,boolean bfloat ,JavaTableInfo jti,Integer tagidx,long startdt,T val,short accuracy) throws Exception
	{
		//RowOb rob = readByStartDT(conn,bfloat,jti, tagidx, startdt) ;
		if(binsert)
		{// insert
			insertByStartDT(conn, bfloat ,jti,tagidx, startdt, val, accuracy) ;
		}
		else
		{
			updateByStartDT(conn, bfloat , jti, tagidx,startdt, val, accuracy);
		}
	}
	
	private <T> int insertByStartDT(Connection conn,boolean bfloat ,JavaTableInfo jti,Integer tagidx,long startdt,T val,short accuracy) throws Exception
	{
		String sql = "insert into "+jti.getTableName()+" (TagIdx,DT,Accuracy,Val) values (?,?,?,?)" ;

		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			ps = conn.prepareStatement(sql);
			ps.setInt(1, tagidx);
			ps.setLong(2, startdt);
			ps.setShort(3, accuracy);
			if(bfloat)
				ps.setDouble(4, (double)val);
			else
				ps.setLong(4, (long)val);

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
		}
	}
	
	private <T> int updateByStartDT(Connection conn,boolean bfloat ,JavaTableInfo jti,Integer tagidx,long startdt,T val,short accuracy) throws Exception
	{
		String sql = "update "+jti.getTableName()+" set  Accuracy=?,Val=?  where TagIdx=? and DT=?" ;

		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			ps = conn.prepareStatement(sql);
			ps.setShort(1, accuracy);
			if(bfloat)
				ps.setDouble(2, (double)val);
			else
				ps.setLong(2, (long)val);
			ps.setInt(3, tagidx);
			ps.setLong(4, startdt);
			
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
		}
	}
	
	// ------------ read data
	
	private RowOb readByStartDT(boolean bfloat,JavaTableInfo jti,Integer tagidx,long startdt) throws Exception
	{
		String sql = "select * from "+jti.getTableName()+" where TagIdx=? and DT=?" ;
		if(bfloat)
		{
			List<RowObF> robfs =  DBUtil.executeQuerySqlWithXORM(connPool,sql,RowObF.class) ;
			if(robfs==null||robfs.size()<=0)
				return null ;
			return robfs.get(0) ;
		}
		else
		{
			List<RowObI> robfs =  DBUtil.executeQuerySqlWithXORM(connPool,sql,RowObI.class) ;
			if(robfs==null||robfs.size()<=0)
				return null ;
			return robfs.get(0) ;
		}
	}
	
	public RowOb readLastByTag(boolean bfloat,String tablen,Integer tagidx) throws Exception
	{
		String sql = "select max(DT) as MAX_DT,* from "+tablen+" where TagIdx="+tagidx ;
		if(bfloat)
		{
			List<RowObF> robis =  DBUtil.executeQuerySqlWithXORM(connPool,sql,RowObF.class) ;
			if(robis==null||robis.size()<=0)
				return null ;
			return robis.get(0) ;
		}
		else
		{
			List<RowObI> robis =  DBUtil.executeQuerySqlWithXORM(connPool,sql,RowObI.class) ;
			if(robis==null||robis.size()<=0)
				return null ;
			return robis.get(0) ;
		}
	}
	
	public List<RowOb> readLastsGroupByTag() throws Exception
	{
		String sql = "select max(DT) as MAX_DT,* from "+jtiI.getTableName()+" group by TagIdx" ;
		String sql2 = "select max(DT) as MAX_DT,* from "+jtiF.getTableName()+" group by TagIdx" ;
		
		List<RowObI> robis =  DBUtil.executeQuerySqlWithXORM(connPool,sql,RowObI.class) ;
		List<RowObF> robfs =  DBUtil.executeQuerySqlWithXORM(connPool,sql2,RowObF.class) ;
		ArrayList<RowOb> rets =new ArrayList<>() ;
		rets.addAll(robis) ;
		rets.addAll(robfs) ;
		return rets ;
	}
	
	public List<RowOb> readRowsForPage(String tagpath, long to_dt,boolean b_desc,int limit_num) throws Exception
	{
		TSSTagSegs<?> ts = this.belongTo.getTSSTagSegs(tagpath) ;
		if(ts==null)
			throw new IllegalArgumentException("no TSSTagSegs found with prj tag path="+tagpath);
		ValTP vtp = ts.getValTP() ;
		Integer tagidx = ts.getTagIdx() ;
		JavaTableInfo jti = null ;
		boolean bfloat = vtp.isNumberFloat();
		if(bfloat)
		{
			jti = jtiF ;
		}
		else
		{
			jti = jtiI ;
		}
		
		Connection conn = null ;
		try
		{
			conn = connPool.getConnection() ;
			return readRowsForPage(conn,jti,bfloat,tagidx,  to_dt,b_desc,limit_num) ;
		}
		finally
		{
			if(conn!=null)
				connPool.free(conn);
		}
	}
	
	private List<RowOb> readRowsForPage(Connection conn,JavaTableInfo jti,boolean bfloat,Integer tagidx, long to_dt,boolean b_desc,int limit_num) throws Exception
	{
		String sql = "select * from "+jti.getTableName()+" where tagidx="+tagidx+" and DT<"+to_dt+" order by DT" ;
		if(b_desc)
			sql += " desc" ;
		if(limit_num>0)
			sql += " limit "+limit_num ;
		
		//ArrayList<RowOb> rets = new ArrayList<>() ;
		
		DataTable dt = DBUtil.executeQuerySql(conn, sql) ;
		ArrayList<RowOb> rets = new ArrayList<>() ;
		if(bfloat)
		{
			List<RowObF> obs = DBResult.transTable2XORMObjList(RowObF.class, dt) ;
			rets.addAll(obs) ;
		}
		else
		{
			List<RowObI> obs = DBResult.transTable2XORMObjList(RowObI.class, dt) ;
			rets.addAll(obs) ;
		}
		return rets ;
	}
	
	// UI
	
//	protected String UI_getTempTitle()
//	{
//		return this.getTitle()+" "+byWay.getTitle() ;
//	}
//
//	@Override
//	protected String UI_getTempIcon()
//	{
//		return "/_iottree/res/dvalue.png" ;
//	}
}
