package org.iottree.driver.opc.opcua.server.types;

import org.eclipse.milo.opcua.stack.core.types.UaEnumeratedType;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.iottree.driver.opc.opcua.server.ExampleNamespace;
import org.jspecify.annotations.Nullable;

public enum CustomEnumType implements UaEnumeratedType {
  Field0(0),
  Field1(1),
  Field2(2);

  public static final ExpandedNodeId TYPE_ID =
      ExpandedNodeId.parse(
          String.format("nsu=%s;s=%s", ExampleNamespace.NAMESPACE_URI, "DataType.CustomEnumType"));

  private final int value;

  CustomEnumType(int value) {
    this.value = value;
  }

  @Override
  public int getValue() {
    return value;
  }

  @Override
  public ExpandedNodeId getTypeId() {
    return TYPE_ID;
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
}
