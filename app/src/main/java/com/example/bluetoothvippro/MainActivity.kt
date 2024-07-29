package com.example.bluetoothvippro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.bluetoothvippro.ui.theme.BluetoothVipProTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BluetoothVipProTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Android")
                }
            }
        }
    }
}
@Composable
private fun TextCount(textCount:String){
    Text(text = "$textCount")
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    var count by rememberSaveable { mutableStateOf(0) }
   Column (horizontalAlignment = Alignment.CenterHorizontally){
       Text(text = "Trang chủ")
       TextCount(count.toString())
       Button(onClick = { count++ }) {
           Text(text = "Click me")
       }
   }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BluetoothVipProTheme {
        Greeting("Android")
    }
}