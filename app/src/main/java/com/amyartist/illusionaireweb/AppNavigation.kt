import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.amyartist.illusionaireweb.FirebaseViewModel
import com.amyartist.illusionaireweb.data.GameViewModel
import com.amyartist.illusionaireweb.screens.GameDisplay
import com.amyartist.illusionaireweb.screens.MainMenuScreen

object AppDestinations {
    const val MAIN_MENU_ROUTE = "mainMenu"
    const val GAME_DISPLAY_ROUTE = "game_display"
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    gameViewModel: GameViewModel
) {
    val context = LocalContext.current
    val firebaseViewModel: FirebaseViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = AppDestinations.MAIN_MENU_ROUTE
    ) {
        composable(AppDestinations.MAIN_MENU_ROUTE) {
            MainMenuScreen(
                onButtonOneClick = {
                    gameViewModel.playSound(gameViewModel.selectSoundId)
                    Toast.makeText(context, "Generating room images... Check Logcat.", Toast.LENGTH_LONG).show()
                    firebaseViewModel.generateRooms(context.applicationContext)
                },
                onButtonTwoClick = {
                    gameViewModel.playSound(gameViewModel.selectSoundId)
                    navController.navigate(AppDestinations.GAME_DISPLAY_ROUTE)
                }
            )
        }

        composable(AppDestinations.GAME_DISPLAY_ROUTE) {
            GameDisplay(
                gameViewModel = gameViewModel
            )
        }
    }
}