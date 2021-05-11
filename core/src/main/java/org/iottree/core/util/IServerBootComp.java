package org.iottree.core.util;

/**
 * ���������������������ӿ�
 * 
 * �ýӿ��������������Ҫ��ӵ����
 * 
 * ��:tomato server ���tomcat��Ϊһ�����������ͬʱ����.
 * ��Ӧ�ð�Tomcat������ʵ�ָýӿ�
 * 
 * �����Ϳ�����tomato�п���tomcat��������ֹͣ
 * @author Jason Zhu
 *
 */
public interface IServerBootComp
{
	public String getBootCompName();
	
	public void startComp() throws Exception;
	
	public void stopComp() throws Exception;
	
	public boolean isRunning() throws Exception;
}
