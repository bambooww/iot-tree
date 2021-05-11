
module oc
{
    type IdxImg={[propName: string]: {n:string,t:string,p:string,img:CanvasImageSource|null}};

    export interface IDrawRes
    {
        //interfaceDrawRes: 'IDrawRes';
        /**
         * bindn may 
         * xxx_xxx-xx unique resource id
         * ./xxx   local res cxt，and name
         * ../xxx  parent res cxt
         * .../xxx parent parent res cxt
         * @param bindn 
         */
        getDrawResUrl(name:string):string ;

        getDrawResParent():IDrawRes|null ;
    }
    /**
     * Res Context
     * like component pics in comp cxt,ui's res in ui
     */
    export class DrawResCxt
	{
        name:string;
        title:string;
        eleRes:HTMLElement;

        urlBase:string;

        idxPath:string;

        idxMap:IdxImg={};
		/**
         * 
         * @param target html page hidden element(like div) in which support dynamicly load pic
         */
        public constructor(name:string,title:string,
            target:string,urlbase:string,idxpath:string)
		{
            this.name = name ;
            this.title = title;

            var ele = document.getElementById("target");
            if(ele==null)
                throw Error("no element with id="+target+" found");
            this.eleRes = ele;
            $(this.eleRes).css("display","none");
            this.urlBase=urlbase;
            this.idxPath = idxpath;
        }

        public getName():string
        {
            return this.name;
        }

        public getTitle():string
        {
            return this.title;
        }
        /**
         * {name:string,title:string,path:string}
         */
        public loadIdx()
        {
            $.ajax({
                type: 'post',
                url:this.urlBase+this.idxPath,
                data: {},
                async: true,  
                success: function (result) {  
                    var ob:any=null;
                    eval("ob="+result);
                    if(ob==null)
                        return;
                    for(var o of ob)
                    {
                        var n=o["n"];
                        if(n==null||n==undefined||n=="")
                            continue;
                        var p=o["p"];
                        if(p==null||p==undefined||p=="")
                            continue ;
                        var t=o["t"];
                        if(t==null||t==undefined||t=="")
                            t=n ;
                        
                        this.idxMap[n]={n:n,t:t,p:p};
                    }
                },
                error:function(req,err,e)
                {
                    //cb(false,e) ;
                }
            });

            //this.
            
        }
    }
}