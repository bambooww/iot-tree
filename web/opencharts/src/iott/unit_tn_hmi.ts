module oc.iott
{
    export class UnitTNHmi extends UnitTN
    {
        public constructor(opts: {} | undefined)
        {
            super(opts);

            
        }

        public getClassName()
        {
            return "oc.iott.UnitTNHmi";
        }

        public draw(cxt: CanvasRenderingContext2D, c: IDrawItemContainer)
        {
            super.draw(cxt,c);
        }

        public getGroupName():string|null
		{
			return "hmi";
		}
    }
}