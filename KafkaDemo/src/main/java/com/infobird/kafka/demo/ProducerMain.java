package com.infobird.kafka.demo;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
public class ProducerMain {
	
public static void main(String[] args) {
		
		//Random random = new Random();
		int events = 10;
		
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
        
        try {
			byte[] content = getContent("F:/users1.avro");
			String ip = "1";
			KeyedMessage<String, byte[]> data = new KeyedMessage<String, byte[]>("topic_user_1", ip, content);
			producer.send(data);
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
        
        System.out.println("耗时:" + (System.currentTimeMillis() - start));
        
        //关闭producer
        producer.close();
        
	}


public static byte[] getContent(String filePath) throws IOException
{
    FileInputStream in=new FileInputStream(filePath);
   
    ByteArrayOutputStream out=new ByteArrayOutputStream(1024);
   
    System.out.println("bytes available:"+in.available());
   
    byte[] temp=new byte[1024];
   
    int size=0;
   
    while((size=in.read(temp))!=-1)
    {
        out.write(temp,0,size);
    }
   
    in.close();
   
    byte[] bytes=out.toByteArray();
    System.out.println("bytes size got is:"+bytes.length);
   
    return bytes;
}

}
