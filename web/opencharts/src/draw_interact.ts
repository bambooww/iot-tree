/**
 * interact , a DrawOper and a Selector
 */
module oc
{
	// export interface DrawLisSelector
	// {
	//  	onItemSelected(item: DrawItem|null): void;
	// }



	export type DROP_DATA={_tp:string,_val:string,_g:string};

	export abstract class DrawInteract
	{//selector
		private drawPanel: DrawPanel;

		private drawLayer: DrawLayer;
		//private drawLayers: DrawLayer[]|null=null;

		bsel_down: boolean = false;
		bsel_down_mv: number = 0;

		private curMouseDownItem: DrawItem | null = null;

		private curMouseOnItem: DrawItem | null = null;

		private curMouseOnItems:DrawItem[]|null=null ;

		private selectItems:DrawItem[]=[];
		//private canSelectItems: DrawItem[] = [];
		private selectedItemDrag: DrawItem | null = null;//current drag item

		/**
		 * when drag and drop,some container item may show special to notify obj that can drap
		 * then these items must be selected
		 */
		protected dragoverSelItems:DrawItem[]=[];

		//selectedListeners: DrawLisSelector[] = [];

		private operStack: DrawOper[] = [];

		/**
		 * support copy paste
		 */
		private copyPasteUrl:string|null=null;

		public constructor(panel: DrawPanel, layer: DrawLayer,opts:{}|undefined)
		{
			this.drawPanel = panel;

			this.drawLayer = layer;

			//this.canSelectItems = [];
			this.selectedItemDrag = null;//current drag item
			//set default oper
			if(opts&&opts["show_only"])
				this.operStack.push(new DrawOperShowOnly(this, layer));
			else
				this.operStack.push(new DrawOperDrag(this, layer));
			
			if(opts)
			{
				var cpu = opts["copy_paste_url"];
				if(cpu&&cpu!=null&&cpu!="")
					this.copyPasteUrl = cpu;
			}
		}

		public isOperDefault(): boolean
		{//stack is not push other oper is normal state
			return this.operStack.length == 1;
		}



		public getCurOper(): DrawOper
		{//get current opers
			return this.operStack[this.operStack.length - 1];
		}

		private logOperStack()
		{
			// var s = "";
			// for (var op of this.operStack)
			// {
			// 	s += " > " + op.getOperName();
			// }
			// console.log(s);
		}

		public pushOperStack(op: DrawOper)
		{
			if (this.getCurOper() == op)
				return;
			this.operStack.push(op);
			op.on_oper_stack_push();

			this.logOperStack();
		}

		public popOperStack(): DrawOper
		{
			if (this.operStack.length <= 1)
				throw new Error("stack must at least has one oper");
			var r = <DrawOper>this.operStack.pop();
			r.on_oper_stack_pop();

			this.logOperStack();

			return r;
		}

		public getPanel()
		{
			return this.drawPanel;
		}

		public setPanel(p: DrawPanel)
		{
			this.drawPanel = p;
		}

		public getLayer()
		{
			return this.drawLayer;
		}

		public setCursor(c: oc.Cursor = oc.Cursor.auto)
		{
			var p = this.getPanel();
			if (p == null)
				return;
			p.setCursor(c);
		}


		public getSelectedItems()
		{
			return this.selectItems;
		}

		public clearSelectedItems()
		{
			//this.canSelectItems = [];
			this.selectItems=[];
		}

		public getSelectedItem()
		{
			if (this.selectItems.length <= 0)
				return null;

			return this.selectItems[this.selectItems.length - 1];
		}

		/**
		 * 
		 */
		public getDragOverSelItems():DrawItem[]
		{
			return this.dragoverSelItems;
		}

		public getCurMouseDownItem()
		{
			return this.curMouseDownItem;
		}

		public getCurMouseOnItem()
		{
			return this.curMouseOnItem;
		}

		public getCurMouseOnItems()
		{
			return this.curMouseOnItems
		}

		public isCurMouseOnItem(item:DrawItem|null):boolean
		{
			if(item==null||this.curMouseOnItems==null||this.curMouseOnItems.length<=0)
				return false;
			for(var di of this.curMouseOnItems)
			{
				if(di==item)
					return true ;
			}
			return false;
		}

		public getSelectedItemDrag()
		{
			//return this.getSelectedItem()
			return this.selectedItemDrag;
		}

