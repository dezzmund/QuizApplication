package com.example.quizapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ActivityProfileBinding
import com.example.quizapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var binding: ActivityProfileBinding
    lateinit var storage: FirebaseStorage
    lateinit var firestore: FirebaseFirestore
    var isEdit: Boolean = false
    private val PICK_IMAGE_REQUEST = 12

    fun updateUseData() {
        val collectionReference = firestore.collection("userData")
        val userDocument = collectionReference.document(firebaseAuth.currentUser!!.uid)
        val dataUpdate = hashMapOf<String, Any>()
        dataUpdate["collegeName"] = binding.etCollege.text.toString()
        dataUpdate["fullName"] = binding.etFullname.text.toString()
        dataUpdate["guardianContact"] = binding.guardianNumberEdittext.text.toString()
        dataUpdate["guardianName"] = binding.guardianNameEdittext.text.toString()
        userDocument.update(dataUpdate)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile data updated to Firestore!", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error adding profile data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImage(imageUri: Uri?) {
        if (imageUri != null) {
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            val imageRef = storageRef.child("images/${firebaseAuth.currentUser!!.uid}.jpg")

            val uploadTask = imageRef.putFile(imageUri)
            uploadTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            // Upload the selected image to Firebase Storage
            uploadImage(imageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val storage = FirebaseStorage.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        var userData: User
        firestore = FirebaseFirestore.getInstance()
        val collectionReference =
            firestore.collection("userData").document(firebaseAuth.currentUser!!.uid)
        collectionReference.addSnapshotListener { value, error ->
            if (value == null || error != null) {
                Toast.makeText(this, "Something Happened", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            userData = value.toObject(User::class.java)!!
            binding.tvEmail.text = firebaseAuth.currentUser?.email
            binding.etFullname.setText(userData.fullName)
            binding.guardianNameEdittext.setText(userData.guardianName)
            binding.guardianNumberEdittext.setText(userData.guardianContact.toString())
            binding.etCollege.setText(userData.collegeName)
            binding.tvuserType.text = userData.userType
        }
        binding.editSaveButton.setOnClickListener {
            isEdit = !isEdit;
            if (isEdit) {
                binding.editSaveButton.text = "Save"
                binding.etFullname.isEnabled = true
                binding.etFullname.isEnabled = true;
                binding.etCollege.isEnabled = true;
                binding.guardianNumberEdittext.isEnabled = true;
                binding.guardianNameEdittext.isEnabled = true
            } else {
                updateUseData()
                binding.editSaveButton.text = "Edit"
                binding.etFullname.isEnabled = false;
                binding.etFullname.isEnabled = false;
                binding.etCollege.isEnabled = false;
                binding.guardianNumberEdittext.isEnabled = false;
                binding.guardianNameEdittext.isEnabled = false;
            }
        }
        val photoRef = storage.reference.child("images/${firebaseAuth.currentUser!!.uid}.jpg")
        photoRef.getBytes(Long.MAX_VALUE)
            .addOnSuccessListener { bytes ->
                // Image exists, create a bitmap from the byte array
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                // Set the bitmap to the ImageView
                binding.ivProfileImg.setImageBitmap(bitmap)
            }
        binding.ivProfileImg.setOnClickListener {
            if(!isEdit)
                Toast.makeText(this,"Click on edit to upload the user profile",Toast.LENGTH_SHORT).show()
            else {
                // Open gallery to select an image
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
            }
        }

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}