package com.example.euro_zhitlo.Chat

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.euro_zhitlo.Account.User
import com.example.euro_zhitlo.Apartment.MessageAdapter
import com.example.euro_zhitlo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.util.Calendar
import java.util.UUID

class ChatActivity : AppCompatActivity() {
    private val REQUEST_IMAGE = 1
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference
    lateinit var companion: User
    val mAuth = FirebaseAuth.getInstance()
    val user = mAuth.currentUser
    val userId = user?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)


        val nickname: TextView = findViewById(R.id.textView_nickname)
        val image_companion: ImageView = findViewById(R.id.imageView3)
        val image_user: ImageView = findViewById(R.id.imageView)

        val back:ImageView = findViewById(R.id.imageView_back)
        val send: Button = findViewById(R.id.button_send)
        val photo: Button = findViewById(R.id.button_photo)

        companion = intent.getParcelableExtra<User>("user")!!

        photo.setOnClickListener(){
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE)
        }
        back.setOnClickListener(){
            finish()
        }

        if (userId != null) {
            User.getUserByUid(userId) { userr ->
                if (userr != null) {
                    userr.getBitmapFromFirebaseStorage { image ->
                        image_user.setImageBitmap(image)
                    }
                }
            }
        }

        if (companion != null) {
            nickname.text = companion.nickname
            companion.getBitmapFromFirebaseStorage { image ->
                image_companion.setImageBitmap(image)
            }
        }

        send.setOnClickListener(){
            uploadData("")
        }

        setRecyclerView()

        val messagesRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("messages")

        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                setRecyclerView()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обробка помилок взаємодії з Firebase
                Log.e("ChatActivity", "Помилка взаємодії з Firebase: ${databaseError.message}")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val image:Bitmap
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            // Отримайте URI вибраної фотографії
            val selectedImageUri = data?.data
            if (selectedImageUri != null) {
                val inputStream = contentResolver.openInputStream(selectedImageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                image = bitmap
                uploadPhoto(image)
            }
        }
    }


    fun uploadData(imageUrl: String) {
        val text: EditText = findViewById(R.id.editTextText2)
        val message = Message()

        if(text.text.toString() != "") {
            message.text = text.text.toString()
            if (companion != null) {
                message.uid_receiver = companion.uid
            }
            if (user != null) {
                message.uid_sender = user.uid
            }
            message.time = Calendar.getInstance().time
            message.photo = imageUrl
            message.saveToDatabase()
            text.setText("")
        }
        else if(imageUrl != ""){
            if (companion != null) {
                message.uid_receiver = companion.uid
            }
            if (user != null) {
                message.uid_sender = user.uid
            }
            message.time = Calendar.getInstance().time
            message.photo = imageUrl
            message.saveToDatabase()
        }
    }
    fun setRecyclerView(){
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        user?.let {
            Message.readMessagesForUser(it.uid,companion.uid){messageList ->

                for(message in messageList) {
                    Log.d("Sender", message.uid_sender)
                }
                val messageAdapter = MessageAdapter(this, messageList)
                recyclerView.adapter = messageAdapter
            }
        }
    }

    fun uploadPhoto(image: Bitmap) {
        val imageRef = storageReference.child("images/${UUID.randomUUID()}.jpg")
        val byteArrayOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()

        // Завантажити зображення в Firebase Storage
        val uploadTask: UploadTask = imageRef.putBytes(data)

        uploadTask.addOnSuccessListener {
            // Завантаження успішне, отримати URL зображення
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                var imageUrl = uri.toString()
                uploadData(imageUrl)
                }
            }
        .addOnFailureListener { exception ->
            // Обробити помилку завантаження зображення
        }
    }
}

