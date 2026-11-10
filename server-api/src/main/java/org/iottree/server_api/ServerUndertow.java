package org.iottree.server_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.StatusCodes;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.iottree.core.Config;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

public class ServerUndertow
{
	private static ILogger log = LoggerManager.getLogger(ServerUndertow.class) ;
	
	private static final ObjectMapper JSON = new ObjectMapper();
	private static final Map<Integer, User> USERS = new ConcurrentHashMap<>();
	private Undertow server = null;

	public synchronized void start(int port)
	{
		if(server!=null)
			return ;
		
		server = this.startNor(port) ;
	}
	
	private Undertow startNor(int port)
	{
		Undertow server = Undertow.builder().addHttpListener(port, "0.0.0.0").setHandler(createHandler()).build();
		server.start();
		
		if(log.isInfoEnabled())
			log.info("Undertow started,port:" + port);
		return server ;
	}
	
	private Undertow startSSL(int port) throws Exception
	{//8443
		// load key store
		KeyStore keyStore = KeyStore.getInstance("JKS");
		try (FileInputStream fis = new FileInputStream("path/to/keystore.jks")) {
		    keyStore.load(fis, "password".toCharArray());
		}

		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(keyStore, "password".toCharArray());

		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(kmf.getKeyManagers(), null, null);

		// 用 addHttpsListener 替代 addHttpListener
		Undertow server = Undertow.builder()
		    .addHttpsListener(port, "0.0.0.0", sslContext)
		    .setHandler(createHandler())
		    .build();
		server.start();
		if(log.isInfoEnabled())
			log.info("Undertow ssl started,port:" + port);
		return server ;
	}
	
	private PathHandler createHandler()
	{
		String webbase = Config.getWebappBase() ;
		
	    HttpHandler apiHandler = new RouterHandler();

	    FileResourceManager fres = new FileResourceManager(Paths.get(webbase).toFile(), 100);
	    
	    ResourceHandler staticHandler = new ResourceHandler(fres);
	    staticHandler.setWelcomeFiles("index.html"); // 访问目录时默认返回 index.html
	    staticHandler.setDirectoryListingEnabled(false); // 关闭目录列表，更安全

	    // 3. PathHandler for router
	    PathHandler pathHandler = new PathHandler()
	        .addPrefixPath("/api", apiHandler)
	        .addPrefixPath("/", staticHandler); 
	    return pathHandler ;
	}

	public synchronized void stop()
	{
		if (server == null)
			return ;
		try
		{
			server.stop();
			if(log.isInfoEnabled())
				log.info("Undertow stopped");
		}
		finally
		{
			server = null ;
		}
	}

	// by path and method
	static class RouterHandler implements HttpHandler
	{
		@Override
		public void handleRequest(HttpServerExchange exchange) throws Exception
		{
			String path = exchange.getRequestPath();
			String method = exchange.getRequestMethod().toString();

			try
			{
				// GET /api/users/{id}
				if (path.matches("/api/users/\\d+") && "GET".equals(method))
				{
					handleGetUser(exchange, Integer.parseInt(path.substring(path.lastIndexOf('/') + 1)));
				}
				// POST /api/users
				else if ("/api/users".equals(path) && "POST".equals(method))
				{
					handleCreateUser(exchange);
				}
				// GET /api/hello
				else if ("/api/hello".equals(path) && "GET".equals(method))
				{
					sendJson(exchange, StatusCodes.OK, Map.of("message", "hello from undertow"));
				}
				else
				{
					sendJson(exchange, StatusCodes.NOT_FOUND, Map.of("error", "not found"));
				}
			}
			catch ( Exception e)
			{
				sendJson(exchange, StatusCodes.INTERNAL_SERVER_ERROR,
						Map.of("error", e.getMessage() == null ? "unknown" : e.getMessage()));
			}
		}
	}

	// GET /api/users/{id}
	static void handleGetUser(HttpServerExchange exchange, int id) throws Exception
	{
		User user = USERS.get(id);
		if (user == null)
		{
			sendJson(exchange, StatusCodes.NOT_FOUND, Map.of("error", "user not found"));
		}
		else
		{
			sendJson(exchange, StatusCodes.OK, user);
		}
	}

	// POST /api/users 请求体: {"name":"xxx"}
	static void handleCreateUser(HttpServerExchange exchange) throws Exception
	{
		exchange.startBlocking(); // 必须：让 Undertow 用阻塞方式读取请求体
		String body;
		try (InputStream is = exchange.getInputStream())
		{
			body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
		}
		User input = JSON.readValue(body, User.class);
		int id = USERS.size() + 1;
		input.id = id;
		USERS.put(id, input);
		sendJson(exchange, StatusCodes.CREATED, input);
	}

	// 
	static void sendJson(HttpServerExchange exchange, int status, Object obj) throws Exception
	{
		exchange.setStatusCode(status);
		exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json; charset=utf-8");
		String json = JSON.writeValueAsString(obj);
		exchange.getResponseSender().send(json);
	}

	// 简单 POJO
	public static class User
	{
		public int id;
		public String name;

		public User()
		{
		} // Jackson 反序列化需要无参构造

		public User(int id, String name)
		{
			this.id = id;
			this.name = name;
		}
	}

//	public static void main(String[] args) throws Exception
//	{
//		start(9090);
//		Thread.sleep(600_000);
//	}
}