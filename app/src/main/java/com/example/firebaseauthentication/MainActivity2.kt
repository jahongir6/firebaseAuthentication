package com.example.firebaseauthentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebaseauthentication.databinding.ActivityMain2Binding
import com.example.firebaseauthentication.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        binding.tvName.text = user?.displayName
    }
}