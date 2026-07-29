package org.iottree.ext.av;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONArray;
import org.json.JSONObject;

public class WavPlayer_NM extends MNNodeMid
{
	static ILogger log = LoggerManager.getLogger(WavPlayer_NM.class);

	static Lan lan = Lan.getLangInPk(WavPlayer_NM.class);

	public static class KeyFileItem
	{
		String key = null;

		String fileName;

		// boolean bDefault = false;

		public KeyFileItem(String key, String file_name)
		{
			this.key = key;
			this.fileName = file_name;
			// this.bDefault = b_def ;
		}

		public String getKey()
		{
			if (this.key == null)
				return "";
			return this.key;
		}

		public boolean hasKey()
		{
			return Convert.isNotNullEmpty(this.key);
		}

		public String getFileName()
		{
			return this.fileName;
		}

		// public boolean isDefault()
		// {
		// return this.bDefault ;
		// }

		public JSONObject toJO()
		{
			JSONObject ret = new JSONObject();
			ret.putOpt("key", this.key).put("filen", this.fileName);
			// if(bDefault)
			// ret.put(", value)
			return ret;
		}

		public static KeyFileItem fromJO(JSONObject jo)
		{
			String filen = jo.optString("filen");
			if (Convert.isNullOrEmpty(filen))
				return null;
			return new KeyFileItem(jo.optString("key"), filen);
		}
	}

	String wavDir = null;

	ArrayList<KeyFileItem> keyFileItems = null;

	String defaultFN = null;

	private transient HashMap<String, KeyFileItem> key2kfi = null;

	private synchronized HashMap<String, KeyFileItem> getKey2Item(boolean b_reload)
	{
		if (!b_reload)
		{
			if (key2kfi != null)
				return this.key2kfi;
		}

		HashMap<String, KeyFileItem> rets = new HashMap<>();
		if (keyFileItems != null)
		{
			for (KeyFileItem kfi : keyFileItems)
			{
				if (kfi.hasKey())
					rets.put(kfi.key, kfi);
			}
		}
		return this.key2kfi = rets;
	}

	private KeyFileItem getKeyFileItem(String key)
	{
		return getKey2Item(false).get(key);
	}

	private String findKeyByFileName(String file_name)
	{
		if (keyFileItems == null)
			return null;
		for (KeyFileItem kfi : keyFileItems)
		{
			if (file_name.equals(kfi.fileName))
				return kfi.getKey();
		}
		return null;
	}

