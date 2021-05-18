
/// <reference path="./draw_item.ts" />

namespace oc
{
	export interface IPts
	{
		addPt(x:number,y:number):void;

        getPts():oc.base.Pt[];
	}
	/**
	 * support draw primtive in rect ,and simple sub class which need only know 
	 * how draw self.
	 * sub class is need not knows draw outer env
	 */
	export abstract class DrawItemRect extends DrawItem implements IRectItem,IActionNode
	{
		private w: number = 100;
		private h: number = 100;

		//private popMenu:PopMenu|null=null;

		public constructor(opts: {} | undefined)
		{
			super(opts);
		}

		static Rect_PNS = {
			_cat_name: "rect", _cat_title: "Rectangle",
			w: { title: "width", type: "int", readonly: true,binder:true },
			h: { title: "height", type: "int", readonly: true,binder:true },
		};

		public getPropDefs(): Array<any>
		{
			var r = super.getPropDefs();
			r.push(DrawItemRect.Rect_PNS);
			return r;
		}

		public getW():number
		{
			return this.w;
		}

		public getH():number
		{
			return this.h;
		}

		public getMinDrawSize():oc.base.Size
		{
			return {w:0,h:0};
		}

		public getBoundRectDraw(): base.Rect | null
		{
			var pt = this.getDrawXY();
			return new oc.base.Rect(pt.x, pt.y, this.getW(), this.getH());
		}

		public getBoundRectPixel(): base.Rect | null
		{
			var dr = this.getBoundRectDraw();
			if(dr==null)
				return null;
			var c = this.getContainer();
			if (c == null)
				return null;
			//var pt = this.getDrawXY();
			var w = c.transDrawLen2PixelLen(true, dr.w);
			var h = c.transDrawLen2PixelLen(false, dr.h);
			var pt = c.transDrawPt2PixelPt(dr.x, dr.y);
			return new oc.base.Rect(pt.x, pt.y, w, h);
		}

		
		public getDrawPreferSize():base.Size
		{
			return {w:100,h:100}
		}

		public getPixelPreferSize():base.Size
		{
			var c = this.getContainer();
			if (c == null)
				throw "no container" ;
			var ds = this.getDrawPreferSize() ;
			var pw = c.transDrawLen2PixelLen(true,ds.w);
			var ph = c.transDrawLen2PixelLen(false,ds.h);
			return {w:pw,h:ph}
		}


		/**
		 * w/h ratio  invalid when val<=0
		 */
		public getWHRatio(): number
		{
			return -1;
		}

		public setDrawSize(w:number,h:number)
		{
			this.w = w ;
			this.h = h ;
		}

		public setDrawW(w:number)
		{
			this.w = w ;
		}

		public setDrawH(h:number)
		{
			this.h = h ;
		}

		public getDrawSize():oc.base.Size
		{
			return {w:this.w,h:this.h};
		}

		protected on_size_chged():void
		{}

		public getPixelSize():oc.base.Size|null
		{
			var c = this.getContainer();
			if(c==null)
				return null ;
			var w = c.transDrawLen2PixelLen(true,this.getW());
			var h = c.transDrawLen2PixelLen(false,this.getH());
			return {w:w,h:h};
		}

		public setDrawBeginXY(cont: IDrawItemContainer, x: number, y: number): boolean
		{
			super.setDrawBeginXY(cont, x, y);

			this.w = cont.transPixelLen2DrawLen(true, 2);
			this.h = cont.transPixelLen2DrawLen(false, 2);
			return true;
		}

		public setDrawEndXY(cont: IDrawItemContainer, x: number, y: number): base.Pt
		{
			var xy = super.setDrawEndXY(cont, x, y);
			x = xy.x;
			y = xy.y;
			this.w = x - this.x;
			this.h = y - this.y;

			var r = this.getWHRatio();
			if (r > 0)
			{
				this.h = this.w / r;
			}

			this.MODEL_fireChged(["w", "h"]);

			return xy;
		}

