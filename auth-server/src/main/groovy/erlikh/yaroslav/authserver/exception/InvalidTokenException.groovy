package erlikh.yaroslav.authserver.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class InvalidTokenException extends ResponseStatusException {

    InvalidTokenException() {
        this(null)
    }

    InvalidTokenException(Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, null, cause)
    }
}