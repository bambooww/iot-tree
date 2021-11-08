package org.iottree.driver.opc.da;

import java.util.Collection;
import java.util.concurrent.Executors;

import org.openscada.opc.dcom.list.ClassDetails;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.list.*;

public class Test
{
	public static void main(String[] args) throws Exception
	{
		ConnectionInformation ci = new ConnectionInformation();
		ci.setHost("localhost");
		ci.setDomain("");
		ci.setUser("zzj");
		ci.setPassword("zhijun1090");
		ci.setClsid("7BC0CC8E-482C-47CA-ABDC-0FE7F9C6E729");
		Server server = new Server(ci, Executors.newSingleThreadScheduledExecutor());
		server.connect();

		// 或以下获取OPCEnum方法
		ServerList serverList = new ServerList("localhost", "zzj", "zhijun1090", "");
		Collection<ClassDetails> classDetails = serverList.listServersWithDetails(
				new Category[] { Categories.OPCDAServer10, Categories.OPCDAServer20, Categories.OPCDAServer30 },
				new Category[] {});
		for (ClassDetails cds : classDetails)
		{
			System.out.println(cds.getProgId() + "=" + cds.getDescription());
		}
	}

}
