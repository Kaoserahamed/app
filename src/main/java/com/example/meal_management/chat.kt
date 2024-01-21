package com.example.meal_management

import android.annotation.SuppressLint
import android.graphics.Color.rgb
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Calendar

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun Chat(navController: NavController) {
    val gradientColors = listOf(
        Color(0, 153, 76),
        Color(188, 255, 192)  // End color
    )

    val brush = Brush.linearGradient(
        colors = gradientColors
    )

    // Get the time and date
    val currentCalendar = Calendar.getInstance()
    val hour = currentCalendar.get(Calendar.HOUR)
    val minute = currentCalendar.get(Calendar.MINUTE)
    val amPm = if (currentCalendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
    val dayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH)
    val displayText = remember { mutableStateOf("") }
    displayText.value = "$hour:$minute $amPm, Day: $dayOfMonth"
    val combinedTime = displayText.value

    // get current user info
    var message by remember { mutableStateOf("") }
    var myname by remember { mutableStateOf("") }
    var mymessId by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    if (user != null) {
        val uid = user.uid
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users").document(uid)
        usersCollection.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                mymessId = documentSnapshot.getString("messId").toString()
                myname = documentSnapshot.getString("name").toString()
            }
        }.addOnFailureListener { e ->
        }
    }

    // get the messages
    var scrollState by remember { mutableIntStateOf(1) }
    var messages by remember { mutableStateOf(emptyList<Message>()) }
    var mymessages by remember { mutableStateOf(emptyList<Message>()) }
    val personsRef = FirebaseDatabase.getInstance().getReference("chat_history")
    DisposableEffect(Unit) {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val persons = snapshot.children.mapNotNull { it.getValue(Message::class.java) }
                messages = persons
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        personsRef.addValueEventListener(valueEventListener)
        onDispose {
            // Clean up the listener when the composable is disposed
            personsRef.removeEventListener(valueEventListener)
        }
    }

    // filter messages
    mymessages = messages.filter { mes ->
        mes.messId == mymessId
    }
    if (mymessages.isNotEmpty())
        scrollState = mymessages.size - 1

    // UI Design
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = Color.White,
                ),
                title = {
                    Text("Messages")
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = brush)
                .padding(8.dp)
                .padding(top = 45.dp), verticalArrangement = Arrangement.Bottom
        ) {
            if (mymessages.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    state = rememberLazyListState(scrollState)
                ) {
                    items(mymessages) { message ->
                        MessageBubble(message = message, myname)
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                BasicTextField(
                    value = message,
                    onValueChange = { newText ->
                        message = newText
                    },
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .width(250.dp)
                                .padding(horizontal = 10.dp) // margin left and right
                                .border(
                                    width = 2.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(size = 16.dp)
                                )
                                .background(Color.White, shape = RoundedCornerShape(16.dp))
                                .padding(horizontal = 16.dp, vertical = 12.dp), // inner padding
                        ) {
                            if (message.isEmpty()) {
                                Text(
                                    text = "Your Text here",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.Black
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                ElevatedButton(
                    onClick = {
                        if (mymessId != "" && myname != "" && message != "")
                            addchatistory(mymessId, myname, message, combinedTime)
                        message = ""
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(text = "Send")
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
        }
    }
}

fun addchatistory(
    messId: String? = null, name: String? = null, message: String? = null, tme: String? = null
) {
    val ref = FirebaseDatabase.getInstance().getReference("chat_history")
    var person = Message(messId, name, message, tme)
    val key = ref.push().key
    key?.let {
        val refe = ref.child(it)
        refe.setValue(person)
    }
}

@Composable
fun MessageBubble(message: Message, currentUser: String) {
    val isCurrentUser = message.name == currentUser
    val backgroundColor = if (isCurrentUser) Color.Blue else Color(150, 239, 255)
    val contentAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
    val textColor = if (!isCurrentUser) Color.Black else Color.White
    val ntext = message.name + "(" + message.tme + ")"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = contentAlignment
        ) {
            Text(
                text = ntext,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Box(
                modifier = Modifier
                    .background(color = backgroundColor, shape = RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = message.message ?: "",
                    color = textColor,
                    fontSize = 16.sp
                )
            }

        }
    }
}



