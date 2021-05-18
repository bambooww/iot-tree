package org.iottree.core.basic;

/**
 * value checker,
 * used to prop value or other value setup checker.
 * @author jason.zhu
 *
 */
public interface ValChker<T>
{
	public boolean checkVal(T v,StringBuilder failedr) ;
}

//class ValChkerRange<T extends Number> implements ValChker<T>
//{
//	T stVal,etVal;
//	
//	public ValChkerRange(T st,T et)
//	{
//		stVal = st ;
//		etVal = et ;
//	}
//
//	@Override
//	public boolean checkVal(T v, StringBuilder failedr)
//	{
//		if(v.<stVal)
//			return false;
//		return false;
//	}
//}
