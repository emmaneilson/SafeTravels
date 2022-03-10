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
import kotlin.collections.ArrayList
import android.content.SharedPreferences
import android.telephony.SmsManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ContactsActivity : AppCompatActivity() {

    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private var PERMISSION_ID = 1234
    public lateinit var userName : String
    public var userNum : Int = 0
    private val sharedPrefFile = "kotlinsharedpreference"
    private lateinit var listViewName: ListView
    private lateinit var listViewNum: ListView
    class PersonObj {
        var userName : String = ""
        var userNum : String = ""
    }
    var maps = MapsActivity()

    private var contactList: MutableList<PersonObj> = ArrayList()
    private var nameList: MutableList<String> = ArrayList()
    private var numberList: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        supportActionBar?.hide()

        val edName: EditText = findViewById(R.id.edName)
        val edNo: EditText = findViewById(R.id.edNo)

        if(checkPref("names")){
            nameList = getArrayList("names")
        }
        if(checkPref("numbers")){
            numberList = getArrayList("numbers")
        }

        listViewName = findViewById<ListView>(R.id.names_list)
        listViewNum = findViewById<ListView>(R.id.number_list)

        val adapterName = ArrayAdapter(this, android.R.layout.simple_list_item_1, nameList)
        listViewName.adapter = adapterName

        val adapterNum = ArrayAdapter(this, android.R.layout.simple_list_item_1, numberList)
        listViewNum.adapter = adapterNum

        val saveBtn: Button = findViewById(R.id.saveBtn)
        val alertBtn: Button = findViewById(R.id.alert_btn)
        val deleteBtn: Button = findViewById(R.id.deleteBtn)

        edName.setOnClickListener {
            if(checkPermission()) {
                accessContacts()
            }else {
                requestPermission()
            }
        }

        alertBtn.setOnClickListener{
            sendAlert()
        }

        saveBtn.setOnClickListener{
            if(edName.text != null && edNo.text != null){
                val name = edName.text.toString()
                val num = edNo.text.toString()
                val contact = PersonObj()

                contact.userName = name
                contact.userNum = num

                if(checkPref("names")){
                    nameList = getArrayList("names")
                }
                if(checkPref("numbers")){
                    numberList = getArrayList("numbers")
                }

                nameList.add(name)
                numberList.add(num)

                saveArrayList(nameList, "names")
                saveArrayList(numberList, "numbers")

                val adapterName = ArrayAdapter(this, android.R.layout.simple_list_item_1, nameList)
                listViewName.adapter = adapterName

                val adapterNum = ArrayAdapter(this, android.R.layout.simple_list_item_1, numberList)
                listViewNum.adapter = adapterNum

                edName.setText("")
                edNo.setText("")
            }
        }

        deleteBtn.setOnClickListener{
            deleteContacts()
        }


        navView = findViewById(R.id.nav_menu)
        drawerLayout = findViewById(R.id.drawerLayout)


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

    private fun checkPermissionText():Boolean{
        if(
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
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

    private fun requestPermissionText(){

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.SEND_SMS), PERMISSION_ID
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

    public fun sendAlert(){
        if(checkPermissionText()){
            for(i in 0 until numberList.size){
                val location = maps.userPos
                var obj = SmsManager.getDefault()
                obj.sendTextMessage(numberList[i], null, "THIS IS AN ALERT FROM SAFETRAVELS SENT TO "+nameList[i]+". LAST LOCATION: "+location+"", null, null)
            }
            Toast.makeText(this, "Alert Sent!", Toast.LENGTH_SHORT).show()
        }else{
            requestPermissionText()

        }
    }

    private fun deleteContacts(){
        if(checkPref("names")){
            nameList = getArrayList("names")
        }
        if(checkPref("numbers")){
            numberList = getArrayList("numbers")
        }

        nameList.clear()
        numberList.clear()

        saveArrayList(nameList, "names")
        saveArrayList(numberList, "numbers")

        val adapterName = ArrayAdapter(this, android.R.layout.simple_list_item_1, nameList)
        listViewName.adapter = adapterName

        val adapterNum = ArrayAdapter(this, android.R.layout.simple_list_item_1, numberList)
        listViewNum.adapter = adapterNum

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

    fun saveArrayList(list: MutableList<String>, key: String?) {
        val prefs: SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        val gson = Gson()
        val json: String = gson.toJson(list)
        editor.putString(key, json)
        editor.apply()
    }

    fun checkPref(key: String): Boolean{
        val prefs: SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        return prefs.contains(key)
    }

    fun getArrayList(key: String?): MutableList<String> {
        val prefs: SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val gson = Gson()
        val json: String? = prefs.getString(key, null)
        val type: Type = object : TypeToken<MutableList<String?>?>() {}.getType()
        return gson.fromJson(json, type)
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