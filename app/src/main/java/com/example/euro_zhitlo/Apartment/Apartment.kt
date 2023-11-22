import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcelable
import android.widget.ImageView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.parcel.Parcelize
import java.io.File
import java.io.IOException
import androidx.annotation.Keep
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.euro_zhitlo.Account.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

@Keep
@Parcelize
data class Apartment(
    var uid:String,
    var uid_poster:String,
    var image: String,
    var title: String,
    var price: Int,
    var country: String,
    var city: String,
    var access: Boolean,
    var description: String,
    var facilities: List<String>
) : Parcelable {

    constructor() : this("","","", "", 0, "","", false, "", emptyList())

    fun updateApartmentImage(mAuth: FirebaseAuth, photo: ImageView, context: Context) {

        val storageRef: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(image)
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 25f
        circularProgressDrawable.start()

        photo.setImageDrawable(circularProgressDrawable)

        mAuth.currentUser?.let {
            User.getUserByUid(it.uid) { user ->
                if (user != null) {
                    try {
                        val localFile = File.createTempFile("images", "jpg")
                        storageRef.getFile(localFile).addOnSuccessListener {
                            Picasso.get()
                                .load(image)
                                .placeholder(circularProgressDrawable)
                                .into(photo, object : com.squareup.picasso.Callback {
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


    fun saveToDatabase() {
        val database = FirebaseDatabase.getInstance()
        val usersRef: DatabaseReference = database.getReference("apartments")
        val userRef: DatabaseReference = usersRef.child(uid)
        userRef.setValue(this)
    }

    fun getApartmentByUid(uid: String, callback: (Apartment?) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val apartmentRef: DatabaseReference = database.getReference("apartments")
        val apartmentQuery = apartmentRef.orderByChild("uid").equalTo(uid)

        apartmentQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (apartmentSnapshot in dataSnapshot.children) {
                        val apartment = apartmentSnapshot.getValue(Apartment::class.java)
                        callback(apartment)
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

    fun removeApartmentById(uid: String, callback: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val apartmentRef: DatabaseReference = database.getReference("apartments").child(uid)

        // Видалення об'єкта за його uid
        apartmentRef.removeValue()
            .addOnSuccessListener {
                // Видалення успішне
                callback(true)
            }
            .addOnFailureListener {
                // Обробка помилки видалення
                callback(false)
            }
    }

}


