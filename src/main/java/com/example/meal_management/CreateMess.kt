package com.example.meal_management

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMess(navController: NavController) {
    var homeName by remember { mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val gradientColors = listOf(
        Color(0, 153, 76),
        Color(188, 255, 192)  // End color
    )

    val brush = Brush.linearGradient(
        colors = gradientColors
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = brush)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create a New Mess", modifier = Modifier.padding(bottom = 16.dp)
        )
        val context = LocalContext.current
        TextField(
            value = homeName,
            onValueChange = { homeName = it },
            label = { Text("Enter Mess Name") },
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(
            onClick = {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val uid = currentUser.email ?: ""
                    val home = CreateHome(uid, homeName, uid, "0", "0", "0", "0")
                    db.collection("homes").document(currentUser.uid).set(home)
                        .addOnSuccessListener {
                        }.addOnFailureListener { e ->

                        }
                    val homeUpdates = mapOf(
                        "depo" to "0",
                        "joined" to false,
                        "meal" to "0",
                        "messId" to "",
                    )
                    val userUpdates = mapOf("joined" to true, "messId" to currentUser?.email)

                    val db = Firebase.firestore
                    val usersCollectionRef = db.collection("users")
                    usersCollectionRef
                        .whereEqualTo("messId", auth.currentUser?.email)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            for (document in querySnapshot.documents) {
                                val userDocumentRef = document.reference
                                userDocumentRef
                                    .update(homeUpdates)
                                    .addOnSuccessListener {
                                        val documentRef =
                                            db.collection("users").document(currentUser.uid)
                                        documentRef.update(userUpdates)
                                            .addOnSuccessListener {
                                            }
                                            .addOnFailureListener { e ->
                                            }
                                    }
                                    .addOnFailureListener {
                                    }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "No user found", Toast.LENGTH_SHORT).show()
                        }
                }
                navController.navigateUp()
            },
        ) {
            Text("Create")
        }
    }
}