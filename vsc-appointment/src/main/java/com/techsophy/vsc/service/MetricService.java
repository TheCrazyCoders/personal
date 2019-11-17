package com.techsophy.vsc.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;

@Service
public class MetricService {
	private ConcurrentMap<String, ConcurrentHashMap<Integer, Integer>> metricMap = new ConcurrentHashMap<String, ConcurrentHashMap<Integer, Integer>>();
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public void increaseStatusCountForEachReq(String request, int status) {
		ConcurrentHashMap<Integer, Integer> statusMap = metricMap.get(request);

		String time = dateFormat.format(new Date());
		if (statusMap == null) {
			statusMap = new ConcurrentHashMap<Integer, Integer>();
		}

		Integer count = statusMap.get(status);
		if (count == null) {
			count = 1;
		} else {
			count++;
		}
		statusMap.put(status, count);

		metricMap.put(request, statusMap);

	}

	public Map getFullMetric() {
		return metricMap;
	}
}
