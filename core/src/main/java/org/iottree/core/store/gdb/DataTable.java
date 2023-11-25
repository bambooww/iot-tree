package org.iottree.core.store.gdb;

import java.io.IOException;
import java.io.Writer;
import java.sql.*;
import java.util.*;

import org.iottree.core.util.xmldata.IXmlDataable;
import org.iottree.core.util.xmldata.XmlData;


/**
 * �˶���֧��ͬ�����ݿ����
 * @author Jason Zhu
 *
 */
public class DataTable implements IXmlDataable
{
	private String tableName = null ;
	private ArrayList<DataColumn> columns = new ArrayList<DataColumn>() ;
	private ArrayList<DataRow> rows = new ArrayList<DataRow>() ;
	
	int totalCount = -1 ;
	int pageSize = -1 ;
	int pageCur = 0 ;
	
	public DataTable()
	{
		
	}
	
	public DataTable(String tn)
	{
		tableName = tn ;
	}
	
	public String getTableName()
	{
		return tableName ;
	}
	
	public void setTableName(String tn)
	{
		tableName = tn ;
	}
	
	public ArrayList<DataColumn> getColumns()
	{
		return columns ;
	}
	
	public void addColumn(DataColumn dc)
	{
		DataColumn odc = getColumn(dc.getName()) ;
//		if(odc!=null)
//			throw new IllegalArgumentException("column with name="+dc.getName()+" is existed!");
		
		columns.add(dc);
	}
	
	public void addColumn(String colname,Class vtype)
	{
		addColumn(new DataColumn(colname,vtype));
	}
	
//	public DataColumn addOrCreateColumn(String name)
//	{
//		DataColumn odc = getColumn(name);
//		if(odc!=null)
//			return odc ;
//		
//		odc = new DataColumn(name, null);
//		columns.add(odc);
//		return odc ;
//	}
	
	public DataColumn getColumn(int idx)
	{
		return columns.get(idx);
	}
	
	public String getColumnName(int idx)
	{
		return columns.get(idx).getName();
	}
	
	public HashMap<String,DataColumn> getColumnMap()
	{
		HashMap<String,DataColumn> n2dc = new HashMap<String,DataColumn>() ;
		for(DataColumn dc:columns)
		{
			n2dc.put(dc.getName(), dc) ;
		}
		return n2dc ;
	}
	
	public DataColumn getColumn(String colname)
	{
		for(DataColumn dc:columns)
		{
			if(dc.getName().equalsIgnoreCase(colname))
				return dc ;
		}
		
		return null ;
	}
	
	public int getColumnNum()
	{
		return columns.size();
	}
	
	public DataRow createNewRow()
	{
		return new DataRow(this);
	}
	
	public void addRow(DataRow dr)
	{
		if(dr.belongToDT!=this)
			throw new RuntimeException("row is not belong to this table!");
		
		rows.add(dr);
	}
	
	public DataRow getRow(int rowidx)
	{
		if(rowidx<0||rowidx>=rows.size())
			return null ;
		
		return rows.get(rowidx);
	}
	
	public int getRowNum()
	{
		return rows.size();
	}
	
	public ArrayList<DataRow> getRows()
	{
		return rows ;
	}
	
	public DataRow removeRow(int rowidx)
	{
		return rows.remove(rowidx);
	}
	
	public void removeRow(DataRow dr)
	{
		rows.remove(dr);
	}
	
	/**
	 * ���һ�����е�����ֵ
	 * @param col_idx
	 * @return
	 */
	public List getColumnValuesAsList(int col_idx)
	{
		DataColumn dc = this.getColumn(col_idx) ;
		if(dc==null)
			return null ;
		
		ArrayList ret = new ArrayList(rows.size()) ;
		for(DataRow dr:rows)
		{
			Object obj = dr.getValue(col_idx) ;
			if(obj==null)
				continue ;
			ret.add(obj);
		}
		return ret;
	}
	
	public List getColumnValuesAsList(String coln)
	{
		DataColumn dc = this.getColumn(coln) ;
		
		if(dc==null)
			return null ;
		
		ArrayList ret = new ArrayList(rows.size()) ;
		for(DataRow dr:rows)
		{
			Object obj = dr.getValue(coln) ;
			if(obj==null)
				continue ;
			ret.add(obj);
		}
		return ret;
	}
	/**
	 * �������ݿ���ʽ������С�γɵ��ܼ�¼����
	 * �ü�¼������rows�������������ܲ�һ��
	 * @return
	 */
	public int getTotalCount()
	{
		return totalCount ;
	}
	
	public void setTotalCount(int tc)
	{
		totalCount = tc ;
	}
	
	public int getPageSize()
	{
		return pageSize ;
	}
	
