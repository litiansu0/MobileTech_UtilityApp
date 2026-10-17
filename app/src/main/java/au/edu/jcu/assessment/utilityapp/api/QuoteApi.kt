package au.edu.jcu.assessment.utilityapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class QuoteResponse(
    val q: String, // The quote text
    val a: String // The author name
)

interface QuoteApi {
    @GET("api/quotes")
    //non-blocking function to perform asynchronous, long-running operations
    suspend fun getQuotes(): List<QuoteResponse>
}

// RetrofitInstance is an object that creates a single, static instance (a singleton)
object RetrofitInstance {
    val api: QuoteApi by lazy {//defers the initialization of the variable until it is first accessed.
        Retrofit.Builder()
            .baseUrl("https://zenquotes.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuoteApi::class.java)
    }
}