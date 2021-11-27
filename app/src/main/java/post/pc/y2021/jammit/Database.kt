package post.pc.y2021.jammit

import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import post.pc.y2021.jammit.utils.LocationManager
import java.util.*
import kotlin.collections.ArrayList

const val RADIUS_IN_METER = 100*1000.0 // 50 km radius TODO

class Database(val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    var thisUser:User? = null
    private val users = mutableMapOf<String, User>()
    private val  usersMutableLiveData = MutableLiveData<MutableMap<String, User>>()
    val usersLiveData:LiveData<MutableMap<String, User>> = usersMutableLiveData
    val usersNearMeLiveData:MutableLiveData<ArrayList<User>> = MutableLiveData()
    var radius = 50
    lateinit var unFilteredUsers:ArrayList<User>
        private set

    init{
        usersMutableLiveData.value = users
//        generateRandomUsers() // TODO remove


    }



    companion object{
        val possibleInstruments =  listOf("Guitar", "Piano", "Violin", "Base Guitar", "Drums")
    }


    //TODO for testing only! delete after use
    private fun generateRandomUsers() {
        val minLong = 34.63821037757344
        val maxLong = 35.53335136002925
        val minLat = 29.565161470981188
        val maxLat = 33.27696572265088
        val usersNumber = 20
        val r = Random()
        val long = List(usersNumber ){minLong + r.nextDouble() * (maxLong - minLong)}
        val lat = List(usersNumber ){minLat + r.nextDouble() * (maxLat - minLat)}
        for (i in 0 until usersNumber){
            val userId = UUID.randomUUID().toString()
            val userName = "Random User $i"
            val user = User(name= userName, geoLocation = GeoLocation(lat[i], long[i]), id = userId)
            chooseUserRandomInstruments(user)
            firestore.collection("testUsers").document(user.id).set(user)

        }
    }

    private fun chooseUserRandomInstruments(user:User) {
        val r = Random()
        val maxNumOfInstruments = possibleInstruments.size
        val numOfInstrumentsToChose = r.nextInt(maxNumOfInstruments)+1 //Choose how many instruments
        var maxIndex = maxNumOfInstruments-1
        for (j in 0 until numOfInstrumentsToChose){
            val randomIndex = r.nextInt(maxIndex+1)
            val instrument = possibleInstruments[randomIndex]
            user.instruments[instrument] = 0
            maxIndex--
        }
    }

    /**
     * Uploads a user to the firebase
     */
    fun setUserData(user: User) {
//        user.instrumentsStringMap = User.serializeInstruments(user.instruments)
        val task = firestore.collection("users").document(user.id).set(user)
        task.addOnFailureListener {
            Log.d("Yiftah", "FailedToUpload")
            Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Update one field in the database, with the field's name as string
     */
    fun updateField(userId: String, field: String, value: Any){
        val task = firestore.collection("users").document(userId).update(field, value)
        task.addOnFailureListener {}
    }

    /**
     * Finds the user with the given ID in the database, and puts it in the usersMutableLiveData
     */
    fun getUserById(id: String){
        val task = firestore.collection("users").document(id).get()
        task.addOnSuccessListener { result: DocumentSnapshot ->
            if (result.exists()){
                val user = result.toObject(User::class.java)?: return@addOnSuccessListener //TODO
//                user.instruments = User.deSerializeInstruments(user.instrumentsStringMap)
                users[id] = user
                usersMutableLiveData.value = users
            } else{
                //TODO
            }
        }
        task.addOnFailureListener {
            //TODO failed to get data from firestore
        }
    }

    /**
     * Sets location for the current user, given by Location variable.
     */
    fun setThisUserLocation(location: Location){
        val geoLocation = GeoLocation(location.latitude, location.longitude)
        if(thisUser != null){
            thisUser?.geoLocation = geoLocation
            val hashedLocation =
                GeoFireUtils.getGeoHashForLocation(geoLocation)
            updateField(thisUser!!.id, "geohash", hashedLocation)
        }
    }

    /**
     * Finds the users around me.
     * Creates a list, sorted by distance from me, and saving it to usersNearMeLiveData
     */
    fun getUsersNearMe(){
        val thisUserLocation = thisUser?.geoLocation ?: return
        val bounds = GeoFireUtils.getGeoHashQueryBounds(thisUserLocation, this.radius * 1000.0)
        val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
        for (b in bounds) {
            val q: Query = firestore.collection("testUsers") //TODO change to "users"
                .orderBy("geohash")
                .startAt(b.startHash)
                .endAt(b.endHash)
            tasks.add(q.get())

        }

        // Collect all the query results together into a single list
        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener {
                val matchingDocs: ArrayList<User> = ArrayList()
                for (task in tasks) {
                    val snap = task.result
                    for (doc in snap.documents) {
                        if (doc.getString("id") == thisUser?.id ) //Dont show this user in list
                            continue
                        val lat = doc.getDouble("lat")!!
                        val lng = doc.getDouble("lng")!!

                        // We have to filter out a few false positives due to GeoHash
                        // accuracy, but most will match
                        val docLocation = GeoLocation(lat, lng)
                        val distanceInM = GeoFireUtils.getDistanceBetween(
                            docLocation,
                            thisUserLocation
                        )
                        if (distanceInM <= RADIUS_IN_METER) {
                            val user = doc.toObject(User::class.java)?: return@addOnCompleteListener //TODO
                            user.geoLocation = GeoLocation(user.lat, user.lng)
//                            user.instruments = User.deSerializeInstruments(user.instrumentsStringMap)
                            matchingDocs.add(user)
                        }
                    }
                }

                // A comparator to compare first names of Name
                class DistanceComparator: Comparator<User>{
                    override fun compare(o1: User?, o2: User?): Int {
                        if(o1 == null || o2 == null){
                            return 0;
                        }
                        val d1 = GeoFireUtils.getDistanceBetween(o1.geoLocation, thisUser!!.geoLocation)
                        val d2 = GeoFireUtils.getDistanceBetween(o2.geoLocation, thisUser!!.geoLocation)
                        if (d1 > d2)
                            return 1;
                        return -1
                    }
                }

                // Save sorted list to liveData
                val comparator = DistanceComparator()
                val users = ArrayList(matchingDocs.sortedWith(comparator))
                unFilteredUsers = users
                this.usersNearMeLiveData.value = users
            }
    }


}