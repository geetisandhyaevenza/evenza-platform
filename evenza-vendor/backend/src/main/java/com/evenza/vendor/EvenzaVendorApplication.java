package com.evenza.vendor;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
@OpenAPIDefinition(
    info = @Info(
        title = "Evenza Vendor API",
        version = "1.0.0",
        description = "Premium Wedding & Event Vendor Management Platform API",
        contact = @Contact(name = "Evenza Team", email = "support@evenza.com"),
        license = @License(name = "MIT", url = "https://opensource.org/licenses/MIT")
    )
)
public class EvenzaVendorApplication {
    public static void main(String[] args) {
        SpringApplication.run(EvenzaVendorApplication.class, args);
    }
}
