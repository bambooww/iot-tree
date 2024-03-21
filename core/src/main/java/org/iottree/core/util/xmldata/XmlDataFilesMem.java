package org.iottree.core.util.xmldata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author jason.zhu
 *
 */
public class XmlDataFilesMem
{
	public static class FileItem
	{
		String fileName = null ;
		
		int startPos = -1 ;
		
		int len = -1 ;
		
		public FileItem(String filename,int startpos,int len)
		{
			this.fileName = filename ;
			this.startPos = startpos ;
			this.len = len ;
		}
	}
	
	byte[] totalBuf = null ;
	
	int headBufLen = -1 ;
	
	XmlData innerXD = null ;
	
	ArrayList<FileItem> fileItems = new ArrayList<>() ;
	
	public XmlDataFilesMem(byte[] totalbuf,int headlen,XmlData innerxd)
	{
		this.totalBuf = totalbuf ;
		this.headBufLen = headlen ;
		this.innerXD = innerxd ;
	}
	
	public XmlData getInnerXD()
	{
		return innerXD ;
	}
	
	public byte[] getTotalBuf()
	{
		return totalBuf ;
	}
	
	public int getHeadBufLen()
	{
		return this.headBufLen ;
	}
	
	public List<FileItem> getFileItems()
	{
		return fileItems ;
	}
	
	public FileItem getFileItem(String filename)
	{
		for(FileItem fi:this.fileItems)
		{
			if(filename.equals(fi.fileName))
				return fi ;
		}
		return null ;
	}
	
	public void writeFileItemTo(FileItem fi,File tarf) throws FileNotFoundException, IOException
	{
		try(FileOutputStream fos = new FileOutputStream(tarf);)
		{
			fos.write(this.totalBuf, fi.startPos, fi.len);
		}
	}
}
