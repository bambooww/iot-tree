package org.iottree.core.plugin.inner;

import java.io.File;
import java.util.HashMap;

/**
 * support
 * @author jason.zhu
 *
 */
public class Http
{
	void init_plug(File plugdir, HashMap<String, String> params) throws Exception
	{
		
	}
	
	public HttpURL JS_createUrl(String url)
	{
		HttpURL hu =  new HttpURL();
		hu.asURL(url) ;
		return hu ;
	}
}
