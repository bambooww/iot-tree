/// <reference path="../draw_mvc.ts" />

namespace oc.hmi
{
    export type PROPBIND_ITEM={name:string,valid:boolean,v:any,dt:string}
    export type PROPBIND={id:string,items:PROPBIND_ITEM[]}
    export type PROPBINDS={binds:PROPBIND[]}

    export interface HMIModelListener
    {
        //run once,background
        on_model_loaded(temp:{}):void;

        //run interval when running
        on_model_dyn_updated(dyn:{}):void;


        on_model_propbind_data(data:PROPBINDS):void;
    }


    //export IOTT
    /**
     * 
     */
    export class HMIModel extends DrawModel
    {
        //unitUrl:string;
        tempUrl:string;
        compUrl:string;
        dynUrl:string;

        //hmi node path,it's context root for inner item or hmi sub
        hmiPath:string;

        private listeners:HMIModelListener[]=[];

        private valUnit:{}|null=null;
        private valTemp:{}|null=null;
        private valCont:{}|null=null;
        private valDyn:{}|null=null;

        public constructor(opts:{temp_url:string,
            comp_url:string,
            dyn_url:string,hmi_path:string})
        {
            super() ;

            this.tempUrl = opts.temp_url;
            this.compUrl = opts.comp_url;
            this.dynUrl = opts.dyn_url;
            this.hmiPath = opts.hmi_path;
            //this.unitUrl = opts.unit_url;

            HMIComp.setAjaxLoadUrl(this.compUrl) ;
        }

        public getHmiPath():string
        {
            return this.hmiPath;
        }

        public registerListener(lis:HMIModelListener)
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

        public fireModelPropBindData(data:PROPBINDS)
        {
            if(typeof(data)=="string")
                eval("data="+data) ;

            for(var lis of this.listeners)
            {
                lis.on_model_propbind_data(data);
            }
            
        }
    }
}