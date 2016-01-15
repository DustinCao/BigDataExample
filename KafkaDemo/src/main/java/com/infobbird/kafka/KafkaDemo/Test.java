package com.infobbird.kafka.KafkaDemo;

public class Test {

	public static void main(String[] args) {
		
		String  ip = "192.168.1.100";
		
		int offset = ip.lastIndexOf(".");
		System.out.println(offset);
		System.out.println(ip.substring(1));
		
		
		System.out.println(10000%10000);
	 
		System.out.println(100011%10000);
		
		System.out.println(100011/10000);
	}
}
