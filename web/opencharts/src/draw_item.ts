/**
 * @module ol/DrawItem
 */

module oc
{
	export type PROP_ITEM={ title: string, type: string, enum_val:[], bind: boolean}; 
	/**
	 * support rect in which drawitems be drawed in it
	 */
	export interface IDrawItemContainer
	{//
		getXYResolution():XYRes;

		transPixelPt2DrawPt(px: number, py: number):base.Pt;

		transDrawPt2PixelPt(dx: number, dy: number):base.Pt;

		transDrawLen2PixelLen(b_xres:boolean,len: number):number;

		transPixelLen2DrawLen(b_xres:boolean,len:number):number;
		
		getItemsShow():DrawItem[];

		removeItem(item:DrawItem):boolean;

		notifyItemsChg():void;

		//calcResId(resid:string):string ;
	}

	export interface IItemsLister
	{
		getItemsShow():DrawItem[];

		removeItem(item:DrawItem):boolean;
	}

	
	export class ItemsContainer implements IDrawItemContainer
	{
		public static calcRect(items:DrawItem[]): oc.base.Rect|null
		{
			if (items.length <= 0)
				return null;
			var r: base.Rect | null = null;
			for (var i = 0; i < items.length; i++)
			{
				var tmpr = items[i].getBoundRectDraw();
				if (tmpr == null)
				{
					var py = items[i].getBoundPolygonDraw() ;
					if(py!=null)
						tmpr = py.getBoundingBox() ;
				}
				if(tmpr==null)
					continue;
				if (r == null)
					r = base.Rect.copy(tmpr);
				else
					r.expandBy(tmpr);
			}
			return r ;
		}

		public static calcRectExt(items:DrawItem[],ext_items:DrawItem[]): oc.base.Rect|null
		{
			if (items.length <= 0)
				return null;
			var r: base.Rect | null = null;
			for (var item of items)
			{
				var tmpr = item.getBoundRectDraw();
				if (tmpr == null)
					continue;
				if (r == null)
					r = base.Rect.copy(tmpr);
				else
					r.expandBy(tmpr);
			}
			for (var item of ext_items)
			{
				var tmpr = item.getBoundRectDraw();
				if (tmpr == null)
					continue;
				if (r == null)
					r = base.Rect.copy(tmpr);
				else
					r.expandBy(tmpr);
			}
			return r ;
		}

		itemRect:DrawItemRect;
		//items:DrawItem[];
		itemsLister:IItemsLister;
		parentC:IDrawItemContainer;

		private itemsRect:base.Rect|null=null;

		public constructor(itemr:DrawItemRect,pc:IDrawItemContainer,items:IItemsLister)
		{
			this.itemRect = itemr;
			this.parentC = pc ;
			this.itemsLister = items;
		}

		public getItemsRectInner(): base.Rect | null
		{
			if(this.itemsRect!=null)
				return this.itemsRect ;

			var items = this.itemsLister.getItemsShow();
			this.itemsRect = ItemsContainer.calcRect(items);
			return this.itemsRect;
		}

		// private getItemsRectOutter():base.Rect|null
		// {
		// 	var r = this.getItemsRectInner();
		// 	if(r==null)
		// 		return null ;

		// 	return new base.Rect(this.x,this.y,this.x+r.w,this.y+r.h) ;
		// }

		private calXYResolution():XYRes
		{
			//if(this.xy_res!=null)
			//	return this.xy_res ;
			var itemr = this.getItemsRectInner();
			if(itemr==null)
			{
				//this.xy_res={x_res:1,y_res:1};
				return {x_res:1,y_res:1};
			}
			var contr = this.itemRect.getBoundRectDraw();
			if(contr==null)
				return {x_res:1,y_res:1};
			return {x_res:itemr.w/contr.w,y_res:itemr.h/contr.h};
			//return this.xy_res ;
		}

		public getXYResolution():XYRes
		{
			var locr = this.calXYResolution();
			// var c = this.getContainer();
			// if(c==null)
			// 	return locr ;
			var cres = this.parentC.getXYResolution();
			return {x_res:locr.x_res*cres.x_res,y_res:locr.y_res*cres.y_res};
		}

