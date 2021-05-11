
oc.di.DICommon=function()
{

	oc.DrawItem.apply(this,arguments);
}

oc.di.DICommon.prototype = new oc.DrawItem() ;

oc.di.DICommon.prototype.getClassName=function()
{
	return "DICommon" ;
}

