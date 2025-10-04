package org.iottree.driver.opc.opcua.server;

import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.eclipse.milo.opcua.sdk.server.util.HostnameUtil;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateBuilder;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateGenerator;
import org.iottree.core.Config;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyStoreLoader
{

	private static final Pattern IP_ADDR_PATTERN = Pattern
			.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

	private static final String SERVER_ALIAS = "iottree";
	private static final char[] PASSWORD = "password".toCharArray();

	private static ILogger logger = LoggerManager.getLogger(KeyStoreLoader.class);
	
	public static class CertItem
	{
		public Path file ;
		public X509Certificate cert ;
		
		public CertItem(Path file, X509Certificate cert)
		{
			this.file = file ;
			this.cert = cert ;
		}
		
		public JSONObject toJO()
		{
			JSONObject ret = new JSONObject() ;
			ret.put("file_dt", file.toFile().lastModified()) ;
			ret.put("fn",file.toFile().getName()) ;
			ret.put("fp", file.toString()) ;
			ret.put("subject", cert.getSubjectX500Principal()) ;
			ret.put("issuer", cert.getIssuerX500Principal()) ;
			ret.put("serial", cert.getSerialNumber().toString(16)) ;
			ret.put("st", cert.getNotBefore().getTime()) ;
			ret.put("et", cert.getNotAfter().getTime()) ;
			return ret ;
		}
	}

	static String tmpdir;
	static Path securityTempDir;
	static File pkiDir;

	static
	{
		try
		{
			tmpdir = Config.getDataTmpDir();
			securityTempDir = Paths.get(tmpdir, "opc_ua_ser", "security");
			Files.createDirectories(securityTempDir);
			if (!Files.exists(securityTempDir))
			{
				throw new Exception("unable to create security temp dir: " + securityTempDir);
			}

			pkiDir = securityTempDir.resolve("pki").toFile();

			logger.info("security dir: " + securityTempDir.toAbsolutePath());
			logger.info("security pki dir: " + pkiDir.getAbsolutePath());

		}
		catch ( Exception ee)
		{
			ee.printStackTrace();
		}
	}

	public static List<CertItem> listTrustedCers() throws IOException
	{
		Path p = securityTempDir.resolve("pki").resolve("trusted").resolve("certs");// Paths.get("security/pki/rejected");
		return listCers(p);
	}

	public static List<CertItem> listRejectedCers() throws IOException
	{
		Path p = securityTempDir.resolve("pki").resolve("rejected").resolve("certs");// Paths.get("security/pki/rejected");
		return listCers(p);
	}

	private static List<CertItem> listCers(Path path) throws IOException
	{
		ArrayList<CertItem> rets = new ArrayList<>();
		if(!path.toFile().exists())
			return rets ;
		// 1. 遍历目录，只拿证书文件
		try (Stream<Path> paths = Files.list(path))
		{
			paths.filter(p -> {
				String name = p.getFileName().toString().toLowerCase();
				return name.endsWith(".der") || name.endsWith(".crt") || name.endsWith(".cer");
			}).forEach((file) -> {
				try (InputStream in = Files.newInputStream(file))
				{
					CertificateFactory cf = CertificateFactory.getInstance("X.509");
					X509Certificate cert = (X509Certificate) cf.generateCertificate(in);
					rets.add(new CertItem(file,cert));
				}
				catch ( Exception e)
				{
					System.err.println("parse cert err:" + file + " - " + e.getMessage());
				}
			});
		}
		return rets;
	}
	
	public static Path calFilePath(boolean b_trusted,String filen)
	{
		if(b_trusted)
			return securityTempDir.resolve("pki").resolve("trusted").resolve("certs").resolve(filen);
		else
			return securityTempDir.resolve("pki").resolve("rejected").resolve("certs").resolve(filen);
	}
	
	public static CertItem getCerByFileName(boolean b_trusted,String filen) throws Exception
	{
		Path fp = calFilePath(b_trusted,filen) ;
		try (InputStream in = Files.newInputStream(fp))
		{
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate cert = (X509Certificate) cf.generateCertificate(in);
			return new CertItem(fp,cert);
		}
	}
	
	public static boolean trustCer(String filen,StringBuilder failedr) throws Exception
	{
		CertItem ci = getCerByFileName(false, filen) ;
		if(ci==null)
		{
			failedr.append("no rejected certificate found with name="+filen) ;
			return false;
		}
		Path tar_p = calFilePath(true,filen) ;
		return ci.file.toFile().renameTo(tar_p.toFile()) ;
	}
	
	public static boolean rejectCer(String filen,StringBuilder failedr) throws Exception
	{
		CertItem ci = getCerByFileName(true, filen) ;
		if(ci==null)
		{
			failedr.append("no trusted certificate found with name="+filen) ;
			return false;
		}
		Path tar_p = calFilePath(false,filen) ;
		return ci.file.toFile().renameTo(tar_p.toFile()) ;
	}
	
	public static boolean deleteCer(boolean b_trusted,String filen) throws Exception
	{
		CertItem ci = getCerByFileName(b_trusted, filen) ;
		if(ci==null)
		{
			return false;
		}
		return ci.file.toFile().delete() ;
	}

	private X509Certificate[] serverCertificateChain;
	private X509Certificate serverCertificate;
	private KeyPair serverKeyPair;

	KeyStoreLoader load(Path baseDir) throws Exception
	{
		KeyStore keyStore = KeyStore.getInstance("PKCS12");

		File serverKeyStore = baseDir.resolve("iottree-server.pfx").toFile();

		logger.info("Loading KeyStore at " + serverKeyStore);

		if (!serverKeyStore.exists())
		{
			keyStore.load(null, PASSWORD);

			KeyPair keyPair = SelfSignedCertificateGenerator.generateRsaKeyPair(2048);

			String applicationUri = "urn:iottree:server:" + UUID.randomUUID();

			SelfSignedCertificateBuilder builder = new SelfSignedCertificateBuilder(keyPair)
					.setCommonName("IOTTree OPC UA Server").setOrganization("iottree").setOrganizationalUnit("dev")
					// .setLocalityName("Folsom")
					.setStateName("CA").setCountryCode("CN").setApplicationUri(applicationUri);

			// Get as many hostnames and IP addresses as we can listed in the
			// certificate.
			Set<String> hostnames = Sets.union(Set.of(HostnameUtil.getHostname()),
					HostnameUtil.getHostnames("0.0.0.0", false));

			for (String hostname : hostnames)
			{
				if (IP_ADDR_PATTERN.matcher(hostname).matches())
				{
					builder.addIpAddress(hostname);
				}
				else
				{
					builder.addDnsName(hostname);
				}
			}

			X509Certificate certificate = builder.build();

			keyStore.setKeyEntry(SERVER_ALIAS, keyPair.getPrivate(), PASSWORD, new X509Certificate[] { certificate });
			keyStore.store(new FileOutputStream(serverKeyStore), PASSWORD);
		}
		else
		{
			keyStore.load(new FileInputStream(serverKeyStore), PASSWORD);
		}

		Key serverPrivateKey = keyStore.getKey(SERVER_ALIAS, PASSWORD);
		if (serverPrivateKey instanceof PrivateKey)
		{
			serverCertificate = (X509Certificate) keyStore.getCertificate(SERVER_ALIAS);

			serverCertificateChain = Arrays.stream(keyStore.getCertificateChain(SERVER_ALIAS))
					.map(X509Certificate.class::cast).toArray(X509Certificate[]::new);

			PublicKey serverPublicKey = serverCertificate.getPublicKey();
			serverKeyPair = new KeyPair(serverPublicKey, (PrivateKey) serverPrivateKey);
		}

		return this;
	}

	X509Certificate getServerCertificate()
	{
		return serverCertificate;
	}

	public X509Certificate[] getServerCertificateChain()
	{
		return serverCertificateChain;
	}

	KeyPair getServerKeyPair()
	{
		return serverKeyPair;
	}
}
