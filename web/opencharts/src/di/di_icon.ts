
module oc.di
{
    /**
     * support font icon like FontAwesome.
     */
	export class DIIcon extends DrawItemRectR
	{
		//txt: string = '#text';
        color: string = "yellow";
        unicode:string="";
        private fontSize:number=30;

		public constructor(opts: {} | undefined)
		{
            super(opts);
            if(opts!=undefined)
            {
                var uc = opts["unicode"];
                if(uc!=undefined||uc!=null)
                    this.unicode = uc ;
            }
		}


		static DIIcon_PNS = {
			_cat_name: "txt", _cat_title: "Text",
            color: { title: "Icon Color", type: "str",edit_plug:"color" },
            unicode: { title: "Icon Code", type: "str" }
		};


		public getClassName()
		{
			return "DIIcon";
		}

		public getPropDefs()
		{
			var r = super.getPropDefs();
			r.push(DIIcon.DIIcon_PNS)
			return r;
		}
        public setDrawBeginXY(cont:IDrawItemContainer,x:number,y:number):boolean
		{
			this.x = x;
			this.y = y;
			//this.txt = "";
			return false;//not end and continue;
		}

        private boundDrawRect: base.Rect | null = null;

		public getPrimRect():oc.base.Rect|null
		{
            if(this.boundDrawRect!=null)
                return this.boundDrawRect;
                
            var cxt = this.getCxt();
            if(cxt==null)
                return null;

            //fit and ajust - imp for first
            this.fontSize = this.getH() - DIIcon.TXT_ADJ_H ;
            if(this.fontSize<=1)
                this.fontSize=1;
        
            var fs = this.fontSize;
			cxt.save();
			//cxt.fontt = this.fontSize+"px serif";
			cxt.font = fs + "px fontawesome";
			cxt.fillStyle = this.color;
			var txt =  this.decodeHexTo(this.unicode);
			//cxt.fillText(txt, 0, fs);
			var mt = cxt.measureText(txt);
			//console.log(mt);
			var tw_px = mt.width + 5;

			this.boundDrawRect = new base.Rect(0, 0, tw_px, this.fontSize);//
			
            cxt.restore();
            return this.boundDrawRect ;
        }
        
		private decodeHexTo(str:string)
		{
			str=str.replace(/\\/g,"%");
			return unescape(str);
        }

        private bfirst:boolean=true;
        
        public drawPrim(cxt: CanvasRenderingContext2D): void
		{
            var c = this.getContainer();
            if(c==null)
                return ;
            if(this.bfirst)
            {
                this.bfirst=false;
                var oldr = this.getPrimRect();
                if(oldr!=null)
                {
                    this.setDrawH(this.getW()*oldr.h/oldr.w);
                    this.fontSize = this.getH() - DIIcon.TXT_ADJ_H ;
                    if(this.fontSize<=1)
                        this.fontSize=1;
                }
            }
            
			var fs = this.fontSize;
			cxt.save();
            //cxt.fontt = this.fontSize+"px serif";
            
			cxt.font = fs + "px fontawesome";
			cxt.fillStyle = this.color;
			var txt =  this.decodeHexTo(this.unicode);
			
			var mt = cxt.measureText(txt);
			//console.log(mt);
			var tw_px = mt.width + 5;
            cxt.fillText(txt, 0, fs*0.86);
			this.boundDrawRect = new base.Rect(0, 0, tw_px, this.fontSize);//
			
			cxt.restore();
		}

		public drawPrimSel(ctx: CanvasRenderingContext2D): void
		{
			
        }


		static TXT_ADJ_H:number = 9 ;

		public changeRect(ctrlpt: string, x: number, y: number)
		{
			var c = this.getContainer();
			if (c == null)
				return;
			if (ctrlpt == null)
				return;
			var oldr = this.getPrimRect();
			if(oldr==null)
				return ;

			var s = this.getDrawSize();
			var minv = c.transPixelLen2DrawLen(true, util.CTRL_LN_MIN_PIXEL * 2);
			switch (ctrlpt)
			{
				case "e":
				case "se":
					var w = x - this.x;
					if (w < minv)
						w = minv;
					var h = w*oldr.h/oldr.w;
					this.setDrawSize(w,h);
					this.fontSize = h - DIIcon.TXT_ADJ_H ;
					if(this.fontSize<=1)
						this.fontSize=1;
					//console.log("fontsize="+this.fontSize);
					this.MODEL_fireChged(["h","w"]);
					break;
				case "s":
					var h = y - this.y;
					if (h < minv)
						h = minv;
					this.setDrawH(h);
					//this.w = oldr.w*this.h/oldr.h
					this.fontSize = h - DIIcon.TXT_ADJ_H ;
					this.MODEL_fireChged(["w","h"]);
					break;
				case "w":
					var rx = this.x + s.w;
					this.x = x;
					var w = rx - x;
					if (w < minv)
					{
						w = minv;
						this.x = rx - minv;
					}
					var h = w*oldr.h/w;
					this.setDrawSize(w,h) ;
					this.MODEL_fireChged(["x", "w","h"]);
					break;
				case "n":
					var ry = this.y + s.h;
					this.y = y;
					var h = ry - y;
					if (h < minv)
					{
						h = minv;
						this.y = ry - minv;
					}
					var w = oldr.w*h/oldr.h
					this.setDrawSize(w,h) ;
					this.MODEL_fireChged(["y", "w","h"]);
					break;
				case "ne":
					var w = x - this.x;
					if (w < minv)
						w = minv;
					var ry = this.y + s.h;
					this.y = y;
					var h = ry - y;
					if (h < minv)
					{
						h = minv;
						this.y = ry - minv;
					}
					h = w*oldr.h/w;
					this.setDrawSize(w,h) ;
					this.MODEL_fireChged(["w", "y", "h"]);
					break;
				case "sw":
					var h = y - this.y;
					if (h < minv)
						h = minv;
					var rx = this.x + s.w;
					this.x = x;
					var w = rx - x;
					if (w < minv)
					{
						w = minv;
						this.x = rx - minv;
					}
					h = w*oldr.h/w;
					this.setDrawSize(w,h) ;
					this.MODEL_fireChged(["h", "x", "w"]);
					break;
				case "nw":
					var ry = this.y + s.h;
					this.y = y;
					var h = ry - y;
					if (h < minv)
					{
						h = minv;
						this.y = ry - minv;
					}
					var rx = this.x + s.w;
					this.x = x;
					var w = rx - x;
					if (w < minv)
					{
						w = minv;
						this.x = rx - minv;
					}
					h = w*oldr.h/w;
					this.setDrawSize(w,h) ;
					this.MODEL_fireChged(["x", "y", "w", "h"]);
					break;
			}
		}
	}
}

