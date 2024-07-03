package erlikh.yaroslav.authserver.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "erlikh.yaroslav.auth")
@Configuration
class AppConfig {

    private String issuer

    String getIssuer() {
        return issuer
    }

    AppConfig setIssuer(String issuer) {
        this.issuer = issuer
        return this
    }
}