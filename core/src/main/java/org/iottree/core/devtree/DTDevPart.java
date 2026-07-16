package org.iottree.core.devtree;

/**
 * device spare part
 * 
 * @author jason.zhu
 *
 */
public class DTDevPart
{
	public static class RefTP
	{
		public String name ;
		
		public String partTpID ;
		
		
	}
	
	/**
	 * using partId
	 */
	String partId = null ;
	
	String parttpId = null ;
	
	String modelNo = null ;
	
	String barCode = null ;
	
	String title = null ;
	
	String supplier ;
	
	String factory ;
	
	
	public String getPartId()
	{
		return this.partId ;
	}
}
