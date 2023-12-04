package org.iottree.core.cxt;

public interface IJsProp
{
	public JsProp toJsProp() ; 
	
	/**
	 * 当此对象被用作上下文根部时显示树形文档时，在输出内部一些JsProp可能为空，则需要
	 * 调用此函数，使得内部的一些字成员输出存在。保证上下文树内容的输出
	 */
	public void constructSubForCxtHelper() ;
}
