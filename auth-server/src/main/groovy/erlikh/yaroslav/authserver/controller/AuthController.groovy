package erlikh.yaroslav.authserver.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import erlikh.yaroslav.authserver.dto.*
import erlikh.yaroslav.authserver.service.AuthService

@RestController
@RequestMapping("/")
class AuthController {

    private final AuthService authService

    AuthController(AuthService authService) {
        this.authService = authService
    }

    @PostMapping("/auth/registerGame")
    ResponseEntity<RegisterGameResponse> registerGame(@RequestBody RegisterGameRequest request) {
        if (request.getUsers() == null || request.getUsers().isEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        return ResponseEntity.ok().body(new RegisterGameResponse(authService.registerGame(request.getUsers())))
    }

    @PostMapping("/auth/token")
    ResponseEntity<TokenResponse> token(@RequestBody TokenRequest request) {
        if (request.getGameId() == null || request.getGameId().isEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        String token = authService.token(request.getGameId())
        return ResponseEntity.ok().body(new TokenResponse(token))
    }

    @PostMapping("/auth/introspect")
    ResponseEntity<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) {
        if (request.getGameId() == null || request.getGameId().isEmpty() || request.getToken() == null || request.getToken().isEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        TokenDto token = authService.verify(request.getToken(), request.getGameId())
        return ResponseEntity.ok().body(new IntrospectResponse(token))
    }
}