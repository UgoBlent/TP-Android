import com.example.tp_android.Entity.Product
import retrofit2.Call

import retrofit2.http.GET

interface ApiService {
    @GET("products/1")
    suspend fun getProduct(): Product
}