package org.iottree.core.station;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.store.gdb.connpool.IConnPool;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public class PGetRecvedRTData_NM extends MNNodeMid
{
	String dbName = null ;
	
	ArrayList<String> station_prjs  = new ArrayList<>() ;
	
	public PGetRecvedRTData_NM()
	{
	}
	

	@Override
	public String getTP()
	{
		return "g_recved_rtd";
	}

	@Override
	public String getTPTitle()
	{
		return "获取接收到的站点数据";
	}


	@Override
	public int getOutNum()
	{
		return station_prjs.size();
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
		JSONObject jo= new JSONObject() ;
		jo.put("station_prjs", station_prjs) ;
		jo.putOpt("db_name", dbName) ;
		return jo;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(station_prjs==null||station_prjs.size()<=0)
		{
			failedr.append("no station.prj_name set") ;
			return false;
		}
		return station_prjs!=null&&station_prjs.size()>0;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		JSONArray jarr = jo.optJSONArray("station_prjs") ;
		if(jarr!=null)
		{
			ArrayList<String> ss = new ArrayList<>() ;
			for(int i = 0 ; i < jarr.length() ; i ++)
			{
				String prjn = jarr.getString(i) ;
				ss.add(prjn) ;
			}
			this.station_prjs = ss ;
		}
		this.dbName = jo.optString("db_name") ;
	}

	
	// rt
	
	private IConnPool RT_getConnPool()
	{
		return null ;//ConnPoolMgr.getConnPool(this.dbName) ;
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn conn, MNMsg msg) throws Exception
	{
		if(this.station_prjs==null||station_prjs.size()<=0)
			return null ;
		int n = station_prjs.size() ;
		RTOut rto = RTOut.createOutIdx() ;
		for(int i = 0 ; i < n ; i ++)
		{
			String stationprj = station_prjs.get(i) ;
			int k = stationprj.indexOf('.') ;
			String stationid = stationprj.substring(0,k);
			String prjname = stationprj.substring(k+1) ;
			String jstr = RT_retrieveAndDeleteJson(prjname) ;
			if(Convert.isNullOrEmpty(jstr))
				continue ;
			MNMsg m = new MNMsg() ;
			m.asPayload(jstr) ;
			m.asTopic(stationprj) ;
			rto.asIdxMsg(i, m) ;
		}
		return rto;
	}

	private String RT_retrieveAndDeleteJson(String prjname) throws Exception
	{
		String tablename =  prjname+"_b";
		String selectSql = "SELECT keyid, json FROM "+tablename+" ORDER BY keyid LIMIT 1";
		String deleteSql = "DELETE FROM "+tablename+" WHERE keyid = ?";
		String json = null;

		IConnPool cp = RT_getConnPool() ;
		Connection connection = null;
		try
		{
			connection = cp.getConnection() ;
			try (Statement selectStmt = connection.createStatement(); ResultSet rs = selectStmt.executeQuery(selectSql))
			{
				if (rs.next())
				{
					String keyid = rs.getString("keyid");
					json = rs.getString("json");
	
					try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql))
					{
						deleteStmt.setString(1, keyid);
						deleteStmt.executeUpdate();
					}
				}
			}
			
			return json ;
		}
		finally
		{
			cp.free(connection);
		}
	}
	
	@Override
	public String RT_getOutTitle(int idx)
	{
		if(idx<0||idx>=this.station_prjs.size())
			return null ;
		return this.station_prjs.get(idx) ;
	}
}
