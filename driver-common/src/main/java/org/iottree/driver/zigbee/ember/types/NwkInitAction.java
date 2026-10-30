package org.iottree.driver.zigbee.ember.types;

public enum NwkInitAction
{
	/** Ain't that nice! */
    DONE(0),
    /** Config mismatch, must leave network. */
    LEAVE(1),
    /** Config mismatched, left network. Will evaluate forming from backup or config next. */
    LEFT(2),
    /** Form the network using config. No backup, or backup mismatch. */
    FORM_CONFIG(3),
    /** Re-form the network using full backed-up data. */
    FORM_BACKUP (4);

	private final int val;
	// private final String title;

	NwkInitAction(int val) // , String title)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static NwkInitAction valOfInt(int v)
	{
		for (NwkInitAction st : NwkInitAction.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
