/**
 * modify rect
 */
module oc.interact
{

	export class OperChgRect extends DrawOper
	{

		downPt: base.Pt | null = null;

		rectCtrlTp:string | null = null;

        itemDragPt: base.Pt | null = null;
        
        rect:oc.DrawItemRect|null = null ;

		public constructor(interact: DrawInteract, layer: DrawLayer)
		{
			super(interact, layer);
		}
		
		public getOperName():string
		{
			return "chg_rect";
		}
        
        public setRect(rect:oc.DrawItemRect|null)
        {
            this.rect = rect ;
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
			if(this.rect==null)
				return false;
			var r= this.rect.chkPtOnCtrl(pxy,dxy) ;
			if(r==null)
				return false;
			return true;
		}

		public on_mouse_down(pxy: base.Pt, dxy: base.Pt):boolean
		{
			if(this.rect==null)
				return true;
			this.rectCtrlTp = this.rect.chkPtOnCtrl(pxy,dxy) ;
			if(this.rectCtrlTp==null)
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
			if(this.rect==null)
				return true;

			if(this.rectCtrlTp==null)
            {//mouse out,pop me
                var tmpcpt = this.rect.chkPtOnCtrl(pxy,dxy)
                if(tmpcpt==null)
                {
                    this.popOperStackMe();
                    return true;
                }
                else
                {
					if(tmpcpt=="r")
						this.setCursor(Cursor.crosshair);
					else
                    	this.setCursor(Cursor[tmpcpt+"_resize"]);
                    return false;
                }
            }

			if(this.rectCtrlTp=="r"&&this.rect instanceof DrawItemRectR)
				this.setCursor(Cursor.crosshair);
			else
            	this.setCursor(Cursor[this.rectCtrlTp+"_resize"]);
            //chg rect
			//this.diRect.setCtrlDrawPt(this.diRectCtrlTp,dxy.x,dxy.y);
			if(this.rectCtrlTp=="r"&&this.rect instanceof DrawItemRectR)
				this.rect.changeRotate(dxy.x,dxy.y);
			else
            	this.rect.changeRect(this.rectCtrlTp,dxy.x,dxy.y)
			return false;
        }
        

		public on_mouse_up(pxy: base.Pt, dxy: base.Pt)
		{
			this.downPt = null;
			//if(this.itemDrag!=null)
			this.rectCtrlTp = null;
			this.rect = null;
			this.popOperStackMe();
			return true;
		}

        protected draw_oper(): void
        {
            
		}
		
		
	}
}