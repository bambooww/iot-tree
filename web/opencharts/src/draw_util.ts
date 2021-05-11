
namespace oc.util
{
	export var _tmpid: number = 0;

	export function create_new_tmp_id(): string
	{
		_tmpid++;
		if(_tmpid>100)
			_tmpid=1;
		var d = new Date();
		var tmps = 'x';
		tmps += d.getFullYear();
		var i = d.getMonth();
		if (i < 10)
			tmps += '0' + i;
		else
			tmps += i;
		i = d.getDay();
		if (i < 10)
			tmps += '0' + i;
		else
			tmps += i;
		i = d.getHours();
		if (i < 10)
			tmps += '0' + i;
		else
			tmps += i;
		i = d.getMinutes();
		if (i < 10)
			tmps += '0' + i;
		else
			tmps += i;
		i = d.getSeconds();
		if (i < 10)
			tmps += '0' + i;
		else
			tmps += i;
		tmps += "_" + _tmpid;
		return tmps;
		//return "id_"+scada_tmpid ;
	}

	export function chkEmpty(v:string|null|undefined)
	{
		return v == null || v== undefined || v == "" ;
	}

	export function chkNotEmpty(v:string|null|undefined)
	{
		return v != null && v!= undefined && v != "" ;
	}

	export function trim(n: string | null): string | null
	{
		if (n == null)
			return null;

		return n.replace(/(^\s+)|\s+$/g, '');
	}

	var msgF:any=null,errF:any=null;

	export function prompt_reg(msgfn:Function,errfn:Function|undefined)
	{
		msgF = msgfn;
		errF=errfn ;
	}

	export function prompt_msg(s:string)
	{
		if(msgF) msgF(s);
	}

	export function prompt_err(s:string)
	{
		if(errF)errF(s);
	}

	export function doAjax(url:string,pm:{},endcb:oc.base.AjaxCB)
	{
		$.ajax({
			type: 'post',
			url: url,
			data: pm
		}).done(function(ret){
			if(typeof(ret)=="string")
				ret = ret.trim();
			endcb(true,ret);
		}).fail(function(req,st,err){
			console.log(err);
			console.log(url);
			endcb(false,err);
		});
	}

	export function setDragEventData(ev:DragEvent,ps:oc.base.Props<string>)
	{
		var tf = ev.dataTransfer;
		if(tf==null)
			return;
		for(var n in ps)
		{
			var v = ps[n] ;
			tf.setData(n+"="+v,"");
		}
	}

	export function getDragEventData(ev:DragEvent):oc.base.Props<string>
	{
		var tf = ev.dataTransfer;
		if(tf==null)
			return {};
		var r:oc.base.Props<string>={};
		for(var nv of tf.types)
		{
			var k = nv.indexOf("=");
			if(k<0)
			{
				r[nv]="";
			}
			else
			{
				var n = nv.substr(0,k);
				var v = nv.substr(k+1);
				r[n]=v;
			}
		}
		return r;
	}

	export function drawRect(cxt: CanvasRenderingContext2D,
		x: number, y: number, width: number, height: number, radius: number | null,
		fillColor: string | null, borderw: number | null, bordercolor: string | null)
	{
		cxt.save();
		cxt.translate(x, y);
		if (fillColor)
		{
			//bordercolor;
			cxt.strokeStyle = cxt.fillStyle = fillColor || "#000";
			if (radius != null && radius != NaN && radius > 0)
				drawRoundRectPath(cxt, width, height, radius);
			else
				//cxt.strokeRect(0,0,width,height);
				drawRectPath(cxt, width, height);
			cxt.fill();
		}

		if (bordercolor || !fillColor || (borderw != null && borderw > 0)) //&&borderw>0)
		{
			cxt.lineWidth = borderw || 1;
			cxt.strokeStyle = bordercolor || "#000";
			if (radius != null && radius != NaN && radius > 0)
				drawRoundRectPath(cxt, width, height, radius);
			else
				drawRectPath(cxt, width, height);//cxt.strokeRect(0,0,width,height);
		}

		//this.strokeRoundRect=function(cxt, x, y, width, height, radius, lineWidth,strokeColor)

		cxt.stroke();
		cxt.restore();
	}

