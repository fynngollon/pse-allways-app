package com.pseteamtwo.allways.uicomponents

import android.location.Address
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.core.content.ContextCompat

import com.fynng.allways.map.addressToString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.bonuspack.location.*
import java.io.IOException

import java.util.Locale



@Composable
fun LocationSelector(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onConfirm: (GeoPoint, String) -> Unit,
    startPosition: GeoPoint
) {
    var selectedPosition by rememberSaveable {mutableStateOf(GeoPoint(startPosition))}
    var addresses by rememberSaveable { mutableStateOf(listOf(Address(Locale.getDefault())))}
    var selectedAddress by rememberSaveable {mutableStateOf(addresses[0])}

    val geocoder: GeocoderNominatim = GeocoderNominatim(Locale.getDefault(), Configuration.getInstance().userAgentValue)

    LaunchedEffect(key1 = null) {
        try {
            addresses = withContext(Dispatchers.IO) {geocoder.getFromLocation(selectedPosition.latitude, selectedPosition.longitude, 1)}
            selectedAddress = addresses[0]
        } catch (exception: IOException) {
            selectedAddress = Address(Locale.getDefault())
        }

    }

    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            securePolicy = SecureFlagPolicy.Inherit,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = modifier.fillMaxSize()
        ) {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                AndroidView(
                    factory = {
                            context ->
                        org.osmdroid.views.MapView(context).apply {
                            Configuration.getInstance().userAgentValue = context.packageName;
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)

                            minZoomLevel = 4.0

                            val controller = controller

                            val marker = Marker(this)
                            marker.position = selectedPosition
                            val icon = ContextCompat.getDrawable(context, com.pseteamtwo.allways.R.drawable.location_on_fill1_wght700_grad200_opsz48)
                            icon?.setTint(0xffFF8C00.toInt())
                            marker.icon = icon

                            overlays.add(marker)

                            overlays.add(
                                MapEventsOverlay(
                                    object: MapEventsReceiver {

                                        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                                            marker.position = p
                                            if(p!=null) {
                                                controller.animateTo(p)
                                                selectedPosition = p
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    try {
                                                        addresses = withContext(Dispatchers.IO) {geocoder.getFromLocation(selectedPosition.latitude, selectedPosition.longitude, 1)}
                                                        selectedAddress = if(addresses.isNotEmpty()) addresses[0] else Address(Locale.getDefault())
                                                    } catch (exception: IOException) {
                                                        selectedAddress = Address(Locale.getDefault())
                                                    }

                                                }
                                            }
                                            return true
                                        }
                                        override fun longPressHelper(p: GeoPoint?): Boolean {
                                            // Do whatever
                                            return true
                                        }
                                    }
                                )
                            )


                            post(
                                kotlinx.coroutines.Runnable {
                                    zoomToBoundingBox(BoundingBox(selectedPosition.latitude + 0.022, selectedPosition.longitude + 0.022, selectedPosition.latitude - 0.022, selectedPosition.longitude - 0.022), true)
                                }
                            )
                        }
                    },
                    update = {
                        //view -> view.controller.animateTo(GeoPoint((52.520820* 1E6),(13.409346* 1E6)))
                    }
                )
                Card(
                    modifier = modifier.fillMaxWidth()
                ) {
                    Row {
                        Text(text = addressToString(selectedAddress))
                        Button(
                            onClick = {
                                onConfirm(selectedPosition, addressToString(selectedAddress))
                                onDismissRequest()
                            }
                        ) {
                            Text(text = "Bestätigen")
                        }
                    }
                }
            }
        }
    }
}