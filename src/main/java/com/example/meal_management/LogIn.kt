package com.example.meal_management

import android.graphics.Color.rgb
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.meal_management.ui.theme.Purple40
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.values
import com.google.firebase.firestore.FirebaseFirestore
import java.time.format.TextStyle


class MyClass {
    companion object {
        var mail: String = ""
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(authViewModel: AuthViewModel, navController: NavController) {

    var personList by remember { mutableStateOf(emptyList<Persons>()) }
    val personsRef = FirebaseDatabase.getInstance().getReference("AllMembers")
    DisposableEffect(Unit) {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val persons = snapshot.children.mapNotNull { it.getValue(Persons::class.java) }
                personList = persons
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if necessary
            }
        }
        personsRef.addValueEventListener(valueEventListener)
        onDispose {
            personsRef.removeEventListener(valueEventListener)
        }
    }

    val backgroundColor = Color(android.graphics.Color.rgb(180, 212, 255))
    var email by remember { mutableStateOf("") }
    var flag by remember { mutableIntStateOf(0) }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var showDialog by remember { mutableStateOf(false) }
    val gradientColors = listOf(
        // Start color
        Color.Blue,
        Purple40
    )

    val brush = Brush.linearGradient(
        colors = gradientColors
    )
    val cols = listOf(
            Color.Blue,
            Purple40
    )
    val testColor = Color(0, 153, 76)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable { focusManager.clearFocus() }
            .background(testColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent, RoundedCornerShape(8.dp))
                .padding(bottom = 40.dp)
                .padding(top = 50.dp)
                .clip(shape = RoundedCornerShape(8.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
            , verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter =painterResource(id = R.drawable.breakfast),
                contentDescription = "Localized description",
            )
        }
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            //.background(Color.White))
            .background(Color(rgb(249, 239, 219)), RoundedCornerShape(4.dp))
        )
        {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(value = email,
                onValueChange = { email = it;MyClass.mail = it },
                label = { Text("Email",color = Color.Black) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onNext = {
                    // Handle next action
                }))
            OutlinedTextField(value = password,
                onValueChange = { password = it },
                label = { Text("Password",color = Color.Black) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    // Handle login action
                    keyboardController?.hide()
                }),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            painter = if (isPasswordVisible) {
                                painterResource(id = R.drawable.visible)
                            } else {
                                painterResource(id = R.drawable.notvisible2)
                            }, contentDescription = if (isPasswordVisible) {
                                "Hide password"
                            } else {
                                "Show password"
                            }
                        )
                    }
                })
            val context = LocalContext.current
            var loading by remember { mutableStateOf(false) }
            Button(
                onClick = {
                    if (email.isEmpty() || password.isEmpty()) {
                        showDialog = true
                    } else {
                        loading = true
                        keyboardController?.hide()
                        val auth = FirebaseAuth.getInstance()
                        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                if (user != null) {
                                    val uid = user.uid
                                    val db = FirebaseFirestore.getInstance()
                                    val usersCollection = db.collection("users").document(uid)
                                    usersCollection.get().addOnSuccessListener { documentSnapshot ->
                                        if (documentSnapshot.exists()) {
                                            loading = false
                                            val isManager = documentSnapshot.getBoolean("manager") == true
                                            val name = documentSnapshot.getString("name").toString()
                                            if (isManager) {
                                                navController.navigate("home")
                                            } else {
                                                navController.navigate("homemember")
                                            }
                                        }
                                    }.addOnFailureListener { e ->
                                    }
                                }
                            }else{
                                Toast.makeText(context, "Log in failed", Toast.LENGTH_SHORT).show()
                                loading = false
                            }
                        }
                    }
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                , colors = ButtonDefaults.buttonColors(testColor), shape = RoundedCornerShape(4.dp)
            ) {
                Text("Log in")
            }
            if (loading) {
                Column(modifier = Modifier.fillMaxWidth()
                    ,horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {
                    CircularProgressIndicator(color = testColor)
                }

            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
                onClick = {
                    navController.navigate("signup")
                }, colors = ButtonDefaults.buttonColors(testColor), shape = RoundedCornerShape(4.dp)
            ) {
                Text("Sign up")
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (showDialog) {
            EmptyFieldDialog(
                showDialog = showDialog,
                onDismiss = { showDialog = false }
            )
        }
    }
}
@Composable
fun EmptyFieldDialog(showDialog: Boolean, onDismiss: () -> Unit) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Error") },
                text = { Text("Please fill in both email and password fields.") },
                confirmButton = {
                    Button(onClick = onDismiss) {
                        Text("OK")
                    }
                }
            )
        }
    }
}



