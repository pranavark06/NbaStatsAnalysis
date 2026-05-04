package G16.menik._0;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Court Vision NBA application.
 * This Spring Boot application provides a web interface for exploring NBA draft history,
 * game statistics, player performance, team rankings, and game outcome predictions.
 */
@SpringBootApplication
public class Application {

	/**
	 * Launches the Spring Boot application.
	 *
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
