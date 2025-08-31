package org.iottree.core.store.gdb.conf;

import java.util.zip.*;
import java.util.jar.*;
import java.util.*;
import java.text.*;
import java.io.*;
import java.lang.reflect.*;

import org.w3c.dom.*;

/**
 * <p>
 * Title: 事件帮助器
 * </p>
 * <p>
 * Description: 提供扫描整个系统环境中每个包和目录的配置文件信息. 为事件系统的初始化提供帮助。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author Jason Zhu
 * @version 1.0
 */

public class XDataHelper
{
	public final static Element getFirstChildElement(Element ele,String tagname)
	{
		Element[] rets = getCurChildElement(ele, tagname);
		if(rets==null||rets.length<=0)
			return null ;
		return rets[0];
	}
	
	public final static Element[] getCurChildElement(Element ele, String tagname)
	{
		HashSet<String> hs = new HashSet<String>();
		hs.add(tagname);
		return getCurChildElement(ele,hs) ;
	}
	
	public final static Element[] getCurChildElement(Element ele, HashSet<String> tagname)
	{
		if (ele == null || tagname == null||tagname.size()<=0)
		{
			return null;
		}

		boolean isall = false;
		if (tagname.contains("*"))
		{
			isall = true;
		}
		NodeList tmpnl = ele.getChildNodes();

		Node tmpn = null;

		Vector v = new Vector();
		int k;
		if (tmpnl != null)
		{
			for (k = 0; k < tmpnl.getLength(); k++)
			{
				tmpn = tmpnl.item(k);

				if (tmpn == null || tmpn.getNodeType() != Node.ELEMENT_NODE)
				{
					continue;
				}
				Element eee = (Element) tmpn;
				if (isall || tagname.contains(eee.getNodeName()))
				{
					v.add(eee);
				}
			}
		}

		Element[] tmpe = new Element[v.size()];
		v.toArray(tmpe);
		return tmpe;
	}

	public final static String getElementFirstTxt(Element ele)
	{
		Node n = ele.getFirstChild();
		if(n==null)
			return null ;
		
		if(n instanceof Text)
		{
			return ((Text)n).getNodeValue();
		}
		return null ;
	}
	/**
	 * 在选定的目录中搜索所有子目录和子目录包含的jar文件中的指定文件内容
	 * 
	 * @param fs
	 *            指定的目录
	 * @param filename
	 *            文件名称
	 * @return 搜索到的文件内容
	 * @throws IOException
	 */
	public static FileDataItem[] readFiles(File[] files, FilenameFilter fnf)
			throws IOException
	{
		if (files == null)
		{
			return null;
		}
		Hashtable<String, FileDataItem> v = new Hashtable<String, FileDataItem>();
		for (int i = 0; i < files.length; i++)
		{
			if(!files[i].exists())
				continue ;
			
			if (files[i].isDirectory())
			{
				readDirFile("/", files[i], v, fnf);
			}
			else
			{
				String n = files[i].getName().toLowerCase();
				if (n.endsWith(".jar") || n.endsWith(".zip"))
				{
					readJarFile(files[i], v, fnf);
				}
			}

		}
		FileDataItem[] rets = new FileDataItem[v.size()];
		v.values().toArray(rets);
		Arrays.sort(rets);
		return rets;
	}

	private static FileDataItem[] readDirFile(File dirfile, FilenameFilter fnf)
			throws IOException
	{
		Hashtable<String, FileDataItem> v = new Hashtable<String, FileDataItem>();
		readDirFile("/", dirfile, v, fnf);
		FileDataItem[] rets = new FileDataItem[v.size()];
		v.values().toArray(rets);
		Arrays.sort(rets);
		return rets;
	}

