package com.dodam.dicegame.dto

// 서버에서 반환된 데이터 구조에 맞게 RoomData 클래스 정의
data class RoomPlayerDto(
    val targetNumber: Int,
    val diceCount: Int,
    val playerId: Int,
    val roomId: Int,
    val maxPlayers: Int,
    val nickName: String
)