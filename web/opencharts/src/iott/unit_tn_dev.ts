module oc.iott
{
    export class UnitTNDev extends UnitTN
    {
        public constructor(opts: {} | undefined)
        {
            super(opts);
        }

        public getClassName()
        {
            return "oc.iott.UnitTNDev";
        }

        public setDynData(dyn:{},bfirechg:boolean=true):string[]
		{
            var brun = dyn["brun"];

            if(brun!=undefined&&brun!=null)
            {
                var ddata = {} ;
                if(brun)
                    ddata["fillColor"]="green";
                else
                    ddata["fillColor"]="red";
                var tmpi = this.getInnerDrawItemByName("runst");
                if(tmpi!=null)
                {
                    tmpi.setDynData(ddata,false);
                }
                return [];
            }
			return super.setDynData(dyn,bfirechg);
		}
    }
}