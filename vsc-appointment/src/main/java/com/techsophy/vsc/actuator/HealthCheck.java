package com.techsophy.vsc.actuator;

import java.sql.Timestamp;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.stereotype.Component;

@Component
public class HealthCheck extends AbstractHealthIndicator {

	@Qualifier("vsc")
	@Autowired
	DataSource vscDataSource;

	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		if (vscDataSource != null) {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			builder.withDetail("timestamp", timestamp);
			builder.withDetail("database", "MySQL");
			builder.up();
		} else {
			builder.down();
		}
	}

}
