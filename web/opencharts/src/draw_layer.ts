/**
 * @module oc/DrawLayer
 */

module oc
{
	export class DrawLayer implements IDrawItemContainer,IItemsLister,IDrawNode
	{
		
		private drawPanel: AbstractDrawPanel | null = null;

		id:string="" ;

		name: string = "";

		title:string="";

		private items: DrawItem[] = [];

		private drawRes:IDrawRes|null=null;

		private context: CanvasRenderingContext2D[];
		private canvas: HTMLCanvasElement[];
		private cur_dbuf_idx:number=0;

		bVis: boolean = true;

		public constructor(opts:{}|string|undefined)//,parentele:HTMLElement|null|undefined)
		{
			//style="position: absolute; left: 0; top: 0; z-index: 0;"
			
			var cxt0 = document.createElement('canvas').getContext('2d') as CanvasRenderingContext2D;
			var canvs0 = cxt0.canvas;
			// if(parentele)
			// 	parentele.appendChild(canvs0)
			
			// var cxt1 = document.createElement('canvas').getContext('2d') as CanvasRenderingContext2D;
			// var canvs1 = cxt1.canvas;
			// this.context=[cxt0,cxt1];
			// this.canvas=[canvs0,canvs1];
			this.context=[cxt0];
			this.canvas=[canvs0];
			for(var c of this.canvas)
			{
				let can = $(canvs0);
				can.css("position", "relative");
				can.css("border", "solid 1px");
				can.css("left", "0px");
				can.css("top", "0px");
				can.css("width", "100%");
				can.css("height", "100%");
				can.css("display","none");
			}
			$(this.canvas[this.cur_dbuf_idx]).css("display","")
			//this.switchCxtFrontBack();

			if (opts!=undefined)
			{
				if (typeof (opts) == 'string')
					this.name = arguments[0];
			}
			

			if (this.id == null || this.id == '' || this.id == undefined || this.id == 'undefined')
				this.id = util.create_new_tmp_id();
		}

		

		public getId()
		{
			return this.id ;
		}

		public getName()
		{
			return this.name;
		}

		getTitle(): string
		{
			return this.title;
		}

		public setName(n:string)
		{
			this.name = n ;
		}


		public getCanvasEles():HTMLCanvasElement[]
		{
			return this.canvas;
		}

		public getCanvasCxts():CanvasRenderingContext2D[]
		{
			return this.context;
		}

		public getCxtFront():CanvasRenderingContext2D
		{
			return this.context[this.cur_dbuf_idx];
		}
		private getCxtBack():CanvasRenderingContext2D
		{
			var i = this.cur_dbuf_idx==0?1:0;
			return this.context[i];
		}

		private switchCxtFrontBack()
		{
			$(this.canvas[this.cur_dbuf_idx]).css("display","none")
			this.cur_dbuf_idx = this.cur_dbuf_idx==0?1:0;
			$(this.canvas[this.cur_dbuf_idx]).css("display","")
		}

		public getCxtCurDraw()
		{
			return this.getCxtFront();
		}

		public getDrawRes():IDrawRes|null
		{
			return this.drawRes ;
		}

		public setDrawRes(dr:IDrawRes|null)
		{
			this.drawRes = dr ;
		}

		public getItemByName(n:string):DrawItem|null
		{
			for(var item of this.getItemsShow())
			{
				if(item.getName()==n)
					return item;
			}
			return null;
		}

		public getItemsByGroupName(gn:string):DrawItem[]
		{
			var r:DrawItem[] = [];
			for(var item of this.getItemsShow())
			{
				if(item.getGroupName()==gn)
					r.push(item);
			}
			return r ;
		}

		public setDynData(dyn:{})
		{
			var tmpis=[];
			for(var n in dyn)
			{
				var item = this.getItemByName(n);
				if(item==null)
					continue;
				item.setDynData(dyn[n],false);
				tmpis.push(item) ;
			}
			this.MODEL_fireChged(tmpis,[]);
		}


		fireTick()
		{
			for(var di of this.items)
			{
				di.on_tick() ;
			}
		}

