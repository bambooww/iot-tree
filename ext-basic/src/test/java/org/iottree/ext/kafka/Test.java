package org.iottree.ext.kafka;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.iottree.ext.roa.ROAKafka;

import junit.framework.TestCase;

public class Test extends TestCase
{
	public void testOpcServers() throws InterruptedException, ExecutionException, TimeoutException
	{
		ROAKafka kc= new ROAKafka(null).asBroker("192.168.18.19", 9092) ;
		kc.RT_start();
		
		ProducerRecord<String, String> record = new ProducerRecord<String, String>("gq_rt_data", "asdfasdfasdf"+TimeUnit.MILLISECONDS.toMillis(1)) ;
		kc.send(record);
	}
}
