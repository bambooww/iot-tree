
namespace oc.iott
{
    export abstract class Member extends DrawItem
	{
        static PST_DEFAULT:number=0;
        static PST_MOVING:number=1;
        static PST_OUT:number=2;

        b_show:boolean=false;
        private w: number = 100;
        private h: number = 100;
        
        private iconImg:any=null ;
        private closeImg:any=null ;


        protected headLen:number=30;

        private belongTo:UnitTN|null=null ;

        //position state 0 -default in parent circle 1-moving 2 out parent circle
        public posST:number=Member.PST_DEFAULT;
		
		public constructor(opts: {} | undefined)
		{
			super(opts);
		}

		// static Mmeber_PNS = {
		// 	_cat_name: "member", _cat_title: "Node Member",
		// 	rotate: { title: "Rotate", type: "float" },
			
		// };

		static Member_PNS = {
			_cat_name: "member", _cat_title: "Node Member",
			w: { title: "width", type: "int", readonly: true},
            h: { title: "height", type: "int", readonly: true},
            posST:{title:"Position State",type: "int", readonly: true}
		};

		public getPropDefs(): Array<any>
		{
			var r = super.getPropDefs();
			r.push(Member.Member_PNS);
			return r;
        }

        public getClassName()
        {
            return "oc.iott.Member";
        }

        public getDrawSize():oc.base.Size
		{
            if(this.posST==Member.PST_OUT)
                return this.getDrawOutSize() ;
            else
                //return {w:this.w,h:this.h};
                return  {w:100,h:100};
        }
        
        public getBoundRectDraw(): base.Rect | null
		{
            var pt = this.getDrawXY();
            var ds = this.getDrawSize() ;
			return new oc.base.Rect(pt.x, pt.y, ds.w, ds.h);
		}


        public getBelongTo():UnitTN|null
        {
            return this.belongTo ;
        }
        public setBelongTo(b:UnitTN)
        {
            this.belongTo = b ;
        }

        public getPosState():number
        {
            return this.posST ;
        }

        public setPosState(st:number)
        {
            this.posST = st ;
            if(st==Member.PST_DEFAULT)
            {//re position
                this.getMemDrawXY();
            }
        }

        public on_mouse_event(tp: MOUSE_EVT_TP, pxy: oc.base.Pt, dxy: oc.base.Pt,e:_MouseEvent)
        {
            super.on_mouse_event(tp,pxy,dxy,e);

            if(tp==MOUSE_EVT_TP.DownLong)
            {
                //console.log(this.getUnitName(),this.getTitle(),"down long") ;
                var pbt = this.getBelongTo() ;
                UnitTN.curShowMemberUnit = pbt ;
                this.MODEL_fireChged(["member"])
            }
            
            if(tp==MOUSE_EVT_TP.Down)
            {
                if(e.button==MOUSE_BTN.RIGHT)
                {
                    //this.setPosState(Member.PST_DEFAULT) ;
                    //this.MODEL_fireChged(["member"]) ;
                }

                if(Member.PST_OUT==this.posST)
                {
                    var s = this.getDrawOutSize() ;
                    var r:oc.base.Rect=new oc.base.Rect(this.x+s.w-this.headLen,this.y,this.headLen,this.headLen) ;
                    if(r.contains(dxy.x,dxy.y))
                    {
                        this.setPosState(Member.PST_DEFAULT) ;
                        this.MODEL_fireChged(["member"]) ;
                    }
                }
            }
        }

        public on_selected(b:boolean)
        {
            if(!b)
            {
                var bt = this.getBelongTo() ;
                if(bt!=null&&UnitTN.curShowMemberUnit==bt)
                {
                    for(var m of bt.getMembers())
                    {
                        if(m.getSelState().selected)
                            return ;
                    }
                    UnitTN.curShowMemberUnit = null ;
                    this.MODEL_fireChged(["member"])
                }
            }
        }

        private getMeCenter():oc.base.Pt|null
        {
            var bt = this.getBelongTo() ;
            if(bt==null)
                return null;
            var r = bt.getBoundRectDraw() ;
            if(r==null)
                return null ;
            var rad = bt.calcShowMemberRadius() ;
            if(rad==null)
                return null;
            rad=rad*2/3;

            var btmems = bt.getMembers();
            var num = btmems.length ;
            var idx = btmems.indexOf(this) ;
            var cxy = r.getCenter() ;
            //cal me center xy
            return oc.util.DrawTransfer.calcRotatePt({x:cxy.x+rad,y:cxy.y}, cxy, 360*idx/num);
        }

        private getMemDrawXY():base.Pt|null
		{
            switch(this.posST)
            {
                case Member.PST_MOVING:
                case Member.PST_OUT:
                    return this.getDrawXY();
                default:
                    var pt = this.getMeCenter();
                    if(pt==null)
                        return null ;
        
                    this.x = pt.x-50;
                    this.y = pt.y-50;
                    return  this.getDrawXY();;
            }
            
        }
        
