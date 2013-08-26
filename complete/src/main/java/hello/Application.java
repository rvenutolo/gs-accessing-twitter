package hello;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableAutoConfiguration
@Import(TwitterConfig.class)
@ComponentScan
public class Application {

    /*
     * SPRING BOOTSTRAP MAIN
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

} 