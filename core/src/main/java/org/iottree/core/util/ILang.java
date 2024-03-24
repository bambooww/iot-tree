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
	
	default public String g(String name,String propn)
	{
		Lan ln = Lan.getLangInPk(this.getClass()) ;
		if(ln==null)
			return "[x]"+name+"[x]" ;
		return ln.g(name,propn) ;
	}
	
	default public String g(String name,String propn,String def)
	{
		Lan ln = Lan.getLangInPk(this.getClass()) ;
		if(ln==null)
			return def ;
		return ln.g(name,propn,def) ;
	}
}
