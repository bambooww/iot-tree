package org.iottree.driver.zigbee.zspec.zdo_def;

public class TagProcessStatus
{
	/** Indicate a previously received TLV tag ID */
    public int tagId ; //: number;
    /** The associated status of whether it is processed */
    public int processingStatus ; //: Status.SUCCESS | Status.INV_REQUESTTYPE | Status.NOT_SUPPORTED;
}
