package org.iottree.server;

import java.io.*;
import java.net.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class ServerCtrlUI extends Panel
{
	static int DEFAULT_CTRL_PORT = 55311 ;
	
	int maxLineNum = 100;

	// InputStream inputStream = null ;
	Vector contBuffer = new Vector();

	BorderLayout borderLayout1 = new BorderLayout();

	//JScrollPane..SCROLLBARS_AS_NEEDED);

	JTextArea contTextArea = new JTextArea();
	JScrollPane scrollPane1 = new JScrollPane(contTextArea);

	Panel cmdPanel = new Panel();

	BorderLayout borderLayout2 = new BorderLayout();

	Button btnSendCmd = new Button("Enter");

	TextField tfCmdTxt = new TextField();

	// Construct the applet
	public ServerCtrlUI()
	{
		try
		{
			jbInit();
			myInit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// Component initialization
	private void jbInit() throws Exception
	{
		this.setLayout(borderLayout1);
		contTextArea.setBackground(Color.black);
		contTextArea.setForeground(Color.white);
		this.add(scrollPane1, BorderLayout.CENTER);
		//scrollPane1.add(contTextArea, null);

		cmdPanel.setLayout(borderLayout2);
		cmdPanel.add(tfCmdTxt, BorderLayout.CENTER);
		cmdPanel.add(btnSendCmd, BorderLayout.EAST);

		this.add(cmdPanel, BorderLayout.SOUTH);
	}

	private void myInit() throws Exception
	{
		btnSendCmd.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				doSendCmd();
			}
		});

		tfCmdTxt.addKeyListener(new KeyAdapter()
		{

			public void keyPressed(KeyEvent e)
			{
				// JOptionPane.showMessageDialog(ServerCtrlUI.this,
				// ""+(int)e.getKeyChar()) ;
				if (e.getKeyChar() == '\r' || e.getKeyChar() == '\n')
				{
					doSendCmd();
				}
				else
				{
					super.keyPressed(e);
				}
			}

		});
		
		tfCmdTxt.requestFocus() ;
	}
	
	private void doSendCmd()
	{
		try
		{
			doSendCmdDo();
		}
		catch(Exception ee)
		{
			onRecvStr("XXX cmd error:"+ee.getMessage()+"\n") ;
		}
	}

	private void doSendCmdDo() throws Exception
	{
		String cmd = tfCmdTxt.getText() ;
		if(cmd==null)
			return ;
	
		tfCmdTxt.setText("");
		tfCmdTxt.requestFocus() ;
		cmd = cmd.trim() ;
		if("".equals(cmd))
			return ;
		
		if("?".equals(cmd))
		{
			setDisLine("************Tomato Server Control Console\n") ;
			setDisLine("conn [server] [port] to connect to server!\n") ;
			
			return ;
		}
		
		if("cls".equalsIgnoreCase(cmd))
		{
			 clear();
			 return ;
		}
		
		if("conn".equals(cmd)||cmd.startsWith("conn "))
		{
			if(recvTh!=null)
			{
				setDisLine("Already Connected\n") ;
				return ;
			}
			StringTokenizer st = new StringTokenizer(cmd," :") ;
			int ct = st.countTokens() ;
			st.nextToken() ;
			if(ct==1)
				connToServer("localhost",DEFAULT_CTRL_PORT) ;
			else if(ct==2)
				connToServer(st.nextToken(),DEFAULT_CTRL_PORT) ;
			else if(ct>2)
				connToServer(st.nextToken(),Integer.parseInt(st.nextToken())) ;
			
			return ;
		}
		
		setDisStr(cmd) ;
		
		PrintStream ps = outPS ;
		if(ps!=null)
			ps.println(cmd) ;
	}
	
	Thread recvTh = null ;
	Socket socket = null ;
	InputStream inputs = null ;
	OutputStream outputs = null ;
	PrintStream outPS = null ;
	
	private void connToServer(String ip,int port) throws Exception
	{
		socket = new Socket(ip,port) ;
		inputs = socket.getInputStream() ;
		outputs = socket.getOutputStream() ;
		outPS = new PrintStream(outputs,true,"UTF-8") ;
		recvTh = new Thread(runner,"server_ctrl_ui_recv") ;
		recvTh.start() ;
	}
	
	Runnable runner = new Runnable()
	{
		public void run()
		{
			try
			{
				InputStreamReader isr = new InputStreamReader(inputs, "UTF-8") ; 
				char[] buf = new char[1024] ;
				while(recvTh!=null)
				{
					int i = isr.read(buf) ;
					if(i<0)
						break ;
					
					String tmps = new String(buf,0,i) ;
					onRecvStr(tmps) ;
				}
			}
			catch(Exception ee)
			{
				onRecvStr(">>**>>read server error:"+ee.getMessage()) ;
				onRecvStr(">>**>>will disconnect from server!!") ;
			}
			finally
			{
				disconnFromServer() ;
			}
		}
	};
	
	private void disconnFromServer()
	{
		if(inputs!=null)
		{
			try
			{
				inputs.close() ;
			}
			catch(Exception ee)
			{}
			inputs = null ;
		}
		
		if(outputs!=null)
		{
			try
			{
				outputs.close() ;
			}
			catch(Exception ee)
			{}
			outputs = null ;
		}
		
		if(socket!=null)
		{
			try
			{
				outputs.close() ;
			}
			catch(Exception ee)
			{}
			socket = null ;
		}
		
		recvTh = null ;
		
		setDisLine(">>**>>disconnected from server,please reconnect!!") ;
	}

	public void onRecvStr(String str)
	{
		try
		{
			// System.out.println("on recv str:"+str);
			BufferedReader br = new BufferedReader(new StringReader(str));
			String line = null;
			line = br.readLine(); // first line
			if (line == null)
			{
				return;
			}

			if (contBuffer.size() > 0)
			{
				contBuffer.setElementAt(contBuffer
						.elementAt(contBuffer.size() - 1)
						+ line, contBuffer.size() - 1);
			}
			else
			{
				contBuffer.addElement(line);
			}

			while ((line = br.readLine()) != null)
			{
				contBuffer.addElement(line);
			}

			while (contBuffer.size() > maxLineNum)
			{
				contBuffer.remove(0);
			}

			int s = contBuffer.size();
			StringBuffer sb = new StringBuffer(s * 100);
			for (int i = 0; i < s; i++)
			{
				if(i<s-1)
					sb.append((String) contBuffer.elementAt(i)).append('\n');
				else
					sb.append((String) contBuffer.elementAt(i));
			}
			
			//System.out.println("set TExt area:"+sb.toString());
			contTextArea.setText(sb.toString());
			
			//contTextArea.append(str)
			//this.validate() ;
			
			contTextArea.setCaretPosition(sb.length());
			//this.scrollPane1.set(0, contTextArea.getHeight());
			contTextArea.invalidate() ;
			contTextArea.requestFocus();
			this.tfCmdTxt.requestFocus() ;
		}
		catch (IOException ioe)
		{
		}
	}

	public void setDisLine(String str)
	{
		contBuffer.addElement(str);
		if (contBuffer.size() > maxLineNum)
		{
			contBuffer.remove(0);
		}

		int s = contBuffer.size();
		StringBuffer sb = new StringBuffer(s * 100);
		for (int i = 0; i < s; i++)
		{
			sb.append((String) contBuffer.elementAt(i)).append('\n');
		}
		contTextArea.setText(sb.toString());
		
		contTextArea.setCaretPosition(sb.length());
		
		contTextArea.requestFocus();
		this.tfCmdTxt.requestFocus() ;
	}
	
	public void setDisStr(String str)
	{
		contBuffer.addElement(str);
		if (contBuffer.size() > maxLineNum)
		{
			contBuffer.remove(0);
		}

		int s = contBuffer.size();
		StringBuffer sb = new StringBuffer(s * 100);
		for (int i = 0; i < s; i++)
		{
			if(i<s-1)
				sb.append((String) contBuffer.elementAt(i)).append('\n');
			else
				sb.append((String) contBuffer.elementAt(i));
		}
		
		contTextArea.setText(sb.toString()) ;
		
		contTextArea.setCaretPosition(sb.length());
		
		contTextArea.requestFocus();
		this.tfCmdTxt.requestFocus() ;
	}
	
	public void clear()
	{
		contBuffer.clear() ;
		contTextArea.setText("");
	}

	public void setMaxLineNum(int mdb)
	{
		if (mdb <= 0)
		{
			throw new IllegalArgumentException("Max line num cannot < 0 !");
		}

		maxLineNum = mdb;
	}

	public static void main(String[] args) throws Throwable
	{
		Frame frame = new Frame();
		// frame.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		frame.addWindowListener(new WindowAdapter()
		{

			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});

		ServerCtrlUI applet = new ServerCtrlUI();
		BorderLayout borderLayout1 = new BorderLayout();
		Panel panel1 = new Panel();
		Panel panel2 = new Panel();
		BorderLayout borderLayout2 = new BorderLayout();

		frame.setLayout(borderLayout1);
		panel1.setLayout(borderLayout2);
		frame.add(panel1, BorderLayout.CENTER);
		frame.add(panel2, BorderLayout.SOUTH);
		panel1.add(applet, BorderLayout.CENTER);

		frame.setTitle("Tomato Server Console");
		frame.setLayout(new BorderLayout());
		frame.add(applet, BorderLayout.CENTER);
		frame.setSize(700, 620);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((d.width - frame.getSize().width) / 2,
				(d.height - frame.getSize().height) / 2);
		frame.setVisible(true);
	}
}
