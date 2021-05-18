package org.iottree.core.basic;

public class ValTransScaling extends ValTranser
{
	public static final int SCALING_LINEAR = 1;

	public static final int SCALING_SQUARE_ROOT = 2;
	
	int tp = SCALING_LINEAR;

	double rawValLow = 0;
	double rowValHigh = 1000;
	
	@Override
	public String getName()
	{
		return "scaling";
	}
	@Override
	public String getTitle()
	{
		return "Scaling";
	}
	@Override
	public Number transVal(Number inval)
	{
		return null;
	}
	
	
	
}
