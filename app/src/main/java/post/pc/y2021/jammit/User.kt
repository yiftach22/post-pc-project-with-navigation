package post.pc.y2021.jammit

import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.firestore.Exclude
import java.util.*

data class User(val id:String = "",
                val name: String = "",
                val age: Int = 0,
                var instruments:MutableMap<String, Int> = mutableMapOf(),
                @get:Exclude var geoLocation: GeoLocation = GeoLocation(31.776616339725415,
                                                                        35.19773479964469)

) {
    var geohash = GeoFireUtils.getGeoHashForLocation(geoLocation)
    var lat = geoLocation.latitude
    var lng = geoLocation.longitude
//    var instrumentsStringMap:Map<String, Int> = mapOf()

//    companion object{
//        fun serializeInstruments(instruments: MutableMap<Instrument, Int>):MutableMap<String, Int>{
//            val newMap:MutableMap<String, Int> = mutableMapOf()
//            for (instrument in instruments.keys){
//                newMap[instrument.name] = instruments[instrument] as Int
//            }
//            return newMap
//        }
//
//        fun deSerializeInstruments(instrumentsAsStringMap:Map<String, Int>):MutableMap<Instrument, Int>{
//            val newMap:MutableMap<Instrument, Int> = mutableMapOf()
//            for (instrument in instrumentsAsStringMap.keys){
//                newMap[Instrument.valueOf(instrument)] = instrumentsAsStringMap[instrument] as Int
//            }
//            return newMap
//        }
//    }
}