        private drawDefault(ctx:CanvasRenderingContext2D,c:IDrawItemContainer)
        {
            
        }

        public abstract getBorderColor():string;

        public abstract getMemberTitle():string;

        public abstract getMemberIcon():string;

        

        private drawIcon(cxt:CanvasRenderingContext2D,w:number,h:number)
        {
			if(this.iconImg!=null)
			{
				cxt.drawImage(this.iconImg, 0, 0, w, h);
			}
			else
			{
                var imppath = this.getMemberIcon() ;
				var ii = new Image();
				ii.onload=()=>{
					this.iconImg = ii ;
					this.MODEL_fireChged([]);
				};
				ii.src=imppath ;
			}
        }

        private drawClose(cxt:CanvasRenderingContext2D,x:number,y:number,w:number,h:number)
        {
			if(this.closeImg!=null)
			{
				cxt.drawImage(this.closeImg, x, y, w, h);
			}
			else
			{
                var imppath = "/_iottree/res/tool_close1.gif"
				var ii = new Image();
				ii.onload=()=>{
					this.closeImg = ii ;
					this.MODEL_fireChged([]);
				};
				ii.src=imppath ;
			}
        }

        protected abstract getDrawOutSize():oc.base.Size;
        

        protected drawOut(ctx:CanvasRenderingContext2D,c:IDrawItemContainer):void
        {
            var ds = this.getDrawOutSize() ;
            var w = c.transDrawLen2PixelLen(true, ds.w);
			var h = c.transDrawLen2PixelLen(false, ds.h);
            
            var dlen = c.transDrawLen2PixelLen(true,this.headLen) ;
            var pt = this.getMemDrawXY();//this.getDrawXY();
            if(pt==null)
                return ;
            var p1 = c.transDrawPt2PixelPt(pt.x, pt.y);
            
			ctx.save();
			
            ctx.translate(p1.x, p1.y);
            
            this.drawClose(ctx,w-dlen,0,dlen,dlen) ;

            //this.drawIcon(ctx,w,h);
			//---
			ctx.lineWidth = 1;
			ctx.strokeStyle = this.getBorderColor();

			ctx.beginPath();
			//ctx.setLineDash([]);
			// ctx.arc(0, 0, 3, 0, Math.PI * 2);
			// ctx.stroke();
			// ctx.beginPath();
			// ctx.arc(w, 0, 3, 0, Math.PI * 2);
			// ctx.stroke();
			// ctx.beginPath();
			// ctx.arc(w, h, 3, 0, Math.PI * 2);
			// ctx.stroke();
			// ctx.beginPath();
			// ctx.arc(0, h, 3, 0, Math.PI * 2);
			// ctx.stroke();

			ctx.rect(0, 0, w, h);
            ctx.stroke();
            ctx.restore();
        }

        protected drawIn(ctx:CanvasRenderingContext2D,c:IDrawItemContainer)
		{
            var ds = this.getDrawSize() ;
            var w = c.transDrawLen2PixelLen(true, ds.w);
			var h = c.transDrawLen2PixelLen(false, ds.h);
            var pt = this.getMemDrawXY();//this.getDrawXY();
            if(pt==null)
                return ;
            var p1 = c.transDrawPt2PixelPt(pt.x, pt.y);
            
			ctx.save();
			
            ctx.translate(p1.x, p1.y);
            
            this.drawIcon(ctx,w,h);
			//---
			ctx.lineWidth = 1;
			ctx.strokeStyle = this.getBorderColor();

			ctx.beginPath();
			//ctx.setLineDash([]);
			ctx.arc(0, 0, 3, 0, Math.PI * 2);
			ctx.stroke();
			ctx.beginPath();
			ctx.arc(w, 0, 3, 0, Math.PI * 2);
			ctx.stroke();
			ctx.beginPath();
			ctx.arc(w, h, 3, 0, Math.PI * 2);
			ctx.stroke();
			ctx.beginPath();
			ctx.arc(0, h, 3, 0, Math.PI * 2);
			ctx.stroke();

			ctx.rect(0, 0, w, h);
            ctx.stroke();
            ctx.restore();
        }
        
        draw(ctx:CanvasRenderingContext2D,c:IDrawItemContainer)
		{
            switch(this.posST)
            {
                case Member.PST_OUT:
                    this.drawOut(ctx,c) ;

                    //draw line
                    var mpt = this.getDrawXY();
                    var ppt = this.getBelongTo()?.getBoundRectDraw()?.getCenter() ;
                    if(mpt!=null&&ppt!=null&&this.getPosState()==Member.PST_OUT)
                    {
                        ctx.save();
                        mpt = c.transDrawPt2PixelPt(mpt.x, mpt.y);
                        ppt = c.transDrawPt2PixelPt(ppt.x, ppt.y);
                        ctx.translate(0, 0);
                        ctx.strokeStyle = "grey";
                        ctx.beginPath();
                        ctx.setLineDash([3,3]);
                        ctx.moveTo(mpt.x,mpt.y) ;
                        ctx.lineTo(ppt.x,ppt.y) ;
                        ctx.stroke() ;
                        ctx.restore();
                    }

                    break ;
                case Member.PST_MOVING:
                default:
                    if(this.getBelongTo()?.getShowMemberRadius()!=null)
                        this.drawIn(ctx,c) ;
            }
        }

