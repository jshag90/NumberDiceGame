package com.dodam.dicegame.vo

enum class RoomType {
    SECRET, PUBLIC
}

data class RoomInfoVO(
    val maxPlayers: Int,
    val targetNumber: Int,
    val diceCount: Int,
    val roomType: RoomType,
    val entryCode: String?,
    val uuid: String
)
