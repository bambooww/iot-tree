
module oc.di
{
    /**
     * 
     */
    export abstract class DIPts extends oc.DrawItem
    {
        color: string = 'yellow';
        lnW: number = 1;
        
        //bLn:boolean=false;//polygon default
        
        protected ptsPy: oc.base.Polygon  = new oc.base.Polygon();


        public constructor(opts: {} | undefined,bln:boolean=false)
        {
            super(opts);
            if (opts != undefined)
            {
               if(opts["pts"])
               {
                   //ptsPy
               }
            }
            //this.bLn = bln ;
        }


        static PNS_PTS = {
            _cat_name: "di_pts", _cat_title: "DI Pts",
            color: { title: "color", type: "str",edit_plug:"color" },
            lnW: { title: "Line Width", type: "int" },
            
            //fillR:{ title: "Fill Rotate", type: "int" },
            //b_ln: { title: "Ln Or Py", type: "bool", enum_val: [[false, "Polygon"], [true, "Line"]], bind: true},
            //lnPipeColor: { title: "Pipe Color", type: "str" },
        };

        public extract():base.Props<any>
		{
            var r = super.extract() ;
            r["pts_py"] = this.ptsPy.getPts() ;
            return r ;
        }

        public inject(opts:base.Props<any>,ignore_readonly:boolean|undefined)
		{
            super.inject(opts,ignore_readonly);
            var pts = opts["pts_py"] as base.Pt[] ;
            if(pts!=null&&pts!=undefined)
                this.ptsPy.setPts(pts);
        }


        private recalXY()
        {
            var r = this.ptsPy.getBoundingBox() ;
            if(r!=null)
            {
                this.x = r.x;
                this.y = r.y ;
            }
        }

        public setDrawXY(x:number,y:number)
        {
            var r = this.ptsPy.getBoundingBox() ;
            if(r==null)
                return
            this.recalXY();

            var dx = x - this.x ;
            var dy = y - this.y ;
            this.ptsPy.movePt(dx,dy) ;
            //this.recalXY();
            this.x = x ;
            this.y = y ;
            //super.setDrawXY()
            this.MODEL_fireChged(["x","y"]) ;
        }

        public addPt(x:number,y:number)
        {
            this.ptsPy.addPt(x,y) ;
            //this.ptsPy()
            this.recalXY();
        }

        public addPts(pts:oc.base.Pt[])
        {
            for(var pt of pts)
            {
                this.addPt(pt.x,pt.y);
            }
        }

        public getPts():oc.base.Pt[]
        {
            return this.ptsPy.getPts() ;
        }

        public chgPt(idx:number,x:number,y:number):boolean
        {
            if(idx<0||idx>=this.ptsPy.getPtNum())
                return false;
            this.ptsPy.chgPt(idx,x,y);
            this.recalXY();
            this.MODEL_fireChged([]) ;
            return true;
        }

        public chgLastPt(x:number,y:number):boolean
        {
            return this.chgPt(this.ptsPy.getPtNum()-1,x,y) ;
        }

        public chkPixelPtIdxOnPt(x:number,y:number):number|null
		{
			var c = this.getContainer();
			if(c==null)
                return null ;
            for (var i = 0 ; i < this.ptsPy.getPtNum() ; i ++)
            {
                var tmppt = this.ptsPy.getPt(i) ;
                if(tmppt==null)
                    continue ;
                var p = c.transDrawPt2PixelPt(tmppt.x,tmppt.y) ;
                if(util.chkPtInRadius(p.x,p.y,x,y,util.CTRL_PT_R))
                {
                    return i;
                }
            }
			
			return null ;
        }
        
        public setDrawPtIdx(idx:number,x:number,y:number)
		{
            if(idx<0||idx>=this.ptsPy.getPtNum())
                return ;
            this.ptsPy.chgPt(idx,x,y);
            this.recalXY();
            this.MODEL_fireChged([]) ;
		}

        public setLnW(w: number)
        {
            this.lnW = w;
        }

        public setLnColor(c: string)
        {
            this.color = c;
        }


        public getPropDefs()
        {
            var r = super.getPropDefs();
            r.push(DIPts.PNS_PTS);
            return r;
        }

        // public getBoundPolygonDraw()
        // {

        //     //return new oc.base.Polygon();
        //     return null;
        // }

        public getBoundPolygonDraw()
        {
            return this.ptsPy ;
        }

        
        public draw_sel(cxt:CanvasRenderingContext2D,c:IDrawItemContainer,color:string)
		{//sel draw 
			if(!this.ptsPy.isValid())
                return ;
			cxt.save();
			cxt.beginPath();
            cxt.strokeStyle=color;
            cxt.lineWidth=1 ;
			
			for (var tmppt of this.ptsPy.getPts())
            {
                var pt = c.transDrawPt2PixelPt(tmppt.x,tmppt.y) ;
				cxt.moveTo(pt.x, pt.y);
				cxt.arc(pt.x, pt.y, util.CTRL_PT_R, 0, Math.PI * 2, true);
            }
            cxt.stroke();
            var r = this.ptsPy.calculateBounds() ;
            if(r!=null)
            {
                var p1 = c.transDrawPt2PixelPt(r.x,r.y) ;
                var w = c.transDrawLen2PixelLen(true,r.w);
                var h = c.transDrawLen2PixelLen(false,r.h);
                oc.util.drawRectEmpty(cxt,p1.x,p1.y,w,h,null);
            }
            
			cxt.restore() ;
				
        }
        
    }
}


