package com.example.safetravels

import android.content.ContentProviderClient
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.location.LocationRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.safetravels.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import java.util.jar.Manifest
import android.widget.TextView
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.gms.maps.model.CircleOptions
import android.os.CountDownTimer
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import com.google.android.gms.maps.model.BitmapDescriptorFactory




class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val markerPoints = ArrayList<Any>()
    private lateinit var binding: ActivityMapsBinding
    private var userPos = LatLng(-200.0, 151.0)
    private var PERMISSION_ID = 1234
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    var counter = 0
    //lateinit var locationRequest: LocationRequest

    // auto create function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = findViewById(R.id.drawerLayout)

        // set onclick method to button (temporary, timer will start from route)
        val button = findViewById<Button>(R.id.startbutton)
        button.setOnClickListener {
            ButtClick()
        }


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        getLastLocation()

        navView = findViewById(R.id.nav_menu)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                    openHome()
                    true
                }
                R.id.nav_map-> {
                    Toast.makeText(this, "Map", Toast.LENGTH_SHORT).show()
                    openMap()
                    true
                }
                R.id.nav_contacts -> {
                    Toast.makeText(this, "Contacts", Toast.LENGTH_SHORT).show()
                    openContacts()
                    true
                }
                R.id.nav_notifications -> {
                    Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show()
                    openNotifications()
                    true
                }
                else -> {
                    false
                }
            }
        }

        val hamburgerMenu = findViewById<ImageView>(R.id.menu_icon)

        hamburgerMenu.setOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }
    }

    // get last known location
    private fun getLastLocation(){
        if(checkPermission()){
            if(isLocationEnabled()){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    var location = task.result

                    if(location == null){

                    }else{
                        userPos = LatLng(location.latitude, location.longitude)
                        onMapReady(mMap)
                    }
                }
            }else{
                Toast.makeText(this, "Please enable your Location Service!", Toast.LENGTH_LONG).show()
            }
        }else{
            requestPermission()
        }
    }

    // start & display timer
    private fun ButtClick() {

        // get timer length and start timer
        object : CountDownTimer(R.id.Length.toLong(), 1000000) {

            // update & display time
            val countTime: TextView = findViewById(R.id.Time)
            override fun onTick(millisUntilFinished: Long) {

                // update timer and update display
                counter++
                countTime.text = counter.toString()

                // ongoing checkins
                if(counter>R.id.Length/4){onQuarter()}// 1/4
                if(counter>R.id.Length/2){onHalfway()}// 1/2
                if(counter>R.id.Length*3/4){onTQuarter()}// 3/4

            }

            // ongoing checkins (todo next sprint)
            fun onQuarter() {
                countTime.text = "One quarter"
            }
            fun onHalfway() {
                countTime.text = "Halfway"
            }
            fun onTQuarter() {
                countTime.text = "Three Quarters"
            }
            override fun onFinish() {
                countTime.text = "Finished"
            }
        }.start()
    }

    // start map
    override fun onMapReady(googleMap: GoogleMap) {
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */

        mMap = googleMap


        mMap.addCircle(
            CircleOptions()
                .center(userPos)
                .radius(10.0)
                .strokeColor(Color.BLUE)
                .fillColor(Color.BLUE)
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPos, 15f))
        mMap.setOnMapClickListener { latLng ->
            if (markerPoints.size > 1) {
                markerPoints.clear()
                mMap.clear()
            }

            // Adding new item to the ArrayList
            markerPoints.add(latLng)

            // Creating MarkerOptions
            val options = MarkerOptions()

            // Setting the position of the marker
            options.position(latLng)
            if (markerPoints.size === 1) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            } else if (markerPoints.size === 2) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            }

            // Add new marker to the Google Map Android API V2
            mMap.addMarker(options)

            // Checks, whether start and end locations are captured
            if (markerPoints.size >= 2) {
                val origin = markerPoints.get(0) as LatLng
                val dest = markerPoints.get(1) as LatLng

                // Getting URL to the Google Directions API
                //val url: String = getDirectionsUrl(origin, dest)
                //val downloadTask = DownloadTask()

                // Start downloading json data from Google Directions API
                //downloadTask.execute(url)
            }
        }
    }

    // permissions
    private fun checkPermission():Boolean{
        if(
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }
    private fun requestPermission(){

        ActivityCompat.requestPermissions(
          this,
           arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_ID
        )
    }
    private fun isLocationEnabled():Boolean{
        var locationManager:LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Debug", "permission granted")
                getLastLocation()
            }
        }
    }

    // menu
    private fun openHome(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    private fun openMap(){
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
    private fun openContacts(){
        val intent = Intent(this, ContactsActivity::class.java)
        startActivity(intent)
    }
    private fun openNotifications(){
        val intent = Intent(this, NotificationsActivity::class.java)
        startActivity(intent)
    }

}