package qrsimple.franz.com.qrsimple

import android.app.Application
import qrsimple.franz.com.qrsimple.core.billing.BillingManager
import qrsimple.franz.com.qrsimple.core.datastore.UserPreferencesDataStore
import qrsimple.franz.com.qrsimple.data.local.QrDatabase
import qrsimple.franz.com.qrsimple.data.repository.QrHistoryRepositoryImpl
import qrsimple.franz.com.qrsimple.domain.repository.QrHistoryRepository
import qrsimple.franz.com.qrsimple.domain.repository.UserPreferencesRepository

class QRSimpleApplication : Application() {

    val database by lazy { QrDatabase.getDatabase(this) }
    val qrRepository: QrHistoryRepository by lazy {
        QrHistoryRepositoryImpl(database.qrDao())
    }
    val preferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesDataStore(this)
    }
    val billingManager: BillingManager by lazy {
        BillingManager(this, preferencesRepository)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: QRSimpleApplication
            private set
    }
}
