/**
 * base on DrawItems,every unit has is own rect in which inner items
 * draw fit in it.
 * 
 * it has border and background or not
 * it also support expose some inner item name which can be set value dynanmicly,
 *  so it can be used to display real time data.
 * 
 * the user must implements an DrawUnit lib,to suport all unit saving,loading and listing
 * 
 */
module oc
{
	export type Props<TV> = { [index: string]: TV }

	/**
	 * draw unit can has instance,which has extends props
	 */
	export class DrawUnit extends DrawItems// implements IItemsLister
	{
		cat: string | null = null;
		//title: string | null = null;

		//on creation intance call this url
		// url must input layername and DrawUnit name and do some creation action,then return id with ins used
		ins_new_url:string|null=null;
		//create instance class name
		ins_new_cn:string|null=null;

		ins_expand_url:string|null=null;
		//on show action panel call this url,get panel html elements
		ins_act_url:string|null=null;
		ins_act_pos:number=0;
		ins_act_w:number=100;
		ins_act_h:number=100;
		ins_group:string|null=null;

		//like title:c1.txt,cc:rect.color
		//left alias name will be a prop used in instance,and right string is
		//   unit inner drawitem's name . prop.
		//   if alias is title,then instance title will set in unit's drawitem and show
		//   others alias will become instance extends props which can be set dyn data
		ins_alias_map:string|null=null;
		//private extProps:oc.base.Props<any>|null=null;

		private aliasMap:oc.base.Props<string[]>|null=null;

		public constructor(opts: {} | undefined)
		{
			super(opts);
		}

		public getClassName()
		{
			return "DrawUnit";
		}

		private static INS_TEMP_DEFS:oc.base.Props<any>= {
			ins_new_cn: { title: "ins_new_cn", type: "str",enum_val:[["oc.iott.Unit","Unit"],["oc.iott.UnitTN","UnitTN"]] },
			ins_new_url: { title: "ins_new_url", type: "str" },
			ins_expand_url: { title: "ins_expand_url", type: "str" },
			ins_act_url: { title: "ins_act_url", type: "str" },
			ins_act_pos: { title: "ins_act_pos", type: "int",enum_val:[[0,"bottom"],[1,"right"],[2,"left"],[3,"top"]] },
			ins_act_w: { title: "ins_act_w", type: "float" },
			ins_act_h: { title: "ins_act_h", type: "float" },
			ins_group: { title: "ins_group", type: "str" },
			ins_alias_map: { title: "ins_alias_map", type: "str",multi_lns:true },
		};

		/**
		 * for unit template edit to show temp prop
		 */
		public static getInsTempDefs():oc.base.Props<any>
		{
			return DrawUnit.INS_TEMP_DEFS;
		}

		static Unit_PNS:{}|null=null;

		private static getUnitPns():{}
		{
			if(DrawUnit.Unit_PNS!=null)
				return DrawUnit.Unit_PNS;

			var r = {
				_cat_name: "unit", _cat_title: "Unit",
				cat: { title: "Cat", type: "str" },
				//title: { title: "Title", type: "str" }
			};
	
			for(var n in DrawUnit.INS_TEMP_DEFS)
			{
				r[n] = DrawUnit.INS_TEMP_DEFS[n];
			}

			DrawUnit.Unit_PNS = r ;
			return r ;
		}

		public getPropDefs(): Array<any>
		{
			var r = super.getPropDefs();
			r.push(DrawUnit.getUnitPns());
			return r;
		}


		public getCat()
		{
			return this.cat;
		}

		public setCat(c: string)
		{
			this.cat = c;
			this.MODEL_fireChged(["cat"]);
		}

		public getTitle()
		{
			return this.title;
		}

		public setTitle(t: string)
		{
			this.title = t;
			this.MODEL_fireChged(["title"]);
		}

		public getInsNewUrl()
		{
			return this.ins_new_url;
		}

		public getInsNewCN()
		{
			return this.ins_new_cn;
		}

		public getInsExpandUrl()
		{
			return this.ins_expand_url;
		}

		public getInsActUrl()
		{
			return this.ins_act_url;
		}

		public getInsActW()
		{
			return this.ins_act_w;
		}

		public getInsActH()
		{
			return this.ins_act_h;
		}

		public getInsActPos()
		{
			return this.ins_act_pos;
		}

		public getInsGroup()
		{
			return this.ins_group;
		}

