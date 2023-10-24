package com.example.euro_zhitlo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import java.security.MessageDigest
import java.security.SecureRandom

class RegisterActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        val buttonPhone: Button = findViewById(R.id.buttonPhone)
        val buttonGoogle: Button = findViewById(R.id.buttonGoogle)
        val buttonRegister: Button = findViewById(R.id.buttonRegister)

        val email: EditText = findViewById(R.id.editTextTextEmailAddress)
        val password: EditText = findViewById(R.id.editTextTextPassword)
        val repeat_password: EditText = findViewById(R.id.editTextTextPassword2)

        val mAuth = FirebaseAuth.getInstance()

        buttonRegister.setOnClickListener {

            val emailText = email.text.toString()
            val passwordText = password.text.toString()
            val repeatPasswordText = repeat_password.text.toString()

            if (emailText.isNotEmpty() && passwordText.isNotEmpty() && repeatPasswordText.isNotEmpty()) {
                if (passwordText == repeatPasswordText) {
                    // Захешувати пароль перед реєстрацією
                    val salt = generateSalt()
                    val hashedPassword = hashPassword(passwordText, salt)

                    // Реєстрація користувача за допомогою захешованого паролю
                    mAuth.createUserWithEmailAndPassword(emailText, hashedPassword)
                        .addOnCompleteListener { task: Task<AuthResult> ->
                            if (task.isSuccessful) {
                                // Реєстрація пройшла успішно
                                val user: FirebaseUser? = mAuth.currentUser
                                if (user != null) {
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            } else {
                                // Реєстрація не вдалася, обробте помилку тут
                                val exception = task.exception
                                if (exception != null) {
                                    // Отримано об'єкт помилки (exception)
                                    // Обробте його, щоб дізнатися, в чому полягає проблема.
                                }
                            }
                        }
                } else {
                    // Виведіть повідомлення про помилку, якщо паролі не співпадають.
                }
            } else {
                // Виведіть повідомлення про помилку, якщо якась з полів є порожньою.
            }
        }

        buttonPhone.setOnClickListener()
        {
            // Опрацювання натискання на кнопку "Телефон"
        }

        buttonGoogle.setOnClickListener()
        {
            // Опрацювання натискання на кнопку "Google"
        }
    }

    private fun hashPassword(password: String, salt: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt)
        val hashedPassword = md.digest(password.toByteArray())

        val hexChars = CharArray(hashedPassword.size * 2)
        for (i in hashedPassword.indices) {
            val v = hashedPassword[i].toInt() and 0xFF
            hexChars[i * 2] = "0123456789ABCDEF"[v ushr 4]
            hexChars[i * 2 + 1] = "0123456789ABCDEF"[v and 0x0F]
        }
        return String(hexChars)
    }

    private fun generateSalt(): ByteArray {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return salt
    }
}
