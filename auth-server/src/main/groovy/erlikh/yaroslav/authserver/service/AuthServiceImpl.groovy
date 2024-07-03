package erlikh.yaroslav.authserver.service

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import erlikh.yaroslav.authserver.dto.TokenDto
import erlikh.yaroslav.authserver.exception.NotAuthorizedException
import erlikh.yaroslav.authserver.exception.NotFoundException
import erlikh.yaroslav.authserver.service.jwt.JwtService

import java.util.concurrent.ConcurrentHashMap

@Service
class AuthServiceImpl implements AuthService {

    private final Map<String, Set<String>> gamesAndUsers
    private final JwtService jwtService

    AuthServiceImpl(JwtService jwtService) {
        this.gamesAndUsers = new ConcurrentHashMap<>()
        this.jwtService = jwtService
    }

    @Override
    String registerGame(List<String> users) {
        String gameId = UUID.randomUUID().toString()
        gamesAndUsers.put(gameId, Set.copyOf(users))
        return gameId
    }

    @Override
    String token(String gameId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication()
        if (authentication == null) {
            throw new NotAuthorizedException()
        }
        String userName = authentication.getName()
        Set<String> users = gamesAndUsers.get(gameId)
        if (users == null) {
            throw new NotFoundException("Game not found")
        }
        if (!users.contains(userName)) {
            throw new NotAuthorizedException()
        }
        return jwtService.token(userName, gameId)
    }

    @Override
    TokenDto verify(String token, String gameId) {
        return jwtService.verify(token, gameId)
    }
}