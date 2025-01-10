import com.example.tp_android.Entity.Product
import retrofit2.Call

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("products/1")
    suspend fun getProduct(): Product

    @GET("products")
    suspend fun getProducts(): List<Product>

//    @GET("products/categories/1")
//    suspend fun getProductByCatId(): Product
}
