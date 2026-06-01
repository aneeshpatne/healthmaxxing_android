package com.aneesh.healthmaxxing.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aneesh.healthmaxxing.R
import com.aneesh.healthmaxxing.account.AccountState
import com.aneesh.healthmaxxing.account.AccountViewModel
import com.aneesh.healthmaxxing.navigation.Destination
import com.aneesh.healthmaxxing.ui.login.login
import com.aneesh.healthmaxxing.ui.metrics.MetricsScreen
import com.aneesh.healthmaxxing.ui.record.RecordScreen
import kotlinx.coroutines.delay

@Composable
fun AppScaffold(vm: AccountViewModel = hiltViewModel()) {
    val accountState by vm.accountState.collectAsState()

    when (accountState) {
        AccountState.Loading -> {
            LoadingScreen()
        }

        AccountState.LoggedOut -> {
            login()
        }

        is AccountState.LoggedIn -> {
            MainAppScaffold()
        }
    }
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
private fun LoadingScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading-screen")
    val subtitleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "subtitle-alpha"
    )

    val logo = AnimatedImageVector.animatedVectorResource(R.drawable.animated_logo)
    var atEnd by remember { mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        delay(120)
        atEnd = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8FCFD),
                        Color.White,
                        Color(0xFFF1F8F9)
                    )
                )
            )
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAnimatedVectorPainter(logo, atEnd),
                contentDescription = "Loading logo",
                modifier = Modifier.size(180.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(26.dp))
            Text(
                text = "Preparing your dashboard",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily(Font(R.font.cormorant_garamond_variablefont_wght)),
                letterSpacing = 0.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Syncing account and health data",
                color = MaterialTheme.colorScheme.primary.copy(alpha = subtitleAlpha),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Image(
                painter = painterResource(R.drawable.aneeshpatne),
                contentDescription = "Aneesh Patne signature",
                modifier = Modifier
                    .fillMaxWidth(0.18f)
                    .alpha(0.42f),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun MainAppScaffold() {
    val navController = rememberNavController()
    var selectedDestination by rememberSaveable {
        mutableIntStateOf(Destination.METRICS.ordinal)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar() },
        bottomBar = {
            FormaBottomBar(
                selectedDestination = selectedDestination,
                onDestinationSelected = { index, destination ->
                    selectedDestination = index
                    navController.navigate(destination.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.METRICS.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destination.METRICS.route) {
                MetricsScreen()
            }
            composable(Destination.WORKOUTS.route) {
                DestinationText(text = "Workouts")
            }
            composable(Destination.RECORD.route) {
                RecordScreen()
            }
            composable(Destination.VITALS.route) {
                DestinationText(text = "Vitals")
            }
        }
    }
}

@Composable
private fun DestinationText(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text)
    }
}
