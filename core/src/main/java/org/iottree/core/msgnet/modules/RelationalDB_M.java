package org.iottree.core.msgnet.modules;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.iottree.core.UAVal;
import org.iottree.core.msgnet.MNModule;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.store.Source;
import org.iottree.core.store.SourceJDBC;
import org.iottree.core.store.StoreManager;
import org.iottree.core.store.gdb.autofit.JavaColumnInfo;
import org.iottree.core.store.gdb.autofit.JavaForeignKeyInfo;
import org.iottree.core.store.gdb.autofit.JavaTableInfo;
import org.iottree.core.store.gdb.connpool.DBConnPool;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlVal;
import org.iottree.core.util.xmldata.XmlVal.XmlValType;
import org.json.JSONArray;
import org.json.JSONObject;

public class RelationalDB_M extends MNModule
{
	String sourceName = null;

	@Override
	public String getTP()
	{
		return "r_db";
	}

	@Override
	public String getTPTitle()
	{
		return g("r_db");
	}

	@Override
	public String getColor()
	{
		return "#e7b686";
	}

	@Override
	public String getIcon()
	{
		return "\\uf1c0";
	}

	@Override
	public String getPmTitle()
	{
		SourceJDBC sj = getSourceJDBC();
		if (sj == null)
			return "no source jdbc";
		return sj.getDBInf();
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if (Convert.isNullOrEmpty(this.sourceName))
		{
			failedr.append("no jdbc source name");
			return false;
		}
		SourceJDBC sorjdbc = getSourceJDBC();
		if (sorjdbc == null)
		{
			failedr.append("no jdbc source found with name=" + this.sourceName);
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		jo.putOpt("sor_name", this.sourceName);
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.sourceName = jo.optString("sor_name");
	}

	public String getSourceName()
	{
		return this.sourceName;
	}

	public SourceJDBC getSourceJDBC()
	{
		if (Convert.isNullOrEmpty(this.sourceName))
			return null;
		Source sor = StoreManager.getSourceByName(this.sourceName);
		if (sor == null || !(sor instanceof SourceJDBC))
			return null;
		return (SourceJDBC) sor;
	}

	public static class TableItem
	{
		public String tableName;
		public String schema;
		public String remarks;

		public JSONObject toJO()
		{
			return new JSONObject().put("table", tableName).putOpt("schema", this.schema).put("remarks", this.remarks);
		}
	}

	public List<TableItem> listAllTables()
	{

		SourceJDBC jdbc = getSourceJDBC();
		DBConnPool cp = jdbc.getConnPool();
		if (cp == null)
			return null;
		Connection conn = null;
		String dbname = jdbc.getDBName();
		try
		{
			conn = cp.getConnection();
			DatabaseMetaData metaData = conn.getMetaData();

			ArrayList<TableItem> rets = new ArrayList<>();
			try (ResultSet resultSet = metaData.getTables(dbname, "public", null, new String[] { "TABLE" }))
			{
				while (resultSet.next())
				{
					TableItem ti = new TableItem();
					ti.tableName = resultSet.getString("TABLE_NAME");
					if (Convert.isNullOrEmpty(ti.tableName))
						continue;
					ti.schema = resultSet.getString("TABLE_SCHEM");
					ti.remarks = resultSet.getString("REMARKS");
					rets.add(ti);
				}
			}
			return rets;
		}
		catch ( SQLException e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			if (conn != null)
				cp.free(conn);
		}
	}

	public static XmlVal.XmlValType[] VAL_TPS_ALL = new XmlVal.XmlValType[] {
			XmlValType.vt_byte, XmlValType.vt_int16, XmlValType.vt_int32, XmlValType.vt_int64,
			XmlValType.vt_bool,XmlValType.vt_string,
			XmlValType.vt_float, XmlValType.vt_double,XmlValType.vt_date };// ,UAVal.ValTP.vt_date};

	public static JSONArray toValTpsAllJArr()
	{
		JSONArray jarr = new JSONArray();
		for (XmlVal.XmlValType atp : VAL_TPS_ALL)
		{
			jarr.put(atp.getTypeStr());
		}
		return jarr;
	}

	public List<JavaColumnInfo> readColsFromDB(String table_name)
	{
		SourceJDBC jdbc = getSourceJDBC();
		DBConnPool cp = jdbc.getConnPool();
		if (cp == null)
			return null;
		Connection conn = null;

		try
		{
			conn = cp.getConnection();
			DatabaseMetaData metaData = conn.getMetaData();
			
			HashSet<String> pk_colns = new HashSet<>() ;
			try (ResultSet rs = metaData.getPrimaryKeys(null, null, table_name))
			{
                while (rs.next())
                {
                    String columnName = rs.getString("COLUMN_NAME");
                    int keySeq = rs.getInt("KEY_SEQ");      // unin pk seq（1,2,3...）
                    String pkName = rs.getString("PK_NAME"); // pk name
                    pk_colns.add(columnName) ;
                }
            }
			
			
			HashMap<String, Boolean> columnHasUniqueIndex = new HashMap<>();
			HashMap<String, Boolean> columnHasAnyIndex = new HashMap<>();
			try(ResultSet rs = metaData.getIndexInfo(null, null, table_name, false, true);)
			{
				while (rs.next()) {
				    short type = rs.getShort("TYPE");
				    if (type == DatabaseMetaData.tableIndexStatistic) {
				        continue;
				    }
				    String columnName = rs.getString("COLUMN_NAME");
	
				    boolean nonUnique = rs.getBoolean("NON_UNIQUE");
				    boolean isUnique = !nonUnique;
				    
				    columnHasAnyIndex.put(columnName, true);
				    if (isUnique)
				        columnHasUniqueIndex.put(columnName, true);
				}
			}

			try (ResultSet rs = metaData.getColumns(null, null, table_name, null))
			{
				ArrayList<JavaColumnInfo> cis = new ArrayList<>();
				while (rs.next())
				{
					String coln = rs.getString("COLUMN_NAME");
					if (Convert.isNullOrEmpty(coln))
						continue;
					int datatp = rs.getInt("DATA_TYPE");
					XmlVal.XmlValType xvt = JavaColumnInfo.SqlType2XmlValType(datatp);
					//String typeName = rs.getString("TYPE_NAME");
					int columnSize = rs.getInt("COLUMN_SIZE");
					//String isNullable = rs.getString("IS_NULLABLE");
					String default_strv = rs.getString("COLUMN_DEF");
					boolean b_autoinc = "YES".equals(rs.getString("IS_AUTOINCREMENT"));
					//String remarks = rs.getString("REMARKS");
					boolean hasidx = columnHasAnyIndex.containsKey(coln);
					boolean bunique = columnHasUniqueIndex.containsKey(coln);
					boolean bpk = pk_colns.contains(coln) ;

					JavaColumnInfo jci = new JavaColumnInfo(coln, bpk, xvt, columnSize, hasidx, bunique, null,
							b_autoinc, -1, default_strv, false, false);

					cis.add(jci);
				}
				//return new JavaTableInfo(table_name, null, cis, null);
				return cis;
			}
		}
		catch ( SQLException e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			if (conn != null)
				cp.free(conn);
		}
	}
}
