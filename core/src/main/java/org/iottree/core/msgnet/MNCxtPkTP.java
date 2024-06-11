package org.iottree.core.msgnet;

public enum MNCxtPkTP
{
	msg,node,flow,prj;
	
	public String getTitle()
	{
		return name();
	}
	
	public String getIconTitle()
	{
		switch(this)
		{
		case msg:
			return "<i class='fa fa-arrow-right'></i>"+name();
		case node:
			return "<i class='fa fa-square-o'></i>"+name();
		case flow:
			return "<i class='fa fa-fork fa-rotate-90'></i>"+name();
		case prj:
			return "<i class='fa fa-project-diagram'></i>"+name();
		default:
			return name() ;
		}
	}
	
	public IMNCxtPk getCxtPkInNode(MNNet net,MNBase mn_item,MNMsg m)
	{
		switch(this)
		{
		case msg:
			return m ;
		case node:
			return mn_item ;
		case flow:
			return net ;
		case prj:
			return net.getBelongTo().RT_getCxtPk();
		default:
			return null ;
		}
	}
	
//	public IMNCxtPk getCxtPkInFlow(MNNet net) //,MNMsg m)
//	{
//		switch(this)
//		{
//		case msg:
//			return null ;
//		case node:
//			return null ;
//		case flow:
//			return net;//mn_item.getBelongTo() ;
//		case prj:
//			return net.getBelongTo().RT_getCxtPk();
//		default:
//			return null ;
//		}
//	}
	
	public Object RT_getValInCxt(String val_str_or_subn,
			MNNet net,MNBase item,MNMsg msg)
	{
		IMNCxtPk cxtpk = this.getCxtPkInNode(net,item, msg) ;
		if(cxtpk==null)
			return null ;
		return cxtpk.CXT_PK_getSubVal(val_str_or_subn) ;
	}
	

	public boolean RT_setValInCxt(String val_str_or_subn,Object val,
			MNNet net,MNBase item,MNMsg msg,StringBuilder failedr)
	{
		IMNCxtPk cxtpk = this.getCxtPkInNode(net,item, msg) ;
		if(cxtpk==null)
		{
			failedr.append("no cxt pk found") ;
			return false ;
		}
		
		return cxtpk.CXT_PK_setSubVal(val_str_or_subn, val, failedr);
	}
	
}
