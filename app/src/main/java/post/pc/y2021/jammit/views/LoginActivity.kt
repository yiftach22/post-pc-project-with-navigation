package post.pc.y2021.jammit.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.lifecycle.observe
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import post.pc.y2021.jammit.JammitApp
import post.pc.y2021.jammit.MainActivity
import post.pc.y2021.jammit.MainActivityTabbed
import post.pc.y2021.jammit.extensions.Extensions.toast
import post.pc.y2021.jammit.utils.FirebaseUtils.firebaseAuth
import post.pc.y2021.jammit.R

class LoginActivity : AppCompatActivity() {
    lateinit var signInEmail: String
    lateinit var signInPassword: String
    lateinit var signInInputsArray: Array<EditText>
    val database = JammitApp.instance.database
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        signInInputsArray = arrayOf(etSignInEmail, etSignInPassword)
        btnCreateAccount2.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        btnSignIn.setOnClickListener {
            signInUser()
        }
    }

    private fun notEmpty(): Boolean = signInEmail.isNotEmpty() && signInPassword.isNotEmpty()

    private fun signInUser() {
        signInEmail = etSignInEmail.text.toString().trim()
        signInPassword = etSignInPassword.text.toString().trim()

        if (notEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(signInEmail, signInPassword)
                .addOnSuccessListener { signIn ->

                    //Get user data to database
                    val userId = firebaseAuth.uid.toString()
                    toast("Loading...")
                    database.usersLiveData.observe(this,{usersMap->
                            Log.d("userLogin", "LiveData1")
                            if (usersMap.containsKey(userId)){
                                Log.d("userLogin", "LiveData2")
                                database.thisUser = usersMap[userId]
                                startActivity(Intent(this, MainActivity::class.java))
                                toast("signed in successfully")
                                finish()
                            }
                    })
                    database.getUserById(userId)
                }.addOnFailureListener { e-> // Check reason for failure
                    if (e is FirebaseAuthInvalidUserException)
                        toast("Wrong Email")
                    else if (e is FirebaseAuthInvalidCredentialsException)
                        toast("wrong password")
                    else
                        toast ("Error to connect")
                }
        } else {
            signInInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} is required"
                }
            }
        }
    }
}