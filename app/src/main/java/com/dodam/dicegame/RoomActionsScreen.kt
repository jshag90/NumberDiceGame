package com.dodam.dicegame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RoomActionsScreen(
    onCreateRoomClick: () -> Unit,
    onPrivateRoomClick: () -> Unit,
    onPublicRoomClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp), // 전체 화면 패딩 설정
        verticalArrangement = Arrangement.spacedBy(30.dp) // 버튼 간 마진 10dp
    ) {
        Button(
            onClick = onCreateRoomClick,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // 세로로 1/3 공간을 차지
                .height(40.dp), // 버튼 세로 크기 절반으로 설정
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)), // Purple
            shape = RectangleShape // 각진 모양으로 설정
        ) {
            Icon(Icons.Filled.Add, contentDescription = "방 만들기", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "방만들기",
                color = Color.White,
                fontSize = 34.sp // 글자 크기를 현재의 2배로 설정
            )
        }
        Button(
            onClick = onPrivateRoomClick,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // 세로로 1/3 공간을 차지
                .height(40.dp), // 버튼 세로 크기 절반으로 설정
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5)), // Teal
            shape = RectangleShape // 각진 모양으로 설정
        ) {
            Icon(Icons.Filled.Lock, contentDescription = "비공개 방 입장", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "비공개방",
                color = Color.White,
                fontSize = 34.sp // 글자 크기를 현재의 2배로 설정
            )
        }
        Button(
            onClick = onPublicRoomClick,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // 세로로 1/3 공간을 차지
                .height(40.dp), // 버튼 세로 크기 절반으로 설정
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)), // Orange
            shape = RectangleShape // 각진 모양으로 설정
        ) {
            Icon(Icons.Filled.Face, contentDescription = "공개 방 입장", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "공개방",
                color = Color.White,
                fontSize = 34.sp // 글자 크기를 현재의 2배로 설정
            )
        }
    }
}




