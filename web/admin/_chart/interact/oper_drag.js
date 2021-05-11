
oc.interact.OperDrag=function(opts)
{
	oc.DrawOper.apply(this,arguments);
	
	this.downPt = null ;
	
	this.itemDrag = null ;
	
	this.itemDragPt=null;
}


oc.interact.OperDrag.prototype = new oc.DrawOper() ;




oc.interact.OperDrag.prototype.on_mouse_down=function(pxy,dxy)
{
	this.downPt = dxy ;
	//this.getInteract().
	this.itemDrag = this.getInteract().selectedItemDrag;
	if(this.itemDrag!=null)
		this.itemDragPt={x:this.itemDrag.x,y:this.itemDrag.y} ;
}

oc.interact.OperDrag.prototype.on_mouse_mv=function(pxy,dxy)
{
	if(this.downPt==null)
		return ;
	if(this.itemDrag==null)
	{//draw panel
		//console.log("delta_y="+(this.downPt.y-dxy.y));
		this.getDrawPanel().moveDrawCenter(this.downPt.x-dxy.x,this.downPt.y-dxy.y) ;
	}
	else
	{
		//console.log(this.itemDrag.id+" is dragged...")
		this.itemDrag.x = this.itemDragPt.x+(dxy.x-this.downPt.x);
		this.itemDrag.y = this.itemDragPt.y+(dxy.y-this.downPt.y);
		this.getDrawPanel().update_draw() ;
	}
}

oc.interact.OperDrag.prototype.on_mouse_up=function(pxy,dxy)
{
	this.downPt = null ;
	//if(this.itemDrag!=null)
	this.itemDrag = null;
}

oc.interact.OperDrag.prototype.on_mouse_wheel=function(pxy,dxy,delta)
{
	this.getDrawPanel().ajustDrawResolution(dxy.x,dxy.y,delta);
}
