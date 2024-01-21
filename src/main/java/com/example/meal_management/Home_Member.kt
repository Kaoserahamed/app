package com.example.meal_management

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.meal_management.Add_Cards
import com.example.meal_management.ScrollContent
import com.example.meal_management.TotalHisab
import com.example.meal_management.getMealRate
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

@SuppressLint("StateFlowValueCalledInComposition", "SuspiciousIndentation")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage_Member(navController: NavController) {
    val gradientColors = listOf(
        Color(0,153,76),
        Color(188,255,192)  // End color
    )
    val brush = Brush.linearGradient(
        colors = gradientColors
    )
    val foregroundColor = Color(249, 239, 219)
    var showDialog by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var houseName by remember { mutableStateOf("") }
    var totalMeal by remember { mutableStateOf("00") }
    var totalDeposit by remember { mutableStateOf("00") }
    var totalShopping by remember { mutableStateOf("00") }
    var mealRate by remember { mutableStateOf("00") }
    var balAnce by remember { mutableStateOf("00") }
    var isjoined by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        titleContentColor = Color.Black,
                    ),
                    title = {
                        Text(houseName,color = Color.White)
                    },
                    navigationIcon = {
                        IconButton(onClick = {

                        }) {
                            Image(
                                painter =painterResource(id = R.drawable.home),
                                contentDescription = "Localized description",
                            )
                        }
                    },
                    actions = {
                        // Add icon to the right side of the top app bar
                        IconButton(onClick = {
                            // Handle icon click
                            showDialog = true
                        }) {
                            Image(
                                painter =painterResource(id = R.drawable.exit),
                                contentDescription = "Localized description",
                            )
                        }
                    }
                )
            },
        ) { innerPadding ->
            ScrollContent(innerPadding)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush)
                    .padding(10.dp)
                    .padding(top = 10.dp)
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = foregroundColor, shape = RoundedCornerShape(8.dp))
                        .padding(15.dp)
                ) {
                    Column {
                        DateDisplay()
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = "Total Meal : $totalMeal", fontSize = 18.sp)
                        Text(text = "Meal Rate : $mealRate Tk", fontSize = 18.sp)
                        Text(text = "Total Deposit : $totalDeposit Tk", fontSize = 18.sp)
                        Text(text = "Total Shopping :  $totalShopping Tk", fontSize = 18.sp)
                        Text(text = "Balance :  $balAnce Tk", fontSize = 18.sp)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .clickable(onClick = { navController.navigate("memlist") })
                        .padding(5.dp),
                    contentAlignment = Alignment.Center

                ) {
                    Text(text = "Members", fontSize = 25.sp, color = Color.Red,fontFamily = FontFamily.Cursive)
                }
                // Confirmation dialog
                val userUpdates = mapOf("messId" to "","depo" to "0","joined" to false,"meal" to "0")
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            // Dismiss the dialog when clicked outside
                            showDialog = false
                        },
                        title = {
                            Text("Confirmation")
                        },
                        text = {
                            Text("Are you sure you want to leave current mess?")
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    // Handle confirmation
                                    showDialog = false
                                    // Add your confirmation logic here
                                    val currentUser = FirebaseAuth.getInstance().currentUser
                                    val db = Firebase.firestore
                                    val usersCollectionRef = db.collection("users")
                                    currentUser?.let { user ->
                                        val userUid = user.uid // Get the UID of the current user
                                        val currentUserRef = usersCollectionRef.document(userUid)
                                        currentUserRef
                                            .update(userUpdates)
                                            .addOnSuccessListener {
                                                // Properties updated successfully

                                            }
                                            .addOnFailureListener { exception ->
                                                // Handle failure while updating the properties
                                            }
                                    }
                                }
                            ) {
                                Text("Yes")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    // Dismiss the dialog
                                    showDialog = false
                                }
                            ) {
                                Text("No")
                            }
                        }
                    )
                }
                val context = LocalContext.current
                var mymessId by remember { mutableStateOf("") }
                var myname by remember { mutableStateOf("") }
                var mydepo by remember { mutableStateOf("") }
                var mymeal by remember { mutableStateOf("") }
                var myleft by remember { mutableStateOf("") }



                val user = auth.currentUser
                if (user != null) {
                    val uid = user.uid
                    val db = FirebaseFirestore.getInstance()
                    val usersCollection = db.collection("users").document(uid)
                    usersCollection.get().addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {

                            mymessId = documentSnapshot.getString("messId").toString()
                             isjoined = documentSnapshot.getBoolean("joined") == true
                            myname = documentSnapshot.getString("name").toString()
                            mydepo = documentSnapshot.getString("depo").toString()
                            mymeal = documentSnapshot.getString("meal").toString()

                        }
                    }.addOnFailureListener { e ->
                    }
                }
                if(mymessId!=""){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent, shape = RoundedCornerShape(8.dp))
                            .clickable(onClick = { navController.navigate("memlist") })
                            .padding(5.dp),
                        contentAlignment = Alignment.Center

                    ) {
                        Column {
                            if(!isjoined){
                                Text(text = "join to $mymessId 's house")
                                Button(onClick = {
                                    isjoined = true
                                    val currentUser = FirebaseAuth.getInstance().currentUser
                                    val db = Firebase.firestore
                                    val usersCollectionRef = db.collection("users")
                                    currentUser?.let { user ->
                                        val userUid = user.uid // Get the UID of the current user
                                        val currentUserRef = usersCollectionRef.document(userUid)
                                        currentUserRef
                                            .update(
                                                "joined", true,
                                            )
                                            .addOnSuccessListener {
                                                // Properties updated successfully

                                            }
                                            .addOnFailureListener { exception ->
                                                // Handle failure while updating the properties
                                            }
                                    }
                                }) {
                                    Text(text = "JOIN")
                                }
                            }

                        }

                    }
                }
                if(isjoined)
                {
                    val db = Firebase.firestore
                    val messref = db.collection("homes")
                    messref
                        .whereEqualTo("homeId", mymessId)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            for (document in querySnapshot.documents) {
                                houseName = document["homeName"].toString()
                                totalMeal = document["totalMeal"].toString()
                                totalDeposit = document["totalDeposit"].toString()
                                totalShopping = document["totalShopping"].toString()
                                if(totalMeal.toInt()!=0)
                                    mealRate = (totalShopping.toDouble()/totalMeal.toDouble()).toInt().toString()
                                balAnce = (totalDeposit.toInt() - totalShopping.toInt()).toString()
                                myleft = (mydepo.toDouble() - (mealRate.toDouble() * mymeal.toDouble()).toInt()).toString()
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Handle failure while querying for the user by email
                        }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier
                    .background(Color.Transparent, shape = RoundedCornerShape(8.dp))
                    .padding(10.dp)) {
                    Surface(
                        modifier = Modifier
                            .width(100.dp)
                            .height(120.dp)
                            .clickable(onClick = { navController.navigate("chat") })
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .background(Color.White, shape = RoundedCornerShape(8.dp)),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = (painterResource(id = R.drawable.message_big)),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(60.dp)
                                    .padding(horizontal = 2.dp)
                            )
                            Text(
                                text = "Message",
                                color = Color.Red,
                                modifier = Modifier
                                    .padding(horizontal = 2.dp), fontSize = 18.sp,fontFamily = FontFamily.Cursive
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Surface(
                        modifier = Modifier
                            .width(100.dp)
                            .height(120.dp)
                            .clickable(onClick = { navController.navigate("depohis") })
                            .clip(RoundedCornerShape(8.dp)),
                    ) {
                        Column(
                            modifier = Modifier.background(Color.White),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = (painterResource(id = R.drawable.de)),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(60.dp)
                                    .padding(2.dp)
                            )
                            Text(
                                text = "Deposit",
                                color = Color.Red,
                                modifier = Modifier
                                    .padding(horizontal = 2.dp), fontSize = 18.sp,fontFamily = FontFamily.Cursive

                            )
                            Text(
                                text = "History",
                                color = Color.Red,
                                modifier = Modifier
                                    .padding(horizontal = 2.dp), fontSize = 18.sp,fontFamily = FontFamily.Cursive

                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Surface(
                        modifier = Modifier
                            .width(100.dp)
                            .height(120.dp)
                            .clickable(onClick = { navController.navigate("shophis") })
                            .clip(RoundedCornerShape(8.dp)),
                    ) {
                        Column(
                            modifier = Modifier
                                .background(Color.White),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = (painterResource(id = R.drawable.shohis)),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(60.dp)
                                    .padding(2.dp)
                            )
                            Text(
                                text = "Shopping",
                                color = Color.Red,
                                modifier = Modifier
                                    .padding(horizontal = 2.dp), fontSize = 18.sp,fontFamily = FontFamily.Cursive
                            )
                            Text(
                                text = "History",
                                color = Color.Red,
                                modifier = Modifier
                                    .padding(horizontal = 2.dp), fontSize = 18.sp,fontFamily = FontFamily.Cursive
                            )
                        }
                    }
                }
            }
        }
    }
}

