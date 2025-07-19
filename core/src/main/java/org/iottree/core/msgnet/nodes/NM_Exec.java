package org.iottree.core.msgnet.nodes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNCxtValSty;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONArray;
import org.json.JSONObject;

public class NM_Exec extends MNNodeMid implements IMNRunner
{
	public static class CmdSeg
	{
		MNCxtValSty valsty = MNCxtValSty.vt_str; //FOR_STR_LIST
		String subN = null ;
		boolean quotationMk = false;
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.put("valsty", valsty.name()) ;
			jo.putOpt("subn", subN) ;
			jo.put("qm", this.quotationMk) ;
			return jo ;
		}
		
		public void fromJO(JSONObject jo)
		{
			this.valsty = MNCxtValSty.valueOf(jo.optString("valsty")) ;
			if(this.valsty==null)
				this.valsty =  MNCxtValSty.vt_str ;
			this.subN = jo.optString("subn") ;
			this.quotationMk = jo.optBoolean("qm",false) ;
		}
	}
	
	String workDir = null ;
	
	String cmd = null ;
	
	ArrayList<CmdSeg> cmdSegs = new ArrayList<>() ;
	
	long timeOut = -1 ;
	
	String outEncod = null;// "UTF-8" ;
	
	private File workDirF = null ;
	
	@Override
	public String getColor()
	{
		return "#fa9275";
	}
	
	@Override
	public String getIcon()
	{
		return "\\uf013";
	}

	@Override
	public int getOutNum()
	{
		return 3;
	}

