module oc.hmi
{
    export interface HMICompModelListener
    {
        //run once,background
        on_model_loaded(temp:{}):void;

        //run interval when running
        on_model_dyn_updated(dyn:{}):void;
    }

    export class HMICompModel
    {
        
        compUrl:string;
        valCompTxt:string|{}="";

        private listeners:HMICompModelListener[]=[];

        public constructor(opts:{comp_url:string})
        {
            this.compUrl = opts.comp_url;

            //HMIComp.setAjaxLoadUrl(this.compUrl) ;
        }

        public registerListener(lis:HMICompModelListener)
        {
            this.listeners.push(lis);
        }

        /**
         * view need call this method,to notify outer is ready,model can start
         * load data,and fire mode changing event
         */
        public initModel():void
        {

            var pm={};
            oc.util.doAjax(this.compUrl,pm,(bsucc,ret)=>{
                if(bsucc)
                {
                    this.valCompTxt = ret;
                    this.fireModelLoaded("comp",ret);
                    //this.loadOrUpdate();

                    // this.loadOrUpdate();
                }
            });
            
           
        }


        public getTemplate():{}
        {
            return {} ;
        }

        public getRTDynData():{}
        {
            return {} ;
        }

        public fireModelLoaded(tp:string,mv:{})
        {
            for(var lis of this.listeners)
            {
                switch(tp)
                {
                    case "comp":
                        lis.on_model_loaded(mv);
                        break;
                    
                }
                
            }
        }
        
        public fireModelContChged(cont:{})
        {
            
        }

        public fireModelDynUpdated(dyn:{})
        {
            for(var lis of this.listeners)
            {
                lis.on_model_dyn_updated(dyn);
            }
        }
    }
}
