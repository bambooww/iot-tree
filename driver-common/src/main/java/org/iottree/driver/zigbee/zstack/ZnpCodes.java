package org.iottree.driver.zigbee.zstack;

/**
 * @version 1.3
 * @author Miles Lorenz
 */
public class ZnpCodes {
    // Maximum number of bytes in a messageBody
    public static final int MAX_MESSAGE_SIZE = 100;

    // Channels -> ATTENTION: byte[] are mirrored!
    public enum Channels {

        CHANNEL_NONE(new byte[0], 0),
        CHANNEL_11(new byte[]{(byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x00}, 11),
        CHANNEL_12(new byte[]{(byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x00}, 12),
        CHANNEL_13(new byte[]{(byte) 0x00, (byte) 0x20, (byte) 0x00, (byte) 0x00}, 13),
        CHANNEL_14(new byte[]{(byte) 0x00, (byte) 0x40, (byte) 0x00, (byte) 0x00}, 14),
        CHANNEL_15(new byte[]{(byte) 0x00, (byte) 0x80, (byte) 0x00, (byte) 0x00}, 15),
        CHANNEL_16(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00}, 16),
        CHANNEL_17(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00}, 17),
        CHANNEL_18(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x00}, 18),
        CHANNEL_19(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x00}, 19),
        CHANNEL_20(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x10, (byte) 0x00}, 20),
        CHANNEL_21(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x20, (byte) 0x00}, 21),
        CHANNEL_22(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x00}, 22),
        CHANNEL_23(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x80, (byte) 0x00}, 23),
        CHANNEL_24(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01}, 24),
        CHANNEL_25(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02}, 25),
        CHANNEL_26(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04}, 26);

        private byte[] value;
        private int logical;

        Channels(byte[] value, int logical) {

            this.value = value;
            this.logical = logical;
        }

        public byte[] value() {
            return value;
        }

        public static Channels resolve(byte channel) {
            for (Channels chan : Channels.values()) {
                if (chan.logical == (int) channel)
                    return chan;
            }
            return CHANNEL_NONE;
        }
    }

    public enum DevStates {
        DEV_HOLD,                                // Initialized - not started automatically
        DEV_INIT,                                // Initialized - not connected to anything
        DEV_NWK_DISC,                            // Discovering PAN's to join
        DEV_NWK_JOINING,                         // Joining a PAN
        DEV_NWK_SEC_REJOIN_CURR_CHANNEL,         // ReJoining a PAN in secure mode scanning in current channel, only for end devices
        DEV_END_DEVICE_UNAUTH,                   // Joined but not yet authenticated by trust center
        DEV_END_DEVICE,                          // Started as device after authentication
        DEV_ROUTER,                              // Device joined, authenticated and is a router
        DEV_COORD_STARTING,                      // Started as Zigbee Coordinator
        DEV_ZB_COORD,                            // Started as Zigbee Coordinator
        DEV_NWK_ORPHAN,                          // Device has lost information about its parent..
        DEV_NWK_KA,                              // Device is sending KeepAlive message to its parent
        DEV_NWK_BACKOFF,                         // Device is waiting before trying to rejoin
        DEV_NWK_SEC_REJOIN_ALL_CHANNEL,          // ReJoining a PAN in secure mode scanning in all channels, only for end devices
        DEV_NWK_TC_REJOIN_CURR_CHANNEL,          // ReJoining a PAN in Trust center mode scanning in current channel, only for end devices
        DEV_NWK_TC_REJOIN_ALL_CHANNEL            // ReJoining a PAN in Trust center mode scanning in all channels, only for end device
    }

    public enum CommissioningModes {
        IDDLE,
        TOUCHLINK,
        STEERING,
        FORMATION,
        BINDING,
        INITIALIZATION,
        PARENT_LOST;

        public static CommissioningModes resolve(byte mode) {

            switch (mode) {
                case 1:
                    return TOUCHLINK;
                case 2:
                case 3:
                    return STEERING;
                case 4:
                case 5:
                case 6:
                case 7:
                    return FORMATION;
                case 8:
                    return BINDING;
                case 16:
                    return INITIALIZATION;
                case 32:
                    return PARENT_LOST;
                default:
                    return IDDLE;
            }
        }

    }

    //CMD0 UTIL
    public static final int util = 7;
    //CMD1 UTIL-LED
    public static final int util_led = 10;
    //CMD1 UTIL-DEVICE_INFO
    public static final int util_deviceInfo = 1;

    //CMD0 BC
    public static final int bc = 22;
    public static final int bcResp = 0xaa;

    public static final int bc_info = 2;
    public static final int bc_reset = 3;
    public static final int bc_startCoord = 4;
    public static final int bc_startRouter = 5;

    public static final int bc_sendToOne = 7;
    public static final int bc_sendToAll = 8;
    public static final int bc_receiveMSG = 9;
    public static final int bc_sendCommand = 10;
    public static final int bc_setChannels = 11;

    public static String toHex(int b) {
        String hex = Integer.toHexString((b & 0xff));
        if (hex.length() == 1)
            hex = "0" + hex;
        hex += " ";
        return hex;
    }

    public static String toHex(byte[] bytes) {
        String bString = "";
        for (byte b : bytes) {
            bString += toHex(b);
        }
        return bString;
    }

    public static String toWords(byte[] status) {
        return new String(status);
    }

    public static byte[] toBytes(String hexString) {

        byte[] bytes = new byte[hexString.length() / 3];
        int index = 0;
        while (hexString != null) {
            bytes[index++] = (byte)
                    ((Character.digit(hexString.charAt(0), 16) << 4)
                            + Character.digit(hexString.charAt(1), 16));

            if (hexString.length() > 3)
                hexString = hexString.substring(3);
            else
                hexString = null;
        }
        return bytes;

    }

    public static byte[] generateMessage(int cmd0, int cmd1, byte[] data) {

        int dataLength;
        if (data == null)
            dataLength = 0;
        else
            dataLength = data.length;

        byte[] tmpBytes = new byte[dataLength + 5];
        int pointer = 0;
        tmpBytes[pointer++] = (byte) 254; // SOP
        tmpBytes[pointer++] = (byte) dataLength;
        tmpBytes[pointer++] = (byte) cmd0;
        tmpBytes[pointer++] = (byte) cmd1;

        if (data != null) {
            for (byte dataByte : data) {
                tmpBytes[pointer++] = dataByte;
            }
        }
        tmpBytes[pointer] = calcFCS(tmpBytes);
        return tmpBytes;
    }

    static byte calcFCS(byte[] bytes) {
        byte xorResult;
        xorResult = 0;
        for (int i = 1; i < bytes.length - 1; i++) {
            xorResult = (byte) (xorResult ^ bytes[i]);
        }
        return xorResult;
    }

}