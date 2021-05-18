package org.iottree.core.util.xmldata;

import java.io.*;
import java.util.*;

/**
 * 
 * 
 * @author Jason Zhu
 */
public class XmlDataWithFile
{
	public static final int BUF_LEN = 1024;

	public static class MemFileItem
	{
		String fileName = null ;
		byte[] fileCont = null ;
		
		public MemFileItem(String fn,byte[] fc)
		{
			fileName = fn ;
			fileCont = fc ;
		}
		
		public String getFileName()
		{
			return fileName ;
		}
		
		public byte[] getFileCont()
		{
			return fileCont ;
		}
	}
	/**
	 * �Խ��ն��ṩ�Ļص�֧�֣��������ݼ��ļ��˿���ͨ��ʵ�ָýӿڡ�����Ч���ṩ���ݽ���
	 * 
	 * @author Jason Zhu
	 */
	public static interface IRecvCallback
	{
		public void onCombinedFileHeadFound(CombinedFileHead cfh);

		public void beforeRecvXmlData();

		public void afterRecvXmlData(XmlData xd);

		public void beforeRecvFile(String filename, long filelen)
				throws Exception;

		/**
		 * 
		 * @param filename
		 * @param filelen
		 * @param total_recvlen
		 * @param recvedd
		 * @param recvlen
		 */
		public void duringRecvFile(String filename, long filelen,
				long total_recvlen, byte[] recvedd, int recvlen)
				throws Exception;

		public void afterRecvFile(String filename, long filelen)
				throws Exception;

		public void recvFinished();

		public void recvException(Exception ee) throws Exception;

		/**
		 * ���ڽ��������п��ܲ���һЩ��Ҫ�ͷŵ���Դ. ʵ�ָ÷���������һЩ��Դ�õ��ͷ�
		 */
		public void disposeMe() throws Exception;
	}

	public static interface ISendCallback
	{
		public void onConnFailed() throws Exception ;
		
		public void onAuthFailed() throws Exception ;
		
		public void onSendingMsg(String msg);

		public void beforeSendXmlData(XmlData sendxd, XmlData innerxd);

		public void afterSendXmlData(XmlData sendxd, XmlData innerxd,
				long sent_len);

		public void beforeSendFile(String filepath, long filelen);

		/**
		 * �ļ������ڼ�
		 * 
		 * @param filepath
		 * @param filelen
		 * @param sentlen
		 *            �Ѿ����͵ĳ���
		 */
		public void duringSendFile(String filepath, long filelen, long sentlen,
				int readlen);

		public void afterSendFile(String filepath, long filelen);

		public void sendFinished();

		/**
		 * ����ʧ��-
		 * 
		 * @param title
		 *            ����������ʾ�������û�����ʾ
		 * @param ee
		 *            ʧ����ϸ��Ϣ
		 * @throws Exception
		 */
		public void sendException(String title, Exception ee) throws Exception;
	}

	public static class DefaultSendCB implements ISendCallback
	{
		public void onConnFailed() throws Exception
		{
			throw new Exception("conn failed") ;
		}
		
		public void onAuthFailed() throws Exception
		{
			throw new Exception("auth failed") ;
		}
		
		public void onSendingMsg(String msg)
		{

		}

		public void beforeSendXmlData(XmlData sendxd, XmlData innerxd)
		{

		}

		public void afterSendXmlData(XmlData sendxd, XmlData innerxd,
				long sent_len)
		{

		}

		public void beforeSendFile(String filepath, long filelen)
		{

		}

		public void duringSendFile(String filepath, long filelen, long sentlen,
				int readlen)
		{

		}

		public void afterSendFile(String filepath, long filelen)
		{

		}

		public void sendFinished()
		{

		}

		public void sendException(String title, Exception ee) throws Exception
		{
			throw ee;
		}

	}

	// public static class DefaultRecvCB()

	/**
	 * ����Ŀ¼�Ľ��ջص�
	 * 
	 * @author Jason Zhu
	 */
	public static class DirRecvCB implements IRecvCallback
	{
		private String recvDir = null;

