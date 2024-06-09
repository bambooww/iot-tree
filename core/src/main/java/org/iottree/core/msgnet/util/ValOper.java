package org.iottree.core.msgnet.util;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.iottree.core.msgnet.MNCxtValSty;
import org.iottree.core.util.ILang;
import org.json.JSONArray;

public abstract class ValOper implements ILang
{
	public static final ValOper[] ALL = new ValOper[] { new VO_Equ(),new VO_NotEqu(),
			new VO_Lt(),new VO_LtEqu(),new VO_Gt(),new VO_GtEqu(),new VO_HasKey(),
			new VO_Between(),new VO_Contains(),new VO_Regex(),new VO_IsTrue(),new VO_IsFalse(),
			new VO_IsNull(),new VO_IsNotNull(),new VO_IsType(),new VO_IsEmpty(),new VO_IsNotEmpty()};

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
	
	public boolean isPm2CanEmpty()
	{
		return false;
	}
	
	public abstract boolean isNeedPm3() ;
	
	public boolean isPm3CanEmpty()
	{
		return false;
	}

	 public abstract boolean checkMath(Object pm1, Object pm2,Object pm3);
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
	
	public boolean isPm2CanEmpty()
	{
		return true;
	}
	
	@Override
	public boolean isNeedPm3()
	{
		return false;
	}

	@Override
	public boolean checkMath(Object pm1, Object pm2,Object pm3)
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
	
	public boolean isPm2CanEmpty()
	{
		return true;
	}
	
	@Override
	public boolean isNeedPm3()
	{
		return false;
	}

	@Override
	public boolean checkMath(Object pm1, Object pm2,Object pm3)
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
	public boolean isNeedPm3()
	{
		return false;
	}

	@Override
	public boolean checkMath(Object pm1, Object pm2,Object pm3)
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
	public boolean isNeedPm3()
	{
		return false;
	}

	@Override
	public boolean checkMath(Object pm1, Object pm2,Object pm3)
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
	public boolean isNeedPm3()
	{
		return false;
	}

	@Override
	public boolean checkMath(Object pm1, Object pm2,Object pm3)
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
	public boolean isNeedPm3()
	{
		return false;
	}

	@Override
	public boolean checkMath(Object pm1, Object pm2,Object pm3)
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

class VO_HasKey extends ValOper
{

	public VO_HasKey() // (ValAlert va)
	{

	}

	@Override
	public String getName()
	{
		return "has_key";
	}
	
	@Override
	public boolean isNeedPm2()
	{
		return true;
	}
	@Override
	public boolean isNeedPm3()
	{
		return false;
	}

	@Override
	public boolean checkMath(Object pm1, Object pm2,Object pm3)
	{
		if(pm1==null || pm2==null)
			return false;
		if(!(pm1 instanceof Map))
			return false;
		return ((Map)pm1).containsKey(pm2) ;
	}
}

class VO_Between extends ValOper
{
	public VO_Between() // (ValAlert va)
	{

	}

	@Override
	public String getName()
	{
		return "between";
	}
	
	@Override
	public boolean isNeedPm2()
	{
		return true;
	}
	@Override
	public boolean isNeedPm3()
	{
		return true;
	}

	@Override
	public boolean checkMath(Object pm1, Object pm2,Object pm3)
	{
		if(pm1==null || pm2==null ||pm3==null)
			return false;
		if(pm1 instanceof Number && pm2 instanceof Number && pm3 instanceof Number)
		{
			return ((Number)pm1).doubleValue() >= ((Number)pm2).doubleValue() &&
					((Number)pm1).doubleValue() <= ((Number)pm3).doubleValue();
		}
		
		if(pm1 instanceof String && pm2 instanceof String && pm3 instanceof String)
			return ((String)pm1).compareTo((String)pm2)>=0 &&  ((String)pm1).compareTo((String)pm3)<=0  ;
		throw new RuntimeException("val cannot be compared,pm1="+pm1.getClass() +" pm2="+pm2.getClass()+" pm3="+pm3.getClass()) ;
	}
}

class VO_Contains extends ValOper
{
	public VO_Contains() // (ValAlert va)
	{

	}

	@Override
	public String getName()
	{
		return "contains";
	}
	
	@Override
	public boolean isNeedPm2()
	{
		return true;
	}
	@Override
	public boolean isNeedPm3()
	{
		return false;
	}

	@Override
	public boolean checkMath(Object pm1, Object pm2,Object pm3)
	{
		if(pm1==null || pm2==null)
			return false;
		if(pm1 instanceof String && pm2 instanceof String)
		{
			String pm1s = (String)pm1 ;
			return pm1s.indexOf((String)pm2)>=0 ;
		}
		if(pm1 instanceof List<?>)
		{
			return ((List<?>)pm1).contains(pm2) ;
		}
		if(pm1 instanceof JSONArray)
		{
			JSONArray jarr = (JSONArray)pm1 ;
			return jarr.toList().contains(pm2) ;
		}
		
		throw new RuntimeException("val cannot be compared,pm1="+pm1.getClass() +" pm2="+pm2.getClass()) ;
	}
}

class VO_Regex extends ValOper
{
	public VO_Regex() // (ValAlert va)
	{

	}