	export function drawRectEmpty(cxt: CanvasRenderingContext2D,
		x: number, y: number, width: number, height: number, bordercolor: string | null)
	{
		cxt.save();
		cxt.translate(x, y);

		cxt.lineWidth = 1;
		cxt.setLineDash([5, 5]);

		cxt.strokeStyle = bordercolor || "#c1cccc";

		drawRectPath(cxt, width, height);

		cxt.stroke();
		cxt.restore();
	}

	function drawRoundRectPath(cxt: CanvasRenderingContext2D, width: number, height: number, radius: number)
	{//private
		//cxt.strokeStyle = "#000";
		cxt.beginPath();
		cxt.arc(width - radius, height - radius, radius, 0, Math.PI / 2);

		cxt.lineTo(radius, height);
		cxt.arc(radius, height - radius, radius, Math.PI / 2, Math.PI);
		cxt.lineTo(0, radius);
		cxt.arc(radius, radius, radius, Math.PI, Math.PI * 3 / 2);

		cxt.lineTo(width - radius, 0);
		cxt.arc(width - radius, radius, radius, Math.PI * 3 / 2, Math.PI * 2);
		cxt.lineTo(width, height - radius);
		cxt.closePath();
	}

	function drawRectPath(cxt: CanvasRenderingContext2D, width: number, height: number)
	{//private
		//cxt.strokeStyle = "#000";
		cxt.beginPath();
		cxt.lineTo(0, 0);
		cxt.lineTo(width, 0);
		cxt.lineTo(width, height);
		cxt.lineTo(0, height);
		cxt.lineTo(0, 0);
		cxt.closePath();
	}

	/**
	 * 绘制箭头
	 * @param cxt 
	 * @param fromx 
	 * @param fromy 
	 * @param tox 
	 * @param toy 
	 * @param arrow_len 
	 * @param arrow_angle 
	 */
	export function drawArrow(cxt: CanvasRenderingContext2D,
		fromx:number,fromy:number,tox:number,toy:number,
		arrow_len:number,arrow_h:number)
	{
		cxt.save();
		cxt.translate(tox,toy);
		//cxt.fillStyle="red";
		var ang=(tox-fromx)/(toy-fromy);
		ang=Math.atan(ang);
		if(toy-fromy >= 0)
		{
		  cxt.rotate(-ang);
		}else
		{
		  cxt.rotate(Math.PI-ang);
		}
		cxt.beginPath();
		cxt.lineTo(-arrow_h,-arrow_len); 
		cxt.lineTo(0,-arrow_len/2); 
		cxt.lineTo(arrow_h,-arrow_len); 
		cxt.lineTo(0,0); 
		cxt.fill(); //
		cxt.closePath();

		cxt.restore();
	}

	export const CTRL_PT_R = 6;

	export const CTRL_LN_MIN_PIXEL = 2;

