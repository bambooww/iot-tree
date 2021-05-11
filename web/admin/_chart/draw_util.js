
var log = {} ;
log.error = function(e)
{
}

var draw_debug_f = null;
function draw_debug_line(s)
{
	if(draw_debug_f!=null)
		draw_debug_f(s) ;
}

Array.prototype.remove=function(dx)
{
	if(isNaN(dx)||dx>this.length){return false;}
	for(var i=0,n=0;i<this.length;i++)
	{
		if(this[i]!=this[dx])
		{
			this[n++]=this[i]
		}
	}
	this.length-=1
}

Array.prototype.setNoExist=function(o)
{
	for(var i = 0 ;i < this.length;i++)
		if(this[i]==o)
			return ;
		
	this.push(o) ;
}

Array.prototype.getFirst=function()
{
	if(this.length<=0)
		return null ;
	return this[0] ;
}

Array.prototype.getLast=function()
{
	if(this.length<=0)
		return null ;
	return this[this.length-1] ;
}

Array.prototype.addAll=function(oas)
{
	if(oas==null)
		return ;
	for(var i=0; i<oas.length ; i++)
		this[this.length] = oas[i] ;
}

oc._tmpid=0;



oc.create_new_tmp_id=function()
{
	this._tmpid++ ;
	var d = new Date() ;
	var tmps = 'x' ;
	tmps += d.getFullYear() ;
	var i = d.getMonth() ;
	if(i<10)
		tmps += '0'+i ;
	else
		tmps += i ;
	i = d.getDay() ;
	if(i<10)
		tmps += '0'+i ;
	else
		tmps += i ;
	i=d.getHours();
	if(i<10)
		tmps += '0'+i ;
	else
		tmps += i ;
	i=d.getMinutes();
	if(i<10)
		tmps += '0'+i ;
	else
		tmps += i ;
	i=d.getSeconds() ;
	if(i<10)
		tmps += '0'+i ;
	else
		tmps += i ;
	tmps += "_"+this._tmpid ;
	return tmps ;
	//return "id_"+scada_tmpid ;
}


function trim(n)
{
if(n==null)
	return null ;

return n.replace(/(^\s+)|\s+$/g,'') ;
}

oc.util.drawRect=function(cxt, x, y, width, height, radius,fillColor,borderw,bordercolor)
{
    //   
    //if (2 * radius > width || 2 * radius > height) { return false; }

    cxt.save();
    cxt.translate(x, y);
    //cxt.scale(1, -1);//chg
    
    if(fillColor)
    {
    	//bordercolor;
    	cxt.strokeStyle =cxt.fillStyle = fillColor || "#000";
    	if(radius!=null)
    		this.drawRoundRectPath(cxt, width, height, radius);
    	else
    		//cxt.strokeRect(0,0,width,height);
    		this.drawRectPath(cxt,width,height) ;
        cxt.fill();
    }
    
    if(bordercolor || !fillColor || borderw>0) //&&borderw>0)
    {
    	cxt.lineWidth =borderw||1 ; 
        cxt.strokeStyle =bordercolor|| "#000";
        if(radius!=null)
    		this.drawRoundRectPath(cxt, width, height, radius);
    	else
    		this.drawRectPath(cxt,width,height) ;//cxt.strokeRect(0,0,width,height);
    }
    
    //this.strokeRoundRect=function(cxt, x, y, width, height, radius, lineWidth,strokeColor)
    
    cxt.stroke();
    cxt.restore();
}


oc.util.drawRoundRectPath=function(cxt, width, height, radius)
{//private
	//cxt.strokeStyle = "#000";
    cxt.beginPath(0);
    cxt.arc(width - radius, height - radius, radius, 0, Math.PI / 2);

    cxt.lineTo(radius, height);
    cxt.arc(radius, height - radius, radius, Math.PI / 2, Math.PI);
    cxt.lineTo(0, radius);
    cxt.arc(radius, radius, radius, Math.PI, Math.PI * 3 / 2);
 
    cxt.lineTo(width - radius, 0);
    cxt.arc(width - radius, radius, radius, Math.PI * 3 / 2, Math.PI * 2);
    cxt.lineTo(width, height - radius);
    cxt.closePath();
}

oc.util.drawRectPath=function(cxt, width, height)
{//private
	//cxt.strokeStyle = "#000";
    cxt.beginPath(0);
    cxt.lineTo(0,0);
    cxt.lineTo(width,0);
    cxt.lineTo(width,height);
    cxt.lineTo(0,height);
    cxt.lineTo(0,0);
    cxt.closePath();
}

