package com.example.meal_management

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(navController: NavController) {
    val foregroundColor = Color(android.graphics.Color.parseColor("#96EFFF"))
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var optionASelected by remember { mutableStateOf(false) }
    var optionBSelected by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = foregroundColor)
            .padding(16.dp)
            .clickable { focusManager.clearFocus() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CheckboxOption(
            optionText = "Manager",
            isChecked = optionASelected,
            onCheckedChange = { optionASelected = it; optionBSelected = !it;type = "Manager" }
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    // Handle next action
                }
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    // Handle next action
                }
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (!isValidEmail(email)) {
                    Toast.makeText(context, "$email is not valid", Toast.LENGTH_SHORT).show()
                } else if (name != null && password != null && type != "") {
                    signUpWithEmailAndPassword(email, password)
                    addPersonsToDatabase(name, "0", "0", email, password, type)
                    Toast.makeText(context, "Sign up completed", Toast.LENGTH_SHORT).show()
                    navController.navigateUp()
                } else {
                    Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                }

            },
            modifier = Modifier
                .fillMaxWidth().padding(horizontal = 20.dp)
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = "Sign Up")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Up")
        }
    }
}
 fun signUpWithEmailAndPassword(email: String, password: String) {
    var errorMessage = ""
    try {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registration successful, additional user data can be stored in Realtime Database
                    val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                    user?.let { saveUserDataToDatabase(it.uid, email) }
                } else {
                    // Registration failed, handle the error
                    val exception = task.exception
                    if (exception is FirebaseAuthException) {
                        // Handle specific authentication errors
                        errorMessage = when (exception.errorCode) {
                            "ERROR_EMAIL_ALREADY_IN_USE" -> "Email is already in use"
                            else -> "Registration failed"
                        }
                    } else {
                        // Handle other exceptions
                        errorMessage = "Registration failed"
                    }
                }
            }
    } catch (e: Exception) {
        // Handle other exceptions
        errorMessage = "Registration failed"
    }
}
 fun saveUserDataToDatabase(userId: String, email: String) {
    // Example: Saving email to "users" node in Realtime Database
    val database = Firebase.database
    val usersRef = database.getReference("users")
    val user = mapOf(
        "email" to email

        // Add other user data if needed
    )
    usersRef.child(userId).setValue(user)
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
    return emailRegex.matches(email)
}


fun addPersonsToDatabase(
    name: String? = null,
    depo: String? = null,
    meal: String? = null,
    email: String? = null,
    pass: String? = null,
    type: String? = null
) {
    var person = Persons(name, depo, meal, email, pass, type)
    val personsRef = FirebaseDatabase.getInstance().getReference("AllMembers")
    val key = personsRef.push().key
    key?.let {
        val personRef = personsRef.child(it)
        personRef.setValue(person)
    }
}

@Composable
fun CheckboxOption(
    optionText: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = isChecked,
                onValueChange = { onCheckedChange(!isChecked) }
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onCheckedChange(it) },
            modifier = Modifier
                .size(24.dp)
                .background(MaterialTheme.colorScheme.primary)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = optionText,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(8.dp)
        )
    }
}

