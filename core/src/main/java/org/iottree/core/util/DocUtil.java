package org.iottree.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * for doc split to multi language
 * @author jason.zhu
 *
 */
public class DocUtil
{
	public static class LangItem
	{
		String lang = null ;
		
		String outDir = null ;
		
		HashMap<String,FileOutputStream> subpath2fos = new HashMap<>() ; 
		
		public LangItem(String ln,String outdir)
		{
			this.lang = ln ;
			this.outDir = outdir ;
		}
		
		public void copyFile(File f,String subpath) throws IOException
		{
			File outf = new File(this.outDir,subpath);
			System.out.println("         "+this.lang+"  --> "+outf.getCanonicalPath()) ;
			FileUtils.copyFile(f, outf);
		}
		
		public FileOutputStream getOrCreateFileOut(String subpath) throws IOException
		{
			FileOutputStream fos = subpath2fos.get(subpath) ;
			if(fos!=null)
				return fos ;
			
			File outf = new File(this.outDir,subpath);
			if(!outf.getParentFile().exists())
				outf.getParentFile().mkdirs();
			System.out.println("          "+this.lang+" --> "+outf.getCanonicalPath()) ;
			fos = new FileOutputStream(outf) ;
			subpath2fos.put(subpath, fos) ;
			return fos ;
		}
		
		public void closeFiles()
		{
			for(FileOutputStream fos:subpath2fos.values())
			{
				try
				{
					fos.close();
				}
				catch(Exception e)
				{}
			}
			
			subpath2fos = new HashMap<>() ;
		}
	}
	
	private static List<LangItem> langItems = null ;
	
	//private static LangItem langDef = null ;
	
	private static List<File> rootFiles = null ;
	
	private static List<String> fileTps = null ;
	
	private static String docRoot = null ;
	
	private static boolean init() throws Exception
	{
		docRoot = new File(".").getCanonicalPath() ;
		
		File conff = new File("./conf.txt") ;
		if(!conff.exists())
		{
			System.out.println("no conf.txt found") ;
			return false;
		}
		HashMap<String,String> cfg = null;//new HashMap<>();
		try(FileInputStream fis = new FileInputStream(conff);)
		{
			cfg = Convert.readStringMapFromStream(fis, "UTF-8") ;
		}
		
		String langall = cfg.get("lang_all");
		if(Convert.isNullOrEmpty(langall))
		{
			System.out.println("no lang_all found") ;
			return false;
		}
		
		String lndef = cfg.get("lang_default") ;
		if(Convert.isNullOrEmpty(lndef))
		{
			System.out.println("no lang_default found") ;
			return false;
		}
		
		ArrayList<LangItem> lnitems = new ArrayList<>() ;
		List<String> ss = Convert.splitStrWith(langall, ",|") ;
		
		for(String s:ss)
		{
			String dir = cfg.get("lang_"+s) ;
			if(Convert.isNullOrEmpty(dir))
				continue ;
			LangItem lni = new LangItem(s,dir) ;
			lnitems.add(lni) ;
//			if(lndef.equals(s))
//				langDef = lni;
		}
		langItems = lnitems ;
		
		String filetps = cfg.get("file_types");
		if(filetps!=null)
		{
			filetps = filetps.toLowerCase();
			fileTps = Convert.splitStrWith(filetps, ",|");
		}
		if(fileTps==null||fileTps.size()<=0)
		{
			System.out.println("no file_types found") ;
			return false;
		}
		
		String rootfiles = cfg.get("root_files");
		ss = Convert.splitStrWith(rootfiles, ",|") ;
		if(ss==null)
		{
			System.out.println("no root_files found") ;
			return false;
		}
		ArrayList<File> rfs = new ArrayList<>() ;
		for(String s:ss)
		{
			File tmpf = new File(s) ;
			if(!tmpf.exists())
				continue ;
			rfs.add(tmpf) ;
		}
		if(rfs.size()<=0)
		{
			System.out.println("no root_files found") ;
			return false;
		}
		rootFiles = rfs ;

		//if(langDef!=null&&langItems.size()>0)
		if(langItems.size()>0)
			return true ;
		else
			return false;
	}
	
