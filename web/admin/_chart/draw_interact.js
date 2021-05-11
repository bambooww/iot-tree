
oc.interact.ALL_INTERACTS={};

oc.DrawOper=function(name,interact,layername)
{
	this.name=name ;
	
	this.belongTo = interact ;
	
	this.layerName=layername ;
	
	this.drawLayer=null ;//tmp
}

oc.DrawOper.prototype.getName=function()
{
	return this.name ;
}

oc.DrawOper.prototype.getInteract=function()
{
	return this.belongTo;
}


oc.DrawOper.prototype.getDrawPanel=function()
{
	return this.belongTo.drawPanel ;
}

oc.DrawOper.prototype.getDrawLayer=function()
{
	if(this.drawLayer!=null)
		return this.drawLayer ;
	if(this.drawPanel==null)
		return null ;
	if(this.layerName==undefined||this.layerName==null||this.layerName=='')
		return null ;
	this.drawLayer = this.getDrawPanel().getLayerByName(this.layerName) ;
	return this.drawLayer;
}


oc.DrawOper.prototype.on_mouse_down=function(pxy,dxy)
{
	
}

oc.DrawOper.prototype.on_mouse_mv=function(pxy,dxy)
{
	
}

oc.DrawOper.prototype.on_mouse_up=function(pxy,dxy)
{
	
}

oc.DrawOper.prototype.on_mouse_dbclk=function(pxy,dxy)
{
	
}

oc.DrawOper.prototype.on_mouse_clk=function(pxy,dxy)
{
	
}

oc.DrawOper.prototype.on_mouse_wheel=function(pxy,dxy,delta)
{
}

oc.DrawOper.prototype.on_draw=function(cxt)
{
	
}

//---------------- interact , a DrawOper and a Selector

oc.DrawInteract=function(opts)
{//selector
	if(!opts)
		opts={} ;
	
	this.drawPanel = null ;
	
	
	this.layerNames=[];
	if(opts.layerNames)
		this.layerNames.addAll(opts.layerNames);//interaction related layers
	if(opts.layerName&&opts.layerName!='')
		this.layerNames.push(opts.layerName) ;
	
	this.drawLayers=null;
	
	this.bdown=false;
	this.bdown_mv=0;
	
	
	this.selectedItems=[];
	this.selectedItemDrag = null;//current drag item
	
	this.selectedListeners=[] ;
}


//oc.DrawInteract.prototype = new oc.DrawOper() ;

oc.DrawInteract.prototype.setDrawPanel=function(p)
{
	this.drawPanel=p;
}

oc.DrawInteract.prototype.fireSelectedChged=function()
{
	var curitem=this.selectedItems.getLast() ;
	for(var i = 0 ; i < this.selectedListeners.length;i++)
	{
		 this.selectedListeners[i].onItemSelected(curitem) ;
	}
}

oc.DrawInteract.prototype.setSelectedListener=function(lis)
{
	this.selectedListeners.push(lis);
}


oc.DrawInteract.prototype.get_interact_opers=function()
{//get current opers
	return [];
}

oc.DrawInteract.prototype.get_interact_view_opers=function()
{//get current opers
	return [];
}

oc.DrawInteract.prototype.getLayers=function()
{
	if(this.drawLayers!=null)
		return this.drawLayers;
	if(this.drawPanel==null)
		return null;
	var lys = [] ;
	for(var i = 0 ; i < this.layerNames.length ; i ++)
	{
		var ly = this.drawPanel.getLayerByName(this.layerNames[i]) ;
		if(ly==null)
			continue ;
		lys.push(ly) ;
	}
	if(lys.length>=0)
		this.drawLayers=lys ;
	return lys ;
}

oc.DrawInteract.prototype.getSelectedItems=function()
{
	return this.selectedItems;
}

oc.DrawInteract.prototype.findCanSelectInLayers=function(dxy)
{
	var panel = this.drawPanel;
	if(panel==null)
		return ;
	var dls = this.getLayers() ;
	if(dls==null)
		return null ;
	
	var r=[] ;
	for(var k = 0 ; k < dls.length ; k ++)
	{
		var items = dls[k].items;
		if(items==null||items.length<=0)
			return ;
		for(var i = 0 ; i < items.length ; i ++)
		{
			if(items[i].containDrawPt(dxy.x,dxy.y))
			{
				r.push(items[i]);
			}
		}
	}
	if(r.length>0)
		return r ;
	else
		return null;
}

