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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.meal_management.ui.theme.Purple40
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepoHistory(navController: NavController) {
    val gradientColors = listOf(
        Color(0,153,76),
        Color(188,255,192)  // End color
    )

    val brush = Brush.linearGradient(
        colors = gradientColors
    )
    val backgroundColor = Color(173, 216, 230)
    val foregroundColor = Color.White
    var messId by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    if (user != null) {
        val uid = user.uid
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users").document(uid)
        usersCollection.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                messId = documentSnapshot.getString("messId").toString()
            }
        }.addOnFailureListener { e ->
        }
    }
    var depolist by remember { mutableStateOf(emptyList<Depo_His>()) }
    val personsRef = FirebaseDatabase.getInstance().getReference("deposit_history")

    DisposableEffect(Unit) {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val persons = snapshot.children.mapNotNull { it.getValue(Depo_His::class.java) }
                depolist = persons
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle the error if necessary
            }
        }
        personsRef.addValueEventListener(valueEventListener)
        onDispose {
            // Clean up the listener when the composable is disposed
            personsRef.removeEventListener(valueEventListener)
        }
    }
    var mymessages by remember { mutableStateOf(emptyList<Depo_His>()) }
    mymessages = depolist.filter { mes ->
        mes.messId == messId
    }
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = Color.White,
                ),
                title = {
                    Text("Deposit History")
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
        val testColor = Color(0, 153, 76)
        if(messId!="")
        {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush)
                    .padding(10.dp),

                ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp)
                        .padding(10.dp),
                    reverseLayout = true,
                    state = rememberLazyListState(mymessages.size-1)
                ) {

                    items(mymessages) { member ->
                        if(member.messId==messId){
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color = foregroundColor, shape = RoundedCornerShape(8.dp))
                                    .border(1.dp,color = Color.White,shape = RoundedCornerShape(8.dp))
                                    .padding(16.dp)

                            ) {
                                Text(text = "Name: ${member.name}",color = Color.Black)
                                Text(text = "Total Deposit: ${member.depo}",color = Color.Black)
                                Text(text = "Date: ${member.dte}",color = Color.Black)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                    }
                }
            }
        }
    }
}