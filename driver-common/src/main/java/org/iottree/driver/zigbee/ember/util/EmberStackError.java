package org.iottree.driver.zigbee.ember.util;

public enum EmberStackError
{
    // Error codes that a router uses to notify the message initiator about a broken route.
    ROUTE_ERROR_NO_ROUTE_AVAILABLE (0x00),
    ROUTE_ERROR_TREE_LINK_FAILURE (0x01),
    ROUTE_ERROR_NON_TREE_LINK_FAILURE (0x02),
    ROUTE_ERROR_LOW_BATTERY_LEVEL (0x03),
    ROUTE_ERROR_NO_ROUTING_CAPACITY (0x04),
    ROUTE_ERROR_NO_INDIRECT_CAPACITY (0x05),
    ROUTE_ERROR_INDIRECT_TRANSACTION_EXPIRY (0x06),
    ROUTE_ERROR_TARGET_DEVICE_UNAVAILABLE (0x07),
    ROUTE_ERROR_TARGET_ADDRESS_UNALLOCATED (0x08),
    ROUTE_ERROR_PARENT_LINK_FAILURE (0x09),
    ROUTE_ERROR_VALIDATE_ROUTE (0x0a),
    ROUTE_ERROR_SOURCE_ROUTE_FAILURE (0x0b),
    ROUTE_ERROR_MANY_TO_ONE_ROUTE_FAILURE (0x0c),
    ROUTE_ERROR_ADDRESS_CONFLICT (0x0d),
    ROUTE_ERROR_VERIFY_ADDRESSES (0x0e),
    ROUTE_ERROR_PAN_IDENTIFIER_UPDATE (0x0f),

    NETWORK_STATUS_NETWORK_ADDRESS_UPDATE (0x10),
    NETWORK_STATUS_BAD_FRAME_COUNTER (0x11),
    NETWORK_STATUS_BAD_KEY_SEQUENCE_NUMBER (0x12),
    NETWORK_STATUS_UNKNOWN_COMMAND (0x13);
    
    private final int val;
	//private final String title;

    EmberStackError(int val) //, String title)
	{
		this.val = val;
	}
	
	public int getIntVal()
	{
		return this.val ;
	}
	
//	public String getTitle()
//	{
//		return this.title ;
//	}
	
	public static EmberStackError valOfInt(int v)
	{
		for(EmberStackError st:EmberStackError.values())
		{
			if(st.val==v)
				return st ;
		}
		return null ;
	}
}
