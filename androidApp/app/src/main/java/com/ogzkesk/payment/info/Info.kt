package com.ogzkesk.payment.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.info(navHostController: NavHostController) {
    composable("info") {
        Info(navHostController)
    }
}


@Composable
fun Info(navController: NavController) {

    Scaffold { padd ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padd),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Payment Success!")
            Button(onClick = { navController.popBackStack() }) {
                Text(text = "Back")
            }
        }
    }
}

