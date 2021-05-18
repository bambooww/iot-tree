package org.iottree.driver.common;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.iottree.core.UAVal.ValTP;

public abstract class SimulatorFunc
{
	/**
	 * create function instance
	 * @param func
	 * @param params
	 * @param failedr
	 * @return
	 */
	public static SimulatorFunc createFunc(String func,List<Object> params,StringBuilder failedr)
	{
		SimulatorFunc ret = null ;
		
		switch(func)
		{
		case SimFuncRandom.NAME:
			ret = new SimFuncRandom() ;
			if(!ret.setParams(params, failedr))
				return null ;
			return ret ;
		case SimFuncSine.NAME:
			ret = new SimFuncSine() ;
			if(!ret.setParams(params, failedr))
				return null ;
			return ret ;
		default:
			failedr.append("unknown function name") ;
			return null ;
		}
	}
	
	List<Object> params = null ;
	
	private transient Object curVal = null ;
	
	private transient long lastDT =-1 ;
	
	public SimulatorFunc()
	{}
	
	
	public List<Object> getParams()
	{
		return params ;
	}
	
	/**
	 * chech parameters ok or not
	 * @param parms
	 * @param failedr
	 * @return
	 */
	protected abstract boolean setParams(List<Object> parms,StringBuilder failedr) ;
	
	/**
	 * function name
	 * @return
	 */
	public abstract String getName() ;
	
	
	
	
	/**
	 * check val tp if fit or not
	 * @param vtp
	 * @return
	 */
	public boolean checkValTp(ValTP vtp)
	{
		List<ValTP> tps = getFitValTps() ;
		if(tps==null)
			return false;
		return tps.contains(vtp) ;
	}
	
	/**
	 * get all support val tps
	 * @return
	 */
	public abstract List<ValTP> getFitValTps() ;
	
	public abstract int getRunRate() ;
	/**
	 * 
	 * @return
	 */
	protected abstract Object calculateNextVal(ValTP vtp) ;
	
	public Object getValWithRunByRate(ValTP vtp)
	{
		long rr = getRunRate() ;
		if(rr<=0)
			return rr;
		long st = System.currentTimeMillis() ;
		if(st-lastDT>rr)
		{
			try
			{
				curVal = calculateNextVal(vtp) ;
			}
			finally
			{
				lastDT = st ;
			}
		}
		return curVal ;
	}
	

	protected Integer parseToInt(Object p1)
	{
		if(p1 instanceof Number)
			return ((Number)p1).intValue() ;
		
		if(p1 instanceof String)
		{
			try
			{
				return Integer.parseInt((String)p1) ;
			}
			catch(Exception e)
			{
				return null ;
			}
		}
		return null ;
	}
	

	protected Double parseToDouble(Object p1)
	{
		if(p1 instanceof Number)
			return ((Number)p1).doubleValue() ;
		
		if(p1 instanceof String)
		{
			try
			{
				return Double.parseDouble((String)p1) ;
			}
			catch(Exception e)
			{
				return null ;
			}
		}
		return null ;
	}
	
}


class SimFuncRandom extends SimulatorFunc
{
	int rate = -1 ;
	
	int lowLimit ;
	
	int highLimit ;
	
	Random rand = null ;
	
	/**
	 * func name
	 */
	public static final String NAME = "random" ;
	
	static List<ValTP> VAL_TPS = Arrays.asList(ValTP.vt_int16,ValTP.vt_int32,ValTP.vt_float,ValTP.vt_double) ;
	
	@Override
	public String getName()
	{
		return NAME ;
	}
	
	protected boolean setParams(List<Object> parms,StringBuilder failedr)
	{
		if(parms.size()<3)
		{
			failedr.append("random(Rate, Low Limit, High Limit)") ;
			return false;
		}
		Object p1 = parms.get(0) ;
		Integer rate = parseToInt(p1) ;
		Integer lowLimit = parseToInt(parms.get(1)) ;
		Integer highLimit = parseToInt(parms.get(2)) ;
		if(rate==null)
		{
			failedr.append("unknown first param: "+p1) ;
			return false;
		}
		if(lowLimit==null||highLimit==null)
		{
			failedr.append("lowLimit or highLimit cannot find") ;
			return false;
		}
		this.rate = rate ;
		this.lowLimit = lowLimit;
		this.highLimit = highLimit;
		rand = new Random() ;
		
		return true;
	}

