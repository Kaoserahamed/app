package com.example.meal_management

import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(authViewModel: AuthViewModel, navController: NavController) {
    val foregroundColor = Color(android.graphics.Color.parseColor("#96EFFF"))
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isManager by remember { mutableStateOf(false) }
    var messId by remember { mutableStateOf("") }
    var isJoined by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var isLoading by remember { mutableStateOf(false) }
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
            .clickable { focusManager.clearFocus() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Text(text = "Signing Up", color = Color.Blue)
                }

            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .background(
                        Color(android.graphics.Color.rgb(249, 239, 219)),
                        RoundedCornerShape(4.dp)
                    )
            ) {
                Box(modifier = Modifier.padding(horizontal = 10.dp)){
                    CheckboxOption(
                        optionText = "Manager",
                        isChecked = isManager,
                        onCheckedChange = { isManager = it }
                    )
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            // Handle next action
                        }
                    )
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            // Handle next action
                        }
                    )
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp),
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // Handle sign-up action
                            keyboardController?.hide()
                        }
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = { isPasswordVisible = !isPasswordVisible }
                        ) {
                            Icon(
                                painter = if (isPasswordVisible) {
                                    painterResource(id = R.drawable.visible)
                                } else {
                                    painterResource(id = R.drawable.notvisible2)
                                },
                                contentDescription = if (isPasswordVisible) {
                                    "Hide password"
                                } else {
                                    "Show password"
                                }
                            )
                        }
                    }
                )
                val context = LocalContext.current
                Button(
                    onClick = {
                        if(name=="" || email=="" || password=="") {
                            Toast.makeText(context, "Fill all the fields", Toast.LENGTH_SHORT).show()
                        }
                        else if(!isValidEmail(email)){
                            Toast.makeText(context, "Not a valid email", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            isLoading = true
                            val auth = FirebaseAuth.getInstance()
                            val db = FirebaseFirestore.getInstance()
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val user = auth.currentUser
                                        val uid = user?.uid
                                        if (uid != null) {
                                            if (isManager)
                                                messId = email
                                            val newUser = User(false, email, isJoined, isManager, messId, name, "0", "0", "0")
                                            db.collection("users").document(uid)
                                                .set(newUser)
                                                .addOnSuccessListener {
                                                    Toast.makeText(context, "Signed Up", Toast.LENGTH_SHORT).show()
                                                    isLoading = false
                                                    navController.navigateUp()
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(context, "Something went error", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                    } else {
                                        isLoading = false
                                        Toast.makeText(context, "$email is already used.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp), colors = ButtonDefaults.buttonColors(Color(0, 153, 76)), shape = RoundedCornerShape(4.dp)
                ) {
                    Text("Sign Up")
                }
            }
        }
    }
}

@Composable
fun SignOutScreen(authViewModel: AuthViewModel) {
    // TODO: Implement the SignOut screen
}


