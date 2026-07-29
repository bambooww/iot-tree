package org.iottree.driver.opc.opcua.client;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.identity.IdentityProvider;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;

public interface ClientExample {

  default String getEndpointUrl() {
    return "opc.tcp://localhost:12686/milo";
  }

  default Predicate<EndpointDescription> endpointFilter() {
    return e -> getSecurityPolicy().getUri().equals(e.getSecurityPolicyUri());
  }

  default SecurityPolicy getSecurityPolicy() {
    return SecurityPolicy.Basic256Sha256;
  }

  default IdentityProvider getIdentityProvider() {
    return new AnonymousProvider();
  }

  void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception;
}
