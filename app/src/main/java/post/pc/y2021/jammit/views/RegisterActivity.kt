package post.pc.y2021.jammit.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*
import post.pc.y2021.jammit.*
import post.pc.y2021.jammit.extensions.Extensions.toast
import post.pc.y2021.jammit.utils.FirebaseUtils.firebaseAuth
import post.pc.y2021.jammit.utils.FirebaseUtils.firebaseUser

class RegisterActivity : AppCompatActivity() {
    lateinit var userEmail: String
    lateinit var userPassword: String
    val database = JammitApp.instance.database
    val firestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkIfLoggedIn()
        setContentView(R.layout.activity_register)

        btnCreateAccount.setOnClickListener {
            signIn()
        }
        btnSignIn2.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            toast("please sign into your account")
            finish()
        }
    }

    /* check if there's a signed-in user*/
    private fun checkIfLoggedIn(){
        firebaseAuth.currentUser?.let {
            //TODO get the user data from firestore
            val userId = firebaseAuth.uid.toString()
            val task = firestore.collection("users").document(userId).get()
            task.addOnSuccessListener {
                val result = task.result
                if (result != null && result.exists()) {
                    database.thisUser = result.toObject(User::class.java)
                    startActivity(Intent(this, MainActivity::class.java))
                    toast("welcome back")
                }
            }
            task.addOnFailureListener {
                toast("Failed to load data")
            }

        }

    }

    private fun notEmpty(): Boolean = etEmail.text.toString().trim().isNotEmpty() &&
            etPassword.text.toString().trim().isNotEmpty() &&
            etConfirmPassword.text.toString().trim().isNotEmpty()

    private fun identicalPassword(): Boolean {
        var identical = false
        if (notEmpty() &&
            etPassword.text.toString().trim() == etConfirmPassword.text.toString().trim()
        ) {
            identical = true
        } else if (!notEmpty()) {
            for (input in arrayListOf<EditText>(etEmail, etPassword, etConfirmPassword))
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} is required"
                }
            }
         else {
            toast("passwords are not matching !")
        }
        return identical
    }

    private fun signIn() {
        if (identicalPassword()) {
            // identicalPassword() returns true only  when inputs are not empty and passwords are identical
            userEmail = etEmail.text.toString().trim()
            userPassword = etPassword.text.toString().trim()

            /*create a user*/
            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        toast("created account successfully !")
                        val userId = firebaseAuth.uid.toString()
                        val user = User(userId, etName.text.toString(), etAge.text.toString().toInt())
                        database.thisUser = user
                        database.setUserData(user)
                        sendEmailVerification()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        toast("failed to Authenticate !")
                    }
                }
        }
    }

    /* send verification email to the new user. This will only
    *  work if the firebase user is not null.
    */

    private fun sendEmailVerification() {
        firebaseUser?.let {
            it.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    toast("email sent to $userEmail")
                }
            }
        }
    }
}