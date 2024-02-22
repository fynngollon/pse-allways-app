package com.pseteamtwo.allways.uicomponents


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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

import androidx.compose.material3.TimeInput

import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pseteamtwo.allways.trips.StageUiState
import com.pseteamtwo.allways.trip.Mode
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StageCard(
    modifier: Modifier = Modifier,
    stageUiState: StageUiState,
) {
    var showStartLocationSelector by rememberSaveable {
        mutableStateOf(false)
    }

    var showEndLocationSelector by rememberSaveable {
        mutableStateOf(false)
    }


    //Displays a Card containing information about one stage of a trip.
    /*Card(
        modifier = modifier.height(200.dp)
    ){
        Column {
            //First Row of the card containing the starting point
            Row(
                modifier = modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Canvas(modifier = modifier
                    .weight(1f)
                ) {
                    drawCircle(
                        color = Color.Black,
                        radius = 17.5f,
                        center = Offset(size.width/2, size.height/2)
                    )
                }
                BasicText(
                    text = stageUiState.startLocationName,
                    modifier = modifier.weight(7f)
                )
                Button(
                    onClick = { showStartLocationSelector = true },
                    modifier = modifier
                        .weight(3f)
                        .height(50.dp)
                        .padding(4.dp)
                ) {
                    Text(text = "ändern")
                }
            }

            //Second Row of the card containing the dashed line and the starting point, end point and mode of the stage.
            Row(
                modifier = modifier.weight(2.5f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //First Column of the second row containing the dashed line
                Column(
                    modifier = modifier.weight(1f)
                ){
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        drawLine(
                            start = Offset(x = size.width/2, y = 0f),
                            end = Offset(x = size.width/2, y = size.height),
                            strokeWidth = 4f,
                            color = Color.Black,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                        )
                    }
                }

                //Second Column of the second row containing the starting point, end point and mode of the stage
                Column(
                    modifier = modifier
                        .weight(10f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    //Row containing the starting time
                    Row(
                        modifier = Modifier
                             ,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center

                    ) {
                        *//*Column() {
                            Row(modifier = Modifier.padding(bottom = 10.dp)){
                                Text(text = "Startzeitpunkt")
                            }
                            Row() {
                                val state = rememberTimePickerState()
                                TimeInput(
                                    state = state,
                                )

                            }

                        }*//*

                        BasicText(text = "Von: ")

                        TimeField(
                            modifier = modifier,
                            initialHour = stageUiState.startDateTime.hour,
                            initialMinute = stageUiState.startDateTime.minute,
                            onHourChange = {hour: Int -> stageUiState.setStartTime(hour, stageUiState.startDateTime.minute)},
                            onMinuteChange = {minute: Int -> stageUiState.setStartTime(stageUiState.startDateTime.hour, minute)}
                        )
                    }

                    //Row containing the mode
                    Row(
                        modifier = modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        var expanded by remember {
                            mutableStateOf(false)
                        }

                        var selectedMode by remember { mutableStateOf(stageUiState.mode) }

                        *//*val categories = arrayOf("Auto", "Zug", "zu Fuß")
                        var category by remember {
                            mutableStateOf(categories[0])
                        }
                        var mode : Mode = when(category) {
                            categories[0] -> Mode.CAR_DRIVER
                            categories[1] -> Mode.REGIONAL_TRAIN
                            categories[2] -> Mode.WALK
                            else -> {Mode.CAR_DRIVER}
                        }*//*


                        BasicText(text = "Verkehrsmittel: ")

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
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
                                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                textStyle = TextStyle.Default.copy(fontSize = 13.sp),
                                modifier = Modifier
                                    .menuAnchor()
                                    .padding(start = 16.dp)
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

                    }

                    //Row containing the end time
                    Row(modifier = Modifier) {
                        *//*Column() {
                            Row(modifier = Modifier.padding(bottom = 10.dp)){
                                Text(text = "Endzeitpunkt")
                            }
                            Row() {
                                val state = rememberTimePickerState()
                                TimeInput(
                                    state = state,
                                )
                            }

                        }*//*
                        BasicText(text = "Bis: ")
                        TimeField(
                            initialHour = stageUiState.endDateTime.hour,
                            initialMinute = stageUiState.endDateTime.minute,
                            onHourChange = {hour: Int -> stageUiState.setEndTime(hour, stageUiState.startDateTime.minute)},
                            onMinuteChange = {minute: Int -> stageUiState.setEndTime(stageUiState.startDateTime.hour, minute)}
                        )

                    }
                }
            }

            //Third row of the card containing the end point
            Row (
                modifier = modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ){
                Canvas(modifier = modifier
                    .weight(1f)
                ) {
                    drawCircle(
                        color = Color.Black,
                        radius = 17.5f,
                        center = Offset(size.width/2, size.height/2)
                    )
                }
                BasicText(
                    text = stageUiState.endLocationName,
                    modifier = modifier.weight(7f)
                )
                Button(
                    onClick = { showEndLocationSelector = true },
                    modifier = modifier
                        .weight(3f)
                        .height(50.dp)
                        .padding(4.dp)
                ) {
                    Text(text = "ändern")
                }
            }
        }
    }*/
    
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
                            Text(
                                text = "Von:",
                                modifier = modifier.weight(0.5f)
                            )
                            TimeField(
                                modifier = modifier.weight(1.25f),
                                initialHour = stageUiState.startDateTime.hour,
                                initialMinute = stageUiState.startDateTime.minute,
                                onTimeChange = { hour: Int, minute: Int -> stageUiState.setStartTime(hour, minute)}
                            )
                            Spacer(modifier = modifier.weight(3f))
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
                                    textStyle = TextStyle.Default.copy(fontSize = 13.sp),
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
                            Text(
                                text = "Bis:",
                                modifier = modifier.weight(0.5f)
                                )
                            TimeField(
                                modifier = modifier.weight(1.25f),
                                initialHour = stageUiState.endDateTime.hour,
                                initialMinute = stageUiState.endDateTime.minute,
                                onTimeChange = { hour: Int, minute: Int -> stageUiState.setEndTime(hour, minute)}
                            )
                            Spacer(modifier = modifier.weight(3f))
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
    initialHour: Int,
    initialMinute: Int,
    onTimeChange: (Int, Int) -> Unit,
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

    var minuteTextFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = String.format("%02d", initialMinute),
                selection = TextRange(0)
            )
        )
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        BasicTextField(
            value = hourTextFieldValue,
            onValueChange = {
                if(Regex("0?[0-9]|1[0-9]|2[0-3]|").matches(it.text)) {
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
                    onTimeChange(hourTextFieldValue.text.toInt(), minuteTextFieldValue.text.toInt())
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
        Text(
            text = " : ",
            style = MaterialTheme.typography.titleMedium
        )
        BasicTextField(
            value = minuteTextFieldValue,
            onValueChange = {
                if(Regex("|0?[0-9]|[1-5][0-9]").matches(it.text)) {
                    minuteTextFieldValue = TextFieldValue(
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
                    onTimeChange(hourTextFieldValue.text.toInt(), minuteTextFieldValue.text.toInt())
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
        Text(
            text = "  Uhr",
        )
    }


    /*val timePickerState = rememberTimePickerState(
        initialHour,
        initialMinute,
        true
    )

    var showTimeInputDialog by rememberSaveable {
        mutableStateOf(false)
    }

    Row(
        modifier = modifier.width(60.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = timePickerState.hour.toString(),
            modifier = modifier
                .clickable {
                showTimeInputDialog = true
                },
            style = MaterialTheme.typography.titleMedium
        )
        BasicTextField(
            value = if (timePickerState.hour < 10) "0" + timePickerState.hour.toString() else timePickerState.hour.toString(),
            onValueChange = {},
            modifier = Modifier
                .height(20.dp)
                .width(30.dp)
                .clickable { showTimeInputDialog = true },
            enabled = false,
            textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
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
        Text(
            text = " : ",
            style = MaterialTheme.typography.titleMedium
            )
        BasicTextField(
            value = if (timePickerState.minute < 10) "0" + timePickerState.minute.toString() else timePickerState.minute.toString(),
            onValueChange = {},
            modifier = Modifier
                .height(20.dp)
                .width(30.dp)
                .clickable { showTimeInputDialog = true },
            enabled = false,
            textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
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
        Text(
            text = timePickerState.minute.toString(),
            modifier = modifier
                .clickable {
                showTimeInputDialog = true
                },
            style = MaterialTheme.typography.titleMedium
        )
    }

    if(showTimeInputDialog) {
        Dialog(
            onDismissRequest = { showTimeInputDialog = false }
        ) {
            Card(
                modifier = modifier
                    .height(90.dp)
                    .width(400.dp)
            ) {
                Row(
                    modifier = modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start

                    ) {
                        TimeInput(
                            state = timePickerState,
                            modifier.offset(0.dp, 10.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            onTimeChange(timePickerState.hour, timePickerState.minute)
                            showTimeInputDialog = false
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check, contentDescription = "Bestätigen",
                            modifier = modifier
                                .size(48.dp)
                        )
                    }
                }
            }
        }
    }*/


    /*NumbersField(
        startValue = hour,
        highestNumber = 23,
        onValueChange = onHourChange
    )
    BasicText(modifier = Modifier.padding(start = 10.dp, end = 10.dp), text = ":")
    NumbersField(
        startValue = minute,
        highestNumber = 59,
        onValueChange = onMinuteChange
    )*/
}

//Composable Function for displaying a textfield that can only have numbers as input
/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumbersField(
    startValue: Int,
    highestNumber : Int,
    onValueChange: (Int) -> Unit
){
    var text by rememberSaveable {mutableStateOf(String.format("%02d", startValue))}


    TextField(
        value = TextFieldValue(text),
        onValueChange = {
            textFieldValue = when(it.text.length) {
                0 -> TextFieldValue("00", TextRange(2, +2))
                1 -> TextFieldValue("0" + it, TextRange(2, 2))
                else -> {
                    if(it.text.take(1) == "0") {
                        if (filter.matches(it.text.takeLast(2)))  TextFieldValue(it.text.takeLast(2), TextRange(2, 2))  else textFieldValue
                    } else {
                        if(filter.matches(it.text.take(2))) TextFieldValue(it.text.take(2), TextRange(2, 2))  else textFieldValue
                    }
                }
            }
        },
        modifier = Modifier
            .height(20.dp)
            .width(30.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(4.dp)),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
    )

    BasicTextField(
        modifier = Modifier
            .height(20.dp)
            .width(30.dp),
        //.wrapContentHeight(Alignment.CenterVertically),
        value = text,
        //onValueChange = {if (it.isDigitsOnly()) text = it},
        onValueChange = {

                text = when(it.length) {
                    0 -> "00"
                    1 -> if (it == "0") "00" else "0" + it
                    else -> {
                        if(it.take(1) == "0") {
                            if (filter.matches(it.takeLast(2))) it.takeLast(2) else text
                        } else {
                            if(filter.matches(it.take(2))) it.take(2) else text
                        }
                    }
                }


            if (it.isDigitsOnly()) {
            if(it != "") {
                if(it.toInt() in 0..highestNumber) {
                    text = it
                    onValueChange(it.toInt())
                }
            }
             }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        decorationBox = {innerTextField ->
            Row(
                modifier = Modifier
                    .border(
                        BorderStroke(1.dp, Color.Black),
                        shape = RoundedCornerShape(4.dp)
                    )
            ){
                innerTextField()
            }

        }
    )
}*/

@Preview
@Composable
fun StageCardPreview() {
    StageCard(
        stageUiState = StageUiState(
            id = 1,
            mode = Mode.NONE,
            isFirstStageOfTrip = false,
            isLastStageOfTrip = true,
            startDateTime = LocalDateTime.MAX,
            endDateTime = LocalDateTime.MAX,
            startLocation = GeoPoint(49.001061, 8.413361),
            endLocation = GeoPoint(49.001061, 8.413361),
            startLocationName = "Test",
            endLocationName = "Test",
            setMode = {mode: Mode -> },
            setStartDate = {},
            setEndDate = {},
            setStartTime = {hour: Int, minute: Int -> },
            setEndTime = {hour: Int, minute: Int -> },
            setStartLocation = {geoPoint: GeoPoint ->  },
            setEndLocation = {geoPoint: GeoPoint ->  },
            setStartLocationName = {locationName: String -> },
            setEndLocationName = {locationName: String -> },
            updateStage = {}
        )
    )
}

