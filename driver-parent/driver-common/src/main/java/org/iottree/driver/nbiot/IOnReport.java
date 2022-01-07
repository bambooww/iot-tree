package org.iottree.driver.nbiot;

import java.util.List;

import org.iottree.driver.nbiot.msg.WMMsg;
import org.iottree.driver.nbiot.msg.WMMsgReport;

public interface IOnReport
{
	public List<WMMsg> onMsgReport(WMMsgReport report) ;
}
