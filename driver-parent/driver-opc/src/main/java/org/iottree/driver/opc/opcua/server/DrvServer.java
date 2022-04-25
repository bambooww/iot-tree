package org.iottree.driver.opc.opcua.server;

import java.io.File;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig;
import org.eclipse.milo.opcua.sdk.server.identity.CompositeValidator;
import org.eclipse.milo.opcua.sdk.server.identity.UsernameIdentityValidator;
import org.eclipse.milo.opcua.sdk.server.identity.X509IdentityValidator;
import org.eclipse.milo.opcua.sdk.server.util.HostnameUtil;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.UaRuntimeException;
import org.eclipse.milo.opcua.stack.core.security.DefaultCertificateManager;
import org.eclipse.milo.opcua.stack.core.security.DefaultTrustListManager;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.transport.TransportProfile;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MessageSecurityMode;
import org.eclipse.milo.opcua.stack.core.types.structured.BuildInfo;
import org.eclipse.milo.opcua.stack.core.util.CertificateUtil;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateGenerator;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedHttpsCertificateBuilder;
import org.eclipse.milo.opcua.stack.server.EndpointConfiguration;
import org.eclipse.milo.opcua.stack.server.security.DefaultServerCertificateValidator;
import org.iottree.core.Config;
import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.service.AbstractService;
import org.slf4j.LoggerFactory;

import static com.google.common.collect.Lists.newArrayList;
import static org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig.USER_TOKEN_POLICY_ANONYMOUS;
import static org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig.USER_TOKEN_POLICY_USERNAME;
import static org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig.USER_TOKEN_POLICY_X509;

public class DrvServer //extends AbstractService
{

	private static final int TCP_BIND_PORT = 12686;
	private static final int HTTPS_BIND_PORT = 8443;

	static
	{
		// Required for SecurityPolicy.Aes256_Sha256_RsaPss
		Security.addProvider(new BouncyCastleProvider());
	}


	private final OpcUaServer server;
	private final PrjsNamespace prjNS;
	private DrvNamespace drvNamespace;
	
	//private List<PrjsNamespace> prjNamespaces;
	
	static final String PRODUCT_URI = "urn:iottree:server" ;

	public DrvServer() throws Exception
	{
		String dirb = Config.getDataDirBase() ;
		if(dirb==null)
			throw new RuntimeException("no DataDirBase found") ;
		File security_dir = new File(Config.getDataDirBase(), "security");
		if (!security_dir.exists() && !security_dir.mkdirs())
		{
			throw new Exception("unable to create security dir: " + security_dir);
		}
		LoggerFactory.getLogger(getClass()).info("security dir: {}", security_dir.getAbsolutePath());

		KeyStoreLoader loader = new KeyStoreLoader().load(security_dir);

		DefaultCertificateManager cer_mgr = new DefaultCertificateManager(loader.getServerKeyPair(),
				loader.getServerCertificateChain());

		File pki_dir = security_dir.toPath().resolve("pki").toFile();
		DefaultTrustListManager trust_list_mgr = new DefaultTrustListManager(pki_dir);
		LoggerFactory.getLogger(getClass()).info("pki dir: {}", pki_dir.getAbsolutePath());

		DefaultServerCertificateValidator cert_validator = new DefaultServerCertificateValidator(trust_list_mgr);

		KeyPair https_keypair = SelfSignedCertificateGenerator.generateRsaKeyPair(2048);

		SelfSignedHttpsCertificateBuilder https_cer_builder = new SelfSignedHttpsCertificateBuilder(https_keypair);
		https_cer_builder.setCommonName(HostnameUtil.getHostname());
		HostnameUtil.getHostnames("0.0.0.0").forEach(https_cer_builder::addDnsName);
		X509Certificate https_cer = https_cer_builder.build();

		UsernameIdentityValidator identity_validator = new UsernameIdentityValidator(true, authChallenge -> {
			String username = authChallenge.getUsername();
			String password = authChallenge.getPassword();

			boolean userok = "user".equals(username) && "password1".equals(password);
			boolean adminok = "admin".equals(username) && "password2".equals(password);

			return userok || adminok;
		});

		X509IdentityValidator x509_id_validator = new X509IdentityValidator(c -> true);

		// If you need to use multiple certificates you'll have to be smarter
		// than this.
		X509Certificate certificate = cer_mgr.getCertificates().stream().findFirst()
				.orElseThrow(() -> new UaRuntimeException(StatusCodes.Bad_ConfigurationError, "no certificate found"));

		// The configured application URI must match the one in the
		// certificate(s)
		String app_uri = CertificateUtil.getSanUri(certificate)
				.orElseThrow(() -> new UaRuntimeException(StatusCodes.Bad_ConfigurationError,
						"certificate is missing the application URI"));

		Set<EndpointConfiguration> epconfigs = createEndpointConfigurations(certificate);

		OpcUaServerConfig serverConfig = OpcUaServerConfig.builder()
				.setApplicationUri(app_uri)
				.setApplicationName(LocalizedText.english("IOTTree OPC UA Server"))
				.setEndpoints(epconfigs)
				.setBuildInfo(new BuildInfo(PRODUCT_URI, "iottree", "iottree opc ua server",
						OpcUaServer.SDK_VERSION, "", DateTime.now()))
				.setCertificateManager(cer_mgr).setTrustListManager(trust_list_mgr)
				.setCertificateValidator(cert_validator).setHttpsKeyPair(https_keypair)
				.setHttpsCertificate(https_cer)
				.setIdentityValidator(new CompositeValidator<>(identity_validator, x509_id_validator))
				.setProductUri(PRODUCT_URI).build();

		server = new OpcUaServer(serverConfig);

		//prjNS = null ;
		prjNS = new PrjsNamespace(server);
		prjNS.startup();
		
		//drvNamespace= new DrvNamespace(server);
		//drvNamespace.startup();
	}

