
module oc.di
{
	export class DITxt extends DrawItemRectR
	{
		
		txt: string = '#text';
		font:string="serif";
		fontSize: number = 30;
		fontColor: string = "yellow";


		public constructor(opts: {} | undefined)
		{
			super(opts);
		}


		static DITxt_PNS = {
			_cat_name: "txt", _cat_title: "Text",
			//w:{title:"width",type:"int"},
			//h:{title:"height",type:"int"},
			txt: { title: "text", type: "str" },
			font:{ title: "Font", type: "str", enum_val: [["serif", "serif"], ["fontawesome", "FontAwesome"]]},
			fontSize: { title: "Font Size", type: "int" },
			fontColor: { title: "Font Color", type: "str",edit_plug:"color" }
		};


		public getClassName()
		{
			return "DITxt";
		}

		public getPropDefs()
		{
			var r = super.getPropDefs();
			r.push(DITxt.DITxt_PNS)
			return r;
		}

		public setDrawBeginXY(cont:IDrawItemContainer,x:number,y:number):boolean
		{
			this.x = x;
			this.y = y;
			//this.txt = "";
			return false;//not end and continue;
		}

		private calBoundRectDraw()
		{
			var c = this.getContainer();
			if (!c)
				return null;
			var cxt = this.getCxt();
			if(cxt==null)
				return null;
			var pt = this.getDrawXY();
			//var ppt = c.transDrawPt2PixelPt(pt.x, pt.y);
			var fs = c.transDrawLen2PixelLen(false, this.fontSize);
			cxt.save();
			//cxt.font = this.fontSize+"px serif";
			cxt.font = fs + "px "+this.font;
			cxt.fillStyle = this.fontColor;
			var tm = cxt.measureText(this.txt);
			//tm.fontBoundingBoxAscent
			var tw_px = tm.width + 5;
			var tw_dr = c.transPixelLen2DrawLen(true, tw_px);
			//console.log("fs="+fs+" txt px width="+tw_px+" drwidth="+tw_dr);
			var r = new base.Rect(pt.x, pt.y, tw_dr, this.fontSize + 8);
			cxt.restore();
			return r ;
		}

		private boundDrawRect: base.Rect | null = null;



		public getPrimRect():oc.base.Rect|null
		{
			return new base.Rect(0, 0, this.getW(), this.fontSize + DITxt.TXT_ADJ_H);
		}

		// public getPrimRect1():oc.base.Rect|null
		// {
		// 	if(this.boundDrawRect!=null)
		// 		return this.boundDrawRect;
		// 	var c = this.getContainer();

		// 	this.boundDrawRect = new base.Rect(0, 0, this.getW(), this.fontSize + DITxt.TXT_ADJ_H);

		// 	return this.boundDrawRect;
		// }

		private decodeHexTo(str:string)
		{
			str=str.replace(/\\/g,"%");
			return unescape(str);
		}

		public drawPrim(cxt: CanvasRenderingContext2D): void
		{
			cxt.save();
			//console.log(`${this.fontSize}px ${this.font}`)
			cxt.font = `${this.fontSize}px ${this.font}`;
			cxt.fillStyle = this.fontColor;
			var txt = this.txt ;
			if(this.font=="fontawesome")
			{
				txt = this.decodeHexTo(txt);
			}
			cxt.fillText(txt, 0, this.fontSize);
			//var mt = cxt.measureText(this.txt);

			//var tw_px = mt.width + 5;

			//this.boundDrawRect = new base.Rect(0, 0, tw_px, this.fontSize + DITxt.TXT_ADJ_H);
			
			cxt.restore();
		}
		public drawPrimSel(ctx: CanvasRenderingContext2D): void
		{
			
		}

		static TXT_ADJ_H:number = 9 ;

		draw0(cxt:CanvasRenderingContext2D,c:IDrawItemContainer)
		{
			//var c = this.getContainer();
			//if (!c)
			//	return;
			var pt = this.getDrawXY();
			var ppt = c.transDrawPt2PixelPt(pt.x, pt.y);
			var fs = c.transDrawLen2PixelLen(false, this.fontSize);
			cxt.save();
			//cxt.font = this.fontSize+"px serif";
			cxt.font = fs + "px "+this.font;
			cxt.fillStyle = this.fontColor;
			cxt.fillText(this.txt, ppt.x, ppt.y + fs);
			var tw_px = cxt.measureText(this.txt).width + 5;
			var tw_dr = c.transPixelLen2DrawLen(true, tw_px);
			//console.log("fs="+fs+" txt px width="+tw_px+" drwidth="+tw_dr);
			this.boundDrawRect = new base.Rect(pt.x, pt.y, tw_dr, this.fontSize + DITxt.TXT_ADJ_H);
			
			cxt.restore();
		}


		public changeRect(ctrlpt: string, x: number, y: number)
		{
			var c = this.getContainer();
			if (c == null)
				return;
			if (ctrlpt == null)
				return;
			//var oldr = this.boundDrawRect;
			//if(oldr==null)
			//	return ;

			var s = this.getDrawSize();
			var minv = c.transPixelLen2DrawLen(true, util.CTRL_LN_MIN_PIXEL * 2);
			switch (ctrlpt)
			{
				case "e":
				case "se":
					var w = x - this.x;
					if (w < minv)
						w = minv;
					//this.h = this.w*oldr.h/oldr.w;
					this.setDrawW(w);
					this.fontSize = s.h - DITxt.TXT_ADJ_H ;
					if(this.fontSize<=1)
						this.fontSize=1;
					//console.log("fontsize="+this.fontSize);
					this.MODEL_fireChged(["h","w","fontSize"]);
					break;
				case "s":
					var h = y - this.y;
					if (h < minv)
						h = minv;
					//this.w = oldr.w*this.h/oldr.h
					this.setDrawH(h);
					this.fontSize = h - DITxt.TXT_ADJ_H ;
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
					this.setDrawW(w);
					//this.h = this.w*oldr.h/this.w;
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
					this.setDrawH(h);
					//this.w = oldr.w*this.h/oldr.h
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
					this.setDrawSize(w,h);
					//this.h = this.w*oldr.h/this.w;
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
					this.setDrawSize(w,h);
					//this.h = this.w*oldr.h/this.w;
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
					this.setDrawSize(w,h);
					//this.h = this.w*oldr.h/this.w;
					this.MODEL_fireChged(["x", "y", "w", "h"]);
					break;
			}
		}
	}
	
	
}

