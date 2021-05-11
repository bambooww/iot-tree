
oc.di.DITxt=function()
{
	//this.w = 20;
	this.txt='#text';
	this.fontSize=30;
	this.fontColor = "#93d543";
	oc.DrawItem.apply(this,arguments);
}

oc.di.DITxt.prototype = new oc.DrawItem();

oc.di.DITxt_PNS= {
		_cat_name:"txt",_cat_title:"Text",
		//w:{title:"width",type:"int"},
		//h:{title:"height",type:"int"},
		txt:{title:"text",type:"str"},
		fontSize:{title:"Font Size",type:"int"},
		fontColor:{title:"Font Color",type:"str"}
} ;


oc.di.DITxt.prototype.getClassName=function()
{
	return "DITxt" ;
}

oc.di.DITxt.prototype.getPropDefs=function()
{
	var r = oc.DrawItem.prototype.getPropDefs.apply(this);
	r.push(oc.di.DITxt_PNS)
	return r;
}

oc.di.DITxt.prototype.getBoundRectDraw=function()
{
	var pt = this.getDrawXY() ;
	var cxt = this.getCxt();
	var fs = panel.transDrawLen2PixelLen(this.fontSize)+5;
	cxt.font = fs+"px serif";
	var tw = cxt.measureText(this.txt).width;
	return new oc.base.Rect(pt.x,pt.y,tw,fs);
}





oc.di.DITxt.prototype.draw=function(cxt)
{
	var panel = this.getPanel() ;
	if(!panel)
		return ;
	var pt = this.getDrawXY() ;
	var ppt = panel.transDrawPt2PixelPt(pt.x,pt.y) ;
	var fs = panel.transDrawLen2PixelLen(this.fontSize);
	cxt.save() ;
	//cxt.font = this.fontSize+"px serif";
	cxt.font = fs+"px serif";
	cxt.fillStyle=this.fontColor;
	cxt.fillText(this.txt,ppt.x,ppt.y+fs);
	cxt.restore();
}

