
module oc.di
{
	export enum DILine_CTRLPT{PT1,PT_CTRL1,PT2,PT_CTRL2};

	export class DILine extends DrawItem
	{
		color: string = 'yellow';
		lnW: number = 1;
		cpx1: number = 20;
		cpy1: number = 100;
		x2: number = 200;
		y2: number = 20;
		cpx2: number = 200;
		cpy2: number = 100;
		bEndArrow:boolean=false;

		public constructor(opts:{}|undefined)
		{
			super(opts);
			//oc.DrawItem.apply(this,arguments);
		}


		static DILine_PNS = {
			_cat_name: "line", _cat_title: "Line",
			//lnTp:{title:"Line Type",type:"int",enum_val: [[0, "Normal"], [1, "Bezier"]]},
			color: { title: "color", type: "str",edit_plug:"color" },
			lnW: { title: "Line Width", type: "int" },
			cpx1: { title: "cpx1", type: "float" },
			cpy1: { title: "cpy1", type: "float" },
			x2: { title: "x2", type: "float" },
			y2: { title: "y2", type: "float" },
			cpx2: { title: "cpx2", type: "float" },
			cpy2: { title: "cpy2", type: "float" },
			bEndArrow:{ title: "end arrow", type: "bool" ,enum_val: [[false, "none"], [1, "has"]]},
		};


		public getClassName()
		{
			return "DILine";
		}

		public getPropDefs()
		{
			var r = super.getPropDefs();
			r.push(DILine.DILine_PNS)
			return r;
		}

		public getBoundPolygonDraw()
		{

			//return new oc.base.Polygon();
			return null;
		}

		static PNS:string[] = ["x","y","cpx1","cpy1","x2","y2","cpx2","cpy2"] ;

		public setDrawXY(x:number,y:number)
		{//override to change all point
			var deltax = x-this.x ;
			var deltay = y-this.y ;
			this.x = x;
			this.y = y;
			this.cpx1+=deltax;
			this.cpy1 += deltay;
			this.x2+=deltax;
			this.y2 += deltay;
			this.cpx2+=deltax;
			this.cpy2 += deltay;

			this.MODEL_fireChged(DILine.PNS) ;
		}

		public setDrawBeginXY(cont:IDrawItemContainer,x:number,y:number):boolean
		{
			this.x = x ;
			this.y = y ;
			var dx = cont.transPixelLen2DrawLen(true,2);
			var dy = cont.transPixelLen2DrawLen(false,2);
			this.cpx1 = this.x+dx;
			this.cpy1 = y;
			this.x2 = this.x+dx ;
			this.y2 = this.y+dy ;
			this.cpx2 = x;
			this.cpy2 = this.y+dy;
			this.MODEL_fireChged(DILine.PNS) ;
			return true ;
		}

		public setDrawEndXY(cont:IDrawItemContainer,x:number,y:number):base.Pt
		{
			//var xy = super.setDrawEndXY(cont,x,y);
			//x = xy.x ;
			//y = xy.y ;
			this.cpx1 = x;
			this.cpy1 = this.y;
			this.x2 = x ;
			this.y2 = y ;
			this.cpx2 = this.x;
			this.cpy2 = y;

			this.MODEL_fireChged(DILine.PNS) ;

			return {x:x,y:y} ;
		}
		

		public chkDrawPtOnCtrlPt(x:number,y:number):DILine_CTRLPT|null
		{
			var p = this.getContainer();
			if(p==null)
				return null ;
			var r = p.transPixelLen2DrawLen(true,util.CTRL_PT_R);
			if(util.chkPtInRadius(this.x,this.y,x,y,r))
				return DILine_CTRLPT.PT1;
			if(util.chkPtInRadius(this.x2,this.y2,x,y,r))
				return DILine_CTRLPT.PT2;
			if(util.chkPtInRadius(this.cpx1,this.cpy1,x,y,r))
				return DILine_CTRLPT.PT_CTRL1;
			if(util.chkPtInRadius(this.cpx2,this.cpy2,x,y,r))
				return DILine_CTRLPT.PT_CTRL2;
			return null ;
		}

		public setCtrlDrawPt(cpt:DILine_CTRLPT,x:number,y:number)
		{
			var pns:string[]|null=null;

			switch(cpt)
			{
				case DILine_CTRLPT.PT1:
					this.x = x;
					this.y = y;
					pns=["x","y"] ;
					break;
				case DILine_CTRLPT.PT2:
					this.x2 = x ;
					this.y2 = y ;
					pns=["x2","y2"] ;
					break;
				case DILine_CTRLPT.PT_CTRL1:
					this.cpx1 = x ;
					this.cpy1 = y ;
					pns=["cpx1","cpy1"] ;
					break;
				case DILine_CTRLPT.PT_CTRL2:
					this.cpx2 = x ;
					this.cpy2 = y ;
					pns=["cpx2","cpy2"] ;
					break;
			}

			this.MODEL_fireChged(pns) ;
		}

