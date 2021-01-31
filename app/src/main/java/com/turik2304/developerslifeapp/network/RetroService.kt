package com.turik2304.developerslifeapp.network

import com.turik2304.developerslifeapp.PostData
//import com.turik2304.developerslifeapp.PostList
import retrofit2.Call
import retrofit2.http.GET

interface RetroService {

    @GET("random?json=true")
    fun getLatestData(): Call<PostData>
}