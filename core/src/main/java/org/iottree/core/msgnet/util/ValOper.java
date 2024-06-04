package org.iottree.core.msgnet.util;

import org.iottree.core.util.ILang;

public abstract class ValOper implements ILang
{
	public static final ValOper[] ALL = new ValOper[] { new VO_Equ(),new VO_NotEqu(),
			new VO_Lt(),new VO_LtEqu(),new VO_Gt(),new VO_GtEqu()};

	public static ValOper getOperByName(String n)
	{
		for(ValOper vo : ALL)
		{
			if(vo.getName().equals(n))
				return vo ;
		}
		return null ;
	}

	public ValOper()
	{
	}

	public abstract String getName();

	public String getTitle()
	{
		return g("vo_"+this.getName()) ;//this.titleEn;
	}
	
	public abstract boolean isNeedPm2() ;

	 public abstract boolean checkMath(Object pm1, Object pm2);
}


class VO_Equ extends ValOper
{
	public VO_Equ()// (ValAlert va)
	{
	}

	@Override
	public String getName()
	{
		return "eq";
	}
	
	@Override
	public boolean isNeedPm2()
	{
		return true;
	}

	@Override
	public boolean checkMath(Object pm1, Object pm2)
	{
		if(pm1==null)
		{
			return pm2==null ;
		}
		return pm1.equals(pm2);
	}
}

class VO_NotEqu extends ValOper
{
	public VO_NotEqu()// (ValAlert va)
	{
	}

	@Override
	public String getName()
	{
		return "n_eq";
	}
	
	@Override
	public boolean isNeedPm2()
	{
		return true;
	}

	@Override
	public boolean checkMath(Object pm1, Object pm2)
	{
		if(pm1==null)
		{
			return pm2!=null ;
		}
		return !pm1.equals(pm2);
	}
}


class VO_Lt extends ValOper
{
	public VO_Lt() // (ValAlert va)
	{

	}

	@Override
	public String getName()
	{
		return "lt";
	}
	
	@Override
	public boolean isNeedPm2()
	{
		return true;
	}

	@Override
	public boolean checkMath(Object pm1, Object pm2)
	{
		if(pm1==null||pm2==null)
			return false;
		if(pm1 instanceof Number && pm2 instanceof Number)
		{
			return ((Number)pm1).doubleValue() < ((Number)pm2).doubleValue() ;
		}
		
		if(pm1 instanceof String && pm2 instanceof String)
			return ((String)pm1).compareTo((String)pm2)<0 ;
		throw new RuntimeException("val cannot be compared,pm1="+pm1.getClass() +" pm2="+pm2.getClass()) ;
	}
}

class VO_LtEqu extends ValOper
{
	public VO_LtEqu() // (ValAlert va)
	{

	}

	@Override
	public String getName()
	{
		return "lt_eq";
	}
	
	@Override
	public boolean isNeedPm2()
	{
		return true;
	}

	@Override
	public boolean checkMath(Object pm1, Object pm2)
	{
		if(pm1==null||pm2==null)
			return false;
		if(pm1 instanceof Number && pm2 instanceof Number)
		{
			return ((Number)pm1).doubleValue() <= ((Number)pm2).doubleValue() ;
		}
		
		if(pm1 instanceof String && pm2 instanceof String)
			return ((String)pm1).compareTo((String)pm2)<=0 ;
		throw new RuntimeException("val cannot be compared,pm1="+pm1.getClass() +" pm2="+pm2.getClass()) ;
	}
}



class VO_Gt extends ValOper
{
	public VO_Gt() // (ValAlert va)
	{

	}

	@Override
	public String getName()
	{
		return "gt";
	}
	
	@Override
	public boolean isNeedPm2()
	{
		return true;
	}

	@Override
	public boolean checkMath(Object pm1, Object pm2)
	{
		if(pm1==null||pm2==null)
			return false;
		if(pm1 instanceof Number && pm2 instanceof Number)
		{
			return ((Number)pm1).doubleValue() > ((Number)pm2).doubleValue() ;
		}
		
		if(pm1 instanceof String && pm2 instanceof String)
			return ((String)pm1).compareTo((String)pm2)>0 ;
		throw new RuntimeException("val cannot be compared,pm1="+pm1.getClass() +" pm2="+pm2.getClass()) ;
	}
}

class VO_GtEqu extends ValOper
{

	public VO_GtEqu() // (ValAlert va)
	{

	}

	@Override
	public String getName()
	{
		return "gt_eq";
	}
	
	@Override
	public boolean isNeedPm2()
	{
		return true;
	}

	@Override
	public boolean checkMath(Object pm1, Object pm2)
	{
		if(pm1==null||pm2==null)
			return false;
		if(pm1 instanceof Number && pm2 instanceof Number)
		{
			return ((Number)pm1).doubleValue() >= ((Number)pm2).doubleValue() ;
		}
		
		if(pm1 instanceof String && pm2 instanceof String)
			return ((String)pm1).compareTo((String)pm2)>=0 ;
		throw new RuntimeException("val cannot be compared,pm1="+pm1.getClass() +" pm2="+pm2.getClass()) ;
	}
}
