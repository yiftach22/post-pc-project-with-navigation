package post.pc.y2021.jammit

import android.app.Application
import com.google.firebase.FirebaseApp

class JammitApp: Application() {
    lateinit var database: Database

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        database =  Database(this)
        instance = this

    }

    companion object {
        lateinit var instance: JammitApp
    }

}