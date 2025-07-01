package com.example.thecodecup.activity

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.thecodecup.R
import com.example.thecodecup.data.AppDatabase
import com.example.thecodecup.data.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    private lateinit var fullNameEdit: EditText
    private lateinit var phoneEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var addressEdit: EditText

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        fullNameEdit = findViewById(R.id.fullNameEdit)
        phoneEdit = findViewById(R.id.phoneEdit)
        emailEdit = findViewById(R.id.emailEdit)
        addressEdit = findViewById(R.id.addressEdit)

        val editName = findViewById<View>(R.id.editName)
        val editPhone = findViewById<View>(R.id.editPhone)
        val editEmail = findViewById<View>(R.id.editEmail)
        val editAddress = findViewById<View>(R.id.editAddress)

        val backButton = findViewById<ImageView>(R.id.back)
        backButton.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        db = AppDatabase.getInstance(applicationContext)

        loadProfile()

        fun enableEditing(editText: EditText) {
            editText.isEnabled = true
            editText.isFocusableInTouchMode = true
            editText.requestFocus()
        }

        editName.setOnClickListener { enableEditing(fullNameEdit) }
        editPhone.setOnClickListener { enableEditing(phoneEdit) }
        editEmail.setOnClickListener { enableEditing(emailEdit) }
        editAddress.setOnClickListener { enableEditing(addressEdit) }
    }

    private fun loadProfile() {
        lifecycleScope.launch {
            val profile = withContext(Dispatchers.IO) {
                db.userProfileDao().getProfile() ?: UserProfile()
            }
            fullNameEdit.setText(profile.fullName)
            phoneEdit.setText(profile.phoneNumber)
            emailEdit.setText(profile.email)
            addressEdit.setText(profile.address)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            saveProfile()
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun saveProfile() {
        val fullName = fullNameEdit.text.toString()
        val phone = phoneEdit.text.toString()
        val email = emailEdit.text.toString()
        val address = addressEdit.text.toString()

        val profile = UserProfile(
            id = 1,
            fullName = fullName,
            phoneNumber = phone,
            email = email,
            address = address
        )

        lifecycleScope.launch(Dispatchers.IO) {
            db.userProfileDao().upsertProfile(profile)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}