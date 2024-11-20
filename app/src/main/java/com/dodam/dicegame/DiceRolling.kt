package com.dodam.dicegame

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size

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
        .crossfade(true) // 부드러운 전환 효과
        .build()

    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context).data(data = drawableId).apply(block = {
                size(Size.ORIGINAL) // 원하는 가로와 세로 크기 (px 단위)
            }).build(), imageLoader = imageLoader
        ),
        contentDescription = null,
        modifier = modifier.size(120.dp).fillMaxWidth(),
    )
}