	public void setPageSize(int ps)
	{
		pageSize = ps ;
	}
	
	public int getPageCur()
	{
		return pageCur ;
	}
	
	public void setPageCur(int pc)
	{
		pageCur = pc ;
	}
	
	public void writeXml(Writer w)
	{
		// TODO 
	}
	
	/**
	 * �ж�ĳһ�е�ֵ�Ƿ���Ψһ˳������
	 * @param colname
	 * @return
	 */
	public boolean checkColumnUniqueInOrder(String colname)
	{
		int rn = getRowNum() ;
		if(rn<=0)
			return true ;
		
		DataRow dr = this.getRow(0) ;
		Object v = dr.getValue(colname) ;
		
		for(int i = 1 ; i < rn ; i ++)
		{
			dr = this.getRow(i) ;
			Object v1 = dr.getValue(colname) ;
			if(v.toString().compareTo(v1.toString())>=0)
				return false;
			
			v = v1 ;
		}
		
		return true ;
	}
	
	public Object getFirstColumnOfFirstRow()
	{
		if (getRowNum() <= 0)
			return null;
		DataRow dr = getRow(0);
		return dr.getValue(0);
	}
	
	
	////////////////////////////////////////
	//�Ա����м����֧��--����Excel�����м��㹫ʽ֧��
	//�磺sum(A0:A5)
	public long calByFormulaInt64(String formula_str)
	{
		throw new RuntimeException("no impl") ;
	}
	
	
	public double calByFormulaDouble(String formula_str)
	{
		throw new RuntimeException("no impl") ;
	}
	
	
	////////////////////////////////////////
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(tableName).append("]\r\n");
		for(DataColumn dc:columns)
		{
			sb.append("|  ").append(dc.getName()) ;
		}
		
