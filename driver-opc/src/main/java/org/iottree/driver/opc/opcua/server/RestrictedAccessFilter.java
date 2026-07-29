package org.iottree.driver.opc.opcua.server;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.sdk.server.Session;
import org.eclipse.milo.opcua.sdk.server.identity.Identity;
import org.eclipse.milo.opcua.sdk.server.nodes.filters.AttributeFilter;
import org.eclipse.milo.opcua.sdk.server.nodes.filters.AttributeFilterContext;
import org.eclipse.milo.opcua.stack.core.AttributeId;

public class RestrictedAccessFilter implements AttributeFilter {

  private static final Set<AccessLevel> INTERNAL_ACCESS = AccessLevel.READ_WRITE;

  private final Function<Identity, Set<AccessLevel>> accessLevelsFn;

  public RestrictedAccessFilter(Function<Identity, Set<AccessLevel>> accessLevelsFn) {
    this.accessLevelsFn = accessLevelsFn;
  }

  @Override
  public Object getAttribute(AttributeFilterContext ctx, AttributeId attributeId) {
    if (attributeId == AttributeId.UserAccessLevel) {
      Optional<Identity> identity = ctx.getSession().map(Session::getIdentity);

      Set<AccessLevel> accessLevels = identity.map(accessLevelsFn).orElse(INTERNAL_ACCESS);

      return AccessLevel.toValue(accessLevels);
    } else {
      return ctx.getAttribute(attributeId);
    }
  }
}
