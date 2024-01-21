package com.example.meal_management

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
fun getMealRate( nu:String?=null, deno:String?=null): Double {
    var x = nu?.toDouble()
    var y = deno?.toDouble()
    if(y!=0.0){
        if (x != null) {
            return x/ y!!
        }
    }
    return 0.0
}

