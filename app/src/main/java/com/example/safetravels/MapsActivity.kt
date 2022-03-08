package com.example.safetravels


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.safetravels.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.navigation.NavigationView
import com.google.maps.android.PolyUtil
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val markerPoints = ArrayList<Any>()
    private lateinit var binding: ActivityMapsBinding
    private var userPos = LatLng(-200.0, 151.0)
    private var PERMISSION_ID = 1234
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggleRouteButton : ToggleButton
    private var isRouteStarted : Boolean = false
    private var polylines: MutableList<LatLng> = ArrayList()
    private val SHORT_DELAY = 2000 // 2 seconds

    //used in timer but need to get these from settings page
    var checkin_length = 1 // temporary
    var timer          = 1// temporary

    // auto create function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        drawerLayout = findViewById(R.id.drawerLayout)
        toggleRouteButton = findViewById(R.id.routeButton)


        // button to start/stop routes
        toggleRouteButton.setOnCheckedChangeListener{ _, isChecked ->
            isRouteStarted = !isChecked
            startTimer()
        }


        // main map
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        getLastLocation()


        //menu
        navView = findViewById(R.id.nav_menu)
        val hamburgerMenu = findViewById<ImageView>(R.id.menu_icon)

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
    private fun startTimer() {
        val channelId = "My_Channel_ID2"

        val toast = Toast.makeText(getApplicationContext(), "click", Toast.LENGTH_SHORT)
        toast.show()

        var no1: Boolean = true;
        var no2: Boolean = true;
        var no3: Boolean = true;

       checkOnRoute()

        var timer = object : CountDownTimer(timer.toLong()*1000*60, 1000) {

            override fun onTick(millisUntilFinished: Long) {



                //check for notifications
                if(no1 && (millisUntilFinished<timer*60*1000/4)){onQuarter()}// 1/4 left
                if(no2 && (millisUntilFinished<timer*60*1000/2)){onHalfway()}// 1/2 left
                if(no3 && (millisUntilFinished<timer*60*1000*3/4)){onTQuarter()}// 3/4 left
            }

            fun onQuarter() {

                no1 = false;

                //send notification
                Toast.makeText(getApplicationContext(), "QUARTER", Toast.LENGTH_SHORT).show()
                var builder = NotificationCompat.Builder(getApplicationContext(), channelId)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("QUARTER")
                    .setContentText("hi there, hope your walk is going well")
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                        .bigText("dismiss the notification to let us know you're ok"))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)

                with(NotificationManagerCompat.from(getApplicationContext())) {
                    // notificationId is a unique int for each notification that you must define
                    notify(12345, builder.build())
                }

                // start notification timer (get checkin_length from settings)
                var timer = object : CountDownTimer((60*1000*checkin_length).toLong(), 1000) {

                    override fun onTick(millisUntilFinished: Long) {

                        //checkin on notifications
                        if (millisUntilFinished < checkin_length / 2 * 60 * 1000) {
                            //anotherNotification()
                        }
                        if (millisUntilFinished < checkin_length * 60 * 1000) {
                            //finalNotification()
                        }
                        if (millisUntilFinished < checkin_length * 2 * 60 * 1000) {
                            //emergencyProcedure()
                        }

                    }
                    override fun onFinish() {
                        //emergencyProcedure()
                    }
                }

            }
            fun onHalfway() {

                no2 = false;

                //send notification
                Toast.makeText(getApplicationContext(), "QUARTER", Toast.LENGTH_SHORT).show()
                var builder = NotificationCompat.Builder(getApplicationContext(), channelId)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("HALFWAY")
                    .setContentText("hi there, hope your walk is going well you're halfway out of time")
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText("dismiss the notification to let us know you're ok"))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)

                with(NotificationManagerCompat.from(getApplicationContext())) {
                    // notificationId is a unique int for each notification that you must define
                    notify(12345, builder.build())
                }

                // start notification timer (get checkin_length from settings)
                var timer = object : CountDownTimer((60*1000*checkin_length).toLong(), 1000) {


                    override fun onTick(millisUntilFinished: Long) {

                        //check notifications
                        if (millisUntilFinished < checkin_length / 2 * 60 * 1000) {
                            //anotherNotification()
                        }

                    }
                    override fun onFinish() {
                        //emergencyProcedure()
                    }
                }

            }
            fun onTQuarter() {

                no3 = false;

                //send notification
                Toast.makeText(getApplicationContext(), "QUARTER", Toast.LENGTH_SHORT).show()
                var builder = NotificationCompat.Builder(getApplicationContext(), channelId)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("THIRD QUARTER")
                    .setContentText("hi there, hope your walk is going well time's almost up")
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText("dismiss the notification to let us know you're ok"))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)

                with(NotificationManagerCompat.from(getApplicationContext())) {
                    // notificationId is a unique int for each notification that you must define
                    notify(12345, builder.build())
                }

                // start notification timer (get checkin_length from settings)
                var timer = object : CountDownTimer((60*1000*checkin_length).toLong(), 1000) {

                    override fun onTick(millisUntilFinished: Long) {

                        //check notifications
                        if (millisUntilFinished < checkin_length / 2 * 60 * 1000) {
                            //anotherNotification()
                        }

                    }
                    override fun onFinish() {
                        //emergencyProcedure()
                    }
                }

            }
            override fun onFinish() {

                //send notification
                Toast.makeText(getApplicationContext(), "QUARTER", Toast.LENGTH_SHORT).show()
                var builder = NotificationCompat.Builder(getApplicationContext(), channelId)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("FINISH")
                    .setContentText("hi there, hope your walk went well and is over now")
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText("dismiss the notification to let us know you're ok"))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)

                with(NotificationManagerCompat.from(getApplicationContext())) {
                    // notificationId is a unique int for each notification that you must define
                    notify(12345, builder.build())
                }

                // start notification timer (get checkin_length from settings)
                var timer = object : CountDownTimer((60*1000*checkin_length).toLong(), 1000) {

                    override fun onTick(millisUntilFinished: Long) {

                        //check notifications
                        if (millisUntilFinished < checkin_length / 2 * 60 * 1000) {
                            //anotherNotification()
                        }

                    }
                    override fun onFinish() {
                        //emergencyProcedure()
                    }
                }

            }
        }.start()

    }

    private fun checkOnRoute(){

        val channelId = "My_Channel_ID2"

        var timer = object : CountDownTimer(1*1000*60, 5000) {

            override fun onTick(millisUntilFinished: Long) {
                if(PolyUtil.isLocationOnPath(userPos, polylines, true, 50.0)) {
                    Log.d("HELP", "on path")
                }else {
                    Log.d("HELP", "not on path")
                    val toast = Toast.makeText(getApplicationContext(), "get on path!", Toast.LENGTH_SHORT)
                    toast.show()


                }
            }
            override fun onFinish() {


            }

        }.start()

    }
    fun showToastMessage(text: String?, duration: Int) {
        val toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        toast.show()
        val handler = Handler()
        handler.postDelayed(Runnable { toast.cancel() }, duration.toLong())
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
                .strokeColor(Color.LTGRAY)
                .fillColor(Color.BLUE)
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPos, 17f))
        mMap.setOnMapClickListener { latLng ->
            if (!isRouteStarted) {
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
                    val origin = markerPoints[0] as LatLng
                    val dest = markerPoints[1] as LatLng

                    // Getting URL to the Google Directions API
                    val url: String = getDirectionsUrl(origin, dest)
                    val downloadTask = DownloadTask()

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url)
                }
            }
        }
    }

    inner class DownloadTask :
        AsyncTask<String?, Void?, String>() {
        override fun doInBackground(vararg url: String?): String? {
            var data = ""
            try {
                data = downloadUrl(url[0].toString()).toString()
            } catch (e: Exception) {
                Log.d("Background Task", e.toString())
            }
            return data
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            val parserTask = ParserTask()
            parserTask.execute(result)
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    inner class ParserTask :
        AsyncTask<String?, Int?, List<List<HashMap<String, String>>>?>() {
        // Parsing the data in non-ui thread
        override fun doInBackground(vararg jsonData: String?): List<List<HashMap<String, String>>>? {
            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? = null
            try {
                jObject = JSONObject(jsonData[0])
                val parser = DirectionsJSONParser()
                routes = parser.parse(jObject)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return routes
        }

        override fun onPostExecute(result: List<List<HashMap<String, String>>>?) {
            val points = ArrayList<LatLng?>()
            var lineOptions= PolylineOptions()
            val markerOptions = MarkerOptions()
            for (i in result!!.indices) {
                val path = result[i]
                for (j in path.indices) {
                    val point = path[j]
                    val lat = point["lat"]!!.toDouble()
                    val lng = point["lng"]!!.toDouble()
                    val position = LatLng(lat, lng)
                    points.add(position)
                    polylines.add(position)

                }
                lineOptions.addAll(points)
                lineOptions.width(12f)
                lineOptions.color(Color.RED)
                lineOptions.geodesic(true)
            }

// Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions)
        }
    }

    /**
     * A method to download json data from url
     */
    @Throws(IOException::class)
    private fun downloadUrl(strUrl: String): String? {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connect()
            iStream = urlConnection.inputStream
            val br = BufferedReader(InputStreamReader(iStream))
            val sb = StringBuffer()
            var line: String? = ""
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            data = sb.toString()
            br.close()
        } catch (e: Exception) {
            Log.d("Exception", e.toString())
        } finally {
            iStream!!.close()
            urlConnection!!.disconnect()
        }
        return data
    }
    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {

        // Origin of route
        val strOrigin = "origin=" + origin.latitude + "," + origin.longitude

        // Destination of route
        val strDest = "destination=" + dest.latitude + "," + dest.longitude

        // Sensor enabled
        val sensor = "sensor=false"
        val mode = "mode=walking"
        // Building the parameters to the web service
        val parameters = "$strOrigin&$strDest&$sensor&$mode"

        // Output format
        val output = "json"

        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters&key=AIzaSyCo_2MssM-1NXXCJB1A09f5_XlZO1Iuybg"
    }

    // permissions
    private fun checkPermission():Boolean{
        if(
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }
    private fun requestPermission(){

        ActivityCompat.requestPermissions(
          this,
           arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_ID
        )
    }
    private fun isLocationEnabled():Boolean{
        var locationManager:LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
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