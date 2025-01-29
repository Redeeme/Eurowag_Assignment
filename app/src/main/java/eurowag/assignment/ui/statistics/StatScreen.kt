package eurowag.assignment.ui.statistics

import android.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import eurowag.assignment.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatScreen(navController: NavController, viewModel: StatisticsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.statistics),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
        ) {
            StatisticRow(
                label = stringResource(id = R.string.total_distance),
                value = "%.3f".format(state.totalDistance),
                unit = stringResource(id = R.string.km)
            )

            StatisticRow(
                label = stringResource(id = R.string.average_speed),
                value = "%.2f".format(state.averageSpeed),
                unit = stringResource(id = R.string.km_h)
            )

            StatisticRow(
                label = stringResource(id = R.string.min_speed),
                value = "%.2f".format(state.minSpeed),
                unit = stringResource(id = R.string.km_h)
            )

            StatisticRow(
                label = stringResource(id = R.string.max_speed),
                value = "%.2f".format(state.maxSpeed),
                unit = stringResource(id = R.string.km_h)
            )
            StatisticRow(
                label = stringResource(id = R.string.average_altitude),
                value = "%.2f".format(state.avgAlt),
                unit = stringResource(id = R.string.m)
            )

            if (state.entries.size < 1) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.empty_stat),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    factory = { context ->
                        LineChart(context).apply {
                            data = LineData(LineDataSet(state.entries, "Speed (km/h)").apply {
                                color = Color.BLUE
                                setDrawCircles(false)
                                lineWidth = 2f
                            })

                            xAxis.apply {
                                valueFormatter = IndexAxisValueFormatter(
                                    state.locations.map {
                                        SimpleDateFormat("HH:mm", Locale.getDefault())
                                            .format(Date(it.time))
                                    }
                                )
                                position = XAxis.XAxisPosition.BOTTOM
                                textColor = Color.WHITE
                            }

                            axisLeft.textColor = Color.WHITE
                            axisRight.textColor = Color.WHITE
                            legend.textColor = Color.WHITE

                            description.isEnabled = false
                            legend.isEnabled = true
                            setTouchEnabled(true)
                            setPinchZoom(true)
                        }
                    }
                )
            }
        }
    }

}

@Composable
fun StatisticRow(
    label: String,
    value: String,
    unit: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Row {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = unit,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}