//	@Override
	public String getTP()
	{
		return "exec";
	}

	@Override
	public String getTPTitle()
	{
		return g("exec");
	}
	

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNotNullEmpty(this.cmd))
			return true ;
		
		for(CmdSeg seg:this.cmdSegs)
		{
			if(Convert.isNotNullEmpty(seg.subN))
				return true ;
		}
		
		failedr.append("no cmd set") ;
		return false;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("cmd", this.cmd) ;
		jo.putOpt("work_dir", this.workDir) ;
		JSONArray segs = new JSONArray() ;
		for(CmdSeg seg:this.cmdSegs)
		{
			segs.put(seg.toJO()) ;
		}
		jo.putOpt("segs", segs) ;
		jo.put("timeout", this.timeOut) ;
		jo.putOpt("out_enc", this.outEncod) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.cmd = jo.optString("cmd") ;
		this.workDir = jo.optString("work_dir") ;
		JSONArray jarr = jo.optJSONArray("segs") ;
		ArrayList<CmdSeg> csegs= new ArrayList<>() ;
		if(jarr!=null)
		{
			int n = jarr.length() ; 
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				CmdSeg cs = new CmdSeg() ;
				cs.fromJO(tmpjo);
				csegs.add(cs) ;
			}
		}
		this.cmdSegs = csegs ;
		this.timeOut = jo.optLong("timeout", -1) ;
		this.outEncod = jo.optString("out_enc") ;
	}
	
	
	// --------------
	
	boolean bRun = false;
	
	@Override
	protected void onAfterLoaded()
	{
		if(Convert.isNotNullEmpty(this.workDir))
			this.workDirF = new File(this.workDir) ;
		else
			this.workDirF = null ;
		
		if(Convert.isNullOrEmpty(this.outEncod))
			this.outEncod = Charset.defaultCharset().name();
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		try
		{
			bRun = true ;
			StringBuilder sb = new StringBuilder() ;
			if(Convert.isNotNullEmpty(cmd))
				sb.append(cmd) ;
			
			for(CmdSeg cs:this.cmdSegs)
			{
				Object tmpo = cs.valsty.RT_getValInCxt(cs.subN, this.getBelongTo(), this, msg) ;
				if(tmpo==null)
				{
					this.RT_DEBUG_ERR.fire("exec", cs.valsty.name()+"."+cs.subN+" is null");
					return null;
				}
				if(sb.length()>0)
					sb.append(" ");
				if(cs.quotationMk)
					sb.append("\"").append(tmpo).append("\"") ;
				else
					sb.append(tmpo) ;
			}

			if(sb.length()<=0)
			{
				this.RT_DEBUG_ERR.fire("exec", "no cmd set");
				return null ;
			}
			
			RTOut rto = RT_runCmd(sb.toString(),this.workDirF,this.outEncod,this.timeOut) ;
			this.RT_DEBUG_ERR.clear("exec");
			return rto ;
		}
		catch(Exception ee)
		{
			this.RT_DEBUG_ERR.fire("exec", ee.getMessage(), ee);
			return null ;
		}
		finally
		{
			bRun = false;
		}
	}
	
	public final static RTOut RT_runCmd(String cmd,File work_dir,String out_enc,long time_out) throws Exception
	{
		//System.out.println("cmd="+cmd) ;
		List<String> ccs = splitCommand(cmd);
		ProcessBuilder processBuilder = new ProcessBuilder(ccs);
        processBuilder.redirectErrorStream(true);  // Combine stdout and stderr
        if(work_dir!=null)
        {
        	processBuilder.directory(work_dir);
        }
        

        Process process = processBuilder.start();

        // Create threads to read stdout and stderr
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        Future<String> stdoutFuture = executorService.submit(() -> readStream(process.getInputStream(),out_enc));
        Future<String> stderrFuture = executorService.submit(() -> readStream(process.getErrorStream(),out_enc));

        // Wait for the process to complete or timeout
        
         
        if(time_out>0)
        {
	        boolean finished = process.waitFor(time_out,TimeUnit.MILLISECONDS);
	        if (!finished)
	        {
	            process.destroy();
	            //MNMsg m = new MNMsg().asPayload("Process time out") ;
	           // RTOut rto = RTOut.createOutIdx().asIdxMsg(1, m) ;
	           // return rto;
	            throw new TimeoutException("Process time out") ;
	        }
        }
        else
        {
        	//process.waitFor() ;
        }

        // Get the results
        String stdout = stdoutFuture.get();
        String stderr = stderrFuture.get();
        int exitc = process.waitFor();

        executorService.shutdown();
        
        RTOut rto = RTOut.createOutIdx();
        
        if(Convert.isNotNullEmpty(stdout))
        {
        	MNMsg m = new MNMsg().asPayload(stdout) ;
            rto.asIdxMsg(0, m) ;
        }
        
        if(Convert.isNotNullEmpty(stderr))
        {
        	MNMsg m = new MNMsg().asPayload(stdout) ;
            rto.asIdxMsg(1, m) ;
        }
        
        MNMsg m = new MNMsg().asPayload(exitc) ;
        rto.asIdxMsg(2, m) ;
        return rto ;
	}
	
    private static String readStream(InputStream inputStream,String encd) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,encd));
        StringBuilder output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line).append(System.lineSeparator());
        }

        return output.toString();
    }
    
    private static List<String> splitCommand(String command) {
        List<String> commands = new ArrayList<>();
        StringBuilder currentArg = new StringBuilder();
        boolean inQuotes = false;
        char quoteChar = '\0';

        for (char c : command.toCharArray()) {
            if (inQuotes) {
                if (c == quoteChar) {
                    inQuotes = false;
                } else {
                    currentArg.append(c);
                }
            } else {
                if (c == '"' || c == '\'') {
                    inQuotes = true;
                    quoteChar = c;
                } else if (Character.isWhitespace(c)) {
                    if (currentArg.length() > 0) {
                        commands.add(currentArg.toString());
                        currentArg.setLength(0);
                    }
                } else {
                    currentArg.append(c);
                }
            }
        }

        if (currentArg.length() > 0) {
            commands.add(currentArg.toString());
        }

        return commands;
    }
    
	@Override
	public String RT_getOutTitle(int idx)
	{
		switch(idx)
		{
		case 0:
			return "stdout" ;
		case 1:
			return "stderr" ;
		case 2:
			return "return code";
		default:
			return null ;
		}
	}

	@Override
	public boolean RT_start(StringBuilder failedr)
	{
		return false;
	}

	@Override
	public void RT_stop()
	{
		
	}

	@Override
	public boolean RT_isRunning()
	{
		return bRun;
	}

	@Override
	public boolean RT_isSuspendedInRun(StringBuilder reson)
	{
		return false;
	}

	@Override
	public boolean RT_runnerEnabled()
	{
		return true;
	}

	@Override
	public boolean RT_runnerStartInner()
	{
		return true;
	}
}
