package erlikh.yaroslav.authserver.service.jwt

import erlikh.yaroslav.authserver.dto.TokenDto

interface JwtService {

    String token(String userName, String gameId)

    TokenDto verify(String token, String gameId)
}