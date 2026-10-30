package org.iottree.driver.zigbee.ember.types;

/**
 * Duty cycle states
 *
 * Applications have no control over the state but the callback exposes
 * state changes to the application.
 */
public enum DutyCycleState
{
	/** No duty cycle tracking or metrics are taking place. */
    TRACKING_OFF(0),
    /** Duty Cycle is tracked and has not exceeded any thresholds. */
    LBT_NORMAL(1),
    /** The limited threshold of the total duty cycle allotment was exceeded. */
    LBT_LIMITED_THRESHOLD_REACHED(2),
    /** The critical threshold of the total duty cycle allotment was exceeded. */
    LBT_CRITICAL_THRESHOLD_REACHED(3),
    /** The suspend limit was reached and all outbound transmissions are blocked. */
    LBT_SUSPEND_LIMIT_REACHED(4);

	private final int val;

	DutyCycleState(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static DutyCycleState valOfInt(int v)
	{
		for (DutyCycleState st : DutyCycleState.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
