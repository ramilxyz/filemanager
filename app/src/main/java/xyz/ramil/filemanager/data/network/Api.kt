package xyz.ramil.filemanager.data.network

import retrofit2.http.GET
import retrofit2.http.Headers

interface Api {
    @Headers("Content-Type: application/json")
    @GET("/source/snapshot")
    suspend fun getFiles(
    ): org.jsoup.nodes.Document
}