package navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.outlined.Check
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource
import tech.mobiledeveloper.jethabit.resources.Res
import tech.mobiledeveloper.jethabit.resources.health_tab
import tech.mobiledeveloper.jethabit.resources.title_daily
import tech.mobiledeveloper.jethabit.resources.title_profile
import tech.mobiledeveloper.jethabit.resources.title_statistics
import tech.mobiledeveloper.jethabit.resources.chat_title

enum class AppScreens(
    val title: String,
    val icon: ImageVector,
    val titleRes: StringResource
) {
    Daily("daily", Icons.Default.Home, Res.string.title_daily),
    Health("health", Icons.Default.Favorite, Res.string.health_tab),
    Statistics("statistics", Icons.Outlined.Check, Res.string.title_statistics),
    Chat("chat", Icons.Default.Chat, Res.string.chat_title),
    Profile("profile", Icons.Default.Person, Res.string.title_profile)
} 