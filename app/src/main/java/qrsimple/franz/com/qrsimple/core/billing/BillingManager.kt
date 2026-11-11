package qrsimple.franz.com.qrsimple.core.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import qrsimple.franz.com.qrsimple.R
import qrsimple.franz.com.qrsimple.domain.repository.UserPreferencesRepository

data class TipOption(
    val id: String,
    val titleRes: Int,
    val descriptionRes: Int,
    val emoji: String,
    val fallbackPrice: String,
    val formattedPrice: String = fallbackPrice,
    val productDetails: ProductDetails? = null
)

class BillingManager(
    private val context: Context,
    private val userPreferencesRepository: UserPreferencesRepository
) : PurchasesUpdatedListener {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    companion object {
        const val PRODUCT_COFFEE = "tip_coffee"
        const val PRODUCT_LUNCH = "tip_lunch"
        const val PRODUCT_SUPPORTER = "tip_supporter"

        val DEFAULT_TIPS = listOf(
            TipOption(
                id = PRODUCT_COFFEE,
                titleRes = R.string.tip_coffee_title,
                descriptionRes = R.string.tip_coffee_desc,
                emoji = "☕",
                fallbackPrice = "$1.49"
            ),
            TipOption(
                id = PRODUCT_LUNCH,
                titleRes = R.string.tip_lunch_title,
                descriptionRes = R.string.tip_lunch_desc,
                emoji = "🍕",
                fallbackPrice = "$4.99"
            ),
            TipOption(
                id = PRODUCT_SUPPORTER,
                titleRes = R.string.tip_supporter_title,
                descriptionRes = R.string.tip_supporter_desc,
                emoji = "🚀",
                fallbackPrice = "$9.99"
            )
        )
    }

    private val _tipOptions = MutableStateFlow(DEFAULT_TIPS)
    val tipOptions: StateFlow<List<TipOption>> = _tipOptions.asStateFlow()

    private val _purchaseSuccessEvent = MutableSharedFlow<String>()
    val purchaseSuccessEvent: SharedFlow<String> = _purchaseSuccessEvent.asSharedFlow()

    private val _purchaseErrorEvent = MutableSharedFlow<String>()
    val purchaseErrorEvent: SharedFlow<String> = _purchaseErrorEvent.asSharedFlow()

    private val billingClient: BillingClient by lazy {
        BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
            .build()
    }

    fun startConnection() {
        if (billingClient.isReady) {
            queryProducts()
            return
        }

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProducts()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Reconnect when needed
            }
        })
    }

    private fun queryProducts() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_COFFEE)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_LUNCH)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_SUPPORTER)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, queryProductDetailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val detailsMap = queryProductDetailsResult.productDetailsList.associateBy { it.productId }
                _tipOptions.update { currentList ->
                    currentList.map { option ->
                        val details = detailsMap[option.id]
                        if (details != null) {
                            val price = details.oneTimePurchaseOfferDetails?.formattedPrice ?: option.fallbackPrice
                            option.copy(
                                formattedPrice = price,
                                productDetails = details
                            )
                        } else {
                            option
                        }
                    }
                }
            }
        }
    }

    fun launchBillingFlow(activity: Activity, productId: String) {
        val option = _tipOptions.value.find { it.id == productId }
        val productDetails = option?.productDetails

        if (productDetails == null) {
            scope.launch {
                _purchaseErrorEvent.emit(context.getString(R.string.tip_play_store_unavailable))
            }
            return
        }

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val result = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            scope.launch {
                _purchaseErrorEvent.emit(context.getString(R.string.tip_error_generic))
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode != BillingClient.BillingResponseCode.USER_CANCELED) {
            scope.launch {
                _purchaseErrorEvent.emit(context.getString(R.string.tip_error_generic))
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            val consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.consumeAsync(consumeParams) { _, _ ->
                scope.launch {
                    userPreferencesRepository.setHasSupportedProject(true)
                    _purchaseSuccessEvent.emit(context.getString(R.string.tip_thanks_message))
                }
            }
        }
    }
}