		public findRelatedItemsByDrawPt(dxy: base.Pt,b_select:boolean=false)
		{
			var panel = this.drawPanel;
			if (panel == null)
				return null;
			var dl = this.getLayer();
			if (dl == null)
				return null;
			var items = dl.getItemsShow();
			if (items == null || items.length <= 0)
				return null;
			var r = [];
			for (var tmpi of items)
			{
				if(!tmpi.isVisiable())
					continue;
				var b = false;
				if(b_select)
				{
					b = tmpi.chkCanSelectDrawPt(dxy.x, dxy.y);
				}
				else
				{
					b = tmpi.containDrawPt(dxy.x,dxy.y) ;
					if (b)
					{
						if(!tmpi.bMouseIn)
						{
							tmpi.bMouseIn=true;
							tmpi.on_mouse_in();
						}
					}
					else
					{
						if(tmpi.bMouseIn)
						{
							tmpi.bMouseIn = false;
							tmpi.on_mouse_out();
						}
					}
				}

				if(b)
					r.push(tmpi);
					
			}

			if (r.length > 0)
				return r;
			else
				return null;
		}

		public findCanSelectByDrawPt(dxy: base.Pt)
		{
			return this.findRelatedItemsByDrawPt(dxy,true);
		}

		private on_select_single(pxy: base.Pt, dxy: base.Pt)
		{
			let can_selitems = this.findCanSelectByDrawPt(dxy);

			let curitem = this.getSelectedItem();

			if (can_selitems == null)
			{
				if (this.selectItems.length > 0)
				{
					var oldsis = this.selectItems ;
					this.selectItems = [];//clear
					//this.canSelectItems=[];
					//if(this.drawPanel!=null)
					//	this.drawPanel.update_draw();
					//this.fireSelectedChged();

					for(var si of oldsis)
						si.on_selected(false) ;

					this.drawPanel.MODEL_fireSelectedChged(this);
				}
			}
			else
			{//
				if(can_selitems.length==1)
				{
					if(curitem==can_selitems[0])
						return;
					var oldsis = this.selectItems ;
					this.selectItems=can_selitems;

					//then trigger event
					for(var si of oldsis)
						si.on_selected(false) ;
					can_selitems[0].on_selected(true) ;
				}
				else
				{//swift in can select items
					if(this.selectItems.length==1)
					{
						var idx = can_selitems.indexOf(this.selectItems[0]);
						if(idx<0)
						{
							var oldsi = this.selectItems[0];
							this.selectItems[0] = can_selitems[0];

							oldsi.on_selected(false) ;
							this.selectItems[0].on_selected(true) ;
						}
						else
						{
							idx ++;
							if(idx==can_selitems.length)
								idx = 0 ;
							
							var oldsi = this.selectItems[0];
							this.selectItems[0] = can_selitems[idx];

							oldsi.on_selected(false) ;
							this.selectItems[0].on_selected(true) ;
						}
					}
					else
					{
						var tmpold:DrawItem|null=null;
						if(this.selectItems.length>0)
							tmpold = this.selectItems[0];

						this.selectItems[0] = can_selitems[0];

						if(tmpold!=null)
							tmpold.on_selected(false)
						this.selectItems[0].on_selected(true) ;
					}
				}
				this.drawPanel.MODEL_fireSelectedChged(this);
			}
		}

		/**
		 * set current selected item
		 * @param item 
		 */
		public setSelectedItem(item: DrawItem | null)
		{
			if (item == null)
				this.selectItems = [];//clear
			else
				this.selectItems = [item];
			this.drawPanel.MODEL_fireSelectedChged(this);
		}

		public clearSelectedItem()
		{
			this.setSelectedItem(null);
		}

		public MODEL_fireOperChged(oper: DrawOper | null)
		{
			this.drawPanel.MODEL_fireOperChged(this, oper);
		}

		public on_mouse_down(pxy: base.Pt, dxy: base.Pt,me:_MouseEvent)
		{
			var canselitems = this.findCanSelectByDrawPt(dxy);

			if(me.button==MOUSE_BTN.LEFT)
			{//
				if(canselitems!=null&&canselitems?.length==1)
				{//only one item ,it can make selected
					this.on_select_single(pxy, dxy);
				}
				else
				{//make up to descision
					this.bsel_down = true;
					this.bsel_down_mv = 0;
				}
			}
			
			if (canselitems != null)
			{
				this.curMouseDownItem = canselitems[canselitems.length - 1];
				var curselitem = this.getSelectedItem();
				if (curselitem != null && canselitems.length > 0 && this.isCurMouseOnItem(curselitem))//curselitem == this.curMouseDownItem)
				{
					this.selectedItemDrag = curselitem;
				}
			}
			else
			{
				this.selectedItemDrag = null;
				this.curMouseDownItem = null;
			}
		}

		public on_mouse_downlong(pxy: base.Pt, dxy: base.Pt,me:_MouseEvent)
		{

		}

		public on_mouse_mv(pxy: base.Pt, dxy: base.Pt,me:_MouseEvent)
		{
			var canselitems = this.findCanSelectByDrawPt(dxy);
			this.curMouseOnItem = canselitems != null ? canselitems[canselitems.length - 1] : null;
			this.curMouseOnItems = canselitems ;
			if (this.bsel_down)
			{
				this.bsel_down_mv++;
				//console.log("mouse mv--->");
			}
		}

