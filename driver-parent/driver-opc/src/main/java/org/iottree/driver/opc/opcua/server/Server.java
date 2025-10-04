package org.iottree.driver.opc.opcua.server;

import static org.eclipse.milo.opcua.sdk.server.OpcUaServerConfig.USER_TOKEN_POLICY_ANONYMOUS;
import static org.eclipse.milo.opcua.sdk.server.OpcUaServerConfig.USER_TOKEN_POLICY_USERNAME;
import static org.eclipse.milo.opcua.sdk.server.OpcUaServerConfig.USER_TOKEN_POLICY_X509;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.milo.opcua.sdk.server.EndpointConfig;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.OpcUaServerConfig;
import org.eclipse.milo.opcua.sdk.server.identity.AnonymousIdentityValidator;
import org.eclipse.milo.opcua.sdk.server.identity.CompositeValidator;
import org.eclipse.milo.opcua.sdk.server.identity.UsernameIdentityValidator;
import org.eclipse.milo.opcua.sdk.server.identity.X509IdentityValidator;
import org.eclipse.milo.opcua.sdk.server.util.HostnameUtil;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.UaRuntimeException;
import org.eclipse.milo.opcua.stack.core.security.DefaultApplicationGroup;
import org.eclipse.milo.opcua.stack.core.security.DefaultCertificateManager;
import org.eclipse.milo.opcua.stack.core.security.DefaultServerCertificateValidator;
import org.eclipse.milo.opcua.stack.core.security.FileBasedCertificateQuarantine;
import org.eclipse.milo.opcua.stack.core.security.FileBasedTrustListManager;
import org.eclipse.milo.opcua.stack.core.security.KeyStoreCertificateStore;
import org.eclipse.milo.opcua.stack.core.security.RsaSha256CertificateFactory;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.transport.TransportProfile;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MessageSecurityMode;
import org.eclipse.milo.opcua.stack.core.types.structured.BuildInfo;
import org.eclipse.milo.opcua.stack.core.util.CertificateUtil;
import org.eclipse.milo.opcua.stack.core.util.NonceUtil;
import org.eclipse.milo.opcua.stack.transport.server.tcp.OpcTcpServerTransport;
import org.eclipse.milo.opcua.stack.transport.server.tcp.OpcTcpServerTransportConfig;
import org.iottree.core.Config;
import org.iottree.core.UAPrj;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.slf4j.LoggerFactory;

public class Server
{
	private static final int TCP_BIND_PORT = 12686;
	
	static ILogger log = LoggerManager.getLogger(Server.class) ;

	static KeyStoreLoader loader ;

	
	static
	{
		// Required for SecurityPolicy.Aes256_Sha256_RsaPss
		Security.addProvider(new BouncyCastleProvider());

		try
		{
			NonceUtil.blockUntilSecureRandomSeeded(10, TimeUnit.SECONDS);
			
			loader = new KeyStoreLoader().load(KeyStoreLoader.securityTempDir);
		}
		catch ( Exception e)
		{
			e.printStackTrace();
			//System.exit(-1);
		}
		

		
	}

//	public static void main(String[] args) throws Exception
//	{
//		Server server = new Server();
//
//		server.startup().get();
//
//		final CompletableFuture<Void> future = new CompletableFuture<>();
//
//		Runtime.getRuntime().addShutdownHook(new Thread(() -> future.complete(null)));
//
//		future.get();
//	}
	
	private final OpcUAService service ;

	private final OpcUaServer server;
	//private final SerNamespace serNS;\
	private HashMap<String,PrjNamespace> prjn2ns = null;// new HashMap<>() ;

	public Server(OpcUAService service) throws Exception
	{
		this.service = service ;
		
		var certificateStore = KeyStoreCertificateStore.createAndInitialize(
				new KeyStoreCertificateStore.Settings(KeyStoreLoader.securityTempDir.resolve("iottree-server.pfx"),
						"password"::toCharArray, alias -> "password".toCharArray()));

		var trustListManager = FileBasedTrustListManager.createAndInitialize(KeyStoreLoader.pkiDir.toPath());
		
		var certificateQuarantine = FileBasedCertificateQuarantine
				.create(KeyStoreLoader.pkiDir.toPath().resolve("rejected").resolve("certs"));
		
		var certificateFactory = new RsaSha256CertificateFactory() {
			@Override
			protected KeyPair createRsaSha256KeyPair()
			{
				return loader.getServerKeyPair();
			}

			@Override
			protected X509Certificate[] createRsaSha256CertificateChain(KeyPair keyPair)
			{
				return loader.getServerCertificateChain();
			}
		};

		var certificateValidator = new DefaultServerCertificateValidator(trustListManager, certificateQuarantine);

		var defaultGroup = DefaultApplicationGroup.createAndInitialize(trustListManager, certificateStore,
				certificateFactory, certificateValidator);

		var certificateManager = new DefaultCertificateManager(certificateQuarantine, defaultGroup);

		var identityValidator = new UsernameIdentityValidator(authChallenge -> {
			String username = authChallenge.getUsername();
			String password = authChallenge.getPassword();

			//boolean userOk = "user".equals(username) && "password1".equals(password);
			//boolean adminOk = "admin".equals(username) && "password2".equals(password);

			//return userOk || adminOk;
			return this.service.checkUserPsw(username, password) ;
		});

		var x509IdentityValidator = new X509IdentityValidator(c -> true);

		X509Certificate certificate = loader.getServerCertificate();

		// The configured application URI must match the one in the
		// certificate(s)
		String applicationUri = CertificateUtil.getSanUri(certificate)
				.orElseThrow(() -> new UaRuntimeException(StatusCodes.Bad_ConfigurationError,
						"certificate is missing the application URI"));

		Set<EndpointConfig> endpointConfigurations = createEndpointConfigs(certificate);

		OpcUaServerConfig serverConfig = OpcUaServerConfig.builder().setApplicationUri(applicationUri)
				.setApplicationName(LocalizedText.english("IOTTree OPC UA Server"))
				.setEndpoints(endpointConfigurations)
				.setBuildInfo(new BuildInfo("urn:iottree:server", "iottree", "iottree opc ua server",
						OpcUaServer.SDK_VERSION, "", DateTime.now()))
				.setCertificateManager(certificateManager)
				.setIdentityValidator(new CompositeValidator(AnonymousIdentityValidator.INSTANCE, identityValidator,
						x509IdentityValidator))
				.setProductUri("urn:iottree:server").build();

		server = new OpcUaServer(serverConfig, transportProfile -> {
			assert transportProfile == TransportProfile.TCP_UASC_UABINARY;

			OpcTcpServerTransportConfig transportConfig = OpcTcpServerTransportConfig.newBuilder().build();

			return new OpcTcpServerTransport(transportConfig);
		});

		
	}

