package com.example.safetravels

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import com.google.android.material.navigation.NavigationView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.drawerlayout.widget.DrawerLayout

class MyApplication: android.app.Application() {
    companion object {
        var timer = 1
        var dist = 1
    }
}

class NotificationsActivity : AppCompatActivity() {
    private lateinit var toggleNotification: Switch
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifcations)
        val channelId = "My_Channel_ID2"
        createNotificationChannel(channelId)

        supportActionBar?.hide()

        navView = findViewById(R.id.nav_menu)
        drawerLayout = findViewById(R.id.drawerLayout)
        val save : Button = findViewById(R.id.save)
        save.setOnClickListener {
            save()
        }
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

    private fun save(){

        val t: TextView = findViewById(R.id.timer)
        MyApplication.Companion.timer = Integer.valueOf(t.text.toString())

        val v: TextView = findViewById(R.id.dist)
        MyApplication.Companion.dist = Integer.valueOf(v.text.toString())

    }

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
    private fun createNotificationChannel(channelId:String) {
        // Create the NotificationChannel, but only on API 26+ (Android 8.0) because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My Channel"
            val channelDescription = "Channel Description"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, name, importance)
            channel.apply {
                description = channelDescription
            }

            // Finally register the channel with system
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}
