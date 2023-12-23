package org.iottree.driver.common;

import java.util.List;

import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.DevDriver;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UATag;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.conn.ConnPtStream;

/**
 * 这是一个通用的基于链接的命令行指令JS驱动
 * 
 * 可以支持很多通过控制终端发送命令和接收命令的设备支持。这种设备一般一个通道只对应一个设备
 * 所以可以不需要下面的设备，可以直接通过驱动建立相关的标签数据。
 * 
 * 然后，在关联的ConnPtStream中，发送和接收指令数据
 * 
 * 为了方便以上的支持，所有的指令发送和处理都以JS方式实现。这样可以非常灵活方便的支持各种应用，而不需要为
 * 类似的设备提供单独的驱动和配置。
 * 
 * JS要支持如下内容，
 * 1 启动初始化时，JS可以在对应的通道下面建立相关的标签。
 * 2 在ConnPt链接成功时，JS可以做一些工作，如发送开始指令
 * 3 在ConnPt链接断开时，JS可以做一些工作。如设置相关的标签数据为valid=false
 * 4 在定时运行的中，发送相关的同步或异步指令
 * 5 当没有同步指令时，接收到某个数据行，产生回调处理。
 * 
 * TODO 此驱动可以支持脚本完成之后的保存。
 * 
 * 
 * @author jason.zhu
 *
 */
public class CmdLineJSDrv extends DevDriver
{
	@Override
	public boolean checkPropValue(String groupn, String itemn, String strv, StringBuilder failedr)
	{
		return true;
	}

	@Override
	public DevDriver copyMe()
	{
		return new CmdLineJSDrv();
	}

	@Override
	public String getName()
	{
		return "cmd_ln_js";
	}

	@Override
	public String getTitle()
	{
		return "Cmd Line JS Handler";
	}

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtStream.class ;
	}

	@Override
	public boolean supportDevFinder()
	{
		return false;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevDef()
	{
		return null;
	}

	@Override
	public List<PropGroup> getPropGroupsForCh(UACh ch)
	{
		return null;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevInCh(UADev d)
	{
		return null;
	}

	@Override
	public DevAddr getSupportAddr()
	{
		return null;
	}

	@Override
	protected void RT_onConnReady(ConnPt cp, UACh ch, UADev dev)
	{
		
	}

	@Override
	protected void RT_onConnInvalid(ConnPt cp, UACh ch, UADev dev)
	{
		
	}

	@Override
	protected boolean RT_runInLoop(UACh ch, UADev dev, StringBuilder failedr) throws Exception
	{
		return false;
	}

	@Override
	public boolean RT_writeVal(UACh ch,UADev dev,UATag tag, DevAddr da, Object v)
	{
		return false;
	}

	@Override
	public boolean RT_writeVals(UACh ch,UADev dev,UATag[] tags, DevAddr[] da, Object[] v)
	{
		return false;
	}

}
