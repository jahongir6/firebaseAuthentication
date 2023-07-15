package com.example.firebaseauthentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebaseauthentication.databinding.ActivitySmsActivitydavomiBinding
import com.google.firebase.auth.FirebaseAuth

class SmsActivitydavomi : AppCompatActivity() {
    private lateinit var binding: ActivitySmsActivitydavomiBinding
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySmsActivitydavomiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
    }
}