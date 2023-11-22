package com.example.euro_zhitlo.Account

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.euro_zhitlo.Landlord.LandlordProfileActivity
import com.example.euro_zhitlo.R
import com.example.euro_zhitlo.Refugee.RefugeeProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.UUID

class EditProfileActivity : AppCompatActivity(){

    private val REQUEST_IMAGE = 1
    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var image: ImageView
    private var newImageUrl = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile_activity)


        val nickname: EditText = findViewById(R.id.editTextNickname)
        val email: EditText = findViewById(R.id.editTextEmail)
        val location: EditText = findViewById(R.id.autoCompleteTextView)
        val phone: EditText= findViewById(R.id.editTextPhone)
        image = findViewById(R.id.imageView)

        val save:Button = findViewById(R.id.buttonDelete)
        val back:ImageView = findViewById(R.id.imageView_back)
        val editPhoto:Button = findViewById(R.id.buttonEditPhoto)

        back.setOnClickListener(){
            finish()
        }

        mAuth.currentUser?.let {
            User.getUserByUid(it.uid){user->
                if (user != null) {
                    nickname.setText(user.nickname)
                    location.setText(user.location)
                    phone.setText(user.phone)
                    if(newImageUrl == "")newImageUrl = user.image
                    if(newImageUrl != "") {
                        User.updateProfileImage(mAuth, image, newImageUrl, this)
                    }

                    save.setOnClickListener(){
                        user.nickname = nickname.text.toString()
                        user.location = location.text.toString()
                        user.phone = phone.text.toString()
                        if(newImageUrl!="")
                        {
                            user.image = newImageUrl
                        }
                        user.saveToDatabase()
                        checkUserRole(mAuth)
                    }
                }
                email.setText(mAuth.currentUser!!.email)

                editPhoto.setOnClickListener(){
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "image/*"
                    startActivityForResult(intent, REQUEST_IMAGE)
                }
            }
        }
    }

    private fun checkUserRole(mAuth: FirebaseAuth) {
        val user = mAuth.currentUser
        if (user != null) {
            User.getUserByUid(user.uid) { userObj ->
                if (userObj != null) {
                    when (userObj.type) {
                        "refugee" -> {
                            val intent = Intent(this@EditProfileActivity, RefugeeProfileActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        "landlord" -> {
                            val intent = Intent(this@EditProfileActivity, LandlordProfileActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        else -> {

                        }
                    }
                }
                else
                {
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)



        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            val selectedImageUri = data?.data

            if (selectedImageUri != null) {
                val storageReference: StorageReference = FirebaseStorage.getInstance().reference
                val imageRef = storageReference.child("images/${UUID.randomUUID()}.jpg")
                val uploadTask: UploadTask = imageRef.putFile(selectedImageUri)

                uploadTask.addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        newImageUrl = uri.toString()

                        mAuth.currentUser?.let {
                            User.getUserByUid(it.uid) { user ->
                                // Видаліть попередню фотографію з Firebase Storage
                                if (user != null) {
                                    if(user.image != "") {
                                        deleteImageFromStorage(user.image)
                                    }
                                    // Оновіть поле image в класі User з новим посиланням на фотографію
                                    user.image = newImageUrl
                                    user.saveToDatabase()
                                }
                            }
                        }

                        if(newImageUrl != "") {
                            User.updateProfileImage(mAuth, image, newImageUrl, this)
                        }
                    }
                }.addOnFailureListener { exception ->
                    // Обробити помилку завантаження зображення
                }
            }
        }
    }



    // Додайте цей метод для видалення фотографії з Firebase Storage
    private fun deleteImageFromStorage(imageUrl: String) {
        val storageReference: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
        storageReference.delete().addOnSuccessListener {
            // Фотографія успішно видалена
        }.addOnFailureListener {
            // Обробити помилку видалення фотографії
        }
    }


}
