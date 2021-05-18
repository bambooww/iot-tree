<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<%@ page import="java.util.*,
				java.io.*,
				java.util.*,
				java.net.*"%>
<%@ taglib uri="wb_tag" prefix="wbt"%><!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta name="viewport"
	content="maximum-scale=1.0,minimum-scale=1.0,user-scalable=0,width=device-width,initial-scale=1.0">
<meta name="format-detection"
	content="telephone=no,email=no,date=no,address=no">
<meta name="renderer" content="webkit">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title></title>
	<link type="text/css" href="inc/eda.css" rel="stylesheet" />
<style type="text/css">
#toolbar-drawSch {
    right: 210px;
    top: 105px;
    width: 68px;
}
#editor-news, .menu, .toolbar {
    -moz-box-shadow: 0 1px 6px rgba(0,0,0,.2);
    -webkit-box-shadow: 0 1px 6px rgba(0,0,0,.2);
    box-shadow: 0 1px 6px rgba(0,0,0,.2);
    border: 1px solid #ddd;
}
.toolbar {
    position: absolute;
    z-index: 1;
    background-color: #f9f9f9;
    box-sizing: content-box;
    -moz-box-sizing: content-box;
    -o-box-sizing: content-box;
    -safari-box-sizing: content-box;
    -webkit-box-sizing: content-box;
    border-radius: 4px 4px 0 0;
    overflow: hidden;
    border: 1px solid #d1d1d1;
}
user agent stylesheet
div {
    display: block;
}
.unselectable, body {
    -moz-user-select: none;
    -ms-user-select: none;
    -khtml-user-select: none;
    -webkit-user-select: none;
    user-select: none;
}
body, html {
    margin: 0;
    padding: 0;
    font-family: Arial,"Microsoft YaHei";
    font-size: 12px;
    background: #fff;
    overflow: hidden;
    color: #333;
    height: 100%;
}
</style>
</head>
<body>
	<div id="toolbar-drawSch" fordoctype="sch" class="toolbar"
		style="cursor: default; z-index: 5; position: absolute; left: auto; top: auto; right: 91px; bottom: 77px;">
		<div class="titlebar" style="cursor: default;">
			<span class="i18n" i18n="Drawing Tools">绘图工具</span>
			<div class="collapse icon-eda-fold"></div>
		</div>
		<div class="btns">
			<div title="图纸设置" cmd="setSheet" icon="icon-eda-sheet"
				class="toolbarbutton icon-eda-sheet" data-i18n-attr="title"></div>
			<div title="线条" cmd="draw(polyline)" icon="icon-eda-line"
				class="toolbarbutton icon-eda-line" data-i18n-attr="title"></div>
			<div title="贝塞尔曲线" cmd="draw(bezier)" icon="icon-eda-bezier"
				class="toolbarbutton icon-eda-bezier" data-i18n-attr="title"></div>
			<div title="圆弧" cmd="draw(arc)" icon="icon-eda-arc"
				class="toolbarbutton icon-eda-arc" data-i18n-attr="title"></div>
			<div title="箭头" cmd="place_part(arrowhead)" icon="icon-eda-arrow"
				class="toolbarbutton icon-eda-arrow" data-i18n-attr="title"></div>
			<div title="文本" cmd="place_part(annotation)" icon="icon-eda-text"
				class="toolbarbutton icon-eda-text" data-i18n-attr="title"></div>
			<div title="自由绘制" cmd="draw(freedraw)" icon="icon-eda-draw"
				class="toolbarbutton icon-eda-draw" data-i18n-attr="title"></div>
			<div title="矩形" cmd="draw(rect)" icon="icon-eda-rect"
				class="toolbarbutton icon-eda-rect" data-i18n-attr="title"></div>
			<div title="多边形" cmd="draw(polygon)" icon="icon-eda-polygon"
				class="toolbarbutton icon-eda-polygon" data-i18n-attr="title"></div>
			<div title="椭圆" cmd="draw(ellipse)" icon="icon-eda-ellipse"
				class="toolbarbutton icon-eda-ellipse" data-i18n-attr="title"></div>
			<div title="饼形" cmd="draw(pie)" icon="icon-eda-pie"
				class="toolbarbutton icon-eda-pie" data-i18n-attr="title"></div>
			<div title="图片" cmd="editImageHref" icon="icon-eda-image"
				class="toolbarbutton icon-eda-image" data-i18n-attr="title"></div>
			<div title="拖移" cmd="dragMoveSwitch" icon="icon-eda-drag"
				class="toolbarbutton icon-eda-drag" data-i18n-attr="title"></div>
			<div title="画布原点" cmd="draw(origin)" icon="icon-eda-origin"
				class="toolbarbutton icon-eda-origin" data-i18n-attr="title"></div>
		</div>
	</div>
</body>
</html>