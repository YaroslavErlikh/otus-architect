package erlikh.yaroslav.authserver

import erlikh.yaroslav.authserver.dto.TokenDto
import erlikh.yaroslav.authserver.exception.NotAuthorizedException
import erlikh.yaroslav.authserver.exception.NotFoundException
import erlikh.yaroslav.authserver.service.AuthService
import erlikh.yaroslav.authserver.service.jwt.JwtService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.context.support.WithMockUser

import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.when

@SpringBootTest
class AuthServiceImplTest {

    @Autowired
    private AuthService authService

    @MockBean
    private JwtService jwtService

    @Test
    void 'register_game'() {
        String gameId = authService.registerGame(List.of("user1", "user2"))
        assertThat(gameId).isNotNull().isNotEmpty()
    }

    @Test
    void 'throw_exc_creating_token_if_not_auth_user'() {
        assertThatThrownBy(() -> {
            authService.token(UUID.randomUUID().toString())
        }).isInstanceOf(NotAuthorizedException.class)
    }

    @WithMockUser(value = "user1", password = "password1")
    @Test
    void 'throw_exc_creating_token_if_not_find_game'() {
        String gameId = authService.registerGame(List.of("user1", "user2"))

        assertThatThrownBy(() -> {
            authService.token(gameId + "!")
        }).isInstanceOf(NotFoundException.class)
    }

    @WithMockUser(value = "user1", password = "password1")
    @Test
    void 'throw_exc_creating_token_if_user_not_in_list_players'() {
        String gameId = authService.registerGame(List.of("user3", "user2"))

        assertThatThrownBy(() -> {
            authService.token(gameId)
        }).isInstanceOf(NotAuthorizedException.class)
    }

    @WithMockUser(value = "user1", password = "password1")
    @Test
    void 'correct_data_create_token'() {
        String gameId = authService.registerGame(List.of("user1", "user2"))
        String token = UUID.randomUUID().toString()
        when(jwtService.token(eq("user1"), eq(gameId))).thenReturn(token)

        assertThat(authService.token(gameId)).isEqualTo(token)
    }

    @WithMockUser(value = "user1", password = "password1")
    @Test
    void 'verified_token'() {
        String gameId = authService.registerGame(List.of("user1", "user2"))
        String token = UUID.randomUUID().toString()
        when(jwtService.verify(eq(token), eq(gameId))).thenReturn(new TokenDto("user1", gameId))

        TokenDto tokenDto = authService.verify(token, gameId)
        assertThat(tokenDto.getUserName()).isEqualTo("user1")
        assertThat(tokenDto.getGameId()).isEqualTo(gameId)
    }
}