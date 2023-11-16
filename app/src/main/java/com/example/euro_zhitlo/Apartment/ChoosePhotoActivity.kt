package com.example.euro_zhitlo.Apartment


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.euro_zhitlo.Apartment.AddApartmentActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.example.euro_zhitlo.R
import java.io.ByteArrayOutputStream
import java.util.UUID

class ChoosePhotoActivity : AppCompatActivity() {

    private val REQUEST_IMAGE = 1
    private lateinit var image: Bitmap
    private lateinit var choosePhotoButton: Button
    private lateinit var photo: ImageView
    private var imageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_photo_activity)

        val back: ImageView = findViewById(R.id.imageView_back)
        back.setOnClickListener() {
            finish()
        }
        photo = findViewById(R.id.imageViewPhoto)
        choosePhotoButton = findViewById(R.id.choosePhotoButton)

        choosePhotoButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, REQUEST_IMAGE)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            val selectedImageUri = data?.data

            if (selectedImageUri != null) {
                val circularProgressDrawable = CircularProgressDrawable(this)
                circularProgressDrawable.strokeWidth = 5f
                circularProgressDrawable.centerRadius = 25f
                circularProgressDrawable.start()

                // Використовуємо Glide для завантаження фотографії і встановлення кругового прогресу
                Glide.with(this)
                    .load(selectedImageUri)
                    .placeholder(circularProgressDrawable)
                    .into(photo)

                val inputStream = contentResolver.openInputStream(selectedImageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                image = bitmap

                val byteArrayOutputStream = ByteArrayOutputStream()
                image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val data = byteArrayOutputStream.toByteArray()

                val storageReference: StorageReference = FirebaseStorage.getInstance().reference
                val imageRef = storageReference.child("images/${UUID.randomUUID()}.jpg")
                val uploadTask: UploadTask = imageRef.putBytes(data)

                uploadTask.addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        imageUrl = uri.toString()
                        // Запускаємо іншу активність і передаємо URL фотографії
                        startNextActivity(imageUrl)
                    }
                }.addOnFailureListener { exception ->
                    // Обробити помилку завантаження зображення
                }
            }
        }
    }

    private fun startNextActivity(imageUrl: String) {
        val intent = Intent(this@ChoosePhotoActivity, AddApartmentActivity::class.java)
        intent.putExtra("imageUrl", imageUrl)
        startActivity(intent)
    }
}
