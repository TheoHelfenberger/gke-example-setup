package ch.thelf.demo.gke.microservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import ch.thelf.demo.gke.microservice.domain.Visitor;
import ch.thelf.demo.gke.microservice.repository.VisitorRepository;
import ch.thelf.demo.gke.microservice.ws.DefaultWebSocketHandler;

@SpringBootApplication
@EnableWebSocket
public class MicroserviceApplication implements WebSocketConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceApplication.class, args);
	}
	
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(myHandler(), "/ws");
	}

	@Bean
	public WebSocketHandler myHandler() {
		return new DefaultWebSocketHandler();
	}
	
	
    @Bean
    public CommandLineRunner demoData(VisitorRepository repo) {
        return args -> { 
        	repo.save(Visitor.builder().name("The visitor-" + System.currentTimeMillis()).build());
        };
    }

}
