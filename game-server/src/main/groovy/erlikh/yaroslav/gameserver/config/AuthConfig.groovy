package erlikh.yaroslav.gameserver.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "erlikh.yaroslav.auth")
@Configuration
class AuthConfig {

    private String uri
    private String user
    private String password

    String getUri() {
        return uri
    }

    AuthConfig setUri(String uri) {
        this.uri = uri
        return this
    }

    String getUser() {
        return user
    }

    AuthConfig setUser(String user) {
        this.user = user
        return this
    }

    String getPassword() {
        return password
    }

    AuthConfig setPassword(String password) {
        this.password = password
        return this
    }
}