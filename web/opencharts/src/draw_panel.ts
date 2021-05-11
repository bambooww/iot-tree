/**
 * @module ol/DrawPanel
 */


module oc
{
	export interface IDrawPanelMouseMV
	{
		on_mouse_mv(pxy: base.Pt, dxy: base.Pt): void;
	}

	export interface IModelListener
	{
		on_model_chged(panel: DrawPanel, layer: DrawLayer, item: DrawItem[]|DrawItem | null, prop_names: string[] | null): void;

		//interact and oper is a part of model
		on_model_oper_chged(panel: DrawPanel, intera: DrawInteract, oper: DrawOper | null): void;

		on_model_sel_chged(panel: DrawPanel, layer: DrawLayer, item: DrawItem | null): void;
	}

	export abstract class AbstractDrawPanel implements IPopMenuPanel
	{
		drawView:DrawView|null = null ;

		layers: DrawLayer[] = [];
		drawCenter: base.Pt = { x: 0.0, y: 0.0 };
		drawResolution: number = 1.0;//分辨率  1/放大倍数

		
		pixelSize: base.Size;


		public constructor()
		{
			this.pixelSize = { w: 100, h: 100 };
		}

		public abstract getHTMLElement(): HTMLElement;

		public abstract delPopMenu(): void;
		public abstract setPopMenu(menuele: JQuery<HTMLElement>): void;

		public getDrawView():DrawView|null
		{
			return this.drawView;
		}

		public abstract getInteract():DrawInteract|null;

		
		public on_draw()
		{
			for (var i = 0; i < this.layers.length; i++)
			{
				if (!this.layers[i].vis(undefined))
					continue;
				//this.layers[i].on_draw();
				this.layers[i].update_draw();
			}
		}

		public clear_draw()
		{
			for (var i = 0; i < this.layers.length; i++)
			{
				var cxt = this.layers[i].clear_draw();
			}
		}

		public update_draw()
		{
			this.clear_draw();
			this.on_draw();
		}

		
		public setPixelSize(sz: base.Size)
		{
			this.pixelSize = sz;
			for (var i = 0; i < this.layers.length; i++)
			{
				var c = this.layers[i].getCanvasEles();
				c[0].setAttribute("width", "" + sz.w);
				c[0].setAttribute("height", "" + sz.h);
				//c[1].setAttribute("width", "" + sz.w);
				//c[1].setAttribute("height", "" + sz.h);
			}
			//console.log(sz);
			this.update_draw();
		}

		
		public getPixelSize()
		{
			return this.pixelSize;
		}

		public updatePixelSize()
		{
			this.setPixelSize(this.calcPixelSize());
		}

		protected calcPixelSize()
		{
			var tarele = this.getHTMLElement() ;
			var computedStyle: CSSStyleDeclaration = getComputedStyle(tarele);
			var w = tarele.offsetWidth -
				this.parseToFloat(computedStyle.borderLeftWidth) -
				this.parseToFloat(computedStyle.paddingLeft) -
				this.parseToFloat(computedStyle.paddingRight) -
				this.parseToFloat(computedStyle.borderRightWidth);

			var h = tarele.offsetHeight -
				this.parseToFloat(computedStyle.borderTopWidth) -
				this.parseToFloat(computedStyle.paddingTop) -
				this.parseToFloat(computedStyle.paddingBottom) -
				this.parseToFloat(computedStyle.borderBottomWidth);
			if(w==NaN)
			{
				console.log("cal pix size err") ;
			}
			return { w: w, h: h };
		}

		
		public addLayer(lay: DrawLayer)
		{
			this.layers.push(lay);
			lay.setPanel(this);
			var cans = lay.getCanvasEles();
			this.getHTMLElement().appendChild(cans[0]);
			//this.tarEle.appendChild(cans[1]);
			var cxt = lay.getCanvasCxts();
			var pixsz = this.getPixelSize() ;
			//var c = lay.getCanvasEle();

			cans[0].setAttribute("width", "" + pixsz.w);
			cans[0].setAttribute("height", "" + pixsz.h);
			cans[0].setAttribute("tabindex", "1");//enable key event

			cxt[0].fillStyle = "rgba(0,0,0,0.0)";
			cxt[0].fillRect(0, 0, pixsz.w, pixsz.w);

			this.update_draw();
		}