		private File recvDirF = null;

		private String name = null;

		public DirRecvCB(String dir, String name)
		{
			recvDir = dir;
			recvDirF = new File(dir);
			recvDirF.mkdirs();
			this.name = name;
		}

		public void onCombinedFileHeadFound(CombinedFileHead cfh)
		{
		}

		public void beforeRecvXmlData()
		{

		}

		public void afterRecvXmlData(XmlData xd)
		{
			File of = new File(recvDirF, name + "_data_.xml");

			try
			{
				XmlData.writeToFile(xd, of.getCanonicalPath());
			}
			catch (Exception ioe)
			{
				throw new RuntimeException(ioe.toString());
			}
		}

		FileOutputStream curRecvFOS = null;

		public void beforeRecvFile(String filename, long filelen)
				throws Exception
		{
			File of = new File(recvDirF, filename);
			curRecvFOS = new FileOutputStream(of);
		}

		public void duringRecvFile(String filename, long filelen,
				long total_recvlen, byte[] recvedd, int recvlen)
				throws Exception
		{
			if (recvlen > 0)
				curRecvFOS.write(recvedd, 0, recvlen);
		}

		public void afterRecvFile(String filename, long filelen)
				throws Exception
		{
			curRecvFOS.close();
			curRecvFOS = null;
		}

		public void recvFinished()
		{

		}

		public void recvException(Exception ee) throws Exception
		{
			throw ee;
		}

		public void disposeMe() throws Exception
		{
			if (curRecvFOS != null)
				curRecvFOS.close();
		}
	}

	/**
	 * ���յ�һ�����еĽ��ջص�.�ûص��ڽ�����������ϵķ�ʽд�뵽һ������.
	 * 
	 * @author Jason Zhu
	 * 
	 */
	public static class StreamRecvCB implements IRecvCallback
	{
		OutputStream recvOutputS = null;

		CombinedFileHead recvedCominedFH = null;// new CombinedFileHead() ;

		public StreamRecvCB(OutputStream outputs)
		{
			recvOutputS = outputs;
		}

		public void onCombinedFileHeadFound(CombinedFileHead cfh)
		{
			recvedCominedFH = cfh;

			byte[] buf = cfh.combinedFormatXmlData.toBytesWithUTF8();
			try
			{
				recvOutputS.write(("" + buf.length + "\n").getBytes());
				recvOutputS.write(buf);
			}
			catch (Exception ioe)
			{
				throw new RuntimeException(ioe.toString());
			}
		}

		public CombinedFileHead getCombinedFileHead()
		{
			return recvedCominedFH;
		}

		public void beforeRecvXmlData()
		{

		}

		public void afterRecvXmlData(XmlData xd)
		{

		}

		public void beforeRecvFile(String filename, long filelen)
				throws Exception
		{

		}

		public void duringRecvFile(String filename, long filelen,
				long total_recvlen, byte[] recvedd, int recvlen)
				throws Exception
		{
			if (recvlen > 0)
				recvOutputS.write(recvedd, 0, recvlen);
		}

		public void afterRecvFile(String filename, long filelen)
				throws Exception
		{

		}

		public void recvFinished()
		{

		}

		public void recvException(Exception ee) throws Exception
		{
			throw ee;
		}

		public void disposeMe() throws Exception
		{

		}
	}
	
	
	public static class CombinedRecvCB implements IRecvCallback
	{
		CombinedFileHead recvedCominedFH = null;// new CombinedFileHead() ;

		public CombinedRecvCB()
		{
			
		}

		public void onCombinedFileHeadFound(CombinedFileHead cfh)
		{
			recvedCominedFH = cfh;
		}

		public CombinedFileHead getCombinedFileHead()
		{
			return recvedCominedFH;
		}

		public void beforeRecvXmlData()
		{

		}

		public void afterRecvXmlData(XmlData xd)
		{

		}

		public void beforeRecvFile(String filename, long filelen)
				throws Exception
		{

		}

		public void duringRecvFile(String filename, long filelen,
				long total_recvlen, byte[] recvedd, int recvlen)
				throws Exception
		{
			
		}

