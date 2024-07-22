package erlikh.yaroslav.gameserver.service.interfaces

import erlikh.yaroslav.gameserver.dto.Message
import erlikh.yaroslav.gameserver.dto.TokenDto

interface AuthService {

    TokenDto verify(Message message, String token)
}
