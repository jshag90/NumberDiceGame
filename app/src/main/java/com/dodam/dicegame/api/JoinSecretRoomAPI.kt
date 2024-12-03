import android.content.Context
import android.widget.Toast
import com.dodam.dicegame.api.createRequestBody
import com.dodam.dicegame.api.executeRequest
import com.dodam.dicegame.api.fromJson
import com.dodam.dicegame.api.toJson
import com.dodam.dicegame.vo.ReturnCodeVO
import com.dodam.dicegame.vo.RoomJoinVO
import okhttp3.Request

// 방 참여 API
fun joinSecretRoomWithOkHttpSync(roomJoinInfo: RoomJoinVO, context: Context): Long? {
    val url = "http://152.67.209.165:9081/dicegame/room/secret/join"
    val jsonBody = toJson(roomJoinInfo, RoomJoinVO::class.java)
    val requestBody = createRequestBody(jsonBody)
    val request = Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .addHeader("accept", "*/*")
                        .addHeader("Content-Type", "application/json")
                        .build()

    val responseBody = executeRequest(request)
    val returnCodeVO: ReturnCodeVO<Long>? = fromJson(responseBody)
    if (returnCodeVO?.returnCode != 0) {
        Toast.makeText(context, "서버에 문제가 발생했습니다. 관리자에게 문의하세요.", Toast.LENGTH_SHORT).show()
        return null
    }

    return returnCodeVO.data //방번호
}

