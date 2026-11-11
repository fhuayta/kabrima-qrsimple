package qrsimple.franz.com.qrsimple.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import qrsimple.franz.com.qrsimple.data.local.entity.QrEntity

@Dao
interface QrDao {

    @Query("SELECT * FROM qr_history ORDER BY timestamp DESC")
    fun getAll(): Flow<List<QrEntity>>

    @Query("SELECT * FROM qr_history WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavorites(): Flow<List<QrEntity>>

    @Query("SELECT * FROM qr_history WHERE title LIKE '%' || :query || '%' OR rawValue LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun search(query: String): Flow<List<QrEntity>>

    @Query("SELECT * FROM qr_history WHERE type = :type ORDER BY timestamp DESC")
    fun getByType(type: String): Flow<List<QrEntity>>

    @Query("SELECT * FROM qr_history WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): QrEntity?

    @Query("SELECT * FROM qr_history WHERE rawValue = :rawValue LIMIT 1")
    suspend fun findByRawValue(rawValue: String): QrEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: QrEntity): Long

    @Update
    suspend fun update(entity: QrEntity)

    @Delete
    suspend fun delete(entity: QrEntity)

    @Query("DELETE FROM qr_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM qr_history")
    suspend fun clearAll()

    @Query("UPDATE qr_history SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("UPDATE qr_history SET title = :title WHERE id = :id")
    suspend fun updateTitle(id: Long, title: String)
}

