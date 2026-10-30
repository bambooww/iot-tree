package org.iottree.driver.zigbee.zstack;

/**
 * 全新协调器初始化过程
 * 
 * Zigbee2mqtt 会通过 zigbee-herdsman 驱动协调器自动生成
 * 	PAN ID、Extended PAN ID、Channel 与 Network Key 四项核心网络参数并写入 NV，
 * 同时将 IEEE 地址（芯片出厂固化）与 逻辑设备类型 一并持久化存储；
 * 其余配置参数仅保存在本地 configuration.yaml 中，不会写入协调器。
 * 
 * 首次上电
  │
  ▼
┌─────────────────────────────────────────────┐
│  Z-Stack 初始化 (osal_nv_init + zgInit)      │
│  · 初始化 NV 存储系统                         │
│  · 读取 IEEE 地址（芯片固化）                  │
│  · 检查 NV 中是否存在有效网络信息              │
└─────────────────────────────────────────────┘
  │
  ▼
┌─────────────────────────────────────────────┐
│  BDB 层检测：未找到有效网络信息 → 判定为出厂新设备  │
└─────────────────────────────────────────────┘
  │
  ▼
┌─────────────────────────────────────────────┐
│  自动建网流程                                 │
│  1. 从 configuration.yaml 读取用户配置         │
│     （pan_id, ext_pan_id, channel, network_key）│
│  2. 对标记为 GENERATE 的参数执行随机生成         │
│  3. 能量扫描选择最优信道（若未指定）              │
└─────────────────────────────────────────────┘
  │
  ▼
┌─────────────────────────────────────────────┐
│  写入 NV（Flash 非易失性存储）                  │
│  · ZCD_NV_PANID        ← PAN ID              │
│  · ZCD_NV_EXT_PAN_ID   ← Extended PAN ID     │
│  · ZCD_NV_CHANLIST     ← Channel 掩码         │
│  · ZCD_NV_NWK_KEY      ← Network Key         │
│  · ZCD_NV_STARTUP_OPTION ← 设为"已组网"        │
└─────────────────────────────────────────────┘
  │
  ▼
┌─────────────────────────────────────────────┐
│  广播信标帧，宣告网络建立完成                    │
│  等待设备加入                                  │
└─────────────────────────────────────────────┘
 * 
 * @author jason.zhu
 */
public class ZStackCoorNewInit
{

}
