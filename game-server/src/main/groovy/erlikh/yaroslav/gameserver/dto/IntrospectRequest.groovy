package erlikh.yaroslav.gameserver.dto

class IntrospectRequest {

    private String token
    private String gameId

    IntrospectRequest() {
    }

    IntrospectRequest(String token, String gameId) {
        this.token = token
        this.gameId = gameId
    }

    String getGameId() {
        return gameId
    }

    IntrospectRequest setGameId(String gameId) {
        this.gameId = gameId
        return this
    }

    String getToken() {
        return token
    }

    IntrospectRequest setToken(String token) {
        this.token = token
        return this
    }
}