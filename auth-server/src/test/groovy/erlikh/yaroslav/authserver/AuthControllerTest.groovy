package erlikh.yaroslav.authserver

import com.fasterxml.jackson.databind.ObjectMapper
import erlikh.yaroslav.authserver.config.SecurityConfig
import erlikh.yaroslav.authserver.dto.*
import erlikh.yaroslav.authserver.service.AuthService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc

import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.when
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mvc

    @MockBean
    private AuthService authService

    private final ObjectMapper mapper = new ObjectMapper()

    @Test
    void 'throw_exc_not_auth_user'() throws Exception {
        mvc.perform(post("/auth/registerGame")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new RegisterGameRequest(List.of("user1", "user2"))))
        )
                .andDo(print())
                .andExpect(status().isUnauthorized())

        mvc.perform(post("/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new TokenRequest("myGame")))
        )
                .andDo(print())
                .andExpect(status().isUnauthorized())

        mvc.perform(post("/auth/introspect")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new IntrospectRequest("myGame", "myToken")))
        )
                .andDo(print())
                .andExpect(status().isUnauthorized())
    }

    @WithMockUser(value = "user1", password = "password1")
    @Test
    void 'throw_exc_if_no_players'() throws Exception {
        mvc.perform(post("/auth/registerGame")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new RegisterGameRequest(List.of())))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
    }

    @WithMockUser(value = "user1", password = "password1")
    @Test
    void 'register_game'() throws Exception {
        List<String> users = List.of("user1", "user2")
        String gameId = UUID.randomUUID().toString()
        when(authService.registerGame(eq(users))).thenReturn(gameId)

        mvc.perform(post("/auth/registerGame")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new RegisterGameRequest(users)))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new RegisterGameResponse(gameId))))
    }

    @WithMockUser(value = "user1", password = "password1")
    @Test
    void 'throw_exc_if_id_empty'() throws Exception {
        mvc.perform(post("/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new TokenRequest("")))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
    }

    @WithMockUser(value = "user1", password = "password1")
    @Test
    void 'create_token'() throws Exception {
        String gameId = "myGame"
        String token = UUID.randomUUID().toString()
        when(authService.token(eq(gameId))).thenReturn(token)

        mvc.perform(post("/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new TokenRequest(gameId)))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new TokenResponse(token))))
    }

    @WithMockUser(value = "user1", password = "password1")
    @Test
    void 'throw_exc_if_empty_id_game_or_token'() throws Exception {
        mvc.perform(post("/auth/introspect")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new IntrospectRequest()))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
    }

    @WithMockUser(value = "user1", password = "password1")
    @Test
    void 'validation_token'() throws Exception {
        String token = UUID.randomUUID().toString()
        String gameId = UUID.randomUUID().toString()
        String userName = "user1"
        when(authService.verify(eq(token), eq(gameId))).thenReturn(new TokenDto(userName, gameId))

        mvc.perform(post("/auth/introspect")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new IntrospectRequest(token, gameId)))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new IntrospectResponse(new TokenDto(userName, gameId)))))
    }
}