
module oc.di
{
	/**
	 * support pic svg etc.
	 */
	export class DIImg extends oc.DrawItemRectR
	{
		
		imgPath:string|null=null;
		imgRes:string|null=null;
		alpha:number=1.0;
		//border: number = 0;
		//borderColor: string | null = "yellow";
		//fillColor: string | null = null;
		//radius: number | null = null;

		private img:any=null;
		private lastImgPath:string|null=null;

		public constructor(opts:{}|undefined)
		{
			super(opts);
			if(opts!=undefined)
            {
				var imgp = opts["imgPath"];
				if(imgp!=undefined&&imgp!=null&&imgp!="")
				{
					this.imgPath=imgp;
				}
			}
		}


		static DIImg_PNS = {
			_cat_name: "img", _cat_title: "DI Image",
			//border: { title: "border", type: "str" },
			//borderColor: { title: "borderColor", type: "str" },
			//fillColor: { title: "fillColor", type: "str", val_tp: "color" },
			alpha: { title: "Alpha", type: "float" },
			imgPath:{title:"Image Path",type:"str"},
			imgRes:{title:"Image Res",type:"str",edit_plug:"imgres"}
		};



		public getClassName()
		{
			return "DIImg";
		}

		public getPropDefs(): Array<any>
		{
			var r = super.getPropDefs();
			r.push(DIImg.DIImg_PNS);
			return r;
		}

		public drawPrim(cxt: CanvasRenderingContext2D): void
		{
			//var dxy = this.getDrawXY();
			//console.log("img>>"+dxy.x+" "+dxy.y);
			var path = null;
			if(this.imgRes!=null&&this.imgRes!="")
			{
				path = this.getDrawRes()?.getDrawResUrl(this.imgRes) ;
				//path = '/admin/util/rescxt_show_img.jsp?resid='+this.imgRes ;
			}
			
			if(!path)
			{
				if(this.imgPath==null||this.imgPath=="")
					return ;
				path = this.imgPath ;
			}
			if(!path)
				return ;
				
			//cxt.save();
			cxt.globalAlpha = this.alpha;
			if(this.img!=null&&this.lastImgPath==path)
			{
				cxt.drawImage(this.img, 0, 0, 100, 100);
			}
			else
			{
				var ii = new Image();
				ii.onload=()=>{
					this.img = ii ;
					this.MODEL_fireChged([]);
					//cxt.drawImage(ii, 0, 0, 100, 100);
				};
				ii.src=this.lastImgPath=path;
			}
			//cxt.restore();
		}

		
		public setImgResInCxt(name:string)
		{

		}

		public drawPrimSel(ctx: CanvasRenderingContext2D): void
		{
			
		}

		public getPrimRect(): base.Rect | null
		{
			return new oc.base.Rect(0,0,100,100);
		}


		public draw0(cxt: CanvasRenderingContext2D,c:IDrawItemContainer)
		{
			//var c = this.getContainer();
			//if (!c)
			//	return;
			if(this.imgPath==null||this.imgPath=="")
				return ;
			var pt = this.getDrawXY();
			var dxy = c.transDrawPt2PixelPt(pt.x, pt.y);
			var dw = c.transDrawLen2PixelLen(true,this.getW());
			var dh = c.transDrawLen2PixelLen(false,this.getH());

			cxt.save();
			cxt.globalAlpha = this.alpha;
			if(this.img!=null&&this.lastImgPath==this.imgPath)
			{
				cxt.drawImage(this.img, dxy.x, dxy.y, dw, dh);
			}
			else
			{
				var ii = new Image();
				var me = this;
				ii.onload=function(){
					cxt.drawImage(ii, dxy.x, dxy.y, dw, dh);
					
					me.img = ii ;
				};
				ii.src=this.lastImgPath=this.imgPath;
			}
			cxt.restore();
		}
	}
}