	private static LangItem getLangItem(String ln)
	{
		for(LangItem li:langItems)
		{
			if(li.lang.equals(ln))
				return li ;
		}
		return null ;
	}
	
	private static String getFileSubName(File f) throws IOException
	{
		String fp = f.getCanonicalPath() ;
		if(!fp.startsWith(docRoot))
		{
			throw new IOException("invalid split file path="+fp) ;
		}
		return fp.substring(docRoot.length()) ;
	}
	
	private static void copyFile(File f) throws IOException
	{
		String subn = getFileSubName(f) ;
		System.out.println(" copy - "+subn) ;
		for(LangItem li:langItems)
			li.copyFile(f,subn);
	}
	
	private static boolean splitFile(File f) throws IOException
	{
		String fn = f.getName().toLowerCase() ;
		int k = fn.lastIndexOf(".") ;
		if(k<=0)
			return false;
		String ext = fn.substring(k+1).toLowerCase() ;
		if(!fileTps.contains(ext))
			return false;
		
		final byte[] next_ln = "\r\n".getBytes();
		
//		String fp = f.getCanonicalPath() ;
//		if(!fp.startsWith(docRoot))
//		{
//			throw new IOException("invalid split file path="+fp) ;
//		}
		String subn = getFileSubName(f) ;
		System.out.println(" split - "+subn) ;
		LangItem cur_li = null ;
		boolean cur_lang_ignore = false;
		
		try(FileInputStream fis = new FileInputStream(f);
				BufferedReader br = new BufferedReader(new InputStreamReader(fis,"utf-8")) ;
				)
		{
			String ln;
			while((ln=br.readLine())!=null)
			{
				String tmpln = ln.trim() ;
				if(tmpln.startsWith("[")&&tmpln.endsWith("]"))
				{
					tmpln = tmpln.substring(1,tmpln.length()-1).trim() ;
					boolean bend = false;
					if(tmpln.startsWith("/"))
					{//may end
						bend = true ;
						tmpln = tmpln.substring(1).trim() ;
					}
					//chk ln
					LangItem litem = getLangItem(tmpln) ;
					if(litem!=null)
					{
						if(bend)
							cur_li = null ;
						else
							cur_li = litem ;
						
						//break;
						//add empty ln
						ln = "" ;
					}
					else
					{
						if(bend)
							cur_lang_ignore = false ;
						else
							cur_lang_ignore = true ;
						
						continue ;
					}
				}
				
				if(cur_lang_ignore)
					continue ;
				
				byte[] bs = ln.getBytes("utf-8");
				if(cur_li!=null)
				{
					FileOutputStream fos = cur_li.getOrCreateFileOut(subn);
					fos.write(bs);
					fos.write(next_ln);
				}
				else
				{
					for(LangItem li:langItems)
					{
						FileOutputStream fos = li.getOrCreateFileOut(subn);
						fos.write(bs);
						fos.write(next_ln);
					}
				}
			}
		}
		
		//close files
		for(LangItem li:langItems)
			li.closeFiles();
		
		return true;
	}
	
	private static void splitDir(File dir) throws IOException
	{
		File[] fs = dir.listFiles() ;
		for(File f:fs)
		{
			if(f.isDirectory())
			{
				splitDir(f) ;
				continue ;
			}
			if(!splitFile(f))
				copyFile(f) ;
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		if(!init())
		{
			System.out.println("init failed,please check conf.txt") ;
			return ;
		}
		
		for(File rf:rootFiles)
		{
			if(rf.isDirectory())
			{
				splitDir(rf) ;
				continue;
			}
			
			if(!splitFile(rf))
			{
				
			}
		}
	}
}