		public inject(opts: {} | string,mark:string|null|undefined)
		{
			if (typeof (opts) == 'string')
				eval("opts=" + opts);
			this.id = opts["id"]?opts["id"]:util.create_new_tmp_id();
			this.title = opts["title"]?opts["title"]:"";
			//alert(JSON.stringify(opts));
			//alert(opts.name);
			var n = opts["name"];
			if (n)
				this.name = n;
			this.bVis = (opts["vis"] != false);
			var dis = opts["dis"];
			if (dis)
			{
				for (var i = 0; i < dis.length; i++)
				{
					var it = dis[i];
					var cn = it._cn;
					if (!cn)
						continue;
					
					var item = DrawItem.createByClassName(cn,undefined);
					//eval("item=new oc.di."+cn+"()") ;
					//console.log("cn="+cn+"  item="+item);
					if (item == null)
						continue;
					item.inject(it as base.Props<string>,false);
					if(mark!==undefined)
						item.setMark(mark);
					this.addItem(item);
				}
			}
		}

		public extract(mark:string|null|undefined): {}
		{
			var r = {};
			r["id"]=this.id;
			r["name"] = this.name;
			r["title"] = this.title;
			r["bvis"] = this.bVis;
			var rdis: any[] = r["dis"] = [];
			for (var item of this.items)
			{
				if(item.isVirtual())
					continue;
				//console.log(item.getMark());
				if(mark!==undefined && item.getMark()!==mark)
					continue;
				//console.log("extract---");
				rdis.push(item.extract());
			}
			return r;
		}


		public setItemsWithMark(items:DrawItem[],mark:string)
		{
			for(var item of items)
			{
				item.setMark(mark);
				this.addItem(item);
			}
		}

		/**
		 * filter list item by mark
		 * @param m 
		 */
		public listItemsByMark(m:string):DrawItem[]
		{
			var r:DrawItem[]=[];
			for(var item of this.items)
			{
				if(item.getMark()==m)
					r.push(item);
			}
			return r ;
		}

		public getPanel()
		{
			return this.drawPanel ;
		}

		public setPanel(p:AbstractDrawPanel)
		{
			this.drawPanel = p ;
			for(var item of this.items)
			{
				item.setContainer(this,this);
			}
		}

		public getItemById(id: string): DrawItem | null
		{
			for (var i = 0; i < this.items.length; i++)
			{
				if (this.items[i].id == id)
					return this.items[i];
			}
			return null;
		}

		public addItem(item: DrawItem)
		{
			if(this.drawPanel!=null)
				item.setContainer(this,this);
			this.items.push(item);

			// if (item.getClassName() == 'DrawItems')
			// 	(<DrawItems>item).setLayer(this);
			// else
			// 	item.drawLayer = this;
			this.MODEL_fireChged(item,[]);
		}

		public setItems(items:DrawItem[])
		{
			for(var m of items)
			{
				m.setContainer(this,this);
				this.items.push(m);
			}
			this.MODEL_fireChged(items,[]);
		}

		public copyItem(itemid:string):DrawItem|null
		{
			var item = this.getItemById(itemid);
			if(item==null)
				return null;
			var ps = item.extract();

			return this.copyByJSON(ps);
		}

		public copyByJSON(json:string|{}):DrawItem|null
		{
			if (typeof (json) == 'string')
				eval("json=" + json);
			
			var cn = json["_cn"];
			if (!cn)
				return null;
					
			var r = DrawItem.createByClassName(cn,undefined);
			if (r == null)
				return null ;
			r.inject(json as base.Props<string>,false);
			r.setId(util.create_new_tmp_id());
			r.setDrawXY(0,0) ;
			this.addItem(r) ;
			this.MODEL_fireChged(r,null);
			return r;
		}

		public vis(v: boolean | undefined)
		{
			if (v == undefined)
				return this.bVis;
			this.bVis = v;
		}

		public MODEL_fireChged(item:DrawItem[]|DrawItem|null,prop_names:string[]|null)
		{
			if(this.drawPanel==null)
				return ;
			if(this.drawPanel instanceof DrawPanel)
				this.drawPanel.MODEL_fireChged(this,item,prop_names);
		}

		//ui suport

		public getRelatedInteract():DrawInteract|null
		{
			if(this.drawPanel==null)
				return null;
			var inta = this.drawPanel.getInteract();
			if(inta==null)
				return null ;
			if(inta.getLayer()==this)
				return inta ;
			return null ;
		}

		//public 

		public clear_draw()
		{
			if (this.drawPanel == null)
				return;
			var pixsz = this.drawPanel.getPixelSize();
			var cxt = this.getCxtCurDraw();
			cxt.clearRect(0, 0, pixsz.w, pixsz.h);
		}

