package org.iottree.core.store.gdb.autofit;

import java.io.*;
import java.util.*;

import org.iottree.core.store.gdb.connpool.DBType;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlVal;
import org.iottree.core.util.xmldata.XmlVal.XmlValType;


public abstract class DbSql
{
	public static DbSql getDbSqlByDBType(DBType dbt)
	{
		if(dbt==DBType.derby)
		{
			return new DBSqlDerby();
		}
		else if (dbt == DBType.hsql)
		{
			//dbSql = new DBSqlHsql();
			return new DBSqlDerby();
		}
		else if (dbt == DBType.sqlserver)
		{
			return new DBSqlSqlServer();
		}
		else if(dbt==DBType.mysql)
		{
			return new DBSqlMySql();
		}
		else
			throw new IllegalArgumentException("not support for db type="+dbt);
	}
	/**
	 * ����java���Ͷ�������ݿ����Ϣ�����ɶ�Ӧ���ݿ�Ĵ������ű�
	 * @param jti
	 * @return
	 */
	public List<String> getCreationSqls(JavaTableInfo jti)
	{
		ArrayList<String> rets = new ArrayList<String>();
		StringBuffer[] sbs = constructCreationTable(jti) ;
		for(StringBuffer sb :sbs)
		{
			rets.add(sb.toString());
		}
		
		HashMap<String,ArrayList<JavaColumnInfo>> idxname2cols = new HashMap<String,ArrayList<JavaColumnInfo>>() ;
		
		for(JavaColumnInfo tmpjci:jti.getNorColumnInfos())
		{
			if(!tmpjci.hasIdx())
				continue ;
			
			String idxn = tmpjci.getIdxName() ;
			if(idxn==null||"".equals(idxn))
			{
				StringBuffer sb = constructIndexTable(jti,tmpjci) ;
				rets.add(sb.toString());
			}
			else
			{
				ArrayList<JavaColumnInfo> jjs = idxname2cols.get(idxn) ;
				if(jjs==null)
				{
					jjs = new ArrayList<JavaColumnInfo>() ;
					idxname2cols.put(idxn,jjs) ;
				}
				jjs.add(tmpjci) ;
			}
		}
		
		for(Map.Entry<String,ArrayList<JavaColumnInfo>> idxn2jcs:idxname2cols.entrySet())
		{//
			String idxn = idxn2jcs.getKey() ;
			ArrayList<JavaColumnInfo> jcs = idxn2jcs.getValue() ;
			boolean bunique = false;
			for(JavaColumnInfo jc:jcs)
			{
				if(jc.isUnique())
					bunique = true ;
			}
			StringBuffer sb = constructIndexTable(jti,bunique,idxn,jcs) ;
			rets.add(sb.toString());
		}
		
		for(JavaForeignKeyInfo tmpfki:jti.getForeignKeyInfos())
		{
			StringBuffer sb = constructForeignKeyTable(jti,tmpfki);
			rets.add(sb.toString());
		}
		
		return rets ;
	}
	
	
	
