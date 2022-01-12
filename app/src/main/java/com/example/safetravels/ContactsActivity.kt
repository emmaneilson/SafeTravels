package com.example.safetravels

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import android.widget.Button
import android.widget.TextView
import java.util.ArrayList
import java.util.List
import android.app.Activity

class ContactsActivity : AppCompatActivity() {

    private lateinit var navView: NavigationView

    // contact array
    val contactArray = arrayListOf<String>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        supportActionBar?.hide()

        // set button to add contacts
        val subContact: Button = findViewById(R.id.subContact)
        subContact.setOnClickListener {

            // get text from input and save to array
            val textView: TextView = findViewById(R.id.addContact)
            var userText = textView.text.toString()
            contactArray.add(userText)

            // get updated array and update view
            val stringTextView: TextView =
                findViewById(R.id.textView); //Initializing integer array list;

            for (i in contactArray) {
                stringTextView.setText(i);
            }
        }
            navView = findViewById(R.id.nav_menu)

            navView.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.nav_home -> {
                        Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                        openHome()
                        true
                    }
                    R.id.nav_map -> {
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
        }


    private fun openHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun openMap() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    private fun openContacts() {
        val intent = Intent(this, ContactsActivity::class.java)
        startActivity(intent)
    }

    private fun openNotifications() {
        val intent = Intent(this, NotificationsActivity::class.java)
        startActivity(intent)
    }
}