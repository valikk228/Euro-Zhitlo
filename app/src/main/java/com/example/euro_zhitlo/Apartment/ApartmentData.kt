package com.example.euro_zhitlo.Apartment
import Apartment
import android.graphics.Bitmap
import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.parcel.Parcelize
import java.io.ByteArrayOutputStream
import java.util.UUID

@Keep
@Parcelize
class ApartmentData : Parcelable {
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("apartments")

    // Завантаження зображення у Firebase Storage та збереження даних в Firebase Realtime Database
    fun uploadData(uid_poster:String,imageUrl:String, title: String, price: Int, country: String,city: String, access: Boolean, description: String, facilities: List<String>) {
            // Використайте push(), щоб отримати унікальний uid
            val apartmentRef = databaseReference.push()

            // Встановіть значення об'єкта Apartment для нового uid
            val uid = apartmentRef.key.toString()
            val apartmentData = Apartment(uid,uid_poster,imageUrl, title, price, country, city, access, description, facilities)
            apartmentRef.setValue(apartmentData)
    }



    fun readApartmentsRefugeeData(completion: (List<Apartment>) -> Unit) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val apartmentsList = ArrayList<Apartment>()

                for (apartmentSnapshot in dataSnapshot.children) {
                    val apartmentData = apartmentSnapshot.getValue(Apartment::class.java)
                    if (apartmentData != null) {
                        apartmentsList.add(apartmentData)
                    }
                }
                completion(apartmentsList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обробка помилок, якщо такі виникли
            }
        })
    }

    fun readApartmentsLandlordData(uid: String,completion: (List<Apartment>) -> Unit) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val apartmentsList = ArrayList<Apartment>()

                for (apartmentSnapshot in dataSnapshot.children) {
                    val apartmentData = apartmentSnapshot.getValue(Apartment::class.java)
                    if (apartmentData != null && apartmentData.uid_poster.equals(uid)) {
                        apartmentsList.add(apartmentData)
                    }
                }
                completion(apartmentsList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обробка помилок, якщо такі виникли
            }
        })
    }

}