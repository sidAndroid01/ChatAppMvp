package com.example.chatappmvp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatappmvp.ui.chatlist.ChatListScreen
import com.example.chatappmvp.viewmodel.ChatListingViewModel

sealed class Screen(val route: String) {
    object ChatList : Screen("chatList")
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
                   // todo
                }
            )
        }

    }

}