		private getPixelCenter():base.Pt
		{
			// var c = this.getContainer();
			// if(c==null)
			// 	return {x:0,y:0} ;
			
			return this.parentC.transDrawPt2PixelPt(this.itemRect.x,this.itemRect.y);
		}

		private getDrawCenter():base.Pt
		{
			// var c = this.getContainer();
			// if(c==null)
			// 	return {x:0,y:0} ;//error
			var itemr = this.getItemsRectInner();
			if(itemr==null)
				return {x:0,y:0} ;//error

			return {x:itemr.x,y:itemr.y};
		}

		public transPixelPt2DrawPt(px: number, py: number):base.Pt
		{//pixel pt to draw pt
			var drawc = this.getDrawCenter();
			var pc = this.getPixelCenter();
			var xyr = this.getXYResolution();
			var dx = (px - pc.x) * xyr.x_res + drawc.x;
			var dy = drawc.y - (pc.y - py) * xyr.y_res;
			//var dy = 0 - (pc.y - py) * xyr.y_res;
			return { x: dx, y: dy };
		}

		public transDrawPt2PixelPt(dx: number, dy: number):base.Pt
		{
			var drawc = this.getDrawCenter();
			var pc = this.getPixelCenter();
			var xyr = this.getXYResolution();
			var px = (dx - drawc.x) / xyr.x_res + pc.x;
			//var px = (dx - this.drawCenter.x) / this.drawResolution + pc.x;
			//var py = pc.y + dy / xyr.y_res;
			var py = pc.y - (drawc.y - dy) / xyr.y_res;
			px = Math.round(px);
			py = Math.round(py);
			return { x: px, y: py };
		}

		public transDrawLen2PixelLen(b_xres:boolean,len: number):number
		{
			var xyr = this.getXYResolution();
			if(b_xres)
				return Math.round(len / xyr.x_res);
			else
				return Math.round(len / xyr.y_res);
		}

		public transPixelLen2DrawLen(b_xres:boolean,len:number):number
		{
			var xyr = this.getXYResolution();
			if(b_xres)
				return len*xyr.x_res ;
			else
				return len*xyr.y_res ;
		}

		getItemsShow():DrawItem[]
		{
			return this.itemsLister.getItemsShow();
		}

		removeItem(item:DrawItem):boolean
		{
			return false;//not support
		}

		notifyItemsChg():void
		{
			this.itemsRect=null;
		}

		// calcResId(resid:string):string
		// {
		// 	return "" ;
		// }
	}

	export interface IDrawNode extends IPopMenuTarget
	{
		getId():string;
		getName():string;
		getTitle():string;

		getPanel():AbstractDrawPanel|null;
	}

	/**
	 * for node with some action related
	 */
    export interface IActionNode extends IDrawNode
    {//implement and in some event call createShowPopMenu(this,pxy,dxy);
        // if(PopMenu.createShowPopMenu(this,pxy,dxy))
		// 		e.preventDefault();
        getActionTypeName():string;

        //getPopMenu(): PopMenu|null
    }

	export abstract class DrawItem implements IDrawNode
	{
		private drawLayer:DrawLayer|null = null;

		//private drawRes:IDrawRes|null = null ;

		private container:IDrawItemContainer|null = null;//

		id: string = "";
		name: string = "";
		title: string = "";

		x: number = 0;//opts?opts.x:0 ; related or abstract
		y: number = 0;//opts?opts.y:0 ;
		zindex: number = 0;
		//b_vis: boolean = true;

		bMouseIn:boolean = false;

		parentNode:DrawItem|null=null ;
		/**
		 * belong to group name
		 * if groupName can be found instance,then group will limit
		 * this item in it's area.
		 */
		private groupName:string|null=null;
		private groupDI:oc.DrawItemGroup|null=null;

		/**
		 * set obj mark,which can be used to filter
		 */
		private mark:string|null=null;

		private bvisiable:boolean=true;
		/**
		 * for drawitem some event interaction
		 * make drawitem can response to some human event
		 * like ,mouse click etc.
		 */
		eventBD:oc.base.Props<EventBinder>={};

        propBD:oc.base.Props<PropBinder>={};

		public constructor(opts:{}|undefined)
		{
			// if(arguments.length>=1)
			// {
			// 	this.inject(arguments[0]);
			// }
			if(opts!=undefined)
			{
				this.inject(opts,false);
			}
		}

