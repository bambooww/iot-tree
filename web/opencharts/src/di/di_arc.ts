
module oc.di
{
	export class DIArc extends oc.DrawItemRectR
	{
        fillColor: string | null = null;
        border:number=1;
        //radius: number | null = null;
        color:string="yellow";
        startAngle:number=0.0;
        endAngle:number=Math.PI*1.5;

		public constructor(opts:{}|undefined)
		{
			super(opts);
		}


		static PNS = {
			_cat_name: "di_arc", _cat_title: "DI Arc",

            fillColor: { title: "fillColor", type: "str", edit_plug:"color"},
            border:{ title: "border", type: "int" },
            color: { title: "color", type: "color",edit_plug:"color"},
            //radius: { title: "radius", type: "float" },
            startAngle: { title: "StartAngle", type: "float" },
            endAngle: { title: "EndAngle", type: "float" }
		};



		public getClassName()
		{
			return "DIArc";
		}

        public getWHRatio():number
		{//w = h
			return 1 ;
        }
        
		public getPropDefs(): Array<any>
		{
			var r = super.getPropDefs();
			r.push(DIArc.PNS);
			return r;
		}

		// public getBoundRectDraw()
		// {
		// 	var pt = this.getDrawXY();
		// 	return new oc.base.Rect(pt.x, pt.y, this.w, this.h);
		// }

        public chkPtOnCtrl(pxy:base.Pt,dxy:base.Pt):string|null
		{
			var c = this.getContainer();
			if(c==null)
				return null ;
            var r = c.transPixelLen2DrawLen(true,util.CTRL_PT_R);
            
            //var dr = this.w/2;//c.transDrawLen2PixelLen(true,this.w/2);
            var startpt = this.calDrawPtByAngle(this.startAngle);
            var endpt = this.calDrawPtByAngle(this.endAngle);
            
			if(util.chkPtInRadius(startpt.x,startpt.y,dxy.x,dxy.y,r))
				return "s";
            if(util.chkPtInRadius(endpt.x,endpt.y,dxy.x,dxy.y,r))
                return "e";
            //chk arc which can change radius
            var rectp = this.getBoundRectPixel();
            if(rectp==null)
                return null ;
            var cp = rectp.getCenter() ;
            var tmpv = Math.sqrt((pxy.x-cp.x)*(pxy.x-cp.x)+(pxy.y-cp.y)*(pxy.y-cp.y));
            tmpv = Math.abs(tmpv - rectp.w/2);
            if(tmpv<=util.CTRL_PT_R)//util.CTRL_PT_R*util.CTRL_PT_R)
                return "c";

			return null ;
        }
        
        
        // /**
        //  * based on center pt,using draw pt to calculate angle
        //  * @param x 
        //  * @param y 
        //  */
        // private calArcAngleByDrawPt(x:number,y:number)
        // {
        //     var pt = this.getDrawXY();
        //     var centerx = pt.x+this.w/2 ;
        //     var centery = pt.y+this.h/2 ;
        //     var dx = x-centerx ;
        //     var dy = y-centery ;
        //     if(dx==0)
        //     {
        //         if(dy>=0)
        //             return Math.PI*0.5 ;
        //         else
        //             return Math.PI*1.5 ;
        //     }
        //     var r = Math.atan(dy/dx);
        //     if(dx>0)
        //     {
        //         if(r>=0)
        //             return r;
        //         else
        //             return Math.PI*2+r;
        //     }
        //     else
        //     {
        //         if(r>=0)
        //             return r+Math.PI;
        //         else
        //             return Math.PI+r;
        //     }
        // }
		
        public setCtrlDrawPt(ctrlpt:string|null,x:number,y:number)
        {
            if("s"==ctrlpt)
            {
                this.startAngle = this.calArcAngleByDrawPt(x,y);
                this.MODEL_fireChged(["startAngle"]);
            }
            else if("e"==ctrlpt)
            {
                this.endAngle = this.calArcAngleByDrawPt(x,y);
                this.MODEL_fireChged(["endAngle"]);
            }
            else if("c"==ctrlpt)
            {//chg radius
                var w = this.getW();
                var h = this.getH() ;
                var pt = this.getDrawXY();
                var cx = pt.x+w/2 ;
                var cy = pt.y+h/2;
                var dx = cx-x ;
                var dy = cy-y;
                var nr = Math.sqrt(dy*dy+dx*dx);
                this.setDrawSize(nr*2,nr*2)
                this.x = cx-nr ;
                this.y = cy-nr;
                this.MODEL_fireChged(["w","h","x","y"]);
            }
        }

