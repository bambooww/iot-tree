package org.iottree.driver.zigbee.zspec.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.iottree.driver.zigbee.zspec.ZSpecConsts;
import org.iottree.driver.zigbee.zspec.types.BroadcastAddr;

public class ZSpecUtil
{

	public static boolean isBroadcastAddress(int addr)
	{
		return BroadcastAddr.valOfInt(addr) !=null;
	}


	/**
	 * AES-128-MMO 哈希（对应 Zigbee Install Code，见 Document 13-0402-13 - 10.1）
	 */
	public static byte[] aes128MmoHash(byte[] data) throws Exception
	{
		byte[] hashResult = new byte[ZSpecConsts.AES_MMO_128_BLOCK_SIZE];
		byte[] temp = new byte[ZSpecConsts.AES_MMO_128_BLOCK_SIZE];

		int position = 0;
		int remainingLength = data.length;

		// 逐块处理完整 16 字节块
		while (remainingLength >= ZSpecConsts.AES_MMO_128_BLOCK_SIZE)
		{
			byte[] chunk = new byte[ZSpecConsts.AES_MMO_128_BLOCK_SIZE];
			System.arraycopy(data, position, chunk, 0, ZSpecConsts.AES_MMO_128_BLOCK_SIZE);
			aes128MmoHashUpdate(hashResult, chunk, chunk.length);
			position += ZSpecConsts.AES_MMO_128_BLOCK_SIZE;
			remainingLength -= ZSpecConsts.AES_MMO_128_BLOCK_SIZE;
		}

		// 拷贝剩余字节到 temp
		for (int i = 0; i < remainingLength; i++)
		{
			temp[i] = data[position + i];
		}

		// 按规范拼接 1 位后跟全 0
		temp[remainingLength] = (byte) 0x80;

		// 若追加该位会越过 16 字节边界，先哈希当前块，再追加一个全 0 块
		if (ZSpecConsts.AES_MMO_128_BLOCK_SIZE - remainingLength < 3)
		{
			aes128MmoHashUpdate(hashResult, temp, ZSpecConsts.AES_MMO_128_BLOCK_SIZE);
			java.util.Arrays.fill(temp, (byte) 0);
		}

		// 长度字段（MMO 规范的特殊写法：>> 5 和 << 3）
		temp[ZSpecConsts.AES_MMO_128_BLOCK_SIZE - 2] = (byte) ((data.length >> 5) & 0xff);
		temp[ZSpecConsts.AES_MMO_128_BLOCK_SIZE - 1] = (byte) ((data.length << 3) & 0xff);

		aes128MmoHashUpdate(hashResult, temp, ZSpecConsts.AES_MMO_128_BLOCK_SIZE);

		// 复制结果
		byte[] result = new byte[ZSpecConsts.AES_MMO_128_BLOCK_SIZE];
		System.arraycopy(hashResult, 0, result, 0, ZSpecConsts.AES_MMO_128_BLOCK_SIZE);
		return result;
	}

	/**
	 * 单块更新：hashResult = AES-128-ECB(hashResult, block) XOR block 其中 AES 的密钥是
	 * hashResult（作为 128 位密钥），明文是 block
	 */
	private static void aes128MmoHashUpdate(byte[] hashResult, byte[] block, int blockLength) throws Exception
	{
		SecretKeySpec key = new SecretKeySpec(hashResult, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encrypted = cipher.doFinal(block, 0, blockLength);

		for (int i = 0; i < ZSpecConsts.AES_MMO_128_BLOCK_SIZE; i++)
		{
			hashResult[i] = (byte) (encrypted[i] ^ block[i]);
		}
	}
}