		public getLayerByName(n: string): DrawLayer | null
		{
			for (var i = 0; i < this.layers.length; i++)
			{
				if (this.layers[i].name == n)
					return this.layers[i];
			}
			return null;
		}


		public getLayer():DrawLayer
		{
			return this.layers[0] ;
		}

		public getPixelCenter()
		{
			var ps = this.getPixelSize();
			return { x: ps.w / 2, y: ps.h / 2 };
		}


		public getDrawCenter()
		{
			return this.drawCenter;
		}

		public getDrawResolution()
		{
			return this.drawResolution;
		}

		public setDrawCenter(x: number, y: number)
		{
			this.drawCenter.x = x;
			this.drawCenter.y = y;
		}

		public moveDrawCenter(deta_x: number, deta_y: number)
		{
			this.drawCenter.x += deta_x;
			this.drawCenter.y += deta_y;

			this.update_draw();
		}

		public movePixelCenter(deta_x: number, deta_y: number)
		{
			var dx = this.transPixelLen2DrawLen(true, deta_x);
			var dy = this.transPixelLen2DrawLen(true, deta_y);
			this.moveDrawCenter(dx, dy);
		}

		public ajustDrawResolution(dx: number, dy: number, delta: number)
		{
			var pix_pt = this.transDrawPt2PixelPt(dx, dy);
			if (delta > 0)
			{
				this.drawResolution *= 1.2;
			}
			else
			{
				this.drawResolution /= 1.2;
			}
			var newdpt = this.transPixelPt2DrawPt(pix_pt.x, pix_pt.y);
			this.drawCenter.x += (dx - newdpt.x);
			this.drawCenter.y += (dy - newdpt.y);
			this.update_draw();
		}


		public ajustDrawFitInRect(rect: oc.base.Rect)
		{
			this.drawCenter = rect.getCenter();
			var ps = this.getPixelSize();
			var res = rect.w / ps.w;
			var res2 = rect.h / ps.h;
			this.drawResolution = Math.max(res, res2);
			this.update_draw();
		}

		public ajustDrawToInit()
		{
			this.drawCenter.x = 0;
			this.drawCenter.y = 0;
			this.drawResolution = 1;
			this.update_draw();
		}

		public getXYResolution(): XYRes
		{
			return { x_res: this.drawResolution, y_res: this.drawResolution };
		}

		public transPixelPt2DrawPt(px: number, py: number): base.Pt
		{//pixel pt to draw pt
			var pc = this.getPixelCenter();
			var dx = (px - pc.x) * this.drawResolution + this.drawCenter.x;
			//var dy = (py-pc.y)*this.drawResolution+this.drawCenter.y;
			var dy = this.drawCenter.y - (pc.y - py) * this.drawResolution;
			//var dy=(this.getPixelHeight()/2-py)*this.drawResolution+this.drawCenter.y;
			return { x: dx, y: dy };
		}

		public transDrawPt2PixelPt(dx: number, dy: number): base.Pt
		{
			var pc = this.getPixelCenter();
			var px = (dx - this.drawCenter.x) / this.drawResolution + pc.x;
			//var py = (dx-this.drawCenter.y)/this.drawResolution+pc.y;
			var py = pc.y - (this.drawCenter.y - dy) / this.drawResolution;
			//var py=this.getPixelHeight()/2-(dy-this.drawCenter.y)/this.drawResolution;
			px = Math.round(px);
			py = Math.round(py);
			return { x: px, y: py };
		}

		public transDrawLen2PixelLen(b_xres: boolean, len: number): number
		{
			return Math.round(len / this.drawResolution);
		}

		public transPixelLen2DrawLen(b_xres: boolean, len: number): number
		{
			return len * this.drawResolution;
		}

		protected parseToFloat(strv: string | null)
		{
			if (strv == null)
				return 0;
			return parseFloat(strv);
		}

		public getEventPixel(e: any): base.Pt
		{
			var r = this.getHTMLElement().getBoundingClientRect();
			return { x: e.clientX - r.left, y: e.clientY - r.top };
		}


	}

	export class DrawPanel extends AbstractDrawPanel implements IModelListener,IPopMenuPanel
	{
		

		tarEle: HTMLElement;
		popMenuEle:JQuery<HTMLElement>|null=null ;

		
		interact: DrawInteract | null = null;

		private modelListeners: IModelListener[] = [];

