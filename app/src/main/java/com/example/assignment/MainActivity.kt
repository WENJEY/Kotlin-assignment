package com.example.assignment

import android.R.color.white
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.assignment.ui.theme.AssignmentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AssignmentTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LogoScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun LogoScreen (modifier : Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ){
        Logo()
    }

}

@Composable
fun Logo(){
    var logoAnimation by remember { mutableStateOf(false) } //initialize the value is false

    val alphaAnimation = animateFloatAsState(
        targetValue = if (logoAnimation) 1f else 0f, // false -> 0f(disappear) , true -> 1f(appear)
        animationSpec = tween(durationMillis = 1000) // time to appear the logo
    )

    LaunchedEffect(Unit) {
        logoAnimation = true
    }
    Image(
        painter = painterResource(R.drawable.logo) ,
        contentDescription =  "Logo",
        modifier = Modifier
            .size(300.dp)
            .alpha(alphaAnimation.value) // alpha animation
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AssignmentTheme {
        LogoScreen()
    }
}