package erlikh.yaroslav.gameserver

import com.fasterxml.jackson.databind.ObjectMapper
import erlikh.yaroslav.gameserver.dto.Message
import erlikh.yaroslav.gameserver.dto.TokenDto
import erlikh.yaroslav.gameserver.service.interfaces.AuthService
import erlikh.yaroslav.gameserver.service.interfaces.GameService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.server.ResponseStatusException

import static org.mockito.ArgumentMatchers.*
import static org.mockito.Mockito.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class GameServerApplicationTests {

    @MockBean
    private AuthService authService

    @MockBean
    private GameService gameService

    @Autowired
    private MockMvc mvc

    private final ObjectMapper mapper = new ObjectMapper()

    @Test
    void 'throw_exc_if_wrong_token'() throws Exception {
        mvc.perform(post("/game/receive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new Message("", "", "", new Object[]{})))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())

        mvc.perform(post("/game/receive")
                .header(HttpHeaders.AUTHORIZATION, "")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new Message("", "", "", new Object[]{})))
        )
                .andDo(print())
                .andExpect(status().isUnauthorized())
    }

    @Test
    void 'not_send_command_if_token_not_valid'() throws Exception {
        String token = UUID.randomUUID().toString()
        String gameId = UUID.randomUUID().toString()
        when(authService.verify(argThat(m -> m.getGameId().equals(gameId)), eq(token)))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED))

        mvc.perform(post("/game/receive")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(mapper.writeValueAsString(new Message(gameId, "", "", new Object[]{})))
        )
                .andDo(print())
                .andExpect(status().isUnauthorized())

        verify(gameService, times(0)).receive(any(), any())
    }

    @Test
    void 'send_command_if_token_valid'() throws Exception {
        String token = UUID.randomUUID().toString()
        String gameId = UUID.randomUUID().toString()
        TokenDto metadata = new TokenDto("user1", gameId)
        when(authService.verify(argThat(m -> m.getGameId().equals(gameId)), eq(token))).thenReturn(metadata)
        when(gameService.receive(any(), any())).thenReturn(true)

        mvc.perform(post("/game/receive")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(mapper.writeValueAsString(new Message(gameId, "", "", new Object[]{})))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"))

        verify(gameService, times(1)).receive(
                argThat(m -> m.getGameId().equals(gameId)),
                argThat(t -> t.getGameId().equals(metadata.getGameId()) && t.getUserName().equals(metadata.getUserName()))
        )
    }
}