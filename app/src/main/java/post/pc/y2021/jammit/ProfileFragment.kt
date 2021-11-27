package post.pc.y2021.jammit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.btnSignOut
import post.pc.y2021.jammit.extensions.Extensions.toast
import post.pc.y2021.jammit.utils.FirebaseUtils
import post.pc.y2021.jammit.views.RegisterActivity
import post.pc.y2021.jammit.extensions.Extensions.toast


/**
 * A fragment for the profile page
 */
class ProfileFragment : Fragment() {
    val database = JammitApp.instance.database
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("yiftach", "Profile onCreate")


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        Log.d("yiftach", "Profile onCreateView ")
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)
        val btnSignOut: Button = rootView.findViewById(R.id.btnSignOut)
        btnSignOut.setOnClickListener {
            FirebaseUtils.firebaseAuth.signOut()
            startActivity(Intent(activity, RegisterActivity::class.java))
            activity?.finish()
        }
        val helloTextView:TextView = rootView.findViewById(R.id.helloTextView)
        Log.d("userlog", database.thisUser.toString())
        helloTextView.text = "Hello ${database.thisUser?.name ?: ""}"



        return rootView
    }



    companion object {
        fun newInstance() = ProfileFragment()
    }
}