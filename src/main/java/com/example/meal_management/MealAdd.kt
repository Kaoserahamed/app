package com.example.meal_management

import android.widget.Toast
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
fun AddMeal(navController: NavController) {
    val backgroundColor = Color(android.graphics.Color.parseColor("#65B741"))
    val foregroundColor = Color(android.graphics.Color.parseColor("#C1F2B0"))
    val gradientColors = listOf(
        Color(0, 153, 76),
        Color(188, 255, 192)  // End color
    )

    val brush = Brush.linearGradient(
        colors = gradientColors
    )
    val auth = FirebaseAuth.getInstance()
    val currentUserEmail = auth.currentUser?.email
    var searchQuery by remember { mutableStateOf("") }
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
                    Text("  Add Meal")
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
                    TextField(
                        value = moneyAmount,
                        onValueChange = {
                            moneyAmount = it
                        },
                        label = { Text("Enter no of meals") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (!isValidInteger(moneyAmount)) {
                                Toast.makeText(context, "Enter valid amount", Toast.LENGTH_SHORT)
                                    .show()
                            } else if (selectedMember == null) {
                                Toast.makeText(context, "Select a member", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                updateTotalMeal(auth.currentUser?.email, moneyAmount)
                                updateTotalMealUser(
                                    selectedMember?.email, (moneyAmount.toInt() +
                                            (selectedMember?.meal?.toInt() ?: 0)).toString()
                                )
                                Toast.makeText(context, "Meal Added", Toast.LENGTH_SHORT).show()
                                selectedMember = null
                                moneyAmount = ""
                                keyboardController?.hide()
                            }

                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

fun updateTotalMeal(userEmail: String? = null, newTotalMeal: String) {
    var totalMeal = ""
    val db = FirebaseFirestore.getInstance()
    if (userEmail != null) {
        db.collection("homes").get().addOnSuccessListener { result ->
            for (document in result) {
                val ans = document.data
                if (ans["homeId"] == userEmail) {
                    totalMeal = ans["totalMeal"].toString()
                    val usersCollection = db.collection("homes")
                    usersCollection.whereEqualTo("homeId", userEmail)
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                val documentId = document.id
                                usersCollection.document(documentId)
                                    .update(
                                        "totalMeal",
                                        (totalMeal.toInt() + newTotalMeal.toInt()).toString()
                                    )
                                    .addOnSuccessListener {
                                    }
                                    .addOnFailureListener {
                                    }
                            }
                        }
                        .addOnFailureListener {
                        }
                }
            }
        }.addOnFailureListener {
        }
    }
}

fun updateTotalMealUser(userEmail: Any? = null, newTotalMeal: String) {
    if (userEmail != null) {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")
        usersCollection.whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Update the totalMeal field in the found document
                    val documentId = document.id
                    usersCollection.document(documentId)
                        .update("meal", newTotalMeal)
                        .addOnSuccessListener {
                        }
                        .addOnFailureListener { e ->
                        }
                }
            }
            .addOnFailureListener { e ->
            }
    }

}






