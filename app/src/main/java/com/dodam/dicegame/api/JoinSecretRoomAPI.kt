import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import com.dodam.dicegame.api.HttpHeaders
import com.dodam.dicegame.api.HttpHeadersValue
import com.dodam.dicegame.api.createRequestBody
import com.dodam.dicegame.api.executeRequest
import com.dodam.dicegame.api.fromJson
import com.dodam.dicegame.api.serverUrl
import com.dodam.dicegame.api.toJson
import com.dodam.dicegame.component.showNicknameChangeModal
import com.dodam.dicegame.dto.RoomPlayerDto
import com.dodam.dicegame.vo.ReturnCodeVO
import com.dodam.dicegame.vo.RoomJoinVO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Request

// 방 참여 API
fun joinSecretRoomWithOkHttpSync(
    roomJoinInfo: RoomJoinVO, context: Context,
    navController: NavController,
    roomPlayerDto: RoomPlayerDto
): RoomPlayerDto? {
    val url = "$serverUrl/room/secret/join"
    val jsonBody = toJson(roomJoinInfo, RoomJoinVO::class.java)
    val requestBody = createRequestBody(jsonBody)
    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .addHeader(HttpHeaders.ACCEPT, HttpHeadersValue.ACCEPT_VALUE)
        .addHeader(HttpHeaders.CONTENT_TYPE, HttpHeadersValue.CONTENT_TYPE_VALUE)
        .build()

    val responseBody = executeRequest(request)
    val returnCodeVO: ReturnCodeVO<RoomPlayerDto>? = fromJson(responseBody)
    if (returnCodeVO == null) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "서버에 문제가 발생했습니다. 관리자에게 문의하세요.", Toast.LENGTH_SHORT).show()
        }
        return null
    }

    if (returnCodeVO.returnCode == -2) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "비밀방이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
        }
        return null
    }

    if (returnCodeVO.returnCode == -3) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "설정된 인원 보다 더 많은 사용자가 입장할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
        return null
    }
    if (returnCodeVO.returnCode == -5) {
        CoroutineScope(Dispatchers.Main).launch {
            returnCodeVO.data?.let {
                showNicknameChangeModal(
                    context,
                    it.playerId,
                    navController,
                    returnCodeVO.data
                )
            }
        }
        return returnCodeVO.data
    }

    return returnCodeVO.data
}

