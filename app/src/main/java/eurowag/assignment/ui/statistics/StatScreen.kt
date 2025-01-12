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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatScreen(navController: NavController, viewModel: StatisticsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        StatisticRow(
            label = "Total Distance",
            value = "%.2f".format(state.totalDistance),
            unit = "km"
        )

        StatisticRow(
            label = "Average Speed",
            value = "%.1f".format(state.averageSpeed),
            unit = "km/h"
        )

        StatisticRow(
            label = "Min Speed",
            value = "%.1f".format(state.minSpeed),
            unit = "km/h"
        )

        StatisticRow(
            label = "Max Speed",
            value = "%.1f".format(state.maxSpeed),
            unit = "km/h"
        )
        StatisticRow(
            label = "Average Altitude",
            value = "%.1f".format(state.avgAlt),
            unit = "m"
        )

        if (state.entries.size < 1) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Not enough data to display chart",
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
                            textColor = android.graphics.Color.WHITE
                        }

                        axisLeft.textColor = android.graphics.Color.WHITE
                        axisRight.textColor = android.graphics.Color.WHITE
                        legend.textColor = android.graphics.Color.WHITE

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