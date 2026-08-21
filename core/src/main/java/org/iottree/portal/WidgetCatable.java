package org.iottree.portal;

import java.util.LinkedHashMap;

public interface WidgetCatable
{
	public String getWidgetCatPrefix() ;
	
	public String getWidgetCatTPUID() ;
	
	public String getWidgetCatInsUID() ;
	
	public LinkedHashMap<String,Widget> getWidgets();
}