		public void afterRecvFile(String filename, long filelen)
				throws Exception
		{

		}

		public void recvFinished()
		{

		}

		public void recvException(Exception ee) throws Exception
		{
			throw ee;
		}

		public void disposeMe() throws Exception
		{

		}
	}

	public static class SaveToFileRecvCB implements IRecvCallback
	{
		private String recvDir = null;

		private File recvDirF = null;

		private String name = null;

		public SaveToFileRecvCB(String dir, String name)
		{
			recvDir = dir;
			recvDirF = new File(dir);
			recvDirF.mkdirs();
			this.name = name;
		}

		public void onCombinedFileHeadFound(CombinedFileHead cfh)
		{
		}

		public void beforeRecvXmlData()
		{

		}

		public void afterRecvXmlData(XmlData xd)
		{
			File of = new File(recvDirF, name + "_data_.xml");

			try
			{
				XmlData.writeToFile(xd, of.getCanonicalPath());
			}
			catch (Exception ioe)
			{
				throw new RuntimeException(ioe.toString());
			}
		}

		FileOutputStream curRecvFOS = null;

		public void beforeRecvFile(String filename, long filelen)
				throws Exception
		{
			File of = new File(recvDirF, filename);
			curRecvFOS = new FileOutputStream(of);
		}

		public void duringRecvFile(String filename, long filelen,
				long total_recvlen, byte[] recvedd, int recvlen)
				throws Exception
		{
			if (recvlen > 0)
				curRecvFOS.write(recvedd, 0, recvlen);
		}

		public void afterRecvFile(String filename, long filelen)
				throws Exception
		{
			curRecvFOS.close();
			curRecvFOS = null;
		}

		public void recvFinished()
		{

		}

		public void recvException(Exception ee) throws Exception
		{
			throw ee;
		}

		public void disposeMe() throws Exception
		{
			if (curRecvFOS != null)
				curRecvFOS.close();
		}
	}

	/**
	 * �Ѷ���õĽṹ�ͱ����ļ�ָ���·��������ϣ������һ���ļ��С�
	 * 
	 * @param combinefn
	 * @param xdwf
	 */
	public static void combineToOneFile(String combinefn, XmlDataWithFile xdwf)
			throws IOException
	{
		FileOutputStream fos = null;

		try
		{
			fos = new FileOutputStream(combinefn);
			xdwf.writeToStream(fos, new DefaultSendCB());
		}
		finally
		{
			if (fos != null)
				fos.close();
		}
	}

	public static void splitToDir(String combinedfile, String dirpath)
			throws Exception
	{
		FileInputStream fis = null;
		try
		{
			File cf = new File(combinedfile);

			fis = new FileInputStream(combinedfile);

			DirRecvCB cb = new DirRecvCB(dirpath, cf.getName());
			XmlDataWithFile xdwf = new XmlDataWithFile();
			xdwf.readFromStream(fis, cb);
		}
		finally
		{
			if (fis != null)
				fis.close();
		}
	}

	/**
	 * ��һ������ļ�������ʱ,��Ҫ���ȶ�ȡ�ļ�ͷ,�Ա��ڻ�ȡ��ʽ���ݲ��Ҷ���������ļ� �������Ϊ�˷��㴦���ר��������֧��
	 * 
	 * @author Jason Zhu
	 */
	public static class CombinedFileHead
	{
		XmlData innerXmlData = null;

		ArrayList<String> fileNames = new ArrayList<String>();

		ArrayList<long[]> startLens = new ArrayList<long[]>();

		// HashMap<String,long[]> filename2startLen = new
		// HashMap<String,long[]>() ;

		/**
		 * ��Ϻ�ĸ�ʽ���ݰ���innerXmlData���ļ�������Ϣ
		 */
		XmlData combinedFormatXmlData = null;

		private CombinedFileHead()
		{
		}

		public XmlData getInnerXmlData()
		{
			return innerXmlData;
		}

		public String[] getFileNames()
		{
			String[] rets = new String[fileNames.size()];
			fileNames.toArray(rets);
			return rets;
		}

