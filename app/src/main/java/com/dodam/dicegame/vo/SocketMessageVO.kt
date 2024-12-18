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

data class PlayGameMessageVO(
    val roomId: String,
    val isGo:String,
    override val action: String
) : SocketMessageVO

data class LeaveRoomMessageVO(
    val roomId: String,
    val nickName:String,
    override val action: String
) : SocketMessageVO

data class ResponseMessageVO(
    val message: String,
    val subMessage:String,
    override val action: String
) : SocketMessageVO