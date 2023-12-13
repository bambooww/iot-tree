package org.iottree.core.store.gdb.autofit;

import org.iottree.core.store.gdb.connpool.DBType;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlVal;
import org.iottree.core.util.xmldata.XmlVal.XmlValType;



public class DBSqlSqlServer extends DbSql
{

	public DBType getDBType()
	{
		return DBType.sqlserver;
	}

	public String getSqlType(XmlValType vt, int maxlen)
	{
		if(vt==XmlValType.vt_int32)
		{
			return "int";
		}
		else if(vt==XmlValType.vt_byte_array)
		{//blob
			return "image";
		}
		else if(vt==XmlValType.vt_date)
		{
			return "datetime";
		}
		else if(vt==XmlValType.vt_double)
		{
			return "real";
		}
		else if(vt==XmlValType.vt_float)
		{
			return "float";
		}
		else if(vt==XmlValType.vt_int64)
		{
			return "bigint";
		}
		else if(vt==XmlValType.vt_int16)
		{
			return "SMALLINT";
		}
		else if(vt==XmlValType.vt_byte)
		{
			return "TINYINT";
		}
		else if(vt==XmlValType.vt_string)
		{
			if(maxlen<=0)
				throw new IllegalArgumentException("max len must >0");
			
			if(maxlen==Integer.MAX_VALUE)
			{//clob
				return "text";
			}
			
			return "varchar("+maxlen*2+")";
		}
		else if(vt==XmlValType.vt_bool)
		{
			return "BIT";
		}
		else// if(vt==XmlValType.vt_xml_schema)
		{
			throw new IllegalArgumentException("unsupported val type="+vt);
		}
	}

	@Override
	protected StringBuffer[] constructCreationTable(JavaTableInfo jti)
	{
		StringBuffer tmpsb = new StringBuffer();
		tmpsb.append("create table ").append(jti.getTableName())
			.append("(");
		
		JavaColumnInfo pkcol = jti.getPkColumnInfo() ;
		if(pkcol!=null)
		{
			if(pkcol.isAutoVal()&& pkcol.getValType()!=XmlVal.XmlValType.vt_string)
			{
				if(pkcol.getAutoValStart()<=0)
				{
					tmpsb.append(pkcol.getColumnName()).append(" ")
						.append(getSqlType(pkcol.getValType(),pkcol.getMaxLen()))
						.append(" IDENTITY (1, 1) primary key");
				}
				else
				{
					tmpsb.append(pkcol.getColumnName()).append(" ")
						.append(getSqlType(pkcol.getValType(),pkcol.getMaxLen()))
						.append(" IDENTITY (").append(pkcol.getAutoValStart()).append(", 1) primary key");
				}
			}
			else
			{
				tmpsb.append(pkcol.getColumnName()).append(" ")
				.append(getSqlType(pkcol.getValType(),pkcol.getMaxLen()))
				.append(" primary key");
			}
		}
		
		for(JavaColumnInfo tmpjci:jti.getNorColumnInfos())
		{
			if(tmpsb.charAt(tmpsb.length()-1)!=',')
				tmpsb.append(',');
			
			tmpsb.append(tmpjci.getColumnName()).append(" ")
				.append(getSqlType(tmpjci.getValType(),tmpjci.getMaxLen()));
			
			String defvstr = tmpjci.getDefaultValStr() ;
			if(defvstr!=null)
			{
				XmlVal.XmlValType vt = tmpjci.getValType();
				if(vt==XmlVal.XmlValType.vt_string)
					tmpsb.append(" default '").append(defvstr).append("'");
				else if(!defvstr.equals(""))
					tmpsb.append(" default ").append(defvstr);
			}
		}
		
		tmpsb.append(")");
		return new StringBuffer[]{tmpsb} ;
	}
	
