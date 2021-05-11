
oc.interact.OperSel=function()
{//selector
	oc.DrawOper.apply(this,arguments);
	
	this.selectedItems=[];
	
	this.bdown=false;
	this.bdown_mv=false;
}


oc.interact.OperSel.prototype = new oc.DrawOper() ;

oc.interact.OperSel.prototype.on_select=function(pxy,dxy)
{
	var panel = this.getDrawPanel() ;
	if(panel==null)
		return ;
	var dl = this.getDrawLayer() ;
	if(dl==null)
		return null ;
	
	var items = dl.items;
	if(items==null||items.length<=0)
		return ;
	
	var curitem = this.selectedItems.getLast();
	
	var bsel=false;
	for(var i = 0 ; i < items.length ; i ++)
	{
		if(items[i].containDrawPt(dxy.x,dxy.y))
		{
			this.selectedItems.push(items[i]);
			bsel=true;
		}
	}
	
	if(!bsel)
	{
		if(this.selectedItems.length>0)
		{
			this.selectedItems=[];//clear
			panel.update_draw() ;
		}
	}
	else
	{
		panel.update_draw() ;
	}
}

oc.interact.OperSel.prototype.on_draw=function(cxt)
{
	if(this.selectedItems.length<=0)
		return;
	var dl = this.getDrawLayer() ;
	if(dl==null)
		return;
	for(var i = 0 ; i < this.selectedItems.length ; i ++)
	{
		var sitem = this.selectedItems[i] ;
		dl.on_draw_sel(sitem) ;
	}
}

oc.interact.OperSel.prototype.on_mouse_down=function(pxy,dxy)
{
	this.bdown=true;
	this.bdown_mv=false;
}



oc.interact.OperSel.prototype.on_mouse_mv=function(pxy,dxy)
{
	this.bdown_mv=true;
}

oc.interact.OperSel.prototype.on_mouse_up=function(pxy,dxy)
{
	if(this.bdown && !this.bdown_mv)
		this.on_select(pxy,dxy);
	this.bdown=false;
}

oc.interact.OperSel.prototype.on_mouse_wheel=function(pxy,dxy,delta)
{
	
}
