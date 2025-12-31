package com.example.chatappmvp.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chatappmvp.ui.chatdetail.ChatDetailScreen
import com.example.chatappmvp.ui.chatlist.ChatListScreen

sealed class Screen(val route: String) {
    object ChatList : Screen("chatList")
    object ChatDetail : Screen("chatDetail/{chatId}") {
        fun createRoute(chatId: String) = "chatDetail/$chatId"
    }
}

@Composable
fun ChatNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.ChatList.route
    ) {
        composable(route = Screen.ChatList.route) {
            ChatListScreen(
                onChatClick = { chatId ->
                    Log.d("###", "ChatNavGraph: coming to onChat click with chatId $chatId")
                    navController.navigate(Screen.ChatDetail.createRoute(chatId))
                }
            )
        }

        composable(
            route = Screen.ChatDetail.route,
            arguments = listOf(
                navArgument("chatId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            ChatDetailScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

    }

}