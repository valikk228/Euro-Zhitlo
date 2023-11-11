package com.example.euro_zhitlo.Account

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.parcel.Parcelize
import java.io.File
import java.io.IOException

@Keep
@Parcelize
data class User(
    val uid: String,
    var nickname: String,
    val type: String,
    val image: String,
    var location: String,
    var phone: String
) : Parcelable {

    constructor() : this("", "", "", "","","")


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
