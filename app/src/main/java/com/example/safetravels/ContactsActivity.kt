package com.example.safetravels

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.util.ArrayList
import java.util.List
import android.app.Activity




class ContactsActivity :  AppCompatActivity() {

    // contact array
    val contactArray = arrayListOf<String>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)


        // set button to add contacts
        val subContact: Button = findViewById(R.id.subContact)
        subContact.setOnClickListener {

            // get text from input and save to array
            val textView: TextView = findViewById(R.id.addContact)
            var userText = textView.text.toString()
            contactArray.add(userText)

            // get updated array and update view
            val stringTextView: TextView = findViewById (R.id.textView); //Initializing integer array list;

            for ( i in contactArray){
                stringTextView.setText(i);
            }
        }
    }
}

