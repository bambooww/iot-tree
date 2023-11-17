IOT-Tree 高级特性
==

如果你对IOT-Tree Server已经有了基本的了解。在此，如果你还想让IOT-Tree Server为你带来更大的价值和方便，那么很有必要深入了解以下内容。




## 1 插件开发


IOT-Tree Server本身提供了丰富多彩的物联网支持功能。但在很多场合，直接使用这些功能还不能满足你的需要。

比如，您如果想在系统内部运行的JS脚本增加一些特殊功能或Api，这些特殊功能不可能由IOT-Tree Server提供。典型的一种情况是：您的组织内部有一套特殊的消息推动通道，如邮件服务器或某个手机短信接口。你希望在IOT-Tree Server内部运行的JS任务中加入这个消息推送功能。那么使用IOT-Tree Server的插件支持机制，就可以达到你的目的。

另外一种情况是，你的业务系统整合了IOT-Tree，你希望在你已经存在的大型管理系统中，直接整合IOT-Tree能够提供的监控界面。此时，就引出一个问题——你必须让IOT-Tree的监控界面的访问权限与你现有的系统统一。IOT-Tree提供了authority插件开发支持来解决这个问题。



详细内容可以参考 [插件开发][plugin],  [插件开发-JsApi][plug_js_api]  , [插件开发-Authority][plug_auth]

[plugin]:./adv_plugin.md
[plug_js_api]:./adv_plugin_jsapi.md
[plug_auth]:./adv_plugin_auth.md


## 2 主站-子站远程控制

TODO

## 3 设备模拟器

TODO
