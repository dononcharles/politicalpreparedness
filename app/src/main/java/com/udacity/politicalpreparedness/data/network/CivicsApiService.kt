package com.udacity.politicalpreparedness.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.politicalpreparedness.data.network.jsonadapter.DateAdapter
import com.udacity.politicalpreparedness.data.network.jsonadapter.ElectionAdapter
import com.udacity.politicalpreparedness.data.network.models.ElectionResponse
import com.udacity.politicalpreparedness.data.network.models.RepresentativeResponse
import com.udacity.politicalpreparedness.data.network.models.VoterInfoResponse
import com.udacity.politicalpreparedness.data.network.models.dto.VoteInfoQueryDto
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://www.googleapis.com/civicinfo/v2/"

private val moshi = Moshi.Builder()
    .add(ElectionAdapter())
    .add(DateAdapter())
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(CivicsHttpClient.getClient())
    .baseUrl(BASE_URL)
    .build()

/**
 *  Documentation for the Google Civics API Service can be found at https://developers.google.com/civic-information/docs/v2
 */

interface CivicsApiService {
    // Add elections API Call
    @GET("elections")
    suspend fun getElections(): ElectionResponse

    // Add voterinfo API Call
    @GET("voterinfo")
    suspend fun getVoteInfo(@Body query: VoteInfoQueryDto): VoterInfoResponse

    // Add representatives API Call BY ADDRESS
    @GET("representatives")
    suspend fun getRepresentativesByAddress(@Query("address", encoded = true) address: String): RepresentativeResponse

    // Add representatives API Call BY DIVISION
     @GET("representatives/ocdId")
     suspend fun getRepresentativesByDivision(@Body query: VoteInfoQueryDto): VoterInfoResponse
}

object CivicsApi {
    val retrofitService: CivicsApiService by lazy {
        retrofit.create(CivicsApiService::class.java)
    }
}