        public getPrimRect(): base.Rect | null
        {
            return new oc.base.Rect(0,0,100,100);
        }

        private calPtByAngle(ang:number)
        {
            var r = 50;
            var px = 50+Math.cos(ang)*r;
            var py = 50+Math.sin(ang)*r;
            return {x:px,y:py};
        }

        public drawPrim(cxt: CanvasRenderingContext2D): void
        {
            cxt.save();
            cxt.strokeStyle=this.color;
            cxt.lineWidth =this.border||1 ;
            cxt.beginPath();
            //cxt.moveTo(startx,starty);
            cxt.arc(50, 50, 50, this.startAngle, this.endAngle);
            if(this.fillColor!=null&&this.fillColor!="")
            {
                cxt.fillStyle=this.fillColor;
                cxt.fill();
                //if(this.border)
                var startpt = this.calPtByAngle(this.startAngle);
                var endpt = this.calPtByAngle(this.endAngle);
                cxt.moveTo(startpt.x,startpt.y);
                cxt.lineTo(endpt.x,endpt.y);
            }
            //else
            {
                cxt.stroke();
            }
            
            cxt.restore();
        }
        public drawPrimSel(ctx: CanvasRenderingContext2D): void
        {
            
        }

        private calDrawPtByAngle(ang:number)
        {
            var w = this.getW();
            var h = this.getH();
            var pt = this.getDrawXY();
            var centerx = pt.x+w/2 ;
            var centery = pt.y+h/2 ;
            var r = w/2;
            var px = centerx+Math.cos(ang)*r;
            var py = centery+Math.sin(ang)*r;
            return {x:px,y:py};
        }

		// public draw0(cxt:CanvasRenderingContext2D,c:IDrawItemContainer)
		// {
		// 	var pt = this.getDrawXY();
		// 	var pcenter = c.transDrawPt2PixelPt(pt.x+this.w/2, pt.y+this.h/2);
		// 	var dr = c.transDrawLen2PixelLen(true,this.w/2);

        //     cxt.save();
        //     cxt.strokeStyle=this.color;
        //     cxt.lineWidth =this.border||1 ;
        //     cxt.beginPath();
        //     //cxt.moveTo(startx,starty);
        //     cxt.arc(pcenter.x, pcenter.y, dr, this.startAngle, this.endAngle);
        //     if(this.fillColor!=null&&this.fillColor!="")
        //     {
        //         cxt.fillStyle=this.fillColor;
        //         cxt.fill();
        //         //if(this.border)
        //         var startpt = this.calDrawPtByAngle(this.startAngle);
        //         startpt = c.transDrawPt2PixelPt(startpt.x,startpt.y);
        //         var endpt = this.calDrawPtByAngle(this.endAngle);
        //         endpt = c.transDrawPt2PixelPt(endpt.x,endpt.y);
        //         cxt.moveTo(startpt.x,startpt.y);
        //         cxt.lineTo(endpt.x,endpt.y);
        //     }
        //     //else
        //     {
        //         cxt.stroke();
        //     }
            
        //     cxt.restore();
        // }
        
        public draw_sel0(cxt:CanvasRenderingContext2D,c:IDrawItemContainer)
        {
            super.draw_sel(cxt,c);
            //
            //var pt = this.getDrawXY();
			//var pcenter = c.transDrawPt2PixelPt(pt.x+this.w/2, pt.y+this.h/2);
            //var dr = c.transDrawLen2PixelLen(true,this.w/2);
            // var startx = pcenter.x+Math.cos(this.startAngle)*dr;
            // var starty = pcenter.y-Math.sin(this.startAngle)*dr;
            // var endx = pcenter.x+Math.cos(this.endAngle)*dr;
            // var endy = pcenter.y-Math.sin(this.endAngle)*dr;
            var startpt = this.calDrawPtByAngle(this.startAngle);
            startpt = c.transDrawPt2PixelPt(startpt.x,startpt.y);
            var endpt = this.calDrawPtByAngle(this.endAngle);
            endpt = c.transDrawPt2PixelPt(endpt.x,endpt.y);
            cxt.save();

            cxt.beginPath();
            cxt.strokeStyle="yellow";
			cxt.moveTo(startpt.x,startpt.y);
			cxt.arc(startpt.x,startpt.y, util.CTRL_PT_R, 0, Math.PI * 2, true);
			cxt.moveTo(endpt.x, endpt.y);
			cxt.arc(endpt.x, endpt.y, util.CTRL_PT_R, 0, Math.PI * 2, true);
			
            cxt.stroke();
            cxt.restore();
        }
	}
}


