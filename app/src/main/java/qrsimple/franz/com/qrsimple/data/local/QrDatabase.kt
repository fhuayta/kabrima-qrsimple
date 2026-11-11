package qrsimple.franz.com.qrsimple.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import qrsimple.franz.com.qrsimple.data.local.dao.QrDao
import qrsimple.franz.com.qrsimple.data.local.entity.QrEntity

@Database(entities = [QrEntity::class], version = 1, exportSchema = false)
abstract class QrDatabase : RoomDatabase() {

    abstract fun qrDao(): QrDao

    companion object {
        @Volatile
        private var INSTANCE: QrDatabase? = null

        fun getDatabase(context: Context): QrDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QrDatabase::class.java,
                    "qr_simple_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

