package com.example.firebaseauthentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.firebaseauthentication.databinding.ActivitySmsBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.R
import java.util.concurrent.TimeUnit

private const val TAG = "SmsActivity"

class SmsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySmsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var OTP: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken//bu tokenni saqlab turipti
    private lateinit var phoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        OTP = intent.getStringExtra("OTP").toString()
        resendToken = intent.getParcelableExtra("resendToken")!!
        phoneNumber = intent.getStringExtra("phoneNumber").toString()

        OTP = intent.getStringExtra("OTP").toString()
        resendToken = intent.getParcelableExtra("resendToken")!!
        phoneNumber = intent.getStringExtra("phoneNumber").toString()

        auth = FirebaseAuth.getInstance()
        addTextChangeListener()
        resendOTPtvVisibility()
        binding.btnVerify.setOnClickListener {
            /*Bu Kotlin dasturida yozilgan kod, tasdiq kodi kiritish jarayonida, foydalanuvchi
             tomonidan kiritilgan kodni tekshiradi va kerakli kerakli ishlar bajarilgandan so'ng
            foydalanuvchini ro'yxatdan o'tkazadi.*/
            binding.apply {
                // typeOTP o'zgaruvchisi, foydalanuvchi tomonidan kiritilgan tasdiq kodi qiymatini yig'ish uchun yaratiladi
                val typeOTP = (inputOTP1.text.toString()
                        + inputOTP2.text.toString()
                        + inputOTP3.text.toString()
                        + inputOTP4.text.toString()
                        + inputOTP5.text.toString()
                        + inputOTP6.text.toString())
// bu yerda kod kiritilgan bolsa va 6 xonali raqam ga teng bolsa
// PhoneAuthProvider.getCredential() yordamida credential o'zgaruvchisi yaratiladi va u
// yordamida signInWithPhoneAuthCredential() funksiyasi ishga tushiriladi.
                if (typeOTP.isNotEmpty()) {
                    if (typeOTP.length == 6) {
// shu yerda avtomatik kiritish kodi
                        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                            OTP, typeOTP
                        )
                        signInWithPhoneAuthCredential(credential)

                    } else {
                        Toast.makeText(
                            this@SmsActivity,
                            "Please enter correct OTP",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this@SmsActivity, "Please enter OTP", Toast.LENGTH_SHORT).show()
                }
            }


        }
        binding.tvResend.setOnClickListener {
            resendVerificationCode()
            resendOTPtvVisibility()
        }
    }

    //  Bu funksiya, tasdiq kodi yuborishni qayta boshlash uchun ishlatiladi.
    private fun resendVerificationCode() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken)// OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    //tasdiq kodi kiritish jarayonida yana bir marta kodni yuborish uchun
    // kerak bo'lgan paytda "Qayta yuborish" tugmasini ko'rsatmaydi.
    //handler classi yordamida 600000 sekundan keyin korsatiladi qayta yuborish tugmasi
    private fun resendOTPtvVisibility() {
        binding.inputOTP1.setText("")
        binding.inputOTP2.setText("")
        binding.inputOTP3.setText("")
        binding.inputOTP4.setText("")
        binding.inputOTP5.setText("")
        binding.inputOTP6.setText("")
        binding.tvResend.visibility = View.INVISIBLE
        binding.tvResend.isEnabled = false
        Handler(Looper.myLooper()!!).postDelayed({
            binding.tvResend.visibility = View.VISIBLE
            binding.tvResend.isEnabled = true

        }, 60000)
    }

    //editTextni har bir xonani tekshirish uchun mo'ljallangan
    //buyerda eitText matn ozgarishlarini kuzatuvchi funsiya
    private fun addTextChangeListener() {
        binding.apply {
            inputOTP1.addTextChangedListener(EditTextWatcher(inputOTP1))
            inputOTP2.addTextChangedListener(EditTextWatcher(inputOTP2))
            inputOTP3.addTextChangedListener(EditTextWatcher(inputOTP3))
            inputOTP4.addTextChangedListener(EditTextWatcher(inputOTP4))
            inputOTP5.addTextChangedListener(EditTextWatcher(inputOTP5))
            inputOTP6.addTextChangedListener(EditTextWatcher(inputOTP6))

        }
    }

    // bu kod tekshiradi uzunligi 1 ga teng bolsa keyingisiga otsin degani
    inner class EditTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            val text = p0.toString()
            binding.apply {
                when (view.id) {
                    com.example.firebaseauthentication.R.id.inputOTP1 -> if (text.length == 1) inputOTP2.requestFocus()
                    com.example.firebaseauthentication.R.id.inputOTP2 -> if (text.length == 1) inputOTP3.requestFocus() else if (text.isEmpty()) inputOTP1.requestFocus()
                    com.example.firebaseauthentication.R.id.inputOTP3 -> if (text.length == 1) inputOTP4.requestFocus() else if (text.isEmpty()) inputOTP2.requestFocus()
                    com.example.firebaseauthentication.R.id.inputOTP4 -> if (text.length == 1) inputOTP5.requestFocus() else if (text.isEmpty()) inputOTP3.requestFocus()
                    com.example.firebaseauthentication.R.id.inputOTP5 -> if (text.length == 1) inputOTP6.requestFocus() else if (text.isEmpty()) inputOTP4.requestFocus()
                    com.example.firebaseauthentication.R.id.inputOTP6 -> if (text.isEmpty()) inputOTP5.requestFocus()
                }
            }

        }

    }

    /*//bu funksiya aytadi kod turi kiritlsa muvaffaqyatli notori kiritilsa muvaffaqyatsiz
         //kod xato kiritilsa xato kiritding deydi
      */
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Toast.makeText(this, "Authenticated Successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, SmsActivitydavomi::class.java))
                //  val user = task.result?.user
            } else {
                // Sign in failed, display a message and update the UI
                Toast.makeText(this, "Wrong verification code", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "onVerificationFailed: ${task.exception.toString()}")

                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    // The verification code entered was invalid
                }
                // Update UI
            }
        }
    }

    // bu nima qiladi desangiz kod jonatgan paytda qanaqa id bn qanaqa token bn jonatdi shuni tepadagi ozgaruvchilarga
    //tenlab qoyadi shu yerda ishimiz tugadi kod jonatish
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            OTP = verificationId
            resendToken = token
        }
    }
}