		public getActionTypeName():string
		{
			return "" ;
		}

		public on_mouse_event(tp: MOUSE_EVT_TP, pxy: oc.base.Pt, dxy: oc.base.Pt,e:_MouseEvent)
        {
			super.on_mouse_event(tp, pxy, dxy,e);
			
            if(tp==MOUSE_EVT_TP.Down)
            {
                if(e.button==MOUSE_BTN.RIGHT)
				{//right
					if(PopMenu.createShowPopMenu(this,pxy,dxy))
						e.preventDefault();
                }
			}
			else if (tp == MOUSE_EVT_TP.DbClk)
            {
				var pmi = PopMenu.getDefaultPopMenuItem(this);
				if(pmi!=null)
				{
					pmi.action(this,pmi.op_name,pxy,dxy) ;
				}
                return ;
            }
        }


		/**
		 * return prim rect
		 */
		public abstract getPrimRect(): oc.base.Rect | null;

		/**
		 * override it and return true,will mask drawPrim
		 * it can get sub class a chance to prevent from scale ugly. sub class must calc point in scale
		 * and draw other which may beauty.
		 * @param ctx 
		 * @param xratio 
		 * @param yratio 
		 */
		public drawPrimScale(ctx:CanvasRenderingContext2D,xratio:number,yratio:number):boolean
		{
			return false;
		}

		/**
		 * before scale drawPrim 
		 * override it can draw normal before draw prim
		 * @param ctx 
		 * @param xratio 
		 * @param yratio 
		 */
		public drawBeforeScale(ctx:CanvasRenderingContext2D,xratio:number,yratio:number)
		{
			return;
		}
		/**
		 * do draw in rect self
		 * @param ctx
		 */
		public abstract drawPrim(ctx: CanvasRenderingContext2D): void;

		public abstract drawPrimSel(ctx: CanvasRenderingContext2D): void;
		/**
		 * cal bound four point and four edge
		 * return null or
		 *   e s w n  ne se ws wn
		 * @param x 
		 * @param y 
		 */
		// public chkPtOnCtrl0(pxy:base.Pt,dxy:base.Pt):string|null
		// {
		// 	var c = this.getContainer();
		// 	if(c==null)
		// 		return null;
		// 	var r = c.transPixelLen2DrawLen(true,util.CTRL_PT_R);
		// 	var minv = c.transPixelLen2DrawLen(true,util.CTRL_LN_MIN_PIXEL) ;
		// 	if(util.chkPtInRadius(this.x,this.y,dxy.x,dxy.y,r))
		// 		return "nw";
		// 	if(util.chkPtInRadius(this.x+this.w,this.y,dxy.x,dxy.y,r))
		// 		return "ne";
		// 	if(util.chkPtInRadius(this.x+this.w,this.y+this.h,dxy.x,dxy.y,r))
		// 		return "se";
		// 	if(util.chkPtInRadius(this.x,this.y+this.h,dxy.x,dxy.y,r))
		// 		return "sw";
		// 	if(dxy.x>this.x&&dxy.x<this.x+this.w&&Math.abs(dxy.y-this.y)<minv)
		// 		return "n" ;
		// 	if(dxy.y>this.y&&dxy.y<this.y+this.h&&Math.abs(dxy.x-this.x-this.w)<minv)
		// 		return "e" ;
		// 	if(dxy.x>this.x&&dxy.x<this.x+this.w&&Math.abs(dxy.y-this.y-this.h)<minv)
		// 		return "s" ;
		// 	if(dxy.y>this.y&&dxy.y<this.y+this.h&&Math.abs(dxy.x-this.x)<minv)
		// 		return "w" ;
		// 	return this.chkPtOnCtrl_rotate(pxy,dxy);
		// }

