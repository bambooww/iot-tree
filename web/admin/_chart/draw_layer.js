
oc.DrawLayer=function()
{
	this.drawPanel = null ;
	
	this.name=null ;
	
	this.items = [];

	//style="position: absolute; left: 0; top: 0; z-index: 0;"
	this.context = document.createElement('canvas').getContext('2d');
	
	this.canvas = this.context.canvas;
	this.can = $(this.canvas) ;
	this.can.css("position","absolute") ;
	this.can.css("left","0px") ;
	this.can.css("top","0px") ;
	this.can.css("z-index","0") ;
	this.can.css("width","100%") ;
	this.can.css("height","100%") ;
	
	this.bVis=true ;
	
	if(arguments.length>=1)
	{
		if(typeof(arguments[0])=='string')
			this.name = arguments[0];
		else if(typeof(arguments[0])=='object')
			this.inject(arguments[0]) ;
	}
		
}

oc.DrawLayer.prototype.getCanvasEle=function()
{
	return this.canvas ;
}

oc.DrawLayer.prototype.getCanvasCxt=function()
{
	return this.context ;
}

oc.DrawLayer.prototype.inject=function(opts)
{
	if(typeof(opts)=='string')
		eval("opts="+opts) ;
	
	//alert(JSON.stringify(opts));
	//alert(opts.name);
	if(opts.name)
		this.name=opts.name ;
	this.bVis=(opts.vis!=false);
	
	if(opts.dis)
	{
		for(var i = 0 ; i < opts.dis.length ; i ++)
		{
			var it = opts.dis[i] ;
			var cn = it._cn ;
			if(!cn)
				continue ;
			var item = null ;
			if(cn=="DrawItems")
				eval("item=new oc."+cn+"()") ;
			else
				eval("item=new oc.di."+cn+"()") ;
			//eval("item=new oc.di."+cn+"()") ;
			if(!item)
				continue ;
			item.inject(it) ;
			this.addItem(item);
		}
	}
}

oc.DrawLayer.prototype.extract=function()
{
	var r = {} ;
	r.name=this.name ;
	r.bvis=this.bVis;
	r.dis=[];
	for(var i = 0 ; i < this.items.length ; i ++)
	{
		r.dis.push(this.items[i].extract()) ;
	}
	return r ;
}

oc.DrawLayer.prototype.getItemById=function(id)
{
	for(var i = 0 ; i < this.items.length ; i ++)
	{
		if(this.items[i].id==id)
			return this.items[i] ;
	}
	return null ;
}

oc.DrawLayer.prototype.addItem=function(item)
{
	this.items.push(item) ;
	
	if(item.getClassName()=='DrawItems')
		item.setLayer(this);
	else
		item.drawLayer = this ;
}

oc.DrawLayer.prototype.vis=function(v)
{
	if(v==undefined)
		return this.bVis ;
	this.bVis=(v==true||v==1) ;
}

oc.DrawLayer.prototype.on_draw=function()
{
	var dxy = this.drawPanel.transDrawPt2PixelPt(0,0) ;
	var sz = this.drawPanel.getPixelSize();
	var w = sz[0];
	var h=sz[1] ;
	this.context.beginPath();              
	this.context.strokeStyle="#4c4f51";
	this.context.moveTo(0,dxy.y);
	this.context.lineTo(w,dxy.y);
	this.context.moveTo(dxy.x,0);
	this.context.lineTo(dxy.x,h);
	this.context.stroke();
	
	//this.context.save() ;
	
	//this.context.scale(1, -1);//chg
	for(var i = 0 ; i < this.items.length ; i ++)
	{
		this.items[i].draw(this.context);
	}
	
	//this.context.restore() ;
}

oc.DrawLayer.prototype.on_draw_sel=function(selitem)
{
	if(selitem==null)
		return ;
	if(selitem.draw_sel_or_not(this.context))
		return ;//
	var dr = selitem.getBoundRectPixel() ;
	//var dr  =selitem.getBoundRectDraw() ;
	if(dr!=null)
	{
		var d = 6;
		var dh=3 ;
		oc.util.drawRect(this.context, dr.x, dr.y, dr.w, dr.h, null,null,2,"red");
		oc.util.drawRect(this.context, dr.x-dh, dr.y-dh, d, d, null,null,2,"red");
		oc.util.drawRect(this.context, dr.x+dr.w-dh, dr.y-dh, d, d, null,null,2,"red");
		oc.util.drawRect(this.context, dr.x+dr.w-dh, dr.y+dr.h-dh, d, d, null,null,2,"red");
		oc.util.drawRect(this.context, dr.x-dh, dr.y+dr.h-dh, d, d, null,null,2,"red");
		return ;
	}
	var py = selitem.getBoundPolygonDraw() ;
	if(py!=null)
	{
		return ;
	}
}