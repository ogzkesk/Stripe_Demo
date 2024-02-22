package com.ogzkesk.payment.stripe

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentService {

    @POST(PAYMENT_ENDPOINT)
    suspend fun getDetails(
        @Body postModel: Map<String,String>
    ): ResponseBody


    companion object {
        const val PAYMENT_ENDPOINT = "payment-sheet"
        const val BASE_URL = "http://10.0.2.2:8080/"
    }
}