package com.example.tp_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import coil.compose.rememberImagePainter

import androidx.compose.runtime.Composable

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import com.example.tp_android.Entity.Product


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PageProduit()
        }
    }
}

@Composable
fun PageProduit() {
    val Produit = remember { mutableStateOf<Product?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val APIRecupProduit = RetrofitInstance.api.getProduct()
            Produit.value = APIRecupProduit
        }
    }



    if (Produit.value != null) {
        val product = Produit.value!!
        Text(text = "${product.title}\nDescription: ${product.description}\nPrix: ${product.price} €")
        Image(
            painter = rememberAsyncImagePainter(model = ImageRequest.Builder(LocalContext.current)
                .data("https://example.com/image.jpg")),
            contentDescription = product.title,
            modifier = Modifier.size(150.dp).fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
    } else {
        Text(text = "Chargement des données...")
    }
}