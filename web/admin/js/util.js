
function chk_var_name(n,letter_first)
{
	if(n==null||n==''||n==undefined)
		return false
	var c = n.length ;
	var fc = n[0] ;
	if(letter_first)
	{
		var b = fc>='a'&&fc<='z' || fc>='A'&&fc<='Z'
		if(!b)
			return false;
	}
	for(var i=0;i<c;i++)
	{
		fc = n[i] ;
		var b = fc>='a'&&fc<='z' || fc>='A'&&fc<='Z' || fc>='0'&&fc<='9' || fc=='_' ;
		if(!b)
			return false;
	}
	return true ;
}

function chk_var_path(path,letter_first)
{
	if(!path)
		return false;
	var ps = path.split(".") ;
	for(var p of ps)
	{
		if(!chk_var_name(p,letter_first))
			return false;
	}
	return true;
}

function get_input_val(id,defv,bnum)
{
	var n = $('#'+id).val();
	if(n==null||n=='')
	{
		return defv ;
	}
	if(bnum)
		return parseInt(n);
	return n;
}