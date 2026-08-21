package org.iottree.core.store.gdb.autofit;

import java.io.*;
import java.util.*;

import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.*;
import org.json.JSONArray;


/**
 * @author Jason Zhu
 */
public class JavaTableInfo implements IXmlDataable
{
	private String tableName = null ;
	private JavaColumnInfo pkColInfo = null ;
	private List<JavaColumnInfo> norColInfos = null ;
	private List<JavaForeignKeyInfo> fkInfos = null ;
	
	public JavaTableInfo()
	{
		
	}
	
	public JavaTableInfo(String tablen,
			JavaColumnInfo pkcol,List<JavaColumnInfo> cols,
			List<JavaForeignKeyInfo> fkinfos)
	{
		tableName = tablen ;
		pkColInfo = pkcol ;
		norColInfos = cols ;
		fkInfos = fkinfos ;
	}
	
	public String getTableName()
	{
		return tableName ;
	}
	
	public void setTableName(String tn)
	{
		tableName = tn ;
	}
	
	public JavaColumnInfo getPkColumnInfo()
	{
		return pkColInfo ;
	}
	
	public JavaColumnInfo[] getNorColumnInfos()
	{
		if(norColInfos==null)
			return new JavaColumnInfo[0];
		
		JavaColumnInfo[] rets = new JavaColumnInfo[norColInfos.size()];
		norColInfos.toArray(rets);
		return rets ;
	}
	
	public List<String> getAllColNames()
	{
		ArrayList<String> rets = new ArrayList<>() ;
		rets.add(pkColInfo.getColumnName()) ;
		for(JavaColumnInfo col:norColInfos)
		{
			rets.add(col.getColumnName()) ;
		}
		return rets ;
	}
	
	private transient String[] all_cols = null ;
	
	public String[] getAllColNamesArr()
	{
		if(all_cols!=null)
			return all_cols ;
		
		String[] ss = new String[this.norColInfos.size()+1] ;
		ss[0] = this.pkColInfo.getColumnName() ;
		for(int i = 1 ; i < ss.length ; i ++)
		{
			ss[i] = this.norColInfos.get(i-1).getColumnName() ;
		}
		return all_cols = ss ;
	}
	
	private transient String[] nor_cols = null ;
	
	public String[] getNorColNames()
	{
		if(nor_cols!=null)
			return nor_cols ;
		
		String[] ss = new String[this.norColInfos.size()] ;
		for(int i = 0 ; i < ss.length ; i ++)
		{
			ss[i] = this.norColInfos.get(i).getColumnName() ;
		}
		return nor_cols = ss ;
	}
	
	public JavaColumnInfo getColumnInfoByName(String n)
	{
		if(pkColInfo.getColumnName().equalsIgnoreCase(n))
			return pkColInfo ;
		
		for(JavaColumnInfo jci:norColInfos)
		{
			if(jci.getColumnName().equalsIgnoreCase(n))
				return jci ;
		}
		
		return null ;
	}
	
	/**
	 * ���һ���е�ǰһ��
	 * @param colname
	 * @return
	 */
	public JavaColumnInfo getBeforeColumn(String colname)
	{
		if(pkColInfo.getColumnName().equalsIgnoreCase(colname))
			return null ;
		int s = 0 ;
		if(norColInfos!=null)
			s = norColInfos.size() ;
		if(s<=0)
			return pkColInfo ;
		for(int k = 0 ; k < s ; k ++)
		{
			if(norColInfos.get(k).getColumnName().equalsIgnoreCase(colname))
			{
				if(k==0)
					return pkColInfo ;
				return norColInfos.get(k-1) ;
			}
		}
		return norColInfos.get(s-1) ;
	}
	
	JavaColumnInfo[] updateNorCols = null ;
	
	public JavaColumnInfo[] getUpdateNorColumnInfos()
	{
		if(updateNorCols!=null)
			return updateNorCols ;
		
		if(norColInfos==null)
		{
			updateNorCols = new JavaColumnInfo[0];
			return updateNorCols ;
		}
		
		ArrayList<JavaColumnInfo> rets = new ArrayList<JavaColumnInfo>() ;
		for(JavaColumnInfo jci:norColInfos)
		{
			if(jci.isUpdateAsSingle())
				continue ;
			
			rets.add(jci) ;
		}
		
		JavaColumnInfo[] ss = new JavaColumnInfo[rets.size()];
		rets.toArray(ss);
		updateNorCols = ss ;
		return updateNorCols ;
	}
	
