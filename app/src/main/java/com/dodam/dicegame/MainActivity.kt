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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
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

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
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
        var rolledSum by remember { mutableStateOf(0) } // 주사위 값의 합을 저장할 변수 추가
        var targetNumber by remember { mutableStateOf("") } // 목표 숫자 상태 변수 추가
        var isMatch by remember { mutableStateOf(false) } // 목표 숫자와 일치하는지 여부
        var rollCount by remember { mutableStateOf(0) } // 주사위 굴린 횟수


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 목표 숫자 입력 UI 추가
                TextField(
                    value = targetNumber,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) {
                            targetNumber = it
                        }
                    },
                    label = { Text("목표 숫자 입력") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("주사위 개수: ")
                    Button(
                        onClick = {
                            if (numDice > 1) {
                                numDice--
                                showGifList = List(numDice) { false }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(Color.LightGray)
                    ) {
                        Text("-")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${numDice}개")
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            numDice++
                            showGifList = List(numDice) { false }
                        },
                        colors = ButtonDefaults.buttonColors(Color.LightGray)
                    ) {
                        Text("+")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (!isRolling) { // 주사위 굴리는 중이 아닐 때만 동작
                            isRolling = true
                            rollCount++
                        }
                    },
                    enabled = !isRolling, // isRolling이 true일 때 버튼 비활성화
                    modifier = Modifier
                        .fillMaxWidth() // 버튼을 양쪽으로 꽉 채움
                        .padding(horizontal = 16.dp) // 양 옆 여백 추가
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Icon",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("굴리기")
                }

                // 주사위 값의 합을 화면에 표시
                Spacer(modifier = Modifier.height(16.dp))
                Text("주사위 합: $rolledSum", style = MaterialTheme.typography.bodyLarge)
                if (targetNumber.isNotEmpty()) {
                    Text("목표 숫자: $targetNumber")
                    if (isMatch) {
                        Text("목표 숫자와 일치! 🎉", color = Color.Green)
                        Text("주사위를 ${rollCount}번 만에 목표 숫자와 일치했어요!")
                        rollCount = 0
                    } else {
                        Text("목표 숫자와 일치하지 않음.", color = Color.Red)
                    }
                }

            }

            // 주사위 결과 표시 부분
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 120.dp), // 상단 고정된 버튼 아래에 위치
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isRolling) {
                    LaunchedEffect(Unit) {
                        delay(1000)
                        diceValues = List(numDice) { Random.nextInt(1, 7) }
                        rolledText = diceValues.joinToString(", ")
                        rolledSum = diceValues.sum() // 주사위 값의 합 계산
                        isMatch = targetNumber.toIntOrNull() == rolledSum // 목표 숫자와 비교
                        showGifList = List(numDice) { false }
                        isRolling = false
                    }
                    showGifList = List(numDice) { true }
                    GifImageList(showGifList)
                } else {
                    RollMultipleDice(diceValues)
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
}