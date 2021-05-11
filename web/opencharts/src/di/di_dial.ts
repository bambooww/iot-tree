
module oc.di
{
	export class DIDial extends oc.DrawItemRectR
	{

		// w: number = 100;
		// h: number = 100;

		border: number = 0;
		borderColor: string | null = "yellow";
		fillColor: string | null = null;
		radius: number | null = null;

		public constructor(opts: {} | undefined)
		{
			super(opts);
		}


		static DIRect_PNS = {
			_cat_name: "direct", _cat_title: "DI Rectangle",
			//w: { title: "width", type: "int" },
			//h: { title: "height", type: "int" },
			border: { title: "border", type: "str" },
			borderColor: { title: "borderColor", type: "str", edit_plug: "color" },
			fillColor: { title: "fillColor", type: "color", val_tp: "color", edit_plug: true },
			radius: { title: "radius", type: "int" }
		};



		public getClassName()
		{
			return "oc.di.DIDial";
		}

		public getPropDefs(): Array<any>
		{
			var r = super.getPropDefs();
			r.push(DIDial.DIRect_PNS);
			return r;
		}

		TICK_WIDTH = 30
		ANNOTATIONS_FILL_STYLE = 'rgba(230, 230, 230, 0.9)'
		ANNOTATIONS_TEXT_SIZE = 12
		TICK_LONG_STROKE_STYLE = 'rgba(100, 140, 230, 0.9)'
		TICK_SHORT_STROKE_STYLE = 'rgba(100, 140, 230, 0.7)'
		RING_INNER_RADIUS = 65
		X = 150
		Y = 150
		R = 150
		//draw one tick
		private drawTick(context: CanvasRenderingContext2D, angle: number, radius: number, cnt: number)
		{
			var tickWidth = cnt % 4 === 0 ? this.TICK_WIDTH : this.TICK_WIDTH / 2;

			context.beginPath();

			//利用三角函数确定小刻度两端的位置并连线
			context.moveTo(150 + Math.cos(angle) * (radius - tickWidth), 150 + Math.sin(angle) * (radius - tickWidth));
			context.lineTo(150 + Math.cos(angle) * radius, 150 + Math.sin(angle) * radius);

			context.strokeStyle = this.TICK_SHORT_STROKE_STYLE;
			context.stroke();
		}

		//画所有刻度线
		private drawTicks(context: CanvasRenderingContext2D)
		{
			var radius = 150,//+this.RING_INNER_RADIUS,
				ANGLE_MAX = 2 * Math.PI,
				//每个小格的角度，注意这里是除以64，因为
				ANGLE_DELTA = Math.PI / 64,
				tickWidth;

			context.save();

			for (var angle = 0, cnt = 0; angle < ANGLE_MAX; angle += ANGLE_DELTA, cnt++)
			{
				this.drawTick(context, angle, radius, cnt);
			}

			context.restore();
		}

		//添加刻度数字,通过角度计算出数字
		private drawAnnotations(context: CanvasRenderingContext2D)
		{
			var radius = 150;//+this.RING_INNER_RADIUS;

			context.save();
			context.fillStyle = this.ANNOTATIONS_FILL_STYLE;
			context.font = this.ANNOTATIONS_TEXT_SIZE + 'px Helvetica';

			for (var angle = 0; angle < 2 * Math.PI; angle += Math.PI / 8)
			{
				context.beginPath();
				context.fillText((angle * 180 / Math.PI).toFixed(0), 150 + Math.cos(angle) * (radius - this.TICK_WIDTH * 2), 150 - Math.sin(angle) * (radius - this.TICK_WIDTH * 2));
			}
			context.restore();
		}

		public getPrimRect(): oc.base.Rect | null
		{
			//var pt = this.getDrawXY();
			return new oc.base.Rect(0, 0, 300, 300);
		}

		public drawPrim(ctx: CanvasRenderingContext2D): void
		{
			//ctx.rect(0,0,100,100);
			//oc.util.drawRect(ctx, 0, 0, 100, 100, this.radius, this.fillColor, this.border, this.borderColor);
			//ctx.stroke();

			this.drawTicks(ctx);
			this.drawAnnotations(ctx);
		}
		public drawPrimSel(ctx: CanvasRenderingContext2D): void
		{

		}

	}
}


