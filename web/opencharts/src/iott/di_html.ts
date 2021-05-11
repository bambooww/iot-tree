


module oc.iott
{
    
    /**
     * Unit TreeNode which has inner div
     * then div can has it's owner display content
     * e.g list
     */
    export class DIHtml extends DrawItemRect
    {
        private divEle: JQuery<HTMLElement> | null = null;
        private contEle: JQuery<HTMLElement> | null = null;

        protected innerEle:JQuery<HTMLElement> | null = null;

        scroll:boolean=false;
        html:string="" ;

        private bMin:boolean=true;

        public constructor(opts: {} | undefined)
        {
            super(opts);
        }

        public getClassName()
        {
            return "oc.iott.DIHtml";
        }

        static PNS = {
			_cat_name: "div", _cat_title: "Div",
            bMin: { title: "Is Min", type: "bool"},
            html:{title:"Html",type:"str",edit_plug:"html",read_only:true},
            scroll:{title:"Scroll",type:"bool"}
		};

		public getPropDefs(): Array<any>
		{
			var r = super.getPropDefs();
			r.push(DIHtml.PNS);
			return r;
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
            var sty = ``;
            if(this.scroll)
                sty=`overflow: auto;`;
            this.divEle = $(`<div id="div_${tmpid}" class="oc_unit_action" style="${sty}">
                <div id="c_${tmpid}" class="content">${this.html}</div></div>`);
            this.divEle.get(0)["_oc_di_html"] = this;
            $(ele).append(this.divEle);
            this.contEle = $(`#c_${tmpid}`);
            if(this.innerEle!=null)
                this.contEle.append(this.innerEle);
            return this.contEle ;
        }

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
        
        private hideDivEle()
        {
            if(this.divEle==null)
                return ;
            this.divEle.css("display","none");
        }

        private displayDivEle()
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
            var r = this.getBoundRectPixel();
            if(r!=null)
            {
                this.divEle.css("display","");
                this.divEle.css("top",(r.y+hh)+"px");
                this.divEle.css("left",r.x+"px");
                this.divEle.css("width",r.w+"px");
                this.divEle.css("height",(r.h-hh)+"px");
            }
        }


        public getPrimRect(): base.Rect | null
        {
            return new oc.base.Rect(0,0,100,100);
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
                    
                }
			}
			
        }

        
        public drawPrim(ctx: CanvasRenderingContext2D): void
        {
            oc.util.drawRect(ctx, 0, 0, 100,100, null, null, 1, "#8cdcda");
            
            this.displayDivEle();
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
            
            if(this.bMin)
            {
                this.hideDivEle();
                return ;
            }

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

        public draw_sel(ctx: CanvasRenderingContext2D, c: IDrawItemContainer)
		{//override to ignore draw sel for selection when min
            if(this.bMin)
            {
                return ;
            }

            super.draw_sel(ctx,c) ;
        }

        public drawPrimSel(ctx: CanvasRenderingContext2D): void
        {
            
        }

    }
}