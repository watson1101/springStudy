package hong.com.simplememo.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import hong.com.simplememo.data.api.ApiClient
import hong.com.simplememo.data.repository.AuthRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val repository = remember { AuthRepository() }

    var serverUrl by remember { mutableStateOf("http://192.168.1.100:8080") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(80.dp))
        Text("Simple Memo", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("多端提醒备忘", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        Spacer(Modifier.height(40.dp))

        OutlinedTextField(
            value = serverUrl, onValueChange = { serverUrl = it },
            label = { Text("服务器地址") }, singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = username, onValueChange = { username = it },
            label = { Text("用户名") }, singleLine = true,
            leadingIcon = { Icon(Icons.Default.Person, null) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("密码") }, singleLine = true,
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )

        errorMsg?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                scope.launch {
                    loading = true; errorMsg = null
                    ApiClient.setBaseUrl(serverUrl)
                    repository.login(username, password).fold(
                        onSuccess = {
                            ApiClient.setBaseUrl(serverUrl)
                            navController.navigate("memos") { popUpTo("login") { inclusive = true } }
                        },
                        onFailure = { errorMsg = it.message ?: "登录失败" }
                    )
                    loading = false
                }
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            enabled = !loading
        ) { if (loading) CircularProgressIndicator( modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
            else Text("登录", fontSize = 16.sp) }
    }
}
