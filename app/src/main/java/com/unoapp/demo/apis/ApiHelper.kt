package com.unoapp.demo.apis

import com.unoapp.demo.model.ResponseListUsers
import retrofit2.Response
import retrofit2.http.GET

interface ApiHelper {
    @GET("/api/users?page=2")
    suspend fun getAllUsers(): Response<ResponseListUsers>

   /* @POST(login_url)
    fun doLogin(@Body hashMap: HashMap<String, Any>): Observable<LoginResponse>*/
}