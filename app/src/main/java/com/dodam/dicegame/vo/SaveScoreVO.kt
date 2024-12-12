package com.dodam.dicegame.vo

data class SaveScoreVO(
    val roomId: Long,
    val nickName: String,
    val finalRound: Int,
    val score: Int
)
