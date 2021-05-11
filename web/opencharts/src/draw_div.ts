/// <reference path="./draw_item_rect.ts" />

namespace oc
{
    
    /**
     * only for div comp loaded var
     */
    export class div_comps
    {
        
    }

    /**
     * Unit TreeNode which has inner div
     * then div can has it's owner display content
     * e.g list
     */
    export class DrawDiv extends DrawItemRect
    {
        private divEle: JQuery<HTMLElement> | null = null;
        private contEle: JQuery<HTMLElement> | null = null;
        protected innerEle:JQuery<HTMLElement> | null = null;

        // div_scroll:boolean=false; for sub may cause display bug
        //div_transparent:number=0.5;
        div_bkcolor:string="" ;
        border:number=0;
        border_color:string="#8cdcda";
        eleId:string|null=null;

        public constructor(opts: {} | undefined)
        {
            super(opts);
        }

        public getClassName()
        {
            return "oc.DrawDiv";
        }

        static PNS_DIV = {
            _cat_name: "ddiv", _cat_title: "Draw Div",
            border: {title: "Border", type:"number",binder:true},
            border_color:{title:"Border Color",type:"str",binder:true},
            div_scroll: {title: "Scroll", type:"boolean",enum_val: [[true, "Yes"], [false, "No"]]},
            //div_transparent: {title: "Transparent", type:"number",binder:true},
            div_bkcolor:{title:"Background Color",type:"str",edit_plug:"color",binder:true}
		};

		public getPropDefs(): Array<any>
		{
			var r = super.getPropDefs();
			r.push(DrawDiv.PNS_DIV);
			return r;
        }

        public on_after_inject(pvs:base.Props<any>)
        {//change some special prop
            super.on_after_inject(pvs) ;
            this.getContEle();
            if(this.contEle==null)
                return ;
            var v = pvs["div_bkcolor"];
            if(v!=undefined&&v!=null)
            {
                if(this.divEle!=null)
                    this.divEle.css("background-color",v) ;
                this.contEle.css("background-color",v) ;
            }
            //var 
            
                //this.contEle.get(0).style.backgroundColor=v ;
        }

        public setVisiable(b:boolean)
        {
            super.setVisiable(b) ;
            if(this.divEle!=null)
            {
                this.divEle.css("display",b?"":"none") ;
            }
        }
        
        protected getContEleId():string
        {
            if(this.eleId==null)
                this.eleId = oc.util.create_new_tmp_id() ;
            return "c_"+this.eleId;//this.getId() ;
        }

        public getContEle():JQuery<HTMLElement> | null
        {
            if(this.contEle!=null)
                return this.contEle ;

            var p = this.getPanel();
            if (p == null)
                return null;

            var ele = p.getHTMLElement();
            var tmpid = this.getId();
            var scroll = "" ;
            //if(this.div_scroll==true)
            //    scroll = "overflow:auto;";
            //else
                scroll = "overflow:hidden;";
            var bkc = "";
            if(this.div_bkcolor!=null&&this.div_bkcolor!=undefined&&this.div_bkcolor!="")
                bkc = "background-color:"+this.div_bkcolor ;
            this.divEle = $(`<div id="div_${tmpid}" class="oc_unit_action" style="${scroll};${bkc}">
                <div id="${this.getContEleId()}" class="content" style="width:100%;height:100%;${bkc}"></div></div>`);
            this.divEle.get(0)["_oc_di_div"] = this;
            $(ele).append(this.divEle);
            this.contEle = $("#"+this.getContEleId());
 
            if(this.innerEle!=null)
                this.contEle.append(this.innerEle);
            
            return this.contEle ;
        }

        //override to init div
        // protected on_container_set()
        // {
        //     super.on_container_set() ;
        //     this.getContEle() ;
        // }

        /**
         * override to del div
         */
        public removeFromContainer():boolean
		{
            if(!super.removeFromContainer())
                return false;
            if(this.divEle!=null)
                this.divEle.remove();
            return true;
		}

        public setInnerEle(ele:string|JQuery<HTMLElement>):JQuery<HTMLElement>
        {
            if(typeof(ele)=="string")
                ele = $(ele) ;
            this.innerEle = ele ;

            var contele = this.getContEle();
            if(contele==null)
                return this.innerEle;
            contele.empty();
            contele.append(this.innerEle);
            return this.innerEle;
        }