		private drawRes:IDrawRes|null=null ;

		on_mouse_mv: ((pxd: base.Pt, dxy: base.Pt) => void) | null = null;
		on_item_sel_chg:((item: DrawItem|null) => void) | null = null;
		on_model_chg:(() => void) | null = null;

		bModelDirty:boolean=false;

		private timeoutDownLong:number|null=null ;

		public constructor(target: string|HTMLElement, opts: {})
		{
			super();

			if (!opts)
				opts = {};

			this.on_mouse_mv = opts["on_mouse_mv"] ? opts["on_mouse_mv"] : null;
			this.on_item_sel_chg = opts["on_item_sel_chg"] ? opts["on_item_sel_chg"] : null;
			this.on_model_chg= opts["on_model_chg"] ? opts["on_model_chg"] : null;

			if(typeof(target)=="string" )
				this.tarEle = <HTMLCanvasElement>document.getElementById(target);
			else
				this.tarEle = target;
			//this.tarEle.getContext()
			


			this.layers = (opts["layers"] ? opts["layers"] : []);
			if (this.layers == null)
				this.layers = [];

			this.interact = null;//[new oc.interact.OperDrag()];
			if (opts["interact"])
				this.interact = opts["interact"];

			this.setCursor();

			this.tarEle[oc.base.OC_DRAW_PANEL] = this;

			//register self
			this.MODEL_registerListener(this);
		}

		/**
		 * name
		 */
		public getHTMLElement(): HTMLElement
		{
			return this.tarEle;
		}
		//this.init_me();

		public delPopMenu()
		{
			if(this.popMenuEle==null)
				return ;
			$(this.popMenuEle).remove();
			this.popMenuEle = null ;
		}

		public setPopMenu(menuele:JQuery<HTMLElement>)
		{
			this.delPopMenu();
			this.popMenuEle = menuele ;
			$(this.tarEle).append(menuele);
		}

		public getDrawRes():IDrawRes|null
		{
			return this.drawRes ;
		}

		public setDrawRes(dr:IDrawRes|null)
		{
			this.drawRes = dr ;
		}


		public setCursor(c: oc.Cursor = oc.Cursor.auto)
		{//auto | crosshair | default | hand | move | help | wait | text | w-resize |s-resize | n-resize |e-resize | ne-resize |sw-resize | se-resize | nw-resize |pointer | url 
			if(this.tarEle==null)
				return ;
				
			var n = oc.Cursor[c];
			var k = n.indexOf('_');
			if (k >= 0)
				n = n.substring(0, k) + '-' + n.substring(k + 1);
			//console.log(" set cursor="+n) ;
			this.tarEle.style.cursor = n;
		}



		public MODEL_registerListener(lis: IModelListener)
		{
			this.modelListeners.push(lis);
		}


		on_model_chged(panel: DrawPanel, layer: DrawLayer, item: DrawItem[] | null, prop_names: string[] | null): void
		{//panel implements IModelListener
			try
			{
				if (layer != null)
					layer.update_draw();
				else
					panel.update_draw();
			}
			catch(ee)//firefox may cause error
			{}

			this.bModelDirty=true;
			if(this.on_model_chg!=null)
				this.on_model_chg() ;
		}

		/**
		 * support model chged notify
		 */
		public isModelDirty():boolean
		{
			return this.bModelDirty ;
		}

		public setModelDirty(b:boolean)
		{
			this.bModelDirty = b;
		}

		on_model_oper_chged(panel: DrawPanel, intera: DrawInteract, oper: DrawOper | null): void
		{
			panel.update_draw();
		}

		on_model_sel_chged(panel: DrawPanel, layer: DrawLayer, item: DrawItem | null): void
		{
			layer.update_draw();
		}

		public MODEL_fireChged(layer: DrawLayer, item: DrawItem[]|DrawItem | null, prop_names: string[] | null)
		{
			for (var lis of this.modelListeners)
			{
				lis.on_model_chged(this, layer, item, prop_names);
			}
		}

		public MODEL_fireOperChged(intera: DrawInteract, oper: DrawOper | null)
		{
			for (var lis of this.modelListeners)
			{
				lis.on_model_oper_chged(this, intera, oper);
			}
		}

