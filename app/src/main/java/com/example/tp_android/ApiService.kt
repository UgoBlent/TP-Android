import com.example.tp_android.Entity.Product
import retrofit2.Call

import retrofit2.http.GET

interface ApiService {
    @GET("products/1")
    suspend fun getProduct(): Product

    @GET("products/categories")
    suspend fun getProductByCat(): Product

    @GET("products/categories/1")
    suspend fun getProductByCatId(): Product
}
