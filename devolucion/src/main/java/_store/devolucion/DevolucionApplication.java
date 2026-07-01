package _store.devolucion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class DevolucionApplication {
	public static void main(String[] args) {
		SpringApplication.run(DevolucionApplication.class, args);
	}
}
