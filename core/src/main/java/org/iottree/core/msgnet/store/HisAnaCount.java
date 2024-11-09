package org.iottree.core.msgnet.store;

import java.util.Arrays;
import java.util.List;

import org.iottree.core.UAVal.ValTP;

/**
 * 单个指标计数功能，如果开关量，则可以计数开次数或关次数
 * 如果是数值，可以设定计数阈值，并根据大小比较计数
 * 
 * @author zzj
 *
 */
public class HisAnaCount extends HisAna
{

	public HisAnaCount()
	{
	}

	@Override
	public String getAnaTp()
	{
		return "count";
	}

	@Override
	public String getAnaTpTitle()
	{
		return "计数";
	}

	@Override
	public List<ValTP> getAnaTagValTps(int idx)
	{
		return Arrays.asList(ValTP.vt_bool,ValTP.vt_float,ValTP.vt_int16,ValTP.vt_int32,ValTP.vt_int64);
	}

	@Override
	public String getAnaTagDesc(int idx)
	{
		return null;
	}

	@Override
	public ValTP getAnaResultValTP()
	{
		return null;
	}

	@Override
	public Object calcAnaResult()
	{
		return null;
	}

}
