package hong.com.simplememo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val AppleBlue = Color(0xFF007AFF)
val AppleBlueDark = Color(0xFF0A84FF)
val AppleRed = Color(0xFFFF3B30)
val AppleGreen = Color(0xFF34C759)
val AppleOrange = Color(0xFFFF9500)
val AppleGray = Color(0xFF8E8E93)
val AppleGray2 = Color(0xFFAEAEB2)
val AppleGray5 = Color(0xFFE5E5EA)
val AppleGray6 = Color(0xFFF2F2F7)

private val LightColors = lightColorScheme(
    primary = AppleBlue, onPrimary = Color.White,
    secondary = AppleGray, onSecondary = Color.White,
    background = Color(0xFFF5F5F7), onBackground = Color(0xFF1D1D1F),
    surface = Color.White, onSurface = Color(0xFF1D1D1F),
    surfaceVariant = Color(0xFFF2F2F7),
    error = AppleRed, onError = Color.White,
    outline = Color(0xFFD2D2D7)
)

private val DarkColors = darkColorScheme(
    primary = AppleBlueDark, onPrimary = Color.White,
    secondary = AppleGray, onSecondary = Color.White,
    background = Color(0xFF1C1C1E), onBackground = Color(0xFFF5F5F7),
    surface = Color(0xFF2C2C2E), onSurface = Color(0xFFF5F5F7),
    surfaceVariant = Color(0xFF3A3A3C),
    error = Color(0xFFFF453A), onError = Color.White,
    outline = Color(0xFF48484A)
)

@Composable
fun SimpleMemoTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography(),
        content = content
    )
}
