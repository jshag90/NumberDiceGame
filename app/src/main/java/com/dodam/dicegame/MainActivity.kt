package com.dodam.dicegame

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dodam.dicegame.ui.theme.DiceGameTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uuid = UUIDManager.getOrCreateUUID(this)

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
                        val navController = rememberNavController()
                        AppNavigation(navController)
                    }
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun AppNavigation(navController: NavHostController) {
        NavHost(
            navController = navController,
            startDestination = "multi_play" // 기본 화면
        ) {
            composable("single_play") {
                SinglePlayScreen(navController)
            }
            composable("multi_play") {
                MultiPlayScreen(navController)
            }
            composable("game_room/{targetNumber}/{numDice}/{isPublic}/{entryCode}/{maxPlayer}/{roomId}/{isRoomMaster}") { backStackEntry ->
                val targetNumber = backStackEntry.arguments?.getString("targetNumber") ?: ""
                val numDice = backStackEntry.arguments?.getString("numDice") ?: ""
                val isPublic = backStackEntry.arguments?.getString("isPublic") ?: ""
                val entryCode = backStackEntry.arguments?.getString("entryCode") ?: ""
                val maxPlayer = backStackEntry.arguments?.getString("maxPlayer") ?: "2"
                val roomId = backStackEntry.arguments?.getString("roomId") ?: "0"
                val isRoomMaster = backStackEntry.arguments?.getString("isRoomMaster") ?:"false"
                MultiDiceRoller(targetNumber, numDice, isPublic, entryCode, maxPlayer, roomId, isRoomMaster,navController)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun SinglePlayScreen(navController: NavHostController) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopButtons(navController, currentScreen = "single_play")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // 남은 공간을 채움
                verticalArrangement = Arrangement.Center
            ) {
                DiceRoller()
            }
        }
    }

    @Composable
    fun TopButtons(navController: NavHostController, currentScreen: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (currentScreen != "multi_play") {
                        navController.navigate("multi_play") {
                            popUpTo("multi_play") { inclusive = true }
                        }
                    }
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (currentScreen == "multi_play") Color(0xFF141C25) else Color.White, // 클릭 여부에 따라 배경색 설정
                    contentColor = if (currentScreen == "multi_play") Color.White else Color(0xFF141C25) // 클릭 여부에 따라 텍스트 색상 설정
                ),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .padding(start = 4.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Text("게임하기")
            }

            Button(
                onClick = {
                    if (currentScreen != "single_play") {
                        navController.navigate("single_play") {
                            popUpTo("single_play") { inclusive = true }
                        }
                    }
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (currentScreen == "single_play") Color(0xFF141C25) else Color.White, // 클릭 여부에 따라 배경색 설정
                    contentColor = if (currentScreen == "single_play") Color.White else Color(0xFF141C25) // 클릭 여부에 따라 텍스트 색상 설정
                ),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .padding(end = 4.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Text("혼자하기")
            }

        }
    }




    @Composable
    fun MultiPlayScreen(navController: NavHostController) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopButtons(navController, currentScreen = "multi_play")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // 남은 공간을 채움
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RoomActionsScreen(
                    navController,
                    onCreateRoomClick = { println("방 만들기 클릭") },
                    onPrivateRoomClick = { println("비공개 방 입장 클릭") },
                    onPublicRoomClick = { println("공개 방 입장 클릭") }
                )

            }
        }
    }


}