package org.iottree.core.conn.masyn;

import java.io.*;
import java.util.*;

import org.iottree.core.Config;

/**
 * asyn cmd,include cmd line and data after
 * @author Jason Zhu
 *
 */
public class MCmdAsyn //implements IQueObjId
{
	public static final int DEFAULT_PORT = 20090 ;
	public static final int MAX_HEAD_LEN = 10240000 ;
	
	
	public static final String CH_CMD = "cmd" ;
	public static final String CH_LEN = "len" ;
	
	
//	public static final int ACK_NULL = 0 ;
//	public static final int ACK_REQ = 1 ;
//	public static final int ACK_RESP = 2 ;
	
	
	/**
	 * need receiver return ack cmd msg id
	 */
	public static final String CH_ACK_ID = "ack_id" ;
	/**
	 * ack sor cmd
	 */
	public static final String CH_ACK_CMD = "ack_cmd" ;
	
	private static final String CMD_ACK = "_ack_" ;
	
	private static final String IS_COMPACT = "_cp_" ;
	
	/**
	 * file cmd
	 */
	public static final String CH_FILE = "_f_" ;

	
	HashMap<String,String> cmdHead=null;
	
    byte[] content;
    
    /**
     * compact or not
     */
    boolean bCompact = false;
    
    public MCmdAsyn(String cmd, byte[] content)
    {
    	this(cmd,content,null) ;
    }

    /**
     * simple cmd
     *  has cmd and has not content
     * @param cmd
     * @param bcompact
     */
    public MCmdAsyn(String cmd,boolean bcompact)
    {
    	this(cmd,null) ;
    	bCompact = bcompact ;
    	if(bcompact && cmd.indexOf('$')>=0)
    		throw new IllegalArgumentException("invalid compact cmd which has $ char") ;
    		
    }
    
    /**
     * cmd msg wish need receiver return ack.
     * IAckListener will be called when receive return ack msg
     * @param cmd
     * @param content
     * @param ackid 
     */
    public MCmdAsyn(String cmd, byte[] content,String ackid)
    {
    	this(cmd, content,ackid,null) ;
    }
    
    private MCmdAsyn(String cmd, byte[] content,String ackid,String ackcmd)
    {
        if (cmd.indexOf('\r') >= 0 || cmd.indexOf('\n') >= 0)
            throw new IllegalArgumentException("name cannot has multi line");
//        if(CMD_ACK.equals(cmd))
//        	throw new 
        
        cmdHead=new HashMap<String,String>();

        cmdHead.put(CH_CMD,cmd) ;
        
        // set cmd that will recv ack. when recved ack,it will callback
        //{@see IAckListener}
        if(ackid!=null&&!"".equals(ackid))
        	cmdHead.put(CH_ACK_ID, ackid) ;
        if(ackcmd!=null&&!"".equals(ackcmd))
        	cmdHead.put(CH_ACK_CMD, ackcmd) ;
        setContent(content) ;
    }
    
    private MCmdAsyn(HashMap<String,String> h,byte[] cont)
    {
    	cmdHead = h ;
    	content = cont ;
    }
    
    public String getQueObjId()
    {
    	String cmd = getCmd() ;
    	String ackid = getAckId() ;
    	if(ackid==null)
    		return cmd ;
    	return cmd+"_"+ackid ;
    }

    public String getCmd()
    {
        return cmdHead.get(CH_CMD);
    }
    
    
    public boolean isCompact()
    {
    	return bCompact ;
    }
    /**
     * 
     * @return
     */
    public boolean isAckCmd()
    {
    	return CMD_ACK.equals(getCmd()) ;
    }
    
    public String getAckId()
    {
    	return cmdHead.get(CH_ACK_ID);
    }
    
