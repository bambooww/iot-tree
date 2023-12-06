Js Api Inner Plugin - \$$http
==

You need to have some understanding of the HTTP protocol.

## 1 \$$http General introduction



The \$$http plugin provides support for GET and POST data for HTTP/https URLs. In many situations, you need to interface with HTTP methods provided by other systems or platforms to send/retrieve data. In most of these applications, there are the following characteristics:

> 1. The connected system provides a RESTful interface, requiring you to register an account and apply for an application ID and digital signature.

> 2. You need to set some special HTTP header parameters every time you initiate a request.

> 3. When you post some data, you need to provide parameters for name value pairs

The \$$http plugin provides some object and functions support specifically for the above applications. Below is an explanation of the JS editing assistance dialog box that comes with the system.

On the middle tree node of the IOT-Tree project management page, right-click and select "JS Context Test". In the pop-up dialog box, you can see the \$$http member. Expand this member to see the child node "HttpURL createUrl(str)". Click this node, and in the "Help For:" section below, you can see the help button "HttpURL". Click this button to see the member help dialog box for the "HttpURL" object. As shown in the following figure:


<img src="../img/js/j015.png">



We can see that \$$http provides a function "createUrl", which returns an HttpURL object that provides relevant functions for accessing a certain URL.


### 1.1 Function:HttpURL &nbsp;&nbsp;\$$http.createUrl(str)



The function provided by \$$http mainly creates an HttpURL object based on the target host's HTTP URL you provide. So the specific subsequent operations are mainly provided by this return object. The calling method is as follows:


```
var u = $$http.createUrl("https://xxx.xxx.com/sms_send");
```

### 1.2 HttpURL Object



This object already has specific URL parameters internally, so next you need to set parameters, submit data, or obtain data according to the specific requirements provided by the docking party. For this purpose, the HttpURL object provides support for the following functions.


#### 1.2.1 HttpURL Object's Function - setRequestHeader(str,str)


This function is used to support setting HTTP request header parameters, such as setting "Content-Type" standard parameters or setting custom parameters.


#### 1.2.2 HttpURL Object's Function - str doGet()


If you are planning to use the HTTP Get method to retrieve the data for this URL, then you can call this function at this time. It will return the string data output by the URL.


#### 1.2.3 HttpURL Object's Function - setPostParam(str,str)


If you are planning to use the POST method to submit data to the corresponding URL, and the host requests that the parameters you submit use the "form urlencoded" format, then you can call this method to set the parameters.


#### 1.2.4 HttpURL Object's Function - str doPost()


After setting by the above two functions, you can call this method to submit data to the URL in POST mode and return the string data output by the server-side.


#### 1.2.5 HttpURL Object's Function - str doPostRaw(str)



If you want to submit a custom string format data using POST, you can directly call this method, and the content set by the setPostParam function will be useless. For example, you can define your own JSON format data, and then call this function to submit directly.


#### 1.2.6 HttpURL Object's Function - setContentTypeJson()
```
Equals to
setRequestHeader("Content-Type", "application/json");
```

#### 1.2.7 HttpURL Object's Function - setContentTypeXml()
```
Equals to
setRequestHeader("Content-Type", "application/xml");
```

#### 1.2.8 HttpURL Object's Function - setContentTypeFormUrlEncoded()
```
Equals to
setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
```

### 1.3 Use Case

#### 1.3.1 SMS interface HTTP call case



The following is an example of calling the mobile SMS sending interface provided by the SMS platform. Among them, "appid, token, sign" are the verification parameters assigned by the platform for your account.


```
let msg = "device 1 has alarm with no. 12345" ;
let u = $$http.createUrl("https://xxx.xxx.com/sms_send");

u.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
u.setRequestHeader("token","1122334455")

u.setPostParam("appid","xxxx");
u.setPostParam("sign","aabbccddee12345678");
u.setPostParam("phone_num","13000000000");
u.setPostParam("content",msg);

let retstr = u.doPost();
//check retstr

```

#### 1.3.2 Obtain JSON data provided by the URL

```
let u = $$http.createUrl("https://xxx.xxx.com/syn_data");

u.setRequestHeader("token","1122334455")

let retstr = u.doGet();
let retob = null;
eval("retob="+retstr) ;

// check retob

```

#### 1.3.3 Submit your custom JSON data


```
let msg = {} ;
let u = $$http.createUrl("https://data.xxx_host.com/recv");

u.setRequestHeader("token","1122334455");
u.setRequestHeader("xx_session_id","xxxxxxxx")

msg.appid="xxxx";
msg.signature="23232455456" ;
msg.data_items=[];
msg.data_items.push({a:100,b:12.3,c:"ready"});
msg.data_items.push({a:134,b:1.2,c:"not ready"});
// .. other data
let msgstr = JSON.stringify(msg) ; //transfer to JSON string

let retstr = u.doPost(msgstr);

//check retstr

```
