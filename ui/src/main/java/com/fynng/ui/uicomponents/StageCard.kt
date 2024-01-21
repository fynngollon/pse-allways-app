
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Card(

    ) {
        Column(

        ) {


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
            Row {
                Column(
                ) {
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
                Column(
                    modifier = Modifier
                        .onSizeChanged {
                            lineEnd = it
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(20.dp)

                    ) {
                        BasicText(text = "Von")
                        OutlinedTextField(
                            modifier = Modifier
                                .height(10.dp)
                                .width(40.dp)
                                .padding(start = 10.dp),
                            value = "",
                            onValueChange = {},
                            trailingIcon = {

                            })
                        BasicText(modifier = Modifier.padding(start = 10.dp), text = ":")

                        OutlinedTextField(
                            modifier = Modifier
                                .height(10.dp)
                                .width(40.dp)
                                .padding(start = 10.dp),
                            value = "",
                            onValueChange = {},
                            trailingIcon = {
                            })

                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(20.dp)

                    ) {
                        var isExpanded by remember {
                            mutableStateOf(false)
                        }
                        val categories = arrayOf("Auto", "Zug", "zu Fuß")
                        var mode by remember {
                            mutableStateOf(categories[0])
                        }

                        if(mode == categories[0]) {
                            modeIcon(Mode.Car)
                        } else if(mode == categories[1]) {
                            modeIcon(Mode.Train)
                        } else if(mode == categories[2]) {
                            modeIcon(Mode.Walk)
                        }


                        BasicText(text = "Verkehrsmittel")

                        ExposedDropdownMenuBox(
                            expanded = isExpanded,
                            onExpandedChange = {isExpanded = it

                            }) {

                                OutlinedTextField(
                                    value = mode,
                                    onValueChange = {},
                                    readOnly = true,
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
                                        mode = categories[0]
                                    })
                                DropdownMenuItem(
                                    text = {
                                        Text(categories[1])
                                    },
                                    onClick = {
                                        mode = categories[1]
                                        isExpanded = false
                                    })
                                DropdownMenuItem(
                                    text = {
                                        Text(categories[2])
                                    },
                                    onClick = {
                                        mode = categories[2]
                                        isExpanded = false
                                    })
                            }
                        }

                    }
                    Row(modifier = Modifier.padding(20.dp)) {
                        BasicText(text = "Bis")

                        OutlinedTextField(
                            modifier = Modifier
                                .height(10.dp)
                                .width(40.dp)
                                .padding(start = 10.dp),
                            value = "",
                            onValueChange = {},
                            trailingIcon = {

                            })
                        BasicText(modifier = Modifier.padding(start = 10.dp), text = ":")

                        OutlinedTextField(
                            modifier = Modifier
                                .height(10.dp)
                                .width(40.dp)
                                .padding(start = 10.dp),
                            value = "",
                            onValueChange = {},
                            trailingIcon = {

                            })
                    }
                }
            }

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
fun modeIcon(mode : Mode) {
    var imageVector : Icons
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