	private Set<EndpointConfiguration> createEndpointConfigurations(X509Certificate certificate)
	{
		Set<EndpointConfiguration> epconfigs = new LinkedHashSet<>();

		List<String> bind_addrs = newArrayList();
		bind_addrs.add("0.0.0.0");

		Set<String> hostnames = new LinkedHashSet<>();
		hostnames.add(HostnameUtil.getHostname());
		hostnames.addAll(HostnameUtil.getHostnames("0.0.0.0"));
		
//		for(UAPrj prj:UAManager.getInstance().listPrjs())
//		{

		for (String bind_addr : bind_addrs)
		{
			for (String hostname : hostnames)
			{
				EndpointConfiguration.Builder builder = EndpointConfiguration.newBuilder().setBindAddress(bind_addr)
						.setHostname(hostname)
						//.setPath("/iottree")
						.setCertificate(certificate).addTokenPolicies(
								USER_TOKEN_POLICY_ANONYMOUS, USER_TOKEN_POLICY_USERNAME, USER_TOKEN_POLICY_X509);

				EndpointConfiguration.Builder noSecurityBuilder = builder.copy().setSecurityPolicy(SecurityPolicy.None)
						.setSecurityMode(MessageSecurityMode.None);

				epconfigs.add(buildTcpEndpoint(noSecurityBuilder));
				epconfigs.add(buildHttpsEndpoint(noSecurityBuilder));

				// TCP Basic256Sha256 / SignAndEncrypt
				epconfigs
						.add(buildTcpEndpoint(builder.copy().setSecurityPolicy(SecurityPolicy.Basic256Sha256)
								.setSecurityMode(MessageSecurityMode.SignAndEncrypt)));

				// HTTPS Basic256Sha256 / Sign (SignAndEncrypt not allowed for
				// HTTPS)
				epconfigs.add(buildHttpsEndpoint(builder.copy()
						.setSecurityPolicy(SecurityPolicy.Basic256Sha256).setSecurityMode(MessageSecurityMode.Sign)));

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

				EndpointConfiguration.Builder discoveryBuilder = builder.copy().setPath("/discovery")
						.setSecurityPolicy(SecurityPolicy.None).setSecurityMode(MessageSecurityMode.None);

				epconfigs.add(buildTcpEndpoint(discoveryBuilder));
				epconfigs.add(buildHttpsEndpoint(discoveryBuilder));
			}
		}
//		}//end of loop prj

		return epconfigs;
	}

	private static EndpointConfiguration buildTcpEndpoint(EndpointConfiguration.Builder base)
	{
		return base.copy().setTransportProfile(TransportProfile.TCP_UASC_UABINARY).setBindPort(TCP_BIND_PORT).build();
	}

	private static EndpointConfiguration buildHttpsEndpoint(EndpointConfiguration.Builder base)
	{
		//TransportProfile.
		return base.copy().setTransportProfile(TransportProfile.HTTPS_UABINARY).setBindPort(HTTPS_BIND_PORT).build();
	}

	public OpcUaServer getServer()
	{
		return server;
	}

	public CompletableFuture<OpcUaServer> startup()
	{
		return server.startup();
	}

	public CompletableFuture<OpcUaServer> shutdown()
	{
		prjNS.shutdown();

		return server.shutdown();
	}

}
