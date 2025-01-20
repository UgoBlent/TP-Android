import com.example.tp_android.Entity.Product
import retrofit2.Call

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("products/1")
    suspend fun getProduct(): Product

    @GET("products")
    suspend fun getAllProducts(): List<Product>

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(@Path("category") category: String): List<Product>

    @GET("products/categories")
    suspend fun getCategories(): List<String>


//    @GET("products/categories/1")
//    suspend fun getProductByCatId(): Product
}
