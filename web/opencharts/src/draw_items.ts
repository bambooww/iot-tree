

//------------------------------------------
// items collection
module oc
{
	/**
	 * items container ,which make subitems draw fit in it's rect
	 *   width or height may be stretched
	 */
	export class DrawItems extends DrawItemRectR implements IItemsLister
	{
		items: DrawItem[] = [];//sub items

		private itemsRect:base.Rect|null=null ;

		private innerCont:ItemsContainer|null=null;
		//private xy_res:XYRes|null =null;

		public constructor(opts:{}|undefined)
		{
			super(opts);
		}

		public getClassName()
		{
			return "DrawItems";
		}

		public getItemsShow()
		{
			return this.items;
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
			item.parentNode = null;

			this.MODEL_fireChged([]);
			return true;
		}


		// public getBoundRectDraw()
		// {//override
		// 	var pt = this.getDrawXY();
		// 	return new oc.base.Rect(pt.x, pt.y, this.w, this.h);
		// }

		
		public getInnerContainer():ItemsContainer|null
		{
			//return this.getContainer();
			if(this.innerCont!=null)
				return this.innerCont;
			var pc = this.getContainer();
			if(pc==null)
				return null;
			this.innerCont = new ItemsContainer(this,pc,this) ;
			return this.innerCont;
		}
		
		
		
		// private recurseSetLayer(disob: DrawItems, cont:IDrawItemContainer,lay: DrawLayer)
		// {//
		// 	disob.setContainerLayer(cont,lay);
		// 	for (var i = 0; i < disob.items.length; i++)
		// 	{
		// 		var it = disob.items[i];
		// 		it.drawLayer = lay;
		// 		it.container = disob;//container changed
		// 		it.setContainerLayer(disob,lay);
		// 		if (it.getClassName() == 'DrawItems')
		// 		{
		// 			this.recurseSetLayer(it as DrawItems,disob, lay);
		// 		}
		// 	}
		// }

		// public setContainerLayer(cont:IDrawItemContainer,lay: DrawLayer)
		// {
		// 	this.recurseSetLayer(this,cont, lay);
		// }

		public inject(opts: base.Props<any>,ignore_readonly:boolean|undefined)
		{
			super.inject(opts,ignore_readonly);

			//if(typeof(opts)=='string')
			//	eval("opts="+opts) ;

			if (opts.items)
			{
				for (var i = 0; i < opts.items.length; i++)
				{
					var it = opts.items[i];
					var cn = it._cn;
					if (!cn)
						continue; 
					var item = DrawItem.createByClassName(cn,undefined);
					//console.log("cn="+cn+"  item="+item);
					if (item==null)
						continue;
					item.inject(it,ignore_readonly);
					//out set layer
					//item.setContainerLayer(this,;
					this.items.push(item);
					item.parentNode = this ;
				}
				//this.innerCont.
				var c = this.getInnerContainer();
				if(c!=null)
					c.notifyItemsChg();
			}
		}

		public extract(): base.Props<any>
		{
			var r = super.extract();
			var rs = r["items"] = [];
			for (var i = 0; i < this.items.length; i++)
			{
				r.items.push(this.items[i].extract());
			}
			return r;
		}

		public addItem(item: DrawItem)
		{
			this.items.push(item);
			item.parentNode = this ;

			var lay = this.getLayer();
			if(lay==null)
				return;
			var ic = this.getInnerContainer();
			if(ic==null)
				return;

			item.setContainer(ic,lay) ;
			ic.notifyItemsChg();
			// if (item.getClassName() == 'DrawItems')
			// 	(<DrawItems>item).setLayer(lay);
			// else
			// 	item.drawLayer = lay;
			this.MODEL_fireChged([]);
		}

		public setItems(items:DrawItem[])
		{
			var lay = this.getLayer();
			var ic = this.getInnerContainer();

			this.items=[];
			for(var m of items)
			{
				this.items.push(m);
				m.parentNode = this ;

				if(ic!=null&&lay!=null)
					m.setContainer(ic,lay) ;
			}
			if(ic!=null)
				ic.notifyItemsChg();

			this.MODEL_fireChged([]);
		}

		public on_mouse_event(tp:MOUSE_EVT_TP,pxy:oc.base.Pt,dxy:oc.base.Pt,e:_MouseEvent)
		{
			var ic = this.getInnerContainer();
			if(ic==null)
				return;
			var sitems = this.getItemsShow() ;
			if(sitems==null||sitems.length<=0)
				return ;
			for(var si of sitems)
			{
				var bc = si.containDrawPt(dxy.x,dxy.y) ;
				if(bc)
					si.on_mouse_event(tp,pxy,dxy,e) ;
			}
		}

		
		public getPrimRect(): base.Rect | null
		{
			var ic = this.getInnerContainer();
			if(ic==null)
			 	return null;
			
			var r = ItemsContainer.calcRect(this.getItemsShow());
			if(r==null)
				return null ;
			var p = ic.transDrawPt2PixelPt(r.x,r.y);
			var w = ic.transDrawLen2PixelLen(true,r.w);
			var h = ic.transDrawLen2PixelLen(false,r.h);

			return new oc.base.Rect(0,0,w,h);
			//return r ;
		}


		public drawPrim(cxt: CanvasRenderingContext2D): void
		{
			var ic = this.getInnerContainer();
			if(ic==null)
				return;
			cxt.save();
			var p = this.getPixelXY();
			if(p!=null)//what the fuck
				cxt.translate(-p.x,-p.y);
			for (var item of this.items)
			{
				item.draw(cxt,ic);
			}
			cxt.restore();

			//
			if (this.items.length<= 0)
			{
				var r = this.getPrimRect();
				var c = this.getContainer() ;
				if(r!=null&&c!=null)
				{
					var pt = c.transDrawPt2PixelPt(r.x,r.y);
					var w = c.transDrawLen2PixelLen(true,r.w);
					var h = c.transDrawLen2PixelLen(false,r.h);
					util.drawRect(cxt,pt.x,pt.y,w,h,null,null,1,"blue");
				}
			}
		}
		public drawPrimSel(ctx: CanvasRenderingContext2D): void
		{
			
		}


	// 	draw0(cxt:CanvasRenderingContext2D,c:IDrawItemContainer)
	// 	{
	// 		var ic = this.getInnerContainer();
	// 		if(ic==null)
	// 			return;

	// 		for (var item of this.items)
	// 		{
	// 			item.draw(cxt,ic);
	// 		}
	// 		//
	// 		if (this.items.length<= 0)
	// 		{
	// 			var r = this.getPrimRect();
	// 			var c = this.getContainer() ;
	// 			if(r!=null&&c!=null)
	// 			{
	// 				var pt = c.transDrawPt2PixelPt(r.x,r.y);
	// 				var w = c.transDrawLen2PixelLen(true,r.w);
	// 				var h = c.transDrawLen2PixelLen(false,r.h);
	// 				util.drawRect(cxt,pt.x,pt.y,w,h,null,null,1,"blue");
	// 			}
	// 		}
	// 	}

	}

}