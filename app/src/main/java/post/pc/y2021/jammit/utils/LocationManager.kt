package post.pc.y2021.jammit.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import post.pc.y2021.jammit.Database
import post.pc.y2021.jammit.JammitApp
import post.pc.y2021.jammit.User


const val INTERVAL_IN_SECONDS = 15L

class LocationManager:ViewModel(){
    private val application = JammitApp.instance
    private val  locationProvider = LocationServices.getFusedLocationProviderClient(application)
    private val database:Database = JammitApp.instance.database

    //set mutable live data
    private val usersNearMeMutableLiveData: MutableLiveData<ArrayList<User>> = MutableLiveData(
        ArrayList()
    )
    val isPermissionGrantedLiveData = MutableLiveData(false)
    // set live data
    val usersNearMeLiveData = usersNearMeMutableLiveData as LiveData<ArrayList<User>>
    private val locationRequest:LocationRequest
    private var locationCallback: LocationCallback


    init{
        isPermissionGrantedLiveData.value = isPermissionGranted()
        locationCallback = createLocationCallBack()
        locationRequest = createLocationRequest()

        isPermissionGrantedLiveData.observeForever{
            if (isPermissionGrantedLiveData.value == true){
                startTakingLocation()
                startLocationUpdates()
                database.getUsersNearMe()
                Toast.makeText(application, "Loading...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Create the callBack for periodic location updates. Gets the last location and updates the
     * database
     */
    private fun createLocationCallBack():LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                val lastLocation = locationResult.lastLocation
                database.setThisUserLocation(lastLocation)
            }
        }
    }

    /**
     * Creates a location request - sets interval to INTERVAL_IN_SECONDS (currently at 15s)
     * As default uses low power / low accuracy.
     */
    private fun createLocationRequest():LocationRequest{
        return LocationRequest.create().apply {
            interval = INTERVAL_IN_SECONDS*1000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates(){
        val request = createLocationRequest()
        if (isPermissionGranted()){
            locationProvider.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }



    @SuppressLint("MissingPermission")
    private fun startTakingLocation(){
        if (isPermissionGranted()) {

            locationProvider.lastLocation.addOnSuccessListener { location: Location ->
                database.setThisUserLocation(location)
                val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(createLocationRequest())
                val client: SettingsClient = LocationServices.getSettingsClient(application)
                val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
                task.addOnSuccessListener { locationSettingsResponse ->
                    // All location settings are satisfied. The client can initialize
                    // location requests here.
                    // ...
                }

                task.addOnFailureListener { exception -> // TODO CRITICAL!! when location is off
                    if (exception is ResolvableApiException){
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            Log.d("yiftach", "Failed locationSettingsResponse")
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
    //                        exception.startResolutionForResult(,
    //                            REQUEST_CHECK_SETTINGS)
                        } catch (sendEx: IntentSender.SendIntentException) {
                            Log.d("yiftach", "Failed locationSettingsResponse2")
                            // Ignore the error.
                        }
                    }
                }

            }
        } else
            isPermissionGrantedLiveData.value = false
        }


    private fun isPermissionGranted():Boolean{
        return (ActivityCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }




    }