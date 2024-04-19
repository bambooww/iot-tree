package org.iottree.core.router;

public class JoinOut extends RouterJoin
{
//	/**
//	 * out is push or not
//	 */
//	boolean bPush;
//	
//	boolean bPull;
	
	
	
	public JoinOut(RouterNode node,String name) //,String title,String desc) //,boolean b_push,boolean b_pull)
	{
		super(node,name); //,title,desc);
		
//		if(!b_push && ! b_pull)
//			throw new IllegalArgumentException("push or pull cannot be all false") ;
//		this.bPull = b_pull ;
//		this.bPush = b_push ;
	}
	
	
	public String getFromId()
	{
		return this.belongNode.id+"-"+this.name ;
	}

//	public boolean canPush()
//	{
//		return bPush ;
//	}
//	
//	public boolean canPull()
//	{
//		return bPull ;
//	}
}
