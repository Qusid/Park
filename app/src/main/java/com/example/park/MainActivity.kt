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
import com.google.firebase.database.FirebaseDatabase
import android.location.Criteria

import android.location.LocationManager
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), OnMapReadyCallback  {
    private lateinit var database: DatabaseReference
    private var mMapView: MapView? = null
    val mapViewBundleKey = "MapViewBundleKey"

    private  var mapet: GoogleMap? = null


    @IgnoreExtraProperties
    data class User(
        var lat: Double? ,
        var long: Double?
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()









        //for testing only
        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
        ActivityCompat.requestPermissions(this, permissions,0)

        mMapView = findViewById(R.id.mapView)
        initGoogleMap(savedInstanceState)

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


    }










    override fun onMapReady(map: GoogleMap) {
        mapet = map
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
    fun signout(view:View){
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
    fun park(view:View){

            // build alert dialog
            val dialogBuilder = AlertDialog.Builder(this)

            // set message of alert dialog
            dialogBuilder.setMessage("aRE you SURE to park ? ")
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
            alert.setTitle("AlertDialogExample")
            // show alert dialog
            alert.show()
        }

    private fun writeNewUser(userId: String, Lat: Double?, Long: Double?) {
        val user = User(Lat, Long)
        database = FirebaseDatabase.getInstance().reference
        database.child("users").child(userId).setValue(user)
    }

    fun markpark(Mapp : GoogleMap?,lat: Double,long: Double){

        var mapp : GoogleMap?  = Mapp
        if(Mapp!=null)
        Mapp.addMarker(MarkerOptions().position(LatLng(lat,long)).title("Car Parked here!!")).showInfoWindow()



    }

}
