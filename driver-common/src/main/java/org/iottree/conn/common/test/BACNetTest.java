package org.iottree.conn.common.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.serotonin.bacnet4j.LocalDevice;
//import com.serotonin.bacnet4j.RemoteDevice;
//import com.serotonin.bacnet4j.RemoteObject;
//import com.serotonin.bacnet4j.event.DeviceEventAdapter;
//import com.serotonin.bacnet4j.event.DeviceEventListener;
//import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
//import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
//import com.serotonin.bacnet4j.obj.BACnetObject;
//import com.serotonin.bacnet4j.service.Service;
//import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
//import com.serotonin.bacnet4j.transport.DefaultTransport;
//import com.serotonin.bacnet4j.type.Encodable;
//import com.serotonin.bacnet4j.type.constructed.Address;
//import com.serotonin.bacnet4j.type.constructed.Choice;
//import com.serotonin.bacnet4j.type.constructed.DateTime;
//import com.serotonin.bacnet4j.type.constructed.PropertyValue;
//import com.serotonin.bacnet4j.type.constructed.SequenceOf;
//import com.serotonin.bacnet4j.type.constructed.TimeStamp;
//import com.serotonin.bacnet4j.type.enumerated.EventState;
//import com.serotonin.bacnet4j.type.enumerated.EventType;
//import com.serotonin.bacnet4j.type.enumerated.MessagePriority;
//import com.serotonin.bacnet4j.type.enumerated.NotifyType;
//import com.serotonin.bacnet4j.type.enumerated.ObjectType;
//import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
//import com.serotonin.bacnet4j.type.notificationParameters.NotificationParameters;
//import com.serotonin.bacnet4j.type.primitive.Boolean;
//import com.serotonin.bacnet4j.type.primitive.CharacterString;
//import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
//import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
//import com.serotonin.bacnet4j.util.PropertyValues;
//import com.serotonin.bacnet4j.util.ReadListener;
//import com.serotonin.bacnet4j.util.RequestUtils;

public class BACNetTest
{
//	private static void test1()
//	{
//		LocalDevice d = null;
//		try
//		{
//			// 创建网络对象
//			IpNetwork ipNetwork = new IpNetworkBuilder().withLocalBindAddress("192.168.1.8")// 本机的ip
//					.withSubnet("255.255.255.0", 24) // 掩码和长度，如果不知道本机的掩码和长度的话，可以使用后面代码的工具类获取
//					.withPort(47808) // Yabe默认的UDP端口
//					.withReuseAddress(true).build();
//			// 创建虚拟的本地设备，deviceNumber随意
//			d = new LocalDevice(12345, new DefaultTransport(ipNetwork));
//			d.initialize();
//			// d.
//			d.startRemoteDeviceDiscovery();
//
//			RemoteDevice rd = d.getRemoteDeviceBlocking(229484);// 获取远程设备，instanceNumber
//																// 是设备的device id
//
//			System.out.println("modelName=" + rd.getDeviceProperty(PropertyIdentifier.modelName));
//			System.out.println("analogInput2= " + RequestUtils.readProperty(d, rd,
//					new ObjectIdentifier(ObjectType.analogInput, 0), PropertyIdentifier.presentValue, null));
//
//			List<ObjectIdentifier> objectList = RequestUtils.getObjectList(d, rd).getValues();
//
//			// 打印所有的Object 名称
//			for (ObjectIdentifier o : objectList)
//			{
//				System.out.println(o);
//			}
//
//			ObjectIdentifier oid = new ObjectIdentifier(ObjectType.analogInput, 0);
//			ObjectIdentifier oid1 = new ObjectIdentifier(ObjectType.analogInput, 1);
//			ObjectIdentifier oid2 = new ObjectIdentifier(ObjectType.analogInput, 2);
//
//			// 获取指定的presentValue
//			PropertyValues pvs = RequestUtils.readOidPresentValues(d, rd, Arrays.asList(oid, oid1, oid2),
//					new ReadListener() {
//						@Override
//						public boolean progress(double progress, int deviceId, ObjectIdentifier oid,
//								PropertyIdentifier pid, UnsignedInteger pin, Encodable value)
//						{
//							System.out.println("========");
//							System.out.println("progress=" + progress);
//							System.out.println("deviceId=" + deviceId);
//							System.out.println("oid=" + oid.toString());
//							System.out.println("pid=" + pid.toString());
//							System.out.println("UnsignedInteger=" + pin);
//							System.out.println("value=" + value.toString() + "  getClass =" + value.getClass());
//							return false;
//						}
//
//					});
//			Thread.sleep(3000);
//			System.out.println("analogInput:0 == " + pvs.get(oid, PropertyIdentifier.presentValue));
//			// 获取指定的presentValue
//			PropertyValues pvs2 = RequestUtils.readOidPresentValues(d, rd, Arrays.asList(oid, oid1, oid2), null);
//			System.out.println("analogInput:1 == " + pvs2.get(oid1, PropertyIdentifier.presentValue));
//
//			d.terminate();
//		}
//		catch ( Exception e)
//		{
//			e.printStackTrace();
//			if (d != null)
//			{
//				d.terminate();
//			}
//		}
//	}
	