		public containDrawPt(x: number, y: number):boolean
		{//override
			var c = this.getContainer();
			if(c==null)
				return false;
			var ctx = this.getCxt();
			if(ctx==null)
				return false;

			if(this.chkDrawPtOnCtrlPt(x,y))
				return true;
			var p = c.transDrawPt2PixelPt(x, y);

			var pt = this.getDrawXY();
			var p1 = c.transDrawPt2PixelPt(pt.x, pt.y);
			var cp1 = c.transDrawPt2PixelPt(this.cpx1, this.cpy1);
			var cp2 = c.transDrawPt2PixelPt(this.cpx2, this.cpy2);
			var p2 = c.transDrawPt2PixelPt(this.x2, this.y2);

			

			ctx.save();
			//ctx.translate(0, 0);
			ctx.lineWidth = this.lnW;
			ctx.strokeStyle = "blue";

			ctx.beginPath();
			ctx.moveTo(p1.x, p1.y);
			ctx.bezierCurveTo(cp1.x, cp1.y, cp2.x, cp2.y, p2.x, p2.y);
			var r = ctx.isPointInPath(p.x, p.y);
			ctx.restore();
			return r;
		}


		public draw(ctx:CanvasRenderingContext2D,c:IDrawItemContainer)
		{
			//var c = this.getContainer();
			//if (!c)
			//	return;
			var pt = this.getDrawXY();
			var p1 = c.transDrawPt2PixelPt(pt.x, pt.y);
			var cp1 = c.transDrawPt2PixelPt(this.cpx1, this.cpy1);
			var cp2 = c.transDrawPt2PixelPt(this.cpx2, this.cpy2);
			var p2 = c.transDrawPt2PixelPt(this.x2, this.y2);
			ctx.save();
			//ctx.translate(0, 0);
			ctx.lineWidth = this.lnW;
			ctx.strokeStyle = this.color;

			ctx.beginPath();
			ctx.moveTo(p1.x, p1.y);
			ctx.bezierCurveTo(cp1.x, cp1.y, cp2.x, cp2.y, p2.x, p2.y);
			ctx.stroke();
			

			if(this.bEndArrow)
			{
				ctx.fillStyle = this.color;
				var arrlen = c.transDrawLen2PixelLen(true,20);
				var arrh = c.transDrawLen2PixelLen(true,8);
				util.drawArrow(ctx,cp2.x, cp2.y,p2.x, p2.y,arrlen,arrh);
			}

			ctx.restore();
		}

		public draw_sel(ctx:CanvasRenderingContext2D,c:IDrawItemContainer)
		{//override and draw selected by itself
			//var c = this.getContainer();
			//if (!c)
			//	return true;//
			var pt = this.getDrawXY();
			var p1 = c.transDrawPt2PixelPt(pt.x, pt.y);
			var cp1 = c.transDrawPt2PixelPt(this.cpx1, this.cpy1);
			var cp2 = c.transDrawPt2PixelPt(this.cpx2, this.cpy2);
			var p2 = c.transDrawPt2PixelPt(this.x2, this.y2);
			ctx.save();
			//ctx.translate(0, 0);
			ctx.lineWidth = this.lnW;
			ctx.strokeStyle = this.color;

			ctx.beginPath();
			ctx.moveTo(p1.x, p1.y);
			ctx.bezierCurveTo(cp1.x, cp1.y, cp2.x, cp2.y, p2.x, p2.y);
			ctx.stroke();

			ctx.beginPath();
			ctx.lineWidth = 1;
			ctx.setLineDash([5, 2]);
			ctx.strokeStyle = "yellow";

			ctx.moveTo(p1.x, p1.y);
			ctx.lineTo(cp1.x, cp1.y);
			//ctx.arc(cp1.x, cp1.y, 6, 0, Math.PI * 2, true);
			ctx.moveTo(p2.x, p2.y);
			ctx.lineTo(cp2.x, cp2.y);
			//ctx.arc(cp2.x, cp2.y, 6, 0, Math.PI * 2, true);
			ctx.stroke();
			
			ctx.beginPath();
			ctx.setLineDash([]);
			ctx.moveTo(cp1.x, cp1.y);
			ctx.arc(cp1.x, cp1.y, 6, 0, Math.PI * 2, true);
			ctx.moveTo(cp2.x, cp2.y);
			ctx.arc(cp2.x, cp2.y, 6, 0, Math.PI * 2, true);
			ctx.moveTo(p1.x, p1.y);
			ctx.arc(p1.x, p1.y, 6, 0, Math.PI * 2, true);
			ctx.moveTo(p2.x, p2.y);
			ctx.arc(p2.x, p2.y, 6, 0, Math.PI * 2, true);

			ctx.stroke();

			ctx.restore();
		}

	}
}


