
module oc.di
{
    /**
     * multi pt (>=3) to create geom in rect
     */
    export class DIBasic extends oc.DrawItemRectR
    {
        static tp2pts = {
            "rect": { ln: [[0, 0], [100, 0], [100, 100], [0, 100]] },
            "diamond": { ln: [[50, 0], [100, 50], [50, 100], [0, 50]] },
            "parallelogram": { ln: [[0, 0], [70, 0], [100, 100], [30, 100]] },
            "pentagon": { ln: [[95.10565, 0], [190.21130, 64.20395], [153.884, 190.2113], [36.32713, 190.2113], [0, 64.20395]] },
            "circle": { arc: { x: 50, y: 50, r: 50, sa: 0, ea: Math.PI * 2 } },
            "circle+": { arc: { x: 50, y: 50, r: 50, sa: 0, ea: Math.PI * 2 }, lns: [[[25, 50], [50, 50], [75, 50]], [[50, 25], [50, 50], [50, 75]]] },
            "circle-": { arc: { x: 50, y: 50, r: 50, sa: 0, ea: Math.PI * 2 }, lns: [[[25, 50], [50, 50], [75, 50]]] },
            "iso_triangle": { ln: [[50, 0], [100, 100], [0, 100]] },
            "eclipse": { eclipse: [[0, 0], [100, 0], [100, 100], [0, 100]] },

        };


        //static TP2PYS = {};

        static TP2RECT = {};

        // private static getPyByTp(tp:string):oc.base.Polygon|null
        // {
        //     var py = DIPts.TP2PYS[tp];
        //     if(py==undefined||py==null)
        //     {
        //         var pts = DIPts.tp2pts[tp];
        //         if(pts==undefined||pts==null)
        //             return null;
        //         py = oc.base.createPolygonByPt2(pts);
        //         DIPts.TP2PYS[tp] = py ;
        //     }
        //     return py;
        // }

        private static getRectByTp(tp: string): oc.base.Rect | null
        {
            var r = DIBasic.TP2RECT[tp];
            if (r != undefined && r != null)
                return r;

            var obj = DIBasic.tp2pts[tp];
            if (obj == undefined || obj == null)
                return null;
            let tmpr = new oc.base.Rect(0, 0, 0, 0);
            for (var t in obj)
            {
                var v = obj[t];
                switch (t)
                {
                    case "ln":
                    case "eclipse":
                        var r0 = oc.base.createPolygonByPt2(v).getBoundingBox();
                        if (r0 != null)
                            tmpr.expandBy(r0);
                        break;
                    case "lns":
                        for (var v0 of v)
                        {
                            var r0 = oc.base.createPolygonByPt2(v0).getBoundingBox();
                            if (r0 != null)
                                tmpr.expandBy(r0);
                        }
                        break;
                    case "arc":
                        tmpr.expandBy(new oc.base.Rect(v.x - v.r, v.y - v.r, v.r * 2, v.r * 2));
                        break;
                    case "arcs":
                        for (var v0 of v)
                        {
                            tmpr.expandBy(new oc.base.Rect(v0.x - v0.r, v0.y - v0.r, v0.r * 2, v0.r * 2));
                        }
                        break;

                        break;
                }
            }

            DIBasic.TP2RECT[tp] = tmpr;
            return tmpr;
        }


        color: string = 'yellow';
        //polygon=new oc.base.Polygon();
        lnW: number = 1;
        fillColor: string | null = null;
        tp: string = "rect";

        private ptsPy: oc.base.Polygon | null = null;

        public constructor(opts: {} | undefined)
        {
            super(opts);
            if (opts != undefined)
            {
                // var pts_str = opts["pts"];
                // if(pts_str!=undefined&&pts_str!=null&&pts_str!="")
                // {
                //     this.tp="";
                //     var pts:oc.base.Pt2[]|null=null;//[[],[]]
                //     eval("pts="+pts_str) ;
                //     if(pts!=null)
                //         this.ptsPy = oc.base.createPolygonByPt2(pts);
                // }
                // else

                var tp = opts["tp"];
                if (tp != undefined && tp != null && tp != "")
                    this.tp = tp;

            }
        }


