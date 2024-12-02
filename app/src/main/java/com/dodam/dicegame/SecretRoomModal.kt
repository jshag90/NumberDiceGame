package com.dodam.dicegame

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun SecretRoomModal(
    onDismiss: () -> Unit,
    onConfirm: (roomNumber: String, entryCode: String, nickName: String) -> Unit
) {
    var roomNumber by remember { mutableStateOf("") }
    var entryCode by remember { mutableStateOf("") }
    var nickName by remember { mutableStateOf("") }
    val context = LocalContext.current // Toast를 표시하기 위해 Context 필요

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = {
                    when {
                        roomNumber.isBlank() -> {
                            Toast.makeText(context, "방번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                        }
                        entryCode.isBlank() -> {
                            Toast.makeText(context, "입장 코드를 입력해주세요.", Toast.LENGTH_SHORT).show()
                        }
                        nickName.isBlank() -> {
                            Toast.makeText(context, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            onConfirm(roomNumber, entryCode, nickName)
                        }
                    }
                }
            ) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("취소")
            }
        },
        title = { Text("비공개 방 입장") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = roomNumber,
                    onValueChange = { roomNumber = it },
                    label = { Text("방번호") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = entryCode,
                    onValueChange = { entryCode = it },
                    label = { Text("입장 코드") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nickName,
                    onValueChange = { nickName = it },
                    label = { Text("닉네임") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
