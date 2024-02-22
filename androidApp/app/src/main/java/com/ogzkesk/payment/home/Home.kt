package com.ogzkesk.payment.home

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.ogzkesk.payment.stripe.PaymentHelper
import com.stripe.android.paymentsheet.rememberPaymentSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun NavGraphBuilder.home(paymentHelper: PaymentHelper, navHostController: NavHostController) {
    composable("home") {
        Home(paymentHelper, navHostController)
    }
}

@Composable
fun Home(paymentHelper: PaymentHelper, navController: NavController) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val viewModel: HomeViewModel = viewModel()
    val pageScrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    val paymentSheet = rememberPaymentSheet(viewModel::onPaymentSheetResult)

    LaunchedEffect(key1 = viewModel.event) {
        viewModel.event.collect { event ->
            when (event) {
                is HomeViewModel.Event.Success -> {
                    navController.navigate("info")
                }

                is HomeViewModel.Event.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    HomeScaffold(
        uiState = uiState,
        pageScrollState = pageScrollState,
        onPriceTextChanged = viewModel::onPriceChanged,
        onCheckoutClicked = { amount ->

            coroutineScope.launch {
                paymentHelper.pay(
                    context = context,
                    amount = amount,
                    currency = "eur",
                    onFailure = {
                        launch(Dispatchers.Main) { context.showToast(it.message) }
                    },
                    onSuccess = { clientSecret, customerConfig ->
                        viewModel.presentPaymentSheet(
                            paymentSheet,
                            customerConfig,
                            clientSecret
                        )
                    }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScaffold(
    pageScrollState: ScrollState,
    uiState: HomeViewModel.UiState<String>,
    onPriceTextChanged: (String) -> Unit,
    onCheckoutClicked: (amount: String) -> Unit,
) {

    Scaffold(
        topBar = {
            AnimatedVisibility(visible = pageScrollState.isScrollInProgress) {
                TopAppBar(title = { Text(text = "TopBar") })
            }
        }
    ) { padd ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(pageScrollState)
                .padding(padd),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = uiState.priceText,
                onValueChange = onPriceTextChanged,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Button(onClick = { onCheckoutClicked(uiState.priceText) }) {
                Text("Checkout")
            }
        }
    }
}


fun Context.showToast(message: String?){
    if(message == null) return
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
}