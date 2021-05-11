
module oc.di
{
    /**
     * 
     */
    export class DIPtsLn extends DIPts
    {
        lineTp:string="" ;
        arrowHead:string="" ;
        arrowTail:string="" ;

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
        }

        public getClassName()
        {
            return "oc.di.DIPtsLn";
        }

        public draw(ctx: CanvasRenderingContext2D, c: IDrawItemContainer)
		{
			if(!this.ptsPy.isValid())
                return  ;
            ctx.save() ;
            this.drawLn(ctx, c);
            ctx.restore();
        }
        
        private drawLn(ctx: CanvasRenderingContext2D, c: IDrawItemContainer)
        {
            var pts = this.ptsPy.getPts() ;
            if(pts.length<2)
                return ;
           
            ctx.beginPath();
            ctx.strokeStyle = this.color;

            ctx.lineWidth = this.lnW;
            ctx.lineCap = "round";
            //ctx.lin
            ctx.lineJoin = "round";
            ctx.miterLimit = 5;

            var p = pts[0] ;
            p = c.transDrawPt2PixelPt(p.x,p.y);
            ctx.moveTo(p.x, p.y);
            
            for(var i = 1 ; i < pts.length ; i ++)
            {
                //ctx.beginPath();
                var pn = c.transDrawPt2PixelPt(pts[i].x,pts[i].y);
                //pipe linear g
                ctx.lineTo(pn.x, pn.y);
                p = pn ;
                ctx.stroke();
                //ctx.closePath();
                
            }
            //ctx.closePath();
            return ;
        }

    }
}