		public chkPtOnCtrl(pxy: base.Pt, dxy: base.Pt): string | null
		{
			var c = this.getContainer();
			if (c == null)
				return null;
			var ctx = this.getCxt();
			if (ctx == null)
				return null;
			var rect = this.getPrimRect();
			if (rect == null)
				return null;
			var w = c.transDrawLen2PixelLen(true, this.w);
			var h = c.transDrawLen2PixelLen(false, this.h);
			ctx.save();
			var pt = this.getDrawXY();
			var p1 = c.transDrawPt2PixelPt(pt.x, pt.y);
			//var cx = p1.x + w / 2;
			//var cy = p1.y + h / 2;
			ctx.translate(p1.x, p1.y);
			//ctx.rotate(this.rotate);
			//ctx.translate(-w / 2, -h / 2);
			//ctx.scale(w / rect.w, h / rect.h);

			var r;
			r = this.chkPtOnCtrl_border(ctx,w,h, pxy, dxy);
			if (r != null)
			{
				ctx.restore();
				return r;
			}
			r = this.chkPtOnCtrl_rotate(ctx,w,h, pxy, dxy);
			ctx.restore();
			return r;
		}

		protected chkPtOnCtrl_border(ctx: CanvasRenderingContext2D,w:number,h:number,  pxy: base.Pt, dxy: base.Pt): string | null
		{
			// var c = this.getContainer();
			// if(c==null)
			// 	return null;
			// var r = c.transPixelLen2DrawLen(true,util.CTRL_PT_R);
			// var minv = c.transPixelLen2DrawLen(true,util.CTRL_LN_MIN_PIXEL) ;
			// if(dxy.x>this.x&&dxy.x<this.x+this.w&&Math.abs(dxy.y-this.y)<minv)
			// 	return "n" ;
			// if(dxy.y>this.y&&dxy.y<this.y+this.h&&Math.abs(dxy.x-this.x-this.w)<minv)
			// 	return "e" ;
			// if(dxy.x>this.x&&dxy.x<this.x+this.w&&Math.abs(dxy.y-this.y-this.h)<minv)
			// 	return "s" ;
			// if(dxy.y>this.y&&dxy.y<this.y+this.h&&Math.abs(dxy.x-this.x)<minv)
			// 	return "w" ;

			ctx.beginPath();
			ctx.arc(0, 0, 3, 0, Math.PI * 2);
			if (ctx.isPointInPath(pxy.x, pxy.y))
				return "nw";
			ctx.beginPath();
			ctx.arc(w, 0, 3, 0, Math.PI * 2);
			if (ctx.isPointInPath(pxy.x, pxy.y))
				return "ne";
			ctx.beginPath();
			ctx.arc(w, h, 3, 0, Math.PI * 2);
			if (ctx.isPointInPath(pxy.x, pxy.y))
				return "se";
			ctx.beginPath();
			ctx.arc(0, h, 3, 0, Math.PI * 2);
			if (ctx.isPointInPath(pxy.x, pxy.y))
				return "sw";

			if(ctx.isPointInStroke)
			{//ie is not support
				ctx.beginPath();
				ctx.moveTo(0, 0);
				ctx.lineTo(w, 0);
				if (ctx.isPointInStroke(pxy.x, pxy.y))
					return "n";
	
				ctx.beginPath();
				ctx.moveTo(w, 0);
				ctx.lineTo(w, h);
				if (ctx.isPointInStroke(pxy.x, pxy.y))
					return "e";
				ctx.beginPath();
				ctx.moveTo(w, h);
				ctx.lineTo(0, h);
				if (ctx.isPointInStroke(pxy.x, pxy.y))
					return "s";
				ctx.beginPath();
				ctx.moveTo(0, h);
				ctx.lineTo(0, 0);
				if (ctx.isPointInStroke(pxy.x, pxy.y))
					return "w";
			}
			

			return null;
		}

		protected chkPtOnCtrl_rotate(ctx: CanvasRenderingContext2D,w:number,h:number,pxy: base.Pt, dxy: base.Pt): string | null
		{
			ctx.beginPath();
			ctx.arc(w / 2, -20, 3, 0, Math.PI * 2);
			//ctx.closePath();
			//var r = ctx.isPointInPath(pxy.x-p1.x, pxy.y-p1.y);
			var r = ctx.isPointInPath(pxy.x, pxy.y);
			//console.log(" in rotatoe path="+r);

			if (r)
				return "r";
			else
				return null;
		}


