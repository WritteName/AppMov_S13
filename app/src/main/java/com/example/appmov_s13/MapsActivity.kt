package com.example.appmov_s13

import android.Manifest
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
                LatLng(-9.12357092612358, -78.5083530841081) to "Restaurante Bambú",
                LatLng(-9.121876025828298, -78.50650772425708) to "Restaurante De Paul",
                LatLng(-9.119587897659894, -78.50886806825255) to "Majos Restaurant",
                LatLng(-9.13331856188405, -78.51436310559593) to "Restaurante Cocoliso",
                LatLng(-9.123135284568372, -78.51720538103082) to "La Cazuela Restaurante",
                LatLng(-9.128650068180646, -78.51013126574524) to "En Cubiertos",
                LatLng(-9.119247901343643, -78.50925805357726) to "LA AVENIDA"
            )
            "Hamburguesería" -> listOf(
                LatLng(-9.116185953455444, -78.51079194646601) to "Kty burguers",
                LatLng(-9.121509794114042, -78.50653999958968) to "La Avenida Burguer Drinks",
                LatLng(-9.122992833834893, -78.50799912133233) to "Artesana Burguers",
                LatLng(-9.12655210401252, -78.51091736481766) to "Burguer Flash",
                LatLng(-9.12816223837718, -78.5240065451562) to "Rockola Burguer"
            )
            "Cafetería" -> listOf(
                LatLng(-9.123179900608312, -78.52201739618633) to "Dulce Traviary",
                LatLng(-9.122873892587664, -78.52460013435615) to "The secret Garden-Brunch Café",
                LatLng(-9.1212214447579, -78.52537495573067) to "LOCAFE",
                LatLng(-9.12646750480524, -78.52643642689407) to "Ola Café",
                LatLng(-9.116798887223352, -78.51557613045955) to "Nuez Melliza"
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
