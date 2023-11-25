package org.iottree.core.store.gdb;

import java.util.*;
import javax.servlet.http.*;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlData;

/**
 * �ܶ������,ҳ����Ҫ��ҳ��ʾ����
 * Ϊ�˷���ҳ��ķ�ҳ֧��. ������jspҳ���ж���һ�������ظó�����
 * 
 * Ȼ��ʹ��֧�ַ�ҳ�ı�ǩ�����б���ʾ. ֧�ַ�ҳ�ı�ǩ���Ը��ݱ����Զ���
 * ��ҳ. �ӿ쿪���ٶ�
 * 
 * @author Jason Zhu
 */
public abstract class GDBPageAccess
{
	//public static final String PN_PAGE_SIZE = "__ps" ;
	public static final String PN_PAGE_CUR = "__p_" ;
	
	HttpServletRequest request = null ;
	
	int pageSize = 20 ;
	int pageCur = 0 ;
	List listObjs = null ;
	int total = -1 ;
	
	public GDBPageAccess()
	{
		
		
	}
	
	public void setHttpRequest(int pagesize,HttpServletRequest req)
		throws Exception
	{
		request = req ;
		//��request����ȡҳ����Ϣ
		//pageSize = Convert.parseToInt32(req.getParameter(PN_PAGE_SIZE), 20) ;
		pageSize = pagesize ;
		if(pageSize<=0)
			pageSize = 20 ;
		pageCur = Convert.parseToInt32(req.getParameter(PN_PAGE_CUR), 0) ;
		
		DataOut dout = new DataOut() ;
		
		XmlData xd = XmlData.getXmlDataFromRequest(req) ;
		listObjs = getPageObjList(xd,pageCur,pageSize,dout) ;
		total = dout.totalCount ;
	}
	
	public int getPageSize()
	{
		return pageSize ;
	}
	
	public int getPageCur()
	{
		return pageCur ;
	}
	
	public List getListObjs()
	{
		return listObjs ;
	}
	
	public int getListObjTotalNum()
	{
		return total ;
	}
	
	public int getPageTotalNum()
	{
		if(total<=0)
			return 0 ;
		
		return total / pageSize + (total % pageSize)>0?1:0 ;
	}
	
	/**
	 * 
	 * @param searchxd ��������
	 * @param pageidx
	 * @param pagesize
	 * @param dout
	 * @return
	 * @throws Exception
	 */
	public abstract List getPageObjList(XmlData searchxd,int pageidx,int pagesize,DataOut dout)
		throws Exception;
}
