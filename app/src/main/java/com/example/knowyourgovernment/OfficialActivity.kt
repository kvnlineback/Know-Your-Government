package com.example.knowyourgovernment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.text.util.Linkify
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import java.util.*

class OfficialActivity : AppCompatActivity() {
    private lateinit var partyView: TextView
    private lateinit var titleView: TextView
    private lateinit var nameView: TextView
    private lateinit var scroller: ScrollView
    private lateinit var official: Official
    private lateinit var addressView: TextView
    private lateinit var address1: TextView
    private lateinit var phoneView: TextView
    private lateinit var phone1: TextView
    private lateinit var emailView: TextView
    private lateinit var email1: TextView
    private lateinit var websiteView: TextView
    private lateinit var website1: TextView
    private lateinit var partyLogo: ImageView
    private lateinit var google: ImageView
    private lateinit var twitter: ImageView
    private lateinit var facebook: ImageView
    private lateinit var youtube: ImageView
    private lateinit var photo: ImageView
    private lateinit var cm: ConnectivityManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_official)
        partyView = findViewById(R.id.partyView)
        titleView = findViewById(R.id.titleView)
        nameView = findViewById(R.id.nameView)
        scroller = findViewById(R.id.scroller)
        addressView = findViewById(R.id.addressView)
        address1 = findViewById(R.id.address1)
        phoneView = findViewById(R.id.phoneView)
        phone1 = findViewById(R.id.phone1)
        emailView = findViewById(R.id.emailView)
        email1 = findViewById(R.id.email1)
        websiteView = findViewById(R.id.websiteView)
        website1 = findViewById(R.id.website1)
        partyLogo = findViewById(R.id.partylogo)
        google = findViewById(R.id.googlePlus)
        twitter = findViewById(R.id.twitter)
        facebook = findViewById(R.id.facebook)
        youtube = findViewById(R.id.youtube)
        photo = findViewById(R.id.photoView)
        val location = findViewById<TextView>(R.id.location1)
        location.text = String.format(Locale.getDefault(), "%s, %s %s", OfficialDownloader.city, OfficialDownloader.state, OfficialDownloader.zip)
        val intent = intent
        if (intent.hasExtra("official")) {
            official = intent.getSerializableExtra("official") as Official
            Log.d(TAG, "onCreate: got an official" + official + "from mainActivity")
            partyView.text = String.format(Locale.getDefault(), "(%s)", official!!.party)
            titleView.text = official!!.title
            nameView.text = official!!.name
            if (official!!.photoURL != "") {
                if (checkConnection()) {
                    val picasso = Picasso.Builder(this).build()
                    picasso.load(official!!.photoURL).error(R.drawable.brokenimage).placeholder(R.drawable.placeholder).into(photo)
                } else photo.setImageResource(R.drawable.brokenimage)
            }
            if (official!!.party.contains("Demo")) {
                scroller.setBackgroundColor(Color.BLUE)
                partyLogo.setImageResource(R.drawable.dem_logo)
            } else if (official!!.party.contains("Repub")) {
                scroller.setBackgroundColor(Color.RED)
                partyLogo.setImageResource(R.drawable.rep_logo)
            } else {
                scroller.setBackgroundColor(Color.BLACK)
                partyLogo.visibility = View.GONE
            }
            if (official!!.address != "") {
                addressView.text = official!!.address
                Linkify.addLinks(addressView, Linkify.ALL)
                addressView.setLinkTextColor(Color.WHITE)
            } else if (official!!.address == "") {
                addressView.visibility = View.GONE
                address1.visibility = View.GONE
            }
            if (official!!.phone != "") {
                phoneView.text = official!!.phone
                Linkify.addLinks(phoneView, Linkify.ALL)
                phoneView.setLinkTextColor(Color.WHITE)
            } else if (official!!.phone == "") {
                phoneView.visibility = View.GONE
                phone1.visibility = View.GONE
            }
            if (official!!.email != "") {
                emailView.text = official!!.email
                Linkify.addLinks(emailView, Linkify.ALL)
                emailView.setLinkTextColor(Color.WHITE)
            } else if (official!!.email == "") {
                emailView.visibility = View.GONE
                email1.visibility = View.GONE
            }
            if (official!!.url != "") {
                websiteView.text = official!!.url
                Linkify.addLinks(websiteView, Linkify.ALL)
                websiteView.setLinkTextColor(Color.WHITE)
            } else if (official!!.url == "") {
                websiteView.visibility = View.GONE
                website1.visibility = View.GONE
            }
            if (!official!!.channels.containsKey("Facebook")) {
                facebook.visibility = View.GONE
            }
            if (!official!!.channels.containsKey("GooglePlus")) {
                google.visibility = View.GONE
            }
            if (!official!!.channels.containsKey("Twitter")) {
                twitter.visibility = View.GONE
            }
            if (!official!!.channels.containsKey("YouTube")) {
                youtube.visibility = View.GONE
            }
        }
    }

    fun photoClick(v: View?) {
        if (official!!.photoURL != "") {
            val intent = Intent(this, PhotoDetailActivity::class.java)
            intent.putExtra("official", official)
            startActivity(intent)
        }
    }

    fun logoClick(v: View?) {
        if (official!!.party.contains("Demo")) {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://democrats.org"))
            startActivity(i)
        } else {
            val j = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gop.com"))
            startActivity(j)
        }
    }

    fun googlePlusClicked(v: View?) {
        val name = official!!.channels["GooglePlus"]
        val intent: Intent
        try {
            intent = Intent(Intent.ACTION_VIEW)
            intent.setClassName("com.google.android.apps.plus", "com.google.android.apps.plus.phone.UrlGatewayActivity")
            intent.putExtra("customAppUri", name)
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/$name")))
        }
    }

    fun twitterClicked(v: View?) {
        var intent: Intent
        val name = official!!.channels["Twitter"]
        try {
            packageManager.getPackageInfo("com.twitter.android", 0)
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=$name"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        } catch (e: Exception) {
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/$name"))
        }
        startActivity(intent)
    }

    fun facebookClicked(v: View?) {
        val FACEBOOK_URL = "https://www.facebook.com/" + official!!.channels["Facebook"]
        val urlToUse: String
        val packageManager = packageManager
        urlToUse = try {
            val versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode
            if (versionCode >= 3002850) {
                "fb://facewebmodal/f?href=$FACEBOOK_URL"
            } else {
                "fb://page/" + official!!.channels["facebook"]
            }
        } catch (e: PackageManager.NameNotFoundException) {
            FACEBOOK_URL
        }
        val facebookIntent = Intent(Intent.ACTION_VIEW)
        facebookIntent.data = Uri.parse(urlToUse)
        startActivity(facebookIntent)
    }

    fun youTubeClicked(v: View?) {
        val name = official!!.channels["YouTube"]
        val intent: Intent
        try {
            intent = Intent(Intent.ACTION_VIEW)
            intent.setPackage("com.google.android.youtube")
            intent.data = Uri.parse("https://www.youtube.com/$name")
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/$name")))
        }
    }

    private fun checkConnection(): Boolean {
        return checkConn()
    }

    private fun checkConn(): Boolean {
        if (cm == null) {
            cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            if (cm == null) return false
        }
        val netInfo = cm!!.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }

    companion object {
        private const val TAG = "OfficialActivity"
    }
}