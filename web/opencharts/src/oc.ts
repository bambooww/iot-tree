/**
 * @module oc  //open chart
 */

namespace oc
 {
	export type XYRes={x_res:number,y_res:number};

	export enum Cursor {auto,crosshair,default,hand,move,text,
		w_resize,s_resize,n_resize,e_resize,ne_resize,sw_resize,se_resize,nw_resize,pointer}

	// export function oc_init(initcb)
	// {
	// 	//oc.loader.load_js(oc.loader._dyn_jss,initcb) ;
	// }

	export interface IRectItem
	{
		chkPtOnCtrl(pxy:base.Pt,dxy:base.Pt):string|null;

		changeRect(ctrlpt:string,x:number,y:number):void;
	}

	export type JsLoaderCB=(jsl:JsLoader)=>void;

	export type JssLoaderCB=(jssl:JssLoader)=>void;

	/**
	 * single js loader
	 */
	export class JsLoader
	{
		url:string ;

		loaded:boolean;

		loadOkCb:JsLoaderCB;

		constructor(url:string,load_ok_cb:JsLoaderCB)
		{
			this.url = url ;
			this.loaded = false;
			this.loadOkCb = load_ok_cb ;
		}

		public getUrl():string
		{
			return this.url ;
		}

		public isLoadedOk():boolean
		{
			return this.loaded ;
		}

		public load()
		{
			JsLoader.loadJsUrl(this.url,()=>{
				this.loaded = true;
				this.loadOkCb(this) ;
			}) ;
		}

		public static loadJsUrl(url:string,loadcb:oc.base.CB_P0)
        {
            var script=document.createElement("script");
            script.type="text/javascript";
            if(script["readyState"])
            {
                //ie
                script["onreadystatechange"]=function(){
                    if(script["readyState"]=="complete"||script["readyState"]=="loaded")
                    {
                        loadcb() ;
                    }
                }
            }else{
                //Chrome Safari Opera Firefox
                script.onload=()=>{
                    loadcb() ;
                }
            }
            script.src=url;
            document.head.appendChild(script);
        }
	}

	/**
	 * multi js loader
	 */
	export class JssLoader
	{
		loaders:JsLoader[]=[];

		loadOkCb:JssLoaderCB;

		constructor(urls:string[],jsscb:JssLoaderCB)
		{
			this.loadOkCb = jsscb ;
			this.initUrls(urls) ;
		}

		private initUrls(urls:string[])
		{
			for(var u of urls)
			{
				var jsl = new JsLoader(u,(jsl)=>{
					if(this.isLoadedOk())
						this.loadOkCb(this) ;
				}) ;
				this.loaders.push(jsl) ;
			}
		}

		public getJsLoaders():JsLoader[]
		{
			return this.loaders ;
		}

		public getJsUrls():string[]
		{
			var ret:string[]=[] ;
			for(var jsl of this.loaders)
				ret.push(jsl.getUrl()) ;
			return ret;
		}

		public isLoadedOk():boolean
		{
			for(var ld of this.loaders)
			{
				if(!ld.isLoadedOk())
					return false;
			}
			return true;
		}

		public load():void
		{
			for(var ld of this.loaders)
			{
				ld.load() ;
			}
		}
	}
 }


// var CHART_ROOT = "/admin/_chart/" ;
// oc.base={};
// oc.interact={};
// oc.di={};
// oc.loader={};
// oc.editor={};
// oc.util={};


// oc.loader.IdxCb=function(js)
// {
// 	this.js=js;
// }

// oc.loader.__idxCbs = [] ;
// oc.loader.__cur_load_idx = 0 ;
// oc.loader.__load_endcb =null ;

// oc.loader._load_one=function()
// {
// 	if(this.__cur_load_idx>=this.__idxCbs.length)
// 		return ;
// 	var ic = this.__idxCbs[this.__cur_load_idx];
// 	//console.log(CHART_ROOT+ic.js);
// 	$.getScript(CHART_ROOT+ic.js).done(function(){
// 		oc.loader.__cur_load_idx ++ ;
// 		if(oc.loader.__cur_load_idx==oc.loader.__idxCbs.length)
// 		{//end
// 			console.log("load end succ");
// 			oc.loader.__load_endcb() ;
// 		}
// 		else
// 		{
// 			oc.loader._load_one();
// 		}
// 	}).error(function(err){
// 		console.log("load err:"+ic.js);
// 	});//) ;
// }

// oc.loader.load_js=function(jss,endcb)
// {
// 	if(jss==null||jss.length<=0)
// 		return ;
// 	oc.loader.__load_endcb = endcb;
// 	for(var i = 0 ; i < jss.length ; i ++)
// 	{
// 		oc.loader.__idxCbs.push(new oc.loader.IdxCb(jss[i])) ;
// 	}
	
// 	oc.loader._load_one();
// }

// oc.loader._dyn_jss = [
// 	"draw_base.js","draw_util.js","draw_item.js","draw_layer.js",
// 	"draw_panel.js","draw_edit.js",//"draw_interact.js",
// 	"di/di_common.js","di/di_rect.js",
// 	"interact/oper_drag.js"//,"interact/interact_editlayer.js"
// 	] ;
// //oc.loader._dyn_jss1 = ["di/di_common.js","interact/dinter_drag.js"] ;



