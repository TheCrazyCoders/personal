package com.techsophy.vsc;

import java.util.Properties;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication
public class VscAppointmentApplication  extends SpringBootServletInitializer{

	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder springApplicationBuilder) {
		return springApplicationBuilder.sources(VscAppointmentApplication.class).properties(getProperties());
	}

	public static void main(String[] args) {

		SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(
				VscAppointmentApplication.class);
		springApplicationBuilder.sources(VscAppointmentApplication.class).properties(getProperties()).run(args);
	}

	static Properties getProperties() {
		Properties props = new Properties();
		props.put("spring.config.location", "classpath:vsc.properties");
		return props;
	}
}
