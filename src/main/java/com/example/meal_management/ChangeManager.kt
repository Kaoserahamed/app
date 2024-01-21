package com.example.meal_management

import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.meal_management.ui.theme.Purple40
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ChangeManager(navController: NavController) {
    // Colors
    val gradientColors = listOf(
        Color(0, 153, 76),
        Color(188, 255, 192)  // End color
    )
    val brush = Brush.linearGradient(
        colors = gradientColors
    )

    // Get user list
    val auth = FirebaseAuth.getInstance()
    val currentUserEmail = auth.currentUser?.email
    var isLoading by remember { mutableStateOf(false) }
    var userList by remember { mutableStateOf(emptyList<User>()) }
    val db = Firebase.firestore
    val focusManager = LocalFocusManager.current
    db.collection("users").get().addOnSuccessListener { result ->
        val fetchedUsers = mutableListOf<User>()
        for (document in result) {
            val ans = document.data
            if ((ans["messId"] == currentUserEmail && ans["joined"] == true)) {
                val name = ans["name"].toString()
                val email = ans["email"].toString()
                val meal = ans["meal"].toString()
                val user = User(false, email, true, false, "", name, "", meal, "")
                fetchedUsers.add(user)
            }
        }
        userList = fetchedUsers
        isLoading = false
    }.addOnFailureListener { exception ->
    }

    // Action when back arrow clicked
    var backArrowClicked by remember { mutableStateOf(false) }
    DisposableEffect(Unit) {
        val backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Perform your action when back arrow is clicked
                backArrowClicked = true
            }
        }
        // Add the callback to the back press dispatcher
        //onBackPressedDispatcher.addCallback(backCallback)

        // Remove the callback when the DisposableEffect is disposed
        onDispose {
            backCallback.remove()
        }
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    var selectedMember by remember { mutableStateOf<User?>(null) }
    var moneyAmount by remember { mutableStateOf("") }


    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = Color.White,
                ),
                title = {
                    Text("  Change Manager")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        backArrowClicked = true
                    }) {
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
                .fillMaxSize()
                .background(brush = brush)
                .padding(10.dp)
                .clickable { focusManager.clearFocus() },

            ) {
            MemberDropdownList(
                memberList = userList,
                selectedMember = selectedMember,
            ) { member ->
                selectedMember = member
            }
            selectedMember?.let {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text("Selected Member: ${it.name}", fontSize = 20.sp)
                }

            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            try {
                                val auth = FirebaseAuth.getInstance()
                                val currentUser = auth.currentUser
                                val uid = currentUser?.uid
                                val db = Firebase.firestore

                                // Change home Id with selected member's email
                                val usersCollection = db.collection("homes")
                                usersCollection.whereEqualTo("homeId", currentUser?.email)
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        for (document in documents) {
                                            // Update the totalMeal field in the found document
                                            val documentId = document.id
                                            usersCollection.document(documentId)
                                                .update("homeId", selectedMember?.email)
                                                .addOnSuccessListener {
                                                }
                                                .addOnFailureListener { e ->
                                                }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                    }

                                // change all members messId with selected member's email
                                val usersCollectionRef = db.collection("users")
                                usersCollectionRef
                                    .whereEqualTo("messId", auth.currentUser?.email)
                                    .get()
                                    .addOnSuccessListener { querySnapshot ->
                                        for (document in querySnapshot.documents) {
                                            val userDocumentRef = document.reference
                                            userDocumentRef
                                                .update("messId", selectedMember?.email)
                                                .addOnSuccessListener {
                                                    val documentRef =
                                                        uid?.let { it1 ->
                                                            db.collection("users").document(
                                                                it1
                                                            )
                                                        }
                                                    // set current manager to member
                                                    documentRef?.update("manager", false)
                                                        ?.addOnSuccessListener {
                                                            // Handle success

                                                        }?.addOnFailureListener {
                                                            // Handle failure
                                                        }

                                                    // set selected member to manager
                                                    val db = FirebaseFirestore.getInstance()
                                                    val usersCollection = db.collection("users")
                                                    usersCollection.whereEqualTo(
                                                        "email",
                                                        selectedMember?.email
                                                    )
                                                        .get()
                                                        .addOnSuccessListener { documents ->
                                                            for (document in documents) {
                                                                val documentId = document.id
                                                                usersCollection.document(documentId)
                                                                    .update("manager", true)
                                                                    .addOnSuccessListener {
                                                                    }
                                                                    .addOnFailureListener { e ->
                                                                    }
                                                            }
                                                        }
                                                        .addOnFailureListener {
                                                        }
                                                    Toast.makeText(context, "Manager Changed", Toast.LENGTH_SHORT).show()
                                                }
                                                .addOnFailureListener {
                                                    Toast.makeText(context, "Not Sent", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "No user found", Toast.LENGTH_SHORT).show()
                                    }
                                navController.navigate("login") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "An error occurred.Try again", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Change")
                    }
                }
            }
        }
    }
}