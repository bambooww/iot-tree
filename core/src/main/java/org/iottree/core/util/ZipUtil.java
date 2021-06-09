package org.iottree.core.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil
{
	static void zipFileOut0(List<File> files, String comment, OutputStream zip_outs) throws Exception
	{
		try (ZipOutputStream zipOut = new ZipOutputStream(zip_outs);
				BufferedOutputStream bufferout = new BufferedOutputStream(zipOut);)
		{
			if (comment != null)
				zipOut.setComment(comment);

			for (File _file : files)
			{
				try (InputStream input = new FileInputStream(_file);
						BufferedInputStream buffin = new BufferedInputStream(input);)
				{
					zipOut.putNextEntry(new ZipEntry(_file.getName()));
					int temp = 0;
					while ((temp = buffin.read()) != -1)
					{
						bufferout.write(temp);
					}
				}
			}
		} // end try
	}

	public static void zipFileOut(List<File> files, String comment, File zipoutf) throws IOException
	{
		File pf = zipoutf.getParentFile();
		if (!pf.exists())
			pf.mkdirs();

		try (FileOutputStream fos = new FileOutputStream(zipoutf); ZipOutputStream zos = new ZipOutputStream(fos);)
		{
			if (comment != null)
				zos.setComment(comment);

			for (File f : files)
			{
				writeZip(f, "", zos);
				;
			}
		}
	}

	private static boolean writeZip(File file, String ppath, ZipOutputStream zos) throws IOException
	{
		if (!file.exists())
			return false;

		if (file.isDirectory())
		{
			ppath += file.getName() + File.separator;
			File[] files = file.listFiles();
			if (files.length != 0)
			{
				for (File f : files)
				{
					writeZip(f, ppath, zos);
				}
			}
			else
			{
				zos.putNextEntry(new ZipEntry(ppath));
			}
			return true;
		}

		try (FileInputStream fis = new FileInputStream(file);)
		{
			ZipEntry ze = new ZipEntry(ppath + file.getName());
			zos.putNextEntry(ze);
			byte[] content = new byte[1024];
			int len;
			while ((len = fis.read(content)) != -1)
			{
				zos.write(content, 0, len);
				zos.flush();
			}
		}
		return true;
	}

	public static List<String> readZipEntrys(File file) throws FileNotFoundException, IOException
	{

		ZipEntry ze;
		ArrayList<String> rets = new ArrayList<>() ;

		try (ZipFile zf = new ZipFile(file);
				InputStream in = new BufferedInputStream(new FileInputStream(file));
				ZipInputStream zin = new ZipInputStream(in);)
		{

			while ((ze = zin.getNextEntry()) != null)
			{
				if (ze.isDirectory())
				{
					rets.add(ze.getName()) ;
				}
				else
				{
					rets.add(ze.getName()) ;
				}
			}
		}
		return rets ;
	}
	
	
	public static String readZipTxt(File file,String entryn,String encoding) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream() ;
		if(!readZipOut(file,entryn,baos))
			return null ;
		byte[] bs = baos.toByteArray() ;
		return new String(bs,encoding) ;
	}
	
	public static byte[] readZipBytes(File file,String entryn,String encoding) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream() ;
		if(!readZipOut(file,entryn,baos))
			return null ;
		return baos.toByteArray() ;
	}
	
	public static boolean readZipOut(File file,String entryn,OutputStream outs) throws IOException
	{
		ZipEntry ze;

		try (ZipFile zf = new ZipFile(file);
				InputStream in = new BufferedInputStream(new FileInputStream(file));
				ZipInputStream zin = new ZipInputStream(in);)
		{
			byte[] buf = new byte[1024] ;
			while ((ze = zin.getNextEntry()) != null)
			{
				String en = ze.getName() ;
				if(!entryn.equals(en))
					continue ;
				
				if (ze.isDirectory())
				{
					return false ;
				}
				
				try(InputStream tmpins = zf.getInputStream(ze);)
				{
					int rc ;
					while((rc=tmpins.read(buf))>0)
					{
						outs.write(buf, 0, rc);
					}
				}
				
				return true;
			} //end of while
		}//end of try
		// zin.closeEntry();
		return false;
	}
	
	public static void readZipOut(File file,Map<String,String> entrys,File outdir) throws IOException
	{

		ZipEntry ze;

		try (ZipFile zf = new ZipFile(file);
				InputStream in = new BufferedInputStream(new FileInputStream(file));
				ZipInputStream zin = new ZipInputStream(in);)
		{
			byte[] buf = new byte[1024] ;
			while ((ze = zin.getNextEntry()) != null)
			{
				String en = ze.getName() ;
				if(entrys!=null&&!entrys.containsKey(en))
					continue ;
				
				if (ze.isDirectory())
				{
					
					continue ;
				}
				String tar_en = entrys.get(en) ;
				if(Convert.isNullOrEmpty(tar_en))
					tar_en = en ;
				File outf = new File(outdir,tar_en) ;
				
				if(!outf.getParentFile().exists())
					outf.getParentFile().mkdirs(); 
				
				try(InputStream tmpins = zf.getInputStream(ze);
						FileOutputStream fos = new FileOutputStream(outf) ;
						)
				{
					int rc ;
					while((rc=tmpins.read(buf))>0)
					{
						fos.write(buf, 0, rc);
					}
				}
				
				
			}
		}
		// zin.closeEntry();
	}
}