	private static void readDirFile(String baseres_path, File dirfile,
			Hashtable<String, FileDataItem> v, FilenameFilter fnf)
			throws IOException
	{
		File[] fs = dirfile.listFiles();
		if (fs == null)
		{
			return;
		}
		for (int i = 0; i < fs.length; i++)
		{
			if (fs[i].isDirectory())
			{
				readDirFile(baseres_path + (fs[i].getName() + "/"), fs[i], v,
						fnf);
			}
			else
			{
				String n = fs[i].getName().toLowerCase();
				if (n.endsWith(".jar") || n.endsWith(".zip"))
				{
					readJarFile(fs[i], v, fnf);
				}
				else
				{
					if (!fnf.accept(fs[i].getAbsoluteFile(), fs[i].getName()))
						continue;

					String resp = baseres_path + fs[i].getName();
					if (v.containsKey(resp))
						continue;

					byte[] buf = new byte[(int) fs[i].length()];
					FileInputStream fis = null;
					try
					{
						fis = new FileInputStream(fs[i]);
						fis.read(buf);
						v.put(resp, new FileDataItem(fs[i].getAbsolutePath(),
								resp, buf));
					}
					finally
					{
						fis.close();
					}
				}
			}
		}
	}

	/**
	 * 读取jar文件中的指定的文件名，并返回其名称和内容
	 * 
	 * @param jarfile
	 *            jar文件
	 * @param filename
	 *            文件名称
	 * @return
	 * @throws IOException
	 */
	private static void readJarFile(File jarfile,
			Hashtable<String, FileDataItem> v, FilenameFilter fnf)
			throws IOException
	{
		JarInputStream jis = null;
		try
		{
			jis = new JarInputStream(new FileInputStream(jarfile));
			JarEntry je = null;
			while ((je = jis.getNextJarEntry()) != null)
			{
				try
				{
					String ename = je.getName();
					String tmpn = ename;
					int tmpp = ename.lastIndexOf('/');
					if (tmpp >= 0)
						tmpn = ename.substring(tmpp + 1);

					if (!fnf.accept(null, tmpn))
						continue;

					ename = "/" + ename;
					if (v.containsKey(ename))
						continue;

					String en = jarfile.getAbsolutePath() + "/" + ename;
					v.put(ename, readData(ename, en, jis));
				}
				finally
				{
					jis.closeEntry();
				}
			}
		}
		finally
		{
			jis.close();
		}
	}

	private static FileDataItem readData(String res_name, String filename,
			InputStream is) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte[] buf = new byte[2048];

		while (true)
		{
			int read = is.read(buf);
			if (read == -1)
			{
				break;
			}
			baos.write(buf, 0, read);
		}
		baos.flush();
		return new FileDataItem(filename, res_name, baos.toByteArray());
	}

	public static void main(String[] args) throws Throwable
	{
		FilenameFilter fnf = new FilenameFilter()
		{

			public boolean accept(File dir, String name)
			{
				if (name == null)
					return false;
				return name.toLowerCase().startsWith("xdata_")
						&& name.endsWith(".xml");
			}
		};
		File[] fs = new File[] {
				new File("E:/working/biz_web/webapps/mail/WEB-INF/classes"),
				new File("E:/working/system4j/output") };
		FileDataItem[] fdis = readFiles(fs, fnf);
		for (FileDataItem fdi : fdis)
		{
			System.out.println(fdi);
		}
	}
	
	public static class FileDataItem implements Comparable<FileDataItem>
	{
		private String name = null;

		/**
		 * 对应文件作为资源路径的信息。
		 */
		private String absResPath = null;

		private byte[] content = null;

		// FileDataItem(String n, byte[] cont)
		// {
		// name = n;
		// content = cont;
		// }

		FileDataItem(String n, String absresp, byte[] cont)
		{
			name = n;
			absResPath = absresp;
			content = cont;
		}

		public String getName()
		{
			return name;
		}

		public String getAbsResPath()
		{
			return absResPath;
		}

		public byte[] getContent()
		{
			return content;
		}

		public String toString()
		{
			return absResPath + "- content size=" + content.length + " @ " + name;
		}

		public int compareTo(FileDataItem o)
		{
			return absResPath.compareTo(o.absResPath);
		}
	}
}