	@Override
	protected StringBuffer[] constructCreationTableDistributedMode1(JavaTableInfo jti)
	{
		StringBuffer tmpsb = new StringBuffer();
		tmpsb.append("create table ").append(jti.getTableName())
			.append("(");
		
		tmpsb.append("_ProxyId integer,");
		
		JavaColumnInfo pkcol = jti.getPkColumnInfo() ;
		if(pkcol!=null)
		{
			if(pkcol.isAutoVal())
			{
				if(pkcol.getAutoValStart()<=0)
				{
					tmpsb.append(pkcol.getColumnName()).append(" ")
						.append(getSqlType(pkcol.getValType(),pkcol.getMaxLen()));
				}
				else
				{
					tmpsb.append(pkcol.getColumnName()).append(" ")
						.append(getSqlType(pkcol.getValType(),pkcol.getMaxLen()));
				}
			}
			else
			{
				tmpsb.append(pkcol.getColumnName()).append(" ")
				.append(getSqlType(pkcol.getValType(),pkcol.getMaxLen()));
			}
		}
		
		for(JavaColumnInfo tmpjci:jti.getNorColumnInfos())
		{
			if(tmpsb.charAt(tmpsb.length()-1)!=',')
				tmpsb.append(',');
			
			tmpsb.append(tmpjci.getColumnName()).append(" ")
				.append(getSqlType(tmpjci.getValType(),tmpjci.getMaxLen()));
			
			String defvstr = tmpjci.getDefaultValStr() ;
			if(defvstr!=null)
			{
				XmlVal.XmlValType vt = tmpjci.getValType();
				if(vt==XmlVal.XmlValType.vt_string)
					tmpsb.append(" default '").append(defvstr).append("'");
				else if(!defvstr.equals(""))
					tmpsb.append(" default ").append(defvstr);
			}
		}
		
		tmpsb.append(")");
		return new StringBuffer[]{tmpsb} ;
	}
	
	
	@Override
	protected StringBuffer[] constructCreationTableDistributedMode2(JavaTableInfo jti)
	{
		StringBuffer tmpsb = new StringBuffer();
		tmpsb.append("create table ").append(jti.getTableName())
			.append("(");
		
		JavaColumnInfo pkcol = jti.getPkColumnInfo() ;
		if(pkcol!=null)
		{
			if(pkcol.isAutoVal())
			{
				if(pkcol.getAutoValStart()<=0)
				{
					tmpsb.append(pkcol.getColumnName()).append(" ")
						.append(getSqlType(pkcol.getValType(),pkcol.getMaxLen()))
						.append(" IDENTITY (1, 1) primary key");
				}
				else
				{
					tmpsb.append(pkcol.getColumnName()).append(" ")
						.append(getSqlType(pkcol.getValType(),pkcol.getMaxLen()))
						.append(" IDENTITY (").append(pkcol.getAutoValStart()).append(", 1) primary key");
				}
			}
			else
			{
				tmpsb.append(pkcol.getColumnName()).append(" ")
				.append(getSqlType(pkcol.getValType(),pkcol.getMaxLen()))
				.append(" primary key");
			}
		}
		
		for(JavaColumnInfo tmpjci:jti.getNorColumnInfos())
		{
			if(tmpsb.charAt(tmpsb.length()-1)!=',')
				tmpsb.append(',');
			
			tmpsb.append(tmpjci.getColumnName()).append(" ")
				.append(getSqlType(tmpjci.getValType(),tmpjci.getMaxLen()));
			
			String defvstr = tmpjci.getDefaultValStr() ;
			if(defvstr!=null)
			{
				XmlVal.XmlValType vt = tmpjci.getValType();
				if(vt==XmlVal.XmlValType.vt_string)
					tmpsb.append(" default '").append(defvstr).append("'");
				else if(!defvstr.equals(""))
					tmpsb.append(" default ").append(defvstr);
			}
		}
		
//		����ʱ�����
		tmpsb.append(",_ServerUpdateDT bigint") ;
		
		tmpsb.append(")");
		return new StringBuffer[]{tmpsb} ;
	}
	
	public StringBuffer constructAddColumnToTable(JavaTableInfo jti,JavaColumnInfo jci,String after_colname)
	{
		StringBuffer tmpsb = new StringBuffer();
		tmpsb.append("ALTER TABLE ")
			.append(jti.getTableName())
			.append(" ADD ").append(jci.getColumnName()).append(" ")
			.append(getSqlType(jci.getValType(),jci.getMaxLen()));
		String defvstr = jci.getDefaultValStr();
		if(defvstr!=null)
		{
			if(jci.getValType()==XmlValType.vt_string)
			{
				tmpsb.append(" DEFAULT \'").append(defvstr).append("\'") ;
			}
			else
			{
				tmpsb.append(" DEFAULT ").append(defvstr).append("") ;
			}
		}
		if(Convert.isNotNullEmpty(after_colname))
		{
			tmpsb.append(" AFTER ").append(after_colname);
		}
		return tmpsb;
	}
	
