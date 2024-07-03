package erlikh.yaroslav.gameserver.dto

class Message {

    private final String gameId
    private final String objectId
    private final String operationId
    private final Object[] args

    Message() {
    }

    Message(String gameId, String objectId, String operationId, Object[] args) {
        this.gameId = gameId
        this.objectId = objectId
        this.operationId = operationId
        this.args = args
    }

    String getGameId() {
        return gameId
    }

    String getObjectId() {
        return objectId
    }

    String getOperationId() {
        return operationId
    }

    Object[] getArgs() {
        return args
    }
}