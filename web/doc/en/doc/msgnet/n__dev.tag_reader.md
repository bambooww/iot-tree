Node: Tags Reader
==

Based on the selected tags and variable names, read the tag values in the current running project to form JSON data output



This node does not perform any analysis or processing on the input message, and the message only serves to trigger the node's operation.

When reading a certain tag item, you can also set whether it is "must be valid". When checked, if the related tag value is invalid, the message will not be output normally.
The node has two output channels. The first outputs data normally, and the second is to output an invalid tags list from this channel when an invalid value is found in a "must be valid" tag item.


### Parameter settings

Double click to open the node parameter settings dialog box

Parameter settings can include multiple tag-variable mappings, resulting in a JSON object when outputted, with internal members being "variables:tag value" content. Each setting item has the following content:

#### Tag



Click on the Tag input box to pop up a selection dialog box. Select OK to proceed.


#### Var



The variable name corresponding to this Tag value will become the member property name of the output JSON object.


#### Must be valid


If you check this option, if the corresponding Tag value read by the node is invalid, it is considered that the entire output is invalid. Therefore, only an invalid Tags list will be output in the second output channel of the node. If this option is not checked and the Tag value is invalid, the JSON object does not contain this data member during normal output.


