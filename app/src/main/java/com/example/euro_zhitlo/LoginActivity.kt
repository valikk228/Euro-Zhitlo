package com.example.euro_zhitlo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

class LoginActivity: AppCompatActivity() {
    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private const val RC_SIGN_IN = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        val buttonLog: Button = findViewById(R.id.button)
        val buttonGoogle: Button = findViewById(R.id.button2)
        val textCreate: TextView = findViewById(R.id.textView9)

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
                    .addOnCompleteListener { task: Task<AuthResult> ->
                        if (task.isSuccessful) {
                            // Вхід в аккаунт пройшов успішно
                            val user: FirebaseUser? = mAuth.currentUser
                            if (user != null) {
                                // Ви можете перейти на іншу активність, наприклад, MainActivity
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()  // Закрити поточну активність
                            }
                        } else {
                            // Вивести повідомлення про помилку, якщо вхід не вдався
                            val exception = task.exception
                            if (exception != null) {
                                // Отримано об'єкт помилки (exception)
                                // Обробте його, щоб дізнатися, в чому полягає проблема.
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
                // Успішно отримали дані облікового запису Google
                val account = task.getResult(ApiException::class.java)

                // Тепер ви можете перейти на іншу активність, наприклад, MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()  // Закрити поточну активність
            } catch (e: ApiException) {
                // Опрацювати помилку, якщо її виникло при реєстрації через Google
            }
        }
    }
}

