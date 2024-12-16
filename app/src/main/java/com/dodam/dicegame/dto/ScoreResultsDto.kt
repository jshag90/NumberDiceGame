package com.dodam.dicegame.dto

data class ScoreResultsDto(
    val rank: Int,
    val score: Int,
    val nickName: String,
    val roomId: Int,
    val targetNumber: Int
)