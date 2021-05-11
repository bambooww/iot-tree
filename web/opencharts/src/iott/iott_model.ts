module oc.iott
{
    export interface IOTTModelListener
    {
        //run once,background
        on_model_temp_loaded(temp:{}):void;
        //run once for unit lib
        on_model_unit_loaded(ret: {}): void

        //run changed when modify, UACh UADev
        on_model_cont_chged(cont:{}):void;

        //run interval when running
        on_model_dyn_updated(dyn:{}):void;
    }


    //export IOTT
    /**
     * 
     */
    export class IOTTModel
    {
        unitUrl:string;
        tempUrl:string;
        contUrl:string;
        dynUrl:string;

        private listeners:IOTTModelListener[]=[];

        private valUnit:{}|null=null;
        private valTemp:{}|null=null;
        private valCont:{}|null=null;
        private valDyn:{}|null=null;

        public constructor(opts:{temp_url:string,
            cont_url:string,
            dyn_url:string,unit_url:string})
        {
            this.tempUrl = opts.temp_url;
            this.contUrl = opts.cont_url;
            this.dynUrl = opts.dyn_url;
            this.unitUrl = opts.unit_url;
        }

        public registerListener(lis:IOTTModelListener)
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
            oc.util.doAjax(this.tempUrl,pm,(bsucc,ret)=>{
                if(bsucc)
                {
                    this.valTemp = ret;
                    this.fireModelLoaded("temp",ret);
                    //this.loadOrUpdate();
                }
            });
            
            oc.util.doAjax(this.unitUrl,pm,(bsucc,ret)=>{
                if(bsucc)
                {
                    
                    this.valUnit = ret;
                    this.fireModelLoaded("unit",ret);
                    //firefox edge must load cont after unit
                    this.loadOrUpdate();
                }
            });
           
        }

        public loadOrUpdate()
        {
            oc.util.doAjax(this.contUrl,{},(bsucc,ret)=>{
                if(!bsucc||(typeof(ret)=="string" && ret.indexOf("{")!=0))
                {
                    oc.util.prompt_err(ret as string);
                    return ;
                }
                this.valCont = ret;
                this.fireModelContChged(ret);
            });
        }

        public refreshDyn(endcb:Function|undefined)
        {
            oc.util.doAjax(this.dynUrl,{},(bsucc,ret)=>{
                try
                {
                    if(!bsucc||(typeof(ret)=="string" && ret.indexOf("{")!=0))
                    {
                        oc.util.prompt_err(ret as string);
                        return ;
                    }
                    this.valDyn = ret;
                    this.fireModelDynUpdated(ret);
                }
                finally
                {
                    if(endcb)
                        endcb() ;
                }
            });
        }

        public getTemplate():{}
        {
            return {} ;
        }

        public getContent():{}
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
                    case "temp":
                        lis.on_model_temp_loaded(mv);
                        break;
                    case "unit":
                        lis.on_model_unit_loaded(mv);
                        break;
                }
                
            }
        }
        
        public fireModelContChged(cont:{})
        {
            for(var lis of this.listeners)
            {
                lis.on_model_cont_chged(cont);
            }
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