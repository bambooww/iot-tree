
namespace oc
{// draw model-view-controller

    
    export class DrawModel
    {

    }

    export class DrawCtrl
	{
        
    }

    export abstract class DrawView
    {
        //repId:string;
        //hmiId:string ;
        model:DrawModel ;
        panel:AbstractDrawPanel ;
        //ctrl:DrawCtrl ;

        constructor(model:DrawModel,panel:AbstractDrawPanel)//,ctrl:DrawCtrl) repid:string,hmiid:string,
        {
            // this.repId = repid ;
            // this.hmiId = hmiid ;
            this.model = model ;
            this.panel = panel ;
            //this.ctrl = ctrl;
            this.panel.drawView = this ;
        }

        public getModel():DrawModel
        {
            return this.model ;
        }

        public getPanel():AbstractDrawPanel
        {
            return this.panel;
        }
        // public getCtrl():DrawCtrl
        // {
        //     return this.ctrl ;
        // }

        public abstract getInteract():DrawInteract|null;

        protected abstract sendMsgToServer(msg:string):void;

        public fireEventToServer(diid:string,eventn:string,eventv:any):void
        {//repid:this.repId,hmiid:this.hmiId
            var msg = {tp:"event",diid:diid,name:eventn,val:eventv} ;
            this.sendMsgToServer(JSON.stringify(msg)) ;
        }

        public comp_fire_event_to_server(comp:IDIDivComp,eventn:string,eventv:any)
        {
            var msg = {} ;
            msg["repid"] = null;
        }
    }

}