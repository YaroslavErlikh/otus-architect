package erlikh.yaroslav.gameserver.service

import erlikh.yaroslav.gameserver.service.interfaces.GameService
import org.springframework.stereotype.Component
import erlikh.yaroslav.gameserver.dto.Message
import erlikh.yaroslav.gameserver.dto.TokenDto

@Component
class GameServiceImpl implements GameService {
    @Override
    boolean receive(Message message, TokenDto metadata) {
        return true
    }
}