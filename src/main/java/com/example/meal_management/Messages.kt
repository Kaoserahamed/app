package com.example.meal_management

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Chat(name: String) {
    var nme by remember { mutableStateOf("")}
    var msg by remember { mutableStateOf("")}
    val keyboardController = LocalSoftwareKeyboardController.current
    var msgList by remember { mutableStateOf(emptyList<Message>()) }
    val databaseReference = FirebaseDatabase.getInstance().reference
    DisposableEffect(Unit) {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val persons = snapshot.children.mapNotNull { it.getValue(Message::class.java) }
                msgList = persons
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if necessary
            }
        }
        databaseReference.addValueEventListener(valueEventListener)
        onDispose {
            // Clean up the listener when the composable is disposed
            databaseReference.removeEventListener(valueEventListener)
        }
    }
    val query = databaseReference.child("AllMembers").orderByChild("email").equalTo(MyClass.mail)
    query.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (snapshot in dataSnapshot.children) {
                val person = snapshot.getValue(Persons::class.java)
                person?.let {
                    nme = person.name.toString()
                }
            }
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // Handle errors
        }
    })

    Column {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(163, 173, 246))
                .padding(top = 20.dp)
                .padding(20.dp)
        ) {

            items(msgList) { member ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(121, 241, 232), shape = RoundedCornerShape(8.dp))
                        .padding(15.dp)

                ) {
                    Text(text = "Name: ${member.name}")
                    Text(text = "Total Deposit: ${member.message}")
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        TextField(
            value = msg,
            onValueChange = { msg = it },
            label = { Text("") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    // Handle login action
                    keyboardController?.hide()
                }
            )
        )
    }
}