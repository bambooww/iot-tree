module oc
{
	export type DrawEditorPlugCB=(htmlele:JQuery<HTMLElement>,tp:string,di:DrawItem,name:string,val:string|EventBinder|PropBinder|null)=>void;

	export class DrawEditor implements IModelListener
	{
		tarEditPropEle:HTMLElement;
		tarEditEvtEle:HTMLElement;
		drawPanel:DrawPanel;

		private selectedItem:DrawItem|null=null;

		plugCB:DrawEditorPlugCB|null=null ;

		public constructor(prop_ele:string,event_ele:string,panel:DrawPanel,opts:{})
		{
			var ele = document.getElementById(prop_ele);
			var evt_ele = document.getElementById(event_ele);
			if(ele==null||evt_ele==null)
				throw new Error("no edit panel element found") ;
			this.tarEditPropEle = ele ;
			this.tarEditEvtEle = evt_ele;
			this.drawPanel = panel ;
			if(!opts)
				opts = {} ;
			var pcb = opts["plug_cb"];
			if(pcb!=null)
				this.plugCB=pcb ;
			panel.MODEL_registerListener(this);
		}
		
		public init_editor()
		{
			
		}
		
		on_model_chged(panel:DrawPanel,layer: DrawLayer, item: DrawItem | null, prop_names: string[] | null): void
		{
			if(item!=null&&this.selectedItem==item&&prop_names!=null)
			{
				for(var n of prop_names)
				{
					var v = item[n] ;
					$("#si_"+n).val(v);
				}
			}
		}

		on_model_oper_chged(panel: DrawPanel, intera: DrawInteract, oper: DrawOper | null): void
		{
			
		}

		on_model_sel_chged(panel: DrawPanel, layer: DrawLayer, item: DrawItem | null): void
		{
			this.onItemSelected(item);
		}
		
		public static createPropItemEditHtml(n:string,pdf:any,v:any,pb:PropBinder|null):string
		{
			var rets = `<tr>
			<td style="width:40%" class="" title="${n}">${pdf.title}</td>
			<td style="width:40%">`;

			var editplug = pdf.edit_plug ;
			if(editplug==undefined||editplug==null)
				editplug = "" ;
			if(pdf.readonly)
			{
				rets += `<input type=text id="si_${n}" propn="${n}" edit_plug="${editplug}" value="${v}" readonly=readonly style="width:100%"/>`;
			}
			else if(pdf.enum_val)
			{
				var opts = '' ;
				for(var j=0;j<pdf.enum_val.length;j++)
				{
					if(pdf.enum_val[j][0]==v)
						opts+="<option value='"+v+"' selected=selected>"+pdf.enum_val[j][1]+"</option>";
					else
						opts+="<option value='"+pdf.enum_val[j][0]+"'>"+pdf.enum_val[j][1]+"</option>";
				}
				rets += `<select id="si_${n}" propn="${n}" prop_tp="${pdf.type}" edit_plug="${editplug}" style="width:100%">${opts}</select>`;
			}
			else
			{
				if(pdf.multiline)
				{
					rets += `<textarea id="si_${n}" propn="${n}" prop_tp="${pdf.type}" edit_plug="${editplug}">${v}</textarea>`;
				}
				else
				{
					var tp = pdf.type ;
					var inptp = "text";
					var inp_step = "1" ;
					if("int"==tp)
					{
						inptp = "number" ;
					}
					else if("number"==tp||"float"==tp)
					{
						inptp = "number" ;
						inp_step = "0.1";
					}
					
					rets += `<input type="${inptp}" propn="${n}" prop_tp="${pdf.type}" edit_plug="${editplug}" step="${inp_step}" id="si_${n}" value="${v}" style="width:100%"/>`;
				}
			}
			rets += `</td><td style="width:20%">`
			if(pdf.binder)
			{
				if(pb==null)
				{
					rets += `<div id="pi_bd_${n}"  propn="${n}" style="border:solid 1px;width:100%;height:100%;">bind</div>`;
				}
				else
				{
					if(pb.isValid())
						rets += `<div id="pi_bd_${n}" propn="${n}" style="width:100%;height:100%;"><span style="background-color:green">bind ok</span></div>`;
					else
						rets += `<div id="pi_bd_${n}" propn="${n}" style="width:100%;height:100%;"><span style="background-color:red">bind err</span></div>`;
				}
			}
			rets += `</td></tr>` ;
			return rets ;
		}

		public static createEventItemEditHtml(n:string,edf:any,v:EventBinder|null):string
		{
			var rets = `<tr>
			<td style="width:50%" class="" title="${n}">${edf.title}</td>
			<td style="width:50%">`;

			var tp = edf.evt_tp ;
			
			if(v==null)
			{
				rets += `<div id="ei_${n}"  eventn="${n}" evt_tp="${tp}" style="width:100%;height:100%;">&nbsp;</div>`;
			}
			else
			{
				var tmps = "" ;
				if(v.hasClientJS())
					tmps += `&nbsp;<span style="background-color:green">client</span>`;
				if(v.hasServerJS())
					tmps += `&nbsp;<span style="background-color:green">server</span>`;
				rets += `<div id="ei_${n}" eventn="${n}" evt_tp="${tp}" style="width:100%;height:100%;">${tmps}</div>`;
				//if(v.isValid())
				//	rets += `<div id="ei_${n}" eventn="${n}" evt_tp="${tp}" style="width:100%;height:100%;"><span style="background-color:green">bind ok</span></div>`;
				//else
				//	rets += `<div id="ei_${n}" eventn="${n}" evt_tp="${tp}" style="width:100%;height:100%;"><span style="background-color:red">bind err</span></div>`;
			}
			
			rets += `</td></tr>`
			return rets ;
		}

		public static readPropItemStr(n:string,pdf:any):any
		{
			if (n.indexOf("_") == 0)
				return null;
			var editi = $("#si_"+n);
			if(editi==null||editi==undefined)
				return null ;
			return editi.val() ;
		}
		
		private onItemSelected(item:DrawItem|null)
		{//alert(1) ;
			this.selectedItem = item;
			if(item==null)
			{
				$(this.tarEditPropEle).html("") ;
				$(this.tarEditEvtEle).html("") ;
				return ;
			}
			var tmpid = oc.util.create_new_tmp_id();
			var tmps = `Type:${item.getClassName()}<br><div id="proppanel_${tmpid}" class="prop_edit_panel">` ;
			var binds='' ;
		
			var propdefs=item.getPropDefs();
			for(var i = 0 ; i < propdefs.length ; i ++)
			{
				var edef = propdefs[i] ;
				var catn = edef._cat_name ;
				var catt = edef._cat_title;
				//tmps += `<div class="oc_edit_cat"></div>`;
				tmps += `<table class="pi_edit_table">
    				<tr><td colspan="3" class="td_left" style="font-weight: bold;color: #000000;background-color: #f0f0f0">${catt}</td></tr>`;
				for(var n in edef)
				{
					if(n.indexOf('_')==0)
						continue;
					var df = edef[n] ;
					
					var v = item[n] ;
					if(v==null)
						v = '' ;
					var pb = item.getPropBinder(n) ;
					tmps += DrawEditor.createPropItemEditHtml(n,df,v,pb);
				}
				tmps += "</table>";
			}
			tmps += "</div>";

			$(this.tarEditPropEle).html(tmps) ;

			tmps = `<div id="eventpanel_${tmpid}" class="prop_edit_panel">` ;
			var eventdefs=item.getEventDefs() ;
			for(var i = 0 ; i < eventdefs.length ; i ++)
			{
				var edef = eventdefs[i] ;
				var catn = edef._cat_name ;
				var catt = edef._cat_title;
				//tmps += `<div class="oc_edit_cat"></div>`;
				tmps += `<table class="pi_edit_table">
    				<tr><td colspan="2" class="td_left" style="font-weight: bold;color: #000000;background-color: #f0f0f0">${catt}</td></tr>`;
				for(var n in edef)
				{
					if(n.indexOf('_')==0)
						continue;
					var df = edef[n] ;
					
					var v = item.getEventBinder(n) ;
					tmps += DrawEditor.createEventItemEditHtml(n,df,v);
				}
				tmps += "</table>";
			}
			tmps += "</div>";

			$(this.tarEditEvtEle).html(tmps)
			$(`#proppanel_${tmpid} input,select`).on('input',(e)=>{
				var tar = $(e.target) ;
				var n = tar.attr("propn") ;
				if(!n)
					return ;
				var v = ""+tar.val() ;
				//this.applyUI2SelectedItem();
				this.applySinglePV2SelectedItem(n,v) ;
			});

			var thiz = this ;

			$(`#proppanel_${tmpid} input,select`).on('click',function(e){
				
				var pn = $(this).attr("propn");
				//var tp = $(this).attr("prop_tp");
				var editplug = $(this).attr("edit_plug");
				//if(editplug!=undefined&&editplug!=null&&editplug!="")
				//	tp = editplug;
				if(pn!=null&&pn!=undefined&&pn!=""&&editplug&&editplug!="")// && beditplug)
				{
					var v = item[pn];
					//console.log("prop clk=",item,pn);
					if(thiz.plugCB!=null)
						thiz.plugCB($(this),"prop_"+editplug,item,pn,v);
				}
				
			});

			$(`#eventpanel_${tmpid} div`).on('click',function(e){
				var en = $(this).attr("eventn");
				var evttp = $(this).attr("evt_tp") ;
				if(en&&evttp)
				{
					var eb = item.getEventBinder(en) ;
					//console.log("event clk=",item,en,eb);
					if(thiz.plugCB!=null)
						//thiz.plugCB($(this),"event_"+evttp,item,en,eb);
						thiz.plugCB($(this),"event_bind",item,en,eb);
				}
			});


			$(`#proppanel_${tmpid} div`).on('click',function(e){
				var pn = $(this).attr("propn");
				
				if(pn!=null&&pn!=undefined&&pn!="")
				{
					var pb = item.getPropBinder(pn) ;
					if(thiz.plugCB!=null)
						thiz.plugCB($(this),"prop_bind",item,pn,pb);
				}
			});
		}

		/**
		 * call by plugCB
		 */
		public refreshEventEditor()
		{
			if(this.selectedItem==null)
				return ;
			var eventdefs=this.selectedItem.getEventDefs() ;
			for(var evtdef of eventdefs)
			{
				for(var n in evtdef)
				{
					if(n.indexOf('_')==0)
						continue;
					
					var pdf = evtdef[n] ;
					
					var v = this.selectedItem.getEventBinder(n) ;
					if(v==null)
					{
						$("#ei_"+n).html("&nbsp;") ;
					}
					else
					{
						var tmps = "" ;
						if(v.hasClientJS())
							tmps += `&nbsp;<span style="background-color:green">client</span>`;
						if(v.hasServerJS())
							tmps += `&nbsp;<span style="background-color:green">server</span>`;
						$("#ei_"+n).html(tmps);
						//if(v.isValid())
						//	$("#ei_"+n).html(`<span style="background-color:green">bind ok</span>`);
						//else
						//	$("#ei_"+n).html(`<span style="background-color:red">bind err</span>`);
					}
				}
			}
		}

		//call by plugCB
		public refreshPropBindEditor()
		{
			if(this.selectedItem==null)
				return ;
			var propdefs=this.selectedItem.getPropDefs() ;
			for(var propdef of propdefs)
			{
				for(var n in propdef)
				{
					if(n.indexOf('_')==0)
						continue;
					
					var pdf = propdef[n] ;
					
					var v = this.selectedItem.getPropBinder(n) ;
					if(v==null)
					{
						$("#pi_bd_"+n).html("bind") ;
					}
					else
					{
						if(v.isValid())
							$("#pi_bd_"+n).html(`<span style="background-color:green">bind ok</span>`);
						else
							$("#pi_bd_"+n).html(`<span style="background-color:red">bind err</span>`);
					}
				}
			}
		}

		public static transUI2PropByPdf(pdef:any):base.Props<any>
		{
			let r:base.Props<any> = {} ;
			for (var n in pdef)
			{
				var v = DrawEditor.readPropItemStr(n,pdef);
				if(v==undefined||v==null)
					continue ;
				var def = pdef[n];
				v = DrawItem.transStr2Val(def["type"],v,null);
				r[n] = v;
			}
			return r;
		}

		public static transUI2PropByPdfs(pdefs:any[]):base.Props<any>
		{
			let r:base.Props<any> = {} ;
			for (var i = 0; i < pdefs.length; i++)
			{
				var pdef = pdefs[i];
				for (var n in pdef)
				{
					var v = DrawEditor.readPropItemStr(n,pdef);
					if(v==undefined||v==null)
						continue ;
					var def = pdef[n];
					v = DrawItem.transStr2Val(def["type"],v,null);
					r[n] = v;
				}
			}
			return r ;
		}

		public transUI2Prop():base.Props<any>|null
		{
			if(this.selectedItem==null)
				return null ;
			var pdefs = this.selectedItem.getPropDefs();
			return DrawEditor.transUI2PropByPdfs(pdefs);
		}

		public applyUI2SelectedItem():boolean
		{
			if(this.selectedItem==null)
				return false ;

			let p = this.transUI2Prop();
			if(p==null)
				return false;
			this.selectedItem.inject(p,true);
			//this.selectedItem.update_draw();
			return true;
		}

		public applySinglePV2SelectedItem(n:string,v:string):boolean
		{
			if(this.selectedItem==null)
				return false ;
			var def = this.selectedItem.findProDefItemByName(n)
			if(def==null)
				return false;
			var objv = DrawItem.transStr2Val(def["type"],v,null);
			if(objv==null)
				return false;
			let r:base.Props<any> = {} ;
			r[n]=objv ;
			this.selectedItem.inject(r,true);
			return true ;
		}
	}
}



