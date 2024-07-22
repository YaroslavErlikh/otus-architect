package erlikh.yaroslav.authserver.service

import erlikh.yaroslav.authserver.dto.TokenDto

interface AuthService {

    String registerGame(List<String> users)

    String token(String gameId)

    TokenDto verify(String token, String gameId)
}