package hw08

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw8.command.CommandQueue
import erlikh.yaroslav.hw8.command.InterpretCommand
import erlikh.yaroslav.hw8.ioc.IoC
import erlikh.yaroslav.hw8.ioc.ScopeBasedStrategy
import erlikh.yaroslav.hw8.message.GameEndpoint
import erlikh.yaroslav.hw8.message.Message

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.Mockito.*

class GameEndpointTest {

    private CommandQueue queue
    private String gameId
    private GameEndpoint gameEndpoint

    @BeforeEach
    void setUp() {
        ScopeBasedStrategy scopeBasedStrategy = new ScopeBasedStrategy()
        (new ScopeBasedStrategy.InitScopeBasedIoCCommand(scopeBasedStrategy)).execute()

        // подменяем зависимость очереди
        queue = mock(CommandQueue.class)
        ((BaseCommand) IoC.resolve("IoC.Register", "Games.CreateQueue", args1 -> {
            if (gameId.equals(args1[0])) {
                return queue
            } else {
                return null
            }
        })).execute()

        gameId = "game1"
        gameEndpoint = new GameEndpoint()
    }

    @Test
    void 'search_game_and_put_InterpretCommand'() {
        IoC.resolve("Games.Create", gameId)
        IoC.resolve("Games.Create", "game2")
        IoC.resolve("Games.Create", "game3")

        String objectId = "123"
        String operationId = "Move"
        Object[] args = new Object[]{ "123", 1 }
        gameEndpoint.receive(new Message.Builder()
                .setGameId(gameId)
                .setObjectId(objectId)
                .setOperationId(operationId)
                .setArgs(args)
                .build()
        )

        verify(queue, times(1)).addLast(argThat(command -> {
            assertThat(command).isInstanceOf(InterpretCommand.class)
            return true
        }))
    }
}
