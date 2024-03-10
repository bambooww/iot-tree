package org.iottree.core.store.record;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.iottree.core.store.Source;
import org.iottree.core.store.SourceJDBC;
import org.iottree.core.store.gdb.DBUtil;
import org.iottree.core.store.gdb.autofit.JavaColumnInfo;
import org.iottree.core.store.gdb.autofit.JavaForeignKeyInfo;
import org.iottree.core.store.gdb.autofit.JavaTableInfo;
import org.iottree.core.store.gdb.connpool.DBConnPool;
import org.iottree.core.store.gdb.xorm.XORMProperty;
import org.iottree.core.store.tssdb.TSSSavePK;
import org.iottree.core.store.tssdb.TSSTagSegs;
import org.iottree.core.util.ILang;
import org.iottree.core.util.Lan;
import org.iottree.core.util.xmldata.XmlVal;
import org.json.JSONObject;

/**
 * Mutation Points recorder 曲线突变记录器
 * 
 * @author jason.zhu
 *
 */
public class RecProL1MutPts extends RecProL1  implements ILang
{
	public static final String TP = "mutpts" ;
	
	public static final List<RecValStyle> FIT_VAL_STYLES =Arrays.asList(RecValStyle.successive_normal,RecValStyle.successive_accumulation) ;
	
	private static Lan lan = Lan.getLangInPk(RecProL1MutPts.class);
	
	/**
	 * TODO 可能还需要拆分，支持更复杂的应用
	 * @author jason.zhu
	 *
	 */
	public static enum MutTP
	{
		up_only(0,"Up Only"),  //
		up_to_vertex(1,"Up To Vertex"),
		up_to_vertex_sustain(2,"Up To Vertex And Sustain"),
		
		down_only(10,"Down Only"),
		down_to_vertex(11,"Down To Vertex"),
		down_to_vertex_sustain(12,"Down To Vertex And Sustain"),
		
		up_down(20,"Up And Down"),
		down_up(21,"Down And Up");
		
		
		private final int val ;
		private final String title;
		
		MutTP(int v,String tt)
		{
			val = v ;
			title = tt;
			//this.mark = mark ;
		}
		
		public int getVal()
		{
			return val ;
		}
		
		public String getTitle()
		{
			return title ;
		}
		
		public static MutTP valOfInt(int i)
		{
			switch(i)
			{
			
			case 0:
				return up_only;
			case 1:
				return up_to_vertex;
			case 2:
				return up_to_vertex_sustain;
			case 10:
				return down_only;
			case 11:
				return down_to_vertex;
			case 12:
				return down_to_vertex_sustain;
			case 20:
				return up_down;
			case 21:
				return down_up;
			default:
				return null ;
			}
		}
	}
	
	/**
	 * 参考斜率方式
	 * @author jason.zhu
	 *
	 */
	public static enum SlopeTP
	{
		fixed(0),
		multi_mean(1);  //Mean 均值倍数 
		
		private final int val ;
		//private final String title;
		
		SlopeTP(int v)
		{
			val = v ;
		}
		
		public int getVal()
		{
			return val ;
		}
		
		public String getTitle()
		{
			return lan.g("slope_tp_"+this.name()) ;
		}
		
		public static SlopeTP valOfInt(int i)
		{
			switch(i)
			{
			
			case 0:
				return fixed;
			case 1:
				return multi_mean;
			
			default:
				return null ;
			}
		}
	}
	
	public static class RowOb<T>
	{
		@XORMProperty(name="TagIdx")
		public int tagIdx ;
		
		@XORMProperty(name="StartDT")
		public long startDt ;
		
		@XORMProperty(name="EndDT")
		public long endDt ;

		@XORMProperty(name="StartVal")
		public T startVal ;
		
		@XORMProperty(name="MaxVal")
		public T maxVal ;
		
		@XORMProperty(name="MinVal")
		public T minVal ;
		
		@XORMProperty(name="MeanVal")
		public T meanVal ;
	}
	
