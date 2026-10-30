package org.iottree.driver.zigbee.zstack.util;

/**
 * 非易失性存储区块定义
 * 
 * @author jason.zhu
 */
public class NVItemIDS
{
	/**
	 * NV ID 0x0001 就是 ZCD_NV_EXTADDR，即 IEEE 地址的存储位置。
	 * TI 官方明确将其列为克隆协调器时必须复制的 NV 项之一
	 */
	public static final int ZCD_NV_EXTADDR = 0x0001; //协调器 IEEE 地址
	
	public static final int ZCD_NV_BOOTCOUNTER = 0x0002;
	public static final int ZCD_NV_STARTUP_OPTION = 0x0003;
	
	/**
	 * 对应 Z-Stack 网络层（NWK）的 ZCD_NV_NIB（Network Information Base，网络信息库），
	 * 这是 Zigbee 网络层最核心的数据结构，记录了协调器当前网络的全部运行状态
	 */
	public static final int ZCD_NV_NIB = 0x0021; //116bytes,网络信息库
	public static final int ZCD_NV_DEVICE_LIST = 0x0022;
	public static final int ZCD_NV_ADDRMGR = 0x0023;
	public static final int ZCD_NV_EXTENDED_PAN_ID = 0x002D;//		扩展PAN ID（8字节）
	
	public static final int ZCD_NV_BINDING_TABLE = 0x0041;
	public static final int ZCD_NV_GROUP_TABLE = 0x0042;
	public static final int ZCD_NV_SECURITY_LEVEL = 0x0061;
	public static final int ZCD_NV_PRECFGKEY = 0x0062;
	public static final int ZCD_NV_NWKKEY = 0x0082;
	public static final int ZCD_NV_PANID = 0x0083; // PAN ID（2字节）
	public static final int ZCD_NV_CHANLIST = 0x0084;
	
	/**
	 * 长度：1 字节
		生成规则：由固件编译时决定，Sonoff Dongle-P 的 Z-Stack 固件预编译为 Coordinator 角色
		作用：标识设备在网络中的角色（协调器/路由器/终端）
	 */
	public static final int ZCD_NV_LOGICAL_TYPE = 0x0087;
	
	
	/**
	 * 重置 NV 的方法：若需清空所有 NV 参数恢复出厂状态，可通过 MT 命令写入
	 * SYS_OSAL_NV_WRITE(0x0089, 0, 1, [0x03])
	 * 然后重启协调器，BDB 层检测到该标志后会清除网络状态并重新建网
	 */
	public static final int ZCD_STARTOPT_DEFAULT_NETWORK_STATE = 0x0089 ; //, 0, 1, [0x03])  // 
	
