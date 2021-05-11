module oc.iott
{
    export class UnitTNCh extends UnitTN
    {
        public constructor(opts: {} | undefined)
        {
            super(opts);
        }

        public getClassName()
        {
            return "oc.iott.UnitTNCh";
        }

        public setDynData(dyn:{},bfirechg:boolean=true):string[]
		{
            var brun = dyn["brun"];

            if(brun!=undefined&&brun!=null)
            {
                var ddata = {} ;
                if(brun)
                    ddata["fillColor"]="#a7ec21";
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