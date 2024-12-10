package com.dodam.dicegame.vo

interface SocketMessageVO {
    val action: String
}

data class JoinRoomMessageVO(
    val roomId: String,
    val nickName: String,
    override val action: String
) : SocketMessageVO

data class GetRoomsCountMessageVO(
    val roomId: String,
    override val action: String
) : SocketMessageVO

data class StartGameMessageVO(
    val roomId: String,
    override val action: String
) : SocketMessageVO

data class ResponseMessageVO(
    val message: String,
    override val action: String
) : SocketMessageVO