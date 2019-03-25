package ch.thelf.demo.gke.microservice;

import javax.swing.text.html.parser.Entity;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import ch.thelf.demo.gke.microservice.domain.Visitor;
import ch.thelf.demo.gke.microservice.repository.VisitorRepository;

@SpringBootApplication
public class MicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceApplication.class, args);
	}
	
	

    @Bean
    public CommandLineRunner demoData(VisitorRepository repo) {
        return args -> { 
        	repo.save(Visitor.builder().name("The visitor-" + System.currentTimeMillis()).build());
        };
    }

}
