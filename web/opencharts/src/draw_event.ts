
module oc
{
    export enum MOUSE_BTN {LEFT=0,RIGHT=2};

    export enum MOUSE_EVT_TP { Down, Move, Up, Clk, DbClk, Wheel,DragOver,DragLeave,Drop,DownLong };
    
    export var MOUSE_EVT_TP_NUM = 10 ;

    export enum KEY_EVT_TP { Down, Up, Press };
    
    //export enum KeyEvtType0 { Mouse, Keyboard }

    //export enum MouseEvtType { Mouse, Keyboard }

    export interface Event { timestamp: number; }
    export interface MouseEvent extends Event { x: number; y: number }
    export interface KeyEvent extends Event { keyCode: number }

    /**
     * 
     * @param tp 
     */
    export function getMouseEventNameByTp(tp:MOUSE_EVT_TP):string
    {
        switch(tp)
        {
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
        return "" ;
    }

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

    export class EventBinder
    {
        static EVENT_BINDER = "_event_binder" ;
        //bServerJS:boolean = false;

        evtName:string="" ;

        clientJS:string="" ;

        serverJS:string="" ;
        // public isServerJS()
        // {
        //     return this.bServerJS ;
        // }

        private mouseF:Function|null = null ;

        private tickF:Function|null = null ;

        public getEventName():string
        {
            return this.evtName ;
        }

        public getClientJS():string
        {
            return this.clientJS ;
        }

        
        public setClientJS(js:string)
        {
            this.clientJS = js ;

            this.mouseF= null ;
            this.tickF = null ;
        }

        public hasClientJS():boolean
        {
            return this.clientJS!=null&&this.clientJS!="" ;
        }


        public getServerJS():string
        {
            return this.serverJS ;
        }

        public setServerJS(js:string)
        {
            this.serverJS = js ;
        }

        public hasServerJS():boolean
        {
            return this.serverJS!=null&&this.serverJS!="" ;
        }

        public isValid():boolean
        {
            try
            {
                var sum = new Function("$_this",this.clientJS);
                //sum() ;
                return true;
            }
            catch(e)
            {
                return false;
            }
        }

        public onEventRunMouse(di:DrawItem,pxy:oc.base.Pt,dxy:oc.base.Pt,e:_MouseEvent):boolean
        {
            var pdi = di.getParentNode() ;
            if(this.mouseF==null)
            {
                try
                {
                    this.mouseF = new Function("$_parent","$_this","pxy","dxy","e",this.clientJS) ;
                }
                catch(e)
                {
                    return false;
                }
            }

            try
            {
                console.log(di.getClassName());
                //client run
                this.mouseF(pdi,di,pxy,dxy,e);
                //
                if(this.serverJS)
                {//send event to server,server run js in node context
                    var v = di.getPanel()?.getDrawView() ;
                    if(v!=null&&v!=undefined)
                        v.fireEventToServer(di.getId(),this.evtName,{pxy:pxy,dxy:dxy}) ;
                }
                return true;
            }
            catch(E)
            {
                console.warn(E) ;
                return false;
            }
        }

        public onEventRunTick(di:DrawItem)
        {

        }

        public onEventRunInter(di:DrawItem)
        {
            var pdi = di.getParentNode() ;
            if(this.mouseF==null)
            {
                try
                {
                    this.mouseF = new Function("$_parent","$_this",this.clientJS) ;
                }
                catch(e)
                {
                    return false;
                }
            }

            try
            {
                console.log(di.getClassName());
                this.mouseF(pdi,di);
                return true;
            }
            catch(E)
            {
                console.warn(E) ;
                return false;
            }
        }

        public toPropStr():oc.base.Props<string>
        {
            return {n:this.evtName,clientjs:this.clientJS,serverjs:this.serverJS} ;
        }

        public fromPropStr(p:oc.base.Props<string>):boolean
        {
            var n = p["n"] ;
            var cjs = p["clientjs"];
            var sjs = p["serverjs"];
            if(n==undefined||n==null)//||js==undefined||js==null)
                return false;
            this.evtName=  n ;
            this.clientJS = cjs ;
            this.serverJS = sjs ;
            return true ;
        }
    }

    export class EventBinderMapper
    {
        eventNames:string[]=[];

        eventBD:oc.base.Props<EventBinder>={};

        public constructor()
        {

        }

        public listEventNames():string[]
        {
            return this.eventNames ;
        }

        public getEventBinder(eventn:string):EventBinder|null
        {
             var r = this.eventBD[eventn];
             if(r==null||r==undefined)
                return null ;
            return r ;
        }

        public setEventBinder(eventn:string,clientjs:string,serverjs:string)
        {
            var r = this.eventBD[eventn];
             if(r==null||r==undefined)
             {
                 r = new EventBinder() ;
                 r.evtName = eventn ;
                 this.eventBD[eventn] = r ;
             }
             r.clientJS = clientjs ;
             r.serverJS = serverjs ;
        }
    }

}

