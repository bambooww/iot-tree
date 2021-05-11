
module oc.di
{
    /**
     * 
     */
    export class DIPtsPipe extends DIPts
    {
        public constructor(opts: {} | undefined)
        {
            super(opts);
            if (opts != undefined)
            {
            }
            this.lnW = 18 ;
        }

        public getClassName()
        {
            return "oc.di.DIPtsPipe";
        }


        public draw(ctx: CanvasRenderingContext2D, c: IDrawItemContainer)
		{
			if(!this.ptsPy.isValid())
                return  ;
            ctx.save() ;
            this.drawPipe(ctx, c);
            
            ctx.restore();
        }


        
        private drawPipe(ctx: CanvasRenderingContext2D, c: IDrawItemContainer)
        {
            var pts = this.ptsPy.getPts() ;
            if(pts.length<2)
                return ;
           
            ctx.beginPath();
            ctx.strokeStyle = this.color;

            ctx.lineWidth = this.lnW;
            ctx.lineCap = "round";
            ctx.lineJoin = "miter";
            ctx.miterLimit = 5;

            var p = pts[0] ;
            p = c.transDrawPt2PixelPt(p.x,p.y);

            for(var i = 1 ; i < pts.length ; i ++)
            {
                ctx.beginPath();
                var pn = c.transDrawPt2PixelPt(pts[i].x,pts[i].y);
                var g = oc.base.Fill.calCxtPipeFillStyle(p.x,p.y,pn.x,pn.y,this.lnW,
                    this.color,ctx);
                if(g!=null)
                    ctx.strokeStyle = g ;

                ctx.moveTo(p.x, p.y);
                
                //pipe linear g
                ctx.lineTo(pn.x, pn.y);
                p = pn ;
                ctx.stroke();
                //ctx.closePath();
                
            }
            //ctx.closePath();
            return ;
        }
        
        private drawJoin(ctx: CanvasRenderingContext2D, c: IDrawItemContainer,
            pa:oc.base.Pt,pb:oc.base.Pt,pc:oc.base.Pt)
        {
            var d = this.lnW/2 ;
            var ang = this.calAngle(pa,pb,pc)/2;
            var k = d/Math.tan(ang) ;
            
        }

        private calAngle(a:oc.base.Pt,b:oc.base.Pt,c:oc.base.Pt):number
        {
            var AB = Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
            var AC = Math.sqrt(Math.pow(a.x - c.x, 2) + Math.pow(a.y - c.y, 2));
            var BC = Math.sqrt(Math.pow(b.x - c.x, 2) + Math.pow(b.y - c.y, 2));
            var cosA = (Math.pow(AB, 2) + Math.pow(AC, 2) - Math.pow(BC, 2)) / (2 * AB * AC);
            return Math.round( Math.acos(cosA) * 180 / Math.PI );
        }

        //private calPt5In
        /**
         * calculate 5 pts in one pipe seg
         * 需要使用面图计算管道交叉点，并进行连接点和rad梯度和管道linear梯度
         * @param ctx 
         * @param c 
         */
        private drawPipeSeg(ctx: CanvasRenderingContext2D, c: IDrawItemContainer)
        {

        }
    }
}