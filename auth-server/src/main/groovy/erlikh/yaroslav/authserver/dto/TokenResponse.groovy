package erlikh.yaroslav.authserver.dto

class TokenResponse {

    private String token

    TokenResponse() {
    }

    TokenResponse(String token) {
        this.token = token
    }

    String getToken() {
        return token
    }

    TokenResponse setToken(String token) {
        this.token = token
        return this
    }
}