		public getParentNode():DrawItem|null
		{
			return this.parentNode;
		}

		/**
		 * get node related DrawRes
		 * 
		 */
		public getDrawRes():IDrawRes|null
		{
			if(this["getDrawResUrl"])
			{
				return oc.base.forceCast<IDrawRes>(this) ;
			}

			var pn = this.getParentNode();
			if(pn!=null)
			{
				var dr = pn.getDrawRes() ;
				if(dr!=null)
					return dr ;
			}

			var ly = this.getLayer() ;
			if(ly!=null)
			{
				var dr = ly.getDrawRes() ;
				if(dr!=null)
					return dr ;
			}

			// var p = this.getPanel() ;
			// if(p!=null)
			// {
			// 	var dr = p.getDrawRes();
			// 	if(dr!=null)
			// 		return dr ;
			// }

			return null;//this.drawRes;
		}

		

		public getClassName(): string
		{
			return "DrawItem";
		}

		public setMark(m:string|null)
		{
			this.mark = m ;
		}

		public getMark():string|null
		{
			return this.mark;
		}


		public static createByClassName(cn:string,opts:{}|undefined):DrawItem|null
		{
			var r = null;
			if(cn=="DrawItems"||cn=="DrawUnitIns"||cn=="DrawUnit"||cn=="DrawItemGroup")
				eval("r=new oc."+cn+"(opts)") ;
			else if(cn.indexOf("oc.")!=0)
				eval("r=new oc.di."+cn+"(opts)") ;
			else
				eval("r=new "+cn+"(opts)");
			return r;
		}

		public static createByFullClassName(cn:string,opts:{}|undefined,bnew:boolean):DrawItem|null
		{
			var r = this.createByClassName(cn,opts);
			if(r==null)
				return null ;
			if(bnew)
				r.id = util.create_new_tmp_id();
			return r ;
		}
	

		static DrawItem_PNS = {
			_cat_name: "basic", _cat_title: "Basic",
			id: { title: "Id", type: "str", readonly: true },
			name: { title: "Name", type: "str" },
			title: { title: "Title", type: "str",binder:true },
			x: { title: "X", type: "number", readonly: true,binder:true },
			y: { title: "Y", type: "number", readonly: true,binder:true },
			zindex: { title: "z-index", type: "int" },
			groupName:{title:"Group Name",type:"str"},
			//b_vis: { title: "visiable", type: "bool", enum_val: [[false, "hidden"], [true, "show"]], bind: true },
		};

		static DrawItem_ENS:{}|null = null;

		public getPropDefs():Array<any>
		{
			var r = [];
			r.push(DrawItem.DrawItem_PNS);
			return r;
		}


		public isVisiable():boolean
		{
			return this.bvisiable ;
		}

		public setVisiable(v:boolean)
		{
			this.bvisiable = v ;
		}
		/**
		 * get prop item def in all pdf
		 * @param propn
		 */
		public findProDefItemByName(propn:string):{}|null
		{
			for(var pdf of this.getPropDefs())
			{
				var f = pdf[propn] ;
				if(f!=null&&f!=undefined)
					return f ;
			}
			return null ;
		}

		public getEventDefs():Array<any>
		{
			var r = [];
			if(DrawItem.DrawItem_ENS==null)
			{
				var di_ens = {_cat_name: "basic", _cat_title: "Basic"} ;
				for(var i = 0 ; i < MOUSE_EVT_TP_NUM ; i++)
				{
					var n = getMouseEventNameByTp(i);
					di_ens[n] = {title:"on_"+n,evt_tp:"mouse"} ;
				}
				di_ens["tick"]={title:"on_tick",evt_tp:"tick"} ;
				DrawItem.DrawItem_ENS = di_ens;
			}
			r.push(DrawItem.DrawItem_ENS);
			return r;
		}


		private getPropDefItem(n:string):{}|null
		{
			for(var pdef of this.getPropDefs())
			{
				var r = pdef[n] ;
				if(r!==null&&r!=undefined)
					return r ;
			}
			return null;
		}


