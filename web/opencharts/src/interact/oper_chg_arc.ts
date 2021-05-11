/**
 * modify line
 */
module oc.interact
{
	export class OperChgArc extends DrawOper
	{
		downPt: base.Pt | null = null;

		idArcCtrlPt: string | null = null;

        itemDragPt: base.Pt | null = null;
        
        diArc:oc.di.DIArc|null = null ;

		public constructor(interact: DrawInteract, layer: DrawLayer)
		{
			super(interact, layer);
        }

        public getOperName():string
		{
			return "chg_arc";
		}
        
        public setDIArc(arc:oc.di.DIArc|null)
        {
            this.diArc = arc ;
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
			if(this.diArc==null)
				return false;
			var r= this.diArc.chkPtOnCtrl(pxy,dxy) ;
			if(r==null)
				return false;
			return true;
        }
        
        public maskInteractEvent():boolean
		{
			return true;
		}

		public on_mouse_down(pxy: base.Pt, dxy: base.Pt):boolean
		{
			if(this.diArc==null)
				return true;
			this.idArcCtrlPt = this.diArc.chkPtOnCtrl(pxy,dxy) ;
			if(this.idArcCtrlPt==null)
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
			if(this.diArc==null)
                return true;
            if(this.idArcCtrlPt==null)
            {
                if(this.diArc.chkPtOnCtrl(pxy,dxy)==null)
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
                
            // if(this.idArcCtrlPt)
            // {//mouse out,pop me
			// 	if(this.diArc.chkDrawPtOnCtrlPt(dxy.x,dxy.y)==null)
			// 	{
			// 		this.popOperStackMe();
			// 		return true;
			// 	}
            // }
			
			this.diArc.setCtrlDrawPt(this.idArcCtrlPt,dxy.x,dxy.y);
			return false;
		}

		public on_mouse_up(pxy: base.Pt, dxy: base.Pt)
		{
			this.downPt = null;
			//
			this.idArcCtrlPt = null;
			this.diArc = null;
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