		public update_draw()
		{
			this.clear_draw();
			this.on_draw();
		}

		

		protected on_draw()
		{
			if (this.drawPanel == null)
				return;
			var inta = this.getRelatedInteract();
			var cxt = this.getCxtCurDraw();
			if(inta!=null)
			{// draw bk coordination,or rule
				var dxy = this.drawPanel.transDrawPt2PixelPt(0, 0);
				var sz = this.drawPanel.getPixelSize();
				var w = sz.w;
				var h = sz.h;
				cxt.save();
				cxt.lineWidth=1;
				cxt.beginPath();
				cxt.strokeStyle = "#4c4f51";
				cxt.moveTo(0, dxy.y);
				cxt.lineTo(w, dxy.y);
				cxt.moveTo(dxy.x, 0);
				cxt.lineTo(dxy.x, h);
				cxt.stroke();
				cxt.restore();
			}

			//draw items
			for (var item of this.items)
			{
				if(!item.isVisiable())
					continue ;
					
				if(item.isHidden())
				{
					item.draw_hidden(cxt,this) ;
					continue ;
				}
					
				item.draw(cxt,this);
			}

			//draw selected and current oper
			var inta = this.getRelatedInteract();
			if(inta!=null)
			{
				var sitems = inta.getSelectedItems();
				for(var si of sitems)
				{
					si.draw_sel(cxt,this,"red") ;
				}
				sitems = inta.getDragOverSelItems();
				for(var si of sitems)
				{
					si.draw_sel(cxt,this,"yellow") ;
				}
				inta.getCurOper().draw();
			}
			//this.switchCxtFrontBack();
		}


		public getShowItemsRect()
		{
			return ItemsContainer.calcRect(this.getItemsShow());
		}
		
		public ajustDrawFit()
		{
			var p = this.getPanel();
			if(p==null)
				return ;

			var r = ItemsContainer.calcRect(this.getItemsShow());
			if(r==null)
			{
				p.ajustDrawToInit();
				return ;
			}
			p.ajustDrawFitInRect(r);
		}

		getXYResolution(): XYRes
		{
			if(this.drawPanel==null)
				throw new Error("no DrawPanel set.");
			return this.drawPanel.getXYResolution();
		}
		transPixelPt2DrawPt(px: number, py: number): base.Pt
		{
			if(this.drawPanel==null)
				throw new Error("no DrawPanel set.");
			return this.drawPanel.transPixelPt2DrawPt(px,py);
		}
		transDrawPt2PixelPt(dx: number, dy: number): base.Pt
		{
			if(this.drawPanel==null)
				throw new Error("no DrawPanel set.");
			return this.drawPanel.transDrawPt2PixelPt(dx,dy);
		}
		transDrawLen2PixelLen(b_xres: boolean, len: number): number
		{
			if(this.drawPanel==null)
				throw new Error("no DrawPanel set.");
			return this.drawPanel.transDrawLen2PixelLen(b_xres,len);
		}
		transPixelLen2DrawLen(b_xres: boolean, len: number): number
		{
			if(this.drawPanel==null)
				throw new Error("no DrawPanel set.");
			return this.drawPanel.transPixelLen2DrawLen(b_xres,len);
		}
		notifyItemsChg(): void
		{
			
		}

		public getItemsAll()
		{
			return this.items;
		}

		public getItemsShow():DrawItem[]
		{
			var r:DrawItem[]=[];
			for(var tmpi of this.items)
			{
				if(tmpi.isHidden())
					continue;
				r.push(tmpi);
			}
			return r;
		}


		getItemByIdx(i:number):DrawItem|null
		{
			if(this.items==null||this.items.length<=0||i>=this.items.length)
				return null;
			return this.items[i] ;
		}

		removeItem(item:DrawItem):boolean
		{
			if(this.items==null||this.items.length<=0)
				return false;
			var i;
			for(i = 0 ; i < this.items.length ; i ++)
			{
				if(this.items[i]==item)
					break ;
			}
			if(i>=this.items.length)
				return false;
			this.items.splice(i,1) ;
			this.MODEL_fireChged(null,null);
			return true;
		}


		/**
		 * for override using
		 * @param tp 
		 * @param pxy 
		 * @param dxy 
		 * @param e 
		 */
		public on_mouse_event(tp: MOUSE_EVT_TP, pxy: oc.base.Pt, dxy: oc.base.Pt,e:_MouseEvent)
        {
		}
	}

}