	//？？
	public static final int ZCD_NV_NWK_ACTIVE_KEY_INFO = 0x003A; //16+1 字节	当前网络密钥
	public static final int ZCD_NV_APS_USE_EXT_PANID = 0x0047; //8 字节	APS 层扩展 PAN ID

	
	// -----------------
	public static final int XTADDR = 1;
	public static final int BOOTCOUNTER = 2;
	public static final int STARTUP_OPTION = 3;
	public static final int START_DELAY = 4;
	public static final int NIB = 33;
	public static final int DEVICE_LIST = 34;
	public static final int ADDRMGR = 35;
	public static final int POLL_RATE = 36;
	public static final int QUEUED_POLL_RATE = 37;
	public static final int RESPONSE_POLL_RATE = 38;
	public static final int REJOIN_POLL_RATE = 39;
	public static final int DATA_RETRIES = 40;
	public static final int POLL_FAILURE_RETRIES = 41;
	public static final int STACK_PROFILE = 42;
	public static final int INDIRECT_MSG_TIMEOUT = 43;
	public static final int ROUTE_EXPIRY_TIME = 44;
	public static final int EXTENDED_PAN_ID = 45;
	public static final int BCAST_RETRIES = 46;
	public static final int PASSIVE_ACK_TIMEOUT = 47;
	public static final int BCAST_DELIVERY_TIME = 48;
	public static final int NWK_MODE = 49;
	public static final int CONCENTRATOR_ENABLE = 50;
	public static final int CONCENTRATOR_DISCOVERY = 51;
	public static final int CONCENTRATOR_RADIUS = 52;
	public static final int CONCENTRATOR_RC = 54;
	public static final int NWK_MGR_MODE = 55;
	public static final int SRC_RTG_EXPIRY_TIME = 56;
	public static final int ROUTE_DISCOVERY_TIME = 57;
	public static final int NWK_ACTIVE_KEY_INFO = 58;
	public static final int NWK_ALTERN_KEY_INFO = 59;
	public static final int ROUTER_OFF_ASSOC_CLEANUP = 60;
	public static final int NWK_LEAVE_REQ_ALLOWED = 61;
	public static final int NWK_CHILD_AGE_ENABLE = 62;
	public static final int DEVICE_LIST_KA_TIMEOUT = 63;
	public static final int BINDING_TABLE = 65;
	public static final int GROUP_TABLE = 66;
	public static final int APS_FRAME_RETRIES = 67;
	public static final int APS_ACK_WAIT_DURATION = 68;
	public static final int APS_ACK_WAIT_MULTIPLIER = 69;
	public static final int BINDING_TIME = 70;
	public static final int APS_USE_EXT_PANID = 71;
	public static final int APS_USE_INSECURE_JOIN = 72;
	public static final int COMMISSIONED_NWK_ADDR = 73;
	public static final int APS_NONMEMBER_RADIUS = 75;
	public static final int APS_LINK_KEY_TABLE = 76;
	public static final int APS_DUPREJ_TIMEOUT_INC = 77;
	public static final int APS_DUPREJ_TIMEOUT_COUNT = 78;
	public static final int APS_DUPREJ_TABLE_SIZE = 79;
	public static final int DIAGNOSTIC_STATS = 80;
	public static final int BDBNODEISONANETWORK = 85;
	public static final int SECURITY_LEVEL = 97;
	public static final int PRECFGKEY = 98;
	public static final int PRECFGKEYS_ENABLE = 99;
	public static final int SECURITY_MODE = 100;
	public static final int SECURE_PERMIT_JOIN = 101;
	public static final int APS_LINK_KEY_TYPE = 102;
	public static final int APS_ALLOW_R19_SECURITY = 103;
	public static final int IMPLICIT_CERTIFICATE = 105;
	public static final int DEVICE_PRIVATE_KEY = 106;
	public static final int CA_PUBLIC_KEY = 107;
	public static final int KE_MAX_DEVICES = 108;
	public static final int USE_DEFAULT_TCLK = 109;
	public static final int RNG_COUNTER = 111;
	public static final int RANDOM_SEED = 112;
	public static final int TRUSTCENTER_ADDR = 113;
	public static final int LEGACY_NWK_SEC_MATERIAL_TABLE_START = 117; // Valid for <= Z-Stack 3.0.x
	public static final int EX_NWK_SEC_MATERIAL_TABLE = 7; // Valid for >= Z-Stack 3.x.0
	public static final int USERDESC = 129;
	public static final int NWKKEY = 130;
	public static final int PANID = 131;
	public static final int CHANLIST = 132;
	public static final int LEAVE_CTRL = 133;
	public static final int SCAN_DURATION = 134;
	public static final int LOGICAL_TYPE = 135;
	public static final int NWKMGR_MIN_TX = 136;
	public static final int NWKMGR_ADDR = 137;
	public static final int ZDO_DIRECT_CB = 143;
	public static final int SCENE_TABLE = 145;
	public static final int MIN_FREE_NWK_ADDR = 146;
	public static final int MAX_FREE_NWK_ADDR = 147;
	public static final int MIN_FREE_GRP_ID = 148;
	public static final int MAX_FREE_GRP_ID = 149;
	public static final int MIN_GRP_IDS = 150;
	public static final int MAX_GRP_IDS = 151;
	public static final int OTA_BLOCK_REQ_DELAY = 152;
	public static final int SAPI_ENDPOINT = 161;
	public static final int SAS_SHORT_ADDR = 177;
	public static final int SAS_EXT_PANID = 178;
	public static final int SAS_PANID = 179;
	public static final int SAS_CHANNEL_MASK = 180;
	public static final int SAS_PROTOCOL_VER = 181;
	public static final int SAS_STACK_PROFILE = 182;
	public static final int SAS_STARTUP_CTRL = 183;
	public static final int SAS_TC_ADDR = 193;
	public static final int SAS_TC_MASTER_KEY = 194;
	public static final int SAS_NWK_KEY = 195;
	public static final int SAS_USE_INSEC_JOIN = 196;
	public static final int SAS_PRECFG_LINK_KEY = 197;
	public static final int SAS_NWK_KEY_SEQ_NUM = 198;
	public static final int SAS_NWK_KEY_TYPE = 199;
	public static final int SAS_NWK_MGR_ADDR = 200;
	public static final int SAS_CURR_TC_MASTER_KEY = 209;
	public static final int SAS_CURR_NWK_KEY = 210;
	public static final int SAS_CURR_PRECFG_LINK_KEY = 211;
	public static final int TCLK_SEED = 257;
	// biome-ignore lint/suspicious/noDuplicateEnumValues: legacy
	public static final int LEGACY_TCLK_TABLE_START_12 = 257; // Keep it for Legacy 1.2 stack
	public static final int LEGACY_TCLK_TABLE_START = 273; // Valid for <= Z-Stack 3.0.x
	public static final int EX_TCLK_TABLE = 4; // Valid for >= Z-Stack 3.0.x
	public static final int APS_LINK_KEY_DATA_START = 513;
	public static final int APS_LINK_KEY_DATA_END = 767;
	public static final int DUPLICATE_BINDING_TABLE = 768;
	public static final int DUPLICATE_DEVICE_LIST = 769;
	public static final int DUPLICATE_DEVICE_LIST_KA_TIMEOUT = 770;
	public static final int ZNP_HAS_CONFIGURED_ZSTACK1 = 3840;
	public static final int ZNP_HAS_CONFIGURED_ZSTACK3 = 96;
	public static final int ZCD_NV_EX_APS_KEY_DATA_TABLE = 6;
	public static final int ZCD_NV_EX_ADDRMGR = 1;
}
