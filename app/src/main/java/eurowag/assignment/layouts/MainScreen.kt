package eurowag.assignment.layouts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberMarkerState
import eurowag.assignment.MainViewModel
import eurowag.assignment.R
import eurowag.assignment.database.LocationPoint
import eurowag.assignment.layouts.navigation.Screen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MainScreen(navController: NavController, viewModel: MainViewModel = hiltViewModel(),permissionRequest: ()->Unit,) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState { state.locations.size }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppMainBar(navController = navController, onZoom = viewModel::zoomToShowPins) },
        bottomBar = {
            BottomAppMainBar(
                isTracking = state.isTracking,
                viewModel = viewModel,
                permissionRequest = permissionRequest
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = state.cameraPositionState,
                properties = MapProperties(mapType = MapType.NORMAL),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = false,
                    compassEnabled = true,
                    mapToolbarEnabled = false
                )
            ) {
                state.locations.forEachIndexed { index, loc ->
                    key(loc.id) {
                        Marker(
                            state = rememberMarkerState(
                                key = "${loc.latitude}_${loc.longitude}",
                                position = LatLng(loc.latitude, loc.longitude)
                            ),
                            icon = if (index == pagerState.currentPage) {
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                            } else {
                                BitmapDescriptorFactory.defaultMarker()
                            },
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                                true
                            }
                        )
                    }
                }

                Polyline(
                    points = state.locations.map { LatLng(it.latitude, it.longitude) },
                    color = Color.Blue,
                    width = 5f,
                    pattern = listOf(Dot(), Gap(20f))
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .wrapContentHeight()
                    .fillMaxWidth()
            ) { page ->
                LocationCard(
                    location = state.locations[page]
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppMainBar(navController: NavController, onZoom: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.labelLarge
            )
        },
        actions = {
            IconButton(onClick = onZoom) {
                Icon(
                    imageVector = Icons.Default.ZoomOutMap,
                    contentDescription = "Zoom to all pins"
                )
            }
            IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        }
    )
}


@Composable
fun BottomAppMainBar(
    isTracking: Boolean,
    viewModel: MainViewModel,
    permissionRequest: ()->Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (viewModel.checkPermissions(context)){
                        viewModel.startTracking()
                    }else{
                        permissionRequest()
                    }

                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                enabled = !isTracking,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start Tracking")
                }
            }

            Button(
                onClick = { viewModel.stopTracking() },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                enabled = isTracking,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Stop Tracking")
                }
            }
        }
    }
}

@Composable
fun LocationCard(
    location: LocationPoint,
) {
    Card(
        modifier = Modifier
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(Date(location.time)),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Lat: ${location.latitude}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Lng: ${location.longitude}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "accuracy: ${location.accuracy}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "provider: ${location.provider}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}