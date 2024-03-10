package org.iottree.core.util;

public interface ILang
{
	default public String g(String name)
	{
		Lan ln = Lan.getLangInPk(this.getClass()) ;
		if(ln==null)
			return "[x]"+name+"[x]" ;
		return ln.g(name) ;
	}
}