		public MODEL_fireSelectedChged(intera: DrawInteract)
		{
			var curitem = intera.getSelectedItem();
			if(this.on_item_sel_chg!=null)
			{
				this.on_item_sel_chg(curitem);
			}
				
			for (var lis of this.modelListeners)
			{
				lis.on_model_sel_chged(this, intera.getLayer(), curitem);
			}
		}



		public setInteract(inta: DrawInteract)
		{
			this.interact = inta;//s.push(inta) ;
		}

		public getInteract()
		{
			return this.interact;
		}

		

		private init_me()
		{
			for (var i = 0; i < this.layers.length; i++)
				this.layers[i].setPanel(this);
			if (this.interact)
				this.interact.setPanel(this);
		}

		private clearTimeoutDownLong()
		{
			if(this.timeoutDownLong!=null)
			{
				clearTimeout(this.timeoutDownLong) ;
				this.timeoutDownLong = null ;
			}
		}

		public init_panel()
		{
			this.init_me();
			this.updatePixelSize();

			//prevent right cxt menu
			window.oncontextmenu=(event:any)=>{
				var evt = event || window.event;
				evt.preventDefault();
			};
			//var this = this;

			this.tarEle.onmousedown = (e:_MouseEvent) =>
			{
				if (this.interact == null)
					return;
				this.delPopMenu();
				//for(var i = 0 ; i < this.interacts.length; i ++)
				this.interact.on_mouse_event(MOUSE_EVT_TP.Down, e);

				this.clearTimeoutDownLong();
				this.timeoutDownLong = setTimeout(()=>{
					this.interact?.on_mouse_event(MOUSE_EVT_TP.DownLong,e) ;
					this.clearTimeoutDownLong();
				},500);
			}



			this.tarEle.onmousemove = (e) =>
			{
				this.clearTimeoutDownLong();

				var p = this.getEventPixel(e);//windowToCanvas(canvas_,e.x,e.y) ;
				var d = this.transPixelPt2DrawPt(p.x, p.y);
				p = this.transDrawPt2PixelPt(d.x, d.y);
				//console.log("["+e.x+","+e.y+"]-("+p.x+","+p.y+")") ;
				if (this.on_mouse_mv != null)
					this.on_mouse_mv(p, d);

				if (this.interact == null)
					return;
				this.interact.on_mouse_event(MOUSE_EVT_TP.Move, e);
			};

			this.tarEle.onmouseup = (e) =>
			{
				this.clearTimeoutDownLong();

				if (this.interact == null)
					return;
				this.interact.on_mouse_event(MOUSE_EVT_TP.Up, e);
			}

			this.tarEle.onclick = (e) =>
			{
				this.clearTimeoutDownLong();

				if (this.interact == null)
					return;
				this.interact.on_mouse_event(MOUSE_EVT_TP.Clk, e);
			}

			this.tarEle.ondblclick = (e) =>
			{
				this.clearTimeoutDownLong();

				if (this.interact == null)
					return;
				
				this.interact.on_mouse_event(MOUSE_EVT_TP.DbClk, e);
			}

			this.tarEle["onmousewheel"] = (e:any) =>
			{
				this.clearTimeoutDownLong();

				if (this.interact == null)
					return;
				this.interact.on_mouse_event(MOUSE_EVT_TP.Wheel, e);
			}

			if (window.navigator.userAgent.toLowerCase().indexOf("firefox") != -1)
			{
				window.addEventListener("DOMMouseScroll", (e: any) =>
				{
					this.clearTimeoutDownLong();

					if (this.interact == null)
						return;
					this.interact.on_mouse_event(MOUSE_EVT_TP.Wheel, e);
				}, false);
			}

			this.tarEle.onkeydown = (e: KeyboardEvent) =>
			{
				if (this.interact == null)
					return;
				this.interact.on_key_event(KEY_EVT_TP.Down, e);
			}

			this.tarEle.onkeyup = (e: KeyboardEvent) =>
			{
				if (this.interact == null)
					return;
				this.interact.on_key_event(KEY_EVT_TP.Up, e);
			}

			this.tarEle.onkeypress = (e: KeyboardEvent) =>
			{
				if (this.interact == null)
					return;
				this.interact.on_key_event(KEY_EVT_TP.Press, e);
			}

			this.tarEle.ondrop = (e: DragEvent) =>
			{
				if (this.interact == null)
					return;
				this.interact.on_mouse_event(MOUSE_EVT_TP.Drop, e);
			}

			this.tarEle.ondragover = (e: DragEvent) =>
			{			
				if (this.interact != null)
				{
					this.interact.on_mouse_event(MOUSE_EVT_TP.DragOver, e);
				}
				e.preventDefault();
			}
			this.tarEle.ondragleave=(e:DragEvent)=>
			{
				if (this.interact != null)
				{
					this.interact.on_mouse_event(MOUSE_EVT_TP.DragLeave, e);
				}
				e.preventDefault();
			}
		}
	}

