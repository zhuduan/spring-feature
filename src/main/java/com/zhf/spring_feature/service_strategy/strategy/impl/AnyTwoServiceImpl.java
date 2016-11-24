package com.zhf.spring_feature.service_strategy.strategy.impl;

import org.springframework.stereotype.Service;

import com.zhf.spring_feature.service_strategy.strategy.AnyService;

@Service("AnyTwo")
public class AnyTwoServiceImpl implements AnyService {

	public String getMessage() {		
		return "hello two";
	}

}
