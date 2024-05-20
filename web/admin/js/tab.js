(function($) {
	this.id2cb={};
	function initTab($tab) {
		$tab.addClass("tab")
			.children("ul").addClass("tab-header")
			.append($("<li></li>").addClass("tab-header-items").css("width", $tab.outerWidth() - 25).append("<ul></ul>"))
			.append($("<li></li>").addClass("tab-header-select tab-header-select-down").css("title", "show hiddle").append("<ul class='hide-title-list'></ul>"));
		$tab.append($("<ul></ul>").addClass("tab-overflow-items")).children("div").addClass("tab-content");
	}

	function initContentHeight($tab) {
		$tab.css("height", ($tab.parent().height() - 2) + "px")
			.find(".tab-content").css("height", ($tab.parent().height() - 35) + "px");
	}

	function initEvents($tab) {
		$tab
		.delegate(".tab-header-item", "click", function(){
			var selected = $(this).hasClass("tab-header-selected");
			if (!selected) {
				selectTab($tab, $(this).attr("target"));
			}
		})
		.delegate(".tab-header-select", "click", function(){
			if($(this).hasClass("tab-header-select-down") && $(this).children().children().length > 0) {
				$(this).removeClass("tab-header-select-down").addClass("tab-header-select-up");
				$(this).children().css("display", "block");
			} else {
				$(this).removeClass("tab-header-select-up").addClass("tab-header-select-down");
				$(this).children().css("display", "none");
			}
		})
		.delegate(".tab-header-select ul li", "click", function(){
			var tabId = $(this).attr("item-id");
			selectTab($tab, tabId);
		})
		.delegate(".close", "click", function() {
			var tabId = $(this).parent().attr("target");
			removeTab($tab, tabId);
		});
	}

	function initContextMenu() {
		var contextMenu = $("body .tab-contextmenu");
		if (!contextMenu[0]) {
			$("<div class='tab-contextmenu'></div>")
				.append(createContextMenuItem("关闭当前标签", "current"))
				.append(createContextMenuItem("关闭左侧标签", "prevAll"))
				.append(createContextMenuItem("关闭右侧标签", "nextAll"))
				.append(createContextMenuItem("关闭其他", "other"))
				.append(createContextMenuItem("关闭全部", "all"))
				.appendTo("body");
		}
		function createContextMenuItem(text, target) {
			return $("<div class='tab-contextmenu-item'></div>").html(text).attr("target", target);
		}
	}

	function initWindowContextMenu() {
		$("body").bind("contextmenu", contextMenuHandler)
		
		.on("click", function(ev) {
		
			$(".tab-contextmenu").css("display", "none");
			
			var e = ev || window.event;
			var src = $(e.srcElement || e.target);
			if(!src.hasClass("tab-header-select"))
				closeSelect();
		});
	}

	function contextMenuHandler(ev) {

		closeSelect();

		var e = ev || window.event;

		var menu = $(".tab-contextmenu");

		src = $(e.srcElement || e.target);

		if (src.hasClass("tab-header-item")) {
			var tab = src.parentsUntil(".tab").parent();
			tab.tab("selectTab", src.attr("target"));
			e.preventDefault();

			menu
				.css({"left": e.clientX + 'px', "top": e.clientY + 'px', "display": "block"})
				.children().unbind("click").bind("click", function() {
					switch($(this).attr("target")){
						case 'current':
							return removeTabs(tab, src);
						case 'prevAll':
							return removeTabs(tab, src.prevAll());
						case 'nextAll':
							removeHideTitles(tab);
							return removeTabs(tab, src.nextAll());
						case 'other':
							removeHideTitles(tab);
							return removeTabs(tab, src.siblings());
						case 'all':
							removeHideTitles(tab);
							return removeTabs(tab, src.parent().children());
					}
				});
		} else {
			menu.css("display", "none");
		}
	}

	function addTab($tab, param) {
		if (isExists($tab, param["id"])) {
			selectTab($tab, param["id"]);
		} else{
			if(param['on_select'])
				this.id2cb[param["id"]]=param['on_select'];
			$tab.find(".tab-header-item").removeClass("tab-header-selected");
			$newHeaderItem = $("<li></li>");
			$newHeaderItem
				.html(param["title"])
				.attr("target", param["id"])
				.addClass("tab-header-item").addClass("tab-header-selected");
			
			//$newHeaderItem.append($("<span class=\"close\"></span>"));
			$newHeaderItem.appendTo($tab.find(".tab-header-items").children("ul"));

			$tab.children("div").children().addClass("hide");
			$newContentItem = $("<div></div>");
			$newContentItem
				.html(param["content"])
				.attr("id", param["id"])
				.addClass("tab-content-item")
				.appendTo($tab.children("div"));
			
			afterAddTab($tab);
		}
	}

	function afterAddTab($tab) {

		
		var headItem = $tab.find(".tab-header-items");
		var titles = headItem.find("ul").children();

		if(titles.length <= 1) return;

		var w1 = titles.eq(0).offset().left;
		var w2 = titles.eq(titles.length - 1).offset().left;
		
		if(w2 > w1) return;
		
		var headerWidth = headItem.outerWidth();
		var tmp = titles.eq(titles.length - 1).outerWidth(true);
		var maxVisible = 0;
		
		titles.each(function(i) {
			var w = $(this).outerWidth(true);
			tmp += w;
			if(tmp > headerWidth) {
				maxVisible = i;
				return false;
			}
		});
		
		var select = $tab.find(".tab-header-select ul");
		for(var j = maxVisible; j < titles.length - 1; j++) {
			var t = titles.eq(j);
			t.removeClass("tab-header-selected")
				.attr("real-width",  t.outerWidth())
				.appendTo($tab.find(".tab-overflow-items"));
			select.append("<li item-id='" + t.attr("target") + "'>" + t.text() + "</li>");
		}
		
	}
	
	function closeSelect() {
		if($(".tab-header-select").hasClass("tab-header-select-up")) {
			$(".tab-header-select")
				.removeClass("tab-header-select-up").addClass("tab-header-select-down")
				.children().css("display", "none");
		}
	}

	function removeHideTitles($tab) {
		$tab.find(".tab-overflow-items").children().remove();
		$tab.find(".hide-title-list").children().remove();
	}

	function addRemoteTab($tab, param) {

		addTab($tab, {"title": param["title"], "id": param["id"], "content": ""});
		$.ajax({
			type: param["method"] || "post",
			dataType: "html",
			url: param["url"],
			cache: false,
			success: function(data) {
				$tab.find("#" + param["id"]).html(data);
			}
		});
	}
	

	function removeTab($tab, tabId) {
		var headerItem = $tab.find(".tab-header-items ul").children("li[target="+ tabId +"]");
		var selected = headerItem.hasClass("tab-header-selected");
		var prevItem = headerItem.prev();
		var nextSelectedId = null;

		if (!prevItem[0])
			prevItem = headerItem.next();
		
		if (prevItem[0]) {
			nextSelectedId = prevItem.attr("target");
		} else {
			var hide1 = $tab.find(".hide-title-list li").eq(0);
			if (hide1[0]) nextSelectedId = hide1.attr("item-id");
		}

		headerItem.remove();
		$tab.children("div").children("#" + tabId).remove();

		var head = $tab.children("ul");
		var titles = head.find(".tab-header-items ul").children();
		
		var hideTitles = $tab.find(".tab-overflow-items").children();
		
		var visibleWidth = 0;
		
		titles.each(function() {
			visibleWidth += $(this).outerWidth();
		});
		
		var j;
		var headerWidth = head.children(".tab-header-items").outerWidth();
		for(j = 0; j < hideTitles.length; j++) {
			visibleWidth += parseInt(hideTitles.eq(j).attr("real-width"));
			if (visibleWidth >= headerWidth)
				break;
		}
		
		for(var k = 0; k < j; k++) {
			hideTitles.eq(k).appendTo($tab.find(".tab-header-items").children("ul"));
			$tab.find(".hide-title-list").children().eq(k).remove();
		}

		if (selected && nextSelectedId)
			selectTab($tab, nextSelectedId);
	}
	

	function removeTabs($tab, items) {
		items.each(function() {
			removeTab($tab, $(this).attr("target"));
		});
	}
	
	function selectTab($tab, tabId) {
		
		var headItems = $tab.find(".tab-header-items");
		var tab = headItems.find("li[target=" + tabId + "]");
		
		if(tab[0]) {
			tab.addClass("tab-header-selected")
				.siblings().removeClass("tab-header-selected");
		} else {
			tab = $tab.find(".tab-overflow-items").children("li[target=" + tabId + "]");
			if(tab[0]) {
				headItems.find("ul li").removeClass("tab-header-selected");
				tab.addClass("tab-header-selected").appendTo(headItems.children("ul"));
				$tab.find(".hide-title-list").children("li[item-id=" + tabId + "]").remove();
			}
		}

		$tab
			.find("#" + tabId).removeClass("hide")
			.siblings().addClass("hide");
		
		afterAddTab($tab);
		
		if($tab[0].__opt && $tab[0].__opt.on_selected_tab)
			$tab[0].__opt.on_selected_tab(tab.attr("target")) ;
			
	}

	function isSelected($tab, tabId) {
		return $tab.find("li[target=" + tabId + "]").hasClass("tab-header-selected");
	}

	function isExists($tab, tabId) {
		return $tab.find("li[target=" + tabId + "]")[0] != undefined;
	}

	$.fn.tab = function(options, param) {
		
		if (typeof options == 'string') {
			switch(options){
				case 'addTab':
					return this.each(function() {
						addTab($(this), param);
					});
				case 'addRemoteTab':
					return this.each(function() {
						addRemoteTab($(this), param);
					});
				case 'removeTab':
					return this.each(function() {
						removeTab($(this), param);
					});
				case 'selectTab':
					return this.each(function() {
						selectTab($(this), param);
					});
				case 'isSelected':
					return isSelected($(this), param);
				case 'isExists':
					return isExists($(this), param);
			}
		}
		
		options = options || {};

		return this.each(function() {
			var tab = $(this);
			tab[0].__opt = options ;
			initTab(tab);
			//initContextMenu();
			initContentHeight(tab);
			initEvents(tab);
			//initWindowContextMenu();
		});
	};
})(jQuery);