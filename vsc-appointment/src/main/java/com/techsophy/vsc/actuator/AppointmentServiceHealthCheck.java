package com.techsophy.vsc.actuator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.stereotype.Component;

import com.techsophy.vsc.service.AppointmentService;

@Component
public class AppointmentServiceHealthCheck extends AbstractHealthIndicator {
	@Autowired
	AppointmentService appointmentService;

	@Override
	protected void doHealthCheck(Builder builder) {
		try {
			if (this.appointmentService != null) {
				builder.up();

			} else {
				builder.down();
			}
		} catch (Exception e) {

		}
	}

}
