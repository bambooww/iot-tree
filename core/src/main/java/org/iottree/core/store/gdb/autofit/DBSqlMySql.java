package org.iottree.core.store.gdb.autofit;

import org.iottree.core.store.gdb.connpool.DBType;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlVal;
import org.iottree.core.util.xmldata.XmlVal.XmlValType;

public class DBSqlMySql extends DbSql
{

	public DBType getDBType()
	{
		return DBType.derby;
	}

	public String getSqlType(XmlValType vt, int maxlen)
	{
		if(vt==XmlValType.vt_int32)
		{
			if(maxlen>0)
				return "int("+maxlen+")";
			return "integer";
		}
		else if(vt==XmlValType.vt_byte_array)
		{//blob
			return "longblob";
		}
		else if(vt==XmlValType.vt_date)
		{
			return "datetime";
		}
		else if(vt==XmlValType.vt_double)
		{
			return "double";
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
				return "LONGVARCHAR";
			}
			
			return "varchar("+maxlen+")";
		}
		else if(vt==XmlValType.vt_bool)
		{
			return "TINYINT(1)";
		}
		else if(vt==XmlValType.vt_bigdecimal)
		{
			return "decimal(18,2)" ;
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
		//StringBuffer auto_inc_sb = null ;
		if(pkcol!=null)
		{
			if(pkcol.isAutoVal()&& pkcol.getValType()!=XmlVal.XmlValType.vt_string)
			{
				tmpsb.append(pkcol.getColumnName()).append(" ")
					.append(getSqlType(pkcol.getValType(),pkcol.getMaxLen()))
					.append(" AUTO_INCREMENT primary key ");
				
//				if(pkcol.getAutoValStart()>0)
//				{
//					auto_inc_sb = new StringBuffer() ;
//					auto_inc_sb.append("ALTER TABLE ")
//						.append(jti.getTableName())
//						.append(" AUTO_INCREMENT=").append(pkcol.getAutoValStart()) ;
//				}
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
		
		if(pkcol!=null&&pkcol.isAutoVal()&&pkcol.getAutoValStart()>0)
		{
			tmpsb.append(")").append(" AUTO_INCREMENT=").append(pkcol.getAutoValStart());
		}
		else
		{
			tmpsb.append(")");
			//tmpsb.append(") ENGINE=InnoDB");
		}
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
		//StringBuffer auto_inc_sb = null ;
		if(pkcol!=null)
		{
			if(pkcol.isAutoVal())
			{
				tmpsb.append(pkcol.getColumnName()).append(" ")
					.append(getSqlType(pkcol.getValType(),pkcol.getMaxLen()));
				
//				if(pkcol.getAutoValStart()>0)
//				{
//					auto_inc_sb = new StringBuffer() ;
//					auto_inc_sb.append("ALTER TABLE ")
//						.append(jti.getTableName())
//						.append(" AUTO_INCREMENT=").append(pkcol.getAutoValStart()) ;
//				}
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
		
		//if(pkcol!=null&&pkcol.isAutoVal()&&pkcol.getAutoValStart()>0)
		//{
		//	tmpsb.append(")").append(" TYPE=MyISAM AUTO_INCREMENT=").append(pkcol.getAutoValStart());
		//}
		//else
		{
			tmpsb.append(") ENGINE=InnoDB");
		}
		return new StringBuffer[]{tmpsb} ;
	}
	
	@Override
	protected StringBuffer[] constructCreationTableDistributedMode2(JavaTableInfo jti)
	{
		StringBuffer tmpsb = new StringBuffer();
		tmpsb.append("create table ").append(jti.getTableName())
			.append("(");
		
		JavaColumnInfo pkcol = jti.getPkColumnInfo() ;
		//StringBuffer auto_inc_sb = null ;
		if(pkcol!=null)
		{
			if(pkcol.isAutoVal())
			{
				tmpsb.append(pkcol.getColumnName()).append(" ")
					.append(getSqlType(pkcol.getValType(),pkcol.getMaxLen()))
					.append(" AUTO_INCREMENT primary key ");
				
//				if(pkcol.getAutoValStart()>0)
//				{
//					auto_inc_sb = new StringBuffer() ;
//					auto_inc_sb.append("ALTER TABLE ")
//						.append(jti.getTableName())
//						.append(" AUTO_INCREMENT=").append(pkcol.getAutoValStart()) ;
//				}
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
		
		//if(pkcol!=null&&pkcol.isAutoVal()&&pkcol.getAutoValStart()>0)
		//{
		//	tmpsb.append(")").append(" TYPE=MyISAM AUTO_INCREMENT=").append(pkcol.getAutoValStart());
		//}
		//else
		{
			tmpsb.append(") ENGINE=InnoDB");
		}
		return new StringBuffer[]{tmpsb} ;
	}
	
	public StringBuffer constructAddColumnToTable(JavaTableInfo jti,JavaColumnInfo jci,String after_colname)
	{
		StringBuffer tmpsb = new StringBuffer();
		tmpsb.append("ALTER TABLE ")
			.append(jti.getTableName())
			.append(" ADD COLUMN ").append(jci.getColumnName()).append(" ")
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
				if(!"".equals(defvstr))
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
	 * ���ع���ɾ��������sqlָ�ȱʡ��ʽ��drop index idxn if exists
	 * @param jti
	 * @param jci
	 * @return
	 */
	public StringBuffer constructDropIndex(JavaTableInfo jti,JavaColumnInfo jci)
	{
		StringBuffer tmpsb = new StringBuffer();
		tmpsb.append("DROP INDEX ")
			.append(calIndexName(jti,jci)).append(" IF EXISTS");
		return tmpsb ;
	}
	
//	private String getPkSeqName(JavaTableInfo jti)
//	{
//		JavaColumnInfo pkcol = jti.getPkColumnInfo();
//		if(pkcol==null)
//			return null ;
//		
//		return "dx_seq_"+jti.getTableName()+"_"+pkcol.getColumnName() ;
//	}
//	@Override
//	public List<String> getCreationSqls(JavaTableInfo jti)
//	{
//		List<String> rets = super.getCreationSqls(jti);
//		
//		String seqn = getPkSeqName(jti) ;
//		if(seqn==null)
//			return rets ;
//		
//		//����sequence
//		rets.add("CREATE SEQUENCE "+seqn+" AS BIGINT START WITH 1");
//		return rets ;
//	}
	
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
		sbgetid.append("select LAST_INSERT_ID()");
		
		return new StringBuffer[]{tmpsb,sbgetid} ;
	}

	@Override
	public SqlAndInputVals getSelectSqlWithPage(JavaTableInfo jti,
			boolean distinct,String[] selectcols,
			String wherestr,Object[] input_vals,
			String groupby,String orderbystr,
			int pageidx,int pagesize)
	{
		String limstr = " limit " + pageidx * pagesize + "," + pagesize;
		StringBuffer sqlsb = new StringBuffer();
		sqlsb.append("select ");
		if(distinct)
			sqlsb.append("DISTINCT ");

		if(selectcols==null||selectcols.length<=0)
		{
			JavaColumnInfo pkcol = jti.getPkColumnInfo();
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

		sqlsb.append(" from ").append(jti.getTableName());
		if(wherestr!=null&&!(wherestr=wherestr.trim()).equals(""))
		{
			sqlsb.append(" where ").append(wherestr);
		}

		if(groupby!=null&&!(groupby=groupby.trim()).equals(""))
		{
			sqlsb.append(" group by ").append(groupby);
		}
		
		if(orderbystr!=null&&!(orderbystr=orderbystr.trim()).equals(""))
		{
			sqlsb.append(" order by ").append(orderbystr);
		}
		
		sqlsb.append(limstr);
		return new SqlAndInputVals(sqlsb,input_vals);
	}
}