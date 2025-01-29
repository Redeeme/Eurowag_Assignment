package eurowag.assignment.ui.main

import android.util.Log
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollSource.Companion.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.currentStateAsState
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
import eurowag.assignment.R
import eurowag.assignment.database.entities.LocationPointEntity
import eurowag.assignment.navigation.Screen
import eurowag.assignment.ui.uiStates.LocationPointState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel(),
    permissionRequest: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState { state.locations.size }

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleStateFlow = lifecycleOwner.lifecycle.currentStateFlow
    val currentLifecycleState by lifecycleStateFlow.collectAsState()
    val currentLifecycleStatee = lifecycleOwner.lifecycle.currentStateAsState()


    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        Log.d("eventtt","ON_CREATE")
    }
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        Log.d("eventtt","ON_PAUSE")
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        Log.d("eventtt","ON_RESUME")
    }
    LifecycleEventEffect(Lifecycle.Event.ON_ANY) {
        Log.d("eventtt","ON_ANY")
    }
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        Log.d("eventtt","ON_PAUSE")
    }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        Log.d("eventtt","ON_STOP")
    }


    SideEffect{
        Log.d("eventtt","SideEffect")
    }
    LaunchedEffect(Unit) {
        Log.d("eventtt","LaunchedEffect")
    }


    Scaffold(
        topBar = {
            TopAppMainBar(
                navController = navController,
                onZoom = viewModel::zoomToShowPins
            )
        },
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
                MarkerList(state.locations, pagerState)

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
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .width(235.dp)
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
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            IconButton(onClick = onZoom) {
                Icon(
                    imageVector = Icons.Default.ZoomOutMap,
                    contentDescription = stringResource(id = R.string.zoom_to_all_pins)
                )
            }
            IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(id = R.string.settings)
                )
            }
        }
    )
}


@Composable
fun BottomAppMainBar(
    isTracking: Boolean,
    viewModel: MainViewModel,
    permissionRequest: () -> Unit,
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
                    if (viewModel.checkPermissions(context)) {
                        viewModel.startTracking()
                    } else {
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
                    Text(text = stringResource(id = R.string.start_tracking))
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
                    Text(text = stringResource(id = R.string.stop_tracking))
                }
            }
        }
    }
}

@Composable
fun LocationCard(
    location: LocationPointState,
) {
    val recomposeCount = remember { mutableStateOf(0) }
    SideEffect {
        recomposeCount.value++
        Log.d(
            "MarkerRecomposition",
            "Marker ${location.id} recomposed ${recomposeCount.value} times"
        )
    }

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
                text = "${stringResource(id = R.string.lat)}: ${location.latitude}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${stringResource(id = R.string.lng)}: ${location.longitude}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${stringResource(id = R.string.accuracy)}: ${location.accuracy}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${stringResource(id = R.string.provider)}: ${location.provider}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun MarkerList(
    locations: List<LocationPointState>,
    pagerState: PagerState,
) {
    val scope = rememberCoroutineScope()
    locations.forEachIndexed { index, location ->
        StableMarker(
            location = location,
            isSelected = index == pagerState.currentPage,
            onMarkerClick = remember(index) {
                {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }

                }
            }
        )
    }
}


@Composable
private fun StableMarker(
    location: LocationPointState,
    isSelected: Boolean,
    onMarkerClick: () -> Unit
) {
    Marker(
        state = rememberMarkerState(
            key = "${location.latitude}_${location.longitude}",
            position = LatLng(location.latitude, location.longitude)
        ),
        icon = if (isSelected) {
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
        } else {
            BitmapDescriptorFactory.defaultMarker()
        },
        onClick = {
            onMarkerClick()
            true
        }
    )
}