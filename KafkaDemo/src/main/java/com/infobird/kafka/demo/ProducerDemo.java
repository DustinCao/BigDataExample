package com.infobird.kafka.demo;

import java.util.Date;
import java.util.Properties;
//import java.util.Random;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class ProducerDemo {

	public static void main(String[] args) {
		
		//Random random = new Random();
		int events = 10000;
		
		//设置配置属性
		Properties props = new Properties();
		props.put("metadata.broker.list", "10.122.74.63:9092,10.122.74.65:9092,10.122.74.66:9092,10.122.74.163:9092");
		//props.put("metadata.broker.list", "slave02.infobird.com:9092");
	    
		props.put("serializer.class", "kafka.serializer.StringEncoder");
        // key.serializer.class默认为serializer.class
        props.put("key.serializer.class", "kafka.serializer.StringEncoder");
        // 可选配置，如果不配置，则使用默认的partitioner
        props.put("partitioner.class", "com.infobird.kafka.demo.PartitionerDemo");
        //props.put("partitioner.class", "com.catt.kafka.demo.PartitionerDemo");
        // 触发acknowledgement机制，否则是fire and forget，可能会引起数据丢失
        // 值为0,1,-1,可以参考
        // http://kafka.apache.org/08/configuration.html
        props.put("request.required.acks", "1");
        ProducerConfig config = new ProducerConfig(props);

        //创建producer
        Producer<String, String> producer = new Producer<String, String>(config);
        //产生并发送消息
        long start = System.currentTimeMillis();
        long phone = 15960000000L;
        for(long i = 0; i < events; i++) {

        	String ip = String.valueOf(phone + i);
        	
        	String msg = "label1;label2;label3;label4;label5";
        	
        	//ip作为key 根据key来选择不同的分区
        	KeyedMessage<String, String> data = new KeyedMessage<String, String>("topic_test_10000", ip, msg);
        	producer.send(data);
        }
        
        System.out.println("耗时:" + (System.currentTimeMillis() - start));
        
        //关闭producer
        producer.close();
        
	}
}
