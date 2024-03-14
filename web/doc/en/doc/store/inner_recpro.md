Secondary processing of recorded data
==



Starting from version 1.3, IOT-Tree supports the internal timing segment recorder function. Based on this, IOT-Tree also designs an architecture for data secondary processing.
This architecture enables secondary processors of data to be implemented in a plug-in manner, which can be continuously expanded as business needs change.

## 1 Secondary Data Processing Architecture

The secondary processing of data is supported by a separate thread.

### 1.3 Why should data processors be based on records

To analyze and process data changes, in many cases, it is only necessary to analyze data changes within a short period of time. However, in some special application scenarios, data changes (such as abrupt changes) are relative to the past. In most industrial scenarios, the so-called abrupt changes may be within a few minutes, but we cannot guarantee that in another scenario, the abrupt change process may last for several days. Therefore, relying solely on short-term in-memory analysis and judgment cannot cover all situations. Therefore, our secondary data processing architecture is based on records.

## 2 Currently Supported or Planned Data Processors

### 2.1 Difference (D-Value) Processor

This processor is designed for data tags with cumulative values, and can calculate and store differences between records at four time intervals: seconds, minutes, hours, and days. This allows us to perform value statistics and recordings for tag data with cumulative values on a per-second, per-minute, per-hour, and per-day basis.
You may wonder why we don't support week, month, and year. This is based on our careful analysis and consideration. If you want to display weekly, monthly, and yearly values, you can simply query based on the daily difference records.

### 2.2 Jump Change Processor (Under Planning)

This processor can analyze the discrete changes in a tag value and record the data changes during the transition. For example, a switch value changes from off-on

### 2.3 Mutation (Value Mutation) Processor (under planning)

This processor can analyze a continuously changing label value and record the data changes when a mutation is detected. For example, a temperature label value changes slowly and then suddenly rises rapidly.

### 2.4 Value Stable Processor (under planning)

This processor can analyze a continuously changing tag value and record the condition when the data is stable.

