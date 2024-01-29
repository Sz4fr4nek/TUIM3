package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.api.ApiLogin
import com.example.myapplication.api.ApiRejestacja
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        val buttonClickLogowanie = findViewById<Button>(R.id.buttonLogowanie)
        buttonClickLogowanie.setOnClickListener {
            val intent = Intent(this, ApiLogin::class.java)
            startActivity(intent)
        }

        val buttonClickRejestacja = findViewById<Button>(R.id.buttonRejestarcja)
        buttonClickRejestacja.setOnClickListener {
            val intent = Intent(this, ApiRejestacja::class.java)
            startActivity(intent)
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
    MyApplicationTheme {
        Greeting("Android")
    }
}