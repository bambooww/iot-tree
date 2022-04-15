<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.eclipse.milo.opcua.stack.core.types.structured.*,
				org.eclipse.milo.opcua.stack.core.types.builtin.*,
				org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.*,
				org.eclipse.milo.opcua.sdk.client.nodes.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"
	%><script>

	function tree_init()
	{
			$.jstree.destroy();
			this.jsTree = $('#list_tree').jstree(
					{
						'core' : {
							'data' : {
								'url' : function(node){
									var pnid = "" ;
									if(node)
										pnid = node.id ;
			                    	return "cpt_bind_ajax.jsp?prjid="+prjid+"&op=treen&cpid="+cpid+"&cptp="+cptp+"&connid="+connid+"&pnid="+pnid;
			                    },
								"dataType" : "json",
								"data":function(node){
			                        return {"id" : node.id};
			                    }
							},
							'check_callback' : function(o, n, p, i, m) {
								if(m && m.dnd && m.pos !== 'i') { return false; }
								if(o === "move_node" || o === "copy_node") {
									if(this.get_node(n).parent === this.get_node(p).id) { return false; }
								}
								return true;
							},
							'themes' : {
								'responsive' : false,
								'variant' : 'small',
								'stripes' : true
							}
						},
						'contextmenu' : { //
							
							'items' :(node)=>{
								//this.get_type(node)==='ch''
								//console.log(node)
								var tp = node.original.type
								//console.log(tp) ;
								return this.get_cxt_menu(tp,node.original) ;
			                }
						},
						'types' : {
							'default' : { 'icon' : 'folder' },
							'file' : { 'valid_children' : [], 'icon' : 'file' }
						},
						'unique' : {
							'duplicate' : function (name, counter) {
								return name + ' ' + counter;
							}
						},
						'plugins' : ['state','dnd','types','contextmenu','unique']
					}
			)
			
			this.jsTree.on('activate_node.jstree',(e,data)=>{
				on_tree_node_sel(data.node.original)
			})
	}


	var cur_sel_node = null ;

	function on_tree_node_sel(n)
	{
		//"prjid="+prjid+"&op=sub_nodes&connid="+connid+"&nodeid="+n.id
		cur_sel_node = n ;
		console.log(n) ;
	}

	function get_sel_tree_nodes()
	{
		var tns = $('#list_tree').jstree(true).get_selected(true);
		var rets=[] ;
		for(var tn of tns)
		{
			rets.push(tn.original) ;
		}
		return rets;
	}


	function tree_get_left_vals()
	{
		var tns = get_sel_tree_nodes();
		if(tns.length<=0)
		{
			return [];
		}
		var ret=[];
		for(var tn of tns)
		{
			if(tn.tp!='tag')
			{
				continue;
			}
			
			ret.push(tn.id+":"+tn.vt) ;
		}
		return ret;
	}


	function get_map_vals()
	{
		var r = "" ;
		var bfirst = true;
		$("#tb_bind_map tr").each(function(){
			var bp = $(this).attr("bindp") ;
			var tp =  $(this).attr("tagp") ;
			if(bp && tp)
			{
				if(bfirst) bfirst=false;
				else r += "|" ;
				r+= tp+'='+bp ;
			}
		});
		return r ;
	}


	//tree_init();
/*
	function show_tree(brefresh)
	{
		dlg.loading(true) ;
		send_ajax("cpt_bind_ajax.jsp",{prjid:prjid,op:"tree",list:b_list,cpid:cpid,cptp:cptp,connid:connid,refresh:brefresh},function(bsucc,ret){
			dlg.loading(false) ;
		//	console.log("ret len="+ret.length) ;
			if(!bsucc||ret.indexOf("{")!=0)
			{
				dlg.msg(ret) ;
				return ;
			}
			var tree = $('#list_tree');
			var ob = null;
			eval("ob="+ret) ;
			tree.jstree(true).settings.core.data = ob;
			tree.jstree(true).refresh();
		});
		
	}
		*/
	//show_tree(false);

	</script>