package org.iottree.driver.common;

import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.core.DevAddr;
import org.iottree.core.UADev;
import org.iottree.core.UAVal.ValTP;

/**
 * topic with has no wildcards + json value path
 * e.g
 *   topic  dev/out/p1  {"press":0.1,"temp":26.5,st:1} 
 *  dev/out/p1+press
 * @author zzj
 *
 */
public class MQTTClientAddr extends DevAddr 
{
	String topic = null ;
	
	List<String> jsonPath = null;
	
	MQTTClientAddr()
	{
		
	}
	
	MQTTClientAddr(String addr,ValTP vtp,String topic,String jsonpath)
	{
		super(addr,vtp) ;
		this.topic = topic ;
		 //= jsonpath ;
		 this.jsonPath = Convert.splitStrWith(jsonpath, "/\\") ;
	}
	
	/**
	 * topic+jsonpath
	 * 
	 * xxx/xx/xx+temp
	 * xxx/xx/xx+press
	 * 
	 * topic#payload
	 */
	@Override
	public DevAddr parseAddr(UADev dev,String str, ValTP vtp, StringBuilder failedr)
	{
		int idx = str.indexOf('+') ;
		if(idx<=0)
		{
			failedr.append("invalid addr="+str+",it must like xxx/xx/xx+temp") ;
			return null;
		}
		String topic = str.substring(0,idx) ;
		String jsonpath = str.substring(idx+1) ;
		if(Convert.isNullOrEmpty(jsonpath))
		{
			failedr.append("invalid addr="+str+",it must like xxx/xx/xx+temp") ;
			return null;
		}
		MQTTClientAddr ret = new MQTTClientAddr(str,vtp,topic,jsonpath) ;
		return ret;
	}
	
	public String getMQTTTopic()
	{
		return topic ;
	}
	
	public List<String> getPayloadJSONPath()
	{
		return jsonPath ;
	}

	@Override
	public boolean isSupportGuessAddr()
	{
		return false;
	}

	@Override
	public DevAddr guessAddr(UADev dev,String str,ValTP vtp)
	{
		return null;
	}

	@Override
	public List<String> listAddrHelpers()
	{
		return null;
	}
	
	//static ValTP[] supTPS = new ValTP[] {ValTP.vt_bool,ValTP.v} ;

	@Override
	public ValTP[] getSupportValTPs()
	{
		return null;
	}

	@Override
	public String toCheckAdjStr()
	{
		return null ;
	}

//	@Override
//	public int getRegPos()
//	{
//		return 0;
//	}
//
//	@Override
//	public int getBitPos()
//	{
//		return 0;
//	}

	@Override
	public boolean canRead()
	{
		return true;
	}

	@Override
	public boolean canWrite()
	{
		return false;
	}

}
