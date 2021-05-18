/**
 * for iottree panel
 */

module oc.hmi
{
	export class HMIInteractEdit extends DrawInteract
	{
		//operDrag?:OperDrag;
		operChgLn:oc.interact.OperChgLine;
		operChgRect:oc.interact.OperChgRect;
		operChgArc:oc.interact.OperChgArc;
		operPtsChg:oc.interact.OperPtsChg;
		

		public constructor(panel:DrawPanel,layer:DrawLayer,opts:{}|undefined)
		{
			super(panel,layer,opts);
		
			this.operChgLn = new oc.interact.OperChgLine(this,layer);
			this.operChgRect = new oc.interact.OperChgRect(this,layer) ;
			this.operChgArc = new oc.interact.OperChgArc(this,layer);
			this.operPtsChg = new oc.interact.OperPtsChg(this,layer);
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
				if(sitem instanceof di.DILine)
				{
					this.operChgLn.setDILine(sitem);
					if(this.operChgLn.chkOperFitByDrawPt(pxy,dxy))
						this.pushOperStack(this.operChgLn);
				}
				else if(sitem instanceof di.DIPts)
				{
					this.operPtsChg.setDIPts(sitem) ;
					if(this.operPtsChg.chkOperFitByDrawPt(pxy,dxy))
						this.pushOperStack(this.operPtsChg);
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
					}//
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
				var opedittxt = new oc.interact.OperEditTxt(this,lay,curon,"txt");
				this.pushOperStack(opedittxt);
			}
		}

		public on_mouse_dragover(pxy: base.Pt, dxy: base.Pt,dd:DROP_DATA)
		{
			var di=null;
			
			switch(dd._tp)
			{
			case "comp":
				var compid = dd._val;
				var comp = HMIComp.getItemByCompId(compid) ;
				if(comp==null)
				{
					return ;
				}
				break;
			case "divcomp":
				break ;
			case "hmi_sub":

				break;
			}
		}

		public on_mouse_dragleave(pxy: base.Pt, dxy: base.Pt,dd:DROP_DATA)
		{
			this.dragoverSelItems = [];
		}

		public on_mouse_drop(pxy: base.Pt, dxy: base.Pt,dd:DROP_DATA)
		{
			var di=null;
			console.log("drop->",dd);
			switch(dd._tp)
			{
			case "icon_fa":
				di = new oc.di.DIIcon({unicode:dd._val});
				di.setDrawXY(dxy.x,dxy.y) ;
				this.getLayer().addItem(di);
				break ;
			case "comp":
				var compid = dd._val;
				var lay = this.getLayer();//du.getLayer();
				var compins = new HMICompIns({}) ;
				lay.addItem(compins);
				compins.setDrawXY(dxy.x,dxy.y) ;
				compins.setCompId(compid) ;
				
				break;
			case "divcomp":
				var divcomp_uid = dd._val;
				var lay = this.getLayer();//du.getLayer();
				var divcomp = new oc.DIDivComp({}) ;
				lay.addItem(divcomp);
				divcomp.setDrawXY(dxy.x,dxy.y) ;
				divcomp.setCompUid(divcomp_uid) ;
				divcomp.setDrawSize(200,100) ;
				break;
			case "hmi_sub":
				var str = dd._val;
				var pm = {} ;
				eval("pm="+str) ;
				console.log(pm) ;
				var hmiid = pm["hmi_id"] ;
				var w = pm["w"];
				var h = pm["h"];
				//var hmipath = pm["hmi_path"] ;
				var lay = this.getLayer();//du.getLayer();
				var hmisub = new oc.hmi.HMISub({}) ;
				hmisub.setDrawXY(dxy.x,dxy.y) ;
				hmisub.setHmiSubId(hmiid) ;
				hmisub.setDrawSize(w,h) ;
				lay.addItem(hmisub) ;
				break;
			}
			this.dragoverSelItems = [];
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
			var oper = new oc.interact.OperAddItem(this,lay,dicn,opts);
			this.pushOperStack(oper);
			return true ;
		}

		public setOperAddPts(tp:string,opts:{}|undefined):boolean
		{
			var lay = this.getLayer();
			if(lay==null)
				return false;
			var oper = new oc.interact.OperPtsAdd(this,lay,opts,tp);
			this.pushOperStack(oper);
			return true ;
		}

		public setOperAddUnitIns(unitname:string):boolean
		{
			var lay = this.getLayer();
			if(lay==null)
				return false;
			var oper = oc.interact.OperAddItem.createOperAddByUnitName(this,lay,unitname,undefined);
			if(oper==null)
				return false;
			this.pushOperStack(oper);
			return true ;
		}

		private removeSelectedItems()
		{
			var si = this.getSelectedItem();//this.getSelectedItems() ;
			if(si==null)
				return ;
			console.log(si.getMark());
			if(si.getMark()==null)
			{
				this.clearSelectedItems();
				si.removeFromContainer();
				return ;
			}
			if(si.on_before_del())
			{//may 
				this.clearSelectedItems();
				si.removeFromContainer();
			}
			

			// if(sis.length>0)
			// {
			// 	this.clearSelectedItems();
			// 	for(var si of sis)
			// 	{
			// 		si.removeFromContainer();
			// 	}
			// }
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
	} // end InteractEditRep
	

	export class HMIInteractShow extends DrawInteract
	{
		public constructor(panel:DrawPanel,layer:DrawLayer,opts:{}|undefined)
		{
			super(panel,layer,opts);
		
			
		}
	}
}


