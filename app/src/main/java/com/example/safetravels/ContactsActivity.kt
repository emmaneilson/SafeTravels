package com.example.safetravels

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import java.util.jar.Manifest
import kotlin.collections.ArrayList
import android.content.SharedPreferences
import java.util.*

class ContactsActivity : AppCompatActivity() {

    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private var PERMISSION_ID = 1234
    public lateinit var userName : String
    public var userNum : Int = 0
    private val sharedPrefFile = "kotlinsharedpreference"
    private lateinit var listView: ListView

    class PersonObj {
        var userName : String = ""
        var userNum : String = ""
    }

    private var contactList: MutableList<PersonObj> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        supportActionBar?.hide()

        val edName: EditText = findViewById(R.id.edName)
        val edNo: EditText = findViewById(R.id.edNo)

        listView = findViewById<ListView>(R.id.contacts_list)

        val saveBtn: Button = findViewById(R.id.saveBtn)
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)

        edName.setOnClickListener {
            if(checkPermission()) {
                accessContacts()
            }else {
                requestPermission()
            }
        }

//        saveBtn.setOnClickListener{
//            if(edName.text != null && edNo.text != null){
//                val name = edName.text.toString()
//                val num = edNo.text.toString()
//                val editor:SharedPreferences.Editor =  sharedPreferences.edit()
//
//                val contact = PersonObj()
//                contact.userName = name
//                contact.userNum = num
//
//                contactList.add(contact)
//
//                Log.d("list", contactList[0].toString())
//
//                val contactListSaved = contactList.toHashSet()
//
////                editor.putStringSet("name", contactListSaved)
////                editor.putString("number", num)
////                editor.apply()
////                editor.commit()
//            }
//        }

        navView = findViewById(R.id.nav_menu)
        drawerLayout = findViewById(R.id.drawerLayout)

//        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, contactList)
//        listView.adapter = adapter

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

        val hamburgerMenu = findViewById<ImageView>(R.id.menu_icon)

        hamburgerMenu.setOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }
    }

    private fun checkPermission():Boolean{
        if(
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }

    private fun requestPermission(){

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_CONTACTS), PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Debug", "permission granted")
                accessContacts()
            }
        }
    }

    private fun accessContacts(){
        var i = Intent(Intent.ACTION_PICK)

        i.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        startActivityForResult(i, 111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val edName: EditText = findViewById(R.id.edName)
        val edNo: EditText = findViewById(R.id.edNo)

        if(requestCode == 111 && resultCode == Activity.RESULT_OK){
            var contacturi:Uri = data?.data ?: return
            var cols:Array<String> = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            var rs = contentResolver.query(contacturi, cols, null, null, null)
            if(rs?.moveToFirst()!!){
                edNo.setText(rs.getString(0))
                edName.setText(rs.getString(1))
            }
        }
    }

//    fun saveContact(name: String, num: String){
//        val contact = personObj()
//        contact.userName = name
//        contact.userNum = num
//
//        Log.d("name: ", contact.userName)
//        Log.d("number: ", contact.userNum)
//    }

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