		sb.append("\r\n------------------------------------------------------\r\n");
		for(DataRow dr:rows)
		{
			for(DataColumn dc:columns)
			{
				Object ov = dr.getValue(dc.getName()) ;
				
				sb.append("|  ").append(ov) ;
				if(ov!=null)
					sb.append(":"+ov.getClass().getCanonicalName());
			}
			sb.append("\r\n");
		}
		return sb.toString();
	}

	public XmlData toXmlData()
	{
		XmlData xd = new XmlData() ;
		if(tableName!=null)
			xd.setParamValue("table_name",tableName) ;
		List<XmlData> xds = xd.getOrCreateSubDataArray("columns") ;
		for(DataColumn dc:columns)
		{
			xds.add(dc.toXmlData()) ;
		}
		xds = xd.getOrCreateSubDataArray("rows") ;
		for(DataRow dr:rows)
		{
			xds.add(dr.toXmlData()) ;
		}
		xd.setParamValue("total", totalCount) ;
		xd.setParamValue("page_size", pageSize) ;
		xd.setParamValue("page_cur", pageCur) ;
		return xd;
	}

	public void fromXmlData(XmlData xd)
	{
		tableName = xd.getParamValueStr("table_name") ;
		List<XmlData> xds = xd.getSubDataArray("columns") ;
		if(xds!=null)
		{
			for(XmlData tmpxd:xds)
			{
				DataColumn dc = new DataColumn() ;
				dc.fromXmlData(tmpxd) ;
				columns.add(dc) ;
			}
		}
		
		xds = xd.getSubDataArray("rows") ;
		if(xds!=null)
		{
			for(XmlData tmpxd:xds)
			{
				DataRow dr = new DataRow(this) ;
				dr.fromXmlData(tmpxd) ;
				rows.add(dr) ;
			}
		}
		
		totalCount = xd.getParamValueInt32("total", -1) ;
		pageSize = xd.getParamValueInt32("page_size",-1 ) ;
		pageCur = xd.getParamValueInt32("page_cur", -1) ;
	}
	
	/**
	 * ���������������ˢ��ҳ��ʹ�õ��ַ���
	 * �磬֧��jsҳ�����ɱ��������
	 * ��������̨��ˢ��jspʵ�־Ϳ��Էǳ���
	 * @param w
	 * @throws IOException 
	 */
	public void writeToJsAjax(Writer w) throws IOException
	{
		int s = 0 ;
		if(this.columns!=null)
			s = this.columns.size() ;
		for(int i = 0 ; i < s ; i ++)
		{
			DataColumn dc = columns.get(i) ;
			if(i==0)
			{
				w.write(dc.getName()) ;
			}
			else
			{
				w.write("|"+dc.getName()) ;
			}
		}
		w.write("\r\n") ;
		
		for(int i = 0 ; i < s ; i ++)
		{
			DataColumn dc = columns.get(i) ;
			String t = dc.getTitle() ;
			if(t==null)
				t = "" ;
			
			if(i==0)
			{
				w.write(t) ;
			}
			else
			{
				w.write("|"+t) ;
			}
		}
		w.write("\r\n") ;
		
		for(DataRow dr:this.rows)
		{
			for(int i = 0 ; i < s ; i ++)
			{
				if(i==0)
				{
					w.write(dr.getValueStr(i,"")) ;
				}
				else
				{
					w.write("|"+dr.getValueStr(i,"")) ;
				}
			}
			
			w.write("\r\n") ;
		}
	}
	
	/**
	 * ͬ������Ӧ�����ݱ���
	 * 1,������Ψһ��
	 * 2,
	 * @param conn ���ݿ�����
	 * @param tablename ���ݿ������
	 * @param uniquecol ���ݿ���Ψһ��ֵ
	 * @param syncols ��Ҫͬ����������
	 * @param bdel �Ƿ�Ҫ����¼ɾ��--��Щ����£�����������ɾ������ʱ��ͬ������Ҫ��ɾ�������������false
	 */
	public int[] synToDBTable(Connection conn,String tablename,String uniquecol,String[] syncols,boolean bdel)
		throws Exception
	{
		String sel_sql = "select "+uniquecol ;
		for(String s:syncols)
		{
			sel_sql += ","+s ;
		}
		sel_sql += " from "+tablename ;
		
		PreparedStatement ps = null ;
		ResultSet rs = null ;
		
		try
		{
			//System.out.println(sel_sql) ;
			//�õ�����Ϣ
			ps = conn.prepareStatement(sel_sql) ;
			rs = ps.executeQuery() ;
			DBResult dbr = new DBResult() ;
			dbr.appendResultSet(tablename, 0, rs, 0, -1, null) ;
			DataTable olddt = dbr.getResultFirstTable() ;
			
			rs.close() ;
			rs = null ;
			ps.close() ;
			ps = null ;
			
			this.columns = olddt.columns ;
			
			//�Ա�����
			ArrayList<DataRow> insert_rows = new ArrayList<DataRow>() ;
			ArrayList<DataRow> update_rows = new ArrayList<DataRow>() ;
			ArrayList<DataRow> delete_rows = new ArrayList<DataRow>() ;
			
			//
			HashMap<Object,DataRow> newdt_key2row = new HashMap<Object,DataRow>() ;
			int newn = this.getRowNum() ;
			for(int i = 0 ; i < newn ; i ++)
			{
				DataRow dr = this.getRow(i) ;
				Object ov = dr.getValue(uniquecol) ;
				if(ov==null)
					throw new Exception("table has no key value with col name="+uniquecol) ;
				newdt_key2row.put(ov, dr) ;
			}
			
			int oldn = olddt.getRowNum() ;
			for(int i = 0 ; i < oldn ; i ++)
			{
				DataRow olddr = olddt.getRow(i) ;
				Object oldkv = olddr.getValue(uniquecol) ;
				if(oldkv==null)
					throw new Exception("db table has no key value with col name="+uniquecol) ;
				DataRow tmpdr = newdt_key2row.remove(oldkv) ;
				if(tmpdr==null)
				{//delete
					delete_rows.add(olddr) ; 
				}
				else
				{//update
					if(!olddr.checkRowEquals4DB(tmpdr))
						update_rows.add(tmpdr) ;
				}
			}
			//insert
			insert_rows.addAll(newdt_key2row.values()) ;
			
			//do insert
			String[] allcols = new String[syncols.length+1] ;
			allcols[0] = uniquecol ;
			System.arraycopy(syncols, 0, allcols, 1, syncols.length) ;
			for(DataRow tmpdr:insert_rows)
			{
				tmpdr.doInsertDB(conn, tablename, allcols) ;
			}
			
			//do update
			for(DataRow tmpdr:update_rows)
			{
				tmpdr.doUpdateDB(conn, tablename, uniquecol, syncols) ;
			}
			
			//do del
			int delc = 0 ;
			if(bdel)
			{
				for(DataRow tmpdr:delete_rows)
				{
					tmpdr.doDeleteDB(conn, tablename, uniquecol) ;
				}
				delc = delete_rows.size() ;
			}
			
			//System.out.println(this.tableName+"  insert="+insert_rows.size()+" update="+update_rows.size()+" delete="+delc) ;
			return new int[]{insert_rows.size(),update_rows.size(),delc} ;
		}
		finally
		{
			if(ps!=null)
			{
				try
				{
					ps.close() ;
				}
				catch(Exception ee){}
			}
			
			if(rs!=null)
			{
				try
				{
					rs.close() ;
				}
				catch(Exception ee){}
			}
		}
	}
}
