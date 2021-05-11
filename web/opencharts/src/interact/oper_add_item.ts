/**
 * modify rect
 */
module oc.interact
{
	export class OperAddItem extends DrawOper
	{
        diCN:string;

        addedDI:DrawItem|null=null;

        addOpts:{}|undefined=undefined;

		public constructor(interact: DrawInteract, layer: DrawLayer,di_cn:string,opts:{}|undefined)
		{
            super(interact, layer);
            this.diCN = di_cn;//di class name
            if(opts!=undefined)
                this.addOpts = opts;
        }

        public getOperName():string
		{
			return "additem";
		}
        
        static createOperAddByUnitName(interact: DrawInteract, layer: DrawLayer,unitname:string,opts:{}|undefined):OperAddItem|null
        {
            var du = DrawUnit.getUnitByName(unitname);
            if(du==null)
                return null;
            var di = new DrawUnitIns(undefined) ;
            di.setUnitName(unitname) ;
            var r = new OperAddItem(interact,layer,"",opts);
            r.addedDI = di ;
            return r ;
        }

        // static createOperAddByUnitName(interact: DrawInteract, layer: DrawLayer,unit_name:string)
        // {
            
        // }

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
        
		public on_mouse_down(pxy: base.Pt, dxy: base.Pt):boolean
		{
            var p = this.getDrawPanel();
            var lay = this.getDrawLayer();
            if(p==null||lay==null)
            {
                this.popOperStackMe();
                return true;
            }
            if(this.addedDI==null)
            {
                this.addedDI = DrawItem.createByFullClassName(this.diCN,this.addOpts,true);
                if(this.addedDI==null)
                {//failed
                    this.popOperStackMe();
                    return true;
                }
            }
            
            var beginr = this.addedDI.setDrawBeginXY(lay,dxy.x,dxy.y);
            lay.addItem(this.addedDI) ;
            this.getInteract().setSelectedItem(this.addedDI);
            if(!beginr)
            {//end
                this.popOperStackMe();
                return true;
            }
			return false;//stop event delivy
		}

		public on_mouse_mv(pxy: base.Pt, dxy: base.Pt):boolean
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
            
            if(this.addedDI==null)
            {
                //this.popOperStackMe();
                return false ;
            }
            this.addedDI.setDrawEndXY(lay,dxy.x,dxy.y);
			return false;
        }
        

		public on_mouse_up(pxy: base.Pt, dxy: base.Pt)
		{
			this.diCN="";
            this.addedDI=null;
            this.popOperStackMe();
			return true;
		}

        protected draw_oper(): void
        {
            
		}
		
		
	}
}