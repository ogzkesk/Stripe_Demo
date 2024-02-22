package com.ogzkesk.payment.stripe

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

class PaymentHelper() {


    private var paymentService: PaymentService? = null


    fun initService() = apply {
        paymentService = Retrofit.Builder()
            .baseUrl(PaymentService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PaymentService::class.java)
    }

    suspend fun pay(
        context: Context,
        amount: String,
        currency: String,
        onSuccess: (clientSecret: String, customerConfig: PaymentSheet.CustomerConfiguration) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        withContext(Dispatchers.IO) {
            try {

                val body: MutableMap<String, String> = mutableMapOf()
                body["amount"] = amount
                body["currency"] = currency

                val result = paymentService?.getDetails(body) ?: return@withContext

                result.use {
                    val responseJson = JSONObject(it.string())
                    val publishableKey = responseJson.getString("publishableKey")
                    val id = responseJson.getString("customer")
                    val ephemeralKey = responseJson.getString("ephemeralKey")
                    PaymentConfiguration.init(context, publishableKey)

                    val clientSecret = responseJson.getString("paymentIntent")
                    val customerConfig = PaymentSheet.CustomerConfiguration(id, ephemeralKey)
                    onSuccess(clientSecret, customerConfig)
                    println(responseJson)
                }
            } catch (e: HttpException) {
                println("HttpException ${e.code()} ${e.message}")
                onFailure(e)
            } catch (e: Exception) {
                e.printStackTrace()
                onFailure(e)
            }
        }
    }
}