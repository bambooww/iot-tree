module oc.iott
{
    export interface ITreeNode
    {
        hasSubNode(): boolean;

        getSubNode(): ITreeNode[]|null;

        getParentTN():UnitTN|null;

        setParentTN(p:UnitTN):void;

        //setChildTNS(c:UnitTN):void;
    }

    /**
     * Unit TreeNode
     * suport display node which has child node.and show like a tree
     * parent can expand or collapse all child node .
     */
    export class UnitTN extends Unit implements ITreeNode
    {
        public static UNIT_SUB:string="_oc_sub";
        public static UNIT_MEMBERS:string="_oc_members";

        private parentTN:UnitTN|null=null;

        private subTNs: ITreeNode[] = [];
        private expandTN: oc.di.DIBasic | null = null;
        private expandR = 10;
        private expanded: boolean = true;
        
        private members:Member[]=[];
        //private bShowMember:boolean=false;

        /**
         * only one unittn can show member
         */
        public static curShowMemberUnit:UnitTN|null = null ;

        private showMemberRadius:number=0;

        public constructor(opts: {} | undefined)
        {
            super(opts);
        }

        public getClassName()
        {
            return "oc.iott.UnitTN";
        }

        static UNITTN_PNS = {
            _cat_name: "unit_tn", _cat_title: "IOTT Unit TreeNode",
            expanded: { title: "Expanded", type: "bool", enum_val: [[true, "Expand"], [false, "Collapse"]] },
            //show_member: { title: "Show Member", type: "bool", enum_val: [[true, "Show"], [false, "Hidden"]] },
        };

        public getPropDefs(): Array<any>
        {
            var r = super.getPropDefs();
            r.push(UnitTN.UNITTN_PNS);
            return r;
        }

        /**
         * override it to make not expanded sub nodes move together
         * @param x 
         * @param y 
         */
        public setDrawXY(x:number,y:number)
		{
            var subns = this.listDescendantsNodesDI(false);
            if(subns.length>0)
            {
                var oldpt = this.getDrawXY();
                var dx = x-oldpt.x ;
                var dy = y-oldpt.y ;
                for(var subn of subns)
                {
                    oldpt = subn.getDrawXY() ;
                    //subn.setDrawXY(oldpt.x+dx,oldpt.y+dy) ;
                    subn.x = oldpt.x+dx;
                    subn.y = oldpt.y+dy;
                }
            }
            super.setDrawXY(x,y) ;
        }

        public listDescendantsNodesDI(bexpanded:boolean|null):DrawItem[]
        {
            var r:DrawItem[]=[] ;
            var subns = this.getSubNode();
            if(subns==null)
                return r ;
            for(var subn of subns)
            {
                if(!(subn instanceof DrawItem))
                    continue ;
                if(bexpanded==null || (bexpanded && !subn.isHidden()) || (!bexpanded && subn.isHidden()))
                {
                    r.push(subn) ;
                    if(subn instanceof UnitTN)
                    {
                        var subr = subn.listDescendantsNodesDI(bexpanded) ;
                        for(var tmpn of subr)
                            r.push(tmpn) ;
                    }
                }
            }
            return r;
        }

        public hasSubNode(): boolean
        {
            return this.subTNs!=null&&this.subTNs.length>0;
        }

        public getSubNode(): ITreeNode[]|null
        {
            return this.subTNs;
        }

        public getParentTN():UnitTN|null
        {
            return this.parentTN;
        }

        public setParentTN(p:UnitTN):void
        {
            this.parentTN = p;
        }

        public setChildTNS(c:ITreeNode)
        {
            c.setParentTN(this) ;
            this.subTNs.push(c);
        }

        public getMembers()
        {
            return this.members ;
        }

        public setMember(m:Member):void
        {
            m.setBelongTo(this) ;
            this.members.push(m) ;
        }

        // public isShowMember():boolean
        // {
        //     return this.bShowMember;
        // }

        // public setShowMember(b:boolean)
        // {
        //     this.bShowMember = b ;
        // }

        public isHidden():boolean
		{
            if(this.parentTN==null)
                return false;
            if(this.parentTN.isHidden())
                return true;
            return !this.parentTN.expanded;
		}

        public isSubExpanded()
        {
            return this.expanded ;
        }

        /**
         * 
         * @param pos 0-right 1-left
         */
        public getPrimRect(): base.Rect | null
        {
            //this.getDrawSize();
            //var u = this.getUnit();
            var r = super.getPrimRect();
            if (r == null)
                return null;

            //if (!this.hasChildTN())
            //    return r;

            var c = this.getContainer();
            if (c == null)
                return r;
            if(this.hasSubNode())
                r.w += c.transDrawLen2PixelLen(true, this.expandR * 2);
            return r;
        }

        private getExpandItem(): oc.di.DIBasic | null
        {
            var w = super.getW();
            var h = super.getH();
            if (this.expandTN != null)
            {
                if (this.expanded)
                    this.expandTN.setTp("circle-");
                else
                    this.expandTN.setTp("circle+");
                this.expandTN.setDrawXY(this.x + w, this.y + (h / 2 - this.expandR));
                return this.expandTN;
            }

            //var r = super.getBoundRectDraw();
            //if(r==null)
            //    return null ;

            var v = new oc.di.DIBasic({ tp: "circle+" });
            v.setLnW(2);
            v.setDrawSize(this.expandR * 2, this.expandR * 2);

            this.expandTN = v;
            if (this.expanded)
                v.setTp("circle-");
            else
                v.setTp("circle+");
            this.expandTN.setDrawXY(this.x + w, this.y + (h / 2 - this.expandR));
            return v;
        }


        public getW(): number
        {
            var w = super.getW();
            if (this.hasSubNode())
                return w + this.expandR * 2;
            else
                return w;
        }

        
        public on_mouse_event(tp: MOUSE_EVT_TP, pxy: oc.base.Pt, dxy: oc.base.Pt,e:_MouseEvent)
        {
            super.on_mouse_event(tp,pxy,dxy,e);

            //chk if mouse clk on expand or not
            if(tp==MOUSE_EVT_TP.Clk)
            {
                var w = super.getW();
                var y = this.getH()/2+this.y;
                if(dxy.x>this.x +w && dxy.x<this.x+w+this.expandR * 2 && dxy.y>y-this.expandR&&dxy.y<y+this.expandR)
                {//
                    //if(this.before_expand_chg())
                    this.expanded = !this.expanded;
                    this.MODEL_fireChged(["expanded"])
                }
            }
            else if(tp==MOUSE_EVT_TP.DownLong)
            {
                //console.log(this.getUnitName(),this.getTitle(),"down long") ;
                UnitTN.curShowMemberUnit = this ;
                //this.setShowMember(true);
                this.MODEL_fireChged(["member"])
            }
            
        }

        public on_selected(b:boolean)
        {
            if(!b)
            {//unselected
                for(var m of this.getMembers())
                {
                    if(m.getSelState().selected)
                        return ;
                }
                UnitTN.curShowMemberUnit = null ;
                //this.setShowMember(false);
            }
        }
        /**
         * called by panel,e.g when mouse down
         * @param pxy 
         * @param dxy 
         */
        private static chkAndHiddenMembers0(pxy: oc.base.Pt, dxy: oc.base.Pt):boolean
        {
            var un = UnitTN.curShowMemberUnit ;
            if(un==null)
                return false;
        
            if(un.showMemberRadius<=0)
            {
                UnitTN.curShowMemberUnit = null ;
                return true;
            }
            var r = un.getBoundRectDraw() ;
            if(r==null)
                return false;
            
            var cxy = r.getCenter() ;
            var dx = cxy.x-dxy.x ;
            var dy = cxy.y-dxy.y ;

            if(dx*dx+dy*dy<un.showMemberRadius*un.showMemberRadius)
                return false;//in circle
            UnitTN.curShowMemberUnit = null ;
            return true;
        }

        public getShowMemberRadius():number|null
        {
            if(UnitTN.curShowMemberUnit==null)
                return null;
            
            if(UnitTN.curShowMemberUnit!=this)
                return null;

            return this.calcShowMemberRadius();
        }

        public calcShowMemberRadius():number|null
        {
            var r = this.getBoundRectDraw() ;
            if(r==null)
                return null;
            
            //if(!this.isShowMember())
            //    return null;
            return (r.w>r.h?r.w:r.h)*2 ;
        }
        
        private displayMembers(ctx: CanvasRenderingContext2D, c: IDrawItemContainer)
        {
            var rad = this.getShowMemberRadius();
            if(rad==null)
                return ;

            var ms = this.getMembers() ;
            //if(ms==null||ms.length<=0)
            //    return ;

            var r = this.getBoundRectDraw() ;
            if(r==null)
                return ;
            
            this.showMemberRadius = rad;
            ctx.beginPath();
            ctx.strokeStyle = "rgba(100,100,100,0.7)";
            ctx.lineWidth = 2;

            var cxy = r.getCenter() ;
            cxy = c.transDrawPt2PixelPt(cxy.x,cxy.y) ;
            
            rad = c.transDrawLen2PixelLen(true,this.showMemberRadius) ;
            ctx.arc(cxy.x, cxy.y, rad, 0, Math.PI*2);
            ctx.stroke();
            ctx.fillStyle = "rgba(100,100,100,0.7)";
            ctx.closePath();
            ctx.fill();
        }

        /**
         * override it ,to support expand display area when show members
         * and hide member circle when not be selected
         * @param x 
         * @param y 
         */
        public containDrawPt(x:number, y:number):boolean
		{
            var un = UnitTN.curShowMemberUnit ;
            
            if(un==null || un!=this)
                return super.containDrawPt(x,y);
        
            var r = un.getBoundRectDraw() ;
            if(r==null || un.showMemberRadius<=0)
                return super.containDrawPt(x,y);
            
            var cxy = r.getCenter() ;
            var dx = cxy.x-x ;
            var dy = cxy.y-y ;

            if(dx*dx+dy*dy<un.showMemberRadius*un.showMemberRadius)
                return true;
            return false;
        }

        public draw(cxt: CanvasRenderingContext2D, c: IDrawItemContainer)
        {
            //super.draw_sel()
            super.draw(cxt, c);

            if (this.hasSubNode())
            {
                var ei = this.getExpandItem();
                if (ei != null)
                    ei.draw(cxt, c);
            }
            
            this.displayMembers(cxt, c) ;
        }

        private before_expand_chg():boolean
        {//do something
            var du = this.getUnit();
            var lay = this.getLayer();
            if(du==null||lay==null)
                return false;
            var expandu = du.getInsExpandUrl()
            if(expandu==null||expandu=='')
                return false;
            var pm={};
            pm["layer_name"]=lay.getName();
            pm["unit_name"]=du.getName();
            pm["uins_id"]=this.getId();
            $.ajax({
                type: 'post',
                url:expandu,
                data: pm,
                async: true,
                success: (result:string)=>{
                    result = result.trim();
                    if(result.indexOf("{")!=0)
                    {//
                        oc.util.prompt_err(result);
                        return ;
                    }
                    var ob:any ;
                    eval("ob="+result);
                    var insid = ob["unit_ins_id"] as string ;
                    
                }
            });
            return true;
        }



    }
}