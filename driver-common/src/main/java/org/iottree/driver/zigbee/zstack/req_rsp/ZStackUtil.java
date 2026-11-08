package org.iottree.driver.zigbee.zstack.req_rsp;

import java.util.Arrays;
import java.util.List;

import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.iottree.driver.zigbee.zcl.ZCLFrmClusterCmd;
import org.iottree.driver.zigbee.zstack.Msg;
import org.iottree.driver.zigbee.zstack.MsgData;
import org.iottree.driver.zigbee.zstack.MsgDataReq;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.iottree.driver.zigbee.zstack.ZStackManager;
import org.iottree.driver.zigbee.zstack.types.AFConsts;
import org.iottree.driver.zigbee.zstack.types.AddressMode;
import org.iottree.driver.zigbee.zstack.types.ZnpCmdStatus;

public class ZStackUtil
{
	private static ILogger log = LoggerManager.getLogger(ZStackUtil.class) ;
	
	public static MsgRecv request(ZStackManager zsm,Msg.SubSys subsys,int mt_cmd1_id,byte[] pld)
		throws Exception
	{
		MsgDataReq req = new MsgDataReq(subsys,mt_cmd1_id,pld) ;
		ReqRspFlw rrf = new ReqRspFlw(req,false) ;
		StringBuilder failedr = new StringBuilder() ;
		if(!zsm.RT_synReqRsp(rrf,failedr))
			return null ;
		return rrf.getRsp() ;
	}
	
	public static MsgRecv request(ZStackManager zsm,MsgData req)
			throws Exception
		{
			ReqRspFlw rrf = new ReqRspFlw(req,false) ;
			StringBuilder failedr = new StringBuilder() ;
			if(!zsm.RT_synReqRsp(rrf,failedr))
				return null ;
			return rrf.getRsp() ;
		}

	public static ZnpCmdStatus sendDataRequest(ZStackManager zsm,int dst_addr,int dst_endpoint,
			int src_endpoint,int cluster_id,int options,int radius,byte[] last_data) throws Exception
	{
		DataReqConfirm_CMD rr = DataReqConfirm_CMD.create(zsm,dst_addr,dst_endpoint,
				src_endpoint,cluster_id,options,radius, last_data);
		
		StringBuilder failedr = new StringBuilder() ;
		if(!zsm.RT_synReqRsp(rr,failedr))
			return null ;
		return rr.getZnpCmdStatus() ;
	}
	

    public static boolean sendDataRequestExt(ZStackManager zsm,
    		AddressMode addr_mode,
        Object dst_addr_or_groupid, //destinationAddressOrGroupID: number | string,
        int dst_ep, //destinationEndpoint: number,
        int pan_id, //: number,
        int src_ep, //sourceEndpoint: number,
        int cluster_id, //clusterID: number,
        int radius, //radius: number,
        byte[] data , //: Buffer,
        int timeout //: number,
        //boolean confirmation
    ) throws Exception //: Promise<ZpiObject | undefined>
    {
    	StringBuilder failedr = new StringBuilder() ;
    	DataReqExtConfirm_CMD rr = DataReqExtConfirm_CMD.createClusterCmdReqRsp(zsm,
    			addr_mode,dst_addr_or_groupid,dst_ep,pan_id,
    			src_ep,cluster_id,
    			0, //int options,
    			radius,data) ;
    	rr.asTimeout(timeout) ;
    	if(!zsm.RT_synReqRsp(rr,failedr))
			return false ;

    	return rr.getConfirmStatus() ;
    }

	
	public static boolean dataRequestExtended(ZStackManager zsm,
			AddressMode addr_mode,Object dst_addr_or_groupid, //destinationAddressOrGroupID: number | string,
	        int dst_ep, //destinationEndpoint: number,
	        int pan_id, //: number,
	        int src_ep, //sourceEndpoint: number,
	        int cluster_id, //clusterID: number,
	        int radius, //radius: number,
	        byte[] data , //: Buffer,
	        int timeout, //: number,
	        boolean confirmation,
	        int attemptsLeft) throws Exception // 5)
	{
		for(int i = 0 ; i < attemptsLeft ; i ++)
		{
			boolean r = sendDataRequestExt(zsm,addr_mode,
			        dst_addr_or_groupid, //destinationAddressOrGroupID: number | string,
			        dst_ep, //destinationEndpoint: number,
			        pan_id, //: number,
			        src_ep, //sourceEndpoint: number,
			        cluster_id, //clusterID: number,
			        radius, //radius: number,
			        data , //: Buffer,
			        timeout //: number,
			        //confirmation
			    );
			if(r)
				return true ;
		}
		return false;
	}
	

    
    public static class AssocRestore
    {
    	public String ieeeadr ;
    	public int nwkaddr ;
    	public int noderelation ;
    }
    

    public static List<ZnpCmdStatus> recoverableErrors = Arrays.asList(
            ZnpCmdStatus.NWK_NO_ROUTE,
            ZnpCmdStatus.MAC_NO_ACK,
            ZnpCmdStatus.MAC_CHANNEL_ACCESS_FAILURE,
            ZnpCmdStatus.MAC_TRANSACTION_EXPIRED,
            ZnpCmdStatus.BUFFER_FULL,
            ZnpCmdStatus.MAC_NO_RESOURCES
        );

}
