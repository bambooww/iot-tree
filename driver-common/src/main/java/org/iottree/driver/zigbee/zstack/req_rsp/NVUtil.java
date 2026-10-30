package org.iottree.driver.zigbee.zstack.req_rsp;

import org.iottree.driver.zigbee.DataBuffer;
import org.iottree.driver.zigbee.zstack.ZStackManager;
import org.iottree.driver.zigbee.zstack.msg_sys.OsalNVItemInitSREQ;
import org.iottree.driver.zigbee.zstack.msg_sys.OsalNVItemInitSRSP;
import org.iottree.driver.zigbee.zstack.msg_sys.OsalNVLengthSREQ;
import org.iottree.driver.zigbee.zstack.msg_sys.OsalNVLengthSRSP;
import org.iottree.driver.zigbee.zstack.msg_sys.OsalNVReadExtSREQ;
import org.iottree.driver.zigbee.zstack.msg_sys.OsalNVReadExtSRSP;
import org.iottree.driver.zigbee.zstack.msg_sys.OsalNVWriteExtSREQ;
import org.iottree.driver.zigbee.zstack.msg_sys.OsalNVWriteExtSRSP;
import org.iottree.driver.zigbee.zstack.util.IPackable;

public class NVUtil
{

	/**
	 * 通用读取某个非易失性存储数据
	 * 
	 * @param zsm
	 * @param nv_item_id
	 * @param offset
	 * @param failedr
	 * @return
	 * @throws Exception
	 */
	public static byte[] readItem(ZStackManager zsm,int nv_item_id,int offset,StringBuilder failedr) throws Exception
	{
		OsalNVLengthSREQ req = new OsalNVLengthSREQ(nv_item_id) ;
		ReqRsp_CMD<OsalNVLengthSREQ,OsalNVLengthSRSP> rr = new ReqRsp_CMD<>(req,OsalNVLengthSRSP.class) ;
		if(!zsm.RT_synReqRsp(rr,failedr))
			return null ;
		
		OsalNVLengthSRSP len_rsp = rr.getRsp();
		if(len_rsp==null)
			return null ;
		int nvlen = len_rsp.getNVLength() ;
		if(nvlen<=0)
			return null ;
		DataBuffer buf = DataBuffer.alloc(nvlen) ;
		while(offset<nvlen)
		{
			OsalNVReadExtSREQ nvr_req = new OsalNVReadExtSREQ(nv_item_id,offset);
			ReqRsp_CMD<OsalNVReadExtSREQ,OsalNVReadExtSRSP> rd_rr = new ReqRsp_CMD<>(nvr_req,OsalNVReadExtSRSP.class) ;
			if(!zsm.RT_synReqRsp(rd_rr,failedr))
				return null ;
			OsalNVReadExtSRSP rd_rsp = rd_rr.getRsp() ;
			if(rd_rsp==null)
				return null ;
			byte[] bs = rd_rsp.getNVValue() ;
			buf.writeBuf(bs,0,bs.length) ;
			offset += bs.length ;
		}
		return buf.getWritten() ;
	}
	
	public static <T extends IPackable> T readItemPack(Class<T> c,ZStackManager zsm,int nv_item_id,int offset,StringBuilder failedr) throws Exception
	{
		byte[] bs = readItem(zsm,nv_item_id, offset, failedr);
		if(bs==null)
			return null ;
		T ob = c.getConstructor().newInstance() ;
		if(ob.unpackFrom(bs, 0, bs.length))
			return ob ;
		failedr.append("unpack error:"+c.getName()) ;
		return null ;
	}
	
	public static <T extends IPackable> boolean writeItemPack(ZStackManager zsm,int nv_item_id,IPackable pk,Boolean auto_init,StringBuilder failedr)
		throws Exception
	{
		return writeItem(zsm,nv_item_id,pk.packTo(),auto_init,failedr) ;
	}
	
