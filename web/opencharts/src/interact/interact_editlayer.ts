module oc.interact
{
	export class InteractEditLayer extends DrawInteract
	{
		//operDrag?:OperDrag;
		operChgLn:OperChgLine;
		operChgRect:OperChgRect;
		operChgArc:OperChgArc;

		

		public constructor(panel:DrawPanel,layer:DrawLayer,opts:{}|undefined)
		{
			super(panel,layer,opts);
		
			this.operChgLn = new OperChgLine(this,layer);
			this.operChgRect = new OperChgRect(this,layer) ;
			this.operChgArc = new OperChgArc(this,layer);
			
		}
		

		public on_mouse_mv(pxy: base.Pt, dxy: base.Pt,e:_MouseEvent)
		{
			super.on_mouse_mv(pxy,dxy,e);
			var p = this.getPanel();
			if (p == null)
				return;
			var curon = this.getCurMouseOnItem();
			var sitem = this.getSelectedItem();
			if(sitem!=null)//curon!=null&&curon==sitem)
			{//
				//this.setCursor("move");
				if(sitem instanceof di.DILine)
				{
					this.operChgLn.setDILine(sitem);
					if(this.operChgLn.chkOperFitByDrawPt(pxy,dxy))
						this.pushOperStack(this.operChgLn);
				}
				else if(sitem instanceof DrawItemRect)
				{
					this.operChgRect.setRect(sitem);
					if(this.operChgRect.chkOperFitByDrawPt(pxy,dxy))
						this.pushOperStack(this.operChgRect);

					if(sitem instanceof di.DIArc)
					{
						this.operChgArc.setDIArc(sitem);
						if(this.operChgArc.chkOperFitByDrawPt(pxy,dxy))
						{
							this.pushOperStack(this.operChgArc);
						}
					}
				}
			}
			else
			{
				//this.setCursor(undefined);
			}
		}

		public on_mouse_dbclk(pxy: base.Pt, dxy: base.Pt,e:_MouseEvent)
		{
			var curon = this.getCurMouseOnItem();
			if(curon==null)
				return;
			if(curon instanceof di.DITxt)
			{
				var lay = this.getLayer();
				var opedittxt = new OperEditTxt(this,lay,curon,"txt");
				this.pushOperStack(opedittxt);
			}
		}

		public on_mouse_drop(pxy: base.Pt, dxy: base.Pt,dd:DROP_DATA)
		{
			//console.log(dd);
			var di=null;
			switch(dd._tp)
			{
			case "icon_fa":
				di = new oc.di.DIIcon({unicode:dd._val});
				break ;
			case "unit":
				di = new oc.DrawUnitIns({});
				di.setUnitName(dd._val);
				break;
			}
			if(di!=null)
			{
				di.setDrawXY(dxy.x,dxy.y) ;
				this.getLayer().addItem(di);
			}
		}

		

		public on_key_down(e:KeyboardEvent)
		{
			super.doCopyPaste(e);

			if(this.isOperDefault())
			{
				//fconsole.log("k="+e.keyCode);
				switch(e.keyCode)
				{
				case 46://del
					this.removeSelectedItems();
					break;
				case 38://up
				case 37://left
				case 39://right
				case 40://down
					this.moveByKeyDir(e.keyCode);
					break;
				}
			}
		}

		public setOperAddItem(dicn:string,opts:{}|undefined):boolean
		{
			var lay = this.getLayer();
			if(lay==null)
				return false;
			var oper = new OperAddItem(this,lay,dicn,opts);
			this.pushOperStack(oper);
			return true ;
		}

		public setOperAddUnitIns(unitname:string):boolean
		{
			var lay = this.getLayer();
			if(lay==null)
				return false;
			var oper = OperAddItem.createOperAddByUnitName(this,lay,unitname,undefined);
			if(oper==null)
				return false;
			this.pushOperStack(oper);
			return true ;
		}

		private removeSelectedItems()
		{
			var sis = this.getSelectedItems() ;
			if(sis.length>0)
			{
				this.clearSelectedItems();
				for(var si of sis)
				{
					si.removeFromContainer();
				}
			}
		}

		private moveByKeyDir(keycode:number)
		{
			var p = this.getPanel() ;
			switch(keycode)
			{
			case 38://up
				p.movePixelCenter(0,-30);
				break;
			case 37://left
				p.movePixelCenter(-30,0);
				break;
			case 39://right
				p.movePixelCenter(30,0);
				break;
			case 40://down
				p.movePixelCenter(0,30);
				break;
			}
		}
	} // end InteractEditLayerLayer
	
	
	
	
}