		draw_hidden(cxt:CanvasRenderingContext2D,c:IDrawItemContainer)
		{

		}
    }


    export class MemberTagList extends Member //implements IActionNode
    {
        public static TP = "tag_list" ;

        private divList:DIDivList ;

        private out_w:number = 500 ;
        private out_h:number = 300 ;

        public constructor(opts: {} | undefined)
		{
            super(opts);
            
            this.divList = new DIDivList({}) ;
            
            this.divList.setMin(false);
		}

        public getClassName()
        {
            return "oc.iott.MemberTagList";
        }


        static Member_TL_PNS = {
			_cat_name: "member_tl", _cat_title: "Member Tag List",
			out_w: { title: "out width", type: "int"},
            out_h: { title: "out height", type: "int"},
		};

		public getPropDefs(): Array<any>
		{
			var r = super.getPropDefs();
			r.push(MemberTagList.Member_TL_PNS);
			return r;
        }

        public getActionTypeName():string
		{//override to provider item to has action like popmenu ,drap panel etc
			return "tag_list" ;
		}
        
        public getBorderColor():string
        {
            return "pink" ;
        }

        public getMemberTitle(): string
        {
            return "Tag List"
        }
        public getMemberIcon(): string
        {
            return "/_iottree/res/icon_tag_list.png" ;
        }

        protected getDrawOutSize():oc.base.Size
        {
            return {w:this.out_w,h:this.out_h};
        }

        public getDivList():DIDivList
        {
            if(this.divList.getLayer()==null)
            {
                var c = this.getContainer();
                if(c!=null)
                    this.divList.setContainer(c,this.getLayer()) ;
            }
            return this.divList;
        }

        public on_mouse_event(tp: MOUSE_EVT_TP, pxy: oc.base.Pt, dxy: oc.base.Pt,e:_MouseEvent)
        {
            super.on_mouse_event(tp,pxy,dxy,e);

            if(tp==MOUSE_EVT_TP.Down)
            {
                if(e.button==MOUSE_BTN.RIGHT)
                {
                    if(oc.PopMenu.createShowPopMenu(this,pxy,dxy))
                        e.preventDefault();
                }
            }
        }

        public on_after_inject(pvs:base.Props<any>)
        {
            super.on_after_inject(pvs) ;
            this.divList.setDrawSize(this.out_w,this.out_h-this.headLen) ;
        }

        public setPosState(st:number)
        {
            super.setPosState(st) ;
            switch(this.posST)
            {
                case Member.PST_OUT:
                    this.getLayer()?.addItem(this.divList) ;
                    this.divList.setVisiable(true) ;
                    break ;
                case Member.PST_MOVING:
                default:
                    this.getLayer()?.removeItem(this.divList) ;
                    this.divList.setVisiable(false) ;
            }
        }

        protected drawIn(ctx:CanvasRenderingContext2D,c:IDrawItemContainer)
		{
            this.divList.setVisiable(false);
            super.drawIn(ctx,c);
        }

        public drawOut(ctx: CanvasRenderingContext2D, c: IDrawItemContainer): void
        {
            super.drawOut(ctx,c) ;

            this.divList.setVisiable(true);
            this.divList.x = this.x;
            this.divList.y = this.y + this.headLen ;
            
            //his.divList.set
            this.divList.draw(ctx,c) ;
        }
    }

    export class MemberConn extends Member
    {
        public static TP = "ua_conn" ;

        private out_w:number = 300 ;
        private out_h:number = 200 ;

        

        public getClassName()
        {
            return "oc.iott.MemberConn";
        }

        public getBorderColor():string
        {
            return "green" ;
        }

        protected getDrawOutSize():oc.base.Size
        {
            return {w:this.out_w,h:this.out_h};
        }

        public getMemberTitle(): string
        {
            var t = this.getTitle() ;
            if(t==null||t=='')
                t = this.getName() ;
            return t ;
        }
        public getMemberIcon(): string
        {
            return "/_iottree/res/icon_conn.png" ;
        }

        //drawIn(ctx)
        public drawOut(ctx: CanvasRenderingContext2D, c: IDrawItemContainer): void
        {
            super.drawOut(ctx,c);//this.drawIn(ctx,c) ;
            var p1 = c.transDrawPt2PixelPt(this.x, this.y);
            
			ctx.save();
			
            ctx.translate(p1.x, p1.y);

            ctx.font = `20px serif`;
			ctx.fillStyle = "yellow";
            var t = this.getTitle() ;
            if(t==null)
                t = "xxxx" ;
            ctx.fillText(t, 10, 20);
            ctx.restore() ;
        }
    }
}