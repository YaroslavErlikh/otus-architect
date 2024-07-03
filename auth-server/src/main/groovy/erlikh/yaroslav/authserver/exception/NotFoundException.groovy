package erlikh.yaroslav.authserver.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class NotFoundException extends ResponseStatusException {

    NotFoundException(String reason) {
        super(HttpStatus.NOT_FOUND, reason)
    }
}