package com.techsophy.vsc.actuator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;

import com.techsophy.vsc.dao.AppointmentDao;

public class AppointmentDaoHealthCheck extends AbstractHealthIndicator {
	@Autowired
	AppointmentDao appointmentDao;

	@Override
	protected void doHealthCheck(Builder builder) {
		try {
			if (this.appointmentDao != null) {
				builder.up();

			} else {
				builder.down();
			}
		} catch (Exception e) {
			builder.down(e);
		}
	}

}
