/**
 * @module oc/base
 */



module oc.base
{
	
	export const OC_DRAW_PANEL="_oc_drawpanel";
	
	export type Pt = { x: number, y: number };

	export type Size={w:number,h:number};

	export type Props<TV> = { [index: string]: TV }

	export type Pt2=[number,number];

	/**
	 * a drawitem current selection state
	 */
	export type ItemSelState={selected:boolean,dragover:boolean}

	//export type Props= {[index:string]: any}

	export type AjaxCB=(bsucc:boolean,ret:string|{})=>void;

	//callback with no parameter
	export type CB_P0=()=>void;

	export function forceCast<T>(input: any): T {

		// ... do runtime checks here
	  
		// @ts-ignore <-- forces TS compiler to compile this as-is
		return input;
	  }

	export class Rect
	{
		x: number = 0;
		y: number = 0;
		w: number = 0;
		h: number = 0;

		public constructor(x: number, y: number, w: number, h: number)
		{
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}

		public static createByPt(minpt: [number, number], maxpt: [number, number]): Rect
		{
			return new Rect(minpt[0], minpt[1], maxpt[0] - minpt[0], maxpt[1] - minpt[1]);
		}

		public static copy(r:Rect): Rect
		{
			return new Rect(r.x,r.y,r.w,r.h);
		}

		public static createEmpty(): Rect
		{
			return new Rect(0, 0, 0, 0);
		}

		// public constructor()
		// {

		// 	if (arguments.length == 4)
		// 	{
		// 		this.x = arguments[0];
		// 		this.y = arguments[1];
		// 		this.w = arguments[2];
		// 		this.h = arguments[3];
		// 	}
		// 	else if (arguments.length == 1)
		// 	{
		// 		//alert(arguments[0] instanceof oc.base.Rect);
		// 		//{
		// 		this.x = arguments[0].x;
		// 		this.y = arguments[0].y;
		// 		this.w = arguments[0].w;
		// 		this.h = arguments[0].h;
		// 		//}

		// 	}
		// }

		public getMaxX(): number
		{
			return this.x + this.w;
		}

		public equals(r:Rect|null)
		{
			if(r==null)
				return false;
			return this.x==r.x && this.y==r.y && this.w==r.w && this.h==r.h ;
		}

		public getMaxY(): number
		{
			return this.y + this.h;
		}

		public setMaxX(mx: number): void
		{
			if (mx <= this.x)
				throw "invalid max x";
			this.w = mx - this.x;
		}

		public setMaxY(my: number): void
		{
			if (my <= this.y)
				throw "invalid max y";
			this.h = my - this.y;
		}

		public getCenter():base.Pt
		{
			return {x:this.x+this.w/2,y:this.y+this.h/2};
		}

		public isValid(): boolean
		{
			return this.w > 0 || this.h > 0;
		}



		//expand by overlapped other rect
		public expandBy(r: Rect): void
		{
			if (!r.isValid())
				return;
			if (!this.isValid())
			{
				this.x = r.x;
				this.y = r.y;
				this.w = r.w;
				this.h = r.h;
				return;
			}

			if (r.getMaxX() > this.getMaxX())
				this.setMaxX(r.getMaxX());
			if (r.getMaxY() > this.getMaxY())
				this.setMaxY(r.getMaxY());
			if (r.x < this.x)
			{
				this.w += this.x-r.x ;
				this.x = r.x;
			}
			if (r.y < this.y)
			{
				this.h += this.y-r.y;
				this.y = r.y;
			}	
		}

		public expandToSquareByCenter():Rect
		{
			var d = this.w>this.h?this.w:this.h;
			var c = this.getCenter();
			return new Rect(c.x-d/2,c.y-d/2,d,d);
		}

		public contains(X: number, Y: number): boolean
		{
			var w = this.w;
			var h = this.h;
			if (w < 0 || h < 0)
			{
				// At least one of the dimensions is negative...
				return false;
			}
			// Note: if either dimension is zero, tests below must return false...
			var x = this.x;
			var y = this.y;
			if (X < x || Y < y)
			{
				return false;
			}
			w += x;
			h += y;
			// overflow || intersect
			return ((w < x || w > X) && (h < y || h > Y));
		}

