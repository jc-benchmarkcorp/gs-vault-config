package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MyConfiguration.class)
class AppConfig extends AbstractVaultConfiguration {

	private final MyConfiguration configuration;

	public Application(MyConfiguration configuration) {
		this.configuration = configuration;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) {

		AppRoleAuthenticationOptions options = AppRoleAuthenticationOptions.builder()
                .roleId(RoleId.provided("…"))
                .secretId(SecretId.wrapped(VaultToken.of("…")))
                .build();

        return new AppRoleAuthentication(options, restOperations());
		
		Logger logger = LoggerFactory.getLogger(Application.class);

		logger.info("----------------------------------------");
		logger.info("Configuration properties");
		logger.info("		example.username is {}", configuration.getUsername());
		logger.info("		example.password is {}", configuration.getPassword());
		logger.info("----------------------------------------");
	}
}
