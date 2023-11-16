package com.example.euro_zhitlo.Account

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.euro_zhitlo.Landlord.LandlordMainActivity
import com.example.euro_zhitlo.R
import com.example.euro_zhitlo.Refugee.RefugeeMainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var userRef: DatabaseReference

    companion object {
        private const val RC_SIGN_IN = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        val buttonLog: Button = findViewById(R.id.button)
        val buttonGoogle: Button = findViewById(R.id.button2)
        val textCreate: TextView = findViewById(R.id.textView9)
        userRef = FirebaseDatabase.getInstance().getReference("typeUser")

        textCreate.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        buttonLog.setOnClickListener {
            val email: EditText = findViewById(R.id.editTextTextEmailAddress)
            val password: EditText = findViewById(R.id.editTextTextPassword)

            val emailText = email.text.toString()
            val passwordText = password.text.toString()

            // Перевірка, чи введені дані коректні (непорожні)
            if (emailText.isNotEmpty() && passwordText.isNotEmpty()) {
                mAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Вхід в аккаунт пройшов успішно
                            checkUserRole()
                            saveAuthenticationStatus(true)
                        } else {
                            // Вивести повідомлення про помилку, якщо вхід не вдався
                            val exception = task.exception
                            if (exception != null) {
                                // Обробити об'єкт помилки (exception)
                            }
                        }
                    }
            } else {
                // Вивести повідомлення про помилку, якщо якась з полів є порожньою.
            }
        }

        buttonGoogle.setOnClickListener {
            signInWithGoogle()
        }

        // Конфігурація для входу через Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                // Отримуємо доступ до Firebase Auth
                val firebaseAuth = FirebaseAuth.getInstance()

                // Входимо в Firebase через Google
                val credentials = GoogleAuthProvider.getCredential(account?.idToken, null)
                firebaseAuth.signInWithCredential(credentials)
                    .addOnCompleteListener { googleSignInTask ->
                        if (googleSignInTask.isSuccessful) {
                            // Вхід відбувся успішно, перевірка ролі та перехід
                            checkUserRole()
                            saveAuthenticationStatus(true)
                        } else {
                            // Обробити помилку входу в Firebase через Google
                        }
                    }
            } catch (e: ApiException) {
                // Обробити помилку, якщо її виникло при реєстрації через Google
            }
        }
    }

    private fun checkUserRole() {
        val user = mAuth.currentUser
        if (user != null) {
            User.getUserByUid(user.uid) { userObj ->
                if (userObj != null) {
                    when (userObj.type) {
                        "refugee" -> {
                            val intent = Intent(this@LoginActivity, RefugeeMainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        "landlord" -> {
                            val intent = Intent(this@LoginActivity, LandlordMainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        else -> {
                            // Якщо роль невідома, перехід на сторінку для вибору ролі
                            val intent = Intent(this@LoginActivity, RoleActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun saveAuthenticationStatus(isAuthenticated: Boolean) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = preferences.edit()
        editor.putBoolean("isAuthenticated", isAuthenticated)
        editor.apply()
    }
}
