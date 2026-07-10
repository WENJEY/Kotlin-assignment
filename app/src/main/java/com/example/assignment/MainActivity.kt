package com.example.assignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.assignment.R.font.archivo_black_regular
import com.example.assignment.ui.theme.AssignmentTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

val ArchivoBlack = FontFamily(
    Font(archivo_black_regular)
)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AssignmentTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyAppNavHost(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MyAppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "logo",
        modifier = modifier
    ) {
        composable("logo") { LogoScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable ("register"){ RegisterScreen(navController)}
    }
}

@Composable
fun RegisterScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun LoginScreen(navController:NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.login),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE2F0D9))
                        .border(2.0.dp, Color.Black, CircleShape)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Welcome",
                        fontSize = 36.sp,
                        fontFamily = FontFamily.Serif,
                        color = Color(0xFF1E4620),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Login!",
                        fontSize = 36.sp,
                        fontFamily = FontFamily.Serif,
                        color = Color(0xFF1E4620),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1.5f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight() // set height based on content size
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0x99FFFFFF))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Gmail",
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontFamily = ArchivoBlack,
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("abc123@example.com", color = Color(0xFF4A5D4E)) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color(0xFF1E4620),
                            unfocusedTextColor = Color(0xFF1E4620)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Password",
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontFamily = ArchivoBlack,
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("*********", color = Color(0xFF4A5D4E)) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color(0xFF1E4620),
                            unfocusedTextColor = Color(0xFF1E4620)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE2F0D9)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .border(1.5.dp, Color(0xFF1E4620), RoundedCornerShape(50))
                    ) {
                        Text(
                            text = "Login",
                            color = Color(0xFF1E4620),
                            fontFamily = FontFamily.Serif,
                            fontSize = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row {
                        Text(
                            text = "Don't have account? ",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Sign Up",
                            color = Color(0xFF0055FF),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier
                                .clickable {navController.navigate("register")}

                        )

                    }
                }
            }

            Spacer(modifier = Modifier.weight(1.2f))
        }
    }
}

@Composable
fun LogoScreen(navController : NavHostController, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Logo(navController = navController)
    }
}

@Composable
fun Logo(navController: NavHostController){
    var logoAnimation by remember { mutableStateOf(false) } //initialize the value is false

    val alphaAnimation = animateFloatAsState(
        targetValue = if (logoAnimation) 1f else 0f, // false -> 0f(invisible) , true -> 1f(fully visible)
        animationSpec = tween(durationMillis = 1000) // time to appear the logo
    )

    LaunchedEffect(Unit) {
        logoAnimation = true

        delay(2500.milliseconds)

        navController.navigate("login") {
            popUpTo("logo") { inclusive = true } // delete the logo screen
        }
    }

    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Logo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(280.dp)
                .alpha(alphaAnimation.value)
                .offset(y = (-30).dp) // move up
        )
        Text(
            text = "Welcome to use\nGovernment HR\nConsultation (GRHC)",
            fontSize = 18.sp,
            fontFamily = ArchivoBlack,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.offset (y = (-30).dp)
        )
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AssignmentTheme {
        LoginScreen(navController = rememberNavController())
    }
}