		public on_mouse_up(pxy: base.Pt, dxy: base.Pt,me:_MouseEvent)
		{
			if (this.bsel_down && this.bsel_down_mv < 2)
				this.on_select_single(pxy, dxy);
			this.bsel_down = false;
		}

		public on_mouse_wheel(pxy: base.Pt, dxy: base.Pt, delta: number)
		{

		}

		public on_mouse_dbclk(pxy: base.Pt, dxy: base.Pt,me:_MouseEvent)
		{

		}

		public on_mouse_clk(pxy: base.Pt, dxy: base.Pt,me:_MouseEvent)
		{

		}

		public on_mouse_drop(pxy:base.Pt,dxy:base.Pt,dd:DROP_DATA)
		{

		}

		public on_mouse_dragover(pxy:base.Pt,dxy:base.Pt,dd:DROP_DATA)
		{

		}

		public on_mouse_dragleave(pxy:base.Pt,dxy:base.Pt,dd:DROP_DATA)
		{

		}

		public on_key_down(e: KeyboardEvent)
		{
			
		}

		protected doCopyPaste(e:KeyboardEvent)
		{
			if(this.copyPasteUrl==null)
				return ;
			var sis = this.getSelectedItems();
			if(e.ctrlKey)
			{
				if(e.key=="c")
				{//copy
					if(sis.length==0)
						return;
					var cpob=[];
					for(var si of sis)
					{
						cpob.push(si.extract());
					}
					var pm={};
					pm["data_tp"]="drawitems";
					pm["op"]="copy";
					pm["items_json"] = JSON.stringify(cpob) ;
					$.ajax({
						type: 'post',
						url:this.copyPasteUrl,
						data: pm,
						async: true,
						success: function (result) {  
							
						}
					});
				}
				else if(e.key=="v")
				{//paste
					var pm={};
					pm["op"]="paste";
					$.ajax({
						type: 'post',
						url:this.copyPasteUrl,
						data: pm,
						async: true,
						success: (result)=> {  
							var ob:[] = [];
							eval("ob="+result);
							for(var o of ob)
							{
								this.getLayer().copyByJSON(o) ;
							}
						}
					});
				}
			}
		}

		public on_key_press(e: KeyboardEvent)
		{

		}

		public on_key_up(e: KeyboardEvent)
		{

		}

		public on_key_event(tp: KEY_EVT_TP, e: KeyboardEvent)
		{
			//console.log("key code="+e.keyCode);
			var curoper = this.getCurOper();
			var opers = this.operStack;
			var bend = false;
			switch (tp)
			{
				case KEY_EVT_TP.Down:
					if (!curoper.maskInteractEvent())
						this.on_key_down(e);
					for (var i = opers.length - 1; i >= 0; i--)
					{
						if (bend = !opers[i].on_key_down(e))
							break;
					}
					break;
				case KEY_EVT_TP.Press:
					if (!curoper.maskInteractEvent())
						this.on_key_press(e);
					for (var i = opers.length - 1; i >= 0; i--)
					{
						if (bend = !opers[i].on_key_press(e))
							break;
					}
					break;
				case KEY_EVT_TP.Up:
					if (!curoper.maskInteractEvent())
						this.on_key_up(e);
					for (var i = opers.length - 1; i >= 0; i--)
					{
						if (bend = !opers[i].on_key_up(e))
							break;
					}
					break;
			}
		}