		/**
         * based on center pt,using draw pt to calculate angle
         * @param x 
         * @param y 
         */
		protected calArcAngleByDrawPt(x: number, y: number)
		{
			var pt = this.getDrawXY();
			var centerx = pt.x + this.w / 2;
			var centery = pt.y + this.h / 2;
			var dx = x - centerx;
			var dy = y - centery;
			if (dx == 0)
			{
				if (dy >= 0)
					return Math.PI * 0.5;
				else
					return Math.PI * 1.5;
			}
			var r = Math.atan(dy / dx);
			if (dx > 0)
			{
				if (r >= 0)
					return r;
				else
					return Math.PI * 2 + r;
			}
			else
			{
				if (r >= 0)
					return r + Math.PI;
				else
					return Math.PI + r;
			}
		}


		public changeRect(ctrlpt: string, x: number, y: number)
		{
			var c = this.getContainer();
			if (c == null)
				return;
			if (ctrlpt == null)
				return;

			var minw = c.transPixelLen2DrawLen(true, util.CTRL_LN_MIN_PIXEL * 2);
			var minh = minw ;
			var ms = this.getMinDrawSize();
			minw = ms.w>minw?ms.w:minw;
			minh = ms.h>minh?ms.h:minh;
			var wh_ratio = this.getWHRatio();
			switch (ctrlpt)
			{
				case "e":
					this.w = x - this.x;
					if (this.w < minw)
						this.w = minw;
					if (wh_ratio <= 0)
					{
						this.MODEL_fireChged(["w"]);
					}
					else
					{
						this.h = this.w / wh_ratio;
						this.MODEL_fireChged(["w", "h"]);
					}
					break;
				case "s":
					this.h = y - this.y;
					if (this.h < minh)
						this.h = minh;
					if (wh_ratio <= 0)
						this.MODEL_fireChged(["h"]);
					else
					{
						this.w = this.h * wh_ratio;
						this.MODEL_fireChged(["w", "h"]);
					}
					break;
				case "w":
					var rx = this.x + this.w;
					this.x = x;
					this.w = rx - x;
					if (this.w < minw)
					{
						this.w = minw;
						this.x = rx - minw;
					}
					if (wh_ratio <= 0)
						this.MODEL_fireChged(["x", "w"]);
					else
					{
						this.h = this.w / wh_ratio;
						this.MODEL_fireChged(["w", "x", "h"]);
					}
					break;
				case "n":
					var ry = this.y + this.h;
					this.y = y;
					this.h = ry - y;
					if (this.h < minh)
					{
						this.h = minh;
						this.y = ry - minh;
					}
					if (wh_ratio <= 0)
						this.MODEL_fireChged(["y", "h"]);
					else
					{
						this.w = this.h * wh_ratio;
						this.MODEL_fireChged(["y", "w", "h"]);
					}
					break;
				case "ne":
					this.w = x - this.x;
					if (this.w < minw)
						this.w = minw;
					var ry = this.y + this.h;
					this.y = y;
					this.h = ry - y;
					if (this.h < minh)
					{
						this.h = minh;
						this.y = ry - minh;
					}
					if (wh_ratio > 0)
					{
						this.w = this.h * wh_ratio;
					}
					this.MODEL_fireChged(["w", "y", "h"]);
					break;
				case "se":
					this.h = y - this.y;
					if (this.h < minh)
						this.h = minh;
					this.w = x - this.x;
					if (this.w < minw)
						this.w = minw;
					if (wh_ratio > 0)
					{
						this.h = this.w / wh_ratio;
					}
					this.MODEL_fireChged(["w", "h"]);
					break;
				case "sw":
					this.h = y - this.y;
					if (this.h < minh)
						this.h = minh;
					var rx = this.x + this.w;
					this.x = x;
					this.w = rx - x;
					if (this.w < minw)
					{
						this.w = minw;
						this.x = rx - minw;
					}
					if (wh_ratio > 0)
					{
						this.h = this.w / wh_ratio;
					}
					this.MODEL_fireChged(["h", "x", "w"]);
					break;
				case "nw":
					var ry = this.y + this.h;
					var rx = this.x + this.w;//fix
					this.y = y;
					this.h = ry - y;
					if (this.h < minh)
					{
						this.h = minh;
						this.y = ry - minh;
					}

					if (wh_ratio > 0)
					{
						this.w = this.h * wh_ratio;
						this.x = rx - this.w;
					}
					else
					{
						this.x = x;
						this.w = rx - x;
					}

					this.MODEL_fireChged(["x", "y", "w", "h"]);
					break;
			}

			this.on_size_chged();
		}



