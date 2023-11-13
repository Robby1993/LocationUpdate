package com.robinson.locationupdate

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.robinson.locationupdate.ui.InitializingScreen
import com.robinson.locationupdate.ui.LocationPermissionState
import com.robinson.locationupdate.ui.LocationUpdatesScreen
import com.robinson.locationupdate.ui.ServiceUnavailableScreen
import com.robinson.locationupdate.ui.theme.LocationUpdateTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val locationPermissionState = LocationPermissionState(this) {
            if (it.hasPermission()) {
                viewModel.toggleLocationUpdates()
            }
        }

        setContent {
            LocationUpdateTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(stringResource(id = R.string.app_name))
                            }
                        )
                    }
                ) {
                    MainScreen(
                        viewModel = viewModel,
                        locationPermissionState = locationPermissionState
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(this, ForegroundLocationService::class.java)
        bindService(serviceIntent, viewModel, BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        unbindService(viewModel)
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    locationPermissionState: LocationPermissionState
) {
    val uiState by viewModel.playServicesAvailableState.collectAsState()
    val isLocationOn by viewModel.isReceivingLocationUpdates.collectAsState()
    val lastLocation by viewModel.lastLocation.collectAsState()

    when (uiState) {
        PlayServicesAvailableState.Initializing -> InitializingScreen()
        PlayServicesAvailableState.PlayServicesUnavailable -> ServiceUnavailableScreen()
        PlayServicesAvailableState.PlayServicesAvailable -> {
            LocationUpdatesScreen(
                showDegradedExperience = locationPermissionState.showDegradedExperience,
                needsPermissionRationale = locationPermissionState.shouldShowRationale(),
                onButtonClick = locationPermissionState::requestPermissions,
                isLocationOn = isLocationOn,
                location = lastLocation,
            )
        }
    }
}
