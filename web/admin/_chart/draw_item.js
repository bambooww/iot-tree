

oc.DrawItem=function()
{
	this.drawLayer = null ;
	
	this.parentItem=null;//
	
	this.id = null ;
	this.name=null;

	this.x = 0;//opts?opts.x:0 ; related or abstract
	this.y = 0;//opts?opts.y:0 ;
	this.zindex=0;
	this.b_vis=true;
	
	if(arguments.length>=1)
	{
		this.inject(arguments[0]);
	}
	
}

oc.DrawItem_PNS = {
		_cat_name:"basic",_cat_title:"Basic",
		id:{title:"Id",type:"str",readonly:true},
		name:{title:"Name",type:"str"},
		x:{title:"X",type:"int",readonly:true},
		y:{title:"Y",type:"int",readonly:true},
		zindex:{title:"z-index",type:"int"},
		b_vis:{title:"visiable",type:"int",enum_val:[[0,"hidden"],[1,"show"]],bind:true},
} ;

oc.DrawItem.prototype.getClassName=function()
{
	return "DrawItem" ;
}

oc.DrawItem.prototype.getPropDefs=function()
{
	var r=[];
	r.push(oc.DrawItem_PNS);
	return r;
}

oc.DrawItem.prototype.inject=function(opts)
{
	if(opts==null)
		opts={} ;
	
	var pdefs = this.getPropDefs();
	for(var i = 0 ; i < pdefs.length ; i ++)
	{
		var pdef = pdefs[i] ;
		for(var n in pdef)
		{
			if(n.indexOf("_")==0)
				continue;
			var v = opts[n] ;
			if(v==undefined||v==null)
				continue;
			this[n] = v ;
		}
	}
	
	
	if(this.id==null||this.id==''||this.id==undefined||this.id=='undefined')
		this.id = oc.create_new_tmp_id();
	
	//this.id = opts._id ;
	//this.name=opts.name?opts.name:"";
	//this.x = opts.x?opts.x:0;
	//this.y = opts.y?opts.y:0;
}

oc.DrawItem.prototype.extract=function()
{
	var r={} ;
	r._cn=this.getClassName() ;
	
	var pdefs = this.getPropDefs();
	for(var i = 0 ; i < pdefs.length ; i ++)
	{
		var pdef = pdefs[i] ;
		for(var n in pdef)
		{
			if(n.indexOf("_")==0)
				continue;
			var v = this[n] ;
			if(v==undefined||v==null)
				continue;
			r[n] = this[n] ;
		}
	}
//	r.id=this.id ;
//	r.name=this.name ;
//	r.x =this.x ;
//	r.y = this.y ;
	return r ;
}

oc.DrawItem.prototype.getBoundRectDraw=function()
{//override
	//alert("getBoundRectDraw cn="+this.getClassName()) ;
	return null;//new oc.base.Rect(this.x,this.y,this.w,)
}


oc.DrawItem.prototype.getBoundRectPixel=function()
{//final
	var dbr = this.getBoundRectDraw();
	if(dbr==null)
		return null;
	var panel = this.getPanel() ;
	if(!panel)
		return null;
	var dxy = panel.transDrawPt2PixelPt(dbr.x,dbr.y) ;
	var dw = panel.transDrawLen2PixelLen(dbr.w);
	var dh = panel.transDrawLen2PixelLen(dbr.h);
	return new oc.base.Rect(dxy.x,dxy.y,dw,dh) ;
}


oc.DrawItem.prototype.getBoundPolygonDraw=function()
{//override
	return null;
}

oc.DrawItem.prototype.getBoundPolygonPixel=function()
{//override
	var dbr = this.getBoundPolygonDraw();
	if(dbr==null)
		return null;
	var panel = this.getPanel() ;
	if(!panel)
		return null;
	var dxy = panel.transDrawPt2PixelPt(dbr.x,dbr.y) ;
	var dw = panel.transDrawLen2PixelLen(dbr.w);
	var dh = panel.transDrawLen2PixelLen(dbr.h);
	return new oc.base.Rect(dxy.x,dxy.y,dw,dh) ;
}

//oc.DrawItem.prototype.


oc.DrawItem.prototype.containDrawPt=function(x,y)
{
	var dr = this.getBoundRectDraw();
	if(dr!=null)
		return dr.contains(x,y) ;
	var py = this.getBoundPolygonDraw() ;
	if(py!=null)
		return py.contains(x,y) ;
	return false;
}


oc.DrawItem.prototype.getLayer=function()
{
	return this.drawLayer ;
}

