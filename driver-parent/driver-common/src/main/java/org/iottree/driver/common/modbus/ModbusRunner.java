package org.iottree.driver.common.modbus;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;


public abstract class ModbusRunner implements Runnable
{
	static ILogger log = LoggerManager.getLogger(ModbusRunner.class);
	
	String uniqueId = null ;
	
	Thread thread = null;
	/**
	 * ��ȡ����ָ��
	 */
	private  List<ModbusCmd> readCmds = null;

	LinkedList<ModbusCmd> asynCmds = new LinkedList<ModbusCmd>();

	private ArrayList<ModbusCmd> runOnceCmds = new ArrayList<ModbusCmd>() ;
	
	/**
	 * �������������ն˶�ʱ�����������壬�Ա�֤���ӱ���
	 */
	transient private long lastPulse = -1;

	/**
	 * ���������������
	 */
	transient int cmdErrorNum = 0;

	transient boolean bCmdRun = false;

	ModbusRunListener runLis = null;
	
	/**
	 * Ϊ�ڲ�������ʱ���ɵ�ModbusCmd�����ṩ����timeout����
	 * �����ָ�����гɹ���
	 */
	int recvTimeout = -1 ;
	int recvEndTimeout = -1 ;
	
	/**
	 * ����ָ��֮��ĵȴ����
	 * ��gprs�Ȼ����£��˼������Ҫ�����Դ�����ӿɿ���
	 */
	int cmdIntervalMs = 10 ;
	
	/**
	 * �������ִ���������һ����ֵ�ں���
	 */
	int ignoreErrCount = 0 ;
	
	public ModbusRunner(String uniqueid)//(List<ModbusCmd> mcmds)
		throws Exception// IMCmdHandler cmdhandler,
	{
		uniqueId = uniqueid ;
		// cmdHandler = cmdhandler ;
		
		//setReadCmds(mcmds) ;
	}
	
	
	public String getUniqueId()
	{
		return uniqueId ;
	}
	
	public void setUniqueId(String id)
	{
		uniqueId = id;
	}
	
	public void setReadCmds(List<ModbusCmd> mcmds)
	{
		//readCmds = mcmds;
		if(mcmds==null)
			mcmds = new ArrayList<ModbusCmd>(0) ;
		//System.out.println("   set modbus Runner read cmds = "+mcmds.size());
		for(ModbusCmd mc:mcmds)
		{
			mc.belongToRunner = this;
		}
		
		readCmds = mcmds ;
	}
	
	
	public void setCmdRecvTimeout(int recv_to,int recv_end_to)
	{
		recvTimeout = recv_to ;
		recvEndTimeout = recv_end_to ;
	}
	
	
	public void setCmdIntervalMS(int cmd_ims)
	{
		cmdIntervalMs = cmd_ims ;
	}
	
	
	public void setIgnoreErrCount(int c)
	{
		ignoreErrCount = c ;
	}
	/**
	 * �жϵ�ǰ�����ͨ��״̬�Ƿ�׼����
	 * @return
	 */
	protected abstract boolean checkReady() ;
	
	
	protected abstract InputStream getInputStream() ;
	
	protected abstract OutputStream getOutputStream() ;
	
	protected abstract boolean beforeRunnerStart();
	
	protected abstract void onRunnerStopped() ;
	
	/**
	 * �ж��Ƿ���������
	 * @return
	 */
	public abstract boolean isRunningOk() ;
	
	/**
	 * ��ǰ����״����Ϣ��������в��������򷵻�����
	 * @return
	 */
	public abstract String getRunningInfo() ;
	//protected abstract boolean 

	public void setRunListener(ModbusRunListener rl)
	{
		runLis = rl;
	}
	

	// public void stopGracefull

	public boolean isCmdRunning()
	{
		return bCmdRun;
	}


	public void stop()
	{
		stopRunner(false);
		// waitInterval();
		// closeCom() ;
	}
	

	synchronized void startRunner()
	{
		if (thread != null)
			return;

		bCmdRun = true;
		thread = new Thread(this, "ModbusRunner");
		thread.start();
	}
	

	public void start() throws Exception
	{
		if(!beforeRunnerStart() && runLis!=null)
			runLis.onModbusCmdRunError() ;

		startRunner();
	}

	
	synchronized protected void stopRunner(boolean interrupt)
	{
		Thread th = thread;
		if (th == null)
			return;

		if (interrupt)
		{
			th.interrupt();
			thread = null;
		}
		bCmdRun = false;
		
		onRunnerStopped();//closeCom();
	}

	
	public void stopForce()
	{
		stopRunner(true) ;
	}

