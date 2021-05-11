

namespace oc.hmi
{
    
    export type HMISubItem={id:string,lay:string,nodepath:string};

    type HMISubLoaded=(ret:HMISubItem|null,lay:DrawLayer|null)=>void;

    //export type DIDivCB=(icomp:IDIDivComp)=>void;

    // /**
    //  * 
    //  */
    // class HMISubLoader
    // {
    //     static LOADED_JS={} ;

    //     hmiSubId:string;
    //     subItem:HMISubItem|null=null;
    //     loadOk:boolean=false;

    //     //loading listeners,
    //     loadingCB:HMISubLoaded[]=[];

    //     constructor(subid:string)
    //     {
    //         this.hmiSubId = subid ;
            
    //     }

    //     public getHmiSubId()
    //     {
    //         return this.hmiSubId ;
    //     }

    //     public addLoadingCB(cb:HMISubLoaded)
    //     {
    //         this.loadingCB.push(cb) ;
    //     }

    //     private fireLoadOk()
    //     {
    //         if(this.loadingCB.length==0)
    //             return ;
           
    //         for(var ldcb of this.loadingCB)
    //         {
    //             ldcb(this.subItem) ;
    //         }
    //         this.loadingCB=[];
    //     }

    //     doLoad()
    //     {
    //         var url = "/hmi_ajax.jsp?cxtid=&id" ;
    //         //load comp first
    //         oc.util.doAjax(url,{},(bsucc,ret)=>{
    //             this.layer = new oc.DrawLayer({});
    //             this.layer.inject(ret,null) ;
    //             this.fireLoadOk();
    //         });
            
    //     }


    //     isLoadOk():boolean
    //     {
    //         return this.loadOk ;
    //     }


    // }

    class HMISubPanel0 extends AbstractDrawPanel
    {
        
        subDrawLayer:DrawLayer|null=null ;
        hmiSub:HMISub ;

        constructor(sub:HMISub)
        {
            super();
            this.hmiSub = sub ;
        }

        public setSubDrawLayer(dl:DrawLayer|null)
        {
            if(dl!=null)
                this.addLayer(dl) ;
            this.subDrawLayer = dl ;
            // if(this.subDrawLayer!=null)
            //     this.subDrawLayer.setPanel(this) ;
        }

        public update_draw(): void
        {
            if(this.subDrawLayer==null)
                return ;
            var ele = this.hmiSub.getContEle();
            if(ele==null)
                return ;
            //    this.subDrawLayer.ajustDrawFit() ;
            this.subDrawLayer.update_draw() ;
        }

        public clear_draw(): void
        {
            
        }
        public setPixelSize(sz: base.Size): void
        {
            
        }
        public getPixelSize(): base.Size
        {
            // var r = this.hmiSub.getBoundRectPixel() ;
            // if(r==null)
            //     return {w:100,h:100};

            // return {w:r.w,h:r.h} ;
            var ps = this.hmiSub.getPixelSize() ;
            if(ps==null)
                return {w:100,h:100};
            return ps ;
        }

        // public ajustDrawFitInRect(rect: oc.base.Rect)
		// {
		// 	this.drawCenter = rect.getCenter();
		// 	var ps = this.getPixelSize();
		// 	var res = rect.w / ps.w;
		// 	var res2 = rect.h / ps.h;
		// 	this.drawResolution = 1;//Math.max(res, res2);
		// 	this.update_draw();
		// }
        
        public getHTMLElement(): HTMLElement
        {
            var ele = this.hmiSub.getContEle();
            if(ele==null)
                throw new Error("no html element") ;
            return ele[0] ;
        }

        public delPopMenu(): void
        {
        }
        public setPopMenu(menuele: JQuery<HTMLElement>): void
        {
        }
        public getDrawView(): DrawView | null
        {
            return null ;
        }
        public getInteract(): DrawInteract | null
        {
            return null ;
        }
    }

    /**
     * for hmi edit and display
     */
     export class HMISubPanel extends DrawPanel
     {
        
         public constructor(id:string,target: string|HTMLElement, opts: {})
         {
            super(target, opts);
            
			
			//var ele = this.getHTMLElement();
			//ele[DrawPanelDiv.DRAW_PANEL_DIV] = this;
		}

        public setDrawLayer(dl:DrawLayer)
        {
            this.addLayer(dl) ;
        }

     }

     class HMISubView extends DrawView
     {
        constructor(model:DrawModel,panel:AbstractDrawPanel)//,ctrl:DrawCtrl) repid:string,hmiid:string,
        {
            super(model,panel);
        }

         public getInteract(): DrawInteract|null
         {
             return null ;
         }

         protected sendMsgToServer(msg: string): void
         {
             
         }
         
     }

    class HMISubLoader
    {
        hmipath:string;
        sub_id:string;

        loaders:HMISubLoaded[]=[];

        loadedSubItem:HMISubItem|null=null ;

