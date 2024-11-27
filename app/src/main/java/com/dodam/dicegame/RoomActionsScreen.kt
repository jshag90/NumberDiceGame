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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun RoomActionsScreen(
    navController: NavHostController,
    onCreateRoomClick: () -> Unit,
    onPrivateRoomClick: () -> Unit,
    onPublicRoomClick: () -> Unit
) {

    var showModal by remember { mutableStateOf(false) }
    var showSecretRoomModal by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp), // 전체 화면 패딩 설정
        verticalArrangement = Arrangement.spacedBy(30.dp) // 버튼 간 마진 10dp
    ) {
        Button(
            onClick = { showModal = true }, // Show modal on button click
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .height(40.dp),
          /*  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),*/
            shape = RectangleShape
        ) {
            Icon(Icons.Filled.Add, contentDescription = "방 만들기", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "방만들기",
                color = Color.White,
                fontSize = 34.sp
            )
        }
        Button(
            onClick = { showSecretRoomModal = true },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // 세로로 1/3 공간을 차지
                .height(40.dp), // 버튼 세로 크기 절반으로 설정
         /*   colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5)),*/ // Teal
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
          /*  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),*/ // Orange
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

    if (showModal) {
        CreateRoomModal(
            onDismiss = { showModal = false },
            onConfirm = { targetNumber, numDice, isPublic, entryCode ->
                showModal = false
                val isPublicText = if (isPublic) "true" else " false"
                val entryCodeText = entryCode.ifBlank { "-1" }
                navController.navigate(
                    "game_room/$targetNumber/$numDice/$isPublicText/$entryCodeText"
                )
                onCreateRoomClick() // TODO 방생성 api 요청
            }
        )
    }

    if (showSecretRoomModal) {
        SecretRoomModal(
            onDismiss = { showSecretRoomModal = false },
            onConfirm = { roomNumber, entryCode ->
                showSecretRoomModal = false

                // 입력값 검증 및 로직 처리
                if (roomNumber.isNotBlank() && entryCode.isNotBlank()) {
                    onPrivateRoomClick() // 비공개방 입장 로직 호출
                } else {
                    // 잘못된 입력값 처리 로직 (필요 시 추가)
                    println("방번호나 입장코드가 비어있습니다!")
                }
            }
        )
    }

}




