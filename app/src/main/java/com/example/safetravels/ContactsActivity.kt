package com.example.safetravels

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView



class ContactsActivity :  AppCompatActivity() {

    // contact array
    val contactArray = arrayListOf<String>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)




        // set button to add contacts
        val subContact: Button = findViewById(R.id.subContact)
        subContact.setOnClickListener{
            // get text from input and save
            val textView: TextView = findViewById(R.id.addContact)
            var userText = textView.text.toString()
            contactArray.add(userText)
        }
    }


    fun getContacts (){
        return // add array to contact list
        }

}

