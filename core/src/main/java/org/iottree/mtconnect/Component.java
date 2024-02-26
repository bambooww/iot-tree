package org.iottree.mtconnect;

public class Component
{
	String id = null ;
	
	String name=  null ;
	
	String nativeName = null ;
	
	/**
	 * interval in milliseconds between the completion of the reading of data
	 * 
	 * sampleRate
	 */
	float sampleInterval = -1 ;
	
	String uuid = null ;
	
	String coordinateSystemIdRef = null ;
	
	public Component()
	{}
	
	public String getId()
	{
		return this.id ;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getNativeName()
	{
		return this.nativeName ;
	}
	
	public float getSampleInterval()
	{
		return this.sampleInterval ;
	}
	
	public String getUuid()
	{
		return uuid ;
	}
	
	public String getCoordinateSystemIdRef()
	{
		return this.coordinateSystemIdRef ;
	}
}
