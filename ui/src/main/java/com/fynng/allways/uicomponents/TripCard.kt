package com.fynng.allways.uicomponents


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import com.fynng.allways.trips.TripUiState
import com.fynng.allways.trips.TripsViewModel
import com.pseteamtwo.allways.R
import java.util.Locale


@Composable
fun TripCard(
    modifier: Modifier = Modifier,
    tripsViewModel: TripsViewModel,
    tripUiState: TripUiState,
) {
    val showEditTripDialog = remember { mutableStateOf(false) }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopEnd
    ) {
        Column {
            Spacer(modifier = modifier.height(8.dp))
            Card(
                modifier = modifier
                    .height(88.dp)
                    .padding(horizontal = 8.dp)
                    .clickable(
                        enabled = true,
                        onClickLabel = "Edit Trip",
                    ) { showEditTripDialog.value = true },
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = modifier
                            .fillMaxSize()
                            .weight(2.5f)
                    ) {
                        Text(
                            text = formatDistance(tripUiState.distance),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = modifier
                            .fillMaxSize()
                            .weight(6f)
                    ) {
                        Row(
                            modifier = modifier
                                .weight(1.25f)
                                .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = modifier
                                    .padding(bottom = 4.dp)
                                    .weight(1f),
                                text = tripUiState.startLocationName,
                                textAlign = TextAlign.Left,
                                overflow = TextOverflow.Ellipsis,
                                softWrap = true,
                                style = MaterialTheme.typography.bodySmall
                            )

                            Column(
                                modifier = modifier
                                    .fillMaxHeight()
                                    .weight(1f),
                                verticalArrangement = Arrangement.Bottom,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                //TODO: Icon abhängig von mode machen
                                Icon(
                                    painter = painterResource(id = R.drawable.directions_walk_fill0_wght400_grad0_opsz24),
                                    contentDescription = "",
                                    modifier = modifier.size(28.dp),
                                    tint = Color.Black
                                )
                            }

                            Text(
                                modifier = modifier
                                    .padding(bottom = 4.dp)
                                    .weight(1f),
                                text = tripUiState.endLocationName,
                                textAlign = TextAlign.Right,
                                overflow = TextOverflow.Ellipsis,
                                softWrap = true,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Row(
                            modifier = modifier
                                .weight(0.5f)
                                .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Canvas(modifier = modifier
                                .weight(1f)
                                .fillMaxSize()
                            ) {
                                drawCircle(
                                    color = Color.Black,
                                    radius = 17.5f,
                                    center = Offset(size.width/2, size.height/2)
                                )
                            }
                            Canvas(modifier = modifier
                                .weight(15f)
                                .fillMaxSize()
                                .padding(8.dp)
                            ) {
                                drawLine(
                                    color = Color.Black,
                                    start = Offset(0f, size.height/2),
                                    end = Offset(size.width, size.height/2),
                                    strokeWidth = 4f,
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(32f, 16f), 0f)
                                )
                            }
                            Canvas(modifier = modifier
                                .weight(1f)
                                .fillMaxSize()

                            ) {
                                drawCircle(
                                    color = Color.Black,
                                    radius = 17.5f,
                                    center = Offset(size.width/2, size.height/2)
                                )
                            }
                        }
                        Row(
                            modifier = modifier
                                .weight(0.6f)
                                .fillMaxSize(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = modifier
                                    .weight(1f)
                                    .fillMaxSize(),
                                text = formatTime(
                                    tripUiState.startDateTime.hour,
                                    tripUiState.startDateTime.minute),
                                textAlign = TextAlign.Left,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                modifier = modifier
                                    .weight(2f)
                                    .fillMaxSize(),
                                text = formatDuration(tripUiState.duration),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                modifier = modifier
                                    .weight(1f)
                                    .fillMaxSize(),
                                text = formatTime(
                                    tripUiState.endDateTime.hour,
                                    tripUiState.endDateTime.minute),
                                textAlign = TextAlign.Right,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Column(
                        modifier = modifier
                            .weight(0.5f)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.End
                    ) {

                    }
                }
            }

        }
        if(!tripUiState.isConfirmed) {
            Box(modifier = modifier.size(24.dp)) {
                Canvas(modifier = modifier
                    .fillMaxSize()
                ) {
                    drawCircle(
                        color = Color.White,
                        radius = 20f,
                        center = Offset(size.width/2, size.height/2)
                    )
                }
            }
            Icon(
                modifier = modifier
                    .size(24.dp),
                painter = painterResource(id = R.drawable.help_fill1_wght400_grad0_opsz24),
                contentDescription = "",
                tint = Color(0xffFF8C00)
            )
        }
    }
    if(showEditTripDialog.value) {
        tripUiState.createStageUiStates
        Dialog(
            onDismissRequest = { showEditTripDialog.value = false },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                securePolicy = SecureFlagPolicy.Inherit,
                usePlatformDefaultWidth = false
            )
        ) {
            EditTripDialog(
                modifier = modifier,
                tripsViewModel = tripsViewModel,
                tripUiState = tripUiState
            )
        }
    }
}

fun formatTime(hour: Int, minute: Int): String {
    return String.format("%02d:%02d", hour, minute)
}

fun formatDistance(distance: Int): String {
    return if (distance > 1000)  String.format(Locale.GERMAN, "%.1f km", distance/1000f) else String.format("%d m", distance)
}

fun formatDuration(duration: Long): String {
    return if(duration >= 1440) {
        String.format("%d d %d h %02d min", duration/1440, (duration%1440)/60, (duration%1440)%60)
    } else if (duration >= 60) {
        String.format("%d h %02d min", duration/60, duration%60)
    } else {
        String.format("%d min", duration)
    }
}

@Preview
@Composable
fun TripCardPreview() {

}