	@Override
	public List<ValTP> getFitValTps()
	{
		return VAL_TPS;
	}
	
	public int getRunRate()
	{
		return this.rate ;
	}

	@Override
	protected Object calculateNextVal(ValTP vtp)
	{
		if(rand==null)
			return null ;
		switch(vtp)
		{
		case vt_int16:
			return (short)rand.nextInt(highLimit)%(highLimit-lowLimit+1) + lowLimit;
		case vt_int32:
			return rand.nextInt(highLimit)%(highLimit-lowLimit+1) + lowLimit;
		case vt_float:
			float v = rand.nextFloat() ;
			if(lowLimit==0.0)
				return highLimit*v ;
			else
				return (highLimit-lowLimit)*v+lowLimit ;
		case vt_double:
			double d = rand.nextDouble();
			if(lowLimit==0.0)
				return highLimit*d ;
			else
				return (highLimit-lowLimit)*d+lowLimit ;
		default: 
			 return null;
		}
	}
	
}


class SimFuncSine extends SimulatorFunc
{
	/**
	 * 10 -  1000
	 */
	int rate = -1 ;
	
	double lowLimit ;
	
	double highLimit ;
	
	/**
	 * 0.001 -  5
	 */
	float freq ;
	
	/**
	 * 0.0 - 360.0
	 */
	float phase ;
	
	/**
	 * (hight-low)/2
	 */
	double A  ;
	
	/**
	 * （high+low)/2
	 */
	double B ;
	
	/**
	 * x value step,every get val
	 */
	double DX ;
	/**
	 * func name
	 */
	public static final String NAME = "sine" ;
	
	static List<ValTP> VAL_TPS = Arrays.asList(ValTP.vt_float,ValTP.vt_double) ;
	
	
	private transient double curX = 0 ;
	
	@Override
	public String getName()
	{
		return NAME ;
	}
	
	
	protected boolean setParams(List<Object> parms,StringBuilder failedr)
	{
		if(parms.size()<5)
		{
			failedr.append("sine(Rate, Low Limit, High Limit, Frequency, Phase)") ;
			return false;
		}
		Object p1 = parms.get(0) ;
		Integer rate = parseToInt(p1) ;
		Double lowLimit = parseToDouble(parms.get(1)) ;
		Double highLimit = parseToDouble(parms.get(2)) ;
		Double freq = parseToDouble(parms.get(3)) ;
		Double phase = parseToDouble(parms.get(4)) ;
		if(rate==null)
		{
			failedr.append("unknown first param: "+p1) ;
			return false;
		}
		if(lowLimit==null||highLimit==null)
		{
			failedr.append("lowLimit or highLimit cannot find") ;
			return false;
		}
		if(freq==null||phase==null)
		{
			failedr.append("freq or phase cannot find") ;
			return false;
		}
		
		this.rate = rate ;
		this.lowLimit = lowLimit;
		this.highLimit = highLimit;
		this.freq = freq.floatValue() ;
		this.phase = (float)(phase.floatValue()/Math.PI/2);
		
		this.A = (this.highLimit-this.lowLimit)/2 ;
		this.B = (this.highLimit+this.lowLimit)/2 ;
		this.DX = (Math.PI*2/this.freq)*this.rate/1000 ;
		return true;
	}

	@Override
	public List<ValTP> getFitValTps()
	{
		return VAL_TPS;
	}
	
	public int getRunRate()
	{
		return this.rate ;
	}

	
	@Override
	protected Object calculateNextVal(ValTP vtp)
	{
		if(rate<=0)
			return null;
	
		final double PI2 = Math.PI*2/this.freq ;
		Double v = A*Math.sin(this.freq*curX+phase)+B ;
		curX += this.DX ; 
		if(curX>PI2)
			curX %= PI2 ;
		switch(vtp)
		{
		case vt_float:
			return v.floatValue() ;
		case vt_double:
			return v ;
		default: 
			 return null;
		}
	}
	
}