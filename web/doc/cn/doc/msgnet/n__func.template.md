节点：模板
==


根据设置的模板文本，使用mustache方式 {{xxx}} 输出payload内容。




### 参数设置

双击可以打开节点参数设置对话框

#### 模板文本


字符串，加{{ }}格式内容。输出固定格式的内容当然也是可以的。以下是个典型例子：



Input Msg's payload
```
payload:{
    list:[
        {name:'n1',age:32,job:'UI'},
        {name:'sam',age:26,job:'java'},
        {name:'nnn',age:30,job:'engineer'},
        {name:'musk',age:53,job:'xx'}
    ]
}
```

Template:
```
<ul>
    {{#payload.list}}
    <li>
        <span>{{name}}</span>
        <span>{{age}}</span>
        <span>{{job}}</span>
    </li>
    {{/payload.list}}
</ul>
```
Output Msg:
```
{
	"payload": "<ul>\n        <li>\n            <span>n1</span>\n            <span>32</span>\n            <span>UI</span>\n        </li>\n        <li>\n            <span>sam</span>\n            <span>26</span>\n            <span>java</span>\n        </li>\n        <li>\n            <span>nnn</span>\n            <span>30</span>\n            <span>engineer</span>\n        </li>\n        <li>\n            <span>musk</span>\n            <span>53</span>\n            <span>xx</span>\n        </li>\n    </ul>",
	"time_ms": 1717922791894,
	"id": "00LX7AVHSM00010"
}
```


#### 输出格式


有json 和 txt两种。其中，json格式输出消息payload会自动进行JSON对象转换；如果txt输出，则不管模板计算之后是哪种格式，都作为一个字符串输出。


