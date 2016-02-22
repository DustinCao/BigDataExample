package com.infobird.kafka.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

public class CosumerMain {

	public static void main(String[] arg) {
        String[] args = { "slave02.infobird.com:2181", "group-1", "topic_7"};
        String zookeeper = args[0];
        String groupId = args[1];
        String topic = args[2];
        
        ConsumerConnector consumer = Consumer.createJavaConsumerConnector(createConsumerConfig(zookeeper, groupId));
        
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(1));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
	
		List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
		
		System.out.println("=====begin=====");
		for (KafkaStream kafkaStream : streams) {
			ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
			
			while(it.hasNext()) {
				MessageAndMetadata<byte[], byte[]> message = it.next();
				String key = message.key() != null? new String(message.key()) : "";
				System.out.println("message:" + new String(message.message()) 
				                 + "key:" + key
				                 + "topic:" + new String(message.topic()));
				//System.out.println("=====middle=====");
			}
		}
		
		System.out.println("===== end =====");
		consumer.shutdown();
	}
	
	private static ConsumerConfig createConsumerConfig(String a_zookeeper,
			String a_groupId) {
		Properties props = new Properties();
		props.put("zookeeper.connect", a_zookeeper);
	    props.put("group.id", a_groupId);
	    props.put("zookeeper.session.timeout.ms", "15000");
	    props.put("zookeeper.sync.time.ms", "200");
	    props.put("auto.commit.interval.ms", "1000");
	    
	    return new ConsumerConfig(props);
	}
}
