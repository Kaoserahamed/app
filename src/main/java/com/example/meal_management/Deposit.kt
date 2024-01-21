package com.example.meal_management

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.meal_management.ui.theme.Purple40
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate


@SuppressLint("StateFlowValueCalledInComposition")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun Deposit(navController: NavController) {
    // Obtaining members from database
    val auth = FirebaseAuth.getInstance()
    val currentUserEmail = auth.currentUser?.email
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var userList by remember { mutableStateOf(emptyList<User>()) }
    val db = Firebase.firestore
    db.collection("users").get().addOnSuccessListener { result ->
        val fetchedUsers = mutableListOf<User>()
        for (document in result) {
            val ans = document.data
            if ((ans["messId"] == currentUserEmail && ans["joined"] == true)) {
                val name = ans["name"].toString()
                val email = ans["email"].toString()
                val depo = ans["depo"].toString()
                val user = User(false, email, true, false, "", name, depo, "", "")
                fetchedUsers.add(user)
            }
        }
        userList = fetchedUsers
        isLoading = false
    }.addOnFailureListener {
    }
//    var personList by remember { mutableStateOf(emptyList<Person>()) }
//    val personsRef = FirebaseDatabase.getInstance().getReference("Members")
//    DisposableEffect(Unit) {
//        val valueEventListener = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val persons = snapshot.children.mapNotNull { it.getValue(Person::class.java) }
//                personList = persons
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Handle the error if necessary
//            }
//        }
//        personsRef.addValueEventListener(valueEventListener)
//        onDispose {
//            personsRef.removeEventListener(valueEventListener)
//        }
//    }
    //new
    val currentDate = MutableStateFlow(LocalDate.now())
    val dte = currentDate.value.dayOfMonth.toString() + " " + currentDate.value.month

    var selectedMember by remember { mutableStateOf<User?>(null) }
    var moneyAmount by remember { mutableStateOf("") }
    val databaseReference = FirebaseDatabase.getInstance().reference
    val totalHisabData = remember { mutableStateOf(TotalHisab("0", "0", "0")) }
    databaseReference.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val totalHisab = snapshot.child("total").getValue(TotalHisab::class.java)
            if (totalHisab != null) {
                totalHisabData.value = totalHisab
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle errors
        }
    })
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = Color.White,
                ),
                title = {
                    Text("Add Deposit")
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
        val keyboardController = LocalSoftwareKeyboardController.current
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
                .padding(10.dp)
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
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = moneyAmount,
                    onValueChange = {
                        moneyAmount = it
                    },
                    label = { Text("Enter Money Amount") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        try {
                            if (!isValidInteger(moneyAmount)) {
                                Toast.makeText(context, "Enter valid amount", Toast.LENGTH_SHORT)
                                    .show()
                            } else if (selectedMember == null) {
                                Toast.makeText(context, "Select a member", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                updateTotalDepo(auth.currentUser?.email, moneyAmount)
                                updateTotalDepoUser(
                                    selectedMember?.email,
                                    (moneyAmount.toInt() + (selectedMember?.depo?.toInt()
                                        ?: 0)).toString()
                                )
                                adddepohistory(
                                    auth.currentUser?.email,
                                    selectedMember?.name,
                                    moneyAmount,
                                    dte
                                )
                                Toast.makeText(context, "Deposit Added", Toast.LENGTH_SHORT).show()
                                selectedMember = null
                                moneyAmount = ""
                                keyboardController?.hide()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "An error occurred.Try again",
                                Toast.LENGTH_SHORT
                            ).show()
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

fun isValidInteger(input: String): Boolean {
    return try {
        input.toInt()
        true
    } catch (e: NumberFormatException) {
        false
    }
}

@Composable
fun MemberDropdownList(
    memberList: List<User>,
    selectedMember: User?,
    onMemberSelected: (User) -> Unit,
) {
    val gradientColors = listOf(
        Color(0, 153, 76),
        Color(188, 255, 192)  // End color
    )

    val brush = Brush.linearGradient(
        colors = gradientColors
    )
    var isexp by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .padding(10.dp)
            .padding(top = 50.dp)
    ) {

        Box(modifier = Modifier
            .padding(top = 10.dp)
            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
            .padding(10.dp)
            .clickable { isexp = true }
        ) {
            Text("Select a Member", fontSize = 20.sp, color = Color.Black)
        }


        DropdownMenu(modifier = Modifier.background(brush = brush),
            expanded = isexp,
            onDismissRequest = { isexp = false }
        ) {
            memberList.forEach { memberName ->
                DropdownMenuItem(modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp)
                    .padding(horizontal = 5.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(5.dp)),
                    text = {
                        if (memberName != null) {
                            memberName.name?.let { Text(it, color = Color.Black) }
                        }
                    },
                    onClick = {
                        val selectedMember = memberList.find { it.name == memberName.name }
                        selectedMember?.let { onMemberSelected(it) }
                        isexp = false
                    }
                )
            }
        }
    }
}

fun updateTotalDepo(userEmail: String? = null, newTotalMeal: String) {
    var totaldepo = ""
    if (userEmail != null) {
        val db = FirebaseFirestore.getInstance()
        db.collection("homes").get().addOnSuccessListener { result ->
            for (document in result) {
                val ans = document.data
                if (ans["homeId"] == userEmail) {
                    totaldepo = ans["totalDeposit"].toString()
                    val usersCollection = db.collection("homes")
                    usersCollection.whereEqualTo("homeId", userEmail)
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                // Update the totalMeal field in the found document
                                val documentId = document.id
                                usersCollection.document(documentId)
                                    .update(
                                        "totalDeposit",
                                        (totaldepo.toInt() + newTotalMeal.toInt()).toString()
                                    )
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
        }.addOnFailureListener { exception ->
        }
    }
}

fun updateTotalDepoUser(userEmail: Any? = null, newTotalMeal: String) {
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
                        .update("depo", newTotalMeal)
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


fun adddepohistory(
    messId: String? = null,
    name: String? = null,
    amount: String? = null,
    dte: String? = null,

    ) {
    val ref = FirebaseDatabase.getInstance().getReference("deposit_history")
    var person = Depo_His(messId, name, amount, dte)
    val key = ref.push().key
    key?.let {
        val refe = ref.child(it)
        refe.setValue(person)
    }
}
