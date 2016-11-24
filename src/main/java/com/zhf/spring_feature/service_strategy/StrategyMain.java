package com.zhf.spring_feature.service_strategy;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.zhf.spring_feature.service_strategy.strategy.AnyService;

@Service
public class StrategyMain {

	public static void main(String[] args) {
		AbstractApplicationContext cxt = new ClassPathXmlApplicationContext("applicationContext.xml");
		StrategyMain mainService = (StrategyMain) cxt.getBean(StrategyMain.class);  

		mainService.hello();
	}
	
	@Autowired
	private Map<String, AnyService> anyServiceMap;
	
	@Autowired
	@Qualifier("AnyOne")
	private AnyService anyService;
	
	public void hello(){
		AnyService service = anyServiceMap.get("AnyTwo");
		System.out.println(service.getMessage());
		
		service = anyServiceMap.get("AnyOne");
		System.out.println(service.getMessage());
		
		System.out.println(anyService.getMessage());
	}

}
