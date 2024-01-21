package com.example.meal_management

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.meal_management.ui.theme.Meal_ManagementTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthViewModel : ViewModel() {

}

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Meal_ManagementTheme {
                val authViewModel by viewModels<AuthViewModel>()
                val databaseReference = FirebaseDatabase.getInstance().reference
                val ob = MessId("1")
                databaseReference.child("messId").setValue(ob)
                val persondata = remember { mutableStateOf(Person()) }
                databaseReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val infoo = snapshot.child("members").getValue(Person::class.java)
                        if (infoo != null) {
                            persondata.value = infoo
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle errors
                    }
                })
                FirebaseApp.initializeApp(this)
                //  SignUpScreen(authViewModel,navController)
                SignOutScreen(authViewModel)
                // Navigation
                val navController = rememberNavController()
                NavHost(
                    navController = navController, startDestination = "login"
                ) {
                    composable("login") {
                        SignInScreen(
                            authViewModel,
                            navController = navController
                        )
                    }
                    composable("chat") { Chat(navController = navController)}
                    composable("home") { HomePage(navController = navController, authViewModel) }
                    composable("addmem") { Add_Mem(navController = navController) }
                    composable("addmeal") { AddMeal(navController = navController) }
                    composable("deposit") { Deposit(navController = navController) }
                    composable("memlist") { MembersList(navController = navController) }
                    composable("shopping") { Shopping_Add(navController = navController) }
                    composable("depohis") { DepoHistory(navController) }
                    composable("shophis") { ShopHistory(navController = navController) }
                    composable("signup") { SignUpScreen(authViewModel, navController) }
                    composable("homemember") { HomePage_Member(navController = navController) }
                    composable("create"){ CreateMess(navController)}
                    composable("change"){ ChangeManager(navController) }
                }
//                val callback = object : OnBackPressedCallback(true) {
//                    override fun handleOnBackPressed() {
//                        // Handle back press
//                        // Call your sign-out method from AuthViewModel
//                        authViewModel.signOut()
//                        finish()  // Optionally, finish the activity
//                    }
//                }
//
//                // Add the callback to the onBackPressedDispatcher
//                onBackPressedDispatcher.addCallback(this, callback)
            }
        }
    }
}