	private static void testFind() throws Exception
	{
		// 配置网络参数
		//IpNetwork network = new IpNetwork("255.255.255.255", 47808); // BACnet标准端口
		// 或者使用IpNetworkBuilder进行更精细的配置
		// IpNetwork network = new IpNetworkBuilder().withPort(47808).withReuseAddress(true).build();

//		IpNetwork network = new IpNetworkBuilder().withLocalBindAddress("192.168.1.8")// 本机的ip
//				.withSubnet("255.255.255.0", 24) // 掩码和长度，如果不知道本机的掩码和长度的话，可以使用后面代码的工具类获取
//				//.withBroadcast(broadcastAddress, networkPrefixLength)
//				.withPort(47808) // Yabe默认的UDP端口
//				.withReuseAddress(true).build();
		
//		IpNetwork network = new IpNetworkBuilder()
//				.withLocalBindAddress("192.168.1.8") // .withLocalBindAddress("0.0.0.0")  // 绑定到所有网络接口
//			    .withBroadcast("255.255.255.255", 47808)  // BACnet广播地址和端口
//			    .withPort(47808)  // 本地绑定端口
//			    .withReuseAddress(true)  // 允许端口重用
//			    .build();
//		
//		// 创建传输层和本地设备
//		DefaultTransport transport = new DefaultTransport(network);
//		LocalDevice localDevice = new LocalDevice(12345, transport); // 12345是本地设备实例编号
//		//localDevice.getNetwork().getTransport().
//		    localDevice.initialize(); // 初始化设备
//		    System.out.println("本地设备初始化成功");
//		
//		 // 存储发现的远程设备
//		    final Map<Integer, RemoteDevice> remoteDevices = new HashMap<>();
//
//		    // 添加设备事件监听器来捕获远程设备的"I-Am"响应
//		    localDevice.getEventHandler().addListener(new DeviceEventAdapter() {
//		        @Override
//		        public void iAmReceived(final RemoteDevice remoteDevice) {
//		            // 当收到I-Am响应时，将设备添加到映射中
//		            remoteDevices.put(remoteDevice.getInstanceNumber(), remoteDevice);
//		            System.out.println("发现设备: ID=" + remoteDevice.getInstanceNumber() + 
//		                             ", 地址=" + remoteDevice.getAddress());
//		        }
//
//		    });
//
//		    // 发送Who-Is广播请求发现网络中的所有设备
//		    localDevice.sendGlobalBroadcast(new WhoIsRequest());
//
//		    // 等待一段时间收集响应（因为广播是异步的）
//		    try {
//		        Thread.sleep(10000); // 等待5秒让设备响应
//		    } catch (InterruptedException e) {
//		        e.printStackTrace();
//		    }
//
//		    System.out.println("共发现 " + remoteDevices.size() + " 个设备");
		    
//		 // 遍历所有发现的设备
//		    for (RemoteDevice remoteDevice : remoteDevices.values()) {
//		        try {
//		            System.out.println("\n查询设备 " + remoteDevice.getInstanceNumber() + " 的对象列表:");
//		            
//		            // 读取设备的OBJECT_LIST属性
//		            List<ObjectIdentifier> objectList = localDevice.getRemoteObjects(remoteDevice);
//		            
//		            for (ObjectIdentifier objId : objectList) {
//		                System.out.println("对象类型: " + objId.getObjectType() + 
//		                                 ", 实例: " + objId.getInstanceNumber());
//		            }
//		        } catch (Exception e) {
//		            System.err.println("读取设备 " + remoteDevice.getInstanceNumber() + " 的对象列表时出错: " + e.getMessage());
//		        }
//		    }
		    
		    //localDevice.terminate();
	}

	public static void main(String[] args) throws Exception
	{
		testFind();
	}

}
