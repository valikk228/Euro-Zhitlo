package com.example.euro_zhitlo.Account

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcelable
import android.widget.ImageView
import androidx.annotation.Keep
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.parcel.Parcelize
import java.io.File
import java.io.IOException

@Keep
@Parcelize
data class User(
    val uid: String,
    var nickname: String,
    var email: String,
    val type: String,
    var image: String,
    var location: String,
    var phone: String
) : Parcelable {

    constructor() : this("", "", "","", "","","")


    // Зберегти об'єкт com.example.euro_zhitlo.Account.User в базі даних
    fun saveToDatabase() {
        val database = FirebaseDatabase.getInstance()
        val usersRef: DatabaseReference = database.getReference("users")
        val userRef: DatabaseReference = usersRef.child(uid)
        userRef.setValue(this)
    }

    companion object {

        // Прочитати об'єкт com.example.euro_zhitlo.Account.User з бази даних за uid
        fun readFromDatabase(uid: String, callback: (User?) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val usersRef: DatabaseReference = database.getReference("users")
            val userRef: DatabaseReference = usersRef.child(uid)

            userRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val dataSnapshot: DataSnapshot? = task.result
                    if (dataSnapshot != null) {
                        val user = dataSnapshot.getValue(User::class.java)
                        callback(user)
                    } else {
                        callback(null)
                    }
                } else {
                    callback(null)
                }
            }
        }

        // Отримати об'єкт com.example.euro_zhitlo.Account.User з бази даних за uid
        fun getUserByUid(uid: String, callback: (User?) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val usersRef: DatabaseReference = database.getReference("users")
            val userQuery = usersRef.orderByChild("uid").equalTo(uid)

            userQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (userSnapshot in dataSnapshot.children) {
                            val user = userSnapshot.getValue(User::class.java)
                            callback(user)
                            return
                        }
                    }
                    callback(null)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    callback(null)
                }
            })
        }

        fun updateProfileImage(mAuth: FirebaseAuth, image: ImageView, imageUrl: String,context: Context) {

            val storageRef: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 25f
            circularProgressDrawable.start()

            image.setImageDrawable(circularProgressDrawable)

            mAuth.currentUser?.let {
                User.getUserByUid(it.uid) { user ->
                    if (user != null) {
                        try {
                            val localFile = File.createTempFile("images", "jpg")
                            storageRef.getFile(localFile).addOnSuccessListener {
                                Picasso.get()
                                    .load(imageUrl)
                                    .placeholder(circularProgressDrawable)
                                    .into(image, object : com.squareup.picasso.Callback {
                                        override fun onSuccess() {
                                            // Викликається, коли завантаження успішне
                                            circularProgressDrawable.stop()
                                            circularProgressDrawable.alpha = 0
                                        }

                                        override fun onError(e: Exception?) {
                                            // Викликається в разі помилки завантаження
                                            circularProgressDrawable.stop()
                                            circularProgressDrawable.alpha = 0
                                        }
                                    })                        }.addOnFailureListener { exception ->
                                // Обробка помилки, якщо виникла
                            }
                        } catch (e: IOException) {
                            // Обробка помилки створення тимчасового файлу
                        }
                    }
                }
            }
        }


    }
    fun getBitmapFromFirebaseStorage(callback: (Bitmap?) -> Unit) {
        val storageRef: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(image)

        try {
            val localFile = File.createTempFile("images", "jpg")
            storageRef.getFile(localFile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                callback(bitmap)
            }.addOnFailureListener { exception ->
                // Обробка помилки, якщо виникла
                callback(null)
            }
        } catch (e: IOException) {
            // Обробка помилки створення тимчасового файлу
            callback(null)
        }
    }

}
