package com.dodam.dicegame.vo

data class ReturnCodeVO<T>(
    val returnCode: Int,
    val data: T? // 반드시 `T`는 boxed 타입이어야 합니다
)