        static PNS = {
            _cat_name: "pts", _cat_title: "Pts",
            color: { title: "color", type: "str",edit_plug:"color" },
            lnW: { title: "Line Width", type: "int" },
            fillColor: { title: "Fill Color", type: "str",edit_plug:"color" },
            tp: {
                title: "Figure Type", type: "string", enum_val: [["rect", "Rect"], ["diamond", "Diamond"],
                ["parallelogram", "parallelogram"], ["pentagon", "pentagon"], ["circle", "circle"],
                ["circle+", "circle+"], ["circle-", "circle-"], ["iso_triangle", "Triangle"], ["eclipse", "Eclipse"]]
            },

        };


        public getClassName()
        {
            return "DIBasic";
        }

        // public getPtsPy():oc.base.Polygon|null
        // {
        //     if(this.pts_tp!="")
        //         return DIPts.getPyByTp(this.pts_tp);
        //     else
        //         return this.ptsPy;
        // }

        public setTp(tp: string)
        {
            this.tp = tp;
        }

        public setLnW(w: number)
        {
            this.lnW = w;
        }

        public setLnColor(c: string)
        {
            this.color = c;
        }

        public setFillColor(c: string)
        {
            this.fillColor = c;
        }

        public getPropDefs()
        {
            var r = super.getPropDefs();
            r.push(DIBasic.PNS)
            return r;
        }

        public getBoundPolygonDraw()
        {

            //return new oc.base.Polygon();
            return null;
        }


        public getPrimRect(): oc.base.Rect | null
        {
            return DIBasic.getRectByTp(this.tp)
        }

        private drawEclipse(cxt: CanvasRenderingContext2D, ln: oc.base.Pt2[],
            xratio: number, yratio: number)
        {
            cxt.beginPath();
            cxt.strokeStyle = this.color;
            cxt.lineWidth = this.lnW;

            var width = 50 * xratio;
            var height = 50 * yratio;
            //Width offset
            var offset_w = width * 2 * 0.2761423749154;
            //Height offset
            var offset_h = height * 2 * 0.2761423749154;

            var centerp: oc.base.Pt = { x: 50 * xratio, y: 50 * yratio };

            var tl_p1 = { x: centerp.x - width, y: centerp.y };
            var tl_c1 = { x: centerp.x - width, y: centerp.y - offset_h };
            var tl_c2 = { x: centerp.x - offset_w, y: centerp.y - height };
            var tl_p2 = { x: centerp.x, y: centerp.y - height };

            var tr_p1 = { x: centerp.x, y: centerp.y - height };
            var tr_c1 = { x: centerp.x + offset_w, y: centerp.y - height };
            var tr_c2 = { x: centerp.x + width, y: centerp.y - offset_h };
            var tr_p2 = { x: centerp.x + width, y: centerp.y };

            //Bottom right
            var br_p1 = { x: centerp.x + width, y: centerp.y };
            var br_c1 = { x: centerp.x + width, y: centerp.y + offset_h };
            var br_c2 = { x: centerp.x + offset_w, y: centerp.y + height };
            var br_p2 = { x: centerp.x, y: centerp.y + height };

            //Bottom left
            var bl_p1 = { x: centerp.x, y: centerp.y + height };
            var bl_c1 = { x: centerp.x - offset_w, y: centerp.y + height };
            var bl_c2 = { x: centerp.x - width, y: centerp.y + offset_h };
            var bl_p2 = { x: centerp.x - width, y: centerp.y };

            cxt.moveTo(tl_p1.x, tl_p1.y);
            cxt.bezierCurveTo(tl_c1.x, tl_c1.y, tl_c2.x, tl_c2.y, tl_p2.x, tl_p2.y);
            cxt.bezierCurveTo(tr_c1.x, tr_c1.y, tr_c2.x, tr_c2.y, tr_p2.x, tr_p2.y);
            cxt.bezierCurveTo(br_p1.x, br_c1.y, br_c2.x, br_c2.y, br_p2.x, br_p2.y);
            cxt.bezierCurveTo(bl_c1.x, bl_c1.y, bl_c2.x, bl_c2.y, bl_p2.x, bl_p2.y);

            //first fill
            if (this.fillColor && this.fillColor != "")
            {
                cxt.fillStyle = this.fillColor;
                cxt.fill();
            }

                var p1 = {x:0,y:0}
                var p2 = {x:100* xratio,y:100* yratio}
                var linear = cxt.createLinearGradient(p1.x,p1.y,p2.x,p2.y);
                linear.addColorStop(0,'#fff');
                linear.addColorStop(0.5,'#f0f');
                linear.addColorStop(1,'#333');
                cxt.fillStyle = linear;
                cxt.closePath();
                cxt.fill();

            //then stroke
            cxt.stroke();
        }

