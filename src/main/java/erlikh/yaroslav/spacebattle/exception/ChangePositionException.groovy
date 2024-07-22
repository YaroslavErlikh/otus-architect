package erlikh.yaroslav.spacebattle.exception

class ChangePositionException extends RuntimeException {

    public ChangePositionException() {
        super();
    }

    public ChangePositionException(String message) {
        super(message);
    }

    public ChangePositionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChangePositionException(Throwable cause) {
        super(cause);
    }

    protected ChangePositionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