	public static boolean writeItem(ZStackManager zsm,int nv_item_id,byte[] data,Boolean auto_init,StringBuilder failedr) throws Exception
	{
		if(auto_init==null)
			auto_init = true ;
		OsalNVLengthSREQ req = new OsalNVLengthSREQ(nv_item_id) ;
		ReqRsp_CMD<OsalNVLengthSREQ,OsalNVLengthSRSP> rr = new ReqRsp_CMD<>(req,OsalNVLengthSRSP.class) ;
		if(!zsm.RT_synReqRsp(rr,failedr))
		{
			failedr.append("OsalNVLength req rsp failed") ;
			return false ;
		}
		OsalNVLengthSRSP len_rsp = rr.getRsp();
		if(len_rsp==null)
		{
			failedr.append("OsalNVLength rsp failed") ;
			return false ;
		}
		int nvlen = len_rsp.getNVLength() ;
		boolean b_exists = nvlen > 0 ;
		
		int offset = 0 ;
		int len = data.length ;
		int left_len = len ; //
		
		if(!b_exists)
		{
			if (!auto_init) {
                failedr.append("Cannot write NV memory item which does not exist id="+nv_item_id);
                return false;
            }
			
			int init_len = len>240 ? 240:len ;
			OsalNVItemInitSREQ initreq = new OsalNVItemInitSREQ(nv_item_id,len,data,offset, init_len) ;
			ReqRsp_CMD<OsalNVItemInitSREQ,OsalNVItemInitSRSP> init_rr = new ReqRsp_CMD<>(initreq,OsalNVItemInitSRSP.class) ;
			if(!zsm.RT_synReqRsp(init_rr,failedr))
			{
				failedr.append("OsalNVItemInit req rsp failed") ;
				return false ;
			}
			OsalNVItemInitSRSP init_rsp = init_rr.getRsp();
			if(init_rsp==null)
			{
				failedr.append("OsalNVItemInit rsp failed - no rsp") ;
				return false ;
			}
			if(!init_rsp.isInitSucc())
			{
				failedr.append("OsalNVItemInit rsp failed status="+init_rsp.getStatusVal()) ;
				return false;
			}
			
			left_len -= init_len ;
			offset += init_len ;
		}
		
		while(left_len>0)
		{
			int w_len = left_len>240 ? 240 : left_len ;
			OsalNVWriteExtSREQ wreq = new OsalNVWriteExtSREQ(nv_item_id,offset,data,offset,w_len) ;
			ReqRsp_CMD<OsalNVWriteExtSREQ,OsalNVWriteExtSRSP> w_rr = new ReqRsp_CMD<>(wreq,OsalNVWriteExtSRSP.class) ;
			if(!zsm.RT_synReqRsp(w_rr,failedr))
			{
				failedr.append("OsalNVWriteExt req rsp failed") ;
				return false ;
			}
			OsalNVWriteExtSRSP w_rsp = w_rr.getRsp() ;
			if(w_rsp==null)
			{
				failedr.append("OsalNVWriteExt rsp failed - no rsp") ;
				return false ;
			}
			if(!w_rsp.getStatus())
			{
				failedr.append("OsalNVWriteExt rsp error status="+w_rsp.getStatusVal()) ;
				return false ;
			}
			
			left_len -= w_len ;
			offset += w_len ;
		}
		
		return true;
	}
	
	private static boolean checkEquals(byte[] bs1,byte[] bs2)
	{
		if(bs1==null || bs1.length==0)
		{
			if(bs2==null||bs2.length==0)
				return true;
			return false;
		}
		
		int len1 = bs1.length ;
		int len2 = bs2!=null?bs2.length:0 ;
		if(len1!=len2)
			return false;
		for(int i = 0 ; i < len1 ; i ++)
		{
			if(bs1[i]!=bs2[i])
				return false;
		}
		return true ;
	}
	/**
     * Determines whether NV item is different from provided data and updates if necessary.
     *
     * @param id NV item identifier.
     * @param data Desired NV item value.
     * @param autoInit Whether NV item should be automatically initialized if not present.
	 * @throws Exception 
     */
    public static boolean updateItem(ZStackManager zsm,int nv_item_id, byte[] data,StringBuilder failedr) throws Exception
    {
    	byte[] bs = readItem(zsm, nv_item_id, 0,failedr) ;
    	if(checkEquals(bs,data))
    		return true;
    	//writeItem(ZStackManager zsm,int nv_item_id,byte[] data,Boolean auto_init,StringBuilder failedr)
        return writeItem(zsm,nv_item_id, data, true,failedr);
    }

}
