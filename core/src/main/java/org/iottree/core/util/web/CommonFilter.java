package org.iottree.core.util.web;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.iottree.core.util.Convert;

public abstract class CommonFilter implements Filter
{
	@Override
	public void init(FilterConfig config) throws ServletException
	{
		initComp(config);
	}
	
	private void initComp(FilterConfig config)
	{
		ClassLoader cl = Thread.currentThread().getContextClassLoader();

		String realpath = config.getServletContext().getRealPath("/");
		if (Convert.isNullOrEmpty(realpath))
			return;

		String comp_rootname = realpath = realpath.replace('\\', '/');
		if (realpath.endsWith("/"))
			comp_rootname = comp_rootname.substring(0, realpath.length() - 1);

		int p = comp_rootname.lastIndexOf('/');
		comp_rootname = comp_rootname.substring(p + 1);

		System.out.println(">>>find comp["+comp_rootname+"] - "+realpath);
		
		AppWebConfig.registerModuleWebConfig(comp_rootname, cl) ;
	}

}
