package com.example.meal_management

import android.graphics.Color.rgb
import android.os.Build
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.meal_management.ui.theme.Purple40

@Composable
fun Add_Cards(navController: NavController) {
    val foregroundColor = Color(rgb(238, 245, 255))
    val txtColor = Color.Red
    Column(
        modifier = Modifier
            .fillMaxWidth()
           // .border(1.dp, Color.Blue, RoundedCornerShape(8.dp))
            .background(Color.Transparent, shape = RoundedCornerShape(8.dp))
    ) {
        //Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.padding(horizontal = 10.dp).padding(top = 10.dp)) {
            Surface(
                modifier = Modifier
                    .width(100.dp)
                    .height(110.dp)
                    .clickable(onClick = { navController.navigate("memlist") })
                    .clip(RoundedCornerShape(8.dp)),
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.Transparent)
                    // .border(1.dp, Color.Red, RoundedCornerShape(8.dp)),
                    , verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = (painterResource(id = R.drawable.member)),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(60.dp)
                            .height(60.dp)
                            .padding(2.dp)
                    )
                    Text(
                        text = "Members",
                        color = Color.Red,
                        modifier = Modifier
                            .padding(2.dp),
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Cursive
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Surface(
                modifier = Modifier
                    .width(100.dp)
                    .height(110.dp)
                    .clickable(onClick = { navController.navigate("chat") })
                    .clip(RoundedCornerShape(8.dp)),
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.Transparent),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = (painterResource(id = R.drawable.big_mes)),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(60.dp)
                            .height(60.dp)
                            .padding(2.dp)
                    )
                    Text(
                        text = "Message",
                        color = Color.Red,
                        modifier = Modifier
                            .padding(4.dp),
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Cursive
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Surface(
                modifier = Modifier
                    .width(100.dp)
                    .height(110.dp)
                    .clickable(onClick = { navController.navigate("addmem") })
                    .clip(RoundedCornerShape(8.dp)),

                ) {
                Column(
                    modifier = Modifier
                        .background(Color.Transparent),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = (painterResource(id = R.drawable.add_member)),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(60.dp)
                            .height(60.dp)
                            .padding(2.dp)
                    )
                    Text(
                        text = "Add",
                        color = Color.Red,
                        modifier = Modifier
                            .padding(4.dp),
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Cursive
                    )
                }
            }
        }
        Row(modifier = Modifier.padding(horizontal = 10.dp).padding(top = 10.dp)) {
            Surface(
                modifier = Modifier
                    .width(100.dp)
                    .height(110.dp)
                    .clickable(onClick = { navController.navigate("addmeal") })
                    .clip(RoundedCornerShape(8.dp)),
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.Transparent)
                    ,verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = (painterResource(id = R.drawable.mead)),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(60.dp)
                            .height(60.dp)
                            .padding(2.dp)
                    )
                    Text(
                        text = "Meal Add",
                        color = txtColor,
                        modifier = Modifier
                            .padding(2.dp),fontSize = 20.sp,fontFamily = FontFamily.Cursive
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Surface(
                modifier = Modifier
                    .width(100.dp)
                    .height(110.dp)
                    .clickable(onClick = { navController.navigate("deposit") })
                    .clip(RoundedCornerShape(8.dp)),
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.Transparent),
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
                        color = txtColor,
                        modifier = Modifier
                            .padding(4.dp),fontSize = 20.sp,fontFamily = FontFamily.Cursive
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Surface(
                modifier = Modifier
                    .width(100.dp)
                    .height(110.dp)
                    .clickable(onClick = { navController.navigate("shopping") })
                    .clip(RoundedCornerShape(8.dp)),

            ) {
                Column(
                    modifier = Modifier
                        .background(Color.Transparent)
                        ,
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = (painterResource(id = R.drawable.sh)),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(60.dp)
                            .height(60.dp)
                            .padding(2.dp)
                    )
                    Text(
                        text = "Shopping",
                        color = txtColor,
                        modifier = Modifier
                            .padding(4.dp),fontSize = 20.sp,fontFamily = FontFamily.Cursive
                    )
                }
            }
        }
        Row(modifier = Modifier.padding(10.dp)) {
            Surface(
                modifier = Modifier
                    .width(100.dp)
                    .height(120.dp)
                    .clickable(onClick = { navController.navigate("change") })
                    .clip(RoundedCornerShape(8.dp)),
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.Transparent, shape = RoundedCornerShape(8.dp))
                        ,
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = (painterResource(id = R.drawable.change)),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(60.dp)
                            .height(60.dp)
                            .padding(2.dp)
                    )
                    Text(
                        text = "Change",
                        color = txtColor,
                        modifier = Modifier
                            .padding(horizontal = 2.dp),fontSize = 20.sp,fontFamily = FontFamily.Cursive

                    )
                    Text(
                        text = "Manager",
                        color = txtColor,
                        modifier = Modifier
                            .padding(horizontal = 2.dp),fontSize = 20.sp,fontFamily = FontFamily.Cursive

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
                    modifier = Modifier.
                        background(Color.Transparent)
                        ,
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = (painterResource(id = R.drawable.de3)),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(60.dp)
                            .height(60.dp)
                            .padding(2.dp)
                    )
                    Text(
                        text = "Deposit",
                        color = txtColor,
                        modifier = Modifier
                            .padding(horizontal = 2.dp),fontSize = 20.sp,fontFamily = FontFamily.Cursive

                    )
                    Text(
                        text = "History",
                        color =txtColor,
                        modifier = Modifier
                            .padding(horizontal = 2.dp),fontSize = 20.sp,fontFamily = FontFamily.Cursive

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
                        .background(Color.Transparent)
                        ,
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = (painterResource(id = R.drawable.shop_history)),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(60.dp)
                            .height(60.dp)
                            .padding(2.dp)
                    )
                    Text(
                        text = "Shopping",
                        color = txtColor,
                        modifier = Modifier
                            .padding(horizontal = 2.dp),fontSize = 20.sp,fontFamily = FontFamily.Cursive
                    )
                    Text(
                        text = "History",
                        color = txtColor,
                        modifier = Modifier
                            .padding(horizontal = 2.dp),fontSize = 20.sp,fontFamily = FontFamily.Cursive
                    )
                }
            }
        }
    }
}

