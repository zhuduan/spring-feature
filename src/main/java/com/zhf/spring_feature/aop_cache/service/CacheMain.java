package com.zhf.spring_feature.aop_cache.service;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.zhf.spring_feature.aop_cache.util.ZzzCache;

@Service
public class CacheMain {

	public static void main(String[] args) {
		AbstractApplicationContext cxt = new ClassPathXmlApplicationContext("applicationContext.xml");
		CacheMain service = (CacheMain) cxt.getBean(CacheMain.class);  
		
		System.out.println("start!");
		for(int i=0;i<10;i++){
			System.out.println(i+": "+service.testCache());
		}
	}
	
	@ZzzCache(expire=60)
	public String testCache(){
		return "hello world";
	}

}