	public ArrayList<KeyFileItem> refreshKeyFileItems()
	{
		if (Convert.isNullOrEmpty(this.wavDir))
			return null;
		File wavd = new File(this.wavDir);
		if (!wavd.exists() || !wavd.isDirectory())
			return null;
		FileFilter ff = new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if (!f.isFile())
					return false;
				String fn = f.getName().toLowerCase();
				return fn.endsWith(".wav");
			}
		};

		ArrayList<KeyFileItem> ret = new ArrayList<>();
		for (File f : wavd.listFiles(ff))
		{
			String key = findKeyByFileName(f.getName());
			// boolean b_def = f.getName().equals(this.defaultFN) ;
			KeyFileItem kfi = new KeyFileItem(key, f.getName());
			ret.add(kfi);
		}
		this.keyFileItems = ret;
		getKey2Item(true);
		return ret;
	}

	public String getWavDir()
	{
		if (this.wavDir == null)
			return "";

		return this.wavDir;
	}

	public String getDefaultFN()
	{
		return this.defaultFN;
	}

	public List<KeyFileItem> getKeyFileItems()
	{
		return this.keyFileItems;
	}

	@Override
	public int getOutNum()
	{
		return 2;
	}

	@Override
	public String getTP()
	{
		return "vo_wavplayer";
	}

	@Override
	public String getTPTitle()
	{
		return g("vo_wavplayer");
	}

	@Override
	public String getColor()
	{
		return "#11caff";
	}

	@Override
	public String getIcon()
	{
		return "\\uf027";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if (Convert.isNullOrEmpty(this.wavDir))
		{
			failedr.append("no wav dir set");
			return false;
		}

		File wavd = new File(this.wavDir);
		if (!wavd.exists() || !wavd.isDirectory())
		{
			failedr.append("wav dir not existed");
			return false;
		}

		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		jo.putOpt("wav_dir", this.wavDir);
		JSONArray jarr = new JSONArray();
		if (this.keyFileItems != null)
		{
			for (KeyFileItem kfi : this.keyFileItems)
			{
				jarr.put(kfi.toJO());
			}
		}
		jo.put("key_file_items", jarr);
		jo.putOpt("default_fn", this.defaultFN);
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.wavDir = jo.optString("wav_dir");
		JSONArray jarr = jo.optJSONArray("key_file_items");
		ArrayList<KeyFileItem> kfis = null;
		if (jarr != null)
		{
			kfis = new ArrayList<>();
			int n = jarr.length();
			for (int i = 0; i < n; i++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i);
				KeyFileItem kfi = KeyFileItem.fromJO(tmpjo);
				if (kfi == null)
					continue;
				kfis.add(kfi);
			}
		}
		this.keyFileItems = kfis;
		this.defaultFN = jo.optString("default_fn");

		this.refreshKeyFileItems();
	}

	@Override
	public String getPmTitle()
	{
		return null;
	}

	@Override
	public boolean getShowOutTitleDefault()
	{
		return true;
	}

	@Override
	protected void RT_onBeforeNetRun()
	{
		try
		{

		}
		catch ( Exception ee)
		{
			ee.printStackTrace();
		}
	}

	@Override
	protected synchronized void RT_onAfterNetStop()
	{
		closeClip();
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		String key = msg.getPayloadStr();
		KeyFileItem kfi = null;
		File wavf = null;
		if (Convert.isNotNullEmpty(key))
			kfi = this.getKeyFileItem(key);
		if (kfi == null)
		{
			if (Convert.isNotNullEmpty(this.defaultFN))
				wavf = new File(this.wavDir + "/" + this.defaultFN);
		}
		else
		{
			wavf = new File(this.wavDir + "/" + kfi.getFileName());
		}
		if (wavf == null || !wavf.exists())
		{
			return RTOut.createOutIdx().asIdxMsg(1, new MNMsg().asPayload("no wav file found"));
		}

		playWav(key, wavf);
		return null;
	}

	private transient AudioInputStream ais = null;
	private transient Clip clip = null;

	private synchronized void closeClip()
	{
		if (clip != null)
			clip.close();
		clip = null;

		if (ais != null)
		{
			try
			{
				ais.close();
			}
			catch ( Exception ee)
			{
			}
			finally
			{
				ais = null;
			}
		}
	}

	private synchronized void createClip(File wavf) throws Exception
	{
		closeClip();

		ais = AudioSystem.getAudioInputStream(wavf);
		clip = AudioSystem.getClip();
	}
	
	private synchronized boolean onLineEvent(LineEvent e)
	{
		if (e.getType() == LineEvent.Type.STOP)
		{
			closeClip();

			return true ;
			
		}
		return false;
	}

	private synchronized void playWav(String key, File wavf) throws Exception
	{
		final String wav_fn = wavf.getName();
		createClip(wavf);

		clip.open(ais);
		clip.start();
		clip.addLineListener(e -> {
			if(onLineEvent(e))
			{
				try
				{
					JSONObject jo = new JSONObject().putOpt("key", key).put("file", wav_fn);
					this.RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(0, new MNMsg().asPayload(jo)));
				}
				catch ( Exception ee)
				{
					ee.printStackTrace();
				}
			}
		});
	}

	@Override
	public String RT_getOutTitle(int idx)
	{
		switch (idx)
		{
		case 0:
			return "end";
		case 1:
			return "error";
		}
		return null;
	}

	@Override
	public String RT_getOutColor(int idx)
	{
		switch (idx)
		{
		case 1:
			return "red";
		}
		return null;
	}

}
