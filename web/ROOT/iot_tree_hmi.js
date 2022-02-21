"use strict";
var iot_tree;
(function (iot_tree) {
    class Hmi {
        constructor(opt) {
            this.url = opt.url||"";
            this.eleid=opt.eleid||"";
        }
        getUrl() {
            return this.url;
        }
        
        render()
        {
        	
        }
        
        init()
        {
        	this.hmiModel = new oc.hmi.HMIModel({
        		temp_url:"/hmi_ajax.jsp?op=load&path="+path,
        		comp_url:"/comp_ajax.jsp?op=comp_load",
        		hmi_path:path
        	});
        	
        	panel = new oc.hmi.HMIPanel(this.eleid,{
        		on_mouse_mv:on_panel_mousemv,
        		on_model_chg:on_model_chg
        	});
        	//editor = new oc.DrawEditor("edit_props","edit_events",panel,{
        	//	plug_cb:editor_plugcb
        	//}) ;
        	hmiView = new oc.hmi.HMIView(hmiModel,panel,null,{
        		copy_paste_url:"util/copy_paste_ajax.jsp",
        		show_only:true,
        		on_model_loaded:()=>{
        			//console.log("loaded") ;
        			draw_fit()
        		},
        		on_new_dlg:(p,title,w,h)=>{
        			var fp = p ;
        			if(p.indexOf("/")!=0)
        				fp = ppath+p ;
        			dlg.open(fp,
        					{title:title,w:w+'px',h:h+'px'},
        					['Cancel'],
        					[
        						function(dlgw)
        						{
        							dlg.close();
        						}
        					]);
        		},
        		on_new_win:(p)=>{
        			
        		}
        	});
        	
        	hmiView.init();
        	
        	loadLayer = hmiView.getLayer();
        	intedit = hmiView.getInteract();
        }
    }
    Hmi.PROP_BINDER = "_prop_binder";
    oc.Hmi = Hmi;
})(iot_tree || (iot_tree = {}));