	private Set<EndpointConfig> createEndpointConfigs(X509Certificate certificate)
	{
		var endpointConfigs = new LinkedHashSet<EndpointConfig>();

		List<String> bindAddresses = List.of("0.0.0.0");

		var hostnames = new LinkedHashSet<String>();
		hostnames.add(HostnameUtil.getHostname());
		hostnames.addAll(HostnameUtil.getHostnames("0.0.0.0", true, false));

		for (String bindAddress : bindAddresses)
		{
			for (String hostname : hostnames)
			{
				EndpointConfig.Builder builder = EndpointConfig.newBuilder().setBindAddress(bindAddress)
						.setHostname(hostname)
						//.setPath("/iottree")
						.setCertificate(certificate).addTokenPolicies(
								USER_TOKEN_POLICY_ANONYMOUS, USER_TOKEN_POLICY_USERNAME, USER_TOKEN_POLICY_X509);

				EndpointConfig.Builder noSecurityBuilder = builder.copy().setSecurityPolicy(SecurityPolicy.None)
						.setSecurityMode(MessageSecurityMode.None);

				endpointConfigs.add(buildTcpEndpoint(noSecurityBuilder));

				endpointConfigs.add(buildTcpEndpoint(builder.copy().setSecurityPolicy(SecurityPolicy.Basic256Sha256)
						.setSecurityMode(MessageSecurityMode.Sign)));
				
				// TCP Basic256Sha256 / SignAndEncrypt
				endpointConfigs.add(buildTcpEndpoint(builder.copy().setSecurityPolicy(SecurityPolicy.Basic256Sha256)
						.setSecurityMode(MessageSecurityMode.SignAndEncrypt)));
				
				

				/*
				 * It's good practice to provide a discovery-specific endpoint
				 * with no security. It's required practice if all regular
				 * endpoints have security configured.
				 *
				 * Usage of the "/discovery" suffix is defined by OPC UA Part 6:
				 *
				 * Each OPC UA Server Application implements the Discovery
				 * Service Set. If the OPC UA Server requires a different
				 * address for this Endpoint it shall create the address by
				 * appending the path "/discovery" to its base address.
				 */

				EndpointConfig.Builder discoveryBuilder = builder.copy()
						//.setPath("/iottree/discovery")
						.setSecurityPolicy(SecurityPolicy.None).setSecurityMode(MessageSecurityMode.None);

				endpointConfigs.add(buildTcpEndpoint(discoveryBuilder));
			}
		}

		return endpointConfigs;
	}

	private static EndpointConfig buildTcpEndpoint(EndpointConfig.Builder base)
	{
		return base.copy().setTransportProfile(TransportProfile.TCP_UASC_UABINARY).setBindPort(TCP_BIND_PORT).build();
	}

	public OpcUaServer getServer()
	{
		return server;
	}

	synchronized public CompletableFuture<OpcUaServer> startup()
	{
		if(prjn2ns!=null)
			return null;
		HashMap<String,PrjNamespace> p2n = new HashMap<>() ;
		List<UAPrj> prjs = this.service.getPrjs() ;
		if(prjs!=null)
		{
			for(UAPrj prj:prjs)
			{
				PrjNamespace pns = new PrjNamespace(server,prj);
				pns.startup();
				p2n.put(prj.getName(), pns) ;
			}
		}
		prjn2ns = p2n ;
		return server.startup();
	}

	synchronized public CompletableFuture<OpcUaServer> shutdown()
	{
		//serNS.shutdown();
		if(prjn2ns!=null)
		{
			for(PrjNamespace ns:prjn2ns.values())
				ns.shutdown();
			
			prjn2ns = null ;
		}
		return server.shutdown();
	}
}
