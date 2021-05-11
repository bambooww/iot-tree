
namespace oc.iott
{
    export type ListHead = {n:string,t:string}[]
    //export type LItem = {id:string,[index: string]: any }

    export type ListItemDyn={id:string,_oc_dyn:{}}

    export class ListItem implements IActionNode
    {
        id:string="";
        name:string="";
        title:string="";

        divList:DIDivList;

        public constructor(dl:DIDivList)
        {
            this.divList = dl ;
        }

        getId(): string
        {
            return this.id ;
        }
        getName(): string
        {
            return this.name;
        }
        getTitle(): string
        {
            return this.title;
        }

        public isValid():boolean
        {
            return this.id!="" ;
        }

        getActionTypeName(): string
        {
            return "list_item";
        }
        
        getPanel(): AbstractDrawPanel | null
        {
            return this.divList.getPanel();
        }
    }

    export class DIDivList extends DIDiv implements ITreeNode
    {
        parentTN:UnitTN|null=null;

        tbHead:ListHead=[];
        listItems:ListItem[]=[];
        //tbBody:oc.base.Props<any>={};

        //private tbBodyEle:JQuery<HTMLElement>|null=null;

        private bFirst=true;

        public constructor(opts: {} | undefined)
        {
            super(opts);

        }

        hasSubNode(): boolean
        {
            return false;
        }
        getSubNode(): ITreeNode[] | null
        {
            return null ;
        }
        getParentTN(): UnitTN | null
        {
            return this.parentTN;
        }
        setParentTN(p: UnitTN): void
        {
            this.parentTN = p;
        }

        public isHidden():boolean
		{
            if(this.parentTN==null)
                return false;
            if(this.parentTN.isHidden())
                return true;
            return !this.parentTN.isSubExpanded();
        }

        /**
         * override to fix parent bottom
         */
        public getDrawXY():base.Pt
		{
            var ptn = this.getParentTN() ;
            if(ptn==null)
                return super.getDrawXY() ;
            var pxy = ptn.getDrawXY() ;
            //set actual xy,to avoid some draw size err
            this.x = pxy.x;
            this.y = pxy.y+ptn.getH();
			return { x: this.x, y:this.y};
		}
        

        public getListItem(id:string):ListItem|null
        {
            for(var li of this.listItems)
            {
                if(id==li.id)
                    return li ;
            }
            return null ;
        }

        public getListItemPixelRect(id:string):oc.base.Rect|null
        {
            
            for(var i = 0 ; i < this.listItems.length ; i ++)
            {
                var li = this.listItems[i];
                if(id==li.id)
                {

                }
            }
            return null ;
        }


        public setListTable(hds:ListHead,items:ListItem[]|null|undefined)
        {
            this.tbHead = hds;
            if(items==null||items==undefined)
                items=[];
            var ss = `<table class="oc_div_list" id="divlist_tb_${this.id}"><thead><tr>`
            for(var h of this.tbHead)
            {
                ss += `<th>${h.t}</th>`;
            }
            ss += `</tr></thead><tbody id="div_list_bd_${this.getId()}">`;
            for(var item of items)
            {
                ss+=`<tr id="divlist_tr_${item.id}" didiv_listitem="${item.id}"
                 onmousedown="oc.iott.DIDivList.trAction(${MOUSE_EVT_TP.Down},'${this.id}','${item.id}')"
                 ondblclick="oc.iott.DIDivList.trAction(${MOUSE_EVT_TP.DbClk},'${this.id}','${item.id}')" >`;
                for(var h of this.tbHead)
                {
                    var v = item[h.n];
                    v = v?v:"" ;
                    ss+= `<td>${v}</td>`
                }
                ss+= `</tr>`;

                item.divList = this;
            }
            ss += `</tbody></table>`;

            var tb = $(ss);
            tb.get(0)["_oc_di_divlist"]=this;
            this.listItems = items;
            super.setInnerEle(tb);
            
            tb[0].onmousedown=(e:_MouseEvent)=>{
                //e.preventDefault();
                //e.stopPropagation();
            };
        }

        public setListDyn(items:ListItemDyn[]|null|undefined)
        {
            if(items==null||items==undefined)
                return ;
            for(var item of items)
            {
                var ocdyn = item._oc_dyn ;
                if(ocdyn==undefined||ocdyn==null)
                    continue ;
                var tds = $(`#divlist_tr_${item.id} td`);
                if(tds.length<=0)
                    continue;
                for(var i = 0 ; i < tds.length && i<this.tbHead.length ; i ++)
                {
                    var h = this.tbHead[i] ;
                    var v = ocdyn[h.n];
                    if(v==undefined||v==null)
                        continue ;
                    tds[i].innerHTML = v ;
                }
            }
        }

        public on_item_mouse_event(item:ListItem,tp: MOUSE_EVT_TP,pxy:oc.base.Pt,dxy:oc.base.Pt,e:_MouseEvent)
        {
            if(tp==MOUSE_EVT_TP.Down)
            {
                if(e.button==MOUSE_BTN.RIGHT)
                {
                    if(PopMenu.createShowPopMenu(item,pxy,dxy))
                        e.preventDefault();
                }
                return;
            }

            if(tp==MOUSE_EVT_TP.DbClk)
            {
                var pmi = PopMenu.getDefaultPopMenuItem(item);
				if(pmi!=null)
				{
					pmi.action(item,pmi.op_name,pxy,dxy) ;
				}
                return ;
            }
        }

        public getActionTypeName():string
        {//override to provider item to has popmenu
			return "div_list" ;
        }
        
        public on_mouse_event(tp: MOUSE_EVT_TP, pxy: oc.base.Pt, dxy: oc.base.Pt,e:_MouseEvent)
        {
            if (tp == MOUSE_EVT_TP.Down)
            {
                if(e.button==MOUSE_BTN.RIGHT)
                {
                    if(PopMenu.createShowPopMenu(this,pxy,dxy))
                        e.preventDefault();
                }
            }
            
            super.on_mouse_event(tp, pxy, dxy,e) ;
        }

        public static trAction(tp: MOUSE_EVT_TP,diid:string,id:string)
        {
            //console.log("menuAction")
            var tb=$("#divlist_tb_"+diid).get(0);
            var di = tb["_oc_di_divlist"] as DIDivList;
            if(di==undefined||di==null)
                return;
            var e = window.event as _MouseEvent;
            if(e==undefined||e==null)
                return ;
            
            e.stopPropagation();
            if (di != undefined &&di != null)
            {
                var p = di.getPanel();
                var c = di.getContainer() ;
                if(p==null||c==null)
                    return ;
                var li = di.getListItem(id) ;
                if(li==null)
                    return;
                var pxy = p.getEventPixel(e);
                var dxy = c.transPixelPt2DrawPt(pxy.x,pxy.y);
                di.on_item_mouse_event(li,tp,pxy,dxy,e);
                e.stopPropagation();
            }
        }

        public getClassName()
        {
            return "oc.iott.DIDivList";
        }
    }
}