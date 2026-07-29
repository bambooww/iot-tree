package org.iottree.driver.common.modbus;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.w3c.dom.Element;


public class ModbusConnForDev // implements Runnable,Callable<Boolean>
{
//    static ILogger log = LoggerManager.getLogger(ModbusConnForDev.class);
//
//    static Object lockObj = new Object();
//    static ArrayList<ModbusConnForDev> ALL_CLIENTS = new ArrayList<ModbusConnForDev>() ;
//    
//    static void increaseCount(ModbusConnForDev c)
//    {
//    	synchronized(lockObj)
//    	{
//    		ALL_CLIENTS.add(c) ;
//    	}
//    }
//    
//    static void decreaseCount(ModbusConnForDev c)
//    {
//    	//System.out.println("client disconn="+c) ;
//    	synchronized(lockObj)
//    	{
//    		ALL_CLIENTS.remove(c);
//    	}
//    }
//    
//    
//    public static int getClientConnCount()
//    {
//    	return ALL_CLIENTS.size() ;
//    }
//    
//    public static ModbusConnForDev[] getAllClients()
//    {
//    	synchronized(lockObj)
//    	{
//    		ModbusConnForDev[] rets = new ModbusConnForDev[ALL_CLIENTS.size()];
//    		ALL_CLIENTS.toArray(rets);
//    		return rets;
//    	}
//    }
//    
//    
//    public static List<ModbusConnForDev> getAllClientsList()
//    {
//    	synchronized(lockObj)
//    	{
//	    	List<ModbusConnForDev> rets = new ArrayList<ModbusConnForDev>() ;
//	    	rets.addAll(ALL_CLIENTS);
//	    	return rets ;
//    	}
//    }
//    
//    
//    public static ModbusConnForDev getClientById(String clientid)
//    {
//    	for(ModbusConnForDev c:ALL_CLIENTS)
//    	{
//    		if(clientid.equals(c.getClientInfo().devId))
//    			return c ;
//    	}
//    	return null ;
//    }
//    
//    static long TMP_ID_C = 0 ;
//    
//    synchronized static long newTmpId()
//    {
//    	TMP_ID_C++ ;
//    	return TMP_ID_C ;
//    }
//    
//
//    ////////////////�豸���Ͷ�Ӧ��
//    
//    /**
//     * 
//     */
//    static HashMap<String,Class> DEVTYPE2PLUG = new HashMap<String,Class>() ;
//    static ArrayList<MCmdDevPlug> DEV_PLUGS = new ArrayList<MCmdDevPlug>() ;
//
//    public static void registerDevTypeClass(Class c) throws Exception
//    {
//    	MCmdDevPlug p = (MCmdDevPlug)c.newInstance() ;
//    	DEV_PLUGS.add(p) ;
//    	DEVTYPE2PLUG.put(p.getDevType(), c) ;
//    }
//    
//    static MCmdDevPlug getNewDevPlugByType(String tt) throws Exception
//    {
//    	Class c = DEVTYPE2PLUG.get(tt) ;
//    	if(c==null)
//    		return null ;
//    	
//    	return (MCmdDevPlug)c.newInstance() ;
//    }
//    
//    public static List<MCmdDevPlug> getAllDevTypePlugs()
//    {
//    	return DEV_PLUGS ;
//    }
//    
//    static
//    {
//    	try
//    	{
//    		registerDevTypeClass(Dev1.class) ;
//    		registerDevTypeClass(Dev2.class) ;
//    	}
//    	catch(Exception ee)
//    	{
//    		ee.printStackTrace() ;
//    	}
//    }
//    
//    /////////////
//    static ArrayList<IMCmdDataCompHandler> dataCompHandlers = new ArrayList<IMCmdDataCompHandler>() ;
//    
//    static void fireCmdReqResp(MCmdServerForClient sfc,MCmd req,MCmd resp,Exception e,MCmdDevPlug.DevStatus resp_ds)
//    {
//    	
//    }
//    //MsgCmdServer server = null;
//    //IMCmdHandler cmdHandler = null ;
//    Socket socket = null;
//    
//    InputStream instream = null ;
//    OutputStream outstream = null ;
//
//    Thread thread = null;
//    //boolean bRun = false;
//    
//    transient MCmdDevPlug devPlug = null ;
//    
//    private ModbusConnInfo clientInfo = null ; 
//    
//    /**
//     * �̳߳����ж���-�˶����������ƶ���ر�ʱ��ͬʱȡ���̳߳��е���������
//     */
//    transient Future<Boolean> future = null ;
//    
//    /**
//     * �������������ն˶�ʱ�����������壬�Ա�֤���ӱ���
//     */
//    transient private long lastPulse = -1 ;
//    
//    
//    /**
//     * ���������������
//     */
//    transient int cmdErrorNum = 0 ;
//
//    transient boolean bCmdRun = false;
//
//    public ModbusConnForDev(Socket tcp) throws Exception//IMCmdHandler cmdhandler,
//    {
//    	//cmdHandler = cmdhandler ;
//        socket = tcp;
//        
//        clientInfo = new MCmdClientInfo(
//        		tcp.getInetAddress().getHostAddress(),
//        		tcp.getPort(),null
//        		);
//       
//        increaseCount(this);
//    	socket.setSoTimeout(60000) ;
//    	socket.setTcpNoDelay(true);
//        // Get a stream object for reading and writing
//        instream = socket.getInputStream();
//        outstream = socket.getOutputStream();
//    }
//    
////    public MsgCmdServerForClient(MsgCmdServer s, Socket tcp)
////    {
////        server = s;
////        tcpClient = tcp;
////    }
//    
//    public MCmdClientInfo getClientInfo()
//    {
//    	return clientInfo ;
//    }
//
//    synchronized public void Start()
//    {
//        if (thread != null)
//                return;
//
//            thread = new Thread(this,"MsgCmdThread");
//            thread.start();
//    }
//
//    //synchronized public void Stop()
//    //{
//    //    bRun = false;
//    //}
//    
//    //transient MCmd cmdToBeSent = null;
//    //transient List<MCmd> cmdsToBeSent = null;
//    
//    
//    
//    
////    public synchronized void setCmdToBeSent(String cmd,String cont)
////    {
////    	cmdToBeSent = new MCmd(cmd,cont) ;
////    	notify() ;
////    }
////    
////    public synchronized void setCmdsToBeSent(List<MCmd> mcs)
////    {
////    	cmdsToBeSent = mcs ;
////    	notify() ;
////    }
//    
//    public boolean isCmdRunning()
//    {
//    	return bCmdRun;
//    }
//    
//    
//    
//    
//    /**
//     * �ֹ�����ָ���
//     * �÷���һ������һ��ָ���̴߳�ʹ�ã�ͨ��������ʽ����
//     * ������ʱ��Call�����еĹ��̻����ų�
//     * ���call�������ڱ����ã���˷�������
//     * @param req
//     * @return
//     */
//    public MCmd sendManualCmd(MCmd req,boolean bwait)
//    	throws Exception
//    {
//    	if(req==null)
//    		return null;
//    	
//    	synchronized(this)
//    	{
//    		if(!bwait && bCmdRun)
//    			return null ;
//    		
//    		while(bCmdRun)
//    		{
//    			wait() ;//�����ⲿָ��
//    		}
//    		
//    		bCmdRun = true;
//    		
//    		try
//    		{
//    			req.writeOut(outstream) ;
//	    		return MCmd.readFrom(instream);
//    		}
//    		finally
//    		{
//    			bCmdRun = false;
//    		}
//    	}
//    	
//    }
//    
//    /**
//     * 
//     * @param cmd
//     * @param cont
//     * @return
//     * @throws Exception
//     */
//    public MCmd sendManualCmd(String cmd,String cont,boolean bwait)
//    	throws Exception
//    {
//    	return sendManualCmd(new MCmd(cmd,cont),bwait) ;
//    }
//    /**
//     * ͨ���̳߳ص��õķ���
//     * �̳߳ص��ȳ���ᶨʱ���ô˷����������ж��ն��Ƿ�����
//     * 
//     * ����˷�������false�������Ѿ����ն�ʧȥ��ϵ����Ҫ���ж�����
//     */
//    public Boolean call() throws Exception
//	{
//    	if(devPlug==null)
//    		return false;
//    	
//    	if(clientInfo.devId.startsWith("*"))
//    		return false;//û��id
//    	
//    	synchronized(this)
//    	{
//    		if(bCmdRun)
//	    		return false;//�ֹ�ָ����������
//    		
//    		bCmdRun = true ;
//    	}
//    	
//    	try
//    	{
//	    	MCmdDevPlug.DevStatus ds = devPlug.onCmdStart();
//	    	if(ds==MCmdDevPlug.DevStatus.error)
//	    	{
//	    		this.close() ;
//	    		return false;
//	    	}
//	    	
//	    	MCmdDevReqResp[] rrs = devPlug.getCmdReqRespAll() ;
//	    	if(rrs!=null)
//	    	{
//	    		for(MCmdDevReqResp rr:rrs)
//	    		{
//	    			if(!rr.needReqResp())
//	    				continue ;//����Ҫ����
//	    			
//	    			MCmd mc = rr.getRequestCmd() ;
//	    			MCmd rmc = null ;
//	    			try
//	        		{
//	    				if(log.isDebugEnabled())
//	    				{
//	    					log.debug(this.clientInfo.getDevId()+">>"+mc.toString()) ;
//	    				}
//	    	    		mc.writeOut(outstream) ;
//	    	    		rmc = MCmd.readFrom(instream);
//	    	    		
//	    	    		if(!mc.cmd.equals(rmc.cmd))//�����д�λ
//	    	    			rmc = MCmd.readFrom(instream) ;
//	    	    		
//	    	    		if(!mc.cmd.equals(rmc.cmd))//���ǲ���ͬ
//	    	    			continue ;//��β�����
//	    	    		
//	    	    		if(log.isDebugEnabled())
//	    				{
//	    	    			log.debug(this.clientInfo.getDevId()+"<<"+rmc.toString()) ;
//	    				}
//	    	    		
//	    	    		ds = rr.onResponse(rmc) ;
//	    	    		//else
//	    	    		
//	    	    		//fire listener
//	    	    		fireCmdReqResp(this,mc,rmc,null,ds);
//	    	    		
//	    	    		if(ds==MCmdDevPlug.DevStatus.error)
//	    	        	{
//	    	    			this.close() ;
//	    	    				    	    			
//	    	        		return false;
//	    	        	}
//	    	    		else if(ds==MCmdDevPlug.DevStatus.abnormal)
//	    	    		{
//	    	    			
//	    	    			break ;
//	    	    		}
//	        		}
//	        		catch(Exception ee)
//	        		{
//	        			if(log.isDebugEnabled())
//	    				{
//	    	    			log.debug(this.clientInfo.getDevId()+"<err<"+ee.getMessage()) ;
//	    				}
//	        			
//	        			ds = rr.onResponseError() ;
//	        			
//	        			fireCmdReqResp(this,mc,rmc,ee,ds);
//	        			
//	        			if(ds==MCmdDevPlug.DevStatus.error)
//	    	        	{
//	    	    			this.close() ;
//	    	        		return false;
//	    	        	}
//	        			else if(ds==MCmdDevPlug.DevStatus.abnormal)
//	    	    		{
//	    	    			break ;
//	    	    		}
//	        		}
//	    		}
//	    	}
//	        
//	    	ds = devPlug.onCmdEnd() ;
//	    	if(ds==MCmdDevPlug.DevStatus.error)
//	    	{
//	    		this.close() ;
//	    		return false;
//	    	}
//	    	
//			return true;
//    	}
//    	finally
//    	{
//    		bCmdRun = false ;
//    		notifyAll() ;
//    	}
//	}
//    
//    
//    
//
//    public void run()
//    {
//        try
//        {
//        	
//            MCmd mc = null;
//            
//            //�ն�CONNECT OK���ػ�û��tcp��Ӧ��
//            //����Ӧ���Ե�,�ٷ���ָ��
//            Thread.sleep(1000) ;
//            
//            int devtry = 0 ;
//            boolean bdev_ok = false;
//            while(devtry<3)
//            {
//            	devtry ++ ;
//	            try
//	            {
//		            //������Ҫ����һ��δ���յ����ݽ������
//	            	int avis = instream.available() ;
//	            	if(avis>0)
//	            	{
//	            		instream.read(new byte[avis]) ;
//	            	}
//	            	
//		            mc = new MCmd("devid",null);
//		            mc.writeOut(outstream) ;
//		            mc = MCmd.readFrom(instream);
//		            System.out.println(">>recv com="+mc) ;
//		            //clientInfo.devId = "no_id" ;
//		            if(mc!=null&&"devid".equals(mc.cmd))
//		            {
//		            	bdev_ok = true ;
//		            	break ;
//		            }
//	            }
//	            catch(SocketTimeoutException ste)
//	            {
//	            	ste.printStackTrace() ;
//	            	continue ;
//	            }
//            }
//            
//            if(!bdev_ok)
//            {
//            	close() ;
//            	
//            	return ;//error
//            }
//            
//            String new_id = mc.getContent() ;
//            if(Convert.isNullOrEmpty(new_id))
//            {
//            	close() ;
//            	return ;
//            }
//            new_id = new_id.trim() ;
//            String type = "1" ;
//            
//            int p = new_id.indexOf('#') ;
//            if(p>0)
//            {
//            	type = new_id.substring(p+1) ;
//            	new_id = new_id.substring(0,p) ;
//            }
//            
//            
//            if("*".equals(new_id))
//            {
//            	new_id += newTmpId() ;
//            }
//            //����ϵĶ�Ӧid����ɾ��
//            MCmdServerForClient oldfc = getClientById(new_id) ;
//            if(oldfc!=null)
//            {
//            	oldfc.close() ;
//            }
//            
//            devPlug = getNewDevPlugByType(type) ;
//            if(devPlug==null)
//            {
//            	close() ;
//            	return ;
//            }
//            
//            //System.out.println(">>dev id found="+mc.getContent()) ;
//            clientInfo.devId = new_id;
//            clientInfo.devType = type ;
//            
//            devPlug.clientInfo = clientInfo ;
//            
//            //clientInfo.devId = "no_id" ;
//            System.out.println(">>dev id found="+clientInfo.devId) ;
//            
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//            log.error("��Ϣ������󣭶Ͽ�����"+e.getMessage());
//            
//            close() ;
//        }
//        
//    }
//    
//    
//
//    public void close()
//    {
//    	if (instream != null)
//        {
//        	try
//        	{
//        		instream.close();
//        		instream = null;
//        	}
//        	catch(Exception e)
//        	{}
//        }
//    	
//    	if (outstream != null)
//        {
//        	try
//        	{
//        		outstream.close();
//        		outstream = null;
//        	}
//        	catch(Exception e)
//        	{}
//        }
//            
//        if (socket != null)
//        {
//        	try
//        	{
//	            socket.close();
//	            socket = null;
//        	}
//        	catch(Exception e)
//        	{}
//        }
//        
//        Future<Boolean> f = future;
//        if(f!=null)
//        {
//        	f.cancel(true) ;//�������ص������������У���ر�֮
//        }
//        
//        decreaseCount(this);
//    }

	
}