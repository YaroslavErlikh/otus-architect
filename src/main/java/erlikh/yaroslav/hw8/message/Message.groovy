package erlikh.yaroslav.hw8.message

class Message {

    private final String gameId
    private final String objectId
    private final String operationId

    /**
     * параметры команды
     */
    private final Object[] args;

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

    static class Builder {
        private String gameId
        private String objectId
        private String operationId
        private Object[] args

        Builder setGameId(String gameId) {
            this.gameId = gameId
            return this
        }

        Builder setObjectId(String objectId) {
            this.objectId = objectId
            return this
        }

        Builder setOperationId(String operationId) {
            this.operationId = operationId
            return this
        }

        Builder setArgs(Object[] args) {
            this.args = args
            return this
        }

        Message build() {
            return new Message(gameId, objectId, operationId, args)
        }
    }
}