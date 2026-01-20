Tags Event/Alert Reader
==



When configuring parameters for this node, all tags in the project that have been configured with event/alarm information will be listed. You need to select the relevant tags that you want to read.

When any message enters the node, it will read the configured tags to check if there is an event or alarm triggered, and finally form a JSON Array for output.

You can set it up so that no messages are output when there are no triggers. This node can be used to process all actual/alarm events for a project. At its simplest, it can be used to determine whether there are any triggered events or alarms in the project, and overall, it serves as a warning function.