        //layTxt:string|null=null;

        constructor(hmipath:string,subid:string)
        {
            this.hmipath = hmipath ;
            this.sub_id = subid ;
        }

        public getOrLoadSubItem(cb:HMISubLoaded)
        {
            if(this.loadedSubItem!=null)
            {
                var dl = new DrawLayer({}) ;
                dl.inject(this.loadedSubItem.lay,null);
                cb(this.loadedSubItem,dl);
                return ;
            }
            this.loaders.push(cb) ;
            if(this.loaders.length==1)
            {
                this.doLoadAjax();
            }
        }

        private doLoadAjax()
        {
            oc.util.doAjax("/hmi_ajax.jsp",{tp:"sub",hmi_path:this.hmipath,sub_id:this.sub_id},(bsucc,ret)=>{
                if(bsucc)
                {
                    //console.log(hmipath+" "+sub_id+" - get load lay ajax") ;
                    var str = ret as string;
                    var k = str.indexOf("\r\n");
                    if(k<=0)
                        return ;
                    var path = str.substr(0,k) ;
                    var laytxt = str.substr(k+2) ;
                    this.loadedSubItem = {id:this.sub_id,lay:laytxt,nodepath:path} ;
                    
                    for(var ld of this.loaders)
                    {
                        var dl = new DrawLayer({}) ;
                        dl.inject(this.loadedSubItem.lay,null);
                        ld(this.loadedSubItem,dl);
                    }
                }
            });
        }
    }
    /**
     * in iottree context ,hmi may reference sub node hmis.
     * it implements like a panel,but it's a drawitem
     * 1)it use draw res to load draw contents
     * 2)it use div and canvas to draw itself
     * 3)simple interact
     * 4)inner draw item need not deep copy
     * 5)dyn data show is specially orginized
     */
    export class HMISub extends DrawDiv implements IItemsLister
    {
        
        private static id2layer: { [id: string]: HMISubItem|"" } = {};
        private static key2loader: { [id: string]: HMISubLoader|"" } = {};
        


        public static getOrLoadSubItem(hmipath:string,sub_id:string,cb:HMISubLoaded):void
        {
            var k = hmipath+'-'+sub_id ;
            var subloader = HMISub.key2loader[k] ;
            if(subloader==null||subloader==undefined)
            {
                subloader = new HMISubLoader(hmipath,sub_id) ;
            }
            if(subloader=="")
                return ;
            subloader.getOrLoadSubItem(cb) ;
            
            // var u = HMISub.id2layer[sub_id];
            // if(u=="")
            // {
            //     cb(null,null) ;
            //     return ;
            // }
            // else if(u!=null&&u!=undefined)
            // {
            //     console.log(hmipath+" "+sub_id+" - get load lay cached") ;
            //     var dl = new DrawLayer({}) ;
            //     dl.inject(u.lay,null);
            //     cb(u,dl);
            //     return ;
            // }

            // oc.util.doAjax("/hmi_ajax.jsp",{tp:"sub",hmi_path:hmipath,sub_id:sub_id},(bsucc,ret)=>{
            //     if(bsucc)
            //     {
            //         console.log(hmipath+" "+sub_id+" - get load lay ajax") ;
            //         var str = ret as string;
            //         var k = str.indexOf("\r\n");
            //         if(k<=0)
            //             return ;
            //         var path = str.substr(0,k) ;
            //         var laytxt = str.substr(k+2) ;
            //         u = {id:sub_id,lay:laytxt,nodepath:path} ;
            //         HMISub.id2layer[sub_id] = u;

            //         var dl = new DrawLayer({}) ;
            //         dl.inject(laytxt,null);
            //         cb(u,dl);
            //     }
            // });
        }

        //private bMin:boolean=true;

        sub_id:string="" ;
        
        private subDrawLayer:DrawLayer|null = null;

        private subPanel:HMISubPanel|null=null;
        //private panelDiv:DrawPanelDiv|null=null ;

        private subModel:HMIModel|null=null;
        private subView:HMISubView|null=null;

        private innerCont: ItemsContainer | null = null;

        
        public constructor(opts: {} | undefined)
        {
            super(opts);
        }
        
        public getClassName()
        {
            return "oc.hmi.HMISub";
        }

        static PNS = {
			_cat_name: "hmisub", _cat_title: "Sub Hmi",
            sub_id: { title: "Sub Hmi Id", type: "str",readonly:true}
		};

		public getPropDefs(): Array<any>
		{
			var r = super.getPropDefs();
			r.push(HMISub.PNS);
			return r;
        }

