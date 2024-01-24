package com.fynng.ui.trips

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fynng.allways.uicomponents.TripCard
import com.fynng.ui.R


@Composable
fun TripsScreen(modifier: Modifier) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Box(
                    modifier = modifier,
                    contentAlignment = Alignment.TopEnd
                    ) {
                    Column {
                        Spacer(modifier = modifier.height(10.dp))
                        TripCard(
                            modifier = modifier,
                            mode = null,
                            isConfirmed = false,
                            startHour = 12,
                            startMinute = 42,
                            endHour = 23,
                            endMinute = 32,
                            startLocation = "sdgsdfgdfhhdf",
                            endLocation = "asdffds dfhdfhhdf",
                            duration = 32,
                            distance = 586
                        )
                    }
                    Icon(
                        modifier = modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.help_fill1_wght400_grad0_opsz24),
                        contentDescription = "",
                        //tint = Color(0xffFF8C00)
                    )
                }
            }
            item {
                TripCard(
                    modifier = modifier,
                    mode = null,
                    isConfirmed = false,
                    startHour = 12,
                    startMinute = 42,
                    endHour = 23,
                    endMinute = 32,
                    startLocation = "sdgsdfgdfhhdf",
                    endLocation = "asdffds dfhdfhhdf",
                    duration = 32,
                    distance = 586
                )
            }
            item {
                TripCard(
                    modifier = modifier,
                    mode = null,
                    isConfirmed = false,
                    startHour = 12,
                    startMinute = 42,
                    endHour = 23,
                    endMinute = 32,
                    startLocation = "sdgsdfgdfhhdf",
                    endLocation = "asdffds dfhdfhhdf",
                    duration = 32,
                    distance = 586
                )
            }
            item {
                TripCard(
                    modifier = modifier,
                    mode = null,
                    isConfirmed = false,
                    startHour = 12,
                    startMinute = 42,
                    endHour = 23,
                    endMinute = 32,
                    startLocation = "sdgsdfgdfhhdf",
                    endLocation = "asdffds dfhdfhhdf",
                    duration = 32,
                    distance = 586
                )
            }
            item {
                TripCard(
                    modifier = modifier,
                    mode = null,
                    isConfirmed = false,
                    startHour = 12,
                    startMinute = 42,
                    endHour = 23,
                    endMinute = 32,
                    startLocation = "sdgsdfgdfhhdf",
                    endLocation = "asdffds dfhdfhhdf",
                    duration = 32,
                    distance = 586
                )
            }
        }
    }

}

@Preview
@Composable
fun TripsScreenPreview() {
    TripsScreen(modifier = Modifier)
}