	@Override
	public String getName()
	{
		return "regex";
	}
	
	@Override
	public boolean isNeedPm2()
	{
		return true;
	}
	@Override
	public boolean isNeedPm3()
	{
		return false;
	}

	@Override
	public boolean checkMath(Object pm1, Object pm2,Object pm3)
	{
		if(pm1==null || pm2==null)
			return false;
		if(!(pm2 instanceof String))
			return false;
		return Pattern.matches((String)pm2, pm1.toString()) ;
	}
}

class VO_IsTrue extends ValOper
{
	public VO_IsTrue() // (ValAlert va)
	{

	}

	@Override
	public String getName()
	{
		return "is_true";
	}
	
	@Override
	public boolean isNeedPm2()
	{
		return false;
	}
	@Override
	public boolean isNeedPm3()
	{
		return false;
	}

	@Override
	public boolean checkMath(Object pm1, Object pm2,Object pm3)
	{
		if(pm1==null && !(pm1 instanceof Boolean))
			return false;
		return ((Boolean)pm1).booleanValue() ;
	}
}

class VO_IsFalse extends ValOper
{
	public VO_IsFalse() // (ValAlert va)
	{

	}

	@Override
	public String getName()
	{
		return "is_false";
	}
	
	@Override
	public boolean isNeedPm2()
	{
		return false;
	}
	@Override
	public boolean isNeedPm3()
	{
		return false;
	}

	@Override
	public boolean checkMath(Object pm1, Object pm2,Object pm3)
	{
		if(pm1==null && !(pm1 instanceof Boolean))
			return false;
		return !((Boolean)pm1).booleanValue() ;
	}
}

class VO_IsNull extends ValOper
{
	public VO_IsNull() // (ValAlert va)
	{

	}

	@Override
	public String getName()
	{
		return "is_null";
	}
	
	@Override
	public boolean isNeedPm2()
	{
		return false;
	}
	@Override
	public boolean isNeedPm3()
	{
		return false;
	}

	@Override
	public boolean checkMath(Object pm1, Object pm2,Object pm3)
	{
		return pm1==null;
	}
}

class VO_IsNotNull extends ValOper
{
	public VO_IsNotNull() // (ValAlert va)
	{

	}

	@Override
	public String getName()
	{
		return "not_null";
	}
	
	@Override
	public boolean isNeedPm2()
	{
		return false;
	}
	@Override
	public boolean isNeedPm3()
	{
		return false;
	}

	@Override
	public boolean checkMath(Object pm1, Object pm2,Object pm3)
	{
		return pm1!=null;
	}
}

class VO_IsType extends ValOper
{
	public static final String TP = "is_type" ;
	public VO_IsType() // (ValAlert va)
	{

	}

	@Override
	public String getName()
	{
		return TP;
	}
	
	@Override
	public boolean isNeedPm2()
	{
		return true;
	}
	@Override
	public boolean isNeedPm3()
	{
		return false;
	}

	@Override
	public boolean checkMath(Object pm1, Object pm2,Object pm3)
	{
		if(pm1==null || pm2==null)
			return false;
		
		if(pm2 instanceof Class)
		{
			Class<?> c = (Class<?>)pm2 ;
			return c.isAssignableFrom(pm1.getClass()) ;
		}
		
		if(pm2 instanceof MNCxtValSty)
		{
			MNCxtValSty vsty = (MNCxtValSty)pm2 ;
			return vsty.checkObjFit(pm1) ;
		}
		return false;
	}
}


class VO_IsEmpty extends ValOper
{
	public VO_IsEmpty() // (ValAlert va)
	{

	}

	@Override
	public String getName()
	{
		return "is_empty";
	}
	
	@Override
	public boolean isNeedPm2()
	{
		return false;
	}
	@Override
	public boolean isNeedPm3()
	{
		return false;
	}

	@Override
	public boolean checkMath(Object pm1, Object pm2,Object pm3)
	{
		return "".equals(pm1);
	}
}

class VO_IsNotEmpty extends ValOper
{
	public VO_IsNotEmpty() // (ValAlert va)
	{

	}

	@Override
	public String getName()
	{
		return "not_empty";
	}
	
	@Override
	public boolean isNeedPm2()
	{
		return false;
	}
	@Override
	public boolean isNeedPm3()
	{
		return false;
	}

	@Override
	public boolean checkMath(Object pm1, Object pm2,Object pm3)
	{
		return !"".equals(pm1);
	}
}

