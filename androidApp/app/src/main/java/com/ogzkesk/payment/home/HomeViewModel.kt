package com.ogzkesk.payment.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<UiState<String>> = MutableStateFlow(UiState.initial())
    val uiState: StateFlow<UiState<String>> = _uiState

    private val _event = MutableSharedFlow<Event>()
    val event: SharedFlow<Event> = _event


    fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        viewModelScope.launch {
            when(paymentSheetResult) {
                is PaymentSheetResult.Canceled -> {
                    println("Cancelled")
                }
                is PaymentSheetResult.Failed -> {
                    println("Error: ${paymentSheetResult.error}")
                    _event.emit(Event.Error(paymentSheetResult.error.message ?: ""))
                }
                is PaymentSheetResult.Completed -> {
                    // Display for example, an order confirmation screen
                    println("Completed")
                    _event.emit(Event.Success)
                }
            }
        }
    }

    fun presentPaymentSheet(
        paymentSheet: PaymentSheet,
        customerConfig: PaymentSheet.CustomerConfiguration,
        paymentIntentClientSecret: String
    ) {
        paymentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret,
            PaymentSheet.Configuration(
                merchantDisplayName = "My merchant name",
                customer = customerConfig,
                // Set `allowsDelayedPaymentMethods` to true if your business handles
                // delayed notification payment methods like US bank accounts.
                allowsDelayedPaymentMethods = true
            )
        )
    }


    fun onPriceChanged(s: String) {
        _uiState.update { it.copy(priceText = s) }
    }

    @Stable
    data class UiState<T>(
        val priceText: String,
        val isLoading: Boolean,
        val error: Throwable?,
        val data: T?,
    ) {
        companion object {
            fun <T> initial(): UiState<T> {
                return UiState(
                    priceText = "",
                    isLoading = false,
                    error = null,
                    data = null
                )
            }
        }
    }

    sealed interface Event{
        data class Error(val message: String): Event
        data object Success: Event
    }
}

