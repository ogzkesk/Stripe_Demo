package com.example.routes

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.stripe.Stripe
import com.stripe.model.Customer
import com.stripe.model.EphemeralKey
import com.stripe.model.PaymentIntent
import com.stripe.param.CustomerCreateParams
import com.stripe.param.EphemeralKeyCreateParams
import com.stripe.param.PaymentIntentCreateParams
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.lang.reflect.Type

fun Route.paymentSheet(){
    post("/payment-sheet") {
        val gson = Gson()

        Stripe.apiKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc"

        val result = call.receive<Map<String, String>>()
        val amount = result["amount"]
        val currency = result["currency"]

        if( amount == null || currency == null){
            val errorResponse = mutableMapOf("error" to "Something went wrong")
             call.respondText(
                contentType = ContentType.parse("application/json"),
                status = HttpStatusCode.BadRequest,
                text = gson.toJson(errorResponse)
            )
            return@post
        }

        val customerParams: CustomerCreateParams = CustomerCreateParams.builder()
            .build()
        val customer: Customer = Customer.create(customerParams)

        val ephemeralKeyParams: EphemeralKeyCreateParams = EphemeralKeyCreateParams.builder()
            .setStripeVersion("2023-10-16") // set newer if available on doc.
            .setCustomer(customer.id)
            .build()
        val ephemeralKey: EphemeralKey = EphemeralKey.create(ephemeralKeyParams)

        val paymentIntentParams: PaymentIntentCreateParams = PaymentIntentCreateParams.builder()
            .setAmount(amount.toLong())
            .setCurrency(currency)
            .setCustomer(customer.id)
            .setAutomaticPaymentMethods(
                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                    .setEnabled(true)
                    .build()
            )
            .build()
        val paymentIntent: PaymentIntent = PaymentIntent.create(paymentIntentParams)

        val responseData: MutableMap<String, String> = mutableMapOf()
        responseData["paymentIntent"] = paymentIntent.clientSecret
        responseData["ephemeralKey"] = ephemeralKey.secret
        responseData["customer"] = customer.id
        responseData["publishableKey"] = "pk_test_TYooMQauvdEDq54NiTphI7jx"

        val response = gson.toJson(responseData)

        call.respondText(
            contentType = ContentType.parse("application/json"),
            status = HttpStatusCode.OK,
            text = response
        )
    }
}