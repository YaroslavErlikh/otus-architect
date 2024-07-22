package erlikh.yaroslav.gameserver.dto

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

    void setUserName(String userName) {
        this.userName = userName
    }

    String getGameId() {
        return gameId
    }

    void setGameId(String gameId) {
        this.gameId = gameId
    }
}
