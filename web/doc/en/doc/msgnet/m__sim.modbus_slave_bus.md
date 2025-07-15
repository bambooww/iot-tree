Module: Modbus Slave Simulator
==


Modbus slave device simulator can simulate Modbus slave devices based on message flow, so that the external (Modbus master) can use this simple protocol to obtain data or issue control instructions.

<b style="color:blue">Through this feature, you can turn your computer or edge device running IOT-Tree into a slave device that supports Modbus protocol. Based on this simulator, IOT-Tree can serve as a protocol conversion gateway, supporting various connector and device drivers that can be converted to Modbus protocol output through intermediate tag organization.</b>

Of course, specialized message flows can also be defined to simulate an on-site operating condition and be used to test other devices or software systems.

A module node represents a bus, on which there can be one or more devices; Each device can define different internal address segments as needed, and each address segment can set corresponding function codes (including data types) and memory space.

Meanwhile, this bus can also define multiple docking methods and protocols, and each docking can have different protocols:

 1 RS485/RS232 interface, supporting RTU protocol

 2 Supports Tcp Server mode, runs multiple master TCP client link in, supports Modbus TCP protocol or Tcp based RTU protocol.

After setting the parameters in this module, the system will automatically generate matching data update nodes and write instruction trigger message nodes based on the defined devices.

 1 Data update node allows other nodes in the message flow to push message updates to the internal device memory data of the bus, and then the external master can obtain the updated data.

 2 Write instruction trigger node: It can trigger specific messages when the external master issues a write instruction. The message flow can perform specific write actions or write tags based on this message (and can then issue instructions to the actual device through device drivers).