	public JavaColumnInfo getNorColumnInfo(String n)
	{
		if(norColInfos==null)
			return null ;
		
		for(JavaColumnInfo jci:norColInfos)
		{
			if(jci.getColumnName().equalsIgnoreCase(n))
				return jci ;
		}
		return null ;
	}
	
	public JavaForeignKeyInfo[] getForeignKeyInfos()
	{
		if(fkInfos==null)
			return new JavaForeignKeyInfo[0];
		
		JavaForeignKeyInfo[] rets = new JavaForeignKeyInfo[fkInfos.size()];
		fkInfos.toArray(rets);
		return rets ;
	}

	public XmlData toXmlData()
	{
		XmlData xd = new XmlData();
		xd.setParamValue("table_name", tableName);
		if(pkColInfo!=null)
		{
			xd.setSubDataSingle("pk_col", pkColInfo.toXmlData());
		}
		
		if(norColInfos!=null)
		{
			List<XmlData> xds = xd.getOrCreateSubDataArray("nor_cols");
			for(JavaColumnInfo jci:norColInfos)
			{
				xds.add(jci.toXmlData());
			}
		}
		
		if(fkInfos!=null)
		{
			List<XmlData> xds = xd.getOrCreateSubDataArray("fks");
			for(JavaForeignKeyInfo fki:fkInfos)
			{
				xds.add(fki.toXmlData());
			}
		}
		return xd;
	}

	public void fromXmlData(XmlData xd)
	{
		tableName = xd.getParamValueStr("table_name");
		XmlData tmpxd = xd.getSubDataSingle("pk_col") ;
		
		if(tmpxd!=null)
		{
			pkColInfo = new JavaColumnInfo();
			pkColInfo.fromXmlData(tmpxd);
		}
		
		List<XmlData> tmpxds = xd.getSubDataArray("nor_cols");
		if(tmpxds!=null)
		{
			norColInfos = new ArrayList<JavaColumnInfo>(tmpxds.size());
			for(XmlData xd0:tmpxds)
			{
				JavaColumnInfo tmpjci = new JavaColumnInfo();
				tmpjci.fromXmlData(xd0);
				norColInfos.add(tmpjci);
			}
		}
		
		tmpxds = xd.getSubDataArray("fks");
		if(tmpxds!=null)
		{
			fkInfos = new ArrayList<JavaForeignKeyInfo>(tmpxds.size());
			for(XmlData xd0:tmpxds)
			{
				JavaForeignKeyInfo tmpfki = new JavaForeignKeyInfo();
				tmpfki.fromXmlData(xd0);
				fkInfos.add(tmpfki);
			}
		}
	}
	
	// - sql helper
	
	public String SQL_calcInsertSql()
	{
		StringBuilder q_mark_str = new StringBuilder() ;
		String pk_nor_colstr = SQL_calcPkNor(q_mark_str) ;
		StringBuilder sb = new StringBuilder() ;
		sb.append("insert into ").append(this.tableName)
			.append(" (").append(pk_nor_colstr).append(" ) values (").append(q_mark_str).append(")");
		return sb.toString() ;
	}
	
	public String[] SQL_calcUpdateByPkIdSql(String[] cols,StringBuilder sql_sb)
	{
		if(this.pkColInfo==null)
			throw new RuntimeException("no pk col") ;
		
		if(cols==null||cols.length<=0)
		{
			cols = new String[this.norColInfos.size()];
			for(int i = 0 ; i < cols.length ; i ++)
				cols[i] = this.norColInfos.get(i).getColumnName() ;
		}

		sql_sb.append("update ").append(this.tableName).append(" set ").append(cols[0]).append("=?");
		for(int i = 1 ; i < cols.length ;i ++)
		{
			sql_sb.append(",").append(cols[i]).append("=?");
		}
		sql_sb.append(" where ").append(this.pkColInfo.getColumnName()).append("=?");
		return cols ;
	}
	
