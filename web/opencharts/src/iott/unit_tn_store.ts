module oc.iott
{
    export class UnitTNStore extends UnitTN
    {
        public constructor(opts: {} | undefined)
        {
            super(opts);

            
        }

        public getClassName()
        {
            return "oc.iott.UnitTNStore";
        }


        public getGroupName():string|null
		{
			return "store";
		}
    }
}