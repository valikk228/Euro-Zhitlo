import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcelable
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.parcel.Parcelize
import java.io.File
import java.io.IOException
import androidx.annotation.Keep

@Keep
@Parcelize
data class Apartment(
    val uid:String,
    val image: String,
    val title: String,
    val price: Int,
    val location: String,
    val access: Boolean,
    val description: String,
    val facilities: List<String>
) : Parcelable {

    constructor() : this("","", "", 0, "", false, "", emptyList())

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


