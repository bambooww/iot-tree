//geometry

oc.base.Rect=function()
{
	this.x ;
	this.y ;
	this.w ;
	this.h ;
	
	if(arguments.length==4)
	{
		this.x=arguments[0] ;
		this.y=arguments[1] ;
		this.w=arguments[2] ;
		this.h=arguments[3] ;
	}
	else if(arguments.length==1)
	{
		//alert(arguments[0] instanceof oc.base.Rect);
		//{
		this.x=arguments[0].x ;
		this.y=arguments[0].y ;
		this.w=arguments[0].w ;
		this.h=arguments[0].h ;
		//}
		
	}
}


oc.base.Rect.prototype.getMaxX=function()
{
	return this.x +this.w ;
}



oc.base.Rect.prototype.getMaxY=function()
{
	return this.y +this.h ;
}
	
oc.base.Rect.prototype.setMaxX=function(mx)
{
	if(mx<=this.x)
		throw "invalid max x" ;
	this.w = mx - this.x ;
}

oc.base.Rect.prototype.setMaxY=function(my)
{
	if(my<=this.y)
		throw "invalid max y" ;
	this.h = my - this.y ;
}


oc.base.Rect.prototype.isValid=function()
{
	return this.w>0||this.h>0 ;
}

oc.base.Rect.toStr=function()
{
	return "["+this.x+","+this.y+","+this.w+","+this.h+"]";
}
	
//expand by overlapped other rect
oc.base.Rect.prototype.expandBy=function(r)
{
	if(!r.isValid())
		return ;
	if(!this.isValid())
	{
		this.x = r.x;
		this.y = r.y;
		this.w = r.w;
		this.h = r.h;
		return ;
	}
	
	if(r.getMaxX()>this.getMaxX())
		this.setMaxX(r.getMaxX());
	if(r.getMaxY()>this.getMaxY())
		this.setMaxY(r.getMaxY());
	if(r.x<this.x)
		this.x = r.x ;
	if(r.y<this.y)
		this.y = r.y ;
}

oc.base.Rect.prototype.contains=function(X,Y)
{
	var w = this.w;
	var h = this.h;
	if (w < 0 || h < 0)
	{
		// At least one of the dimensions is negative...
		return false;
	}
	// Note: if either dimension is zero, tests below must return false...
	var x = this.x;
	var y = this.y;
	if (X < x || Y < y)
	{
		return false;
	}
	w += x;
	h += y;
	// overflow || intersect
	return ((w < x || w > X) && (h < y || h > Y));
}

oc.base.Rect.prototype.toStr=function()
{
	return "["+this.x+","+this.y+","+this.w+","+this.h+"]" ;
}

oc.base.Pts=function()
{
	Array.apply(this);

	if(arguments.length==1)
	{
		var pts = arguments[0] ;
		if(pts instanceof Array)
		{
			if(pts==null||pts.length<3)
				throw "pts num must >=3" ;
			for(var i = 0 ; i < pts.length ; i ++)
			{
				var pt = pts[i] ;
				if(pt instanceof Array)
					this.push({x:pt[0],y:pt[1]}) ;
				else
					this.push(pt) ;
			}
		}
	}
	else
	{
		for(var i = 0 ; i < arguments.length; i++)
		{
			var pt = arguments[i]
			if(pt instanceof Array)
				this.push({x:pt[0],y:pt[1]}) ;
			else
				this.push(pt) ;
		}
	}
	
	this.bound = null ;
	
}

oc.base.Pts.prototype=new Array();

oc.base.Pts.prototype.getFirst=function()
{
	if(this.length<=0)
		return null ;
	return this[0] ;
}

oc.base.Pts.prototype.getLast=function()
{
	if(this.length<=0)
		return null ;
	return this[this.length-1] ;
}

oc.base.Pts.prototype.addPt=function(x,y)
{
	this.push({x:x,y:y});
}

oc.base.Pts.prototype.calculateBounds=function()
{
	var boundsMinX = Number.MAX_VALUE;
	var boundsMinY = Number.MAX_VALUE;
	var boundsMaxX = Number.MIN_VALUE;
	var boundsMaxY = Number.MIN_VALUE;

	for (var i = 0; i < this.length; i++)
	{
		var p = this[i];
		var x = p.x;
		boundsMinX = Math.min(boundsMinX, x);
		boundsMaxX = Math.max(boundsMaxX, x);
		var y = p.y;
		boundsMinY = Math.min(boundsMinY, y);
		boundsMaxY = Math.max(boundsMaxY, y);
	}
	return new oc.base.Rect(boundsMinX, boundsMinY, boundsMaxX - boundsMinX, boundsMaxY - boundsMinY);
}

oc.base.Pts.prototype.getBoundingBox=function()
{
	if (this.length == 0)
	{
		return new oc.base.Rect();
	}
	if (this.bound == null)
	{
		this.bound = this.calculateBounds();
	}
	return this.bound;
}

oc.base.Pts.prototype.toStr=function()
{
	if (this.length <= 0)
		return "";
	var ret = "("+this[0].x+","+this[0].y+")";
	for (var i = 1; i < this.length; i++)
	{
		ret += ","+ "("+this[i].x+","+this[i].y+")";
	}
	return ret;
}

oc.base.Polygon=function()
{
	oc.base.Pts.apply(this,arguments);//super.constructor

}

oc.base.Polygon.prototype=new oc.base.Pts() ;

oc.base.Polygon.prototype.contains=function(x, y)
{
	if (this.length < 3 || !this.getBoundingBox().contains(x, y))
	{
		return false;
	}
	var hits = 0;

	var n = this.length - 1;
	var lastpt = this[n];
	var lastx = lastpt.x;
	var lasty = lastpt.y;
	var curx, cury;

	// Walk the edges of the polygon
	for (var i = 0; i < n; lastx = curx, lasty = cury, i++)
	{
		var p = this[i];
		curx = p.x;// xpoints[i];
		cury = p.y;// ypoints[i];

		if (cury == lasty)
		{
			continue;
		}

		var leftx;
		if (curx < lastx)
		{
			if (x >= lastx)
			{
				continue;
			}
			leftx = curx;
		}
		else
		{
			if (x >= curx)
			{
				continue;
			}
			leftx = lastx;
		}

		var test1, test2;
		if (cury < lasty)
		{
			if (y < cury || y >= lasty)
			{
				continue;
			}
			if (x < leftx)
			{
				hits++;
				continue;
			}
			test1 = x - curx;
			test2 = y - cury;
		}
		else
		{
			if (y < lasty || y >= cury)
			{
				continue;
			}
			if (x < leftx)
			{
				hits++;
				continue;
			}
			test1 = x - lastx;
			test2 = y - lasty;
		}

		if (test1 < (test2 / (lasty - cury) * (lastx - curx)))
		{
			hits++;
		}
	}

	return hits%2==1;//((hits & 1) != 0);
}