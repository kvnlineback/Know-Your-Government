package com.example.knowyourgovernment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Criteria
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val officialList = ArrayList<Official>()
    private lateinit var recycler: RecyclerView
    private var officialAdapter: OfficialAdapter? = null
    private var cm: ConnectivityManager? = null
    private lateinit var address: TextView
    private var locationManager: LocationManager? = null
    private var criteria: Criteria? = null
    private val mainActivity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        address = findViewById(R.id.address)
        recycler = findViewById(R.id.recycler)
        officialAdapter = OfficialAdapter(officialList, this)
        recycler.adapter = officialAdapter
        recycler.layoutManager = LinearLayoutManager(this)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        criteria = Criteria()
        criteria!!.powerRequirement = Criteria.POWER_LOW
        criteria!!.accuracy = Criteria.ACCURACY_MEDIUM
        criteria!!.isAltitudeRequired = false
        criteria!!.isBearingRequired = false
        criteria!!.isSpeedRequired = false
        if (!checkConnection()) {
            connectionDialogue()
            address.text = "No Data For Location"
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_LOCATION_REQUEST_CODE) else setLocation()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    @SuppressLint("MissingPermission")
    private fun setLocation() {
        val bestProvider = locationManager!!.getBestProvider(criteria, true)
        val currentLocation = locationManager!!.getLastKnownLocation(bestProvider)
        if (currentLocation != null) {
            val geocoder = Geocoder(this, Locale.getDefault())
            val lat = currentLocation.latitude
            val lon = currentLocation.longitude
            try {
                val addresses: List<Address> = geocoder.getFromLocation(lat, lon, 1)
                OfficialDownloader(this).execute(addresses[0].postalCode)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else address!!.text = "Location Unavailable"
    }

    fun receiveOfficials(list: ArrayList<Official>) {
        officialList.clear()
        for (official in list) {
            officialList.add(official)
        }
        officialAdapter!!.notifyDataSetChanged()
        if (OfficialDownloader.city != null || OfficialDownloader.city != "") address!!.text = String.format(Locale.getDefault(), "%s, %s %s", OfficialDownloader.city, OfficialDownloader.state, OfficialDownloader.zip)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setLocation()
                return
            }
        }
        address!!.text = "No Permission"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about -> {
                val intent2 = Intent(this, AboutActivity::class.java)
                startActivity(intent2)
            }
            R.id.location -> {
                val builder = AlertDialog.Builder(this)
                val et = EditText(this)
                et.gravity = Gravity.CENTER_HORIZONTAL
                builder.setView(et)
                builder.setTitle("Enter a city, state or zip code")
                builder.setPositiveButton("OK") { dialog, which -> OfficialDownloader(mainActivity).execute(et.text.toString().trim { it <= ' ' }) }
                builder.setNegativeButton("CANCEL") { dialog, which -> }
                val dialog = builder.create()
                dialog.show()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onClick(v: View) {
        val position = recycler!!.getChildLayoutPosition(v)
        val selection = officialList[position]
        val intent = Intent(this, OfficialActivity::class.java)
        intent.putExtra("official", selection)
        startActivity(intent)
    }

    fun checkConnection(): Boolean {
        return if (checkConn()) true else false
    }

    private fun checkConn(): Boolean {
        if (cm == null) {
            cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            if (cm == null) return false
        }
        val netInfo = cm!!.activeNetworkInfo
        return if (netInfo != null && netInfo.isConnected) true else false
    }

    fun connectionDialogue() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("No Network Connection")
        builder.setMessage("Data cannot be accessed/loaded without an internet connection")
        val dialog = builder.create()
        dialog.show()
    }

    companion object {
        private const val MY_LOCATION_REQUEST_CODE = 329
    }
}