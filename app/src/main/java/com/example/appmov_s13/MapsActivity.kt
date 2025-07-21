package com.example.appmov_s13

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationClickListener {

    private lateinit var map: GoogleMap
    private var tipoEvento: String? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object{
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        tipoEvento = intent.getStringExtra("tipo_evento")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        agregarMarcadores()
        map.setOnMyLocationClickListener(this)
        enableLocation()
        moveCameraToUserLocation()
    }

    private fun agregarMarcadores() {
        val ubicaciones = when (tipoEvento) {
            "Restaurantes" -> listOf(
                LatLng(-12.06, -77.03) to "Museo de Arte",
                LatLng(-12.05, -77.04) to "Casa de la Literatura",
                LatLng(-12.045, -77.02) to "Teatro Municipal"
            )
            "Hamburguesería" -> listOf(
                LatLng(-12.12, -77.03) to "Parque Kennedy",
                LatLng(-12.14, -77.02) to "Costa Verde",
                LatLng(-12.10, -77.05) to "Malecón de Miraflores"
            )
            "Cafetería" -> listOf(
                LatLng(-12.08, -77.03) to "Mercado de Surquillo",
                LatLng(-12.09, -77.02) to "Central Restaurante",
                LatLng(-12.11, -77.04) to "La Mar Cebichería"
            )
            else -> listOf(
                LatLng(-12.081784049835493, -77.03593195780321) to "OTS Lima"
            )
        }

        // Agregar marcadores al mapa
        for ((coord, titulo) in ubicaciones) {
            map.addMarker(MarkerOptions().position(coord).title(titulo))
        }
    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun enableLocation(){
        if(!::map.isInitialized) return
        if(isLocationPermissionGranted()){
            map.isMyLocationEnabled = true
        }else{
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (::map.isInitialized) {
                        map.isMyLocationEnabled = true
                        moveCameraToUserLocation()
                    }
                } else {
                    Toast.makeText(this, "Para activar la localización, ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if(!::map.isInitialized) return
        if(!isLocationPermissionGranted()){
            map.isMyLocationEnabled = false
            Toast.makeText(this, "Para activar la localización, ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this, "Estás en ${p0.latitude}, ${p0.longitude}", Toast.LENGTH_SHORT).show()
    }

    private fun moveCameraToUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 16f))
                } else {
                    Toast.makeText(this, "No se pudo obtener tu ubicación", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
