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
