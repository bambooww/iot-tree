

oc.editor.DrawEditor=function(target,panel,opts)
{
	this.tarEditEle = document.getElementById(target) ;
	this.drawPanel = panel ;
}

oc.editor.DrawEditor.prototype.init_editor=function()
{
	
}



oc.editor.DrawEditor.prototype.onItemSelected=function(item)
{//alert(1) ;
	if(item==null)
	{
		$(this.tarEditEle).html("") ;
		return ;
	}
	var tmps = 'Type:'+item.getClassName()+'<br>' ;
	var binds='' ;

	var propdefs=item.getPropDefs();
	for(var i = 0 ; i < propdefs.length ; i ++)
	{
		var propdef = propdefs[i] ;
		var catn = propdef._cat_name ;
		var catt = propdef._cat_title;
		tmps += catt +"<br>";
		for(var n in propdef)
		{
			if(n.indexOf('_')==0)
				continue;
			var pdf = propdef[n] ;
			
			var v = item[n] ;
			if(v==null)
				v = '' ;
			
			if(pdf.readonly)
			{
				tmps += pdf.title+'<input type=text id=si_'+n+' value='+v+' readonly=readonly/><br>';
				continue ;
			}
			
			if(pdf.enum_val)
			{
				var opts = '' ;
				for(var j=0;j<pdf.enum_val.length;j++)
				{
					if(pdf.enum_val[j][0]==v)
						opts+="<option value='"+v+"' selected=selected>"+pdf.enum_val[j][1]+"</option>";
					else
						opts+="<option value='"+pdf.enum_val[j][0]+"'>"+pdf.enum_val[j][1]+"</option>";
				}
				tmps += pdf.title+'<select id=si_'+n+'>'+opts+'</select><br>';
			}
			else
			{
				if(pdf.multiline)
				{
					tmps += pdf.title+'<textarea id=si_'+n+'>'+v+'</textarea><br>';
				}
				else
					tmps += pdf.title+'<input type=text id=si_'+n+' value=\''+v+'\' /><br>';
			}

		}

	}
		
	
	
//	var e_str="" ;
//	var ens = cur_sel_item.getDefEvents() ;
//	if(ens!=null)
//	{
//		for(var n in ens)
//		{
//			var en = ens[n]
//			var es = cur_sel_item.getEventBinderByName(n) ;
//			e_str +=en.title+":"+en.type+"[<span id='eshow_"+n+"'>"+es+"</span>]<input type=button value='.' id=ei_"+n+" onclick=\"set_bind_event('"+n+"')\"/><br>";
//		}
//	}
	
	
	//var info = item.getItemInfo() ;
	//if(info!=null)
	//	tmps+="<br>"+info.title+":"+info.desc ;

	$(this.tarEditEle).html(tmps) ;
	//alert(binds) ;
	//document.getElementById('dyn_binds').innerHTML = binds ;
	
	//document.getElementById('event_binds').innerHTML = e_str ;
}


