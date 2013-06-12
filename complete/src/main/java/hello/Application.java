package hello;

import org.springframework.bootstrap.SpringApplication;
import org.springframework.bootstrap.context.annotation.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.EnableInMemoryConnectionRepository;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.twitter.config.annotation.EnableTwitter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableAutoConfiguration
@EnableWebMvc
@EnableTwitter(appId="3se6dplpiWhqJjDOfM6iQ", appSecret="NJpQGWSpnQZ6KV58XOabtKdzWDCZtNQHqLC2yPnE")
@EnableInMemoryConnectionRepository
@ComponentScan
public class HelloTwitterConfiguration {

	@Bean
	public ConnectController connectController(ConnectionFactoryLocator connectionFactoryLocator, ConnectionRepository connectionRepository) {
		return new ConnectController(connectionFactoryLocator, connectionRepository);
	}
	
	@Bean
	public UserIdSource userIdSource() {
		return new UserIdSource() {			
			@Override
			public String getUserId() {
				return "testuser";
			}
		};
	}

	/*
	 * SPRING BOOTSTRAP MAIN
	 */
	public static void main(String[] args) {
		SpringApplication.run(HelloTwitterConfiguration.class, args);
	}

} 