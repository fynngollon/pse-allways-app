
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.pseteamtwo.allways.trip.Mode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StageCard() {

    var lineStart by remember {
        mutableStateOf(IntSize.Zero)
    }
    var lineEnd by remember {
        mutableStateOf(IntSize.Zero)
    }

    //Displays a Card containing information about one stage of a trip.
    Card{
        Column (
        ){

            //First Row of the card containing the starting point
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier
                        .size(size = 30.dp)
                        .padding(
                            start = 5.dp,
                            end = 5.dp
                        ),
                    imageVector = Icons.Default.Home,
                    contentDescription = "Person Icon"
                )
                BasicText(text = "Startort")
            }

            //Second Row of the card containing the dashed line and the starting point, end point and mode of the stage.
            Row {

                //First Column of the second row containing the dashed line
                Column{
                    Canvas(
                        modifier = Modifier
                            .width(50.dp)
                    ) {
                        val canvasWidth = size.width
                        val canvasHeight = lineEnd.height.toFloat()
                        drawLine(
                            start = Offset(x = 30f, y = 0f),
                            end = Offset(x = 30f, y = lineEnd.height.toFloat()),
                            color = Color.Black,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                        )
                    }
                }

                //Second Column of the second row containing the starting point, end point and mode of the stage
                Column(
                    modifier = Modifier
                        .onSizeChanged {
                            lineEnd = it
                        }
                ) {
                    //Row containing the starting time
                    Row(
                        modifier = Modifier
                            .padding(10.dp) ,
                        verticalAlignment = Alignment.CenterVertically

                    ) {
                        /*Column() {
                            Row(modifier = Modifier.padding(bottom = 10.dp)){
                                Text(text = "Startzeitpunkt")
                            }
                            Row() {
                                val state = rememberTimePickerState()
                                TimeInput(
                                    state = state,
                                )

                            }

                        }*/
                        
                        BasicText(text = "Von: ")
                        TimeField()


                    }

                    //Row containing the mode
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(10.dp)

                    ) {
                        var isExpanded by remember {
                            mutableStateOf(false)
                        }
                        val categories = arrayOf("Auto", "Zug", "zu Fuß")
                        var category by remember {
                            mutableStateOf(categories[0])
                        }
                        var mode : Mode = when(category) {
                            categories[0] -> Mode.Car
                            categories[1] -> Mode.Train
                            categories[2] -> Mode.Walk
                            else -> {Mode.Car}
                        }


                        BasicText(text = "Verkehrsmittel : ")

                        ExposedDropdownMenuBox(
                            expanded = isExpanded,
                            onExpandedChange = {isExpanded = it

                            }) {

                                OutlinedTextField(
                                    value = category,
                                    onValueChange = {},
                                    readOnly = true,
                                    leadingIcon = {
                                                  modeIcon(mode)
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                                    },
                                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                    textStyle = TextStyle.Default.copy(fontSize = 13.sp),
                                    modifier = Modifier
                                        .menuAnchor()
                                        .padding(start = 16.dp)
                                        .height(50.dp)
                                )

                            ExposedDropdownMenu(expanded = isExpanded,
                                onDismissRequest = { isExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                           Text(categories[0])
                                    },
                                    onClick = {
                                        isExpanded = false
                                        category = categories[0]
                                    })
                                DropdownMenuItem(
                                    text = {
                                        Text(categories[1])
                                    },
                                    onClick = {
                                        category = categories[1]
                                        isExpanded = false
                                    })
                                DropdownMenuItem(
                                    text = {
                                        Text(categories[2])
                                    },
                                    onClick = {
                                        category = categories[2]
                                        isExpanded = false
                                    })
                            }
                        }

                    }

                    //Row containing the end time
                    Row(modifier = Modifier.padding(20.dp)) {
                        /*Column() {
                            Row(modifier = Modifier.padding(bottom = 10.dp)){
                                Text(text = "Endzeitpunkt")
                            }
                            Row() {
                                val state = rememberTimePickerState()
                                TimeInput(
                                    state = state,
                                )
                            }

                        }*/
                        BasicText(text = "Bis: ")
                        TimeField()

                    }
                }
            }

            //Third row of the card containing the end point
            Row (verticalAlignment = Alignment.CenterVertically){
                Icon(
                    modifier = Modifier
                        .size(size = 30.dp)
                        .padding(
                            start = 5.dp,
                            end = 5.dp
                        ),
                    imageVector = Icons.Default.School,
                    contentDescription = "school Icon"
                )
                BasicText(text = "Zielort")
            }
        }
    }
}

@Composable
fun TimeField() {
    NumbersField(23)
    BasicText(modifier = Modifier.padding(start = 10.dp, end = 10.dp), text = ":")
    NumbersField(59)

}

//Composable Function for displaying a textfield that can only have numbers as input
@Composable
fun NumbersField(highestNumber : Int){
    var text by remember { mutableStateOf("") }
    BasicTextField(
        modifier = Modifier
            .height(20.dp)
            .width(30.dp),
            //.wrapContentHeight(Alignment.CenterVertically),
        value = text,
        //onValueChange = {if (it.isDigitsOnly()) text = it},
        onValueChange = {if (it.isDigitsOnly()) {
            if(it.toInt() in 0..highestNumber) {
                text = it
            }
        }
                                                },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        decorationBox = {innerTextField ->
            Row(
                modifier = Modifier
                    .border(
                        BorderStroke(1.dp, Color.Black)
                    )
            ){
                innerTextField()
            }

        }
    )
}

//Composable Function for displaying an Icon for a given mode
@Composable
fun modeIcon(mode : Mode) {
    val size = 30.dp

    if(mode == Mode.Car) {
        Icon(
            modifier = Modifier
                .size(size = size)
                .padding(
                    start = 5.dp,
                    end = 5.dp
                ),
            imageVector = Icons.Default.DirectionsCar,
            contentDescription = "Car Icon"
        )
    } else if(mode == Mode.Train) {
        Icon(
            modifier = Modifier
                .size(size = size)
                .padding(
                    start = 5.dp,
                    end = 5.dp
                ),
            imageVector = Icons.Default.Train,
            contentDescription = "Train Icon"
        )
    } else if(mode == Mode.Walk) {
        Icon(
            modifier = Modifier
                .size(size = size)
                .padding(
                    start = 5.dp,
                    end = 5.dp
                ),
            imageVector = Icons.Default.DirectionsWalk,
            contentDescription = "Walk Icon"
        )
    }
}

