package com.techsophy.vsc.utils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techsophy.vsc.dao.AppointmentDao;
import com.techsophy.vsc.service.MetricService;

@Component
public class MetricFilter implements Filter {
	private static final Logger LOGGER = LoggerFactory.getLogger(MetricFilter.class);
	@Autowired
	MetricService metricService;
	@Autowired
	AppointmentDao appointmentDao;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = ((HttpServletRequest) request);
		String req = httpRequest.getMethod() + " " + httpRequest.getRequestURI();
		long time = System.currentTimeMillis();
		try {
			chain.doFilter(request, response);
			int status = ((HttpServletResponse) response).getStatus();
			appointmentDao.increaseStatusCodeCount(status);
			metricService.increaseStatusCountForEachReq(req, status);
		} finally {
			time = System.currentTimeMillis() - time;
			LOGGER.trace("{}: {} ms ", ((HttpServletRequest) request).getRequestURI(), time);
		}

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
