package com.techsophy.vsc.utils;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class DataSourceBinder {
	@Bean(name = "vsc")
	@ConfigurationProperties("spring.datasource.vsc")
	public DataSource vpsDataSource() {
		return DataSourceBuilder.create().build();
	}
	
}	
