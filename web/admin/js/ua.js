



function hmi_del(path,ok_cb)
{
var pm = {
			type : 'post',
			url : "/admin/ua/hmi_ajax.jsp",
			data :{path:path,op:'del'}
		};
	if(dlg.confirm("delete this ui?",null,()=>{
		$.ajax(pm).done((ret)=>{
			if(ret!='succ')
			{
				dlg.msg(ret.err) ;
				return ;
			}
			if(ok_cb)
				ok_cb() ;
		}).fail(function(req, st, err) {
			dlg.msg(err);
		});
	})) ;
}

function hmi_add_edit(bedit,path,ok_cb)
{
	var u = "/admin/ua/hmi_edit.jsp?ppath="+path;
	var t = "Add HMI";
	var op="add" ;
	if(bedit)
	{
		t = "Edit HMI";
		op="edit" ;
		u = "/admin/ua/hmi_edit.jsp?hmi_path="+path;
	}
	
	dlg.open(u,{title:t,w:'500px',h:'400px'},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 ret.op=op;
						 ret.path = path;
						 send_ajax('/admin/ua/hmi_ajax.jsp',ret,function(bsucc,ret)
							{
								if(!bsucc || ret.indexOf('succ')<0)
								{
									dlg.msg(ret);
									return ;
								}
								//dlg.msg(ret);
								dlg.close();
								if(ok_cb)
									ok_cb() ;
								refresh_ui();
							},false);
							
						 
						 //document.location.href=document.location.href;
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function tagg_del(path,ok_cb)
{
	var pm = {
			type : 'post',
			url : "/admin/ua/tagg_ajax.jsp",
			data :{path:path,op:'del'}
		};
	if(dlg.confirm("delete this tagg?",null,()=>{
		$.ajax(pm).done((ret)=>{
			if(ret!='succ')
			{
				dlg.msg(ret.err) ;
				return ;
			}
			if(ok_cb)
				ok_cb();
			
		}).fail(function(req, st, err) {
			dlg.msg(err);
		});
	})) ;
}


function tagg_add_edit(bedit,path,ok_cb)
{
	var u = "/admin/ua/tagg_edit.jsp?ppath="+path;
	var t = "Add TagGroup";
	var op="add" ;
	if(bedit)
	{
		t = "Edit TagGroup";
		op="edit" ;
		u = "/admin/ua/tagg_edit.jsp?tagg_path="+path;
	}
	
	dlg.open(u,{title:t,w:'500px',h:'400px'},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 ret.op=op;
						 ret.path = path;
						 send_ajax('/admin/ua/tagg_ajax.jsp',ret,function(bsucc,ret)
							{
								if(!bsucc || ret.indexOf('succ')<0)
								{
									dlg.msg(ret);
									return ;
								}
								//dlg.msg(ret);
								dlg.close();
								if(ok_cb)
									ok_cb() ;
								refresh_ui();
							},false);
							
						 
						 //document.location.href=document.location.href;
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}
