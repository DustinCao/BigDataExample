package com.infobird.kafka.demo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;

import com.infobird.data.entity.User;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class ConsumerAvro {


	public static void main(String[] arg) {
        String[] args = { "slave02.infobird.com:2181", "group-1", "topic_user_avro1"};
        String zookeeper = args[0];
        String groupId = args[1];
        String topic = args[2];
        
        ConsumerConnector consumer = Consumer.createJavaConsumerConnector(createConsumerConfig(zookeeper, groupId));
        
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(1));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
	
		List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
		
		System.out.println("=====begin=====");
		
		for (KafkaStream<byte[], byte[]> kafkaStream : streams) {
			
			ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
			
			while(it.hasNext()) {
				byte[] received_message = it.next().message();
				SpecificDatumReader<User> datumReader = new SpecificDatumReader<User>(User.class);
				User user = null;
				BinaryDecoder binaryDecoder = DecoderFactory.get().binaryDecoder(received_message,null);
				
				try {
		/*			System.out.println("1:" + binaryDecoder.isEnd());
					user = datumReader.read(user, binaryDecoder);
					System.out.println(user);
					System.out.println("2:" + binaryDecoder.isEnd());
					user = datumReader.read(user, binaryDecoder);
					System.out.println(user);
					System.out.println("3:" + binaryDecoder.isEnd());
					*/
					while(!binaryDecoder.isEnd()) {
						user = datumReader.read(user, binaryDecoder);
						System.out.println(user);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

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
