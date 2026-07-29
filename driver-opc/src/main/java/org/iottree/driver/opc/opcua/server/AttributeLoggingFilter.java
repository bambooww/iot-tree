package org.iottree.driver.opc.opcua.server;

import java.util.function.Predicate;
import org.eclipse.milo.opcua.sdk.server.nodes.filters.AttributeFilter;
import org.eclipse.milo.opcua.sdk.server.nodes.filters.AttributeFilterContext;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttributeLoggingFilter implements AttributeFilter {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final Predicate<AttributeId> attributePredicate;

  public AttributeLoggingFilter() {
    this(attributeId -> true);
  }

  public AttributeLoggingFilter(Predicate<AttributeId> attributePredicate) {
    this.attributePredicate = attributePredicate;
  }

  @Override
  public Object getAttribute(AttributeFilterContext ctx, AttributeId attributeId) {
    Object value = ctx.getAttribute(attributeId);

    // only log external reads
    if (attributePredicate.test(attributeId) && ctx.getSession().isPresent()) {
      logger.info(
          "get nodeId={} attributeId={} value={}", ctx.getNode().getNodeId(), attributeId, value);
    }

    return value;
  }

  @Override
  public void setAttribute(AttributeFilterContext ctx, AttributeId attributeId, Object value) {
    // only log external writes
    if (attributePredicate.test(attributeId) && ctx.getSession().isPresent()) {
      logger.info(
          "set nodeId={} attributeId={} value={}", ctx.getNode().getNodeId(), attributeId, value);
    }

    ctx.setAttribute(attributeId, value);
  }
}
