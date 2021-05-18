"use strict";
var oc;
(function (oc) {
    class PropBinder {
        constructor() {
            //bServerJS:boolean = false;
            this.propName = "";
            this.bExp = false;
            this.binderTxt = "";
            // public isServerJS()
            // {
            //     return this.bServerJS ;
            // }
            this.mouseF = null;
        }
        getPropName() {
            return this.propName;
        }
        isExp() {
            return this.bExp;
        }
        setExp(b) {
            this.bExp = b;
        }
        getBinderTxt() {
            return this.binderTxt;
        }
        setBinderTxt(js) {
            this.binderTxt = js;
        }
        isValid() {
            return this.binderTxt != null;
        }
        toPropStr() {
            return { n: this.propName, txt: this.binderTxt, exp: "" + this.bExp };
        }
        fromPropStr(p) {
            var n = p["n"];
            var txt = p["txt"];
            if (n == undefined || n == null || txt == undefined || txt == null)
                return false;
            this.propName = n;
            this.binderTxt = txt;
            this.bExp = "true" == p["exp"];
            return true;
        }
    }
    PropBinder.PROP_BINDER = "_prop_binder";
    oc.PropBinder = PropBinder;
})(oc || (oc = {}));
/**
 * @module ol/DrawItem
 */
var oc;
(function (oc) {
    class ItemsContainer {
        constructor(itemr, pc, items) {
            this.itemsRect = null;
            this.itemRect = itemr;
            this.parentC = pc;
            this.itemsLister = items;
        }
        static calcRect(items) {
            if (items.length <= 0)
                return null;
            var r = null;
            for (var i = 0; i < items.length; i++) {
                var tmpr = items[i].getBoundRectDraw();
                if (tmpr == null) {
                    var py = items[i].getBoundPolygonDraw();
                    if (py != null)
                        tmpr = py.getBoundingBox();
                }
                if (tmpr == null)
                    continue;
                if (r == null)
                    r = oc.base.Rect.copy(tmpr);
                else
                    r.expandBy(tmpr);
            }
            return r;
        }
        static calcRectExt(items, ext_items) {
            if (items.length <= 0)
                return null;
            var r = null;
            for (var item of items) {
                var tmpr = item.getBoundRectDraw();
                if (tmpr == null)
                    continue;
                if (r == null)
                    r = oc.base.Rect.copy(tmpr);
                else
                    r.expandBy(tmpr);
            }
            for (var item of ext_items) {
                var tmpr = item.getBoundRectDraw();
                if (tmpr == null)
                    continue;
                if (r == null)
                    r = oc.base.Rect.copy(tmpr);
                else
                    r.expandBy(tmpr);
            }
            return r;
        }
        getItemsRectInner() {
            if (this.itemsRect != null)
                return this.itemsRect;
            var items = this.itemsLister.getItemsShow();
            this.itemsRect = ItemsContainer.calcRect(items);
            return this.itemsRect;
        }
        // private getItemsRectOutter():base.Rect|null
        // {
        // 	var r = this.getItemsRectInner();
        // 	if(r==null)
        // 		return null ;
        // 	return new base.Rect(this.x,this.y,this.x+r.w,this.y+r.h) ;
        // }
        calXYResolution() {
            //if(this.xy_res!=null)
            //	return this.xy_res ;
            var itemr = this.getItemsRectInner();
            if (itemr == null) {
                //this.xy_res={x_res:1,y_res:1};
                return { x_res: 1, y_res: 1 };
            }
            var contr = this.itemRect.getBoundRectDraw();
            if (contr == null)
                return { x_res: 1, y_res: 1 };
            return { x_res: itemr.w / contr.w, y_res: itemr.h / contr.h };
            //return this.xy_res ;
        }
        getXYResolution() {
            var locr = this.calXYResolution();
            // var c = this.getContainer();
            // if(c==null)
            // 	return locr ;
            var cres = this.parentC.getXYResolution();
            return { x_res: locr.x_res * cres.x_res, y_res: locr.y_res * cres.y_res };
        }
        getPixelCenter() {
            // var c = this.getContainer();
            // if(c==null)
            // 	return {x:0,y:0} ;
            return this.parentC.transDrawPt2PixelPt(this.itemRect.x, this.itemRect.y);
        }
        getDrawCenter() {
            // var c = this.getContainer();
            // if(c==null)
            // 	return {x:0,y:0} ;//error
            var itemr = this.getItemsRectInner();
            if (itemr == null)
                return { x: 0, y: 0 }; //error
            return { x: itemr.x, y: itemr.y };
        }
        transPixelPt2DrawPt(px, py) {
            var drawc = this.getDrawCenter();
            var pc = this.getPixelCenter();
            var xyr = this.getXYResolution();
            var dx = (px - pc.x) * xyr.x_res + drawc.x;
            var dy = drawc.y - (pc.y - py) * xyr.y_res;
            //var dy = 0 - (pc.y - py) * xyr.y_res;
            return { x: dx, y: dy };
        }
        transDrawPt2PixelPt(dx, dy) {
            var drawc = this.getDrawCenter();
            var pc = this.getPixelCenter();
            var xyr = this.getXYResolution();
            var px = (dx - drawc.x) / xyr.x_res + pc.x;
            //var px = (dx - this.drawCenter.x) / this.drawResolution + pc.x;
            //var py = pc.y + dy / xyr.y_res;
            var py = pc.y - (drawc.y - dy) / xyr.y_res;
            px = Math.round(px);
            py = Math.round(py);
            return { x: px, y: py };
        }
        transDrawLen2PixelLen(b_xres, len) {
            var xyr = this.getXYResolution();
            if (b_xres)
                return Math.round(len / xyr.x_res);
            else
                return Math.round(len / xyr.y_res);
        }
        transPixelLen2DrawLen(b_xres, len) {
            var xyr = this.getXYResolution();
            if (b_xres)
                return len * xyr.x_res;
            else
                return len * xyr.y_res;
        }
        getItemsShow() {
            return this.itemsLister.getItemsShow();
        }
        removeItem(item) {
            return false; //not support
        }
        notifyItemsChg() {
            this.itemsRect = null;
        }
    }
    oc.ItemsContainer = ItemsContainer;
    class DrawItem {
        constructor(opts) {
            this.drawLayer = null;
            //private drawRes:IDrawRes|null = null ;
            this.container = null; //
            this.id = "";
            this.name = "";
            this.title = "";
            this.x = 0; //opts?opts.x:0 ; related or abstract
            this.y = 0; //opts?opts.y:0 ;
            this.zindex = 0;
            //b_vis: boolean = true;
            this.bMouseIn = false;
            this.parentNode = null;
            /**
             * belong to group name
             * if groupName can be found instance,then group will limit
             * this item in it's area.
             */
            this.groupName = null;
            this.groupDI = null;
            /**
             * set obj mark,which can be used to filter
             */
            this.mark = null;
            this.bvisiable = true;
            /**
             * for drawitem some event interaction
             * make drawitem can response to some human event
             * like ,mouse click etc.
             */
            this.eventBD = {};
            this.propBD = {};
            // if(arguments.length>=1)
            // {
            // 	this.inject(arguments[0]);
            // }
            if (opts != undefined) {
                this.inject(opts, false);
            }
        }
        getParentNode() {
            return this.parentNode;
        }
        /**
         * get node related DrawRes
         *
         */
        getDrawRes() {
            if (this["getDrawResUrl"]) {
                return oc.base.forceCast(this);
            }
            var pn = this.getParentNode();
            if (pn != null) {
                var dr = pn.getDrawRes();
                if (dr != null)
                    return dr;
            }
            var ly = this.getLayer();
            if (ly != null) {
                var dr = ly.getDrawRes();
                if (dr != null)
                    return dr;
            }
            // var p = this.getPanel() ;
            // if(p!=null)
            // {
            // 	var dr = p.getDrawRes();
            // 	if(dr!=null)
            // 		return dr ;
            // }
            return null; //this.drawRes;
        }
        getClassName() {
            return "DrawItem";
        }
        setMark(m) {
            this.mark = m;
        }
        getMark() {
            return this.mark;
        }
        static createByClassName(cn, opts) {
            var r = null;
            if (cn == "DrawItems" || cn == "DrawUnitIns" || cn == "DrawUnit" || cn == "DrawItemGroup")
                eval("r=new oc." + cn + "(opts)");
            else if (cn.indexOf("oc.") != 0)
                eval("r=new oc.di." + cn + "(opts)");
            else
                eval("r=new " + cn + "(opts)");
            return r;
        }
        static createByFullClassName(cn, opts, bnew) {
            var r = this.createByClassName(cn, opts);
            if (r == null)
                return null;
            if (bnew)
                r.id = oc.util.create_new_tmp_id();
            return r;
        }
        getPropDefs() {
            var r = [];
            r.push(DrawItem.DrawItem_PNS);
            return r;
        }
        isVisiable() {
            return this.bvisiable;
        }
        setVisiable(v) {
            this.bvisiable = v;
        }
        /**
         * get prop item def in all pdf
         * @param propn
         */
        findProDefItemByName(propn) {
            for (var pdf of this.getPropDefs()) {
                var f = pdf[propn];
                if (f != null && f != undefined)
                    return f;
            }
            return null;
        }
        getEventDefs() {
            var r = [];
            if (DrawItem.DrawItem_ENS == null) {
                var di_ens = { _cat_name: "basic", _cat_title: "Basic" };
                for (var i = 0; i < oc.MOUSE_EVT_TP_NUM; i++) {
                    var n = oc.getMouseEventNameByTp(i);
                    di_ens[n] = { title: "on_" + n, evt_tp: "mouse" };
                }
                di_ens["tick"] = { title: "on_tick", evt_tp: "tick" };
                DrawItem.DrawItem_ENS = di_ens;
            }
            r.push(DrawItem.DrawItem_ENS);
            return r;
        }
        getPropDefItem(n) {
            for (var pdef of this.getPropDefs()) {
                var r = pdef[n];
                if (r !== null && r != undefined)
                    return r;
            }
            return null;
        }
        /**
         * get prop def names which can be used to bind dyn data
         * sub class may override it ,to change bind prop names
         *   e.g when x y prop is calculated by other ,then it must no to be bind
         */
        getPropDefBinderNames() {
            return ["x", "y"];
        }
        setBinderItem(pn, event, js) {
        }
        /**
         * return change propnames
         * @param dyn
         * @param bfirechg
         */
        setDynData(dyn, bfirechg = true) {
            var ns = [];
            for (var n in dyn) {
                ns.push(n);
                this[n] = dyn[n];
            }
            if (ns.length > 0 && bfirechg)
                this.MODEL_fireChged(ns);
            return ns;
        }
        getGroupName() {
            return this.groupName;
        }
        setGroupName(n) {
            this.groupName = n;
        }
        getGroup() {
            if (this.groupName == null || this.groupName == "") {
                this.groupDI = null;
                return null;
            }
            if (this.groupDI != null)
                return this.groupDI;
            var lay = this.getLayer();
            if (lay == null)
                return null;
            this.groupDI = lay.getItemByName(this.groupName);
            return this.groupDI;
        }
        static transStr2Val(tp, strv, defaultv) {
            var r;
            if (tp == "number" || tp == "float" || tp == "double")
                r = parseFloat(strv);
            else if (tp == "int" || tp == "short" || tp == "long")
                r = parseInt(strv);
            else if (tp == "bool" || tp == "boolean")
                return "true" == strv || "1" == strv;
            else
                r = strv;
            if (r == null || r == NaN || r == undefined)
                r = defaultv;
            return r;
        }
        inject(opts, ignore_readonly) {
            if (opts == null)
                opts = {};
            var pdefs = this.getPropDefs();
            var chgpns = [];
            for (var i = 0; i < pdefs.length; i++) {
                var pdef = pdefs[i];
                for (var n in pdef) {
                    if (n.indexOf("_") == 0)
                        continue;
                    var v = opts[n];
                    if (v == undefined || v == null)
                        continue;
                    var def = pdef[n];
                    if (def["readonly"] && ignore_readonly)
                        continue;
                    v = DrawItem.transStr2Val(def["type"], v, null);
                    if (v == null)
                        continue;
                    this[n] = v;
                    chgpns.push(n);
                }
            }
            var tmpob = opts[oc.EventBinder.EVENT_BINDER];
            if (tmpob) {
                for (var tmpn in tmpob) {
                    var eb = new oc.EventBinder();
                    if (eb.fromPropStr(tmpob[tmpn]))
                        this.eventBD[tmpn] = eb;
                }
            }
            var tmpob = opts[oc.PropBinder.PROP_BINDER];
            if (tmpob) {
                for (var tmpn in tmpob) {
                    var pb = new oc.PropBinder();
                    if (pb.fromPropStr(tmpob[tmpn]))
                        this.propBD[tmpn] = pb;
                }
            }
            if (this.id == null || this.id == '' || this.id == undefined || this.id == 'undefined') {
                this.id = oc.util.create_new_tmp_id();
                chgpns.push("id");
            }
            this.on_after_inject(opts);
            if (chgpns.length > 0)
                this.MODEL_fireChged(chgpns);
        }
        extract() {
            let r = {};
            //let r={};
            r["_cn"] = this.getClassName();
            var pdefs = this.getPropDefs();
            for (var i = 0; i < pdefs.length; i++) {
                var pdef = pdefs[i];
                for (var n in pdef) {
                    if (n.indexOf("_") == 0)
                        continue;
                    var v = this[n];
                    if (v == undefined || v == null)
                        continue;
                    r[n] = v;
                }
            }
            //var eventbd = this.eventBD ;
            var tmpob = {};
            for (var tmpn in this.eventBD) {
                var eb = this.eventBD[tmpn];
                tmpob[tmpn] = eb.toPropStr();
            }
            r[oc.EventBinder.EVENT_BINDER] = tmpob;
            tmpob = {};
            for (var tmpn in this.propBD) {
                var pb = this.propBD[tmpn];
                tmpob[tmpn] = pb.toPropStr();
            }
            r[oc.PropBinder.PROP_BINDER] = tmpob;
            return r;
        }
        duplicateMe() {
            var ps = this.extract();
            var r = DrawItem.createByClassName(this.getClassName(), undefined);
            if (r == null)
                throw Error("duplicate instance null");
            r.inject(ps, false);
            return r;
        }
        setPropValue(pnv) {
            var pns = [];
            for (var n in pnv) {
                var v = pnv[n];
                var defitem = this.getPropDefItem(n);
                if (defitem == null)
                    continue;
                //var def = defitem[n];
                v = DrawItem.transStr2Val(defitem["type"], v, null);
                if (v == null)
                    continue;
                this[n] = v;
                pns.push(n);
            }
            if (pns.length > 0)
                this.MODEL_fireChged(pns);
        }
        setContainer(cont, lay) {
            this.container = cont;
            this.drawLayer = lay;
            //parent first
            this.on_container_set();
            if (this instanceof oc.DrawItems) {
                var c = this.getInnerContainer();
                if (c != null) {
                    var items = this.getItemsShow();
                    for (var tmpi of items) {
                        tmpi.setContainer(c, lay);
                    }
                }
            }
        }
        //call after drawitem to be set container
        on_container_set() { }
        getBoundRectDraw() {
            return null; //new oc.base.Rect(this.x,this.y,this.w,)
        }
        redraw() {
            var lay = this.getLayer();
            if (lay == null)
                return;
            var cxt = lay.getCxtCurDraw();
            if (cxt == null)
                return;
            var cont = this.getContainer();
            if (cont == null)
                return;
            this.draw(cxt, cont);
        }
        getBoundRectPixel() {
            var dbr = this.getBoundRectDraw();
            if (dbr == null)
                return null;
            var c = this.getContainer();
            if (!c)
                return null;
            var dxy = c.transDrawPt2PixelPt(dbr.x, dbr.y);
            var dw = c.transDrawLen2PixelLen(true, dbr.w);
            var dh = c.transDrawLen2PixelLen(false, dbr.h);
            return new oc.base.Rect(dxy.x, dxy.y, dw, dh);
        }
        getBoundPolygonDraw() {
            return null;
        }
        getBoundPolygonPixel() {
            var dbr = this.getBoundPolygonDraw();
            if (dbr == null)
                return null;
            var panel = this.getPanel();
            if (!panel)
                return null;
            // var dxy = panel.transDrawPt2PixelPt(dbr.x, dbr.y);
            // var dw = panel.transDrawLen2PixelLen(dbr.w);
            // var dh = panel.transDrawLen2PixelLen(dbr.h);
            // //return new oc.base.Rect(dxy.x, dxy.y, dw, dh);
            return null; //TODO
        }
        //public 
        /**
         * check current input draw pt can make select drawitem
         * @param x
         * @param y
         */
        chkCanSelectDrawPt(x, y) {
            return this.containDrawPt(x, y);
        }
        containDrawPt(x, y) {
            var dr = this.getBoundRectDraw();
            if (dr != null)
                return dr.contains(x, y);
            var py = this.getBoundPolygonDraw();
            if (py != null)
                return py.contains(x, y);
            return false;
        }
        getEventBinder(eventn) {
            var r = this.eventBD[eventn];
            if (r == null || r == undefined)
                return null;
            return r;
        }
        setEventBinder(eventn, clientjs, serverjs) {
            var r = this.eventBD[eventn];
            if (r == null || r == undefined) {
                r = new oc.EventBinder();
                r.evtName = eventn;
                this.eventBD[eventn] = r;
            }
            r.clientJS = clientjs;
            r.serverJS = serverjs;
        }
        getPropBinder(propn) {
            var r = this.propBD[propn];
            if (r == null || r == undefined)
                return null;
            return r;
        }
        setPropBinder(propn, jstxt, bexp) {
            var r = this.propBD[propn];
            if (r == null || r == undefined) {
                r = new oc.PropBinder();
                r.propName = propn;
                this.propBD[propn] = r;
            }
            r.binderTxt = jstxt;
            r.bExp = bexp;
        }
        on_selected(bsel) {
        }
        on_mouse_event(tp, pxy, dxy, e) {
            var evtn = oc.getMouseEventNameByTp(tp);
            var eb = this.getEventBinder(evtn);
            if (eb != null)
                eb.onEventRunMouse(this, pxy, dxy, e);
        }
        /**
         * when mouse in item,and has many event tp
         * @param pxy
         * @param dxy
         */
        on_mouse_in() {
        }
        on_mouse_over(tp, pxy, dxy) {
        }
        /**
         * when mouse out
         */
        on_mouse_out() { }
        /**
         * on tick event by timer
         */
        on_tick() {
            var eb = this.getEventBinder("tick");
            if (eb != null)
                eb.onEventRunTick(this);
        }
        on_before_del() {
            return false;
        }
        on_after_inject(pvs) {
            //console.log(this.getClassName()+" "+this.getId()+"   after inj") ;
        }
        isMouseIn() {
            return this.bMouseIn;
        }
        getLayer() {
            return this.drawLayer;
        }
        getPanel() {
            if (this.drawLayer == null)
                return null;
            return this.drawLayer.getPanel();
        }
        getModel() {
            var _a;
            var dp = this.getPanel();
            if (dp == null)
                return null;
            var m = (_a = dp.getDrawView()) === null || _a === void 0 ? void 0 : _a.getModel();
            if (m == undefined)
                return null;
            return m;
        }
        getContainer() {
            return this.container;
        }
        getCxt() {
            if (this.drawLayer == null)
                return null;
            return this.drawLayer.getCxtCurDraw();
        }
        getId() {
            return this.id;
        }
        setId(v) {
            this.id = v;
            this.MODEL_fireChged(["id"]);
        }
        getName() {
            return this.name;
        }
        setName(n) {
            this.name = n;
        }
        getTitle() {
            return this.title;
        }
        setTitle(t) {
            this.title = t;
        }
        getDrawXY() {
            return { x: this.x, y: this.y };
        }
        getPixelXY() {
            var c = this.getContainer();
            if (c == null)
                return null;
            var dxy = this.getDrawXY();
            return c.transDrawPt2PixelPt(dxy.x, dxy.y);
        }
        MODEL_fireChged(prop_names) {
            if (prop_names == null)
                return;
            //empty pn must fire event
            var lay = this.getLayer();
            if (lay == null)
                return;
            lay.MODEL_fireChged(this, prop_names);
        }
        setDrawXY(x, y) {
            this.x = x;
            this.y = y;
            var g = this.getGroup();
            if (g != null) { //limit in group
                var pt = g.getDrawXY();
                var s = g.getDrawSize();
                var r = this.getBoundRectDraw();
                if (r != null) {
                    var m = r.getMaxX();
                    if (m > pt.x + s.w)
                        this.x = pt.x + s.w - r.w;
                    m = r.getMaxY();
                    if (m > pt.y + s.h)
                        this.y = pt.y + s.h - r.h;
                }
                if (this.x < pt.x)
                    this.x = pt.x;
                if (this.y < pt.y)
                    this.y = pt.y;
            }
            this.MODEL_fireChged(["x", "y"]);
        }
        /**
         * when add new item in layer,item is set by beginxy and endxy
         * return min end xy which must bigger than start xy
         * @param x
         * @param y
         */
        setDrawBeginXY(cont, x, y) {
            this.x = x;
            this.y = y;
            return true; //not end and continue;
        }
        /**
         * when add new item in layer,item is set by beginxy and endxy
         *
         * @param x
         * @param y
         */
        setDrawEndXY(cont, x, y) {
            var minx = this.x + cont.transPixelLen2DrawLen(true, 2);
            var miny = this.y + cont.transPixelLen2DrawLen(false, 2);
            x = x < minx ? minx : x;
            y = y < miny ? miny : y;
            return { x: x, y: y };
        }
        /**
         * true will make item cannot be display and select and any other oper
         */
        isHidden() {
            return false;
        }
        /**
         * true means item has no entity data,it's depends other items to create and need not to be saved
         */
        isVirtual() {
            return false;
        }
        draw(cxt, c) {
        }
        draw_hidden(cxt, c) {
        }
        getSelState() {
            var p = this.getPanel();
            if (p == null || this.isHidden())
                return { selected: false, dragover: false };
            var inta = p.getInteract();
            if (inta == null)
                return { selected: false, dragover: false };
            var bsel = false;
            var sis = inta.getSelectedItems();
            var bsel = sis != null && sis.indexOf(this) >= 0;
            sis = inta.getDragOverSelItems();
            var bdragover = sis != null && sis.indexOf(this) >= 0;
            return { selected: bsel, dragover: bdragover };
        }
        draw_sel(cxt, c, color) {
            var dr = this.getBoundRectPixel();
            if (dr == null)
                return;
            var d = 6;
            cxt.beginPath();
            cxt.strokeStyle = color;
            oc.util.drawRect(cxt, dr.x, dr.y, dr.w, dr.h, null, null, 1, "red");
            for (var pt of dr.listFourPt()) {
                cxt.moveTo(pt.x, pt.y);
                cxt.arc(pt.x, pt.y, d, 0, Math.PI * 2, true);
            }
            cxt.stroke();
        }
        draw_sel_bk0(cxt) {
            // if (selitem.draw_sel_or_not(this.context))
            // 	return;//
            var dr = this.getBoundRectPixel();
            //var dr  =selitem.getBoundRectDraw() ;
            if (dr != null) {
                var d = 6;
                var dh = 3;
                oc.util.drawRect(cxt, dr.x, dr.y, dr.w, dr.h, null, null, 2, "red");
                oc.util.drawRect(cxt, dr.x - dh, dr.y - dh, d, d, null, null, 2, "red");
                oc.util.drawRect(cxt, dr.x + dr.w - dh, dr.y - dh, d, d, null, null, 2, "red");
                oc.util.drawRect(cxt, dr.x + dr.w - dh, dr.y + dr.h - dh, d, d, null, null, 2, "red");
                oc.util.drawRect(cxt, dr.x - dh, dr.y + dr.h - dh, d, d, null, null, 2, "red");
                return;
            }
            var py = this.getBoundPolygonDraw();
            if (py != null) {
                return;
            }
        }
        removeFromContainer() {
            if (this.container == null)
                return false;
            return this.container.removeItem(this);
        }
        toStr() {
            return "id=" + this.id + ",x=" + this.x + ",y=" + this.y;
        }
    }
    DrawItem.DrawItem_PNS = {
        _cat_name: "basic", _cat_title: "Basic",
        id: { title: "Id", type: "str", readonly: true },
        name: { title: "Name", type: "str" },
        title: { title: "Title", type: "str", binder: true },
        x: { title: "X", type: "number", readonly: true, binder: true },
        y: { title: "Y", type: "number", readonly: true, binder: true },
        zindex: { title: "z-index", type: "int" },
        groupName: { title: "Group Name", type: "str" },
    };
    DrawItem.DrawItem_ENS = null;
    oc.DrawItem = DrawItem;
})(oc || (oc = {}));
/// <reference path="./draw_item.ts" />
var oc;
(function (oc) {
    /**
     * support draw primtive in rect ,and simple sub class which need only know
     * how draw self.
     * sub class is need not knows draw outer env
     */
    class DrawItemRect extends oc.DrawItem {
        //private popMenu:PopMenu|null=null;
        constructor(opts) {
            super(opts);
            this.w = 100;
            this.h = 100;
        }
        getPropDefs() {
            var r = super.getPropDefs();
            r.push(DrawItemRect.Rect_PNS);
            return r;
        }
        getW() {
            return this.w;
        }
        getH() {
            return this.h;
        }
        getMinDrawSize() {
            return { w: 0, h: 0 };
        }
        getBoundRectDraw() {
            var pt = this.getDrawXY();
            return new oc.base.Rect(pt.x, pt.y, this.getW(), this.getH());
        }
        getBoundRectPixel() {
            var dr = this.getBoundRectDraw();
            if (dr == null)
                return null;
            var c = this.getContainer();
            if (c == null)
                return null;
            //var pt = this.getDrawXY();
            var w = c.transDrawLen2PixelLen(true, dr.w);
            var h = c.transDrawLen2PixelLen(false, dr.h);
            var pt = c.transDrawPt2PixelPt(dr.x, dr.y);
            return new oc.base.Rect(pt.x, pt.y, w, h);
        }
        getDrawPreferSize() {
            return { w: 100, h: 100 };
        }
        getPixelPreferSize() {
            var c = this.getContainer();
            if (c == null)
                throw "no container";
            var ds = this.getDrawPreferSize();
            var pw = c.transDrawLen2PixelLen(true, ds.w);
            var ph = c.transDrawLen2PixelLen(false, ds.h);
            return { w: pw, h: ph };
        }
        /**
         * w/h ratio  invalid when val<=0
         */
        getWHRatio() {
            return -1;
        }
        setDrawSize(w, h) {
            this.w = w;
            this.h = h;
        }
        setDrawW(w) {
            this.w = w;
        }
        setDrawH(h) {
            this.h = h;
        }
        getDrawSize() {
            return { w: this.w, h: this.h };
        }
        on_size_chged() { }
        getPixelSize() {
            var c = this.getContainer();
            if (c == null)
                return null;
            var w = c.transDrawLen2PixelLen(true, this.getW());
            var h = c.transDrawLen2PixelLen(false, this.getH());
            return { w: w, h: h };
        }
        setDrawBeginXY(cont, x, y) {
            super.setDrawBeginXY(cont, x, y);
            this.w = cont.transPixelLen2DrawLen(true, 2);
            this.h = cont.transPixelLen2DrawLen(false, 2);
            return true;
        }
        setDrawEndXY(cont, x, y) {
            var xy = super.setDrawEndXY(cont, x, y);
            x = xy.x;
            y = xy.y;
            this.w = x - this.x;
            this.h = y - this.y;
            var r = this.getWHRatio();
            if (r > 0) {
                this.h = this.w / r;
            }
            this.MODEL_fireChged(["w", "h"]);
            return xy;
        }
        getActionTypeName() {
            return "";
        }
        on_mouse_event(tp, pxy, dxy, e) {
            super.on_mouse_event(tp, pxy, dxy, e);
            if (tp == oc.MOUSE_EVT_TP.Down) {
                if (e.button == oc.MOUSE_BTN.RIGHT) { //right
                    if (oc.PopMenu.createShowPopMenu(this, pxy, dxy))
                        e.preventDefault();
                }
            }
            else if (tp == oc.MOUSE_EVT_TP.DbClk) {
                var pmi = oc.PopMenu.getDefaultPopMenuItem(this);
                if (pmi != null) {
                    pmi.action(this, pmi.op_name, pxy, dxy);
                }
                return;
            }
        }
        /**
         * override it and return true,will mask drawPrim
         * it can get sub class a chance to prevent from scale ugly. sub class must calc point in scale
         * and draw other which may beauty.
         * @param ctx
         * @param xratio
         * @param yratio
         */
        drawPrimScale(ctx, xratio, yratio) {
            return false;
        }
        /**
         * before scale drawPrim
         * override it can draw normal before draw prim
         * @param ctx
         * @param xratio
         * @param yratio
         */
        drawBeforeScale(ctx, xratio, yratio) {
            return;
        }
        /**
         * cal bound four point and four edge
         * return null or
         *   e s w n  ne se ws wn
         * @param x
         * @param y
         */
        // public chkPtOnCtrl0(pxy:base.Pt,dxy:base.Pt):string|null
        // {
        // 	var c = this.getContainer();
        // 	if(c==null)
        // 		return null;
        // 	var r = c.transPixelLen2DrawLen(true,util.CTRL_PT_R);
        // 	var minv = c.transPixelLen2DrawLen(true,util.CTRL_LN_MIN_PIXEL) ;
        // 	if(util.chkPtInRadius(this.x,this.y,dxy.x,dxy.y,r))
        // 		return "nw";
        // 	if(util.chkPtInRadius(this.x+this.w,this.y,dxy.x,dxy.y,r))
        // 		return "ne";
        // 	if(util.chkPtInRadius(this.x+this.w,this.y+this.h,dxy.x,dxy.y,r))
        // 		return "se";
        // 	if(util.chkPtInRadius(this.x,this.y+this.h,dxy.x,dxy.y,r))
        // 		return "sw";
        // 	if(dxy.x>this.x&&dxy.x<this.x+this.w&&Math.abs(dxy.y-this.y)<minv)
        // 		return "n" ;
        // 	if(dxy.y>this.y&&dxy.y<this.y+this.h&&Math.abs(dxy.x-this.x-this.w)<minv)
        // 		return "e" ;
        // 	if(dxy.x>this.x&&dxy.x<this.x+this.w&&Math.abs(dxy.y-this.y-this.h)<minv)
        // 		return "s" ;
        // 	if(dxy.y>this.y&&dxy.y<this.y+this.h&&Math.abs(dxy.x-this.x)<minv)
        // 		return "w" ;
        // 	return this.chkPtOnCtrl_rotate(pxy,dxy);
        // }
        chkPtOnCtrl(pxy, dxy) {
            var c = this.getContainer();
            if (c == null)
                return null;
            var ctx = this.getCxt();
            if (ctx == null)
                return null;
            var rect = this.getPrimRect();
            if (rect == null)
                return null;
            var w = c.transDrawLen2PixelLen(true, this.w);
            var h = c.transDrawLen2PixelLen(false, this.h);
            ctx.save();
            var pt = this.getDrawXY();
            var p1 = c.transDrawPt2PixelPt(pt.x, pt.y);
            //var cx = p1.x + w / 2;
            //var cy = p1.y + h / 2;
            ctx.translate(p1.x, p1.y);
            //ctx.rotate(this.rotate);
            //ctx.translate(-w / 2, -h / 2);
            //ctx.scale(w / rect.w, h / rect.h);
            var r;
            r = this.chkPtOnCtrl_border(ctx, w, h, pxy, dxy);
            if (r != null) {
                ctx.restore();
                return r;
            }
            r = this.chkPtOnCtrl_rotate(ctx, w, h, pxy, dxy);
            ctx.restore();
            return r;
        }
        chkPtOnCtrl_border(ctx, w, h, pxy, dxy) {
            // var c = this.getContainer();
            // if(c==null)
            // 	return null;
            // var r = c.transPixelLen2DrawLen(true,util.CTRL_PT_R);
            // var minv = c.transPixelLen2DrawLen(true,util.CTRL_LN_MIN_PIXEL) ;
            // if(dxy.x>this.x&&dxy.x<this.x+this.w&&Math.abs(dxy.y-this.y)<minv)
            // 	return "n" ;
            // if(dxy.y>this.y&&dxy.y<this.y+this.h&&Math.abs(dxy.x-this.x-this.w)<minv)
            // 	return "e" ;
            // if(dxy.x>this.x&&dxy.x<this.x+this.w&&Math.abs(dxy.y-this.y-this.h)<minv)
            // 	return "s" ;
            // if(dxy.y>this.y&&dxy.y<this.y+this.h&&Math.abs(dxy.x-this.x)<minv)
            // 	return "w" ;
            ctx.beginPath();
            ctx.arc(0, 0, 3, 0, Math.PI * 2);
            if (ctx.isPointInPath(pxy.x, pxy.y))
                return "nw";
            ctx.beginPath();
            ctx.arc(w, 0, 3, 0, Math.PI * 2);
            if (ctx.isPointInPath(pxy.x, pxy.y))
                return "ne";
            ctx.beginPath();
            ctx.arc(w, h, 3, 0, Math.PI * 2);
            if (ctx.isPointInPath(pxy.x, pxy.y))
                return "se";
            ctx.beginPath();
            ctx.arc(0, h, 3, 0, Math.PI * 2);
            if (ctx.isPointInPath(pxy.x, pxy.y))
                return "sw";
            if (ctx.isPointInStroke) { //ie is not support
                ctx.beginPath();
                ctx.moveTo(0, 0);
                ctx.lineTo(w, 0);
                if (ctx.isPointInStroke(pxy.x, pxy.y))
                    return "n";
                ctx.beginPath();
                ctx.moveTo(w, 0);
                ctx.lineTo(w, h);
                if (ctx.isPointInStroke(pxy.x, pxy.y))
                    return "e";
                ctx.beginPath();
                ctx.moveTo(w, h);
                ctx.lineTo(0, h);
                if (ctx.isPointInStroke(pxy.x, pxy.y))
                    return "s";
                ctx.beginPath();
                ctx.moveTo(0, h);
                ctx.lineTo(0, 0);
                if (ctx.isPointInStroke(pxy.x, pxy.y))
                    return "w";
            }
            return null;
        }
        chkPtOnCtrl_rotate(ctx, w, h, pxy, dxy) {
            ctx.beginPath();
            ctx.arc(w / 2, -20, 3, 0, Math.PI * 2);
            //ctx.closePath();
            //var r = ctx.isPointInPath(pxy.x-p1.x, pxy.y-p1.y);
            var r = ctx.isPointInPath(pxy.x, pxy.y);
            //console.log(" in rotatoe path="+r);
            if (r)
                return "r";
            else
                return null;
        }
        /**
         * based on center pt,using draw pt to calculate angle
         * @param x
         * @param y
         */
        calArcAngleByDrawPt(x, y) {
            var pt = this.getDrawXY();
            var centerx = pt.x + this.w / 2;
            var centery = pt.y + this.h / 2;
            var dx = x - centerx;
            var dy = y - centery;
            if (dx == 0) {
                if (dy >= 0)
                    return Math.PI * 0.5;
                else
                    return Math.PI * 1.5;
            }
            var r = Math.atan(dy / dx);
            if (dx > 0) {
                if (r >= 0)
                    return r;
                else
                    return Math.PI * 2 + r;
            }
            else {
                if (r >= 0)
                    return r + Math.PI;
                else
                    return Math.PI + r;
            }
        }
        changeRect(ctrlpt, x, y) {
            var c = this.getContainer();
            if (c == null)
                return;
            if (ctrlpt == null)
                return;
            var minw = c.transPixelLen2DrawLen(true, oc.util.CTRL_LN_MIN_PIXEL * 2);
            var minh = minw;
            var ms = this.getMinDrawSize();
            minw = ms.w > minw ? ms.w : minw;
            minh = ms.h > minh ? ms.h : minh;
            var wh_ratio = this.getWHRatio();
            switch (ctrlpt) {
                case "e":
                    this.w = x - this.x;
                    if (this.w < minw)
                        this.w = minw;
                    if (wh_ratio <= 0) {
                        this.MODEL_fireChged(["w"]);
                    }
                    else {
                        this.h = this.w / wh_ratio;
                        this.MODEL_fireChged(["w", "h"]);
                    }
                    break;
                case "s":
                    this.h = y - this.y;
                    if (this.h < minh)
                        this.h = minh;
                    if (wh_ratio <= 0)
                        this.MODEL_fireChged(["h"]);
                    else {
                        this.w = this.h * wh_ratio;
                        this.MODEL_fireChged(["w", "h"]);
                    }
                    break;
                case "w":
                    var rx = this.x + this.w;
                    this.x = x;
                    this.w = rx - x;
                    if (this.w < minw) {
                        this.w = minw;
                        this.x = rx - minw;
                    }
                    if (wh_ratio <= 0)
                        this.MODEL_fireChged(["x", "w"]);
                    else {
                        this.h = this.w / wh_ratio;
                        this.MODEL_fireChged(["w", "x", "h"]);
                    }
                    break;
                case "n":
                    var ry = this.y + this.h;
                    this.y = y;
                    this.h = ry - y;
                    if (this.h < minh) {
                        this.h = minh;
                        this.y = ry - minh;
                    }
                    if (wh_ratio <= 0)
                        this.MODEL_fireChged(["y", "h"]);
                    else {
                        this.w = this.h * wh_ratio;
                        this.MODEL_fireChged(["y", "w", "h"]);
                    }
                    break;
                case "ne":
                    this.w = x - this.x;
                    if (this.w < minw)
                        this.w = minw;
                    var ry = this.y + this.h;
                    this.y = y;
                    this.h = ry - y;
                    if (this.h < minh) {
                        this.h = minh;
                        this.y = ry - minh;
                    }
                    if (wh_ratio > 0) {
                        this.w = this.h * wh_ratio;
                    }
                    this.MODEL_fireChged(["w", "y", "h"]);
                    break;
                case "se":
                    this.h = y - this.y;
                    if (this.h < minh)
                        this.h = minh;
                    this.w = x - this.x;
                    if (this.w < minw)
                        this.w = minw;
                    if (wh_ratio > 0) {
                        this.h = this.w / wh_ratio;
                    }
                    this.MODEL_fireChged(["w", "h"]);
                    break;
                case "sw":
                    this.h = y - this.y;
                    if (this.h < minh)
                        this.h = minh;
                    var rx = this.x + this.w;
                    this.x = x;
                    this.w = rx - x;
                    if (this.w < minw) {
                        this.w = minw;
                        this.x = rx - minw;
                    }
                    if (wh_ratio > 0) {
                        this.h = this.w / wh_ratio;
                    }
                    this.MODEL_fireChged(["h", "x", "w"]);
                    break;
                case "nw":
                    var ry = this.y + this.h;
                    var rx = this.x + this.w; //fix
                    this.y = y;
                    this.h = ry - y;
                    if (this.h < minh) {
                        this.h = minh;
                        this.y = ry - minh;
                    }
                    if (wh_ratio > 0) {
                        this.w = this.h * wh_ratio;
                        this.x = rx - this.w;
                    }
                    else {
                        this.x = x;
                        this.w = rx - x;
                    }
                    this.MODEL_fireChged(["x", "y", "w", "h"]);
                    break;
            }
            this.on_size_chged();
        }
        draw(ctx, c) {
            var rect = this.getPrimRect();
            //console.log("rect draw "+this.getClassName()+">>rect="+rect+"  w="+this.w+" h="+this.h);
            if (rect == null)
                return;
            if (this.w <= 0 || this.h <= 0)
                return;
            var w = c.transDrawLen2PixelLen(true, this.w);
            var h = c.transDrawLen2PixelLen(false, this.h);
            ctx.save();
            var pt = this.getDrawXY();
            var p1 = c.transDrawPt2PixelPt(pt.x, pt.y);
            ctx.translate(p1.x, p1.y);
            var xratio = w / rect.w;
            var yratio = h / rect.h;
            if (!this.drawPrimScale(ctx, xratio, yratio)) {
                this.drawBeforeScale(ctx, xratio, yratio);
                ctx.scale(xratio, yratio);
                this.drawPrim(ctx); //call sub
            }
            ctx.restore();
        }
        draw_sel(ctx, c) {
            var rect = this.getPrimRect();
            if (rect == null)
                return;
            if (this.w <= 0 || this.h <= 0)
                return;
            var w = c.transDrawLen2PixelLen(true, this.w);
            var h = c.transDrawLen2PixelLen(false, this.h);
            var rd = 20; //c.transDrawLen2PixelLen(true,20) ;
            ctx.save();
            var pt = this.getDrawXY();
            var p1 = c.transDrawPt2PixelPt(pt.x, pt.y);
            ctx.translate(p1.x, p1.y);
            //---
            ctx.lineWidth = 1;
            ctx.strokeStyle = "red";
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
            //ctx.translate(p1.x,p1.y);
            ctx.rect(0, 0, w, h);
            ctx.stroke();
            ctx.scale(w / rect.w, h / rect.h);
            ctx.beginPath();
            //ctx.scale(w/rect.w,h/rect.h);
            this.drawPrimSel(ctx);
            ctx.restore();
        }
    }
    DrawItemRect.Rect_PNS = {
        _cat_name: "rect", _cat_title: "Rectangle",
        w: { title: "width", type: "int", readonly: true, binder: true },
        h: { title: "height", type: "int", readonly: true, binder: true },
    };
    oc.DrawItemRect = DrawItemRect;
    class DrawItemRectBorder extends DrawItemRect {
        constructor(opts) {
            super(opts);
            this.pts = [[0, 0], [100, 0], [100, 100], [0, 100]];
            if (opts != undefined) {
                var r = opts["rect"];
                if (r != undefined && r != null) {
                    this.x = r.x;
                    this.y = r.y;
                    this.setDrawSize(r.w, r.h);
                }
            }
        }
        getPrimRect() {
            return new oc.base.Rect(0, 0, 100, 100);
        }
        drawPrim(ctx) {
            ctx.lineWidth = 1;
            ctx.setLineDash([5, 5]);
            ctx.strokeStyle = "#c1cccc";
            ctx.beginPath();
            for (var tmppt of this.pts) {
                ctx.lineTo(tmppt[0], tmppt[1]);
            }
            ctx.lineTo(this.pts[0][0], this.pts[0][1]);
            ctx.stroke();
        }
        drawPrimSel(ctx) {
        }
    }
    oc.DrawItemRectBorder = DrawItemRectBorder;
    class DrawItemRectR extends DrawItemRect {
        constructor(opts) {
            super(opts);
            this.rotate = 0;
            //in rect rotate center ratio 0-1
            this.rc_ratio_x = 0.5;
            this.rc_ratio_y = 0.5;
        }
        getPropDefs() {
            var r = super.getPropDefs();
            r.push(DrawItemRectR.Rect_PNSR);
            return r;
        }
        changeRotate(x, y) {
            this.rotate = this.calArcAngleByDrawPt(x, y) + Math.PI / 2;
            this.MODEL_fireChged(["rotate"]);
        }
        chkPtOnCtrl(pxy, dxy) {
            var c = this.getContainer();
            if (c == null)
                return null;
            var ctx = this.getCxt();
            if (ctx == null)
                return null;
            var rect = this.getPrimRect();
            if (rect == null)
                return null;
            var w = c.transDrawLen2PixelLen(true, this.getW());
            var h = c.transDrawLen2PixelLen(false, this.getH());
            ctx.save();
            var pt = this.getDrawXY();
            var p1 = c.transDrawPt2PixelPt(pt.x, pt.y);
            var cx = p1.x + w * this.rc_ratio_x;
            var cy = p1.y + h * this.rc_ratio_y;
            ctx.translate(cx, cy);
            ctx.rotate(this.rotate);
            ctx.translate(-w * this.rc_ratio_x, -h * this.rc_ratio_y);
            //ctx.scale(w / rect.w, h / rect.h);
            var r;
            r = this.chkPtOnCtrl_border(ctx, w, h, pxy, dxy);
            if (r != null) {
                ctx.restore();
                return r;
            }
            r = this.chkPtOnCtrl_rotate(ctx, w, h, pxy, dxy);
            ctx.restore();
            return r;
        }
        calcTransMatrix(x, y, w, h, r) {
            var cx = x + r.w / 2;
            var cy = y + r.h / 2;
            //ctx.translate(cx,cy);
            var m1 = oc.util.Matrix.getTranslation(cx, cy);
            //ctx.rotate(this.rotate);
            var mr = oc.util.Matrix.getRotation(this.rotate);
            //ctx.translate(-w/2,-h/2);
            var m2 = oc.util.Matrix.getTranslation(-r.w / 2, -r.h / 2);
            var ms = oc.util.Matrix.getScale(w / r.w, h / r.h);
            var m = oc.util.Matrix.mergeTransformations([m1, mr, m2, ms]);
            return m;
        }
        calcTransReverse(x, y, w, h, r) {
            var cx = x + r.w / 2;
            var cy = y + r.h / 2;
            //ctx.translate(cx,cy);
            var ms = oc.util.Matrix.getScale(r.w / w, r.h / h);
            var m2 = oc.util.Matrix.getTranslation(r.w / 2, r.h / 2);
            var mr = oc.util.Matrix.getRotation(this.rotate);
            var m1 = oc.util.Matrix.getTranslation(-cx, -cy);
            var m = oc.util.Matrix.mergeTransformations([ms, m2, mr, m1]);
            return m;
        }
        draw(ctx, c) {
            var rect = this.getPrimRect();
            //console.log("rect draw "+this.getClassName()+">>rect="+rect+"  w="+this.w+" h="+this.h);
            if (rect == null)
                return;
            if (this.getW() <= 0 || this.getH() <= 0)
                return;
            var w = c.transDrawLen2PixelLen(true, this.getW());
            var h = c.transDrawLen2PixelLen(false, this.getH());
            ctx.save();
            var pt = this.getDrawXY();
            var p1 = c.transDrawPt2PixelPt(pt.x, pt.y);
            if (this.rotate != 0) {
                var cx = p1.x + w * this.rc_ratio_x;
                var cy = p1.y + h * this.rc_ratio_y;
                ctx.translate(cx, cy);
                ctx.rotate(this.rotate);
                ctx.translate(-w * this.rc_ratio_x, -h * this.rc_ratio_y);
            }
            else {
                ctx.translate(p1.x, p1.y);
            }
            var xratio = w / rect.w;
            var yratio = h / rect.h;
            if (!this.drawPrimScale(ctx, xratio, yratio)) {
                ctx.scale(xratio, yratio);
                this.drawPrim(ctx); //call sub
            }
            ctx.restore();
        }
        draw_sel(ctx, c) {
            var rect = this.getPrimRect();
            if (rect == null)
                return;
            if (this.getW() <= 0 || this.getH() <= 0)
                return;
            var w = c.transDrawLen2PixelLen(true, this.getW());
            var h = c.transDrawLen2PixelLen(false, this.getH());
            var rd = 20; //c.transDrawLen2PixelLen(true,20);
            ctx.save();
            var pt = this.getDrawXY();
            var p1 = c.transDrawPt2PixelPt(pt.x, pt.y);
            if (this.rotate != 0) {
                var cx = p1.x + w * this.rc_ratio_x;
                var cy = p1.y + h * this.rc_ratio_y;
                ctx.translate(cx, cy);
                ctx.rotate(this.rotate);
                ctx.translate(-w * this.rc_ratio_x, -h * this.rc_ratio_y);
            }
            else {
                ctx.translate(p1.x, p1.y);
            }
            //---
            ctx.lineWidth = 1;
            ctx.strokeStyle = "red";
            ctx.beginPath();
            ctx.arc(w / 2, -rd, 3, 0, Math.PI * 2);
            ctx.fill();
            ctx.stroke();
            ctx.beginPath();
            ctx.setLineDash([5, 5]);
            ctx.moveTo(w / 2, -rd);
            ctx.lineTo(w / 2, 0);
            ctx.stroke();
            ctx.beginPath();
            ctx.setLineDash([]);
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
            //ctx.translate(p1.x,p1.y);
            ctx.rect(0, 0, w, h);
            ctx.stroke();
            ctx.beginPath();
            //ctx.scale(w/rect.w,h/rect.h);
            ctx.scale(w / rect.w, h / rect.h);
            this.drawPrimSel(ctx);
            ctx.restore();
        }
    }
    DrawItemRectR.Rect_PNSR = {
        _cat_name: "rect_r", _cat_title: "Rectangle Rotation",
        rotate: { title: "Rotate", type: "float" },
        rc_ratio_x: { title: "Rotate Center Ratio X", type: "float", val_range: [0, 1.0] },
        rc_ratio_y: { title: "Rotate Center Ratio Y", type: "float", val_range: [0, 1.0] },
    };
    oc.DrawItemRectR = DrawItemRectR;
    /**
     * specal container drawitem.
     * it containers multi drawitem(not group),and can limit inner drawitem draw in it rect area
     * it can select only by border
     */
    class DrawItemGroup extends DrawItemRect {
        constructor(opts) {
            super(opts);
            this.title = "group";
            this.titleFontSize = 40;
        }
        getClassName() {
            return "DrawItemGroup";
        }
        getPropDefs() {
            var r = super.getPropDefs();
            r.push(DrawItemGroup.Rect_PNSG);
            return r;
        }
        /**
         * override to set xy self and inner drawitems
         * @param x
         * @param y
         */
        setDrawXY(x, y) {
            var n = this.getName();
            var lay = this.getLayer();
            if (n == null || n == "" || lay == null) {
                super.setDrawXY(x, y);
                return;
            }
            var gis = lay.getItemsByGroupName(n);
            if (gis.length == 0) {
                super.setDrawXY(x, y);
                return;
            }
            var dx = x - this.x;
            var dy = y - this.y;
            this.x = x;
            this.y = y;
            for (var gi of gis) {
                gi.setDrawXY(gi.x + dx, gi.y + dy);
            }
            this.MODEL_fireChged(["x", "y"]);
        }
        getPrimRect() {
            return new oc.base.Rect(0, 0, 100, 100);
        }
        drawPrim(ctx) {
            //util.drawRect(ctx,0,0,100,100,null,null,1,"blue");
        }
        drawPrimSel(ctx) {
        }
        chkCanSelectDrawPt(x, y) {
            if (this.getSelState().selected)
                return super.chkCanSelectDrawPt(x, y);
            else
                return this.chkCanSelectDrawPtBorder(x, y);
        }
        /**
         * override to make select only on border
         * @param x
         * @param y
         */
        chkCanSelectDrawPtBorder(x, y) {
            var c = this.getContainer();
            if (c == null)
                return false;
            var w = this.getW();
            var h = this.getH();
            var minv = c.transPixelLen2DrawLen(true, oc.util.CTRL_LN_MIN_PIXEL);
            if (x > this.x && x < this.x + w && Math.abs(y - this.y) < minv)
                return true;
            if (y > this.y && y < this.y + h && Math.abs(x - this.x - w) < minv)
                return true;
            if (x > this.x && x < this.x + w && Math.abs(y - this.y - h) < minv)
                return true;
            if (y > this.y && y < this.y + h && Math.abs(x - this.x) < minv)
                return true;
            return false;
        }
        draw(cxt, c) {
            var pt = this.getDrawXY();
            var dxy = c.transDrawPt2PixelPt(pt.x, pt.y);
            var dxyt = c.transDrawPt2PixelPt(pt.x + 10, pt.y + 40);
            var dw = c.transDrawLen2PixelLen(true, this.getW());
            var dh = c.transDrawLen2PixelLen(false, this.getH());
            oc.util.drawRectEmpty(cxt, dxy.x, dxy.y, dw, dh, null);
            var fs = c.transDrawLen2PixelLen(true, this.titleFontSize);
            cxt.save();
            //cxt.fontt = this.fontSize+"px serif";
            cxt.font = fs + "px serif";
            cxt.fillStyle = "#c1cccc";
            cxt.fillText(this.title, dxyt.x, dxyt.y);
            cxt.restore();
            return;
        }
    }
    DrawItemGroup.Rect_PNSG = {
        _cat_name: "group", _cat_title: "Group",
        title: { title: "Title", type: "string" },
    };
    oc.DrawItemGroup = DrawItemGroup;
})(oc || (oc = {}));
/// <reference path="./draw_item_rect.ts" />
var oc;
(function (oc) {
    /**
     * only for div comp loaded var
     */
    class div_comps {
    }
    oc.div_comps = div_comps;
    /**
     * Unit TreeNode which has inner div
     * then div can has it's owner display content
     * e.g list
     */
    class DrawDiv extends oc.DrawItemRect {
        constructor(opts) {
            super(opts);
            this.divEle = null;
            this.contEle = null;
            this.innerEle = null;
            // div_scroll:boolean=false; for sub may cause display bug
            //div_transparent:number=0.5;
            this.div_bkcolor = "";
            this.border = 0;
            this.border_color = "#8cdcda";
            this.eleId = null;
            this.lastRect = null;
        }
        getClassName() {
            return "oc.DrawDiv";
        }
        getPropDefs() {
            var r = super.getPropDefs();
            r.push(DrawDiv.PNS_DIV);
            return r;
        }
        on_after_inject(pvs) {
            super.on_after_inject(pvs);
            this.getContEle();
            if (this.contEle == null)
                return;
            var v = pvs["div_bkcolor"];
            if (v != undefined && v != null) {
                if (this.divEle != null)
                    this.divEle.css("background-color", v);
                this.contEle.css("background-color", v);
            }
            //var 
            //this.contEle.get(0).style.backgroundColor=v ;
        }
        setVisiable(b) {
            super.setVisiable(b);
            if (this.divEle != null) {
                this.divEle.css("display", b ? "" : "none");
            }
        }
        getContEleId() {
            if (this.eleId == null)
                this.eleId = oc.util.create_new_tmp_id();
            return "c_" + this.eleId; //this.getId() ;
        }
        getContEle() {
            if (this.contEle != null)
                return this.contEle;
            var p = this.getPanel();
            if (p == null)
                return null;
            var ele = p.getHTMLElement();
            var tmpid = this.getId();
            var scroll = "";
            //if(this.div_scroll==true)
            //    scroll = "overflow:auto;";
            //else
            scroll = "overflow:hidden;";
            var bkc = "";
            if (this.div_bkcolor != null && this.div_bkcolor != undefined && this.div_bkcolor != "")
                bkc = "background-color:" + this.div_bkcolor;
            this.divEle = $(`<div id="div_${tmpid}" class="oc_unit_action" style="${scroll};${bkc}">
                <div id="${this.getContEleId()}" class="content" style="width:100%;height:100%;${bkc}"></div></div>`);
            this.divEle.get(0)["_oc_di_div"] = this;
            $(ele).append(this.divEle);
            this.contEle = $("#" + this.getContEleId());
            if (this.innerEle != null)
                this.contEle.append(this.innerEle);
            return this.contEle;
        }
        //override to init div
        // protected on_container_set()
        // {
        //     super.on_container_set() ;
        //     this.getContEle() ;
        // }
        /**
         * override to del div
         */
        removeFromContainer() {
            if (!super.removeFromContainer())
                return false;
            if (this.divEle != null)
                this.divEle.remove();
            return true;
        }
        setInnerEle(ele) {
            if (typeof (ele) == "string")
                ele = $(ele);
            this.innerEle = ele;
            var contele = this.getContEle();
            if (contele == null)
                return this.innerEle;
            contele.empty();
            contele.append(this.innerEle);
            return this.innerEle;
        }
        draw_hidden(cxt, c) {
            this.hideDivEle();
        }
        hideDivEle() {
            if (this.divEle == null)
                return;
            this.divEle.css("display", "none");
        }
        onDivResize(x, y, w, h, b_notchg) {
        }
        displayDivEle() {
            if (this.divEle == null)
                return;
            var contele = this.getContEle();
            if (contele == null || this.divEle == null)
                return;
            var c = this.getContainer();
            if (c == null)
                return;
            var hh = c.transDrawLen2PixelLen(false, 30);
            if (this.title == null || this.title == "")
                hh = 0;
            var r = this.getBoundRectPixel();
            if (r != null) {
                var not_chg = r.equals(this.lastRect);
                if (not_chg)
                    return;
                this.lastRect = r;
                this.divEle.css("display", "");
                //this.divEle.css("background-color","#eee");
                this.divEle.css("top", (r.y + hh) + "px");
                this.divEle.css("left", r.x + "px");
                this.divEle.css("width", r.w + "px");
                this.divEle.css("height", (r.h - hh) + "px");
                this.onDivResize(r.x, r.y + hh, r.w, r.h - hh, not_chg);
            }
        }
        getPrimRect() {
            return new oc.base.Rect(0, 0, 100, 100);
        }
        drawPrim(ctx) {
            this.displayDivEle();
            if (this.border > 0) { // hmi sub may cause display err
                //oc.util.drawRect(ctx, 0,0,100,100, null, null, this.border, this.border_color);
            }
        }
        getTitle() {
            var t = super.getTitle();
            if (t == null)
                return "";
            return t;
        }
        getMinDrawSize() {
            var t = this.getTitle();
            return { w: 20 * t.length, h: 30 };
        }
        draw(ctx, c) {
            //draw circle at left top position
            super.draw(ctx, c);
            var pxy = this.getPixelXY();
            if (pxy == null)
                return;
            var fh = c.transDrawLen2PixelLen(false, 20);
            //var pt = c.tr
            ctx.font = `${fh}px serif`;
            ctx.fillStyle = "yellow";
            var t = this.getTitle();
            if (t == null)
                t = "";
            ctx.fillText(t, pxy.x + fh, pxy.y + fh);
        }
        drawPrimSel(ctx) {
        }
    }
    DrawDiv.PNS_DIV = {
        _cat_name: "ddiv", _cat_title: "Draw Div",
        border: { title: "Border", type: "number", binder: true },
        border_color: { title: "Border Color", type: "str", binder: true },
        div_scroll: { title: "Scroll", type: "boolean", enum_val: [[true, "Yes"], [false, "No"]] },
        //div_transparent: {title: "Transparent", type:"number",binder:true},
        div_bkcolor: { title: "Background Color", type: "str", edit_plug: "color", binder: true }
    };
    oc.DrawDiv = DrawDiv;
    /**
     *
     */
    class DIDivCompLoader {
        constructor(catname, compname) {
            this.comp = null;
            this.loadOk = false;
            //loading listeners,
            this.loadingCB = [];
            this.catName = catname;
            var catob = oc.div_comps[catname];
            if (catob == undefined || catob == null)
                oc.div_comps[catname] = {};
            this.compName = compname;
        }
        getCompCat() {
            return this.catName;
        }
        getCompName() {
            return this.compName;
        }
        addLoadingCB(cb) {
            var cp = this.createComp();
            if (cp != null) {
                cb(cp);
                return;
            }
            this.loadingCB.push(cb);
        }
        fireLoadOk() {
            if (this.loadingCB.length == 0)
                return;
            if (this.comp == null)
                return;
            for (var ldcb of this.loadingCB) {
                var tmpcomp = this.createComp();
                if (tmpcomp != null)
                    ldcb(tmpcomp);
            }
            this.loadingCB = [];
        }
        doLoad() {
            var url = "/_iottree/di_div_comps/" + this.catName + "/comp_" + this.compName + ".js";
            //load comp first
            oc.JsLoader.loadJsUrl(url, () => {
                this.comp = this.createCompInner();
                if (this.comp == null || this.comp == undefined)
                    return;
                var rjs = this.comp.comp_require_js;
                if (rjs == null || rjs == undefined || rjs.length == 0) { //no needed js
                    this.loadOk = true;
                    this.fireLoadOk();
                    return;
                }
                //then load needed js
                var jsus = [];
                for (var rj of rjs) {
                    if (!rj.startsWith("/"))
                        rj = "/_iottree/di_div_comps/" + this.catName + "/" + rj;
                    if (DIDivCompLoader.LOADED_JS.hasOwnProperty(rj))
                        continue;
                    jsus.push(rj);
                }
                if (jsus.length == 0) { //no needed js
                    this.loadOk = true;
                    this.fireLoadOk();
                    return;
                }
                var jssl = new oc.JssLoader(jsus, (ld) => {
                    this.loadOk = true;
                    for (var jsu of ld.getJsUrls())
                        DIDivCompLoader.LOADED_JS[jsu] = "";
                    this.fireLoadOk();
                    return;
                });
                jssl.load();
                return;
            });
        }
        createCompInner() {
            try {
                var tt = {};
                eval("tt['aa']=new oc.div_comps." + this.catName + "." + this.compName + "(this)");
                var r = tt['aa'];
                if (r == undefined)
                    return null;
                return r;
            }
            catch (e) {
                console.error(e);
                return null;
            }
        }
        createComp() {
            if (!this.loadOk)
                return null;
            return this.createCompInner();
        }
        isLoadOk() {
            return this.loadOk;
        }
    }
    DIDivCompLoader.LOADED_JS = {};
    class DIDivComp extends DrawDiv {
        //compName:string|null|undefined = null ;
        constructor(opts) {
            super(opts);
            this.comp = null;
            this.isCompLoading = false;
            this.comp_uid = "";
            this.compInsChged = false;
        }
        static getCompLoader(compuid) {
            var ld = DIDivComp.name2compitem[compuid];
            if (ld == null) {
                var nn = DIDivComp.splitCompUid(compuid);
                ld = new DIDivCompLoader(nn[0], nn[1]);
                DIDivComp.name2compitem[compuid] = ld;
                ld.doLoad();
            }
            return ld;
        }
        static splitCompUid(compuid) {
            var i = compuid.indexOf('-');
            var catn = compuid.substr(0, i);
            var compn = compuid.substr(i + 1);
            return [catn, compn];
        }
        comp_inter_event_fire(n, v) {
        }
        getClassName() {
            return "oc.DIDivComp";
        }
        getPropDefs() {
            var r = super.getPropDefs();
            r.push(DIDivComp.PNS_COMP);
            var cp = this.getOrLoadComp();
            if (cp != null && cp.comp_inters) {
                var cpinter = cp.comp_inters();
                if (cpinter != null) {
                    var pns = { _cat_name: "divcomp_inter", _cat_title: "Component Interface" };
                    var nn = DIDivComp.splitCompUid(this.comp_uid);
                    var prefix = nn[0] + "_" + nn[1] + "_";
                    for (var p of cpinter.props) {
                        var tmpn = p.name;
                        if (tmpn == null || tmpn == undefined || tmpn == "")
                            continue;
                        pns[prefix + tmpn] = p;
                    }
                    r.push(pns);
                }
            }
            return r;
        }
        getCompUid() {
            var compuid = this["comp_uid"];
            if (compuid == null || compuid == undefined) {
                return null;
            }
            return compuid;
        }
        setCompUid(uid) {
            if (uid == this.getCompUid())
                return;
            this["comp_uid"] = uid;
            this.reloadComp();
            this.MODEL_fireChged(["comp_uid"]);
        }
        inject(opts, ignore_readonly) {
            super.inject(opts, ignore_readonly);
            var cp = this.getOrLoadComp();
            if (cp != null && cp.comp_inters) {
                var nn = DIDivComp.splitCompUid(this.comp_uid);
                var prefix = nn[0] + "_" + nn[1] + "_";
                var cpinter = cp.comp_inters();
                if (cpinter != null) {
                    for (var p of cpinter.props) {
                        var tmpn = prefix + p.name;
                        var v = opts[tmpn];
                        if (v == undefined)
                            continue;
                        cp.comp_inter_prop_set(p.name, v);
                    }
                }
            }
            if (this.comp_uid != null && this.comp_uid != undefined) {
            }
        }
        extract() {
            var r = super.extract();
            var cp = this.getOrLoadComp();
            if (cp == null)
                return r;
            if (!cp.comp_inters)
                return r;
            var nn = DIDivComp.splitCompUid(this.comp_uid);
            var prefix = nn[0] + "_" + nn[1] + "_";
            var cpinter = cp.comp_inters();
            if (cpinter != null) {
                for (var p of cpinter.props) {
                    var v = cp.comp_inter_prop_get(p.name);
                    if (v == null || v == undefined)
                        continue;
                    var tmpn = prefix + p.name;
                    r[tmpn] = v;
                }
            }
            return r;
        }
        on_container_set() {
            super.on_container_set();
            this.getOrLoadComp();
            ``;
        }
        reloadComp() {
            this.comp = null;
            this.isCompLoading = false;
            return this.getOrLoadComp();
        }
        getOrLoadComp() {
            var _a;
            var compuid = this.getCompUid();
            if (compuid == null) {
                return null;
            }
            if (this.comp != null)
                return this.comp;
            var contele = this.getContEle();
            if (contele == null)
                return null;
            if (this.isCompLoading)
                return null;
            var view = (_a = this.getPanel()) === null || _a === void 0 ? void 0 : _a.getDrawView();
            if (view == null || view == undefined)
                return null;
            var id = this.getContEleId();
            var ld = DIDivComp.getCompLoader(compuid);
            if (ld.isLoadOk()) {
                var comp = ld.createComp();
                if (comp != null)
                    comp.comp_init(id, view);
                this.comp = comp;
                this.compInsChged = true;
                this.redraw();
                return this.comp;
            }
            this.isCompLoading = true;
            ld.addLoadingCB((comp) => {
                if (view == null || view == undefined)
                    return null;
                comp.comp_init(id, view);
                this.comp = comp;
                this.isCompLoading = false;
                this.compInsChged = true;
                this.redraw();
            });
            return null;
        }
        onDivResize(x, y, w, h, b_notchg) {
            //console.log("onDivResize - - ") ;
            if (this.comp == null)
                return;
            if (b_notchg && !this.compInsChged)
                return;
            //console.log("  chart resized") ;
            this.compInsChged = false;
            this.comp.comp_on_resize(w, h);
        }
    }
    DIDivComp.DRAW_DIVCOMP = "_draw_divcomp";
    DIDivComp.name2compitem = {};
    DIDivComp.PNS_COMP = {
        _cat_name: "divcomp", _cat_title: "Div Component",
        comp_uid: { title: "Component Uid", type: "str", readonly: true },
    };
    oc.DIDivComp = DIDivComp;
})(oc || (oc = {}));
var oc;
(function (oc) {
    class DrawEditor {
        constructor(prop_ele, event_ele, panel, opts) {
            this.selectedItem = null;
            this.plugCB = null;
            var ele = document.getElementById(prop_ele);
            var evt_ele = document.getElementById(event_ele);
            if (ele == null || evt_ele == null)
                throw new Error("no edit panel element found");
            this.tarEditPropEle = ele;
            this.tarEditEvtEle = evt_ele;
            this.drawPanel = panel;
            if (!opts)
                opts = {};
            var pcb = opts["plug_cb"];
            if (pcb != null)
                this.plugCB = pcb;
            panel.MODEL_registerListener(this);
        }
        init_editor() {
        }
        on_model_chged(panel, layer, item, prop_names) {
            if (item != null && this.selectedItem == item && prop_names != null) {
                for (var n of prop_names) {
                    var v = item[n];
                    $("#si_" + n).val(v);
                }
            }
        }
        on_model_oper_chged(panel, intera, oper) {
        }
        on_model_sel_chged(panel, layer, item) {
            this.onItemSelected(item);
        }
        static createPropItemEditHtml(n, pdf, v, pb) {
            var rets = `<tr>
			<td style="width:40%" class="" title="${n}">${pdf.title}</td>
			<td style="width:40%">`;
            var editplug = pdf.edit_plug;
            if (editplug == undefined || editplug == null)
                editplug = "";
            if (pdf.readonly) {
                rets += `<input type=text id="si_${n}" propn="${n}" edit_plug="${editplug}" value="${v}" readonly=readonly style="width:100%"/>`;
            }
            else if (pdf.enum_val) {
                var opts = '';
                for (var j = 0; j < pdf.enum_val.length; j++) {
                    if (pdf.enum_val[j][0] == v)
                        opts += "<option value='" + v + "' selected=selected>" + pdf.enum_val[j][1] + "</option>";
                    else
                        opts += "<option value='" + pdf.enum_val[j][0] + "'>" + pdf.enum_val[j][1] + "</option>";
                }
                rets += `<select id="si_${n}" propn="${n}" prop_tp="${pdf.type}" edit_plug="${editplug}" style="width:100%">${opts}</select>`;
            }
            else {
                if (pdf.multiline) {
                    rets += `<textarea id="si_${n}" propn="${n}" prop_tp="${pdf.type}" edit_plug="${editplug}">${v}</textarea>`;
                }
                else {
                    var tp = pdf.type;
                    var inptp = "text";
                    var inp_step = "1";
                    if ("int" == tp) {
                        inptp = "number";
                    }
                    else if ("number" == tp || "float" == tp) {
                        inptp = "number";
                        inp_step = "0.1";
                    }
                    rets += `<input type="${inptp}" propn="${n}" prop_tp="${pdf.type}" edit_plug="${editplug}" step="${inp_step}" id="si_${n}" value="${v}" style="width:100%"/>`;
                }
            }
            rets += `</td><td style="width:20%">`;
            if (pdf.binder) {
                if (pb == null) {
                    rets += `<div id="pi_bd_${n}"  propn="${n}" style="border:solid 1px;width:100%;height:100%;">bind</div>`;
                }
                else {
                    if (pb.isValid())
                        rets += `<div id="pi_bd_${n}" propn="${n}" style="width:100%;height:100%;"><span style="background-color:green">bind ok</span></div>`;
                    else
                        rets += `<div id="pi_bd_${n}" propn="${n}" style="width:100%;height:100%;"><span style="background-color:red">bind err</span></div>`;
                }
            }
            rets += `</td></tr>`;
            return rets;
        }
        static createEventItemEditHtml(n, edf, v) {
            var rets = `<tr>
			<td style="width:50%" class="" title="${n}">${edf.title}</td>
			<td style="width:50%">`;
            var tp = edf.evt_tp;
            if (v == null) {
                rets += `<div id="ei_${n}"  eventn="${n}" evt_tp="${tp}" style="width:100%;height:100%;">&nbsp;</div>`;
            }
            else {
                var tmps = "";
                if (v.hasClientJS())
                    tmps += `&nbsp;<span style="background-color:green">client</span>`;
                if (v.hasServerJS())
                    tmps += `&nbsp;<span style="background-color:green">server</span>`;
                rets += `<div id="ei_${n}" eventn="${n}" evt_tp="${tp}" style="width:100%;height:100%;">${tmps}</div>`;
                //if(v.isValid())
                //	rets += `<div id="ei_${n}" eventn="${n}" evt_tp="${tp}" style="width:100%;height:100%;"><span style="background-color:green">bind ok</span></div>`;
                //else
                //	rets += `<div id="ei_${n}" eventn="${n}" evt_tp="${tp}" style="width:100%;height:100%;"><span style="background-color:red">bind err</span></div>`;
            }
            rets += `</td></tr>`;
            return rets;
        }
        static readPropItemStr(n, pdf) {
            if (n.indexOf("_") == 0)
                return null;
            var editi = $("#si_" + n);
            if (editi == null || editi == undefined)
                return null;
            return editi.val();
        }
        onItemSelected(item) {
            this.selectedItem = item;
            if (item == null) {
                $(this.tarEditPropEle).html("");
                $(this.tarEditEvtEle).html("");
                return;
            }
            var tmpid = oc.util.create_new_tmp_id();
            var tmps = `Type:${item.getClassName()}<br><div id="proppanel_${tmpid}" class="prop_edit_panel">`;
            var binds = '';
            var propdefs = item.getPropDefs();
            for (var i = 0; i < propdefs.length; i++) {
                var edef = propdefs[i];
                var catn = edef._cat_name;
                var catt = edef._cat_title;
                //tmps += `<div class="oc_edit_cat"></div>`;
                tmps += `<table class="pi_edit_table">
    				<tr><td colspan="3" class="td_left" style="font-weight: bold;color: #000000;background-color: #f0f0f0">${catt}</td></tr>`;
                for (var n in edef) {
                    if (n.indexOf('_') == 0)
                        continue;
                    var df = edef[n];
                    var v = item[n];
                    if (v == null)
                        v = '';
                    var pb = item.getPropBinder(n);
                    tmps += DrawEditor.createPropItemEditHtml(n, df, v, pb);
                }
                tmps += "</table>";
            }
            tmps += "</div>";
            $(this.tarEditPropEle).html(tmps);
            tmps = `<div id="eventpanel_${tmpid}" class="prop_edit_panel">`;
            var eventdefs = item.getEventDefs();
            for (var i = 0; i < eventdefs.length; i++) {
                var edef = eventdefs[i];
                var catn = edef._cat_name;
                var catt = edef._cat_title;
                //tmps += `<div class="oc_edit_cat"></div>`;
                tmps += `<table class="pi_edit_table">
    				<tr><td colspan="2" class="td_left" style="font-weight: bold;color: #000000;background-color: #f0f0f0">${catt}</td></tr>`;
                for (var n in edef) {
                    if (n.indexOf('_') == 0)
                        continue;
                    var df = edef[n];
                    var v = item.getEventBinder(n);
                    tmps += DrawEditor.createEventItemEditHtml(n, df, v);
                }
                tmps += "</table>";
            }
            tmps += "</div>";
            $(this.tarEditEvtEle).html(tmps);
            $(`#proppanel_${tmpid} input,select`).on('input', (e) => {
                var tar = $(e.target);
                var n = tar.attr("propn");
                if (!n)
                    return;
                var v = "" + tar.val();
                //this.applyUI2SelectedItem();
                this.applySinglePV2SelectedItem(n, v);
            });
            var thiz = this;
            $(`#proppanel_${tmpid} input,select`).on('click', function (e) {
                var pn = $(this).attr("propn");
                //var tp = $(this).attr("prop_tp");
                var editplug = $(this).attr("edit_plug");
                //if(editplug!=undefined&&editplug!=null&&editplug!="")
                //	tp = editplug;
                if (pn != null && pn != undefined && pn != "" && editplug && editplug != "") // && beditplug)
                 {
                    var v = item[pn];
                    //console.log("prop clk=",item,pn);
                    if (thiz.plugCB != null)
                        thiz.plugCB($(this), "prop_" + editplug, item, pn, v);
                }
            });
            $(`#eventpanel_${tmpid} div`).on('click', function (e) {
                var en = $(this).attr("eventn");
                var evttp = $(this).attr("evt_tp");
                if (en && evttp) {
                    var eb = item.getEventBinder(en);
                    //console.log("event clk=",item,en,eb);
                    if (thiz.plugCB != null)
                        //thiz.plugCB($(this),"event_"+evttp,item,en,eb);
                        thiz.plugCB($(this), "event_bind", item, en, eb);
                }
            });
            $(`#proppanel_${tmpid} div`).on('click', function (e) {
                var pn = $(this).attr("propn");
                if (pn != null && pn != undefined && pn != "") {
                    var pb = item.getPropBinder(pn);
                    if (thiz.plugCB != null)
                        thiz.plugCB($(this), "prop_bind", item, pn, pb);
                }
            });
        }
        /**
         * call by plugCB
         */
        refreshEventEditor() {
            if (this.selectedItem == null)
                return;
            var eventdefs = this.selectedItem.getEventDefs();
            for (var evtdef of eventdefs) {
                for (var n in evtdef) {
                    if (n.indexOf('_') == 0)
                        continue;
                    var pdf = evtdef[n];
                    var v = this.selectedItem.getEventBinder(n);
                    if (v == null) {
                        $("#ei_" + n).html("&nbsp;");
                    }
                    else {
                        var tmps = "";
                        if (v.hasClientJS())
                            tmps += `&nbsp;<span style="background-color:green">client</span>`;
                        if (v.hasServerJS())
                            tmps += `&nbsp;<span style="background-color:green">server</span>`;
                        $("#ei_" + n).html(tmps);
                        //if(v.isValid())
                        //	$("#ei_"+n).html(`<span style="background-color:green">bind ok</span>`);
                        //else
                        //	$("#ei_"+n).html(`<span style="background-color:red">bind err</span>`);
                    }
                }
            }
        }
        //call by plugCB
        refreshPropBindEditor() {
            if (this.selectedItem == null)
                return;
            var propdefs = this.selectedItem.getPropDefs();
            for (var propdef of propdefs) {
                for (var n in propdef) {
                    if (n.indexOf('_') == 0)
                        continue;
                    var pdf = propdef[n];
                    var v = this.selectedItem.getPropBinder(n);
                    if (v == null) {
                        $("#pi_bd_" + n).html("bind");
                    }
                    else {
                        if (v.isValid())
                            $("#pi_bd_" + n).html(`<span style="background-color:green">bind ok</span>`);
                        else
                            $("#pi_bd_" + n).html(`<span style="background-color:red">bind err</span>`);
                    }
                }
            }
        }
        static transUI2PropByPdf(pdef) {
            let r = {};
            for (var n in pdef) {
                var v = DrawEditor.readPropItemStr(n, pdef);
                if (v == undefined || v == null)
                    continue;
                var def = pdef[n];
                v = oc.DrawItem.transStr2Val(def["type"], v, null);
                r[n] = v;
            }
            return r;
        }
        static transUI2PropByPdfs(pdefs) {
            let r = {};
            for (var i = 0; i < pdefs.length; i++) {
                var pdef = pdefs[i];
                for (var n in pdef) {
                    var v = DrawEditor.readPropItemStr(n, pdef);
                    if (v == undefined || v == null)
                        continue;
                    var def = pdef[n];
                    v = oc.DrawItem.transStr2Val(def["type"], v, null);
                    r[n] = v;
                }
            }
            return r;
        }
        transUI2Prop() {
            if (this.selectedItem == null)
                return null;
            var pdefs = this.selectedItem.getPropDefs();
            return DrawEditor.transUI2PropByPdfs(pdefs);
        }
        applyUI2SelectedItem() {
            if (this.selectedItem == null)
                return false;
            let p = this.transUI2Prop();
            if (p == null)
                return false;
            this.selectedItem.inject(p, true);
            //this.selectedItem.update_draw();
            return true;
        }
        applySinglePV2SelectedItem(n, v) {
            if (this.selectedItem == null)
                return false;
            var def = this.selectedItem.findProDefItemByName(n);
            if (def == null)
                return false;
            var objv = oc.DrawItem.transStr2Val(def["type"], v, null);
            if (objv == null)
                return false;
            let r = {};
            r[n] = objv;
            this.selectedItem.inject(r, true);
            return true;
        }
    }
    oc.DrawEditor = DrawEditor;
})(oc || (oc = {}));
var oc;
(function (oc) {
    let MOUSE_BTN;
    (function (MOUSE_BTN) {
        MOUSE_BTN[MOUSE_BTN["LEFT"] = 0] = "LEFT";
        MOUSE_BTN[MOUSE_BTN["RIGHT"] = 2] = "RIGHT";
    })(MOUSE_BTN = oc.MOUSE_BTN || (oc.MOUSE_BTN = {}));
    ;
    let MOUSE_EVT_TP;
    (function (MOUSE_EVT_TP) {
        MOUSE_EVT_TP[MOUSE_EVT_TP["Down"] = 0] = "Down";
        MOUSE_EVT_TP[MOUSE_EVT_TP["Move"] = 1] = "Move";
        MOUSE_EVT_TP[MOUSE_EVT_TP["Up"] = 2] = "Up";
        MOUSE_EVT_TP[MOUSE_EVT_TP["Clk"] = 3] = "Clk";
        MOUSE_EVT_TP[MOUSE_EVT_TP["DbClk"] = 4] = "DbClk";
        MOUSE_EVT_TP[MOUSE_EVT_TP["Wheel"] = 5] = "Wheel";
        MOUSE_EVT_TP[MOUSE_EVT_TP["DragOver"] = 6] = "DragOver";
        MOUSE_EVT_TP[MOUSE_EVT_TP["DragLeave"] = 7] = "DragLeave";
        MOUSE_EVT_TP[MOUSE_EVT_TP["Drop"] = 8] = "Drop";
        MOUSE_EVT_TP[MOUSE_EVT_TP["DownLong"] = 9] = "DownLong";
    })(MOUSE_EVT_TP = oc.MOUSE_EVT_TP || (oc.MOUSE_EVT_TP = {}));
    ;
    oc.MOUSE_EVT_TP_NUM = 10;
    let KEY_EVT_TP;
    (function (KEY_EVT_TP) {
        KEY_EVT_TP[KEY_EVT_TP["Down"] = 0] = "Down";
        KEY_EVT_TP[KEY_EVT_TP["Up"] = 1] = "Up";
        KEY_EVT_TP[KEY_EVT_TP["Press"] = 2] = "Press";
    })(KEY_EVT_TP = oc.KEY_EVT_TP || (oc.KEY_EVT_TP = {}));
    ;
    /**
     *
     * @param tp
     */
    function getMouseEventNameByTp(tp) {
        switch (tp) {
            case MOUSE_EVT_TP.Down:
                return "mouse_down";
            case MOUSE_EVT_TP.Move:
                return "mouse_move";
            case MOUSE_EVT_TP.Up:
                return "mouse_up";
            case MOUSE_EVT_TP.Clk:
                return "mouse_clk";
            case MOUSE_EVT_TP.DbClk:
                return "mouse_dbclk";
            case MOUSE_EVT_TP.Wheel:
                return "mouse_whell";
            case MOUSE_EVT_TP.DragOver:
                return "mouse_dragover";
            case MOUSE_EVT_TP.DragLeave:
                return "mouse_dragleave";
            case MOUSE_EVT_TP.Drop:
                return "mouse_drop";
            case MOUSE_EVT_TP.DownLong:
                return "mouse_downlong";
        }
        return "";
    }
    oc.getMouseEventNameByTp = getMouseEventNameByTp;
    // // export interface MouseEvtListener
    // // {
    // //     on_mouse_event(tp:MouseEvtType,evt:MouseEvent):void ;
    // // }
    // let mouseEvts:MouseEvtListener[]=[] ;
    // export function registerMouseEvt(lis:MouseEvtListener):void
    // {
    //     mouseEvts.push(lis) ;
    // }
    // export function fireMouseEvt(e:any)
    // {
    // }
    class EventBinder {
        constructor() {
            //bServerJS:boolean = false;
            this.evtName = "";
            this.clientJS = "";
            this.serverJS = "";
            // public isServerJS()
            // {
            //     return this.bServerJS ;
            // }
            this.mouseF = null;
            this.tickF = null;
        }
        getEventName() {
            return this.evtName;
        }
        getClientJS() {
            return this.clientJS;
        }
        setClientJS(js) {
            this.clientJS = js;
            this.mouseF = null;
            this.tickF = null;
        }
        hasClientJS() {
            return this.clientJS != null && this.clientJS != "";
        }
        getServerJS() {
            return this.serverJS;
        }
        setServerJS(js) {
            this.serverJS = js;
        }
        hasServerJS() {
            return this.serverJS != null && this.serverJS != "";
        }
        isValid() {
            try {
                var sum = new Function("$_this", this.clientJS);
                //sum() ;
                return true;
            }
            catch (e) {
                return false;
            }
        }
        onEventRunMouse(di, pxy, dxy, e) {
            var _a;
            var pdi = di.getParentNode();
            if (this.mouseF == null) {
                try {
                    this.mouseF = new Function("$_parent", "$_this", "pxy", "dxy", "e", this.clientJS);
                }
                catch (e) {
                    return false;
                }
            }
            try {
                console.log(di.getClassName());
                //client run
                this.mouseF(pdi, di, pxy, dxy, e);
                //
                if (this.serverJS) { //send event to server,server run js in node context
                    var v = (_a = di.getPanel()) === null || _a === void 0 ? void 0 : _a.getDrawView();
                    if (v != null && v != undefined)
                        v.fireEventToServer(di.getId(), this.evtName, { pxy: pxy, dxy: dxy });
                }
                return true;
            }
            catch (E) {
                console.warn(E);
                return false;
            }
        }
        onEventRunTick(di) {
        }
        onEventRunInter(di) {
            var pdi = di.getParentNode();
            if (this.mouseF == null) {
                try {
                    this.mouseF = new Function("$_parent", "$_this", this.clientJS);
                }
                catch (e) {
                    return false;
                }
            }
            try {
                console.log(di.getClassName());
                this.mouseF(pdi, di);
                return true;
            }
            catch (E) {
                console.warn(E);
                return false;
            }
        }
        toPropStr() {
            return { n: this.evtName, clientjs: this.clientJS, serverjs: this.serverJS };
        }
        fromPropStr(p) {
            var n = p["n"];
            var cjs = p["clientjs"];
            var sjs = p["serverjs"];
            if (n == undefined || n == null) //||js==undefined||js==null)
                return false;
            this.evtName = n;
            this.clientJS = cjs;
            this.serverJS = sjs;
            return true;
        }
    }
    EventBinder.EVENT_BINDER = "_event_binder";
    oc.EventBinder = EventBinder;
    class EventBinderMapper {
        constructor() {
            this.eventNames = [];
            this.eventBD = {};
        }
        listEventNames() {
            return this.eventNames;
        }
        getEventBinder(eventn) {
            var r = this.eventBD[eventn];
            if (r == null || r == undefined)
                return null;
            return r;
        }
        setEventBinder(eventn, clientjs, serverjs) {
            var r = this.eventBD[eventn];
            if (r == null || r == undefined) {
                r = new EventBinder();
                r.evtName = eventn;
                this.eventBD[eventn] = r;
            }
            r.clientJS = clientjs;
            r.serverJS = serverjs;
        }
    }
    oc.EventBinderMapper = EventBinderMapper;
})(oc || (oc = {}));
/**
 * interact , a DrawOper and a Selector
 */
var oc;
(function (oc) {
    // export interface DrawLisSelector
    // {
    //  	onItemSelected(item: DrawItem|null): void;
    // }
    class DrawInteract {
        constructor(panel, layer, opts) {
            //private drawLayers: DrawLayer[]|null=null;
            this.bsel_down = false;
            this.bsel_down_mv = 0;
            this.curMouseDownItem = null;
            this.curMouseOnItem = null;
            this.curMouseOnItems = null;
            this.selectItems = [];
            //private canSelectItems: DrawItem[] = [];
            this.selectedItemDrag = null; //current drag item
            /**
             * when drag and drop,some container item may show special to notify obj that can drap
             * then these items must be selected
             */
            this.dragoverSelItems = [];
            //selectedListeners: DrawLisSelector[] = [];
            this.operStack = [];
            /**
             * support copy paste
             */
            this.copyPasteUrl = null;
            this.drawPanel = panel;
            this.drawLayer = layer;
            //this.canSelectItems = [];
            this.selectedItemDrag = null; //current drag item
            //set default oper
            if (opts && opts["show_only"])
                this.operStack.push(new oc.DrawOperShowOnly(this, layer));
            else
                this.operStack.push(new oc.DrawOperDrag(this, layer));
            if (opts) {
                var cpu = opts["copy_paste_url"];
                if (cpu && cpu != null && cpu != "")
                    this.copyPasteUrl = cpu;
            }
        }
        isOperDefault() {
            return this.operStack.length == 1;
        }
        getCurOper() {
            return this.operStack[this.operStack.length - 1];
        }
        logOperStack() {
            // var s = "";
            // for (var op of this.operStack)
            // {
            // 	s += " > " + op.getOperName();
            // }
            // console.log(s);
        }
        pushOperStack(op) {
            if (this.getCurOper() == op)
                return;
            this.operStack.push(op);
            op.on_oper_stack_push();
            this.logOperStack();
        }
        popOperStack() {
            if (this.operStack.length <= 1)
                throw new Error("stack must at least has one oper");
            var r = this.operStack.pop();
            r.on_oper_stack_pop();
            this.logOperStack();
            return r;
        }
        getPanel() {
            return this.drawPanel;
        }
        setPanel(p) {
            this.drawPanel = p;
        }
        getLayer() {
            return this.drawLayer;
        }
        setCursor(c = oc.Cursor.auto) {
            var p = this.getPanel();
            if (p == null)
                return;
            p.setCursor(c);
        }
        getSelectedItems() {
            return this.selectItems;
        }
        clearSelectedItems() {
            //this.canSelectItems = [];
            this.selectItems = [];
        }
        getSelectedItem() {
            if (this.selectItems.length <= 0)
                return null;
            return this.selectItems[this.selectItems.length - 1];
        }
        /**
         *
         */
        getDragOverSelItems() {
            return this.dragoverSelItems;
        }
        getCurMouseDownItem() {
            return this.curMouseDownItem;
        }
        getCurMouseOnItem() {
            return this.curMouseOnItem;
        }
        getCurMouseOnItems() {
            return this.curMouseOnItems;
        }
        isCurMouseOnItem(item) {
            if (item == null || this.curMouseOnItems == null || this.curMouseOnItems.length <= 0)
                return false;
            for (var di of this.curMouseOnItems) {
                if (di == item)
                    return true;
            }
            return false;
        }
        getSelectedItemDrag() {
            //return this.getSelectedItem()
            return this.selectedItemDrag;
        }
        findRelatedItemsByDrawPt(dxy, b_select = false) {
            var panel = this.drawPanel;
            if (panel == null)
                return null;
            var dl = this.getLayer();
            if (dl == null)
                return null;
            var items = dl.getItemsShow();
            if (items == null || items.length <= 0)
                return null;
            var r = [];
            for (var tmpi of items) {
                if (!tmpi.isVisiable())
                    continue;
                var b = false;
                if (b_select) {
                    b = tmpi.chkCanSelectDrawPt(dxy.x, dxy.y);
                }
                else {
                    b = tmpi.containDrawPt(dxy.x, dxy.y);
                    if (b) {
                        if (!tmpi.bMouseIn) {
                            tmpi.bMouseIn = true;
                            tmpi.on_mouse_in();
                        }
                    }
                    else {
                        if (tmpi.bMouseIn) {
                            tmpi.bMouseIn = false;
                            tmpi.on_mouse_out();
                        }
                    }
                }
                if (b)
                    r.push(tmpi);
            }
            if (r.length > 0)
                return r;
            else
                return null;
        }
        findCanSelectByDrawPt(dxy) {
            return this.findRelatedItemsByDrawPt(dxy, true);
        }
        on_select_single(pxy, dxy) {
            let can_selitems = this.findCanSelectByDrawPt(dxy);
            let curitem = this.getSelectedItem();
            if (can_selitems == null) {
                if (this.selectItems.length > 0) {
                    var oldsis = this.selectItems;
                    this.selectItems = []; //clear
                    //this.canSelectItems=[];
                    //if(this.drawPanel!=null)
                    //	this.drawPanel.update_draw();
                    //this.fireSelectedChged();
                    for (var si of oldsis)
                        si.on_selected(false);
                    this.drawPanel.MODEL_fireSelectedChged(this);
                }
            }
            else { //
                if (can_selitems.length == 1) {
                    if (curitem == can_selitems[0])
                        return;
                    var oldsis = this.selectItems;
                    this.selectItems = can_selitems;
                    //then trigger event
                    for (var si of oldsis)
                        si.on_selected(false);
                    can_selitems[0].on_selected(true);
                }
                else { //swift in can select items
                    if (this.selectItems.length == 1) {
                        var idx = can_selitems.indexOf(this.selectItems[0]);
                        if (idx < 0) {
                            var oldsi = this.selectItems[0];
                            this.selectItems[0] = can_selitems[0];
                            oldsi.on_selected(false);
                            this.selectItems[0].on_selected(true);
                        }
                        else {
                            idx++;
                            if (idx == can_selitems.length)
                                idx = 0;
                            var oldsi = this.selectItems[0];
                            this.selectItems[0] = can_selitems[idx];
                            oldsi.on_selected(false);
                            this.selectItems[0].on_selected(true);
                        }
                    }
                    else {
                        var tmpold = null;
                        if (this.selectItems.length > 0)
                            tmpold = this.selectItems[0];
                        this.selectItems[0] = can_selitems[0];
                        if (tmpold != null)
                            tmpold.on_selected(false);
                        this.selectItems[0].on_selected(true);
                    }
                }
                this.drawPanel.MODEL_fireSelectedChged(this);
            }
        }
        /**
         * set current selected item
         * @param item
         */
        setSelectedItem(item) {
            if (item == null)
                this.selectItems = []; //clear
            else
                this.selectItems = [item];
            this.drawPanel.MODEL_fireSelectedChged(this);
        }
        clearSelectedItem() {
            this.setSelectedItem(null);
        }
        MODEL_fireOperChged(oper) {
            this.drawPanel.MODEL_fireOperChged(this, oper);
        }
        on_mouse_down(pxy, dxy, me) {
            var canselitems = this.findCanSelectByDrawPt(dxy);
            if (me.button == oc.MOUSE_BTN.LEFT) { //
                if (canselitems != null && (canselitems === null || canselitems === void 0 ? void 0 : canselitems.length) == 1) { //only one item ,it can make selected
                    this.on_select_single(pxy, dxy);
                }
                else { //make up to descision
                    this.bsel_down = true;
                    this.bsel_down_mv = 0;
                }
            }
            if (canselitems != null) {
                this.curMouseDownItem = canselitems[canselitems.length - 1];
                var curselitem = this.getSelectedItem();
                if (curselitem != null && canselitems.length > 0 && this.isCurMouseOnItem(curselitem)) //curselitem == this.curMouseDownItem)
                 {
                    this.selectedItemDrag = curselitem;
                }
            }
            else {
                this.selectedItemDrag = null;
                this.curMouseDownItem = null;
            }
        }
        on_mouse_downlong(pxy, dxy, me) {
        }
        on_mouse_mv(pxy, dxy, me) {
            var canselitems = this.findCanSelectByDrawPt(dxy);
            this.curMouseOnItem = canselitems != null ? canselitems[canselitems.length - 1] : null;
            this.curMouseOnItems = canselitems;
            if (this.bsel_down) {
                this.bsel_down_mv++;
                //console.log("mouse mv--->");
            }
        }
        on_mouse_up(pxy, dxy, me) {
            if (this.bsel_down && this.bsel_down_mv < 2)
                this.on_select_single(pxy, dxy);
            this.bsel_down = false;
        }
        on_mouse_wheel(pxy, dxy, delta) {
        }
        on_mouse_dbclk(pxy, dxy, me) {
        }
        on_mouse_clk(pxy, dxy, me) {
        }
        on_mouse_drop(pxy, dxy, dd) {
        }
        on_mouse_dragover(pxy, dxy, dd) {
        }
        on_mouse_dragleave(pxy, dxy, dd) {
        }
        on_key_down(e) {
        }
        doCopyPaste(e) {
            if (this.copyPasteUrl == null)
                return;
            var sis = this.getSelectedItems();
            if (e.ctrlKey) {
                if (e.key == "c") { //copy
                    if (sis.length == 0)
                        return;
                    var cpob = [];
                    for (var si of sis) {
                        cpob.push(si.extract());
                    }
                    var pm = {};
                    pm["data_tp"] = "drawitems";
                    pm["op"] = "copy";
                    pm["items_json"] = JSON.stringify(cpob);
                    $.ajax({
                        type: 'post',
                        url: this.copyPasteUrl,
                        data: pm,
                        async: true,
                        success: function (result) {
                        }
                    });
                }
                else if (e.key == "v") { //paste
                    var pm = {};
                    pm["op"] = "paste";
                    $.ajax({
                        type: 'post',
                        url: this.copyPasteUrl,
                        data: pm,
                        async: true,
                        success: (result) => {
                            var ob = [];
                            eval("ob=" + result);
                            for (var o of ob) {
                                this.getLayer().copyByJSON(o);
                            }
                        }
                    });
                }
            }
        }
        on_key_press(e) {
        }
        on_key_up(e) {
        }
        on_key_event(tp, e) {
            //console.log("key code="+e.keyCode);
            var curoper = this.getCurOper();
            var opers = this.operStack;
            var bend = false;
            switch (tp) {
                case oc.KEY_EVT_TP.Down:
                    if (!curoper.maskInteractEvent())
                        this.on_key_down(e);
                    for (var i = opers.length - 1; i >= 0; i--) {
                        if (bend = !opers[i].on_key_down(e))
                            break;
                    }
                    break;
                case oc.KEY_EVT_TP.Press:
                    if (!curoper.maskInteractEvent())
                        this.on_key_press(e);
                    for (var i = opers.length - 1; i >= 0; i--) {
                        if (bend = !opers[i].on_key_press(e))
                            break;
                    }
                    break;
                case oc.KEY_EVT_TP.Up:
                    if (!curoper.maskInteractEvent())
                        this.on_key_up(e);
                    for (var i = opers.length - 1; i >= 0; i--) {
                        if (bend = !opers[i].on_key_up(e))
                            break;
                    }
                    break;
            }
        }
        on_mouse_event(tp, e) {
            if (this.drawPanel == null)
                return;
            var pxy = this.drawPanel.getEventPixel(e);
            var dxy = this.drawPanel.transPixelPt2DrawPt(pxy.x, pxy.y);
            //var opers = this.get_interact_opers();
            var opers = this.operStack;
            var curoper = this.getCurOper();
            var bend = false;
            var ritems = this.findRelatedItemsByDrawPt(dxy);
            if (ritems != null) {
                for (var tmpi of ritems) {
                    tmpi.on_mouse_event(tp, pxy, dxy, e);
                    tmpi.on_mouse_over(tp, pxy, dxy);
                }
            }
            else {
                this.getLayer().on_mouse_event(tp, pxy, dxy, e);
            }
            switch (tp) {
                case oc.MOUSE_EVT_TP.Down:
                    if (!curoper.maskInteractEvent())
                        this.on_mouse_down(pxy, dxy, e);
                    for (var i = opers.length - 1; i >= 0; i--) {
                        if (bend = !opers[i].on_mouse_down(pxy, dxy, e))
                            break;
                    }
                    break;
                case oc.MOUSE_EVT_TP.DownLong:
                    if (!curoper.maskInteractEvent())
                        this.on_mouse_downlong(pxy, dxy, e);
                    for (var i = opers.length - 1; i >= 0; i--) {
                        if (bend = !opers[i].on_mouse_downlong(pxy, dxy, e))
                            break;
                    }
                    break;
                case oc.MOUSE_EVT_TP.Move:
                    if (!curoper.maskInteractEvent())
                        this.on_mouse_mv(pxy, dxy, e);
                    for (var i = opers.length - 1; i >= 0; i--) {
                        if (bend = !opers[i].on_mouse_mv(pxy, dxy, e))
                            break;
                    }
                    break;
                case oc.MOUSE_EVT_TP.Up:
                    if (!curoper.maskInteractEvent())
                        this.on_mouse_up(pxy, dxy, e);
                    for (var i = opers.length - 1; i >= 0; i--) {
                        if (bend = !opers[i].on_mouse_up(pxy, dxy, e))
                            break;
                    }
                    break;
                case oc.MOUSE_EVT_TP.DbClk:
                    if (!curoper.maskInteractEvent())
                        this.on_mouse_dbclk(pxy, dxy, e);
                    for (var i = opers.length - 1; i >= 0; i--) {
                        if (bend = !opers[i].on_mouse_dbclk(pxy, dxy, e))
                            break;
                    }
                    break;
                case oc.MOUSE_EVT_TP.Clk:
                    if (!curoper.maskInteractEvent())
                        this.on_mouse_clk(pxy, dxy, e);
                    for (var i = opers.length - 1; i >= 0; i--) {
                        if (bend = !opers[i].on_mouse_clk(pxy, dxy, e))
                            break;
                    }
                    break;
                case oc.MOUSE_EVT_TP.Wheel:
                    var delta = 0;
                    if (!e)
                        e = window.event;
                    if (e.wheelDelta) {
                        delta = e.wheelDelta / 120;
                        if (window["opera"])
                            delta = -delta;
                    }
                    else if (e.detail) { //
                        delta = -e.detail / 3;
                    }
                    if (!curoper.maskInteractEvent())
                        this.on_mouse_wheel(pxy, dxy, delta);
                    for (var i = opers.length - 1; i >= 0; i--) {
                        if (bend = !opers[i].on_mouse_wheel(pxy, dxy, delta))
                            break;
                    }
                    break;
                case oc.MOUSE_EVT_TP.DragOver: //cannot getData ,only data name list
                    var pvs = oc.util.getDragEventData(e);
                    var val = pvs["_val"]; //e.dataTransfer.getData("_val");
                    var dtp = pvs["_tp"]; //e.dataTransfer.getData("_tp");
                    var g = pvs["_g"];
                    //console.log(e.dataTransfer.types);
                    if (dtp == null || dtp == undefined || dtp == "")
                        break;
                    var dd = { _tp: dtp, _val: val, _g: g };
                    if (!curoper.maskInteractEvent())
                        this.on_mouse_dragover(pxy, dxy, dd);
                    for (var i = opers.length - 1; i >= 0; i--) {
                        if (bend = !opers[i].on_mouse_dragover(pxy, dxy, dd))
                            break;
                    }
                    break;
                case oc.MOUSE_EVT_TP.DragLeave: //cannot getData ,only data name list
                    var pvs = oc.util.getDragEventData(e);
                    var val = pvs["_val"]; //e.dataTransfer.getData("_val");
                    var dtp = pvs["_tp"]; //e.dataTransfer.getData("_tp");
                    var g = pvs["_g"];
                    //console.log(e.dataTransfer.types);
                    if (dtp == null || dtp == undefined || dtp == "")
                        break;
                    var dd = { _tp: dtp, _val: val, _g: g };
                    if (!curoper.maskInteractEvent())
                        this.on_mouse_dragleave(pxy, dxy, dd);
                    for (var i = opers.length - 1; i >= 0; i--) {
                        if (bend = !opers[i].on_mouse_dragleave(pxy, dxy, dd))
                            break;
                    }
                    break;
                case oc.MOUSE_EVT_TP.Drop:
                    var pvs = oc.util.getDragEventData(e);
                    var val = pvs["_val"]; //e.dataTransfer.getData("_val");
                    var dtp = pvs["_tp"]; //e.dataTransfer.getData("_tp");
                    var g = pvs["_g"];
                    if (dtp == null || dtp == undefined || dtp == "")
                        break;
                    var dd = { _tp: dtp, _val: val, _g: g };
                    if (!curoper.maskInteractEvent())
                        this.on_mouse_drop(pxy, dxy, dd);
                    for (var i = opers.length - 1; i >= 0; i--) {
                        if (bend = !opers[i].on_mouse_drop(pxy, dxy, dd))
                            break;
                    }
                    break;
            }
        }
    }
    oc.DrawInteract = DrawInteract;
    /**
     * only for drawitems to show and response to hmi event
     * 1) it has no selection
     * 2) trick mouse event
     */
    class DrawInteractShow extends DrawInteract {
    }
    oc.DrawInteractShow = DrawInteractShow;
})(oc || (oc = {}));
//------------------------------------------
// items collection
var oc;
(function (oc) {
    /**
     * items container ,which make subitems draw fit in it's rect
     *   width or height may be stretched
     */
    class DrawItems extends oc.DrawItemRectR {
        //private xy_res:XYRes|null =null;
        constructor(opts) {
            super(opts);
            this.items = []; //sub items
            this.itemsRect = null;
            this.innerCont = null;
        }
        getClassName() {
            return "DrawItems";
        }
        getItemsShow() {
            return this.items;
        }
        getItemByIdx(i) {
            if (this.items == null || this.items.length <= 0 || i >= this.items.length)
                return null;
            return this.items[i];
        }
        removeItem(item) {
            if (this.items == null || this.items.length <= 0)
                return false;
            var i;
            for (i = 0; i < this.items.length; i++) {
                if (this.items[i] == item)
                    break;
            }
            if (i >= this.items.length)
                return false;
            this.items.splice(i, 1);
            item.parentNode = null;
            this.MODEL_fireChged([]);
            return true;
        }
        // public getBoundRectDraw()
        // {//override
        // 	var pt = this.getDrawXY();
        // 	return new oc.base.Rect(pt.x, pt.y, this.w, this.h);
        // }
        getInnerContainer() {
            //return this.getContainer();
            if (this.innerCont != null)
                return this.innerCont;
            var pc = this.getContainer();
            if (pc == null)
                return null;
            this.innerCont = new oc.ItemsContainer(this, pc, this);
            return this.innerCont;
        }
        // private recurseSetLayer(disob: DrawItems, cont:IDrawItemContainer,lay: DrawLayer)
        // {//
        // 	disob.setContainerLayer(cont,lay);
        // 	for (var i = 0; i < disob.items.length; i++)
        // 	{
        // 		var it = disob.items[i];
        // 		it.drawLayer = lay;
        // 		it.container = disob;//container changed
        // 		it.setContainerLayer(disob,lay);
        // 		if (it.getClassName() == 'DrawItems')
        // 		{
        // 			this.recurseSetLayer(it as DrawItems,disob, lay);
        // 		}
        // 	}
        // }
        // public setContainerLayer(cont:IDrawItemContainer,lay: DrawLayer)
        // {
        // 	this.recurseSetLayer(this,cont, lay);
        // }
        inject(opts, ignore_readonly) {
            super.inject(opts, ignore_readonly);
            //if(typeof(opts)=='string')
            //	eval("opts="+opts) ;
            if (opts.items) {
                for (var i = 0; i < opts.items.length; i++) {
                    var it = opts.items[i];
                    var cn = it._cn;
                    if (!cn)
                        continue;
                    var item = oc.DrawItem.createByClassName(cn, undefined);
                    //console.log("cn="+cn+"  item="+item);
                    if (item == null)
                        continue;
                    item.inject(it, ignore_readonly);
                    //out set layer
                    //item.setContainerLayer(this,;
                    this.items.push(item);
                    item.parentNode = this;
                }
                //this.innerCont.
                var c = this.getInnerContainer();
                if (c != null)
                    c.notifyItemsChg();
            }
        }
        extract() {
            var r = super.extract();
            var rs = r["items"] = [];
            for (var i = 0; i < this.items.length; i++) {
                r.items.push(this.items[i].extract());
            }
            return r;
        }
        addItem(item) {
            this.items.push(item);
            item.parentNode = this;
            var lay = this.getLayer();
            if (lay == null)
                return;
            var ic = this.getInnerContainer();
            if (ic == null)
                return;
            item.setContainer(ic, lay);
            ic.notifyItemsChg();
            // if (item.getClassName() == 'DrawItems')
            // 	(<DrawItems>item).setLayer(lay);
            // else
            // 	item.drawLayer = lay;
            this.MODEL_fireChged([]);
        }
        setItems(items) {
            var lay = this.getLayer();
            var ic = this.getInnerContainer();
            this.items = [];
            for (var m of items) {
                this.items.push(m);
                m.parentNode = this;
                if (ic != null && lay != null)
                    m.setContainer(ic, lay);
            }
            if (ic != null)
                ic.notifyItemsChg();
            this.MODEL_fireChged([]);
        }
        on_mouse_event(tp, pxy, dxy, e) {
            var ic = this.getInnerContainer();
            if (ic == null)
                return;
            var sitems = this.getItemsShow();
            if (sitems == null || sitems.length <= 0)
                return;
            for (var si of sitems) {
                var bc = si.containDrawPt(dxy.x, dxy.y);
                if (bc)
                    si.on_mouse_event(tp, pxy, dxy, e);
            }
        }
        getPrimRect() {
            var ic = this.getInnerContainer();
            if (ic == null)
                return null;
            var r = oc.ItemsContainer.calcRect(this.getItemsShow());
            if (r == null)
                return null;
            var p = ic.transDrawPt2PixelPt(r.x, r.y);
            var w = ic.transDrawLen2PixelLen(true, r.w);
            var h = ic.transDrawLen2PixelLen(false, r.h);
            return new oc.base.Rect(0, 0, w, h);
            //return r ;
        }
        drawPrim(cxt) {
            var ic = this.getInnerContainer();
            if (ic == null)
                return;
            cxt.save();
            var p = this.getPixelXY();
            if (p != null) //what the fuck
                cxt.translate(-p.x, -p.y);
            for (var item of this.items) {
                item.draw(cxt, ic);
            }
            cxt.restore();
            //
            if (this.items.length <= 0) {
                var r = this.getPrimRect();
                var c = this.getContainer();
                if (r != null && c != null) {
                    var pt = c.transDrawPt2PixelPt(r.x, r.y);
                    var w = c.transDrawLen2PixelLen(true, r.w);
                    var h = c.transDrawLen2PixelLen(false, r.h);
                    oc.util.drawRect(cxt, pt.x, pt.y, w, h, null, null, 1, "blue");
                }
            }
        }
        drawPrimSel(ctx) {
        }
    }
    oc.DrawItems = DrawItems;
})(oc || (oc = {}));
/**
 * @module oc/DrawLayer
 */
var oc;
(function (oc) {
    class DrawLayer {
        constructor(opts) {
            //style="position: absolute; left: 0; top: 0; z-index: 0;"
            this.drawPanel = null;
            this.id = "";
            this.name = "";
            this.title = "";
            this.items = [];
            this.drawRes = null;
            this.cur_dbuf_idx = 0;
            this.bVis = true;
            var cxt0 = document.createElement('canvas').getContext('2d');
            var canvs0 = cxt0.canvas;
            // if(parentele)
            // 	parentele.appendChild(canvs0)
            // var cxt1 = document.createElement('canvas').getContext('2d') as CanvasRenderingContext2D;
            // var canvs1 = cxt1.canvas;
            // this.context=[cxt0,cxt1];
            // this.canvas=[canvs0,canvs1];
            this.context = [cxt0];
            this.canvas = [canvs0];
            for (var c of this.canvas) {
                let can = $(canvs0);
                can.css("position", "relative");
                can.css("border", "solid 1px");
                can.css("left", "0px");
                can.css("top", "0px");
                can.css("width", "100%");
                can.css("height", "100%");
                can.css("display", "none");
            }
            $(this.canvas[this.cur_dbuf_idx]).css("display", "");
            //this.switchCxtFrontBack();
            if (opts != undefined) {
                if (typeof (opts) == 'string')
                    this.name = arguments[0];
            }
            if (this.id == null || this.id == '' || this.id == undefined || this.id == 'undefined')
                this.id = oc.util.create_new_tmp_id();
        }
        getId() {
            return this.id;
        }
        getName() {
            return this.name;
        }
        getTitle() {
            return this.title;
        }
        setName(n) {
            this.name = n;
        }
        getCanvasEles() {
            return this.canvas;
        }
        getCanvasCxts() {
            return this.context;
        }
        getCxtFront() {
            return this.context[this.cur_dbuf_idx];
        }
        getCxtBack() {
            var i = this.cur_dbuf_idx == 0 ? 1 : 0;
            return this.context[i];
        }
        switchCxtFrontBack() {
            $(this.canvas[this.cur_dbuf_idx]).css("display", "none");
            this.cur_dbuf_idx = this.cur_dbuf_idx == 0 ? 1 : 0;
            $(this.canvas[this.cur_dbuf_idx]).css("display", "");
        }
        getCxtCurDraw() {
            return this.getCxtFront();
        }
        getDrawRes() {
            return this.drawRes;
        }
        setDrawRes(dr) {
            this.drawRes = dr;
        }
        getItemByName(n) {
            for (var item of this.getItemsShow()) {
                if (item.getName() == n)
                    return item;
            }
            return null;
        }
        getItemsByGroupName(gn) {
            var r = [];
            for (var item of this.getItemsShow()) {
                if (item.getGroupName() == gn)
                    r.push(item);
            }
            return r;
        }
        setDynData(dyn) {
            var tmpis = [];
            for (var n in dyn) {
                var item = this.getItemByName(n);
                if (item == null)
                    continue;
                item.setDynData(dyn[n], false);
                tmpis.push(item);
            }
            this.MODEL_fireChged(tmpis, []);
        }
        fireTick() {
            for (var di of this.items) {
                di.on_tick();
            }
        }
        inject(opts, mark) {
            if (typeof (opts) == 'string')
                eval("opts=" + opts);
            this.id = opts["id"] ? opts["id"] : oc.util.create_new_tmp_id();
            this.title = opts["title"] ? opts["title"] : "";
            //alert(JSON.stringify(opts));
            //alert(opts.name);
            var n = opts["name"];
            if (n)
                this.name = n;
            this.bVis = (opts["vis"] != false);
            var dis = opts["dis"];
            if (dis) {
                for (var i = 0; i < dis.length; i++) {
                    var it = dis[i];
                    var cn = it._cn;
                    if (!cn)
                        continue;
                    var item = oc.DrawItem.createByClassName(cn, undefined);
                    //eval("item=new oc.di."+cn+"()") ;
                    //console.log("cn="+cn+"  item="+item);
                    if (item == null)
                        continue;
                    item.inject(it, false);
                    if (mark !== undefined)
                        item.setMark(mark);
                    this.addItem(item);
                }
            }
        }
        extract(mark) {
            var r = {};
            r["id"] = this.id;
            r["name"] = this.name;
            r["title"] = this.title;
            r["bvis"] = this.bVis;
            var rdis = r["dis"] = [];
            for (var item of this.items) {
                if (item.isVirtual())
                    continue;
                //console.log(item.getMark());
                if (mark !== undefined && item.getMark() !== mark)
                    continue;
                //console.log("extract---");
                rdis.push(item.extract());
            }
            return r;
        }
        setItemsWithMark(items, mark) {
            for (var item of items) {
                item.setMark(mark);
                this.addItem(item);
            }
        }
        /**
         * filter list item by mark
         * @param m
         */
        listItemsByMark(m) {
            var r = [];
            for (var item of this.items) {
                if (item.getMark() == m)
                    r.push(item);
            }
            return r;
        }
        getPanel() {
            return this.drawPanel;
        }
        setPanel(p) {
            this.drawPanel = p;
            for (var item of this.items) {
                item.setContainer(this, this);
            }
        }
        getItemById(id) {
            for (var i = 0; i < this.items.length; i++) {
                if (this.items[i].id == id)
                    return this.items[i];
            }
            return null;
        }
        addItem(item) {
            if (this.drawPanel != null)
                item.setContainer(this, this);
            this.items.push(item);
            // if (item.getClassName() == 'DrawItems')
            // 	(<DrawItems>item).setLayer(this);
            // else
            // 	item.drawLayer = this;
            this.MODEL_fireChged(item, []);
        }
        setItems(items) {
            for (var m of items) {
                m.setContainer(this, this);
                this.items.push(m);
            }
            this.MODEL_fireChged(items, []);
        }
        copyItem(itemid) {
            var item = this.getItemById(itemid);
            if (item == null)
                return null;
            var ps = item.extract();
            return this.copyByJSON(ps);
        }
        copyByJSON(json) {
            if (typeof (json) == 'string')
                eval("json=" + json);
            var cn = json["_cn"];
            if (!cn)
                return null;
            var r = oc.DrawItem.createByClassName(cn, undefined);
            if (r == null)
                return null;
            r.inject(json, false);
            r.setId(oc.util.create_new_tmp_id());
            r.setDrawXY(0, 0);
            this.addItem(r);
            this.MODEL_fireChged(r, null);
            return r;
        }
        vis(v) {
            if (v == undefined)
                return this.bVis;
            this.bVis = v;
        }
        MODEL_fireChged(item, prop_names) {
            if (this.drawPanel == null)
                return;
            if (this.drawPanel instanceof oc.DrawPanel)
                this.drawPanel.MODEL_fireChged(this, item, prop_names);
        }
        //ui suport
        getRelatedInteract() {
            if (this.drawPanel == null)
                return null;
            var inta = this.drawPanel.getInteract();
            if (inta == null)
                return null;
            if (inta.getLayer() == this)
                return inta;
            return null;
        }
        //public 
        clear_draw() {
            if (this.drawPanel == null)
                return;
            var pixsz = this.drawPanel.getPixelSize();
            var cxt = this.getCxtCurDraw();
            cxt.clearRect(0, 0, pixsz.w, pixsz.h);
        }
        update_draw() {
            this.clear_draw();
            this.on_draw();
        }
        on_draw() {
            if (this.drawPanel == null)
                return;
            var inta = this.getRelatedInteract();
            var cxt = this.getCxtCurDraw();
            if (inta != null) { // draw bk coordination,or rule
                var dxy = this.drawPanel.transDrawPt2PixelPt(0, 0);
                var sz = this.drawPanel.getPixelSize();
                var w = sz.w;
                var h = sz.h;
                cxt.save();
                cxt.lineWidth = 1;
                cxt.beginPath();
                cxt.strokeStyle = "#4c4f51";
                cxt.moveTo(0, dxy.y);
                cxt.lineTo(w, dxy.y);
                cxt.moveTo(dxy.x, 0);
                cxt.lineTo(dxy.x, h);
                cxt.stroke();
                cxt.restore();
            }
            //draw items
            for (var item of this.items) {
                if (!item.isVisiable())
                    continue;
                if (item.isHidden()) {
                    item.draw_hidden(cxt, this);
                    continue;
                }
                item.draw(cxt, this);
            }
            //draw selected and current oper
            var inta = this.getRelatedInteract();
            if (inta != null) {
                var sitems = inta.getSelectedItems();
                for (var si of sitems) {
                    si.draw_sel(cxt, this, "red");
                }
                sitems = inta.getDragOverSelItems();
                for (var si of sitems) {
                    si.draw_sel(cxt, this, "yellow");
                }
                inta.getCurOper().draw();
            }
            //this.switchCxtFrontBack();
        }
        getShowItemsRect() {
            return oc.ItemsContainer.calcRect(this.getItemsShow());
        }
        ajustDrawFit() {
            var p = this.getPanel();
            if (p == null)
                return;
            var r = oc.ItemsContainer.calcRect(this.getItemsShow());
            if (r == null) {
                p.ajustDrawToInit();
                return;
            }
            p.ajustDrawFitInRect(r);
        }
        getXYResolution() {
            if (this.drawPanel == null)
                throw new Error("no DrawPanel set.");
            return this.drawPanel.getXYResolution();
        }
        transPixelPt2DrawPt(px, py) {
            if (this.drawPanel == null)
                throw new Error("no DrawPanel set.");
            return this.drawPanel.transPixelPt2DrawPt(px, py);
        }
        transDrawPt2PixelPt(dx, dy) {
            if (this.drawPanel == null)
                throw new Error("no DrawPanel set.");
            return this.drawPanel.transDrawPt2PixelPt(dx, dy);
        }
        transDrawLen2PixelLen(b_xres, len) {
            if (this.drawPanel == null)
                throw new Error("no DrawPanel set.");
            return this.drawPanel.transDrawLen2PixelLen(b_xres, len);
        }
        transPixelLen2DrawLen(b_xres, len) {
            if (this.drawPanel == null)
                throw new Error("no DrawPanel set.");
            return this.drawPanel.transPixelLen2DrawLen(b_xres, len);
        }
        notifyItemsChg() {
        }
        getItemsAll() {
            return this.items;
        }
        getItemsShow() {
            var r = [];
            for (var tmpi of this.items) {
                if (tmpi.isHidden())
                    continue;
                r.push(tmpi);
            }
            return r;
        }
        getItemByIdx(i) {
            if (this.items == null || this.items.length <= 0 || i >= this.items.length)
                return null;
            return this.items[i];
        }
        removeItem(item) {
            if (this.items == null || this.items.length <= 0)
                return false;
            var i;
            for (i = 0; i < this.items.length; i++) {
                if (this.items[i] == item)
                    break;
            }
            if (i >= this.items.length)
                return false;
            this.items.splice(i, 1);
            this.MODEL_fireChged(null, null);
            return true;
        }
        /**
         * for override using
         * @param tp
         * @param pxy
         * @param dxy
         * @param e
         */
        on_mouse_event(tp, pxy, dxy, e) {
        }
    }
    oc.DrawLayer = DrawLayer;
})(oc || (oc = {}));
var oc;
(function (oc) {
    class DrawModel {
    }
    oc.DrawModel = DrawModel;
    class DrawCtrl {
    }
    oc.DrawCtrl = DrawCtrl;
    class DrawView {
        //ctrl:DrawCtrl ;
        constructor(model, panel) {
            // this.repId = repid ;
            // this.hmiId = hmiid ;
            this.model = model;
            this.panel = panel;
            //this.ctrl = ctrl;
            this.panel.drawView = this;
        }
        getModel() {
            return this.model;
        }
        getPanel() {
            return this.panel;
        }
        fireEventToServer(diid, eventn, eventv) {
            var msg = { tp: "event", diid: diid, name: eventn, val: eventv };
            this.sendMsgToServer(JSON.stringify(msg));
        }
        comp_fire_event_to_server(comp, eventn, eventv) {
            var msg = {};
            msg["repid"] = null;
        }
    }
    oc.DrawView = DrawView;
})(oc || (oc = {}));
var oc;
(function (oc) {
    class DrawOper {
        constructor(interact, layer) {
            //this.name = name;
            this.belongTo = interact;
            this.drawLayer = layer; //tmp
        }
        getInteract() {
            return this.belongTo;
        }
        getDrawPanel() {
            return this.belongTo.getPanel();
        }
        setCursor(c = oc.Cursor.auto) {
            var p = this.belongTo.getPanel();
            if (p == null)
                return;
            p.setCursor(c);
        }
        /**
         * mask interact event。
         * true - cur oper can mask interact event
         */
        maskInteractEvent() {
            return false;
        }
        getDrawLayer() {
            return this.drawLayer;
        }
        pushOperStack(oper) {
            if (oper == this.belongTo.getCurOper())
                return; //
            this.belongTo.pushOperStack(oper);
        }
        popOperStackMe() {
            if (this != this.belongTo.getCurOper())
                return;
            return this.belongTo.popOperStack();
        }
        MODEL_fireOperChged() {
            this.belongTo.MODEL_fireOperChged(this);
        }
        /**
         *
         * @param pxy
         * @param dxy
         * @returns true make event transfer to next stack oper，and end to interact
         *    false will stop event
         */
        on_mouse_down(pxy, dxy, me) {
            return true;
        }
        on_mouse_downlong(pxy, dxy, me) {
            return true;
        }
        on_mouse_mv(pxy, dxy, me) {
            return true;
        }
        on_mouse_up(pxy, dxy, me) {
            return true;
        }
        on_mouse_dbclk(pxy, dxy, me) {
            return true;
        }
        on_mouse_clk(pxy, dxy, me) {
            return true;
        }
        on_mouse_wheel(pxy, dxy, delta) {
            return true;
        }
        on_mouse_dragover(pxy, dxy, dd) {
            return true;
        }
        on_mouse_dragleave(pxy, dxy, dd) {
            return true;
        }
        on_mouse_drop(pxy, dxy, dd) {
            return true;
        }
        on_key_down(e) {
            return true;
        }
        on_key_up(e) {
            return true;
        }
        on_key_press(e) {
            return true;
        }
        draw() {
            this.draw_oper();
        }
    }
    oc.DrawOper = DrawOper;
    class DrawOperDrag extends DrawOper {
        constructor(interact, layer) {
            super(interact, layer);
            this.downPt = null;
            this.itemDrag = null;
            this.itemDragPt = null;
        }
        getOperName() {
            return "drag";
        }
        on_oper_stack_push() {
        }
        on_oper_stack_pop() {
        }
        chkOperFitByDrawPt(pxy, dxy) {
            return false;
        }
        // public on_mouse_dbclk(pxy:base.Pt,dxy:base.Pt):boolean
        // {
        // 	this.downPt = null;
        // 	//if(this.itemDrag!=null)
        // 	this.itemDrag = null;
        // 	this.setCursor(undefined);
        // 	return true;
        // }
        on_mouse_down(pxy, dxy, me) {
            if (me.button != 0) //left btn
                return true;
            this.downPt = dxy;
            var inter = this.getInteract();
            //var cur_moused_item = inter.getCurMouseOnItem() ;
            this.itemDrag = this.getInteract().getSelectedItemDrag();
            if (this.itemDrag != null && inter.isCurMouseOnItem(this.itemDrag)) {
                this.itemDragPt = { x: this.itemDrag.x, y: this.itemDrag.y };
                this.setCursor(oc.Cursor.move);
                return false;
            }
            //if(cur_moused_item!=this.itemDrag)
            //	this.itemDrag = null ;
            // if (this.itemDrag != null)
            // {
            // 	this.itemDragPt = { x: this.itemDrag.x, y: this.itemDrag.y };
            // 	this.setCursor(Cursor.move);
            // 	return false;
            // }
            return true;
        }
        on_mouse_mv(pxy, dxy, me) {
            var p = this.getDrawPanel();
            if (p == null)
                return true;
            var inter = this.getInteract();
            if (this.itemDrag == null && this.downPt == null) { //normal mv
                var onitems = inter.getCurMouseOnItems();
                if (onitems != null && onitems.length > 0) {
                    var selitem = inter.getSelectedItemDrag();
                    if (selitem != null && inter.isCurMouseOnItem(selitem))
                        this.setCursor(oc.Cursor.move);
                    else
                        this.setCursor(oc.Cursor.crosshair);
                }
                else {
                    this.setCursor(undefined);
                }
            }
            if (this.downPt == null)
                return true;
            //	console.log("mv item drag="+this.itemDrag)
            if (this.itemDrag == null) { //draw panel
                //console.log("delta_y="+(this.downPt.y-dxy.y));
                p.moveDrawCenter(this.downPt.x - dxy.x, this.downPt.y - dxy.y);
            }
            else {
                if (this.itemDragPt != null) {
                    //console.log(this.itemDrag.id+" is dragged...")
                    this.itemDrag.setDrawXY(this.itemDragPt.x + (dxy.x - this.downPt.x), this.itemDragPt.y + (dxy.y - this.downPt.y));
                    return false;
                }
            }
            return true;
        }
        on_mouse_up(pxy, dxy, me) {
            this.downPt = null;
            //if(this.itemDrag!=null)
            this.itemDrag = null;
            this.setCursor(undefined);
            return true;
        }
        on_mouse_wheel(pxy, dxy, delta) {
            var p = this.getDrawPanel();
            if (p == null)
                return true;
            p.ajustDrawResolution(dxy.x, dxy.y, delta);
            return true;
        }
        draw_oper() {
        }
    }
    oc.DrawOperDrag = DrawOperDrag;
    class DrawOperShowOnly extends DrawOper {
        //itemDrag: DrawItem | null = null;
        //itemDragPt: base.Pt | null = null;
        constructor(interact, layer) {
            super(interact, layer);
            this.downPt = null;
        }
        getOperName() {
            return "show_only";
        }
        on_oper_stack_push() {
        }
        on_oper_stack_pop() {
        }
        chkOperFitByDrawPt(pxy, dxy) {
            return false;
        }
        // public on_mouse_dbclk(pxy:base.Pt,dxy:base.Pt):boolean
        // {
        // 	this.downPt = null;
        // 	//if(this.itemDrag!=null)
        // 	this.itemDrag = null;
        // 	this.setCursor(undefined);
        // 	return true;
        // }
        on_mouse_down(pxy, dxy, me) {
            if (me.button != 0) //left btn
                return true;
            this.downPt = dxy;
            var inter = this.getInteract();
            //var cur_moused_item = inter.getCurMouseOnItem() ;
            return true;
        }
        on_mouse_mv(pxy, dxy, me) {
            var p = this.getDrawPanel();
            if (p == null)
                return true;
            var inter = this.getInteract();
            if (this.downPt == null)
                return true;
            p.moveDrawCenter(this.downPt.x - dxy.x, this.downPt.y - dxy.y);
            return true;
        }
        on_mouse_up(pxy, dxy, me) {
            this.downPt = null;
            this.setCursor(undefined);
            return true;
        }
        on_mouse_wheel(pxy, dxy, delta) {
            var p = this.getDrawPanel();
            if (p == null)
                return true;
            p.ajustDrawResolution(dxy.x, dxy.y, delta);
            return true;
        }
        draw_oper() {
        }
    }
    oc.DrawOperShowOnly = DrawOperShowOnly;
})(oc || (oc = {}));
/**
 * @module ol/DrawPanel
 */
var oc;
(function (oc) {
    class AbstractDrawPanel {
        constructor() {
            this.drawView = null;
            this.layers = [];
            this.drawCenter = { x: 0.0, y: 0.0 };
            this.drawResolution = 1.0; //分辨率  1/放大倍数
            this.pixelSize = { w: 100, h: 100 };
        }
        getDrawView() {
            return this.drawView;
        }
        on_draw() {
            for (var i = 0; i < this.layers.length; i++) {
                if (!this.layers[i].vis(undefined))
                    continue;
                //this.layers[i].on_draw();
                this.layers[i].update_draw();
            }
        }
        clear_draw() {
            for (var i = 0; i < this.layers.length; i++) {
                var cxt = this.layers[i].clear_draw();
            }
        }
        update_draw() {
            this.clear_draw();
            this.on_draw();
        }
        setPixelSize(sz) {
            this.pixelSize = sz;
            for (var i = 0; i < this.layers.length; i++) {
                var c = this.layers[i].getCanvasEles();
                c[0].setAttribute("width", "" + sz.w);
                c[0].setAttribute("height", "" + sz.h);
                //c[1].setAttribute("width", "" + sz.w);
                //c[1].setAttribute("height", "" + sz.h);
            }
            //console.log(sz);
            this.update_draw();
        }
        getPixelSize() {
            return this.pixelSize;
        }
        updatePixelSize() {
            this.setPixelSize(this.calcPixelSize());
        }
        calcPixelSize() {
            var tarele = this.getHTMLElement();
            var computedStyle = getComputedStyle(tarele);
            var w = tarele.offsetWidth -
                this.parseToFloat(computedStyle.borderLeftWidth) -
                this.parseToFloat(computedStyle.paddingLeft) -
                this.parseToFloat(computedStyle.paddingRight) -
                this.parseToFloat(computedStyle.borderRightWidth);
            var h = tarele.offsetHeight -
                this.parseToFloat(computedStyle.borderTopWidth) -
                this.parseToFloat(computedStyle.paddingTop) -
                this.parseToFloat(computedStyle.paddingBottom) -
                this.parseToFloat(computedStyle.borderBottomWidth);
            if (w == NaN) {
                console.log("cal pix size err");
            }
            return { w: w, h: h };
        }
        addLayer(lay) {
            this.layers.push(lay);
            lay.setPanel(this);
            var cans = lay.getCanvasEles();
            this.getHTMLElement().appendChild(cans[0]);
            //this.tarEle.appendChild(cans[1]);
            var cxt = lay.getCanvasCxts();
            var pixsz = this.getPixelSize();
            //var c = lay.getCanvasEle();
            cans[0].setAttribute("width", "" + pixsz.w);
            cans[0].setAttribute("height", "" + pixsz.h);
            cans[0].setAttribute("tabindex", "1"); //enable key event
            cxt[0].fillStyle = "rgba(0,0,0,0.0)";
            cxt[0].fillRect(0, 0, pixsz.w, pixsz.w);
            this.update_draw();
        }
        getLayerByName(n) {
            for (var i = 0; i < this.layers.length; i++) {
                if (this.layers[i].name == n)
                    return this.layers[i];
            }
            return null;
        }
        getLayer() {
            return this.layers[0];
        }
        getPixelCenter() {
            var ps = this.getPixelSize();
            return { x: ps.w / 2, y: ps.h / 2 };
        }
        getDrawCenter() {
            return this.drawCenter;
        }
        getDrawResolution() {
            return this.drawResolution;
        }
        setDrawCenter(x, y) {
            this.drawCenter.x = x;
            this.drawCenter.y = y;
        }
        moveDrawCenter(deta_x, deta_y) {
            this.drawCenter.x += deta_x;
            this.drawCenter.y += deta_y;
            this.update_draw();
        }
        movePixelCenter(deta_x, deta_y) {
            var dx = this.transPixelLen2DrawLen(true, deta_x);
            var dy = this.transPixelLen2DrawLen(true, deta_y);
            this.moveDrawCenter(dx, dy);
        }
        ajustDrawResolution(dx, dy, delta) {
            var pix_pt = this.transDrawPt2PixelPt(dx, dy);
            if (delta > 0) {
                this.drawResolution *= 1.2;
            }
            else {
                this.drawResolution /= 1.2;
            }
            var newdpt = this.transPixelPt2DrawPt(pix_pt.x, pix_pt.y);
            this.drawCenter.x += (dx - newdpt.x);
            this.drawCenter.y += (dy - newdpt.y);
            this.update_draw();
        }
        ajustDrawFitInRect(rect) {
            this.drawCenter = rect.getCenter();
            var ps = this.getPixelSize();
            var res = rect.w / ps.w;
            var res2 = rect.h / ps.h;
            this.drawResolution = Math.max(res, res2);
            this.update_draw();
        }
        ajustDrawToInit() {
            this.drawCenter.x = 0;
            this.drawCenter.y = 0;
            this.drawResolution = 1;
            this.update_draw();
        }
        getXYResolution() {
            return { x_res: this.drawResolution, y_res: this.drawResolution };
        }
        transPixelPt2DrawPt(px, py) {
            var pc = this.getPixelCenter();
            var dx = (px - pc.x) * this.drawResolution + this.drawCenter.x;
            //var dy = (py-pc.y)*this.drawResolution+this.drawCenter.y;
            var dy = this.drawCenter.y - (pc.y - py) * this.drawResolution;
            //var dy=(this.getPixelHeight()/2-py)*this.drawResolution+this.drawCenter.y;
            return { x: dx, y: dy };
        }
        transDrawPt2PixelPt(dx, dy) {
            var pc = this.getPixelCenter();
            var px = (dx - this.drawCenter.x) / this.drawResolution + pc.x;
            //var py = (dx-this.drawCenter.y)/this.drawResolution+pc.y;
            var py = pc.y - (this.drawCenter.y - dy) / this.drawResolution;
            //var py=this.getPixelHeight()/2-(dy-this.drawCenter.y)/this.drawResolution;
            px = Math.round(px);
            py = Math.round(py);
            return { x: px, y: py };
        }
        transDrawLen2PixelLen(b_xres, len) {
            return Math.round(len / this.drawResolution);
        }
        transPixelLen2DrawLen(b_xres, len) {
            return len * this.drawResolution;
        }
        parseToFloat(strv) {
            if (strv == null)
                return 0;
            return parseFloat(strv);
        }
        getEventPixel(e) {
            var r = this.getHTMLElement().getBoundingClientRect();
            return { x: e.clientX - r.left, y: e.clientY - r.top };
        }
    }
    oc.AbstractDrawPanel = AbstractDrawPanel;
    class DrawPanel extends AbstractDrawPanel {
        constructor(target, opts) {
            super();
            this.popMenuEle = null;
            this.interact = null;
            this.modelListeners = [];
            this.drawRes = null;
            this.on_mouse_mv = null;
            this.on_item_sel_chg = null;
            this.on_model_chg = null;
            this.bModelDirty = false;
            this.timeoutDownLong = null;
            if (!opts)
                opts = {};
            this.on_mouse_mv = opts["on_mouse_mv"] ? opts["on_mouse_mv"] : null;
            this.on_item_sel_chg = opts["on_item_sel_chg"] ? opts["on_item_sel_chg"] : null;
            this.on_model_chg = opts["on_model_chg"] ? opts["on_model_chg"] : null;
            if (typeof (target) == "string")
                this.tarEle = document.getElementById(target);
            else
                this.tarEle = target;
            //this.tarEle.getContext()
            this.layers = (opts["layers"] ? opts["layers"] : []);
            if (this.layers == null)
                this.layers = [];
            this.interact = null; //[new oc.interact.OperDrag()];
            if (opts["interact"])
                this.interact = opts["interact"];
            this.setCursor();
            this.tarEle[oc.base.OC_DRAW_PANEL] = this;
            //register self
            this.MODEL_registerListener(this);
        }
        /**
         * name
         */
        getHTMLElement() {
            return this.tarEle;
        }
        //this.init_me();
        delPopMenu() {
            if (this.popMenuEle == null)
                return;
            $(this.popMenuEle).remove();
            this.popMenuEle = null;
        }
        setPopMenu(menuele) {
            this.delPopMenu();
            this.popMenuEle = menuele;
            $(this.tarEle).append(menuele);
        }
        getDrawRes() {
            return this.drawRes;
        }
        setDrawRes(dr) {
            this.drawRes = dr;
        }
        setCursor(c = oc.Cursor.auto) {
            if (this.tarEle == null)
                return;
            var n = oc.Cursor[c];
            var k = n.indexOf('_');
            if (k >= 0)
                n = n.substring(0, k) + '-' + n.substring(k + 1);
            //console.log(" set cursor="+n) ;
            this.tarEle.style.cursor = n;
        }
        MODEL_registerListener(lis) {
            this.modelListeners.push(lis);
        }
        on_model_chged(panel, layer, item, prop_names) {
            try {
                if (layer != null)
                    layer.update_draw();
                else
                    panel.update_draw();
            }
            catch (ee) //firefox may cause error
             { }
            this.bModelDirty = true;
            if (this.on_model_chg != null)
                this.on_model_chg();
        }
        /**
         * support model chged notify
         */
        isModelDirty() {
            return this.bModelDirty;
        }
        setModelDirty(b) {
            this.bModelDirty = b;
        }
        on_model_oper_chged(panel, intera, oper) {
            panel.update_draw();
        }
        on_model_sel_chged(panel, layer, item) {
            layer.update_draw();
        }
        MODEL_fireChged(layer, item, prop_names) {
            for (var lis of this.modelListeners) {
                lis.on_model_chged(this, layer, item, prop_names);
            }
        }
        MODEL_fireOperChged(intera, oper) {
            for (var lis of this.modelListeners) {
                lis.on_model_oper_chged(this, intera, oper);
            }
        }
        MODEL_fireSelectedChged(intera) {
            var curitem = intera.getSelectedItem();
            if (this.on_item_sel_chg != null) {
                this.on_item_sel_chg(curitem);
            }
            for (var lis of this.modelListeners) {
                lis.on_model_sel_chged(this, intera.getLayer(), curitem);
            }
        }
        setInteract(inta) {
            this.interact = inta; //s.push(inta) ;
        }
        getInteract() {
            return this.interact;
        }
        init_me() {
            for (var i = 0; i < this.layers.length; i++)
                this.layers[i].setPanel(this);
            if (this.interact)
                this.interact.setPanel(this);
        }
        clearTimeoutDownLong() {
            if (this.timeoutDownLong != null) {
                clearTimeout(this.timeoutDownLong);
                this.timeoutDownLong = null;
            }
        }
        init_panel() {
            this.init_me();
            this.updatePixelSize();
            //prevent right cxt menu
            window.oncontextmenu = (event) => {
                var evt = event || window.event;
                evt.preventDefault();
            };
            //var this = this;
            this.tarEle.onmousedown = (e) => {
                if (this.interact == null)
                    return;
                this.delPopMenu();
                //for(var i = 0 ; i < this.interacts.length; i ++)
                this.interact.on_mouse_event(oc.MOUSE_EVT_TP.Down, e);
                this.clearTimeoutDownLong();
                this.timeoutDownLong = setTimeout(() => {
                    var _a;
                    (_a = this.interact) === null || _a === void 0 ? void 0 : _a.on_mouse_event(oc.MOUSE_EVT_TP.DownLong, e);
                    this.clearTimeoutDownLong();
                }, 500);
            };
            this.tarEle.onmousemove = (e) => {
                this.clearTimeoutDownLong();
                var p = this.getEventPixel(e); //windowToCanvas(canvas_,e.x,e.y) ;
                var d = this.transPixelPt2DrawPt(p.x, p.y);
                p = this.transDrawPt2PixelPt(d.x, d.y);
                //console.log("["+e.x+","+e.y+"]-("+p.x+","+p.y+")") ;
                if (this.on_mouse_mv != null)
                    this.on_mouse_mv(p, d);
                if (this.interact == null)
                    return;
                this.interact.on_mouse_event(oc.MOUSE_EVT_TP.Move, e);
            };
            this.tarEle.onmouseup = (e) => {
                this.clearTimeoutDownLong();
                if (this.interact == null)
                    return;
                this.interact.on_mouse_event(oc.MOUSE_EVT_TP.Up, e);
            };
            this.tarEle.onclick = (e) => {
                this.clearTimeoutDownLong();
                if (this.interact == null)
                    return;
                this.interact.on_mouse_event(oc.MOUSE_EVT_TP.Clk, e);
            };
            this.tarEle.ondblclick = (e) => {
                this.clearTimeoutDownLong();
                if (this.interact == null)
                    return;
                this.interact.on_mouse_event(oc.MOUSE_EVT_TP.DbClk, e);
            };
            this.tarEle["onmousewheel"] = (e) => {
                this.clearTimeoutDownLong();
                if (this.interact == null)
                    return;
                this.interact.on_mouse_event(oc.MOUSE_EVT_TP.Wheel, e);
            };
            if (window.navigator.userAgent.toLowerCase().indexOf("firefox") != -1) {
                window.addEventListener("DOMMouseScroll", (e) => {
                    this.clearTimeoutDownLong();
                    if (this.interact == null)
                        return;
                    this.interact.on_mouse_event(oc.MOUSE_EVT_TP.Wheel, e);
                }, false);
            }
            this.tarEle.onkeydown = (e) => {
                if (this.interact == null)
                    return;
                this.interact.on_key_event(oc.KEY_EVT_TP.Down, e);
            };
            this.tarEle.onkeyup = (e) => {
                if (this.interact == null)
                    return;
                this.interact.on_key_event(oc.KEY_EVT_TP.Up, e);
            };
            this.tarEle.onkeypress = (e) => {
                if (this.interact == null)
                    return;
                this.interact.on_key_event(oc.KEY_EVT_TP.Press, e);
            };
            this.tarEle.ondrop = (e) => {
                if (this.interact == null)
                    return;
                this.interact.on_mouse_event(oc.MOUSE_EVT_TP.Drop, e);
            };
            this.tarEle.ondragover = (e) => {
                if (this.interact != null) {
                    this.interact.on_mouse_event(oc.MOUSE_EVT_TP.DragOver, e);
                }
                e.preventDefault();
            };
            this.tarEle.ondragleave = (e) => {
                if (this.interact != null) {
                    this.interact.on_mouse_event(oc.MOUSE_EVT_TP.DragLeave, e);
                }
                e.preventDefault();
            };
        }
    }
    oc.DrawPanel = DrawPanel;
    /**
     * support single DrawItem to draw in html element like
     * canvas or div
     */
    class DrawPanelItem extends AbstractDrawPanel {
        constructor(ele, di) {
            super();
            this.canvasEle = ele;
            this.drawItem = di;
            this.context = document.createElement('canvas').getContext('2d');
            this.drawItem.setContainer(this, null);
        }
        getHTMLElement() {
            return this.canvasEle;
        }
        getItemsShow() {
            return [this.drawItem];
        }
        removeItem(item) {
            return false;
        }
        notifyItemsChg() {
        }
        getCanvasEle() {
            return this.canvasEle;
        }
        setPixelSize(sz) {
        }
        getPixelSize() {
            var computedStyle = getComputedStyle(this.canvasEle);
            return {
                w: this.canvasEle.offsetWidth -
                    this.parseToFloat(computedStyle.borderLeftWidth) -
                    this.parseToFloat(computedStyle.paddingLeft) -
                    this.parseToFloat(computedStyle.paddingRight) -
                    this.parseToFloat(computedStyle.borderRightWidth),
                h: this.canvasEle.offsetHeight -
                    this.parseToFloat(computedStyle.borderTopWidth) -
                    this.parseToFloat(computedStyle.paddingTop) -
                    this.parseToFloat(computedStyle.paddingBottom) -
                    this.parseToFloat(computedStyle.borderBottomWidth)
            };
        }
        getDrawItem() {
            return this.drawItem;
        }
        update_draw() {
            this.drawItem.draw(this.context, this);
        }
        clear_draw() {
            var pixsz = this.getPixelSize();
            this.context.clearRect(0, 0, pixsz.w, pixsz.h);
        }
        delPopMenu() {
        }
        setPopMenu(menuele) {
        }
        getDrawView() {
            return null;
        }
        getInteract() {
            return null;
        }
    }
    oc.DrawPanelItem = DrawPanelItem;
    /**
     * for single drawitem to draw in a fixed div
     */
    class DrawPanelDiv {
        constructor(divele, opts) {
            this.drawItem = null;
            if (opts == undefined)
                opts = {};
            if (opts["panel"])
                this.panel = opts["panel"];
            else
                this.panel = new DrawPanel(divele, {});
            this.panel.init_panel();
            this.layer = (opts["layer"] != undefined) ? opts["layer"] : new oc.DrawLayer("lay");
            this.panel.addLayer(this.layer);
            var ele = this.panel.getHTMLElement();
            ele[DrawPanelDiv.DRAW_PANEL_DIV] = this;
        }
        getPanel() {
            return this.panel;
        }
        getLayer() {
            return this.layer;
        }
        setDrawItem(di) {
            this.drawItem = di;
            this.layer.addItem(di);
            //this.layer.ajustDrawFit();//cause firefox error
        }
        getDrawItem() {
            return this.drawItem;
        }
        updateByResize() {
            this.panel.updatePixelSize();
            this.layer.ajustDrawFit();
        }
    }
    DrawPanelDiv.DRAW_PANEL_DIV = "_drawpanel_div";
    oc.DrawPanelDiv = DrawPanelDiv;
})(oc || (oc = {}));
var oc;
(function (oc) {
    /**
     * Res Context
     * like component pics in comp cxt,ui's res in ui
     */
    class DrawResCxt {
        /**
         *
         * @param target html page hidden element(like div) in which support dynamicly load pic
         */
        constructor(name, title, target, urlbase, idxpath) {
            this.idxMap = {};
            this.name = name;
            this.title = title;
            var ele = document.getElementById("target");
            if (ele == null)
                throw Error("no element with id=" + target + " found");
            this.eleRes = ele;
            $(this.eleRes).css("display", "none");
            this.urlBase = urlbase;
            this.idxPath = idxpath;
        }
        getName() {
            return this.name;
        }
        getTitle() {
            return this.title;
        }
        /**
         * {name:string,title:string,path:string}
         */
        loadIdx() {
            $.ajax({
                type: 'post',
                url: this.urlBase + this.idxPath,
                data: {},
                async: true,
                success: function (result) {
                    var ob = null;
                    eval("ob=" + result);
                    if (ob == null)
                        return;
                    for (var o of ob) {
                        var n = o["n"];
                        if (n == null || n == undefined || n == "")
                            continue;
                        var p = o["p"];
                        if (p == null || p == undefined || p == "")
                            continue;
                        var t = o["t"];
                        if (t == null || t == undefined || t == "")
                            t = n;
                        this.idxMap[n] = { n: n, t: t, p: p };
                    }
                },
                error: function (req, err, e) {
                    //cb(false,e) ;
                }
            });
            //this.
        }
    }
    oc.DrawResCxt = DrawResCxt;
})(oc || (oc = {}));
/**
 * base on DrawItems,every unit has is own rect in which inner items
 * draw fit in it.
 *
 * it has border and background or not
 * it also support expose some inner item name which can be set value dynanmicly,
 *  so it can be used to display real time data.
 *
 * the user must implements an DrawUnit lib,to suport all unit saving,loading and listing
 *
 */
var oc;
(function (oc) {
    /**
     * draw unit can has instance,which has extends props
     */
    class DrawUnit extends oc.DrawItems // implements IItemsLister
     {
        constructor(opts) {
            super(opts);
            this.cat = null;
            //title: string | null = null;
            //on creation intance call this url
            // url must input layername and DrawUnit name and do some creation action,then return id with ins used
            this.ins_new_url = null;
            //create instance class name
            this.ins_new_cn = null;
            this.ins_expand_url = null;
            //on show action panel call this url,get panel html elements
            this.ins_act_url = null;
            this.ins_act_pos = 0;
            this.ins_act_w = 100;
            this.ins_act_h = 100;
            this.ins_group = null;
            //like title:c1.txt,cc:rect.color
            //left alias name will be a prop used in instance,and right string is
            //   unit inner drawitem's name . prop.
            //   if alias is title,then instance title will set in unit's drawitem and show
            //   others alias will become instance extends props which can be set dyn data
            this.ins_alias_map = null;
            //private extProps:oc.base.Props<any>|null=null;
            this.aliasMap = null;
        }
        getClassName() {
            return "DrawUnit";
        }
        /**
         * for unit template edit to show temp prop
         */
        static getInsTempDefs() {
            return DrawUnit.INS_TEMP_DEFS;
        }
        static getUnitPns() {
            if (DrawUnit.Unit_PNS != null)
                return DrawUnit.Unit_PNS;
            var r = {
                _cat_name: "unit", _cat_title: "Unit",
                cat: { title: "Cat", type: "str" },
            };
            for (var n in DrawUnit.INS_TEMP_DEFS) {
                r[n] = DrawUnit.INS_TEMP_DEFS[n];
            }
            DrawUnit.Unit_PNS = r;
            return r;
        }
        getPropDefs() {
            var r = super.getPropDefs();
            r.push(DrawUnit.getUnitPns());
            return r;
        }
        getCat() {
            return this.cat;
        }
        setCat(c) {
            this.cat = c;
            this.MODEL_fireChged(["cat"]);
        }
        getTitle() {
            return this.title;
        }
        setTitle(t) {
            this.title = t;
            this.MODEL_fireChged(["title"]);
        }
        getInsNewUrl() {
            return this.ins_new_url;
        }
        getInsNewCN() {
            return this.ins_new_cn;
        }
        getInsExpandUrl() {
            return this.ins_expand_url;
        }
        getInsActUrl() {
            return this.ins_act_url;
        }
        getInsActW() {
            return this.ins_act_w;
        }
        getInsActH() {
            return this.ins_act_h;
        }
        getInsActPos() {
            return this.ins_act_pos;
        }
        getInsGroup() {
            return this.ins_group;
        }
        /**
         * trans title:c1.txt,cc:rect.color to map
         * and support ins to update by alias map
         */
        getAliasMap() {
            if (this.aliasMap != null)
                return this.aliasMap;
            if (this.ins_alias_map == null || this.ins_alias_map == "") {
                return null;
            }
            var r = {};
            //title:c1.txt,cc:rect.color
            var ss = this.ins_alias_map.split(',');
            for (var s of ss) {
                var n2m = s.split(':');
                if (n2m.length != 2)
                    continue;
                var ms = n2m[1].split('.');
                if (ms.length != 2)
                    continue;
                r[n2m[0].trim()] = [ms[0].trim(), ms[1].trim()];
            }
            this.aliasMap = r;
            return this.aliasMap;
        }
        // public getExtProp():oc.base.Props<any>|null
        // {
        // 	return this.extProps;
        // }
        // public setExtProp(ep:oc.base.Props<any>|null)
        // {
        // 	this.extProps = ep;
        // }
        inject(opts, ignore_readonly) {
            if (typeof (opts) == 'string')
                eval("opts=" + opts);
            super.inject(opts, ignore_readonly);
            this.title = opts["title"] ? opts["title"] : "";
            var dis = opts["dis"];
            if (dis) {
                for (var i = 0; i < dis.length; i++) {
                    var it = dis[i];
                    var cn = it._cn;
                    if (!cn)
                        continue;
                    var item = oc.DrawItem.createByClassName(cn, undefined);
                    if (item == null)
                        continue;
                    item.inject(it, false);
                    this.addItem(item);
                    //console.log(" draw unit cn="+cn+"  item="+item);
                }
            }
        }
        extract() {
            var r = super.extract();
            if (this.cat)
                r["cat"] = this.cat;
            // var rdis: any[] = r["dis"] = [];
            // for (var i = 0; i < this.items.length; i++)
            // {
            // 	rdis.push(this.items[i].extract());
            // }
            return r;
        }
        getUnitDrawSize() {
            var n = this.items.length;
            if (n <= 0)
                return { w: 100, h: 100 };
            var last = this.items[n - 1];
            var r;
            if (last instanceof oc.DrawItemRectBorder) {
                var tmps = [];
                for (var i = 0; i < n - 1; i++)
                    tmps.push(this.items[i]);
                r = oc.ItemsContainer.calcRect(tmps);
            }
            else
                r = oc.ItemsContainer.calcRect(this.items);
            if (r == null)
                return { w: 100, h: 100 };
            return { w: r.w, h: r.h };
        }
        /**
         * to fit for list with fix square size
         * a square border sub item is needed to be add
         */
        addSquareBorder() {
            this.getDrawSize();
            var r = oc.ItemsContainer.calcRect(this.items);
            if (r == null)
                return false;
            //console.log(r);
            r = r.expandToSquareByCenter();
            //console.log(r);
            var tmpi = new oc.DrawItemRectBorder({ rect: r });
            this.addItem(tmpi);
            return true;
        }
        /**
         *
         */
        drawUnit(cxt, c, dyn_ps) {
            for (var item of this.items) {
                var n = item.getName();
                if (n != null && n != "") {
                    var dynp = dyn_ps[n];
                    if (dynp != undefined && dynp != null) {
                    }
                }
                item.draw(cxt, c);
            }
        }
        static setAjaxLoadUrl(u) {
            DrawUnit.ajaxLoadUrl = u;
        }
        // static addUnit(u: DrawUnit,ext:oc.base.Props<any>|null)
        // {
        // 	DrawUnit.setUnit(u,ext);
        // }
        static setUnit(u) {
            // if(ext!=null)
            // 	u.setExtProp(ext);
            DrawUnit.id2unit[u.getId()] = u;
            var n = u.getName();
            if (n == null || n == undefined || n == "")
                return;
            DrawUnit.name2unit[n] = u;
        }
        static addUnitByJSON(json) {
            if (typeof (json) == 'string')
                eval("json=" + json);
            var u = new DrawUnit(undefined);
            u.inject(json, false);
            var ext = json["_ext"];
            if (ext != undefined && ext != null) {
                u.setDynData(ext, false);
            }
            DrawUnit.setUnit(u);
        }
        static getUnitById(id) {
            var u = DrawUnit.id2unit[id];
            if (u == null || u == undefined)
                return null;
            return u;
        }
        // static getOrLoadUnitById(id:string):DrawUnit|null
        // {
        // 	var u = DrawUnit.id2unit[id];
        // 	if(u!=undefined&&u!=null)
        // 		return u;
        // }
        static getUnitByName(n) {
            var u = DrawUnit.name2unit[n];
            if (u == null || u == undefined)
                return null;
            return u;
        }
        static getUnitJSONStr(id) {
            var du = DrawUnit.getUnitById(id);
            if (du == null)
                return null;
            var ob = du.extract();
            return JSON.stringify(ob);
        }
    }
    DrawUnit.INS_TEMP_DEFS = {
        ins_new_cn: { title: "ins_new_cn", type: "str", enum_val: [["oc.iott.Unit", "Unit"], ["oc.iott.UnitTN", "UnitTN"]] },
        ins_new_url: { title: "ins_new_url", type: "str" },
        ins_expand_url: { title: "ins_expand_url", type: "str" },
        ins_act_url: { title: "ins_act_url", type: "str" },
        ins_act_pos: { title: "ins_act_pos", type: "int", enum_val: [[0, "bottom"], [1, "right"], [2, "left"], [3, "top"]] },
        ins_act_w: { title: "ins_act_w", type: "float" },
        ins_act_h: { title: "ins_act_h", type: "float" },
        ins_group: { title: "ins_group", type: "str" },
        ins_alias_map: { title: "ins_alias_map", type: "str", multi_lns: true },
    };
    DrawUnit.Unit_PNS = null;
    // public getUnitActItems():UnitActItem[]|null
    // {
    // 	var n = this.getName();
    // 	if(n==null||n=="")
    // 		return null ;
    // 	return DrawUnit.getActionsByUnitName(n);
    // }
    // public getUnitActItem(op_name:string):UnitActItem|null
    // {
    // 	var uais = this.getUnitActItems();
    // 	if(uais==null)
    // 		return null;
    // 	for(var r of uais)
    // 	{
    // 		if(op_name==r.op_name)
    // 			return r ;
    // 	}
    // 	return null ;
    // }
    DrawUnit.id2unit = {};
    DrawUnit.name2unit = {};
    DrawUnit.ajaxLoadUrl = null;
    oc.DrawUnit = DrawUnit;
    class DrawUnitIns extends oc.DrawItemRectR {
        constructor(opts) {
            super(opts);
            this.borderPixel = null;
            this.borderColor = "yellow";
            this.fillColor = null;
            this.radius = null;
            this.unitName = null;
            /**
             * deep copy of DrawItem
             */
            this.dynUnit = null;
            this.innerCont = null;
        }
        setUnitName(n) {
            this.unitName = n;
            this.MODEL_fireChged(["unitName"]);
        }
        getUnitName() {
            return this.unitName;
        }
        getUnit() {
            if (this.dynUnit != null)
                return this.dynUnit;
            if (this.unitName == null || this.unitName == "")
                return null;
            var du = DrawUnit.getUnitByName(this.unitName);
            if (du == null)
                return null;
            this.dynUnit = du.duplicateMe();
            var c = this.getContainer();
            if (this.dynUnit != null && c != null)
                this.dynUnit.setContainer(c, this.getLayer());
            return this.dynUnit;
        }
        // public getUnitExtProps():oc.base.Props<any>|null
        // {
        // 	var du = this.getUnit();
        // 	if(du==null)
        // 		return null;
        // 	return du.getExtProp();
        // }
        //public getUnit
        getInnerDrawItemByName(n) {
            var u = this.getUnit();
            if (u == null)
                return null;
            for (var i of u.getItemsShow()) {
                if (n == i.getName())
                    return i;
            }
            return null;
        }
        /**
         * set inneritem dyn must by item's name
         * if item is not set name,it cannot be set dyn
         * @param dyn
         * @param bfirechg
         */
        setDynData(dyn, bfirechg = true) {
            var ns = super.setDynData(dyn, false);
            var u = this.getUnit();
            if (u != null) {
                var aliasmap = u.getAliasMap();
                if (aliasmap != null) {
                    for (var aliasn in aliasmap) {
                        var v = dyn[aliasn]; //use alias name getval
                        if (v == undefined || v == null)
                            continue;
                        var mapss = aliasmap[aliasn];
                        if (mapss.length < 2)
                            continue;
                        var tmpi = this.getInnerDrawItemByName(mapss[0]);
                        if (tmpi == null)
                            continue;
                        tmpi[mapss[1]] = v;
                    }
                }
            }
            var dyn_unit = dyn[DrawUnitIns.PN_DYN_UNIT];
            if (dyn_unit != undefined && dyn_unit != null) {
                ns.push(DrawUnitIns.PN_DYN_UNIT);
                for (var n in dyn_unit) {
                    var tmpi = this.getInnerDrawItemByName(n);
                    if (tmpi == null)
                        continue;
                    tmpi.setDynData(dyn_unit[n], false);
                }
            }
            if (bfirechg)
                this.MODEL_fireChged(ns);
            return ns;
        }
        getInnerContainer() {
            if (this.innerCont != null)
                return this.innerCont;
            var pc = this.getContainer();
            if (pc == null)
                return null;
            var u = this.getUnit();
            if (u == null)
                return null;
            this.innerCont = new oc.ItemsContainer(this, pc, u);
            return this.innerCont;
        }
        getClassName() {
            return "DrawUnitIns";
        }
        getPropDefs() {
            var r = super.getPropDefs();
            r.push(DrawUnitIns.PNS);
            return r;
        }
        /**
         * override it to support alias map inject
         * to unit
         * @param prop_names
         */
        MODEL_fireChged(prop_names) {
            var u = this.getUnit();
            if (u != null) {
                var aliasmap = u.getAliasMap();
                if (aliasmap != null) {
                    for (var aliasn in aliasmap) {
                        var v = this[aliasn]; //instance alias name getval
                        if (v == undefined || v == null)
                            continue;
                        var mapss = aliasmap[aliasn];
                        if (mapss.length < 2)
                            continue;
                        var tmpi = this.getInnerDrawItemByName(mapss[0]);
                        if (tmpi == null)
                            continue;
                        tmpi[mapss[1]] = v;
                    }
                }
            }
            super.MODEL_fireChged(prop_names);
        }
        getPrimRect() {
            var ic = this.getInnerContainer();
            if (ic == null)
                return new oc.base.Rect(0, 0, this.getW(), this.getH());
            // return ic.getItemsRectInner();
            var u = this.getUnit();
            if (u == null)
                return new oc.base.Rect(0, 0, this.getW(), this.getH());
            //return u.getPrimRect();
            //return new oc.base.Rect(0,0,100,100);
            var r = oc.ItemsContainer.calcRect(u.getItemsShow());
            if (r == null)
                return null;
            var p = ic.transDrawPt2PixelPt(r.x, r.y);
            var w = ic.transDrawLen2PixelLen(true, r.w);
            var h = ic.transDrawLen2PixelLen(false, r.h);
            return new oc.base.Rect(0, 0, w, h);
            //return r ;
        }
        /**
         * override to provider more extends item in unit
         */
        getUnitExtItems() {
            return [];
        }
        drawPrim(cxt) {
            var u = this.getUnit();
            var ic = this.getInnerContainer();
            var items = (u != null ? u.getItemsShow() : null);
            if (ic == null || u == null || items == null || items.length <= 0) {
                //var tmpr = this.getPrimRect();
                //if (tmpr == null)
                //	return;
                oc.util.drawRectEmpty(cxt, 0, 0, this.getW(), this.getH(), this.borderColor);
                return;
            }
            //this.drawRect(cxt,c);
            //
            cxt.save();
            var pt = this.getPixelXY();
            if (pt != null) //what the fuck
                cxt.translate(-pt.x, -pt.y);
            for (var item of items) {
                item.draw(cxt, ic);
            }
            for (var item of this.getUnitExtItems()) {
                item.draw(cxt, ic);
            }
            //u.draw(cxt, ic);
            cxt.restore();
        }
        drawPrimSel(ctx) {
        }
    }
    DrawUnitIns.PN_DYN_UNIT = "_unit";
    DrawUnitIns.PNS = {
        _cat_name: "unitins", _cat_title: "Unit Ins",
        borderPixel: { title: "border", type: "str" },
        borderColor: { title: "borderColor", type: "str" },
        fillColor: { title: "fillColor", type: "str", val_tp: "color" },
        radius: { title: "radius", type: "int" },
        unitName: { title: "Unit Name", type: "str", readonly: true }
    };
    oc.DrawUnitIns = DrawUnitIns;
})(oc || (oc = {}));
var oc;
(function (oc) {
    var util;
    (function (util) {
        util._tmpid = 0;
        function create_new_tmp_id() {
            util._tmpid++;
            if (util._tmpid > 100)
                util._tmpid = 1;
            var d = new Date();
            var tmps = 'x';
            tmps += d.getFullYear();
            var i = d.getMonth();
            if (i < 10)
                tmps += '0' + i;
            else
                tmps += i;
            i = d.getDay();
            if (i < 10)
                tmps += '0' + i;
            else
                tmps += i;
            i = d.getHours();
            if (i < 10)
                tmps += '0' + i;
            else
                tmps += i;
            i = d.getMinutes();
            if (i < 10)
                tmps += '0' + i;
            else
                tmps += i;
            i = d.getSeconds();
            if (i < 10)
                tmps += '0' + i;
            else
                tmps += i;
            tmps += "_" + util._tmpid;
            return tmps;
            //return "id_"+scada_tmpid ;
        }
        util.create_new_tmp_id = create_new_tmp_id;
        function chkEmpty(v) {
            return v == null || v == undefined || v == "";
        }
        util.chkEmpty = chkEmpty;
        function chkNotEmpty(v) {
            return v != null && v != undefined && v != "";
        }
        util.chkNotEmpty = chkNotEmpty;
        function trim(n) {
            if (n == null)
                return null;
            return n.replace(/(^\s+)|\s+$/g, '');
        }
        util.trim = trim;
        var msgF = null, errF = null;
        function prompt_reg(msgfn, errfn) {
            msgF = msgfn;
            errF = errfn;
        }
        util.prompt_reg = prompt_reg;
        function prompt_msg(s) {
            if (msgF)
                msgF(s);
        }
        util.prompt_msg = prompt_msg;
        function prompt_err(s) {
            if (errF)
                errF(s);
        }
        util.prompt_err = prompt_err;
        function doAjax(url, pm, endcb) {
            $.ajax({
                type: 'post',
                url: url,
                data: pm
            }).done(function (ret) {
                if (typeof (ret) == "string")
                    ret = ret.trim();
                endcb(true, ret);
            }).fail(function (req, st, err) {
                console.log(err);
                console.log(url);
                endcb(false, err);
            });
        }
        util.doAjax = doAjax;
        function setDragEventData(ev, ps) {
            var tf = ev.dataTransfer;
            if (tf == null)
                return;
            for (var n in ps) {
                var v = ps[n];
                tf.setData(n + "=" + v, "");
            }
        }
        util.setDragEventData = setDragEventData;
        function getDragEventData(ev) {
            var tf = ev.dataTransfer;
            if (tf == null)
                return {};
            var r = {};
            for (var nv of tf.types) {
                var k = nv.indexOf("=");
                if (k < 0) {
                    r[nv] = "";
                }
                else {
                    var n = nv.substr(0, k);
                    var v = nv.substr(k + 1);
                    r[n] = v;
                }
            }
            return r;
        }
        util.getDragEventData = getDragEventData;
        function drawRect(cxt, x, y, width, height, radius, fillColor, borderw, bordercolor) {
            cxt.save();
            cxt.translate(x, y);
            if (fillColor) {
                //bordercolor;
                cxt.strokeStyle = cxt.fillStyle = fillColor || "#000";
                if (radius != null && radius != NaN && radius > 0)
                    drawRoundRectPath(cxt, width, height, radius);
                else
                    //cxt.strokeRect(0,0,width,height);
                    drawRectPath(cxt, width, height);
                cxt.fill();
            }
            if (bordercolor || !fillColor || (borderw != null && borderw > 0)) //&&borderw>0)
             {
                cxt.lineWidth = borderw || 1;
                cxt.strokeStyle = bordercolor || "#000";
                if (radius != null && radius != NaN && radius > 0)
                    drawRoundRectPath(cxt, width, height, radius);
                else
                    drawRectPath(cxt, width, height); //cxt.strokeRect(0,0,width,height);
            }
            //this.strokeRoundRect=function(cxt, x, y, width, height, radius, lineWidth,strokeColor)
            cxt.stroke();
            cxt.restore();
        }
        util.drawRect = drawRect;
        function drawRectEmpty(cxt, x, y, width, height, bordercolor) {
            cxt.save();
            cxt.translate(x, y);
            cxt.lineWidth = 1;
            cxt.setLineDash([5, 5]);
            cxt.strokeStyle = bordercolor || "#c1cccc";
            drawRectPath(cxt, width, height);
            cxt.stroke();
            cxt.restore();
        }
        util.drawRectEmpty = drawRectEmpty;
        function drawRoundRectPath(cxt, width, height, radius) {
            //cxt.strokeStyle = "#000";
            cxt.beginPath();
            cxt.arc(width - radius, height - radius, radius, 0, Math.PI / 2);
            cxt.lineTo(radius, height);
            cxt.arc(radius, height - radius, radius, Math.PI / 2, Math.PI);
            cxt.lineTo(0, radius);
            cxt.arc(radius, radius, radius, Math.PI, Math.PI * 3 / 2);
            cxt.lineTo(width - radius, 0);
            cxt.arc(width - radius, radius, radius, Math.PI * 3 / 2, Math.PI * 2);
            cxt.lineTo(width, height - radius);
            cxt.closePath();
        }
        function drawRectPath(cxt, width, height) {
            //cxt.strokeStyle = "#000";
            cxt.beginPath();
            cxt.lineTo(0, 0);
            cxt.lineTo(width, 0);
            cxt.lineTo(width, height);
            cxt.lineTo(0, height);
            cxt.lineTo(0, 0);
            cxt.closePath();
        }
        /**
         * 绘制箭头
         * @param cxt
         * @param fromx
         * @param fromy
         * @param tox
         * @param toy
         * @param arrow_len
         * @param arrow_angle
         */
        function drawArrow(cxt, fromx, fromy, tox, toy, arrow_len, arrow_h) {
            cxt.save();
            cxt.translate(tox, toy);
            //cxt.fillStyle="red";
            var ang = (tox - fromx) / (toy - fromy);
            ang = Math.atan(ang);
            if (toy - fromy >= 0) {
                cxt.rotate(-ang);
            }
            else {
                cxt.rotate(Math.PI - ang);
            }
            cxt.beginPath();
            cxt.lineTo(-arrow_h, -arrow_len);
            cxt.lineTo(0, -arrow_len / 2);
            cxt.lineTo(arrow_h, -arrow_len);
            cxt.lineTo(0, 0);
            cxt.fill(); //
            cxt.closePath();
            cxt.restore();
        }
        util.drawArrow = drawArrow;
        util.CTRL_PT_R = 6;
        util.CTRL_LN_MIN_PIXEL = 2;
        function chkPtInRadius(x1, y1, x2, y2, r) {
            return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) <= r * r;
        }
        util.chkPtInRadius = chkPtInRadius;
        class Matrix {
            static createMatrix(a, b, c, d, e, f) {
                return [
                    [a, c, e],
                    [b, d, f],
                    [0, 0, 1]
                ];
            }
            static createMatrixEmpty() {
                return [
                    [0, 0, 0],
                    [0, 0, 0],
                    [0, 0, 1]
                ];
            }
            static add(m1, m2) {
                var mReturn = Matrix.createMatrixEmpty();
                if (m1.length == m2.length) {
                    for (var row = 0; row < m1.length; row++) {
                        //mReturn[row]=[];
                        for (var column = 0; column < m1[row].length; column++) {
                            mReturn[row][column] = m1[row][column] + m2[row][column];
                        }
                    }
                }
                return mReturn;
            }
            static copyRow(mr) {
                return [mr[0], mr[1], mr[2]];
            }
            static copy(m) {
                return [
                    Matrix.copyRow(m[0]),
                    Matrix.copyRow(m[1]),
                    Matrix.copyRow(m[2]),
                ];
            }
            /**
             * Substract matrix m2 from m1
             * @returns m1-m2
             */
            static subtract(m1, m2) {
                var mReturn = Matrix.createMatrixEmpty();
                if (m1.length == m2.length) {
                    for (var row = 0; row < m1.length; row++) {
                        for (var column = 0; column < m1[row].length; column++) {
                            mReturn[row][column] = m1[row][column] - m2[row][column];
                        }
                    }
                }
                return mReturn;
            }
            ;
            /**
             *Check has NaN values
             *@returns true - if it contains NaN values, false otherwise
             */
            static hasNaN(m) {
                for (var row = 0; row < m.length; row++) {
                    for (var column = 0; column < m[row].length; column++) {
                        if (isNaN(m[row][column])) {
                            return true;
                        }
                    }
                }
                return false;
            }
            ;
            /**
             * Multiply matrix m2 with m1
             */
            static multiply(m1, m2) {
                var mReturn = Matrix.createMatrixEmpty(); // [];
                if (m1[0].length == m2.length) { //check that width=height
                    for (var m1Row = 0; m1Row < m1.length; m1Row++) {
                        //mReturn[m1Row] = [];
                        for (var m2Column = 0; m2Column < m2[0].length; m2Column++) {
                            mReturn[m1Row][m2Column] = 0;
                            for (var m2Row = 0; m2Row < m2.length; m2Row++) {
                                mReturn[m1Row][m2Column] += m1[m1Row][m2Row] * m2[m2Row][m2Column];
                            }
                        }
                    }
                }
                return mReturn;
            }
            ;
            static transPt(m, x, y) {
                var r = Matrix.multiply(m, [[x], [y], [1]]);
                return { x: r[0][0], y: r[1][0] };
            }
            /**
             * Multiply matrix m2 with m1
             *If you apply a transformation T to a point P the new point is:
             *  P' = T x P
             *So if you apply more then one transformation (T1, T2, T3) then the new point is:
             *  P'= T3 x (T2 x (T1 x P)))
             **/
            static mergeTransformations(ts) {
                if (ts.length <= 0)
                    throw Error("transform matrix is empty");
                var mReturn = Matrix.copy(ts[ts.length - 1]);
                for (var m = arguments.length - 2; m >= 0; m--) {
                    mReturn = Matrix.multiply(mReturn, ts[m]);
                }
                return mReturn;
            }
            /**
             * Inverts a matrix
             *
             **/
            static invertMatrix(m) {
            }
            ;
            /**
             * Compares two matrixes
             */
            static equals(m1, m2) {
                if (m1.length != m2.length) {
                    return false;
                }
                for (var i in m1) {
                    if (m1[i].length != m2[i].length) { //
                        return false;
                    }
                    else {
                        for (var j in m1[i]) {
                            if (m1[i][j] != m2[i][j]) {
                                return false;
                            }
                        }
                    }
                }
                return true;
            }
            static getRotation(angle) {
                return [
                    [Math.cos(angle), -Math.sin(angle), 0],
                    [Math.sin(angle), Math.cos(angle), 0],
                    [0, 0, 1]
                ];
            }
            static getTranslation(dx, dy) {
                return [
                    [1, 0, dx],
                    [0, 1, dy],
                    [0, 0, 1]
                ];
            }
            static getScale(sx, sy) {
                return [
                    [1 / sx, 0, 0],
                    [0, 1 / sy, 0],
                    [0, 0, 1]
                ];
            }
            static setCxtRotation(cxt, angle) {
                var cv = Math.cos(angle);
                var sv = Math.sin(angle);
                cxt.transform(cv, sv, -sv, cv, 0, 0);
            }
            /**
             *
             * @param cxt
             * @param ax x方向放大倍数
             * @param ay y方向放大倍数
             */
            static setCxtScale(cxt, ax, ay) {
                cxt.transform(ax, 0, 0, ay, 0, 0);
            }
            /**
             *
             * @param cxt 移动
             * @param mv_x
             * @param mv_y
             */
            static setCxtTranslation(cxt, dx, dy) {
                cxt.transform(1, 0, 0, 1, dx, dy);
            }
        }
        util.Matrix = Matrix;
        class DrawTransfer {
            static calcRotatePt(p, pcenter, angle) {
                // calc arc 
                var ang = ((angle * Math.PI) / 180);
                //sin/cos value
                var cosv = Math.cos(ang);
                var sinv = Math.sin(ang);
                // calc new point
                var rx = ((p.x - pcenter.x) * cosv - (p.y - pcenter.y) * sinv + pcenter.x);
                var ry = ((p.x - pcenter.x) * sinv + (p.y - pcenter.y) * cosv + pcenter.y);
                return { x: rx, y: ry };
            }
        }
        util.DrawTransfer = DrawTransfer;
        //arr [r,g,b]  hsv H(hues)表示色相，S(saturation)表示饱和度，B（brightness）
        function transRGB2HSV(arr) {
            var h = 0, s = 0, v = 0;
            var r = arr[0], g = arr[1], b = arr[2];
            arr.sort((a, b) => {
                return a - b;
            });
            var max = arr[2];
            var min = arr[0];
            v = max / 255;
            if (max === 0) {
                s = 0;
            }
            else {
                s = 1 - (min / max);
            }
            if (max === min) {
                h = 0; // max===min h can any value
            }
            else if (max === r && g >= b) {
                h = 60 * ((g - b) / (max - min)) + 0;
            }
            else if (max === r && g < b) {
                h = 60 * ((g - b) / (max - min)) + 360;
            }
            else if (max === g) {
                h = 60 * ((b - r) / (max - min)) + 120;
            }
            else if (max === b) {
                h = 60 * ((r - g) / (max - min)) + 240;
            }
            h = parseInt("" + h);
            s = parseInt("" + s * 100);
            v = parseInt("" + v * 100);
            return [h, s, v];
        }
        util.transRGB2HSV = transRGB2HSV;
        function transHSV2RGB(arr) {
            var h = arr[0], s = arr[1], v = arr[2];
            s = s / 100;
            v = v / 100;
            var r = 0, g = 0, b = 0;
            var i = parseInt("" + ((h / 60) % 6));
            var f = h / 60 - i;
            var p = v * (1 - s);
            var q = v * (1 - f * s);
            var t = v * (1 - (1 - f) * s);
            switch (i) {
                case 0:
                    r = v;
                    g = t;
                    b = p;
                    break;
                case 1:
                    r = q;
                    g = v;
                    b = p;
                    break;
                case 2:
                    r = p;
                    g = v;
                    b = t;
                    break;
                case 3:
                    r = p;
                    g = q;
                    b = v;
                    break;
                case 4:
                    r = t;
                    g = p;
                    b = v;
                    break;
                case 5:
                    r = v;
                    g = p;
                    b = q;
                    break;
                default:
                    break;
            }
            r = parseInt("" + r * 255.0);
            g = parseInt("" + g * 255.0);
            b = parseInt("" + b * 255.0);
            return [r, g, b];
        }
        util.transHSV2RGB = transHSV2RGB;
    })(util = oc.util || (oc.util = {}));
})(oc || (oc = {}));
/**
 * @module oc  //open chart
 */
var oc;
(function (oc) {
    let Cursor;
    (function (Cursor) {
        Cursor[Cursor["auto"] = 0] = "auto";
        Cursor[Cursor["crosshair"] = 1] = "crosshair";
        Cursor[Cursor["default"] = 2] = "default";
        Cursor[Cursor["hand"] = 3] = "hand";
        Cursor[Cursor["move"] = 4] = "move";
        Cursor[Cursor["text"] = 5] = "text";
        Cursor[Cursor["w_resize"] = 6] = "w_resize";
        Cursor[Cursor["s_resize"] = 7] = "s_resize";
        Cursor[Cursor["n_resize"] = 8] = "n_resize";
        Cursor[Cursor["e_resize"] = 9] = "e_resize";
        Cursor[Cursor["ne_resize"] = 10] = "ne_resize";
        Cursor[Cursor["sw_resize"] = 11] = "sw_resize";
        Cursor[Cursor["se_resize"] = 12] = "se_resize";
        Cursor[Cursor["nw_resize"] = 13] = "nw_resize";
        Cursor[Cursor["pointer"] = 14] = "pointer";
    })(Cursor = oc.Cursor || (oc.Cursor = {}));
    /**
     * single js loader
     */
    class JsLoader {
        constructor(url, load_ok_cb) {
            this.url = url;
            this.loaded = false;
            this.loadOkCb = load_ok_cb;
        }
        getUrl() {
            return this.url;
        }
        isLoadedOk() {
            return this.loaded;
        }
        load() {
            JsLoader.loadJsUrl(this.url, () => {
                this.loaded = true;
                this.loadOkCb(this);
            });
        }
        static loadJsUrl(url, loadcb) {
            var script = document.createElement("script");
            script.type = "text/javascript";
            if (script["readyState"]) {
                //ie
                script["onreadystatechange"] = function () {
                    if (script["readyState"] == "complete" || script["readyState"] == "loaded") {
                        loadcb();
                    }
                };
            }
            else {
                //Chrome Safari Opera Firefox
                script.onload = () => {
                    loadcb();
                };
            }
            script.src = url;
            document.head.appendChild(script);
        }
    }
    oc.JsLoader = JsLoader;
    /**
     * multi js loader
     */
    class JssLoader {
        constructor(urls, jsscb) {
            this.loaders = [];
            this.loadOkCb = jsscb;
            this.initUrls(urls);
        }
        initUrls(urls) {
            for (var u of urls) {
                var jsl = new JsLoader(u, (jsl) => {
                    if (this.isLoadedOk())
                        this.loadOkCb(this);
                });
                this.loaders.push(jsl);
            }
        }
        getJsLoaders() {
            return this.loaders;
        }
        getJsUrls() {
            var ret = [];
            for (var jsl of this.loaders)
                ret.push(jsl.getUrl());
            return ret;
        }
        isLoadedOk() {
            for (var ld of this.loaders) {
                if (!ld.isLoadedOk())
                    return false;
            }
            return true;
        }
        load() {
            for (var ld of this.loaders) {
                ld.load();
            }
        }
    }
    oc.JssLoader = JssLoader;
})(oc || (oc = {}));
// var CHART_ROOT = "/admin/_chart/" ;
// oc.base={};
// oc.interact={};
// oc.di={};
// oc.loader={};
// oc.editor={};
// oc.util={};
// oc.loader.IdxCb=function(js)
// {
// 	this.js=js;
// }
// oc.loader.__idxCbs = [] ;
// oc.loader.__cur_load_idx = 0 ;
// oc.loader.__load_endcb =null ;
// oc.loader._load_one=function()
// {
// 	if(this.__cur_load_idx>=this.__idxCbs.length)
// 		return ;
// 	var ic = this.__idxCbs[this.__cur_load_idx];
// 	//console.log(CHART_ROOT+ic.js);
// 	$.getScript(CHART_ROOT+ic.js).done(function(){
// 		oc.loader.__cur_load_idx ++ ;
// 		if(oc.loader.__cur_load_idx==oc.loader.__idxCbs.length)
// 		{//end
// 			console.log("load end succ");
// 			oc.loader.__load_endcb() ;
// 		}
// 		else
// 		{
// 			oc.loader._load_one();
// 		}
// 	}).error(function(err){
// 		console.log("load err:"+ic.js);
// 	});//) ;
// }
// oc.loader.load_js=function(jss,endcb)
// {
// 	if(jss==null||jss.length<=0)
// 		return ;
// 	oc.loader.__load_endcb = endcb;
// 	for(var i = 0 ; i < jss.length ; i ++)
// 	{
// 		oc.loader.__idxCbs.push(new oc.loader.IdxCb(jss[i])) ;
// 	}
// 	oc.loader._load_one();
// }
// oc.loader._dyn_jss = [
// 	"draw_base.js","draw_util.js","draw_item.js","draw_layer.js",
// 	"draw_panel.js","draw_edit.js",//"draw_interact.js",
// 	"di/di_common.js","di/di_rect.js",
// 	"interact/oper_drag.js"//,"interact/interact_editlayer.js"
// 	] ;
// //oc.loader._dyn_jss1 = ["di/di_common.js","interact/dinter_drag.js"] ;
/**
 * @module oc/base
 */
var oc;
(function (oc) {
    var base;
    (function (base) {
        base.OC_DRAW_PANEL = "_oc_drawpanel";
        function forceCast(input) {
            // ... do runtime checks here
            // @ts-ignore <-- forces TS compiler to compile this as-is
            return input;
        }
        base.forceCast = forceCast;
        class Rect {
            constructor(x, y, w, h) {
                this.x = 0;
                this.y = 0;
                this.w = 0;
                this.h = 0;
                this.x = x;
                this.y = y;
                this.w = w;
                this.h = h;
            }
            static createByPt(minpt, maxpt) {
                return new Rect(minpt[0], minpt[1], maxpt[0] - minpt[0], maxpt[1] - minpt[1]);
            }
            static copy(r) {
                return new Rect(r.x, r.y, r.w, r.h);
            }
            static createEmpty() {
                return new Rect(0, 0, 0, 0);
            }
            // public constructor()
            // {
            // 	if (arguments.length == 4)
            // 	{
            // 		this.x = arguments[0];
            // 		this.y = arguments[1];
            // 		this.w = arguments[2];
            // 		this.h = arguments[3];
            // 	}
            // 	else if (arguments.length == 1)
            // 	{
            // 		//alert(arguments[0] instanceof oc.base.Rect);
            // 		//{
            // 		this.x = arguments[0].x;
            // 		this.y = arguments[0].y;
            // 		this.w = arguments[0].w;
            // 		this.h = arguments[0].h;
            // 		//}
            // 	}
            // }
            getMaxX() {
                return this.x + this.w;
            }
            equals(r) {
                if (r == null)
                    return false;
                return this.x == r.x && this.y == r.y && this.w == r.w && this.h == r.h;
            }
            getMaxY() {
                return this.y + this.h;
            }
            setMaxX(mx) {
                if (mx <= this.x)
                    throw "invalid max x";
                this.w = mx - this.x;
            }
            setMaxY(my) {
                if (my <= this.y)
                    throw "invalid max y";
                this.h = my - this.y;
            }
            getCenter() {
                return { x: this.x + this.w / 2, y: this.y + this.h / 2 };
            }
            isValid() {
                return this.w > 0 || this.h > 0;
            }
            //expand by overlapped other rect
            expandBy(r) {
                if (!r.isValid())
                    return;
                if (!this.isValid()) {
                    this.x = r.x;
                    this.y = r.y;
                    this.w = r.w;
                    this.h = r.h;
                    return;
                }
                if (r.getMaxX() > this.getMaxX())
                    this.setMaxX(r.getMaxX());
                if (r.getMaxY() > this.getMaxY())
                    this.setMaxY(r.getMaxY());
                if (r.x < this.x) {
                    this.w += this.x - r.x;
                    this.x = r.x;
                }
                if (r.y < this.y) {
                    this.h += this.y - r.y;
                    this.y = r.y;
                }
            }
            expandToSquareByCenter() {
                var d = this.w > this.h ? this.w : this.h;
                var c = this.getCenter();
                return new Rect(c.x - d / 2, c.y - d / 2, d, d);
            }
            contains(X, Y) {
                var w = this.w;
                var h = this.h;
                if (w < 0 || h < 0) {
                    // At least one of the dimensions is negative...
                    return false;
                }
                // Note: if either dimension is zero, tests below must return false...
                var x = this.x;
                var y = this.y;
                if (X < x || Y < y) {
                    return false;
                }
                w += x;
                h += y;
                // overflow || intersect
                return ((w < x || w > X) && (h < y || h > Y));
            }
            listFourPt() {
                return [{ x: this.x, y: this.y }, { x: this.x + this.w, y: this.y },
                    { x: this.x + this.w, y: this.y + this.h }, { x: this.x, y: this.y + this.h }];
            }
            toStr() {
                return "[" + this.x + "," + this.y + "," + this.w + "," + this.h + "]";
            }
        } //end of rect
        base.Rect = Rect;
        class Pts {
            constructor() {
                this.pts = [];
                this.bound = null;
                if (arguments.length == 1) {
                    var pts = arguments[0];
                    if (pts instanceof Array) {
                        if (pts == null || pts.length < 3)
                            throw "pts num must >=3";
                        for (var i = 0; i < pts.length; i++) {
                            var pt = pts[i];
                            if (pt instanceof Array)
                                this.pts.push({ x: pt[0], y: pt[1] });
                            else
                                this.pts.push(pt);
                        }
                    }
                }
                else {
                    for (var i = 0; i < arguments.length; i++) {
                        var pt = arguments[i];
                        if (pt instanceof Array)
                            this.pts.push({ x: pt[0], y: pt[1] });
                        else
                            this.pts.push(pt);
                    }
                }
            }
            getFirst() {
                if (this.pts.length <= 0)
                    return null;
                return this.pts[0];
            }
            getLast() {
                if (this.pts.length <= 0)
                    return null;
                return this.pts[this.pts.length - 1];
            }
            getPt(idx) {
                if (idx < 0 || idx >= this.pts.length)
                    return null;
                return this.pts[idx];
            }
            addPt(x, y) {
                this.pts.push({ x: x, y: y });
                this.bound = this.calculateBounds();
            }
            chgPt(idx, x, y) {
                if (idx < 0 || idx >= this.pts.length)
                    return false;
                this.pts[idx].x = x;
                this.pts[idx].y = y;
                this.bound = this.calculateBounds();
                return true;
            }
            getPts() {
                return this.pts;
            }
            setPts(pts) {
                if (pts == null || pts == undefined)
                    this.pts = [];
                else
                    this.pts = pts;
                this.calculateBounds();
            }
            getPtNum() {
                return this.pts.length;
            }
            calculateBounds() {
                if (this.pts.length <= 2)
                    return null;
                let minx = Number.MAX_VALUE;
                let miny = Number.MAX_VALUE;
                let maxx = Number.NEGATIVE_INFINITY;
                let maxy = Number.NEGATIVE_INFINITY;
                for (var p of this.pts) {
                    var x = p.x;
                    if (minx > x)
                        minx = x;
                    if (maxx < x)
                        maxx = x;
                    //minx = Math.min(minx, x);
                    //maxx = Math.max(maxx, x);
                    var y = p.y;
                    if (miny > y)
                        miny = y;
                    if (maxy < y)
                        maxy = y;
                    //miny = Math.min(miny, y);
                    //maxy = Math.max(maxy, y);
                    //console.
                }
                return new Rect(minx, miny, maxx - minx, maxy - miny);
            }
            getBoundingBox() {
                if (this.pts.length == 0) {
                    return null;
                }
                if (this.bound == null) {
                    this.bound = this.calculateBounds();
                }
                return this.bound;
            }
            movePt(dx, dy) {
                for (var pt of this.pts) {
                    pt.x += dx;
                    pt.y += dy;
                }
                this.bound = this.calculateBounds();
            }
            toStr() {
                if (this.pts.length <= 0)
                    return "";
                var ret = "(" + this.pts[0].x + "," + this.pts[0].y + ")";
                for (var i = 1; i < this.pts.length; i++) {
                    ret += "," + "(" + this.pts[i].x + "," + this.pts[i].y + ")";
                }
                return ret;
            }
        }
        base.Pts = Pts;
        class Polygon extends Pts {
            constructor() {
                super();
            }
            isValid() {
                return this.pts.length >= 3;
            }
            contains(x, y) {
                if (this.pts.length < 3)
                    return false;
                let bbox = this.getBoundingBox();
                if (bbox == null || !bbox.contains(x, y)) {
                    //console.log(bbox,"x=",x,"y=",y)
                    return false;
                }
                var hits = 0;
                var n = this.pts.length - 1;
                var tmppts = this.pts;
                if (n == 2) { //add a pt
                    tmppts = [];
                    for (var pt of this.pts)
                        tmppts.push(pt);
                    tmppts.push(this.pts[2]);
                    n++;
                }
                var lastpt = tmppts[n];
                var lastx = lastpt.x;
                var lasty = lastpt.y;
                var curx, cury;
                // Walk the edges of the polygon
                for (var i = 0; i < n; lastx = curx, lasty = cury, i++) {
                    var p = tmppts[i];
                    curx = p.x; // xpoints[i];
                    cury = p.y; // ypoints[i];
                    if (cury == lasty) {
                        continue;
                    }
                    var leftx;
                    if (curx < lastx) {
                        if (x >= lastx) {
                            continue;
                        }
                        leftx = curx;
                    }
                    else {
                        if (x >= curx) {
                            continue;
                        }
                        leftx = lastx;
                    }
                    var test1, test2;
                    if (cury < lasty) {
                        if (y < cury || y >= lasty) {
                            continue;
                        }
                        if (x < leftx) {
                            hits++;
                            continue;
                        }
                        test1 = x - curx;
                        test2 = y - cury;
                    }
                    else {
                        if (y < lasty || y >= cury) {
                            continue;
                        }
                        if (x < leftx) {
                            hits++;
                            continue;
                        }
                        test1 = x - lastx;
                        test2 = y - lasty;
                    }
                    if (test1 < (test2 / (lasty - cury) * (lastx - curx))) {
                        hits++;
                    }
                }
                return hits % 2 == 1; //((hits & 1) != 0);
            }
        }
        base.Polygon = Polygon;
        base.createPolygonByPt2 = function (pts) {
            if (pts.length < 3)
                throw Error("pts length must > 2");
            var r = new Polygon();
            for (var pt of pts) {
                r.addPt(pt[0], pt[1]);
            }
            return r;
        };
        class Fill {
            constructor() {
                this.tp = "";
                this.color = "";
                this.colors = [];
                this.rotate = 0;
                this.pat_sty = "";
            }
            calNor() {
            }
            getColorsStr() {
                if (this.colors.length <= 0)
                    return "";
                var r = "";
                for (var c of this.colors)
                    r += "|" + c;
                return r.substr(1);
            }
            toStr() {
                switch (this.tp) {
                    case "nor":
                        return "nor;" + this.color;
                    case "g_lin":
                        return "g_lin;" + this.getColorsStr() + ";" + this.rotate;
                    case "g_rad":
                        return "g_rad;" + this.getColorsStr();
                    case "pat":
                        return "pat;" + this.getColorsStr() + ";" + this.pat_sty;
                }
                return "";
            }
            static parseStr(str) {
                var ss = str.split(";");
                if (ss.length < 2)
                    return null;
                switch (ss[0]) {
                    case "nor":
                        return Fill.createNor(ss[1]);
                    case "g_lin":
                        var r = 0;
                        if (ss.length >= 3)
                            r = parseInt(ss[2]);
                        return Fill.createLinearG(ss[1].split("|"), r);
                    case "g_rad":
                        return Fill.createRadialG(ss[1].split("|"));
                    case "pat":
                        if (ss.length < 3)
                            return null;
                        var cs = ss[1].split("|");
                        if (cs.length != 2)
                            return null;
                        return Fill.createPattern(cs[0], cs[1], ss[2]);
                }
                return null;
            }
            static createNor(color) {
                var f = new Fill();
                f.tp = "nor";
                f.color = color;
                return f;
            }
            static createLinearG(colors, rotate) {
                var f = new Fill();
                f.tp = "g_lin";
                f.colors = colors; //"#xxxxx|rgba(12,21,22)"
                f.rotate = rotate;
                return f;
            }
            static createRadialG(colors) {
                var f = new Fill();
                f.tp = "g_rad";
                f.colors = colors;
                return f;
            }
            static createPattern(color1, color2, ptsty) {
                var f = new Fill();
                f.tp = "pat";
                f.colors = [color1, color2];
                f.pat_sty = ptsty;
                return f;
            }
            calLinearPts(r, rotate) {
                rotate %= 360;
                var cr = rotate * Math.PI / 180;
                var ang1 = Math.atan(r.h / r.w);
                if (cr <= ang1 || cr >= 2 * Math.PI - ang1) { //left right
                    var dh = r.w / 2 * Math.tan(cr);
                    var p2 = { x: r.x + r.w, y: r.y + r.h / 2 + dh };
                    var p1 = { x: r.x, y: r.y + r.h / 2 - dh };
                    return [p1, p2];
                }
                else if (cr > ang1 && cr <= Math.PI - ang1) { //top btm
                    var dw = Math.tan(Math.PI / 2 - cr) * r.h / 2;
                    var p2 = { x: r.x + r.w / 2 + dw, y: r.y + r.h };
                    var p1 = { x: r.x + r.w / 2 - dw, y: r.y };
                    return [p1, p2];
                }
                else if (cr >= Math.PI - ang1 && cr <= Math.PI + ang1) { //
                    var dh = r.w / 2 * Math.tan(cr);
                    var p1 = { x: r.x + r.w, y: r.y + r.h / 2 + dh };
                    var p2 = { x: r.x, y: r.y + r.h / 2 - dh };
                    return [p1, p2];
                }
                else {
                    var dw = Math.tan(Math.PI / 2 - cr) * r.h / 2;
                    var p1 = { x: r.x + r.w / 2 + dw, y: r.y + r.h };
                    var p2 = { x: r.x + r.w / 2 - dw, y: r.y };
                    return [p1, p2];
                }
            }
            calCxtFillStyle(r, cxt) {
                switch (this.tp) {
                    case "nor":
                        return this.color;
                    case "g_lin":
                        var pts = this.calLinearPts(r, this.rotate);
                        var linear = cxt.createLinearGradient(pts[0].x, pts[0].y, pts[1].x, pts[1].y);
                        var cn = this.colors.length;
                        for (var i = 0; i < cn; i++) {
                            var st = 1.0 * i / (cn - 1);
                            linear.addColorStop(st, this.colors[i]);
                        }
                        return linear;
                    case "g_rad":
                    //var rad = cxt.createRadialGradient()
                    case "pat":
                    //cxt.createPattern() ;
                }
                return this.color;
            }
            static calPipeLinearPts(x1, y1, x2, y2, ln_w) {
                var d = ln_w / 2;
                if (y1 == y2) { //
                    var p1 = { x: x1 - d, y: y1 };
                    var p2 = { x: x1 + d, y: y1 };
                    return [p1, p2];
                }
                var a = Math.atan((y2 - y1) / (x2 - x1));
                var dx = d * Math.sin(a);
                var dy = d * Math.cos(a);
                var p1 = { x: x1 - dx, y: y1 + dy };
                var p2 = { x: x1 + dx, y: y1 - dy };
                return [p1, p2];
            }
            static calCxtPipeFillStyle(x1, y1, x2, y2, ln_w, color, cxt) {
                if (x1 == x2 && y1 == y2)
                    return null;
                //cal line pipe 
                var pts = Fill.calPipeLinearPts(x1, y1, x2, y2, ln_w);
                var linear = cxt.createLinearGradient(pts[0].x, pts[0].y, pts[1].x, pts[1].y);
                cxt.createRadialGradient;
                linear.addColorStop(0, "#000");
                linear.addColorStop(0.5, color);
                linear.addColorStop(1, "#000");
                return linear;
            }
        }
        base.Fill = Fill;
    })(base = oc.base || (oc.base = {}));
})(oc || (oc = {}));
/**
 * menu support for drawitem
 */
var oc;
(function (oc) {
    class PopMenu {
        constructor(di, menuitems) {
            //private panel:DrawPanel;
            this.menuEle = null;
            this.menuItems = [];
            //this.panel=p;
            this.target = di;
            this.menuItems = menuitems;
        }
        getTarget() {
            return this.target;
        }
        getMenuItems() {
            return this.menuItems;
        }
        getMenuItem(op) {
            for (var mi of this.menuItems) {
                if (mi.op_name == op)
                    return mi;
            }
            return null;
        }
        getMenuEle() {
            var tmpid = this.target.getId();
            if (this.menuEle != null)
                return this.menuEle;
            var tmps = `<div id="menu_${tmpid}" class="oc_menu">`;
            for (var actitem of this.menuItems) {
                var icon = actitem.op_icon;
                if (icon == undefined || icon == '')
                    icon = "icon";
                tmps += `<div class="menu" act_id="${tmpid}" act_op="${actitem.op_name}" onmousedown="oc.PopMenu.menuAction('${tmpid}','${actitem.op_name}')"><i class="${icon}">&nbsp;</i>&nbsp;&nbsp;<span>${actitem.op_title}</span></div>`;
            }
            tmps += `</div>`;
            this.menuEle = $(tmps);
            this.menuEle.get(0)["_oc_popmenu"] = this;
            return this.menuEle;
        }
        showPopMenu(tar, pxy, dxy) {
            var p = this.target.getPanel();
            if (p == null)
                return false;
            var menuele = this.getMenuEle();
            p.setPopMenu(menuele);
            menuele.css("left", pxy.x + "px");
            menuele.css("top", pxy.y + "px");
            menuele.get(0)["_oc_pxy"] = pxy;
            menuele.get(0)["_oc_dxy"] = dxy;
            return true;
        }
        // static displayPopMenu(tar:any,popm:PopMenu,pxy:oc.base.Pt,dxy:oc.base.Pt)
        // {
        //     var p = popm.getDrawItem().getPanel();
        //     if(p==null)
        //         return ;
        //     var menuele = popm.getMenuEle();
        //     p.setPopMenu(menuele) ;
        //     menuele.css("left", pxy.x+ "px");
        //     menuele.css("top", pxy.y + "px");
        //     menuele.get(0)["_oc_pxy"]=pxy ;
        //     menuele.get(0)["_oc_dxy"]=dxy ;
        // }
        static getDefaultPopMenuItem(dn) {
            var n = dn.getActionTypeName();
            if (n == "")
                return null;
            return PopMenu.getMenuItemDefaultByName(n);
        }
        static createShowPopMenu(dn, pxy, dxy) {
            var n = dn.getActionTypeName();
            if (n == "")
                return false;
            //if(this.popMenu!=null)
            //	return this.popMenu;
            var pms = PopMenu.getMenuItemsByName(n);
            if (pms == null)
                return false;
            var p = dn.getPanel();
            if (p == null)
                return false;
            var pm = new PopMenu(dn, pms);
            //return this.popMenu;
            return pm.showPopMenu(dn, pxy, dxy);
        }
        static menuAction(id, op) {
            //console.log("menuAction")
            var ob = $("#menu_" + id);
            if (ob == null || ob == undefined)
                return;
            var pm = ob.get(0)["_oc_popmenu"];
            if (pm == null)
                return;
            var dn = pm.getTarget();
            var mitem = pm.getMenuItem(op);
            var p = dn.getPanel();
            if (mitem == null || p == null)
                return;
            //var u = ob.get(0)["_oc_unit"]// as Unit;
            var pxy = ob.get(0)["_oc_pxy"];
            var dxy = ob.get(0)["_oc_dxy"];
            if (window != undefined && window.event)
                window.event.stopPropagation();
            mitem.action(dn, op, pxy, dxy);
            p.delPopMenu();
        }
        static setMenuTp2Items(tp2items) {
            PopMenu.tp2items = tp2items;
        }
        static getMenuItemsByName(u) {
            if (PopMenu.tp2items == null)
                return null;
            return PopMenu.tp2items[u];
        }
        static getMenuItemDefaultByName(n) {
            var pms = this.getMenuItemsByName(n);
            if (pms == null)
                return null;
            for (var pm of pms) {
                if (pm.default === true)
                    return pm;
            }
            return null;
        }
        static getMenuItemByNameOp(u, op) {
            if (PopMenu.tp2items == null)
                return null;
            var acts = PopMenu.tp2items[u];
            if (!acts)
                return null;
            for (var act of acts) {
                if (op == act.op_name)
                    return act;
            }
            return null;
        }
    }
    PopMenu.tp2items = null;
    oc.PopMenu = PopMenu;
})(oc || (oc = {}));
var oc;
(function (oc) {
    var di;
    (function (di) {
        class DIArc extends oc.DrawItemRectR {
            constructor(opts) {
                super(opts);
                this.fillColor = null;
                this.border = 1;
                //radius: number | null = null;
                this.color = "yellow";
                this.startAngle = 0.0;
                this.endAngle = Math.PI * 1.5;
            }
            getClassName() {
                return "DIArc";
            }
            getWHRatio() {
                return 1;
            }
            getPropDefs() {
                var r = super.getPropDefs();
                r.push(DIArc.PNS);
                return r;
            }
            // public getBoundRectDraw()
            // {
            // 	var pt = this.getDrawXY();
            // 	return new oc.base.Rect(pt.x, pt.y, this.w, this.h);
            // }
            chkPtOnCtrl(pxy, dxy) {
                var c = this.getContainer();
                if (c == null)
                    return null;
                var r = c.transPixelLen2DrawLen(true, oc.util.CTRL_PT_R);
                //var dr = this.w/2;//c.transDrawLen2PixelLen(true,this.w/2);
                var startpt = this.calDrawPtByAngle(this.startAngle);
                var endpt = this.calDrawPtByAngle(this.endAngle);
                if (oc.util.chkPtInRadius(startpt.x, startpt.y, dxy.x, dxy.y, r))
                    return "s";
                if (oc.util.chkPtInRadius(endpt.x, endpt.y, dxy.x, dxy.y, r))
                    return "e";
                //chk arc which can change radius
                var rectp = this.getBoundRectPixel();
                if (rectp == null)
                    return null;
                var cp = rectp.getCenter();
                var tmpv = Math.sqrt((pxy.x - cp.x) * (pxy.x - cp.x) + (pxy.y - cp.y) * (pxy.y - cp.y));
                tmpv = Math.abs(tmpv - rectp.w / 2);
                if (tmpv <= oc.util.CTRL_PT_R) //util.CTRL_PT_R*util.CTRL_PT_R)
                    return "c";
                return null;
            }
            // /**
            //  * based on center pt,using draw pt to calculate angle
            //  * @param x 
            //  * @param y 
            //  */
            // private calArcAngleByDrawPt(x:number,y:number)
            // {
            //     var pt = this.getDrawXY();
            //     var centerx = pt.x+this.w/2 ;
            //     var centery = pt.y+this.h/2 ;
            //     var dx = x-centerx ;
            //     var dy = y-centery ;
            //     if(dx==0)
            //     {
            //         if(dy>=0)
            //             return Math.PI*0.5 ;
            //         else
            //             return Math.PI*1.5 ;
            //     }
            //     var r = Math.atan(dy/dx);
            //     if(dx>0)
            //     {
            //         if(r>=0)
            //             return r;
            //         else
            //             return Math.PI*2+r;
            //     }
            //     else
            //     {
            //         if(r>=0)
            //             return r+Math.PI;
            //         else
            //             return Math.PI+r;
            //     }
            // }
            setCtrlDrawPt(ctrlpt, x, y) {
                if ("s" == ctrlpt) {
                    this.startAngle = this.calArcAngleByDrawPt(x, y);
                    this.MODEL_fireChged(["startAngle"]);
                }
                else if ("e" == ctrlpt) {
                    this.endAngle = this.calArcAngleByDrawPt(x, y);
                    this.MODEL_fireChged(["endAngle"]);
                }
                else if ("c" == ctrlpt) { //chg radius
                    var w = this.getW();
                    var h = this.getH();
                    var pt = this.getDrawXY();
                    var cx = pt.x + w / 2;
                    var cy = pt.y + h / 2;
                    var dx = cx - x;
                    var dy = cy - y;
                    var nr = Math.sqrt(dy * dy + dx * dx);
                    this.setDrawSize(nr * 2, nr * 2);
                    this.x = cx - nr;
                    this.y = cy - nr;
                    this.MODEL_fireChged(["w", "h", "x", "y"]);
                }
            }
            getPrimRect() {
                return new oc.base.Rect(0, 0, 100, 100);
            }
            calPtByAngle(ang) {
                var r = 50;
                var px = 50 + Math.cos(ang) * r;
                var py = 50 + Math.sin(ang) * r;
                return { x: px, y: py };
            }
            drawPrim(cxt) {
                cxt.save();
                cxt.strokeStyle = this.color;
                cxt.lineWidth = this.border || 1;
                cxt.beginPath();
                //cxt.moveTo(startx,starty);
                cxt.arc(50, 50, 50, this.startAngle, this.endAngle);
                if (this.fillColor != null && this.fillColor != "") {
                    cxt.fillStyle = this.fillColor;
                    cxt.fill();
                    //if(this.border)
                    var startpt = this.calPtByAngle(this.startAngle);
                    var endpt = this.calPtByAngle(this.endAngle);
                    cxt.moveTo(startpt.x, startpt.y);
                    cxt.lineTo(endpt.x, endpt.y);
                }
                //else
                {
                    cxt.stroke();
                }
                cxt.restore();
            }
            drawPrimSel(ctx) {
            }
            calDrawPtByAngle(ang) {
                var w = this.getW();
                var h = this.getH();
                var pt = this.getDrawXY();
                var centerx = pt.x + w / 2;
                var centery = pt.y + h / 2;
                var r = w / 2;
                var px = centerx + Math.cos(ang) * r;
                var py = centery + Math.sin(ang) * r;
                return { x: px, y: py };
            }
            // public draw0(cxt:CanvasRenderingContext2D,c:IDrawItemContainer)
            // {
            // 	var pt = this.getDrawXY();
            // 	var pcenter = c.transDrawPt2PixelPt(pt.x+this.w/2, pt.y+this.h/2);
            // 	var dr = c.transDrawLen2PixelLen(true,this.w/2);
            //     cxt.save();
            //     cxt.strokeStyle=this.color;
            //     cxt.lineWidth =this.border||1 ;
            //     cxt.beginPath();
            //     //cxt.moveTo(startx,starty);
            //     cxt.arc(pcenter.x, pcenter.y, dr, this.startAngle, this.endAngle);
            //     if(this.fillColor!=null&&this.fillColor!="")
            //     {
            //         cxt.fillStyle=this.fillColor;
            //         cxt.fill();
            //         //if(this.border)
            //         var startpt = this.calDrawPtByAngle(this.startAngle);
            //         startpt = c.transDrawPt2PixelPt(startpt.x,startpt.y);
            //         var endpt = this.calDrawPtByAngle(this.endAngle);
            //         endpt = c.transDrawPt2PixelPt(endpt.x,endpt.y);
            //         cxt.moveTo(startpt.x,startpt.y);
            //         cxt.lineTo(endpt.x,endpt.y);
            //     }
            //     //else
            //     {
            //         cxt.stroke();
            //     }
            //     cxt.restore();
            // }
            draw_sel0(cxt, c) {
                super.draw_sel(cxt, c);
                //
                //var pt = this.getDrawXY();
                //var pcenter = c.transDrawPt2PixelPt(pt.x+this.w/2, pt.y+this.h/2);
                //var dr = c.transDrawLen2PixelLen(true,this.w/2);
                // var startx = pcenter.x+Math.cos(this.startAngle)*dr;
                // var starty = pcenter.y-Math.sin(this.startAngle)*dr;
                // var endx = pcenter.x+Math.cos(this.endAngle)*dr;
                // var endy = pcenter.y-Math.sin(this.endAngle)*dr;
                var startpt = this.calDrawPtByAngle(this.startAngle);
                startpt = c.transDrawPt2PixelPt(startpt.x, startpt.y);
                var endpt = this.calDrawPtByAngle(this.endAngle);
                endpt = c.transDrawPt2PixelPt(endpt.x, endpt.y);
                cxt.save();
                cxt.beginPath();
                cxt.strokeStyle = "yellow";
                cxt.moveTo(startpt.x, startpt.y);
                cxt.arc(startpt.x, startpt.y, oc.util.CTRL_PT_R, 0, Math.PI * 2, true);
                cxt.moveTo(endpt.x, endpt.y);
                cxt.arc(endpt.x, endpt.y, oc.util.CTRL_PT_R, 0, Math.PI * 2, true);
                cxt.stroke();
                cxt.restore();
            }
        }
        DIArc.PNS = {
            _cat_name: "di_arc", _cat_title: "DI Arc",
            fillColor: { title: "fillColor", type: "str", edit_plug: "color" },
            border: { title: "border", type: "int" },
            color: { title: "color", type: "color", edit_plug: "color" },
            //radius: { title: "radius", type: "float" },
            startAngle: { title: "StartAngle", type: "float" },
            endAngle: { title: "EndAngle", type: "float" }
        };
        di.DIArc = DIArc;
    })(di = oc.di || (oc.di = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var di;
    (function (di) {
        /**
         * multi pt (>=3) to create geom in rect
         */
        class DIBasic extends oc.DrawItemRectR {
            constructor(opts) {
                super(opts);
                this.color = 'yellow';
                //polygon=new oc.base.Polygon();
                this.lnW = 1;
                this.fillColor = null;
                this.tp = "rect";
                this.ptsPy = null;
                if (opts != undefined) {
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
            static getRectByTp(tp) {
                var r = DIBasic.TP2RECT[tp];
                if (r != undefined && r != null)
                    return r;
                var obj = DIBasic.tp2pts[tp];
                if (obj == undefined || obj == null)
                    return null;
                let tmpr = new oc.base.Rect(0, 0, 0, 0);
                for (var t in obj) {
                    var v = obj[t];
                    switch (t) {
                        case "ln":
                        case "eclipse":
                            var r0 = oc.base.createPolygonByPt2(v).getBoundingBox();
                            if (r0 != null)
                                tmpr.expandBy(r0);
                            break;
                        case "lns":
                            for (var v0 of v) {
                                var r0 = oc.base.createPolygonByPt2(v0).getBoundingBox();
                                if (r0 != null)
                                    tmpr.expandBy(r0);
                            }
                            break;
                        case "arc":
                            tmpr.expandBy(new oc.base.Rect(v.x - v.r, v.y - v.r, v.r * 2, v.r * 2));
                            break;
                        case "arcs":
                            for (var v0 of v) {
                                tmpr.expandBy(new oc.base.Rect(v0.x - v0.r, v0.y - v0.r, v0.r * 2, v0.r * 2));
                            }
                            break;
                            break;
                    }
                }
                DIBasic.TP2RECT[tp] = tmpr;
                return tmpr;
            }
            getClassName() {
                return "DIBasic";
            }
            // public getPtsPy():oc.base.Polygon|null
            // {
            //     if(this.pts_tp!="")
            //         return DIPts.getPyByTp(this.pts_tp);
            //     else
            //         return this.ptsPy;
            // }
            setTp(tp) {
                this.tp = tp;
            }
            setLnW(w) {
                this.lnW = w;
            }
            setLnColor(c) {
                this.color = c;
            }
            setFillColor(c) {
                this.fillColor = c;
            }
            getPropDefs() {
                var r = super.getPropDefs();
                r.push(DIBasic.PNS);
                return r;
            }
            getBoundPolygonDraw() {
                //return new oc.base.Polygon();
                return null;
            }
            getPrimRect() {
                return DIBasic.getRectByTp(this.tp);
            }
            drawEclipse(cxt, ln, xratio, yratio) {
                cxt.beginPath();
                cxt.strokeStyle = this.color;
                cxt.lineWidth = this.lnW;
                var width = 50 * xratio;
                var height = 50 * yratio;
                //Width offset
                var offset_w = width * 2 * 0.2761423749154;
                //Height offset
                var offset_h = height * 2 * 0.2761423749154;
                var centerp = { x: 50 * xratio, y: 50 * yratio };
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
                if (this.fillColor && this.fillColor != "") {
                    cxt.fillStyle = this.fillColor;
                    cxt.fill();
                }
                var p1 = { x: 0, y: 0 };
                var p2 = { x: 100 * xratio, y: 100 * yratio };
                var linear = cxt.createLinearGradient(p1.x, p1.y, p2.x, p2.y);
                linear.addColorStop(0, '#fff');
                linear.addColorStop(0.5, '#f0f');
                linear.addColorStop(1, '#333');
                cxt.fillStyle = linear;
                cxt.closePath();
                cxt.fill();
                //then stroke
                cxt.stroke();
            }
            drawLn(ctx, ln, xratio, yratio) {
                ctx.beginPath();
                ctx.strokeStyle = this.color;
                ctx.lineWidth = this.lnW;
                for (var tmppt of ln) {
                    ctx.lineTo(tmppt[0] * xratio, tmppt[1] * yratio);
                }
                ctx.lineTo(ln[0][0] * xratio, ln[0][1] * yratio);
                ctx.stroke();
                if (this.fillColor && this.fillColor != "") {
                    ctx.fillStyle = this.fillColor;
                    ctx.closePath();
                    ctx.fill();
                }
            }
            drawArc(ctx, arc, xratio, yratio) {
                ctx.beginPath();
                ctx.strokeStyle = this.color;
                ctx.lineWidth = this.lnW;
                ctx.arc(arc.x * xratio, arc.y * yratio, arc.r * xratio, arc.sa, arc.ea);
                ctx.stroke();
                if (this.fillColor && this.fillColor != "") {
                    ctx.fillStyle = this.fillColor;
                    ctx.closePath();
                    ctx.fill();
                }
            }
            drawPrimScale(ctx, xratio, yratio) {
                var obj = DIBasic.tp2pts[this.tp];
                if (obj == undefined || obj == null)
                    return true;
                ctx.strokeStyle = this.color;
                for (var t in obj) {
                    var v = obj[t];
                    switch (t) {
                        case "ln":
                            this.drawLn(ctx, v, xratio, yratio);
                            break;
                        case "eclipse":
                            this.drawEclipse(ctx, v, xratio, yratio);
                            break;
                        case "lns":
                            for (var ln of v) {
                                this.drawLn(ctx, ln, xratio, yratio);
                            }
                            break;
                        case "arc":
                            this.drawArc(ctx, v, xratio, yratio);
                            break;
                        case "arcs":
                            for (var ln of v) {
                                this.drawArc(ctx, ln, xratio, yratio);
                            }
                            break;
                    }
                }
                return true; //will make draw prim not to be called
            }
            drawPrim(ctx) {
                var obj = DIBasic.tp2pts[this.tp];
                if (obj == undefined || obj == null)
                    return;
                ctx.strokeStyle = this.color;
                for (var t in obj) {
                    var v = obj[t];
                    switch (t) {
                        case "ln":
                            this.drawLn(ctx, v, 1, 1);
                            break;
                        case "lns":
                            for (var ln of v) {
                                this.drawLn(ctx, ln, 1, 1);
                            }
                            break;
                        case "arc":
                            this.drawArc(ctx, v, 1, 1);
                            break;
                        case "arcs":
                            for (var ln of v) {
                                this.drawArc(ctx, ln, 1, 1);
                            }
                            break;
                    }
                }
            }
            drawPrimSel(ctx) {
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
        DIBasic.tp2pts = {
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
        DIBasic.TP2RECT = {};
        DIBasic.PNS = {
            _cat_name: "pts", _cat_title: "Pts",
            color: { title: "color", type: "str", edit_plug: "color" },
            lnW: { title: "Line Width", type: "int" },
            fillColor: { title: "Fill Color", type: "str", edit_plug: "color" },
            tp: {
                title: "Figure Type", type: "string", enum_val: [["rect", "Rect"], ["diamond", "Diamond"],
                    ["parallelogram", "parallelogram"], ["pentagon", "pentagon"], ["circle", "circle"],
                    ["circle+", "circle+"], ["circle-", "circle-"], ["iso_triangle", "Triangle"], ["eclipse", "Eclipse"]]
            },
        };
        di.DIBasic = DIBasic;
    })(di = oc.di || (oc.di = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var di;
    (function (di) {
        class DIDial extends oc.DrawItemRectR {
            constructor(opts) {
                super(opts);
                // w: number = 100;
                // h: number = 100;
                this.border = 0;
                this.borderColor = "yellow";
                this.fillColor = null;
                this.radius = null;
                this.TICK_WIDTH = 30;
                this.ANNOTATIONS_FILL_STYLE = 'rgba(230, 230, 230, 0.9)';
                this.ANNOTATIONS_TEXT_SIZE = 12;
                this.TICK_LONG_STROKE_STYLE = 'rgba(100, 140, 230, 0.9)';
                this.TICK_SHORT_STROKE_STYLE = 'rgba(100, 140, 230, 0.7)';
                this.RING_INNER_RADIUS = 65;
                this.X = 150;
                this.Y = 150;
                this.R = 150;
            }
            getClassName() {
                return "oc.di.DIDial";
            }
            getPropDefs() {
                var r = super.getPropDefs();
                r.push(DIDial.DIRect_PNS);
                return r;
            }
            //draw one tick
            drawTick(context, angle, radius, cnt) {
                var tickWidth = cnt % 4 === 0 ? this.TICK_WIDTH : this.TICK_WIDTH / 2;
                context.beginPath();
                //利用三角函数确定小刻度两端的位置并连线
                context.moveTo(150 + Math.cos(angle) * (radius - tickWidth), 150 + Math.sin(angle) * (radius - tickWidth));
                context.lineTo(150 + Math.cos(angle) * radius, 150 + Math.sin(angle) * radius);
                context.strokeStyle = this.TICK_SHORT_STROKE_STYLE;
                context.stroke();
            }
            //画所有刻度线
            drawTicks(context) {
                var radius = 150, //+this.RING_INNER_RADIUS,
                ANGLE_MAX = 2 * Math.PI, 
                //每个小格的角度，注意这里是除以64，因为
                ANGLE_DELTA = Math.PI / 64, tickWidth;
                context.save();
                for (var angle = 0, cnt = 0; angle < ANGLE_MAX; angle += ANGLE_DELTA, cnt++) {
                    this.drawTick(context, angle, radius, cnt);
                }
                context.restore();
            }
            //添加刻度数字,通过角度计算出数字
            drawAnnotations(context) {
                var radius = 150; //+this.RING_INNER_RADIUS;
                context.save();
                context.fillStyle = this.ANNOTATIONS_FILL_STYLE;
                context.font = this.ANNOTATIONS_TEXT_SIZE + 'px Helvetica';
                for (var angle = 0; angle < 2 * Math.PI; angle += Math.PI / 8) {
                    context.beginPath();
                    context.fillText((angle * 180 / Math.PI).toFixed(0), 150 + Math.cos(angle) * (radius - this.TICK_WIDTH * 2), 150 - Math.sin(angle) * (radius - this.TICK_WIDTH * 2));
                }
                context.restore();
            }
            getPrimRect() {
                //var pt = this.getDrawXY();
                return new oc.base.Rect(0, 0, 300, 300);
            }
            drawPrim(ctx) {
                //ctx.rect(0,0,100,100);
                //oc.util.drawRect(ctx, 0, 0, 100, 100, this.radius, this.fillColor, this.border, this.borderColor);
                //ctx.stroke();
                this.drawTicks(ctx);
                this.drawAnnotations(ctx);
            }
            drawPrimSel(ctx) {
            }
        }
        DIDial.DIRect_PNS = {
            _cat_name: "direct", _cat_title: "DI Rectangle",
            //w: { title: "width", type: "int" },
            //h: { title: "height", type: "int" },
            border: { title: "border", type: "str" },
            borderColor: { title: "borderColor", type: "str", edit_plug: "color" },
            fillColor: { title: "fillColor", type: "color", val_tp: "color", edit_plug: true },
            radius: { title: "radius", type: "int" }
        };
        di.DIDial = DIDial;
    })(di = oc.di || (oc.di = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var di;
    (function (di) {
        /**
         * support font icon like FontAwesome.
         */
        class DIIcon extends oc.DrawItemRectR {
            constructor(opts) {
                super(opts);
                //txt: string = '#text';
                this.color = "yellow";
                this.unicode = "";
                this.fontSize = 30;
                this.boundDrawRect = null;
                this.bfirst = true;
                if (opts != undefined) {
                    var uc = opts["unicode"];
                    if (uc != undefined || uc != null)
                        this.unicode = uc;
                }
            }
            getClassName() {
                return "DIIcon";
            }
            getPropDefs() {
                var r = super.getPropDefs();
                r.push(DIIcon.DIIcon_PNS);
                return r;
            }
            setDrawBeginXY(cont, x, y) {
                this.x = x;
                this.y = y;
                //this.txt = "";
                return false; //not end and continue;
            }
            getPrimRect() {
                if (this.boundDrawRect != null)
                    return this.boundDrawRect;
                var cxt = this.getCxt();
                if (cxt == null)
                    return null;
                //fit and ajust - imp for first
                this.fontSize = this.getH() - DIIcon.TXT_ADJ_H;
                if (this.fontSize <= 1)
                    this.fontSize = 1;
                var fs = this.fontSize;
                cxt.save();
                //cxt.fontt = this.fontSize+"px serif";
                cxt.font = fs + "px fontawesome";
                cxt.fillStyle = this.color;
                var txt = this.decodeHexTo(this.unicode);
                //cxt.fillText(txt, 0, fs);
                var mt = cxt.measureText(txt);
                //console.log(mt);
                var tw_px = mt.width + 5;
                this.boundDrawRect = new oc.base.Rect(0, 0, tw_px, this.fontSize); //
                cxt.restore();
                return this.boundDrawRect;
            }
            decodeHexTo(str) {
                str = str.replace(/\\/g, "%");
                return unescape(str);
            }
            drawPrim(cxt) {
                var c = this.getContainer();
                if (c == null)
                    return;
                if (this.bfirst) {
                    this.bfirst = false;
                    var oldr = this.getPrimRect();
                    if (oldr != null) {
                        this.setDrawH(this.getW() * oldr.h / oldr.w);
                        this.fontSize = this.getH() - DIIcon.TXT_ADJ_H;
                        if (this.fontSize <= 1)
                            this.fontSize = 1;
                    }
                }
                var fs = this.fontSize;
                cxt.save();
                //cxt.fontt = this.fontSize+"px serif";
                cxt.font = fs + "px fontawesome";
                cxt.fillStyle = this.color;
                var txt = this.decodeHexTo(this.unicode);
                var mt = cxt.measureText(txt);
                //console.log(mt);
                var tw_px = mt.width + 5;
                cxt.fillText(txt, 0, fs * 0.86);
                this.boundDrawRect = new oc.base.Rect(0, 0, tw_px, this.fontSize); //
                cxt.restore();
            }
            drawPrimSel(ctx) {
            }
            changeRect(ctrlpt, x, y) {
                var c = this.getContainer();
                if (c == null)
                    return;
                if (ctrlpt == null)
                    return;
                var oldr = this.getPrimRect();
                if (oldr == null)
                    return;
                var s = this.getDrawSize();
                var minv = c.transPixelLen2DrawLen(true, oc.util.CTRL_LN_MIN_PIXEL * 2);
                switch (ctrlpt) {
                    case "e":
                    case "se":
                        var w = x - this.x;
                        if (w < minv)
                            w = minv;
                        var h = w * oldr.h / oldr.w;
                        this.setDrawSize(w, h);
                        this.fontSize = h - DIIcon.TXT_ADJ_H;
                        if (this.fontSize <= 1)
                            this.fontSize = 1;
                        //console.log("fontsize="+this.fontSize);
                        this.MODEL_fireChged(["h", "w"]);
                        break;
                    case "s":
                        var h = y - this.y;
                        if (h < minv)
                            h = minv;
                        this.setDrawH(h);
                        //this.w = oldr.w*this.h/oldr.h
                        this.fontSize = h - DIIcon.TXT_ADJ_H;
                        this.MODEL_fireChged(["w", "h"]);
                        break;
                    case "w":
                        var rx = this.x + s.w;
                        this.x = x;
                        var w = rx - x;
                        if (w < minv) {
                            w = minv;
                            this.x = rx - minv;
                        }
                        var h = w * oldr.h / w;
                        this.setDrawSize(w, h);
                        this.MODEL_fireChged(["x", "w", "h"]);
                        break;
                    case "n":
                        var ry = this.y + s.h;
                        this.y = y;
                        var h = ry - y;
                        if (h < minv) {
                            h = minv;
                            this.y = ry - minv;
                        }
                        var w = oldr.w * h / oldr.h;
                        this.setDrawSize(w, h);
                        this.MODEL_fireChged(["y", "w", "h"]);
                        break;
                    case "ne":
                        var w = x - this.x;
                        if (w < minv)
                            w = minv;
                        var ry = this.y + s.h;
                        this.y = y;
                        var h = ry - y;
                        if (h < minv) {
                            h = minv;
                            this.y = ry - minv;
                        }
                        h = w * oldr.h / w;
                        this.setDrawSize(w, h);
                        this.MODEL_fireChged(["w", "y", "h"]);
                        break;
                    case "sw":
                        var h = y - this.y;
                        if (h < minv)
                            h = minv;
                        var rx = this.x + s.w;
                        this.x = x;
                        var w = rx - x;
                        if (w < minv) {
                            w = minv;
                            this.x = rx - minv;
                        }
                        h = w * oldr.h / w;
                        this.setDrawSize(w, h);
                        this.MODEL_fireChged(["h", "x", "w"]);
                        break;
                    case "nw":
                        var ry = this.y + s.h;
                        this.y = y;
                        var h = ry - y;
                        if (h < minv) {
                            h = minv;
                            this.y = ry - minv;
                        }
                        var rx = this.x + s.w;
                        this.x = x;
                        var w = rx - x;
                        if (w < minv) {
                            w = minv;
                            this.x = rx - minv;
                        }
                        h = w * oldr.h / w;
                        this.setDrawSize(w, h);
                        this.MODEL_fireChged(["x", "y", "w", "h"]);
                        break;
                }
            }
        }
        DIIcon.DIIcon_PNS = {
            _cat_name: "txt", _cat_title: "Text",
            color: { title: "Icon Color", type: "str", edit_plug: "color" },
            unicode: { title: "Icon Code", type: "str" }
        };
        DIIcon.TXT_ADJ_H = 9;
        di.DIIcon = DIIcon;
    })(di = oc.di || (oc.di = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var di;
    (function (di) {
        /**
         * support pic svg etc.
         */
        class DIImg extends oc.DrawItemRectR {
            constructor(opts) {
                super(opts);
                this.imgPath = null;
                this.imgRes = null;
                this.alpha = 1.0;
                //border: number = 0;
                //borderColor: string | null = "yellow";
                //fillColor: string | null = null;
                //radius: number | null = null;
                this.img = null;
                this.lastImgPath = null;
                if (opts != undefined) {
                    var imgp = opts["imgPath"];
                    if (imgp != undefined && imgp != null && imgp != "") {
                        this.imgPath = imgp;
                    }
                }
            }
            getClassName() {
                return "DIImg";
            }
            getPropDefs() {
                var r = super.getPropDefs();
                r.push(DIImg.DIImg_PNS);
                return r;
            }
            drawPrim(cxt) {
                var _a;
                //var dxy = this.getDrawXY();
                //console.log("img>>"+dxy.x+" "+dxy.y);
                var path = null;
                if (this.imgRes != null && this.imgRes != "") {
                    path = (_a = this.getDrawRes()) === null || _a === void 0 ? void 0 : _a.getDrawResUrl(this.imgRes);
                    //path = '/admin/util/rescxt_show_img.jsp?resid='+this.imgRes ;
                }
                if (!path) {
                    if (this.imgPath == null || this.imgPath == "")
                        return;
                    path = this.imgPath;
                }
                if (!path)
                    return;
                //cxt.save();
                cxt.globalAlpha = this.alpha;
                if (this.img != null && this.lastImgPath == path) {
                    cxt.drawImage(this.img, 0, 0, 100, 100);
                }
                else {
                    var ii = new Image();
                    ii.onload = () => {
                        this.img = ii;
                        this.MODEL_fireChged([]);
                        //cxt.drawImage(ii, 0, 0, 100, 100);
                    };
                    ii.src = this.lastImgPath = path;
                }
                //cxt.restore();
            }
            setImgResInCxt(name) {
            }
            drawPrimSel(ctx) {
            }
            getPrimRect() {
                return new oc.base.Rect(0, 0, 100, 100);
            }
            draw0(cxt, c) {
                //var c = this.getContainer();
                //if (!c)
                //	return;
                if (this.imgPath == null || this.imgPath == "")
                    return;
                var pt = this.getDrawXY();
                var dxy = c.transDrawPt2PixelPt(pt.x, pt.y);
                var dw = c.transDrawLen2PixelLen(true, this.getW());
                var dh = c.transDrawLen2PixelLen(false, this.getH());
                cxt.save();
                cxt.globalAlpha = this.alpha;
                if (this.img != null && this.lastImgPath == this.imgPath) {
                    cxt.drawImage(this.img, dxy.x, dxy.y, dw, dh);
                }
                else {
                    var ii = new Image();
                    var me = this;
                    ii.onload = function () {
                        cxt.drawImage(ii, dxy.x, dxy.y, dw, dh);
                        me.img = ii;
                    };
                    ii.src = this.lastImgPath = this.imgPath;
                }
                cxt.restore();
            }
        }
        DIImg.DIImg_PNS = {
            _cat_name: "img", _cat_title: "DI Image",
            //border: { title: "border", type: "str" },
            //borderColor: { title: "borderColor", type: "str" },
            //fillColor: { title: "fillColor", type: "str", val_tp: "color" },
            alpha: { title: "Alpha", type: "float" },
            imgPath: { title: "Image Path", type: "str" },
            imgRes: { title: "Image Res", type: "str", edit_plug: "imgres" }
        };
        di.DIImg = DIImg;
    })(di = oc.di || (oc.di = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var di;
    (function (di) {
        let DILine_CTRLPT;
        (function (DILine_CTRLPT) {
            DILine_CTRLPT[DILine_CTRLPT["PT1"] = 0] = "PT1";
            DILine_CTRLPT[DILine_CTRLPT["PT_CTRL1"] = 1] = "PT_CTRL1";
            DILine_CTRLPT[DILine_CTRLPT["PT2"] = 2] = "PT2";
            DILine_CTRLPT[DILine_CTRLPT["PT_CTRL2"] = 3] = "PT_CTRL2";
        })(DILine_CTRLPT = di.DILine_CTRLPT || (di.DILine_CTRLPT = {}));
        ;
        class DILine extends oc.DrawItem {
            constructor(opts) {
                super(opts);
                this.color = 'yellow';
                this.lnW = 1;
                this.cpx1 = 20;
                this.cpy1 = 100;
                this.x2 = 200;
                this.y2 = 20;
                this.cpx2 = 200;
                this.cpy2 = 100;
                this.bEndArrow = false;
                //oc.DrawItem.apply(this,arguments);
            }
            getClassName() {
                return "DILine";
            }
            getPropDefs() {
                var r = super.getPropDefs();
                r.push(DILine.DILine_PNS);
                return r;
            }
            getBoundPolygonDraw() {
                //return new oc.base.Polygon();
                return null;
            }
            setDrawXY(x, y) {
                var deltax = x - this.x;
                var deltay = y - this.y;
                this.x = x;
                this.y = y;
                this.cpx1 += deltax;
                this.cpy1 += deltay;
                this.x2 += deltax;
                this.y2 += deltay;
                this.cpx2 += deltax;
                this.cpy2 += deltay;
                this.MODEL_fireChged(DILine.PNS);
            }
            setDrawBeginXY(cont, x, y) {
                this.x = x;
                this.y = y;
                var dx = cont.transPixelLen2DrawLen(true, 2);
                var dy = cont.transPixelLen2DrawLen(false, 2);
                this.cpx1 = this.x + dx;
                this.cpy1 = y;
                this.x2 = this.x + dx;
                this.y2 = this.y + dy;
                this.cpx2 = x;
                this.cpy2 = this.y + dy;
                this.MODEL_fireChged(DILine.PNS);
                return true;
            }
            setDrawEndXY(cont, x, y) {
                //var xy = super.setDrawEndXY(cont,x,y);
                //x = xy.x ;
                //y = xy.y ;
                this.cpx1 = x;
                this.cpy1 = this.y;
                this.x2 = x;
                this.y2 = y;
                this.cpx2 = this.x;
                this.cpy2 = y;
                this.MODEL_fireChged(DILine.PNS);
                return { x: x, y: y };
            }
            chkDrawPtOnCtrlPt(x, y) {
                var p = this.getContainer();
                if (p == null)
                    return null;
                var r = p.transPixelLen2DrawLen(true, oc.util.CTRL_PT_R);
                if (oc.util.chkPtInRadius(this.x, this.y, x, y, r))
                    return DILine_CTRLPT.PT1;
                if (oc.util.chkPtInRadius(this.x2, this.y2, x, y, r))
                    return DILine_CTRLPT.PT2;
                if (oc.util.chkPtInRadius(this.cpx1, this.cpy1, x, y, r))
                    return DILine_CTRLPT.PT_CTRL1;
                if (oc.util.chkPtInRadius(this.cpx2, this.cpy2, x, y, r))
                    return DILine_CTRLPT.PT_CTRL2;
                return null;
            }
            setCtrlDrawPt(cpt, x, y) {
                var pns = null;
                switch (cpt) {
                    case DILine_CTRLPT.PT1:
                        this.x = x;
                        this.y = y;
                        pns = ["x", "y"];
                        break;
                    case DILine_CTRLPT.PT2:
                        this.x2 = x;
                        this.y2 = y;
                        pns = ["x2", "y2"];
                        break;
                    case DILine_CTRLPT.PT_CTRL1:
                        this.cpx1 = x;
                        this.cpy1 = y;
                        pns = ["cpx1", "cpy1"];
                        break;
                    case DILine_CTRLPT.PT_CTRL2:
                        this.cpx2 = x;
                        this.cpy2 = y;
                        pns = ["cpx2", "cpy2"];
                        break;
                }
                this.MODEL_fireChged(pns);
            }
            containDrawPt(x, y) {
                var c = this.getContainer();
                if (c == null)
                    return false;
                var ctx = this.getCxt();
                if (ctx == null)
                    return false;
                if (this.chkDrawPtOnCtrlPt(x, y))
                    return true;
                var p = c.transDrawPt2PixelPt(x, y);
                var pt = this.getDrawXY();
                var p1 = c.transDrawPt2PixelPt(pt.x, pt.y);
                var cp1 = c.transDrawPt2PixelPt(this.cpx1, this.cpy1);
                var cp2 = c.transDrawPt2PixelPt(this.cpx2, this.cpy2);
                var p2 = c.transDrawPt2PixelPt(this.x2, this.y2);
                ctx.save();
                //ctx.translate(0, 0);
                ctx.lineWidth = this.lnW;
                ctx.strokeStyle = "blue";
                ctx.beginPath();
                ctx.moveTo(p1.x, p1.y);
                ctx.bezierCurveTo(cp1.x, cp1.y, cp2.x, cp2.y, p2.x, p2.y);
                var r = ctx.isPointInPath(p.x, p.y);
                ctx.restore();
                return r;
            }
            draw(ctx, c) {
                //var c = this.getContainer();
                //if (!c)
                //	return;
                var pt = this.getDrawXY();
                var p1 = c.transDrawPt2PixelPt(pt.x, pt.y);
                var cp1 = c.transDrawPt2PixelPt(this.cpx1, this.cpy1);
                var cp2 = c.transDrawPt2PixelPt(this.cpx2, this.cpy2);
                var p2 = c.transDrawPt2PixelPt(this.x2, this.y2);
                ctx.save();
                //ctx.translate(0, 0);
                ctx.lineWidth = this.lnW;
                ctx.strokeStyle = this.color;
                ctx.beginPath();
                ctx.moveTo(p1.x, p1.y);
                ctx.bezierCurveTo(cp1.x, cp1.y, cp2.x, cp2.y, p2.x, p2.y);
                ctx.stroke();
                if (this.bEndArrow) {
                    ctx.fillStyle = this.color;
                    var arrlen = c.transDrawLen2PixelLen(true, 20);
                    var arrh = c.transDrawLen2PixelLen(true, 8);
                    oc.util.drawArrow(ctx, cp2.x, cp2.y, p2.x, p2.y, arrlen, arrh);
                }
                ctx.restore();
            }
            draw_sel(ctx, c) {
                //var c = this.getContainer();
                //if (!c)
                //	return true;//
                var pt = this.getDrawXY();
                var p1 = c.transDrawPt2PixelPt(pt.x, pt.y);
                var cp1 = c.transDrawPt2PixelPt(this.cpx1, this.cpy1);
                var cp2 = c.transDrawPt2PixelPt(this.cpx2, this.cpy2);
                var p2 = c.transDrawPt2PixelPt(this.x2, this.y2);
                ctx.save();
                //ctx.translate(0, 0);
                ctx.lineWidth = this.lnW;
                ctx.strokeStyle = this.color;
                ctx.beginPath();
                ctx.moveTo(p1.x, p1.y);
                ctx.bezierCurveTo(cp1.x, cp1.y, cp2.x, cp2.y, p2.x, p2.y);
                ctx.stroke();
                ctx.beginPath();
                ctx.lineWidth = 1;
                ctx.setLineDash([5, 2]);
                ctx.strokeStyle = "yellow";
                ctx.moveTo(p1.x, p1.y);
                ctx.lineTo(cp1.x, cp1.y);
                //ctx.arc(cp1.x, cp1.y, 6, 0, Math.PI * 2, true);
                ctx.moveTo(p2.x, p2.y);
                ctx.lineTo(cp2.x, cp2.y);
                //ctx.arc(cp2.x, cp2.y, 6, 0, Math.PI * 2, true);
                ctx.stroke();
                ctx.beginPath();
                ctx.setLineDash([]);
                ctx.moveTo(cp1.x, cp1.y);
                ctx.arc(cp1.x, cp1.y, 6, 0, Math.PI * 2, true);
                ctx.moveTo(cp2.x, cp2.y);
                ctx.arc(cp2.x, cp2.y, 6, 0, Math.PI * 2, true);
                ctx.moveTo(p1.x, p1.y);
                ctx.arc(p1.x, p1.y, 6, 0, Math.PI * 2, true);
                ctx.moveTo(p2.x, p2.y);
                ctx.arc(p2.x, p2.y, 6, 0, Math.PI * 2, true);
                ctx.stroke();
                ctx.restore();
            }
        }
        DILine.DILine_PNS = {
            _cat_name: "line", _cat_title: "Line",
            //lnTp:{title:"Line Type",type:"int",enum_val: [[0, "Normal"], [1, "Bezier"]]},
            color: { title: "color", type: "str", edit_plug: "color" },
            lnW: { title: "Line Width", type: "int" },
            cpx1: { title: "cpx1", type: "float" },
            cpy1: { title: "cpy1", type: "float" },
            x2: { title: "x2", type: "float" },
            y2: { title: "y2", type: "float" },
            cpx2: { title: "cpx2", type: "float" },
            cpy2: { title: "cpy2", type: "float" },
            bEndArrow: { title: "end arrow", type: "bool", enum_val: [[false, "none"], [1, "has"]] },
        };
        DILine.PNS = ["x", "y", "cpx1", "cpy1", "x2", "y2", "cpx2", "cpy2"];
        di.DILine = DILine;
    })(di = oc.di || (oc.di = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var di;
    (function (di) {
        /**
         *
         */
        class DIPts extends oc.DrawItem {
            constructor(opts, bln = false) {
                super(opts);
                this.color = 'yellow';
                this.lnW = 1;
                //bLn:boolean=false;//polygon default
                this.ptsPy = new oc.base.Polygon();
                if (opts != undefined) {
                    if (opts["pts"]) {
                        //ptsPy
                    }
                }
                //this.bLn = bln ;
            }
            extract() {
                var r = super.extract();
                r["pts_py"] = this.ptsPy.getPts();
                return r;
            }
            inject(opts, ignore_readonly) {
                super.inject(opts, ignore_readonly);
                var pts = opts["pts_py"];
                if (pts != null && pts != undefined)
                    this.ptsPy.setPts(pts);
            }
            recalXY() {
                var r = this.ptsPy.getBoundingBox();
                if (r != null) {
                    this.x = r.x;
                    this.y = r.y;
                }
            }
            setDrawXY(x, y) {
                var r = this.ptsPy.getBoundingBox();
                if (r == null)
                    return;
                this.recalXY();
                var dx = x - this.x;
                var dy = y - this.y;
                this.ptsPy.movePt(dx, dy);
                //this.recalXY();
                this.x = x;
                this.y = y;
                //super.setDrawXY()
                this.MODEL_fireChged(["x", "y"]);
            }
            addPt(x, y) {
                this.ptsPy.addPt(x, y);
                //this.ptsPy()
                this.recalXY();
            }
            addPts(pts) {
                for (var pt of pts) {
                    this.addPt(pt.x, pt.y);
                }
            }
            getPts() {
                return this.ptsPy.getPts();
            }
            chgPt(idx, x, y) {
                if (idx < 0 || idx >= this.ptsPy.getPtNum())
                    return false;
                this.ptsPy.chgPt(idx, x, y);
                this.recalXY();
                this.MODEL_fireChged([]);
                return true;
            }
            chgLastPt(x, y) {
                return this.chgPt(this.ptsPy.getPtNum() - 1, x, y);
            }
            chkPixelPtIdxOnPt(x, y) {
                var c = this.getContainer();
                if (c == null)
                    return null;
                for (var i = 0; i < this.ptsPy.getPtNum(); i++) {
                    var tmppt = this.ptsPy.getPt(i);
                    if (tmppt == null)
                        continue;
                    var p = c.transDrawPt2PixelPt(tmppt.x, tmppt.y);
                    if (oc.util.chkPtInRadius(p.x, p.y, x, y, oc.util.CTRL_PT_R)) {
                        return i;
                    }
                }
                return null;
            }
            setDrawPtIdx(idx, x, y) {
                if (idx < 0 || idx >= this.ptsPy.getPtNum())
                    return;
                this.ptsPy.chgPt(idx, x, y);
                this.recalXY();
                this.MODEL_fireChged([]);
            }
            setLnW(w) {
                this.lnW = w;
            }
            setLnColor(c) {
                this.color = c;
            }
            getPropDefs() {
                var r = super.getPropDefs();
                r.push(DIPts.PNS_PTS);
                return r;
            }
            // public getBoundPolygonDraw()
            // {
            //     //return new oc.base.Polygon();
            //     return null;
            // }
            getBoundPolygonDraw() {
                return this.ptsPy;
            }
            draw_sel(cxt, c, color) {
                if (!this.ptsPy.isValid())
                    return;
                cxt.save();
                cxt.beginPath();
                cxt.strokeStyle = color;
                cxt.lineWidth = 1;
                for (var tmppt of this.ptsPy.getPts()) {
                    var pt = c.transDrawPt2PixelPt(tmppt.x, tmppt.y);
                    cxt.moveTo(pt.x, pt.y);
                    cxt.arc(pt.x, pt.y, oc.util.CTRL_PT_R, 0, Math.PI * 2, true);
                }
                cxt.stroke();
                var r = this.ptsPy.calculateBounds();
                if (r != null) {
                    var p1 = c.transDrawPt2PixelPt(r.x, r.y);
                    var w = c.transDrawLen2PixelLen(true, r.w);
                    var h = c.transDrawLen2PixelLen(false, r.h);
                    oc.util.drawRectEmpty(cxt, p1.x, p1.y, w, h, null);
                }
                cxt.restore();
            }
        }
        DIPts.PNS_PTS = {
            _cat_name: "di_pts", _cat_title: "DI Pts",
            color: { title: "color", type: "str", edit_plug: "color" },
            lnW: { title: "Line Width", type: "int" },
        };
        di.DIPts = DIPts;
    })(di = oc.di || (oc.di = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var di;
    (function (di) {
        /**
         *
         */
        class DIPtsLn extends di.DIPts {
            constructor(opts, bln = false) {
                super(opts);
                this.lineTp = "";
                this.arrowHead = "";
                this.arrowTail = "";
                if (opts != undefined) {
                    if (opts["pts"]) {
                        //ptsPy
                    }
                }
            }
            getClassName() {
                return "oc.di.DIPtsLn";
            }
            draw(ctx, c) {
                if (!this.ptsPy.isValid())
                    return;
                ctx.save();
                this.drawLn(ctx, c);
                ctx.restore();
            }
            drawLn(ctx, c) {
                var pts = this.ptsPy.getPts();
                if (pts.length < 2)
                    return;
                ctx.beginPath();
                ctx.strokeStyle = this.color;
                ctx.lineWidth = this.lnW;
                ctx.lineCap = "round";
                //ctx.lin
                ctx.lineJoin = "round";
                ctx.miterLimit = 5;
                var p = pts[0];
                p = c.transDrawPt2PixelPt(p.x, p.y);
                ctx.moveTo(p.x, p.y);
                for (var i = 1; i < pts.length; i++) {
                    //ctx.beginPath();
                    var pn = c.transDrawPt2PixelPt(pts[i].x, pts[i].y);
                    //pipe linear g
                    ctx.lineTo(pn.x, pn.y);
                    p = pn;
                    ctx.stroke();
                    //ctx.closePath();
                }
                //ctx.closePath();
                return;
            }
        }
        di.DIPtsLn = DIPtsLn;
    })(di = oc.di || (oc.di = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var di;
    (function (di) {
        /**
         *
         */
        class DIPtsPipe extends di.DIPts {
            constructor(opts) {
                super(opts);
                if (opts != undefined) {
                }
                this.lnW = 18;
            }
            getClassName() {
                return "oc.di.DIPtsPipe";
            }
            draw(ctx, c) {
                if (!this.ptsPy.isValid())
                    return;
                ctx.save();
                this.drawPipe(ctx, c);
                ctx.restore();
            }
            drawPipe(ctx, c) {
                var pts = this.ptsPy.getPts();
                if (pts.length < 2)
                    return;
                ctx.beginPath();
                ctx.strokeStyle = this.color;
                ctx.lineWidth = this.lnW;
                ctx.lineCap = "round";
                ctx.lineJoin = "miter";
                ctx.miterLimit = 5;
                var p = pts[0];
                p = c.transDrawPt2PixelPt(p.x, p.y);
                for (var i = 1; i < pts.length; i++) {
                    ctx.beginPath();
                    var pn = c.transDrawPt2PixelPt(pts[i].x, pts[i].y);
                    var g = oc.base.Fill.calCxtPipeFillStyle(p.x, p.y, pn.x, pn.y, this.lnW, this.color, ctx);
                    if (g != null)
                        ctx.strokeStyle = g;
                    ctx.moveTo(p.x, p.y);
                    //pipe linear g
                    ctx.lineTo(pn.x, pn.y);
                    p = pn;
                    ctx.stroke();
                    //ctx.closePath();
                }
                //ctx.closePath();
                return;
            }
            drawJoin(ctx, c, pa, pb, pc) {
                var d = this.lnW / 2;
                var ang = this.calAngle(pa, pb, pc) / 2;
                var k = d / Math.tan(ang);
            }
            calAngle(a, b, c) {
                var AB = Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
                var AC = Math.sqrt(Math.pow(a.x - c.x, 2) + Math.pow(a.y - c.y, 2));
                var BC = Math.sqrt(Math.pow(b.x - c.x, 2) + Math.pow(b.y - c.y, 2));
                var cosA = (Math.pow(AB, 2) + Math.pow(AC, 2) - Math.pow(BC, 2)) / (2 * AB * AC);
                return Math.round(Math.acos(cosA) * 180 / Math.PI);
            }
            //private calPt5In
            /**
             * calculate 5 pts in one pipe seg
             * 需要使用面图计算管道交叉点，并进行连接点和rad梯度和管道linear梯度
             * @param ctx
             * @param c
             */
            drawPipeSeg(ctx, c) {
            }
        }
        di.DIPtsPipe = DIPtsPipe;
    })(di = oc.di || (oc.di = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var di;
    (function (di) {
        /**
         *
         */
        class DIPtsPy extends di.DIPts {
            constructor(opts) {
                super(opts);
                this.fillColor = "#cccccc";
                this.fillR = 0; //0-360
            }
            getClassName() {
                return "oc.di.DIPtsPy";
            }
            getPropDefs() {
                var r = super.getPropDefs();
                r.push(DIPtsPy.PNS_PTSPY);
                return r;
            }
            setFillColor(c) {
                this.fillColor = c;
            }
            draw(ctx, c) {
                if (!this.ptsPy.isValid())
                    return;
                ctx.save();
                this.drawPy(ctx, c);
                ctx.restore();
            }
            drawPy(ctx, c) {
                ctx.beginPath();
                ctx.strokeStyle = this.color;
                ctx.lineWidth = this.lnW;
                var pts = this.ptsPy.getPts();
                for (var tmppt of pts) {
                    var p = c.transDrawPt2PixelPt(tmppt.x, tmppt.y);
                    ctx.lineTo(p.x, p.y);
                }
                var p = c.transDrawPt2PixelPt(pts[0].x, pts[0].y);
                ctx.lineTo(p.x, p.y);
                ctx.stroke();
                // if (this.fillColor && this.fillColor != "")
                // {
                //     ctx.fillStyle = this.fillColor;
                //     ctx.closePath();
                //     ctx.fill();
                // }
                var r = this.ptsPy.getBoundingBox();
                if (r != null) {
                    var p1 = c.transDrawPt2PixelPt(r.x, r.y);
                    var p2 = c.transDrawPt2PixelPt(r.getMaxX(), r.getMaxY());
                    var tmpr = new oc.base.Rect(p1.x, p1.y, p2.x - p1.x, p2.y - p1.y);
                    // var linear = ctx.createLinearGradient(p1.x,p1.y,p2.x,p2.y);
                    // linear.addColorStop(0,'#fff');
                    // linear.addColorStop(0.5,'#f0f');
                    // linear.addColorStop(1,'rgba(112,112,112,0.6)');
                    // ctx.
                    var bcolor = this.calLinearBorderColor(this.fillColor);
                    var f = oc.base.Fill.createLinearG([bcolor, this.fillColor, bcolor], this.fillR);
                    ctx.fillStyle = f.calCxtFillStyle(tmpr, ctx);
                    ctx.closePath();
                    ctx.fill();
                }
                return;
            }
            tranInt2HexStr(i) {
                var str = i.toString(16);
                if (str.length == 1)
                    return "0" + str;
                else
                    return str;
            }
            calLinearBorderColor(c) {
                var r, g, b;
                if (c.length == 7 && c.indexOf("#") == 0) {
                    r = parseInt(c.substr(1, 2), 16);
                    g = parseInt(c.substr(3, 2), 16);
                    b = parseInt(c.substr(5, 2), 16);
                }
                else {
                    return "#000000";
                }
                var hsv = oc.util.transRGB2HSV([r, g, b]);
                hsv[2] = hsv[2] / 2;
                var rgb = oc.util.transHSV2RGB(hsv);
                return "#" + this.tranInt2HexStr(rgb[0]) + this.tranInt2HexStr(rgb[1]) + this.tranInt2HexStr(rgb[2]);
            }
        }
        DIPtsPy.PNS_PTSPY = {
            _cat_name: "di_ptspy", _cat_title: "Polygon",
            fillColor: { title: "Fill Color", type: "str", edit_plug: "color" },
            fillR: { title: "Fill Rotate", type: "int" },
        };
        di.DIPtsPy = DIPtsPy;
    })(di = oc.di || (oc.di = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var di;
    (function (di) {
        class DIRect extends oc.DrawItemRectR {
            constructor(opts) {
                super(opts);
                // w: number = 100;
                // h: number = 100;
                this.border = 0;
                this.borderColor = "yellow";
                this.fillColor = null;
                this.radius = null;
            }
            getClassName() {
                return "DIRect";
            }
            getPropDefs() {
                var r = super.getPropDefs();
                r.push(DIRect.DIRect_PNS);
                return r;
            }
            // public getBoundRectDraw()
            // {
            // 	var pt = this.getDrawXY();
            // 	console.log("rect bound="+pt.x+" "+pt.y +" "+this.w+" "+this.h);
            // 	return new oc.base.Rect(pt.x, pt.y, this.w, this.h);
            // }
            getPrimRect() {
                //var pt = this.getDrawXY();
                return new oc.base.Rect(0, 0, 100, 100);
            }
            drawPrim(ctx) {
                //ctx.rect(0,0,100,100);
                oc.util.drawRect(ctx, 0, 0, 100, 100, this.radius, this.fillColor, this.border, this.borderColor);
                //ctx.stroke();
            }
            drawPrimSel(ctx) {
            }
            draw0(cxt, c) {
                //var c = this.getContainer();
                //if (!c)
                //	return;
                var pt = this.getDrawXY();
                var dxy = c.transDrawPt2PixelPt(pt.x, pt.y);
                var dw = c.transDrawLen2PixelLen(true, this.getW());
                var dh = c.transDrawLen2PixelLen(false, this.getH());
                var dr = null;
                if (this.radius != null && this.radius != undefined)
                    dr = c.transDrawLen2PixelLen(true, this.radius);
                var lw = null;
                if (this.border != null && this.border != undefined)
                    lw = c.transDrawLen2PixelLen(true, this.border);
                oc.util.drawRect(cxt, dxy.x, dxy.y, dw, dh, dr, this.fillColor, lw, this.borderColor);
            }
        }
        DIRect.DIRect_PNS = {
            _cat_name: "direct", _cat_title: "DI Rectangle",
            //w: { title: "width", type: "int" },
            //h: { title: "height", type: "int" },
            border: { title: "border", type: "str" },
            borderColor: { title: "borderColor", type: "str", edit_plug: "color" },
            fillColor: { title: "fillColor", type: "color", val_tp: "color", edit_plug: true },
            radius: { title: "radius", type: "int" }
        };
        di.DIRect = DIRect;
    })(di = oc.di || (oc.di = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var di;
    (function (di) {
        class DITxt extends oc.DrawItemRectR {
            constructor(opts) {
                super(opts);
                this.txt = '#text';
                this.font = "serif";
                this.fontSize = 30;
                this.fontColor = "yellow";
                this.boundDrawRect = null;
            }
            getClassName() {
                return "DITxt";
            }
            getPropDefs() {
                var r = super.getPropDefs();
                r.push(DITxt.DITxt_PNS);
                return r;
            }
            setDrawBeginXY(cont, x, y) {
                this.x = x;
                this.y = y;
                //this.txt = "";
                return false; //not end and continue;
            }
            calBoundRectDraw() {
                var c = this.getContainer();
                if (!c)
                    return null;
                var cxt = this.getCxt();
                if (cxt == null)
                    return null;
                var pt = this.getDrawXY();
                //var ppt = c.transDrawPt2PixelPt(pt.x, pt.y);
                var fs = c.transDrawLen2PixelLen(false, this.fontSize);
                cxt.save();
                //cxt.font = this.fontSize+"px serif";
                cxt.font = fs + "px " + this.font;
                cxt.fillStyle = this.fontColor;
                var tm = cxt.measureText(this.txt);
                //tm.fontBoundingBoxAscent
                var tw_px = tm.width + 5;
                var tw_dr = c.transPixelLen2DrawLen(true, tw_px);
                //console.log("fs="+fs+" txt px width="+tw_px+" drwidth="+tw_dr);
                var r = new oc.base.Rect(pt.x, pt.y, tw_dr, this.fontSize + 8);
                cxt.restore();
                return r;
            }
            getPrimRect() {
                return new oc.base.Rect(0, 0, this.getW(), this.fontSize + DITxt.TXT_ADJ_H);
            }
            // public getPrimRect1():oc.base.Rect|null
            // {
            // 	if(this.boundDrawRect!=null)
            // 		return this.boundDrawRect;
            // 	var c = this.getContainer();
            // 	this.boundDrawRect = new base.Rect(0, 0, this.getW(), this.fontSize + DITxt.TXT_ADJ_H);
            // 	return this.boundDrawRect;
            // }
            decodeHexTo(str) {
                str = str.replace(/\\/g, "%");
                return unescape(str);
            }
            drawPrim(cxt) {
                cxt.save();
                //console.log(`${this.fontSize}px ${this.font}`)
                cxt.font = `${this.fontSize}px ${this.font}`;
                cxt.fillStyle = this.fontColor;
                var txt = this.txt;
                if (this.font == "fontawesome") {
                    txt = this.decodeHexTo(txt);
                }
                cxt.fillText(txt, 0, this.fontSize);
                //var mt = cxt.measureText(this.txt);
                //var tw_px = mt.width + 5;
                //this.boundDrawRect = new base.Rect(0, 0, tw_px, this.fontSize + DITxt.TXT_ADJ_H);
                cxt.restore();
            }
            drawPrimSel(ctx) {
            }
            draw0(cxt, c) {
                //var c = this.getContainer();
                //if (!c)
                //	return;
                var pt = this.getDrawXY();
                var ppt = c.transDrawPt2PixelPt(pt.x, pt.y);
                var fs = c.transDrawLen2PixelLen(false, this.fontSize);
                cxt.save();
                //cxt.font = this.fontSize+"px serif";
                cxt.font = fs + "px " + this.font;
                cxt.fillStyle = this.fontColor;
                cxt.fillText(this.txt, ppt.x, ppt.y + fs);
                var tw_px = cxt.measureText(this.txt).width + 5;
                var tw_dr = c.transPixelLen2DrawLen(true, tw_px);
                //console.log("fs="+fs+" txt px width="+tw_px+" drwidth="+tw_dr);
                this.boundDrawRect = new oc.base.Rect(pt.x, pt.y, tw_dr, this.fontSize + DITxt.TXT_ADJ_H);
                cxt.restore();
            }
            changeRect(ctrlpt, x, y) {
                var c = this.getContainer();
                if (c == null)
                    return;
                if (ctrlpt == null)
                    return;
                //var oldr = this.boundDrawRect;
                //if(oldr==null)
                //	return ;
                var s = this.getDrawSize();
                var minv = c.transPixelLen2DrawLen(true, oc.util.CTRL_LN_MIN_PIXEL * 2);
                switch (ctrlpt) {
                    case "e":
                    case "se":
                        var w = x - this.x;
                        if (w < minv)
                            w = minv;
                        //this.h = this.w*oldr.h/oldr.w;
                        this.setDrawW(w);
                        this.fontSize = s.h - DITxt.TXT_ADJ_H;
                        if (this.fontSize <= 1)
                            this.fontSize = 1;
                        //console.log("fontsize="+this.fontSize);
                        this.MODEL_fireChged(["h", "w", "fontSize"]);
                        break;
                    case "s":
                        var h = y - this.y;
                        if (h < minv)
                            h = minv;
                        //this.w = oldr.w*this.h/oldr.h
                        this.setDrawH(h);
                        this.fontSize = h - DITxt.TXT_ADJ_H;
                        this.MODEL_fireChged(["w", "h"]);
                        break;
                    case "w":
                        var rx = this.x + s.w;
                        this.x = x;
                        var w = rx - x;
                        if (w < minv) {
                            w = minv;
                            this.x = rx - minv;
                        }
                        this.setDrawW(w);
                        //this.h = this.w*oldr.h/this.w;
                        this.MODEL_fireChged(["x", "w", "h"]);
                        break;
                    case "n":
                        var ry = this.y + s.h;
                        this.y = y;
                        var h = ry - y;
                        if (h < minv) {
                            h = minv;
                            this.y = ry - minv;
                        }
                        this.setDrawH(h);
                        //this.w = oldr.w*this.h/oldr.h
                        this.MODEL_fireChged(["y", "w", "h"]);
                        break;
                    case "ne":
                        var w = x - this.x;
                        if (w < minv)
                            w = minv;
                        var ry = this.y + s.h;
                        this.y = y;
                        var h = ry - y;
                        if (h < minv) {
                            h = minv;
                            this.y = ry - minv;
                        }
                        this.setDrawSize(w, h);
                        //this.h = this.w*oldr.h/this.w;
                        this.MODEL_fireChged(["w", "y", "h"]);
                        break;
                    case "sw":
                        var h = y - this.y;
                        if (h < minv)
                            h = minv;
                        var rx = this.x + s.w;
                        this.x = x;
                        var w = rx - x;
                        if (w < minv) {
                            w = minv;
                            this.x = rx - minv;
                        }
                        this.setDrawSize(w, h);
                        //this.h = this.w*oldr.h/this.w;
                        this.MODEL_fireChged(["h", "x", "w"]);
                        break;
                    case "nw":
                        var ry = this.y + s.h;
                        this.y = y;
                        var h = ry - y;
                        if (h < minv) {
                            h = minv;
                            this.y = ry - minv;
                        }
                        var rx = this.x + s.w;
                        this.x = x;
                        var w = rx - x;
                        if (w < minv) {
                            w = minv;
                            this.x = rx - minv;
                        }
                        this.setDrawSize(w, h);
                        //this.h = this.w*oldr.h/this.w;
                        this.MODEL_fireChged(["x", "y", "w", "h"]);
                        break;
                }
            }
        }
        DITxt.DITxt_PNS = {
            _cat_name: "txt", _cat_title: "Text",
            //w:{title:"width",type:"int"},
            //h:{title:"height",type:"int"},
            txt: { title: "text", type: "str" },
            font: { title: "Font", type: "str", enum_val: [["serif", "serif"], ["fontawesome", "FontAwesome"]] },
            fontSize: { title: "Font Size", type: "int" },
            fontColor: { title: "Font Color", type: "str", edit_plug: "color" }
        };
        DITxt.TXT_ADJ_H = 9;
        di.DITxt = DITxt;
    })(di = oc.di || (oc.di = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var hmi;
    (function (hmi) {
        /**
         * component may expose prop interface when in designing
         */
        class CompInterProp {
            constructor(opt) {
                this.n = "";
                this.t = "";
                this.tp = "str"; //number color 
                this.onSetJS = null;
                this.onSetFunc = null;
                this.onGetJS = null;
                this.onGetFunc = null;
                if (opt) {
                    this.n = opt.n;
                    this.setBy(opt);
                }
            }
            toItem() {
                return { n: this.n, t: this.t, onGetJS: this.onGetJS, onSetJS: this.onSetJS };
            }
            toPropDef() {
                return { title: this.t, type: this.tp, binder: true };
            }
            setBy(ipi) {
                var _a, _b;
                this.t = ipi.t;
                this.onGetJS = ipi.onGetJS;
                this.onSetJS = ipi.onSetJS;
                this.onSetFunc = null;
                this.onGetFunc = null;
                if (((_a = this.onSetJS) === null || _a === void 0 ? void 0 : _a.trim()) == "")
                    this.onSetJS = null;
                if (((_b = this.onGetJS) === null || _b === void 0 ? void 0 : _b.trim()) == "")
                    this.onGetJS = null;
            }
            getName() {
                return this.n;
            }
            getTitle() {
                return this.t;
            }
            getTp() {
                return this.tp;
            }
            isIgnoreSetNull() {
                return true;
            }
            getOnGetJS() {
                return this.onGetJS;
            }
            hasOnGetJS() {
                return this.onGetJS != null;
            }
            // public setOnGetJS(js:string|null)
            // {
            //     this.onGetJS = js ;
            //     this.onGetFunc = null;
            // }
            getOnSetJS() {
                return this.onSetJS;
            }
            hasOnSetJS() {
                return this.onSetJS != null;
            }
            // public setOnSetJS(js:string|null)
            // {
            //     this.onSetJS = js ;
            //     this.onSetFunc = null;
            // }
            runGet(ins) {
                if (this.onGetJS == null)
                    return undefined;
                if (this.onGetFunc == null) {
                    this.onGetFunc = new Function("$_this", this.onGetJS);
                }
                return this.onGetFunc(ins);
            }
            runSet(ins, v) {
                if (this.onSetJS == null)
                    return;
                if (this.onSetFunc == null) {
                    this.onSetFunc = new Function("$_this", "value", this.onSetJS);
                }
                return this.onSetFunc(ins, v);
            }
        }
        hmi.CompInterProp = CompInterProp;
        class CompInterEvent {
            constructor(ei) {
                this.n = "";
                this.t = "";
                this.n = ei.n;
                this.t = ei.t;
            }
            toItem() {
                return { n: this.n, t: this.t };
            }
            setBy(ipi) {
                this.t = ipi.t;
            }
            getName() {
                return this.n;
            }
            getTitle() {
                return this.t;
            }
            fireInsValue(ins, v) {
            }
        }
        hmi.CompInterEvent = CompInterEvent;
        class CompInter {
            constructor() {
                this.interProps = [];
                this.interEvents = [];
            }
            injectInter(ii) {
                for (var ci of ii.props) {
                    var cc = new CompInterProp(ci);
                    this.interProps.push(cc);
                }
                for (var ei of ii.events) {
                    var ie = new CompInterEvent(ei);
                    this.interEvents.push(ie);
                }
            }
            extractInter() {
                var ps = [];
                for (var ci of this.interProps) {
                    var ii = ci.toItem();
                    ps.push(ii);
                }
                var es = [];
                for (var ie of this.interEvents) {
                    var ei = ie.toItem();
                    es.push(ei);
                }
                return { props: ps, events: es };
            }
            getInterProps() {
                return this.interProps;
            }
            getInterPropByName(n) {
                for (var cc of this.interProps) {
                    if (cc.getName() == n)
                        return cc;
                }
                return null;
            }
            setInterProp(n, ci) {
                if (ci == null) { //del
                    for (var i = 0; i < this.interProps.length; i++) {
                        if (this.interProps[i].n == n) {
                            this.interProps.splice(i, 1);
                            return true;
                        }
                    }
                    return true;
                }
                if (n.indexOf("_") == 0) {
                    return false;
                }
                var oci = this.getInterPropByName(n);
                if (oci == null) {
                    oci = new CompInterProp(ci);
                    this.interProps.push(oci);
                }
                else {
                    oci.setBy(ci);
                }
                return true;
            }
            getInterEvents() {
                return this.interEvents;
            }
            getInterEventByName(n) {
                for (var cc of this.interEvents) {
                    if (cc.getName() == n)
                        return cc;
                }
                return null;
            }
            setInterEvent(n, ci) {
                if (ci == null) { //del
                    for (var i = 0; i < this.interEvents.length; i++) {
                        if (this.interEvents[i].n == n) {
                            this.interEvents.splice(i, 1);
                            return true;
                        }
                    }
                    return true;
                }
                if (n.indexOf("_") == 0) {
                    return false;
                }
                var oci = this.getInterEventByName(n);
                if (oci == null) {
                    oci = new CompInterEvent(ci);
                    this.interEvents.push(oci);
                }
                else {
                    oci.setBy(ci);
                }
                return true;
            }
        }
        hmi.CompInter = CompInter;
        class HMICompCat {
            constructor(catid, title) {
                this.catId = "";
                this.catTitle = "";
                this.catId = catid;
                this.catTitle = title;
            }
            getDrawResUrl(name) {
                return "/admin/util/rescxt_show_img.jsp?resid=ccat_" + this.catId + "-" + name;
            }
            getDrawResParent() {
                return null;
            }
        }
        hmi.HMICompCat = HMICompCat;
        /**
         * related hmi comp
         */
        class HMIComp extends oc.DrawItems {
            constructor(opts) {
                super(opts);
                this.catId = "";
                this.catTitle = "";
                this.compId = "";
                this.compTitle = "";
                this.ins_alias_map = null;
                //private extProps:oc.base.Props<any>|null=null;
                //private aliasMap:oc.base.Props<string[]>|null=null;
                /**
                 * for component interface definition for every instance
                 * and it has props and events
                 */
                this.inter = new CompInter();
                this.interPropDefsForIns = null;
            }
            static calcDrawResUrl(compid, name) {
                return "/admin/util/rescxt_show_img.jsp?resid=comp_" + compid + "-" + name;
            }
            getDrawResUrl(name) {
                return HMIComp.calcDrawResUrl(this.compId, name);
            }
            getDrawResParent() {
                return new HMICompCat(this.catId, this.catTitle);
            }
            getCompId() {
                return this.compId;
            }
            setCompId(compid) {
                this.compId = compid;
            }
            getClassName() {
                return "oc.hmi.HMIComp";
            }
            /**
             * for unit template edit to show temp prop
             */
            static getInsTempDefs() {
                return HMIComp.INS_TEMP_DEFS;
            }
            static getUnitPns() {
                if (HMIComp.Unit_PNS != null)
                    return HMIComp.Unit_PNS;
                var r = {
                    _cat_name: "unit", _cat_title: "Unit",
                    cat: { title: "Cat", type: "str" },
                };
                for (var n in HMIComp.INS_TEMP_DEFS) {
                    r[n] = HMIComp.INS_TEMP_DEFS[n];
                }
                HMIComp.Unit_PNS = r;
                return r;
            }
            getInterPropDefsForIns() {
                if (this.interPropDefsForIns != null)
                    return this.interPropDefsForIns;
                var r = {
                    _cat_name: "comp_inter", _cat_title: "Comp Interface",
                };
                for (var ip of this.inter.getInterProps()) {
                    r[ip.n] = ip.toPropDef();
                }
                this.interPropDefsForIns = r;
                return this.interPropDefsForIns;
            }
            getPropDefs() {
                var r = super.getPropDefs();
                r.push(HMIComp.getUnitPns());
                return r;
            }
            getCatId() {
                return this.catId;
            }
            setCat(c) {
                this.catId = c;
                this.MODEL_fireChged(["cat"]);
            }
            getTitle() {
                return this.title;
            }
            setTitle(t) {
                this.title = t;
                this.MODEL_fireChged(["title"]);
            }
            inject(opts, ignore_readonly) {
                if (typeof (opts) == 'string')
                    eval("opts=" + opts);
                super.inject(opts, ignore_readonly);
                var inter = opts[HMIComp.PN_INTER];
                if (inter != undefined && inter != null)
                    this.inter.injectInter(inter);
                this.title = opts["title"] ? opts["title"] : "";
                var dis = opts["dis"];
                if (dis) {
                    for (var i = 0; i < dis.length; i++) {
                        var it = dis[i];
                        var cn = it._cn;
                        if (!cn)
                            continue;
                        var item = oc.DrawItem.createByClassName(cn, undefined);
                        if (item == null)
                            continue;
                        item.inject(it, false);
                        this.addItem(item);
                        //console.log(" draw unit cn="+cn+"  item="+item);
                    }
                }
            }
            extract() {
                var r = super.extract();
                var interitem = this.inter.extractInter();
                r[HMIComp.PN_INTER] = interitem;
                return r;
            }
            getCompInter() {
                return this.inter;
            }
            getCompDrawSize() {
                var n = this.items.length;
                if (n <= 0)
                    return { w: 100, h: 100 };
                var last = this.items[n - 1];
                var r;
                if (last instanceof oc.DrawItemRectBorder) {
                    var tmps = [];
                    for (var i = 0; i < n - 1; i++)
                        tmps.push(this.items[i]);
                    r = oc.ItemsContainer.calcRect(tmps);
                }
                else
                    r = oc.ItemsContainer.calcRect(this.items);
                if (r == null)
                    return { w: 100, h: 100 };
                return { w: r.w, h: r.h };
            }
            /**
             * to fit for list with fix square size
             * a square border sub item is needed to be add
             */
            addSquareBorder() {
                this.getDrawSize();
                var r = oc.ItemsContainer.calcRect(this.items);
                if (r == null)
                    return false;
                //console.log(r);
                r = r.expandToSquareByCenter();
                //console.log(r);
                var tmpi = new oc.DrawItemRectBorder({ rect: r });
                this.addItem(tmpi);
                return true;
            }
            static setAjaxLoadUrl(u) {
                HMIComp.ajaxLoadUrl = u;
            }
            static setComp(u) {
                HMIComp.compid2item[u.getCompId()] = u;
            }
            static addCompByJSON(json) {
                if (typeof (json) == 'string')
                    eval("json=" + json);
                var u = new HMIComp(undefined);
                u.inject(json, false);
                var ext = json["_ext"];
                if (ext != undefined && ext != null) {
                    u.setDynData(ext, false);
                }
                HMIComp.setComp(u);
            }
            static getItemByCompId(id) {
                var u = HMIComp.compid2item[id];
                if (u == "")
                    return null;
                if (u == null || u == undefined)
                    return null;
                return u;
            }
            static getOrLoadItemByCompId(compins) {
                if (HMIComp.ajaxLoadUrl == null)
                    return;
                var compid = compins.getCompId();
                if (compid == null)
                    return;
                if (compid.startsWith("d_")) {
                }
                else if (compid.startsWith("r_")) {
                }
                else {
                }
                var u = HMIComp.compid2item[compid];
                if (u != undefined && u != null) {
                    if (u == "")
                        return;
                    compins.onCompSet(u);
                    return;
                }
                var ldins = HMIComp.compid2loadIns[compid];
                if (ldins == undefined || ldins == null) {
                    ldins = [];
                    HMIComp.compid2loadIns[compid] = ldins;
                }
                ldins.push(compins);
                oc.util.doAjax(HMIComp.ajaxLoadUrl, { compid: compid }, (bsucc, ret) => {
                    if (!bsucc || compid == null)
                        return;
                    var compi = new HMIComp({});
                    compi.setCompId(compid);
                    if (typeof (ret) == "string")
                        eval("ret=" + ret);
                    compi.inject(ret, undefined);
                    HMIComp.compid2item[compid] = compi;
                    var tmpins = HMIComp.compid2loadIns[compid];
                    for (var ins of tmpins) {
                        ins.onCompSet(compi);
                    }
                });
            }
            static getCompJSONStr(compid) {
                var du = HMIComp.getItemByCompId(compid);
                if (du == null)
                    return null;
                var ob = du.extract();
                return JSON.stringify(ob);
            }
        }
        HMIComp.PN_INTER = "_comp_inter";
        HMIComp.INS_TEMP_DEFS = {
            ins_act_w: { title: "ins_act_w", type: "float" },
            ins_act_h: { title: "ins_act_h", type: "float" },
            ins_alias_map: { title: "ins_alias_map", type: "str", multi_lns: true },
        };
        HMIComp.Unit_PNS = null;
        /**
         *
         */
        // private drawComp(cxt: CanvasRenderingContext2D, c: IDrawItemContainer, dyn_ps: {})
        // {
        // 	for (var item of this.items)
        // 	{
        // 		var n = item.getName();
        // 		if (n != null && n != "")
        // 		{
        // 			var dynp = dyn_ps[n];
        // 			if (dynp != undefined && dynp != null)
        // 			{
        // 			}
        // 		}
        // 		item.draw(cxt, c);
        // 	}
        // }
        HMIComp.compid2item = {};
        HMIComp.ajaxLoadUrl = null;
        HMIComp.compid2loadIns = {};
        hmi.HMIComp = HMIComp;
        /**
         * comp instance of item
         */
        class HMICompIns extends oc.DrawItemRect {
            constructor(opts) {
                super(opts);
                this.borderPixel = null;
                this.borderColor = "yellow";
                this.fillColor = null;
                this.radius = null;
                this.compId = null;
                //compInterPns:string|null=null;
                this.hmiComp = null;
                /**
                 * deep copy of DrawItem
                 */
                this.dynComp = null;
                this.innerCont = null;
                this.firstInjectOb = null;
            }
            setCompId(n) {
                this.compId = n;
                this.MODEL_fireChged(["compId"]);
                //load comp asyn
                HMIComp.getOrLoadItemByCompId(this);
            }
            getCompId() {
                return this.compId;
            }
            setInterPropVal(name, v) {
                if (this.dynComp == null)
                    return false;
                var cip = this.dynComp.getCompInter().getInterPropByName(name);
                if (cip == null || !cip.hasOnSetJS())
                    return false;
                if ((v == undefined || v == null) && cip.isIgnoreSetNull())
                    return false;
                cip.runSet(this, v);
                return true;
            }
            inject(opts, ignore_readonly) {
                super.inject(opts, ignore_readonly);
                if (this.firstInjectOb == null)
                    this.firstInjectOb = opts;
                if (this.dynComp != null) { //
                    for (var tmpn in opts) {
                        var v = this[tmpn];
                        this.setInterPropVal(tmpn, v);
                    }
                }
                if (this.compId != null && this.compId != undefined) {
                    //load comp asyn
                    HMIComp.getOrLoadItemByCompId(this);
                }
            }
            extract() {
                var r = super.extract();
                if (this.dynComp == null)
                    return r;
                var ipdf = this.dynComp.getInterPropDefsForIns();
                for (var tmpn in ipdf) {
                    if (tmpn.indexOf("_") == 0)
                        continue;
                    var cip = this.dynComp.getCompInter().getInterPropByName(tmpn);
                    if (cip == null)
                        continue;
                    if (!cip.hasOnGetJS())
                        continue;
                    var v = cip.runGet(this);
                    if ((v == undefined || v == null))
                        continue;
                    r[tmpn] = v;
                }
                return r;
            }
            /**
             * override to provider res
             */
            getDrawRes() {
                return this.hmiComp;
            }
            onCompSet(comp) {
                this.hmiComp = comp;
                this.compId = comp.getCompId();
                this.MODEL_fireChged([]);
                this.dynComp = comp.duplicateMe();
                var c = this.getContainer();
                if (this.dynComp != null && c != null)
                    this.dynComp.setContainer(c, this.getLayer());
                for (var tmpdi of this.dynComp.getItemsShow())
                    tmpdi.parentNode = this;
                //interface prop may be set
                this.updateInInterProps();
                this.MODEL_fireChged([]);
                return this.dynComp;
            }
            updateInInterProps() {
                if (this.dynComp == null)
                    return;
                var ipdf = this.dynComp.getInterPropDefsForIns();
                for (var tmpn in ipdf) {
                    if (tmpn.indexOf("_") == 0)
                        continue;
                    var cip = this.dynComp.getCompInter().getInterPropByName(tmpn);
                    if (cip == null)
                        continue;
                    var v = this[tmpn];
                    if ((v == undefined || v == null) && this.firstInjectOb != null) {
                        v = this.firstInjectOb[tmpn]; //
                        this[tmpn] = v;
                    }
                    if ((v == undefined || v == null) && cip.isIgnoreSetNull())
                        continue;
                    cip.runSet(this, v);
                }
            }
            getComp() {
                // if (this.dynComp != null)
                //     return this.dynComp;
                // if (this.compId == null || this.compId == "")
                //     return null;
                // var du = HMIComp.getItemByCompId(this.compId);
                // if (du == null)
                //     return null;
                // this.dynComp = du.duplicateMe() as HMIComp;
                // var c = this.getContainer();
                // if (this.dynComp != null && c != null)
                //     this.dynComp.setContainer(c, this.getLayer());
                // return this.dynComp;
                return this.dynComp;
            }
            getInnerDrawItemByName(n) {
                var u = this.getComp();
                if (u == null)
                    return null;
                for (var i of u.getItemsShow()) {
                    if (n == i.getName())
                        return i;
                }
                return null;
            }
            $(ob) {
                if (typeof (ob) == 'string') {
                    return this.getInnerDrawItemByName(ob);
                }
                else {
                    var b = false;
                    for (var n in ob) {
                        var tmpi = this.getInnerDrawItemByName(n);
                        if (tmpi == null)
                            continue;
                        tmpi.setDynData(ob[n], false);
                        b = true;
                    }
                    this.MODEL_fireChged([]);
                    return null;
                }
            }
            /**
             * set inneritem dyn must by item's name
             * if item is not set name,it cannot be set dyn
             * @param dyn
             * @param bfirechg
             */
            setDynData(dyn, bfirechg = true) {
                var ns = super.setDynData(dyn, false);
                var u = this.getComp();
                var dyn_comp = dyn[HMICompIns.PN_DYN_COMP];
                if (dyn_comp != undefined && dyn_comp != null) {
                    ns.push(HMICompIns.PN_DYN_COMP);
                    for (var n in dyn_comp) {
                        var tmpi = this.getInnerDrawItemByName(n);
                        if (tmpi == null)
                            continue;
                        tmpi.setDynData(dyn_comp[n], false);
                    }
                }
                this.updateInInterProps();
                if (bfirechg)
                    this.MODEL_fireChged(ns);
                return ns;
            }
            fireInterEvent(eventn, eventv) {
                var dyncomp = this.getComp();
                if (dyncomp == null)
                    return false;
                var cc = dyncomp.getCompInter().getInterEventByName(eventn);
                if (cc == null)
                    return false;
                //var evtb = this.getEventBinder("on_"+eventn) ;
                var evtb = this.getEventBinder(eventn);
                if (evtb != null) {
                    evtb.onEventRunInter(this);
                }
                return true;
            }
            getInnerContainer() {
                if (this.innerCont != null)
                    return this.innerCont;
                var pc = this.getContainer();
                if (pc == null)
                    return null;
                var u = this.getComp();
                if (u == null)
                    return null;
                this.innerCont = new oc.ItemsContainer(this, pc, u);
                return this.innerCont;
            }
            getClassName() {
                return "oc.hmi.HMICompIns";
            }
            getPropDefs() {
                var r = super.getPropDefs();
                r.push(HMICompIns.PNS);
                var cp = this.getComp();
                if (cp != null)
                    r.push(cp.getInterPropDefsForIns());
                return r;
            }
            getEventDefs() {
                var r = super.getEventDefs();
                var comp = this.getComp();
                if (comp != null) {
                    var interevts = comp.getCompInter().getInterEvents();
                    var di_ens = { _cat_name: "hmi_comp", _cat_title: "Component Interface" };
                    for (var interevt of interevts) {
                        var evtn = interevt.getName();
                        di_ens[evtn] = { title: "on_" + evtn, evt_tp: "inter" };
                    }
                    r.push(di_ens);
                }
                return r;
            }
            on_mouse_event(tp, pxy, dxy, e) {
                var ic = this.getInnerContainer();
                if (ic == null)
                    return;
                var u = this.getComp();
                if (u == null)
                    return;
                var sitems = u.getItemsShow();
                if (sitems == null || sitems.length <= 0)
                    return;
                //var innerr = ic.getItemsRectInner() ;
                var innerdxy = ic.transPixelPt2DrawPt(pxy.x, pxy.y);
                for (var si of sitems) {
                    //console.log(innerdxy,si.getBoundRectDraw(),innerr) ;
                    var bc = si.containDrawPt(innerdxy.x, innerdxy.y);
                    if (bc) {
                        si.on_mouse_event(tp, pxy, innerdxy, e);
                        //console.log("sub item mouse in="+si.getName());
                    }
                }
            }
            getPrimRect() {
                var ic = this.getInnerContainer();
                if (ic == null)
                    return new oc.base.Rect(0, 0, this.getW(), this.getH());
                // return ic.getItemsRectInner();
                var u = this.getComp();
                if (u == null)
                    return new oc.base.Rect(0, 0, this.getW(), this.getH());
                //return u.getPrimRect();
                //return new oc.base.Rect(0,0,100,100);
                var r = oc.ItemsContainer.calcRect(u.getItemsShow());
                if (r == null)
                    return null;
                var p = ic.transDrawPt2PixelPt(r.x, r.y);
                var w = ic.transDrawLen2PixelLen(true, r.w);
                var h = ic.transDrawLen2PixelLen(false, r.h);
                return new oc.base.Rect(0, 0, w, h);
                //return r ;
            }
            /**
             * override to provider more extends item in unit
             */
            getUnitExtItems() {
                return [];
            }
            drawPrim(cxt) {
                var u = this.getComp();
                var ic = this.getInnerContainer();
                var items = (u != null ? u.getItemsShow() : null);
                if (ic == null || u == null || items == null || items.length <= 0) {
                    oc.util.drawRectEmpty(cxt, 0, 0, this.getW(), this.getH(), this.borderColor);
                    return;
                }
                //this.drawRect(cxt,c);
                //
                cxt.save();
                var pt = this.getPixelXY();
                if (pt != null) //what the fuck
                    cxt.translate(-pt.x, -pt.y);
                for (var item of items) {
                    item.draw(cxt, ic);
                }
                for (var item of this.getUnitExtItems()) {
                    item.draw(cxt, ic);
                }
                //u.draw(cxt, ic);
                cxt.restore();
            }
            drawPrimSel(ctx) {
            }
        }
        HMICompIns.PN_DYN_COMP = "_comp";
        HMICompIns.PNS = {
            _cat_name: "compins", _cat_title: "Comp Ins",
            borderPixel: { title: "border", type: "str" },
            borderColor: { title: "borderColor", type: "str" },
            fillColor: { title: "fillColor", type: "str", val_tp: "color" },
            radius: { title: "radius", type: "int" },
            compId: { title: "Comp Id", type: "str", readonly: true },
        };
        hmi.HMICompIns = HMICompIns;
    })(hmi = oc.hmi || (oc.hmi = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var hmi;
    (function (hmi) {
        class HMICompModel {
            constructor(opts) {
                this.valCompTxt = "";
                this.listeners = [];
                this.compUrl = opts.comp_url;
                //HMIComp.setAjaxLoadUrl(this.compUrl) ;
            }
            registerListener(lis) {
                this.listeners.push(lis);
            }
            /**
             * view need call this method,to notify outer is ready,model can start
             * load data,and fire mode changing event
             */
            initModel() {
                var pm = {};
                oc.util.doAjax(this.compUrl, pm, (bsucc, ret) => {
                    if (bsucc) {
                        this.valCompTxt = ret;
                        this.fireModelLoaded("comp", ret);
                        //this.loadOrUpdate();
                        // this.loadOrUpdate();
                    }
                });
            }
            getTemplate() {
                return {};
            }
            getRTDynData() {
                return {};
            }
            fireModelLoaded(tp, mv) {
                for (var lis of this.listeners) {
                    switch (tp) {
                        case "comp":
                            lis.on_model_loaded(mv);
                            break;
                    }
                }
            }
            fireModelContChged(cont) {
            }
            fireModelDynUpdated(dyn) {
                for (var lis of this.listeners) {
                    lis.on_model_dyn_updated(dyn);
                }
            }
        }
        hmi.HMICompModel = HMICompModel;
    })(hmi = oc.hmi || (oc.hmi = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var hmi;
    (function (hmi) {
        class HMICompRes {
            constructor(compid) {
                this.compId = compid;
            }
            getDrawResUrl(name) {
                return hmi.HMIComp.calcDrawResUrl(this.compId, name);
            }
            getDrawResParent() {
                return null;
            }
        }
        hmi.HMICompRes = HMICompRes;
        /**
         * for hmi edit and display
         */
        class HMICompPanel extends oc.DrawPanel {
            constructor(compid, target, opts) {
                super(target, opts);
                this.fixWidth = 1024;
                this.fixHeight = 768;
                this.fixWidth = opts["width"] ? opts["width"] : 1024;
                this.fixHeight = opts["height"] ? opts["height"] : 768;
                this.setDrawRes(new HMICompRes(compid));
            }
            getFixWidth() {
                return this.fixWidth;
            }
            getFixHeight() {
                return this.fixHeight;
            }
        }
        hmi.HMICompPanel = HMICompPanel;
    })(hmi = oc.hmi || (oc.hmi = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var hmi;
    (function (hmi) {
        class HMICompLayer extends oc.DrawLayer {
            constructor(opts) {
                super(opts);
                //unitName:string;
                this.menuEle = null;
                this.hmiComp = null;
                this.compInter = new hmi.CompInter();
                //this.unitName = "" ;
            }
            on_mouse_event(tp, pxy, dxy, e) {
                if (tp == oc.MOUSE_EVT_TP.Down) { //display rep right menu
                    if (e.button == oc.MOUSE_BTN.RIGHT) { //right
                        if (oc.PopMenu.createShowPopMenu(this, pxy, dxy))
                            e.preventDefault();
                    }
                }
            }
            getActionTypeName() {
                return "layer";
            }
            getCompInter() {
                return this.compInter;
            }
            extract(mark) {
                var r = super.extract(mark);
                r[hmi.HMIComp.PN_INTER] = this.compInter.extractInter();
                return r; //JSON.stringify(r) ;
            }
            inject(opts, mark) {
                if (typeof (opts) == "string")
                    eval("opts=" + opts);
                super.inject(opts, mark);
                //
                var cis = opts[hmi.HMIComp.PN_INTER];
                if (cis)
                    this.compInter.injectInter(cis);
            }
        }
        hmi.HMICompLayer = HMICompLayer;
        class HMICompView {
            constructor(m, dp, de, opts) {
                this.contId = "";
                this.contName = "";
                this.contTitle = "";
                this.contUnitName = "";
                this.options = {};
                this.bLoadFirst = true;
                if (opts == undefined || opts == null)
                    opts = {};
                this.model = m;
                this.drawPanel = dp;
                this.drawEditor = de;
                this.drawPanel.init_panel();
                this.drawPanel.on_draw();
                if (this.drawEditor != null)
                    this.drawEditor.init_editor();
                this.drawLayer = new HMICompLayer({});
                this.drawPanel.addLayer(this.drawLayer);
                if (this.drawEditor != null)
                    this.drawInter = new hmi.HMIInteractEdit(this.drawPanel, this.drawLayer, { copy_paste_url: opts.copy_paste_url });
                else
                    this.drawInter = new oc.DrawInteractShow(this.drawPanel, this.drawLayer, { copy_paste_url: opts.copy_paste_url });
                this.drawPanel.setInteract(this.drawInter);
                this.options = opts;
                this.loadedCB = opts.loaded_cb || null;
                this.model.registerListener(this);
            }
            init() {
                this.model.initModel();
            }
            getPanel() {
                return this.drawPanel;
            }
            getLayer() {
                return this.drawLayer;
            }
            getInteract() {
                return this.drawInter;
            }
            on_model_loaded(v) {
                if (v == null || v == "")
                    return;
                if (typeof (v) == 'string')
                    eval("v=" + v);
                this.drawLayer.inject(v, undefined);
                if (this.loadedCB != null)
                    this.loadedCB();
            }
            on_model_dyn_updated(dyn) {
                throw new Error("Method not implemented.");
            }
            on_model_propbind_data(data) {
                throw new Error("Method not implemented.");
            }
        }
        hmi.HMICompView = HMICompView;
        /**
         * only for tester show
         */
        class HMICompViewShow {
            constructor(editlayer, dp, opts) {
                this.options = {};
                if (opts == undefined || opts == null)
                    opts = {};
                this.editLayer = editlayer;
                this.comp = new hmi.HMIComp({});
                var compob = editlayer.extract(null);
                this.comp.inject(compob, false);
                this.drawPanel = dp;
                this.drawPanel.init_panel();
                this.drawPanel.on_draw();
                this.showLayer = new HMICompLayer({});
                this.drawPanel.addLayer(this.showLayer);
                this.compIns = new hmi.HMICompIns({});
                this.compIns.onCompSet(this.comp);
                this.showLayer.addItem(this.compIns);
                this.drawInter = new oc.DrawInteractShow(this.drawPanel, this.showLayer, { copy_paste_url: opts.copy_paste_url });
                this.drawPanel.setInteract(this.drawInter);
                this.options = opts;
                this.showLayer.ajustDrawFit();
            }
            getComp() {
                return this.comp;
            }
            getCompIns() {
                return this.compIns;
            }
        }
        hmi.HMICompViewShow = HMICompViewShow;
    })(hmi = oc.hmi || (oc.hmi = {}));
})(oc || (oc = {}));
/// <reference path="../draw_mvc.ts" />
var oc;
(function (oc) {
    var hmi;
    (function (hmi) {
        /**
         * for hmi controller in MVC
         * controller receive view items(panel layer drawitem) events
         *   and call inner bind js or other function to change model or view items prop
         *
         * 1)in a context,it has one controller.
         * 2)all event binder are register in it
         * 3)event call back js,may use drawitem's name as var like $diname
         *   and then change prop of it. e.g   $diname.aa=0.5
         *   so $diname may be compiled into a Proxy obj,which can call some set method for drawitem
         *
         */
        class HMIController extends oc.DrawCtrl {
            constructor(panel, model) {
                super();
                this.tickInt = null;
                this.name2DiProxy = {};
                this.hmiPanel = panel;
                this.hmiModel = model;
                this.drawLayer = panel.getLayer();
            }
            startRunning() {
                if (this.tickInt != null)
                    return;
                this.tickInt = setInterval(() => {
                    this.drawLayer.fireTick();
                }, 100);
            }
            stopRunning() {
                if (this.tickInt == null)
                    return;
                clearInterval(this.tickInt);
                this.tickInt = null;
            }
            $(name) {
                var r = this.name2DiProxy[name];
                if (r != undefined && r != null)
                    return;
                var di = this.drawLayer.getItemByName(name);
                if (di == null)
                    return;
                var bcomp_ins = di.getClassName() == "oc.hmi.HMICompIns";
                r = new Proxy(di, {
                    get(target, key) {
                        let result = target[key];
                        return result;
                    },
                    set(target, key, value) {
                        var b = false;
                        if (bcomp_ins) {
                            b = di.setInterPropVal(key, value);
                        }
                        if (!b) {
                            b = Reflect.set(target, key, value);
                        }
                        //target[key] = value;
                        return b;
                    }
                });
                this.name2DiProxy[name] = r;
                return r;
            }
        }
        hmi.HMIController = HMIController;
    })(hmi = oc.hmi || (oc.hmi = {}));
})(oc || (oc = {}));
/**
 * for iottree panel
 */
var oc;
(function (oc) {
    var hmi;
    (function (hmi) {
        class HMIInteractEdit extends oc.DrawInteract {
            constructor(panel, layer, opts) {
                super(panel, layer, opts);
                this.operChgLn = new oc.interact.OperChgLine(this, layer);
                this.operChgRect = new oc.interact.OperChgRect(this, layer);
                this.operChgArc = new oc.interact.OperChgArc(this, layer);
                this.operPtsChg = new oc.interact.OperPtsChg(this, layer);
            }
            on_mouse_mv(pxy, dxy, e) {
                super.on_mouse_mv(pxy, dxy, e);
                var p = this.getPanel();
                if (p == null)
                    return;
                var curon = this.getCurMouseOnItem();
                var sitem = this.getSelectedItem();
                if (sitem != null) //curon!=null&&curon==sitem)
                 { //
                    if (sitem instanceof oc.di.DILine) {
                        this.operChgLn.setDILine(sitem);
                        if (this.operChgLn.chkOperFitByDrawPt(pxy, dxy))
                            this.pushOperStack(this.operChgLn);
                    }
                    else if (sitem instanceof oc.di.DIPts) {
                        this.operPtsChg.setDIPts(sitem);
                        if (this.operPtsChg.chkOperFitByDrawPt(pxy, dxy))
                            this.pushOperStack(this.operPtsChg);
                    }
                    else if (sitem instanceof oc.DrawItemRect) {
                        this.operChgRect.setRect(sitem);
                        if (this.operChgRect.chkOperFitByDrawPt(pxy, dxy))
                            this.pushOperStack(this.operChgRect);
                        if (sitem instanceof oc.di.DIArc) {
                            this.operChgArc.setDIArc(sitem);
                            if (this.operChgArc.chkOperFitByDrawPt(pxy, dxy)) {
                                this.pushOperStack(this.operChgArc);
                            }
                        } //
                    }
                }
                else {
                    //this.setCursor(undefined);
                }
            }
            on_mouse_dbclk(pxy, dxy, e) {
                var curon = this.getCurMouseOnItem();
                if (curon == null)
                    return;
                if (curon instanceof oc.di.DITxt) {
                    var lay = this.getLayer();
                    var opedittxt = new oc.interact.OperEditTxt(this, lay, curon, "txt");
                    this.pushOperStack(opedittxt);
                }
            }
            on_mouse_dragover(pxy, dxy, dd) {
                var di = null;
                switch (dd._tp) {
                    case "comp":
                        var compid = dd._val;
                        var comp = hmi.HMIComp.getItemByCompId(compid);
                        if (comp == null) {
                            return;
                        }
                        break;
                    case "divcomp":
                        break;
                    case "hmi_sub":
                        break;
                }
            }
            on_mouse_dragleave(pxy, dxy, dd) {
                this.dragoverSelItems = [];
            }
            on_mouse_drop(pxy, dxy, dd) {
                var di = null;
                console.log("drop->", dd);
                switch (dd._tp) {
                    case "icon_fa":
                        di = new oc.di.DIIcon({ unicode: dd._val });
                        di.setDrawXY(dxy.x, dxy.y);
                        this.getLayer().addItem(di);
                        break;
                    case "comp":
                        var compid = dd._val;
                        var lay = this.getLayer(); //du.getLayer();
                        var compins = new hmi.HMICompIns({});
                        lay.addItem(compins);
                        compins.setDrawXY(dxy.x, dxy.y);
                        compins.setCompId(compid);
                        break;
                    case "divcomp":
                        var divcomp_uid = dd._val;
                        var lay = this.getLayer(); //du.getLayer();
                        var divcomp = new oc.DIDivComp({});
                        lay.addItem(divcomp);
                        divcomp.setDrawXY(dxy.x, dxy.y);
                        divcomp.setCompUid(divcomp_uid);
                        divcomp.setDrawSize(200, 100);
                        break;
                    case "hmi_sub":
                        var str = dd._val;
                        var pm = {};
                        eval("pm=" + str);
                        console.log(pm);
                        var hmiid = pm["hmi_id"];
                        var w = pm["w"];
                        var h = pm["h"];
                        //var hmipath = pm["hmi_path"] ;
                        var lay = this.getLayer(); //du.getLayer();
                        var hmisub = new oc.hmi.HMISub({});
                        hmisub.setDrawXY(dxy.x, dxy.y);
                        hmisub.setHmiSubId(hmiid);
                        hmisub.setDrawSize(w, h);
                        lay.addItem(hmisub);
                        break;
                }
                this.dragoverSelItems = [];
            }
            on_key_down(e) {
                super.doCopyPaste(e);
                if (this.isOperDefault()) {
                    //fconsole.log("k="+e.keyCode);
                    switch (e.keyCode) {
                        case 46: //del
                            this.removeSelectedItems();
                            break;
                        case 38: //up
                        case 37: //left
                        case 39: //right
                        case 40: //down
                            this.moveByKeyDir(e.keyCode);
                            break;
                    }
                }
            }
            setOperAddItem(dicn, opts) {
                var lay = this.getLayer();
                if (lay == null)
                    return false;
                var oper = new oc.interact.OperAddItem(this, lay, dicn, opts);
                this.pushOperStack(oper);
                return true;
            }
            setOperAddPts(tp, opts) {
                var lay = this.getLayer();
                if (lay == null)
                    return false;
                var oper = new oc.interact.OperPtsAdd(this, lay, opts, tp);
                this.pushOperStack(oper);
                return true;
            }
            setOperAddUnitIns(unitname) {
                var lay = this.getLayer();
                if (lay == null)
                    return false;
                var oper = oc.interact.OperAddItem.createOperAddByUnitName(this, lay, unitname, undefined);
                if (oper == null)
                    return false;
                this.pushOperStack(oper);
                return true;
            }
            removeSelectedItems() {
                var si = this.getSelectedItem(); //this.getSelectedItems() ;
                if (si == null)
                    return;
                console.log(si.getMark());
                if (si.getMark() == null) {
                    this.clearSelectedItems();
                    si.removeFromContainer();
                    return;
                }
                if (si.on_before_del()) { //may 
                    this.clearSelectedItems();
                    si.removeFromContainer();
                }
                // if(sis.length>0)
                // {
                // 	this.clearSelectedItems();
                // 	for(var si of sis)
                // 	{
                // 		si.removeFromContainer();
                // 	}
                // }
            }
            moveByKeyDir(keycode) {
                var p = this.getPanel();
                switch (keycode) {
                    case 38: //up
                        p.movePixelCenter(0, -30);
                        break;
                    case 37: //left
                        p.movePixelCenter(-30, 0);
                        break;
                    case 39: //right
                        p.movePixelCenter(30, 0);
                        break;
                    case 40: //down
                        p.movePixelCenter(0, 30);
                        break;
                }
            }
        } // end InteractEditRep
        hmi.HMIInteractEdit = HMIInteractEdit;
        class HMIInteractShow extends oc.DrawInteract {
            constructor(panel, layer, opts) {
                super(panel, layer, opts);
            }
        }
        hmi.HMIInteractShow = HMIInteractShow;
    })(hmi = oc.hmi || (oc.hmi = {}));
})(oc || (oc = {}));
/// <reference path="../draw_mvc.ts" />
var oc;
(function (oc) {
    var hmi;
    (function (hmi) {
        //export IOTT
        /**
         *
         */
        class HMIModel extends oc.DrawModel {
            constructor(opts) {
                super();
                this.listeners = [];
                this.valUnit = null;
                this.valTemp = null;
                this.valCont = null;
                this.valDyn = null;
                this.tempUrl = opts.temp_url;
                this.compUrl = opts.comp_url;
                this.dynUrl = opts.dyn_url;
                this.hmiPath = opts.hmi_path;
                //this.unitUrl = opts.unit_url;
                hmi.HMIComp.setAjaxLoadUrl(this.compUrl);
            }
            getHmiPath() {
                return this.hmiPath;
            }
            registerListener(lis) {
                this.listeners.push(lis);
            }
            /**
             * view need call this method,to notify outer is ready,model can start
             * load data,and fire mode changing event
             */
            initModel() {
                var pm = {};
                oc.util.doAjax(this.tempUrl, pm, (bsucc, ret) => {
                    if (bsucc) {
                        this.valTemp = ret;
                        this.fireModelLoaded("temp", ret);
                        //this.loadOrUpdate();
                        // this.loadOrUpdate();
                    }
                });
            }
            // public loadOrUpdate()
            // {
            //     oc.util.doAjax(this.contUrl,{},(bsucc,ret)=>{
            //         if(!bsucc||(typeof(ret)=="string" && ret.indexOf("{")!=0))
            //         {
            //             oc.util.prompt_err(ret as string);
            //             return ;
            //         }
            //         this.valCont = ret;
            //         this.fireModelContChged(ret);
            //     });
            // }
            refreshDyn(endcb) {
                oc.util.doAjax(this.dynUrl, {}, (bsucc, ret) => {
                    try {
                        if (!bsucc || (typeof (ret) == "string" && ret.indexOf("{") != 0)) {
                            oc.util.prompt_err(ret);
                            return;
                        }
                        this.valDyn = ret;
                        this.fireModelDynUpdated(ret);
                    }
                    finally {
                        if (endcb)
                            endcb();
                    }
                });
            }
            getTemplate() {
                return {};
            }
            getRTDynData() {
                return {};
            }
            fireModelLoaded(tp, mv) {
                for (var lis of this.listeners) {
                    switch (tp) {
                        case "temp":
                            lis.on_model_loaded(mv);
                            break;
                    }
                }
            }
            fireModelContChged(cont) {
            }
            fireModelDynUpdated(dyn) {
                for (var lis of this.listeners) {
                    lis.on_model_dyn_updated(dyn);
                }
            }
            fireModelPropBindData(data) {
                if (typeof (data) == "string")
                    eval("data=" + data);
                for (var lis of this.listeners) {
                    lis.on_model_propbind_data(data);
                }
            }
        }
        hmi.HMIModel = HMIModel;
    })(hmi = oc.hmi || (oc.hmi = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var hmi;
    (function (hmi) {
        /**
         * for hmi edit and display
         */
        class HMIPage {
            constructor(target, opts) {
                this.panels = [];
                if (!opts)
                    opts = {};
                this.tarEle = document.getElementById(target);
                this.tarEle["hmi_page"] = this;
            }
            addPanel(hmip) {
                this.panels.push(hmip);
                this.tarEle.appendChild(hmip.getHTMLElement());
            }
        }
        hmi.HMIPage = HMIPage;
    })(hmi = oc.hmi || (oc.hmi = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var hmi;
    (function (hmi) {
        /**
         * for hmi edit and display
         */
        class HMIPanel extends oc.DrawPanel {
            constructor(target, opts) {
                super(target, opts);
                this.fixWidth = 1024;
                this.fixHeight = 768;
                this.fixWidth = opts["width"] ? opts["width"] : 1024;
                this.fixHeight = opts["height"] ? opts["height"] : 768;
            }
            getFixWidth() {
                return this.fixWidth;
            }
            getFixHeight() {
                return this.fixHeight;
            }
        }
        hmi.HMIPanel = HMIPanel;
    })(hmi = oc.hmi || (oc.hmi = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var hmi;
    (function (hmi) {
        //export type DIDivCB=(icomp:IDIDivComp)=>void;
        /**
         * for hmi edit and display
         */
        class HMISubPanel extends oc.DrawPanel {
            constructor(id, target, opts) {
                super(target, opts);
                //var ele = this.getHTMLElement();
                //ele[DrawPanelDiv.DRAW_PANEL_DIV] = this;
            }
            setDrawLayer(dl) {
                this.addLayer(dl);
            }
        }
        hmi.HMISubPanel = HMISubPanel;
        class HMISubView extends oc.DrawView {
            constructor(model, panel) {
                super(model, panel);
            }
            getInteract() {
                return null;
            }
            sendMsgToServer(msg) {
            }
        }
        class HMISubLoader {
            //layTxt:string|null=null;
            constructor(hmipath, subid) {
                this.loaders = [];
                this.loadedSubItem = null;
                this.hmipath = hmipath;
                this.sub_id = subid;
            }
            getOrLoadSubItem(cb) {
                if (this.loadedSubItem != null) {
                    var dl = new oc.DrawLayer({});
                    dl.inject(this.loadedSubItem.lay, null);
                    cb(this.loadedSubItem, dl);
                    return;
                }
                this.loaders.push(cb);
                if (this.loaders.length == 1) {
                    this.doLoadAjax();
                }
            }
            doLoadAjax() {
                oc.util.doAjax("/hmi_ajax.jsp", { tp: "sub", hmi_path: this.hmipath, sub_id: this.sub_id }, (bsucc, ret) => {
                    if (bsucc) {
                        //console.log(hmipath+" "+sub_id+" - get load lay ajax") ;
                        var str = ret;
                        var k = str.indexOf("\r\n");
                        if (k <= 0)
                            return;
                        var path = str.substr(0, k);
                        var laytxt = str.substr(k + 2);
                        this.loadedSubItem = { id: this.sub_id, lay: laytxt, nodepath: path };
                        for (var ld of this.loaders) {
                            var dl = new oc.DrawLayer({});
                            dl.inject(this.loadedSubItem.lay, null);
                            ld(this.loadedSubItem, dl);
                        }
                    }
                });
            }
        }
        /**
         * in iottree context ,hmi may reference sub node hmis.
         * it implements like a panel,but it's a drawitem
         * 1)it use draw res to load draw contents
         * 2)it use div and canvas to draw itself
         * 3)simple interact
         * 4)inner draw item need not deep copy
         * 5)dyn data show is specially orginized
         */
        class HMISub extends oc.DrawDiv {
            constructor(opts) {
                super(opts);
                //private bMin:boolean=true;
                this.sub_id = "";
                this.subDrawLayer = null;
                this.subPanel = null;
                //private panelDiv:DrawPanelDiv|null=null ;
                this.subModel = null;
                this.subView = null;
                this.innerCont = null;
            }
            static getOrLoadSubItem(hmipath, sub_id, cb) {
                var k = hmipath + '-' + sub_id;
                var subloader = HMISub.key2loader[k];
                if (subloader == null || subloader == undefined) {
                    subloader = new HMISubLoader(hmipath, sub_id);
                }
                if (subloader == "")
                    return;
                subloader.getOrLoadSubItem(cb);
                // var u = HMISub.id2layer[sub_id];
                // if(u=="")
                // {
                //     cb(null,null) ;
                //     return ;
                // }
                // else if(u!=null&&u!=undefined)
                // {
                //     console.log(hmipath+" "+sub_id+" - get load lay cached") ;
                //     var dl = new DrawLayer({}) ;
                //     dl.inject(u.lay,null);
                //     cb(u,dl);
                //     return ;
                // }
                // oc.util.doAjax("/hmi_ajax.jsp",{tp:"sub",hmi_path:hmipath,sub_id:sub_id},(bsucc,ret)=>{
                //     if(bsucc)
                //     {
                //         console.log(hmipath+" "+sub_id+" - get load lay ajax") ;
                //         var str = ret as string;
                //         var k = str.indexOf("\r\n");
                //         if(k<=0)
                //             return ;
                //         var path = str.substr(0,k) ;
                //         var laytxt = str.substr(k+2) ;
                //         u = {id:sub_id,lay:laytxt,nodepath:path} ;
                //         HMISub.id2layer[sub_id] = u;
                //         var dl = new DrawLayer({}) ;
                //         dl.inject(laytxt,null);
                //         cb(u,dl);
                //     }
                // });
            }
            getClassName() {
                return "oc.hmi.HMISub";
            }
            getPropDefs() {
                var r = super.getPropDefs();
                r.push(HMISub.PNS);
                return r;
            }
            setHmiItem(hmiitem, lay) {
                this.subDrawLayer = lay;
                var hmipath = hmiitem.nodepath;
                this.subModel = new hmi.HMIModel({
                    temp_url: "hmi_editor_ajax.jsp?op=load&path=" + hmipath,
                    comp_url: "comp_ajax.jsp?op=comp_load",
                    dyn_url: "",
                    hmi_path: hmipath
                });
                var p = this.getSubPanel();
                if (p != null) {
                    this.subView = new HMISubView(this.subModel, p);
                    p.setDrawLayer(this.subDrawLayer);
                }
            }
            getSubPanel() {
                if (this.subPanel != null) {
                    console.log("get sub panel - eleid=" + this.subPanel.getHTMLElement().id);
                    return this.subPanel;
                }
                var ele = this.getContEle();
                if (ele == null)
                    return null;
                this.subPanel = new oc.hmi.HMISubPanel("id", ele[0], {});
                console.log("get sub panel - eleid=" + this.subPanel.getHTMLElement().id);
                return this.subPanel;
            }
            getHmiSubId() {
                var subid = this["sub_id"];
                if (subid == null || subid == undefined) {
                    return null;
                }
                return subid;
            }
            setHmiSubId(subid) {
                if (subid == this.getHmiSubId())
                    return;
                this["sub_id"] = subid;
                this.reloadSubHmi();
                //this.MODEL_fireChged(["sub_id"]);
            }
            getDrawPreferSize() {
                if (this.subDrawLayer == null)
                    return super.getDrawPreferSize();
                var r = this.subDrawLayer.getShowItemsRect();
                if (r == null)
                    return super.getDrawPreferSize();
                return { w: r.w, h: r.h };
            }
            on_container_set() {
                super.on_container_set();
                this.reloadSubHmi();
            }
            updateInner() {
                if (this.subDrawLayer != null) {
                    this.subDrawLayer.ajustDrawFit();
                }
            }
            onDivResize(x, y, w, h, b_notchg) {
                var _a;
                //super.onDivResize() ;
                if (b_notchg)
                    return;
                (_a = this.getSubPanel()) === null || _a === void 0 ? void 0 : _a.updatePixelSize();
                //this.redraw();
                this.updateInner();
            }
            reloadSubHmi() {
                var subid = this.getHmiSubId();
                if (subid == null)
                    return;
                var m = this.getModel();
                if (m == null)
                    return;
                var hmipath = m.getHmiPath();
                //var p = this.getSubPanel() ;
                console.log(hmipath, subid);
                HMISub.getOrLoadSubItem(hmipath, subid, (subitem, lay) => {
                    if (subitem == null || lay == null)
                        return;
                    this.setHmiItem(subitem, lay);
                    this.redraw();
                });
            }
            on_mouse_event(tp, pxy, dxy, e) {
                if (tp == oc.MOUSE_EVT_TP.Down) {
                    if (e.button == oc.MOUSE_BTN.LEFT) {
                        var xy = this.getDrawXY();
                        var dx = dxy.x - xy.x;
                        var dy = dxy.y - xy.y;
                        if (dx > 0 && dy > 0 && dx < HMISub.LEFTTOP_R && dy < HMISub.LEFTTOP_R) { //click top left ctrl
                            //this.bMin = !this.bMin;
                            //this.MODEL_fireChged([]);
                        }
                    }
                }
            }
            // public getPrimRect(): base.Rect | null
            // {
            //     var ic = this.getInnerContainer();
            //     if (ic == null)
            //         return new oc.base.Rect(0, 0, this.getW(), this.getH());
            //     // return ic.getItemsRectInner();
            //     var r = ItemsContainer.calcRect(this.getItemsShow());
            //     if (r == null)
            //         return null;
            //     //var p = ic.transDrawPt2PixelPt(r.x, r.y);
            //     var w = ic.transDrawLen2PixelLen(true, r.w);
            //     var h = ic.transDrawLen2PixelLen(false, r.h);
            //     return new oc.base.Rect(0, 0, w, h);
            //     //return r ;
            // }
            drawPrim(ctx) {
                this.displayDivEle();
                this.updateInner();
                if (this.subDrawLayer != null) {
                    var cxt = this.subDrawLayer.getCxtFront();
                    if (this.border > 0) { // override to draw border
                        var psz = this.getPixelSize();
                        if (psz != null)
                            oc.util.drawRect(cxt, 0, 0, psz.w, psz.h, null, null, this.border, this.border_color);
                    }
                }
            }
            getInnerContainer() {
                if (this.innerCont != null)
                    return this.innerCont;
                var pc = this.getContainer();
                if (pc == null)
                    return null;
                this.innerCont = new oc.ItemsContainer(this, pc, this);
                return this.innerCont;
            }
            getItemsShow() {
                if (this.subDrawLayer == null)
                    return [];
                return this.subDrawLayer.getItemsAll();
            }
            removeItem(item) {
                return false; //not support
            }
        }
        HMISub.id2layer = {};
        HMISub.key2loader = {};
        HMISub.PNS = {
            _cat_name: "hmisub", _cat_title: "Sub Hmi",
            sub_id: { title: "Sub Hmi Id", type: "str", readonly: true }
        };
        HMISub.LEFTTOP_R = 30;
        hmi.HMISub = HMISub;
        class HMISubDiv {
            constructor(divele, opts) {
                this.drawItem = null;
                if (opts == undefined)
                    opts = {};
                if (opts["panel"])
                    this.panel = opts["panel"];
                else
                    this.panel = new oc.DrawPanel(divele, {});
                this.subModel = new hmi.HMIModel({ temp_url: "",
                    comp_url: "",
                    dyn_url: "",
                    hmi_path: opts["hmi_path"] });
                this.panel.init_panel();
                this.subView = new HMISubView(this.subModel, this.panel);
                this.layer = (opts["layer"] != undefined) ? opts["layer"] : new oc.DrawLayer("lay");
                this.panel.addLayer(this.layer);
                var ele = this.panel.getHTMLElement();
                ele[oc.DrawPanelDiv.DRAW_PANEL_DIV] = this;
            }
            getPanel() {
                return this.panel;
            }
            getLayer() {
                return this.layer;
            }
            setDrawItem(di) {
                this.drawItem = di;
                this.layer.addItem(di);
                //this.layer.ajustDrawFit();//cause firefox error
            }
            getDrawItem() {
                return this.drawItem;
            }
            updateByResize() {
                this.panel.updatePixelSize();
                this.layer.ajustDrawFit();
            }
        }
        HMISubDiv.DRAW_PANEL_DIV = "_hmisub_div";
        hmi.HMISubDiv = HMISubDiv;
    })(hmi = oc.hmi || (oc.hmi = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var hmi;
    (function (hmi) {
        /**
         * A unit instance,which use unit template to display
         * for hmi configuration
         * overrider must provider template name,which can be used to edit
         *
         */
        class HMIUnit extends oc.DrawItemRectR {
            constructor(opts) {
                super(opts);
                this.borderPixel = null;
                this.borderColor = "yellow";
                this.fillColor = null;
                this.radius = null;
                //unitName: string | null = null;
                /**
                 * deep copy of DrawItem
                 */
                this.dynUnit = null;
                this.innerCont = null;
            }
            static setAjaxLoadUrl(u) {
                HMIUnit.ajaxLoadUrl = u;
            }
            static setUnitTemp(u) {
                var n = u.getName();
                if (n == null || n == undefined || n == "")
                    return;
                HMIUnit.name2temp[n] = u;
            }
            static addUnitTempByJSON(json) {
                if (typeof (json) == 'string')
                    eval("json=" + json);
                var u = new oc.DrawUnit(undefined);
                u.inject(json, false);
                var ext = json["_ext"];
                if (ext != undefined && ext != null) {
                    u.setDynData(ext, false);
                }
                HMIUnit.setUnitTemp(u);
            }
            static getUnitTempByName(n) {
                var u = HMIUnit.name2temp[n];
                if (u == null || u == undefined)
                    return null;
                return u;
            }
            static getUnitTempJSONStr(n) {
                var du = HMIUnit.getUnitTempByName(n);
                if (du == null)
                    return null;
                var ob = du.extract();
                return JSON.stringify(ob);
            }
            getClassName() {
                return "oc.hmi.HMIUnit";
            }
            getPropDefs() {
                var r = super.getPropDefs();
                r.push(HMIUnit.PNS);
                return r;
            }
            getUnitTemp() {
                if (this.dynUnit != null)
                    return this.dynUnit;
                var tn = this.getUnitTempName();
                if (tn == null || tn == "")
                    return null;
                var du = oc.DrawUnit.getUnitByName(tn);
                if (du == null)
                    return null;
                this.dynUnit = du.duplicateMe();
                var c = this.getContainer();
                if (this.dynUnit != null && c != null)
                    this.dynUnit.setContainer(c, this.getLayer());
                return this.dynUnit;
            }
            getInnerDrawItemByName(n) {
                var u = this.getUnitTemp();
                if (u == null)
                    return null;
                for (var i of u.getItemsShow()) {
                    if (n == i.getName())
                        return i;
                }
                return null;
            }
            /**
             * set inneritem dyn must by item's name
             * if item is not set name,it cannot be set dyn
             * @param dyn
             * @param bfirechg
             */
            setDynData(dyn, bfirechg = true) {
                var ns = super.setDynData(dyn, false);
                var u = this.getUnitTemp();
                if (u != null) {
                    var aliasmap = u.getAliasMap();
                    if (aliasmap != null) {
                        for (var aliasn in aliasmap) {
                            var v = dyn[aliasn]; //use alias name getval
                            if (v == undefined || v == null)
                                continue;
                            var mapss = aliasmap[aliasn];
                            if (mapss.length < 2)
                                continue;
                            var tmpi = this.getInnerDrawItemByName(mapss[0]);
                            if (tmpi == null)
                                continue;
                            tmpi[mapss[1]] = v;
                        }
                    }
                }
                var dyn_unit = dyn[oc.DrawUnitIns.PN_DYN_UNIT];
                if (dyn_unit != undefined && dyn_unit != null) {
                    ns.push(oc.DrawUnitIns.PN_DYN_UNIT);
                    for (var n in dyn_unit) {
                        var tmpi = this.getInnerDrawItemByName(n);
                        if (tmpi == null)
                            continue;
                        tmpi.setDynData(dyn_unit[n], false);
                    }
                }
                if (bfirechg)
                    this.MODEL_fireChged(ns);
                return ns;
            }
            getInnerContainer() {
                if (this.innerCont != null)
                    return this.innerCont;
                var pc = this.getContainer();
                if (pc == null)
                    return null;
                var u = this.getUnitTemp();
                if (u == null)
                    return null;
                this.innerCont = new oc.ItemsContainer(this, pc, u);
                return this.innerCont;
            }
            /**
             * override it to support alias map inject
             * to unit
             * @param prop_names
             */
            MODEL_fireChged(prop_names) {
                var u = this.getUnitTemp();
                if (u != null) {
                    var aliasmap = u.getAliasMap();
                    if (aliasmap != null) {
                        for (var aliasn in aliasmap) {
                            var v = this[aliasn]; //instance alias name getval
                            if (v == undefined || v == null)
                                continue;
                            var mapss = aliasmap[aliasn];
                            if (mapss.length < 2)
                                continue;
                            var tmpi = this.getInnerDrawItemByName(mapss[0]);
                            if (tmpi == null)
                                continue;
                            tmpi[mapss[1]] = v;
                        }
                    }
                }
                super.MODEL_fireChged(prop_names);
            }
            getPrimRect() {
                var ic = this.getInnerContainer();
                if (ic == null)
                    return new oc.base.Rect(0, 0, this.getW(), this.getH());
                // return ic.getItemsRectInner();
                var u = this.getUnitTemp();
                if (u == null)
                    return new oc.base.Rect(0, 0, this.getW(), this.getH());
                //return u.getPrimRect();
                //return new oc.base.Rect(0,0,100,100);
                var r = oc.ItemsContainer.calcRect(u.getItemsShow());
                if (r == null)
                    return null;
                var p = ic.transDrawPt2PixelPt(r.x, r.y);
                var w = ic.transDrawLen2PixelLen(true, r.w);
                var h = ic.transDrawLen2PixelLen(false, r.h);
                return new oc.base.Rect(0, 0, w, h);
                //return r ;
            }
            /**
             * override to provider more extends item in unit
             */
            getUnitExtItems() {
                return [];
            }
            drawPrim(cxt) {
                var u = this.getUnitTemp();
                var ic = this.getInnerContainer();
                var items = (u != null ? u.getItemsShow() : null);
                if (ic == null || u == null || items == null || items.length <= 0) {
                    //var tmpr = this.getPrimRect();
                    //if (tmpr == null)
                    //	return;
                    oc.util.drawRectEmpty(cxt, 0, 0, this.getW(), this.getH(), this.borderColor);
                    return;
                }
                //this.drawRect(cxt,c);
                //
                cxt.save();
                var pt = this.getPixelXY();
                if (pt != null) //what the fuck
                    cxt.translate(-pt.x, -pt.y);
                for (var item of items) {
                    item.draw(cxt, ic);
                }
                for (var item of this.getUnitExtItems()) {
                    item.draw(cxt, ic);
                }
                //u.draw(cxt, ic);
                cxt.restore();
            }
            drawPrimSel(ctx) {
            }
        }
        HMIUnit.name2temp = {};
        HMIUnit.ajaxLoadUrl = null;
        HMIUnit.PN_DYN_UNIT = "_unit";
        HMIUnit.PNS = {
            _cat_name: "hmi_unit", _cat_title: "HMI Unit",
            borderPixel: { title: "border", type: "str" },
            borderColor: { title: "borderColor", type: "str" },
            fillColor: { title: "fillColor", type: "str", val_tp: "color" },
            radius: { title: "radius", type: "int" }
        };
        hmi.HMIUnit = HMIUnit;
    })(hmi = oc.hmi || (oc.hmi = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var hmi;
    (function (hmi) {
        class HMIUnitBasic extends hmi.HMIUnit {
            getUnitTempName() {
                return "hmi_unit_basic";
            }
        }
        hmi.HMIUnitBasic = HMIUnitBasic;
    })(hmi = oc.hmi || (oc.hmi = {}));
})(oc || (oc = {}));
/// <reference path="../draw_mvc.ts" />
var oc;
(function (oc) {
    var hmi;
    (function (hmi) {
        class HMILayer extends oc.DrawLayer {
            constructor(opts) {
                super(opts);
                this.menuEle = null;
                this.unitName = "";
            }
            on_mouse_event(tp, pxy, dxy, e) {
                if (tp == oc.MOUSE_EVT_TP.Down) { //display rep right menu
                    if (e.button == oc.MOUSE_BTN.RIGHT) { //right
                        if (oc.PopMenu.createShowPopMenu(this, pxy, dxy))
                            e.preventDefault();
                    }
                }
            }
            setUnitName(u) {
                this.unitName = u;
            }
            getActionTypeName() {
                return "layer";
            }
        }
        hmi.HMILayer = HMILayer;
        /**
         * using template and unit(unittn) to support load iott graphics data
         * 1)Panel load a template which has some container(group). e.g conn ui
         * 2)panel load from app ajax ,and make unit nodes show etc.
         * 3)support some edit action like add,modify,delete etc
         */
        class HMIView extends oc.DrawView {
            constructor(m, dp, de, opts) {
                super(m, dp); //repid,hmiid,
                this.contId = "";
                this.contName = "";
                this.contTitle = "";
                this.contUnitName = "";
                this.options = {};
                this.websock = null;
                this.bLoadFirst = true;
                this.hmiModel = m;
                this.drawPanel = dp;
                this.drawEditor = de;
                this.drawPanel.init_panel();
                this.drawPanel.on_draw();
                if (this.drawEditor != null)
                    this.drawEditor.init_editor();
                this.drawLayer = new HMILayer({});
                this.drawPanel.addLayer(this.drawLayer);
                if (opts && opts.show_only)
                    this.drawInter = new hmi.HMIInteractShow(this.drawPanel, this.drawLayer, { show_only: true });
                else
                    this.drawInter = new hmi.HMIInteractEdit(this.drawPanel, this.drawLayer, { copy_paste_url: opts.copy_paste_url });
                this.drawPanel.setInteract(this.drawInter);
                this.options = opts;
                this.hmiModel.registerListener(this);
            }
            init() {
                this.hmiModel.initModel();
            }
            getPanel() {
                return this.drawPanel;
            }
            getLayer() {
                return this.drawLayer;
            }
            getInteract() {
                return this.drawInter;
            }
            setWebSocket(ws) {
                this.websock = ws;
            }
            sendMsgToServer(msg) {
                if (this.websock == null)
                    return;
                this.websock.send(msg);
            }
            on_model_loaded(temp) {
                if (temp == null || temp == "")
                    return;
                if (typeof (temp) == 'string')
                    eval("temp=" + temp);
                this.drawLayer.inject(temp, undefined);
            }
            on_model_dyn_updated(dyn) {
                throw new Error("Method not implemented.");
            }
            on_model_propbind_data(data) {
                var lay = this.getLayer();
                if (lay == null)
                    return;
                var bds = data.binds;
                for (var bd of bds) {
                    var di = lay.getItemById(bd.id);
                    if (di == null)
                        continue;
                    if (bd.items && bd.items.length <= 0)
                        continue;
                    let r = {};
                    var has = false;
                    for (var item of bd.items) {
                        if (!item.valid)
                            continue;
                        r[item.name] = item.v;
                        has = true;
                    }
                    if (has)
                        di.inject(r, true);
                }
            }
        }
        hmi.HMIView = HMIView;
    })(hmi = oc.hmi || (oc.hmi = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var interact;
    (function (interact) {
        class InteractEditLayer extends oc.DrawInteract {
            constructor(panel, layer, opts) {
                super(panel, layer, opts);
                this.operChgLn = new interact.OperChgLine(this, layer);
                this.operChgRect = new interact.OperChgRect(this, layer);
                this.operChgArc = new interact.OperChgArc(this, layer);
            }
            on_mouse_mv(pxy, dxy, e) {
                super.on_mouse_mv(pxy, dxy, e);
                var p = this.getPanel();
                if (p == null)
                    return;
                var curon = this.getCurMouseOnItem();
                var sitem = this.getSelectedItem();
                if (sitem != null) //curon!=null&&curon==sitem)
                 { //
                    //this.setCursor("move");
                    if (sitem instanceof oc.di.DILine) {
                        this.operChgLn.setDILine(sitem);
                        if (this.operChgLn.chkOperFitByDrawPt(pxy, dxy))
                            this.pushOperStack(this.operChgLn);
                    }
                    else if (sitem instanceof oc.DrawItemRect) {
                        this.operChgRect.setRect(sitem);
                        if (this.operChgRect.chkOperFitByDrawPt(pxy, dxy))
                            this.pushOperStack(this.operChgRect);
                        if (sitem instanceof oc.di.DIArc) {
                            this.operChgArc.setDIArc(sitem);
                            if (this.operChgArc.chkOperFitByDrawPt(pxy, dxy)) {
                                this.pushOperStack(this.operChgArc);
                            }
                        }
                    }
                }
                else {
                    //this.setCursor(undefined);
                }
            }
            on_mouse_dbclk(pxy, dxy, e) {
                var curon = this.getCurMouseOnItem();
                if (curon == null)
                    return;
                if (curon instanceof oc.di.DITxt) {
                    var lay = this.getLayer();
                    var opedittxt = new interact.OperEditTxt(this, lay, curon, "txt");
                    this.pushOperStack(opedittxt);
                }
            }
            on_mouse_drop(pxy, dxy, dd) {
                //console.log(dd);
                var di = null;
                switch (dd._tp) {
                    case "icon_fa":
                        di = new oc.di.DIIcon({ unicode: dd._val });
                        break;
                    case "unit":
                        di = new oc.DrawUnitIns({});
                        di.setUnitName(dd._val);
                        break;
                }
                if (di != null) {
                    di.setDrawXY(dxy.x, dxy.y);
                    this.getLayer().addItem(di);
                }
            }
            on_key_down(e) {
                super.doCopyPaste(e);
                if (this.isOperDefault()) {
                    //fconsole.log("k="+e.keyCode);
                    switch (e.keyCode) {
                        case 46: //del
                            this.removeSelectedItems();
                            break;
                        case 38: //up
                        case 37: //left
                        case 39: //right
                        case 40: //down
                            this.moveByKeyDir(e.keyCode);
                            break;
                    }
                }
            }
            setOperAddItem(dicn, opts) {
                var lay = this.getLayer();
                if (lay == null)
                    return false;
                var oper = new interact.OperAddItem(this, lay, dicn, opts);
                this.pushOperStack(oper);
                return true;
            }
            setOperAddUnitIns(unitname) {
                var lay = this.getLayer();
                if (lay == null)
                    return false;
                var oper = interact.OperAddItem.createOperAddByUnitName(this, lay, unitname, undefined);
                if (oper == null)
                    return false;
                this.pushOperStack(oper);
                return true;
            }
            removeSelectedItems() {
                var sis = this.getSelectedItems();
                if (sis.length > 0) {
                    this.clearSelectedItems();
                    for (var si of sis) {
                        si.removeFromContainer();
                    }
                }
            }
            moveByKeyDir(keycode) {
                var p = this.getPanel();
                switch (keycode) {
                    case 38: //up
                        p.movePixelCenter(0, -30);
                        break;
                    case 37: //left
                        p.movePixelCenter(-30, 0);
                        break;
                    case 39: //right
                        p.movePixelCenter(30, 0);
                        break;
                    case 40: //down
                        p.movePixelCenter(0, 30);
                        break;
                }
            }
        } // end InteractEditLayerLayer
        interact.InteractEditLayer = InteractEditLayer;
    })(interact = oc.interact || (oc.interact = {}));
})(oc || (oc = {}));
/**
 * modify rect
 */
var oc;
(function (oc) {
    var interact;
    (function (interact_1) {
        class OperAddItem extends oc.DrawOper {
            constructor(interact, layer, di_cn, opts) {
                super(interact, layer);
                this.addedDI = null;
                this.addOpts = undefined;
                this.diCN = di_cn; //di class name
                if (opts != undefined)
                    this.addOpts = opts;
            }
            getOperName() {
                return "additem";
            }
            static createOperAddByUnitName(interact, layer, unitname, opts) {
                var du = oc.DrawUnit.getUnitByName(unitname);
                if (du == null)
                    return null;
                var di = new oc.DrawUnitIns(undefined);
                di.setUnitName(unitname);
                var r = new OperAddItem(interact, layer, "", opts);
                r.addedDI = di;
                return r;
            }
            // static createOperAddByUnitName(interact: DrawInteract, layer: DrawLayer,unit_name:string)
            // {
            // }
            on_oper_stack_push() {
                this.setCursor(oc.Cursor.crosshair);
            }
            on_oper_stack_pop() {
                this.setCursor(oc.Cursor.auto);
            }
            chkOperFitByDrawPt(pxy, dxy) {
                return false;
            }
            maskInteractEvent() {
                return true;
            }
            on_mouse_down(pxy, dxy) {
                var p = this.getDrawPanel();
                var lay = this.getDrawLayer();
                if (p == null || lay == null) {
                    this.popOperStackMe();
                    return true;
                }
                if (this.addedDI == null) {
                    this.addedDI = oc.DrawItem.createByFullClassName(this.diCN, this.addOpts, true);
                    if (this.addedDI == null) { //failed
                        this.popOperStackMe();
                        return true;
                    }
                }
                var beginr = this.addedDI.setDrawBeginXY(lay, dxy.x, dxy.y);
                lay.addItem(this.addedDI);
                this.getInteract().setSelectedItem(this.addedDI);
                if (!beginr) { //end
                    this.popOperStackMe();
                    return true;
                }
                return false; //stop event delivy
            }
            on_mouse_mv(pxy, dxy) {
                var p = this.getDrawPanel();
                var lay = this.getDrawLayer();
                if (p == null || lay == null) {
                    this.popOperStackMe();
                    return true;
                }
                var lay = this.getDrawLayer();
                if (lay == null) {
                    this.popOperStackMe();
                    return true;
                }
                if (this.addedDI == null) {
                    //this.popOperStackMe();
                    return false;
                }
                this.addedDI.setDrawEndXY(lay, dxy.x, dxy.y);
                return false;
            }
            on_mouse_up(pxy, dxy) {
                this.diCN = "";
                this.addedDI = null;
                this.popOperStackMe();
                return true;
            }
            draw_oper() {
            }
        }
        interact_1.OperAddItem = OperAddItem;
    })(interact = oc.interact || (oc.interact = {}));
})(oc || (oc = {}));
/**
 * modify line
 */
var oc;
(function (oc) {
    var interact;
    (function (interact_2) {
        class OperChgArc extends oc.DrawOper {
            constructor(interact, layer) {
                super(interact, layer);
                this.downPt = null;
                this.idArcCtrlPt = null;
                this.itemDragPt = null;
                this.diArc = null;
            }
            getOperName() {
                return "chg_arc";
            }
            setDIArc(arc) {
                this.diArc = arc;
            }
            on_oper_stack_push() {
                this.setCursor(oc.Cursor.crosshair);
            }
            on_oper_stack_pop() {
                this.setCursor(oc.Cursor.auto);
            }
            chkOperFitByDrawPt(pxy, dxy) {
                if (this.diArc == null)
                    return false;
                var r = this.diArc.chkPtOnCtrl(pxy, dxy);
                if (r == null)
                    return false;
                return true;
            }
            maskInteractEvent() {
                return true;
            }
            on_mouse_down(pxy, dxy) {
                if (this.diArc == null)
                    return true;
                this.idArcCtrlPt = this.diArc.chkPtOnCtrl(pxy, dxy);
                if (this.idArcCtrlPt == null) { //normal drag
                    return true;
                }
                this.downPt = dxy;
                return false; //stop event delivy
            }
            on_mouse_mv(pxy, dxy) {
                var lay = this.getDrawLayer();
                if (lay == null)
                    return true;
                if (this.diArc == null)
                    return true;
                if (this.idArcCtrlPt == null) {
                    if (this.diArc.chkPtOnCtrl(pxy, dxy) == null) {
                        this.popOperStackMe();
                        return true;
                    }
                    else {
                        this.setCursor(oc.Cursor.crosshair);
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
                this.diArc.setCtrlDrawPt(this.idArcCtrlPt, dxy.x, dxy.y);
                return false;
            }
            on_mouse_up(pxy, dxy) {
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
            draw_oper() {
            }
        }
        interact_2.OperChgArc = OperChgArc;
    })(interact = oc.interact || (oc.interact = {}));
})(oc || (oc = {}));
/**
 * modify line
 */
var oc;
(function (oc) {
    var interact;
    (function (interact_3) {
        class OperChgLine extends oc.DrawOper {
            constructor(interact, layer) {
                super(interact, layer);
                this.downPt = null;
                this.diLnCtrlTp = null;
                this.itemDragPt = null;
                this.diLn = null;
            }
            getOperName() {
                return "chg_line";
            }
            setDILine(ln) {
                this.diLn = ln;
            }
            on_oper_stack_push() {
                this.setCursor(oc.Cursor.crosshair);
            }
            on_oper_stack_pop() {
                this.setCursor(oc.Cursor.auto);
            }
            chkOperFitByDrawPt(pxy, dxy) {
                if (this.diLn == null)
                    return false;
                var r = this.diLn.chkDrawPtOnCtrlPt(dxy.x, dxy.y);
                if (r == null)
                    return false;
                return true;
            }
            on_mouse_down(pxy, dxy) {
                if (this.diLn == null)
                    return true;
                this.diLnCtrlTp = this.diLn.chkDrawPtOnCtrlPt(dxy.x, dxy.y);
                if (this.diLnCtrlTp == null) { //normal drag
                    return true;
                }
                this.downPt = dxy;
                return false; //stop event delivy
            }
            on_mouse_mv(pxy, dxy) {
                var lay = this.getDrawLayer();
                if (lay == null)
                    return true;
                if (this.diLn == null)
                    return true;
                if (this.diLnCtrlTp == null) { //mouse out,pop me
                    if (this.diLn.chkDrawPtOnCtrlPt(dxy.x, dxy.y) == null) {
                        this.popOperStackMe();
                        return true;
                    }
                    else {
                        this.setCursor(oc.Cursor.crosshair);
                        return false;
                    }
                }
                this.diLn.setCtrlDrawPt(this.diLnCtrlTp, dxy.x, dxy.y);
                return false;
            }
            on_mouse_up(pxy, dxy) {
                this.downPt = null;
                //
                this.diLnCtrlTp = null;
                this.diLn = null;
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
            draw_oper() {
            }
        }
        interact_3.OperChgLine = OperChgLine;
    })(interact = oc.interact || (oc.interact = {}));
})(oc || (oc = {}));
/**
 * modify rect
 */
var oc;
(function (oc) {
    var interact;
    (function (interact_4) {
        class OperChgRect extends oc.DrawOper {
            constructor(interact, layer) {
                super(interact, layer);
                this.downPt = null;
                this.rectCtrlTp = null;
                this.itemDragPt = null;
                this.rect = null;
            }
            getOperName() {
                return "chg_rect";
            }
            setRect(rect) {
                this.rect = rect;
            }
            on_oper_stack_push() {
                this.setCursor(oc.Cursor.crosshair);
            }
            on_oper_stack_pop() {
                this.setCursor(oc.Cursor.auto);
            }
            chkOperFitByDrawPt(pxy, dxy) {
                if (this.rect == null)
                    return false;
                var r = this.rect.chkPtOnCtrl(pxy, dxy);
                if (r == null)
                    return false;
                return true;
            }
            on_mouse_down(pxy, dxy) {
                if (this.rect == null)
                    return true;
                this.rectCtrlTp = this.rect.chkPtOnCtrl(pxy, dxy);
                if (this.rectCtrlTp == null) { //normal drag
                    return true;
                }
                this.downPt = dxy;
                return false; //stop event delivy
            }
            on_mouse_mv(pxy, dxy) {
                var lay = this.getDrawLayer();
                if (lay == null)
                    return true;
                if (this.rect == null)
                    return true;
                if (this.rectCtrlTp == null) { //mouse out,pop me
                    var tmpcpt = this.rect.chkPtOnCtrl(pxy, dxy);
                    if (tmpcpt == null) {
                        this.popOperStackMe();
                        return true;
                    }
                    else {
                        if (tmpcpt == "r")
                            this.setCursor(oc.Cursor.crosshair);
                        else
                            this.setCursor(oc.Cursor[tmpcpt + "_resize"]);
                        return false;
                    }
                }
                if (this.rectCtrlTp == "r" && this.rect instanceof oc.DrawItemRectR)
                    this.setCursor(oc.Cursor.crosshair);
                else
                    this.setCursor(oc.Cursor[this.rectCtrlTp + "_resize"]);
                //chg rect
                //this.diRect.setCtrlDrawPt(this.diRectCtrlTp,dxy.x,dxy.y);
                if (this.rectCtrlTp == "r" && this.rect instanceof oc.DrawItemRectR)
                    this.rect.changeRotate(dxy.x, dxy.y);
                else
                    this.rect.changeRect(this.rectCtrlTp, dxy.x, dxy.y);
                return false;
            }
            on_mouse_up(pxy, dxy) {
                this.downPt = null;
                //if(this.itemDrag!=null)
                this.rectCtrlTp = null;
                this.rect = null;
                this.popOperStackMe();
                return true;
            }
            draw_oper() {
            }
        }
        interact_4.OperChgRect = OperChgRect;
    })(interact = oc.interact || (oc.interact = {}));
})(oc || (oc = {}));
/**
 * modify rect
 */
var oc;
(function (oc) {
    var interact;
    (function (interact_5) {
        class OperEditTxt extends oc.DrawOper {
            constructor(interact, layer, di, propn) {
                super(interact, layer);
                this.editEle = null;
                this.editDI = di; //di
                this.propName = propn;
            }
            getOperName() {
                return "chg_txt";
            }
            on_oper_stack_push() {
                var rect = this.editDI.getBoundRectPixel();
                if (rect == null) {
                    this.popOperStackMe();
                    return;
                }
                this.setCursor(oc.Cursor.text);
                var ele = this.getDrawPanel().getHTMLElement();
                this.editEle = document.createElement("div");
                var txt = this.editDI[this.propName];
                var edele = $(this.editEle);
                edele.css("position", "absolute");
                edele.css("left", rect.x + "px");
                edele.css("top", rect.y + "px");
                $(this.editEle).css("z-index", "60000");
                if (rect.h < 20)
                    rect.h = 20;
                if (rect.w < 30)
                    rect.w = 40;
                this.editEle.innerHTML = `<input id="oper_edit_txt" type="text" size="10" style="width:${rect.w}px;height:${(rect.h - 2)}px;font-size:${rect.h - 8}px" value="${txt}"/>`;
                $(ele).append(this.editEle);
            }
            on_oper_stack_pop() {
                this.setCursor(oc.Cursor.auto);
                if (this.editEle != null) {
                    var ele = this.getDrawPanel().getHTMLElement();
                    ele.removeChild(this.editEle);
                }
            }
            chkOperFitByDrawPt(pxy, dxy) {
                return false;
            }
            on_mouse_down(pxy, dxy) {
                var di = this.getInteract().getCurMouseOnItem();
                if (di == this.editDI) {
                    return false;
                }
                this.popOperStackMe();
                return true;
            }
            on_mouse_mv(pxy, dxy) {
                return false;
            }
            on_mouse_up(pxy, dxy) {
                return false;
            }
            on_key_down(e) {
                if (this.editEle == null)
                    return true;
                if (e.keyCode == 13) {
                    var v = $("#oper_edit_txt").val();
                    if (v == null || v == "") {
                        //todo 
                        return false;
                    }
                    var pnv = {};
                    pnv[this.propName] = v;
                    this.editDI.setPropValue(pnv);
                    this.popOperStackMe();
                    return false;
                }
                return true;
            }
            draw_oper() {
            }
        }
        interact_5.OperEditTxt = OperEditTxt;
    })(interact = oc.interact || (oc.interact = {}));
})(oc || (oc = {}));
/**
 * add multi pts line or polygon
 */
var oc;
(function (oc) {
    var interact;
    (function (interact_6) {
        class OperPtsAdd extends oc.DrawOper {
            constructor(interact, layer, opts, tp) {
                super(interact, layer);
                //diCN:string;
                this.addedDI = null;
                this.addOpts = undefined;
                this.firstPt = null;
                this.secondPt = null;
                this.mvPt = null;
                //this.diCN = di_cn;//di class name DIPtsPoly  DIPtsLn
                if (opts != undefined)
                    this.addOpts = opts;
                this.tp = tp;
            }
            getOperName() {
                return "pyln_add";
            }
            on_oper_stack_push() {
                this.setCursor(oc.Cursor.crosshair);
            }
            on_oper_stack_pop() {
                this.setCursor(oc.Cursor.auto);
            }
            chkOperFitByDrawPt(pxy, dxy) {
                return false;
            }
            maskInteractEvent() {
                return true;
            }
            on_mouse_down(pxy, dxy, me) {
                var p = this.getDrawPanel();
                var lay = this.getDrawLayer();
                if (p == null || lay == null) {
                    this.popOperStackMe();
                    return true;
                }
                if (me.button == oc.MOUSE_BTN.RIGHT) {
                    //if(this.secondPt!=null)
                    this.popOperStackMe();
                    return true;
                }
                if (this.firstPt == null) {
                    this.firstPt = dxy;
                    return false;
                }
                if (this.secondPt == null) {
                    this.secondPt = dxy;
                    return false;
                }
                if (this.addedDI != null) {
                    this.addedDI.addPt(dxy.x, dxy.y);
                }
                return false; //stop event delivy
            }
            on_mouse_dbclk(pxy, dxy, me) {
                this.popOperStackMe();
                return true;
            }
            on_mouse_mv(pxy, dxy, me) {
                var p = this.getDrawPanel();
                var lay = this.getDrawLayer();
                if (p == null || lay == null) {
                    this.popOperStackMe();
                    return true;
                }
                var lay = this.getDrawLayer();
                if (lay == null) {
                    this.popOperStackMe();
                    return true;
                }
                if (this.firstPt == null) {
                    return false;
                }
                if (this.secondPt == null) { //draw line
                    this.mvPt = dxy;
                    lay.update_draw();
                    return false;
                }
                if (this.addedDI == null) {
                    switch (this.tp) {
                        case "ln":
                            this.addedDI = new oc.di.DIPtsLn({});
                            break;
                        case "py":
                            this.addedDI = new oc.di.DIPtsPy({});
                            break;
                        case "pipe":
                            this.addedDI = new oc.di.DIPtsPipe({});
                            break;
                        default:
                            return false;
                    }
                    this.addedDI.addPt(this.firstPt.x, this.firstPt.y);
                    this.addedDI.addPt(this.secondPt.x, this.secondPt.y);
                    this.addedDI.addPt(dxy.x, dxy.y);
                    lay.addItem(this.addedDI);
                    this.getInteract().setSelectedItem(this.addedDI);
                }
                this.addedDI.chgLastPt(dxy.x, dxy.y);
                //this.addedDI.setDrawEndXY(lay,dxy.x,dxy.y);
                return false;
            }
            // public on_mouse_up(pxy: base.Pt, dxy: base.Pt,me:_MouseEvent)
            // {
            //     this.addedDI=null;
            //     this.popOperStackMe();
            // 	return true;
            // }
            draw_oper() {
                var lay = this.getDrawLayer();
                if (lay == null)
                    return;
                if (this.firstPt == null || this.mvPt == null) {
                    return;
                }
                if (this.secondPt != null) {
                    return;
                }
                var p1 = lay.transDrawPt2PixelPt(this.firstPt.x, this.firstPt.y);
                var p2 = lay.transDrawPt2PixelPt(this.mvPt.x, this.mvPt.y);
                //draw line
                var cxt = lay.getCxtCurDraw();
                cxt.save();
                cxt.beginPath();
                cxt.moveTo(p1.x, p1.y);
                cxt.lineTo(p2.x, p2.y);
                cxt.restore();
            }
        }
        interact_6.OperPtsAdd = OperPtsAdd;
    })(interact = oc.interact || (oc.interact = {}));
})(oc || (oc = {}));
/**
 * modify line
 */
var oc;
(function (oc) {
    var interact;
    (function (interact_7) {
        class OperPtsChg extends oc.DrawOper {
            constructor(interact, layer) {
                super(interact, layer);
                this.diPts = null;
                this.diChgPtIdx = null;
                this.downPt = null;
            }
            getOperName() {
                return "pyln_chg";
            }
            setDIPts(ln) {
                this.diPts = ln;
            }
            on_oper_stack_push() {
                this.setCursor(oc.Cursor.crosshair);
            }
            on_oper_stack_pop() {
                this.setCursor(oc.Cursor.auto);
            }
            chkOperFitByDrawPt(pxy, dxy) {
                if (this.diPts == null)
                    return false;
                var idx = this.diPts.chkPixelPtIdxOnPt(pxy.x, pxy.y);
                return idx != null;
            }
            on_mouse_down(pxy, dxy) {
                if (this.diPts == null)
                    return true;
                this.diChgPtIdx = this.diPts.chkPixelPtIdxOnPt(pxy.x, pxy.y);
                if (this.diChgPtIdx == null) { //normal drag
                    return true;
                }
                this.downPt = dxy;
                return false; //stop event delivy
            }
            on_mouse_mv(pxy, dxy) {
                var lay = this.getDrawLayer();
                if (lay == null)
                    return true;
                if (this.diPts == null)
                    return true;
                if (this.diChgPtIdx == null) { //mouse out,pop me
                    if (this.diPts.chkPixelPtIdxOnPt(pxy.x, pxy.y) == null) {
                        this.popOperStackMe();
                        return true;
                    }
                    else {
                        this.setCursor(oc.Cursor.crosshair);
                        return false;
                    }
                }
                this.diPts.setDrawPtIdx(this.diChgPtIdx, dxy.x, dxy.y);
                return false;
            }
            on_mouse_up(pxy, dxy) {
                this.downPt = null;
                //
                this.diChgPtIdx = null;
                this.diPts = null;
                this.popOperStackMe();
                return true;
            }
            draw_oper() {
            }
        }
        interact_7.OperPtsChg = OperPtsChg;
    })(interact = oc.interact || (oc.interact = {}));
})(oc || (oc = {}));
/**
 * conntion between unit
  */
var oc;
(function (oc) {
    var iott;
    (function (iott) {
        let CONN_POS;
        (function (CONN_POS) {
            CONN_POS[CONN_POS["W"] = 0] = "W";
            CONN_POS[CONN_POS["WN"] = 1] = "WN";
            CONN_POS[CONN_POS["N"] = 2] = "N";
            CONN_POS[CONN_POS["NE"] = 3] = "NE";
            CONN_POS[CONN_POS["E"] = 4] = "E";
            CONN_POS[CONN_POS["ES"] = 5] = "ES";
            CONN_POS[CONN_POS["S"] = 6] = "S";
            CONN_POS[CONN_POS["SW"] = 7] = "SW";
        })(CONN_POS = iott.CONN_POS || (iott.CONN_POS = {}));
        ;
        class Conn extends oc.DrawItem {
            constructor(opts) {
                super(opts);
                this.color = 'yellow';
                this.lnW = 1;
                this.bEndArrow = false;
                this.fromId = "";
                this.formPos = CONN_POS.E;
                this.toId = "";
                this.toPos = CONN_POS.W;
                //oc.DrawItem.apply(this,arguments);
                if (opts != undefined) {
                    var ufid = opts.nodeid_from;
                    var utid = opts.nodeid_to;
                    this.fromId = ufid ? ufid : "";
                    this.toId = utid ? utid : "";
                }
            }
            getClassName() {
                return "oc.iott.Conn";
            }
            isVirtual() {
                return true;
            }
            getNodeFromId() {
                return this.fromId;
            }
            setFromPos(p) {
                this.formPos = p;
            }
            getNodeToId() {
                return this.toId;
            }
            setToPos(p) {
                this.toPos = p;
            }
            getDIFrom() {
                var lay = this.getLayer();
                if (lay == null)
                    return null;
                return lay.getItemById(this.fromId);
            }
            getDITo() {
                var lay = this.getLayer();
                if (lay == null)
                    return null;
                return lay.getItemById(this.toId);
            }
            calConnCtrlPt(pxy, ps, pos, ctrllen) {
                switch (pos) {
                    case CONN_POS.W:
                        var p = { x: pxy.x, y: pxy.y + ps.h / 2 };
                        return [p, { x: p.x - ctrllen, y: p.y }];
                    case CONN_POS.WN:
                        var p = pxy;
                        return [p, { x: p.x - ctrllen, y: p.y }];
                    case CONN_POS.SW:
                        var p = { x: pxy.x, y: pxy.y + ps.h };
                        return [p, { x: p.x - ctrllen, y: p.y }];
                    case CONN_POS.NE:
                        var p = { x: pxy.x + ps.w, y: pxy.y };
                        return [p, { x: p.x + ctrllen, y: p.y }];
                    case CONN_POS.ES:
                        var p = { x: pxy.x + ps.w, y: pxy.y + ps.h };
                        return [p, { x: p.x + ctrllen, y: p.y }];
                    case CONN_POS.N:
                        var p = { x: pxy.x + ps.w / 2, y: pxy.y };
                        return [p, { x: p.x, y: p.y - ctrllen }];
                    case CONN_POS.S:
                        var p = { x: pxy.x + ps.w / 2, y: pxy.y + ps.h };
                        return [p, { x: p.x, y: p.y + ctrllen }];
                    case CONN_POS.E:
                    default:
                        var p = { x: pxy.x + ps.w, y: pxy.y + ps.h / 2 };
                        return [p, { x: p.x + ctrllen, y: p.y }];
                }
            }
            draw(ctx, c) {
                var uf = this.getDIFrom();
                var ut = this.getDITo();
                if (uf == null || ut == null)
                    return;
                if (uf.isHidden() || ut.isHidden())
                    return;
                var pf = uf.getPixelXY();
                var ps = uf.getPixelSize();
                var tf = ut.getPixelXY();
                var ts = ut.getPixelSize();
                if (pf == null || ps == null || tf == null || ts == null)
                    return;
                var ctrllen = c.transDrawLen2PixelLen(true, 80);
                var f_pc = this.calConnCtrlPt(pf, ps, this.formPos, ctrllen);
                var t_pc = this.calConnCtrlPt(tf, ts, this.toPos, ctrllen);
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
                if (this.bEndArrow) {
                    ctx.fillStyle = this.color;
                    var arrlen = c.transDrawLen2PixelLen(true, 20);
                    var arrh = c.transDrawLen2PixelLen(true, 8);
                    oc.util.drawArrow(ctx, cp2.x, cp2.y, p2.x, p2.y, arrlen, arrh);
                }
                ctx.restore();
            }
        }
        iott.Conn = Conn;
    })(iott = oc.iott || (oc.iott = {}));
})(oc || (oc = {}));
/// <reference path="../draw_div.ts" />
var oc;
(function (oc) {
    var iott;
    (function (iott) {
        class DIDiv extends oc.DrawDiv {
            constructor(opts) {
                super(opts);
                this.bMin = true;
                //this.div_scroll=true;
            }
            getClassName() {
                return "oc.iott.DIDiv";
            }
            getPropDefs() {
                var r = super.getPropDefs();
                r.push(DIDiv.PNS);
                return r;
            }
            setMin(b) {
                this.bMin = b;
            }
            on_mouse_event(tp, pxy, dxy, e) {
                if (tp == oc.MOUSE_EVT_TP.Down) {
                    if (e.button == oc.MOUSE_BTN.LEFT) {
                        var xy = this.getDrawXY();
                        var dx = dxy.x - xy.x;
                        var dy = dxy.y - xy.y;
                        if (dx > 0 && dy > 0 && dx < DIDiv.LEFTTOP_R && dy < DIDiv.LEFTTOP_R) { //click top left ctrl
                            //console.log("clk top left");
                            this.bMin = !this.bMin;
                            this.MODEL_fireChged([]);
                        }
                    }
                }
            }
            drawLeftTop(ctx) {
                var c = this.getContainer();
                if (c == null)
                    return;
                var xy = this.getPixelXY();
                if (xy == null)
                    return;
                var hh = c.transDrawLen2PixelLen(false, DIDiv.LEFTTOP_R) / 2;
                ctx.beginPath();
                ctx.strokeStyle = "red";
                ctx.lineWidth = 2;
                ctx.arc(xy.x + hh, xy.y + hh, hh, 0, Math.PI * 2);
                ctx.stroke();
                ctx.fillStyle = "pink";
                ctx.closePath();
                ctx.fill();
                return;
            }
            draw(ctx, c) {
                //draw circle at left top position
                this.drawLeftTop(ctx);
                if (this.bMin) {
                    this.hideDivEle();
                    return;
                }
                super.draw(ctx, c);
            }
            draw_sel(ctx, c) {
                if (this.bMin) {
                    return;
                }
                super.draw_sel(ctx, c);
            }
        }
        DIDiv.PNS = {
            _cat_name: "div", _cat_title: "Div",
            bMin: { title: "Is Min", type: "bool" }
        };
        DIDiv.LEFTTOP_R = 30;
        iott.DIDiv = DIDiv;
    })(iott = oc.iott || (oc.iott = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var iott;
    (function (iott) {
        class ListItem {
            constructor(dl) {
                this.id = "";
                this.name = "";
                this.title = "";
                this.divList = dl;
            }
            getId() {
                return this.id;
            }
            getName() {
                return this.name;
            }
            getTitle() {
                return this.title;
            }
            isValid() {
                return this.id != "";
            }
            getActionTypeName() {
                return "list_item";
            }
            getPanel() {
                return this.divList.getPanel();
            }
        }
        iott.ListItem = ListItem;
        class DIDivList extends iott.DIDiv {
            constructor(opts) {
                super(opts);
                this.parentTN = null;
                this.tbHead = [];
                this.listItems = [];
                //tbBody:oc.base.Props<any>={};
                //private tbBodyEle:JQuery<HTMLElement>|null=null;
                this.bFirst = true;
            }
            hasSubNode() {
                return false;
            }
            getSubNode() {
                return null;
            }
            getParentTN() {
                return this.parentTN;
            }
            setParentTN(p) {
                this.parentTN = p;
            }
            isHidden() {
                if (this.parentTN == null)
                    return false;
                if (this.parentTN.isHidden())
                    return true;
                return !this.parentTN.isSubExpanded();
            }
            /**
             * override to fix parent bottom
             */
            getDrawXY() {
                var ptn = this.getParentTN();
                if (ptn == null)
                    return super.getDrawXY();
                var pxy = ptn.getDrawXY();
                //set actual xy,to avoid some draw size err
                this.x = pxy.x;
                this.y = pxy.y + ptn.getH();
                return { x: this.x, y: this.y };
            }
            getListItem(id) {
                for (var li of this.listItems) {
                    if (id == li.id)
                        return li;
                }
                return null;
            }
            getListItemPixelRect(id) {
                for (var i = 0; i < this.listItems.length; i++) {
                    var li = this.listItems[i];
                    if (id == li.id) {
                    }
                }
                return null;
            }
            setListTable(hds, items) {
                this.tbHead = hds;
                if (items == null || items == undefined)
                    items = [];
                var ss = `<table class="oc_div_list" id="divlist_tb_${this.id}"><thead><tr>`;
                for (var h of this.tbHead) {
                    ss += `<th>${h.t}</th>`;
                }
                ss += `</tr></thead><tbody id="div_list_bd_${this.getId()}">`;
                for (var item of items) {
                    ss += `<tr id="divlist_tr_${item.id}" didiv_listitem="${item.id}"
                 onmousedown="oc.iott.DIDivList.trAction(${oc.MOUSE_EVT_TP.Down},'${this.id}','${item.id}')"
                 ondblclick="oc.iott.DIDivList.trAction(${oc.MOUSE_EVT_TP.DbClk},'${this.id}','${item.id}')" >`;
                    for (var h of this.tbHead) {
                        var v = item[h.n];
                        v = v ? v : "";
                        ss += `<td>${v}</td>`;
                    }
                    ss += `</tr>`;
                    item.divList = this;
                }
                ss += `</tbody></table>`;
                var tb = $(ss);
                tb.get(0)["_oc_di_divlist"] = this;
                this.listItems = items;
                super.setInnerEle(tb);
                tb[0].onmousedown = (e) => {
                    //e.preventDefault();
                    //e.stopPropagation();
                };
            }
            setListDyn(items) {
                if (items == null || items == undefined)
                    return;
                for (var item of items) {
                    var ocdyn = item._oc_dyn;
                    if (ocdyn == undefined || ocdyn == null)
                        continue;
                    var tds = $(`#divlist_tr_${item.id} td`);
                    if (tds.length <= 0)
                        continue;
                    for (var i = 0; i < tds.length && i < this.tbHead.length; i++) {
                        var h = this.tbHead[i];
                        var v = ocdyn[h.n];
                        if (v == undefined || v == null)
                            continue;
                        tds[i].innerHTML = v;
                    }
                }
            }
            on_item_mouse_event(item, tp, pxy, dxy, e) {
                if (tp == oc.MOUSE_EVT_TP.Down) {
                    if (e.button == oc.MOUSE_BTN.RIGHT) {
                        if (oc.PopMenu.createShowPopMenu(item, pxy, dxy))
                            e.preventDefault();
                    }
                    return;
                }
                if (tp == oc.MOUSE_EVT_TP.DbClk) {
                    var pmi = oc.PopMenu.getDefaultPopMenuItem(item);
                    if (pmi != null) {
                        pmi.action(item, pmi.op_name, pxy, dxy);
                    }
                    return;
                }
            }
            getActionTypeName() {
                return "div_list";
            }
            on_mouse_event(tp, pxy, dxy, e) {
                if (tp == oc.MOUSE_EVT_TP.Down) {
                    if (e.button == oc.MOUSE_BTN.RIGHT) {
                        if (oc.PopMenu.createShowPopMenu(this, pxy, dxy))
                            e.preventDefault();
                    }
                }
                super.on_mouse_event(tp, pxy, dxy, e);
            }
            static trAction(tp, diid, id) {
                //console.log("menuAction")
                var tb = $("#divlist_tb_" + diid).get(0);
                var di = tb["_oc_di_divlist"];
                if (di == undefined || di == null)
                    return;
                var e = window.event;
                if (e == undefined || e == null)
                    return;
                e.stopPropagation();
                if (di != undefined && di != null) {
                    var p = di.getPanel();
                    var c = di.getContainer();
                    if (p == null || c == null)
                        return;
                    var li = di.getListItem(id);
                    if (li == null)
                        return;
                    var pxy = p.getEventPixel(e);
                    var dxy = c.transPixelPt2DrawPt(pxy.x, pxy.y);
                    di.on_item_mouse_event(li, tp, pxy, dxy, e);
                    e.stopPropagation();
                }
            }
            getClassName() {
                return "oc.iott.DIDivList";
            }
        }
        iott.DIDivList = DIDivList;
    })(iott = oc.iott || (oc.iott = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var iott;
    (function (iott) {
        /**
         * Unit TreeNode which has inner div
         * then div can has it's owner display content
         * e.g list
         */
        class DIHtml extends oc.DrawItemRect {
            constructor(opts) {
                super(opts);
                this.divEle = null;
                this.contEle = null;
                this.innerEle = null;
                this.scroll = false;
                this.html = "";
                this.bMin = true;
            }
            getClassName() {
                return "oc.iott.DIHtml";
            }
            getPropDefs() {
                var r = super.getPropDefs();
                r.push(DIHtml.PNS);
                return r;
            }
            getContEle() {
                if (this.contEle != null)
                    return this.contEle;
                var p = this.getPanel();
                if (p == null)
                    return null;
                var ele = p.getHTMLElement();
                var tmpid = this.getId();
                var sty = ``;
                if (this.scroll)
                    sty = `overflow: auto;`;
                this.divEle = $(`<div id="div_${tmpid}" class="oc_unit_action" style="${sty}">
                <div id="c_${tmpid}" class="content">${this.html}</div></div>`);
                this.divEle.get(0)["_oc_di_html"] = this;
                $(ele).append(this.divEle);
                this.contEle = $(`#c_${tmpid}`);
                if (this.innerEle != null)
                    this.contEle.append(this.innerEle);
                return this.contEle;
            }
            /**
             * override to del div
             */
            removeFromContainer() {
                if (!super.removeFromContainer())
                    return false;
                if (this.divEle != null)
                    this.divEle.remove();
                return true;
            }
            setInnerEle(ele) {
                if (typeof (ele) == "string")
                    ele = $(ele);
                this.innerEle = ele;
                var contele = this.getContEle();
                if (contele == null)
                    return this.innerEle;
                contele.empty();
                contele.append(this.innerEle);
                return this.innerEle;
            }
            draw_hidden(cxt, c) {
                this.hideDivEle();
            }
            hideDivEle() {
                if (this.divEle == null)
                    return;
                this.divEle.css("display", "none");
            }
            displayDivEle() {
                if (this.divEle == null)
                    return;
                var contele = this.getContEle();
                if (contele == null || this.divEle == null)
                    return;
                var c = this.getContainer();
                if (c == null)
                    return;
                var hh = c.transDrawLen2PixelLen(false, 30);
                var r = this.getBoundRectPixel();
                if (r != null) {
                    this.divEle.css("display", "");
                    this.divEle.css("top", (r.y + hh) + "px");
                    this.divEle.css("left", r.x + "px");
                    this.divEle.css("width", r.w + "px");
                    this.divEle.css("height", (r.h - hh) + "px");
                }
            }
            getPrimRect() {
                return new oc.base.Rect(0, 0, 100, 100);
            }
            on_mouse_event(tp, pxy, dxy, e) {
                if (tp == oc.MOUSE_EVT_TP.Down) {
                    if (e.button == oc.MOUSE_BTN.LEFT) {
                        var xy = this.getDrawXY();
                        var dx = dxy.x - xy.x;
                        var dy = dxy.y - xy.y;
                    }
                }
            }
            drawPrim(ctx) {
                oc.util.drawRect(ctx, 0, 0, 100, 100, null, null, 1, "#8cdcda");
                this.displayDivEle();
            }
            getTitle() {
                var t = super.getTitle();
                if (t == null)
                    return "";
                return t;
            }
            getMinDrawSize() {
                var t = this.getTitle();
                return { w: 20 * t.length, h: 30 };
            }
            draw(ctx, c) {
                if (this.bMin) {
                    this.hideDivEle();
                    return;
                }
                super.draw(ctx, c);
                var pxy = this.getPixelXY();
                if (pxy == null)
                    return;
                var fh = c.transDrawLen2PixelLen(false, 20);
                //var pt = c.tr
                ctx.font = `${fh}px serif`;
                ctx.fillStyle = "yellow";
                var t = this.getTitle();
                if (t == null)
                    t = "";
                ctx.fillText(t, pxy.x + fh, pxy.y + fh);
            }
            draw_sel(ctx, c) {
                if (this.bMin) {
                    return;
                }
                super.draw_sel(ctx, c);
            }
            drawPrimSel(ctx) {
            }
        }
        DIHtml.PNS = {
            _cat_name: "div", _cat_title: "Div",
            bMin: { title: "Is Min", type: "bool" },
            html: { title: "Html", type: "str", edit_plug: "html", read_only: true },
            scroll: { title: "Scroll", type: "bool" }
        };
        DIHtml.LEFTTOP_R = 30;
        iott.DIHtml = DIHtml;
    })(iott = oc.iott || (oc.iott = {}));
})(oc || (oc = {}));
/**
 * for iottree panel
 */
var oc;
(function (oc) {
    var iott;
    (function (iott) {
        class InteractEditUnit extends oc.DrawInteract {
        }
        iott.InteractEditUnit = InteractEditUnit;
    })(iott = oc.iott || (oc.iott = {}));
})(oc || (oc = {}));
/**
 * for iottree panel
 */
var oc;
(function (oc) {
    var iott;
    (function (iott) {
        class OperMember extends oc.DrawOper {
            constructor(interact, layer) {
                super(interact, layer);
                this.unitTN = null;
                this.curMem = null;
                this.downPt = null;
                this.itemDragPt = null;
            }
            setUnitTN(u) {
                this.unitTN = u;
            }
            getOperName() {
                return "oper_member";
            }
            on_oper_stack_push() {
                this.setCursor(oc.Cursor.hand);
                console.log("oper mem push");
            }
            on_oper_stack_pop() {
                this.setCursor(oc.Cursor.auto);
                console.log("oper mem pop");
            }
            chkOperFitByDrawPt(pxy, dxy) {
                return false;
            }
            on_mouse_down(pxy, dxy) {
                if (this.unitTN == null)
                    return false;
                for (var m of this.unitTN.getMembers()) {
                    if (m.chkCanSelectDrawPt(dxy.x, dxy.y)) {
                        this.curMem = m;
                        this.downPt = dxy;
                        this.itemDragPt = m.getDrawXY();
                        this.curMem.setPosState(iott.Member.PST_MOVING);
                        break;
                    }
                }
                return false; //stop event delivy
            }
            on_mouse_mv(pxy, dxy) {
                var lay = this.getDrawLayer();
                if (lay == null)
                    return true;
                if (this.unitTN == null)
                    return true;
                if (this.curMem != null && this.downPt != null && this.itemDragPt != null) {
                    this.curMem.setDrawXY(this.itemDragPt.x + (dxy.x - this.downPt.x), this.itemDragPt.y + (dxy.y - this.downPt.y));
                    return false;
                }
                var bin = this.unitTN.chkCanSelectDrawPt(dxy.x, dxy.y);
                if (!bin) { //mouse out,pop me
                    this.popOperStackMe();
                    return true;
                }
                return false;
            }
            on_mouse_up(pxy, dxy) {
                if (this.unitTN != null && this.curMem != null && this.downPt != null && this.itemDragPt != null) {
                    if (this.unitTN.chkCanSelectDrawPt(dxy.x, dxy.y)) { //in
                        this.curMem.setPosState(iott.Member.PST_DEFAULT);
                    }
                    else { //out
                        this.curMem.setPosState(iott.Member.PST_OUT);
                    }
                    this.curMem = null;
                    return false;
                }
                this.popOperStackMe();
                return true;
            }
            draw_oper() {
            }
        }
        iott.OperMember = OperMember;
        class InteractEditRep extends oc.DrawInteract {
            constructor(panel, layer, opts) {
                super(panel, layer, opts);
                this.operChgLn = new oc.interact.OperChgLine(this, layer);
                this.operChgRect = new oc.interact.OperChgRect(this, layer);
                this.operChgArc = new oc.interact.OperChgArc(this, layer);
                this.operPtsChg = new oc.interact.OperPtsChg(this, layer);
                this.operMem = new OperMember(this, layer);
            }
            on_mouse_mv(pxy, dxy, e) {
                super.on_mouse_mv(pxy, dxy, e);
                var p = this.getPanel();
                if (p == null)
                    return;
                var curon = this.getCurMouseOnItem();
                var sitem = this.getSelectedItem();
                if (sitem != null) //curon!=null&&curon==sitem)
                 { //
                    if (sitem instanceof oc.di.DILine) {
                        this.operChgLn.setDILine(sitem);
                        if (this.operChgLn.chkOperFitByDrawPt(pxy, dxy))
                            this.pushOperStack(this.operChgLn);
                    }
                    else if (sitem instanceof oc.di.DIPts) {
                        this.operPtsChg.setDIPts(sitem);
                        if (this.operPtsChg.chkOperFitByDrawPt(pxy, dxy))
                            this.pushOperStack(this.operPtsChg);
                    }
                    else if (sitem instanceof iott.UnitTN) {
                        var r = sitem.getShowMemberRadius();
                        if (r != null) {
                            if (sitem.chkCanSelectDrawPt(dxy.x, dxy.y)) {
                                this.operMem.setUnitTN(sitem);
                                this.pushOperStack(this.operMem);
                            }
                        }
                    }
                    // else if(sitem instanceof Member)
                    // {
                    // 	var pbt = sitem.getBelongTo() ;
                    // 	if(pbt!=null)
                    // 	{
                    // 		//if(pbt.chkCanSelectDrawPt(dxy.x,dxy.y))
                    // 		{
                    // 			this.operMem.setUnitTN(pbt) ;
                    // 			this.pushOperStack(this.operMem) ;
                    // 		}
                    // 	}
                    // }
                    else if (sitem instanceof oc.DrawItemRect) {
                        this.operChgRect.setRect(sitem);
                        if (this.operChgRect.chkOperFitByDrawPt(pxy, dxy))
                            this.pushOperStack(this.operChgRect);
                        if (sitem instanceof oc.di.DIArc) {
                            this.operChgArc.setDIArc(sitem);
                            if (this.operChgArc.chkOperFitByDrawPt(pxy, dxy)) {
                                this.pushOperStack(this.operChgArc);
                            }
                        } //
                    }
                }
                else {
                    //this.setCursor(undefined);
                }
            }
            on_mouse_dbclk(pxy, dxy, e) {
                var curon = this.getCurMouseOnItem();
                if (curon == null)
                    return;
                if (curon instanceof oc.di.DITxt) {
                    var lay = this.getLayer();
                    var opedittxt = new oc.interact.OperEditTxt(this, lay, curon, "txt");
                    this.pushOperStack(opedittxt);
                }
            }
            on_mouse_dragover(pxy, dxy, dd) {
                var di = null;
                console.log(dd);
                switch (dd._tp) {
                    case "unit":
                        var un = dd._val;
                        var du = oc.DrawUnit.getUnitByName(un);
                        if (du == null) {
                            //oc.util.prompt_msg("");
                            return;
                        }
                        var du_g = dd._g; //du.getInsGroup();
                        var tmpsis = [];
                        if (du_g != null && du_g != undefined) {
                            for (var item of this.getLayer().getItemsShow()) {
                                if (item instanceof iott.Win) {
                                    if (du_g == item.getName()) {
                                        tmpsis.push(item);
                                    }
                                }
                            }
                        }
                        //console.log(tmpsis) ;
                        this.dragoverSelItems = tmpsis;
                        break;
                }
            }
            on_mouse_dragleave(pxy, dxy, dd) {
                this.dragoverSelItems = [];
            }
            on_mouse_drop(pxy, dxy, dd) {
                var di = null;
                switch (dd._tp) {
                    case "icon_fa":
                        di = new oc.di.DIIcon({ unicode: dd._val });
                        di.setDrawXY(dxy.x, dxy.y);
                        this.getLayer().addItem(di);
                        break;
                    case "unit":
                        var un = dd._val;
                        var du = oc.DrawUnit.getUnitByName(un);
                        if (du == null) {
                            //oc.util.prompt_msg("");
                            break;
                        }
                        var du_sz = du.getUnitDrawSize();
                        var du_g = du.getInsGroup();
                        var newurl = du.getInsNewUrl();
                        var lay = this.getLayer(); //du.getLayer();
                        var new_cn = du.getInsNewCN();
                        if (newurl == null || newurl == "" || lay == null || new_cn == null || new_cn == "")
                            break;
                        var pm = {};
                        pm["layer_name"] = lay.getName();
                        pm["unit_name"] = du.getName();
                        $.ajax({
                            type: 'post',
                            url: newurl,
                            data: pm,
                            async: true,
                            success: (result) => {
                                result = result.trim();
                                if (result.indexOf("{") != 0) { //
                                    oc.util.prompt_err(result);
                                    return;
                                }
                                var ob;
                                eval("ob=" + result);
                                var insid = ob["unit_ins_id"];
                                var di = eval(`new ${new_cn}({})`);
                                if (di != undefined) {
                                    di.setUnitName(un);
                                    di.setId(insid);
                                    di.setGroupName(du_g);
                                    di.setDrawSize(du_sz.w, du_sz.h);
                                    this.getLayer().addItem(di);
                                    di.setDrawXY(dxy.x, dxy.y);
                                }
                            }
                        });
                        break;
                }
                this.dragoverSelItems = [];
            }
            on_key_down(e) {
                super.doCopyPaste(e);
                if (this.isOperDefault()) {
                    //fconsole.log("k="+e.keyCode);
                    switch (e.keyCode) {
                        case 46: //del
                            this.removeSelectedItems();
                            break;
                        case 38: //up
                        case 37: //left
                        case 39: //right
                        case 40: //down
                            this.moveByKeyDir(e.keyCode);
                            break;
                    }
                }
            }
            setOperAddItem(dicn, opts) {
                var lay = this.getLayer();
                if (lay == null)
                    return false;
                var oper = new oc.interact.OperAddItem(this, lay, dicn, opts);
                this.pushOperStack(oper);
                return true;
            }
            setOperAddPts(tp, opts) {
                var lay = this.getLayer();
                if (lay == null)
                    return false;
                var oper = new oc.interact.OperPtsAdd(this, lay, opts, tp);
                this.pushOperStack(oper);
                return true;
            }
            setOperAddUnitIns(unitname) {
                var lay = this.getLayer();
                if (lay == null)
                    return false;
                var oper = oc.interact.OperAddItem.createOperAddByUnitName(this, lay, unitname, undefined);
                if (oper == null)
                    return false;
                this.pushOperStack(oper);
                return true;
            }
            removeSelectedItems() {
                var si = this.getSelectedItem(); //this.getSelectedItems() ;
                if (si == null)
                    return;
                console.log(si.getMark());
                if (si.getMark() == null) {
                    this.clearSelectedItems();
                    si.removeFromContainer();
                    return;
                }
                if (si.on_before_del()) { //may 
                    this.clearSelectedItems();
                    si.removeFromContainer();
                }
                // if(sis.length>0)
                // {
                // 	this.clearSelectedItems();
                // 	for(var si of sis)
                // 	{
                // 		si.removeFromContainer();
                // 	}
                // }
            }
            moveByKeyDir(keycode) {
                var p = this.getPanel();
                switch (keycode) {
                    case 38: //up
                        p.movePixelCenter(0, -30);
                        break;
                    case 37: //left
                        p.movePixelCenter(-30, 0);
                        break;
                    case 39: //right
                        p.movePixelCenter(30, 0);
                        break;
                    case 40: //down
                        p.movePixelCenter(0, 30);
                        break;
                }
            }
        } // end InteractEditRep
        iott.InteractEditRep = InteractEditRep;
    })(iott = oc.iott || (oc.iott = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var iott;
    (function (iott) {
        //export IOTT
        /**
         *
         */
        class IOTTModel {
            constructor(opts) {
                this.listeners = [];
                this.valUnit = null;
                this.valTemp = null;
                this.valCont = null;
                this.valDyn = null;
                this.tempUrl = opts.temp_url;
                this.contUrl = opts.cont_url;
                this.dynUrl = opts.dyn_url;
                this.unitUrl = opts.unit_url;
            }
            registerListener(lis) {
                this.listeners.push(lis);
            }
            /**
             * view need call this method,to notify outer is ready,model can start
             * load data,and fire mode changing event
             */
            initModel() {
                var pm = {};
                oc.util.doAjax(this.tempUrl, pm, (bsucc, ret) => {
                    if (bsucc) {
                        this.valTemp = ret;
                        this.fireModelLoaded("temp", ret);
                        //this.loadOrUpdate();
                    }
                });
                oc.util.doAjax(this.unitUrl, pm, (bsucc, ret) => {
                    if (bsucc) {
                        this.valUnit = ret;
                        this.fireModelLoaded("unit", ret);
                        //firefox edge must load cont after unit
                        this.loadOrUpdate();
                    }
                });
            }
            loadOrUpdate() {
                oc.util.doAjax(this.contUrl, {}, (bsucc, ret) => {
                    if (!bsucc || (typeof (ret) == "string" && ret.indexOf("{") != 0)) {
                        oc.util.prompt_err(ret);
                        return;
                    }
                    this.valCont = ret;
                    this.fireModelContChged(ret);
                });
            }
            refreshDyn(endcb) {
                oc.util.doAjax(this.dynUrl, {}, (bsucc, ret) => {
                    try {
                        if (!bsucc || (typeof (ret) == "string" && ret.indexOf("{") != 0)) {
                            oc.util.prompt_err(ret);
                            return;
                        }
                        this.valDyn = ret;
                        this.fireModelDynUpdated(ret);
                    }
                    finally {
                        if (endcb)
                            endcb();
                    }
                });
            }
            getTemplate() {
                return {};
            }
            getContent() {
                return {};
            }
            getRTDynData() {
                return {};
            }
            fireModelLoaded(tp, mv) {
                for (var lis of this.listeners) {
                    switch (tp) {
                        case "temp":
                            lis.on_model_temp_loaded(mv);
                            break;
                        case "unit":
                            lis.on_model_unit_loaded(mv);
                            break;
                    }
                }
            }
            fireModelContChged(cont) {
                for (var lis of this.listeners) {
                    lis.on_model_cont_chged(cont);
                }
            }
            fireModelDynUpdated(dyn) {
                for (var lis of this.listeners) {
                    lis.on_model_dyn_updated(dyn);
                }
            }
        }
        iott.IOTTModel = IOTTModel;
    })(iott = oc.iott || (oc.iott = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var iott;
    (function (iott) {
        class IOTTLayer extends oc.DrawLayer {
            constructor(opts) {
                super(opts);
                this.menuEle = null;
                this.unitName = "";
            }
            on_mouse_event(tp, pxy, dxy, e) {
                if (tp == oc.MOUSE_EVT_TP.Down) { //display rep right menu
                    if (e.button == oc.MOUSE_BTN.RIGHT) { //right
                        if (oc.PopMenu.createShowPopMenu(this, pxy, dxy))
                            e.preventDefault();
                    }
                }
            }
            setUnitName(u) {
                this.unitName = u;
            }
            getActionTypeName() {
                return "layer";
            }
        }
        iott.IOTTLayer = IOTTLayer;
        /**
         * using template and unit(unittn) to support load iott graphics data
         * 1)Panel load a template which has some container(group). e.g conn ui
         * 2)panel load from app ajax ,and make unit nodes show etc.
         * 3)support some edit action like add,modify,delete etc
         */
        class IOTTView extends oc.DrawView {
            constructor(m, dp, de, opts) {
                super(m, dp);
                this.contId = "";
                this.contName = "";
                this.contTitle = "";
                this.contUnitName = "";
                this.options = {};
                this.bLoadFirst = true;
                this.repDI = null;
                this.iottModel = m;
                this.drawPanel = dp;
                this.drawPanel.init_panel();
                this.drawPanel.on_draw();
                this.drawEditor = de;
                if (this.drawEditor != null)
                    this.drawEditor.init_editor();
                this.drawLayer = new IOTTLayer({});
                this.drawPanel.addLayer(this.drawLayer);
                this.drawInter = new oc.iott.InteractEditRep(this.drawPanel, this.drawLayer, { copy_paste_url: opts.copy_paste_url });
                this.drawPanel.setInteract(this.drawInter);
                this.options = opts;
                this.iottModel.registerListener(this);
            }
            init() {
                this.iottModel.initModel();
            }
            getPanel() {
                return this.drawPanel;
            }
            getLayer() {
                return this.drawLayer;
            }
            getInteract() {
                return this.drawInter;
            }
            sendMsgToServer(msg) {
            }
            on_model_temp_loaded(temp) {
                if (temp == null || temp == "")
                    return;
                if (typeof (temp) == 'string')
                    eval("temp=" + temp);
                this.drawLayer.inject(temp, undefined);
            }
            on_model_unit_loaded(ret) {
                if (typeof (ret) == "string") {
                    if (ret.indexOf("[") != 0) { //
                        oc.util.prompt_err("load unit def err:" + ret);
                        return;
                    }
                }
                var ob = null;
                if (typeof (ret) == "string")
                    eval("ob=" + ret);
                else
                    ob = ret;
                for (var item of ob) {
                    oc.DrawUnit.addUnitByJSON(item);
                }
            }
            on_model_cont_chged(cont) {
                if (typeof (cont) == 'string')
                    eval("cont=" + cont);
                this.injectContJSON(cont);
                this.drawInter.clearSelectedItem();
                //this.drawLayer.inject(cont,"u");
                if (this.bLoadFirst) {
                    this.bLoadFirst = false;
                    this.drawLayer.ajustDrawFit();
                }
            }
            on_model_dyn_updated(dyn) {
                if (typeof (dyn) == 'string')
                    eval("dyn=" + dyn);
                this.injectDynJSON(dyn);
                this.drawLayer.update_draw();
            }
            injectDynJSON(dyn) {
                var octp = dyn["_oc_tp"];
                if (octp == "unit")
                    this.dynJSON2Unit(dyn);
                //else if(octp=="list")
                //    this.dynJSON2List(dyn);
            }
            injectContJSON(cont) {
                this.contId = cont["id"];
                this.contName = cont["name"];
                this.contTitle = cont["title"];
                if (oc.util.chkNotEmpty(this.contId))
                    this.drawLayer["id"] = this.contId;
                if (oc.util.chkNotEmpty(this.contName))
                    this.drawLayer["name"] = this.contName;
                if (oc.util.chkNotEmpty(this.contTitle))
                    this.drawLayer["title"] = this.contTitle;
                //this.contUnitName=cont["unitName"];
                //if(this.contUnitName!=undefined&&this.contUnitName!=null)
                //    this.drawLayer.setUnitName(this.contUnitName);
                var not_del_ids = [];
                var lay = this.getLayer();
                this.repDI = IOTTView.transJSON2Unit(lay, null, cont, 0, not_del_ids);
                // var subs = cont[UnitTN.UNIT_SUB];
                // var c=0 ;
                // for(var sub of subs)
                // {
                //     var octp = sub["_oc_tp"];
                //     if(octp=="unit")
                //         this.transJSON2Unit(null,sub,c,not_del_ids);
                //     else if(octp=="list")
                //         this.transJSON2List(null,sub,c)
                //     c ++ ;
                // }
                //chk need del drawitem
                for (var u of this.findUnitsAll()) {
                    if (not_del_ids.indexOf(u.getId()) < 0) {
                        var conns = this.findConnsByUnit(u);
                        u.removeFromContainer();
                        for (var tmpc of conns)
                            tmpc.removeFromContainer();
                    }
                }
            }
            findUnitsAll() {
                var lay = this.getLayer();
                var r = [];
                for (var di of lay.getItemsAll()) {
                    if (di instanceof iott.Unit)
                        r.push(di);
                }
                return r;
            }
            findConnsByUnit(u) {
                var lay = this.getLayer();
                var r = [];
                for (var di of lay.getItemsAll()) {
                    if (di instanceof iott.Conn) {
                        if (di.getNodeFromId() == u.getId())
                            r.push(di);
                        else if (di.getNodeToId() == u.getId())
                            r.push(di);
                    }
                }
                return r;
            }
            extractContJSON() {
                var r = {};
                if (this.repDI != null)
                    this.transUnit2JSON(r, this.repDI);
                r["id"] = this.contId;
                r["name"] = this.contName;
                r["title"] = this.contTitle;
                return r;
            }
            /**
             *
             * @param lay outer provided it,e.g for outer panel preview
             * @param cont
             */
            static injectLayerByCont(lay, cont) {
                var not_del_ids = [];
                //var lay = this.getLayer() ;
                IOTTView.transJSON2Unit(lay, null, cont, 0, not_del_ids); // as UnitTN;
            }
            static transJSON2Unit(lay, parent_u, uobj, idx, not_del_ids) {
                var id = uobj["id"];
                if (id == undefined || id == null || id == "")
                    return null;
                var un = uobj["unitName"];
                if (un == undefined || un == null || un == "")
                    return null;
                var du = oc.DrawUnit.getUnitByName(un);
                if (du == null) {
                    return null;
                }
                //var lay = this.getLayer();
                var du_sz = du.getUnitDrawSize();
                var du_g = du.getInsGroup();
                var new_cn = iott.Unit.matchTempName2CN(du.getName());
                if (new_cn == null || new_cn == "")
                    new_cn = du.getInsNewCN();
                if (new_cn == null || new_cn == "")
                    return null;
                var di = lay.getItemById(id);
                if (di == undefined || di == null) {
                    di = eval(`new ${new_cn}({})`);
                    if (di == undefined)
                        return null;
                    di.setMark("unit"); //
                    lay.addItem(di);
                }
                di.inject(uobj, false);
                di.setUnitName(un);
                di.setGroupName(du_g);
                di.setDrawSize(du_sz.w, du_sz.h);
                //this id cannot be del
                not_del_ids.push(di.getId());
                if (parent_u != null) {
                    var pxy = parent_u.getDrawXY();
                    if (di.x == 0 && di.y == 0)
                        di.setDrawXY(pxy.x + du_sz.w * 2, pxy.y + du_sz.h * 2 * idx);
                    if (parent_u instanceof iott.UnitTN) {
                        var tmpu = di;
                        parent_u.setChildTNS(tmpu);
                        var conn = new iott.Conn({ nodeid_from: parent_u.getId(), nodeid_to: tmpu.getId() });
                        lay.addItem(conn);
                    }
                }
                else {
                    if (di.x == 0 && di.y == 0)
                        di.setDrawXY(du_sz.w * 2, du_sz.h * 2 * idx);
                }
                var subs = uobj[iott.UnitTN.UNIT_SUB];
                if (subs != undefined && subs != null) {
                    var c = 0;
                    for (var sub of subs) {
                        var octp = sub["_oc_tp"];
                        if (octp == "unit")
                            this.transJSON2Unit(lay, di, sub, c, not_del_ids);
                        else if (octp == "list")
                            this.transJSON2List(lay, di, sub, c);
                        else if (octp == "member")
                            this.transJSON2Member(lay, di, sub, c);
                        c++;
                    }
                }
                //di.setDrawXY(dxy.x,dxy.y) ;
                return di;
            }
            dynJSON2Unit(uobj) {
                var id = uobj["id"];
                if (id == undefined || id == null || id == "")
                    return null;
                var lay = this.getLayer();
                var di = lay.getItemById(id);
                if (di == undefined || di == null) {
                    return null;
                }
                var dyn = uobj["_oc_dyn"];
                if (dyn != undefined && dyn != null) {
                    di.setDynData(dyn, false);
                }
                var subs = uobj[iott.UnitTN.UNIT_SUB];
                if (subs != undefined && subs != null) {
                    var c = 0;
                    for (var sub of subs) {
                        var octp = sub["_oc_tp"];
                        if (octp == "unit")
                            this.dynJSON2Unit(sub);
                        else if (octp == "member") {
                            this.dynJSON2Member(sub);
                        }
                        else if (octp == "list")
                            this.dynJSON2List(sub);
                        c++;
                    }
                }
                //di.setDrawXY(dxy.x,dxy.y) ;
                return di;
            }
            static transJSON2List(lay, parent_u, lobj, idx) {
                var id = lobj["id"];
                if (id == undefined || id == null || id == "")
                    return null;
                //var lay = this.getLayer();
                var di = lay.getItemById(id);
                if (di == undefined || di == null) {
                    di = new iott.DIDivList(undefined);
                    di.setMark("unit"); //
                    lay.addItem(di);
                    di.inject(lobj, false);
                }
                else {
                    var olddxy = di.getDrawXY();
                    var oldds = di.getDrawSize();
                    di.inject(lobj, false);
                    di.setDrawXY(olddxy.x, olddxy.y);
                    di.setDrawSize(oldds.w, oldds.h);
                }
                var head = lobj["head"];
                if (head) {
                    var jsitems = lobj["items"];
                    var items = [];
                    for (var joitem of jsitems) {
                        var item = new iott.ListItem(di);
                        for (var n in joitem)
                            item[n] = joitem[n];
                        if (item.isValid())
                            items.push(item);
                    }
                    di.setListTable(head, items);
                }
                //di.setDrawSize(100,100);
                if (parent_u != null) {
                    var pxy = parent_u.getDrawXY();
                    if (di.x == 0 && di.y == 0)
                        di.setDrawXY(pxy.x + parent_u.getW() * 2, pxy.y);
                    if (parent_u instanceof iott.UnitTN) {
                        parent_u.setChildTNS(di);
                        //var conn = new Conn({nodeid_from:parent_u.getId(),nodeid_to:di.getId()});
                        //conn.setToPos(CONN_POS.WN);
                        //lay.addItem(conn);
                    }
                }
                else {
                    if (di.x == 0 && di.y == 0)
                        di.setDrawXY(100, 20 * idx);
                }
                return di;
            }
            static transJSON2Member(lay, parent_u, lobj, idx) {
                var id = lobj["id"];
                var tp = lobj["member_tp"];
                if (id == undefined || id == null || id == "" || tp == undefined || tp == null || tp == "")
                    return null;
                //var lay = this.getLayer();
                var di = lay.getItemById(id);
                if (di == undefined || di == null) {
                    switch (tp) {
                        case iott.MemberTagList.TP:
                            var mtl = new iott.MemberTagList(undefined);
                            lay.addItem(mtl);
                            mtl.inject(lobj, false);
                            var head = lobj["head"];
                            if (head) {
                                var jsitems = lobj["items"];
                                var items = [];
                                for (var joitem of jsitems) {
                                    var item = new iott.ListItem(di);
                                    for (var n in joitem)
                                        item[n] = joitem[n];
                                    if (item.isValid())
                                        items.push(item);
                                }
                                mtl.getDivList().setListTable(head, items);
                            }
                            di = mtl;
                            break;
                        case iott.MemberConn.TP:
                            di = new iott.MemberConn(undefined);
                            lay.addItem(di);
                            di.inject(lobj, false);
                            break;
                        default:
                            return null;
                    }
                }
                else {
                    var olddxy = di.getDrawXY();
                    di.inject(lobj, false);
                    di.setDrawXY(olddxy.x, olddxy.y);
                }
                //di.setDrawSize(100,100);
                if (parent_u != null) {
                    var pxy = parent_u.getDrawXY();
                    if (di.x == 0 && di.y == 0)
                        di.setDrawXY(pxy.x + parent_u.getW() * 2, pxy.y);
                    if (parent_u instanceof iott.UnitTN) {
                        parent_u.setMember(di);
                    }
                }
                else {
                    if (di.x == 0 && di.y == 0)
                        di.setDrawXY(100, 20 * idx);
                }
                return di;
            }
            dynJSON2List(lobj) {
                var id = lobj["id"];
                if (id == undefined || id == null || id == "")
                    return null;
                var lay = this.getLayer();
                var di = lay.getItemById(id);
                if (di == undefined || di == null) {
                    return null;
                }
                var dyn = lobj["_oc_dyn"];
                if (dyn != undefined && dyn != null) {
                    di.setDynData(dyn, false);
                }
                var jsitems = lobj["items"];
                var items = [];
                for (var joitem of jsitems) {
                    items.push(joitem);
                }
                //di.setListDyn(items);
                return di;
            }
            dynJSON2Member(lobj) {
                var id = lobj["id"];
                if (id == undefined || id == null || id == "")
                    return null;
                var lay = this.getLayer();
                var di = lay.getItemById(id);
                if (di == undefined || di == null) {
                    return null;
                }
                return di;
            }
            transMember2JSON(cur_ob, u) {
                if (!(u instanceof oc.DrawItem))
                    return;
                var ps = u.extract();
                for (var n in ps) {
                    cur_ob[n] = ps[n];
                }
            }
            transUnit2JSON(cur_ob, u) {
                if (!(u instanceof oc.DrawItem))
                    return;
                var ps = u.extract();
                for (var n in ps) {
                    cur_ob[n] = ps[n];
                }
                if (u instanceof iott.UnitTN) {
                    var subtns = u.getSubNode();
                    var subobs = [];
                    cur_ob[iott.UnitTN.UNIT_SUB] = subobs;
                    if (subtns != null) {
                        for (var subtn of subtns) {
                            var tmpo = {};
                            subobs.push(tmpo);
                            this.transUnit2JSON(tmpo, subtn);
                        }
                    }
                    var submems = u.getMembers();
                    var suboms = [];
                    cur_ob[iott.UnitTN.UNIT_MEMBERS] = suboms;
                    if (submems != null) {
                        for (var subm of submems) {
                            var tmpo = {};
                            suboms.push(tmpo);
                            this.transMember2JSON(tmpo, subm);
                        }
                    }
                }
                else if (u instanceof iott.DIDivList) {
                }
                else if (u instanceof iott.Member) {
                }
            }
        }
        iott.IOTTView = IOTTView;
    })(iott = oc.iott || (oc.iott = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var iott;
    (function (iott) {
        class Member extends oc.DrawItem {
            constructor(opts) {
                super(opts);
                this.b_show = false;
                this.w = 100;
                this.h = 100;
                this.iconImg = null;
                this.closeImg = null;
                this.headLen = 30;
                this.belongTo = null;
                //position state 0 -default in parent circle 1-moving 2 out parent circle
                this.posST = Member.PST_DEFAULT;
            }
            getPropDefs() {
                var r = super.getPropDefs();
                r.push(Member.Member_PNS);
                return r;
            }
            getClassName() {
                return "oc.iott.Member";
            }
            getDrawSize() {
                if (this.posST == Member.PST_OUT)
                    return this.getDrawOutSize();
                else
                    //return {w:this.w,h:this.h};
                    return { w: 100, h: 100 };
            }
            getBoundRectDraw() {
                var pt = this.getDrawXY();
                var ds = this.getDrawSize();
                return new oc.base.Rect(pt.x, pt.y, ds.w, ds.h);
            }
            getBelongTo() {
                return this.belongTo;
            }
            setBelongTo(b) {
                this.belongTo = b;
            }
            getPosState() {
                return this.posST;
            }
            setPosState(st) {
                this.posST = st;
                if (st == Member.PST_DEFAULT) { //re position
                    this.getMemDrawXY();
                }
            }
            on_mouse_event(tp, pxy, dxy, e) {
                super.on_mouse_event(tp, pxy, dxy, e);
                if (tp == oc.MOUSE_EVT_TP.DownLong) {
                    //console.log(this.getUnitName(),this.getTitle(),"down long") ;
                    var pbt = this.getBelongTo();
                    iott.UnitTN.curShowMemberUnit = pbt;
                    this.MODEL_fireChged(["member"]);
                }
                if (tp == oc.MOUSE_EVT_TP.Down) {
                    if (e.button == oc.MOUSE_BTN.RIGHT) {
                        //this.setPosState(Member.PST_DEFAULT) ;
                        //this.MODEL_fireChged(["member"]) ;
                    }
                    if (Member.PST_OUT == this.posST) {
                        var s = this.getDrawOutSize();
                        var r = new oc.base.Rect(this.x + s.w - this.headLen, this.y, this.headLen, this.headLen);
                        if (r.contains(dxy.x, dxy.y)) {
                            this.setPosState(Member.PST_DEFAULT);
                            this.MODEL_fireChged(["member"]);
                        }
                    }
                }
            }
            on_selected(b) {
                if (!b) {
                    var bt = this.getBelongTo();
                    if (bt != null && iott.UnitTN.curShowMemberUnit == bt) {
                        for (var m of bt.getMembers()) {
                            if (m.getSelState().selected)
                                return;
                        }
                        iott.UnitTN.curShowMemberUnit = null;
                        this.MODEL_fireChged(["member"]);
                    }
                }
            }
            getMeCenter() {
                var bt = this.getBelongTo();
                if (bt == null)
                    return null;
                var r = bt.getBoundRectDraw();
                if (r == null)
                    return null;
                var rad = bt.calcShowMemberRadius();
                if (rad == null)
                    return null;
                rad = rad * 2 / 3;
                var btmems = bt.getMembers();
                var num = btmems.length;
                var idx = btmems.indexOf(this);
                var cxy = r.getCenter();
                //cal me center xy
                return oc.util.DrawTransfer.calcRotatePt({ x: cxy.x + rad, y: cxy.y }, cxy, 360 * idx / num);
            }
            getMemDrawXY() {
                switch (this.posST) {
                    case Member.PST_MOVING:
                    case Member.PST_OUT:
                        return this.getDrawXY();
                    default:
                        var pt = this.getMeCenter();
                        if (pt == null)
                            return null;
                        this.x = pt.x - 50;
                        this.y = pt.y - 50;
                        return this.getDrawXY();
                        ;
                }
            }
            drawDefault(ctx, c) {
            }
            drawIcon(cxt, w, h) {
                if (this.iconImg != null) {
                    cxt.drawImage(this.iconImg, 0, 0, w, h);
                }
                else {
                    var imppath = this.getMemberIcon();
                    var ii = new Image();
                    ii.onload = () => {
                        this.iconImg = ii;
                        this.MODEL_fireChged([]);
                    };
                    ii.src = imppath;
                }
            }
            drawClose(cxt, x, y, w, h) {
                if (this.closeImg != null) {
                    cxt.drawImage(this.closeImg, x, y, w, h);
                }
                else {
                    var imppath = "/_iottree/res/tool_close1.gif";
                    var ii = new Image();
                    ii.onload = () => {
                        this.closeImg = ii;
                        this.MODEL_fireChged([]);
                    };
                    ii.src = imppath;
                }
            }
            drawOut(ctx, c) {
                var ds = this.getDrawOutSize();
                var w = c.transDrawLen2PixelLen(true, ds.w);
                var h = c.transDrawLen2PixelLen(false, ds.h);
                var dlen = c.transDrawLen2PixelLen(true, this.headLen);
                var pt = this.getMemDrawXY(); //this.getDrawXY();
                if (pt == null)
                    return;
                var p1 = c.transDrawPt2PixelPt(pt.x, pt.y);
                ctx.save();
                ctx.translate(p1.x, p1.y);
                this.drawClose(ctx, w - dlen, 0, dlen, dlen);
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
            drawIn(ctx, c) {
                var ds = this.getDrawSize();
                var w = c.transDrawLen2PixelLen(true, ds.w);
                var h = c.transDrawLen2PixelLen(false, ds.h);
                var pt = this.getMemDrawXY(); //this.getDrawXY();
                if (pt == null)
                    return;
                var p1 = c.transDrawPt2PixelPt(pt.x, pt.y);
                ctx.save();
                ctx.translate(p1.x, p1.y);
                this.drawIcon(ctx, w, h);
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
            draw(ctx, c) {
                var _a, _b, _c;
                switch (this.posST) {
                    case Member.PST_OUT:
                        this.drawOut(ctx, c);
                        //draw line
                        var mpt = this.getDrawXY();
                        var ppt = (_b = (_a = this.getBelongTo()) === null || _a === void 0 ? void 0 : _a.getBoundRectDraw()) === null || _b === void 0 ? void 0 : _b.getCenter();
                        if (mpt != null && ppt != null && this.getPosState() == Member.PST_OUT) {
                            ctx.save();
                            mpt = c.transDrawPt2PixelPt(mpt.x, mpt.y);
                            ppt = c.transDrawPt2PixelPt(ppt.x, ppt.y);
                            ctx.translate(0, 0);
                            ctx.strokeStyle = "grey";
                            ctx.beginPath();
                            ctx.setLineDash([3, 3]);
                            ctx.moveTo(mpt.x, mpt.y);
                            ctx.lineTo(ppt.x, ppt.y);
                            ctx.stroke();
                            ctx.restore();
                        }
                        break;
                    case Member.PST_MOVING:
                    default:
                        if (((_c = this.getBelongTo()) === null || _c === void 0 ? void 0 : _c.getShowMemberRadius()) != null)
                            this.drawIn(ctx, c);
                }
            }
            draw_hidden(cxt, c) {
            }
        }
        Member.PST_DEFAULT = 0;
        Member.PST_MOVING = 1;
        Member.PST_OUT = 2;
        // static Mmeber_PNS = {
        // 	_cat_name: "member", _cat_title: "Node Member",
        // 	rotate: { title: "Rotate", type: "float" },
        // };
        Member.Member_PNS = {
            _cat_name: "member", _cat_title: "Node Member",
            w: { title: "width", type: "int", readonly: true },
            h: { title: "height", type: "int", readonly: true },
            posST: { title: "Position State", type: "int", readonly: true }
        };
        iott.Member = Member;
        class MemberTagList extends Member //implements IActionNode
         {
            constructor(opts) {
                super(opts);
                this.out_w = 500;
                this.out_h = 300;
                this.divList = new iott.DIDivList({});
                this.divList.setMin(false);
            }
            getClassName() {
                return "oc.iott.MemberTagList";
            }
            getPropDefs() {
                var r = super.getPropDefs();
                r.push(MemberTagList.Member_TL_PNS);
                return r;
            }
            getActionTypeName() {
                return "tag_list";
            }
            getBorderColor() {
                return "pink";
            }
            getMemberTitle() {
                return "Tag List";
            }
            getMemberIcon() {
                return "/_iottree/res/icon_tag_list.png";
            }
            getDrawOutSize() {
                return { w: this.out_w, h: this.out_h };
            }
            getDivList() {
                if (this.divList.getLayer() == null) {
                    var c = this.getContainer();
                    if (c != null)
                        this.divList.setContainer(c, this.getLayer());
                }
                return this.divList;
            }
            on_mouse_event(tp, pxy, dxy, e) {
                super.on_mouse_event(tp, pxy, dxy, e);
                if (tp == oc.MOUSE_EVT_TP.Down) {
                    if (e.button == oc.MOUSE_BTN.RIGHT) {
                        if (oc.PopMenu.createShowPopMenu(this, pxy, dxy))
                            e.preventDefault();
                    }
                }
            }
            on_after_inject(pvs) {
                super.on_after_inject(pvs);
                this.divList.setDrawSize(this.out_w, this.out_h - this.headLen);
            }
            setPosState(st) {
                var _a, _b;
                super.setPosState(st);
                switch (this.posST) {
                    case Member.PST_OUT:
                        (_a = this.getLayer()) === null || _a === void 0 ? void 0 : _a.addItem(this.divList);
                        this.divList.setVisiable(true);
                        break;
                    case Member.PST_MOVING:
                    default:
                        (_b = this.getLayer()) === null || _b === void 0 ? void 0 : _b.removeItem(this.divList);
                        this.divList.setVisiable(false);
                }
            }
            drawIn(ctx, c) {
                this.divList.setVisiable(false);
                super.drawIn(ctx, c);
            }
            drawOut(ctx, c) {
                super.drawOut(ctx, c);
                this.divList.setVisiable(true);
                this.divList.x = this.x;
                this.divList.y = this.y + this.headLen;
                //his.divList.set
                this.divList.draw(ctx, c);
            }
        }
        MemberTagList.TP = "tag_list";
        MemberTagList.Member_TL_PNS = {
            _cat_name: "member_tl", _cat_title: "Member Tag List",
            out_w: { title: "out width", type: "int" },
            out_h: { title: "out height", type: "int" },
        };
        iott.MemberTagList = MemberTagList;
        class MemberConn extends Member {
            constructor() {
                super(...arguments);
                this.out_w = 300;
                this.out_h = 200;
            }
            getClassName() {
                return "oc.iott.MemberConn";
            }
            getBorderColor() {
                return "green";
            }
            getDrawOutSize() {
                return { w: this.out_w, h: this.out_h };
            }
            getMemberTitle() {
                var t = this.getTitle();
                if (t == null || t == '')
                    t = this.getName();
                return t;
            }
            getMemberIcon() {
                return "/_iottree/res/icon_conn.png";
            }
            //drawIn(ctx)
            drawOut(ctx, c) {
                super.drawOut(ctx, c); //this.drawIn(ctx,c) ;
                var p1 = c.transDrawPt2PixelPt(this.x, this.y);
                ctx.save();
                ctx.translate(p1.x, p1.y);
                ctx.font = `20px serif`;
                ctx.fillStyle = "yellow";
                var t = this.getTitle();
                if (t == null)
                    t = "xxxx";
                ctx.fillText(t, 10, 20);
                ctx.restore();
            }
        }
        MemberConn.TP = "ua_conn";
        iott.MemberConn = MemberConn;
    })(iott = oc.iott || (oc.iott = {}));
})(oc || (oc = {}));
/**
 * working unit
 * create by drawitems,which can has many name prop and can control dyn.
 */
var oc;
(function (oc) {
    var iott;
    (function (iott) {
        class Unit extends oc.DrawUnitIns // implements IPopMenuProvider
         {
            //menuBtn:
            constructor(opts) {
                super(opts);
                this.actionEle = null;
                this.menuEle = null;
                this.bShowAction = false;
                //this.title = "unit";
            }
            static getTempName2CN() {
                if (Unit.TEMPNAME2CN != null)
                    return Unit.TEMPNAME2CN;
                var v = {};
                v["rep"] = "oc.iott.UnitTNRep";
                v["ch"] = "oc.iott.UnitTNCh";
                v["dev"] = "oc.iott.UnitTNDev";
                v["conn"] = "oc.iott.UnitTNConn";
                v["hmi"] = "oc.iott.UnitTNHmi";
                v["store"] = "oc.iott.UnitTNStore";
                Unit.TEMPNAME2CN = v;
                return v;
            }
            static matchTempName2CN(tempn) {
                var r = Unit.getTempName2CN()[tempn];
                if (r == undefined || r == null || r == "")
                    return null;
                return r;
            }
            getClassName() {
                return "oc.iott.Unit";
            }
            getActionTypeName() {
                var u = this.getUnit();
                if (u == null)
                    return "";
                return "unit-" + u.getName();
            }
            // on_right_menu(op:string,pxy:oc.base.Pt,dxy:oc.base.Pt):boolean
            // {
            //     var u = this.getUnit()
            //     if(u==null)
            //         return true;
            //     var actitem = u.getUnitActItem(op);
            //     if(actitem==null)
            //         return true;
            //     actitem.action(this,op,pxy,dxy) ;//do action
            //     return false;
            // }
            on_mouse_event(tp, pxy, dxy, e) {
                if (tp == oc.MOUSE_EVT_TP.Clk) {
                    this.bShowAction = !this.bShowAction;
                    if (!this.bShowAction)
                        this.hideActionEle();
                    else
                        this.MODEL_fireChged([]);
                    return;
                }
                super.on_mouse_event(tp, pxy, dxy, e);
            }
            on_before_del() {
                return false;
            }
            hideActionEle() {
                if (this.actionEle == null)
                    return;
                var p = this.getPanel();
                if (p == null)
                    return;
                this.actionEle.remove();
                this.actionEle = null;
                this.bShowAction = false;
            }
            // protected getUnitExtVal(pn:string,defaultval:any):any
            // {
            //     var ext = this.getUnitExtProps();
            //     if(ext==null)
            //         return defaultval;
            //     var v = ext[pn];
            //     if(v==undefined||v==null)
            //         return defaultval;
            //     return v;
            // }
            showActionView() {
                var acttn = this.getActionTypeName();
                if (acttn == null || acttn == "")
                    return;
                var actitem = Unit.getActionItem(acttn);
                if (actitem == null)
                    return;
                var ajaxurl = actitem.ajax_url;
                if (ajaxurl == undefined || ajaxurl == null || ajaxurl == "")
                    return;
                var pm = {};
                pm["unit_id"] = this.getId();
                oc.util.doAjax(ajaxurl, pm, (bsucc, ret) => {
                    $("#c_" + this.getId()).html(ret);
                });
            }
            displayActionEle() {
                var acttn = this.getActionTypeName();
                if (acttn == null || acttn == "")
                    return;
                var actitem = Unit.getActionItem(acttn);
                if (actitem == null)
                    return;
                var p = this.getPanel();
                if (p == null)
                    return;
                var pxy = this.getPixelXY();
                var s = this.getPixelSize();
                if (pxy == null || s == null)
                    return;
                var act_w = actitem.width;
                var act_h = actitem.height;
                var act_pos = actitem.pos;
                var ele = p.getHTMLElement();
                if (this.actionEle == null) {
                    var tmpid = this.getId();
                    //     this.actionEle = $(`<div id="act_${tmpid}" class="oc_unit_action">
                    //     <div class="close">
                    //         <a href="javascript:oc.iott.Unit.refreshUnitAction('${tmpid}')"><i class="fa fa fa-refresh"></i></a>    
                    //         <a href="javascript:oc.iott.Unit.closeUnitAction('${tmpid}')"><i class="fa fa-times-circle"></i></a>
                    //     </div>
                    //     <div id="c_${tmpid}" class="content"></div>
                    // </div>`);
                    this.actionEle = $(`<div id="act_${tmpid}" class="oc_unit_action">
                <div id="c_${tmpid}" class="content"></div>
            </div>`);
                    this.actionEle.css("width", act_w + "px");
                    this.actionEle.css("height", act_h + "px");
                    this.actionEle.get(0)["_oc_unit"] = this;
                    $(ele).append(this.actionEle);
                    this.showActionView();
                }
                switch (act_pos) {
                    case 1: //right
                        this.actionEle.css("left", (pxy.x + s.w) + "px");
                        this.actionEle.css("top", pxy.y + "px");
                        break;
                    case 2: //left
                        this.actionEle.css("left", (pxy.x - act_w) + "px");
                        this.actionEle.css("top", pxy.y + "px");
                        break;
                    case 3: //top
                        this.actionEle.css("left", pxy.x + "px");
                        this.actionEle.css("top", (pxy.y - act_h) + "px");
                        break;
                    default: //0-bottom
                        this.actionEle.css("left", pxy.x + "px");
                        this.actionEle.css("top", (pxy.y + s.h) + "px");
                        break;
                }
            }
            draw(cxt, c) {
                super.draw(cxt, c);
                //console.log(this.bShowAction+" "+this.isSelected());
                if (this.bShowAction) {
                    this.displayActionEle();
                }
                // else
                // {
                //     this.hideActionEle();
                //     if(!this.isSelected())
                //         this.bShowAction=false;
                // }
            }
            /**
             * used by action div to close self
             * @param id
             */
            static closeUnitAction(id) {
                var ob = $("#act_" + id);
                if (ob == null || ob == undefined)
                    return;
                var u = ob.get(0)["_oc_unit"];
                if (u != undefined && u != null)
                    u.hideActionEle();
            }
            static refreshUnitAction(id) {
                var ob = $("#" + id);
                if (ob == null || ob == undefined)
                    return;
                var u = ob.get(0)["_oc_unit"];
                if (u != undefined && u != null)
                    u.showActionView();
            }
            static setActionTp2Item(tp2items) {
                Unit.tp2items = tp2items;
            }
            static getActionItem(tpname) {
                if (Unit.tp2items == null)
                    return null;
                var r = Unit.tp2items[tpname];
                if (r == undefined)
                    return null;
                return r;
            }
        }
        Unit.TEMPNAME2CN = null;
        Unit.tp2items = null;
        iott.Unit = Unit;
    })(iott = oc.iott || (oc.iott = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var iott;
    (function (iott) {
        /**
         * Unit TreeNode
         * suport display node which has child node.and show like a tree
         * parent can expand or collapse all child node .
         */
        class UnitTN extends iott.Unit {
            constructor(opts) {
                super(opts);
                this.parentTN = null;
                this.subTNs = [];
                this.expandTN = null;
                this.expandR = 10;
                this.expanded = true;
                this.members = [];
                this.showMemberRadius = 0;
            }
            getClassName() {
                return "oc.iott.UnitTN";
            }
            getPropDefs() {
                var r = super.getPropDefs();
                r.push(UnitTN.UNITTN_PNS);
                return r;
            }
            /**
             * override it to make not expanded sub nodes move together
             * @param x
             * @param y
             */
            setDrawXY(x, y) {
                var subns = this.listDescendantsNodesDI(false);
                if (subns.length > 0) {
                    var oldpt = this.getDrawXY();
                    var dx = x - oldpt.x;
                    var dy = y - oldpt.y;
                    for (var subn of subns) {
                        oldpt = subn.getDrawXY();
                        //subn.setDrawXY(oldpt.x+dx,oldpt.y+dy) ;
                        subn.x = oldpt.x + dx;
                        subn.y = oldpt.y + dy;
                    }
                }
                super.setDrawXY(x, y);
            }
            listDescendantsNodesDI(bexpanded) {
                var r = [];
                var subns = this.getSubNode();
                if (subns == null)
                    return r;
                for (var subn of subns) {
                    if (!(subn instanceof oc.DrawItem))
                        continue;
                    if (bexpanded == null || (bexpanded && !subn.isHidden()) || (!bexpanded && subn.isHidden())) {
                        r.push(subn);
                        if (subn instanceof UnitTN) {
                            var subr = subn.listDescendantsNodesDI(bexpanded);
                            for (var tmpn of subr)
                                r.push(tmpn);
                        }
                    }
                }
                return r;
            }
            hasSubNode() {
                return this.subTNs != null && this.subTNs.length > 0;
            }
            getSubNode() {
                return this.subTNs;
            }
            getParentTN() {
                return this.parentTN;
            }
            setParentTN(p) {
                this.parentTN = p;
            }
            setChildTNS(c) {
                c.setParentTN(this);
                this.subTNs.push(c);
            }
            getMembers() {
                return this.members;
            }
            setMember(m) {
                m.setBelongTo(this);
                this.members.push(m);
            }
            // public isShowMember():boolean
            // {
            //     return this.bShowMember;
            // }
            // public setShowMember(b:boolean)
            // {
            //     this.bShowMember = b ;
            // }
            isHidden() {
                if (this.parentTN == null)
                    return false;
                if (this.parentTN.isHidden())
                    return true;
                return !this.parentTN.expanded;
            }
            isSubExpanded() {
                return this.expanded;
            }
            /**
             *
             * @param pos 0-right 1-left
             */
            getPrimRect() {
                //this.getDrawSize();
                //var u = this.getUnit();
                var r = super.getPrimRect();
                if (r == null)
                    return null;
                //if (!this.hasChildTN())
                //    return r;
                var c = this.getContainer();
                if (c == null)
                    return r;
                if (this.hasSubNode())
                    r.w += c.transDrawLen2PixelLen(true, this.expandR * 2);
                return r;
            }
            getExpandItem() {
                var w = super.getW();
                var h = super.getH();
                if (this.expandTN != null) {
                    if (this.expanded)
                        this.expandTN.setTp("circle-");
                    else
                        this.expandTN.setTp("circle+");
                    this.expandTN.setDrawXY(this.x + w, this.y + (h / 2 - this.expandR));
                    return this.expandTN;
                }
                //var r = super.getBoundRectDraw();
                //if(r==null)
                //    return null ;
                var v = new oc.di.DIBasic({ tp: "circle+" });
                v.setLnW(2);
                v.setDrawSize(this.expandR * 2, this.expandR * 2);
                this.expandTN = v;
                if (this.expanded)
                    v.setTp("circle-");
                else
                    v.setTp("circle+");
                this.expandTN.setDrawXY(this.x + w, this.y + (h / 2 - this.expandR));
                return v;
            }
            getW() {
                var w = super.getW();
                if (this.hasSubNode())
                    return w + this.expandR * 2;
                else
                    return w;
            }
            on_mouse_event(tp, pxy, dxy, e) {
                super.on_mouse_event(tp, pxy, dxy, e);
                //chk if mouse clk on expand or not
                if (tp == oc.MOUSE_EVT_TP.Clk) {
                    var w = super.getW();
                    var y = this.getH() / 2 + this.y;
                    if (dxy.x > this.x + w && dxy.x < this.x + w + this.expandR * 2 && dxy.y > y - this.expandR && dxy.y < y + this.expandR) { //
                        //if(this.before_expand_chg())
                        this.expanded = !this.expanded;
                        this.MODEL_fireChged(["expanded"]);
                    }
                }
                else if (tp == oc.MOUSE_EVT_TP.DownLong) {
                    //console.log(this.getUnitName(),this.getTitle(),"down long") ;
                    UnitTN.curShowMemberUnit = this;
                    //this.setShowMember(true);
                    this.MODEL_fireChged(["member"]);
                }
            }
            on_selected(b) {
                if (!b) { //unselected
                    for (var m of this.getMembers()) {
                        if (m.getSelState().selected)
                            return;
                    }
                    UnitTN.curShowMemberUnit = null;
                    //this.setShowMember(false);
                }
            }
            /**
             * called by panel,e.g when mouse down
             * @param pxy
             * @param dxy
             */
            static chkAndHiddenMembers0(pxy, dxy) {
                var un = UnitTN.curShowMemberUnit;
                if (un == null)
                    return false;
                if (un.showMemberRadius <= 0) {
                    UnitTN.curShowMemberUnit = null;
                    return true;
                }
                var r = un.getBoundRectDraw();
                if (r == null)
                    return false;
                var cxy = r.getCenter();
                var dx = cxy.x - dxy.x;
                var dy = cxy.y - dxy.y;
                if (dx * dx + dy * dy < un.showMemberRadius * un.showMemberRadius)
                    return false; //in circle
                UnitTN.curShowMemberUnit = null;
                return true;
            }
            getShowMemberRadius() {
                if (UnitTN.curShowMemberUnit == null)
                    return null;
                if (UnitTN.curShowMemberUnit != this)
                    return null;
                return this.calcShowMemberRadius();
            }
            calcShowMemberRadius() {
                var r = this.getBoundRectDraw();
                if (r == null)
                    return null;
                //if(!this.isShowMember())
                //    return null;
                return (r.w > r.h ? r.w : r.h) * 2;
            }
            displayMembers(ctx, c) {
                var rad = this.getShowMemberRadius();
                if (rad == null)
                    return;
                var ms = this.getMembers();
                //if(ms==null||ms.length<=0)
                //    return ;
                var r = this.getBoundRectDraw();
                if (r == null)
                    return;
                this.showMemberRadius = rad;
                ctx.beginPath();
                ctx.strokeStyle = "rgba(100,100,100,0.7)";
                ctx.lineWidth = 2;
                var cxy = r.getCenter();
                cxy = c.transDrawPt2PixelPt(cxy.x, cxy.y);
                rad = c.transDrawLen2PixelLen(true, this.showMemberRadius);
                ctx.arc(cxy.x, cxy.y, rad, 0, Math.PI * 2);
                ctx.stroke();
                ctx.fillStyle = "rgba(100,100,100,0.7)";
                ctx.closePath();
                ctx.fill();
            }
            /**
             * override it ,to support expand display area when show members
             * and hide member circle when not be selected
             * @param x
             * @param y
             */
            containDrawPt(x, y) {
                var un = UnitTN.curShowMemberUnit;
                if (un == null || un != this)
                    return super.containDrawPt(x, y);
                var r = un.getBoundRectDraw();
                if (r == null || un.showMemberRadius <= 0)
                    return super.containDrawPt(x, y);
                var cxy = r.getCenter();
                var dx = cxy.x - x;
                var dy = cxy.y - y;
                if (dx * dx + dy * dy < un.showMemberRadius * un.showMemberRadius)
                    return true;
                return false;
            }
            draw(cxt, c) {
                //super.draw_sel()
                super.draw(cxt, c);
                if (this.hasSubNode()) {
                    var ei = this.getExpandItem();
                    if (ei != null)
                        ei.draw(cxt, c);
                }
                this.displayMembers(cxt, c);
            }
            before_expand_chg() {
                var du = this.getUnit();
                var lay = this.getLayer();
                if (du == null || lay == null)
                    return false;
                var expandu = du.getInsExpandUrl();
                if (expandu == null || expandu == '')
                    return false;
                var pm = {};
                pm["layer_name"] = lay.getName();
                pm["unit_name"] = du.getName();
                pm["uins_id"] = this.getId();
                $.ajax({
                    type: 'post',
                    url: expandu,
                    data: pm,
                    async: true,
                    success: (result) => {
                        result = result.trim();
                        if (result.indexOf("{") != 0) { //
                            oc.util.prompt_err(result);
                            return;
                        }
                        var ob;
                        eval("ob=" + result);
                        var insid = ob["unit_ins_id"];
                    }
                });
                return true;
            }
        }
        UnitTN.UNIT_SUB = "_oc_sub";
        UnitTN.UNIT_MEMBERS = "_oc_members";
        //private bShowMember:boolean=false;
        /**
         * only one unittn can show member
         */
        UnitTN.curShowMemberUnit = null;
        UnitTN.UNITTN_PNS = {
            _cat_name: "unit_tn", _cat_title: "IOTT Unit TreeNode",
            expanded: { title: "Expanded", type: "bool", enum_val: [[true, "Expand"], [false, "Collapse"]] },
        };
        iott.UnitTN = UnitTN;
    })(iott = oc.iott || (oc.iott = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var iott;
    (function (iott) {
        class UnitTNCh extends iott.UnitTN {
            constructor(opts) {
                super(opts);
            }
            getClassName() {
                return "oc.iott.UnitTNCh";
            }
            setDynData(dyn, bfirechg = true) {
                var brun = dyn["brun"];
                if (brun != undefined && brun != null) {
                    var ddata = {};
                    if (brun)
                        ddata["fillColor"] = "#a7ec21";
                    else
                        ddata["fillColor"] = "red";
                    var tmpi = this.getInnerDrawItemByName("runst");
                    if (tmpi != null) {
                        tmpi.setDynData(ddata, false);
                    }
                    return [];
                }
                return super.setDynData(dyn, bfirechg);
            }
        }
        iott.UnitTNCh = UnitTNCh;
    })(iott = oc.iott || (oc.iott = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var iott;
    (function (iott) {
        class UnitTNDev extends iott.UnitTN {
            constructor(opts) {
                super(opts);
            }
            getClassName() {
                return "oc.iott.UnitTNDev";
            }
            setDynData(dyn, bfirechg = true) {
                var brun = dyn["brun"];
                if (brun != undefined && brun != null) {
                    var ddata = {};
                    if (brun)
                        ddata["fillColor"] = "green";
                    else
                        ddata["fillColor"] = "red";
                    var tmpi = this.getInnerDrawItemByName("runst");
                    if (tmpi != null) {
                        tmpi.setDynData(ddata, false);
                    }
                    return [];
                }
                return super.setDynData(dyn, bfirechg);
            }
        }
        iott.UnitTNDev = UnitTNDev;
    })(iott = oc.iott || (oc.iott = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var iott;
    (function (iott) {
        class UnitTNHmi extends iott.UnitTN {
            constructor(opts) {
                super(opts);
            }
            getClassName() {
                return "oc.iott.UnitTNHmi";
            }
            draw(cxt, c) {
                super.draw(cxt, c);
            }
            getGroupName() {
                return "hmi";
            }
        }
        iott.UnitTNHmi = UnitTNHmi;
    })(iott = oc.iott || (oc.iott = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var iott;
    (function (iott) {
        class UnitTNRep extends iott.UnitTN {
            constructor(opts) {
                super(opts);
            }
            getClassName() {
                return "oc.iott.UnitTNRep";
            }
            setDynData(dyn, bfirechg = true) {
                var brun = dyn["brun"];
                if (brun != undefined && brun != null) {
                    var ddata = {};
                    if (brun)
                        ddata["fillColor"] = "#a7ec21";
                    else
                        ddata["fillColor"] = "red";
                    var tmpi = this.getInnerDrawItemByName("runst");
                    if (tmpi != null) {
                        tmpi.setDynData(ddata, false);
                    }
                    return [];
                }
                return super.setDynData(dyn, bfirechg);
            }
        }
        iott.UnitTNRep = UnitTNRep;
    })(iott = oc.iott || (oc.iott = {}));
})(oc || (oc = {}));
var oc;
(function (oc) {
    var iott;
    (function (iott) {
        class UnitTNStore extends iott.UnitTN {
            constructor(opts) {
                super(opts);
            }
            getClassName() {
                return "oc.iott.UnitTNStore";
            }
            getGroupName() {
                return "store";
            }
        }
        iott.UnitTNStore = UnitTNStore;
    })(iott = oc.iott || (oc.iott = {}));
})(oc || (oc = {}));
/**
 * win is container which can has many unit.it can limit units in is area.
 */
var oc;
(function (oc) {
    var iott;
    (function (iott) {
        class Win extends oc.DrawItemGroup // implements IActionNode
         {
            //menuBtn:
            constructor(opts) {
                super(opts);
                this.actions = [];
                this.actionEle = null;
                this.actionEleId = null;
                this.actEleInnerX = 0;
                this.actEleInnerY = 0;
                this.title = "win";
            }
            getClassName() {
                return "oc.iott.Win";
            }
            getActionTypeName() {
                var n = this.getName();
                if (n == null || n == "")
                    return "win-";
                return "win-" + n;
            }
            getActionElement() {
                if (this.actionEle != null)
                    return this.actionEle;
                if (this.actionEleId == null || this.actionEleId == "")
                    return null;
                this.actionEle = document.getElementById(this.actionEleId);
                if (this.actionEle == null)
                    return null;
                $(this.actionEle).css("position", "absulate");
                return this.actionEle;
            }
            getPropDefs() {
                var r = super.getPropDefs();
                r.push(Win.WIN_PNSG);
                return r;
            }
            on_mouse_in() {
                this.MODEL_fireChged([]);
            }
            on_mouse_out() {
                if (this.actionEle == null)
                    return;
                //console.log("win mouse on_mouse_in");
                var p = this.getPixelXY();
                $(this.actionEle).css("display", "none");
            }
            displayActionEle() {
                var actele = this.getActionElement();
                if (actele == null)
                    return;
                //console.log("win mouse on_mouse_in");
                var p = this.getDrawXY();
                if (p == null)
                    return;
                var c = this.getContainer();
                if (c == null)
                    return;
                var div = $(actele);
                var innerp = c.transDrawPt2PixelPt(p.x + this.actEleInnerX, p.y + this.actEleInnerY);
                div.css("display", "");
                div.css("left", innerp.x + "px");
                div.css("top", innerp.y + "px");
            }
            draw(cxt, c) {
                super.draw(cxt, c);
                if (this.isMouseIn()) {
                    var pt = this.getDrawXY();
                    var dxy = c.transDrawPt2PixelPt(pt.x, pt.y);
                    for (var i = 0; i < this.actions.length; i++) {
                        var di = this.actions[i];
                        di.setDrawXY(pt.x + 100 + i * 70, pt.y + 30);
                        di.setDrawSize(50, 50);
                        di.draw(cxt, c);
                    }
                    //cxt.fillRect(dxy.x+100,dxy.y+30,50,50);
                    //util.drawRect(cxt,dxy.x+100,dxy.y+30,50,50,2,"pink",1,null);
                    this.displayActionEle();
                }
            }
        }
        Win.WIN_PNSG = {
            _cat_name: "win", _cat_title: "IOTT Win",
            actionEleId: { title: "Action Ele Id", type: "string" },
            actEleInnerX: { title: "Inner X", type: "int" },
            actEleInnerY: { title: "Inner Y", type: "int" },
        };
        iott.Win = Win;
    })(iott = oc.iott || (oc.iott = {}));
})(oc || (oc = {}));
//# sourceMappingURL=oc.js.map