    /**
     * 
     * @param ackid
     */
    public void setAckId(String ackid)
    {
    	if(ackid!=null&&!"".equals(ackid))
        	cmdHead.put(CH_ACK_ID, ackid) ;
    	else
    		cmdHead.remove(CH_ACK_ID) ;
    }
    /**
     * if it's Ack cmd,then get ack sor cmd
     * @return
     */
    String getAckCmd()
    {
    	return cmdHead.get(CH_ACK_CMD) ;
    }
    /**
     * create a ack msg from this cmd
     * @return
     */
    public MCmdAsyn createAckCmd()
    {
    	String curcmd = getCmd() ;
    	if(CMD_ACK.equals(curcmd))
    		return null ;
    	String ackid = cmdHead.get(CH_ACK_ID) ;
    	if(ackid==null||"".equals(ackid))
    		return null ;//cannot create 
    	return new MCmdAsyn(CMD_ACK,null,ackid,curcmd) ;
    }
    
    //sender file may locate anywhere,then it set by setFiles
    //recver will recv file to tmp dir
    private transient ArrayList<File> tmpFiles = null ;
    
        
    public void setFiles(File[] fs)
    	throws IOException
    {
    	if(fs!=null&&fs.length>0)
    	{
    		ArrayList<File> fls = new ArrayList<File>(fs.length) ;
    		String finfo = "" ;
    		for(File f:fs)
    		{
    			if(!f.exists())
    				throw new IOException("not found file="+f.getCanonicalPath()) ;
    			finfo += f.getName()+":"+f.length()+":"+f.getCanonicalPath()+"|" ;
    			fls.add(f) ;
    		}
    		cmdHead.put(CH_FILE, finfo);
    		tmpFiles = fls;
    	}
    	else
    	{
    		cmdHead.remove(CH_FILE);
    		tmpFiles = null ;
    	}
    }
    
    
    public void setFiles(String[] f_paths)
    	throws IOException
    {
    	if(f_paths==null||f_paths.length==0)
    		setFiles((File[])null) ;
    	else
    	{
    		File[] fs = new File[f_paths.length] ;
    		for(int i=0;i<f_paths.length;i++)
    			fs[i] = new File(f_paths[i]) ;
    		setFiles(fs) ;
    	}
    }
    
    
    public void setFiles(List<File> fs)
    	throws IOException
    {
    	if(fs==null||fs.size()<=0)
    		setFiles((File[])null) ;
    	else
    	{
    		File[] fss = new File[fs.size()] ;
    		fs.toArray(fss) ;
    		setFiles(fss);
    	}
    }
    
    
    public List<File> getFiles()
    {
    	return tmpFiles ;
    }


    public byte[] getContent() 
    {
        return content;
    }
    
    public void setContent(byte[] bs)
    {
    	this.content = bs;
    	int len = 0 ;
        if(bs!=null)
        	len = content.length ;
        
        cmdHead.put(CH_LEN, ""+len) ;
    }
    
    public String getHeadStr()
    {//[k1=a1&b=xx]
    	StringBuilder sb = new StringBuilder() ;
    	sb.append('[') ;
    	for(Map.Entry<String,String> se:cmdHead.entrySet())
    	{
    		sb.append(se.getKey()).append('=').append(se.getValue()).append('&') ;
    	}
    	
    	sb.setCharAt(sb.length()-1, ']') ;
    	return sb.toString() ;
    }
    
    //out of [],it provider more compact head format
    //e.g. heart pulse msg,will has small flow
    public byte[] getCompactHead()
    {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream() ;
    	try
    	{
	    	baos.write('$');
	    	baos.write(getCmd().getBytes()) ;
	    	baos.write('$');
	    	return baos.toByteArray() ;
    	}
    	catch(Exception ee)
    	{
    		return null ;
    	}
    }
    
    public void writeOut(OutputStream s)
		throws Exception
	{
		writeOut(s,true,null) ;
	}
    
    public void writeOut(OutputStream s,ITransListener tlis)
		throws Exception
	{
    	writeOut(s,true,tlis) ;
	}

