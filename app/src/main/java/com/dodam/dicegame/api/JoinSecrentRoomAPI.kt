import android.util.Log
import com.dodam.dicegame.api.createRequestBody
import com.dodam.dicegame.api.executeRequest
import com.dodam.dicegame.api.fromJson
import com.dodam.dicegame.api.toJson
import com.dodam.dicegame.vo.RoomJoinVO
import okhttp3.Request

// 방 참여 API
fun joinRoomWithOkHttpSync(roomJoinInfo: RoomJoinVO): Int? {
    val url = "http://152.67.209.165:9081/dicegame/room/join"
    val jsonBody = toJson(roomJoinInfo, RoomJoinVO::class.java)
    val requestBody = createRequestBody(jsonBody)
    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .addHeader("accept", "*/*")
        .addHeader("Content-Type", "application/json")
        .build()

    val responseBody = executeRequest(request)
    val responseData = fromJson(responseBody, ResponseData::class.java)
    return responseData?.returnCode?.also {
        Log.d("OkHttp", "Room joined successfully with returnCode: $it")
    } ?: run {
        Log.e("OkHttp", "Failed to join room or parse returnCode")
        null
    }
}

// 응답 데이터 클래스
data class ResponseData(val returnCode: Int)
