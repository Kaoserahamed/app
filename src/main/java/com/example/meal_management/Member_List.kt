package com.example.meal_management

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.meal_management.ui.theme.Purple40
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersList(navController: NavController) {
    val gradientColors = listOf(
        Color(0,153,76),
        Color(188,255,192)  // End color
    )

    val brush = Brush.linearGradient(
        colors = gradientColors
    )
    var houseName by remember { mutableStateOf("") }
    var totalMeal by remember { mutableStateOf("") }
    var totalDeposit by remember { mutableStateOf("") }
    var totalShopping by remember { mutableStateOf("") }
    var mealRate by remember { mutableStateOf("0") }
    var balAnce by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val db = FirebaseFirestore.getInstance()
    val usersCollection = user?.let { db.collection("homes").document(it.uid) }
    usersCollection?.get()?.addOnSuccessListener { documentSnapshot ->
        if (documentSnapshot.exists()) {
            houseName = documentSnapshot.getString("homeName").toString()
            totalMeal = documentSnapshot.getString("totalMeal").toString()
            totalDeposit = documentSnapshot.getString("totalDeposit").toString()
            totalShopping = documentSnapshot.getString("totalShopping").toString()
            balAnce = (totalDeposit.toInt() - totalShopping.toInt()).toString()
            if(totalMeal!="0")
                mealRate = (totalShopping.toDouble()/totalMeal.toDouble()).toInt().toString()
        }
    }?.addOnFailureListener { e ->
    }
    // New code
    var messId by remember { mutableStateOf("") }
    if (user != null) {
        val uid = user.uid
        val usersCollection = db.collection("users").document(uid)
        usersCollection.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                 messId = documentSnapshot.getString("messId").toString()
            }
        }.addOnFailureListener { e ->
        }
    }

    var isLoading by remember { mutableStateOf(false) }

    var userList by remember { mutableStateOf(emptyList<User>()) }
    db.collection("users").get().addOnSuccessListener { result ->
        val fetchedUsers = mutableListOf<User>()
        for (document in result) {
            val ans = document.data
            if(ans["messId"]==messId && ans["joined"]==true)
            {
                val name = ans["name"].toString()
                val email = ans["email"].toString()
                val depo = ans["depo"].toString()
                val meal = ans["meal"].toString()
                val left = ans["left"].toString()
                val user = User(false,email,true,false,"",name,depo,meal,left)
                fetchedUsers.add(user)
            }
        }
        userList = fetchedUsers
        isLoading = false
    }.addOnFailureListener {
    }
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = Color.White,
                ),
                title = {
                    Text("Member List")
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
        val foregroundColor = Color.White
        ScrollContent(innerPadding)
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .background(brush = brush)
                .padding(top = 60.dp)
                .padding(20.dp)) {

                items(userList) { member ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = foregroundColor,shape = RoundedCornerShape(8.dp))
                            .padding(15.dp)

                    ) {
                        Text(text = "Name : ${member.name}",color = Color.Red)
                        Text(text = "Email : ${member.email}",color = Color.Black)
                        Text(text = "Total Deposit : ${member.depo}",color = Color.Black)
                        Text(text = "Total Meal : ${member.meal}",color = Color.Black)
                        Text(text = "Left : ${((member.depo?.toDouble() ?: 0.0) - (member.meal?.toDouble() ?: 0.0)
                                * mealRate.toDouble()).toInt()}",color = Color.Black)

                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
    }
}