		/**
		 * get prop def names which can be used to bind dyn data
		 * sub class may override it ,to change bind prop names
		 *   e.g when x y prop is calculated by other ,then it must no to be bind
		 */
		public getPropDefBinderNames():string[]
		{
			return ["x","y"];
		}

		public setBinderItem(pn:string,event:string,js:string)
		{

		}

		/**
		 * return change propnames
		 * @param dyn 
		 * @param bfirechg 
		 */
		public setDynData(dyn:{},bfirechg:boolean=true):string[]
		{
			var ns = [];
			for(var n in dyn)
			{
				ns.push(n);
				this[n] = dyn[n];
			}
			if(ns.length>0&&bfirechg)
				this.MODEL_fireChged(ns);
			return ns;
		}

		public getGroupName():string|null
		{
			return this.groupName;
		}

		public setGroupName(n:string|null)
		{
			this.groupName = n ;
		}

		public getGroup():oc.DrawItemGroup|null
		{
			if(this.groupName==null||this.groupName=="")
			{
				this.groupDI=null;
				return null ;
			}
			if(this.groupDI!=null)
				return this.groupDI;
			var lay = this.getLayer();
			if(lay==null)
				return null ;
			this.groupDI = lay.getItemByName(this.groupName) as oc.DrawItemGroup;
			return this.groupDI;
		}

		public static transStr2Val(tp:string,strv:string,defaultv:number|string|boolean|null):number|string|boolean|null
		{
			var r ;
			if(tp=="number"||tp=="float"||tp=="double")
				r = parseFloat(strv) ;
			else if(tp=="int"||tp=="short"||tp=="long")
				r = parseInt(strv) ;
			else if(tp=="bool"||tp=="boolean")
				return "true"==strv || "1"==strv ;
			else
				r = strv ;
			if(r==null||r==NaN||r==undefined)
				r = defaultv ;
			return r ;
		}

		public inject(opts:base.Props<any>,ignore_readonly:boolean|undefined)
		{
			if (opts == null)
				opts = {};

			var pdefs = this.getPropDefs();
			var chgpns:string[]=[];
			for (var i = 0; i < pdefs.length; i++)
			{
				var pdef = pdefs[i];
				for (var n in pdef)
				{
					if (n.indexOf("_") == 0)
						continue;
					var v = opts[n];
					if (v == undefined || v == null)
						continue;
					var def = pdef[n];
					if(def["readonly"]&&ignore_readonly)
						continue;
					v = DrawItem.transStr2Val(def["type"],v,null);
					if(v==null)
						continue;
					this[n] = v;

					chgpns.push(n);
				}
			}

			var tmpob:{[n:string]:base.Props<string>} = opts[EventBinder.EVENT_BINDER];
			if(tmpob)
			{
				for(var tmpn in tmpob)
				{
					var eb = new EventBinder() ;
					if(eb.fromPropStr(tmpob[tmpn]))
						this.eventBD[tmpn]=eb ;
				}
			}

			var tmpob:{[n:string]:base.Props<string>} = opts[PropBinder.PROP_BINDER];
			if(tmpob)
			{
				for(var tmpn in tmpob)
				{
					var pb = new PropBinder() ;
					if(pb.fromPropStr(tmpob[tmpn]))
						this.propBD[tmpn]=pb ;
				}
			}

			if (this.id == null || this.id == '' || this.id == undefined || this.id == 'undefined')
			{
				this.id = util.create_new_tmp_id();
				chgpns.push("id");
			}
			
			this.on_after_inject(opts);
			if(chgpns.length>0)
				this.MODEL_fireChged(chgpns) ;
		}
		

		public extract():base.Props<any>
		{
			let r:base.Props<any>={};
			//let r={};
			r["_cn"] = this.getClassName();

			var pdefs = this.getPropDefs();
			for (var i = 0; i < pdefs.length; i++)
			{
				var pdef = pdefs[i];
				for (var n in pdef)
				{
					if (n.indexOf("_") == 0)
						continue;
					var v = this[n];
					if (v == undefined || v == null)
						continue;
					r[n] = v;
				}
			}

			//var eventbd = this.eventBD ;
			var tmpob = {} ;
			for(var tmpn in this.eventBD)
			{
				var eb = this.eventBD[tmpn] ;
				tmpob[tmpn] = eb.toPropStr() ;
			}
			r[EventBinder.EVENT_BINDER] = tmpob ;

			tmpob = {} ;
			for(var tmpn in this.propBD)
			{
				var pb = this.propBD[tmpn] ;
				tmpob[tmpn] = pb.toPropStr() ;
			}
			r[PropBinder.PROP_BINDER] = tmpob ;
			return r;
		}

