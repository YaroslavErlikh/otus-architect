package erlikh.yaroslav.authserver.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class NotAuthorizedException extends ResponseStatusException {

    NotAuthorizedException() {
        this("Not authorized")
    }

    NotAuthorizedException(String reason) {
        super(HttpStatus.UNAUTHORIZED, reason)
    }
}