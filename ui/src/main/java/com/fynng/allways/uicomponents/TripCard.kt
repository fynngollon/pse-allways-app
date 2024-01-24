package com.fynng.allways.uicomponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.fynng.ui.R
import com.pseteamtwo.allways.trip.Mode
import java.util.Locale


@Composable
fun TripCard(
    modifier: Modifier,
    mode: Mode?,
    isConfirmed: Boolean,
    startHour: Int,
    startMinute: Int,
    endHour: Int,
    endMinute: Int,
    startLocation: String,
    endLocation: String,
    duration: Int,
    distance: Int,
) {
    Card(
        modifier = modifier
            .height(64.dp),
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
                    .weight(1f)
            ) {
                Text(text = formatDistance(distance))
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
                    .fillMaxSize()
                    .weight(3f)
            ) {
                Row(
                    modifier = modifier
                        .weight(1.5f)
                        .fillMaxSize()
                ) {
                    Text(
                        modifier = modifier.padding(top = 2.dp).weight(1f).fillMaxSize(),
                        text = startLocation,
                        textAlign = TextAlign.Left,
                        overflow = TextOverflow.Ellipsis
                    )

                    Column(
                        modifier = modifier.fillMaxHeight().weight(1f).fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        //TODO: Icon abhängig von mode machen
                        Icon(
                            painter = painterResource(id = R.drawable.directions_walk_fill0_wght400_grad0_opsz24),
                            contentDescription = "",
                            tint = Color.Black
                        )
                    }

                    Text(
                        modifier = modifier.padding(top = 2.dp).weight(1f).fillMaxSize(),
                        text = endLocation,
                        textAlign = TextAlign.Right,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    modifier = modifier
                        .weight(1f)
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
                            radius = 20f,
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
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(32f, 16f), 32f)
                        )
                    }
                    Canvas(modifier = modifier
                        .weight(1f)
                        .fillMaxSize()

                    ) {
                        drawCircle(
                            color = Color.Black,
                            radius = 20f,
                            center = Offset(size.width/2, size.height/2)
                        )
                    }
                }
                Row(
                    modifier = modifier
                        .weight(1.25f)
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                    Text(
                        modifier = modifier.weight(1f).fillMaxSize(),
                        text = formatTime(startHour, startMinute),
                        textAlign = TextAlign.Left
                    )
                    Text(
                        modifier = modifier.weight(1f).fillMaxSize(),
                        text = formatDuration(duration),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = modifier.weight(1f).fillMaxSize(),
                        text = formatTime(endHour, endMinute),
                        textAlign = TextAlign.Right
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
                /*if(!isConfirmed) {
                    Icon(
                        modifier = modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.help_black_24dp),
                        contentDescription = "",
                        tint = Color(0xffFF8C00)
                    )
                }*/
            }
        }
    }

}

fun formatTime(hour: Int, minute: Int): String {
    return String.format("%02d:%02d", hour, minute)
}

fun formatDistance(distance: Int): String {
    return if (distance > 1000)  String.format(Locale.GERMANY, "%.1f km", distance/1000f) else String.format("%d m", distance)
}

fun formatDuration(duration: Int): String {
    if(duration >= 1440) {
        return String.format("%d d %d h %02d min", duration/1440, (duration%1440)/60, (duration%1440)%60)
    } else if (duration >= 60) {
        return String.format("%d h %02d min", duration/60, duration%60)
    } else {
        return String.format("%d min", duration)
    }
}

@Preview
@Composable
fun TripCardPreview() {
    TripCard(
        mode = null,
        isConfirmed = false,
        startHour = 9,
        startMinute = 5,
        endHour = 9,
        endMinute = 10,
        startLocation = "KIT Mensadfhdfhfdhfd",
        endLocation = "KIT ",
        duration = 1738,
        distance = 4200,
        modifier = Modifier
    )
}
