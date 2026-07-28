package com.sk.facerecognition

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
//import com.sk.custom_camera.ui.CameraScreen
import com.sk.facerecognition.ui.theme.FaceRecognitionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FaceRecognitionTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    /*CameraScreen(
                        modifier = Modifier.padding(innerPadding),
                        onImageCaptured = { uri ->
                                          *//* handle saved photo *//*
                            Toast.makeText(this,"Captured successfully", Toast.LENGTH_SHORT).show()
                                          },
                        onError = { exception -> *//* handle failure *//* }
                    )*/
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FaceRecognitionTheme {
        Greeting("Android")
    }
}