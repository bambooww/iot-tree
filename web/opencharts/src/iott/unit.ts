/**
 * working unit
 * create by drawitems,which can has many name prop and can control dyn.
 */
module oc.iott
{
    

    export class Unit extends oc.DrawUnitIns// implements IPopMenuProvider
    {
        private static TEMPNAME2CN:oc.base.Props<string>|null = null ;

        private static getTempName2CN():oc.base.Props<string>
        {
            if(Unit.TEMPNAME2CN!=null)
                return Unit.TEMPNAME2CN ;
            var v:oc.base.Props<string> = {} ;
            v["rep"]="oc.iott.UnitTNRep";
            v["ch"]="oc.iott.UnitTNCh";
            v["dev"]="oc.iott.UnitTNDev";
            v["conn"]="oc.iott.UnitTNConn";
            v["hmi"]="oc.iott.UnitTNHmi";
            v["store"]="oc.iott.UnitTNStore";
            Unit.TEMPNAME2CN = v ;
            return v ;
        }

        public static matchTempName2CN(tempn:string):string|null
        {
            var r = Unit.getTempName2CN()[tempn] ;
            if(r==undefined||r==null||r=="")
                return null ;
            return r ;
        }


        private actionEle: JQuery<HTMLElement> | null = null;
        private menuEle: JQuery<HTMLElement> | null = null;
        private bShowAction: boolean = false;

       
        //menuBtn:
        public constructor(opts: {} | undefined)
        {
            super(opts);
            //this.title = "unit";
        }
        
        public getClassName()
        {
            return "oc.iott.Unit";
        }

        
        
        public getActionTypeName():string
        {//override to provider item to has popmenu
            var u = this.getUnit()
            if(u==null)
                return "";
			return "unit-"+u.getName() ;
		}

        // on_right_menu(op:string,pxy:oc.base.Pt,dxy:oc.base.Pt):boolean
        // {
        //     var u = this.getUnit()
        //     if(u==null)
        //         return true;
        //     var actitem = u.getUnitActItem(op);
        //     if(actitem==null)
        //         return true;
            
        //     actitem.action(this,op,pxy,dxy) ;//do action
        //     return false;
        // }

        public on_mouse_event(tp: MOUSE_EVT_TP, pxy: oc.base.Pt, dxy: oc.base.Pt,e:_MouseEvent)
        {
            if (tp == MOUSE_EVT_TP.Clk)
            {
                this.bShowAction = !this.bShowAction;
                if (!this.bShowAction)
                    this.hideActionEle();
                else
                    this.MODEL_fireChged([]);
                return ;
            }
           
            super.on_mouse_event(tp, pxy, dxy,e) ;
            
        }

        public on_before_del():boolean
		{
			return false;
		}

        private hideActionEle()
        {
            if (this.actionEle == null)
                return;
            var p = this.getPanel();
            if (p == null)
                return;

            this.actionEle.remove();
            this.actionEle = null;
            this.bShowAction = false;
        }

        // protected getUnitExtVal(pn:string,defaultval:any):any
        // {
        //     var ext = this.getUnitExtProps();
        //     if(ext==null)
        //         return defaultval;
        //     var v = ext[pn];
        //     if(v==undefined||v==null)
        //         return defaultval;
        //     return v;
        // }

        private showActionView()
        {
            var acttn = this.getActionTypeName() ;
            if(acttn==null||acttn=="")
                return ;
            
            var actitem = Unit.getActionItem(acttn) ;
            if(actitem==null)
                return ;
            var ajaxurl = actitem.ajax_url ;
            if(ajaxurl==undefined||ajaxurl==null||ajaxurl=="")
                return ;

            var pm={};
            pm["unit_id"]=this.getId();
            oc.util.doAjax(ajaxurl,pm,(bsucc,ret)=>{
                $("#c_"+this.getId()).html(ret as string);
            });
        }

        private displayActionEle()
        {
            var acttn = this.getActionTypeName() ;
            if(acttn==null||acttn=="")
                return ;
            
            var actitem = Unit.getActionItem(acttn) ;
            if(actitem==null)
                return ;
            var p = this.getPanel();
            if (p == null)
                return;
            var pxy = this.getPixelXY();
            var s = this.getPixelSize();
            if (pxy == null || s == null)
                return;

            var act_w = actitem.width ;
            var act_h = actitem.height;
            var act_pos = actitem.pos ;
            var ele = p.getHTMLElement();
            if (this.actionEle == null)
            {
                var tmpid = this.getId();
            //     this.actionEle = $(`<div id="act_${tmpid}" class="oc_unit_action">
            //     <div class="close">
            //         <a href="javascript:oc.iott.Unit.refreshUnitAction('${tmpid}')"><i class="fa fa fa-refresh"></i></a>    
            //         <a href="javascript:oc.iott.Unit.closeUnitAction('${tmpid}')"><i class="fa fa-times-circle"></i></a>
            //     </div>
            //     <div id="c_${tmpid}" class="content"></div>
            // </div>`);

            this.actionEle = $(`<div id="act_${tmpid}" class="oc_unit_action">
                <div id="c_${tmpid}" class="content"></div>
            </div>`);

                
                this.actionEle.css("width",act_w+"px");
                this.actionEle.css("height",act_h+"px");
                this.actionEle.get(0)["_oc_unit"] = this;

                $(ele).append(this.actionEle);

                this.showActionView();
            }

            
            switch (act_pos)
            {
                case 1://right
                    this.actionEle.css("left", (pxy.x + s.w) + "px");
                    this.actionEle.css("top", pxy.y + "px");
                    break;
                case 2://left
                    this.actionEle.css("left", (pxy.x - act_w) + "px");
                    this.actionEle.css("top", pxy.y + "px");
                    break;
                case 3://top
                    this.actionEle.css("left", pxy.x + "px");
                    this.actionEle.css("top", (pxy.y - act_h) + "px");
                    break;
                default://0-bottom
                    this.actionEle.css("left", pxy.x + "px");
                    this.actionEle.css("top", (pxy.y + s.h) + "px");
                    break;
            }

        }

        public draw(cxt: CanvasRenderingContext2D, c: IDrawItemContainer)
        {
            super.draw(cxt, c);

            //console.log(this.bShowAction+" "+this.isSelected());
            if (this.bShowAction)
            {
                this.displayActionEle();
            }
            // else
            // {
            //     this.hideActionEle();
            //     if(!this.isSelected())
            //         this.bShowAction=false;
            // }

            
        }

        /**
         * used by action div to close self
         * @param id
         */
        public static closeUnitAction(id: string)
        {
            var ob = $("#act_" + id);
            if (ob == null || ob == undefined)
                return;
            var u = ob.get(0)["_oc_unit"] as Unit;
            if (u != undefined && u != null)
                u.hideActionEle();
        }

        public static refreshUnitAction(id:string)
        {
            var ob = $("#" + id);
            if (ob == null || ob == undefined)
                return;
            var u = ob.get(0)["_oc_unit"] as Unit;
            if (u != undefined && u != null)
                u.showActionView();
        }

        private static tp2items:{[tpname: string]: UnitActionItem}|null=null;

        public static setActionTp2Item(tp2items:{[tpname: string]: UnitActionItem})
		{
			Unit.tp2items = tp2items ;
        }
        
        private static getActionItem(tpname:string):UnitActionItem|null
        {
            if(Unit.tp2items==null)
                return null ;
            var r = Unit.tp2items[tpname];
            if(r==undefined)
                return null ;
            return r ;
        }
    }

	export type UnitActionItem={ajax_url:string,width:number,height:number,pos:number,refresh_interval:number};

}