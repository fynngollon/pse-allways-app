package com.pseteamtwo.allways.uicomponents


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField

import androidx.compose.material3.rememberDatePickerState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pseteamtwo.allways.trips.StageUiState
import com.pseteamtwo.allways.trip.Mode
import org.osmdroid.util.GeoPoint
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StageCard(
    modifier: Modifier = Modifier,
    stageUiState: StageUiState,
) {
    val previousStageUiState = stageUiState.getPreviousStageUiState()
    val nextStageUiState = stageUiState.getNextStageUiState()

    var showStartLocationSelector by rememberSaveable {
        mutableStateOf(false)
    }

    var showEndLocationSelector by rememberSaveable {
        mutableStateOf(false)
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        Row {
            Column(
                modifier = modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Canvas(modifier = modifier
                    .weight(0.5f)
                    .fillMaxSize()
                ) {
                    drawCircle(
                        color = Color.Black,
                        radius = 17.5f,
                        center = Offset(size.width/2, size.height)
                    )
                }
                Canvas(modifier = modifier
                    .weight(3.5f)
                    .fillMaxSize()
                    .padding(16.dp)
                ) {
                    drawLine(
                        color = Color.Black,
                        start = Offset(size.width/2, 0f),
                        end = Offset(size.width/2, size.height),
                        strokeWidth = 6f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                }
                Canvas(modifier = modifier
                    .weight(0.5f)
                    .fillMaxSize()
                ) {
                    drawCircle(
                        color = Color.Black,
                        radius = 17.5f,
                        center = Offset(size.width/2, 0f)
                    )
                }
            }
            Column(
                modifier = modifier
                    .weight(10f)
                    .fillMaxSize(),
            ) {
                Row(
                    modifier = modifier
                        .weight(1f)
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stageUiState.startLocationName,
                        modifier = modifier.weight(7f)
                    )
                    if(stageUiState.isFirstStageOfTrip) {
                        Button(
                            onClick = {showStartLocationSelector = true},
                            modifier.weight(3f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B7DAF))
                        ) {
                            Text(text = "Ändern")
                        }
                    }
                    Spacer(modifier = modifier.weight(0.25f))
                }
                Row(
                    modifier = modifier
                        .weight(2.5f)
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = modifier
                                .weight(1f)
                                .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TimeField(
                                initialDateTime = stageUiState.startDateTime,
                                initialHour = stageUiState.startHour,
                                initialMinute = stageUiState.startMinute,
                                minDateTime = previousStageUiState?.endDateTime ?: LocalDateTime.MIN,
                                minHour = if(previousStageUiState?.endDate == stageUiState.startDate) previousStageUiState.endHour else 0,
                                minMinute =
                                if(previousStageUiState?.endDate == stageUiState.startDate) {
                                    if (previousStageUiState.endHour == stageUiState.startHour) previousStageUiState.endMinute else 0
                                } else 0,
                                maxDateTime = stageUiState.endDateTime,
                                maxHour = if(stageUiState.startDate == stageUiState.endDate) stageUiState.endHour else 23,
                                maxMinute =
                                if(stageUiState.startDate == stageUiState.endDate) {
                                    if (stageUiState.startHour == stageUiState.endHour) stageUiState.endMinute else 59
                                } else 59,
                                onTimeChange = { hour: Int, minute: Int -> stageUiState.setStartTime(hour, minute)},
                                onDateChange = { date: LocalDate -> stageUiState.setStartDate(date)}
                            )
                        }
                        Row(
                            modifier = modifier
                                .weight(1f)
                                .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            var expanded by rememberSaveable {
                                mutableStateOf(false)
                            }

                            var selectedMode by rememberSaveable {
                                mutableStateOf(stageUiState.mode)
                            }

                            Text(
                                text = "Verkehrsmittel: ",
                                modifier = modifier.weight(2.5f)
                            )

                            Spacer(modifier = modifier.weight(1.5f))

                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded },
                                modifier = modifier.weight(3f)
                            ) {

                                OutlinedTextField(
                                    value = selectedMode.name,
                                    onValueChange = {},
                                    readOnly = true,
                                    leadingIcon = {

                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                    },
                                    shape = RoundedCornerShape(4.dp),
                                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                    textStyle = TextStyle.Default.copy(fontSize = 12.sp),
                                    modifier = Modifier
                                        .menuAnchor()
                                        .height(50.dp)
                                )

                                ExposedDropdownMenu(expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    for(mode in Mode.entries) {
                                        DropdownMenuItem(
                                            text = {
                                                Text(mode.name)
                                            },
                                            onClick = {
                                                expanded = false
                                                selectedMode = mode
                                                stageUiState.setMode(mode)
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = modifier.weight(0.25f))
                        }
                        Row(
                            modifier = modifier
                                .weight(1f)
                                .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TimeField(
                                initialDateTime = stageUiState.endDateTime,
                                initialHour = stageUiState.endDateTime.hour,
                                initialMinute = stageUiState.endDateTime.minute,
                                minDateTime = stageUiState.startDateTime,
                                minHour = if(stageUiState.startDate == stageUiState.endDate) stageUiState.startHour else 0,
                                minMinute =
                                    if(stageUiState.startDate == stageUiState.endDate) {
                                        if (stageUiState.startHour == stageUiState.endHour) stageUiState.startMinute else 0
                                    } else 0,
                                maxDateTime = nextStageUiState?.startDateTime ?: LocalDateTime.MAX,
                                maxHour = if(stageUiState.endDate == nextStageUiState?.startDate) nextStageUiState.startHour else 23,
                                maxMinute =
                                    if(stageUiState.endDate == nextStageUiState?.startDate) {
                                        if(stageUiState.endHour == nextStageUiState.startHour) nextStageUiState.startMinute else 59
                                    } else 59,
                                onTimeChange = { hour: Int, minute: Int -> stageUiState.setEndTime(hour, minute)},
                                onDateChange = { date: LocalDate -> stageUiState.setEndDate(date)}
                            )

                        }
                    }
                }
                Row(
                    modifier = modifier
                        .weight(1f)
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stageUiState.endLocationName,
                        modifier = modifier.weight(7f)
                    )
                    Button(
                        onClick = {showEndLocationSelector = true},
                        modifier.weight(3f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B7DAF))
                    ) {
                        Text(text = "Ändern")
                    }
                    Spacer(modifier = modifier.weight(0.25f))
                }
            }
        }
    }


    // star
    if (showStartLocationSelector) {
        Dialog(onDismissRequest = {showStartLocationSelector = false}) {
            LocationSelector(
                modifier = modifier,
                onDismissRequest = {showStartLocationSelector = false},
                onConfirm = {
                    geoPoint: GeoPoint, locationName: String ->
                    stageUiState.setStartLocation(geoPoint)
                    stageUiState.setStartLocationName(locationName)
                },
                startPosition = GeoPoint(stageUiState.startLocation)
            )
        }
    }

    // end Location
    if (showEndLocationSelector) {
        Dialog(onDismissRequest = {showEndLocationSelector = false}) {
            LocationSelector(
                modifier = modifier,
                onDismissRequest = {showEndLocationSelector = false},
                onConfirm = {
                    geoPoint: GeoPoint, locationName: String ->
                    stageUiState.setEndLocation(geoPoint)
                    stageUiState.setEndLocationName(locationName)
                },
                startPosition = GeoPoint(stageUiState.endLocation),

            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeField(
    modifier: Modifier = Modifier,
    initialDateTime: LocalDateTime,
    initialHour: Int,
    initialMinute: Int,
    minDateTime: LocalDateTime,
    minHour: Int,
    minMinute: Int,
    maxDateTime: LocalDateTime,
    maxHour: Int,
    maxMinute: Int,
    onTimeChange: (Int, Int) -> Unit,
    onDateChange: (LocalDate) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyBoardController = LocalSoftwareKeyboardController.current


    var hourTextFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = String.format("%02d", initialHour),
                selection = TextRange(2)
            )
        )
    }
    var lastValidHour by rememberSaveable {
        mutableIntStateOf(initialHour)
    }

    var minuteTextFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = String.format("%02d", initialMinute),
                selection = TextRange(2)
            )
        )
    }
    var lastValidMinute by rememberSaveable {
        mutableIntStateOf(initialHour)
    }

    var showDatePicker by rememberSaveable {
        mutableStateOf(false)
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = "Bis:",
        )
        Spacer(modifier = modifier.width(16.dp))

        BasicTextField(
            value = hourTextFieldValue,
            onValueChange = {
                if(Regex("0?[0-9]|1[0-9]|2[0-3]|").matches(it.text)) {
                    /*if (it.text.isEmpty() || it.text == "0") {
                        hourTextFieldValue = TextFieldValue(
                            text = it.text,
                            selection = TextRange(it.text.length)
                        )
                    } else {
                        val hour = it.text.toInt()
                        if (minHour <= hour && hour <= maxHour) {
                            hourTextFieldValue = TextFieldValue(
                                text = it.text,
                                selection = TextRange(it.text.length)
                            )
                        }
                    }*/
                    hourTextFieldValue = TextFieldValue(
                        text = it.text,
                        selection = TextRange(it.text.length)
                    )
                }

            },
            modifier = modifier
                .height(20.dp)
                .width(30.dp),

            enabled = true,
            textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    keyBoardController?.hide()
                    if (hourTextFieldValue.text.isEmpty()) {
                        hourTextFieldValue = TextFieldValue(
                            text = String.format("%02d", lastValidHour),
                            selection = TextRange(2)
                        )
                    } else {
                        val hour = hourTextFieldValue.text.toInt()
                        if ( minHour <= hour && hour <= maxHour) {
                            onTimeChange(
                                hourTextFieldValue.text.toInt(),
                                minuteTextFieldValue.text.toInt()
                            )
                            lastValidHour = hour
                        } else {
                            hourTextFieldValue = TextFieldValue(
                                text = String.format("%02d", lastValidHour),
                                selection = TextRange(2)
                            )
                        }
                    }
                }
            ),
            decorationBox = {innerTextField ->
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .border(
                            BorderStroke(0.25.dp, Color.Black),
                            shape = RoundedCornerShape(2.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){

                        innerTextField()

                }
            }
        )
        Text(
            text = " : ",
            style = MaterialTheme.typography.titleMedium
        )
        BasicTextField(
            value = minuteTextFieldValue,
            onValueChange = {
                if(Regex("0?[0-9]|[1-5][0-9]|").matches(it.text)) {
                    /*if (it.text.isEmpty()) {
                        minuteTextFieldValue = TextFieldValue(
                            text = it.text,
                            selection = TextRange(it.text.length)
                        )
                    } else {
                        val minute = it.text.toInt()
                        if( minMinute <= minute && minute <= maxMinute) {
                            minuteTextFieldValue = TextFieldValue(
                                text = it.text,
                                selection = TextRange(it.text.length)
                            )
                        }
                    }*/
                    minuteTextFieldValue = TextFieldValue(
                        text = it.text,
                        selection = TextRange(it.text.length)
                    )
                }

            },
            modifier = modifier
                .height(20.dp)
                .width(30.dp),
                //.weight(1f),
            enabled = true,
            textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    keyBoardController?.hide()
                    if (hourTextFieldValue.text.isEmpty()) {
                        hourTextFieldValue = TextFieldValue(
                            text = String.format("%02d", lastValidMinute),
                            selection = TextRange(2)
                        )
                    } else {
                        val minute = minuteTextFieldValue.text.toInt()
                        if ( minMinute < minute && minute < maxMinute) {
                            onTimeChange(
                                hourTextFieldValue.text.toInt(),
                                minuteTextFieldValue.text.toInt()
                            )
                            lastValidMinute = minute
                        } else {
                            minuteTextFieldValue = TextFieldValue(
                                text = String.format("%02d", lastValidMinute),
                                selection = TextRange(2)
                            )
                        }
                    }
                }
            ),
            decorationBox = {innerTextField ->
                Row(
                    modifier = modifier
                        .border(
                            BorderStroke(0.25.dp, Color.Black),
                            shape = RoundedCornerShape(2.dp)
                        ),
                    horizontalArrangement = Arrangement.Center
                ){
                    innerTextField()
                }

            }

        )
        Spacer(modifier = modifier.width(4.dp))
        Text(
            text = "Uhr",
            //modifier = modifier.weight(1f)
        )
        Spacer(modifier = modifier.width(4.dp))
        IconButton(
            onClick = { showDatePicker = true },
        ) {
            Icon(imageVector = Icons.Rounded.CalendarMonth, contentDescription = "Datum auswählen")
        }
        /*if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    val selectedDate = Instant
                        .ofEpochMilli(datePickerState.selectedDateMillis!!)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    if (minDateTime.toLocalDate() < selectedDate && selectedDate < maxDateTime.toLocalDate()) {
                        onDateChange(selectedDate)
                        showDatePicker = false
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }*/
    }
}

@Preview
@Composable
fun StageCardPreview() {
    StageCard(
        stageUiState = StageUiState(
            id = 1,
            stageId = 0,
            mode = Mode.NONE,
            isInDatabase = true,
            isToBeAddedBefore = false,
            isFirstStageOfTrip = false,
            isLastStageOfTrip = true,
            startDateTime = LocalDateTime.MAX,
            endDateTime = LocalDateTime.MAX,
            startLocation = GeoPoint(49.001061, 8.413361),
            endLocation = GeoPoint(49.001061, 8.413361),
            startLocationName = "Test",
            endLocationName = "Test",
            getPreviousStageUiState = {null},
            getNextStageUiState = {null},
            setMode = {mode: Mode -> },
            setStartDate = {},
            setEndDate = {},
            setStartTime = {hour: Int, minute: Int -> },
            setEndTime = {hour: Int, minute: Int -> },
            setStartLocation = {geoPoint: GeoPoint ->  },
            setEndLocation = {geoPoint: GeoPoint ->  },
            setStartLocationName = {locationName: String -> },
            setEndLocationName = {locationName: String -> },
        )
    )
}

