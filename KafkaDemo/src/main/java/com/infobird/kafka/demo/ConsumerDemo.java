package com.infobird.kafka.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class ConsumerDemo {

	private final ConsumerConnector consumer;
	private final String topic;
	private ExecutorService executor;
	
	public ConsumerDemo(String zookeeper, String groupId, String a_topic) {
		consumer = Consumer.createJavaConsumerConnector(createConsumerConfig(zookeeper, groupId));
		this.topic = a_topic;
		
	}
	
	public void shutdown() {
		if(consumer != null) {
			consumer.shutdown();
		}
		
		if(executor != null) {
			executor.shutdown();
		}
	}
	
	public void run(int numThreads) {
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(numThreads));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		
		List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
		
		executor = Executors.newFixedThreadPool(numThreads);
		
		int threadNumber = 0;
		
		System.out.println("======streams.size()=======" + streams.size());
	    for (final KafkaStream stream : streams) {
	    	//System.out.println("stream:" + stream);
            executor.submit(new ConsumerMsgTask(stream, threadNumber));
            threadNumber++;
        }
	    
	    System.out.println("===========end==================");
	}
	
	private static ConsumerConfig createConsumerConfig(String a_zookeeper,
			String a_groupId) {
		Properties props = new Properties();
		props.put("zookeeper.connect", a_zookeeper);
	    props.put("group.id", a_groupId);
	    props.put("zookeeper.session.timeout.ms", "400");
	    props.put("zookeeper.sync.time.ms", "200");
	    props.put("auto.commit.interval.ms", "1000");
	    
	    return new ConsumerConfig(props);
	}
	
	 public static void main(String[] arg) {
	        String[] args = { "slave02.infobird.com:2181", "group-1", "connect-call_info_history_10000_1", "12" };
	        String zooKeeper = args[0];
	        String groupId = args[1];
	        String topic = args[2];
	        int threads = Integer.parseInt(args[3]);
	 
	        ConsumerDemo demo = new ConsumerDemo(zooKeeper, groupId, topic);
	        demo.run(threads);
	 
	        try {
	            Thread.sleep(1000);
	        } catch (InterruptedException ie) {
	 
	        }
	        demo.shutdown();
	    }
}
