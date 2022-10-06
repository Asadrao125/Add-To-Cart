package com.asad.addtocart.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.asad.addtocart.R
import com.asad.addtocart.utils.Commons
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.rengwuxian.materialedittext.MaterialEditText

class LoginActivity : AppCompatActivity() {
    var etEmail: MaterialEditText? = null
    var etPassword: MaterialEditText? = null
    var layoutCreateAccount: LinearLayout? = null
    var btnLogin: Button? = null
    var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setTitle("Login")

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        layoutCreateAccount = findViewById(R.id.layoutCreateAccount)
        btnLogin = findViewById(R.id.btnLogin)
        mAuth = FirebaseAuth.getInstance()

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val intent = Intent(this, ProductActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnLogin?.setOnClickListener {
            val email = etEmail?.text.toString().trim()
            val password = etPassword?.text.toString().trim()
            if (!email.isEmpty() && !password.isEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!Environment.isExternalStorageManager()) {
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.addCategory("android.intent.category.DEFAULT")
                        intent.data =
                            Uri.parse(String.format("package:%s", applicationContext.packageName))
                        startActivityForResult(intent, 2296)
                    } else {
                        checkPermission2(email, password)
                    }
                } else {
                    checkPermission(email, password)
                }
            } else {
                Toast.makeText(this, "Please enter email or password", Toast.LENGTH_SHORT).show()
            }
        }

        layoutCreateAccount?.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    fun checkPermission(email: String, password: String) {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        login(email, password)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            }).check()
    }

    fun checkPermission2(email: String, password: String) {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.RECORD_AUDIO
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        login(email, password)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            }).check()
    }

    private fun login(email: String, password: String) {
        mAuth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener(OnCompleteListener {
                if (it.isSuccessful) {
                    val intent = Intent(this, ProductActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Commons.Toast(this, it.exception?.localizedMessage!!)
                }
            })
    }

}