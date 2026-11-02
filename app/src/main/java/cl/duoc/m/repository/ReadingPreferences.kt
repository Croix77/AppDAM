package cl.duoc.m.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.readingStore by preferencesDataStore(name="reading")
class ReadingPreferences(private val context: Context) {
    private val key=booleanPreferencesKey("large_text")
    val largeText=context.readingStore.data.map {it[key] ?: false}
    suspend fun save(value: Boolean) {context.readingStore.edit {it[key]=value}}
}
