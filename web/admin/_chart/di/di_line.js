
oc.di.DILine=function()
{//this.x this.y is start pt - inner x1=0 y1=0
	this.color='#text';
	this.lnW=5;
	
	this.cpx1 = 20;
	this.cpy1 = 100;
	this.x2 = 200 ;
	this.y2=20;
	this.cpx2 = 200;
	this.cpy2 = 100;
	oc.DrawItem.apply(this,arguments);
}

oc.di.DILine.prototype = new oc.DrawItem();

oc.di.DILine_PNS= {
		_cat_name:"line",_cat_title:"Line",
		color:{title:"color",type:"str"},
		lnW:{title:"text",type:"int"},
		cpx1:{title:"cpx1",type:"float"},
		cpy1:{title:"cpy1",type:"float"},
		x2:{title:"x2",type:"float"},
		y2:{title:"y2",type:"float"},
		cpx2:{title:"cpx2",type:"float"},
		cpy2:{title:"cpy2",type:"float"},
} ;


oc.di.DILine.prototype.getClassName=function()
{
	return "DILine" ;
}

oc.di.DILine.prototype.getPropDefs=function()
{
	var r = oc.DrawItem.prototype.getPropDefs.apply(this);
	r.push(oc.di.DILine_PNS)
	return r;
}

oc.di.DILine.prototype.getBoundPolygonDraw=function()
{
	
	//return new oc.base.Polygon();
	return null;
}


oc.di.DILine.prototype.containDrawPt=function(x,y)
{//override
	var panel = this.getPanel();
	var p = panel.transDrawPt2PixelPt(x,y) ;
	
	var pt = this.getDrawXY() ;
	var p1 = panel.transDrawPt2PixelPt(pt.x,pt.y) ;
	var cp1 = panel.transDrawPt2PixelPt(pt.x+this.cpx1, pt.y+this.cpy1);
	var cp2 = panel.transDrawPt2PixelPt(pt.x+this.cpx2, pt.y+this.cpy2);
	var p2 = panel.transDrawPt2PixelPt(pt.x+this.x2,pt.y+this.y2) ;
	
	var ctx = this.getCxt();
	ctx.save() ;
	//ctx.translate(0, 0);
	ctx.lineWidth = this.lnW;
	ctx.strokeStyle = "blue";
	
	ctx.beginPath();
    ctx.moveTo(p1.x, p1.y);
    ctx.bezierCurveTo(cp1.x, cp1.y, cp2.x, cp2.y,p2.x,p2.y);
    var r = ctx.isPointInPath(p.x,p.y);
    ctx.restore();
    return r ;
}


oc.di.DILine.prototype.draw=function(ctx)
{
	var panel = this.getPanel() ;
	if(!panel)
		return ;
	var pt = this.getDrawXY() ;
	var p1 = panel.transDrawPt2PixelPt(pt.x,pt.y) ;
	var cp1 = panel.transDrawPt2PixelPt(pt.x+this.cpx1, pt.y+this.cpy1);
	var cp2 = panel.transDrawPt2PixelPt(pt.x+this.cpx2, pt.y+this.cpy2);
	var p2 = panel.transDrawPt2PixelPt(pt.x+this.x2,pt.y+this.y2) ;
	ctx.save() ;
	//ctx.translate(0, 0);
	ctx.lineWidth = this.lnW;
	ctx.strokeStyle = "blue";
	
	ctx.beginPath();
    ctx.moveTo(p1.x, p1.y);
    ctx.bezierCurveTo(cp1.x, cp1.y, cp2.x, cp2.y,p2.x,p2.y);
    ctx.stroke();
    
    ctx.restore();
}

oc.di.DILine.prototype.draw_sel_or_not=function(ctx)
{//override and draw selected by itself
	var panel = this.getPanel() ;
	if(!panel)
		return ;
	var pt = this.getDrawXY() ;
	var p1 = panel.transDrawPt2PixelPt(pt.x,pt.y) ;
	var cp1 = panel.transDrawPt2PixelPt(pt.x+this.cpx1, pt.y+this.cpy1);
	var cp2 = panel.transDrawPt2PixelPt(pt.x+this.cpx2, pt.y+this.cpy2);
	var p2 = panel.transDrawPt2PixelPt(pt.x+this.x2,pt.y+this.y2) ;
	ctx.save() ;
	//ctx.translate(0, 0);
	ctx.lineWidth = this.lnW;
	ctx.strokeStyle = "red";
	
	ctx.beginPath();
    ctx.moveTo(p1.x, p1.y);
    ctx.bezierCurveTo(cp1.x, cp1.y, cp2.x, cp2.y,p2.x,p2.y);
    ctx.stroke();
    ctx.beginPath();
    ctx.lineWidth = 1;
    ctx.setLineDash([5, 2]);
    ctx.strokeStyle = "yellow";
    
    ctx.moveTo(p1.x, p1.y);
    ctx.lineTo(cp1.x,cp1.y);
    ctx.arc(cp1.x,cp1.y, 6, 0, Math.PI * 2, true);
    ctx.moveTo(p2.x, p2.y);
    ctx.lineTo(cp2.x,cp2.y);
    ctx.arc(cp2.x,cp2.y, 6, 0, Math.PI * 2, true);
    ctx.stroke();
    
    ctx.restore();
	return true;
}