		public on_mouse_event(tp: MOUSE_EVT_TP, e: any)
		{
			if (this.drawPanel == null)
				return;
			var pxy = this.drawPanel.getEventPixel(e);
			var dxy = this.drawPanel.transPixelPt2DrawPt(pxy.x, pxy.y);
			//var opers = this.get_interact_opers();
			var opers = this.operStack;
			var curoper = this.getCurOper();
			var bend = false;

			var ritems = this.findRelatedItemsByDrawPt(dxy);
			if(ritems!=null)
			{
				for(var tmpi of ritems)
				{
					tmpi.on_mouse_event(tp,pxy,dxy,e as _MouseEvent);
					tmpi.on_mouse_over(tp,pxy,dxy) ;
				}
			}
			else
			{
				this.getLayer().on_mouse_event(tp, pxy, dxy,e as _MouseEvent);
			}

			switch (tp)
			{
				case MOUSE_EVT_TP.Down:
					if (!curoper.maskInteractEvent())
						this.on_mouse_down(pxy, dxy,e);
					for (var i = opers.length - 1; i >= 0; i--)
					{
						if (bend = !opers[i].on_mouse_down(pxy, dxy,e))
							break;
					}
					break;
				case MOUSE_EVT_TP.DownLong:
					if (!curoper.maskInteractEvent())
						this.on_mouse_downlong(pxy, dxy,e);
					for (var i = opers.length - 1; i >= 0; i--)
					{
						if (bend = !opers[i].on_mouse_downlong(pxy, dxy,e))
							break;
					}
					break;
				case MOUSE_EVT_TP.Move:
					if (!curoper.maskInteractEvent())
						this.on_mouse_mv(pxy, dxy,e);
					for (var i = opers.length - 1; i >= 0; i--)
					{
						if (bend = !opers[i].on_mouse_mv(pxy, dxy,e))
							break;
					}
					break;
				case MOUSE_EVT_TP.Up:
					if (!curoper.maskInteractEvent())
						this.on_mouse_up(pxy, dxy,e);
					for (var i = opers.length - 1; i >= 0; i--)
					{
						if (bend = !opers[i].on_mouse_up(pxy, dxy,e))
							break;
					}
					break;
				case MOUSE_EVT_TP.DbClk:
					if (!curoper.maskInteractEvent())
						this.on_mouse_dbclk(pxy, dxy,e);
					for (var i = opers.length - 1; i >= 0; i--)
					{
						if (bend = !opers[i].on_mouse_dbclk(pxy, dxy,e))
							break;
					}
					break;
				case MOUSE_EVT_TP.Clk:
					if (!curoper.maskInteractEvent())
						this.on_mouse_clk(pxy, dxy,e);
					for (var i = opers.length - 1; i >= 0; i--)
					{
						if (bend = !opers[i].on_mouse_clk(pxy, dxy,e))
							break;
					}
					break;
				case MOUSE_EVT_TP.Wheel:
					var delta = 0;
					if (!e) e = window.event;
					if (e.wheelDelta)
					{
						delta = e.wheelDelta / 120;
						if (window["opera"]) delta = -delta;
					} else if (e.detail)
					{//
						delta = -e.detail / 3;
					}

					if (!curoper.maskInteractEvent())
						this.on_mouse_wheel(pxy, dxy, delta);
					for (var i = opers.length - 1; i >= 0; i--)
					{
						if (bend = !opers[i].on_mouse_wheel(pxy, dxy, delta))
							break;
					}
					break;
				case MOUSE_EVT_TP.DragOver://cannot getData ,only data name list
					var pvs = oc.util.getDragEventData(e);
					var val = pvs["_val"];//e.dataTransfer.getData("_val");
					var dtp = pvs["_tp"];//e.dataTransfer.getData("_tp");
					var g = pvs["_g"];
					//console.log(e.dataTransfer.types);
					
					if(dtp==null||dtp==undefined||dtp=="")
						break;
					var dd:DROP_DATA={_tp:dtp,_val:val,_g:g} ;
					if (!curoper.maskInteractEvent())
						this.on_mouse_dragover(pxy, dxy,dd);
					for (var i = opers.length - 1; i >= 0; i--)
					{
						if (bend = !opers[i].on_mouse_dragover(pxy, dxy,dd))
							break;
					}
					break;
				case MOUSE_EVT_TP.DragLeave://cannot getData ,only data name list
					var pvs = oc.util.getDragEventData(e);
					var val = pvs["_val"];//e.dataTransfer.getData("_val");
					var dtp = pvs["_tp"];//e.dataTransfer.getData("_tp");
					var g = pvs["_g"];
					//console.log(e.dataTransfer.types);
					
					if(dtp==null||dtp==undefined||dtp=="")
						break;
					var dd:DROP_DATA={_tp:dtp,_val:val,_g:g} ;
					if (!curoper.maskInteractEvent())
						this.on_mouse_dragleave(pxy, dxy,dd);
					for (var i = opers.length - 1; i >= 0; i--)
					{
						if (bend = !opers[i].on_mouse_dragleave(pxy, dxy,dd))
							break;
					}
					break;
				case MOUSE_EVT_TP.Drop:
					var pvs = oc.util.getDragEventData(e);
					var val = pvs["_val"];//e.dataTransfer.getData("_val");
					var dtp = pvs["_tp"];//e.dataTransfer.getData("_tp");
					var g = pvs["_g"];
					if(dtp==null||dtp==undefined||dtp=="")
						break;
					var dd:DROP_DATA={_tp:dtp,_val:val,_g:g} ;
					if (!curoper.maskInteractEvent())
						this.on_mouse_drop(pxy, dxy,dd);
					for (var i = opers.length - 1; i >= 0; i--)
					{
						if (bend = !opers[i].on_mouse_drop(pxy, dxy,dd))
							break;
					}
					break;
			}
		}
	}

	/**
	 * only for drawitems to show and response to hmi event
	 * 1) it has no selection
	 * 2) trick mouse event
	 */
	export class DrawInteractShow extends DrawInteract
	{

	}

}





