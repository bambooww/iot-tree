
module oc
{
	export abstract class DrawOper
	{
		belongTo: DrawInteract;

		drawLayer: DrawLayer|null;

		public constructor(interact: DrawInteract, layer: DrawLayer|null)
		{
			//this.name = name;

			this.belongTo = interact;

			this.drawLayer = layer;//tmp
		}

		public abstract getOperName():string;

		public getInteract()
		{
			return this.belongTo;
		}


		public getDrawPanel()
		{
			return this.belongTo.getPanel();
		}

		public setCursor(c:oc.Cursor=oc.Cursor.auto)
		{
			var p = this.belongTo.getPanel() ;
			if(p==null)
				return ;
			p.setCursor(c);
		}

		/**
		 * mask interact event。
		 * true - cur oper can mask interact event
		 */
		public maskInteractEvent():boolean
		{
			return false;
		}


		public abstract on_oper_stack_push():void;

		public abstract on_oper_stack_pop():void;

		public abstract chkOperFitByDrawPt(pxy:base.Pt,dxy:base.Pt):boolean;

		public getDrawLayer():DrawLayer|null
		{
			return this.drawLayer;
		}

		
		public pushOperStack(oper:DrawOper)
		{//push new oper to stack top
			if(oper==this.belongTo.getCurOper())
				return;//
			this.belongTo.pushOperStack(oper) ;
		}

		public popOperStackMe()
		{//pop self from stack
			if(this!=this.belongTo.getCurOper())
				return ;

			return this.belongTo.popOperStack();
		}


		public MODEL_fireOperChged()
		{
			this.belongTo.MODEL_fireOperChged(this) ;
		}
		/**
		 * 
		 * @param pxy 
		 * @param dxy 
		 * @returns true make event transfer to next stack oper，and end to interact
		 *    false will stop event 
		 */
		public on_mouse_down(pxy: base.Pt, dxy: base.Pt,me:_MouseEvent):boolean
		{//
			return true;
		}

		public on_mouse_downlong(pxy: base.Pt, dxy: base.Pt,me:_MouseEvent):boolean
		{//
			return true;
		}

		public on_mouse_mv(pxy: base.Pt, dxy: base.Pt,me:_MouseEvent):boolean
		{//
			return true;
		}

		public on_mouse_up(pxy: base.Pt, dxy: base.Pt,me:_MouseEvent):boolean
		{//
			return true;
		}

		public on_mouse_dbclk(pxy: base.Pt, dxy: base.Pt,me:_MouseEvent):boolean
		{//
			return true;
		}

		public on_mouse_clk(pxy: base.Pt, dxy: base.Pt,me:_MouseEvent):boolean
		{//
			return true;
		}

		public on_mouse_wheel(pxy: base.Pt, dxy: base.Pt, delta: number):boolean
		{//
			return true;
		}

		public on_mouse_dragover(pxy:base.Pt,dxy:base.Pt,dd:DROP_DATA):boolean
		{
			return true;
		}

		public on_mouse_dragleave(pxy:base.Pt,dxy:base.Pt,dd:DROP_DATA):boolean
		{
			return true;
		}

		public on_mouse_drop(pxy:base.Pt,dxy:base.Pt,dd:DROP_DATA):boolean
		{
			return true;
		}

		public on_key_down(e:KeyboardEvent):boolean
		{
			return true;
		}

		public on_key_up(e:KeyboardEvent):boolean
		{
			return true;
		}

		public on_key_press(e:KeyboardEvent):boolean
		{
			return true;
		}

		draw()//(cxt: CanvasRenderingContext2D)
		{
			this.draw_oper();
		}

		protected abstract draw_oper():void;
	}

	export class DrawOperDrag extends DrawOper
	{
		downPt: base.Pt | null = null;

		itemDrag: DrawItem | null = null;

		itemDragPt: base.Pt | null = null;

		public constructor(interact: DrawInteract, layer: DrawLayer|null)
		{
			super(interact, layer);
		}

		public getOperName():string
		{
			return "drag";
		}

		public on_oper_stack_push():void
		{

		}

		public on_oper_stack_pop():void
		{

		}

		public chkOperFitByDrawPt(pxy:base.Pt,dxy:base.Pt):boolean
		{
			return false;
		}

		// public on_mouse_dbclk(pxy:base.Pt,dxy:base.Pt):boolean
		// {
		// 	this.downPt = null;
		// 	//if(this.itemDrag!=null)
		// 	this.itemDrag = null;
		// 	this.setCursor(undefined);
		// 	return true;
		// }
		
