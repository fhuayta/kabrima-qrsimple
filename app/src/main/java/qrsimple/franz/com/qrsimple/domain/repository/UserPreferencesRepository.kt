package qrsimple.franz.com.qrsimple.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val hasSupportedProject: Flow<Boolean>
    suspend fun setHasSupportedProject(supported: Boolean)
}

