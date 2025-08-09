package org.iottree.ext.armlinux;

import java.io.File;
import java.util.LinkedHashMap;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.iottree.ext.armlinux.ArmLinuxConf.GPIOPin;
import org.json.JSONArray;
import org.json.JSONObject;

public class ArmLinuxConf
{
	public static File getConfFile()
	{
		return new File(Config.getDataDirBase(),"/msg_net/arm_linux.json") ;
	}
	
	private static JSONObject confJO = null ;
	
	public synchronized static JSONObject getConfJO()
	{
		if(confJO!=null)
			return confJO ;
		try
		{
			return confJO = Convert.readFileJO(getConfFile()) ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			return null ;
		}
	}
	
	public static class GPIOPin
	{
		int num ;
		String t ;
		
		GPIOPin(int n,String t)
		{
			this.num = n ;
			this.t = t ;
		}
		
		public int getNum()
		{
			return this.num ;
		}
		
		public String getTitle()
		{
			return this.t ;
		}
		
		public static GPIOPin fromJO(JSONObject jo)
		{
			int n = jo.optInt("num",-1) ;
			if(n<0)
				return null ;
			String t = jo.optString("t","io"+n) ;
			return new GPIOPin(n,t) ;
		}
	}
	
	public static JSONObject getGPIOJO()
	{
		JSONObject tmpjo = getConfJO();
		if(tmpjo==null)
			return null ;
		return tmpjo.optJSONObject("gpio") ;
	}
	

	static LinkedHashMap<Integer,GPIOPin> num2pin = null ;
	static String GPIO_EXPORT_PATH = null;
	static String GPIO_UNEXPORT_PATH = null;
	static String GPIO_PATH = null;
	
	private static boolean GPIO_OK = false; 
	
	public static LinkedHashMap<Integer,GPIOPin> getNum2Pin()
	{
		return num2pin ; 
	}

	static
	{
		JSONObject gpio_jo = getGPIOJO();
		if (gpio_jo != null)
		{
			GPIO_EXPORT_PATH = gpio_jo.optString("EXPORT_PATH") ;
			GPIO_UNEXPORT_PATH = gpio_jo.optString("UNEXPORT_PATH") ;
			GPIO_PATH = gpio_jo.optString("GPIO_PATH") ;
		
			LinkedHashMap<Integer,GPIOPin> io2pin = new LinkedHashMap<>() ;
			JSONArray jarr = gpio_jo.optJSONArray("gpio_pins") ;
			if(jarr!=null)
			{
				int n = jarr.length() ;
				for(int i = 0 ; i < n ; i ++)
				{
					JSONObject tmpjo = jarr.getJSONObject(i) ;
					ArmLinuxConf.GPIOPin pin = ArmLinuxConf.GPIOPin.fromJO(tmpjo) ;
					if(pin==null)
						continue ;
					io2pin.put(pin.num, pin) ;
				}
				num2pin = io2pin;
			}
			
			GPIO_OK = Convert.isNotNullEmpty(GPIO_EXPORT_PATH) && Convert.isNotNullEmpty(GPIO_PATH)
					&& num2pin.size()>0 ;
		}
	}
	
	public static boolean isGPIOOk()
	{
		return GPIO_OK;
	}
}