        draw_hidden(cxt:CanvasRenderingContext2D,c:IDrawItemContainer)
		{//override to hide div
            this.hideDivEle();
        }
        
        protected hideDivEle()
        {
            if(this.divEle==null)
                return ;
            this.divEle.css("display","none");
        }

        protected onDivResize(x:number,y:number,w:number,h:number,b_notchg:boolean):void
        {

        }


        private lastRect:oc.base.Rect|null = null ;

        protected displayDivEle()
        {
            if(this.divEle==null)
                return ;
                
            var contele = this.getContEle();
            if(contele==null||this.divEle==null)
                return ;
            var c = this.getContainer() ;
            if(c==null)
                return ;
            var hh = c.transDrawLen2PixelLen(false,30) ;
            if(this.title==null||this.title=="")
                hh = 0 ;
            var r = this.getBoundRectPixel();
            
            if(r!=null)
            {
                var not_chg = r.equals(this.lastRect)
                

                if(not_chg)
                    return ;
                this.lastRect = r ;

                this.divEle.css("display","");
                //this.divEle.css("background-color","#eee");
                this.divEle.css("top",(r.y+hh)+"px");
                this.divEle.css("left",r.x+"px");
                this.divEle.css("width",r.w+"px");
                this.divEle.css("height",(r.h-hh)+"px");

                this.onDivResize(r.x,r.y+hh,r.w,r.h-hh,not_chg) ;
            }
        }

        public getPrimRect(): base.Rect | null
        {
            return new oc.base.Rect(0,0,100,100);
        }


        public drawPrim(ctx: CanvasRenderingContext2D): void
        {
            this.displayDivEle();

            if(this.border>0)
            {// hmi sub may cause display err
                //oc.util.drawRect(ctx, 0,0,100,100, null, null, this.border, this.border_color);
            }
                
        }

        public getTitle()
        {
            var t = super.getTitle() ;
            if(t==null)
                return "";
            return t ;
        }


        public getMinDrawSize():oc.base.Size
		{
            var t = this.getTitle() ;
			return {w:20*t.length,h:30};
		}

        public draw(ctx: CanvasRenderingContext2D, c: IDrawItemContainer)
		{
            //draw circle at left top position
            super.draw(ctx,c) ;

            var pxy = this.getPixelXY() ;
            if(pxy==null)
                return ;
            var fh = c.transDrawLen2PixelLen(false,20) ;
            //var pt = c.tr
            ctx.font = `${fh}px serif`;
			ctx.fillStyle = "yellow";
            var t = this.getTitle() ;
            if(t==null)
                t = "" ;
			ctx.fillText(t, pxy.x+fh, pxy.y+fh);

            
            
            
        }

        public drawPrimSel(ctx: CanvasRenderingContext2D): void
        {
            
        }
    }

    export type ICompInterItem={name:string,title:string,type:string,binder:boolean} ;
    export type ICompInters={props:ICompInterItem[],events:ICompInterItem[]}
    export interface ICompInterEventFirer
    {
        comp_inter_event_fire(n:string,v:string):void;
    }

    /**
     * js component must implements this interface
     */
    export interface IDIDivComp
	{
        comp_di:DIDivComp; //must be set instance when new construct

        comp_name:string ;

        comp_title:string ;

        comp_desc:string;

        comp_require_js:string[];

        comp_edit_url:string;

        comp_init(divid:string,view:DrawView):void;

        //inter
        comp_inters():ICompInters ;

        comp_inter_prop_set(n:string,v:any):void ;
        comp_inter_prop_get(n:string):any ;

        //events
        comp_on_resize(w:number,h:number):void;

        comp_on_data(bvalid:boolean,dt:number,val:number):void;


    }

    export type DIDivCB=(icomp:IDIDivComp)=>void;

    /**
     * 
     */
    class DIDivCompLoader
    {
        static LOADED_JS={} ;

        catName:string;
        compName:string;
        comp:IDIDivComp|null=null;
        loadOk:boolean=false;

        //loading listeners,
        loadingCB:DIDivCB[]=[];

        constructor(catname:string,compname:string)
        {
            this.catName = catname ;
            var catob = oc.div_comps[catname] ;
            if(catob==undefined||catob==null)
                oc.div_comps[catname]={} ;
            this.compName = compname ;
        }

        public getCompCat()
        {
            return this.catName ;
        }

