package org.iottree.core.basic;

import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.ILang;
import org.json.JSONObject;

public class ValTransCalc extends ValTranser implements ILang
{
	public static final String NAME = "calc";
	
	//add, subtract, multiply and divide
	public static final int OP_ADD = 1 ;
	public static final int OP_SUBS = 2 ;
	public static final int OP_MULTI = 3 ;
	public static final int OP_DIV = 4 ;
	
	int op = OP_MULTI ;
	double op_v = 1.0 ;
	
	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public String getTitle()
	{
		return g(NAME);//"Simple Calc";
	}

	@Override
	public Object transVal(Object v) throws Exception
	{
		if(v==null || !(v instanceof Number))
			return null ;
		Number nv=  (Number)v ;
		double raw_v = nv.doubleValue() ;
		double rv ;
		switch(op)
		{
		case OP_MULTI:
			rv = raw_v*op_v ;
			break ;
		case OP_ADD:
			rv = raw_v+op_v ;
			break ;
		case OP_SUBS:
			rv = raw_v-op_v ;
			break ;
		case OP_DIV:
			rv = raw_v/op_v ;
			break ;
		default:
			return null ;
		}
		
		ValTP tvtp = this.getTransValTP() ;
		if(tvtp!=null)
			return UAVal.transStr2ObjVal(tvtp, ""+rv) ;
		return rv ;
	}

	@Override
	public Object inverseTransVal(Object v) throws Exception
	{
		throw new Exception("no support") ;
//		if(v==null || !(v instanceof Number))
//			return null ;
//		Number nv=  (Number)v ;
//		if(nv.doubleValue()==0.0)
//			return null ;
//		double raw_v = nv.doubleValue() ;
//		double rv = raw_v/multiV ;
//		ValTP tvtp = this.getTransValTP() ;
//		if(tvtp!=null)
//			return UAVal.transStr2ObjVal(tvtp, ""+rv) ;
//		return rv ;
	}
	

	@Override
	public JSONObject toTransJO()
	{
		JSONObject ret = super.toTransJO();
		ret.put("op", this.op) ;
		ret.put("op_v", this.op_v) ;
		return ret;
	}
	
	@Override
	public boolean fromTransJO(JSONObject m)
	{
		boolean r = super.fromTransJO(m);
		this.op = m.optInt("op", OP_MULTI) ;
		this.op_v = m.optDouble("op_v", 1.0);
		return r;
	}
}
