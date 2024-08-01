package org.iottree.core.msgnet;

import java.util.*;

import org.iottree.core.basic.NameTitle;

/**
 * IMNContainer 接口实现的同时，如果也实现了此接口，则
 * 
 * @author jason.zhu
 *
 */
public interface IMNContTagListMapper
{
	/**
	 * 
	 * @return
	 */
	public List<NameTitle> getMNContTagListCatTitles() ;
	
	public List<NameTitle> getMNContTagList(String cat) ;
}
