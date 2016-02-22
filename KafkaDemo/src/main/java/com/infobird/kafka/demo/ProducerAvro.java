package com.infobird.kafka.demo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.util.Utf8;

import com.infobird.data.entity.User;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class ProducerAvro {

public static void main(String[] args) {
		
		//设置配置属性
		Properties props = new Properties();
		props.put("metadata.broker.list", "10.122.74.63:9092,10.122.74.65:9092,10.122.74.66:9092,10.122.74.163:9092");
		//props.put("metadata.broker.list", "slave02.infobird.com:9092");
	    
		props.put("serializer.class", "kafka.serializer.DefaultEncoder");
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
        Producer<String, byte[]> producer = new Producer<String, byte[]>(config);
        //产生并发送消息
        long start = System.currentTimeMillis();
        
        List<User> users = new ArrayList<User>();
        
        User user1 = new User();
		user1.setName(new Utf8("曹帅"));
		user1.setAge(new Utf8("12"));
		user1.setGender(new Utf8("Male"));
		User user2 = new User();
		user2.setName(new Utf8("xuzili"));
		user2.setAge(new Utf8("13"));
		user2.setGender(new Utf8("Male"));
		User user3 = new User();
		user3.setName(new Utf8("LiLy"));
		user3.setAge(new Utf8("13"));
		user3.setGender(new Utf8("Male"));
		
		User user4 = new User();
		user4.setName(new Utf8("Lucy"));
		user4.setAge(new Utf8("13"));
		user4.setGender(new Utf8("Male"));
		
		User user5 = new User();
		user5.setName(new Utf8("zhangsan"));
		user5.setAge(new Utf8("13"));
		user5.setGender(new Utf8("Male"));
		
		users.add(user1);
		users.add(user2);
		users.add(user3);
		users.add(user4);
		users.add(user5);
		
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
		DatumWriter<User> datumWriter = new SpecificDatumWriter<User>(User.class);
		try {
			for(int i=1;i<=users.size();i++){
				
				
				if(i%2==0){
					System.out.println("user1==" + users.get(i-1));
					datumWriter.write(users.get(i-1), encoder);
					
					encoder.flush();
					
					byte[] serializedBytes = out.toByteArray();
					KeyedMessage<String, byte[]> message = new KeyedMessage<String, byte[]>("test_222", serializedBytes);

					System.out.println("message:" + new String(message.message()));
					producer.send(message);
					
					out = new ByteArrayOutputStream();
					encoder = EncoderFactory.get().binaryEncoder(out, null);
					
				}else if(i != users.size()){
					System.out.println("user2==" + users.get(i-1));
					datumWriter.write(users.get(i-1), encoder);
				} else {
					System.out.println("user3==" + users.get(i-1));
					datumWriter.write(users.get(i-1), encoder);
					encoder.flush();
					
					byte[] serializedBytes = out.toByteArray();
					KeyedMessage<String, byte[]> message = new KeyedMessage<String, byte[]>("test_222", serializedBytes);

					System.out.println("message:" + new String(message.message()));
					producer.send(message);
					//producer.send(message);
				}
			}

			//datumWriter.write(user1, encoder);
			//datumWriter.write(user2, encoder);     
			
			//encoder.flush();
		    out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	   // byte[] serializedBytes = out.toByteArray();
	    
	   // KeyedMessage<String, byte[]> message = new KeyedMessage<String, byte[]>("topic_user_avro1", serializedBytes);
      //  producer.send(message);
        
        
        System.out.println("耗时:" + (System.currentTimeMillis() - start));
        
        //关闭producer
        producer.close();
        

        
	}
}
