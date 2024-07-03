package erlikh.yaroslav.gameserver.dto

class IntrospectResponse {

    private TokenDto token

    IntrospectResponse() {
    }

    IntrospectResponse(TokenDto token) {
        this.token = token
    }

    TokenDto getToken() {
        return token
    }

    IntrospectResponse setToken(TokenDto token) {
        this.token = token
        return this
    }
}