		public long getFileStartPos(String filename)
		{
			int s = fileNames.size();
			for (int i = 0; i < s; i++)
			{
				String fn = fileNames.get(i);
				if (fn.equals(filename))
					return startLens.get(i)[0];
			}

			return -1;
		}

		public long getFileLenth(String filename)
		{
			int s = fileNames.size();
			for (int i = 0; i < s; i++)
			{
				String fn = fileNames.get(i);
				if (fn.equals(filename))
					return startLens.get(i)[1];
			}

			return -1;
		}
	}

	/**
	 * ��һ������ļ��ֻ���ļ�ͷ,���ļ�ͷ����ԭ�����ڲ�XmlData���ļ���Ϣ(�ļ���,��ʼλ��,����)
	 * �÷�������֧�ֻ�ȡ�ļ�ͷ��Ϣ�Ͷ�λ����ļ�����
	 * 
	 * @param inputs
	 * @return
	 * @throws Exception
	 */
	public static CombinedFileHead readCombinedFileHead(RandomAccessFile inputs)
			throws Exception
	{
		inputs.seek(0);

		// read first line
		StringBuilder firstline = new StringBuilder();
		int c, firstline_len = -1, datalen = -1;
		while ((c = inputs.read()) >= 0)
		{
			if (c == '\n')
			{
				if (firstline.length() > 50)
				{
					throw new Exception(
							"invalid data format,no data len found!");
				}
				datalen = Integer.parseInt(firstline.toString());
				firstline_len = firstline.length() + 1;
				break;
			}

			firstline.append((char) c);
			if (firstline.length() > 50)
			{
				throw new Exception("invalid data format,no data len found!");
			}
		}

		if (datalen < 0)
		{
			throw new Exception("no data len found!");
		}

		byte[] databuf = new byte[datalen];
		int len, readlen = 0;

		while ((len = inputs.read(databuf, readlen, datalen - readlen)) >= 0)
		{
			readlen += len;
			if (readlen == datalen)
				break;
		}

		if (readlen < datalen)
		{
			throw new Exception("invalid data format,data read len[" + readlen
					+ "] is less to data len[" + datalen + "]");
		}

		XmlData xd = XmlData.parseFromByteArrayUTF8(databuf);

		CombinedFileHead ret = new CombinedFileHead();
		ret.innerXmlData = xd.getSubDataSingle("data");
		ret.combinedFormatXmlData = xd;
		List<XmlData> fxds = xd.getSubDataArray("files");
		if (fxds != null && fxds.size() > 0)
		{
			long startp = firstline_len + datalen;
			for (XmlData fxd : fxds)
			{
				String filen = fxd.getParamValueStr("name");
				long filelen = fxd.getParamValueInt64("len", -1);

				ret.fileNames.add(filen);
				ret.startLens.add(new long[] { startp, filelen });
				startp += filelen;
			}
		}
		return ret;
	}

	public static CombinedFileHead readCombinedFileHead(InputStream inputs)
			throws Exception
	{
		// read first line
		StringBuilder firstline = new StringBuilder();
		int c, firstline_len = -1, datalen = -1;
		while ((c = inputs.read()) >= 0)
		{
			if (c == '\n')
			{
				if (firstline.length() > 50)
				{
					throw new Exception(
							"invalid data format,no data len found!");
				}
				datalen = Integer.parseInt(firstline.toString());
				firstline_len = firstline.length() + 1;
				break;
			}

			firstline.append((char) c);
			if (firstline.length() > 50)
			{
				throw new Exception("invalid data format,no data len found!");
			}
		}

		if (datalen < 0)
		{
			throw new Exception("no data len found!");
		}

		byte[] databuf = new byte[datalen];
		int len, readlen = 0;

		while ((len = inputs.read(databuf, readlen, datalen - readlen)) >= 0)
		{
			readlen += len;
			if (readlen == datalen)
				break;
		}

		if (readlen < datalen)
		{
			throw new Exception("invalid data format,data read len[" + readlen
					+ "] is less to data len[" + datalen + "]");
		}

		XmlData xd = XmlData.parseFromByteArrayUTF8(databuf);

		//System.out.println(xd.toXmlString()) ;
		
		CombinedFileHead ret = new CombinedFileHead();
		ret.innerXmlData = xd.getSubDataSingle("data");
		ret.combinedFormatXmlData = xd;
		List<XmlData> fxds = xd.getSubDataArray("files");
		if (fxds != null && fxds.size() > 0)
		{
			long startp = firstline_len + datalen;
			for (XmlData fxd : fxds)
			{
				String filen = fxd.getParamValueStr("name");
				long filelen = fxd.getParamValueInt64("len", -1);

				ret.fileNames.add(filen);
				ret.startLens.add(new long[] { startp, filelen });
				startp += filelen;
			}
		}
		return ret;
	}
	