    private void writeOut(OutputStream s,boolean bwithfile,ITransListener tlis)
    	throws Exception
    {
    	long total_len = -1 ;
    	long r = 0 ;
    	if(tlis!=null)
    	{
    		total_len = calTotalPackLen(bwithfile) ;
    		tlis.onTransStarted(total_len) ;
    		tlis.onTransProcess(r);
    	}
    	
        byte[] bn = null;
        if(bCompact)
        {
        	bn = getCompactHead() ;
        }
        else
        {
        	bn = getHeadStr().getBytes("UTF-8");
        }
        //System.out.println("write head=="+getHeadStr()) ;
        
        
        s.write(bn, 0, bn.length);
        r += bn.length ;
        if(tlis!=null)
        {
        	tlis.onTransProcess(r);
        }
        
        if(bCompact)
        {
        	s.flush();
        	return ;
        }
        //s.flush() ;
        if(content!=null)
        {
        	s.write(content) ;
        	r += content.length ;
        	if(tlis!=null)
            {
            	tlis.onTransProcess(r);
            }
        }
        s.flush();
        
        if(bwithfile&&tmpFiles!=null)
        {
        	for(File tmpf:tmpFiles)
        	{
        		r += writeStreamFromFile(tmpf,s,tlis,r);
        	}
        }
        
        if(tlis!=null)
        	tlis.onTransEnd() ;
    }
    
    
    public long calTotalPackLen(boolean bwithfile)
    {
    	long r = 0 ;
    	byte[] bn = null;
        if(bCompact)
        {
        	bn = getCompactHead() ;
        }
        else
        {
        	try
        	{
        		bn = getHeadStr().getBytes("UTF-8");
        	}
        	catch(Exception e)
        	{
        		throw new RuntimeException("encode err") ;
        	}
        }
        //System.out.println("write head=="+getHeadStr()) ;
        r += bn.length;
        
        if(bCompact)
        {
        	return r;
        }
        //s.flush() ;
        if(content!=null)
        	r += content.length ;
        
        //System.out.println("writeOut>> tmpfs size="+tmpFiles.size());
        if(bwithfile&&tmpFiles!=null)
        {
        	for(File tmpf:tmpFiles)
        	{
        		//System.out.println("writeOut>>"+tmpf.getCanonicalPath()+" len="+tmpf.length()+" ");
        		//writeStreamFromFile(tmpf,s);
        		r += tmpf.length() ;
        	}
        }
        return r ;
    }
    /**
     * pack cmd with no file
     * @return
     */
    public byte[] packWithNoFile() throws Exception
    {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream() ;
    	writeOut(baos,false,null) ;
    	return baos.toByteArray();
    }
    
    
    public static MCmdAsyn unpackWithNoFile(byte[] bs) throws Exception
    {
    	ByteArrayInputStream bais = new ByteArrayInputStream(bs) ;
    	return readFrom(null,bais,false) ;
    }
    
    private static long writeStreamFromFile(File rf,OutputStream outputs,
    		ITransListener tls,long sendlen)
		throws IOException
	{
    	if(!rf.exists())
    		throw new IOException("no file found:"+rf.getCanonicalPath()) ;
		byte[] buf = new byte[1024] ;
		//long rlen = 0 ;
		int len = 0 ;
		FileInputStream fis = null ;
		try
		{
			fis = new FileInputStream(rf) ;
			
			//System.out.println("writeStreamFromFile>>"+rf.getCanonicalPath()+" len="+rf.length()+" ");
	    	while((len=fis.read(buf))>=0)
	    	{
	    		outputs.write(buf, 0, len) ;
	    		outputs.flush() ;
	    		sendlen += len ;
	    		if(tls!=null&&len>0)
	    			tls.onTransProcess(sendlen) ;
	    	}
	    	return sendlen ;
		}
		finally
		{
			if(fis!=null)
				fis.close() ;
		}
	}
    
    public String toString()
    {
    	return getHeadStr();
    }
    
