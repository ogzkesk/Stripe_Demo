package com.ogzkesk.payment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ogzkesk.payment.home.home
import com.ogzkesk.payment.info.info
import com.ogzkesk.payment.stripe.PaymentHelper
import com.ogzkesk.payment.ui.theme.PaymentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val paymentHelper = PaymentHelper()
            .initService()

        setContent {
            val navController = rememberNavController()
            PaymentTheme {
                NavHost(navController = navController, startDestination = "home"){
                    home(paymentHelper,navController)
                    info(navController)
                }
            }
        }
    }
}