        private setHmiItem(hmiitem:HMISubItem,lay:DrawLayer)
        {
            this.subDrawLayer = lay ;
            var hmipath = hmiitem.nodepath ;
            this.subModel = new HMIModel({
                temp_url:"hmi_editor_ajax.jsp?op=load&path="+hmipath,
                comp_url:"comp_ajax.jsp?op=comp_load",
                dyn_url:"",
                hmi_path:hmipath
            });

            var p = this.getSubPanel();
            if(p!=null)
            {
                this.subView = new HMISubView(this.subModel,p);
                p.setDrawLayer(this.subDrawLayer) ;
            }
        }

        private getSubPanel():HMISubPanel|null
        {
            if(this.subPanel!=null)
            {
                console.log("get sub panel - eleid="+this.subPanel.getHTMLElement().id);
                return this.subPanel ;
            }
                
            var ele = this.getContEle() ;
            if(ele==null)
                return null ;
            
            this.subPanel = new oc.hmi.HMISubPanel("id",ele[0],{});
            console.log("get sub panel - eleid="+this.subPanel.getHTMLElement().id);
            return this.subPanel ;
        }

        public getHmiSubId():string|null
        {
            var subid = this["sub_id"] ;
            if(subid==null||subid==undefined)
            {
                return null;
            }
            return subid ;
        }

        public setHmiSubId(subid:string)
        {
            if(subid==this.getHmiSubId())
                return ;
            this["sub_id"] = subid;
            this.reloadSubHmi();
            //this.MODEL_fireChged(["sub_id"]);
        }


        public on_container_set()
        {
            super.on_container_set() ;
            this.reloadSubHmi();
        }

        private updateInner()
        {
            if(this.subDrawLayer!=null)
            {
                this.subDrawLayer.ajustDrawFit();
            }
        }

        protected onDivResize(x:number,y:number,w:number,h:number,b_notchg:boolean):void
        {
            //super.onDivResize() ;
            if(b_notchg)
                return;
            this.getSubPanel()?.updatePixelSize();
            //this.redraw();
            this.updateInner();
        }

        public reloadSubHmi()
        {
            var subid = this.getHmiSubId();
            if(subid==null)
                return ;
            var m = this.getModel() as HMIModel ;
            if(m==null)
                return;
            var hmipath = m.getHmiPath() ;
            var p = this.getSubPanel() ;
            console.log(hmipath,subid) ;
            HMISub.getOrLoadSubItem(hmipath,subid,(subitem,lay)=>{
                if(subitem==null||lay==null)
                    return ;
               
                this.setHmiItem(subitem,lay);
                this.redraw() ;
            }) ;
            
        }

        private static LEFTTOP_R = 30 ;

        public on_mouse_event(tp: MOUSE_EVT_TP, pxy: oc.base.Pt, dxy: oc.base.Pt,e:_MouseEvent)
        {
            if(tp==MOUSE_EVT_TP.Down)
            {
                if(e.button==MOUSE_BTN.LEFT)
				{
                    var xy = this.getDrawXY() ;
                    var dx = dxy.x - xy.x ;
                    var dy = dxy.y - xy.y ;
                    if(dx>0&&dy>0&&dx<HMISub.LEFTTOP_R&&dy<HMISub.LEFTTOP_R)
                    {//click top left ctrl
                        //this.bMin = !this.bMin;
                        //this.MODEL_fireChged([]);
                    }
                }
			}
        }

        
        // public getPrimRect(): base.Rect | null
        // {
        //     var ic = this.getInnerContainer();
        //     if (ic == null)
        //         return new oc.base.Rect(0, 0, this.getW(), this.getH());
        //     // return ic.getItemsRectInner();
            
        //     var r = ItemsContainer.calcRect(this.getItemsShow());
        //     if (r == null)
        //         return null;
        //     //var p = ic.transDrawPt2PixelPt(r.x, r.y);
        //     var w = ic.transDrawLen2PixelLen(true, r.w);
        //     var h = ic.transDrawLen2PixelLen(false, r.h);

        //     return new oc.base.Rect(0, 0, w, h);
        //     //return r ;
        // }

        public drawPrim(ctx: CanvasRenderingContext2D): void
        {
            this.displayDivEle();

            this.updateInner();

            if(this.subDrawLayer!=null)
            {
                var cxt = this.subDrawLayer.getCxtFront() ;
                if(this.border>0)
                {// override to draw border
                    var psz = this.getPixelSize() ;
                    if(psz!=null)
                        oc.util.drawRect(cxt, 0,0,psz.w,psz.h, null, null, this.border, this.border_color);
                }
            }
			    
        }

        private getInnerContainer(): ItemsContainer | null
        {
            if (this.innerCont != null)
                return this.innerCont;

            var pc = this.getContainer();
            if (pc == null)
                return null;
            this.innerCont = new ItemsContainer(this, pc, this);
            return this.innerCont;
        }

        getItemsShow(): DrawItem[]
        {
            if(this.subDrawLayer==null)
                return [] ;
            return this.subDrawLayer.getItemsAll();
        }
        removeItem(item: DrawItem): boolean
        {
            return false;//not support
        }

    }


}