		public listFourPt():base.Pt[]
		{
			return [{x:this.x,y:this.y},{x:this.x+this.w,y:this.y}
				,{x:this.x+this.w,y:this.y+this.h},{x:this.x,y:this.y+this.h}];
		}

		public toStr(): string
		{
			return "[" + this.x + "," + this.y + "," + this.w + "," + this.h + "]";
		}

	}//end of rect

	export class Pts
	{
		protected pts:Pt[]=[];

		protected bound: Rect | null=null;

		public constructor()
		{
			if (arguments.length == 1)
			{
				var pts = arguments[0];
				if (pts instanceof Array)
				{
					if (pts == null || pts.length < 3)
						throw "pts num must >=3";
					for (var i = 0; i < pts.length; i++)
					{
						var pt = pts[i];
						if (pt instanceof Array)
							this.pts.push({ x: pt[0], y: pt[1] });
						else
							this.pts.push(pt);
					}
				}
			}
			else
			{
				for (var i = 0; i < arguments.length; i++)
				{
					var pt = arguments[i]
					if (pt instanceof Array)
						this.pts.push({ x: pt[0], y: pt[1] });
					else
						this.pts.push(pt);
				}
			}
		}


		public getFirst():Pt|null
		{
			if (this.pts.length <= 0)
				return null;
			return this.pts[0];
		}

		public getLast():Pt|null
		{
			if (this.pts.length <= 0)
				return null;
			return this.pts[this.pts.length - 1];
		}

		public getPt(idx:number):Pt|null
		{
			if(idx<0||idx>=this.pts.length)
				return null ;
			return this.pts[idx];
		}

		public addPt(x: number, y: number)
		{
			this.pts.push({ x: x, y: y });

			this.bound = this.calculateBounds();
		}

		public chgPt(idx:number,x:number,y:number):boolean
		{
			if(idx<0||idx>=this.pts.length)
				return false;
			this.pts[idx].x = x ;
			this.pts[idx].y= y ;
			this.bound = this.calculateBounds();
			return true;
		}

		public getPts():Pt[]
		{
			return this.pts;
		}

		public setPts(pts:Pt[])
		{
			if(pts==null||pts==undefined)
				this.pts = [] ;
			else
				this.pts = pts ;
			this.calculateBounds();
		}

		public getPtNum():number
		{
			return this.pts.length ;
		}

		public calculateBounds(): Rect|null
		{
			if(this.pts.length<=2)
				return null ;

			let minx: number = Number.MAX_VALUE;
			let miny: number = Number.MAX_VALUE;
			let maxx: number = Number.NEGATIVE_INFINITY;
			let maxy: number = Number.NEGATIVE_INFINITY;

			for (var p of this.pts)
			{
				var x = p.x;
				if(minx>x)
					minx = x ;
				if(maxx<x)
					maxx = x ;
				//minx = Math.min(minx, x);
				//maxx = Math.max(maxx, x);
				var y = p.y;
				if(miny>y)
					miny = y ;
				if(maxy<y)
					maxy = y;
				//miny = Math.min(miny, y);
				//maxy = Math.max(maxy, y);
				//console.
			}
			return new Rect(minx, miny, maxx - minx, maxy - miny);
		}

		public getBoundingBox(): Rect | null
		{
			if (this.pts.length == 0)
			{
				return null;
			}
			if (this.bound == null)
			{
				this.bound = this.calculateBounds();
			}
			return this.bound;
		}

		public movePt(dx:number,dy:number)
		{
			for(var pt of this.pts)
			{
				pt.x += dx ;
				pt.y += dy ;
			}
			this.bound = this.calculateBounds();
		}

		public toStr(): string
		{
			if (this.pts.length <= 0)
				return "";
			var ret = "(" + this.pts[0].x + "," + this.pts[0].y + ")";
			for (var i = 1; i < this.pts.length; i++)
			{
				ret += "," + "(" + this.pts[i].x + "," + this.pts[i].y + ")";
			}
			return ret;
		}
	}

	export class Polygon extends Pts
	{
		public constructor()
		{
			super();
		}

		public isValid():boolean
		{
			return this.pts.length>=3 ;
		}
		
