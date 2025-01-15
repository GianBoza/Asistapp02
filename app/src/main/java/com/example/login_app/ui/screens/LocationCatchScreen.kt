package com.example.login_app.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.login_app.RetrofitInstance
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@Composable
fun LocationCatchScreen(context: Context) {
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    val hasLocationPermission = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val plazaDeArmasPolygon = listOf(
        LatLng(-16.295668, -71.626483),
        LatLng(-16.295725, -71.626537),
        LatLng(-16.295812, -71.626420),
        LatLng(-16.295758, -71.626369),
    )

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val locationRequest = remember {
        LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L // 5 segundos
        ).setMinUpdateIntervalMillis(3000L) // 3 segundos
            .build()
    }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    userLocation = LatLng(it.latitude, it.longitude)
                }
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission.value = isGranted
        if (isGranted) {
            checkAndRequestLocationUpdates(context, fusedLocationClient, locationRequest, locationCallback)
        }
    }

    LaunchedEffect(hasLocationPermission.value) {
        if (!hasLocationPermission.value) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            checkAndRequestLocationUpdates(context, fusedLocationClient, locationRequest, locationCallback)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    val ArequipaLocation = LatLng(-16.4040102, -71.559611)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            userLocation ?: ArequipaLocation,
            18f
        )
    }

    LaunchedEffect(userLocation) {
        userLocation?.let { location ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    location,
                    18f
                )
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = remember {
                MapProperties(
                    isMyLocationEnabled = hasLocationPermission.value,
                    mapType = MapType.NORMAL
                )
            }
        ) {
            Polygon(
                points = plazaDeArmasPolygon,
                strokeColor = Color.Red,
                fillColor = Color.Blue.copy(alpha = 0.3f),
                strokeWidth = 5f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val coroutineScope = rememberCoroutineScope()
            Button(
                onClick = {
                    userLocation?.let { location ->
                        val buffer = 0.00001
                        val isInside = PolyUtil.containsLocation(
                            location.latitude,
                            location.longitude,
                            plazaDeArmasPolygon,
                            true
                        ) || plazaDeArmasPolygon.any { point ->
                            val distance = computeDistanceBetween(location, point)
                            distance < buffer
                        }

                        if (isInside) {
                            // Llama a la función de suspensión desde una coroutine
                            coroutineScope.launch {
                                registerAttendance(context)
                            }
                            dialogMessage = "Asistencia Registrada"
                        } else {
                            dialogMessage = "Asegúrate de estar dentro del área de trabajo"
                        }
                        showDialog = true
                    }
                }
            ) {
                Text("Registrar Asistencia")
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Estado de Asistencia") },
                text = { Text(dialogMessage) },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

private fun checkAndRequestLocationUpdates(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    locationRequest: LocationRequest,
    locationCallback: LocationCallback
) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    if (isGpsEnabled) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    } else {
        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }
}

// Función para calcular la distancia entre dos puntos
private fun computeDistanceBetween(point1: LatLng, point2: LatLng): Double {
    val R = 6371000
    val lat1 = Math.toRadians(point1.latitude)
    val lat2 = Math.toRadians(point2.latitude)
    val dLat = Math.toRadians(point2.latitude - point1.latitude)
    val dLng = Math.toRadians(point2.longitude - point1.longitude)

    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(lat1) * Math.cos(lat2) *
            Math.sin(dLng / 2) * Math.sin(dLng / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

    return R * c
}

// Cambia esta función a una función de suspensión
private suspend fun registerAttendance(context: Context) {
    val apiService = RetrofitInstance.api
    val request = createAttendanceRequest(context)

    if (request != null) {
        try {
            val response = apiService.attendance(request)
            if (response != null) {
                // Asistencia registrada exitosamente
            } else {
                // Manejar error en el registro de asistencia
            }
        } catch (e: Exception) {
            // Manejar fallo en la solicitud
        }
    } else {
        // Manejar el caso en que no se pueda obtener el ID del usuario
    }
}

fun createAttendanceRequest(context: Context): AttendanceRequest? {
    val userId = getUserId(context)
    return if (userId != null && userId != -1) {
        AttendanceRequest(user = userId)
    } else {
        null
    }
}

fun getUserId(context: Context): Int? {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    return if (sharedPreferences.contains("user_id")) {
        sharedPreferences.getInt("user_id", -1)
    } else {
        null
    }
}