package com.dodam.dicegame.vo

data class SaveScoreVO(
    val roomId: Long,
    val uuid: String,
    val finalRound: Int,
    val score: Int
)
