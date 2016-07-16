package com.dianwoba.rha.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dianwoba.rha.service.CacheService;

@Controller
public class IndexController {

	private static Logger logger = LoggerFactory.getLogger(IndexController.class);

	@Autowired
	private CacheService cacheService;

	@RequestMapping("/index")
	@ResponseBody
	public Map<Object, Object> index() {
		long tid = Thread.currentThread().getId();
		String key = "tid-" + tid + "--" + System.currentTimeMillis();
		String value = Thread.currentThread().getName();
		cacheService.putCache(key, value, 1200);
		logger.info("put cache [key=%s][value=%s]", key, value);

		Map<Object, Object> rlt = new HashMap<Object, Object>();
		rlt.put("cache-value", cacheService.getCache(key));
		rlt.put("fuzzy-num", cacheService.fuzzyQuery("tid-*").size());
		return rlt;
	}

	@RequestMapping("/cache/num")
	@ResponseBody
	public Map<Object, Object> cacheNum() {
		Map<Object, Object> rlt = new HashMap<Object, Object>();
		rlt.put("fuzzy-num", cacheService.fuzzyQuery("tid-*").size());
		return rlt;
	}
}