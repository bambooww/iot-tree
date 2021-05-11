/**
 * for iottree panel
 */

module oc.iott
{
	export class OperMember extends DrawOper
	{
		unitTN : UnitTN|null=null;

		curMem:Member|null= null ;

		private downPt:oc.base.Pt|null=null;
		private itemDragPt:oc.base.Pt|null=null;

		public constructor(interact: DrawInteract, layer: DrawLayer)
		{
			super(interact, layer);
		}

		public setUnitTN(u:UnitTN)
		{
			this.unitTN = u ;
		}

		public getOperName(): string
		{
			return "oper_member";
		}
		public on_oper_stack_push(): void
		{
			this.setCursor(Cursor.hand);
			console.log("oper mem push") ;
		}

		public on_oper_stack_pop(): void
		{
			this.setCursor(Cursor.auto);
			console.log("oper mem pop") ;
		}

		public chkOperFitByDrawPt(pxy: base.Pt, dxy: base.Pt): boolean
		{
			return false;
		}

		
		public on_mouse_down(pxy: base.Pt, dxy: base.Pt):boolean
		{
			if(this.unitTN==null)
				return false;
			for(var m of this.unitTN.getMembers())
			{
				if(m.chkCanSelectDrawPt(dxy.x,dxy.y))
				{
					this.curMem = m ;
					this.downPt = dxy ;
					this.itemDragPt = m.getDrawXY() ;
					this.curMem.setPosState(Member.PST_MOVING);
					break ;
				}
			}

			return false;//stop event delivy
		}

		public on_mouse_mv(pxy: base.Pt, dxy: base.Pt):boolean
		{
			var lay = this.getDrawLayer();
			if (lay == null)
				return true;
			if(this.unitTN==null)
				return true;

			if(this.curMem!=null&&this.downPt!=null&&this.itemDragPt!=null)
			{
				this.curMem.setDrawXY(this.itemDragPt.x + (dxy.x - this.downPt.x),
						this.itemDragPt.y + (dxy.y - this.downPt.y));
				return false;
			}

			var bin = this.unitTN.chkCanSelectDrawPt(dxy.x,dxy.y) ;
			if(!bin)
			{//mouse out,pop me
				this.popOperStackMe();
				return true;
			}
			
			return false;
		}

		public on_mouse_up(pxy: base.Pt, dxy: base.Pt)
		{
			if(this.unitTN!=null&&this.curMem!=null&&this.downPt!=null&&this.itemDragPt!=null)
			{
				if(this.unitTN.chkCanSelectDrawPt(dxy.x,dxy.y))
				{//in
					this.curMem.setPosState(Member.PST_DEFAULT) ;
				}
				else
				{//out
					this.curMem.setPosState(Member.PST_OUT) ;
				}

				this.curMem=null;
				return false;
			}

			this.popOperStackMe();
			return true;
		}

		protected draw_oper(): void
		{
			
		}

	}

	export class InteractEditRep extends DrawInteract
	{
		//operDrag?:OperDrag;
		operChgLn:oc.interact.OperChgLine;
		operChgRect:oc.interact.OperChgRect;
		operChgArc:oc.interact.OperChgArc;
		operPtsChg:oc.interact.OperPtsChg;

		operMem:OperMember ;
		

		public constructor(panel:DrawPanel,layer:DrawLayer,opts:{}|undefined)
		{
			super(panel,layer,opts);
		
			this.operChgLn = new oc.interact.OperChgLine(this,layer);
			this.operChgRect = new oc.interact.OperChgRect(this,layer) ;
			this.operChgArc = new oc.interact.OperChgArc(this,layer);
			this.operPtsChg = new oc.interact.OperPtsChg(this,layer);
			this.operMem = new OperMember(this,layer) ;
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
				else if(sitem instanceof UnitTN)
				{
					var r = sitem.getShowMemberRadius() ;
					if(r!=null)
					{
						if(sitem.chkCanSelectDrawPt(dxy.x,dxy.y))
						{
							this.operMem.setUnitTN(sitem) ;
							this.pushOperStack(this.operMem) ;
						}
						
					}
				}
				// else if(sitem instanceof Member)
				// {
				// 	var pbt = sitem.getBelongTo() ;
				// 	if(pbt!=null)
				// 	{
				// 		//if(pbt.chkCanSelectDrawPt(dxy.x,dxy.y))
				// 		{
				// 			this.operMem.setUnitTN(pbt) ;
				// 			this.pushOperStack(this.operMem) ;
				// 		}
				// 	}
				// }
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
			console.log(dd);
			switch(dd._tp)
			{
			case "unit":
				var un = dd._val;
				var du = DrawUnit.getUnitByName(un);
				if(du==null)
				{
					//oc.util.prompt_msg("");
					return ;
				}

				var du_g=dd._g;//du.getInsGroup();
				var tmpsis:DrawItem[]=[];
				if(du_g!=null&&du_g!=undefined)
				{
					for(var item of this.getLayer().getItemsShow())
					{
						if(item instanceof Win)
						{
							if(du_g==item.getName())
							{
								tmpsis.push(item) ;
							}
						}
					}
				}
				//console.log(tmpsis) ;
				this.dragoverSelItems = tmpsis;
				
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
			switch(dd._tp)
			{
			case "icon_fa":
				di = new oc.di.DIIcon({unicode:dd._val});
				di.setDrawXY(dxy.x,dxy.y) ;
				this.getLayer().addItem(di);
				break ;
			case "unit":
				var un = dd._val;
				var du = DrawUnit.getUnitByName(un);
				if(du==null)
				{
					//oc.util.prompt_msg("");
					break ;
				}
				var du_sz =du.getUnitDrawSize();
				var du_g=du.getInsGroup();
				var newurl = du.getInsNewUrl();
				var lay = this.getLayer();//du.getLayer();
				var new_cn = du.getInsNewCN();
				if(newurl==null||newurl==""||lay==null||new_cn==null||new_cn=="")
					break;
				var pm={};
				pm["layer_name"]=lay.getName();
				pm["unit_name"]=du.getName();
				$.ajax({
					type: 'post',
					url:newurl,
					data: pm,
					async: true,
					success: (result:string)=>{
						result = result.trim();
						if(result.indexOf("{")!=0)
						{//
							oc.util.prompt_err(result);
							return ;
						}
						var ob:any ;
						eval("ob="+result);
						var insid = ob["unit_ins_id"] as string ;
						var di:DrawUnitIns|undefined = eval(`new ${new_cn}({})`) as DrawUnitIns;
						if(di!=undefined)
						{
							di.setUnitName(un);
							di.setId(insid);
							di.setGroupName(du_g);

							di.setDrawSize(du_sz.w,du_sz.h);
							this.getLayer().addItem(di);
							di.setDrawXY(dxy.x,dxy.y) ;
						}
					}
				});
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
	
}


