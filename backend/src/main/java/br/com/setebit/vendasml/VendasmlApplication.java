package br.com.setebit.vendasml;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class VendasmlApplication {

	public static void main(String[] args) {
		SpringApplication.run(VendasmlApplication.class, args);
	}

}