		public on_mouse_down(pxy: base.Pt, dxy: base.Pt,me:_MouseEvent):boolean
		{
			if(me.button!=0)//left btn
				return true;
			this.downPt = dxy;
			var inter = this.getInteract();
			//var cur_moused_item = inter.getCurMouseOnItem() ;
			
			this.itemDrag = this.getInteract().getSelectedItemDrag();
			if(this.itemDrag!=null&&inter.isCurMouseOnItem(this.itemDrag))
			{
				this.itemDragPt = { x: this.itemDrag.x, y: this.itemDrag.y };
				this.setCursor(Cursor.move);
				return false;
			}

			//if(cur_moused_item!=this.itemDrag)
			//	this.itemDrag = null ;

			// if (this.itemDrag != null)
			// {
			// 	this.itemDragPt = { x: this.itemDrag.x, y: this.itemDrag.y };
			// 	this.setCursor(Cursor.move);
			// 	return false;
			// }
			return true;
		}

		public on_mouse_mv(pxy: base.Pt, dxy: base.Pt,me:_MouseEvent):boolean
		{
			var p = this.getDrawPanel();
			if (p == null)
				return true;
			var inter = this.getInteract();

			if(this.itemDrag == null&&this.downPt==null)
			{//normal mv
				var onitems = inter.getCurMouseOnItems() ;
				if(onitems!=null&&onitems.length>0)
				{
					var selitem = inter.getSelectedItemDrag();
					if(selitem!=null && inter.isCurMouseOnItem(selitem))
						this.setCursor(Cursor.move);
					else
						this.setCursor(Cursor.crosshair);
				}
				else
				{
					this.setCursor(undefined);
				}
			}

			if (this.downPt == null)
				return true;
			//	console.log("mv item drag="+this.itemDrag)
			if (this.itemDrag == null)
			{//draw panel
				//console.log("delta_y="+(this.downPt.y-dxy.y));
				p.moveDrawCenter(this.downPt.x - dxy.x, this.downPt.y - dxy.y);
			}
			else
			{
				if(this.itemDragPt!=null)
				{
					//console.log(this.itemDrag.id+" is dragged...")
					this.itemDrag.setDrawXY(this.itemDragPt.x + (dxy.x - this.downPt.x),
						this.itemDragPt.y + (dxy.y - this.downPt.y));
					return false;
				}
				
			}

			return true;
		}

		public on_mouse_up(pxy: base.Pt, dxy: base.Pt,me:_MouseEvent):boolean
		{
			this.downPt = null;
			//if(this.itemDrag!=null)
			this.itemDrag = null;
			this.setCursor(undefined);
			return true;
		}

		public on_mouse_wheel(pxy: base.Pt, dxy: base.Pt, delta: number):boolean
		{
			var p = this.getDrawPanel();
			if (p == null)
				return true;
			p.ajustDrawResolution(dxy.x, dxy.y, delta);
			return true;
		}
		
		protected draw_oper():void
		{

		}
	}

	export class DrawOperShowOnly extends DrawOper
	{
		downPt: base.Pt | null = null;

		//itemDrag: DrawItem | null = null;

		//itemDragPt: base.Pt | null = null;

		public constructor(interact: DrawInteract, layer: DrawLayer|null)
		{
			super(interact, layer);
		}

		public getOperName():string
		{
			return "show_only";
		}

		public on_oper_stack_push():void
		{

		}

		public on_oper_stack_pop():void
		{

		}

		public chkOperFitByDrawPt(pxy:base.Pt,dxy:base.Pt):boolean
		{
			return false;
		}

		// public on_mouse_dbclk(pxy:base.Pt,dxy:base.Pt):boolean
		// {
		// 	this.downPt = null;
		// 	//if(this.itemDrag!=null)
		// 	this.itemDrag = null;
		// 	this.setCursor(undefined);
		// 	return true;
		// }
		
		public on_mouse_down(pxy: base.Pt, dxy: base.Pt,me:_MouseEvent):boolean
		{
			if(me.button!=0)//left btn
				return true;
			this.downPt = dxy;
			var inter = this.getInteract();
			//var cur_moused_item = inter.getCurMouseOnItem() ;
			return true;
		}

		public on_mouse_mv(pxy: base.Pt, dxy: base.Pt,me:_MouseEvent):boolean
		{
			var p = this.getDrawPanel();
			if (p == null)
				return true;
			var inter = this.getInteract();

			if (this.downPt == null)
				return true;
			p.moveDrawCenter(this.downPt.x - dxy.x, this.downPt.y - dxy.y);
			return true;
		}

		public on_mouse_up(pxy: base.Pt, dxy: base.Pt,me:_MouseEvent):boolean
		{
			this.downPt = null;

			this.setCursor(undefined);
			return true;
		}

		public on_mouse_wheel(pxy: base.Pt, dxy: base.Pt, delta: number):boolean
		{
			var p = this.getDrawPanel();
			if (p == null)
				return true;
			p.ajustDrawResolution(dxy.x, dxy.y, delta);
			return true;
		}
		
		protected draw_oper():void
		{
		}
	}
}
