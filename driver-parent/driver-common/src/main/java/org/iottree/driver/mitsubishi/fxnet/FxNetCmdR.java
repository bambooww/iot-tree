package org.iottree.driver.mitsubishi.fxnet;

import java.io.InputStream;
import java.io.OutputStream;

public class FxNetCmdR extends FxNetCmd
{

	@Override
	public boolean doCmd(InputStream inputs, OutputStream outputs) throws Exception
	{
		return false;
	}

}
