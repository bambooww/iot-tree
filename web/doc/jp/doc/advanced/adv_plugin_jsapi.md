



js_api插件可以使得IOT-Tree Server服务器端JS脚本支持更多的特殊api，这样会让系统更加贴近您的业务需求。

如果你对IOT-Tree Server的插件机制不了解，请先查看 [插件开发][plug]。

# 1 JsApi插件配置

在插件目录下的config.json文件中，配置JSON对象"js_api"属性，此类型对应一个JSON数组，数组每个成员都是一个JSON对象，每个对象对应一个js_api的对象。
如下:
```
{
"name":"demo","title":"Demo Plug",

"js_api":[
	{
		name:"demo",class:"com.xxx.plug.JsApiMail",
		params:{smtp_host:"localhost","mail_user":"user1",mail_password:"xxxxx"}
	}
	]
}
```
每个js_api对应的对象，有如下属性"name","class"和"params"。其中，"name"和"class"是必不可少的。并且，"name"属性的值是个符合js变量命名的字符串，"class"这是配套提供的java对象全名称。这个类必须在插件目录下的"classes/"或"lib/"中的某个jar文件中存在。
"params"是对应插件需要用到的参数，可以根据插件具体实现的提供格式定义和支持。



[plug]: ./adv_plugin.md