	public String SQL_calcDeleteByPkIdSql()
	{
		if(this.pkColInfo==null)
			throw new RuntimeException("no pk col") ;
		return SQL_calcDelSqlByColOpers(new String[] {this.pkColInfo.getColumnName()},new String[] {"="},
				null,null,null) ;
	}
	
	public String SQL_calcDelSqlByColOpers(String[] cols,String[] opers,
			Object[] vals,boolean[] null_ignores,
			String more_wherestr)
	{
		StringBuilder sb = new StringBuilder() ;
		sb.append("delete from ").append(this.tableName);
		sb.append(SQL_calcWhereSqlByColOpers(cols,opers,vals,null_ignores,more_wherestr,null));
		return sb.toString();
	}
	
	public String SQL_calcSqlByColOpers(String[] cols,String[] opers,
			Object[] vals,boolean[] null_ignores,//判断每个列值中的vals空值是否需要忽略
			String more_wherestr,
			String orderby)
	{
		StringBuilder sb = new StringBuilder() ;
		sb.append("select ").append(SQL_calcPkNor(null)).append(" from ").append(this.tableName);
		sb.append(SQL_calcWhereSqlByColOpers(cols,opers,vals,null_ignores,more_wherestr,orderby));
		return sb.toString();
	}
	
	public String SQL_calcSqlCountByColOpers(String[] cols,String[] opers,
			Object[] vals,boolean[] null_ignores,//判断每个列值中的vals空值是否需要忽略
			String more_wherestr,
			String orderby) throws ClassNotFoundException
	{
		StringBuilder sb = new StringBuilder() ;
		sb.append("select count(*) from ").append(this.tableName);
		sb.append(SQL_calcWhereSqlByColOpers(cols,opers,vals,null_ignores,more_wherestr,orderby));
		return sb.toString();
	}
	
	
	private String SQL_calcPkNor(StringBuilder insert_q_mark)
	{
		StringBuilder sb = new StringBuilder() ;
		
		boolean bfirst = true;
		if(pkColInfo!=null)
		{
			sb.append(pkColInfo.getColumnName()) ;
			bfirst = false;
			
			if(insert_q_mark!=null)
				insert_q_mark.append("?") ;
		}
		for(JavaColumnInfo jci:this.norColInfos)
		{
			if(bfirst)
				bfirst = false;
			else
			{
				sb.append(",");
				if(insert_q_mark!=null)
					insert_q_mark.append(",") ;
			}
			
			sb.append(jci.getColumnName()) ;
			
			if(insert_q_mark!=null)
				insert_q_mark.append("?") ;
		}
		return sb.toString() ;
	}
	
	private String SQL_calcWhereSqlByColOpers(String[] cols,String[] opers,
			Object[] vals,boolean[] ignore_nulls,String more_wherestr,
			String orderby)
	{
		StringBuilder sb = new StringBuilder() ;
		if(vals==null)
		{
			if(cols.length>0)
			{
				sb.append(" where ");
				sb.append(cols[0]).append(opers[0]).append("?") ;
				for(int k = 1 ; k < cols.length ; k ++)
					sb.append(" and ").append(cols[k]).append(opers[k]).append("?") ;
			}
		}
		else
		{//
			StringBuffer tmpsb = new StringBuffer() ;
			for(int k = 0 ; k < cols.length ; k ++)
			{
				if(vals[k]==null && 
						ignore_nulls!=null && ignore_nulls[k])
					continue ;//ignore null
				if(tmpsb.length()==0)
					tmpsb.append(" where ").append(cols[k]).append(opers[k]).append("?") ;
				else
					tmpsb.append(" and ").append(cols[k]).append(opers[k]).append("?") ;
			}
			sb.append(tmpsb) ;
		}
		
		if(Convert.isNotNullEmpty(more_wherestr))
			sb.append(" and ").append(more_wherestr) ;
		
		if(!Convert.isNullOrTrimEmpty(orderby))
		{
			sb.append(" order by ").append(orderby);
//			if(b_order_desc)
//				sb.append(" desc");
		}
		return sb.toString();
	}
}
