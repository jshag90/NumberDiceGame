package com.dodam.dicegame.dto

data class PlayerDto(
    val id:Int,
    val uuid:String,
    val createdAt:String,
    val isManager:String,
    val room:Int?,
    val totalScore:Int
)
