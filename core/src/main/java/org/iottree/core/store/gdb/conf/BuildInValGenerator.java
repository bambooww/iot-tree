package org.iottree.core.store.gdb.conf;

import java.util.HashMap;

import org.iottree.core.store.gdb.conf.buildin.VGCurTimestamp;
import org.iottree.core.store.gdb.conf.buildin.VGRandomUUID;

/**
 * 由于不同的数据库，在定义数据库列时可能会使用缺省值，并且缺省值的产生会由
 * 内部方法提供。如sqlserver如果定义时间类型的列的缺省值可以引用getdata()函数
 * 而其他数据库使用其他函数。甚至很多数据库不支持这种数据库列的缺省值由动态函数
 * 提供。
 * 
 * 在此，gdb通过提供自己的内部函数来支持这种情况－－使得数据库设计时不需要使用数据库
 * 特定的东西，影响了移植性。
 * 
 * 缺点：如果在分布式环境里，使用这种情况要求一些缺省值的产生在不同的应用服务器里。而不同的
 * 应用服务器的时间值可能会有差异。在一些对时间很敏感的系统中不适合。－－这种情况下应该
 * 统一使用数据库中的时间值。
 * 
 * @author Jason Zhu
 */
public abstract class BuildInValGenerator
{
	private static HashMap<String,BuildInValGenerator> n2bi = new HashMap<String,BuildInValGenerator>();
	/**
	 * 根据名称获得内部值产生器
	 * @param vg_name
	 * @return
	 */
	public static BuildInValGenerator getBuildInVG(String vg_name)
	{
		return n2bi.get(vg_name);
	}
	
	private static void putVG(BuildInValGenerator vg)
	{
		n2bi.put(vg.getName(), vg);
	}
	
	static
	{
		putVG(new VGCurTimestamp());
		putVG(new VGRandomUUID());
	}
	
	
	public abstract String getName();
	
	public abstract Object getVal(String[] parms);
	
	public abstract String getDesc();
}
