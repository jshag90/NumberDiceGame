import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.navigation.NavController
import com.dodam.dicegame.api.executeRequest
import com.dodam.dicegame.api.fromJson
import com.dodam.dicegame.api.serverUrl
import com.dodam.dicegame.api.updateNickNameWithOkHttpAsync
import com.dodam.dicegame.dto.RoomPlayerDto
import com.dodam.dicegame.vo.ReturnCodeVO
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Request

fun joinPublicRoomWithOkHttpSync(nickName: String, context: Context,navController: NavController, roomPlayerDto: RoomPlayerDto): RoomPlayerDto? {
    val url = "$serverUrl/room/public/join/nick_name=${nickName}"

    val request = Request.Builder().url(url).get().addHeader("accept", "*/*").build()

    val responseBody = executeRequest(request)
    if (responseBody == null) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "서버 응답이 없습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
        }
        return null
    }

    val returnCodeVO: ReturnCodeVO<RoomPlayerDto>? = fromJson(responseBody)

    if (returnCodeVO == null) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "서버에 문제가 발생했습니다. 관리자에게 문의하세요.", Toast.LENGTH_SHORT).show()
        }
        return null
    }

    if (returnCodeVO.returnCode == -2) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "공개방이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
        }
        return null
    }

    if (returnCodeVO.returnCode == -5) {
        CoroutineScope(Dispatchers.Main).launch {
            returnCodeVO.data?.let { showNicknameChangeModal(context, it.playerId, navController,  returnCodeVO.data) }
        }
        return returnCodeVO.data
    }

    return returnCodeVO.data
}

fun showNicknameChangeModal(
    context: Context,
    playerId: Int,
    navController: NavController,
    roomPlayerDto: RoomPlayerDto
) {
    val editText = EditText(context).apply {
        hint = "닉네임이 이미 존재합니다. 새로운 닉네임을 입력해주세요."
        textSize = 14f
    }

    val layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    editText.layoutParams = layoutParams

    val dialog = AlertDialog.Builder(context)
        .setCancelable(false)
        .setView(editText) // Set the EditText directly in the dialog
        .setPositiveButton("변경") { _, _ ->
            val newNickName = editText.text.toString()
            if (newNickName.isNotEmpty()) {
                updateNickNameWithOkHttpAsync(playerId, newNickName, context, navController, roomPlayerDto)
            } else {
                showNicknameChangeModal(context, playerId, navController, roomPlayerDto)
                Toast.makeText(context, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        .create()

    // Show the dialog
    dialog.show()
}

