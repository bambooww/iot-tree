Plugin - JsApi
==



The JsApi plugin can enable IOT-Tree Server server-side JS scripts to support more special objects ($$xxx), and these objects provide special function support, which can make the system more tailored to your business needs. For example, if your application needs to use IOT Tree as the edge computing node of an IoT site, and needs to communicate with the cloud (push data) through the secure channel of a private protocol, then you can specifically implement the corresponding JsApi plug-in for this communication, and then use JS script to organize data in the task in IOT Tree, and send data by calling related functions through this plug-in object.

If you are not familiar with the plugin mechanism of IOT-Tree Server, please check [Plugin Development][plug] first.

To provide a more intuitive and detailed description of the development, deployment, and use of JsApi. Let's use an example to illustrate.

In order to enable the IOT-Tree server JS script to support email push, a $$mail plugin is specifically implemented, which includes support for email sending and other related functions.


## 1 Java Class Development


You can use any tool to develop this Java class, and IOT-Tree only has some naming restrictions on the Java classes of the JsApi plugin. There is no Java interface or abstract class provided for you to implement or inherit. This way, you don't need to rely on anything provided by IOT-Tree. Of course, the class you implement has its own special dependencies, so you can refer to the "lib/" or "classes/" directory specified in [plugin development][plug] to store it.


### 1.1 Implementation regulations for Java plugin classes

#### 1.1.1 Initialization Method


There must be an initialization method inside the plugin class, defined as follows:

```
void init_plug(File plugdir, HashMap<String, String> params)
```


This method name must be 'init_plug' and have two parameters. One is the directory where this plugin will be deployed, and the other is the input parameter. This method will be automatically called before the plugin is loaded and used. You can perform some initialization work inside based on the plugin directory and input parameters. It can roughly include the following content:

>You can use the plugin root directory provided by "plugdir" to locate the absolute location for plugin deployment and load specific files required for plugin operation. These files are determined by the plugin implementation itself, for example, you can locate a special configuration file through this directory.

>You can initialize the required content for subsequent runs through the parameters (which come from the plugin configuration "config.json"). For example, for email sending, you can configure the email server address, port, user, and authentication information.

#### 1.1.2 Methods open to JS


In the plugin class you implement, if you want to open a method for JS to use, the method name must start with "JS_", such as

```
public String JS_get_host()

public void JS_send_mail(String to_mail, String mail_title, String mail_content) 
```


After loading this plugin class, IOT-Tree will search for methods starting with "JS_" and set them as member functions that JS can call. Among them, there is no need to write "JS_" content when calling JS. When calling the above two functions in JS, use the following method:

```
String host = $$mail.get_host();

$$mail.send_mail("xx@xxx.com","mail title","hello send mail") ;
```


<font color="red">Please note that for the convenience of JS calls, IOT-Tree stipulates that when defining open methods, their parameters and return values can only use basic types. Because if we want to implement more complex objects as parameters, it will also make the development of plugins much more complex and debugging difficult. Although IOT-Tree can open more dependencies and interfaces to support it. In fact, the basic types used by the vast majority of plugin applications can meet the requirements</font>


### 1.2 Implementation examples


As can be seen, the implementation of JsApi's plugin is very simple, just follow the regulations to implement "init_plug" and starting with "JS_" methods. The following is the complete code for this email sending implementation.

```
package com.xxx.plug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * use JavaMail and smtp protocal to send mail it will be a JsApi plugin for
 * IOT-Tree Server
 * 
 * @author demo
 *
 */
public class JsApiMail
{
	private String smtpHost = null;
	private String mailUser = null;
	private String mailPsw = null;
	private String senderMail = null;

	void init_plug(File plugdir, HashMap<String, String> params) throws Exception
	{
		smtpHost = params.get("smtp_host");
		mailUser = params.get("mail_user");
		mailPsw = params.get("mail_password");
		senderMail = params.get("sender_mail");
	}

	public String JS_get_host()
	{
		return smtpHost ;
	}

	public void JS_send_mail(String to_mail, String mail_title, String mail_content) throws Exception
	{
		Properties props = new Properties();

		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.auth", "true");

		Session session = Session.getInstance(props);
		session.setDebug(true); //

		MimeMessage message = new MimeMessage(session);

		message.setFrom(new InternetAddress(senderMail));
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(to_mail));
		message.setSubject(mail_title);

		message.setContent(mail_content, "text/html;charset=UTF-8"); //send as html
		message.setSentDate(new Date());
		message.saveChanges();// 

		// send mail
		Transport transport = null;
		try
		{
			transport = session.getTransport("smtp");
			transport.connect(mailUser, mailPsw);
			transport.sendMessage(message, message.getAllRecipients());
		}
		finally
		{
			if (transport != null)
				transport.close();
		}
	}
}

```


