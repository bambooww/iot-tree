package org.iottree.core.station;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.iottree.core.UAPrj;
import org.iottree.core.msgnet.IMNContainer;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.store.SourceJDBC;
import org.iottree.core.store.StoreManager;
import org.iottree.core.store.gdb.connpool.IConnPool;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class PlatRecvedDataGet_NM extends MNNodeMid
{
	//String dbName = null;

	//ArrayList<String> station_prjs = new ArrayList<>();

	public PlatRecvedDataGet_NM()
	{
	}

	@Override
	public String getTP()
	{
		return "recved_get";
	}

	@Override
	public String getTPTitle()
	{
		return g("recved_get");//"获取接收到的站点数据";
	}

	@Override
	public int getOutNum()
	{
		return 1;
	}

	@Override
	public String getColor()
	{
		return "#1d90ad";
	}

	@Override
	public String getIcon()
	{
		return "\\uf148";
	}

	@Override
	public JSONObject getParamJO()
	{
		return null ;
	}
	
	private UAPrj getPrj()
	{
		IMNContainer mnc = this.getBelongTo().getBelongTo().getBelongTo();
		if (mnc == null || !(mnc instanceof UAPrj))
			return null;

		return (UAPrj) mnc;
	}
	
//	private PStation getPrjStation()
//	{
//		UAPrj prj = getPrj() ;
//		if(prj==null)
//			return null ;
//		return prj.getPrjPStationInsDef() ;
//	}
	
	private SourceJDBC getSourceJDBC()
	{
		UAPrj prj = getPrj() ;
		if(prj==null)
			return null ;
		
		String saversor = prj.getPrjPStationSaverSor() ;
		if(Convert.isNullOrEmpty(saversor))
			return null ;
		return StoreManager.getSourceJDBC(saversor) ;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		UAPrj prj = getPrj() ;
		if(prj==null)
		{
			failedr.append("no prj found") ;
			return false;
		}
		PStation ps = prj.getPrjPStationInsDef() ;
		if(ps==null)
		{
			failedr.append("prj is not run as remote station instance") ;
			return false;
		}
		String saversor = prj.getPrjPStationSaverSor() ;
		if(Convert.isNullOrEmpty(saversor))
		{
			failedr.append("prj no saver data source set") ;
			return false ;
		}
		SourceJDBC sorj = StoreManager.getSourceJDBC(saversor) ;
		if(sorj==null)
		{
			failedr.append("prj no saver data source found with name="+saversor) ;
			return false;
		}
		return true;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		
	}
	
	@Override
	public boolean isFitForPrj(UAPrj prj)
	{
		if(prj==null)
			return false;
		return prj.isPrjPStationIns() ;
	}

	// rt

	private IConnPool RT_getConnPool()
	{
		SourceJDBC sorj = getSourceJDBC() ;
		if(sorj==null)
			return null ;
		return sorj.getConnPool() ;
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn conn, MNMsg msg) throws Exception
	{
		UAPrj prj = this.getPrj() ;
		if(prj==null)
			return null ;
		
		IConnPool cp = RT_getConnPool() ;
		if(cp==null)
			return null ;
		
		RTOut rto = RTOut.createOutIdx();
		String jstr = RT_retrieveAndDeleteJson(prj,cp);
		if (Convert.isNullOrEmpty(jstr))
		{
//			jstr = RT_retrieveAndDeleteJsonOld(prj,cp) ;
//			if(Convert.isNullOrEmpty(jstr))
			return null;
		}
		MNMsg m = new MNMsg();
		m.asPayload(jstr);
		m.asTopic(prj.getName());
		rto.asIdxMsg(0, m);
		return rto;
	}

	private String RT_retrieveAndDeleteJson(UAPrj prj,IConnPool cp) throws Exception
	{
		String prjname = prj.getName();

		String tablename = prjname + "_b";
		String selectSql = "SELECT keyid, json FROM " + tablename + " ORDER BY keyid desc LIMIT 1";
		String deleteSql = "DELETE FROM " + tablename + " WHERE keyid = ?";
		String json = null;

		Connection connection = null;
		try
		{
			connection = cp.getConnection();
			try (Statement selectStmt = connection.createStatement(); ResultSet rs = selectStmt.executeQuery(selectSql))
			{
				if (rs.next())
				{
					String keyid = rs.getString("keyid");
					byte[] bs = rs.getBytes("json");
					if(bs==null||bs.length<=0)
						return null ;
					json = PSCmdPrjRtData.unzip(bs) ;
					try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql))
					{
						deleteStmt.setString(1, keyid);
						deleteStmt.executeUpdate();
					}
				}
			}

			return json;
		}
		finally
		{
			cp.free(connection);
		}
	}
	
	private String RT_retrieveAndDeleteJsonOld(UAPrj prj,IConnPool cp) throws Exception
	{
		String prjname = prj.getName();

		String tablename = prjname;
		String selectSql = "SELECT keyid, json FROM " + tablename + " ORDER BY keyid LIMIT 1";
		String deleteSql = "DELETE FROM " + tablename + " WHERE keyid = ?";
		String json = null;

		Connection connection = null;
		try
		{
			connection = cp.getConnection();
			try (Statement selectStmt = connection.createStatement(); ResultSet rs = selectStmt.executeQuery(selectSql))
			{
				if (rs.next())
				{
					String keyid = rs.getString("keyid");
					json = rs.getString("json");
					if(Convert.isNullOrEmpty(json))
						return null ;
					try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql))
					{
						deleteStmt.setString(1, keyid);
						deleteStmt.executeUpdate();
					}
				}
			}

			return json;
		}
		finally
		{
			cp.free(connection);
		}
	}

//	@Override
//	public String RT_getOutTitle(int idx)
//	{
//		if (idx < 0 || idx >= this.station_prjs.size())
//			return null;
//		return this.station_prjs.get(idx);
//	}
}
