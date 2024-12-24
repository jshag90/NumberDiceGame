package com.dodam.dicegame.component

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.navigation.NavController
import com.dodam.dicegame.api.updateNickNameWithOkHttpAsync
import com.dodam.dicegame.dto.RoomPlayerDto

fun showNicknameChangeModal(
    context: Context,
    playerId: Int,
    navController: NavController,
    roomPlayerDto: RoomPlayerDto
) {
    val editText = EditText(context).apply {
        setPadding(16, 8, 16, 8) // 내부 여백 설정
    }

    val container = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(52, 26, 52, 16)
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = android.view.Gravity.CENTER
        }
        addView(editText)
    }

    val dialog = AlertDialog.Builder(context)
        .setMessage("입장하려는 방에 닉네임이 이미 존재합니다.\n새로운 닉네임을 입력해주세요.")
        .setCancelable(false)
        .setView(container)
        .setPositiveButton("변경") { _, _ ->
            val newNickName = editText.text.toString()
            if (newNickName.isNotEmpty()) {
                updateNickNameWithOkHttpAsync(
                    playerId,
                    newNickName,
                    context,
                    navController,
                    roomPlayerDto
                )
            } else {
                showNicknameChangeModal(context, playerId, navController, roomPlayerDto)
                Toast.makeText(context, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        .create()

    dialog.show()
}