From the above code, it can be seen that you can fully implement and test it in your familiar Java development environment, generating the class file JsApiMail.class. After completion, you can deploy and use it in the IOT-Tree environment.


## 2 Plugin deployment

### 2.1 </en>Plugin deployment Directory</en>


Assuming that you have installed and configured the IOT-Tree runtime environment, then find the "data/plugins/" directory in the IOT-Tree installation directory. Create a new directory called 'mail' inside.

In addition, the email plugin requires two dependent Java library files, activation-1.1.jar and mail-1.4.jar. This also requires deployment.

The overall deployment directory and file structure are as follows:

```
│  config.json
│  readme.txt
│
├─classes
│  └─com
│      └─xxx
│          └─plug
│                  JsApiMail.class
│
├─lib
│      activation-1.1.jar
│      mail-1.4.jar
│
└─src
    └─com
        └─xxx
            └─plug
                    JsApiMail.java
```


After deploying the files and directories, we also need to edit the configuration file "config.json" in order to finally complete the deployment of the plugin.


### 2.2 JsApi plugin configuration


Because config.json not only supports JsApi plugins, but also other plugin types, JsApi is only a part of this configuration file.

Similarly, we can publish multiple JsApi content in a single plugin directory. In the config.json file in the plugin directory, the configurations related to JsApi are all under the "js_api" attribute, and "js_api" corresponds to a JSON array. Each member of the array is a JSON object, and each object corresponds to a object of the API. (This example only has one)

As follows:

```
{
	"name":"mails",
	"title":"Mail Plug,support send mail. using $$mail.send_mail(to_mail,mail_title,mail_content)",
	"js_api_doc":"//will set js run context global var $$xxx then you can use these global variable object,and call public method in js code",

	"js_api":
	[
		{
			name:"mail",
			class:"com.xxx.plug.JsApiMail",
			params:
			{
				"smtp_host":"smtp.xxx.com",
				"sender_mail":"uuu@xxx.com",
				"mail_user":"uuu@xxx.com",
				"mail_password":"xxxxxxx"
			}
		}
	]
}
```


The object corresponding to each 'js_api' has the following attributes: 'name', 'class', and' params'. Among them, "name" and "class" are essential. Moreover, the value of the "name" attribute is a string that matches the naming of the JavaScript variable, and "class" is the full name of the Java object provided with it. This class must exist in the plugin directory 'classes/' or in a jar file of 'lib/'.

"params" are the parameters required for the initialization function "init_plug", and the it is determined by the plugin class.

In this example, the JsApi plugin name is mail, and the corresponding JS context member is $$mail.


## 3 Plugin usage and testing



Through the above development, deployment, and configuration, we can test this plugin in the internal JS script of IOT-Tree.


### 3.1 Pre deployment testing recommendations



<font color=red>Strongly recommend that you test this plugin during its development and debugging process. The following code can be used:</font>


```
	public void test1() throws Exception
	{
		JsApiMail jam = new JsApiMail() ;
		HashMap<String,String> params = new HashMap<String,String>() ;
		params.put("smtp_host","you host addr");
		params.put("mail_user","user");
		params.put("mail_password","your password");
		params.put("sender_mail","xxx@xxx.com");
		jam.init_plug(null, params);
		
		jam.JS_send_mail("tar@ttt.com","title test 1", "hello mail");
	}

```


<font color=green>As long as you pass the testing in your testing environment, you can assume that the plugin you developed is basically fine and the testing environment you are working with is also normal. Then,deploying it to the IOT-Tree runtime environment can greatly reduce the probability of errors. After all, when there are problems with JS calls within IOT-Tree, the complexity of the environment makes it more difficult to find the bugs.</font>


### 3.2 Deploy and Enable Plugins



If the IOT-Tree Server is already running, it must be restarted after deploying a new plugin in order to load the new plugin content.

We can see the successful loading of this plugin at the prompt for starting printing on the IOT-Tree Server:

<img src="../img/adv/001.png">



<font color=green>After successfully loading the plugin object $$mail, it can be shared and used across all projects of the entire IOT-Tree deployment instance</font>

We can also view and test this plugin in the server-side JS editing dialog.

We open the demo project "Water tank and Medical Dosing", right-click on any container node in the main tree, and select "JS Context Test" to open the corresponding JS runtime context editing test dialog, as shown in the following figure:

<img src="../img/adv/002.png">



We can see that in the JS context member on the left, there is a $$mail node, and below it are two functions "send_mail" and "get_host". In the JS editing area on the right, we can write test code and click the test button to run the test.


[plug]: ./adv_plugin.md
