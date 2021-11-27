package post.pc.y2021.jammit

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.widget.Toolbar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import post.pc.y2021.jammit.utils.LocationManager
import kotlin.collections.ArrayList

/**
 * A fragment for the list of musicians around
 */
class ListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var usersNearMeLiveData: LiveData<ArrayList<User>>
    private lateinit var permissionTextView:TextView
    private lateinit var permissionButton: Button
    private lateinit var toolbar: Toolbar
    private lateinit var filterButton: FloatingActionButton
    private lateinit var requestPermissionToLocationLauncher:ActivityResultLauncher<String>
    private  val locationManager:LocationManager by activityViewModels()
    private val database = JammitApp.instance.database
    private var radius = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        usersNearMeLiveData = locationManager.usersNearMeLiveData
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        val adapter = NearbyMusiciansAdapter(database.usersNearMeLiveData)
        recyclerView.adapter = adapter
        permissionButton = view.findViewById(R.id.permissionButton)
        permissionTextView = view.findViewById(R.id.permissionTextView)
        toolbar = view.findViewById(R.id.toolbar)
        filterButton = view.findViewById(R.id.filterButton)


        // initialize the permission launcher. updates liveData according to the result
        requestPermissionToLocationLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                locationManager.isPermissionGrantedLiveData.value = isGranted
            }

        // change the screen if permission is granted/denied (List of users / ask for permission)
        locationManager.isPermissionGrantedLiveData.observeForever { isPermissionGranted ->
            if (isPermissionGranted){
                hidePermissionRequestText()
            }
            else{
                showPermissionRequestText()
            }
        }

        // Ask for permission on button click
        permissionButton.setOnClickListener {
            requestPermissionToLocationLauncher.launch( Manifest.permission.ACCESS_FINE_LOCATION)
        }

        //
        database.usersNearMeLiveData.observeForever{ usersNearMe ->
            adapter.setUsers(usersNearMe)
            adapter.notifyDataSetChanged()
        }

        filterButton.setOnClickListener {
            val action = ListFragmentDirections.actionListFragment2ToFilterFragment()
            findNavController().navigate(action)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance() = ListFragment()
    }


    /**
     * These two methods toggles between two "screens" - the list, and a location request explenation
     * and button.
     */
    private fun showPermissionRequestText() {
        recyclerView.visibility = View.GONE
        toolbar.visibility = View.GONE
        filterButton.visibility = View.GONE
        permissionButton.visibility = View.VISIBLE
        permissionTextView.visibility = View.VISIBLE

    }
    private fun hidePermissionRequestText(){
        toolbar.visibility = View.VISIBLE
        filterButton.visibility = View.VISIBLE
        recyclerView.visibility = View.VISIBLE
        permissionButton.visibility = View.GONE
        permissionTextView.visibility = View.GONE
    }


}