oc.DrawItem.prototype.getPanel=function()
{
	return this.drawLayer.drawPanel;
}

oc.DrawItem.prototype.getCxt=function()
{
	if(this.drawLayer==null)
		return null;
	return this.drawLayer.getCanvasCxt();
}


oc.DrawItem.prototype.getId=function()
{
	return this.id ;
}
oc.DrawItem.prototype.setId=function(v)
{
	this.id = v ;
}

oc.DrawItem.prototype.getDrawXY=function()
{//get absoluate pos of this item
	if(this.parentItem==null)
	{
		return {x:this.x,y:this.y} ;
	}
	else
	{
		var ppt = this.parentItem.getDrawXY();
		return {x:ppt.x+this.x,y:ppt.y+this.y};
	}
}


oc.DrawItem.prototype.draw=function(cxt)
{
	
}

oc.DrawItem.prototype.draw_sel_or_not=function(cxt)
{//override to return ture,and draw itself
	return false;
}

oc.DrawItem.prototype.getExtStr=function()
{
	if(this.extPNS==null)
		return "" ;
	var tmps = '' ;
	for(var n in this.extPNS)
	{
		if(n==null||n=='')
			continue;
		tmps += n+'='+this.extPNS[n]+'|' ;
	}
	return tmps;
}
oc.DrawItem.prototype.setExtStr=function(es)
{
	this.extPNS = transStr2PNS(es);
}

oc.DrawItem.prototype.getExtStrPNS=function()
{
	return this.extPNS ;
}



oc.DrawItem.prototype.toStr=function()
{
	return "id="+this.id+",x="+this.x+",y="+this.y ;
}

//------------------------------------------
// items collection
oc.DrawItems=function()
{
	this.items=[];//sub items
	oc.DrawItem.apply(this,arguments);
}

oc.DrawItems.prototype = new oc.DrawItem() ;

oc.DrawItems.prototype.getClassName=function()
{
	return "DrawItems" ;
}

oc.DrawItems.prototype.recurseSetLayer=function(disob,lay)
{//
	disob.drawLayer=lay;
	for(var i = 0 ; i < disob.items.length ; i ++)
	{
		var it = disob.items[i] ;
		it.drawLayer= lay ;
		if(it.getClassName()=='DrawItems')
		{
			this.recurseSetLayer(it,lay) ;
		}
	}
}

oc.DrawItems.prototype.setLayer=function(lay)
{
	this.recurseSetLayer(this,lay);
}

oc.DrawItems.prototype.inject=function(opts)
{
	oc.DrawItem.prototype.inject.apply(this,arguments) ;

	//if(typeof(opts)=='string')
	//	eval("opts="+opts) ;
	
	if(opts.items)
	{
		for(var i = 0 ; i < opts.items.length ; i ++)
		{
			var it = opts.items[i] ;
			var cn = it._cn ;
			if(!cn)
				continue ;
			var item = null ;
			if(cn=="DrawItems")
				eval("item=new oc."+cn+"()") ;
			else
				eval("item=new oc.di."+cn+"()") ;
			if(!item)
				continue ;
			item.inject(it) ;
			//out set layer
			item.parentItem=this;
			this.items.push(item) ;
		}
	}
}

oc.DrawItems.prototype.extract=function()
{
	var r = oc.DrawItem.prototype.extract.apply(this) ;
	r.items=[];
	for(var i = 0 ; i < this.items.length ; i ++)
	{
		r.items.push(this.items[i].extract()) ;
	}
	return r ;
}

oc.DrawItems.prototype.addItem=function(item)
{
	this.items.push(item) ;
	if(item.getClassName()=='DrawItems')
		item.setLayer(this);
	else
		item.drawLayer = this ;
}

oc.DrawItems.prototype.draw=function(cxt)
{
	if(this.items.length<=0)
		return ;
	var cxt = this.getLayer().getCanvasCxt();
	for(var i = 0 ; i < this.items.length ; i ++)
	{
		this.items[i].draw(cxt);
	}
}



oc.DrawItems.prototype.getBoundRectDraw=function()
{//override
	if(this.items.length<=0)
		return ;
	var r = null ;
	for(var i = 0 ; i < this.items.length ; i ++)
	{
		var tmpr = this.items[i].getBoundRectDraw();
		if(tmpr==null)
			continue ;
		if(r==null)
			r = new oc.base.Rect(tmpr) ;
		else
			r.expandBy(tmpr);
	}
	return r;
}
