

oc.interact.InteractEditLayer=function()
{//selector scheduler
	oc.DrawInteract.apply(this,arguments);
	
	this.operDrag=new oc.interact.OperDrag("drag",this,null) ;
	
}


oc.interact.InteractEditLayer.prototype = new oc.DrawInteract() ;


oc.interact.InteractEditLayer.prototype.get_interact_opers=function()
{//get current opers
	return [this.operDrag];
}

oc.interact.InteractEditLayer.prototype.get_interact_view_opers=function()
{//get current opers
	return [];
}