		public contains(x: number, y: number): boolean
		{
			if (this.pts.length < 3)
				return false;

			let bbox = this.getBoundingBox();
			if (bbox == null || !bbox.contains(x, y))
			{
				//console.log(bbox,"x=",x,"y=",y)
				return false;
			}
				
			var hits = 0;

			var n = this.pts.length - 1;
			var tmppts = this.pts;
			if(n==2)
			{//add a pt
				tmppts=[];
				for(var pt of this.pts)
					tmppts.push(pt) ;
				tmppts.push(this.pts[2]) ;
				n ++ ;
			}
			var lastpt = tmppts[n];
			var lastx = lastpt.x;
			var lasty = lastpt.y;
			var curx, cury;

			// Walk the edges of the polygon
			for (var i = 0; i < n; lastx = curx, lasty = cury, i++)
			{
				var p = tmppts[i];
				curx = p.x;// xpoints[i];
				cury = p.y;// ypoints[i];

				if (cury == lasty)
				{
					continue;
				}

				var leftx;
				if (curx < lastx)
				{
					if (x >= lastx)
					{
						continue;
					}
					leftx = curx;
				}
				else
				{
					if (x >= curx)
					{
						continue;
					}
					leftx = lastx;
				}

				var test1, test2;
				if (cury < lasty)
				{
					if (y < cury || y >= lasty)
					{
						continue;
					}
					if (x < leftx)
					{
						hits++;
						continue;
					}
					test1 = x - curx;
					test2 = y - cury;
				}
				else
				{
					if (y < lasty || y >= cury)
					{
						continue;
					}
					if (x < leftx)
					{
						hits++;
						continue;
					}
					test1 = x - lastx;
					test2 = y - lasty;
				}

				if (test1 < (test2 / (lasty - cury) * (lastx - curx)))
				{
					hits++;
				}
			}

			return hits % 2 == 1;//((hits & 1) != 0);
		}
	}

	export var createPolygonByPt2=function(pts:oc.base.Pt2[]):Polygon
	{
		if(pts.length<3)
			throw Error("pts length must > 2");
		var r = new Polygon();
		for(var pt of pts)
		{
			r.addPt(pt[0],pt[1]);
		}
		return r;
	}


	export class Fill
	{
		tp:string="";
		color:string="" ;
		colors:string[]=[];
		rotate:number=0 ;
		pat_sty:string="" ;

		private calNor()
		{

		}

		private getColorsStr():string
		{
			if(this.colors.length<=0)
				return "";
			var r = "" ;
			for(var c of this.colors)
				r += "|"+c;
			return r.substr(1) ;
		}

		public toStr():string
		{
			switch(this.tp)
			{
				case "nor":
					return "nor;"+this.color;
				case "g_lin":
					return "g_lin;"+this.getColorsStr()+";"+this.rotate ;
				case "g_rad":
					return "g_rad;"+this.getColorsStr() ;
				case "pat":
					return "pat;"+this.getColorsStr() +";"+this.pat_sty;
			}
			return "" ;
		}

		public static parseStr(str:string):Fill|null
		{
			var ss = str.split(";");
			if(ss.length<2)
				return null ;
			switch(ss[0])
			{
				case "nor":
					return Fill.createNor(ss[1]);
				case "g_lin":
					var r = 0 ;
					if(ss.length>=3)
						r = parseInt(ss[2]) ;
					return Fill.createLinearG(ss[1].split("|"),r) ;
				case "g_rad":
					return Fill.createRadialG(ss[1].split("|")) ;
				case "pat":
					if(ss.length<3)
						return null ;
					var cs = ss[1].split("|");
					if(cs.length!=2)
						return null ;
					return Fill.createPattern(cs[0],cs[1],ss[2]) ;
			}
			return null ;
		}

		public static createNor(color:string):Fill
		{
			var f = new Fill();
			f.tp = "nor" ;
			f.color = color;
			return f ;
		}

		public static createLinearG(colors:string[],rotate:number):Fill
		{
			var f = new Fill();
			f.tp = "g_lin" ;
			f.colors = colors; //"#xxxxx|rgba(12,21,22)"
			f.rotate = rotate;
			return f ;
		}

