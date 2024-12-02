import android.util.Log
import com.dodam.dicegame.vo.RoomJoinVO
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException

// 응답에서 returnCode 값을 파싱해서 반환
fun joinRoomWithOkHttpSync(roomJoinInfo: RoomJoinVO): Int? {

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val roomJoinAdapter = moshi.adapter(RoomJoinVO::class.java)

    val client = OkHttpClient()

    // RoomJoinVO 객체를 JSON으로 변환
    val jsonBody = roomJoinAdapter.toJson(roomJoinInfo)

    // 요청 본문 설정
    val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())

    // 요청 객체 생성
    val url = "http://152.67.209.165:9081/dicegame/room/join"
    val request = Request.Builder()
        .url(url) // API 엔드포인트
        .post(requestBody)
        .addHeader("accept", "*/*")
        .addHeader("Content-Type", "application/json")
        .build()

    return try {
        // 동기 요청 실행
        val response: Response = client.newCall(request).execute()
        if (response.isSuccessful) {
            // 응답 본문 읽기
            val responseBody = response.body?.string()
            val returnCode = parseReturnCodeFromJson(responseBody)

            if (returnCode != null) {
                Log.d("OkHttp", "Room joined with returnCode: $returnCode")
            } else {
                Log.e("OkHttp", "Failed to parse returnCode from response body")
            }

            returnCode
        } else {
            Log.e("OkHttp", "Error: ${response.code} - ${response.message}")
            null
        }
    } catch (e: IOException) {
        Log.e("OkHttp", "Failed to join room: ${e.message}")
        e.printStackTrace() // 스택 트레이스 출력
        null
    }
}

// 응답 JSON에서 returnCode 추출
fun parseReturnCodeFromJson(responseBody: String?): Int? {
    return try {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val jsonAdapter = moshi.adapter(ResponseData::class.java)
        val response = jsonAdapter.fromJson(responseBody ?: "")
        response?.returnCode
    } catch (e: Exception) {
        Log.e("OkHttp", "Failed to parse JSON: ${e.message}")
        null
    }
}

// 응답 데이터 클래스
data class ResponseData(val returnCode: Int)
