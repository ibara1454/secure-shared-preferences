package com.github.ibara1454.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.ibara1454.secure_shared_preferences.getSecureSharedPreferences

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getSecureSharedPreferences("name", MODE_PRIVATE)
    }
}