		public duplicateMe():DrawItem
		{
			var ps = this.extract();
			var r = DrawItem.createByClassName(this.getClassName(),undefined);
			if(r==null)
				throw Error("duplicate instance null");
			r.inject(ps,false);
			return r ;
		}

		public setPropValue(pnv:{})
		{
			var pns = [] ;
			for(var n in pnv)
			{
				var v = pnv[n];
				var defitem = this.getPropDefItem(n);
				if(defitem==null)
					continue ;
				//var def = defitem[n];
				v = DrawItem.transStr2Val(defitem["type"],v,null);
				if(v==null)
					continue;
				this[n] = v;
				pns.push(n);
			}
			if(pns.length>0)
				this.MODEL_fireChged(pns);
		}

		public setContainer(cont:IDrawItemContainer,lay: DrawLayer|null)
		{
			this.container = cont ;
			this.drawLayer = lay ;
			
			//parent first
			this.on_container_set() ;

			if(this instanceof DrawItems)
			{
				var c = this.getInnerContainer();
				if(c!=null)
				{
					var items = (<DrawItems>this).getItemsShow();
					for(var tmpi of items)
					{
						tmpi.setContainer(c,lay);
					}
				}
			}
			
		}

		//call after drawitem to be set container
		protected on_container_set()
		{}

		public getBoundRectDraw():base.Rect|null
		{//override
			return null;//new oc.base.Rect(this.x,this.y,this.w,)
		}

		public redraw()
		{
			var lay = this.getLayer() ;
			if(lay==null)
				return ;
			var cxt = lay.getCxtCurDraw();
			if(cxt==null)
				return ;
			var cont = this.getContainer() ;
			if(cont==null)
				return ;
			this.draw(cxt,cont);
		}


		public getBoundRectPixel():base.Rect|null
		{//final
			var dbr = this.getBoundRectDraw();
			if (dbr == null)
				return null;
			var c = this.getContainer();
			if (!c)
				return null;
			var dxy = c.transDrawPt2PixelPt(dbr.x, dbr.y);
			var dw = c.transDrawLen2PixelLen(true,dbr.w);
			var dh = c.transDrawLen2PixelLen(false,dbr.h);
			return new base.Rect(dxy.x, dxy.y, dw, dh);
		}


		public getBoundPolygonDraw():base.Polygon|null
		{//override
			return null;
		}

		public getBoundPolygonPixel():base.Polygon|null
		{//override
			var dbr = this.getBoundPolygonDraw();
			if (dbr == null)
				return null;
			var panel = this.getPanel();
			if (!panel)
				return null;
			// var dxy = panel.transDrawPt2PixelPt(dbr.x, dbr.y);
			// var dw = panel.transDrawLen2PixelLen(dbr.w);
			// var dh = panel.transDrawLen2PixelLen(dbr.h);
			// //return new oc.base.Rect(dxy.x, dxy.y, dw, dh);
			return null ;//TODO
		}

		//public 

		/**
		 * check current input draw pt can make select drawitem
		 * @param x 
		 * @param y 
		 */
		public chkCanSelectDrawPt(x:number,y:number):boolean
		{
			return this.containDrawPt(x, y);
		}


		public containDrawPt(x:number, y:number):boolean
		{
			var dr = this.getBoundRectDraw();
			if (dr != null)
				return dr.contains(x, y);
			var py = this.getBoundPolygonDraw();
			if (py != null)
				return py.contains(x, y);
			return false;
		}


		
		


		public getEventBinder(eventn:string):EventBinder|null
        {
             var r = this.eventBD[eventn];
             if(r==null||r==undefined)
                return null ;
            return r ;
        }

        public setEventBinder(eventn:string,clientjs:string,serverjs:string)
        {
            var r = this.eventBD[eventn];
             if(r==null||r==undefined)
             {
                 r = new EventBinder() ;
                 r.evtName = eventn ;
                 this.eventBD[eventn] = r ;
             }
			 r.clientJS = clientjs ;
			 r.serverJS = serverjs;
		}


