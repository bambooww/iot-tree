package org.iottree.core.conn.masyn;

/**
 * ��������еļ�����
 * ����֧�ַ��ͻ���ܹ����еĽ��ȸ���
 * @author zzj
 *
 */
public interface ITransListener
{
	void onTransStarted(long total_len) ;
	
	void onTransProcess(long trans_len) ;
	
	void onTransEnd() ;
}