        public getCompName()
        {
            return this.compName ;
        }

        public addLoadingCB(cb:DIDivCB)
        {
            var cp = this.createComp();
            if(cp!=null)
            {
                cb(cp) ;
                return ;
            }
            this.loadingCB.push(cb) ;
        }

        private fireLoadOk()
        {
            if(this.loadingCB.length==0)
                return ;
            if(this.comp==null)
                return ;
            for(var ldcb of this.loadingCB)
            {
                var tmpcomp = this.createComp() ;
                if(tmpcomp!=null)
                    ldcb(tmpcomp) ;
            }
            this.loadingCB=[];
        }

        doLoad()
        {
            var url = "/_iottree/di_div_comps/"+this.catName+"/comp_"+this.compName+".js" ;
            //load comp first
            JsLoader.loadJsUrl(url,()=>{
                this.comp = this.createCompInner();
                if(this.comp==null||this.comp==undefined)
                    return ;
                var rjs = this.comp.comp_require_js ;
                if(rjs==null||rjs==undefined||rjs.length==0)
                {//no needed js
                    this.loadOk = true ;
                    this.fireLoadOk();
                    return ;
                }
                //then load needed js
                var jsus:string[] = [] ;
                for(var rj of rjs)
                {
                    if(!rj.startsWith("/"))
                        rj = "/_iottree/di_div_comps/"+this.catName+"/"+rj;
                    if(DIDivCompLoader.LOADED_JS.hasOwnProperty(rj))
                        continue ;
                    jsus.push(rj) ;
                }
                if(jsus.length==0)
                {//no needed js
                    this.loadOk = true ;
                    this.fireLoadOk();
                    return ;
                }
                var jssl = new JssLoader(jsus,(ld)=>{
                    this.loadOk = true ;
                    for(var jsu of ld.getJsUrls())
                        DIDivCompLoader.LOADED_JS[jsu]="" ;
                    this.fireLoadOk();
                    return ;
                }) ;
                jssl.load() ;
                return ;
            });
        }

        private createCompInner():IDIDivComp|null
        {
            try
            {
                var tt={} ;
                eval("tt['aa']=new oc.div_comps."+this.catName+"."+this.compName+"(this)") ;
                var r = tt['aa'];
                if(r==undefined)
                    return null ;
                return r ;
            }
            catch(e)
            {
                console.error(e);
                return null ;
            }
        }

        createComp():IDIDivComp|null
        {
            if(!this.loadOk)
                return null ;
            return this.createCompInner();
        }

        isLoadOk():boolean
        {
            return this.loadOk ;
        }


    }

    export class DIDivComp extends DrawDiv implements ICompInterEventFirer
    {
        static DRAW_DIVCOMP="_draw_divcomp";

        static name2compitem:{}={};

        static getCompLoader(compuid:string):DIDivCompLoader
        {
            var ld:DIDivCompLoader|null = DIDivComp.name2compitem[compuid] ;
            if(ld==null)
            {
                var nn = DIDivComp.splitCompUid(compuid);
                ld = new DIDivCompLoader(nn[0],nn[1]) ;
                DIDivComp.name2compitem[compuid]=ld;
                ld.doLoad() ;
            }
            return ld ;
        }

        static splitCompUid(compuid:string):string[]
        {
            var i = compuid.indexOf('-') ;
            var catn = compuid.substr(0,i) ;
            var compn = compuid.substr(i+1) ;
            return [catn,compn] ;
        }

        comp:IDIDivComp|null = null ;
        isCompLoading:boolean=false;
        comp_uid:string="" ;
        //compName:string|null|undefined = null ;

        public constructor(opts: {} | undefined)
        {
            super(opts);
        }

        comp_inter_event_fire(n: string, v: string): void
        {
            
        }

        public getClassName()
        {
            return "oc.DIDivComp";
        }

        static PNS_COMP = {
			_cat_name: "divcomp", _cat_title: "Div Component",
            comp_uid: {title: "Component Uid", type:"str",readonly:true},
            //comp_param: {title: "Component param", type:"str"}
		};

