package org.iottree.core.conn;

import java.io.Writer;
import java.util.List;
import java.util.UUID;

import org.iottree.core.conn.masyn.MCmdAsyn;
import org.iottree.core.conn.masyn.MCmdAsynEndPoint;
import org.iottree.core.conn.masyn.MCmdAsynStateM;

public class ConnPtOpcAgent extends ConnPtTcpAccepted
{
	private Thread recvTh = null ;
	
	@Override
	public List<String> transBindIdToPath(String bindid)
	{
		return null;
	}

	@Override
	public void writeBindBeSelectedTreeJson(Writer w) throws Exception
	{
		w.write("{\"id\":\""+UUID.randomUUID().toString()+"\"");
    	w.write(",\"nc\":0");
    	w.write(",\"icon\": \"fa fa-sitemap fa-lg\"");
    	
    	w.write(",\"text\":\""+this.getTitle()+"\"");
    	w.write(",\"state\": {\"opened\": true}");
		w.write("}");
	}

	@Override
	public String getConnType()
	{
		return "opc_agent";
	}

	@Override
	public String getStaticTxt()
	{
		return null;
	}

	
	public List<String> listOpcProgIds()
	{
		return null ;
	}
	
	private MCmdAsynEndPoint cmdEP = null ;
	/**
	 * override it will do something
	 * when conn ready,it will start recv msg thread
	 * @param r
	 */
	protected void onConnReadyOrNot(boolean r)
	{
		if(r)
		{//
			startRecv();
		}
		else
		{
			stopRecv();
		}
	}
	
	private MCmdAsynStateM asynStatM = new MCmdAsynStateM()
			{

				@Override
				public void onMCmdAsynRecved(MCmdAsyn mca)
				{
					
				}

				@Override
				public void onMCmdAsynBroken()
				{
					
				}

				@Override
				public boolean checkStateMOk()
				{
					
					return false;
				}

				@Override
				public StateRes onPulseStateMachine()
				{
					
					return null;
				}
		
			};
	
	private synchronized void startRecv()
	{
		stopRecv();
		//
		cmdEP = new MCmdAsynEndPoint(this.getId(),this,asynStatM);
		cmdEP.setConnPtStream(this);
		cmdEP.start();
	}
	
	private synchronized void stopRecv()
	{
		if(cmdEP==null)
		{
			return ;
		}
		
		cmdEP.dispose();
		cmdEP = null ;
	}
	
}
