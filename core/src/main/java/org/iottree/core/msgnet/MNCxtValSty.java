package org.iottree.core.msgnet;

import org.iottree.core.util.Convert;

public enum MNCxtValSty
{
	msg,node,flow,prj,
	vt_int,
	vt_float,
	vt_bool,
	vt_str,
	vt_date,
	timestamp,
	vt_jo,
	vt_jarr;
	
	
	
	public String getTitle()
	{
		String n = this.name() ;
		if(isCxtPk())
			return "$"+n;
		if(n.startsWith("vt_"))
		{
			MNCxtValTP vtp = MNCxtValTP.parseFrom(n) ;
			return vtp.getTitle() ;
		}
		return n;
	}
	
	public boolean isConstant()
	{
		return name().startsWith("vt_") ;
	}
	
	public MNCxtPkTP getCxtPkTP()
	{
		if(!isCxtPk())
			return null ;
		String n = this.name() ;
		return MNCxtPkTP.valueOf(n);
	}
	
	public boolean isCxtPk()
	{
		return this==msg || this==node || this==flow || this==prj ;
	}
	
	public MNCxtValTP getConstantValTP()
	{
		String n = this.name() ;
		if(!n.startsWith("vt_"))
			return null ;
		return MNCxtValTP.parseFrom(n) ;
	}
	
	public Object RT_getValInCxt(String val_str_or_subn,
			MNNet net,MNBase item,MNMsg msg)
	{
		MNCxtValTP vtp = this.getConstantValTP();
		if(vtp!=null)
			return vtp.transStrToObj(val_str_or_subn) ;
		MNCxtPkTP pktp = this.getCxtPkTP() ;
		if(pktp!=null)
			return pktp.RT_getValInCxt(val_str_or_subn, net, item, msg) ;

		if(this==timestamp)
			return System.currentTimeMillis() ;
		return null;
	}
	
	
	
	public boolean RT_setValInCxt(String val_str_or_subn,Object val,
			MNNet net,MNBase item,MNMsg msg,StringBuilder failedr)
	{
		MNCxtPkTP pktp = this.getCxtPkTP() ;
		if(pktp==null)
		{
			failedr.append("no CxtPkTP found in ValSty") ;
			return false;
		}
		return pktp.RT_setValInCxt(val_str_or_subn,val,
				net,item,msg,failedr);
	}
	
	public static MNCxtValSty[] FOR_COMPARE_LIST = new  MNCxtValSty[] {
			msg,node,flow,prj,
			vt_int,
			vt_float,
			vt_str,
			vt_date};
	
	
}