		public draw(ctx: CanvasRenderingContext2D, c: IDrawItemContainer)
		{
			var rect = this.getPrimRect();
			//console.log("rect draw "+this.getClassName()+">>rect="+rect+"  w="+this.w+" h="+this.h);
			if (rect == null)
				return;
			if (this.w <= 0 || this.h <= 0)
				return;
			var w = c.transDrawLen2PixelLen(true, this.w);
			var h = c.transDrawLen2PixelLen(false, this.h);
			ctx.save();
			var pt = this.getDrawXY();
			var p1 = c.transDrawPt2PixelPt(pt.x, pt.y);

			ctx.translate(p1.x, p1.y);
			var xratio = w / rect.w;
			var yratio = h / rect.h;
			if(!this.drawPrimScale(ctx,xratio,yratio))
			{
				this.drawBeforeScale(ctx,xratio,yratio) ;
				ctx.scale(xratio,yratio);
				this.drawPrim(ctx);//call sub
			}
			ctx.restore();
		}

		public draw_sel(ctx: CanvasRenderingContext2D, c: IDrawItemContainer)
		{
			var rect = this.getPrimRect();
			if (rect == null)
				return;
			if (this.w <= 0 || this.h <= 0)
				return;
			var w = c.transDrawLen2PixelLen(true, this.w);
			var h = c.transDrawLen2PixelLen(false, this.h);
			var rd =20;//c.transDrawLen2PixelLen(true,20) ;
			ctx.save();
			var pt = this.getDrawXY();
			var p1 = c.transDrawPt2PixelPt(pt.x, pt.y);

			ctx.translate(p1.x, p1.y);
			//---
			ctx.lineWidth = 1;
			ctx.strokeStyle = "red";

			ctx.beginPath();
			//ctx.setLineDash([]);
			ctx.arc(0, 0, 3, 0, Math.PI * 2);
			ctx.stroke();
			ctx.beginPath();
			ctx.arc(w, 0, 3, 0, Math.PI * 2);
			ctx.stroke();
			ctx.beginPath();
			ctx.arc(w, h, 3, 0, Math.PI * 2);
			ctx.stroke();
			ctx.beginPath();
			ctx.arc(0, h, 3, 0, Math.PI * 2);
			ctx.stroke();

			//ctx.translate(p1.x,p1.y);
			ctx.rect(0, 0, w, h);
			ctx.stroke();

			ctx.scale(w / rect.w, h / rect.h);
			ctx.beginPath();
			//ctx.scale(w/rect.w,h/rect.h);
			
			this.drawPrimSel(ctx);

			ctx.restore();
		}
	}

	export class DrawItemRectBorder extends DrawItemRect
	{
		private pts:oc.base.Pt2[]=[[0,0],[100,0],[100,100],[0,100]];

		public constructor(opts:{}|undefined)
		{
			super(opts);
			if(opts!=undefined)
			{
				var r:oc.base.Rect = opts["rect"];
				if(r!=undefined&&r!=null)
				{
					this.x = r.x;
					this.y = r.y ;
					this.setDrawSize(r.w,r.h);
				}
			}
		}

