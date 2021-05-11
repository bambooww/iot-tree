
oc.di.DIRect=function()
{
	this.w = 100;
	this.h = 100 ;
	
	this.border=0;
	this.borderColor=null;
	this.fillColor=null;
	this.radius=null ;
	
	oc.di.DICommon.apply(this,arguments);
	//oc.DrawItem.apply(this,arguments);
}

oc.di.DIRect.prototype = new oc.di.DICommon();
//oc.di.DIRect.prototype = new oc.DrawItem();

oc.di.DIRect_PNS= {
		_cat_name:"rect",_cat_title:"Rectangle",
		w:{title:"width",type:"int"},
		h:{title:"height",type:"int"},
		border:{title:"border",type:"str"},
		borderColor:{title:"borderColor",type:"str"},
		fillColor:{title:"fillColor",type:"str",val_tp:"color"},
		radius:{title:"radius",type:"int"}
} ;

oc.di.DIRect_EVTS = {
		SW_TRIGGER:{title:"开关触发",type:"str"}
	} ;


oc.di.DIRect.prototype.getClassName=function()
{
	return "DIRect" ;
}

oc.di.DIRect.prototype.getPropDefs=function()
{
	var r = oc.di.DICommon.prototype.getPropDefs.apply(this);
	r.push(oc.di.DIRect_PNS)
	return r;
}

oc.di.DIRect.prototype.getBoundRectDraw=function()
{
	var pt = this.getDrawXY() ;
	return new oc.base.Rect(pt.x,pt.y,this.w,this.h);
}





oc.di.DIRect.prototype.draw=function(cxt)
{
	var panel = this.getPanel() ;
	if(!panel)
		return ;
	var pt = this.getDrawXY() ;
	var dxy = panel.transDrawPt2PixelPt(pt.x,pt.y) ;
	var dw = panel.transDrawLen2PixelLen(this.w);
	var dh = panel.transDrawLen2PixelLen(this.h);
	
	var dr = null;
	if(this.radius!=null&&this.radius!=undefined)
		dr = panel.transDrawLen2PixelLen(this.radius);
	var lw = null;
	if(this.border!=null&&this.border!=undefined)
		lw = panel.transDrawLen2PixelLen(this.border);
	
//	dxy.x+=lw;
//	dxy.y+=lw;
//	dw-= lw;
//	dh-=lw;
//	dr+=lw/2;
	
//	var fillc = "rgba(222,123,0,0.3)";
//	var borderc="#dd4f43"
	//cxt.fillStyle=;
	//this.drawFillRect(cxt,this.x,this.y,this.w,this.h);
	//this.strokeRoundRect(cxt, dxy.x, dxy.y, dw, dh, lw,"#e82f3a");
	
	oc.util.drawRect(cxt, dxy.x, dxy.y, dw, dh, dr,this.fillColor,lw,this.borderColor);
	//cxt.strokeRect( dxy.x, dxy.y, dw, dh);
	
}

