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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbUp
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
        var rolledSum by remember { mutableStateOf(0) }
        var targetNumber by remember { mutableStateOf(6) }
        var rollCount by remember { mutableStateOf(0) }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 주사위 UI를 스크롤 가능하게 설정
            Box(
                modifier = Modifier
                    .weight(1f) // 상단 영역을 스크롤 가능한 공간으로 설정
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    if (isRolling) {
                        LaunchedEffect(Unit) {
                            delay(1000)
                            diceValues = List(numDice) { Random.nextInt(1, 7) }
                            rolledText = diceValues.joinToString(", ")
                            rolledSum += diceValues.sum()
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

            // 고정된 하단 UI
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("목표 숫자: ")
                    Button(
                        onClick = {
                            if (targetNumber > 0) targetNumber--
                        },
                        colors = ButtonDefaults.buttonColors(Color.LightGray),
                        enabled = rolledSum < targetNumber // 조건 추가
                    ) {
                        Text("-")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("$targetNumber")
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { targetNumber++ },
                        colors = ButtonDefaults.buttonColors(Color.LightGray),
                        enabled = rolledSum < targetNumber // 조건 추가
                    ) {
                        Text("+")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                        colors = ButtonDefaults.buttonColors(Color.LightGray),
                        enabled = rolledSum < targetNumber // 조건 추가
                    ) {
                        Text("-")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(numDice.toString())
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            numDice++
                            showGifList = List(numDice) { false }
                        },
                        colors = ButtonDefaults.buttonColors(Color.LightGray),
                        enabled = rolledSum < targetNumber // 조건 추가
                    ) {
                        Text("+")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("개")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (!isRolling) {
                            isRolling = true
                            rollCount++
                        }
                    },
                    enabled = !isRolling && rolledSum < targetNumber, // 조건 추가
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Refresh Icon",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("굴리기(${rollCount}회)")
                }

                Spacer(modifier = Modifier.height(5.dp))

                Button(
                    onClick = {
                        // 모든 상태 초기화
                      //  diceValues = List(1) { 1 }
                        numDice = 1
                        rolledText = ""
                        isRolling = false
                        showGifList = List(1) { false }
                        rolledSum = 0
                      //  targetNumber = 0
                        rollCount = 0
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Icon",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("다시하기")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("주사위 합: $rolledSum", style = MaterialTheme.typography.bodyLarge)
                if (targetNumber >= 1 && rollCount > 0) {
                    Text("목표 숫자: $targetNumber")
                    if (targetNumber == rolledSum) {
                        Text("목표 숫자와 일치! 🎉", color = Color.Green)
                        Text("주사위를 ${rollCount}번 만에 목표 숫자와 일치했어요!")
                    } else if (targetNumber < rolledSum) {
                        Text("목표 숫자 맞추기에 실패하였습니다! \uD83D\uDC80", color = Color.Red)
                    } else {
                        Text("목표 숫자에 도달하지 못했습니다.", color = Color.Blue)
                    }
                }
            }
        }
    }




    @Composable
    fun GifImageList(showGifList: List<Boolean>) {
        // GIF 이미지 개수가 1개일 때는 Row로 가로로 정렬하고, 그 외에는 LazyVerticalGrid로 세로로 정렬
        if (showGifList.size == 1) {
            Box(
                modifier = Modifier
                    .fillMaxSize(), // 화면 전체를 채움
                contentAlignment = Alignment.Center // 수평, 수직 중앙 정렬
            ) {
                if (showGifList.first()) {
                    GifImage(drawableId = R.drawable.dice_rolling)
                }
            }
        } else if(showGifList.size == 2){

            Box(
                modifier = Modifier.fillMaxSize(), // 화면 전체를 채우도록 설정
                contentAlignment = Alignment.Center // 상하, 좌우 중앙 정렬
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3), // 한 줄에 3개씩 세로로 나열
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center, // 항목 간 세로 간격 설정
                    horizontalArrangement = Arrangement.Center // 항목 간 가로 간격 설정
                ) {
                    if (showGifList.getOrNull(0) == true) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center // 중앙 정렬
                            ) {
                                GifImage(
                                    drawableId = R.drawable.dice_rolling,
                                    modifier = Modifier
                                        .offset(x = (70).dp) // 왼쪽으로 30dp 이동
                                )
                            }
                        }
                    }

                    if (showGifList.getOrNull(1) == true) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center // 중앙 정렬
                            ) {
                                GifImage(
                                    drawableId = R.drawable.dice_rolling,
                                    modifier = Modifier
                                        .offset(x = (70).dp) // 왼쪽으로 30dp 이동
                                )
                            }
                        }
                    }
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
                    size(Size(600, 600)) // 원하는 가로와 세로 크기 (px 단위)
                }).build(), imageLoader = imageLoader
            ),
            contentDescription = null,
            modifier = modifier.fillMaxWidth(),
        )
    }


    @Composable
    fun RollMultipleDice(diceValues: List<Int>) {
        if (diceValues.size == 1) {
            // 주사위 개수가 1개일 때 중앙 배치
            Box(
                modifier = Modifier
                    .fillMaxSize(), // 화면 전체를 채움
                contentAlignment = Alignment.Center // 수평, 수직 중앙 정렬
            ) {
                Dice(diceValues.first()) // 주사위 1개 표시
            }
        } else if (diceValues.size == 2) {
            // 주사위 개수가 2개일 때 좌우 중간 배치
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.Center, // 좌우 중앙 정렬
                verticalAlignment = Alignment.CenterVertically // 상하 중앙 정렬
            ) {
                Dice(
                    value = diceValues.first(),
                    modifier = Modifier.padding(start = 16.dp) // 첫 번째 주사위에 왼쪽 마진 추가
                )
                Spacer(modifier = Modifier.width(16.dp)) // 주사위 간 간격 설정
                Dice(value = diceValues.last()) // 두 번째 주사위 표시
            }

        } else {
            // 주사위 개수가 3개 이상일 때는 기존 방식대로 세로로 배치
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), // 여백 추가
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
            modifier = Modifier.size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            DiceFace(value)
        }
    }

    @Composable
    fun Dice(value: Int, modifier: Modifier = Modifier) {
        Box(
            modifier = modifier.size(100.dp),
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