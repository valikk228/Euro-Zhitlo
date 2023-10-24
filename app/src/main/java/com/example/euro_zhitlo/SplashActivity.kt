package com.example.euro_zhitlo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT: Long = 4000 // 2 секунды (в миллисекундах)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_activity)

        Handler().postDelayed({
            // Здесь создается интент для перехода к MainActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish() // Закрыть текущую активность, чтобы пользователь не мог вернуться на экран приветствия
        }, SPLASH_TIME_OUT)
    }
}


