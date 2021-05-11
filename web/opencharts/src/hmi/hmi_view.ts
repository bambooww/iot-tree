/// <reference path="../draw_mvc.ts" />

namespace oc.hmi
{
    export class HMILayer extends DrawLayer implements IActionNode
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
    export class HMIView extends DrawView implements HMIModelListener
    {
        hmiModel:HMIModel;

        private contId:string="" ;
        private contName:string="" ;
        private contTitle:string="" ;
        private contUnitName:string="";

        drawPanel: DrawPanel;
        drawEditor: DrawEditor|null;
        drawLayer: HMILayer;

        drawInter: DrawInteract;
        options: any = {};

        websock:WebSocket|null=null;

        private bLoadFirst:boolean = true ;

        public constructor(m:HMIModel,dp: DrawPanel, de: DrawEditor|null, opts: any) //repid:string,hmiid:string,
        {
            super(m,dp) ; //repid,hmiid,

            this.hmiModel = m;

            this.drawPanel = dp;
            this.drawEditor = de;

            this.drawPanel.init_panel();
            this.drawPanel.on_draw();
            if(this.drawEditor!=null)
                this.drawEditor.init_editor();

            this.drawLayer = new HMILayer({});
            this.drawPanel.addLayer(this.drawLayer);

            if(opts&&opts.show_only)
                this.drawInter = new HMIInteractShow(this.drawPanel,this.drawLayer,{show_only:true}) ;
            else
                this.drawInter = new HMIInteractEdit(this.drawPanel, this.drawLayer, { copy_paste_url: opts.copy_paste_url });

            this.drawPanel.setInteract(this.drawInter);
            this.options = opts;

            this.hmiModel.registerListener(this);
        }
        
        public init()
        {
            this.hmiModel.initModel();
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

        
        public setWebSocket(ws:WebSocket)
        {
            this.websock = ws ;
        }

        protected sendMsgToServer(msg:string):void
        {
            if(this.websock==null)
                return ;
            this.websock.send(msg) ;
        }

        on_model_loaded(temp: {}): void
        {
            if(temp==null||temp=="")
                return ;
                
            if (typeof (temp) == 'string')
                eval("temp=" + temp);
            this.drawLayer.inject(temp,undefined);
        }
        
        on_model_dyn_updated(dyn: {}): void
        {
            throw new Error("Method not implemented.");
        }

        on_model_propbind_data(data:PROPBINDS):void
        {
            var lay = this.getLayer() ;
            if(lay==null)
                return ;
            var bds = data.binds ;
            for(var bd of bds)
            {
                var di = lay.getItemById(bd.id) ;
                if(di==null)
                    continue ;
                if(bd.items&&bd.items.length<=0)
                    continue;
                let r:base.Props<any> = {} ;
                var has = false;
                for(var item of bd.items)
                {
                    if(!item.valid)
                        continue ;
                    r[item.name]=item.v ;
                    has = true;
                }
                if(has)
                    di.inject(r,true) ;
            }
        }
        
    }
}