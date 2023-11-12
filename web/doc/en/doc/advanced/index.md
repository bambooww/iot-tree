Extends Functions of IOT-Tree
==



If you already have a basic understanding of IOT-Tree Server. Here, if you want IOT-Tree Server to bring you greater value and convenience, it is necessary to deeply understand the following contents.


## 1 插件开发

IOT-Tree Server本身提供了丰富多彩的物联网支持功能。但在很多场合，直接使用这些功能还不能满足你的需要。

比如，您如果想在系统内部运行的JS脚本增加一些特殊功能或Api，这些特殊功能不可能由IOT-Tree Server提供。典型的一种情况是：您的组织内部有一套特殊的消息推动通道，如邮件服务器或某个手机短信接口。你希望在IOT-Tree Server内部运行的JS任务中加入这个消息推送功能。那么使用IOT-Tree Server的插件支持机制，就可以达到你的目的。

详细内容可以参考 [Plugins Development][plugin]

[插件开发-JsApi][plug_js_api]   [插件开发-authority][plug_auth]

[plugin]:./adv_plugin.md
[plug_js_api]:./adv_plugin_jsapi.md
[plug_auth]:./adv_plugin_auth.md

## 2 
