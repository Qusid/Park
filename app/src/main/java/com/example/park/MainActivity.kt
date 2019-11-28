package com.example.park

import android.Manifest
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng

import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import androidx.core.content.ContextCompat

import com.example.park.ui.login.LoginActivity

import com.google.firebase.auth.FirebaseAuth
import android.location.Criteria

import android.location.LocationManager
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.Button
import android.widget.Toast.LENGTH_SHORT
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), OnMapReadyCallback  {
    private lateinit var database: DatabaseReference
    private var mMapView: MapView? = null
    val mapViewBundleKey = "MapViewBundleKey"

    private  var mapet: GoogleMap? = null


    @IgnoreExtraProperties
    data class User(
        var lat: Double = 0.0 ,
        var long: Double = 0.0
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()







        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val rootRef = FirebaseDatabase.getInstance().reference
        val uidRef = rootRef.child("users").child(uid)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                if(user==null){
                    button3.setEnabled(false)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Log.d(TAG, databaseError.message) //Don't ignore errors!
            }
        }
        uidRef.addListenerForSingleValueEvent(valueEventListener)




        //for testing only
        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
        ActivityCompat.requestPermissions(this, permissions,0)

        mMapView = findViewById(R.id.mapView)
        initGoogleMap(savedInstanceState)

        var setting = findViewById<Button>(R.id.button5)

        registerForContextMenu(setting)

    }

    fun openFun(v: View){
        openContextMenu(v)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.settings, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.set1 -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.set2 -> {
                val about = AlertDialog.Builder(this)
                about.setTitle("About us")
                about.setMessage("We are students at Lakehead University. For any queries, contact us: \n sgupta13@lakeheadu.ca \n gvira@lakeheadu.ca")
                about.setNegativeButton("OK", DialogInterface.OnClickListener{dialog, id ->

                })
                about.create()
                about.show()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun initGoogleMap(savedInstanceState: Bundle?) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        var mapViewBundle: Bundle? = null

        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(mapViewBundleKey)
        }

        mMapView?.onCreate(mapViewBundle)

        mMapView?.getMapAsync(this)


        //test push

    }










    override fun onMapReady(map: GoogleMap) {
        mapet = map
        val G19 = LatLng(48.420939, -89.257409)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(48.4205048, -89.2585114),17f))
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.

            Toast.makeText(this@MainActivity, "no permission", Toast.LENGTH_SHORT).show()
        }
        print(true)
        Freespots(mapet)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            return
        } else {
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
            return
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var mapViewBundle = outState.getBundle(mapViewBundleKey)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(mapViewBundleKey, mapViewBundle)
        }

        mMapView!!.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        super.onResume()
        mMapView!!.onResume()
    }

    override fun onStart() {
        super.onStart()
        mMapView!!.onStart()

    }

    override fun onStop() {
        super.onStop()
        mMapView!!.onStop()
    }


    override fun onPause() {
        mMapView!!.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mMapView!!.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView!!.onLowMemory()
    }


    fun set(view:View){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    fun parkout(view:View){
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val rootRef = FirebaseDatabase.getInstance().reference
        val uidRef = rootRef.child("users").child(uid)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                markpark(mapet,user!!.lat,user!!.long)
            }

            override fun onCancelled(databaseError: DatabaseError) {
               // Log.d(TAG, databaseError.message) //Don't ignore errors!
            }
        }
        uidRef.addListenerForSingleValueEvent(valueEventListener)
    }
    fun park(view:View){

            // build alert dialog
            val dialogBuilder = AlertDialog.Builder(this)

            // set message of alert dialog
            dialogBuilder.setMessage("Do you want to park here?")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("yes", DialogInterface.OnClickListener {
                        dialog, id ->
                    val locationManager =
                        getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val criteria = Criteria()
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                        val location = locationManager.getLastKnownLocation(
                            locationManager.getBestProvider(
                                criteria,
                                false
                            )
                        )
                        val lat = location.latitude
                        val lng = location.longitude
                        var currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser()


                        if(currentFirebaseUser !=null) {
                            writeNewUser(currentFirebaseUser.uid, lat, lng)
                        }
                        markpark(mapet,lat,lng)

                    }





                })
                // negative button text and action
                .setNegativeButton("No", DialogInterface.OnClickListener {
                        dialog, id -> dialog.cancel()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Park?")
            // show alert dialog
            alert.show()
        }

    private fun writeNewUser(userId: String, Lat: Double, Long: Double) {
        val user = User(Lat, Long)
        database = FirebaseDatabase.getInstance().reference
        database.child("users").child(userId).setValue(user)
    }

    fun markpark(Mapp : GoogleMap?,lat: Double,long: Double){

        var mapp : GoogleMap?  = Mapp
        if(Mapp!=null)
        Mapp.addMarker(MarkerOptions().position(LatLng(lat,long)).title("Car Parked here!!")).showInfoWindow()
        Mapp!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat,long),19f))


    }


    fun Freespots(Mapp : GoogleMap?){
        var mapp : GoogleMap?  = Mapp
        if(Mapp!=null) {
            Mapp.addMarker(MarkerOptions().position(LatLng(48.419908, -89.258655))).setIcon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_GREEN ))
            Mapp.addMarker(MarkerOptions().position(LatLng(48.420213, -89.258001))).setIcon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_GREEN ))
            Mapp.addMarker(MarkerOptions().position(LatLng(48.419366, -89.259444))).setIcon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_ROSE ))
            Mapp.addMarker(MarkerOptions().position(LatLng(48.420911, -89.257416))).setIcon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_GREEN ))
            Mapp.addMarker(MarkerOptions().position(LatLng(48.419610, -89.261033))).setIcon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_RED ))
            Mapp.addMarker(MarkerOptions().position(LatLng(48.421230, -89.258061))).setIcon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_GREEN ))
            Mapp.addMarker(MarkerOptions().position(LatLng(48.421849, -89.257047))).setIcon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_ORANGE ))
            Mapp.addMarker(MarkerOptions().position(LatLng(48.419610, -89.261033))).setIcon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_ROSE ))
            Mapp.addMarker(MarkerOptions().position(LatLng(48.423099, -89.257353))).setIcon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_GREEN ))

        }
        else
            Toast.makeText(this, "map null", Toast.LENGTH_LONG).show()
    }

}
