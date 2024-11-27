package com.dodam.dicegame

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
import androidx.compose.ui.unit.dp

@Composable
fun SecretRoomModal(
    onDismiss: () -> Unit,
    onConfirm: (roomNumber: String, entryCode: String) -> Unit
) {
    var roomNumber by remember { mutableStateOf("") }
    var entryCode by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onConfirm(roomNumber, entryCode) }) {
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
            }
        }
    )
}