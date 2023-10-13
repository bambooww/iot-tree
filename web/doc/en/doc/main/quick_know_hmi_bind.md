


Quick understanding of HMI - properties binding and event handling:
==




IOT tree server provides powerful online HMI editing function. If you don't understand this, please refer to [HMI UI editing instructions][qn_hmi_edit].


Interactive UI editing allows you to create your monitoring screen or scene according to your needs. Next, how can you make these monitoring images change dynamically according to your real-time data? Conversely, you also need to issue control instructions to the monitored devices. How can this be achieved?

IOT tree server establishes the association between UI components and data context through entity attribute binding mechanism. At the same time, the element provides an event response mechanism to respond to user actions.





## 1 Draw item's property binding (bind)

In the property bar on the right of the UI editing area, if a property supports binding, a bind button will appear.

<img src="../img/hmi_bind1.png">




Click this button to open the attribute binding window. There are two binding properties: context tag binding and JS expression binding.


<img src="../img/hmi_bind2.png">




The context tag is based on the data context corresponding to the tree node to which the HMI UI node belongs. JS based expressions can provide a more flexible way to generate attribute data. For example, you can meet specific logic according to multiple context tags and output data types and values that meet the attribute display.

After binding, if the draw item in HMI UI enters the external application access interface, the attribute value will be displayed dynamically according to the real-time context of system project operation. That is, it produces a dynamically changing picture effect.





### 1.1 Context based tag binding

Click the tag input box to pop up the context tags that can be used by this HMI UI and the corresponding proterties values. You can choose directly. As shown below:

<img src="../img/hmi_bind3.png">



After selection, the unique identification string corresponding to the context tag will appear in the tag input box:


<img src="../img/hmi_bind4.png">



### 1.2 JS expression binding

Based on the running environment of the HMI UI context, the bound data can be defined using expressions in JS syntax format. As shown below:

<img src="../img/hmi_bind5.png">




For more information about JS expressions, please refer to [quick understanding of JS expressions][qn_js_exp] or [in-depth understanding of JS expressions of IOT tree server][adv_js_exp].





## 2 Draw item event handling

After the draw item is selected, Click the events bar on the right. You can see all the events that this element can provide. For example, on_mouse_down is the event that the user presses the mouse on the element. If you want to make relevant UI changes or the server does corresponding actions (such as issuing an instruction to the device) when the user clicks this element, you need to add a JS script for handling this event.

Click the input box of the corresponding event to pop up the event handling JS script editing interface. As shown below:

<img src="../img/hmi_bind_evt1.png">




As shown in the figure, the event handling JS script is divided into client JS (client JS) and server JS (server JS).

The operation on the client is mainly based on the browser HMI UI framework, and no interaction with the server is allowed. The element properties are dynamically adjusted through the UI framework to form the dynamic changes of the element display. For details, please refer to [quick understanding JS expression][qn_js_exp] or [in-depth understanding of JS expression of IOT tree server][adv_js_exp].

Scripts running on the server respond to browser side events, but all scripts run on the server. Its running environment is mainly based on the label data context corresponding to the node to which the UI belongs. On the server side, the script can directly read or write the tag value (finally, it may issue instructions to the device through the device driver corresponding to the tag). For details, please refer to [quick understanding of JS expression][qn_js_exp] or [in-depth understanding of JS expression of IOT tree server][adv_js_exp].


[qn_js_exp]: ./quick_know_js_exp.md
[adv_js_exp]: ../advanced/adv_js_exp.md
[qn_hmi_edit]: ./quick_know_hmi_edit.md
