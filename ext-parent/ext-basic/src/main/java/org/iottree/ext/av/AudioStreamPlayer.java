package org.iottree.ext.av;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Lan;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONObject;

public class AudioStreamPlayer extends MNNodeMid implements IMNRunner
{
	static ILogger log = LoggerManager.getLogger(AudioStreamPlayer.class);

	static Lan lan = Lan.getLangInPk(AudioStreamPlayer.class);

	public static LinkedHashMap<String, AudioFormat.Encoding> NAME2ENC = new LinkedHashMap<>();
	static
	{
		NAME2ENC.put(AudioFormat.Encoding.PCM_SIGNED.toString(), AudioFormat.Encoding.PCM_SIGNED);
		NAME2ENC.put(AudioFormat.Encoding.PCM_UNSIGNED.toString(), AudioFormat.Encoding.PCM_UNSIGNED);
		NAME2ENC.put(AudioFormat.Encoding.PCM_FLOAT.toString(), AudioFormat.Encoding.PCM_FLOAT);
		NAME2ENC.put(AudioFormat.Encoding.ULAW.toString(), AudioFormat.Encoding.ULAW);
		NAME2ENC.put(AudioFormat.Encoding.ALAW.toString(), AudioFormat.Encoding.ALAW);
	}

	public static List<AudioFormat> listCurLineFormats()
	{
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		ArrayList<AudioFormat> fmts = new ArrayList<>();
		for (Mixer.Info mixerInfo : mixerInfos)
		{
			Mixer mixer = AudioSystem.getMixer(mixerInfo);
			// SourceDataLine
			Line.Info[] lineInfos = mixer.getSourceLineInfo();

			for (Line.Info lineInfo : lineInfos)
			{
				if (lineInfo instanceof DataLine.Info)
				{
					DataLine.Info dataLineInfo = (DataLine.Info) lineInfo;
					AudioFormat[] supportedFormats = dataLineInfo.getFormats();
					if (supportedFormats == null)
						continue;
					for (AudioFormat af : supportedFormats)
						fmts.add(af);
				}
			}
		}
		return fmts;
	}

	AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
	int sampleRate = 24000;
	int sampleSizeInBits = 16;

	int audioChannels = 1; // (1 for mono, 2 for stereo, and soon)

	@Override
	public int getOutNum()
	{
		return 0;
	}

	@Override
	public String getTP()
	{
		return "audio_stream_player";
	}

	@Override
	public String getTPTitle()
	{
		return g("audio_stream_player");
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
	
	public float getSampleRate()
	{
		return this.sampleRate ;
	}
	
	public int getSampleSizeInBits()
	{
		return this.sampleSizeInBits ;
	}
	
	public int getAudioChannels()
	{
		return this.audioChannels ;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{

		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		jo.put("encoding", this.encoding.toString());
		jo.put("sample_rate", this.sampleRate);
		jo.put("sample_sizeinbit", this.sampleSizeInBits);
		jo.put("audio_ch", this.audioChannels);
		// jo.put("encoding",this.encoding.toString()) ;

		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		String enc = jo.optString("encoding", AudioFormat.Encoding.PCM_SIGNED.toString());
		this.encoding = NAME2ENC.get(enc);
		this.sampleRate = jo.optInt("sample_rate", 24000);
		this.sampleSizeInBits = jo.optInt("sample_sizeinbit", 16);
		this.audioChannels = jo.optInt("audio_ch", 1);
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
		// closeClip();
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		byte[] bs = msg.getBytesArray();
		if (bs == null)
			return null;
		addAudioData(bs);
		return null;
	}

	// @Override
	// public String RT_getOutTitle(int idx)
	// {
	// switch (idx)
	// {
	// case 0:
	// return "end";
	// case 1:
	// return "error";
	// }
	// return null;
	// }
	//
	// @Override
	// public String RT_getOutColor(int idx)
	// {
	// switch (idx)
	// {
	// case 1:
	// return "red";
	// }
	// return null;
	// }

	private AudioFormat audioFormat;
	private SourceDataLine sourceDataLine;
	private BlockingQueue<byte[]> audioQueue;
	private volatile boolean isPlaying;
	private Thread playbackThread;

	private void initAudioPlayer() throws LineUnavailableException
	{
		// AudioFormat format = new AudioFormat(
		// encoding,
		// sampleRate,
		// sampleSizeInBits,
		// audioChannels,
		// 4,
		// sampleRate,
		// false
		// );

		AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, audioChannels, true, false);

		this.audioFormat = format;
		this.audioQueue = new LinkedBlockingQueue<>(100);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
		sourceDataLine.open(audioFormat);

		sourceDataLine.start();
	}

	@Override
	public synchronized boolean RT_start(StringBuilder failedr)
	{
		if (isPlaying)
		{
			return true;
		}

		isPlaying = true;

		playbackThread = new Thread(this::playbackLoop, "MMNet-AudioStreamPlayer-Thread");
		playbackThread.setDaemon(true);
		playbackThread.start();
		return true;
	}

	@Override
	public synchronized void RT_stop()
	{
		Thread th = playbackThread;
		if (th != null)
			th.interrupt();
		close();
	}

	private void close()
	{
		isPlaying = false;
		playbackThread = null;

		if (sourceDataLine != null)
		{
			sourceDataLine.stop();
			sourceDataLine.close();
		}
	}

	private void playbackLoop()
	{
		try
		{
			initAudioPlayer();
		}
		catch ( Exception e)
		{
			// e.printStackTrace();
			RT_DEBUG_ERR.fire("init_err", "init audio player err", e);
			return;
		}

		RT_DEBUG_ERR.clear("init_err");

		try
		{
			while (isPlaying && !Thread.currentThread().isInterrupted())
			{
				byte[] audioData = audioQueue.take(); // 阻塞直到有数据
				if (audioData.length > 0)
				{
					sourceDataLine.write(audioData, 0, audioData.length);
				}
			}
		}
		catch ( InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
		finally
		{
			close();
		}
	}

	public void addAudioData(byte[] audioData)
	{
		if (isPlaying)
		{
			try
			{
				audioQueue.put(audioData);
			}
			catch ( InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		}
	}

	public void clearQueue()
	{
		audioQueue.clear();
	}

	@Override
	public boolean RT_isRunning()
	{
		return isPlaying;
	}

	@Override
	public boolean RT_isSuspendedInRun(StringBuilder reson)
	{
		return false;
	}

	/**
	 * false will not support runner
	 * 
	 * @return
	 */
	public boolean RT_runnerEnabled()
	{
		return true;
	}

	/**
	 * true will not support manual trigger to start
	 * 
	 * @return
	 */
	public boolean RT_runnerStartInner()
	{
		return false;
	}
}
