package org.iottree.driver.zigbee.zspec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 对应ZSCoor中的NetworkOptions
 * 
 * @author jason.zhu
 */
public class ZSSettings
{
	private int pan_id = -1 ;
	private String ext_pan_id = null ;
	private String network_key = null ;
	private int channel = 11 ;
	private ArrayList<Integer> channelList = new ArrayList<>() ;
	
	private transient ZSCoor owner ;
	
	private static ZSSettings DEF_SETS = new ZSSettings(null) ;
	static
	{
		DEF_SETS.channel = 11 ;
	}
	
	public static ZSSettings acquireSettings(ZSCoor coor)
	{
		ZSSettings ret = new ZSSettings(coor) ;
		try
		{
			ret.loadOrNew();
			return ret ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			return null ;
		}
	}
	
	private ZSSettings(ZSCoor coor)
	{
		this.owner = coor ;
	}
	
	public ZSCoor getOwner()
	{
		return this.owner ;
	}
	
	public int getPanId()
	{
		return this.pan_id ;
	}
	
	public void setPanId(int panid) throws IOException
	{
		this.pan_id = panid ;
		this.save();
	}
	
	public String getExtPanId()
	{
		return this.ext_pan_id ;
	}
	
	public String getNetworkKey()
	{
		return this.network_key ;
	}
	
	public int getChannel()
	{
		return this.channel ;
	}
	
	public void setChannel(int ch) throws IOException
	{
		if(ch<11||ch>26)
			throw new IllegalArgumentException("channel is not in [11,26]") ;
		if(ch==this.channel)
			return ;
		this.channel = ch ;
		this.save();
	}
	
	public List<Integer> getChannelList()
	{
		return this.channelList ;
	}

	public boolean includeCh(int radioch)
	{
		if(this.channelList==null)
			return false;
		return this.channelList.contains(radioch) ;
	}
	
//	public boolean chkNetworkKey(String nwk_key)
//	{
//		if(Convert.isNullOrEmpty(this.network_key))
//			return false;
//		return this.network_key.equalsIgnoreCase(nwk_key) ;
//	}
	
	
	
	private File calFile()
	{
		File dirb = this.owner.getStoreDirBase() ;
		return new File(dirb,"settings.json") ;
	}
	
	private void loadOrNew() throws IOException
	{
		File setf = calFile() ;
		JSONObject jo = Convert.readFileJO(setf) ;
		if(jo==null || jo.isEmpty())
		{
			this.pan_id = generatePanID() ;
			this.ext_pan_id = generateExtPanID() ;
			this.network_key = generateNetworkKey() ;
			this.channelList.add(this.channel) ;
			save() ;
		}
		else
		{
			this.fromJO(jo) ;
		}
	}
	
	private void save() throws IOException
	{
		File setf = calFile() ;
		JSONObject jo = this.toJO() ;
		Convert.writeFileJO(setf, jo);
	}
	
	private int generatePanID()
	{
        return ThreadLocalRandom.current().nextInt(1, 0xffff - 1);
    }
	
	private String generateExtPanID()
	{
        StringBuilder hex = new StringBuilder(16);
        for (int i = 0; i < 8; i++) {
            int b = ThreadLocalRandom.current().nextInt(256); // [0, 256) -> 0~255
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
	
	private String generateNetworkKey()
	{
        StringBuilder hex = new StringBuilder(32);
        for (int i = 0; i < 16; i++) {
            int b = ThreadLocalRandom.current().nextInt(256); // [0, 256) -> 0~255
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
	
	public JSONObject toJO()
	{
		return new JSONObject().put("pan_id",this.pan_id).putOpt("ext_pan_id", this.ext_pan_id)
				.putOpt("network_key",this.network_key).put("channel", this.channel)
				.putOpt("channel_list", this.channelList);
	}
	
	public boolean fromJO(JSONObject jo)
	{
		this.pan_id = jo.getInt("pan_id") ;
		this.ext_pan_id = jo.getString("ext_pan_id") ;
		this.network_key = jo.getString("network_key") ;
		this.channel = jo.getInt("channel") ;
		JSONArray jarr = jo.optJSONArray("channel_list") ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				int ch = jarr.getInt(i) ;
				this.channelList.add(ch) ;
			}
		}
		if(this.channelList.size()<=0)
			this.channelList.add(this.channel) ;
		return true;
	}
}