		public getPropBinder(propn:string):PropBinder|null
        {
             var r = this.propBD[propn];
             if(r==null||r==undefined)
                return null ;
            return r ;
        }

        public setPropBinder(propn:string,jstxt:string,bexp:boolean)
        {
            var r = this.propBD[propn];
             if(r==null||r==undefined)
             {
                 r = new PropBinder() ;
                 r.propName = propn ;
                 this.propBD[propn] = r ;
             }
			 r.binderTxt = jstxt ;
			 r.bExp = bexp ;
		}
		
		public on_selected(bsel:boolean)
		{

		}

		public on_mouse_event(tp:MOUSE_EVT_TP,pxy:oc.base.Pt,dxy:oc.base.Pt,e:_MouseEvent)
		{//override to process mouse event directly,before interact to useit
			var evtn = getMouseEventNameByTp(tp) ;

			var eb = this.getEventBinder(evtn) ;
			if(eb!=null)
				eb.onEventRunMouse(this,pxy,dxy,e) ;
		}



		/**
		 * when mouse in item,and has many event tp
		 * @param pxy 
		 * @param dxy 
		 */
		public on_mouse_in()
		{

		}

		public on_mouse_over(tp: MOUSE_EVT_TP,pxy:oc.base.Pt,dxy:oc.base.Pt)
		{

		}

		/**
		 * when mouse out
		 */
		public on_mouse_out()
		{}

		/**
		 * on tick event by timer
		 */
		public on_tick()
		{
			var eb = this.getEventBinder("tick") ;
			if(eb!=null)
				eb.onEventRunTick(this) ;
		}

		public on_before_del():boolean
		{
			return false;
		}

		public on_after_inject(pvs:base.Props<any>)
		{
			//console.log(this.getClassName()+" "+this.getId()+"   after inj") ;
		}

		public isMouseIn():boolean
		{
			return this.bMouseIn;
		}

		public getLayer():DrawLayer|null
		{
			return this.drawLayer;
		}

		public getPanel():AbstractDrawPanel|null
		{
			if(this.drawLayer==null)
				return null ;
			return this.drawLayer.getPanel();
		}


		public getModel():DrawModel|null
		{
			var dp = this.getPanel() ;
			if(dp==null)
				return null ;
			var m = dp.getDrawView()?.getModel() ;
			if(m==undefined)
				return null ;
			return m ;
		}

		public getContainer():IDrawItemContainer|null
		{
			return this.container;
		}

		public getCxt():CanvasRenderingContext2D|null
		{
			if (this.drawLayer == null)
				return null;
			return this.drawLayer.getCxtCurDraw();
		}


		public getId():string
		{
			return this.id;
		}
		public setId(v:string)
		{
			this.id = v;
			this.MODEL_fireChged(["id"]) ;
		}

		public getName():string
		{
			return this.name;
		}

		public setName(n:string)
		{
			this.name = n;
		}

		public getTitle()
		{
			return this.title;
		}

		public setTitle(t:string)
		{
			this.title = t ;
		}

		public getDrawXY():base.Pt
		{
			return { x: this.x, y: this.y };
		}

		public getPixelXY():base.Pt|null
		{
			var c = this.getContainer();
			if(c==null)
				return null;
			var dxy = this.getDrawXY() ;
			return c.transDrawPt2PixelPt(dxy.x,dxy.y);
		}

		public MODEL_fireChged(prop_names:string[]|null)
		{
			if(prop_names==null)
				return;
			//empty pn must fire event
			var lay = this.getLayer();
			if(lay==null)
				return ;
			lay.MODEL_fireChged(this,prop_names) ;
		}

		public setDrawXY(x:number,y:number)
		{
			this.x = x;
			this.y = y;

			var g = this.getGroup();
			if(g!=null)
			{//limit in group
				var pt = g.getDrawXY();
				var s = g.getDrawSize();
				var r = this.getBoundRectDraw();
				if(r!=null)
				{
					var m = r.getMaxX();
					if(m>pt.x+s.w)
						this.x = pt.x+s.w-r.w;
					m = r.getMaxY();
					if(m>pt.y+s.h)
						this.y = pt.y+s.h-r.h ;
				}
				if(this.x<pt.x)
					this.x = pt.x ;
				if(this.y<pt.y)
					this.y = pt.y ;
			}

			this.MODEL_fireChged(["x","y"]) ;
		}

