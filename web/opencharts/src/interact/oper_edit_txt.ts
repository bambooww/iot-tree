/**
 * modify rect
 */
module oc.interact
{
	export class OperEditTxt extends DrawOper
	{
        editDI:DrawItem;

        propName:string;

        private editEle:HTMLDivElement|null=null;

		public constructor(interact: DrawInteract, layer: DrawLayer,di:DrawItem,propn:string)
		{
            super(interact, layer);
            this.editDI = di;//di
            this.propName = propn;
        }

        public getOperName():string
		{
			return "chg_txt";
		}
        
		public on_oper_stack_push():void
		{
            var rect = this.editDI.getBoundRectPixel();
            if(rect==null)
            {
                this.popOperStackMe();
                return;
            }

            this.setCursor(Cursor.text);
            var ele= this.getDrawPanel().getHTMLElement() ;
            this.editEle = document.createElement("div");
            var txt = this.editDI[this.propName];
            var edele = $(this.editEle) ;
            edele.css("position","absolute");
            edele.css("left",rect.x+"px");
            edele.css("top",rect.y+"px");
            $(this.editEle).css("z-index","60000");
            if(rect.h<20)
                rect.h = 20;
            if(rect.w<30)
                rect.w=40 ;
            this.editEle.innerHTML=`<input id="oper_edit_txt" type="text" size="10" style="width:${rect.w}px;height:${(rect.h-2)}px;font-size:${rect.h-8}px" value="${txt}"/>`;
            $(ele).append(this.editEle);
		}

		public on_oper_stack_pop():void
		{
            this.setCursor(Cursor.auto);
            if(this.editEle!=null)
            {
                var ele= this.getDrawPanel().getHTMLElement() ;
                ele.removeChild(this.editEle);
            }
		}
		
		public chkOperFitByDrawPt(pxy:base.Pt,dxy:base.Pt): boolean
        {
            
            return false;
        }
        
		public on_mouse_down(pxy: base.Pt, dxy: base.Pt):boolean
		{
            var di = this.getInteract().getCurMouseOnItem();
            if(di==this.editDI)
            {
                return false;
            }
            this.popOperStackMe();
            return true;
		}

		public on_mouse_mv(pxy: base.Pt, dxy: base.Pt):boolean
		{
			return false;
        }
        

		public on_mouse_up(pxy: base.Pt, dxy: base.Pt)
		{
			return false;
        }
        
        public on_key_down(e:KeyboardEvent):boolean
		{
            if(this.editEle==null)
                return true;
            if(e.keyCode==13)
            {
                var v = $("#oper_edit_txt").val();
                if(v==null||v=="")
                {
                    //todo 
                    return false;
                }
                var pnv = {} ;
                pnv[this.propName]=v;
                this.editDI.setPropValue(pnv);
                this.popOperStackMe();
                return false;
            }
			return true;
		}

        protected draw_oper(): void
        {
            
		}
		
		
	}
}