	private void waitInterval()
	{
		try
		{
			Thread.sleep(10);
		}
		catch (Exception ee)
		{
		}
	}
	
	private void waitInterval(long ms)
	{
		try
		{
			Thread.sleep(ms);
		}
		catch (Exception ee)
		{
		}
	}

	long lastDoCmd = -1 ;
	/**
	 * ��ÿ������ִ��ǰ����һ�������ʱ�����ж�
	 * �˹�����gprs����º���Ҫ
	 */
	private void BEFORE_doCmd()
	{
		long t = cmdIntervalMs - (System.currentTimeMillis()-lastDoCmd) ;
		
		if(t>0)
		{
			try
			{//System.out.println("before sleep="+t) ;
				Thread.sleep(t) ;
			}
			catch(Exception ee){}
		}
	}
	
	private void AFTER_doCmd()
	{
		//System.out.println("after ") ;
		lastDoCmd =  System.currentTimeMillis();
	}
	/**
	 * �̳�ʵ�������⴦��
	 * @param t
	 */
	protected void onRunCmdError(Throwable t)
	{
		
	}
	
	
	boolean bHalt = false;
	
	/**
	 * �����Ƿ�Ҫ��ͣ
	 * �˷�����ֹͣ�̣߳�����֧����ӵĿ���
	 * @param h
	 */
	public void setRunHalt(boolean h)
	{
		bHalt = h ;
	}
	
	
	public boolean isRunHalt()
	{
		return bHalt ;
	}

	public void run()
	{
		try
		{
			// clientInfo.devId = "no_id" ;
			//System.out.println(">>modbus runner="+this.getClass().getCanonicalName()+" bHalt="+bHalt +" bCmdRun="+bCmdRun);

			//SerialPort sp = null;
			while (bCmdRun)
			{
				//sp = serialPort;
				if(bHalt)
				{
					waitInterval();
					
					//ֹͣ��ʱ�������޷����ֵײ���socket�Ƿ�Ͽ���������Ҫ�Դ����ר�ŵ���һ��
					//�жϵײ�ͨ���Ƿ������Ĺ���
					if(checkEnd(true))
						break ;//
					
					InputStream inputs = getInputStream() ;
					if(inputs!=null)
					{//halt����£��ײ������socket�з���һЩ����Ҫ������
				    	//���ر���һ�������϶��͸������ģ��֮�以����ţ�
				    	//��ʱ������Ҫ�������е����ݽ������
						int avn = inputs.available() ;
						if(avn>0)
							inputs.skip(avn) ;
					}
					
					continue;
				}
				
				if (!checkReady())
				{
					waitInterval();
					continue;
				}

				if(checkEnd(false))
					break ;//���ֽ���
				
				//boolean b_cmd_run = false;
				
				waitInterval(1);//�������û��ָ�������ʱռ��cpu���
				
				long tst = System.currentTimeMillis() ;
				// do scan
				runReadCmds();

				//System.out.println("read cost="+(System.currentTimeMillis()-tst)) ;
				// check asyn cmd
				runQueAsynCmd();
				
				ArrayList<ModbusCmd> runone_ends = new ArrayList<ModbusCmd>() ;
				int run_num = runOnceCmds.size() ;
				for(int i = 0; i< run_num;i++)
				{
					ModbusCmd mc  = runOnceCmds.get(i) ;
					if(mc.getRunOnceTime()<System.currentTimeMillis())
					{
						try
						{
							runone_ends.add(mc) ;
							
							BEFORE_doCmd();
							
							mc.doCmd(getOutputStream(),getInputStream());
							
							AFTER_doCmd();
						}
						catch(Throwable ee)
						{
							if(log.isErrorEnabled())
								log.error("",ee) ;
						}
					}
				}
				//System.out.println(runone_ends.size()) ;
				removeRunOnceCmds(runone_ends) ;
			}// end of while
		}
		catch (Exception e)
		{
			for (ModbusCmd mc : readCmds)
			{//��������ݳ�����Ч
				if (mc.isReadCmd() && runLis != null)
				{
					try
					{//����������Ͳ�������
						runLis.onModbusReadFailed(mc);
					}
					catch(Exception iee)
					{
						if(log.isErrorEnabled())
							log.error("",iee) ;
					}
				}
			}
			
			//e.printStackTrace();
			if(log.isDebugEnabled())
			{
				log.debug("����ֹͣ����" + e.getMessage());
				//e.printStackTrace();
			}

			// close() ;
		}
		finally
		{
			thread = null;
			bCmdRun = false;
			onRunnerStopped();
		}
	}