		public getPrimRect(): base.Rect | null
		{
			return new oc.base.Rect(0,0,100,100);
		}
		public drawPrim(ctx: CanvasRenderingContext2D): void
		{
			ctx.lineWidth = 1;
 			ctx.setLineDash([5, 5]);
			ctx.strokeStyle = "#c1cccc";
            ctx.beginPath();
            
            for(var tmppt of this.pts)
            {
                ctx.lineTo(tmppt[0],tmppt[1]);
            }
            ctx.lineTo(this.pts[0][0],this.pts[0][1]);

			ctx.stroke();
		}
		public drawPrimSel(ctx: CanvasRenderingContext2D): void
		{
			
		}

	}

	export abstract class DrawItemRectR extends DrawItemRect
	{
		rotate: number = 0;
		//in rect rotate center ratio 0-1
		rc_ratio_x:number=0.5;
		rc_ratio_y:number=0.5;

		public constructor(opts: {} | undefined)
		{
			super(opts);
		}

		static Rect_PNSR = {
			_cat_name: "rect_r", _cat_title: "Rectangle Rotation",
			rotate: { title: "Rotate", type: "float" },
			rc_ratio_x:{title:"Rotate Center Ratio X",type:"float",val_range:[0,1.0]},
			rc_ratio_y:{title:"Rotate Center Ratio Y",type:"float",val_range:[0,1.0]},
		};

		public getPropDefs(): Array<any>
		{
			var r = super.getPropDefs();
			r.push(DrawItemRectR.Rect_PNSR);
			return r;
		}


		public changeRotate(x: number, y: number)
		{
			this.rotate = this.calArcAngleByDrawPt(x, y) + Math.PI / 2;
			this.MODEL_fireChged(["rotate"]);
		}

		public chkPtOnCtrl(pxy: base.Pt, dxy: base.Pt): string | null
		{
			var c = this.getContainer();
			if (c == null)
				return null;
			var ctx = this.getCxt();
			if (ctx == null)
				return null;
			var rect = this.getPrimRect();
			if (rect == null)
				return null;
			var w = c.transDrawLen2PixelLen(true, this.getW());
			var h = c.transDrawLen2PixelLen(false, this.getH());
			ctx.save();
			var pt = this.getDrawXY();
			var p1 = c.transDrawPt2PixelPt(pt.x, pt.y);
			var cx = p1.x + w*this.rc_ratio_x;
			var cy = p1.y + h*this.rc_ratio_y;
			ctx.translate(cx, cy);
			ctx.rotate(this.rotate);
			ctx.translate(-w*this.rc_ratio_x, -h*this.rc_ratio_y);
			//ctx.scale(w / rect.w, h / rect.h);

			var r;
			r = this.chkPtOnCtrl_border(ctx,w,h,pxy, dxy);
			if (r != null)
			{
				ctx.restore();
				return r;
			}
			r = this.chkPtOnCtrl_rotate(ctx,w,h, pxy, dxy);
			ctx.restore();
			return r;
		}


		private calcTransMatrix(x: number, y: number, w: number, h: number, r: oc.base.Rect)
		{
			var cx = x + r.w / 2;
			var cy = y + r.h / 2;
			//ctx.translate(cx,cy);
			var m1 = oc.util.Matrix.getTranslation(cx, cy)
			//ctx.rotate(this.rotate);
			var mr = oc.util.Matrix.getRotation(this.rotate);
			//ctx.translate(-w/2,-h/2);
			var m2 = oc.util.Matrix.getTranslation(-r.w / 2, -r.h / 2);
			var ms = oc.util.Matrix.getScale(w / r.w, h / r.h);

			var m = oc.util.Matrix.mergeTransformations([m1, mr, m2, ms]);
			return m;
		}

