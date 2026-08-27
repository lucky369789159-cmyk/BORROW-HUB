package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.BorrowViewModel
import com.example.ui.components.HubHeader
import com.example.ui.screens.*
import com.example.ui.theme.BorrowHubTheme

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Explore : Screen("explore", "Explore", Icons.Default.Search)
    object Requests : Screen("requests", "I Need This", Icons.Default.Campaign)
    object ListAi : Screen("list_ai", "List Item", Icons.Default.AddAPhoto)
    object Rentals : Screen("rentals", "Rentals & Chat", Icons.Default.Chat)
    object Profile : Screen("profile", "Borrow Score", Icons.Default.VerifiedUser)
}

class MainActivity : ComponentActivity() {

    private val viewModel: BorrowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BorrowHubTheme {
                BorrowHubApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun BorrowHubApp(viewModel: BorrowViewModel) {
    val selectedHub by viewModel.selectedHub.collectAsState()
    val hubs = viewModel.hubs

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Explore) }

    val screens = listOf(
        Screen.Explore,
        Screen.Requests,
        Screen.ListAi,
        Screen.Rentals,
        Screen.Profile
    )

    Scaffold(
        topBar = {
            HubHeader(
                currentHub = selectedHub,
                allHubs = hubs,
                onHubSelected = { viewModel.selectHub(it) }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFF3F4F9),
                tonalElevation = 0.dp
            ) {
                screens.forEach { screen ->
                    val isSelected = currentScreen.route == screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF001D35),
                            selectedTextColor = Color(0xFF001D35),
                            indicatorColor = Color(0xFFD3E4FF),
                            unselectedIconColor = Color(0xFF44474E),
                            unselectedTextColor = Color(0xFF44474E)
                        ),
                        modifier = Modifier.testTag("nav_tab_${screen.route}")
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentScreen) {
                Screen.Explore -> ExploreScreen(
                    viewModel = viewModel,
                    onNavigateToRentals = { currentScreen = Screen.Rentals }
                )
                Screen.Requests -> NeedRequestsScreen(
                    viewModel = viewModel,
                    onNavigateToRentals = { currentScreen = Screen.Rentals }
                )
                Screen.ListAi -> AiListingScreen(
                    viewModel = viewModel,
                    onItemListed = { currentScreen = Screen.Explore }
                )
                Screen.Rentals -> RentalsChatScreen(
                    viewModel = viewModel
                )
                Screen.Profile -> ProfileTrustScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}
