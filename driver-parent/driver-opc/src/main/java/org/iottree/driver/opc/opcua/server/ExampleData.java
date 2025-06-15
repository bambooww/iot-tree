package org.iottree.driver.opc.opcua.server;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ubyte;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ulong;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ushort;

import java.util.UUID;
import org.eclipse.milo.opcua.stack.core.NodeIds;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.XmlElement;

class ExampleData {

  static final Object[][] STATIC_SCALAR_NODES =
      new Object[][] {
        {"Boolean", NodeIds.Boolean, Variant.ofBoolean(false)},
        {"Byte", NodeIds.Byte, Variant.ofByte(ubyte(0x00))},
        {"SByte", NodeIds.SByte, Variant.ofSByte((byte) 0x00)},
        {"Integer", NodeIds.Integer, Variant.ofInt32(32)},
        {"Int16", NodeIds.Int16, Variant.ofInt16((short) 16)},
        {"Int32", NodeIds.Int32, Variant.ofInt32(32)},
        {"Int64", NodeIds.Int64, Variant.ofInt64(64L)},
        {"UInteger", NodeIds.UInteger, Variant.ofUInt32(uint(32))},
        {"UInt16", NodeIds.UInt16, Variant.ofUInt16(ushort(16))},
        {"UInt32", NodeIds.UInt32, Variant.ofUInt32(uint(32))},
        {"UInt64", NodeIds.UInt64, Variant.ofUInt64(ulong(64L))},
        {"Float", NodeIds.Float, Variant.ofFloat(3.14f)},
        {"Double", NodeIds.Double, Variant.ofDouble(3.14d)},
        {"String", NodeIds.String, Variant.ofString("string value")},
        {"DateTime", NodeIds.DateTime, Variant.ofDateTime(DateTime.now())},
        {"Guid", NodeIds.Guid, Variant.ofGuid(UUID.randomUUID())},
        {
          "ByteString",
          NodeIds.ByteString,
          Variant.ofByteString(new ByteString(new byte[] {0x01, 0x02, 0x03, 0x04}))
        },
        {"XmlElement", NodeIds.XmlElement, Variant.ofXmlElement(new XmlElement("<a>hello</a>"))},
        {
          "LocalizedText",
          NodeIds.LocalizedText,
          Variant.ofLocalizedText(LocalizedText.english("localized text"))
        },
        {
          "QualifiedName",
          NodeIds.QualifiedName,
          Variant.ofQualifiedName(new QualifiedName(1234, "defg"))
        },
        {"NodeId", NodeIds.NodeId, Variant.ofNodeId(new NodeId(1234, "abcd"))},
        {"Variant", NodeIds.BaseDataType, Variant.ofInt32(32)},
        {"Duration", NodeIds.Duration, Variant.ofDouble(1.0)},
        {"UtcTime", NodeIds.UtcTime, Variant.ofDateTime(DateTime.now())},
      };

  static final Object[][] STATIC_ARRAY_NODES =
      new Object[][] {
        {"BooleanArray", NodeIds.Boolean, false},
        {"ByteArray", NodeIds.Byte, ubyte(0)},
        {"SByteArray", NodeIds.SByte, (byte) 0x00},
        {"Int16Array", NodeIds.Int16, (short) 16},
        {"Int32Array", NodeIds.Int32, 32},
        {"Int64Array", NodeIds.Int64, 64L},
        {"UInt16Array", NodeIds.UInt16, ushort(16)},
        {"UInt32Array", NodeIds.UInt32, uint(32)},
        {"UInt64Array", NodeIds.UInt64, ulong(64L)},
        {"FloatArray", NodeIds.Float, 3.14f},
        {"DoubleArray", NodeIds.Double, 3.14d},
        {"StringArray", NodeIds.String, "string value"},
        {"DateTimeArray", NodeIds.DateTime, DateTime.now()},
        {"GuidArray", NodeIds.Guid, UUID.randomUUID()},
        {
          "ByteStringArray", NodeIds.ByteString, new ByteString(new byte[] {0x01, 0x02, 0x03, 0x04})
        },
        {"XmlElementArray", NodeIds.XmlElement, new XmlElement("<a>hello</a>")},
        {"LocalizedTextArray", NodeIds.LocalizedText, LocalizedText.english("localized text")},
        {"QualifiedNameArray", NodeIds.QualifiedName, new QualifiedName(1234, "defg")},
        {"NodeIdArray", NodeIds.NodeId, new NodeId(1234, "abcd")}
      };
}
