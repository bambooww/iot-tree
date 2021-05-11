
module oc.hmi
{
    /**
     * for hmi edit and display
     */
    export class HMIPanel extends DrawPanel
    {
        fixWidth:number = 1024 ;
        fixHeight:number = 768 ;

        public constructor(target: string, opts: {})
		{
            super(target, opts);
            this.fixWidth = opts["width"]?opts["width"]:1024;
            this.fixHeight = opts["height"]?opts["height"]:768;
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