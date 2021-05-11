module oc.iott
{
    export class IOTTLayer extends DrawLayer implements IActionNode
    {
        unitName:string;
        private menuEle: JQuery<HTMLElement> | null = null;

        public constructor(opts:{}|string|undefined)
		{
            super(opts);
            this.unitName = "" ;
        }
        

        public on_mouse_event(tp: MOUSE_EVT_TP, pxy: oc.base.Pt, dxy: oc.base.Pt,e:_MouseEvent)
        {
            if(tp==MOUSE_EVT_TP.Down)
            {//display rep right menu
                if(e.button==MOUSE_BTN.RIGHT)
                {//right
                    if(PopMenu.createShowPopMenu(this,pxy,dxy))
                        e.preventDefault();
                }
            }
        }

        public setUnitName(u:string)
        {
            this.unitName = u ;
        }

        getActionTypeName(): string
        {
            return "layer" ;
        }
    }
    /**
     * using template and unit(unittn) to support load iott graphics data
     * 1)Panel load a template which has some container(group). e.g conn ui
     * 2)panel load from app ajax ,and make unit nodes show etc.
     * 3)support some edit action like add,modify,delete etc
     */
    export class IOTTView extends DrawView implements IOTTModelListener
    {
        iottModel:IOTTModel;

        private contId:string="" ;
        private contName:string="" ;
        private contTitle:string="" ;
        private contUnitName:string="";

        drawPanel: DrawPanel;
        drawEditor: DrawEditor;
        drawLayer: IOTTLayer;

        drawInter: InteractEditRep;
        options: any = {};

        private bLoadFirst:boolean = true ;

        private repDI:UnitTN|null = null ;

        public constructor(m:IOTTModel,dp: DrawPanel, de: DrawEditor, opts: any) //repid:string,hmiid:string,
        {
            super(m,dp) ;

            this.iottModel = m;

            this.drawPanel = dp;
            
            this.drawPanel.init_panel();
            this.drawPanel.on_draw();

            this.drawEditor = de;
            if(this.drawEditor!=null)
                this.drawEditor.init_editor();

            this.drawLayer = new IOTTLayer({});
            this.drawPanel.addLayer(this.drawLayer);

            this.drawInter = new oc.iott.InteractEditRep(this.drawPanel, this.drawLayer, { copy_paste_url: opts.copy_paste_url });
            this.drawPanel.setInteract(this.drawInter);
            this.options = opts;

            this.iottModel.registerListener(this);
        }

        public init()
        {
            this.iottModel.initModel();
        }

        public getPanel():DrawPanel
        {
            return this.drawPanel;
        }

        public getLayer():DrawLayer
        {
            return this.drawLayer;
        }

        public getInteract():DrawInteract
        {
            return this.drawInter;
        }

        protected sendMsgToServer(msg:string):void
        {

        }

        on_model_temp_loaded(temp: {}): void
        {
            if(temp==null||temp=="")
                return ;
                
            if (typeof (temp) == 'string')
                eval("temp=" + temp);
            this.drawLayer.inject(temp,undefined);
        }
        on_model_unit_loaded(ret: {}): void
        {
            if(typeof(ret)=="string")
            {
                if (ret.indexOf("[") != 0)
                {//
                    oc.util.prompt_err("load unit def err:" + ret);
                    return;
                }
            }
            var ob: any = null;
            if(typeof(ret)=="string")
                eval("ob=" + ret);
            else
                ob = ret;
            for (var item of ob)
            {
                oc.DrawUnit.addUnitByJSON(item);
            }
        }
        on_model_cont_chged(cont: {}): void
        {
            if (typeof (cont) == 'string')
                eval("cont=" + cont);
            
            this.injectContJSON(cont);
            this.drawInter.clearSelectedItem();
            //this.drawLayer.inject(cont,"u");
            if(this.bLoadFirst)
            {
                this.bLoadFirst=false;
                this.drawLayer.ajustDrawFit();
            }
        }
        on_model_dyn_updated(dyn: {}): void
        {
            if (typeof (dyn) == 'string')
                eval("dyn=" + dyn);
            this.injectDynJSON(dyn);
            this.drawLayer.update_draw();
        }

        private injectDynJSON(dyn:{})
        {
            var octp = dyn["_oc_tp"];
            if(octp=="unit")
                this.dynJSON2Unit(dyn);
            //else if(octp=="list")
            //    this.dynJSON2List(dyn);
        }

        private injectContJSON(cont:{})
        {
            this.contId = cont["id"] ;
            this.contName=cont["name"];
            this.contTitle=cont["title"];
            if(oc.util.chkNotEmpty(this.contId))
                this.drawLayer["id"]=this.contId ;
            if(oc.util.chkNotEmpty(this.contName))
                this.drawLayer["name"]=this.contName ;
            if(oc.util.chkNotEmpty(this.contTitle))
                this.drawLayer["title"]=this.contTitle ;
            //this.contUnitName=cont["unitName"];
            //if(this.contUnitName!=undefined&&this.contUnitName!=null)
            //    this.drawLayer.setUnitName(this.contUnitName);

            var not_del_ids:string[]=[];
            var lay = this.getLayer() ;
            this.repDI = IOTTView.transJSON2Unit(lay,null,cont,0,not_del_ids) as UnitTN;

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
            for(var u of this.findUnitsAll())
            {
                if(not_del_ids.indexOf(u.getId())<0)
                {
                    var conns = this.findConnsByUnit(u);
                    u.removeFromContainer();
                    for(var tmpc of conns)
                        tmpc.removeFromContainer() ;
                }
            }
        }

        private findUnitsAll():Unit[]
        {
            var lay = this.getLayer();
            var r:Unit[]=[];
            for(var di of lay.getItemsAll())
            {
                if(di instanceof Unit)
                    r.push(di) ;
            }
            return r ;
        }

        private findConnsByUnit(u:Unit):Conn[]
        {
            var lay = this.getLayer();
            var r:Conn[]=[];
            for(var di of lay.getItemsAll())
            {
                if(di instanceof Conn)
                {
                    if(di.getNodeFromId()==u.getId())
                        r.push(di);
                    else if(di.getNodeToId()==u.getId())
                        r.push(di) ;
                }
            }
            return r;
        }

        public extractContJSON():oc.base.Props<any>
        {
            var r:oc.base.Props<any>={} ;
            if(this.repDI!=null)
                this.transUnit2JSON(r,this.repDI) ;
            
            r["id"]=this.contId ;
            r["name"]=this.contName;
            r["title"]=this.contTitle;

            return r ;
        }

        /**
         * 
         * @param lay outer provided it,e.g for outer panel preview
         * @param cont 
         */
        public static injectLayerByCont(lay:DrawLayer,cont:{})
        {
            var not_del_ids:string[]=[];
            //var lay = this.getLayer() ;
            IOTTView.transJSON2Unit(lay,null,cont,0,not_del_ids);// as UnitTN;
        }

        private static transJSON2Unit(lay:DrawLayer,parent_u:Unit|null,uobj:{},idx:number,not_del_ids:string[]):Unit|null
        {
            var id = uobj["id"];
            if(id==undefined||id==null||id=="")
                return null;
            var un = uobj["unitName"];
            if(un==undefined||un==null||un=="")
                return null ;
            var du = DrawUnit.getUnitByName(un);
            if(du==null)
            {
                return null;
            }
            //var lay = this.getLayer();
            var du_sz =du.getUnitDrawSize();
            var du_g=du.getInsGroup();
            var new_cn = Unit.matchTempName2CN(du.getName());
            if(new_cn==null||new_cn=="")
                new_cn = du.getInsNewCN();
			if(new_cn==null||new_cn=="")
                return null;
            
            var di:Unit|undefined|null = lay.getItemById(id) as Unit;
            if(di==undefined||di==null)
            {
                di = eval(`new ${new_cn}({})`) as Unit;
                if(di==undefined)
                    return null;
                di.setMark("unit");//
                lay.addItem(di);
            }
			
            di.inject(uobj,false);
            di.setUnitName(un);
            di.setGroupName(du_g);

            di.setDrawSize(du_sz.w,du_sz.h);

            //this id cannot be del
            not_del_ids.push(di.getId());
            
            if(parent_u!=null)
            {
                var pxy = parent_u.getDrawXY();
                if(di.x==0&&di.y==0)
                    di.setDrawXY(pxy.x+du_sz.w*2,pxy.y+du_sz.h*2*idx);
                if(parent_u instanceof UnitTN)
                {
                    var tmpu = di as UnitTN;
                    parent_u.setChildTNS(tmpu);
                    var conn = new Conn({nodeid_from:parent_u.getId(),nodeid_to:tmpu.getId()});
                    lay.addItem(conn);
                }
            }
            else
            {
                if(di.x==0&&di.y==0)
                    di.setDrawXY(du_sz.w*2,du_sz.h*2*idx);
            }

            var subs = uobj[UnitTN.UNIT_SUB];
            if(subs!=undefined&&subs!=null)
            {
                var c = 0 ;
                for(var sub of subs)
                {
                    var octp = sub["_oc_tp"];
                    if(octp=="unit")
                        this.transJSON2Unit(lay,di,sub,c,not_del_ids);
                    else if(octp=="list")
                        this.transJSON2List(lay,di,sub,c)
                    else if(octp=="member")
                        this.transJSON2Member(lay,di,sub,c);
                    c ++ ;
                }
            }

            //di.setDrawXY(dxy.x,dxy.y) ;
            return di ;
        }


        private dynJSON2Unit(uobj:{}):Unit|null
        {
            var id = uobj["id"];
            if(id==undefined||id==null||id=="")
                return null;
            
            var lay = this.getLayer();
            var di:Unit|undefined|null = lay.getItemById(id) as Unit;
            if(di==undefined||di==null)
            {
                return null;
            }
			var dyn = uobj["_oc_dyn"] ;
            if(dyn!=undefined&&dyn!=null)
            {
                di.setDynData(dyn,false);
            }
            
            var subs = uobj[UnitTN.UNIT_SUB];
            if(subs!=undefined&&subs!=null)
            {
                var c = 0 ;
                for(var sub of subs)
                {
                    var octp = sub["_oc_tp"];
                    if(octp=="unit")
                        this.dynJSON2Unit(sub);
                    else if(octp=="member")
                    {
                        this.dynJSON2Member(sub) ;
                    }
                    else if(octp=="list")
                        this.dynJSON2List(sub);
                    
                    c ++ ;
                }
            }

            //di.setDrawXY(dxy.x,dxy.y) ;
            return di ;
        }

        private static transJSON2List(lay:DrawLayer,parent_u:Unit|null,lobj:{},idx:number):DIDivList|null
        {
            var id = lobj["id"];
            if(id==undefined||id==null||id=="")
                return null;
            
            //var lay = this.getLayer();
            var di:DIDivList|undefined|null = lay.getItemById(id) as DIDivList;
            if(di==undefined||di==null)
            {
                di = new DIDivList(undefined);
                di.setMark("unit");//
                lay.addItem(di);
                di.inject(lobj,false);
            }
            else
            {
                var olddxy = di.getDrawXY();
                var oldds = di.getDrawSize() ;
                di.inject(lobj,false);
                di.setDrawXY(olddxy.x,olddxy.y) ;
                di.setDrawSize(oldds.w,oldds.h);
            }
            
            var head = lobj["head"] as ListHead;
            
            if(head)
            {
                var jsitems = lobj["items"];
                var items:ListItem[]=[];
                for(var joitem of jsitems)
                {
                    var item = new ListItem(di) ;
                    for(var n in joitem)
                        item[n] = joitem[n] ;
                    if(item.isValid())
                        items.push(item) ;
                }

                di.setListTable(head,items);
            }
            
            //di.setDrawSize(100,100);

            if(parent_u!=null)
            {
                var pxy = parent_u.getDrawXY();
                if(di.x==0&&di.y==0)
                    di.setDrawXY(pxy.x+parent_u.getW()*2,pxy.y);
                if(parent_u instanceof UnitTN)
                {
                    parent_u.setChildTNS(di);
                    //var conn = new Conn({nodeid_from:parent_u.getId(),nodeid_to:di.getId()});
                    //conn.setToPos(CONN_POS.WN);
                    //lay.addItem(conn);
                }
            }
            else
            {
                if(di.x==0&&di.y==0)
                    di.setDrawXY(100,20*idx);
            }
            return di ;
        }

        private static transJSON2Member(lay:DrawLayer,parent_u:Unit|null,lobj:{},idx:number):Member|null
        {
            var id = lobj["id"];
            var tp = lobj["member_tp"];
            if(id==undefined||id==null||id==""||tp==undefined||tp==null||tp=="")
                return null;
            
            //var lay = this.getLayer();
            var di:Member|undefined|null = lay.getItemById(id) as Member;
            if(di==undefined||di==null)
            {
                switch(tp)
                {
                    case MemberTagList.TP:
                        var mtl = new MemberTagList(undefined);
                        lay.addItem(mtl);
                        mtl.inject(lobj,false);

                        var head = lobj["head"] as ListHead;
                        if(head)
                        {
                            var jsitems = lobj["items"];
                            var items:ListItem[]=[];
                            for(var joitem of jsitems)
                            {
                                var item = new ListItem(di) ;
                                for(var n in joitem)
                                    item[n] = joitem[n] ;
                                if(item.isValid())
                                    items.push(item) ;
                            }
                            mtl.getDivList().setListTable(head,items);
                        }
                        di = mtl ;
                        break ;
                    case MemberConn.TP:
                        di = new MemberConn(undefined);
                        lay.addItem(di);
                        di.inject(lobj,false);
                        break ;
                    default:
                        return null ;
                }
                
                
            }
            else
            {
                var olddxy = di.getDrawXY();
                 di.inject(lobj,false);
                di.setDrawXY(olddxy.x,olddxy.y) ;
            }
            
            //di.setDrawSize(100,100);

            if(parent_u!=null)
            {
                var pxy = parent_u.getDrawXY();
                if(di.x==0&&di.y==0)
                    di.setDrawXY(pxy.x+parent_u.getW()*2,pxy.y);
                if(parent_u instanceof UnitTN)
                {
                    parent_u.setMember(di) ;
                }
            }
            else
            {
                if(di.x==0&&di.y==0)
                    di.setDrawXY(100,20*idx);
            }
            return di ;
        }

        private dynJSON2List(lobj:{}):DIDivList|null
        {
            var id = lobj["id"];
            if(id==undefined||id==null||id=="")
                return null;
            
            var lay = this.getLayer();
            var di:DIDivList|undefined|null = lay.getItemById(id) as DIDivList;
            if(di==undefined||di==null)
            {
                return null ;
            }
            var dyn = lobj["_oc_dyn"] ;
            if(dyn!=undefined&&dyn!=null)
            {
                di.setDynData(dyn,false);
            }
            
            var jsitems = lobj["items"];
            var items:ListItemDyn[]=[];
            for(var joitem of jsitems)
            {
                items.push(joitem) ;
            }

            //di.setListDyn(items);
            return di ;
        }

        private dynJSON2Member(lobj:{}):Member|null
        {
            var id = lobj["id"];
            if(id==undefined||id==null||id=="")
                return null;
            
            var lay = this.getLayer();
            var di:Member|undefined|null = lay.getItemById(id) as Member;
            if(di==undefined||di==null)
            {
                return null ;
            }

            return di ;
        }

        private transMember2JSON(cur_ob:{},u:Member)
        {
            if(!(u instanceof DrawItem))
                return ;
            var ps = u.extract() ;
            for(var n in ps)
            {
                cur_ob[n]=ps[n];
            }

        }

        private transUnit2JSON(cur_ob:{},u:ITreeNode)
        {
            if(!(u instanceof DrawItem))
                return ;
            var ps = u.extract() ;
            for(var n in ps)
            {
                cur_ob[n]=ps[n];
            }

            if(u instanceof UnitTN)
            {
                var subtns = u.getSubNode();
                var subobs:oc.base.Props<any>[]=[];
                cur_ob[UnitTN.UNIT_SUB]=subobs;
                if(subtns!=null)
                {
                    for(var subtn of subtns)
                    {
                        var tmpo={}
                        subobs.push(tmpo);
                        this.transUnit2JSON(tmpo,subtn)
                    }
                }

                var submems = u.getMembers() ;
                var suboms:oc.base.Props<any>[]=[];
                cur_ob[UnitTN.UNIT_MEMBERS]=suboms;
                if(submems!=null)
                {
                    for(var subm of submems)
                    {
                        var tmpo={}
                        suboms.push(tmpo);
                        this.transMember2JSON(tmpo,subm);
                        
                    }
                }
            }
            else if(u instanceof DIDivList)
            {

            }
            else if(u instanceof Member)
            {

            }
        }
    }
}