	/**
	 * sqlserver ɾ���������﷨
	 *   IF EXISTS (SELECT name FROM sysindexes
         WHERE name = 'au_id_ind')
         DROP INDEX authors.au_id_ind

	 */
	public StringBuffer constructDropIndex(JavaTableInfo jti,JavaColumnInfo jci)
	{
		String idxn = calIndexName(jti,jci) ;
		StringBuffer tmpsb = new StringBuffer();
		tmpsb.append("IF EXISTS (SELECT name FROM sysindexes WHERE name = '")
			.append(idxn)
			.append("')");
		
		tmpsb.append("\r\nDROP INDEX ")
			.append(jti.getTableName()).append('.').append(idxn);
		return tmpsb ;
	}
	
	

	   
	public StringBuffer[] getInsertSqlWithNewIdReturn(JavaTableInfo jti,String tablename)
	{
		StringBuffer tmpsb = new StringBuffer();
		JavaColumnInfo[] jcis = jti.getNorColumnInfos() ;
		if(Convert.isNullOrEmpty(tablename))
			tablename = jti.getTableName() ;
		tmpsb.append("insert into ").append(tablename)
			.append(" (").append(jcis[0].getColumnName());
		
		for(int i = 1 ; i < jcis.length ; i ++)
		{
			tmpsb.append(",").append(jcis[i].getColumnName());
		}
		tmpsb.append(") values (?");
		for(int i = 1 ; i < jcis.length ; i ++)
		{
			tmpsb.append(",?");
		}
		tmpsb.append(")");
		
		StringBuffer sbgetid=  new StringBuffer();
		sbgetid.append("select @@IDENTITY");
		 
		return new StringBuffer[]{tmpsb,sbgetid} ;
	}

	@Override
	public SqlAndInputVals getSelectSqlWithPage(JavaTableInfo jti,
			boolean distinct,String[] selectcols,
			String wherestr,Object[] input_vals,
			String groupby, String orderbystr, 
			int pageidx, int pagesize)
	{
		String fromwhere = " from "+jti.getTableName();
		if(wherestr!=null&&!(wherestr=wherestr.trim()).equals(""))
		{
			if(wherestr.toLowerCase().startsWith("where "))
				fromwhere += " " +wherestr ;
			else
				fromwhere += " where "+wherestr;
		}
		
		JavaColumnInfo pkcol = jti.getPkColumnInfo();
		
		StringBuffer sqlsb = new StringBuffer();
		sqlsb.append("select");
		if(distinct)
			sqlsb.append(" DISTINCT ");
		sqlsb.append(" top ").append(pagesize).append(" ");
		
		if(selectcols==null||selectcols.length<=0)
		{
			JavaColumnInfo[] norcols = jti.getNorColumnInfos() ;
			int i = 0 ;
			if(pkcol!=null)
			{
				sqlsb.append(pkcol.getColumnName()) ;
			}
			else
			{
				sqlsb.append(norcols[0].getColumnName());
				i ++ ;
			}
			
			for(;i<norcols.length ; i ++)
			{
				sqlsb.append(',').append(norcols[i].getColumnName());
			}
		}
		else
		{
			sqlsb.append(selectcols[0]);
			for(int i = 1 ; i < selectcols.length ; i ++)
			{
				sqlsb.append(',').append(selectcols[i]);
			}
		}
		
		sqlsb.append(fromwhere);
		if(pageidx==0)
		{
			if(groupby!=null&&!(groupby=groupby.trim()).equals(""))
			{
				sqlsb.append(" group by ").append(groupby);
			}
			
			if(orderbystr!=null&&!(orderbystr=orderbystr.trim()).equals(""))
			{
				sqlsb.append(" order by ").append(orderbystr);
			}
			
			return new SqlAndInputVals(sqlsb,input_vals);
		}
		
		String pageNotInCol = pkcol.getColumnName();
		if(groupby!=null&&!(groupby=groupby.trim()).equals(""))
			pageNotInCol = groupby;
		//page idx >0
		if(wherestr!=null&&!(wherestr=wherestr.trim()).equals(""))
			sqlsb.append(" and ").append(pageNotInCol).append(" not in(");
		else
			sqlsb.append(" where ").append(pageNotInCol).append(" not in(");
		
		sqlsb.append("select top ").append(pageidx*pagesize).append(" ").append(pageNotInCol)
			.append(fromwhere);
		
		if(groupby!=null&&!(groupby=groupby.trim()).equals(""))
		{
			sqlsb.append(" group by ").append(groupby);
		}
		
		if(orderbystr!=null&&!(orderbystr=orderbystr.trim()).equals(""))
		{
			sqlsb.append(" order by ").append(orderbystr);
		}
		sqlsb.append(")");
		
		if(groupby!=null&&!(groupby=groupby.trim()).equals(""))
		{
			sqlsb.append(" group by ").append(groupby);
		}
		
		if(orderbystr!=null&&!(orderbystr=orderbystr.trim()).equals(""))
		{
			sqlsb.append(" order by ").append(orderbystr);
		}
		
		//����where���ظ�������,��ô��Ҫ��input_valsҲ����
		if(input_vals!=null&&input_vals.length>0)
		{
			Object[] tmpos = new Object[input_vals.length*2];
			System.arraycopy(input_vals, 0, tmpos, 0, input_vals.length);
			System.arraycopy(input_vals, 0, tmpos, input_vals.length, input_vals.length);
			input_vals = tmpos ;
		}
		return new SqlAndInputVals(sqlsb,input_vals);
	}
}
