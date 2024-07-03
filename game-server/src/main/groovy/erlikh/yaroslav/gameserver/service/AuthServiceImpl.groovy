package erlikh.yaroslav.gameserver.service

import erlikh.yaroslav.gameserver.service.interfaces.AuthService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import erlikh.yaroslav.gameserver.dto.IntrospectRequest
import erlikh.yaroslav.gameserver.dto.IntrospectResponse
import erlikh.yaroslav.gameserver.dto.Message
import erlikh.yaroslav.gameserver.dto.TokenDto

@Component
class AuthServiceImpl implements AuthService {

    private final WebClient webClient

    AuthServiceImpl(WebClient webClient) {
        this.webClient = webClient
    }

    @Override
    TokenDto verify(Message message, String token) {
        return webClient.post()
            .bodyValue(new IntrospectRequest(token, message.getGameId()))
            .retrieve()
            .bodyToMono(IntrospectResponse.class)
            .block()
            .getToken()
    }
}