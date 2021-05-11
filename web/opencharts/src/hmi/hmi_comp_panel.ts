
module oc.hmi
{
    export class HMICompRes implements IDrawRes
    {
        compId:string;

        public constructor(compid:string)
		{
            this.compId = compid;
        }

        getDrawResUrl(name: string): string
        {
            return HMIComp.calcDrawResUrl(this.compId,name) ;
        }

        getDrawResParent(): IDrawRes | null
        {
            return null ;
        }
    }

    /**
     * for hmi edit and display
     */
    export class HMICompPanel extends DrawPanel
    {
        fixWidth:number = 1024 ;
        fixHeight:number = 768 ;

        public constructor(compid:string,target: string, opts: {})
		{
            super(target, opts);
            this.fixWidth = opts["width"]?opts["width"]:1024;
            this.fixHeight = opts["height"]?opts["height"]:768;

            this.setDrawRes(new HMICompRes(compid));
        }

        public getFixWidth()
        {
            return this.fixWidth ;
        }

        public getFixHeight()
        {
            return this.fixHeight;
        }
    }
}