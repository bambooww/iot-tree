Node: Tags Read Filter
==

This node does not process the input message in any way, and the input message is only a trigger for running




In project data organization, all tags are in one project tree. If we want to extract and utilize some of the data inside, the simplest way is to use this tree as the basis, filter a subtree, and send it to the data user end. This is also a typical scenario for IoT-Tree applications.


### Parameter settings

Double click to open the node parameter settings dialog box

#### Root Path



Select a container node in the project organization, and this node is limited to the container and tag data content below this.


#### Include System Tags



Check this option to output the system Tags below the subtree as well


#### Container Node


Filter by container node type. When this filtering is enabled, you can check the container types that meet the output conditions


#### Container Node Properties


When using container node properties as filtering criteria, you can check the container properties that meet the output criteria. These properties exist in project management by making relevant settings for container nodes through dictionaries.


#### Tags Properties


When using Tag properties as filtering criteria, you can check the container properties that meet the output criteria. These properties exist in project management by making relevant settings for Tags through dictionaries.