		/**
		 * trans title:c1.txt,cc:rect.color to map
		 * and support ins to update by alias map
		 */
		public getAliasMap():oc.base.Props<string[]>|null
		{
			if(this.aliasMap!=null)
				return this.aliasMap ;
			if(this.ins_alias_map==null||this.ins_alias_map=="")
			{
				return null ;
			}
			var r:oc.base.Props<string[]> = {};
			//title:c1.txt,cc:rect.color
			var ss = this.ins_alias_map.split(',');
			for(var s of ss)
			{
				var n2m = s.split(':');
				if(n2m.length!=2)
					continue ;
				var ms = n2m[1].split('.');
				if(ms.length!=2)
					continue ;
				r[n2m[0].trim()]=[ms[0].trim(),ms[1].trim()] ;
			}
			this.aliasMap = r ;
			return this.aliasMap;
		}
		// public getExtProp():oc.base.Props<any>|null
		// {
		// 	return this.extProps;
		// }

		// public setExtProp(ep:oc.base.Props<any>|null)
		// {
		// 	this.extProps = ep;
		// }

		public inject(opts: base.Props<any>,ignore_readonly:boolean|undefined)
		{
			if (typeof (opts) == 'string')
				eval("opts=" + opts);
			super.inject(opts,ignore_readonly);

			this.title = opts["title"] ? opts["title"] : "";

			var dis = opts["dis"];
			if (dis)
			{
				for (var i = 0; i < dis.length; i++)
				{
					var it = dis[i];
					var cn = it._cn;
					if (!cn)
						continue;
					var item = DrawItem.createByClassName(cn, undefined);
					if (item == null)
						continue;
					item.inject(it as base.Props<string>, false);
					this.addItem(item);
					//console.log(" draw unit cn="+cn+"  item="+item);
				}
			}
		}

		public extract(): {}
		{
			var r = super.extract();
			if (this.cat)
				r["cat"] = this.cat;
			// var rdis: any[] = r["dis"] = [];
			// for (var i = 0; i < this.items.length; i++)
			// {
			// 	rdis.push(this.items[i].extract());
			// }
			return r;
		}

		public getUnitDrawSize():oc.base.Size
		{
			var n = this.items.length;
			if(n<=0)
				return {w:100,h:100};
			var last = this.items[n-1];
			var r;
			if(last instanceof DrawItemRectBorder)
			{
				var tmps:DrawItem[]=[];
				for(var i = 0 ; i < n-1 ; i ++)
					tmps.push(this.items[i]);
				r = ItemsContainer.calcRect(tmps);
			}
			else
				r = ItemsContainer.calcRect(this.items);
			if (r == null)
				return {w:100,h:100};
			return {w:r.w,h:r.h};
		}

		/**
		 * to fit for list with fix square size
		 * a square border sub item is needed to be add
		 */
		public addSquareBorder(): boolean
		{
			this.getDrawSize();
			var r = ItemsContainer.calcRect(this.items);
			if (r == null)
				return false;
			//console.log(r);
			r = r.expandToSquareByCenter();
			//console.log(r);
			var tmpi = new DrawItemRectBorder({ rect: r });
			this.addItem(tmpi);
			return true;
		}

		
		/**
		 * 
		 */
		private drawUnit(cxt: CanvasRenderingContext2D, c: IDrawItemContainer, dyn_ps: {})
		{
			for (var item of this.items)
			{
				var n = item.getName();
				if (n != null && n != "")
				{
					var dynp = dyn_ps[n];
					if (dynp != undefined && dynp != null)
					{

					}
				}
				item.draw(cxt, c);
			}
		}


		// public getUnitActItems():UnitActItem[]|null
		// {
		// 	var n = this.getName();
		// 	if(n==null||n=="")
		// 		return null ;
		// 	return DrawUnit.getActionsByUnitName(n);
		// }

		// public getUnitActItem(op_name:string):UnitActItem|null
		// {
		// 	var uais = this.getUnitActItems();
		// 	if(uais==null)
		// 		return null;
		// 	for(var r of uais)
		// 	{
		// 		if(op_name==r.op_name)
		// 			return r ;
		// 	}
		// 	return null ;
		// }
		

		private static id2unit: { [id: string]: DrawUnit } = {};
		private static name2unit: { [name: string]: DrawUnit } = {};

		private static ajaxLoadUrl: string | null = null;

		

