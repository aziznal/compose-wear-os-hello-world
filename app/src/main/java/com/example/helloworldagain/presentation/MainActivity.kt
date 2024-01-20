/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

// For HTTP stuff: Retrofit or Ktor
// Local Storage: Room
// Shared Prefs for key-value storage


package com.example.helloworldagain.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.helloworldagain.R
import com.example.helloworldagain.presentation.destinations.DetailScreenDestination
import com.example.helloworldagain.presentation.theme.HelloWorldAgainTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {2
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp("Android")
        }
    }
}

@Composable
fun WearApp(greetingName: String) {
    HelloWorldAgainTheme {

        DestinationsNavHost(
            navGraph = NavGraphs.root
        )
    }
}

@Destination(start = true)
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator
) {
    val vm = remember {
        VM()
    }
    val state = vm.state.collectAsState()

    LaunchedEffect(true) {
        vm.effect.collect {
            when (it) {
                is Effect.ShowToast -> {
                    println("safdhgdfgh")
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        vm.onAction(UiAction.OnScreenLoad)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(text = "Home Screen ${state.value.isLoading}")
            Button(onClick = { vm.onAction(UiAction.OnButtonClick("sadfasf")) }) {
                Text(text = "Go to Detail Screen")
            }
        }
    }
}

@Destination
@Composable
fun DetailScreen(
    string: String,
    navigator: DestinationsNavigator
) {
    Column {
        Text(text = "Detail Screen $string")
        Button(onClick = { navigator.popBackStack() }) {
            Text(text = "Back")
        }
    }
}


class VM : ViewModel() {
    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private val _effect: Channel<Effect> = Channel()
    val effect: Flow<Effect> = _effect.consumeAsFlow()

    fun onAction(action: UiAction) {
        when (action) {
            is UiAction.OnButtonClick -> {
                _state.value = _state.value.copy(isLoading = false)
            }
            is UiAction.OnScreenLoad -> {
                viewModelScope.launch {
                    _effect.send(Effect.ShowToast("Toast"))
                }
            }
        }
    }
}

data class State(
    val isLoading: Boolean = true,
)

interface UiAction {
    data class OnButtonClick(val value: String) : UiAction
    object OnScreenLoad: UiAction
}

interface Effect {
    data class ShowToast(val message: String) : Effect
}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android")
}