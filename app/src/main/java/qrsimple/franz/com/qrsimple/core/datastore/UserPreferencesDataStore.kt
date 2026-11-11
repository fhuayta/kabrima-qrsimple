package qrsimple.franz.com.qrsimple.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import qrsimple.franz.com.qrsimple.domain.repository.UserPreferencesRepository
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "qrsimple_preferences")

class UserPreferencesDataStore(private val context: Context) : UserPreferencesRepository {

    private object PreferencesKeys {
        val HAS_SUPPORTED_PROJECT = booleanPreferencesKey("has_supported_project")
    }

    override val hasSupportedProject: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.HAS_SUPPORTED_PROJECT] ?: false
        }

    override suspend fun setHasSupportedProject(supported: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_SUPPORTED_PROJECT] = supported
        }
    }
}

