package org.iottree.core.msgnet;

/**
 * 实现此接口的节点，能够提供Manager范围内的资源内容
 * 
 * 外界程序，可以根据资源名称，直接获取到多个流程中的对应节点——如此，此节点可以成为
 * 顶层应用程序的配置节点。
 * 
 * 1）如InfluxDB_M节点，实现此接口，外界程序可以通过这个资源名称获取对应节点，进而可以获取对应数据库的访问Client
 * 
 * @author jason.zhu
 *
 */
public interface IMNNodeRes
{
	public String getMNResName() ;
}
