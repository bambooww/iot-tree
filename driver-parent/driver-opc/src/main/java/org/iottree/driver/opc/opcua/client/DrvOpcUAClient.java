package org.iottree.driver.opc.opcua.client;


import java.util.*;
import java.math.*;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.security.*;
import java.security.Security;


import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.stack.client.UaStackClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

public class DrvOpcUAClient
{
	static {
        // Required for SecurityPolicy.Aes256_Sha256_RsaPss
        Security.addProvider(new BouncyCastleProvider());
    }
	
//    public void createCert(String issuer,Date notBefore,Date notAfter,String certDestPath,
//            BigInteger serial,String keyPassword,String alias) throws Exception{
//        //产生公私钥对
//        KeyPairGenerator kpg = KeyPairGenerator.getInstance(Default_KeyPairGenerator);
//        kpg.initialize(Default_KeySize);
//        KeyPair keyPair = kpg.generateKeyPair();
//        PublicKey publicKey = keyPair.getPublic();  
//        PrivateKey privateKey = keyPair.getPrivate();  
//        // 组装证书
//        X500Name issueDn = new X500Name(issuer);  
//        X500Name subjectDn = new X500Name(issuer);  
//        //组装公钥信息  
//        SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo  
//                 .getInstance(new ASN1InputStream(publicKey.getEncoded())  
//                         .readObject());
//
//        X509v3CertificateBuilder builder = new X509v3CertificateBuilder(  
//                issueDn, serial, notBefore, notAfter, subjectDn,  
//                subjectPublicKeyInfo); 
//         //证书的签名数据  
//        ContentSigner sigGen = new JcaContentSignerBuilder(Default_Signature).build(privateKey);  
//        X509CertificateHolder holder = builder.build(sigGen); 
//        byte[] certBuf = holder.getEncoded();  
//        X509Certificate certificate = (X509Certificate) CertificateFactory.getInstance(cert_type).generateCertificate(new ByteArrayInputStream(certBuf));  
//        // 创建KeyStore,存储证书
//        KeyStore store = KeyStore.getInstance(Default_keyType);
//        store.load(null, null);
//        store.setKeyEntry(alias, keyPair.getPrivate(),   
//                 keyPassword.toCharArray(), new Certificate[] { certificate });
//        FileOutputStream fout =new FileOutputStream(certDestPath);
//        store.store(fout, keyPassword.toCharArray());       
//        fout.close(); 
//    }
	
	private OpcUaClient createClient(String opcurl) throws Exception {
        Path securityTempDir = Paths.get(System.getProperty("java.io.tmpdir"), "security");
        Files.createDirectories(securityTempDir);
        if (!Files.exists(securityTempDir)) {
            throw new Exception("unable to create security dir: " + securityTempDir);
        }

        KeyStoreLoader loader = new KeyStoreLoader().load(securityTempDir);
        
        return OpcUaClient.create( opcurl,
            endpoints ->endpoints.stream().filter(e->true).findFirst(),
            configBuilder ->
                configBuilder
                    .setApplicationName(LocalizedText.english("eclipse milo opc-ua client"))
                    .setApplicationUri("urn:eclipse:milo:examples:client")
                    .setCertificate(loader.getClientCertificate())
                    .setKeyPair(loader.getClientKeyPair())
                    .setIdentityProvider(new AnonymousProvider())
                    .setRequestTimeout(uint(5000))
                    .build()
        );
    }
	
	private static void test1()
	{
		//String opcurl = "opc.tcp://localhost:49320";
		//UaStackClient.
		
	}
	public static void main(String[] args) throws InterruptedException, ExecutionException
	{
		
	}
}