        private drawLn(ctx: CanvasRenderingContext2D, ln: oc.base.Pt2[],
            xratio: number, yratio: number)
        {
            ctx.beginPath();
            ctx.strokeStyle = this.color;
            ctx.lineWidth = this.lnW;
            for (var tmppt of ln)
            {
                ctx.lineTo(tmppt[0] * xratio, tmppt[1] * yratio);
            }
            ctx.lineTo(ln[0][0] * xratio, ln[0][1] * yratio);

            ctx.stroke();
            if (this.fillColor && this.fillColor != "")
            {
                ctx.fillStyle = this.fillColor;
                ctx.closePath();
                ctx.fill();
            }
        }

        public drawArc(ctx: CanvasRenderingContext2D, arc: { x: number, y: number, r: number, sa: number, ea: number }
            , xratio: number, yratio: number)
        {
            ctx.beginPath();
            ctx.strokeStyle = this.color;
            ctx.lineWidth = this.lnW;
            ctx.arc(arc.x * xratio, arc.y * yratio, arc.r * xratio, arc.sa, arc.ea);

            ctx.stroke();
            if (this.fillColor && this.fillColor != "")
            {
                ctx.fillStyle = this.fillColor;
                ctx.closePath();
                ctx.fill();
            }
        }

        public drawPrimScale(ctx: CanvasRenderingContext2D, xratio: number, yratio: number): boolean
        {
            var obj = DIBasic.tp2pts[this.tp];
            if (obj == undefined || obj == null)
                return true;

            ctx.strokeStyle = this.color;
            for (var t in obj)
            {
                var v = obj[t];
                switch (t)
                {
                    case "ln":
                        this.drawLn(ctx, v, xratio, yratio);
                        break;
                    case "eclipse":
                        this.drawEclipse(ctx, v, xratio, yratio);
                        break;
                    case "lns":
                        for (var ln of v)
                        {
                            this.drawLn(ctx, ln, xratio, yratio);
                        }
                        break;
                    case "arc":
                        this.drawArc(ctx, v, xratio, yratio);
                        break;
                    case "arcs":
                        for (var ln of v)
                        {
                            this.drawArc(ctx, ln, xratio, yratio);
                        }
                        break;
                }
            }
            return true;//will make draw prim not to be called
        }

        public drawPrim(ctx: CanvasRenderingContext2D): void
        {
            var obj = DIBasic.tp2pts[this.tp];
            if (obj == undefined || obj == null)
                return;


            ctx.strokeStyle = this.color;
            for (var t in obj)
            {
                var v = obj[t];
                switch (t)
                {
                    case "ln":
                        this.drawLn(ctx, v, 1, 1);
                        break;
                    case "lns":
                        for (var ln of v)
                        {
                            this.drawLn(ctx, ln, 1, 1);
                        }
                        break;
                    case "arc":
                        this.drawArc(ctx, v, 1, 1);
                        break;
                    case "arcs":
                        for (var ln of v)
                        {
                            this.drawArc(ctx, ln, 1, 1);
                        }
                        break;
                }
            }
        }

        public drawPrimSel(ctx: CanvasRenderingContext2D): void
        {
            // var py = this.getPtsPy();
            // if(py==null)
            //     return ;
            // ctx.lineWidth = 1;
            // ctx.strokeStyle = "red";
            // for(var tmppt of py.getPts())
            // {
            //     ctx.beginPath();
            //     ctx.arc(tmppt.x,tmppt.y,3,0,Math.PI*2);
            //     ctx.stroke();
            // }
        }
    }
}


