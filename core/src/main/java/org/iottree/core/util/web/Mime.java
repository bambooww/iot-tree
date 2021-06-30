package org.iottree.core.util.web ;

import java.util.* ;


public class Mime
{
	static HashMap<String,String> MIME = new HashMap<> () ;
	
	static final String DEFAULT = "application/octet-stream" ;
	
	static 	
	{
		MIME.put ("jpeg" , "image/jpeg") ;
		MIME.put ("jpg" , "image/jpeg") ;
		MIME.put ("jfif" , "image/jpeg") ;
		MIME.put ("jfif-tbnl" , "image/jpeg") ;
		MIME.put ("jpe" , "image/jpeg") ;
		MIME.put ("jfif" , "image/jpeg") ;
		MIME.put ("tiff" , "image/tiff") ;
		MIME.put ("tif" , "image/tiff") ;
		MIME.put ("gif" , "image/gif") ;
		MIME.put ("zip" , "application/zip") ;
		MIME.put ("htm" , "text/html") ;
		MIME.put ("html" , "text/html") ;
		MIME.put ("oda" , "application/oda") ;
		MIME.put ("pdf" , "application/pdf") ;
		MIME.put ("rtf" , "application/rtf") ;
		MIME.put ("css" , "text/css") ;
		MIME.put ("js" , "text/javascript") ;
		MIME.put ("doc" , "text/application/msword") ;
		MIME.put ("png" , "image/png") ;
	}
	
	
	public static String getContentType (String fileName) 
	{
		if (fileName == null)
			return DEFAULT ;
		fileName = fileName.trim () ;
		int index = fileName.lastIndexOf ('.') ;
		if (index < 0 || index == fileName.length () - 1)
			return DEFAULT ;
			
		String ext = fileName.substring (index + 1).toLowerCase() ;
		
		String contentType = (String) MIME.get (ext) ;
		
		if (contentType == null)
			return DEFAULT ;
			
		return contentType ;
	}
	
	/**
	 * 根据文件扩展名，获得对应的mime表现形式的content type
	 * @param file_ext
	 * @return
	 */
	public static String getContentTypeByExt(String file_ext)
	{
		return MIME.get(file_ext.toLowerCase()) ;
	}
}