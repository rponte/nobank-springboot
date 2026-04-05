package br.com.deveficiente.nobank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableRetry
@EnableFeignClients // does NOT work on @Configuration classes: https://github.com/spring-cloud/spring-cloud-openfeign/issues/301
@SpringBootApplication
public class NobankApplication {

	public static void main(String[] args) {
		SpringApplication.run(NobankApplication.class, args);
	}

	@Configuration
	@EnableScheduling
	@ConditionalOnProperty(name = "app.scheduling.enabled", havingValue = "true", matchIfMissing = true)
	static class SchedulingConfiguration {}

}
