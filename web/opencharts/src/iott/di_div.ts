/// <reference path="../draw_div.ts" />

namespace oc.iott
{
    
    
    export class DIDiv extends DrawDiv
    {
        private bMin:boolean=true;

        public constructor(opts: {} | undefined)
        {
            super(opts);
            //this.div_scroll=true;
        }

        public getClassName()
        {
            return "oc.iott.DIDiv";
        }

        static PNS = {
			_cat_name: "div", _cat_title: "Div",
            bMin: { title: "Is Min", type: "bool"}
		};

		public getPropDefs(): Array<any>
		{
			var r = super.getPropDefs();
			r.push(DIDiv.PNS);
			return r;
        }
        
        public setMin(b:boolean)
        {
            this.bMin = b;
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
                    if(dx>0&&dy>0&&dx<DIDiv.LEFTTOP_R&&dy<DIDiv.LEFTTOP_R)
                    {//click top left ctrl
                        //console.log("clk top left");
                        this.bMin = !this.bMin;
                        this.MODEL_fireChged([]);
                    }
                }
			}
        }

        
		private drawLeftTop(ctx:CanvasRenderingContext2D)
		{
            var c = this.getContainer() ;
            if(c==null)
                return ;
            var xy = this.getPixelXY() ;
            if(xy==null)
                return ;
            var hh = c.transDrawLen2PixelLen(false,DIDiv.LEFTTOP_R)/2;
            ctx.beginPath();
            
            ctx.strokeStyle = "red";
            ctx.lineWidth = 2;
            ctx.arc(xy.x+hh, xy.y+hh, hh, 0, Math.PI*2);

            ctx.stroke();
            
            ctx.fillStyle = "pink";
            ctx.closePath();
            ctx.fill();
            
			return ;
		}

        public draw(ctx: CanvasRenderingContext2D, c: IDrawItemContainer)
		{
            //draw circle at left top position
            this.drawLeftTop(ctx) ;

            if(this.bMin)
            {
                this.hideDivEle();
                return ;
            }

            super.draw(ctx,c) ;
        }

        public draw_sel(ctx: CanvasRenderingContext2D, c: IDrawItemContainer)
		{//override to ignore draw sel for selection when min
            if(this.bMin)
            {
                return ;
            }

            super.draw_sel(ctx,c) ;
        }
    }
}