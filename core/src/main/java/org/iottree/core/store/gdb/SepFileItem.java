package org.iottree.core.store.gdb;

import java.io.File;
import java.io.FileInputStream;
import java.io.*;

/**
 * ������Ǵ洢�ڶ����ļ����ļ�����
 * 
 * �Ķ�����Ա����ȡ����ʱ��û�б�Ҫ��ȡ�ļ����ݶ�ռ��ϵͳ�ڴ���Դ
 * 
 * @author Jason Zhu
 */
public class SepFileItem
{
	private long id = -1 ;
	
	private File file = null ;
	
	/**
	 * ����
	 */
	private transient byte[] cont = null ;
	
	
	/**
	 * ���������ʱ�Ĺ��췽��
	 * @param c
	 */
	public SepFileItem(byte[] c)
	{
		cont = c ;
	}
	
	public SepFileItem(long id,File f)
	{
		this.id = id ;
		this.file = f ;
	}
	
	public long getId()
	{
		return id ;
	}
	/**
	 * ������ļ������Ѿ������ڴ���,����ͨ���÷�����ȡ
	 * ������ʱ��Ӧ��ʹ�ø÷���
	 * @return
	 */
	public byte[] getTransientContent()
	{
		return cont ;
	}
	
	/**
	 * �����Ӧ���ļ������Ѿ����ڿ���,��÷������Է��ض�Ӧ������
	 * 
	 * �÷������ȡ���е��ļ����ݵ��ڴ���,���Ƽ�ʹ��
	 * @return
	 */
	public byte[] readContentBytes() throws IOException
	{
		if(id<=0)
			throw new RuntimeException("Sep File Has no id");
		
		if(cont!=null)
			return cont ;
		
		//��ȡ���ڴ���
		
		FileInputStream fis = null;
		try
		{
			byte[] buf = new byte[(int) file.length()];
			fis = new FileInputStream(file);
			fis.read(buf);

			cont = buf ;
			return cont ;
		}
		finally
		{
			if (fis != null)
				fis.close();
		}
	}
	
	/**
	 * �����Ӧ���ļ������Ѿ����ڿ���,��÷������Զ�ȡ�������,��������������
	 * �÷���ռ���ڴ���ԴС
	 * @param outs
	 */
	public void readContentToOutput(OutputStream outs)
		throws IOException
	{
		if(id<=0)
			throw new RuntimeException("Sep File Has no id");
		
		
		FileInputStream fis = null;
		try
		{
			byte[] buf = new byte[1024];
			fis = new FileInputStream(file);
			int l = 0 ;
			while((l=fis.read(buf))>0)
			{
				outs.write(buf, 0, l);
			}
		}
		finally
		{
			if (fis != null)
				fis.close();
		}
	}
	
	
	//public void writeContentFrom
}