	/**
	 * ����
	 * @param combinefn
	 * @param innerxd
	 * @param filename2cont
	 */
	public static void combineToOneFile(String combinefn, XmlData innerxd,HashMap<String,byte[]> filename2cont)
	{
		
	}

	XmlData innerXmlData = null;

	ArrayList<String> localPathToBeSend = new ArrayList<String>();

	ArrayList<MemFileItem> memFileItems = new ArrayList<MemFileItem>();

	public XmlDataWithFile()
	{
	}

	/**
	 * �������ݸ�ʽ�������ص��ļ�·��������� �ö������Ϊ����������׼��
	 * 
	 * ֮��Ϳ��Ե���{@see writeToStream}
	 * 
	 * @param xd
	 * @param localpaths
	 */
	public XmlDataWithFile(XmlData xd, String[] localpaths)
	{
		innerXmlData = xd;
		innerXmlData.belongToXdFile = this ;
		if (localpaths != null)
		{
			for (String lp : localpaths)
			{
				localPathToBeSend.add(lp);
			}
		}
	}

	/**
	 * 
	 * @param xd
	 * @param localpaths
	 * @param memfs �ڴ��ļ�����
	 */
	public XmlDataWithFile(XmlData xd, List<String> localpaths,List<MemFileItem> memfs)
	{
		innerXmlData = xd;
		if (localpaths != null)
		{
			for (String lp : localpaths)
			{
				localPathToBeSend.add(lp);
			}
		}
		
		if(memfs!=null)
			memFileItems.addAll(memfs) ;
	}
	
	public XmlDataWithFile(XmlData xd, List<String> localpaths)
	{
		this(xd,localpaths,null);
	}

	public void setInnerXmlData(XmlData xd)
	{
		innerXmlData = xd;
		innerXmlData.belongToXdFile = this ;
	}

	public XmlData getInnerXmlData()
	{
		return innerXmlData;
	}
	
	public List<String> getLocalFilePaths()
	{
		return this.localPathToBeSend ;
	}

//	public void setFileCont(String filename, byte[] filec)
//	{
//		if (filec == null)
//		{
//			fileName2Cont.remove(filename);
//			return;
//		}
//
//		fileName2Cont.put(filename, filec);
//	}

	/**
	 * ����������ڲ����ݸ�ʽ�������ļ�·����Ϣ�ĸ�ʽ��Ϣ �����ݿ�����Ϊ���ط�����ʱ�������Ϣ-�����û��ظ��������Ĵ���,�緢��
	 * 
	 * @return
	 */
	public XmlData toLocalFormatXmlData()
	{
		XmlData xd = new XmlData();
		if (innerXmlData != null)
			xd.setSubDataSingle("data", innerXmlData);

		if(localPathToBeSend!=null&&localPathToBeSend.size()>0)
			xd.setParamValues("local_files", localPathToBeSend);
		return xd;
	}

	/**
	 * �ӱ��صĸ�ʽ���ݽṹ��,�ָ��ɶ���. �ָ����������������
	 * 
	 * @param xd
	 */
	public void fromLocalFormatXmlData(XmlData xd)
	{
		innerXmlData = xd.getSubDataSingle("data");
		if(innerXmlData!=null)
			innerXmlData.belongToXdFile = this ;
		String[] fps = xd.getParamValuesStr("local_files");
		if (fps != null && fps.length > 0)
		{
			for (String fp : fps)
			{
				localPathToBeSend.add(fp);
			}
		}
	}