		private calcTransReverse(x: number, y: number, w: number, h: number, r: oc.base.Rect)
		{
			var cx = x + r.w / 2;
			var cy = y + r.h / 2;
			//ctx.translate(cx,cy);
			var ms = oc.util.Matrix.getScale(r.w / w, r.h / h);
			var m2 = oc.util.Matrix.getTranslation(r.w / 2, r.h / 2);
			var mr = oc.util.Matrix.getRotation(this.rotate);
			var m1 = oc.util.Matrix.getTranslation(-cx, -cy);

			var m = oc.util.Matrix.mergeTransformations([ms, m2, mr, m1]);
			return m;
		}

		public draw(ctx: CanvasRenderingContext2D, c: IDrawItemContainer)
		{
			var rect = this.getPrimRect();
			//console.log("rect draw "+this.getClassName()+">>rect="+rect+"  w="+this.w+" h="+this.h);
			if (rect == null)
				return;
			if (this.getW() <= 0 || this.getH() <= 0)
				return;
			var w = c.transDrawLen2PixelLen(true, this.getW());
			var h = c.transDrawLen2PixelLen(false, this.getH());
			ctx.save();
			var pt = this.getDrawXY();
			var p1 = c.transDrawPt2PixelPt(pt.x, pt.y);

			if (this.rotate != 0)
			{
				var cx = p1.x + w*this.rc_ratio_x;
				var cy = p1.y + h*this.rc_ratio_y;
				ctx.translate(cx, cy);
				ctx.rotate(this.rotate);
				ctx.translate(-w*this.rc_ratio_x, -h*this.rc_ratio_y);
			}
			else
			{
				ctx.translate(p1.x, p1.y);
			}

			var xratio = w / rect.w;
			var yratio = h / rect.h;
			if(!this.drawPrimScale(ctx,xratio,yratio))
			{
				ctx.scale(xratio, yratio);
				this.drawPrim(ctx);//call sub
			}

			ctx.restore();
		}

		public draw_sel(ctx: CanvasRenderingContext2D, c: IDrawItemContainer)
		{
			var rect = this.getPrimRect();
			if (rect == null)
				return;
			if (this.getW() <= 0 || this.getH() <= 0)
				return;
			var w = c.transDrawLen2PixelLen(true, this.getW());
			var h = c.transDrawLen2PixelLen(false, this.getH());
			var rd = 20;//c.transDrawLen2PixelLen(true,20);
			ctx.save();
			var pt = this.getDrawXY();
			var p1 = c.transDrawPt2PixelPt(pt.x, pt.y);

			if (this.rotate != 0)
			{
				var cx = p1.x + w*this.rc_ratio_x;
				var cy = p1.y + h*this.rc_ratio_y;
				ctx.translate(cx, cy);
				ctx.rotate(this.rotate);
				ctx.translate(-w*this.rc_ratio_x, -h*this.rc_ratio_y);
			}
			else
			{
				ctx.translate(p1.x, p1.y);
			}
			

			//---
			ctx.lineWidth = 1;
			ctx.strokeStyle = "red";

			ctx.beginPath();
			ctx.arc(w / 2, -rd, 3, 0, Math.PI * 2);
			ctx.fill();
			ctx.stroke();

			ctx.beginPath();
			ctx.setLineDash([5, 5]);
			ctx.moveTo(w / 2, -rd);
			ctx.lineTo(w / 2, 0);
			ctx.stroke();

			ctx.beginPath();
			ctx.setLineDash([]);
			ctx.arc(0, 0, 3, 0, Math.PI * 2);
			ctx.stroke();
			ctx.beginPath();
			ctx.arc(w, 0, 3, 0, Math.PI * 2);
			ctx.stroke();
			ctx.beginPath();
			ctx.arc(w, h, 3, 0, Math.PI * 2);
			ctx.stroke();
			ctx.beginPath();
			ctx.arc(0, h, 3, 0, Math.PI * 2);
			ctx.stroke();

			//ctx.translate(p1.x,p1.y);
			ctx.rect(0, 0, w, h);
			ctx.stroke();

			ctx.beginPath();
			//ctx.scale(w/rect.w,h/rect.h);
			ctx.scale(w / rect.w, h / rect.h);

			this.drawPrimSel(ctx);

			ctx.restore();
		}
	}