	/**
	 * 
	 * @param proxyserver ���ϵͳ�����ڷֲ�ʽ�����µ�server
	 * @param jti
	 * @return
	 */
	protected StringBuffer[] constructCreationTable(JavaTableInfo jti)
	{
		StringBuffer tmpsb = new StringBuffer();
		tmpsb.append("create table ").append(jti.getTableName())
			.append("(");
		
		JavaColumnInfo pkcol = jti.getPkColumnInfo() ;
		if(pkcol!=null)
		{
			tmpsb.append(pkcol.getColumnName()).append(" ")
				.append(getSqlType(pkcol.getValType(),pkcol.getMaxLen()))
				.append(" primary key");
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
	
	
	protected StringBuffer[] constructCreationTableDistributedMode1(JavaTableInfo jti)
	{
		StringBuffer tmpsb = new StringBuffer();
		tmpsb.append("create table ").append(jti.getTableName())
			.append("(");
		
		tmpsb.append("_ProxyId integer,");
		
		JavaColumnInfo pkcol = jti.getPkColumnInfo() ;
		if(pkcol!=null)
		{
			tmpsb.append(pkcol.getColumnName()).append(" ")
				.append(getSqlType(pkcol.getValType(),pkcol.getMaxLen()));
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
	
	
	protected StringBuffer[] constructCreationTableDistributedMode2(JavaTableInfo jti)
	{
		StringBuffer tmpsb = new StringBuffer();
		tmpsb.append("create table ").append(jti.getTableName())
			.append("(");
		
		JavaColumnInfo pkcol = jti.getPkColumnInfo() ;
		if(pkcol==null)
		{
			throw new RuntimeException("distributed mode2 must has pk column!") ;
		}
		
		tmpsb.append(pkcol.getColumnName()).append(" ")
			.append(getSqlType(pkcol.getValType(),pkcol.getMaxLen()))
			.append(" primary key");
		
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
		
		//����ʱ�����
		tmpsb.append(",_ServerUpdateDT bigint") ;
		
		tmpsb.append(")");
		return new StringBuffer[]{tmpsb} ;
	}
	
	/**
	 * ������һ���Ѿ����ڵı��������е�sqlһ��
	 * @param jti
	 * @param jci
	 * @return
	 */
	public abstract StringBuffer constructAddColumnToTable(JavaTableInfo jti,JavaColumnInfo jci,String after_colname);
	
	
	public StringBuffer constructChgColumnSql(JavaTableInfo jti,JavaColumnInfo jci)
	{
		//ALTER TABLE `ddd`.`case_course` 
		//CHANGE COLUMN `CourseType` `CourseType` INT(3) NULL DEFAULT NULL ;
		StringBuffer tmpsb = new StringBuffer();
		tmpsb.append("ALTER TABLE ")
			.append(jti.getTableName())
			.append(" CHANGE COLUMN ").append(jci.getColumnName()).append(" ")
			.append(jci.getColumnName()).append(" ")
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
		return tmpsb;
	}
	
	public StringBuffer constructIndexTable(JavaTableInfo jti,JavaColumnInfo jci)
	{
		//create index idx_security_users_email on security_users (Email)
		StringBuffer tmpsb = new StringBuffer();
		if(jci.isUnique())
			tmpsb.append("create unique index ");
		else
			tmpsb.append("create index ");
		
		tmpsb.append(calIndexName(jti, jci));
		tmpsb.append(" on ").append(jti.getTableName())
			.append(" (").append(jci.getColumnName()).append(")");
		
		return tmpsb ;
	}
	
	
	public StringBuffer constructIndexTable(JavaTableInfo jti,boolean bunique,
			String idxname,List<JavaColumnInfo> jcis)
	{
		//create index idx_security_users_email on security_users (Email)
		int s = jcis.size();
		if(s<=0)
			return null ;
		
		StringBuffer tmpsb = new StringBuffer();
		if(bunique)
			tmpsb.append("create unique index ");
		else
			tmpsb.append("create index ");
		
		tmpsb.append(calIndexName(jti, idxname));
		tmpsb.append(" on ").append(jti.getTableName())
			.append(" (").append(jcis.get(0).getColumnName());
		for(int i = 1 ; i < s ; i ++)
			tmpsb.append(',').append(jcis.get(i).getColumnName());
		tmpsb.append(")");
		
		return tmpsb ;
	}
	
	private StringBuffer constructIndexTableNoUnique(JavaTableInfo jti,JavaColumnInfo jci)
	{
		//create index idx_security_users_email on security_users (Email)
		StringBuffer tmpsb = new StringBuffer();
		tmpsb.append("create index ");
		
		tmpsb.append(calIndexName(jti, jci));
		tmpsb.append(" on ").append(jti.getTableName())
			.append(" (").append(jci.getColumnName()).append(")");
		
		return tmpsb ;
	}
	
	protected String calIndexName(JavaTableInfo jti,JavaColumnInfo jci)
	{
		return "idx_"+jti.getTableName()+"_"+jci.getColumnName();
	}
	
	protected String calIndexName(JavaTableInfo jti,String name)
	{
		return "idx_"+jti.getTableName()+"_"+name;
	}
	
	protected String calIndexName(String tn,String coln)
	{
		return "idx_"+tn+"_"+coln;
	}
	
	/**
	 * ����ɾ��������sqlָ�ȱʡ��ʽ��drop index tablen.idxn
	 * @param jti
	 * @param jci
	 * @return
	 */
	public abstract StringBuffer constructDropIndex(JavaTableInfo jti,JavaColumnInfo jci);
	
	public StringBuffer constructForeignKeyTable(JavaTableInfo jti,JavaForeignKeyInfo jfki)
	{
		StringBuffer tmpsb = new StringBuffer();
		tmpsb.append("ALTER TABLE ").append(jti.getTableName())
			.append(" ADD CONSTRAINT FK_").append(jti.getTableName()).append('_').append(jfki.getRefTableName())
			.append(" FOREIGN KEY ")
			.append("(").append(jfki.getLocalColName()).append(") REFERENCES ")
			.append(jfki.getRefTableName()).append(" (").append(jfki.getRefColName()).append(") ON DELETE CASCADE");
		
		return tmpsb ;
	}
	
	public StringBuffer constructDropForeignKeyTable(JavaTableInfo jti,JavaForeignKeyInfo jfki)
	{
		StringBuffer tmpsb = new StringBuffer();
		tmpsb.append("ALTER TABLE ").append(jti.getTableName())
			.append(" DROP CONSTRAINT FK_").append(jti.getTableName()).append('_').append(jfki.getRefTableName());
		return tmpsb ;
	}
	
	/**
	 * ��ø���pkid�������ݿ��sql���
	 * @param jti
	 * @return
	 */
	public StringBuilder getSelectByPkIdSql(JavaTableInfo jti,String tablename)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("select ").append(jti.getPkColumnInfo().getColumnName());
		for(JavaColumnInfo jci:jti.getNorColumnInfos())
		{
			sb.append(',').append(jci.getColumnName());
		}
		if(Convert.isNullOrEmpty(tablename))
			tablename = jti.getTableName();
		sb.append(" from ").append(tablename)
			.append(" where ").append(jti.getPkColumnInfo().getColumnName()).append("=?");
		return sb ;
	}
	
	
	public StringBuilder getSelectColsByPkIdSql(JavaTableInfo jti,String[] colnames)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("select ").append(colnames[0]);
		for(int i = 1 ; i < colnames.length ; i ++)
		{
			sb.append(',').append(colnames[i]);
		}
		sb.append(" from ").append(jti.getTableName())
			.append(" where ").append(jti.getPkColumnInfo().getColumnName()).append("=?");
		return sb ;
	}
	
	public StringBuilder getMaxPkIdSql(JavaTableInfo jti)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("select max(").append(jti.getPkColumnInfo().getColumnName());
		sb.append(") from ").append(jti.getTableName());
		return sb ;
	}
	
	
	public StringBuilder getSelectByUniqueColSql(JavaTableInfo jti,String unique_col)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("select ").append(jti.getPkColumnInfo().getColumnName());
		for(JavaColumnInfo jci:jti.getNorColumnInfos())
		{
			sb.append(',').append(jci.getColumnName());
		}
		sb.append(" from ").append(jti.getTableName())
			.append(" where ").append(unique_col).append("=?");
		return sb ;
	}
	