    static HashMap<String,String> parseHeadStr(String s)
    {
    	if(s==null)
    		return null ;
    	
    	if(s.startsWith("["))
    		s = s.substring(1) ;
    	if(s.endsWith("]"))
    		s = s.substring(0,s.length()-1) ;

    	HashMap<String,String> ret = new HashMap<String,String>() ;
    	for(String tmps:s.split("&"))
    	{
    		int i = tmps.indexOf('=') ;
    		if(i<0)
    		{
    			ret.put(tmps, "") ;
    		}
    		else
    		{
    			ret.put(tmps.substring(0,i), tmps.substring(i+1)) ;
    		}
    	}
    	
    	return ret ;
    }

    public static HashMap<String,String> readHeadStrFromStream(InputStream s) throws Exception
    {
    	int b=-1;
    	int st = 0 ;
    	//byte[] buf = new byte[MAX_HEAD_LEN] ;
    	ByteArrayOutputStream baos = new ByteArrayOutputStream() ;
    	int c = 0 ;
    	boolean bok = false;
        while (!bok && (b = s.read()) != -1)
        {
        	//System.out.println("--["+(char)b+"]") ;
        	switch(st)
        	{
        	case 0:
        		if(b=='[')
        		{
        			st = 1 ;
        		}
        		else if(b=='$')
        		{
        			st = 10 ;
        		}
        		break ;
        	case 1:
        		if(b==']')
        		{
        			bok = true ;
        		}
        		else
        		{
        			//buf[c] = (byte)b ;
        			baos.write(b);
        			c ++ ;
        			//System.out.println(new String(buf,0,c)) ;
        			if(c==MAX_HEAD_LEN)
        				return null ;
        		}
        		break ;
        	case 10:
        		if(b=='$')
        		{//compact format
        			bok = true ;
        		}
        		else
        		{
        			//buf[c] = (byte)b ;
        			baos.write(b);
        			c ++ ;
        			//System.out.println(new String(buf,0,c)) ;
        			if(c==MAX_HEAD_LEN)
        				return null ;
        		}
        		break ;
        	default:
        		break ;
        	}
            
        }
        
        if(!bok&&b==-1)//make sure find tcp exception
        	throw new Exception("end of stream") ;

        if (b == -1 || !bok || c<=0)
        {
            return null;
        }
        
        byte[] buf = baos.toByteArray() ;
        
        if(st == 10)
        {
        	
        	String cmd = new String(buf,0,c,"UTF-8") ;
        	HashMap<String,String> ret =new HashMap<String,String>() ;
        	ret.put(CH_CMD, cmd) ;
        	ret.put(IS_COMPACT,"1") ;
        	return ret ;
        }
        
        String headstr = new String(buf,0,c,"UTF-8") ;
        //System.out.println("MCmdAsyn read headstr="+headstr) ;
        return parseHeadStr(headstr) ;
    }
    
    public static MCmdAsyn readFrom(InputStream s) throws Exception
    {
    	return readFrom(null,s) ;
    }
    
    public static MCmdAsyn readFrom(String filebase,InputStream s) throws Exception
    {
    	return readFrom(filebase,s,true);
    }
    