	/**
     * specal container drawitem.
     * it containers multi drawitem(not group),and can limit inner drawitem draw in it rect area
     * it can select only by border
     */
	export class DrawItemGroup extends DrawItemRect
	{
		title: string = "group";
		titleFontSize:number=40;

		public constructor(opts: {} | undefined)
		{
			super(opts);
		}

		public getClassName()
		{
			return "DrawItemGroup";
		}

		static Rect_PNSG = {
			_cat_name: "group", _cat_title: "Group",
			title: { title: "Title", type: "string"},
		};

		public getPropDefs(): Array<any>
		{
			var r = super.getPropDefs();
			r.push(DrawItemGroup.Rect_PNSG);
			return r;
		}

		/**
		 * override to set xy self and inner drawitems
		 * @param x
		 * @param y 
		 */
		public setDrawXY(x:number,y:number)
		{
			var n = this.getName();
			var lay = this.getLayer();
			if(n==null||n==""||lay==null)
			{
				super.setDrawXY(x,y);
				return ;
			}
			var gis = lay.getItemsByGroupName(n);
			if(gis.length==0)
			{
				super.setDrawXY(x,y);
				return ;
			}

			var dx = x-this.x ;
			var dy = y-this.y ;
			this.x = x ;
			this.y = y ;
			for(var gi of gis)
			{
				gi.setDrawXY(gi.x+dx,gi.y+dy);
			}
			this.MODEL_fireChged(["x","y"]) ;
		}

		public getPrimRect(): base.Rect | null
		{
			return new oc.base.Rect(0, 0, 100, 100);
		}

		public drawPrim(ctx: CanvasRenderingContext2D): void
		{
			//util.drawRect(ctx,0,0,100,100,null,null,1,"blue");
		}

		public drawPrimSel(ctx: CanvasRenderingContext2D): void
		{

		}

		public chkCanSelectDrawPt(x: number, y: number): boolean
		{
			if(this.getSelState().selected)
				return super.chkCanSelectDrawPt(x,y);
			else
				return this.chkCanSelectDrawPtBorder(x,y);
		}
        /**
         * override to make select only on border
         * @param x 
         * @param y 
         */
		private chkCanSelectDrawPtBorder(x: number, y: number): boolean
		{
			var c = this.getContainer();
			if (c == null)
				return false;
			var w = this.getW();
			var h = this.getH();
			var minv = c.transPixelLen2DrawLen(true, util.CTRL_LN_MIN_PIXEL);
			if (x > this.x && x < this.x + w && Math.abs(y - this.y) < minv)
				return true;
			if (y > this.y && y < this.y + h && Math.abs(x - this.x - w) < minv)
				return true;
			if (x > this.x && x < this.x + w && Math.abs(y - this.y - h) < minv)
				return true;
			if (y > this.y && y < this.y + h && Math.abs(x - this.x) < minv)
				return true;
			return false;
		}

		public draw(cxt: CanvasRenderingContext2D, c: IDrawItemContainer)
		{
			var pt = this.getDrawXY();
			var dxy = c.transDrawPt2PixelPt(pt.x, pt.y);
			var dxyt = c.transDrawPt2PixelPt(pt.x+10, pt.y+40);
			var dw = c.transDrawLen2PixelLen(true, this.getW());
			var dh = c.transDrawLen2PixelLen(false, this.getH());

			util.drawRectEmpty(cxt,
				dxy.x, dxy.y, dw, dh, null);
			var fs = c.transDrawLen2PixelLen(true,this.titleFontSize);
			cxt.save();
			//cxt.fontt = this.fontSize+"px serif";
			cxt.font = fs + "px serif";
			cxt.fillStyle = "#c1cccc";
			cxt.fillText(this.title,dxyt.x,dxyt.y);
			cxt.restore();
			return;
		}
	}
}