		/**
		 * when add new item in layer,item is set by beginxy and endxy
		 * return min end xy which must bigger than start xy
		 * @param x 
		 * @param y 
		 */
		public setDrawBeginXY(cont:IDrawItemContainer,x:number,y:number):boolean
		{
			this.x = x;
			this.y = y;
			return true;//not end and continue;
		}

		/**
		 * when add new item in layer,item is set by beginxy and endxy
		 * 
		 * @param x 
		 * @param y 
		 */
		public setDrawEndXY(cont:IDrawItemContainer,x:number,y:number):base.Pt
		{
			var minx = this.x + cont.transPixelLen2DrawLen(true,2);
			var miny = this.y + cont.transPixelLen2DrawLen(false,2);
			x = x<minx ? minx:x ;
			y = y<miny ? miny:y ;
			return {x:x,y:y};
		}


		/**
		 * true will make item cannot be display and select and any other oper
		 */
		public isHidden():boolean
		{
			return false;
		}

		/**
		 * true means item has no entity data,it's depends other items to create and need not to be saved
		 */
		public isVirtual():boolean
		{//
			return false;
		}

		draw(cxt:CanvasRenderingContext2D,c:IDrawItemContainer)
		{

		}

		draw_hidden(cxt:CanvasRenderingContext2D,c:IDrawItemContainer)
		{

		}

		public getSelState():oc.base.ItemSelState
		{
			var p = this.getPanel();
			
			if(p==null||this.isHidden())
				return {selected:false,dragover:false};
			var inta = p.getInteract();
			if(inta==null)
				return {selected:false,dragover:false};
			var bsel=false;
			var sis = inta.getSelectedItems();
			var bsel = sis!=null&&sis.indexOf(this)>=0;
			sis = inta.getDragOverSelItems();
			var bdragover = sis!=null&&sis.indexOf(this)>=0;
			return {selected:bsel,dragover:bdragover};
		}

		public draw_sel(cxt:CanvasRenderingContext2D,c:IDrawItemContainer,color:string)
		{//sel draw 
			var dr = this.getBoundRectPixel();
			if(dr==null)
				return;

			var d = 6;
			cxt.beginPath();
			cxt.strokeStyle=color;
			oc.util.drawRect(cxt, dr.x, dr.y, dr.w, dr.h,null, null, 1, "red");

			for(var pt of dr.listFourPt())
			{
				cxt.moveTo(pt.x, pt.y);
				cxt.arc(pt.x, pt.y, d, 0, Math.PI * 2, true);
			}
			cxt.stroke();
				
		}

		public draw_sel_bk0(cxt:CanvasRenderingContext2D)
		{//default draw 
			// if (selitem.draw_sel_or_not(this.context))
			// 	return;//

			var dr = this.getBoundRectPixel();
			//var dr  =selitem.getBoundRectDraw() ;
			if (dr != null)
			{
				var d = 6;
				var dh = 3;
				oc.util.drawRect(cxt, dr.x, dr.y, dr.w, dr.h, null, null, 2, "red");
				oc.util.drawRect(cxt, dr.x - dh, dr.y - dh, d, d, null, null, 2, "red");
				oc.util.drawRect(cxt, dr.x + dr.w - dh, dr.y - dh, d, d, null, null, 2, "red");
				oc.util.drawRect(cxt, dr.x + dr.w - dh, dr.y + dr.h - dh, d, d, null, null, 2, "red");
				oc.util.drawRect(cxt, dr.x - dh, dr.y + dr.h - dh, d, d, null, null, 2, "red");
				return;
			}
			var py = this.getBoundPolygonDraw();
			if (py != null)
			{
				return;
			}
		}
		
		public removeFromContainer():boolean
		{
			if(this.container==null)
				return false;
			return this.container.removeItem(this);
		}

		public toStr()
		{
			return "id=" + this.id + ",x=" + this.x + ",y=" + this.y;
		}
	}
}

