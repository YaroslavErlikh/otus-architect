package erlikh.yaroslav.gameserver.service.interfaces

import erlikh.yaroslav.gameserver.dto.Message
import erlikh.yaroslav.gameserver.dto.TokenDto

interface GameService {

    boolean receive(Message message, TokenDto metadata)
}
