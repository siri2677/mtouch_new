package com.example.cleanarchitech_text_0506.view.ui.theme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cleanarchitech_text_0506.R

class recyler : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CleanArchitech_text_0506Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DynamicList()
                }
            }
        }
    }
}

data class Item(val name: String, val price: Int)

@Composable
fun DynamicList() {
    val items = remember { mutableStateListOf<Item>() } // Mutable list to hold the items
    // Function to add items to the list dynamically
    fun addItem() {
        val newItem = Item("Item ${items.size + 1}", (items.size + 1) * 10)
        items.add(newItem)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.white))
            .padding(16.dp)
    ) {
        Button(onClick = { addItem() }) {
            Text("Add Item")
        }
        LazyColumn {
            items(items = items) { item ->
                Text("Name: ${item.name}, Price: $${item.price}")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    CleanArchitech_text_0506Theme {
        DynamicList()
    }
}