package org.iottree.core.msgnet;

public interface IMNContProvider
{
//	/**
//	 * 唯一名称，可以通过配置来限定当前系统使用哪个Provider
//	 * @return
//	 */
//	public String getMsgNetContName() ;
	
	public IMNContainer getMsgNetContainer(String container_id) ;
}