    private static MCmdAsyn readFrom(String filebase,InputStream s,boolean readfile) throws Exception
    {
    	if(filebase==null||filebase.equals(""))
    		filebase = Config.getDataTmpDir()+"/_cmd_asyn_files/" ;
    	
    	HashMap<String,String> h = readHeadStrFromStream(s) ;
    	if(h==null)
    		return null ;
    	
    	String cmd = h.get(CH_CMD) ;
    	if(cmd==null||cmd.equals(""))
    		return null ;
    	
    	if(h.get(IS_COMPACT)!=null)
    	{
    		MCmdAsyn r = new MCmdAsyn(cmd,true);
    		return r ;
    	}
    	
    	int len = Integer.parseInt(h.get(CH_LEN)) ;
    	
    	byte[] cont = null ;
    	if(len>0)
    	{
    		cont= new byte[len] ;
    		int c = 0,i ;
    		while((i=s.read(cont, c, len-c))>=0)
    		{
    			c += i ;
    			if(c==cont.length)
    				break ;
    		}
    		
    		if(c<cont.length)
    			return null ;
    	}
    	
    	MCmdAsyn r = new MCmdAsyn(h,cont);
    	String fnstr = h.get(CH_FILE) ;
    	if(fnstr!=null&&!fnstr.equals(""))
    	{//has files
    		File filebf = new File(filebase) ;
        	if(!filebf.exists())
        		filebf.mkdirs() ;
        	
    		StringTokenizer st = new StringTokenizer(fnstr,"|") ;
    		ArrayList<File> fls = new ArrayList<File>() ;
    		while(st.hasMoreTokens())
    		{
    			String tmps = st.nextToken() ;
    			if(tmps==null||tmps.equals(""))
    				continue ;
    			int i = tmps.indexOf(':') ;
    			if(i<=0)
    				throw new Exception("invalid file info="+fnstr);
    			String fn = tmps.substring(0,i) ;
    			String strlen = tmps.substring(i+1) ;
    			String sorpath = null ;
    			int k = strlen.indexOf(':') ;
    			if(k>0)
    			{
    				sorpath = strlen.substring(k+1) ;//远程传过来的内容没有用
    				strlen = strlen.substring(0,k);
    			}
    			long flen = Long.parseLong(strlen) ;
    			if(flen<=0)
    				continue ;
    			
    			if(readfile)
    			{//remote tranfer
	    			File tmpf = new File(filebf,fn);
	    			readStreamToFile(tmpf,flen,s) ;
	    			//
	    			fls.add(tmpf) ;
    			}
    			else
    			{//pack or unpack local,file location is not change
    				if(sorpath==null)
    					continue ;
    				File tmpf = new File(sorpath) ;
    				fls.add(tmpf) ;
    			}
    		}
    		r.tmpFiles = fls ;
    	}
    	
    	return r;
    }
    
    
    private static void readStreamToFile(File savef,long flen,InputStream inputs)
    	throws IOException
    {
    	byte[] buf = new byte[1024] ;
    	long rlen = 0 ;
    	int len = 0 ;
    	FileOutputStream fos = null ;
    	try
    	{
    		fos = new FileOutputStream(savef) ;
    		int rleft = 1024;
    		if(flen - rlen<1024)
    			rleft = (int)(flen-rlen) ;// prevent read next file content
	    	while((len=inputs.read(buf,0,rleft))>=0)
	    	{
	    		fos.write(buf, 0, len) ;
	    		rlen += len ;
	    		if(rlen>=flen)
	    			break ;
	    		
	    		if(flen - rlen<1024)
	    			rleft = (int)(flen-rlen) ;// prevent read next file content
	    	}
    	}
    	finally
    	{
    		if(fos!=null)
    			fos.close() ;
    	}
    }
    
    
    public static void main(String[] args) throws Exception
    {
    	MCmdAsyn m = new MCmdAsyn("cmd1",null,"ack001") ;
    	System.out.println(m) ;
    	ByteArrayOutputStream baos = new ByteArrayOutputStream() ;
    	m.writeOut(baos) ;
    	byte[] bs = baos.toByteArray() ;
    	System.out.println("baso len="+bs.length) ;
    	
    	MCmdAsyn om = MCmdAsyn.readFrom(new ByteArrayInputStream(bs)) ;
    	System.out.println(om) ;
    	
    	System.out.println("\r\n\r\n") ;
    	m = new MCmdAsyn("ht",false) ;
    	System.out.println(m) ;
    	baos = new ByteArrayOutputStream() ;
    	m.writeOut(baos) ;
    	bs = baos.toByteArray() ;
    	System.out.println("baso len="+bs.length) ;
    	om = MCmdAsyn.readFrom(new ByteArrayInputStream(bs)) ;
    	System.out.println(om) ;
    	
    	System.out.println("\r\n\r\n") ;
    	m = new MCmdAsyn("ht",true) ;
    	System.out.println(m) ;
    	baos = new ByteArrayOutputStream() ;
    	m.writeOut(baos) ;
    	bs = baos.toByteArray() ;
    	System.out.println("baso len="+bs.length) ;
    	om = MCmdAsyn.readFrom(new ByteArrayInputStream(bs)) ;
    	System.out.println(om) ;
    }
}