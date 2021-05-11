var CHART_ROOT = "/admin/_chart/" ;
var oc={};//open chart
oc.base={};
oc.interact={};
oc.di={};
oc.loader={};
oc.editor={};
oc.util={};


oc.loader.IdxCb=function(js)
{
	this.js=js;
}

oc.loader.__idxCbs = [] ;
oc.loader.__cur_load_idx = 0 ;
oc.loader.__load_endcb =null ;

oc.loader._load_one=function()
{
	if(this.__cur_load_idx>=this.__idxCbs.length)
		return ;
	var ic = this.__idxCbs[this.__cur_load_idx];
	//console.log(CHART_ROOT+ic.js);
	$.getScript(CHART_ROOT+ic.js).done(function(){
		oc.loader.__cur_load_idx ++ ;
		if(oc.loader.__cur_load_idx==oc.loader.__idxCbs.length)
		{//end
			console.log("load end succ");
			oc.loader.__load_endcb() ;
		}
		else
		{
			oc.loader._load_one();
		}
	}).error(function(err){
		console.log("load err:"+ic.js);
	});//) ;
}

oc.loader.load_js=function(jss,endcb)
{
	if(jss==null||jss.length<=0)
		return ;
	oc.loader.__load_endcb = endcb;
	for(var i = 0 ; i < jss.length ; i ++)
	{
		oc.loader.__idxCbs.push(new oc.loader.IdxCb(jss[i])) ;
	}
	
	oc.loader._load_one();
}

oc.loader._dyn_jss = [
	"draw_base.js","draw_util.js","draw_item.js","draw_layer.js",
	"draw_panel.js","draw_edit.js",//"draw_interact.js",
	"di/di_common.js","di/di_rect.js",
	"interact/oper_drag.js"//,"interact/interact_editlayer.js"
	] ;
//oc.loader._dyn_jss1 = ["di/di_common.js","interact/dinter_drag.js"] ;

oc.chart_init=function(initcb)
{
	oc.loader.load_js(oc.loader._dyn_jss,initcb) ;
}

