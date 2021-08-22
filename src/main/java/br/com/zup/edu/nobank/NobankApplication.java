package br.com.zup.edu.nobank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients // does NOT work on @Configuration classes: https://github.com/spring-cloud/spring-cloud-openfeign/issues/301
@SpringBootApplication
public class NobankApplication {

	public static void main(String[] args) {
		SpringApplication.run(NobankApplication.class, args);
	}

}
