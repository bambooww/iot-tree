package org.iottree.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.iottree.core.UAVal.ValTP;

public class UAValList
{
	private LinkedList<UAVal> vals = new LinkedList<>();

	private int maxNum = 14;

	public UAValList(int maxnum)
	{
		this.maxNum = maxnum;
	}

	public int getValNum()
	{
		return vals.size();
	}
	
	public synchronized void setMaxNum(int n)
	{
		if(n<=0)
			return ;
		
		if(n>=this.maxNum)
		{
			this.maxNum = n ;
			return ;
		}
		
		if(vals.size()>n)
		{
			int k = vals.size()-n ;
			for(int i = 0 ; i < k ; i ++)
				vals.removeFirst() ;
		}
	}

	public synchronized void addVal(UAVal v)
	{
		vals.addLast(v);
		if (vals.size() > this.maxNum)
			vals.removeFirst();
	}

	public synchronized List<UAVal> getAllAndClear()
	{
		LinkedList<UAVal> r = vals;
		vals = new LinkedList<>();
		return r;
	}

	public List<UAVal> getVals(long lastdt)
	{
		ArrayList<UAVal> rets = new ArrayList<>();
		for (UAVal v : vals)
		{
			if (lastdt > 0 && lastdt >= v.getValDT())
				continue;
			rets.add(v);
		}
		return rets;
	}

	public UAVal getValLast()
	{
		if (vals.size() <= 0)
			return null;
		return vals.getLast();
	}

	// public List<UAVal> getValsLast(int max_last_num)
	// {
	// int n = vals.size() ;
	// if(n<=0)
	// return null ;
	//
	//
	// }
	/**
	 * Anti-interference filter 3-14
	 */
	public UAVal filterValByAntiInterference(UATag tag)
	{
		ValTP vt = tag.getValTp();
		if (!vt.isNumberVT())
			return null;
		int n = vals.size();
		if (n <= 2)
			return null;
		boolean bfloat = vt.isNumberFloat();

		UAVal lastv = vals.get(n - 1);
		// 连续3个invalid-输出invalid
		for (int i = 1; i <= 3; i++)
		{
			UAVal v = vals.get(n - i);
			if (v.isValid())
				break;

			if (i == 3)
				return lastv; // out last invalid
		}

		String strv = null ;
		if (bfloat)
			strv = filterDouble()+"";
		else
			strv = filterLong()+"";

		Object obv = UAVal.transStr2ObjVal(vt,strv) ;
		//if(lastv.isValid() && lastv.getObjVal()
		UAVal rv = lastv.copyMe();
		rv.setVal(true, obv, rv.getValDT());
		//UAVal.createByStrVal(vt, strv, valdt, val_chgdt)
		return rv;
	}

	private double filterDouble()
	{
		int n = vals.size();

		int min_idx = n - 14;
		if (min_idx < 0)
			min_idx = 0;

		// 只要有无效值，则删除，计算剩余有效数字的平均值
		//  否则，删除一个最大最小值，计算剩余数字平均值
		int invalid_n = 0;
		//ArrayList<UAVal> tmps = new ArrayList<>();
		double min_v = Double.MAX_VALUE;
		double max_v = Double.MIN_VALUE;
		int valid_n = 0;
		double sum = 0;
		for (int i = n - 1; i >= min_idx; i--)
		{
			UAVal v = vals.get(i);
			if (!v.isValid())
			{
				invalid_n++;
				//if (invalid_n <= 2)
				//	continue;
				//tmps.add(v);
				continue;
			}

			valid_n++;
			Number nb = (Number) v.getObjVal();
			double dv = nb.doubleValue();
			if (dv > max_v)
				max_v = dv;
			if (dv < min_v)
				min_v = dv;
			sum += dv;
		}
		
		if(invalid_n>0 || valid_n<=2)
			return sum/valid_n ;
		
		sum -= max_v;
		sum -= min_v ;
		return sum/(valid_n-2) ;
	}

	private long filterLong()
	{
		int n = vals.size();

		int min_idx = n - 14;
		if (min_idx < 0)
			min_idx = 0;

		// 只要有无效值，则删除，计算剩余有效数字的平均值
		//  否则，删除一个最大最小值，计算剩余数字平均值
		int invalid_n = 0;
		//ArrayList<UAVal> tmps = new ArrayList<>();
		long min_v = Long.MAX_VALUE;
		long max_v = Long.MIN_VALUE;
		int valid_n = 0;
		long sum = 0;
		for (int i = n - 1; i >= min_idx; i--)
		{
			UAVal v = vals.get(i);
			if (!v.isValid())
			{
				invalid_n++;
				//if (invalid_n <= 2)
				//	continue;
				//tmps.add(v);
				continue;
			}

			valid_n++;
			Number nb = (Number) v.getObjVal();
			long dv = nb.longValue();
			if (dv > max_v)
				max_v = dv;
			if (dv < min_v)
				min_v = dv;
			sum += dv;
		}
		
		if(invalid_n>0 || valid_n<=2)
			return sum/valid_n ;
		
		sum -= max_v;
		sum -= min_v ;
		return sum/(valid_n-2) ;
	}
}