	public void writeToStream(OutputStream outputs) throws Exception
	{
		writeToStream(outputs, new DefaultSendCB());
	}
	
	public void writeToFile(File f) throws Exception
	{
		if(!f.getParentFile().exists())
			f.getParentFile().mkdirs() ;
		
		FileOutputStream fos = null ;
		
		try
		{
			fos = new FileOutputStream(f);
			writeToStream(fos) ;
		}
		finally
		{
			if(fos!=null)
				fos.close() ;
		}
	}

	/**
	 * ���㷢�����ݵĳ���--����֧��http��ʽ����ǰ��Ҫ�趨��ContentLength
	 * 
	 * @return
	 */
	public long calculateSendLen()
	{
		XmlData sendxd = new XmlData();
		if (innerXmlData != null)
			sendxd.setSubDataSingle("data", innerXmlData);

		ArrayList<File> files = new ArrayList<File>();
		if (localPathToBeSend!=null&&localPathToBeSend.size() > 0)
		{
			List<XmlData> fxds = sendxd.getOrCreateSubDataArray("files");
			for (String lp : localPathToBeSend)
			{
				File f = new File(lp);
				if (!f.exists())
					continue;

				files.add(f);

				XmlData tmpxd = new XmlData();
				tmpxd.setParamValue("name", f.getName());
				tmpxd.setParamValue("len", f.length());
				fxds.add(tmpxd);
			}
		}
		
		if(memFileItems!=null && memFileItems.size()>0)
		{
			List<XmlData> fxds = sendxd.getOrCreateSubDataArray("files");
			for(MemFileItem mfi:memFileItems)
			{
				XmlData tmpxd = new XmlData();
				tmpxd.setParamValue("name", mfi.getFileName());
				tmpxd.setParamValue("len", (long)(mfi.getFileCont().length));
				fxds.add(tmpxd);
			}
		}

		byte[] ddbuf = sendxd.toBytesWithUTF8();
		// write datalen--Ϊ�˱����޶��ṹ���ݵĳ���
		byte[] lenbs = ("" + ddbuf.length + "\n").getBytes();

		long ret = lenbs.length + ddbuf.length;

		// write file
		if (files.size() > 0)
		{
			for (File f : files)
			{
				ret += f.length();
			}// end of for
		}
		return ret;
	}