	export function chkPtInRadius(x1: number, y1: number, x2: number, y2: number, r: number)
	{
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) <= r * r;
	}

	export type Matrix_Row = number[];
	export type MatrixTable = Matrix_Row[];//[Matrix_Row,Matrix_Row,Matrix_Row];
	export type Matrix_3x3 = [Matrix_Row, Matrix_Row, Matrix_Row];

	export class Matrix
	{
		public static createMatrix(a: number, b: number, c: number, d: number, e: number, f: number): Matrix_3x3
		{//same as canvas transform params
			return [
				[a, c, e],
				[b, d, f],
				[0, 0, 1]
			];
		}

		public static createMatrixEmpty(): Matrix_3x3
		{//same as canvas transform params
			return [
				[0, 0, 0],
				[0, 0, 0],
				[0, 0, 1]
			];
		}

		public static add(m1: Matrix_3x3, m2: Matrix_3x3)
		{
			var mReturn = Matrix.createMatrixEmpty();
			if (m1.length == m2.length)
			{
				for (var row = 0; row < m1.length; row++)
				{
					//mReturn[row]=[];
					for (var column = 0; column < m1[row].length; column++)
					{
						mReturn[row][column] = m1[row][column] + m2[row][column];
					}
				}
			}
			return mReturn;
		}


		public static copyRow(mr: Matrix_Row): Matrix_Row
		{
			return [mr[0], mr[1], mr[2]];
		}

		public static copy(m: Matrix_3x3): Matrix_3x3
		{
			return [
				Matrix.copyRow(m[0]),
				Matrix.copyRow(m[1]),
				Matrix.copyRow(m[2]),
			];
		}




		/**
		 * Substract matrix m2 from m1
		 * @returns m1-m2
		 */
		public static subtract(m1: Matrix_3x3, m2: Matrix_3x3)
		{
			var mReturn = Matrix.createMatrixEmpty();
			if (m1.length == m2.length)
			{
				for (var row = 0; row < m1.length; row++)
				{
					for (var column = 0; column < m1[row].length; column++)
					{
						mReturn[row][column] = m1[row][column] - m2[row][column];
					}
				}
			}
			return mReturn;
		};


		/**
		 *Check has NaN values
		 *@returns true - if it contains NaN values, false otherwise
		 */
		public static hasNaN(m: Matrix_3x3)
		{
			for (var row = 0; row < m.length; row++)
			{
				for (var column = 0; column < m[row].length; column++)
				{
					if (isNaN(m[row][column]))
					{
						return true;
					}
				}
			}
			return false;
		};


		/**
		 * Multiply matrix m2 with m1
		 */
		public static multiply(m1: Matrix_3x3, m2: Matrix_3x3): Matrix_3x3
		{
			var mReturn: Matrix_3x3 = Matrix.createMatrixEmpty();// [];
			if (m1[0].length == m2.length)
			{//check that width=height
				for (var m1Row = 0; m1Row < m1.length; m1Row++)
				{
					//mReturn[m1Row] = [];
					for (var m2Column = 0; m2Column < m2[0].length; m2Column++)
					{
						mReturn[m1Row][m2Column] = 0
						for (var m2Row = 0; m2Row < m2.length; m2Row++)
						{
							mReturn[m1Row][m2Column] += m1[m1Row][m2Row] * m2[m2Row][m2Column];
						}
					}
				}
			}
			return mReturn;
		};

		public static transPt(m:Matrix_3x3,x:number,y:number):oc.base.Pt
		{
			var r = Matrix.multiply(m, [[x],[y],[1]]);
			return {x:r[0][0],y:r[1][0]};
		}

		/**
		 * Multiply matrix m2 with m1
		 *If you apply a transformation T to a point P the new point is:
		 *  P' = T x P
		 *So if you apply more then one transformation (T1, T2, T3) then the new point is:
		 *  P'= T3 x (T2 x (T1 x P)))
		 **/
		public static mergeTransformations(ts: Matrix_3x3[])
		{
			if (ts.length <= 0)
				throw Error("transform matrix is empty");

			var mReturn = Matrix.copy(ts[ts.length - 1]);
			for (var m = arguments.length - 2; m >= 0; m--)
			{
				mReturn = Matrix.multiply(mReturn, ts[m]);
			}
			return mReturn;
		}

		/**
		 * Inverts a matrix
		 *
		 **/
		public static invertMatrix(m: Matrix_3x3)
		{

		};

		/**
		 * Compares two matrixes
		 */
		public static equals(m1: Matrix_3x3, m2: Matrix_3x3)
		{
			if (m1.length != m2.length)
			{
				return false;
			}

			for (var i in m1)
			{
				if (m1[i].length != m2[i].length)
				{ //
					return false;
				}
				else
				{
					for (var j in m1[i])
					{
						if (m1[i][j] != m2[i][j])
						{
							return false;
						}
					}
				}
			}
			return true;
		}

		public static getRotation(angle: number):Matrix_3x3
		{
			return [
				[Math.cos(angle), -Math.sin(angle), 0],
				[Math.sin(angle), Math.cos(angle), 0],
				[0, 0, 1]];
		}

		public static getTranslation(dx: number, dy: number):Matrix_3x3
		{
			return [
				[1, 0, dx],
				[0, 1, dy],
				[0, 0, 1]
			];
		}

		public static getScale(sx: number, sy: number):Matrix_3x3
		{
			return [
				[1/sx, 0, 0],
				[0, 1/sy, 0],
				[0, 0, 1]
			];
		}

		public static setCxtRotation(cxt: CanvasRenderingContext2D, angle: number)
		{
			var cv = Math.cos(angle);
			var sv = Math.sin(angle);
			cxt.transform(cv, sv, -sv, cv, 0, 0);
		}

		/**
		 * 
		 * @param cxt 
		 * @param ax x方向放大倍数
		 * @param ay y方向放大倍数
		 */
		public static setCxtScale(cxt: CanvasRenderingContext2D, ax: number, ay: number)
		{
			cxt.transform(ax, 0, 0, ay, 0, 0);
		}

		/**
		 * 
		 * @param cxt 移动
		 * @param mv_x 
		 * @param mv_y 
		 */
		public static setCxtTranslation(cxt: CanvasRenderingContext2D, dx: number, dy: number)
		{
			cxt.transform(1, 0, 0, 1, dx, dy);
		}

	}

	export class DrawTransfer
	{
		
        public static calcRotatePt(p:oc.base.Pt, pcenter:oc.base.Pt, angle:number):oc.base.Pt
        {
            // calc arc 
            var ang = ((angle * Math.PI) / 180);
            
            //sin/cos value
            var cosv = Math.cos(ang);
            var sinv = Math.sin(ang);
     
            // calc new point
            var rx = ((p.x - pcenter.x) * cosv - (p.y - pcenter.y) * sinv + pcenter.x);
            var ry = ((p.x - pcenter.x) * sinv + (p.y - pcenter.y) * cosv + pcenter.y);
            return {x:rx, y:ry};
        }

	}
		//arr [r,g,b]  hsv H(hues)表示色相，S(saturation)表示饱和度，B（brightness）
	export function transRGB2HSV(arr:number[]):number[]
	{
		var h = 0, s = 0, v = 0;
		var r = arr[0], g = arr[1], b = arr[2];
		arr.sort((a, b)=> {
			return a - b;
		})
		var max = arr[2]
		var min = arr[0];
		v = max / 255;
		if (max === 0) {
			s = 0;
		} else {
			s = 1 - (min / max);
		}
		if (max === min) {
			h = 0;// max===min h can any value
		} else if (max === r && g >= b) {
			h = 60 * ((g - b) / (max - min)) + 0;
		} else if (max === r && g < b) {
			h = 60 * ((g - b) / (max - min)) + 360
		} else if (max === g) {
			h = 60 * ((b - r) / (max - min)) + 120
		} else if (max === b) {
			h = 60 * ((r - g) / (max - min)) + 240
		}
		h = parseInt(""+h);
		s = parseInt(""+s * 100);
		v = parseInt(""+v * 100);
		return [h, s, v]
	}

	export function transHSV2RGB(arr:number[]):number[]
	{
		var h = arr[0], s = arr[1], v = arr[2];
		s = s / 100;
		v = v / 100;
		var r = 0, g = 0, b = 0;
		var i = parseInt(""+((h / 60) % 6));
		var f = h / 60 - i;
		var p = v * (1 - s);
		var q = v * (1 - f * s);
		var t = v * (1 - (1 - f) * s);
		switch (i) {
			case 0:
				r = v; g = t; b = p;
				break;
			case 1:
				r = q; g = v; b = p;
				break;
			case 2:
				r = p; g = v; b = t;
				break;
			case 3:
				r = p; g = q; b = v;
				break;
			case 4:
				r = t; g = p; b = v;
				break;
			case 5:
				r = v; g = p; b = q;
				break;
			default:
				break;
		}
		r = parseInt(""+r * 255.0)
		g = parseInt(""+g * 255.0)
		b = parseInt(""+b * 255.0)
		return [r, g, b];
	}
}



