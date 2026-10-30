package org.iottree.driver.zigbee.ember.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ASHCounters
{
	/** DATA frame data fields bytes transmitted */
    public int txData=0;
    /** frames of all types transmitted */
    public int txAllFrames=0;
    /** DATA frames transmitted */
    public int txDataFrames=0;
    /** ACK frames transmitted */
    public int txAckFrames=0;
    /** NAK frames transmitted */
    public int txNakFrames=0;
    /** DATA frames retransmitted */
    public int  txReDataFrames=0;
    /** ACK and NAK frames with nFlag 0 transmitted */
    // txN0Frames=0,
    /** ACK and NAK frames with nFlag 1 transmitted */
    public int txN1Frames=0;
    /** frames cancelled (with ASH_CAN byte) */
    public int txCancelled=0;

    /** DATA frame data fields bytes received */
    public int rxData=0;
    /** frames of all types received */
    public int rxAllFrames=0;
    /** DATA frames received */
    public int rxDataFrames=0;
    /** ACK frames received */
    public int rxAckFrames=0;
    /** NAK frames received */
    public int rxNakFrames=0;
    /** retransmitted DATA frames received */
    public int rxReDataFrames=0;
    /** ACK and NAK frames with nFlag 0 received */
    // rxN0Frames=0,
    /** ACK and NAK frames with nFlag 1 received */
    public int  rxN1Frames=0;
    /** frames cancelled (with ASH_CAN byte) */
    public int rxCancelled=0;

    /** frames with CRC errors */
    public int rxCrcErrors=0;
    /** frames with comm errors (with ASH_SUB byte) */
    public int  rxCommErrors=0;
    /** frames shorter than minimum */
    public int rxTooShort=0;
    /** frames longer than maximum */
    public int  rxTooLong=0;
    /** frames with illegal control byte */
    public int rxBadControl=0;
    /** frames with illegal length for type of frame */
    public int  rxBadLength=0;
    /** frames with bad ACK numbers */
    public int     rxBadAckNumber=0;
    /** DATA frames discarded due to lack of buffers */
    public int   rxNoBuffer=0;
    /** duplicate retransmitted DATA frames */
    public int    rxDuplicates=0;
    /** DATA frames received out of sequence */
    public int   rxOutOfSequence=0;
    /** received ACK timeouts */
    public int    rxAckTimeouts=0;
    
    public List<Integer> readCouters()
    {
    	return Arrays.asList(this.txData,
                this.txAllFrames,
                this.txDataFrames,
                this.txAckFrames,
                this.txNakFrames,
                this.txReDataFrames,
                this.txN1Frames,
                this.txCancelled,

                this.rxData,
                this.rxAllFrames,
                this.rxDataFrames,
                this.rxAckFrames,
                this.rxNakFrames,
                this.rxReDataFrames,
                this.rxN1Frames,
                this.rxCancelled,

                this.rxCrcErrors,
                this.rxCommErrors,
                this.rxTooShort,
                this.rxTooLong,
                this.rxBadControl,
                this.rxBadLength,
                this.rxBadAckNumber,
                this.rxNoBuffer,
                this.rxDuplicates,
                this.rxOutOfSequence,
                this.rxAckTimeouts) ;
    }
}
