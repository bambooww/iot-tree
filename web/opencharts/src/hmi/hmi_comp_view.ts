module oc.hmi
{
    export class HMICompLayer extends DrawLayer implements IActionNode
    {
        //unitName:string;
        private menuEle: JQuery<HTMLElement> | null = null;

        private hmiComp:HMIComp|null=null ;

        private compInter:CompInter=new CompInter() ;


        public constructor(opts:{}|string|undefined)
		{
            super(opts);
            //this.unitName = "" ;
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

        getActionTypeName(): string
        {
            return "layer" ;
        }

        public getCompInter()
        {
            return this.compInter ;
        }


        public extract(mark:string|null|undefined): {}
		{
            var r = super.extract(mark);
            
            r[HMIComp.PN_INTER]=this.compInter.extractInter();
            return r;//JSON.stringify(r) ;
        }

        public inject(opts: {} | string,mark:string|null|undefined)
		{
            if(typeof(opts)=="string")
                eval("opts="+opts);
            super.inject(opts,mark) ;

            //
            var cis = opts[HMIComp.PN_INTER];
            if(cis)
                this.compInter.injectInter(cis);
        }


        
    }
    

    export class HMICompView implements HMIModelListener
    {
        model:HMICompModel;

        private contId:string="" ;
        private contName:string="" ;
        private contTitle:string="" ;
        private contUnitName:string="";

        drawPanel: HMICompPanel;
        drawEditor: DrawEditor|null;
        drawLayer: HMICompLayer;

        drawInter: DrawInteract;
        options: any = {};

        private loadedCB:()=>void|null;
        private bLoadFirst:boolean = true ;

        public constructor(m:HMICompModel,dp: HMICompPanel, de: DrawEditor|null, opts: any)
        {
            if(opts==undefined||opts==null)
                opts={} ;
            this.model = m;

            this.drawPanel = dp;
            this.drawEditor = de;

            this.drawPanel.init_panel();
            this.drawPanel.on_draw();
            if(this.drawEditor!=null)
                this.drawEditor.init_editor();

            this.drawLayer = new HMICompLayer({});
            this.drawPanel.addLayer(this.drawLayer);

            if(this.drawEditor!=null)
                this.drawInter = new HMIInteractEdit(this.drawPanel, this.drawLayer, { copy_paste_url: opts.copy_paste_url });
            else
                this.drawInter = new DrawInteractShow(this.drawPanel, this.drawLayer, { copy_paste_url: opts.copy_paste_url });
            this.drawPanel.setInteract(this.drawInter);
            this.options = opts;
            this.loadedCB = opts.loaded_cb||null;

            this.model.registerListener(this);
        }
        
        
        public init()
        {
            this.model.initModel();
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

        on_model_loaded(v: {}): void
        {
            if(v==null||v=="")
                return ;
                
            if (typeof (v) == 'string')
                eval("v=" + v);
            this.drawLayer.inject(v,undefined);
            if(this.loadedCB!=null)
                this.loadedCB() ;
        }
        
        on_model_dyn_updated(dyn: {}): void
        {
            throw new Error("Method not implemented.");
        }

        on_model_propbind_data(data: PROPBINDS): void
        {
            throw new Error("Method not implemented.");
        }
        
    }

    /**
     * only for tester show
     */
    export class HMICompViewShow
    {
        drawPanel: HMICompPanel;
        
        editLayer: HMICompLayer;

        showLayer:DrawLayer ;

        drawInter: DrawInteract;
        options: any = {};

        compIns:HMICompIns ;
        comp:HMIComp ;

        public constructor(editlayer:HMICompLayer,dp: HMICompPanel, opts: any)
        {
            if(opts==undefined||opts==null)
                opts={} ;
            
            this.editLayer = editlayer;
            this.comp = new HMIComp({}) ;
            var compob = editlayer.extract(null) ;
            this.comp.inject(compob,false) ;

            this.drawPanel = dp;
            
            this.drawPanel.init_panel();
            this.drawPanel.on_draw();
            
            this.showLayer = new HMICompLayer({});
            this.drawPanel.addLayer(this.showLayer);
            this.compIns = new HMICompIns({}) ;
            this.compIns.onCompSet(this.comp) ;
            this.showLayer.addItem(this.compIns) ;

            this.drawInter = new DrawInteractShow(this.drawPanel, this.showLayer, { copy_paste_url: opts.copy_paste_url });
            this.drawPanel.setInteract(this.drawInter);
            this.options = opts;

            this.showLayer.ajustDrawFit();
        }

        public getComp():HMIComp
        {
            return this.comp;
        }

        public getCompIns():HMICompIns
        {
            return this.compIns;
        }
    }
}