		public static createRadialG(colors:string[]):Fill
		{
			var f = new Fill();
			f.tp = "g_rad" ;
			f.colors = colors;
			return f ;
		}

		public static createPattern(color1:string,color2:string,ptsty:string)
		{
			var f = new Fill();
			f.tp = "pat" ;
			f.colors = [color1,color2];
			f.pat_sty = ptsty ;
			return f ;
		}

		private calLinearPts(r:oc.base.Rect,rotate:number):oc.base.Pt[]
		{
			rotate %= 360 ;
			var cr = rotate*Math.PI/180 ;
			var ang1 = Math.atan(r.h/r.w);
			
			if(cr<=ang1||cr>=2*Math.PI-ang1)
			{//left right
				var dh = r.w/2*Math.tan(cr) ;
				var p2 = {x:r.x+r.w,y:r.y+r.h/2+dh} ;
				var p1 = {x:r.x,y:r.y+r.h/2-dh} ;
				return [p1,p2] ;
			}
			else if(cr>ang1 && cr<=Math.PI-ang1)
			{//top btm
				var dw = Math.tan(Math.PI/2-cr)*r.h/2 ;
				var p2 = {x:r.x+r.w/2+dw,y:r.y+r.h} ;
				var p1 = {x:r.x+r.w/2-dw,y:r.y} ;
				return [p1,p2] ;
			}
			else if(cr>=Math.PI-ang1&&cr<=Math.PI+ang1)
			{//
				var dh = r.w/2*Math.tan(cr) ;
				var p1 = {x:r.x+r.w,y:r.y+r.h/2+dh} ;
				var p2 = {x:r.x,y:r.y+r.h/2-dh} ;
				return [p1,p2] ;
			}
			else
			{
				var dw = Math.tan(Math.PI/2-cr)*r.h/2 ;
				var p1 = {x:r.x+r.w/2+dw,y:r.y+r.h} ;
				var p2 = {x:r.x+r.w/2-dw,y:r.y} ;
				return [p1,p2] ;
			}
		}


		public calCxtFillStyle(r:oc.base.Rect,cxt:CanvasRenderingContext2D):string|CanvasGradient|CanvasPattern
		{
			switch(this.tp)
			{
				case "nor":
					return this.color;
				case "g_lin":
					var pts = this.calLinearPts(r,this.rotate);
					var linear = cxt.createLinearGradient(pts[0].x,pts[0].y,pts[1].x,pts[1].y);
					var cn = this.colors.length ;
					for(var i = 0 ; i < cn ; i ++)
					{
						var st = 1.0*i/(cn-1);
						linear.addColorStop(st,this.colors[i]);
					}
					return linear ;
				case "g_rad":
					//var rad = cxt.createRadialGradient()
					
				case "pat":
					//cxt.createPattern() ;
			}
			
			return this.color ;
			
		}

		private static calPipeLinearPts(x1:number,y1:number,x2:number,y2:number,ln_w:number):oc.base.Pt[]
		{
			var d = ln_w/2 ;
			if(y1==y2)
			{//
				var p1:oc.base.Pt = {x:x1-d,y:y1};
				var p2:oc.base.Pt = {x:x1+d,y:y1} ;
				return [p1,p2];
			}

			var a = Math.atan((y2-y1)/(x2-x1)) ;
			var dx = d*Math.sin(a) ;
			var dy = d*Math.cos(a) ;
			var p1:oc.base.Pt = {x:x1-dx,y:y1+dy};
			var p2:oc.base.Pt = {x:x1+dx,y:y1-dy} ;
			return [p1,p2];
		}

		public static calCxtPipeFillStyle(x1:number,y1:number,x2:number,y2:number,ln_w:number,
			color:string,cxt:CanvasRenderingContext2D):CanvasGradient|null
		{
			if(x1==x2&&y1==y2)
				return null ;
			//cal line pipe 
			var pts = Fill.calPipeLinearPts(x1,y1,x2,y2,ln_w)
			var linear = cxt.createLinearGradient(pts[0].x,pts[0].y,pts[1].x,pts[1].y);
			cxt.createRadialGradient
			linear.addColorStop(0,"#000");
			linear.addColorStop(0.5,color);
			linear.addColorStop(1,"#000");
			return linear ;
		}
	}
}









