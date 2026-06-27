package hong.com.simplememo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hong.com.simplememo.ui.login.LoginScreen
import hong.com.simplememo.ui.memo.MemoEditorScreen
import hong.com.simplememo.ui.memo.MemoListScreen
import hong.com.simplememo.ui.settings.SettingsScreen
import hong.com.simplememo.ui.theme.SimpleMemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleMemoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "login") {
                        composable("login") { LoginScreen(navController) }
                        composable("memos") { MemoListScreen(navController) }
                        composable("memos/create") { MemoEditorScreen(navController) }
                        composable("memos/{memoId}/edit") { backStackEntry ->
                            MemoEditorScreen(navController, backStackEntry.arguments?.getString("memoId"))
                        }
                        composable("settings") { SettingsScreen(navController) }
                    }
                }
            }
        }
    }
}
