package org.iottree.core.msgnet.store;

import java.util.List;

import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;

/**
 * 历史数据分析器
 * 
 * @author zzj
 */
public abstract class HisAna
{
	public HisAna()
	{
	}
	
	public abstract String getAnaTp() ;
	
	public abstract String getAnaTpTitle() ;

	/**
	 * 判断是否支持多个标签
	 * @return
	 */
	public int supportAnaTagNum()
	{
		return 1;
	}
	
	public abstract List<ValTP> getAnaTagValTps(int idx) ;
	
	/**
	 * 获取第几个Tag对应的描述——可以认为是帮助
	 * @param idx
	 * @return
	 */
	public abstract String getAnaTagDesc(int idx) ;
	
	public abstract ValTP getAnaResultValTP() ;
	
	public abstract Object calcAnaResult() ;
	
}
