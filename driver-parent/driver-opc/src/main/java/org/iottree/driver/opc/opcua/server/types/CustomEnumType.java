package org.iottree.driver.opc.opcua.server.types;

import javax.annotation.Nullable;

import org.eclipse.milo.opcua.stack.core.serialization.SerializationContext;
import org.eclipse.milo.opcua.stack.core.serialization.UaDecoder;
import org.eclipse.milo.opcua.stack.core.serialization.UaEncoder;
import org.eclipse.milo.opcua.stack.core.serialization.UaEnumeration;
import org.eclipse.milo.opcua.stack.core.serialization.codecs.GenericDataTypeCodec;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.iottree.driver.opc.opcua.server.DrvNamespace;

public enum CustomEnumType implements UaEnumeration {

    Field0(0),
    Field1(1),
    Field2(2);

    public static final ExpandedNodeId TYPE_ID = ExpandedNodeId.parse(String.format(
        "nsu=%s;s=%s",
        DrvNamespace.NAMESPACE_URI,
        "DataType.CustomEnumType"
    ));

    private final int value;

    CustomEnumType(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Nullable
    public static CustomEnumType from(int value) {
        switch (value) {
            case 0:
                return Field0;
            case 1:
                return Field1;
            case 2:
                return Field2;
            default:
                return null;
        }
    }

    public static class Codec extends GenericDataTypeCodec<CustomEnumType> {
        @Override
        public Class<CustomEnumType> getType() {
            return CustomEnumType.class;
        }

        @Override
        public CustomEnumType decode(SerializationContext context, UaDecoder decoder) {
            return CustomEnumType.from(decoder.readInt32(null));
        }

        @Override
        public void encode(SerializationContext context, UaEncoder encoder, CustomEnumType value) {
            encoder.writeInt32(null, value.getValue());
        }
    }

}
