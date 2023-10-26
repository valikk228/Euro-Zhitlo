package com.example.euro_zhitlo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task

class RegisterActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        val buttonGoogle: Button = findViewById(R.id.buttonGoogle)
        val buttonRegister: Button = findViewById(R.id.buttonRegister)
        val textLogin: TextView = findViewById(R.id.textView9)

        val email: EditText = findViewById(R.id.editTextTextEmailAddress)
        val password: EditText = findViewById(R.id.editTextTextPassword)
        val repeat_password: EditText = findViewById(R.id.editTextTextPassword2)

        val mAuth = FirebaseAuth.getInstance()


        textLogin.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        buttonRegister.setOnClickListener {

            val emailText = email.text.toString()
            val passwordText = password.text.toString()
            val repeatPasswordText = repeat_password.text.toString()

            if (emailText.isNotEmpty() && passwordText.isNotEmpty() && repeatPasswordText.isNotEmpty()) {
                if (passwordText == repeatPasswordText) {
                    mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                        .addOnCompleteListener { task: Task<AuthResult> ->
                            if (task.isSuccessful) {
                                val user: FirebaseUser? = mAuth.currentUser
                                if (user != null) {
                                    val intent = Intent(this, RoleActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
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
                val intent = Intent(this, RoleActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: ApiException) {
                // Обробити помилку, якщо її виникло при реєстрації через Google
            }
        }
    }
}
