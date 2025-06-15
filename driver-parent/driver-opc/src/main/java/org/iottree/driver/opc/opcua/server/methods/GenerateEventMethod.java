package org.iottree.driver.opc.opcua.server.methods;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ushort;

import java.util.UUID;
import org.eclipse.milo.opcua.sdk.core.ValueRanks;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.methods.AbstractMethodInvocationHandler;
import org.eclipse.milo.opcua.sdk.server.model.objects.BaseEventTypeNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaMethodNode;
import org.eclipse.milo.opcua.stack.core.NodeIds;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.structured.Argument;

public class GenerateEventMethod extends AbstractMethodInvocationHandler {

  public static final Argument EVENT_TYPE_ID =
      new Argument(
          "EventTypeId",
          NodeIds.NodeId,
          ValueRanks.Scalar,
          null,
          new LocalizedText("NodeId of the TypeDefinition of the event to generate."));

  private final OpcUaServer server;

  public GenerateEventMethod(UaMethodNode methodNode) {
    super(methodNode);

    this.server = methodNode.getNodeContext().getServer();
  }

  @Override
  public Argument[] getInputArguments() {
    return new Argument[] {EVENT_TYPE_ID};
  }

  @Override
  public Argument[] getOutputArguments() {
    return new Argument[0];
  }

  @Override
  protected Variant[] invoke(InvocationContext invocationContext, Variant[] inputValues)
      throws UaException {
    NodeId eventTypeId = (NodeId) inputValues[0].value();

    BaseEventTypeNode eventNode =
        server.getEventFactory().createEvent(new NodeId(1, UUID.randomUUID()), eventTypeId);

    eventNode.setBrowseName(new QualifiedName(1, "foo"));
    eventNode.setDisplayName(LocalizedText.english("foo"));
    eventNode.setEventId(ByteString.of(new byte[] {0, 1, 2, 3}));
    eventNode.setEventType(NodeIds.BaseEventType);
    eventNode.setSourceNode(getNode().getNodeId());
    eventNode.setSourceName(getNode().getDisplayName().text());
    eventNode.setTime(DateTime.now());
    eventNode.setReceiveTime(DateTime.NULL_VALUE);
    eventNode.setMessage(LocalizedText.english("event message!"));
    eventNode.setSeverity(ushort(2));

    server.getEventNotifier().fire(eventNode);

    eventNode.delete();

    return new Variant[0];
  }
}