	/**
	 * �����ݼ����ض�����ļ�·�������͵��������
	 * 
	 * @param outputs
	 * @param scb
	 */
	public void writeToStream(OutputStream outputs, ISendCallback scb)
	{
		XmlData sendxd = new XmlData();
		if (innerXmlData != null)
			sendxd.setSubDataSingle("data", innerXmlData);

		ArrayList<File> files = new ArrayList<File>();
		if (localPathToBeSend!=null&&localPathToBeSend.size() > 0)
		{
			List<XmlData> fxds = sendxd.getOrCreateSubDataArray("files");
			for (String lp : localPathToBeSend)
			{
				File f = new File(lp);
				if (!f.exists())
					continue;

				files.add(f);

				XmlData tmpxd = new XmlData();
				tmpxd.setParamValue("name", f.getName());
				tmpxd.setParamValue("len", f.length());
				fxds.add(tmpxd);
			}
		}
		
		if(memFileItems!=null && memFileItems.size()>0)
		{
			List<XmlData> fxds = sendxd.getOrCreateSubDataArray("files");
			for(MemFileItem mfi:memFileItems)
			{
				XmlData tmpxd = new XmlData();
				tmpxd.setParamValue("name", mfi.getFileName());
				tmpxd.setParamValue("len", (long)(mfi.getFileCont().length));
				fxds.add(tmpxd);
			}
		}

		try
		{
			scb.beforeSendXmlData(sendxd, innerXmlData);

			byte[] ddbuf = sendxd.toBytesWithUTF8();
			// write datalen--Ϊ�˱����޶��ṹ���ݵĳ���
			byte[] lenbs = ("" + ddbuf.length + "\n").getBytes();
			outputs.write(lenbs);
			// write data
			outputs.write(ddbuf);

			outputs.flush();

			scb.afterSendXmlData(sendxd, innerXmlData, ddbuf.length
					+ lenbs.length);

			// write file
			if (files.size() > 0)
			{
				byte[] tmpb = new byte[BUF_LEN];
				for (File f : files)
				{
					long filelen = f.length();
					String fp = f.getCanonicalPath();
					FileInputStream fis = null;
					try
					{
						fis = new FileInputStream(f);
						long sendlen = 0;
						int len;
						// call back
						scb.beforeSendFile(fp, filelen);

						while ((len = fis.read(tmpb)) >= 0)
						{
							sendlen += len;

							outputs.write(tmpb, 0, len);

							outputs.flush();
							// call back
							scb.duringSendFile(fp, filelen, sendlen, len);
						}
						// call back
						scb.afterSendFile(fp, filelen);
					}
					finally
					{
						if (fis != null)
							fis.close();
					}
				}// end of for
			}
			
			//mem file
			if(memFileItems!=null && memFileItems.size()>0)
			{
				List<XmlData> fxds = sendxd.getOrCreateSubDataArray("files");
				for(MemFileItem mfi:memFileItems)
				{
//					 call back
					scb.beforeSendFile(mfi.fileName, mfi.fileCont.length);
					outputs.write(mfi.fileCont) ;
					outputs.flush();
					scb.afterSendFile(mfi.fileName, mfi.fileCont.length);
				}
			}

			// callback
			//scb.sendFinished();
		}
		catch (IOException ioe)
		{
			try
			{
				scb.sendException("", ioe);
			}
			catch (Exception ee)
			{
			}
		}
	}

	public void readFromStream(InputStream inputs, IRecvCallback rcb)
			throws Exception
	{
		try
		{
			readFromStreamInner(inputs, rcb);
		}
		catch (Exception e)
		{
			rcb.recvException(e);
		}
		finally
		{
			try
			{
				rcb.disposeMe();
			}
			catch (Exception ee)
			{
			}
		}
	}

	private void readFromStreamInner(InputStream inputs, IRecvCallback rcb)
			throws Exception
	{
		/*
		 * //read first line StringBuilder firstline = new StringBuilder() ; int
		 * c,firstline_len = -1,datalen = -1 ; while((c=inputs.read())>=0) {
		 * if(c=='\n') { if(firstline.length()>50) { rcb.recvException(new
		 * Exception("invalid data format,no data len found!")) ; return ; }
		 * datalen = Integer.parseInt(firstline.toString()) ; firstline_len =
		 * firstline.length()+1 ; break ; }
		 * 
		 * firstline.append((char)c) ; if(firstline.length()>50) {
		 * rcb.recvException(new Exception("invalid data format,no data len
		 * found!")) ; return ; } }
		 * 
		 * if(datalen<0) { rcb.recvException(new Exception("no data len
		 * found!")) ; return ; }
		 * 
		 * byte[] databuf = new byte[datalen] ; int len,readlen=0;
		 * 
		 * rcb.beforeRecvXmlData() ;
		 * 
		 * while((len= inputs.read(databuf,readlen,datalen-readlen))>=0) {
		 * readlen += len ; if(readlen==datalen) break ; }
		 * 
		 * if(readlen<datalen) { rcb.recvException(new Exception("invalid data
		 * format,data read len["+readlen+"] is less to data len["+datalen+"]")) ;
		 * return ; }
		 * 
		 * XmlData xd = XmlData.parseFromByteArrayUTF8(databuf) ;
		 */

		rcb.beforeRecvXmlData();

		CombinedFileHead cfh = readCombinedFileHead(inputs);
		XmlData xd = cfh.combinedFormatXmlData;
		XmlData innerxd = xd.getSubDataSingle("data");
		if(innerxd!=null)
			innerxd.belongToXdFile = this ;

		rcb.afterRecvXmlData(innerxd);

		this.innerXmlData = innerxd ;
		// call back
		rcb.onCombinedFileHeadFound(cfh);

		recvFiles(xd, inputs, rcb);
	}

