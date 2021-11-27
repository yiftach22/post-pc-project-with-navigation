package post.pc.y2021.jammit

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.firebase.geofire.GeoFireUtils
import java.math.RoundingMode
import java.text.DecimalFormat

class NearbyMusiciansAdapter(usersNearMeLiveData:LiveData<ArrayList<User>>):
    RecyclerView.Adapter<NearbyMusiciansAdapter.NearbyMusiciansHolder>() {
    var nearbyMusiciansList = usersNearMeLiveData.value

    val database = JammitApp.instance.database
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearbyMusiciansHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_nearby_musicins, parent, false)
        return NearbyMusiciansHolder(itemView)
    }

    override fun onBindViewHolder(holder: NearbyMusiciansHolder, position: Int) {
        holder.fullNameView.text = nearbyMusiciansList?.get(position)?.name
        val user = nearbyMusiciansList!!.get(position)
        var distanceFromMe = (GeoFireUtils.getDistanceBetween(database.thisUser!!.geoLocation,
                                                        user.geoLocation))
        distanceFromMe = roundOffDecimal(distanceFromMe/1000)
        val distanceText = "${distanceFromMe} km from you"
        holder.distanceFromYou.text = distanceText
        var instrumentText = "Instruments: "
        for (instrument in user.instruments){
            instrumentText += "$instrument, "
        }
        holder.instrumentView.text =  instrumentText
    }
    override fun getItemCount(): Int {
        return nearbyMusiciansList?.size ?: 0
    }

    public fun setUsers(usersNearMe:ArrayList<User>){
        this.nearbyMusiciansList = usersNearMe
    }

    private fun roundOffDecimal(number: Double): Double {
        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.FLOOR
        return df.format(number).toDouble()
    }


    class NearbyMusiciansHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val fullNameView:TextView = itemView.findViewById(R.id.fullName)
        val distanceFromYou:TextView = itemView.findViewById(R.id.distanceFromYou)
        val instrumentView:TextView = itemView.findViewById(R.id.instrumentView)
    }
}