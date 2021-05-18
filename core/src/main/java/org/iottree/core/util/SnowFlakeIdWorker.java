package org.iottree.core.util;


import java.util.Date;


public class SnowFlakeIdWorker
{

	// ==============================Fields===========================================
	/** ��ʼʱ��� (2020-01-01) */
	private static final long twepoch = 1577808000000L;

	/** ����id��ռ��λ�� */
	private static  final long workerIdBits = 5L;

	/** ���ݱ�ʶid��ռ��λ�� */
	private static  final long datacenterIdBits = 5L;

	/** ֧�ֵ�������id�������31 (�����λ�㷨���Ժܿ�ļ������λ�����������ܱ�ʾ�����ʮ������) */
	private static  final long maxWorkerId = -1L ^ (-1L << workerIdBits);

	/** ֧�ֵ�������ݱ�ʶid�������15 */
	private static  final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

	/** ������id��ռ��λ�� */
	private static  final long sequenceBits = 12L;

	/** ����ID������12λ */
	private final long workerIdShift = sequenceBits;

	/** ���ݱ�ʶid������16λ(12+4) */
	private final long datacenterIdShift = sequenceBits + workerIdBits;

	/** ʱ���������20λ(4+4+12) */
	private static  final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

	/** �������е����룬����Ϊ4095 (0b111111111111=0xfff=4095) */
	private final long sequenceMask = -1L ^ (-1L << sequenceBits);

	/** ��������ID(0~15) */
	private long workerId;

	/** ��������ID(0~15) */
	private long datacenterId;

	/** ����������(0~4095) */
	private long sequence = 0L;

	/** �ϴ�����ID��ʱ��� */
	private long lastTimestamp = -1L;

	// ==============================Constructors=====================================
	/**
	 * ���캯��
	 * 
	 * @param workerId
	 *            ����ID (0~31) ����id
	 * @param datacenterId
	 *            ��������ID (0~31) 
	 */
	public SnowFlakeIdWorker(long workerId, long datacenterId)
	{
		if (workerId > maxWorkerId || workerId < 0)
		{
			throw new IllegalArgumentException(
					String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
		}
		if (datacenterId > maxDatacenterId || datacenterId < 0)
		{
			throw new IllegalArgumentException(
					String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
		}
		this.workerId = workerId;
		this.datacenterId = datacenterId;
	}

	/**
	 * ����id����ȡ�����ʱ�����
	 * @param id
	 * @return
	 */
	public static long parseIdTimeMillis(long id)
	{
		return (id>>timestampLeftShift) +twepoch;
	}
	
	// ==============================Methods==========================================
	/**
	 * �����һ��ID (�÷������̰߳�ȫ��)
	 * 
	 * @return SnowflakeId
	 */
	public synchronized long nextId()
	{
		long timestamp = timeGen();

		// �����ǰʱ��С����һ��ID���ɵ�ʱ�����˵��ϵͳʱ�ӻ��˹����ʱ��Ӧ���׳��쳣
		if (timestamp < lastTimestamp)
		{
			throw new RuntimeException(String.format(
					"Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
		}

		// �����ͬһʱ�����ɵģ�����к���������
		if (lastTimestamp == timestamp)
		{
			sequence = (sequence + 1) & sequenceMask;
			// �������������
			if (sequence == 0)
			{
				// ��������һ������,����µ�ʱ���
				timestamp = tilNextMillis(lastTimestamp);
			}
		}
		// ʱ����ı䣬��������������
		else
		{
			sequence = 0L;
		}

		// �ϴ�����ID��ʱ���
		lastTimestamp = timestamp;

		// ��λ��ͨ��������ƴ��һ�����64λ��ID
		return ((timestamp - twepoch) << timestampLeftShift) //
				| (datacenterId << datacenterIdShift) //
				| (workerId << workerIdShift) //
				| sequence;
	}

	/**
	 * ��������һ�����룬ֱ������µ�ʱ���
	 * 
	 * @param lastTimestamp
	 *            �ϴ�����ID��ʱ���
	 * @return ��ǰʱ���
	 */
	protected long tilNextMillis(long lastTimestamp)
	{
		long timestamp = timeGen();
		while (timestamp <= lastTimestamp)
		{
			timestamp = timeGen();
		}
		return timestamp;
	}

	/**
	 * �����Ժ���Ϊ��λ�ĵ�ǰʱ��
	 * 
	 * @return ��ǰʱ��(����)
	 */
	protected long timeGen()
	{
		return System.currentTimeMillis();
	}

}