	private void recvFiles(XmlData xd, InputStream inputs, IRecvCallback rcb)
			throws Exception
	{
		List<XmlData> fxds = xd.getSubDataArray("files");
		if (fxds == null || fxds.size() <= 0)
			return;

		byte[] buf = new byte[BUF_LEN];
		for (XmlData fxd : fxds)
		{
			String filen = fxd.getParamValueStr("name");
			long filelen = fxd.getParamValueInt64("len", -1);

			rcb.beforeRecvFile(filen, filelen);

			long readlen = 0;
			int len;
			while (true)
			{
				int rl = BUF_LEN;
				if (filelen - readlen < rl)
					rl = (int) (filelen - readlen);
				len = inputs.read(buf, 0, rl);
				if (len < 0)
					break;
				readlen += len;

				rcb.duringRecvFile(filen, filelen, readlen, buf, len);

				if (readlen == filelen)
					break;
			}

			if (readlen < filelen)
			{
				rcb.recvException(new Exception("read file len[" + readlen
						+ "] is less than [" + filelen + "]"));
				return;
			}

			rcb.afterRecvFile(filen, filelen);
		}
	}

	public static void main(String[] args) throws Exception
	{
		// do test
		String dir = "E:/tmp/";
		XmlData tmpxd = new XmlData();
		tmpxd.setParamValue("xx", "XXX����");
		tmpxd.setParamValue("num", 2324.2f);
		tmpxd.setParamValue("dd", 2324.2d);
		tmpxd.setParamValue("date", new Date());
		XmlDataWithFile xdwf = new XmlDataWithFile(tmpxd, new String[] {
				"E:/tmp/111.htm", "E:/tmp/222��.swf", "E:/tmp/1/HELLOWIN.WAV",
				"E:/tmp/ttt/WebBrick/LocalFile/mm1/m2/55.jpg" });

		XmlDataWithFile.combineToOneFile("E:/tmp/xdf/xdf1.txt", xdwf);

		XmlDataWithFile.splitToDir("E:/tmp/xdf/xdf1.txt", "E:/tmp/xdf/split/");

		//
		RandomAccessFile raf = null;
		try
		{
			raf = new RandomAccessFile("E:/tmp/xdf/xdf1.txt", "r");
			CombinedFileHead cfh = XmlDataWithFile.readCombinedFileHead(raf);
			File f = new File("E:/tmp/xdf/split_by_combine_file_head/");
			f.mkdirs();
			XmlData tmpxd0 = cfh.getInnerXmlData();
			if (tmpxd0 != null)
				XmlData.writeToFile(tmpxd0,
						"E:/tmp/xdf/split_by_combine_file_head/head_xxx.xml");
			for (String fn : cfh.getFileNames())
			{
				long sp = cfh.getFileStartPos(fn);
				long sl = cfh.getFileLenth(fn);
				if (sp < 0 || sl < 0)
					continue;

				FileOutputStream fos = null;
				try
				{
					fos = new FileOutputStream(
							"E:/tmp/xdf/split_by_combine_file_head/" + fn);
					readAndWrite(raf, fos, sp, sl);
				}
				finally
				{
					if (fos != null)
						fos.close();
				}
			}
		}
		finally
		{
			if (raf != null)
				raf.close();
		}
	}

	private static void readAndWrite(RandomAccessFile raf, OutputStream fos,
			long sp, long sl) throws FileNotFoundException, IOException
	{

		byte[] buf = new byte[1024];

		raf.seek(sp);
		int rl = 0;
		while (rl < sl)
		{
			long leftlen = sl - rl;
			int currl = 1024;
			if (currl > leftlen)
				currl = (int) leftlen;
			int ll = raf.read(buf, 0, currl);
			if (ll < 0)
				break;

			fos.write(buf, 0, ll);
			rl += ll;
		}

	}
}
