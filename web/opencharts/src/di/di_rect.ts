
module oc.di
{
	export class DIRect extends oc.DrawItemRectR
	{
		
		// w: number = 100;
		// h: number = 100;

		border: number = 0;
		borderColor: string | null = "yellow";
		fillColor: string | null = null;
		radius: number | null = null;

		public constructor(opts:{}|undefined)
		{
			super(opts);
		}


		static DIRect_PNS = {
			_cat_name: "direct", _cat_title: "DI Rectangle",
			//w: { title: "width", type: "int" },
			//h: { title: "height", type: "int" },
			border: { title: "border", type: "str" },
			borderColor: { title: "borderColor", type: "str",edit_plug:"color" },
			fillColor: { title: "fillColor", type: "color", val_tp: "color",edit_plug:true },
			radius: { title: "radius", type: "int" }
		};



		public getClassName()
		{
			return "DIRect";
		}

		public getPropDefs(): Array<any>
		{
			var r = super.getPropDefs();
			r.push(DIRect.DIRect_PNS);
			return r;
		}

		// public getBoundRectDraw()
		// {
		// 	var pt = this.getDrawXY();
		// 	console.log("rect bound="+pt.x+" "+pt.y +" "+this.w+" "+this.h);
		// 	return new oc.base.Rect(pt.x, pt.y, this.w, this.h);
		// }

		public getPrimRect():oc.base.Rect|null
		{
			//var pt = this.getDrawXY();
			return new oc.base.Rect(0,0,100,100);
		}
		
		public drawPrim(ctx: CanvasRenderingContext2D): void
		{
			//ctx.rect(0,0,100,100);
			oc.util.drawRect(ctx, 0, 0, 100, 100, this.radius, this.fillColor, this.border, this.borderColor);
			//ctx.stroke();
		}
		public drawPrimSel(ctx: CanvasRenderingContext2D): void
		{
			
		}

		public draw0(cxt:CanvasRenderingContext2D,c:IDrawItemContainer)
		{
			//var c = this.getContainer();
			//if (!c)
			//	return;
			var pt = this.getDrawXY();
			var dxy = c.transDrawPt2PixelPt(pt.x, pt.y);
			var dw = c.transDrawLen2PixelLen(true,this.getW());
			var dh = c.transDrawLen2PixelLen(false,this.getH());

			var dr = null;
			if (this.radius != null && this.radius != undefined)
				dr = c.transDrawLen2PixelLen(true,this.radius);
			var lw = null;
			if (this.border != null && this.border != undefined)
				lw = c.transDrawLen2PixelLen(true,this.border);

			oc.util.drawRect(cxt, dxy.x, dxy.y, dw, dh, dr, this.fillColor, lw, this.borderColor);
		}
	}
}


