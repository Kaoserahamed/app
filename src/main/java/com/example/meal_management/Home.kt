package com.example.meal_management
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color.rgb
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.example.meal_management.Add_Cards
import com.example.meal_management.ScrollContent
import com.example.meal_management.TotalHisab
import com.example.meal_management.getMealRate
import com.example.meal_management.ui.theme.Purple40
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate

@SuppressLint("StateFlowValueCalledInComposition")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavController,authViewModel: AuthViewModel) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var mymessId by remember { mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()
    val usersCollection = user?.let { db.collection("users").document(it.uid) }
    usersCollection?.get()?.addOnSuccessListener { documentSnapshot ->
        if (documentSnapshot.exists()) {

            mymessId = documentSnapshot.getString("messId").toString()
        }
    }?.addOnFailureListener { e ->
    }

    //Database total Data new
    val gradientColors = listOf(
        Color(0,153,76),
        Color(188,255,192)  // End color
    )

    val brush = Brush.linearGradient(
        colors = gradientColors
    )
    val foregroundColor = Color(249, 239, 219)
    val txtColor = Color(android.graphics.Color.parseColor("#000000"))
    var showDialog by remember { mutableStateOf(false) }
    var houseName by remember { mutableStateOf("") }
    var totalMeal by remember { mutableStateOf("0") }
    var totalDeposit by remember { mutableStateOf("0") }
    var totalShopping by remember { mutableStateOf("0") }
    var mealRate by remember { mutableStateOf("0") }
    var balAnce by remember { mutableStateOf("0") }

//    val usersCollection = user?.let { db.collection("homes").document(it.uid) }
//    usersCollection?.get()?.addOnSuccessListener { documentSnapshot ->
//        if (documentSnapshot.exists()) {
//            houseName = documentSnapshot.getString("homeName").toString()
//            totalMeal = documentSnapshot.getString("totalMeal").toString()
//            totalDeposit = documentSnapshot.getString("totalDeposit").toString()
//            totalShopping = documentSnapshot.getString("totalShopping").toString()
//            balAnce = (totalDeposit.toInt() - totalShopping.toInt()).toString()
//            if(totalMeal!="0")
//                mealRate = (totalShopping.toDouble()/totalMeal.toDouble()).toInt().toString()
//        }
//    }?.addOnFailureListener { e ->
//    }

    db.collection("homes").get().addOnSuccessListener { result ->
        for (document in result) {
            val ans = document.data
            if(ans["homeId"]==mymessId)
            {
                 houseName = ans["homeName"].toString()
                 totalMeal = ans["totalMeal"].toString()
                 totalDeposit = ans["totalDeposit"].toString()
                 totalShopping = ans["totalShopping"].toString()
                balAnce = (totalDeposit.toInt() - totalShopping.toInt()).toString()
                if(totalMeal!="0")
                    mealRate = (totalShopping.toDouble()/totalMeal.toDouble()).toInt().toString()
            }
        }
    }.addOnFailureListener { exception ->
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        titleContentColor = Color.White,
                    ),
                    title = {
                        Text(text = houseName,color = Color.White)
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                           // authViewModel.signOut();

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
                            navController.navigate("create")
                        }) {
                            Image(
                                painter =painterResource(id = R.drawable.create),
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
                     .background(brush = brush)
                    //.background(backgroundColor)
                    .padding(10.dp)
                    .padding(top = 10.dp)
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                        .background(color = foregroundColor, shape = RoundedCornerShape(8.dp))
                        .padding(15.dp)
                ) {
                    Column {
                        DateDisplay()
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Total Meal : $totalMeal",
                            fontSize = 16.sp,
                            color = txtColor
                        )
                        Text(
                            text = "Meal Rate : $mealRate Tk",
                            fontSize = 16.sp,
                            color = txtColor
                        )
                        Text(
                            text = "Total Deposit : $totalDeposit Tk",
                            fontSize = 16.sp,
                            color = txtColor
                        )
                        Text(
                            text = "Total Shopping :  $totalShopping Tk",
                            fontSize = 16.sp,
                            color = txtColor
                        )
                        Text(
                            text = "Balance :  $balAnce Tk",
                            fontSize = 16.sp,
                            color = txtColor
                        )
                    }
                }
                Column(modifier = Modifier.fillMaxSize(),verticalArrangement = Arrangement.Bottom) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Add_Cards(navController)
                }
            }
        }
    }
}


@SuppressLint("StateFlowValueCalledInComposition")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateDisplay() {
    val currentDate = MutableStateFlow(LocalDate.now())
    val foregroundColor = Color(255, 255, 255)
    val txtColor = Color(android.graphics.Color.parseColor("#000000"))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = foregroundColor, shape = RoundedCornerShape(10.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(currentDate.value.dayOfMonth.toString(), fontSize = 18.sp, color = Color.Red, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(10.dp))
        Text(currentDate.value.month.toString(), fontSize = 18.sp, color = Color.Red, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = currentDate.value.year.toString(), fontSize = 18.sp, color = Color.Red, fontWeight = FontWeight.Bold)
    }
}


