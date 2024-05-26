package org.iottree.ext.msg_net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNModule;
import org.iottree.core.msgnet.MNNode;
import org.json.JSONObject;

public class Kafka_M extends MNModule implements IMNRunner
{
	static ArrayList<MNNode> supNodes = new ArrayList<>() ;
	static
	{
		supNodes.add(new KafkaIn_NS()) ;
		supNodes.add(new KafkaOut_NE()) ;
	}
	
	@Override
	public String getTP()
	{
		return "kafka";
	}

	@Override
	public String getTPTitle()
	{
		return "Kafka";
	}
	
	@Override
	public String getColor()
	{
		return "#debed7";
	}

	@Override
	public String getIcon()
	{
		return "\\uf0ec";
	}
	
	protected List<MNNode> getSupportedNodes() 
	{
		return supNodes;
	}
	
	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return false;
	}

	@Override
	public JSONObject getParamJO()
	{
		return null;
	}

	@Override
	protected void setParamJO(JSONObject jo, long up_dt)
	{
		
	}
	
	//
	
	public static enum SecurityProto {
	    /** Un-authenticated, non-encrypted channel */
	    PLAINTEXT(0, "PLAINTEXT"),
	    /** SSL channel */
//	    SSL(1, "SSL"),
	    /** SASL authenticated, non-encrypted channel */
	    SASL_PLAINTEXT(2, "SASL_PLAINTEXT");
	    /** SASL authenticated, SSL channel */
//	    SASL_SSL(3, "SASL_SSL");

	    private static final Map<Short, SecurityProto> CODE_TO_SECURITY_PROTOCOL;
	    private static final List<String> NAMES;

	    static {
	        SecurityProto[] protocols = SecurityProto.values();
	        List<String> names = new ArrayList<>(protocols.length);
	        Map<Short, SecurityProto> codeToSecurityProtocol = new HashMap<>(protocols.length);
	        for (SecurityProto proto : protocols) {
	            codeToSecurityProtocol.put(proto.id, proto);
	            names.add(proto.name);
	        }
	        CODE_TO_SECURITY_PROTOCOL = Collections.unmodifiableMap(codeToSecurityProtocol);
	        NAMES = Collections.unmodifiableList(names);
	    }

	    /** The permanent and immutable id of a security protocol -- this can't change, and must match kafka.cluster.SecurityProtocol  */
	    public final short id;

	    /** Name of the security protocol. This may be used by client configuration. */
	    public final String name;

	    SecurityProto(int id, String name) {
	        this.id = (short) id;
	        this.name = name;
	    }

	    public static List<String> names() {
	        return NAMES;
	    }

	    public static SecurityProto forId(short id) {
	        return CODE_TO_SECURITY_PROTOCOL.get(id);
	    }

	    /** Case insensitive lookup by protocol name */
	    public static SecurityProto forName(String name) {
	        return SecurityProto.valueOf(name.toUpperCase(Locale.ROOT));
	    }

	}
	
	public static enum SaslMech
	{
		PLAIN(0, "PLAIN"),
		SCRAM_SHA_256(1, "SCRAM-SHA-256"),
		SCRAM_SHA_512(2, "SCRAM-SHA-512");

		
	    public final int id;

	    
	    public final String name;

	    SaslMech(int id, String name) {
	        this.id = (short) id;
	        this.name = name;
	    }

//	    public static List<String> names() {
//	        return NAMES;
//	    }

	    public static SaslMech fromId(int id)
	    {
	    	switch(id)
	    	{
	    	case 0:
	    		return PLAIN ;
	    	case 1:
	    		return SCRAM_SHA_256;
	    	case 2:
	    		return SCRAM_SHA_512;
	    	default:
	    		return PLAIN; 
	    	}
	    }
	}
	
	private KafkaProducer<String, String> producer;

	private KafkaConsumer<String, String> consumer;

	String brokerHost ;

	int brokerPort = 9092;
	
	/**
	 * producer send time out
	 */
	long sendTo = 1000 ;
	
	String user = "" ;
	
	String psw = "" ;
	
	int producerAck = 1 ;
	
	int producerRetries = 3 ;
	
	SecurityProto securityProto = SecurityProto.PLAINTEXT ;
	
	SaslMech saslMech  = SaslMech.PLAIN;

	@Override
	public boolean RT_init(StringBuilder failedr)
	{
		return false;
	}

	@Override
	public boolean RT_start(StringBuilder failedr)
	{
		return false;
	}

	@Override
	public void RT_stop()
	{
		
	}

	@Override
	public boolean RT_isRunning()
	{
		return false;
	}
}