	public final StringBuilder getUpdateByPkIdSql(JavaTableInfo jti,String tablename)
	{
		StringBuilder sb = new StringBuilder();
		if(Convert.isNullOrEmpty(tablename))
			tablename = jti.getTableName() ;
		sb.append("update ").append(tablename).append(" set ");
		JavaColumnInfo[] jcis = jti.getUpdateNorColumnInfos();
		sb.append(jcis[0].getColumnName()).append("=?") ;
		for(int i = 1 ; i < jcis.length ; i ++)
		{
			sb.append(',').append(jcis[i].getColumnName()).append("=?");
		}
		sb.append(" where ").append(jti.getPkColumnInfo().getColumnName()).append("=?");
		return sb ;
	}
	
	public StringBuilder getInsertSqlWithInputId(JavaTableInfo jti,String tablename)
	{
		StringBuilder tmpsb = new StringBuilder();
		JavaColumnInfo[] jcis = jti.getNorColumnInfos() ;
		if(Convert.isNullOrEmpty(tablename))
			tablename = jti.getTableName();
		tmpsb.append("insert into ").append(tablename).append("(");
		JavaColumnInfo pkcol = jti.getPkColumnInfo() ;
		if(pkcol!=null)
			tmpsb.append(pkcol.getColumnName()).append(',').append(jcis[0].getColumnName());
		else
			tmpsb.append(jcis[0].getColumnName());
		
		for(int i = 1 ; i < jcis.length ; i ++)
		{
			tmpsb.append(",").append(jcis[i].getColumnName());
		}
		if(pkcol!=null)
			tmpsb.append(") values (?,?");
		else
			tmpsb.append(") values (?");
		
		for(int i = 1 ; i < jcis.length ; i ++)
		{
			tmpsb.append(",?");
		}
		tmpsb.append(")");
		return tmpsb ;
	}
	
