package org.iottree.ext.av;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.core.util.jt.JSONTemp;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONObject;

public class MicIn_NS extends MNNodeStart implements IMNRunner
{
	static ILogger log = LoggerManager.getLogger(MicIn_NS.class) ;
	
	private static final int SAMPLE_RATE = 16000;
	private static final int BUFFER_SIZE = 4096; // voice buf size
	// String topic = null ;
	String mixerInName = null ;
	
	@Override
	public String getTP()
	{
		return "mic_in";
	}

	@Override
	public String getTPTitle()
	{
		return g("mic_in");
	}
	
	@Override
	public JSONTemp getInJT()
	{
		return null;
	}

	@Override
	public JSONTemp getOutJT()
	{
		return null;
	}

	@Override
	public int getOutNum()
	{
		return 1;
	}

	@Override
	public String getColor()
	{
		return "#11caff";
	}

	@Override
	public String getIcon()
	{
		return "\\uf130";
	}
	
//	public String getTopic()
//	{
//		return this.topic ;
//	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
//		if(Convert.isNullOrEmpty(this.mixerInName))
//		{
//			failedr.append("no input set") ;
//			return false ;
//		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("mixer_n", this.mixerInName) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.mixerInName = jo.optString("mixer_n",null) ;
	}


	@Override
	public String RT_getOutTitle(int idx)
	{
		if(idx==0)
			return "byte[] output" ;
		return null ;
	}
	
	private TargetDataLine getMixerLine()
	{
		if(Convert.isNullOrEmpty(mixerInName))
			return null ;
	
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		for (Mixer.Info info : mixerInfos)
		{
			Mixer mixer = AudioSystem.getMixer(info);
			Line.Info targetLineInfo = new Line.Info(TargetDataLine.class);
	        boolean isInput = mixer.isLineSupported(targetLineInfo);
	        if(!isInput)
	        	continue ;
	        
	        TargetDataLine tdl = null;
	        try {
	        	tdl = (TargetDataLine)AudioSystem.getLine(targetLineInfo) ;
	        	/*
                Line line = mixer.getLine(targetLineInfo);
                if (line instanceof DataLine) {
                    AudioFormat[] formats = ((DataLine.Info) line.getLineInfo()).getFormats();
                    System.out.println("支持格式: " + formats.length + " 种");
                }
                */
            } catch (Exception e) {
                System.out.println(e.getMessage());
                continue ;
            }
	        if(tdl==null)
	        	continue ;
	        
			String mix_n = info.getName();
			if(mix_n.equals(this.mixerInName))
				return tdl ;
		}
		return null ;
	}
	
	static AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
	
//	private Line.Info getLine()
//	{
//		Mixer.Info mixer_inf = getMixerInf() ;
//		if(mixer_inf==null)
//			return new DataLine.Info(TargetDataLine.class, format);
//		Mixer mixer = AudioSystem.getMixer(mixer_inf);
//        //Line.Info targetLineInfo = new Line.Info(TargetDataLine.class);
//        return mixer.getLineInfo() ;
//	}
	// rt
	
//	/**
//	 * will called by Kafka_M
//	 * @param topic
//	 * @param msg
//	 */
//	void RT_onTopicMsgRecv(String topic,String msg)
//	{
//		MNMsg m = new MNMsg().asBytesArray(bs).asPayload(msg).asTopic(topic) ;
//		RT_sendMsgOut(RTOut.createOutAll(m));
//	}
	
	Thread RT_th = null ;
	
	//AtomicBoolean RT_bRun = new AtomicBoolean(false);
	boolean RT_bRun = false;
	
	private Runnable runner = new Runnable()
	{
		public void run()
		{
			runInLoop() ;
		}
	};
	
	private void runInLoop()
	{
		TargetDataLine microphone = null;
		try
		{
			byte[] buffer = new byte[BUFFER_SIZE];
			//Line.Info info = getLine() ;
			microphone = getMixerLine() ;
			if(microphone==null)
			{
				DataLine.Info info =new DataLine.Info(TargetDataLine.class, format);
				microphone =  (TargetDataLine) AudioSystem.getLine(info);
			}
			if(microphone==null)
			{
				RT_DEBUG_ERR.fire("mic_in", "no microphone found");
				return ;
			}
			
			microphone.open(format);
			microphone.start();
			
			RT_DEBUG_ERR.clear("mic_in");
			
			while (RT_bRun)
			{
				int rlen = microphone.read(buffer, 0, buffer.length);
				if (rlen > 0)
				{
					MNMsg msg = new MNMsg().asBytesArray(buffer,0,rlen) ;
					RTOut rto = RTOut.createOutIdx().asIdxMsg(0, msg) ;
					this.RT_sendMsgOut(rto);
				}
			}//end of while
		}
		catch(Exception ee)
		{
			if(log.isErrorEnabled())
				log.error(ee);
			
			RT_DEBUG_ERR.fire("mic_in", ee.getMessage(),ee);
		}
		finally
		{
			if(microphone!=null)
				microphone.close();
			RT_bRun=false;;
			RT_th = null ;
		}
	}

	@Override
	public synchronized boolean RT_start(StringBuilder failedr)
	{
		if(RT_bRun)
			return true;
		
		RT_bRun=true;
		RT_th = new Thread(runner);
		RT_th.start();
		return true;
		
	}

	@Override
	public synchronized void RT_stop()
	{
		RT_bRun = false;
	}

	@Override
	public boolean RT_isRunning()
	{
		return RT_th!=null;
	}
	
	@Override
	public boolean RT_isSuspendedInRun(StringBuilder reson)
	{
		return false;
	}
	
	/**
	 * false will not support runner
	 * @return
	 */
	public boolean RT_runnerEnabled()
	{
		return true ;
	}
	
	/**
	 * true will not support manual trigger to start
	 * @return
	 */
	public boolean RT_runnerStartInner()
	{
		return false;
	}
	
}
