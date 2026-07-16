
function DTPlugItem(opt)
{
	this.name=opt.name ;
	this.title_cn = opt.title_cn ;
	this.title_en = opt.title_en ;
	this.icon = opt.icon ;
	this.width = opt.width ;
	this.url = opt.url ;
	this.pos = opt.pos||1 ;
	
	this.getTitle=function(lan)
	{
		if("cn"==lan)
			return this.title_cn ;
		else
			return this.title_en ;
	}
}

var BAR_WIDTH = 50;

function DTPlugs(panel,treeid,lan)
{
	this.treeid = treeid ;
	this.panel = panel ;
	this.lan = lan||"en";
	
	this.left_bar = $("#left_bar").css("width",BAR_WIDTH+"px");
	this.left_div = $("#left");
	this.mid_div = $("#mid") ;
	this.right_bar = $("#right_bar");
	this.right_div = $("#right");
	
	this.leftItems=[];
	this.midItems=[];
	this.rightItems=[];
	
	this.curLeftItem = null ;
	this.curRightItem = null ;
	
	this.curNode = null ;
	
	this.set_plug_item=function(opt)
	{
		switch(opt.pos)
		{
		case 1: //left
			this.leftItems.push(new DTPlugItem(opt))
			break;
		case 2://mid
			this.midItems.push(new DTPlugItem(opt))
			break;
		case 3://right
			this.rightItems.push(new DTPlugItem(opt))
			break;
		}
	}
	
	this.get_plug_item=function(n)
	{
		let items = this.leftItems ;
		for(let item of items)
		{
			if(item.name==n)
				return item;
		}
		items=  this.midItems;
		for(let item of items)
		{
			if(item.name==n)
				return item;
		}
		items = this.rightItems ;
		for(let item of items)
		{
			if(item.name==n)
				return item;
		}
		return null;
	}
	
	this.init_left_mid_right=function()
	{
		let left_icos = "" ;
		for(let item of this.leftItems)
		{
			left_icos += `<div id="left_ico_${item.name}" class="bar_item" title="${item.title}" onclick="dt_plugs.show_item('${item.name}',this)">${item.icon}</div>` ;
		}
		this.left_bar.html(left_icos) ;
		
		let right_icos = "" ;
		for(let item of this.rightItems)
		{
			right_icos += `<div id="right_ico_${item.name}" class="bar_item" title="${item.title}" onclick="dt_plugs.show_item('${item.name}',this)">${item.icon}</div>` ;
		}
		this.right_bar.html(right_icos) ;
	}

	this.init=function()
	{
		this.set_plug_item({pos:1,name:"tags",title_en:"Local Project Tags",title_cn:"本地项目标签",icon:"<i class='fa fa-tags'></i>"
			,width:450,url:"plugs/tn_runtags.jsp?treeid="+this.treeid});
		this.set_plug_item({pos:1,name:"staticdata",title_en:"Static Data",title_cn:"静态数据",icon:"<i class='fa-regular fa-file-lines'></i>"
			,width:450,url:"plugs/tn_staticdata.jsp?treeid="+this.treeid});
		//this.set_plug_item({pos:1,name:"links",title:"Related Link URL",icon:"<i class='fa fa-link'></i>"
		//	,width:300,url:"plugs/tn_links.jsp?treeid="+this.treeid});
		this.set_plug_item({pos:3,name:"runblks",title_en:"Node Run Blocks",title_cn:"运行模块",icon:"<i class='fa-solid fa-gear'></i>"
			,width:600,url:"plugs/tn_runblks.jsp?treeid="+this.treeid});
		this.set_plug_item({pos:3,name:"props",title_en:"Node Properties",title_cn:"节点属性",icon:"<i class='fa-solid fa-table-list'></i>"
			,width:300,url:"util_props.jsp?treeid="+this.treeid});
		
		this.init_left_mid_right();
		
		this.hide_item(1);
		this.hide_item(2);
		this.hide_item(3);
	}
	
	
	this.show_item = function(n,db_hide)
	{
		let pi = null;
		if(n)
		{
			pi = this.get_plug_item(n) ;
			if(!pi) return ;
		}
		if(db_hide==undefined||db_hide==null)
			db_hide = true ;
		this.show_pi_item(pi,db_hide) ;
	}
	
	this.show_pi_item=function(pi,db_hide)
	{
		if(!pi) return;
		if(pi.pos==1)
		{//left
			let lft = $("#left") ;
			if(db_hide&&this.curLeftItem == pi)
				pi = null ;//close
			
			if(pi)
			{
				lft.find('.hd_t').html(pi.getTitle(this.lan)) ;
				lft.find(".if").attr("src",pi.url+"&tree_nid="+(this.curNode?this.curNode.id:"")) ;
				this.left_bar.find('.bar_item').removeClass("seled") ;
				$("#left_ico_"+pi.name).addClass("seled") ;
				this.left_div.css("display","").css("width",pi.width+"px");
				this.mid_div.css("left",(pi.width+BAR_WIDTH)+"px") ;
				this.panel.updatePixelSize() ;
				this.curLeftItem = pi ;
			}
			else
			{
				this.hide_item(1)
			}
			
		}
		else if(pi.pos==2)
		{//mid
			
		}
		else if(pi.pos==3)
		{//right
			let lft = $("#right") ;
			if(db_hide&&this.curRightItem == pi)
				pi = null ;//close
			if(pi)
			{
				lft.find('.hd_t').html(pi.getTitle(this.lan)) ;
				lft.find(".if").attr("src",pi.url+"&tree_nid="+(this.curNode?this.curNode.id:"")) ;
				this.right_bar.find('.bar_item').removeClass("seled") ;
				$("#right_ico_"+pi.name).addClass("seled") ;
				this.right_div.css("display","").css("width",pi.width+"px");
				this.mid_div.css("right",(pi.width+BAR_WIDTH)+"px") ;
				this.panel.updatePixelSize() ;
				this.curRightItem = pi ;
			}
			else
			{
				this.hide_item(3)
			}
		}
	}
	
	this.hide_item=function(pos)
	{
		if(pos==1)
		{//left
			let lft = $("#left") ;

			this.left_bar.find('.bar_item').removeClass("seled") ;
			this.left_div.css("display","none");
			this.mid_div.css("left",BAR_WIDTH+"px") ;
			this.panel.updatePixelSize() ;
			this.curLeftItem = null ;
		}
		else if(pos==2)
		{//mid
			
		}
		else if(pos==3)
		{//right
			let lft = $("#right") ;
			this.right_bar.find('.bar_item').removeClass("seled") ;
			this.right_div.css("display","none");
			this.mid_div.css("right",BAR_WIDTH+"px") ;
			this.panel.updatePixelSize() ;
			this.curRightItem = null ;
		}
	}
	
	
	this.on_tree_node_seled=function(nd)
	{//console.log(nd);
		this.curNode=nd;
		if(this.curLeftItem)
			this.show_pi_item(this.curLeftItem,false) ;
		if(this.curRightItem)
			this.show_pi_item(this.curRightItem,false) ;
	}
}