	public abstract StringBuffer[] getInsertSqlWithNewIdReturn(JavaTableInfo jti,String tablename);
	
	public abstract SqlAndInputVals getSelectSqlWithPage(JavaTableInfo jti,
			boolean distinct,String[] selectcols,
			String wherestr,Object[] input_vals,//where ����������Ĳ���
			String groupby,String orderbystr,
			int pageidx,int pagesize);
//	public String SQL_select(String tablename,)
//	{
//		StringBuffer sqlsb = new StringBuffer();
//		sqlsb.append("select ");
//		if (bHasKeyCol)
//		{
//			sqlsb.append(mon.getDBKeyColumn());
//			String[] norcols = mon.getDBNorColumns();
//			for (int i = 0; i < norcols.length; i++)
//			{
//				sqlsb.append(",").append(norcols[i]);
//			}
//		}
//		else
//		{
//			String[] norcols = mon.getDBNorColumns();
//			sqlsb.append(norcols[0]);
//			for (int i = 1; i < norcols.length; i++)
//			{
//				sqlsb.append(",").append(norcols[i]);
//			}
//		}
//
//		sqlsb.append(" from ").append(mon.getDBTableName());
//		if (condcols != null && condcols.length > 0)
//		{
//			sqlsb.append(" where");
//			for (int i = 0; i < condcols.length; i++)
//			{
//				sqlsb.append(" ");
//				if (i > 0)
//				{
//					sqlsb.append("AND ");
//
//				}
//				sqlsb.append(condcols[i]).append("=?");
//			}
//		}
//
//		return sqlsb.toString();
//	}
	
	public static class SqlAndInputVals
	{
		public StringBuffer sqlSB = null ;
		public Object[] inputVals = null ;
		
		public SqlAndInputVals(StringBuffer sb,Object[] inputvs)
		{
			sqlSB = sb ;
			inputVals = inputvs ;
		}
	}
	/**
	 * �õ����ݿ�����
	 * @return
	 */
	public abstract DBType getDBType();
	
	/**
	 * ����javaֵ���ͣ���ñ����ݿ��Ӧ��sql�������
	 * @param vt
	 * @return
	 */
	public abstract String getSqlType(XmlVal.XmlValType vt,int maxlen) ;
}
