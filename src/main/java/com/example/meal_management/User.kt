package com.example.meal_management

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class TotalHisab(val totalMeal:String? = null,val totalDeposit:String?=null,val totalShopping:String?=null){

}
@IgnoreExtraProperties
data class Person(val name:String?=null, var depo:String?=null, var meal:String?=null, var left:String?=null){

}
@IgnoreExtraProperties
data class Depo_His(val messId: String?=null,val name:String?=null, var depo:String?=null, var dte:String?=null){

}
@IgnoreExtraProperties
data class Shop_His(val messId:String?=null,val name:String?=null,val amount:String?=null, var itemS:String?=null){

}
@IgnoreExtraProperties
data class Persons(val name:String?=null, var depo:String?=null, var meal:String?=null,
                   var email:String?=null,var pass:String?=null,var type:String?=null){
}
data class Message(val messId:String?=null, val name:String?=null, var message:String?=null,var tme:String?=null){

}
data class User(
    val created: Boolean,
    val email: String? = null,
    val joined: Boolean,
    val manager: Boolean,
    var messId:String,
    val name: String?=null,
    val depo:String?="00",
    val meal:String?="00",
    val left :String?="00"
){

}

data class CreateHome(
    val userEmail: String,
    val homeName: String,
    var homeId: String,
    var totalMeal: String,
    var totalDeposit: String,
    var totalShopping: String,
    var Balance:String
)  {

}
data class MessId(
    var messId : String
)
//data class FirebaseAuthUser(
//    val email: String?=null,
//    val password: String?=null
//)
//data class RealtimeDatabaseUser(
//    val isManager: String ?=null,
//    val isCreated: String ?=null,
//    val isJoined: String ?=null
//)
//                val query = databaseReference.child("AllMembers").orderByChild("email").equalTo(email)
//                query.addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        for (snapshot in dataSnapshot.children) {
//                            val person = snapshot.getValue(Persons::class.java)
//                            person?.let {
//                                if(password == person.pass)
//                                {
//                                    selectedMember = person
//                                   if(person.type=="Manager")
//                                       flag = 1;
//                                    else if(it.type=="Member") flag = 2;
//                                    else flag = 3;
//                                }
//                            }
//                        }
//                    }
//                    override fun onCancelled(databaseError: DatabaseError) {
//                        // Handle errors
//                    }
//                })
//                signInWithEmailAndPassword(email, password)
//                if(flag==1) {navController.navigate("home")}
//                else if(flag==2){navController.navigate("homemember")}
//                else Toast.makeText(context,"Enter correct password", Toast.LENGTH_SHORT).show()
