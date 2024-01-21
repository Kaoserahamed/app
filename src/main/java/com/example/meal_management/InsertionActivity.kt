package com.example.meal_management

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore
@Composable
fun Fireinsert(){
    var nam = "lsdflds"
    val databas = Firebase.database
    val myRef = databas.getReference("message")
    myRef.setValue(nam)

    println("Where i will be printerd")

    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("users").document("userId")

    val database = FirebaseDatabase.getInstance()
    val ref: DatabaseReference = database.getReference("users")
    var name = ref.get()

// Listen for changes in the data
    ref.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val data = dataSnapshot.getValue()
            // Handle the data here

        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Handle errors
        }
    })

    docRef.get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val data = document.data
                // Handle the data here
            } else {
                // No such document
            }
        }
        .addOnFailureListener { exception ->
            // Handle errors
        }

}