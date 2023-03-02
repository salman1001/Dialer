package com.hoogsoftware.dialer.resources.cache
import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Cache(
    context: Context) {
    private val applicationContext=context.applicationContext
    private val datastore: DataStore<Preferences> = applicationContext.createDataStore(name="my_data_store")

    val authToken:Flow<Long?>
        get()=datastore.data.map { pref->pref[KEY_AUTH] }
    suspend fun saveToken(authToken:Long){
        datastore.edit {
                pref->pref[KEY_AUTH]=authToken

        }

    }
    companion object{
        private val KEY_AUTH= preferencesKey<Long>("key_auth")
    }
}