		public static setAjaxLoadUrl(u: string | null)
		{
			DrawUnit.ajaxLoadUrl = u;
		}

		// static addUnit(u: DrawUnit,ext:oc.base.Props<any>|null)
		// {
		// 	DrawUnit.setUnit(u,ext);
		// }

		static setUnit(u: DrawUnit)//,ext:oc.base.Props<any>|null)
		{
			// if(ext!=null)
			// 	u.setExtProp(ext);
			DrawUnit.id2unit[u.getId()] = u;
			var n = u.getName();
			if (n == null || n == undefined || n == "")
				return;
			DrawUnit.name2unit[n] = u;
		}

		static addUnitByJSON(json: string | {})
		{
			if (typeof (json) == 'string')
				eval("json=" + json);

			var u = new DrawUnit(undefined);
			u.inject(json as oc.base.Props<any>,false);
			var ext = json["_ext"];
			if(ext!=undefined&&ext!=null)
			{
				u.setDynData(ext,false);
			}
			DrawUnit.setUnit(u);
		}

		static getUnitById(id: string): DrawUnit | null
		{
			var u = DrawUnit.id2unit[id];
			if (u == null || u == undefined)
				return null;
			return u;
		}

		// static getOrLoadUnitById(id:string):DrawUnit|null
		// {
		// 	var u = DrawUnit.id2unit[id];
		// 	if(u!=undefined&&u!=null)
		// 		return u;

		// }
		static getUnitByName(n: string): DrawUnit | null
		{
			var u = DrawUnit.name2unit[n];
			if (u == null || u == undefined)
				return null;
			return u;
		}
		static getUnitJSONStr(id: string): string | null
		{
			var du = DrawUnit.getUnitById(id);
			if (du == null)
				return null;
			var ob = du.extract();
			return JSON.stringify(ob);
		}
	}


	
	export class DrawUnitIns extends DrawItemRectR
	{
		borderPixel: number | null = null;
		borderColor: string | null = "yellow";
		fillColor: string | null = null;
		radius: number | null = null;
		unitName: string | null = null;
		/**
		 * deep copy of DrawItem
		 */
		private dynUnit:DrawUnit|null=null;

		private innerCont: ItemsContainer | null = null;

		public constructor(opts: {} | undefined)
		{
			super(opts);
		}

		public static PN_DYN_UNIT = "_unit";

		private static PNS = {
			_cat_name: "unitins", _cat_title: "Unit Ins",
			borderPixel: { title: "border", type: "str" },
			borderColor: { title: "borderColor", type: "str" },
			fillColor: { title: "fillColor", type: "str", val_tp: "color" },
			radius: { title: "radius", type: "int" },
			unitName: { title: "Unit Name", type: "str", readonly: true }
		};

		public setUnitName(n: string)
		{
			this.unitName = n;
			this.MODEL_fireChged(["unitName"]);
		}
		public getUnitName()
		{
			return this.unitName;
		}

		public getUnit(): DrawUnit | null
		{//a copy of unit
			if(this.dynUnit!=null)
				return this.dynUnit;
			if (this.unitName == null || this.unitName == "")
				return null;

			var du = DrawUnit.getUnitByName(this.unitName);
			if(du==null)
				return null;
			this.dynUnit = du.duplicateMe() as DrawUnit;
			var c = this.getContainer();
			if(this.dynUnit!=null&&c!=null)
				this.dynUnit.setContainer(c,this.getLayer());
			return this.dynUnit;
		}

		// public getUnitExtProps():oc.base.Props<any>|null
		// {
		// 	var du = this.getUnit();
		// 	if(du==null)
		// 		return null;
		// 	return du.getExtProp();
		// }

		//public getUnit

		public getInnerDrawItemByName(n:string):DrawItem|null
		{
			var u = this.getUnit();
			if(u==null)
				return null;
			for(var i of u.getItemsShow())
			{
				if(n==i.getName())
					return i;
			}
			return null;
		}

