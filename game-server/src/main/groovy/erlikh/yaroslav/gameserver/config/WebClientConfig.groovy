package erlikh.yaroslav.gameserver.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.Base64Utils
import org.springframework.web.reactive.function.client.WebClient

import static java.nio.charset.StandardCharsets.UTF_8

@Configuration
class WebClientConfig {

    private final AuthConfig authConfig

    WebClientConfig(AuthConfig authConfig) {
        this.authConfig = authConfig
    }

    @Bean
    WebClient webClient() {
        return WebClient.builder()
            .defaultHeader("Authorization", "Basic " +
                Base64Utils.encodeToString((authConfig.getUser() + ":" + authConfig.getPassword()).getBytes(UTF_8))
            )
            .baseUrl(authConfig.getUri())
            .build()
    }
}