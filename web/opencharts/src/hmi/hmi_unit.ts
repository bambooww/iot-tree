
module oc.hmi
{
    /**
     * A unit instance,which use unit template to display
     * for hmi configuration
     * overrider must provider template name,which can be used to edit
     * 
     */
    export abstract class HMIUnit extends DrawItemRectR
	{
		private static name2temp: { [name: string]: DrawUnit } = {};

		private static ajaxLoadUrl: string | null = null;

		

		public static setAjaxLoadUrl(u: string | null)
		{
			HMIUnit.ajaxLoadUrl = u;
		}

		static setUnitTemp(u: DrawUnit)//,ext:oc.base.Props<any>|null)
		{
			var n = u.getName();
			if (n == null || n == undefined || n == "")
				return;
            HMIUnit.name2temp[n] = u;
		}

		static addUnitTempByJSON(json: string | {})
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
			HMIUnit.setUnitTemp(u);
		}

		static getUnitTempByName(n: string): DrawUnit | null
		{
			var u = HMIUnit.name2temp[n];
			if (u == null || u == undefined)
				return null;
			return u;
		}
		static getUnitTempJSONStr(n: string): string | null
		{
			var du = HMIUnit.getUnitTempByName(n);
			if (du == null)
				return null;
			var ob = du.extract();
			return JSON.stringify(ob);
        }
        
		borderPixel: number | null = null;
		borderColor: string | null = "yellow";
		fillColor: string | null = null;
		radius: number | null = null;
		//unitName: string | null = null;
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
			_cat_name: "hmi_unit", _cat_title: "HMI Unit",
			borderPixel: { title: "border", type: "str" },
			borderColor: { title: "borderColor", type: "str" },
			fillColor: { title: "fillColor", type: "str", val_tp: "color" },
			radius: { title: "radius", type: "int" }
		};

        public abstract getUnitTempName():string;
        
        
		public getClassName()
		{
			return "oc.hmi.HMIUnit";
		}

		public getPropDefs(): Array<any>
		{
			var r = super.getPropDefs();
			r.push(HMIUnit.PNS);
			return r;
		}


		public getUnitTemp(): DrawUnit | null
		{//a copy of unit
			if(this.dynUnit!=null)
				return this.dynUnit;
            var tn = this.getUnitTempName() ;
            if(tn==null||tn=="")
                return null ;
			var du = DrawUnit.getUnitByName(tn);
			if(du==null)
				return null;
			this.dynUnit = du.duplicateMe() as DrawUnit;
			var c = this.getContainer();
			if(this.dynUnit!=null&&c!=null)
				this.dynUnit.setContainer(c,this.getLayer());
			return this.dynUnit;
		}

		public getInnerDrawItemByName(n:string):DrawItem|null
		{
			var u = this.getUnitTemp();
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

			var u = this.getUnitTemp() ;
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
			var u = this.getUnitTemp();
			if (u == null)
				return null;
			this.innerCont = new ItemsContainer(this, pc, u);
			return this.innerCont;
		}


		/**
		 * override it to support alias map inject
		 * to unit
		 * @param prop_names 
		 */
		public MODEL_fireChged(prop_names:string[]|null)
		{
			var u = this.getUnitTemp() ;
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
			var u = this.getUnitTemp();
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
			var u = this.getUnitTemp();
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