package erlikh.yaroslav.hw12.exceptions

class CommandException extends RuntimeException {

    CommandException(String message) {
        super(message)
    }

    CommandException(Exception e) {
        super(e)
    }
}
