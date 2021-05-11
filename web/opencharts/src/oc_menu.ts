/**
 * menu support for drawitem
 */
namespace oc
{
    export type PopMenuAction=(dn:IPopMenuTarget,op:string,pxy:oc.base.Pt,dxy:oc.base.Pt)=>void;

    //{op_name:"new_ch",op_title:"New Channel",action:act_ch_new_ch},
	export type PopMenuItem={op_name:string,op_title:string,op_icon:string|undefined,action:PopMenuAction,default:boolean|undefined};

    export interface IPopMenuPanel
    {
        delPopMenu():void;

		setPopMenu(menuele:JQuery<HTMLElement>):void;
    }

    export interface IPopMenuTarget
	{
		getId():string;
		//getName():string;
		//getTitle():string;

		getPanel():IPopMenuPanel|null;
    }
    
    export class PopMenu
    {
        private target:IPopMenuTarget;
        //private panel:DrawPanel;
        private menuEle: JQuery<HTMLElement> | null = null;

        menuItems:PopMenuItem[]=[];

        public constructor(di:IDrawNode,menuitems:PopMenuItem[])
        {
            //this.panel=p;
            this.target = di ;
            this.menuItems = menuitems;
        }

        public getTarget():IPopMenuTarget
        {
            return this.target;
        }

        public getMenuItems():PopMenuItem[]
		{
            return this.menuItems;
        }

        public getMenuItem(op:string):PopMenuItem|null
        {
            for(var mi of this.menuItems)
            {
                if(mi.op_name==op)
                    return mi ;
            }
            return null ;
        }
        
        public getMenuEle():JQuery<HTMLElement>
        {
            var tmpid = this.target.getId();
            if(this.menuEle!=null)
                return this.menuEle ;

            var tmps = `<div id="menu_${tmpid}" class="oc_menu">`;
            for(var actitem of this.menuItems)
            {
                var icon = actitem.op_icon;
                if(icon==undefined||icon=='')
                    icon = "icon";
                tmps += `<div class="menu" act_id="${tmpid}" act_op="${actitem.op_name}" onmousedown="oc.PopMenu.menuAction('${tmpid}','${actitem.op_name}')"><i class="${icon}">&nbsp;</i>&nbsp;&nbsp;<span>${actitem.op_title}</span></div>`;
            }
            tmps += `</div>`;

            this.menuEle = $(tmps);
            this.menuEle.get(0)["_oc_popmenu"]=this;
            return this.menuEle;
        }

        public showPopMenu(tar:any,pxy:oc.base.Pt,dxy:oc.base.Pt):boolean
        {
            var p = this.target.getPanel() ;
            if(p==null)
                return false;
            var menuele = this.getMenuEle();

            p.setPopMenu(menuele) ;
            
            menuele.css("left", pxy.x+ "px");
            menuele.css("top", pxy.y + "px");
            menuele.get(0)["_oc_pxy"]=pxy ;
            menuele.get(0)["_oc_dxy"]=dxy ;
            return true;
        }


        // static displayPopMenu(tar:any,popm:PopMenu,pxy:oc.base.Pt,dxy:oc.base.Pt)
        // {
        //     var p = popm.getDrawItem().getPanel();
        //     if(p==null)
        //         return ;
        //     var menuele = popm.getMenuEle();
            
        //     p.setPopMenu(menuele) ;
            
        //     menuele.css("left", pxy.x+ "px");
        //     menuele.css("top", pxy.y + "px");
        //     menuele.get(0)["_oc_pxy"]=pxy ;
        //     menuele.get(0)["_oc_dxy"]=dxy ;
        // }
        public static getDefaultPopMenuItem(dn:IActionNode):PopMenuItem|null
        {
            var n = dn.getActionTypeName();
			if(n=="")
				return null ;
			return PopMenu.getMenuItemDefaultByName(n) ;
        }
        
		public static createShowPopMenu(dn:IActionNode,pxy: oc.base.Pt, dxy: oc.base.Pt):boolean
        {
			var n = dn.getActionTypeName();
			if(n=="")
				return false ;

			//if(this.popMenu!=null)
			//	return this.popMenu;
			
			var pms = PopMenu.getMenuItemsByName(n) ;
			if(pms==null)
				return false ;
			var p = dn.getPanel() ;
			if(p==null)
				return false;
			var pm = new PopMenu(dn,pms) ;
            //return this.popMenu;
            return pm.showPopMenu(dn,pxy,dxy) ;
        }


        public static menuAction(id:string,op:string)
        {
            //console.log("menuAction")
            var ob = $("#menu_" + id);
            if (ob == null || ob == undefined)
                return;
            var pm = ob.get(0)["_oc_popmenu"] as PopMenu;
            if(pm==null)
                return;
            var dn = pm.getTarget();
            var mitem = pm.getMenuItem(op) ;
            var p = dn.getPanel() ;
            if(mitem==null||p==null)
                return;
            //var u = ob.get(0)["_oc_unit"]// as Unit;
            var pxy=ob.get(0)["_oc_pxy"];
            var dxy=ob.get(0)["_oc_dxy"];
            if(window!=undefined&& window.event)
                window.event.stopPropagation();
            mitem.action(dn,op,pxy,dxy);
            p.delPopMenu();
        }

        private static tp2items:{[tpname: string]: PopMenuItem[]}|null=null;


		public static setMenuTp2Items(tp2items:{[tpname: string]: PopMenuItem[]})
		{
			PopMenu.tp2items = tp2items ;
		}

		public static getMenuItemsByName(u:string):PopMenuItem[]|null
		{
			if(PopMenu.tp2items==null)
				return null ;
			return PopMenu.tp2items[u];
        }
        
        public static getMenuItemDefaultByName(n:string):PopMenuItem|null
        {
            var pms = this.getMenuItemsByName(n);
            if(pms==null)
                return null ;
            for(var pm of pms)
            {
                if(pm.default===true)
                    return pm ;
            }
            return null ;
        }


		public static getMenuItemByNameOp(u:string,op:string):PopMenuItem|null
		{
			if(PopMenu.tp2items==null)
				return null ;
			var acts = PopMenu.tp2items[u];
			if(!acts)
				return null ;
			for(var act of acts)
			{
				if(op==act.op_name)
					return act ;
			}
			return null;
		}
    }
    
}