	/**
	 * support single DrawItem to draw in html element like
	 * canvas or div
	 */
	export class DrawPanelItem extends AbstractDrawPanel implements IDrawItemContainer
	{
		
		private canvasEle: HTMLCanvasElement;
		private drawItem: DrawItemRect;
		private context: CanvasRenderingContext2D;



		public constructor(ele: HTMLCanvasElement, di: DrawItemRect)
		{
			super();

			this.canvasEle = ele;
			this.drawItem = di;
			this.context = document.createElement('canvas').getContext('2d') as CanvasRenderingContext2D;
			this.drawItem.setContainer(this, null);
		}

		public getHTMLElement(): HTMLElement
		{
			return this.canvasEle;
		}
		

		getItemsShow(): DrawItem[]
		{
			return [this.drawItem];
		}
		removeItem(item: DrawItem): boolean
		{
			return false;
		}
		notifyItemsChg(): void
		{

		}

		public getCanvasEle()
		{
			return this.canvasEle;
		}

		public setPixelSize(sz: base.Size): void
		{

		}
		public getPixelSize(): base.Size
		{
			var computedStyle: CSSStyleDeclaration = getComputedStyle(this.canvasEle);
			return {
				w:
					this.canvasEle.offsetWidth -
					this.parseToFloat(computedStyle.borderLeftWidth) -
					this.parseToFloat(computedStyle.paddingLeft) -
					this.parseToFloat(computedStyle.paddingRight) -
					this.parseToFloat(computedStyle.borderRightWidth),
				h: this.canvasEle.offsetHeight -
					this.parseToFloat(computedStyle.borderTopWidth) -
					this.parseToFloat(computedStyle.paddingTop) -
					this.parseToFloat(computedStyle.paddingBottom) -
					this.parseToFloat(computedStyle.borderBottomWidth)
			}
		}

		public getDrawItem(): DrawItemRect
		{
			return this.drawItem;
		}

		public update_draw(): void
		{
			this.drawItem.draw(this.context, this);
		}
		public clear_draw(): void
		{
			var pixsz = this.getPixelSize();
			this.context.clearRect(0, 0, pixsz.w, pixsz.h);
		}

		public delPopMenu(): void
		{
			
		}
		public setPopMenu(menuele: JQuery<HTMLElement>): void
		{
			
		}
		public getDrawView(): DrawView | null
		{
			return null ;
		}
		public getInteract(): DrawInteract | null
		{
			return null ;
		}

	}

	/**
	 * for single drawitem to draw in a fixed div
	 */
	export class DrawPanelDiv
	{
		static DRAW_PANEL_DIV="_drawpanel_div";

		private panel:DrawPanel;
		private layer:DrawLayer;
		private drawItem:DrawItem|null=null;

		public constructor(divele:string,opts:{}|undefined)
		{
			if(opts==undefined)
				opts={} ;
			if(opts["panel"])
				this.panel = opts["panel"];
			else
				this.panel = new DrawPanel(divele,{});
				
			this.panel.init_panel();
			
			this.layer =(opts["layer"]!=undefined)?opts["layer"]:new oc.DrawLayer("lay") ;
			this.panel.addLayer(this.layer);

			var ele = this.panel.getHTMLElement();
			ele[DrawPanelDiv.DRAW_PANEL_DIV] = this;
		}

		public getPanel()
		{
			return this.panel;
		}

		public getLayer()
		{
			return this.layer;
		}

		public setDrawItem(di:DrawItem)
		{
			this.drawItem = di;
			this.layer.addItem(di);
			//this.layer.ajustDrawFit();//cause firefox error
		}

		public getDrawItem():DrawItem|null
		{
			return this.drawItem;
		}

		public updateByResize()
		{
			this.panel.updatePixelSize() ;
			this.layer.ajustDrawFit();
		}
	}
}
