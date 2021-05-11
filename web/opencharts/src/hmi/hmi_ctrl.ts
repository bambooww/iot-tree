/// <reference path="../draw_mvc.ts" />

namespace oc.hmi
{
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
    export class HMIController extends DrawCtrl
    {
        hmiPanel: HMIPanel;

        hmiModel: HMIModel;


        drawLayer: DrawLayer;


        tickInt: number | null = null;


        name2DiProxy: oc.base.Props<any> = {}

        public constructor(panel: HMIPanel, model: HMIModel)
        {
            super();
            
            this.hmiPanel = panel;
            this.hmiModel = model;

            this.drawLayer = panel.getLayer();
        }


        public startRunning()
        {
            if (this.tickInt != null)
                return;
            this.tickInt = setInterval(() =>
            {
                this.drawLayer.fireTick();
            }, 100);
        }

        public stopRunning()
        {
            if (this.tickInt == null)
                return;
            clearInterval(this.tickInt);
            this.tickInt = null;
        }

        public $(name: string)
        {
            var r = this.name2DiProxy[name];
            if (r != undefined && r != null)
                return;

            var di = this.drawLayer.getItemByName(name);
            if (di == null)
                return;
            var bcomp_ins = di.getClassName()=="oc.hmi.HMICompIns";
            r = new Proxy(di, {
                get(target, key)
                {
                    let result = target[key];
                    return result;
                },
                set(target, key, value)
                {
                    var b = false;
                    if(bcomp_ins)
                    {
                        b = (di as HMICompIns).setInterPropVal(key as string,value);
                    }
                    
                    if(!b)
                    {
                        b = Reflect.set(target, key, value);
                    }

                    //target[key] = value;
                    return b ;
                }
            });
            this.name2DiProxy[name] = r;
            return r ;
        }


    }
}