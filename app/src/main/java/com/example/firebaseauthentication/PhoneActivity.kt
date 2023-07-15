package com.example.firebaseauthentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.firebaseauthentication.databinding.ActivityPhoneBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

private const val TAG = "PhoneActivity"

class PhoneActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhoneBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var phoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnOhine.setOnClickListener {
            phoneNumber = binding.edtPhone.text.toString()
            if (phoneNumber.isNotEmpty()) {//bu yerda telefon nomer kiritilgan bolsa shartni keyin bajarsin deyapti
                if (phoneNumber.length == 13) {//bu yerda telefon nomerimiz +998901234567 shunda yozilgan bolsa deyapti
                    // bu obyekt malumotlarini shakillatiradi va callBackni ishlatadi
                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)

                } else {
                    Toast.makeText(this, "Please enter correct Number!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter Number", Toast.LENGTH_SHORT).show()
            }
        }


        init()

    }

    private fun init() {
        auth = FirebaseAuth.getInstance()//bu yerda authdan obyekt olyapti
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
            // telefonga sms kelsa narigi oynaga otsin deyapti
            //shunga tushsa kod jonatiladi
            val intent = Intent(this@PhoneActivity, SmsActivity::class.java)
            intent.putExtra("OTP", verificationId)
            intent.putExtra("resendToken", token)
            intent.putExtra("phoneNumber", phoneNumber)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    /*//bu funksiya aytadi kod turi kiritlsa muvaffaqyatli notori kiritilsa muvaffaqyatsiz
        //kod xato kiritilsa xato kiritding deydi
     */
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Toast.makeText(this, "Muvaffaqiyatli", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                //  val user = task.result?.user
            } else {
                // Sign in failed, display a message and update the UI
                Log.d("TAG", "onVerificationFailed: ${task.exception.toString()}")
                Toast.makeText(this, "Muvaffaqiyatsiz!!!", Toast.LENGTH_SHORT).show()
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(this, "Kod xato kiritildi", Toast.LENGTH_SHORT).show()
                    // The verification code entered was invalid
                }
                // Update UI
            }
        }
    }


}
