

namespace oc
{

    
    export class PropBinder
    {
        static PROP_BINDER = "_prop_binder" ;
        //bServerJS:boolean = false;

        propName:string="" ;

        bExp:boolean=false;

        binderTxt:string="" ;

        // public isServerJS()
        // {
        //     return this.bServerJS ;
        // }

        private mouseF:Function|null = null ;

        public getPropName():string
        {
            return this.propName ;
        }

        public isExp():boolean
        {
            return this.bExp ;
        }

        public setExp(b:boolean)
        {
            this.bExp = b ;
        }

        public getBinderTxt():string
        {
            return this.binderTxt ;
        }

        public setBinderTxt(js:string)
        {
            this.binderTxt = js ;
        }

        public isValid():boolean
        {
            return this.binderTxt!=null;
        }

        public toPropStr():oc.base.Props<string>
        {
            return {n:this.propName,txt:this.binderTxt,exp:""+this.bExp} ;
        }

        public fromPropStr(p:oc.base.Props<string>):boolean
        {
            var n = p["n"] ;
            var txt = p["txt"];
            if(n==undefined||n==null||txt==undefined||txt==null)
                return false;
            this.propName=  n ;
            this.binderTxt = txt ;
            this.bExp = "true"==p["exp"] ;
            return true ;
        }
    }
}