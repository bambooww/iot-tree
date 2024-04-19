package org.iottree.core.router;

public class JoinIn extends RouterJoin
{
	public JoinIn(RouterNode node,String name) //,String title,String desc)
	{
		super(node,name) ;// ,title,desc);
	}
	
	public String getToId()
	{
		return this.belongNode.id+"-"+this.name ;
	}
}
