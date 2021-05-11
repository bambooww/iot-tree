module oc.hmi
{
    
    export type InterPropItem={n:string,t:string,onGetJS:string|null,onSetJS:string|null} ;
    export type InterEventItem={n:string,t:string} ;
    export type InterItem={props:InterPropItem[],events:InterEventItem[]}
    /**
     * component may expose prop interface when in designing
     */
    export class CompInterProp
    {
        n:string="" ;

        t:string="" ;

        tp:string="str" ;//number color 

        onSetJS:string|null=null ;

        private onSetFunc:Function|null = null ;

        onGetJS:string|null=null ;

        private onGetFunc:Function|null = null ;

        public constructor(opt:InterPropItem|undefined)
		{
            if(opt)
            {
                this.n = opt.n;
                this.setBy(opt) ;
            }
        }

        public toItem():InterPropItem
        {
            return {n:this.n,t:this.t,onGetJS:this.onGetJS,onSetJS:this.onSetJS} ;
        }

        public toPropDef()
        {
            return { title: this.t, type: this.tp,binder:true };
        }

        public setBy(ipi:InterPropItem)
        {
            this.t = ipi.t ;
            this.onGetJS = ipi.onGetJS ;
            this.onSetJS = ipi.onSetJS ;
            this.onSetFunc = null;
            this.onGetFunc = null;
            if(this.onSetJS?.trim()=="")
                this.onSetJS= null ;
            if(this.onGetJS?.trim()=="")
                this.onGetJS = null ;
        }

        public getName():string
        {
            return this.n ;
        }

        public getTitle():string
        {
            return this.t ;
        }

        public getTp():string
        {
            return this.tp ;
        }

        public isIgnoreSetNull()
        {//true will not trigger set func for input null or undefined
            return true;
        }

        public getOnGetJS():string|null
        {
            return this.onGetJS;
        }

        public hasOnGetJS():boolean
        {
            return this.onGetJS!=null;
        }

        // public setOnGetJS(js:string|null)
        // {
        //     this.onGetJS = js ;
        //     this.onGetFunc = null;
        // }

        public getOnSetJS():string|null
        {
            return this.onSetJS;
        }

        public hasOnSetJS():boolean
        {
            return this.onSetJS!=null;
        }

        // public setOnSetJS(js:string|null)
        // {
        //     this.onSetJS = js ;
        //     this.onSetFunc = null;
        // }

        public runGet(ins:HMICompIns):any
        {
            if(this.onGetJS==null)
                return undefined ;
            if(this.onGetFunc==null)
            {
                this.onGetFunc=new Function("$_this",this.onGetJS) ;
            }
            return this.onGetFunc(ins) ;
        }

        public runSet(ins:HMICompIns,v:any):void
        {
            if(this.onSetJS==null)
                return ;
            if(this.onSetFunc==null)
            {
                this.onSetFunc=new Function("$_this","value",this.onSetJS) ;
            }
            return this.onSetFunc(ins,v) ;
        }

    }

    export class CompInterEvent
    {
        n:string="" ;

        t:string="" ;

        public constructor(ei:InterEventItem)
		{
            this.n = ei.n;
            this.t = ei.t;
        }

        public toItem():InterEventItem
        {
            return {n:this.n,t:this.t} ;
        }

        public setBy(ipi:InterEventItem)
        {
            this.t = ipi.t ;
        }

        public getName():string
        {
            return this.n ;
        }

        public getTitle():string
        {
            return this.t ;
        }



        public fireInsValue(ins:HMICompIns,v:string)
        {

        }

    }


    export class CompInter
    {
        private interProps:CompInterProp[]=[] ;

        private interEvents:CompInterEvent[]=[] ;

        public injectInter(ii:InterItem)
        {
            for(var ci of ii.props)
            {
                var cc = new CompInterProp(ci) ;
                this.interProps.push(cc) ;
            }

            for(var ei of ii.events)
            {
                var ie = new CompInterEvent(ei) ;
                this.interEvents.push(ie) ;
            }
        }

        public extractInter():InterItem
        {
            var ps:InterPropItem[]=[] ;
            for(var ci of this.interProps)
            {
                var ii = ci.toItem() ;
                ps.push(ii) ;
            }
            var es:InterEventItem[]=[] ;
            for(var ie of this.interEvents)
            {
                var ei = ie.toItem() ;
                es.push(ei) ;
            }
            return {props:ps,events:es} ;
        }

        public getInterProps():CompInterProp[]
        {
            return this.interProps ;
        }

        public getInterPropByName(n:string):CompInterProp|null
        {
            for(var cc of this.interProps)
            {
                if(cc.getName()==n)
                    return cc ;
            }
            return null;
        }

        public setInterProp(n:string,ci:InterPropItem|null):boolean
        {
            if(ci==null)
            {//del
                for(var i = 0 ; i < this.interProps.length ; i ++)
                {
                    if(this.interProps[i].n==n)
                    {
                        this.interProps.splice(i,1) ;
                        return true;
                    }
                }
                return true;
            }
            if(n.indexOf("_")==0)
            {
                return false;
            }

            var oci = this.getInterPropByName(n) ;
            if(oci==null)
            {
                oci = new CompInterProp(ci) ;
                this.interProps.push(oci);
            }
            else
            {
                oci.setBy(ci);
            }
            return true;
        }

        public getInterEvents():CompInterEvent[]
        {
            return this.interEvents ;
        }

        public getInterEventByName(n:string):CompInterEvent|null
        {
            for(var cc of this.interEvents)
            {
                if(cc.getName()==n)
                    return cc ;
            }
            return null;
        }

        public setInterEvent(n:string,ci:InterEventItem|null):boolean
        {
            if(ci==null)
            {//del
                for(var i = 0 ; i < this.interEvents.length ; i ++)
                {
                    if(this.interEvents[i].n==n)
                    {
                        this.interEvents.splice(i,1) ;
                        return true;
                    }
                }
                return true;
            }
            if(n.indexOf("_")==0)
            {
                return false;
            }

            var oci = this.getInterEventByName(n) ;
            if(oci==null)
            {
                oci = new CompInterEvent(ci) ;
                this.interEvents.push(oci);
            }
            else
            {
                oci.setBy(ci);
            }
            return true;
        }
    }

    export class HMICompCat implements IDrawRes
    {
        catId :string = "" ;
		catTitle: string ="" ;
        public constructor(catid:string,title:string)
		{
            this.catId = catid ;
            this.catTitle = title ;
        }

        getDrawResUrl(name: string): string
        {
            return "/admin/util/rescxt_show_img.jsp?resid=ccat_"+this.catId+"-"+name ;
        }

        getDrawResParent(): IDrawRes|null
        {
            return null ;
        }
        
    }
    /**
     * related hmi comp
     */
    export class HMIComp extends DrawItems implements IDrawRes
	{
        static PN_INTER = "_comp_inter";


        catId :string = "" ;
		catTitle: string ="" ;
        compId:string = "" ;
        compTitle:string="" ;

		
		ins_alias_map:string|null=null;
		//private extProps:oc.base.Props<any>|null=null;

        //private aliasMap:oc.base.Props<string[]>|null=null;
        
        /**
         * for component interface definition for every instance
         * and it has props and events
         */
        private inter:CompInter=new CompInter() ;

        private interPropDefsForIns:{}|null=null;        

		public constructor(opts: {} | undefined)
		{
			super(opts);
        }

        public static calcDrawResUrl(compid:string,name:string):string
        {
            return "/admin/util/rescxt_show_img.jsp?resid=comp_"+compid+"-"+name ;
        }

        getDrawResUrl(name: string): string
        {
            return HMIComp.calcDrawResUrl(this.compId,name) ;
        }

        getDrawResParent(): IDrawRes
        {
            return new HMICompCat(this.catId,this.catTitle);
        }
        
        public getCompId():string
        {
            return this.compId ;
        }

        setCompId(compid:string)
        {
            this.compId = compid ;
        }

		public getClassName()
		{
			return "oc.hmi.HMIComp";
		}

		private static INS_TEMP_DEFS:oc.base.Props<any>= {
			ins_act_w: { title: "ins_act_w", type: "float" },
			ins_act_h: { title: "ins_act_h", type: "float" },
			
			ins_alias_map: { title: "ins_alias_map", type: "str",multi_lns:true },
		};

		/**
		 * for unit template edit to show temp prop
		 */
		public static getInsTempDefs():oc.base.Props<any>
		{
			return HMIComp.INS_TEMP_DEFS;
		}

		static Unit_PNS:{}|null=null;

		private static getUnitPns():{}
		{
			if(HMIComp.Unit_PNS!=null)
				return HMIComp.Unit_PNS;

			var r = {
				_cat_name: "unit", _cat_title: "Unit",
				cat: { title: "Cat", type: "str" },
				//title: { title: "Title", type: "str" }
			};
	
			for(var n in HMIComp.INS_TEMP_DEFS)
			{
				r[n] = HMIComp.INS_TEMP_DEFS[n];
			}

			HMIComp.Unit_PNS = r ;
			return r ;
        }
        

        public getInterPropDefsForIns()
        {
            if(this.interPropDefsForIns!=null)
                return this.interPropDefsForIns ;
            var r = {
                _cat_name: "comp_inter", _cat_title: "Comp Interface",
            };

            for(var ip of this.inter.getInterProps())
            {
                r[ip.n]=ip.toPropDef() ;
            }
            this.interPropDefsForIns = r ;
            return this.interPropDefsForIns;
        }

		public getPropDefs(): Array<any>
		{
			var r = super.getPropDefs();
			r.push(HMIComp.getUnitPns());
			return r;
		}


		public getCatId()
		{
			return this.catId;
		}

		public setCat(c: string)
		{
			this.catId = c;
			this.MODEL_fireChged(["cat"]);
		}

		public getTitle()
		{
			return this.title;
		}

		public setTitle(t: string)
		{
			this.title = t;
			this.MODEL_fireChged(["title"]);
		}

		
		
		public inject(opts: base.Props<any>,ignore_readonly:boolean|undefined)
		{
			if (typeof (opts) == 'string')
				eval("opts=" + opts);
            super.inject(opts,ignore_readonly);
            
            var inter:InterItem = opts[HMIComp.PN_INTER] ;
            if(inter!=undefined&&inter!=null)
                this.inter.injectInter(inter);

			this.title = opts["title"] ? opts["title"] : "";

			var dis = opts["dis"];
			if (dis)
			{
				for (var i = 0; i < dis.length; i++)
				{
					var it = dis[i];
					var cn = it._cn;
					if (!cn)
						continue;
					var item = DrawItem.createByClassName(cn, undefined);
					if (item == null)
						continue;
					item.inject(it as base.Props<string>, false);
					this.addItem(item);
					//console.log(" draw unit cn="+cn+"  item="+item);
				}
			}
		}

		public extract(): {}
		{
			var r = super.extract();
			
            var interitem = this.inter.extractInter() ;
            r[HMIComp.PN_INTER] = interitem ;
            
			return r;
        }

        public getCompInter()
        {
            return this.inter ;
        }

		public getCompDrawSize():oc.base.Size
		{
			var n = this.items.length;
			if(n<=0)
				return {w:100,h:100};
			var last = this.items[n-1];
			var r;
			if(last instanceof DrawItemRectBorder)
			{
				var tmps:DrawItem[]=[];
				for(var i = 0 ; i < n-1 ; i ++)
					tmps.push(this.items[i]);
				r = ItemsContainer.calcRect(tmps);
			}
			else
				r = ItemsContainer.calcRect(this.items);
			if (r == null)
				return {w:100,h:100};
			return {w:r.w,h:r.h};
		}

		/**
		 * to fit for list with fix square size
		 * a square border sub item is needed to be add
		 */
		public addSquareBorder(): boolean
		{
			this.getDrawSize();
			var r = ItemsContainer.calcRect(this.items);
			if (r == null)
				return false;
			//console.log(r);
			r = r.expandToSquareByCenter();
			//console.log(r);
			var tmpi = new DrawItemRectBorder({ rect: r });
			this.addItem(tmpi);
			return true;
		}

		
		/**
		 * 
		 */
		// private drawComp(cxt: CanvasRenderingContext2D, c: IDrawItemContainer, dyn_ps: {})
		// {
		// 	for (var item of this.items)
		// 	{
		// 		var n = item.getName();
		// 		if (n != null && n != "")
		// 		{
		// 			var dynp = dyn_ps[n];
		// 			if (dynp != undefined && dynp != null)
		// 			{

		// 			}
		// 		}
		// 		item.draw(cxt, c);
		// 	}
		// }


		private static compid2item: { [id: string]: HMIComp|"" } = {};
		
		private static ajaxLoadUrl: string | null = null;

		private static compid2loadIns:{[id: string]: HMICompIns[]}={} ;

		public static setAjaxLoadUrl(u: string | null)
		{
			HMIComp.ajaxLoadUrl = u;
		}

		static setComp(u: HMIComp)//,ext:oc.base.Props<any>|null)
		{
			HMIComp.compid2item[u.getCompId()] = u;
		}

		static addCompByJSON(json: string | {})
		{
			if (typeof (json) == 'string')
				eval("json=" + json);

			var u = new HMIComp(undefined);
			u.inject(json as oc.base.Props<any>,false);
			var ext = json["_ext"];
			if(ext!=undefined&&ext!=null)
			{
				u.setDynData(ext,false);
			}
			HMIComp.setComp(u);
		}

		static getItemByCompId(id: string): HMIComp | null
		{
            var u = HMIComp.compid2item[id];
            if(u=="")
                return null ;

			if (u == null || u == undefined)
				return null;
			return u;
        }
        
        static getOrLoadItemByCompId(compins:HMICompIns)
        {
            if(HMIComp.ajaxLoadUrl==null)
                return ;
            var compid = compins.getCompId() ;
            if(compid==null)
                return ;
            if(compid.startsWith("d_"))
            {

            }
            else if(compid.startsWith("r_"))
            {

            }
            else
            {
                
            }
            var u = HMIComp.compid2item[compid];
            if (u != undefined && u != null)
            {
                if(u=="")
                    return ;
                compins.onCompSet(u) ;
                return ;
            }
            var ldins = HMIComp.compid2loadIns[compid];
            if(ldins==undefined || ldins==null)
            {
                ldins = [];
                HMIComp.compid2loadIns[compid] =ldins;
            }
            ldins.push(compins) ;

            oc.util.doAjax(HMIComp.ajaxLoadUrl,{compid:compid},(bsucc,ret)=>{
                if(!bsucc||compid==null)
                    return ;
                var compi = new HMIComp({}) ;
                compi.setCompId(compid) ;
                if(typeof(ret)=="string")
                    eval("ret=" + ret);
                compi.inject(ret as base.Props<any>,undefined) ;

                HMIComp.compid2item[compid] = compi ;
                var tmpins = HMIComp.compid2loadIns[compid];
                for(var ins of tmpins)
                {
                    ins.onCompSet(compi) ;
                }
            }) ;
        }

		static getCompJSONStr(compid: string): string | null
		{
			var du = HMIComp.getItemByCompId(compid);
			if (du == null)
				return null;
			var ob = du.extract();
			return JSON.stringify(ob);
		}
	}


    /**
     * comp instance of item
     */
    export class HMICompIns extends DrawItemRect
    {
        borderPixel: number | null = null;
        borderColor: string | null = "yellow";
        fillColor: string | null = null;
        radius: number | null = null;
        compId: string | null = null;
        //compInterPns:string|null=null;

        private hmiComp:HMIComp|null=null;
        /**
         * deep copy of DrawItem
         */
        private dynComp: HMIComp | null = null;

        private innerCont: ItemsContainer | null = null;

        

        public constructor(opts: {} | undefined)
        {
            super(opts);
        }

        public static PN_DYN_COMP = "_comp";

        private static PNS = {
            _cat_name: "compins", _cat_title: "Comp Ins",
            borderPixel: { title: "border", type: "str" },
            borderColor: { title: "borderColor", type: "str" },
            fillColor: { title: "fillColor", type: "str", val_tp: "color" },
            radius: { title: "radius", type: "int" },
            compId: { title: "Comp Id", type: "str", readonly: true },
           //compInterPns:{ title: "inter pns", type: "str", readonly: true },
        };

        
        public setCompId(n: string)
        {
            this.compId = n;
            this.MODEL_fireChged(["compId"]);

            //load comp asyn
            HMIComp.getOrLoadItemByCompId(this) ;
        }
        public getCompId()
        {
            return this.compId;
        }


        public setInterPropVal(name:string,v:any):boolean
        {
            if(this.dynComp==null)
                return false;

            var cip= this.dynComp.getCompInter().getInterPropByName(name) ;
            if(cip==null || !cip.hasOnSetJS())
                return false;
            
            if((v==undefined||v==null)&&cip.isIgnoreSetNull())
                return false;
            cip.runSet(this,v) ;
            return true;
        }


        private firstInjectOb:base.Props<any>|null=null ;

        public inject(opts:base.Props<any>,ignore_readonly:boolean|undefined)
        {//override to load comp
            super.inject(opts,ignore_readonly);

            if(this.firstInjectOb==null)
                this.firstInjectOb = opts ;

            if(this.dynComp!=null)
            {//
                for(var tmpn in opts)
                {
                    var v = this[tmpn] ;
                    this.setInterPropVal(tmpn,v);
                }
            }

            if(this.compId!=null&&this.compId!=undefined)
            {
                //load comp asyn
                HMIComp.getOrLoadItemByCompId(this) ;
            }
        }

        public extract():base.Props<any>
		{
            var r = super.extract() ;

            if(this.dynComp==null)
                return r ;
            
            var ipdf = this.dynComp.getInterPropDefsForIns() ;
            for(var tmpn in ipdf)
            {
                if(tmpn.indexOf("_")==0)
                    continue ;
                var cip = this.dynComp.getCompInter().getInterPropByName(tmpn);
                if(cip==null)
                    continue;
                if(!cip.hasOnGetJS())
                    continue ;
                var v = cip.runGet(this) ;
                if((v==undefined||v==null))
                    continue;
                r[tmpn] = v ;
            }
            
            return r ;
        }

        /**
         * override to provider res
         */
        public getDrawRes():IDrawRes|null
		{
            return this.hmiComp;
        }

        public onCompSet(comp:HMIComp)
        {
            this.hmiComp = comp ;
            this.compId = comp.getCompId() ;
            this.MODEL_fireChged([]) ;

            this.dynComp = comp.duplicateMe() as HMIComp;
            var c =this.getContainer();
            if (this.dynComp != null && c != null)
                this.dynComp.setContainer(c, this.getLayer());

            for(var tmpdi of this.dynComp.getItemsShow())
                tmpdi.parentNode = this ;
            
            //interface prop may be set
            this.updateInInterProps();

            this.MODEL_fireChged([]) ;

            return this.dynComp;
        }

        private updateInInterProps()
        {//out -> inter
            if(this.dynComp==null)
                return ;
            
            var ipdf = this.dynComp.getInterPropDefsForIns() ;
            for(var tmpn in ipdf)
            {
                if(tmpn.indexOf("_")==0)
                    continue ;
                var cip = this.dynComp.getCompInter().getInterPropByName(tmpn);
                if(cip==null)
                    continue;
                var v = this[tmpn] ;
                if((v==undefined||v==null)&&this.firstInjectOb!=null)
                {
                    v = this.firstInjectOb[tmpn] ;//
                    this[tmpn] = v ;
                }
                    
                if((v==undefined||v==null)&&cip.isIgnoreSetNull())
                    continue;
                cip.runSet(this,v) ;
            }
        }

        public getComp(): HMIComp | null
        {//a copy of unit
            // if (this.dynComp != null)
            //     return this.dynComp;
            // if (this.compId == null || this.compId == "")
            //     return null;

            // var du = HMIComp.getItemByCompId(this.compId);
            // if (du == null)
            //     return null;
            // this.dynComp = du.duplicateMe() as HMIComp;
            // var c = this.getContainer();
            // if (this.dynComp != null && c != null)
            //     this.dynComp.setContainer(c, this.getLayer());
            // return this.dynComp;
            return this.dynComp ;
        }


        public getInnerDrawItemByName(n: string): DrawItem | null
        {
            var u = this.getComp();
            if (u == null)
                return null;
            for (var i of u.getItemsShow())
            {
                if (n == i.getName())
                    return i;
            }
            return null;
        }

        public $(ob:{}|string):DrawItem|null
        {
            if(typeof(ob)=='string')
            {
                return this.getInnerDrawItemByName(ob) ;
            }
            else
            {
                var b = false;
                for(var n in ob)
                {
                    var tmpi = this.getInnerDrawItemByName(n);
                    if (tmpi == null)
                        continue;
                    tmpi.setDynData(ob[n], false);
                    b = true ;
                }
                this.MODEL_fireChged([]);
                return null ;
            }
            
        }

        
        /**
         * set inneritem dyn must by item's name
         * if item is not set name,it cannot be set dyn
         * @param dyn 
         * @param bfirechg 
         */
        public setDynData(dyn: {}, bfirechg: boolean = true): string[]
        {
            var ns = super.setDynData(dyn, false);

            var u = this.getComp();
            

            var dyn_comp = dyn[HMICompIns.PN_DYN_COMP];
            if (dyn_comp != undefined && dyn_comp != null)
            {
                ns.push(HMICompIns.PN_DYN_COMP);
                for (var n in dyn_comp)
                {
                    var tmpi = this.getInnerDrawItemByName(n);
                    if (tmpi == null)
                        continue;
                    tmpi.setDynData(dyn_comp[n], false);
                }
            }

            this.updateInInterProps();

            if (bfirechg)
                this.MODEL_fireChged(ns);
            return ns;
        }



        

        public fireInterEvent(eventn:string,eventv:any):boolean
        {
            var dyncomp = this.getComp() ;
            if(dyncomp==null)
                return false;
            var cc = dyncomp.getCompInter().getInterEventByName(eventn) ;
            if(cc==null)
                return false;
            //var evtb = this.getEventBinder("on_"+eventn) ;
            var evtb = this.getEventBinder(eventn) ;
            if(evtb!=null)
            {
                evtb.onEventRunInter(this) ; 
            }
            return true ;
        }

        private getInnerContainer(): ItemsContainer | null
        {
            if (this.innerCont != null)
                return this.innerCont;

            var pc = this.getContainer();
            if (pc == null)
                return null;
            var u = this.getComp();
            if (u == null)
                return null;
            this.innerCont = new ItemsContainer(this, pc, u);
            return this.innerCont;
        }



        public getClassName()
        {
            return "oc.hmi.HMICompIns";
        }

        public getPropDefs(): Array<any>
        {
            var r = super.getPropDefs();
            r.push(HMICompIns.PNS);
            var cp = this.getComp() ;
            if(cp!=null)
                r.push(cp.getInterPropDefsForIns()) ;
            return r;
        }

        public getEventDefs():Array<any>
		{
            var r = super.getEventDefs() ;
            var comp = this.getComp() ;
            if(comp!=null)
            {
                var interevts = comp.getCompInter().getInterEvents() ;
                var di_ens = {_cat_name: "hmi_comp", _cat_title: "Component Interface"} ;
                for(var interevt of interevts)
                {
                    var evtn = interevt.getName() ;
                    di_ens[evtn]={title:"on_"+evtn,evt_tp:"inter"} ;
                }
				r.push(di_ens) ;
            }
			
			return r;
		}

        public on_mouse_event(tp:MOUSE_EVT_TP,pxy:oc.base.Pt,dxy:oc.base.Pt,e:_MouseEvent)
		{
			var ic = this.getInnerContainer();
			if(ic==null)
                return;
            var u = this.getComp();
            if (u == null)
                return ;
			var sitems =  u.getItemsShow() ;
			if(sitems==null||sitems.length<=0)
                return ;
            //var innerr = ic.getItemsRectInner() ;
            var innerdxy = ic.transPixelPt2DrawPt(pxy.x,pxy.y) ;
			for(var si of sitems)
			{
                //console.log(innerdxy,si.getBoundRectDraw(),innerr) ;
                var bc = si.containDrawPt(innerdxy.x,innerdxy.y) ;
                if(bc)
                {
                    si.on_mouse_event(tp,pxy,innerdxy,e) ;
                    //console.log("sub item mouse in="+si.getName());
                }
			}
		}

        public getPrimRect(): base.Rect | null
        {
            var ic = this.getInnerContainer();
            if (ic == null)
                return new oc.base.Rect(0, 0, this.getW(), this.getH());
            // return ic.getItemsRectInner();
            var u = this.getComp();
            if (u == null)
                return new oc.base.Rect(0, 0, this.getW(), this.getH());
            //return u.getPrimRect();
            //return new oc.base.Rect(0,0,100,100);

            var r = ItemsContainer.calcRect(u.getItemsShow());
            if (r == null)
                return null;
            var p = ic.transDrawPt2PixelPt(r.x, r.y);
            var w = ic.transDrawLen2PixelLen(true, r.w);
            var h = ic.transDrawLen2PixelLen(false, r.h);

            return new oc.base.Rect(0, 0, w, h);
            //return r ;
        }

        /**
         * override to provider more extends item in unit
         */
        protected getUnitExtItems(): DrawItem[]
        {
            return [];
        }

        public drawPrim(cxt: CanvasRenderingContext2D): void
        {
            var u = this.getComp();
            var ic = this.getInnerContainer();
            var items = (u != null ? u.getItemsShow() : null);
            if (ic == null || u == null || items == null || items.length <= 0)
            {
                util.drawRectEmpty(cxt,
                    0, 0, this.getW(), this.getH(), this.borderColor);
                return;
            }

            //this.drawRect(cxt,c);
            //
            cxt.save();
            var pt = this.getPixelXY();
            if (pt != null)//what the fuck
                cxt.translate(-pt.x, -pt.y);
            for (var item of items)
            {
                item.draw(cxt, ic);
            }
            for (var item of this.getUnitExtItems())
            {
                item.draw(cxt, ic);
            }
            //u.draw(cxt, ic);
            cxt.restore();
        }

        public drawPrimSel(ctx: CanvasRenderingContext2D): void
        {

        }
    }
}