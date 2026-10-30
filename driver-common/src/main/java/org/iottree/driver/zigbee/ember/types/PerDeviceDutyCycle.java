package org.iottree.driver.zigbee.ember.types;

/** A structure containing, per device, overall duty cycle consumed (up to the suspend limit). */
public class PerDeviceDutyCycle
{
	/** Node ID of the device whose duty cycle is reported. uint16*/
    public int nodeId;
    
    /** The amount of overall duty cycle consumed (up to suspend limit). */
    /**
     * The percent of duty cycle for a limit.
     *
     * Duty cycle, limits, and thresholds are reported in units of
     * percent * 100 (i.e., 10000 = 100.00%, 1 = 0.01%).
     * uint16_t EmberDutyCycleHectoPct
     */
    public int dutyCycleConsumed ;
}