	/**
	 * ������Ҫ��readָ��
	 * @throws Exception
	 */
	public void runReadCmds() throws Exception
	{
		for (ModbusCmd mc : readCmds)
		{
			if (!mc.tickCanRun())
				continue;

			runQueAsynCmd();//may be manual cmd
			
			try
			{
				BEFORE_doCmd();
				
				if(log.isDebugEnabled())
					log.debug(" ****run modbus cmd:"+mc);
				
				synchronized (this)
				{
					// run it
					mc.doCmd(getOutputStream(),getInputStream());
				}
				
				AFTER_doCmd() ;
				
				//System.out.println("mc "+mc+" cost="+(System.currentTimeMillis()-tst)) ;

				if (mc.isReadCmd() && runLis != null)
				{
					Object[] ovs = mc.getReadVals();

					if (ovs != null)
						runLis.onModbusReadData(mc, ovs);
					else
					{
						if(mc.getErrCount()>this.ignoreErrCount)
							runLis.onModbusReadFailed(mc);
					}
				}
				
				
			}
			catch (Throwable ee)
			{
				//֪ͨ����ʵ�ֵ���Դ˴���������⴦��
				//��tcp����£�������Ҫ��������
				onRunCmdError(ee) ;
				
				//ee.printStackTrace();
				if (mc.isReadCmd() && runLis != null)
				{
					try
					{//����������Ͳ�������
						runLis.onModbusReadFailed(mc);
					}
					catch(Exception iee)
					{
						if(log.isErrorEnabled())
							log.error("",iee) ;
					}
					
//							��һЩ��Ҫ�������Ӳ����п����жϵ�ͨ������£����ִ˴���ʱ
					//������Ҫ�Ե�ǰ�����ӽ��жϿ�,ֻ��Ҫ�˷���ֱ���׳�����
					runLis.onModbusCmdRunError() ;
				}
				
				
				
			}
			// if(mc.get)
		}
	}

	private int runQueAsynCmd()
	{
		int asynn = asynCmds.size();
		if (asynn > 0)
		{
			for (int i = 0; i < asynn; i++)
			{
				try
				{
					ModbusCmd mc = dequeueAsynFirst();

					BEFORE_doCmd();
					
					mc.doCmd(getOutputStream(),getInputStream());
					
					AFTER_doCmd();
				}
				catch(Throwable ee)
				{
					if(log.isErrorEnabled())
						log.error("",ee) ;
				}
			}
		}
		return asynn;
	}


	/**
	 * �õ���ȡ����ָ��
	 * 
	 * @return
	 */
	public List<ModbusCmd> getReadCmds()
	{
		return readCmds;
	}

	/**
	 * ʵ���������жϱ�runner�Ƿ����
	 * ���bhalt=true����ײ�ʵ��ʱ��Ҫ�����жϵײ�������ͨ�Ŷ����Ƿ�Ͽ�
	 *    ����socket���������halt�������û����socket���е��ã�����û���ж�socket�Ƿ�Ͽ���java tcp�ص㣩
	 *    ������Ҫ�Դ�true�������socket���жϿ��ж�
	 *    
	 *    bhalt=falseʱ��˵����ʱ����õײ��ͨ��
	 * @return
	 */
	protected abstract boolean checkEnd(boolean bhalt) ;
	/**
	 * ��Ե�������������д����������֧�ַ��� ͬ����������
	 * 
	 * @param mc
	 */
	public boolean doModbusCmdSyn(ModbusCmd mc) throws Exception
	{
		//SerialPort sp = serialPort;
		if (!this.checkReady())
		{
			return false;
		}
		synchronized (this)
		{
			BEFORE_doCmd();
			
			mc.doCmd(this.getOutputStream(),this.getInputStream());
			//Thread.sleep(10);
			AFTER_doCmd();
		}
		return true;
	}

	/**
	 * �첽��������
	 * 
	 * @param mc
	 * @return
	 * @throws Exception
	 */
	public boolean doModbusCmdAsyn(ModbusCmd mc) throws Exception
	{
		if (!this.checkReady())
		{
			return false;
		}
		//
		// set run param and ret
		// mc.doCmd(serialPort) ;
		synchronized (asynCmds)
		{
			asynCmds.addLast(mc);
		}
		return true;
	}
	
	
	private ModbusCmd dequeueAsynFirst()
	{
		synchronized (asynCmds)
		{
			return asynCmds.removeFirst();
		}
	}
	
	public synchronized void doModbusCmdRunOnceDelay(ModbusCmd mc,long delay_ms)
	{
		mc.setRunOnceTime(System.currentTimeMillis()+delay_ms) ;
		runOnceCmds.add(mc) ;
	}

	
	private synchronized void removeRunOnceCmds(List<ModbusCmd> mcs)
	{
		runOnceCmds.removeAll(mcs) ;
	}

}
