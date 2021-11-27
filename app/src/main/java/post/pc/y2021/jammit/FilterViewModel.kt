package post.pc.y2021.jammit

import androidx.lifecycle.ViewModel

class FilterViewModel:ViewModel() {
    var checkedInstruments:MutableList<String> = Database.possibleInstruments.toMutableList()
    private val database = JammitApp.instance.database

    public fun filterUsersByInstrument(instruments:List<String>){
        val filteredUsers = database.unFilteredUsers.filter { s-> doesUserHaveInstrument(instruments, s) } as ArrayList
        database.usersNearMeLiveData.value = filteredUsers
    }

    private fun doesUserHaveInstrument(instruments:List<String>, user:User):Boolean{
        for (instrument in instruments){
            if (  user.instruments.containsKey(instrument)) {
                return true
            }
        }
        return false
    }

}