		/**
		 * set inneritem dyn must by item's name
		 * if item is not set name,it cannot be set dyn
		 * @param dyn 
		 * @param bfirechg 
		 */
		public setDynData(dyn:{},bfirechg:boolean=true):string[]
		{
			var ns = super.setDynData(dyn,false);

			var u = this.getUnit() ;
			if(u!=null)
			{
				var aliasmap = u.getAliasMap() ;
				if(aliasmap!=null)
				{
					for(var aliasn in aliasmap)
					{
						var v = dyn[aliasn] ;//use alias name getval
						if(v==undefined||v==null)
							continue;
						var mapss = aliasmap[aliasn];
						if(mapss.length<2)
							continue ;
						var tmpi = this.getInnerDrawItemByName(mapss[0]);
						if(tmpi==null)
							continue;
						tmpi[mapss[1]] = v;
					}
				}
			}
			

			var dyn_unit = dyn[DrawUnitIns.PN_DYN_UNIT];
			if(dyn_unit!=undefined&&dyn_unit!=null)
			{
				ns.push(DrawUnitIns.PN_DYN_UNIT);
				for(var n in dyn_unit)
				{
					var tmpi = this.getInnerDrawItemByName(n);
					if(tmpi==null)
						continue;
					tmpi.setDynData(dyn_unit[n],false);
				}
			}
			if(bfirechg)
				this.MODEL_fireChged(ns);
			return ns;
		}

		private getInnerContainer(): ItemsContainer | null
		{
			if (this.innerCont != null)
				return this.innerCont;

			var pc = this.getContainer();
			if (pc == null)
				return null;
			var u = this.getUnit();
			if (u == null)
				return null;
			this.innerCont = new ItemsContainer(this, pc, u);
			return this.innerCont;
		}



		public getClassName()
		{
			return "DrawUnitIns";
		}

		public getPropDefs(): Array<any>
		{
			var r = super.getPropDefs();
			r.push(DrawUnitIns.PNS);
			return r;
		}

		/**
		 * override it to support alias map inject
		 * to unit
		 * @param prop_names 
		 */
		public MODEL_fireChged(prop_names:string[]|null)
		{
			var u = this.getUnit() ;
			if(u!=null)
			{
				var aliasmap = u.getAliasMap() ;
				if(aliasmap!=null)
				{
					for(var aliasn in aliasmap)
					{
						var v = this[aliasn] ;//instance alias name getval
						if(v==undefined||v==null)
							continue;
						var mapss = aliasmap[aliasn];
						if(mapss.length<2)
							continue ;
						var tmpi = this.getInnerDrawItemByName(mapss[0]);
						if(tmpi==null)
							continue;
						tmpi[mapss[1]] = v;
					}
				}
			}

			super.MODEL_fireChged(prop_names);
		}

		public getPrimRect(): base.Rect | null
		{
			var ic = this.getInnerContainer();
			if (ic == null)
				return new oc.base.Rect(0, 0, this.getW(), this.getH());
			// return ic.getItemsRectInner();
			var u = this.getUnit();
			if (u == null)
				return new oc.base.Rect(0, 0, this.getW(), this.getH());
			//return u.getPrimRect();
			//return new oc.base.Rect(0,0,100,100);

			var r = ItemsContainer.calcRect(u.getItemsShow());
			if (r == null)
				return null;
			var p = ic.transDrawPt2PixelPt(r.x, r.y);
			var w = ic.transDrawLen2PixelLen(true, r.w);
			var h = ic.transDrawLen2PixelLen(false, r.h);

			return new oc.base.Rect(0, 0, w, h);
			//return r ;
		}

		/**
		 * override to provider more extends item in unit
		 */
		protected getUnitExtItems():DrawItem[]
		{
			return [];
		}

		public drawPrim(cxt: CanvasRenderingContext2D): void
		{
			var u = this.getUnit();
			var ic = this.getInnerContainer();
			var items = (u != null ? u.getItemsShow() : null);
			if (ic == null || u == null || items == null || items.length <= 0)
			{
				//var tmpr = this.getPrimRect();
				//if (tmpr == null)
				//	return;
				util.drawRectEmpty(cxt,
					0, 0, this.getW(), this.getH(), this.borderColor);
				return;
			}

			//this.drawRect(cxt,c);
			//
			cxt.save();
			var pt = this.getPixelXY();
			if (pt != null)//what the fuck
				cxt.translate(-pt.x, -pt.y);
			for(var item of items)
			{
				item.draw(cxt,ic);
			}
			for(var item of this.getUnitExtItems())
			{
				item.draw(cxt,ic);
			}
			//u.draw(cxt, ic);
			cxt.restore();
		}

		public drawPrimSel(ctx: CanvasRenderingContext2D): void
		{

		}


	}
}