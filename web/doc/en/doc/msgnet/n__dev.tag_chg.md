Node: Tag Change Trigger
==

By selecting a tag, listen for updating in the tag during runtime, and generate message output based on a certain strategy

Each tag change will trigger an output message, and the payload format for each message is as follows:
```
"payload": {
		"valid": true,
		"chgdt": 1752453543170,
		"tag_title": "TotalV",
		"tag_id": "r25",
		"tag_path": "dlb1.d1.eng",
		"updt": 1752462618027,
		"tag_val": 12407.9,
		"vt": "float"
	}
```

You can choose to listen to tags that only need to be updated to trigger (regardless of whether the value changes). Meanwhile, it is also possible to choose to trigger only when the tags value changes.
