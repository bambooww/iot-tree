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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
		
		public boolean hasOutDir()
		{
			return Convert.isNotNullEmpty(outDir) ;
		}
		
		public void copyFile(File f,String subpath) throws IOException
		{
			File outf = new File(this.outDir,subpath);
			if(show_prompt)
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
			if(show_prompt)
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
	
	static boolean show_prompt = true;
	
	private static List<LangItem> langItems = null ;
	
	private static List<String> langs = null ;
	
	//private static LangItem langDef = null ;
	
	private static List<File> rootFiles = null ;
	
	private static List<String> fileTps = null ;
	
	private static String docRoot = null ;
	
	private static boolean init(boolean no_prompt ) throws Exception
	{
		show_prompt = !no_prompt ;
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
		langs = ss ;
		
		for(String s:ss)
		{
			String dir = cfg.get("lang_"+s) ;
			//boolean b_split = Convert.isNotNullEmpty(dir) ;
			
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
		if(show_prompt)
			System.out.println(" copy - "+subn) ;
		for(LangItem li:langItems)
		{
			if(!li.hasOutDir())
				continue ;
			
			li.copyFile(f,subn);
		}
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
		if(show_prompt)
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
				LangItem find_ln_tag = null;
				boolean find_ln_tag_end=false;
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
					find_ln_tag = getLangItem(tmpln) ;
					if(find_ln_tag!=null)
					{
						find_ln_tag_end = bend ;
					}
				}
				

				if(find_ln_tag!=null)
				{
					if(find_ln_tag_end)
						cur_li = null ;
					else
						cur_li = find_ln_tag ;
					
					//break;
					//add empty ln
					ln = "" ;
				}
//				else
//				{
//					if(bend)
//						cur_lang_ignore = false ;
//					else
//						cur_lang_ignore = true ;
//					
//					//continue ;
//				}
				
				if(cur_lang_ignore)
					continue ;
				
				
				
				
				if(cur_li!=null)
				{
					String ln0 = transLangInLn(ln,cur_li.lang) ;
					byte[] bs = ln0.getBytes("utf-8");
					FileOutputStream fos = cur_li.getOrCreateFileOut(subn);
					fos.write(bs);
					fos.write(next_ln);
				}
				else
				{
					for(LangItem li:langItems)
					{
						if(!li.hasOutDir())
							continue ;
						
						String ln0 = transLangInLn(ln,li.lang) ;
						byte[] bs = ln0.getBytes("utf-8");
						FileOutputStream fos = li.getOrCreateFileOut(subn);
						fos.write(bs);
						fos.write(next_ln);
					}
				}
			} // end of while
		}
		
		//close files
		for(LangItem li:langItems)
			li.closeFiles();
		
		return true;
	}
	
	
	static class LnSeg
	{
		String lang = null ;
		
		String str = null ;
		
		public LnSeg(String str)
		{
			this.str = str ;
		}
		
		
		public LnSeg(String str,String lang)
		{
			this.str = str ;
			this.lang = lang ;
		}
		
		public String toString()
		{
			if(lang==null)
				return "["+str+"]";
			return "["+lang+" - "+str+"]";
		}
	}
	
	static class SegTag
	{
		String lang = null ;
		
		int idx = -1 ;
		
		public SegTag(int idx,String lan)
		{
			this.idx = idx ;
			this.lang = lan ;
		}
	}
	
	private static List<LnSeg> splitToLnSeg(String in_ln)
	{
		String ln = in_ln ;
		ArrayList<LnSeg> segs = new ArrayList<>() ;
		do
		{
			SegTag st = findSegTag(ln) ;
			if(st==null)
			{}
			if(st==null)
			{
				if(ln!=null&&ln.length()>0)
				{
					if(segs.size()<=0)
						return null ;
					
					LnSeg lnseg = new LnSeg(ln) ;
					segs.add(lnseg) ;
					ln = null ;
				}
				return segs ;
			}
			
			if(st.idx>0)
			{
				LnSeg lnseg = new LnSeg(ln.substring(0,st.idx)) ;
				segs.add(lnseg) ;
			}
			ln = ln.substring(st.idx+st.lang.length()+2) ;
			int k = ln.indexOf("</"+st.lang+">") ;
			if(k<0)
				throw new IllegalArgumentException("<"+st.lang+"> has no end </"+st.lang+"> @ "+in_ln) ;
			if(k>0)
			{
				LnSeg lnseg = new LnSeg(ln.substring(0,k),st.lang) ;
				segs.add(lnseg) ;
			}
			ln = ln.substring(k+st.lang.length()+3) ;
			if(ln.length()<=0)
				return segs ;
		}while(ln!=null) ;
		return segs ;
	}
	
	//asdfasdf<cn>sdfasdf</cn><en>enen</en>
	/**
	 *   
	 * @param ln
	 * @return
	 */
	private static String transLangInLn(String ln,String lang)
	{
		List<LnSeg> lnsegs = splitToLnSeg(ln) ;
		if(lnsegs==null||lnsegs.size()<=0)
			return ln ;
		if(lnsegs.size()==1 && lnsegs.get(0).lang==null)
			return ln ;
		
		//System.out.print(ln+"\r\n"+lnsegs.size()+">>");
		StringBuilder sb = new StringBuilder() ;
		for(LnSeg lnseg:lnsegs)
		{
			//System.out.print(lnseg);
			if(lnseg.lang==null||lang.equals(lnseg.lang))
				sb.append(lnseg.str) ;
		}
		//System.out.println("\r\n  "+lang+">>"+sb.toString());
		return sb.toString();
	}
	
	private static SegTag findSegTag(String str)
	{
		SegTag ret = null ;
		for(String lan:langs)
		{
			int i = str.indexOf("<"+lan+">") ;
			if(i<0)
				continue ;
			if(ret==null)
			{
				ret = new SegTag(i, lan);
			}
			else
			{
				if(i<ret.idx)
					ret = new SegTag(i, lan);
			}
		}
		return ret ;
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
//		String tmps = "#### <a href=\"README.md\">1 <cn>概述</cn><en>Summary</en></a>";
//		langs = Arrays.asList("cn","en","jp") ;
//		String ss = transLangInLn(tmps,"cn") ;
//		ss = transLangInLn(tmps,"en") ;
//		if(true)
//			return ;
		
		boolean no_prompt = args.length>0 && "no_prompt".equals(args[0]) ;
		
		if(!init(no_prompt ))
		{
			System.out.println("init failed,please check conf.txt") ;
			return ;
		}
		
		System.out.println(" langs="+langs) ;
		
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
