/**
 * conntion between unit
  */
module oc.iott
{
    export enum CONN_POS { W,WN,N,NE,E,ES,S,SW};

    export class Conn extends DrawItem
    {
        color: string = 'yellow';
        lnW: number = 1;
        bEndArrow: boolean = false;
        fromId: string = "";
        formPos:CONN_POS=CONN_POS.E;
        toId: string = "";
        toPos:CONN_POS=CONN_POS.W;

        public constructor(opts: {nodeid_from:string,nodeid_to:string} | undefined)
        {
            super(opts);
            //oc.DrawItem.apply(this,arguments);
            if(opts!=undefined)
            {
                var ufid = opts.nodeid_from
                var utid = opts.nodeid_to;
                this.fromId = ufid?ufid:"" ;
                this.toId = utid?utid:"";
            }
            
        }



        public getClassName()
        {
            return "oc.iott.Conn";
        }

        public isVirtual():boolean
        {
            return true;
        }

        public getNodeFromId():string
        {
            return this.fromId ;
        }

        public setFromPos(p:CONN_POS)
        {
            this.formPos = p ;
        }

        public getNodeToId():string
        {
            return this.toId;
        }

        public setToPos(p:CONN_POS)
        {
            this.toPos = p ;
        }

        public getDIFrom():DrawItemRect|null
        {
            var lay = this.getLayer();
            if(lay==null)
                return null ;
            return lay.getItemById(this.fromId) as DrawItemRect;
        }

        public getDITo():DrawItemRect|null
        {
            var lay = this.getLayer();
            if(lay==null)
                return null ;
            return lay.getItemById(this.toId) as DrawItemRect;
        }

        private calConnCtrlPt(pxy:oc.base.Pt,ps:oc.base.Size,pos:CONN_POS,ctrllen:number):oc.base.Pt[]
        {
            switch(pos)
            {
                case CONN_POS.W:
                    var p = {x:pxy.x,y:pxy.y+ps.h/2};
                    return [p,{x:p.x-ctrllen,y:p.y}];
                case CONN_POS.WN:
                    var p = pxy;
                    return [p,{x:p.x-ctrllen,y:p.y}];
                case CONN_POS.SW:
                    var p = {x:pxy.x,y:pxy.y+ps.h};
                    return [p,{x:p.x-ctrllen,y:p.y}];
                case CONN_POS.NE:
                    var p = {x:pxy.x+ps.w,y:pxy.y};
                    return [p,{x:p.x+ctrllen,y:p.y}];
                case CONN_POS.ES:
                    var p = {x:pxy.x+ps.w,y:pxy.y+ps.h};
                    return [p,{x:p.x+ctrllen,y:p.y}];
                case CONN_POS.N:
                    var p = {x:pxy.x+ps.w/2,y:pxy.y};
                    return [p,{x:p.x,y:p.y-ctrllen}];
                case CONN_POS.S:
                    var p = {x:pxy.x+ps.w/2,y:pxy.y+ps.h};
                    return [p,{x:p.x,y:p.y+ctrllen}];
                case CONN_POS.E:
                default:
                    var p = {x:pxy.x+ps.w,y:pxy.y+ps.h/2};
                    return [p,{x:p.x+ctrllen,y:p.y}];
            }
        }

        public draw(ctx: CanvasRenderingContext2D, c: IDrawItemContainer)
        {
            var uf = this.getDIFrom();
            var ut = this.getDITo();
            if(uf==null||ut==null)
                return ;
            if(uf.isHidden()||ut.isHidden())
                return ;

            var pf = uf.getPixelXY();
            var ps = uf.getPixelSize() ;
            
            var tf = ut.getPixelXY() ;
            var ts = ut.getPixelSize() ;
            if(pf==null||ps==null||tf==null||ts==null)
                return ;
            
            var ctrllen = c.transDrawLen2PixelLen(true,80) ;
            var f_pc = this.calConnCtrlPt(pf,ps,this.formPos,ctrllen);
            var t_pc = this.calConnCtrlPt(tf,ts,this.toPos,ctrllen);
            var p1 = f_pc[0];
            var p2 = t_pc[0];
            var cp1 = f_pc[1];
            var cp2 = t_pc[1];
            
            ctx.save();
            //ctx.translate(0, 0);
            ctx.lineWidth = this.lnW;
            ctx.strokeStyle = this.color;

            ctx.beginPath();
            ctx.moveTo(p1.x, p1.y);
            ctx.bezierCurveTo(cp1.x, cp1.y, cp2.x, cp2.y, p2.x, p2.y);
            ctx.stroke();


            if (this.bEndArrow)
            {
                ctx.fillStyle = this.color;
                var arrlen = c.transDrawLen2PixelLen(true, 20);
                var arrh = c.transDrawLen2PixelLen(true, 8);
                util.drawArrow(ctx, cp2.x, cp2.y, p2.x, p2.y, arrlen, arrh);
            }

            ctx.restore();
        }

    }
}