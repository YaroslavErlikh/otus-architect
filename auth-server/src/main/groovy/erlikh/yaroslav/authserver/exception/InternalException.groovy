package erlikh.yaroslav.authserver.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class InternalException extends ResponseStatusException {
    InternalException() {
        this(null)
    }

    InternalException(Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, null, cause)
    }
}