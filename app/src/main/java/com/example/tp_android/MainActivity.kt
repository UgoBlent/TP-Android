package com.example.tp_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageProduit() {
    var drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var products by remember { mutableStateOf<List<Product>?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Charger les produits selon la catégorie
    LaunchedEffect(selectedCategory) {
        coroutineScope.launch {
            try {
                products = if (selectedCategory == null) {
                    RetrofitInstance.api.getAllProducts()
                } else {
                    RetrofitInstance.api.getProductsByCategory(selectedCategory!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight()
                    .background(Color.White.copy(alpha = 0.9f)) // Fond blanc avec légère transparence
            ) {
                DrawerContent(
                    onAllProductsClick = {
                        selectedCategory = null
                        coroutineScope.launch { drawerState.close() } // Fermer le menu après sélection
                    },
                    onCategorySelected = { category ->
                        selectedCategory = category
                        coroutineScope.launch { drawerState.close() } // Fermer le menu après sélection
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                // Barre avec le titre + le bouton du drawer
                TopAppBar(
                    title = { Text("La Boutique du TP d'Android") },
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            content = { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    if (products != null) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            items(products!!) { product ->
                                ProductCard(product = product)
                            }
                        }
                    } else {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        )
    }

}


// Une carte par produit, avec l'image, le titre et le prix du produit
@Composable
fun ProductCard(product: Product) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { showDialog = true }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(product.image),
                contentDescription = product.title,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 8.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "${product.price} €",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    // Affichage du pop-up
    if (showDialog) {
        ProductDetailDialog(product = product, onDismiss = { showDialog = false })
    }
}


// Pop up pour afficher les information d'un produit lorsqu'on clique sur sa ProductCard
@Composable
fun ProductDetailDialog(product: Product, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = product.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .height(500.dp)
                    .fillMaxWidth()
            ) {
                // Image du produit
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(product.image),
                        contentDescription = product.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Description du produit, scrollable si trop long
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Prix : ${product.price} €",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Catégorie : ${product.category}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Description :",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = "Fermer")
            }
        }
    )
}



// Drawer pour le menu latéral
@Composable
fun DrawerContent(
    onAllProductsClick: () -> Unit,
    onCategorySelected: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var categories by remember { mutableStateOf<List<String>?>(null) }

    // Charger les catégories depuis l'API
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                categories = RetrofitInstance.api.getCategories()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Menu",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Tous les produits
        TextButton(onClick = onAllProductsClick) {
            Text(text = "Tous les produits", style = MaterialTheme.typography.bodyLarge)
        }

        // Catégories
        if (categories != null) {
            Text(text = "Catégories", style = MaterialTheme.typography.titleMedium)
            categories!!.forEach { category ->
                TextButton(onClick = { onCategorySelected(category) }) {
                    Text(text = category.capitalize() // déprécié, à modif
                        , style = MaterialTheme.typography.bodyLarge)
                }
            }
        } else {
            CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
        }
    }
}

