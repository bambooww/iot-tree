package org.iottree.driver.opc.opcua.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import com.google.common.collect.Sets;

import org.eclipse.milo.opcua.sdk.server.util.HostnameUtil;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateBuilder;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class KeyStoreLoader
{

	private static final Pattern IP_ADDR_PATTERN = Pattern
			.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

	private static final String SERVER_ALIAS = "server-ai";
	private static final char[] PASSWORD = "password".toCharArray();

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private X509Certificate[] serverCerChain;
	private X509Certificate serverCer;
	private KeyPair serverKeyPair;

	KeyStoreLoader load(File baseDir) throws Exception
	{
		KeyStore keystore = KeyStore.getInstance("PKCS12");

		File server_keystore = baseDir.toPath().resolve("iottree-server.pfx").toFile();

		logger.info("Loading KeyStore at {}", server_keystore);

		if (!server_keystore.exists())
		{
			keystore.load(null, PASSWORD);

			KeyPair keyPair = SelfSignedCertificateGenerator.generateRsaKeyPair(2048);

			String app_uri = "urn:iottree:opcua:server:" + UUID.randomUUID();

			SelfSignedCertificateBuilder builder = new SelfSignedCertificateBuilder(keyPair)
					.setCommonName("IOTTree OPCUA Server").setOrganization("iottree")
					.setOrganizationalUnit("dev").setLocalityName("Beijing").setStateName("CA").setCountryCode("CN")
					.setApplicationUri(app_uri);

			// Get as many hostnames and IP addresses as we can listed in the
			// certificate.
			Set<String> hostnames = Sets.union(Sets.newHashSet(HostnameUtil.getHostname()),
					HostnameUtil.getHostnames("0.0.0.0", false));

			for (String hostname : hostnames)
			{
				if (IP_ADDR_PATTERN.matcher(hostname).matches())
				{
					builder.addIpAddress(hostname);
				} else
				{
					builder.addDnsName(hostname);
				}
			}

			X509Certificate certificate = builder.build();

			keystore.setKeyEntry(SERVER_ALIAS, keyPair.getPrivate(), PASSWORD, new X509Certificate[] { certificate });
			keystore.store(new FileOutputStream(server_keystore), PASSWORD);
		} else
		{
			keystore.load(new FileInputStream(server_keystore), PASSWORD);
		}

		Key serverPrivateKey = keystore.getKey(SERVER_ALIAS, PASSWORD);
		if (serverPrivateKey instanceof PrivateKey)
		{
			serverCer = (X509Certificate) keystore.getCertificate(SERVER_ALIAS);

			serverCerChain = Arrays.stream(keystore.getCertificateChain(SERVER_ALIAS))
					.map(X509Certificate.class::cast).toArray(X509Certificate[]::new);

			PublicKey serverPublicKey = serverCer.getPublicKey();
			serverKeyPair = new KeyPair(serverPublicKey, (PrivateKey) serverPrivateKey);
		}

		return this;
	}

	X509Certificate getServerCertificate()
	{
		return serverCer;
	}

	public X509Certificate[] getServerCertificateChain()
	{
		return serverCerChain;
	}

	KeyPair getServerKeyPair()
	{
		return serverKeyPair;
	}

}
