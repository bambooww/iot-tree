
module oc.di
{
    /**
     * 
     */
    export class DIPtsPy extends DIPts
    {
        fillColor: string ="#cccccc";
        fillR:number=0 ;//0-360

        public constructor(opts: {} | undefined)
        {
            super(opts);
        }


        static PNS_PTSPY = {
            _cat_name: "di_ptspy", _cat_title: "Polygon",
            fillColor: { title: "Fill Color", type: "str",edit_plug:"color" },
            fillR:{ title: "Fill Rotate", type: "int"},

        };

        public getClassName()
        {
            return "oc.di.DIPtsPy";
        }

        public getPropDefs()
        {
            var r = super.getPropDefs();
            r.push(DIPtsPy.PNS_PTSPY);
            return r;
        }

        
        public setFillColor(c: string)
        {
            this.fillColor = c;
            
        }

        public draw(ctx: CanvasRenderingContext2D, c: IDrawItemContainer)
		{
			if(!this.ptsPy.isValid())
                return  ;
            ctx.save() ;
            this.drawPy(ctx, c);
            
            ctx.restore();
        }
        
        private drawPy(ctx: CanvasRenderingContext2D, c: IDrawItemContainer)
        {
            ctx.beginPath();
            ctx.strokeStyle = this.color;
            ctx.lineWidth = this.lnW;
            var pts = this.ptsPy.getPts();
            for (var tmppt of pts)
            {
                var p = c.transDrawPt2PixelPt(tmppt.x,tmppt.y) ;
                ctx.lineTo(p.x, p.y);
            }
            var p = c.transDrawPt2PixelPt(pts[0].x,pts[0].y) ;
                ctx.lineTo(p.x,p.y);
            
            ctx.stroke();
            // if (this.fillColor && this.fillColor != "")
            // {
            //     ctx.fillStyle = this.fillColor;
            //     ctx.closePath();
            //     ctx.fill();
            // }
            var r = this.ptsPy.getBoundingBox() ;
            if(r!=null)
            {
                var p1 = c.transDrawPt2PixelPt(r.x,r.y) ;
                var p2 = c.transDrawPt2PixelPt(r.getMaxX(),r.getMaxY()) ;
                var tmpr:oc.base.Rect = new oc.base.Rect(p1.x,p1.y,p2.x-p1.x,p2.y-p1.y);
                // var linear = ctx.createLinearGradient(p1.x,p1.y,p2.x,p2.y);
                // linear.addColorStop(0,'#fff');
                // linear.addColorStop(0.5,'#f0f');
                // linear.addColorStop(1,'rgba(112,112,112,0.6)');
                // ctx.
                var bcolor = this.calLinearBorderColor(this.fillColor);
                var f = oc.base.Fill.createLinearG([bcolor,this.fillColor,bcolor],this.fillR) ;
                ctx.fillStyle = f.calCxtFillStyle(tmpr,ctx);
                ctx.closePath();
                ctx.fill();
            }
            return ;
        }

        private tranInt2HexStr(i:number)
        {
            var str = i.toString(16) ;
            if(str.length==1)
                return "0"+str ;
            else
                return str ;
        }

        private calLinearBorderColor(c:string)
        {
            var r,g,b;
            if(c.length==7&&c.indexOf("#")==0)
            {
                r = parseInt(c.substr(1,2),16) ;
                g = parseInt(c.substr(3,2),16) ;
                b = parseInt(c.substr(5,2),16) ;
            }
            else
            {
                return "#000000" ;
            }

            var hsv = oc.util.transRGB2HSV([r,g,b]) ;
            hsv[2] = hsv[2]/2 ;
            var rgb = oc.util.transHSV2RGB(hsv) ;
            return "#"+this.tranInt2HexStr(rgb[0])+this.tranInt2HexStr(rgb[1])+this.tranInt2HexStr(rgb[2]) ;
        }
    }
}


