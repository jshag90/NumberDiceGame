import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import android.widget.Toast
import com.dodam.dicegame.api.executeRequest
import com.dodam.dicegame.api.fromJson
import com.dodam.dicegame.dto.RoomPlayerDto
import com.dodam.dicegame.vo.ReturnCodeVO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Request

fun joinPublicRoomWithOkHttpSync(nickName: String, context: Context): RoomPlayerDto? {
    val url = "http://192.168.0.20:8080/room/public/join/nick_name=${nickName}"

    // GET 요청 생성
    val request = Request.Builder()
        .url(url)
        .get() // GET 요청으로 설정
        .addHeader("accept", "*/*")
        .build()

    // 요청 실행 및 응답 처리
    val responseBody = executeRequest(request)
    if (responseBody == null) {
        // Make sure to show this Toast on the main thread
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "서버 응답이 없습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
        }
        return null
    }

    // 응답 파싱
    val returnCodeVO: ReturnCodeVO<RoomPlayerDto>? = fromJson(responseBody)

    if (returnCodeVO == null) {
        // Make sure to show this Toast on the main thread
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "서버에 문제가 발생했습니다. 관리자에게 문의하세요.", Toast.LENGTH_SHORT).show()
        }
        return null
    }

    if (returnCodeVO.returnCode == -2) {
        // Make sure to show this Toast on the main thread
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "공개방이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
        }
        return null
    }

    // returnCode가 -5일 때 처리
    if (returnCodeVO.returnCode == -5) {
        CoroutineScope(Dispatchers.Main).launch {
            showNicknameChangeModal(context)
        }
        return returnCodeVO.data
    }

    // RoomData 반환
    return returnCodeVO.data
}

fun showNicknameChangeModal(context: Context) {
    val editText = EditText(context).apply {
        hint = "새로운 닉네임을 입력하세요."
    }

    val dialog = AlertDialog.Builder(context)
        .setMessage("해당 닉네임을 사용하는 사용자가 존재합니다.")
        .setCancelable(false)
        .setView(editText)  // Directly adding EditText to the dialog
        .setPositiveButton("변경") { _, _ ->
            val newNickName = editText.text.toString()
            if (newNickName.isNotEmpty()) {
                // Call API to update the nickname
                //updateNicknameAndJoinRoom(newNickName, currentNickName, context)
            } else {
                Toast.makeText(context, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        .setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }
        .create()

    dialog.show()
}