oc.DrawInteract.prototype.on_select=function(pxy,dxy)
{
	var can_selitems = this.findCanSelectInLayers(dxy) ;
	
	var curitem = this.selectedItems.getLast();
	
	if(can_selitems==null)
	{
		if(this.selectedItems.length>0)
		{
			this.selectedItems=[];//clear
			panel.update_draw() ;
			this.fireSelectedChged() ;
		}
	}
	else
	{
		this.selectedItems=can_selitems;//this.selectedItems.addAll(can_selitems) ;
		panel.update_draw() ;
		this.fireSelectedChged() ;
	}
}

oc.DrawInteract.prototype.on_draw=function(cxt)
{
	if(this.selectedItems.length<=0)
		return;
	var dls = this.getLayers() ;
	if(dls==null)
		return;
	for(var k = 0 ; k < dls.length ; k ++)
	{
		var dl = dls[k];
		for(var i = 0 ; i < this.selectedItems.length ; i ++)
		{
			var sitem = this.selectedItems[i] ;
			dl.on_draw_sel(sitem) ;
		}
	}
	
	//opers
	var opers = this.get_interact_view_opers();
	if(opers!=null&&opers.length>0)
	{
		for(var i = 0 ; i < opers.length ; i ++)
    	{
			opers[i].on_draw();
    	}
	}
}


oc.DrawInteract.prototype.on_mouse_down=function(pxy,dxy)
{
	this.bdown=true;
	this.bdown_mv=0;
	
	var canselitems = this.findCanSelectInLayers(dxy) ;
	if(canselitems!=null)
	{
		if(canselitems.getLast()==this.selectedItems.getLast())
		{
			this.selectedItemDrag = this.selectedItems.getLast();
		}
	}
	else
	{
		this.selectedItemDrag=null;
	}
}



oc.DrawInteract.prototype.on_mouse_mv=function(pxy,dxy)
{
	if(this.bdown)
	{
		this.bdown_mv++;
		//console.log("mouse mv--->");
	}
	
}

oc.DrawInteract.prototype.on_mouse_up=function(pxy,dxy)
{
	if(this.bdown && this.bdown_mv<2)
		this.on_select(pxy,dxy);
	this.bdown=false;
}

oc.DrawInteract.prototype.on_mouse_wheel=function(pxy,dxy,delta)
{
	
}

oc.DrawInteract.prototype.on_mouse_dbclk=function(pxy,dxy)
{
	
}

oc.DrawInteract.prototype.on_mouse_clk=function(pxy,dxy)
{
	
}


oc.DrawInteract.prototype.on_mouse_event=function(n,e)
{
	var pxy = this.drawPanel.getEventPixel(e) ;
	var dxy = this.drawPanel.transPixelPt2DrawPt(pxy.x,pxy.y) ;
	var opers = this.get_interact_opers();
	if("down"==n)
	{
		this.on_mouse_down(pxy,dxy) ;
		for(var i = 0 ; i < opers.length ; i ++)
			opers[i].on_mouse_down(pxy,dxy) ;
	}
	else if("mv"==n)
	{
		this.on_mouse_mv(pxy,dxy) ;
		for(var i = 0 ; i < opers.length ; i ++)
			opers[i].on_mouse_mv(pxy,dxy) ;
	}
	else if("up"==n)
	{
		this.on_mouse_up(pxy,dxy) ;
		for(var i = 0 ; i < opers.length ; i ++)
			opers[i].on_mouse_up(pxy,dxy) ;
	}
	else if("dbclk"==n)
	{
		this.on_mouse_dbclk(pxy,dxy) ;
		for(var i = 0 ; i < opers.length ; i ++)
			opers[i].on_mouse_dbclk(pxy,dxy) ;
	}
	else if("clk"==n)
	{
		this.on_mouse_clk(pxy,dxy) ;
		for(var i = 0 ; i < opers.length ; i ++)
			opers[i].on_mouse_clk(pxy,dxy) ;
	}
	else if("w"==n)
	{
		var delta = 0;
        if (!e) e = window.event;
        if (e.wheelDelta) {
            delta = e.wheelDelta/120; 
            if (window.opera) delta = -delta;
        } else if (e.detail) {//
            delta = -e.detail/3;
        }
       
        this.on_mouse_wheel(pxy,dxy,delta);
        for(var i = 0 ; i < opers.length ; i ++)
			opers[i].on_mouse_wheel(pxy,dxy,delta);
	}
}

