package com.dodam.dicegame

import android.os.Build
import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.layout.width
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.dodam.dicegame.ui.theme.DiceGameTheme
import kotlinx.coroutines.delay
import kotlin.random.Random
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiceGameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DiceRoller()
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun DiceRoller() {
    var diceValues by remember { mutableStateOf(List(1) { 1 }) }
    var numDice by remember { mutableStateOf(1) }
    var rolledText by remember { mutableStateOf("") }
    var isRolling by remember { mutableStateOf(false) }
    var showGifList by remember { mutableStateOf(List(1) { false }) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally, // 가로로 가운데 정렬
        verticalArrangement = Arrangement.Center
    ) {
        Text("주사위 갯수를 선택해주세요: ${numDice}개")
        Row {
            Button(onClick = {
                if (numDice > 1) {
                    numDice-- // 주사위 수 감소
                    showGifList = List(numDice) { false } // 리스트 사이즈 조정
                }
            }) {
                Text("-")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                numDice++ // 주사위 수 증가
                showGifList = List(numDice) { false } // 리스트 사이즈 조정
            }) {
                Text("+")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // GIF 이미지 리스트 표시 (주사위가 1개일 때 오른쪽, 왼쪽 가운데 정렬)
        if (isRolling) {
            showGifList = List(numDice) { true }
            if (numDice == 1) {
                // 주사위와 GIF 이미지가 하나일 경우 가로 중앙에 정렬
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center // 가로로 가운데 정렬
                ) {
                    GifImageList(showGifList) // GIF 이미지 리스트 표시
                }
            } else {
                GifImageList(showGifList) // GIF 이미지 리스트 표시
            }
        } else {
            if (numDice == 1) {
                // 주사위가 하나일 경우 가로로 중앙 정렬
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center // 가로로 가운데 정렬
                ) {
                    RollMultipleDice(diceValues)
                }
            } else {
                RollMultipleDice(diceValues) // 여러 개의 주사위 표시
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (numDice > 1) {
            Text("숫자 목록: $rolledText")
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(onClick = {
            isRolling = true
        }) {
            Text("주사위 굴리기")
        }

        if (isRolling) {
            LaunchedEffect(Unit) {
                delay(1000)
                diceValues = List(numDice) { Random.nextInt(1, 7) }
                rolledText = diceValues.joinToString(", ")
                showGifList = List(numDice) { false } // 주사위 굴린 후 GIF 숨기기
                isRolling = false
            }
        }
    }
}



@Composable
fun GifImageList(showGifList: List<Boolean>) {
    // GIF 이미지 개수가 1개일 때는 Row로 가로로 정렬하고, 그 외에는 LazyVerticalGrid로 세로로 정렬
    if (showGifList.size == 1) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center // 가로로 중앙 정렬
        ) {
            if (showGifList.first()) {
                GifImage(drawableId = R.drawable.dice_rolling)
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3), // 한 줄에 3개씩 세로로 나열
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp), // 항목 간 세로 간격 설정
            horizontalArrangement = Arrangement.spacedBy(16.dp) // 항목 간 가로 간격 설정
        ) {
            items(showGifList.size) { index ->
                if (showGifList[index]) {
                    GifImage(drawableId = R.drawable.dice_rolling)
                }
            }
        }
    }
}


@Composable
fun GifImage(
    modifier: Modifier = Modifier,
    drawableId: Int,
) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context).data(data = drawableId).apply(block = {
                size(Size.ORIGINAL)
            }).build(), imageLoader = imageLoader
        ),
        contentDescription = null,
        modifier = modifier.fillMaxWidth(),
    )
}


@Composable
fun RollMultipleDice(diceValues: List<Int>) {
    // 주사위 개수가 1개일 때는 Row로 가로로 정렬하고, 그 외에는 LazyVerticalGrid로 세로로 정렬
    if (diceValues.size == 1) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center // 가로로 중앙 정렬
        ) {
            Dice(diceValues.first()) // 주사위 1개만 표시
        }
    } else {
        // 여러 개의 주사위가 있을 경우 LazyVerticalGrid로 세로로 정렬
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(diceValues.size) { index ->
                Dice(diceValues[index]) // 각 주사위 표시
            }
        }
    }
}


@Composable
fun Dice(value: Int) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(Color.White)
            .border(2.dp, Color.Black)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        DiceFace(value)
    }
}

@Composable
fun DiceFace(value: Int) {
    val imageResId = when (value) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        6 -> R.drawable.dice_6
        else -> R.drawable.dice_1
    }

    Image(
        painter = painterResource(id = imageResId),
        contentDescription = "Dice Face",
        modifier = Modifier.size(200.dp)
    )
}
