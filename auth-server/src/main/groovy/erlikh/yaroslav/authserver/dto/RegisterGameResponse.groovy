package erlikh.yaroslav.authserver.dto

class RegisterGameResponse {

    private String gameId

    RegisterGameResponse() {
    }

    RegisterGameResponse(String gameId) {
        this.gameId = gameId
    }

    String getGameId() {
        return gameId
    }

    RegisterGameResponse setGameId(String gameId) {
        this.gameId = gameId
        return this
    }
}