	public static class RowObI extends RowOb<Long>
	{}
	
	public static class RowObF extends RowOb<Double>
	{}

	MutTP mutTp = MutTP.up_only ;
	SlopeTP slopeTp = SlopeTP.fixed ;
	
	public MutTP getMutTP()
	{
		return mutTp ;
	}
	
	public SlopeTP getSlopeTP()
	{
		return slopeTp ;
	}

	@Override
	public String getTp()
	{
		return TP;
	}
	
	protected RecPro newInstance()
	{
		return new RecProL1MutPts() ;
	}
	
	public String getTpTitle()
	{
		return g(TP+"_tt") ;
	}
	
	public String getTpDesc()
	{
		return g(TP+"_desc") ;
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
		jo.put("muttp",mutTp.getVal()) ;
		jo.put("slopetp",slopeTp.getVal()) ;
		return jo ;
	}
	
	@Override
	public boolean fromJO(JSONObject jo,StringBuilder failedr)
	{
		if(!super.fromJO(jo,failedr))
			return false ;
		
		this.mutTp = MutTP.valOfInt(jo.optInt("muttp", -1)) ;
		if(this.mutTp==null)
		{
			failedr.append("no mutTp pn=muttp") ;
			return false;
		}
		
		this.slopeTp = SlopeTP.valOfInt(jo.optInt("slopetp", -1)) ;
		if(this.slopeTp==null)
		{
			failedr.append("no slopeTp pn=slopetp") ;
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
		
		sorJDBC = (SourceJDBC)sor ;
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

	@Override
	protected boolean RT_initPro(StringBuilder failedr)
	{
		
		return false;
	}
	
	
	private JavaTableInfo getJTI(XmlVal.XmlValType col_valtp)// throws Exception
	{
		String tablen = null;
		switch(col_valtp)
		{
		case vt_int64:
			tablen = calTableName("i") ;
			break ;
		case vt_double:
			tablen = calTableName("f") ;
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
		norcols.add(new JavaColumnInfo("StartDT", false, XmlVal.XmlValType.vt_int64, -1, true, false, "StartDT_idx",
				false, -1, "", false, false));
		
		norcols.add(new JavaColumnInfo("EndDT", false, XmlVal.XmlValType.vt_int64, -1, true, false, "EndDT_idx",
				false, -1, "", false, false));
		
		norcols.add(new JavaColumnInfo("StartVal", false, col_valtp, -1, false, false, "", false, -1, "", false, false));

		norcols.add(new JavaColumnInfo("MaxVal", false, col_valtp, -1, false, false, "", false, -1, "", false, false));
		
		norcols.add(new JavaColumnInfo("MinVal", false, col_valtp, -1, false, false, "", false, -1, "", false, false));
		
		norcols.add(new JavaColumnInfo("MeanVal", false, col_valtp, -1, false, false, "", false, -1, "", false, false));
		
		

		return new JavaTableInfo(tablen, pkcol, norcols, fks);
	}

	// ------------------------------
	
	// rt run
	

	@Override
	protected boolean RT_onTagSegsSaved(TSSSavePK savepk)  throws Exception
	{
		return false;
	}

	
	
	// ------------ read data
	
	public List<RowOb<?>> readLastsGroupByTag() throws Exception
	{
		String sql = "select max(DT) as MAX_DT,* from "+jtiI.getTableName()+" group by TagIdx" ;
		String sql2 = "select max(DT) as MAX_DT,* from "+jtiF.getTableName()+" group by TagIdx" ;
		
		List<RowObI> robis =  DBUtil.executeQuerySqlWithXORM(connPool,sql,RowObI.class) ;
		List<RowObF> robfs =  DBUtil.executeQuerySqlWithXORM(connPool,sql2,RowObF.class) ;
		ArrayList<RowOb<?>> rets =new ArrayList<>() ;
		rets.addAll(robis) ;
		rets.addAll(robfs) ;
		return rets ;
	}
}
