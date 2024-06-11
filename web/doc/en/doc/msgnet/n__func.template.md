Node: Template
==




According to the set template text, use the mustache method {{xxx}} to output the payload content.


### Parameter settings

Double click to open the node parameter settings dialog box

#### Template Txt



String, with {{}} format content added. It is also possible to output fixed format content. Here is a typical example:


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


#### Output Format



There are two types: JSON and TXT. Among them, the JSON format output message payload will automatically perform JSON object conversion; If txt is output, regardless of the format after template calculation, it will be output as a string.

