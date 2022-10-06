package com.asad.addtocart.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.asad.addtocart.R
import com.asad.addtocart.utils.Commons
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.rengwuxian.materialedittext.MaterialEditText
import java.util.*
import kotlin.collections.HashMap

class SignupActivity : AppCompatActivity() {
    var etName: MaterialEditText? = null
    var etEmail: MaterialEditText? = null
    var etAboutInfo: MaterialEditText? = null
    var etPassword: MaterialEditText? = null
    var btnRegister: Button? = null
    var imgProfile: ImageView? = null
    var imgAddImage: ImageView? = null
    var auth: FirebaseAuth? = null
    var rootRef: DatabaseReference? = null
    var userRef: DatabaseReference? = null
    var Image_Request_Code = 7
    var imageUrl: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        setTitle("Signup")
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)

        etName = findViewById(R.id.etName)
        etAboutInfo = findViewById(R.id.etAboutInfo)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        imgProfile = findViewById(R.id.imgProfile)
        imgAddImage = findViewById(R.id.imgAddImage)
        auth = FirebaseAuth.getInstance()
        rootRef = FirebaseDatabase.getInstance().getReference()

        btnRegister?.setOnClickListener(View.OnClickListener {
            val name = etName?.text.toString().trim()
            val email = etEmail?.text.toString().trim()
            val password = etPassword?.text.toString().trim()
            val aboutInfo = etAboutInfo?.text.toString().trim()

            if (imageUrl!!.isEmpty()) {
                Commons.Toast(applicationContext, "Please Upload Image")
            } else if (name.isEmpty()) {
                Commons.Toast(applicationContext, "Please Enter Name")
            } else if (email.isEmpty()) {
                Commons.Toast(applicationContext, "Please Enter Email")
            } else if (password.isEmpty()) {
                Commons.Toast(applicationContext, "Please Enter Password")
            } else if (aboutInfo.isEmpty()) {
                Commons.Toast(applicationContext, "Please Enter About Info")
            } else {
                getToken(name, email, password, imageUrl!!, aboutInfo)
            }
        })

        imgAddImage?.setOnClickListener(View.OnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, Image_Request_Code)
        })
    }

    private fun getToken(
        name: String,
        email: String,
        password: String,
        imageUrl: String,
        aboutInfo: String
    ) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                register(name, email, password, token, imageUrl, aboutInfo)
            } else {
                Commons.Toast(applicationContext, "" + task.exception?.localizedMessage)
            }
        })
    }

    private fun register(
        name: String,
        email: String,
        password: String,
        token: String,
        imageUrl: String,
        aboutInfo: String
    ) {
        auth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val firebaseUser = auth!!.currentUser
                val userid = firebaseUser!!.uid
                val hashMap: HashMap<String, String> = HashMap()
                hashMap["id"] = userid
                hashMap["name"] = name
                hashMap["email"] = email
                hashMap["password"] = password
                hashMap["token"] = token
                hashMap["profilePic"] = imageUrl
                hashMap["aboutInfo"] = aboutInfo
                userRef = rootRef?.child("Users")?.child(userid)
                userRef?.setValue(hashMap)?.addOnCompleteListener(OnCompleteListener {
                    if (it.isSuccessful) {
                        Commons.Toast(this, "Registered Successfully")
                        onBackPressed()
                    } else {
                        Commons.Toast(applicationContext, "" + task.exception?.localizedMessage)
                    }
                })
            } else {
                Commons.Toast(applicationContext, "" + task.exception?.localizedMessage)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Image_Request_Code
            && resultCode == RESULT_OK
            && data != null
            && data.data != null
        ) {
            val file_uri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, file_uri)
            imgProfile?.setImageBitmap(bitmap)
            uploadImageToFirebase(file_uri!!)
        }
    }

    private fun uploadImageToFirebase(fileUri: Uri) {
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("profilePictures/$fileName")

        refStorage.putFile(fileUri)
            .addOnSuccessListener(
                OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                        imageUrl = it.toString()
                    }
                }).addOnFailureListener(OnFailureListener { e ->
                print(e.message)
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}