		public getPropDefs(): Array<any>
		{
			var r = super.getPropDefs();
            r.push(DIDivComp.PNS_COMP);
            var cp = this.getOrLoadComp() ;
            if(cp!=null&&cp.comp_inters)
            {
                var cpinter = cp.comp_inters() ;
                if(cpinter!=null)
                {
                    var pns = {_cat_name:"divcomp_inter",_cat_title:"Component Interface"}
                    var nn = DIDivComp.splitCompUid(this.comp_uid) ;
                    var prefix = nn[0]+"_"+nn[1]+"_" ;
                    for(var p of cpinter.props)
                    {
                        var tmpn = p.name ;
                        if(tmpn==null||tmpn==undefined||tmpn=="")
                            continue ;
                        pns[prefix+tmpn] = p ;
                    }
                    r.push(pns) ;
                }
            }
			return r;
        }

        public getCompUid():string|null
        {
            var compuid = this["comp_uid"] ;
            if(compuid==null||compuid==undefined)
            {
                return null;
            }
            return compuid ;
        }

        public setCompUid(uid:string)
        {
            if(uid==this.getCompUid())
                return ;
            this["comp_uid"] = uid;
            this.reloadComp();
            this.MODEL_fireChged(["comp_uid"]);
        }

        public inject(opts:base.Props<any>,ignore_readonly:boolean|undefined)
        {//override to load comp
            super.inject(opts,ignore_readonly);

            var cp = this.getOrLoadComp() ;
            if(cp!=null&&cp.comp_inters)
            {
                var nn = DIDivComp.splitCompUid(this.comp_uid) ;
                var prefix = nn[0]+"_"+nn[1]+"_" ;

                var cpinter = cp.comp_inters() ;
                if(cpinter!=null)
                {
                    for(var p of cpinter.props)
                    {
                        var tmpn = prefix+p.name ;
                        var v = opts[tmpn] ;
                        if(v==undefined)
                            continue ;
                        cp.comp_inter_prop_set(p.name,v)
                    }
                }
            }
            
            if(this.comp_uid!=null&&this.comp_uid!=undefined)
            {
                
            }
        }

        public extract():base.Props<any>
		{
            var r = super.extract() ;

            var cp = this.getOrLoadComp() ;
            if(cp==null)
                return r ;
            
            if(!cp.comp_inters)
                return r ;
            
            var nn = DIDivComp.splitCompUid(this.comp_uid) ;
            var prefix = nn[0]+"_"+nn[1]+"_" ;

            var cpinter = cp.comp_inters() ;
            if(cpinter!=null)
            {
                for(var p of cpinter.props)
                {
                    var v = cp.comp_inter_prop_get(p.name) ;
                    if(v==null||v==undefined)
                        continue ;
                    var tmpn = prefix+p.name ;
                    r[tmpn]=v;
                }
            }
            
            return r ;
        }

        protected on_container_set()
        {
            super.on_container_set();
            this.getOrLoadComp();``
        }

        private  reloadComp():IDIDivComp|null
        {
            this.comp=null ;
            this.isCompLoading=false;
            return this.getOrLoadComp();
        }

        private compInsChged:boolean=false;
        
        private getOrLoadComp():IDIDivComp|null
        {
            var compuid = this.getCompUid();
            if(compuid==null)
            {
                return null;
            }
            
            if(this.comp!=null)
                return this.comp;

            var contele = this.getContEle();
            if(contele==null)
                return null;
            
            if(this.isCompLoading)
                return null ;

            var view = this.getPanel()?.getDrawView() ;
            if(view==null||view==undefined)
                return null ;
            var id = this.getContEleId() ;
            var ld = DIDivComp.getCompLoader(compuid) ;
            if(ld.isLoadOk())
            {
                var comp = ld.createComp() ;
                if(comp!=null)
                    comp.comp_init(id,view);
                this.comp = comp ;
                this.compInsChged=true ;
                this.redraw() ;
                return this.comp ;
            }

            this.isCompLoading=true;
            ld.addLoadingCB((comp)=>{
                if(view==null||view==undefined)
                    return null ;
                comp.comp_init(id,view);
                this.comp = comp ;
                this.isCompLoading=false;
                this.compInsChged=true ;
                this.redraw() ;
            });
            return null ;
        }

        
        
        protected onDivResize(x:number,y:number,w:number,h:number,b_notchg:boolean):void
        {
            //console.log("onDivResize - - ") ;
            if(this.comp==null)
                return ;
            if(b_notchg && !this.compInsChged)
                return ;
            //console.log("  chart resized") ;
            this.compInsChged=false;
            this.comp.comp_on_resize(w,h) ;
        }
  }
}