/**
 * win is container which can has many unit.it can limit units in is area.
 */
module oc.iott
{
    export class Win extends oc.DrawItemGroup// implements IActionNode
    {
        private actions:DrawItemRect[]=[];

        private actionEle:HTMLElement|null=null;

        private actionEleId:string|null=null;
        private actEleInnerX:number=0;
        private actEleInnerY:number=0;
        //menuBtn:
		public constructor(opts: {} | undefined)
		{
            super(opts);
            this.title = "win";
		}

		public getClassName()
		{
			return "oc.iott.Win";
        }
        
        public getActionTypeName():string
		{
            var n = this.getName() ;
            if(n==null||n=="")
                return "win-" ;
            return "win-"+n ;
		}

        public getActionElement():HTMLElement|null
        {
            if(this.actionEle!=null)
                return this.actionEle;
            if(this.actionEleId==null||this.actionEleId=="")
                return null ;
            this.actionEle = document.getElementById(this.actionEleId);
            if(this.actionEle==null)
                return null ;
            $(this.actionEle).css("position","absulate");
            return this.actionEle;
        }

		static WIN_PNSG = {
			_cat_name: "win", _cat_title: "IOTT Win",
            actionEleId: { title: "Action Ele Id", type: "string"},
            actEleInnerX: { title: "Inner X", type: "int"},
            actEleInnerY: { title: "Inner Y", type: "int"},
		};

		public getPropDefs(): Array<any>
		{
			var r = super.getPropDefs();
			r.push(Win.WIN_PNSG);
			return r;
        }
        
        public on_mouse_in()
		{
            
            this.MODEL_fireChged([]);
        }

        public on_mouse_out()
        {
            if(this.actionEle==null)
                return ;
            //console.log("win mouse on_mouse_in");
            var p = this.getPixelXY();
            $(this.actionEle).css("display","none");
        }

        private displayActionEle()
        {
            var actele = this.getActionElement();
            if(actele==null)
                return ;
            //console.log("win mouse on_mouse_in");
            var p = this.getDrawXY();
            if(p==null)
                return ;
            var c = this.getContainer();
            if(c==null)
                return ;
            var div = $(actele);
            var innerp = c.transDrawPt2PixelPt(p.x+this.actEleInnerX,p.y+this.actEleInnerY);
            div.css("display","");
            div.css("left",innerp.x+"px");
            div.css("top",innerp.y+"px");
        }
        
        public draw(cxt: CanvasRenderingContext2D, c: IDrawItemContainer)
		{
            super.draw(cxt,c);
			
            if(this.isMouseIn())
            {
                var pt = this.getDrawXY();
                var dxy = c.transDrawPt2PixelPt(pt.x, pt.y);
                for(var i = 0 ; i < this.actions.length ; i ++)
                {
                    var di = this.actions[i];
                    di.setDrawXY(pt.x + 100+i*70,pt.y+30) ;
                    di.setDrawSize(50,50);
                    di.draw(cxt,c);
                }

                //cxt.fillRect(dxy.x+100,dxy.y+30,50,50);
                //util.drawRect(cxt,dxy.x+100,dxy.y+30,50,50,2,"pink",1,null);
                this.displayActionEle();
            }
		}
    }
}