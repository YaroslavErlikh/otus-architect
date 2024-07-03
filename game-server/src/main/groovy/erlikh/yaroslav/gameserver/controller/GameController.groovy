package erlikh.yaroslav.gameserver.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import erlikh.yaroslav.gameserver.dto.Message
import erlikh.yaroslav.gameserver.dto.TokenDto
import erlikh.yaroslav.gameserver.service.interfaces.*

@RestController
@RequestMapping("/")
class GameController {

    private final GameService gameService
    private final AuthService authService

    GameController(GameService gameService, AuthService authService) {
        this.gameService = gameService
        this.authService = authService
    }

    @PostMapping("/game/receive")
    ResponseEntity<Boolean> receive(@RequestBody Message message, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        TokenDto metadata = authService.verify(message, token)
        return ResponseEntity.ok().body(gameService.receive(message, metadata))
    }
}