package com.example.park

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.park.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.password
import kotlinx.android.synthetic.main.activity_login.username
import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()
    }
    fun rigist(view: View) {
        auth.createUserWithEmailAndPassword(username.text.toString(), password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(
                        baseContext, "Authentication passed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    // updateUI(user)

                } else {
                    // If sign in fails, display a message to the user.
                    // Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    // updateUI(null)
                }

                // ...
            }
    }
    fun login(view:View){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}