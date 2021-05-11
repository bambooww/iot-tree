/**
 * modify line
 */
module oc.interact
{
	export class OperPtsChg extends DrawOper
	{
        diPts:oc.di.DIPts|null=null;
        
        diChgPtIdx:number|null=null;

        downPt:oc.base.Pt|null=null;
        
		public constructor(interact: DrawInteract, layer: DrawLayer)
		{
			super(interact, layer);
		}
		
		public getOperName():string
		{
			return "pyln_chg";
		}
        
        public setDIPts(ln:oc.di.DIPts|null)
        {
            this.diPts = ln ;
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
			if(this.diPts==null)
				return false;
			var idx= this.diPts.chkPixelPtIdxOnPt(pxy.x,pxy.y) ;
			return idx!=null;
		}

		public on_mouse_down(pxy: base.Pt, dxy: base.Pt):boolean
		{
			if(this.diPts==null)
				return true;
			this.diChgPtIdx = this.diPts.chkPixelPtIdxOnPt(pxy.x,pxy.y) ;
			if(this.diChgPtIdx==null)
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
			if(this.diPts==null)
				return true;

			if(this.diChgPtIdx==null)
			{//mouse out,pop me
				if(this.diPts.chkPixelPtIdxOnPt(pxy.x,pxy.y)==null)
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
			this.diPts.setDrawPtIdx(this.diChgPtIdx,dxy.x,dxy.y);
			return false;
		}

		public on_mouse_up(pxy: base.Pt, dxy: base.Pt)
		{
			this.downPt = null;
			//
			this.diChgPtIdx = null;
			this.diPts = null;
			this.popOperStackMe();
			return true;
		}

		protected draw_oper(): void
		{
			
		}
	}
}