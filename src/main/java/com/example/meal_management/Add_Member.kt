package com.example.meal_management

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.meal_management.ui.theme.Purple40
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Add_Mem(navController: NavController) {
    // Colors
    val gradientColors = listOf(
        Color(0, 153, 76),
        Color(188, 255, 192)
    )
    val brush = Brush.linearGradient(
        colors = gradientColors
    )
    val foregroundColor = Color.White

    // Check available members
    var isJoined by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    if (user != null) {
        val uid = user.uid
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users").document(uid)
        usersCollection.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                isJoined = documentSnapshot.getBoolean("joined") == true
            }
        }.addOnFailureListener { e ->
        }
    }

    // Get the member list
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var userList by remember { mutableStateOf(emptyList<User>()) }
    val db = Firebase.firestore
    db.collection("users").get().addOnSuccessListener { result ->
        val fetchedUsers = mutableListOf<User>()
        for (document in result) {
            val ans = document.data
            val name = ans["name"].toString()
            val mail = ans["email"].toString()
            val isjoined = ans["joined"].toString().toBoolean()
            if (!isjoined) {
                val user = User(true, mail, joined = false, manager = true, messId = "", name = name, "0", "0", "0")
                fetchedUsers.add(user)
            }
        }
        userList = fetchedUsers
        isLoading = false
    }.addOnFailureListener {
    }

    // UI Design Starts
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = Color.White,
                ),
                title = {
                    Text("Add Members")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Image(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Localized description",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        ScrollContent(innerPadding)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush)
                .padding(top = 60.dp)
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by name") },
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(userList.filter {
                    it.name?.contains(
                        searchQuery, ignoreCase = true
                    ) ?: false
                }) { user ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                            .border(1.dp, Color.Green, RoundedCornerShape(8.dp))
                            .background(
                                color = foregroundColor,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Email: ${user.email}", Modifier.padding(horizontal = 2.dp)
                            .padding(top = 5.dp), color = Color.Black)

                        Text("Name: ${user.name}", Modifier.padding(horizontal = 2.dp), color = Color.Black)
                        Button(
                            onClick = {
                                if (isJoined) {
                                    val db = Firebase.firestore
                                    val usersCollectionRef = db.collection("users")
                                    val userEmail = user.email
                                    usersCollectionRef
                                        .whereEqualTo("email", userEmail)
                                        .get()
                                        .addOnSuccessListener { querySnapshot ->
                                            for (document in querySnapshot.documents) {
                                                val userDocumentRef = document.reference

                                                userDocumentRef
                                                    .update("messId", auth.currentUser?.email)
                                                    .addOnSuccessListener {
                                                        Toast.makeText(context, "Invitation Sent", Toast.LENGTH_SHORT).show()
                                                    }
                                                    .addOnFailureListener { exception ->
                                                        Toast.makeText(context, "Not Sent", Toast.LENGTH_SHORT).show()
                                                    }
                                            }
                                        }
                                        .addOnFailureListener { exception ->
                                            Toast.makeText(context, "No user found", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    Toast.makeText(context, "Create a mess first", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Text(text = "Invite")
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }
            }
        }
    }
}


