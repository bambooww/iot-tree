/**
 * modify line
 */
module oc.interact
{
	export class OperChgLine extends DrawOper
	{
		downPt: base.Pt | null = null;

		diLnCtrlTp: di.DILine_CTRLPT | null = null;

        itemDragPt: base.Pt | null = null;
        
        diLn:oc.di.DILine|null = null ;

		public constructor(interact: DrawInteract, layer: DrawLayer)
		{
			super(interact, layer);
		}
		
		public getOperName():string
		{
			return "chg_line";
		}
        
        public setDILine(ln:oc.di.DILine|null)
        {
            this.diLn = ln ;
		}

		public on_oper_stack_push():void
		{
			this.setCursor(Cursor.crosshair);
		}

		public on_oper_stack_pop():void
		{
			this.setCursor(Cursor.auto);
		}
		
		public chkOperFitByDrawPt(pxy:base.Pt,dxy:base.Pt):boolean
		{
			if(this.diLn==null)
				return false;
			var r= this.diLn.chkDrawPtOnCtrlPt(dxy.x,dxy.y) ;
			if(r==null)
				return false;
			return true;
		}

		public on_mouse_down(pxy: base.Pt, dxy: base.Pt):boolean
		{
			if(this.diLn==null)
				return true;
			this.diLnCtrlTp = this.diLn.chkDrawPtOnCtrlPt(dxy.x,dxy.y) ;
			if(this.diLnCtrlTp==null)
			{//normal drag
				return true;
			}
			this.downPt = dxy;
			return false;//stop event delivy
		}

		public on_mouse_mv(pxy: base.Pt, dxy: base.Pt):boolean
		{
			var lay = this.getDrawLayer();
			if (lay == null)
				return true;
			if(this.diLn==null)
				return true;

			if(this.diLnCtrlTp==null)
			{//mouse out,pop me
				if(this.diLn.chkDrawPtOnCtrlPt(dxy.x,dxy.y)==null)
				{
					this.popOperStackMe();
					return true;
				}
				else
				{
					this.setCursor(Cursor.crosshair);
					return false;
				}
					
			}
			this.diLn.setCtrlDrawPt(this.diLnCtrlTp,dxy.x,dxy.y);
			return false;
		}

		public on_mouse_up(pxy: base.Pt, dxy: base.Pt)
		{
			this.downPt = null;
			//
			this.diLnCtrlTp = null;
			this.diLn = null;
			this.popOperStackMe();
			return true;
		}

		// public on_mouse_wheel(pxy: base.Pt, dxy: base.Pt, delta: number)
		// {
		// 	var p = this.getDrawPanel();
		// 	if (p == null)
		// 		return true;
		// 	p.ajustDrawResolution(dxy.x, dxy.y, delta);
		// 	return true;
		// }

		protected draw_oper(): void
		{
			
		}
	}
}