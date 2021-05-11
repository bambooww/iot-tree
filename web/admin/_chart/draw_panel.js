

oc.DrawPanel=function(target,opts)
{
	if(!opts)
		opts={} ;
	
	var on_mouse_mv=opts.on_mouse_mv?opts.on_mouse_mv:null;
	
	this.tarEle = document.getElementById(target) ;
	this.pixelSize = null ;

	//var context_ = document.createElement('CANVAS').getContext('2d');;
	//var canvas_ = context_.canvas;
	//canvas_.style.width = '100%';
	//canvas_.style.height = '100%';
	//$(canvas_).css("z-index","5");
	
	//this.tarEle.insertBefore(canvas_, this.tarEle.childNodes[0] || null);
	
	this.layers = (opts.layers?opts.layers:[]) ;
	if(this.layers==null)
		this.layers=[];
	
	this.interact=null;//[new oc.interact.OperDrag()];
	if(opts.interact)
		this.interact=opts.interact;
	//if(opts.interacts)
	//	this.interacts=
	
	
	var me = this ;
	
	
	this.init_me=function()
	{
		for(var i = 0 ; i < this.layers.length; i ++)
			this.layers[i].drawPanel = this ;
		//for(var i = 0 ; i < this.interacts.length; i ++)
		//	this.interacts[i].drawPanel = this ;
		if(this.interact)
			this.interact.drawPanel=this;
	}
	
	this.init_me();
	

	this.addLayer=function(lay)
	{
		this.layers.push(lay);
		lay.drawPanel = this ;
		var can = lay.getCanvasEle() ;
		this.tarEle.appendChild(can);//, this.tarEle.childNodes[0] || null);
		var cxt = lay.getCanvasCxt() ;
		var c = lay.getCanvasEle();
		c.setAttribute("width",this.pixelSize[0]) ;
		c.setAttribute("height",this.pixelSize[1]) ;
		cxt.fillStyle="rgba(0,0,0,0.0)";
		cxt.fillRect(0,0,this.pixelSize[0],this.pixelSize[1]);
		
		this.update_draw();
	}

	this.getLayerByName=function(n)
	{
		for(var i = 0 ; i < this.layers.length ; i ++)
		{
			if(this.layers[i].name==n)
				return this.layers[i] ;
		}
		return null ;
	}
	

    this.setPixelSize=function(sz)
    {
    	this.pixelSize = sz ;
    	for(var i = 0 ; i < this.layers.length ; i ++)
		{
			var c = this.layers[i].getCanvasEle();
			c.setAttribute("width",sz[0]) ;
			c.setAttribute("height",sz[1]) ;
		}
    	//canvas_.setAttribute("width",sz[0]) ;
    	//canvas_.setAttribute("height",sz[1]) ;
    	//context_.fillStyle="rgba(0,0,0,0.5)";
    	//context_.fillRect(0,0,this.size[0],this.size[1]);
    	//console.log(sz);
    	this.update_draw();
    }

    this.getPixelSize=function()
    {
    	return this.pixelSize ;
    }
    
    this.getPixelHeight=function()
    {
    	return this.pixelSize[1]/2;
    }
    
    this.getPixelCenter=function()
    {
    	return {x:this.pixelSize[0]/2,y:this.pixelSize[1]/2} ;
    }
    
    this.updatePixelSize = function()
    {
	    var computedStyle = getComputedStyle(this.tarEle);
	    this.setPixelSize([
	    	this.tarEle.offsetWidth -
	          parseFloat(computedStyle['borderLeftWidth']) -
	          parseFloat(computedStyle['paddingLeft']) -
	          parseFloat(computedStyle['paddingRight']) -
	          parseFloat(computedStyle['borderRightWidth']),
	          this.tarEle.offsetHeight -
	          parseFloat(computedStyle['borderTopWidth']) -
	          parseFloat(computedStyle['paddingTop']) -
	          parseFloat(computedStyle['paddingBottom']) -
	          parseFloat(computedStyle['borderBottomWidth'])
	    ]);
    }

    this.on_draw=function()
    {
    	for(var i = 0 ; i < this.layers.length ; i ++)
    	{
    		if(!this.layers[i].vis())
    			continue;
    		this.layers[i].on_draw() ;
    	}
    	
    	if(this.interact!=null)
    	{
    		this.interact.on_draw();
    	}
    	
    }
    
    this.clear_draw=function()
    {
    	for(var i = 0 ; i < this.layers.length ; i ++)
		{
			var cxt = this.layers[i].getCanvasCxt() ;
			//cxt.fillStyle="rgba(0,0,0,0.0)";
			cxt.clearRect(0,0,this.pixelSize[0],this.pixelSize[1]);
		}
    }
    
    this.update_draw=function()
    {
    	this.clear_draw();
    	this.on_draw() ;
    }
    
    this.getEventPixel = function(e)
    {
    	  var r = this.tarEle.getBoundingClientRect();
    	  return {x:e.clientX - r.left,y:e.clientY - r.top};
    }
    
    this.drawCenter={x:0.0,y:0.0};
    this.drawResolution=1.0;//分辨率  1/放大倍数
    
    this.getDrawCenter=function()
    {
    	return this.drawCenter ;
    }
    
    this.getDrawResolution=function()
    {
    	return this.drawResolution;
    }
    
    this.setDrawCenter=function(x,y)
    {
    	this.drawCenter.x = x ;
    	this.drawCenter.y = y ;
    }
    
    this.moveDrawCenter=function(deta_x,deta_y)
    {
    	this.drawCenter.x += deta_x ;
    	this.drawCenter.y += deta_y ;
    	
    	this.update_draw();
    }
    
    this.ajustDrawResolution=function(dx,dy,delta)
    {
    	var pix_pt = this.transDrawPt2PixelPt(dx,dy) ;
    	if(delta>0)
    	{
    		this.drawResolution *=2;
    	}
    	else
    	{
    		this.drawResolution /= 2;
    	}
    	var newdpt = this.transPixelPt2DrawPt(pix_pt.x,pix_pt.y) ;
    	this.drawCenter.x += (dx-newdpt.x) ;
    	this.drawCenter.y += (dy-newdpt.y) ;
    	this.update_draw();
    }
    
    this.transPixelPt2DrawPt=function(px,py)
    {//pixel pt to draw pt
    	var pc = this.getPixelCenter();
    	var dx = (px-pc.x)*this.drawResolution+this.drawCenter.x;
    	//var dy = (py-pc.y)*this.drawResolution+this.drawCenter.y;
    	var dy = this.drawCenter.y-(pc.y-py)*this.drawResolution ;
    	//var dy=(this.getPixelHeight()/2-py)*this.drawResolution+this.drawCenter.y;
    	return {x:dx,y:dy};
    }
    
    this.transDrawPt2PixelPt=function(dx,dy)
    {
    	var pc = this.getPixelCenter();
    	var px = (dx-this.drawCenter.x)/this.drawResolution+pc.x;
    	//var py = (dx-this.drawCenter.y)/this.drawResolution+pc.y;
    	var py =pc.y- (this.drawCenter.y-dy)/this.drawResolution ;
    	//var py=this.getPixelHeight()/2-(dy-this.drawCenter.y)/this.drawResolution;
    	px = parseInt(px);
    	py = parseInt(py);
    	return {x:px,y:py};
    }
    
    this.transDrawLen2PixelLen=function(len)
    {
    	var r = Math.round(len/this.drawResolution);
    	return parseInt(r);
    }
	
    this.updatePixelSize();
    
    
//    this.addInteract=function(inta)
//    {
//    	inta.drawPanel=this ;
//    	this.interacts.push(inta) ;
//    }
    
    this.setInteract=function(inta)
    {
    	inta.setDrawPanel(this) ;
    	this.interact = inta;//s.push(inta) ;
    }
    
//    this.addOper=function(inta)
//    {
//    	inta.drawPanel=this ;
//    	this.interacts.push(inta) ;
//    }

    me.tarEle.onmousedown=function(e)
    {
    	if(me.interact==null)
    		return ;
    	//for(var i = 0 ; i < me.interacts.length; i ++)
		me.interact.on_mouse_event("down",e);
    }
	
    me.tarEle.onmousemove=function (e)
	{
    	
		var p = me.getEventPixel(e);//windowToCanvas(canvas_,e.x,e.y) ;
		var d = me.transPixelPt2DrawPt(p.x,p.y) ;
		p = me.transDrawPt2PixelPt(d.x,d.y);
		//console.log("["+e.x+","+e.y+"]-("+p.x+","+p.y+")") ;
		if(on_mouse_mv!=null)
			on_mouse_mv(p,d);
		if(me.interact==null)
    		return ;
		me.interact.on_mouse_event("mv",e);
		//for(var i = 0 ; i < me.interacts.length; i ++)
		//	me.interacts[i].on_mouse_event("mv",e);
    };
	
    me.tarEle.onmouseup=function(e)
    {
    	if(me.interact==null)
    		return ;
    	me.interact.on_mouse_event("up",e);
    	//for(var i = 0 ; i < me.interacts.length; i ++)
		//	me.interacts[i].on_mouse_event("up",e);
    }
    
    me.tarEle.onclick=function(e)
    {
    	if(me.interact==null)
    		return ;
    	//for(var i = 0 ; i < me.interacts.length; i ++)
			me.interact.on_mouse_event("clk",e);
    }
    
    
    me.tarEle.ondblclick=function(e)
    {
    	if(me.interact==null)
    		return ;
    	//for(var i = 0 ; i < me.interacts.length; i ++)
			me.interact.on_mouse_event("dbclk",e);
    }
    
    
    me.tarEle.onmousewheel=function(e)
    {
    	if(me.interact==null)
    		return ;
    	//for(var i = 0 ; i < me.interacts.length; i ++)
			me.interact.on_mouse_event("w",e);
    }
    
    if(window.navigator.userAgent.toLowerCase().indexOf("firefox") != -1)
    {
    	if(me.interact==null)
    		return ;
		window.addEventListener("DOMMouseScroll",function(e){
			//for(var i = 0 ; i < me.interacts.length; i ++)
				me.interact.on_mouse_event("w",e);
		},false);
    }
}






