package org.iottree.core.basic;

import java.util.HashMap;
import java.util.Map;

import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.ILang;
import org.json.JSONObject;

public class ValTransScaling extends ValTranser implements ILang
{
	public static final String NAME = "scaling";
	
	public static final int SCALING_LINEAR = 1;

	public static final int SCALING_SQUARE_ROOT = 2;
	
	int tp = SCALING_LINEAR;

	double rawLow = 0;
	double rawHigh = 1000;
	
	double scaledHigh = 1000;
	boolean scaledHighClamp = false;
	
	double scaledLow = 0 ;
	boolean scaledLowClamp = false;
	
	boolean scaledNegate = false;
	
	@Override
	public String getName()
	{
		return NAME;
	}
	@Override
	public String getTitle()
	{
		return g("scaling");
	}
	
	public int getScalingTp()
	{
		return tp ;
	}
	
	public double getRawLow()
	{
		return rawLow ;
	}
	
	public double getRawHigh()
	{
		return rawHigh ;
	}
	
	public double getScaledHigh()
	{
		return this.scaledHigh ;
	}
	
	public boolean isScaledHighClamp()
	{
		return scaledHighClamp ;
	}
	
	public double getScaledLow()
	{
		return this.scaledLow ;
	}
	
	public boolean isScaledLowClamp()
	{
		return this.scaledLowClamp ;
	}
	
	
	public boolean isScaledNegate()
	{
		return this.scaledNegate ;
	}
	
	@Override
	public Object transVal(Object v)
	{
		Number inval = (Number)v ;
		double raw_v = inval.doubleValue() ;
		double rv;
		switch(tp)
		{
		case SCALING_LINEAR:
			rv = (((this.scaledHigh - this.scaledLow)/(this.rawHigh - this.rawLow)) * (raw_v - this.rawLow)) + this.scaledLow ;
			break;
		case SCALING_SQUARE_ROOT:
			rv =Math.sqrt((raw_v-rawLow)/(rawHigh - rawLow)) * (scaledHigh - scaledLow) + scaledLow;
			break;
		default:
			return null ;
		}
		
		if(this.scaledHighClamp&&rv>this.scaledHigh)
			rv = this.scaledHigh ;
		if(this.scaledLowClamp&&rv<this.scaledLow)
			rv = this.scaledLow ;
		if(this.scaledNegate)
			rv = -rv ;
		ValTP tvtp = this.getTransValTP() ;
		if(tvtp!=null)
			return UAVal.transStr2ObjVal(tvtp, ""+rv) ;
		return rv ;
	}
	
	public Object inverseTransVal(Object v) throws Exception
	{
		Number inval = (Number)v ;
		double tv = inval.doubleValue() ;
		double raw_v ;
		if(this.scaledNegate)
			tv = -tv ;
		
		if(this.scaledHighClamp&&tv>this.scaledHigh)
			tv = this.scaledHigh ;
		if(this.scaledLowClamp&&tv<this.scaledLow)
			tv = this.scaledLow ;
		
		switch(tp)
		{
		case SCALING_LINEAR:
			 raw_v = (tv - this.scaledLow)/ ((this.scaledHigh - this.scaledLow)/(this.rawHigh - this.rawLow))  + this.rawLow  ;
			break;
		case SCALING_SQUARE_ROOT:
			double tmpv = (tv - scaledLow)/ (scaledHigh - scaledLow);
			raw_v = tmpv*tmpv*(rawHigh - rawLow)+rawLow ;
			break;
		default:
			return null ;
		}
		
		return raw_v;
	}
	
	@Override
	public JSONObject toTransJO()
	{
		JSONObject ret = super.toTransJO();
		
		ret.put("tp", this.tp) ;
		ret.put("raw_low", rawLow);
		ret.put("raw_high", rawHigh);
		
		ret.put("scaled_high", scaledHigh);
		ret.put("scaled_high_c", scaledHighClamp);
		
		ret.put("scaled_low", scaledLow);
		ret.put("scaled_low_c", scaledLowClamp);
		
		ret.put("scaled_neg", scaledNegate);
		
		return ret;
	}
	
	@Override
	public boolean fromTransJO(JSONObject m)
	{
		boolean r = super.fromTransJO(m);
		this.tp = m.optInt("tp", SCALING_LINEAR) ;
		rawLow = m.optDouble("raw_low", 0);
		rawHigh = m.optDouble("raw_high", 1000);
		
		scaledHigh = m.optDouble("scaled_high", 1000);
		scaledHighClamp = m.optBoolean("scaled_high_c", false);
		
		scaledLow = m.optDouble("scaled_low", 0);
		scaledLowClamp = m.optBoolean("scaled_low_c", false);
		
		scaledNegate = m.optBoolean("scaled_neg", false);
		return r;
	}
}
