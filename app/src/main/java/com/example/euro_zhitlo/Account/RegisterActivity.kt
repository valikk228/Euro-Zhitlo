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
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var userRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        mAuth = FirebaseAuth.getInstance()
        userRef = FirebaseDatabase.getInstance().getReference("typeUser")


        val buttonGoogle: Button = findViewById(R.id.buttonGoogle)
        val buttonRegister: Button = findViewById(R.id.buttonRegister)
        val textLogin: TextView = findViewById(R.id.textView9)
        val email: EditText = findViewById(R.id.editTextTextEmailAddress)
        val password: EditText = findViewById(R.id.editTextTextPassword)
        val repeatPassword: EditText = findViewById(R.id.editTextTextPassword2)

        textLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        buttonRegister.setOnClickListener {
            val emailText = email.text.toString()
            val passwordText = password.text.toString()
            val repeatPasswordText = repeatPassword.text.toString()

            if (emailText.isNotEmpty() && passwordText.isNotEmpty() && repeatPasswordText.isNotEmpty()) {
                if (passwordText == repeatPasswordText) {
                    mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                        .addOnCompleteListener { task: Task<AuthResult> ->
                            if (task.isSuccessful) {
                                val intent = Intent(this@RegisterActivity, RoleActivity::class.java)
                                startActivity(intent)
                                saveAuthenticationStatus(true)
                                finish()
                            } else {
                                val exception = task.exception
                                if (exception != null) {
                                    // Обробити помилку
                                }
                            }
                        }
                } else {
                    // Вивести повідомлення про помилку, якщо паролі не співпадають.
                }
            } else {
                // Вивести повідомлення про помилку, якщо якась з полів є порожньою.
            }
        }

        buttonGoogle.setOnClickListener {
            val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
            val signInIntent = googleSignInClient.signInIntent

            startActivityForResult(signInIntent, 123)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 123) {
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
                            val intent = Intent(this@RegisterActivity, RefugeeMainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        "landlord" -> {
                            val intent = Intent(this@RegisterActivity, LandlordMainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        else -> {
                            // Якщо роль невідома, перехід на сторінку для вибору ролі
                            val intent = Intent(this@RegisterActivity, RoleActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
                else
                {
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

