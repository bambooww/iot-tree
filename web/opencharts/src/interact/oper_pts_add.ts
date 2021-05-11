/**
 * add multi pts line or polygon
 */
module oc.interact
{
	export class OperPtsAdd extends DrawOper
	{
        //diCN:string;

        addedDI:oc.di.DIPts|null=null;

        addOpts:{}|undefined=undefined;

        tp:string ;

		public constructor(interact: DrawInteract, layer: DrawLayer,opts:{}|undefined,tp:string)
		{
            super(interact, layer);
            //this.diCN = di_cn;//di class name DIPtsPoly  DIPtsLn
            if(opts!=undefined)
                this.addOpts = opts;
            this.tp = tp ;
        }

        public getOperName():string
		{
			return "pyln_add";
		}
        

		public on_oper_stack_push():void
		{
			this.setCursor(Cursor.crosshair);
		}

		public on_oper_stack_pop():void
		{
			this.setCursor(Cursor.auto);
		}
		
		public chkOperFitByDrawPt(pxy:base.Pt,dxy:base.Pt): boolean
        {
            return false;
        }

        public maskInteractEvent():boolean
		{
			return true;
        }
        
        private firstPt:oc.base.Pt|null=null ;
        private secondPt:oc.base.Pt|null=null ;

        private mvPt:oc.base.Pt|null = null ;
        
		public on_mouse_down(pxy: base.Pt, dxy: base.Pt,me:_MouseEvent):boolean
		{
            var p = this.getDrawPanel();
            var lay = this.getDrawLayer();
            if(p==null||lay==null)
            {
                this.popOperStackMe();
                return true;
            }
            if(me.button==MOUSE_BTN.RIGHT)
            {
                //if(this.secondPt!=null)
                this.popOperStackMe();
                return true ;
            }
            if(this.firstPt==null)
            {
                this.firstPt = dxy ;
                return false ;
            }
            if(this.secondPt==null)
            {
                this.secondPt = dxy ;
                return false ;
            }
            if(this.addedDI!=null)
            {
                this.addedDI.addPt(dxy.x,dxy.y) ;
            }
            
			return false;//stop event delivy
        }
        
        public on_mouse_dbclk(pxy: base.Pt, dxy: base.Pt,me:_MouseEvent):boolean
        {
            this.popOperStackMe();
            return true ;
        }

		public on_mouse_mv(pxy: base.Pt, dxy: base.Pt,me:_MouseEvent):boolean
		{
            var p = this.getDrawPanel();
            var lay = this.getDrawLayer();
            if(p==null||lay==null)
            {
                this.popOperStackMe();
                return true;
            }
			var lay = this.getDrawLayer();
            if (lay == null)
            {
                this.popOperStackMe();
                return true;
            }
            if(this.firstPt==null)
            {
                return false ;
            }
            if(this.secondPt==null)
            {//draw line
                this.mvPt = dxy ;
                lay.update_draw();
                return false ;
            }
            if(this.addedDI==null)
            {
                switch(this.tp)
                {
                case "ln":
                    this.addedDI = new oc.di.DIPtsLn({});
                    break ;
                case "py":
                    this.addedDI = new oc.di.DIPtsPy({});
                    break ;
                case "pipe":
                    this.addedDI = new oc.di.DIPtsPipe({});
                    break ;
                default:
                    return false;
                }
                
                this.addedDI.addPt(this.firstPt.x,this.firstPt.y) ;
                this.addedDI.addPt(this.secondPt.x,this.secondPt.y) ;
                this.addedDI.addPt(dxy.x,dxy.y) ;
                lay.addItem(this.addedDI) ;
                this.getInteract().setSelectedItem(this.addedDI);
            }
            
            this.addedDI.chgLastPt(dxy.x,dxy.y) ;
            //this.addedDI.setDrawEndXY(lay,dxy.x,dxy.y);
			return false;
        }
        

		// public on_mouse_up(pxy: base.Pt, dxy: base.Pt,me:_MouseEvent)
		// {
        //     this.addedDI=null;
        //     this.popOperStackMe();
		// 	return true;
		// }

        protected draw_oper(): void
        {
            var lay = this.getDrawLayer();
            if(lay==null)
                return ;
            if(this.firstPt==null||this.mvPt==null)
            {
                return ;
            }
            if(this.secondPt!=null)
            {
                return ;
            }

            var p1 = lay.transDrawPt2PixelPt(this.firstPt.x,this.firstPt.y) ;
            var p2 = lay.transDrawPt2PixelPt(this.mvPt.x,this.mvPt.y) ;
            //draw line
            var cxt = lay.getCxtCurDraw() ;
            cxt.save();
            cxt.beginPath() ;
            cxt.moveTo(p1.x,p1.y) ;
            cxt.lineTo(p2.x,p2.y) ;
			cxt.restore();
		}
		
		
	}
}