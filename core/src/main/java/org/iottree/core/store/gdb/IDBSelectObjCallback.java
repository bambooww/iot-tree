package org.iottree.core.store.gdb;

/**
 * ��һЩ����£���Ҫ�Բ��ҵ����ݽ�����д�����ÿ����Ҫ������������ܴ�
 * ���ͨ��ֱ�Ӳ��������ҽ�������γɽ�����Ļ�����Ҫռ�ü�����ڴ�
 * 
 * Ϊ�˱���������������������ʵ�ִ������ݽӿڣ��������ݿ��ѯ��ʱ����Ϊ
 * �����ṩ��
 * 
 * �÷���Ϊ�����ݿ����о�XORM�����б�����У�ֱ�ӽ��д���ķ���
 * @author Jason Zhu
 *
 */
public interface IDBSelectObjCallback<T>
{
	/**
	 * ����true��ʾ����������һ�еĶ�ӦXORM����
	 * ����false��ʾֹͣ����
	 * @param rowidx
	 * @param o
	 * @return
	 */
	public boolean onFindObj(int rowidx,T o);
}
