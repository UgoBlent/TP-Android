package com.example.tp_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
    var product by remember { mutableStateOf<Product?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val apiProduct = RetrofitInstance.api.getProduct()
                product = apiProduct
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    if (product != null) {
        val productValue = product!!
        Text(text = "${productValue.title}\nDescription: ${productValue.description}\nPrix: ${productValue.price} €")

        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(productValue.image)
                    .crossfade(true)
                    .build()
            ),
            contentDescription = if (productValue.title != null) productValue.title else "Image du produit",
            modifier = Modifier
                .size(150.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
    } else {
        Text(text = "Chargement des données...")
    }
}
