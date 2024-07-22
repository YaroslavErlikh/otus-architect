package erlikh.yaroslav.authserver.dto

class TokenDto {

    private String userName
    private String gameId

    TokenDto() {
    }

    TokenDto(String userName, String gameId) {
        this.userName = userName
        this.gameId = gameId
    }

    String getUserName() {
        return userName
    }

    TokenDto setUserName(String userName) {
        this.userName = userName
        return this
    }

    String getGameId() {
        return gameId
    }

    TokenDto setGameId(String gameId) {
        this.gameId = gameId
        return this
    }
}