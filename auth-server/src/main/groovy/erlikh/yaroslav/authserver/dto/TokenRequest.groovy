package erlikh.yaroslav.authserver.dto

class TokenRequest {

    private String gameId

    TokenRequest() {
    }

    TokenRequest(String gameId) {
        this.gameId = gameId
    }

    String getGameId() {
        return gameId
    }

    TokenRequest setGameId(String gameId) {
        this.gameId = gameId
        return this
    }
}