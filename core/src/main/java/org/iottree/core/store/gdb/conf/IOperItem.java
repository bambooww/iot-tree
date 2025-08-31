package org.iottree.core.store.gdb.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.w3c.dom.Element;

public abstract class IOperItem
{
	/**
	 * 在一个Oper语句标签中,如果设置了形如set_param="@NewWorkItemId=(0,0)|@xx=(0,1)"的属性
	 * 并且内部语句是有结果的语句
	 * 则,在该语句运行结束后,会对输入参数进行设置.以满足后续语句的运行需要的参数.
	 * 
	 * 比如,一个insert语句结束后,获得自动生成的id,可以临时的设置到一个参数中,作为另一个语句
	 * 的输入参数
	 * 
	 * 该类就是描述这种情景的信息
	 * @author Jason Zhu
	 *
	 */
	public static class ParamSetter
	{
		String paramName = null ;
		int resultTableRow = -1 ;
		int resultTableCol = -1 ;
		
		public ParamSetter(String attrv)
		{
			attrv = attrv.trim();
			int p = attrv.indexOf('=');
			if(p<=0)
				throw new IllegalArgumentException("set_param must like @paramn=(0,0) but it is ="+attrv);
			
			paramName = attrv.substring(0,p);
			String tmps = attrv.substring(p+1).trim();
			StringTokenizer st = new StringTokenizer(tmps,"(,)");
			if(st.countTokens()!=2)
				throw new IllegalArgumentException("set_param must like @paramn=(0,0)");
			
			resultTableRow = Integer.parseInt(st.nextToken());
			resultTableCol = Integer.parseInt(st.nextToken());
		}
		
		public String getParamName()
		{
			return paramName ;
		}
		
		public int getResultTableRow()
		{
			return resultTableRow ;
		}
		
		public int getResultTableCol()
		{
			return resultTableCol ;
		}
	}
	
	private List<ParamSetter> paramSetters = null ;
	
	/**
	 * 如果存在,则运行sql前会判定是否满足条件,如果不满足,则不运行对应的sql
	 */
	//private BoolExp ifBoolExp = null ;
	
	protected void parseEle(Element sqlele) throws Exception
	{
		String setp = sqlele.getAttribute(Gdb.ATTR_SET_PARAM);
		if(setp!=null&&!setp.equals(""))
		{
			StringTokenizer tmpst = new StringTokenizer(setp,"|");
			
			paramSetters = new ArrayList<ParamSetter>() ;
			while(tmpst.hasMoreTokens())
			{
				paramSetters.add(new ParamSetter(tmpst.nextToken()));
			}
		}
		
//		String ifstr = sqlele.getAttribute(Gdb.ATTR_IF);
//		if(ifstr!=null&&!ifstr.equals(""))
//		{
//			ifBoolExp = new BoolExp(ifstr);
//		}
	}
	
	/**
	 * 判断该操作项是否修改数据,如数据库的插入更新删除都是
	 * 它会影响到运行时是否需要使用事务控制
	 * @return
	 */
	public abstract boolean isChangedData() ;
	
	public final List<ParamSetter> getParamSetters()
	{
		return paramSetters ;
	}
	
//	public final BoolExp getIfBoolExp()
//	{
//		return ifBoolExp ;
//	}
}
