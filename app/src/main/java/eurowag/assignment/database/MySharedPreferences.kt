package eurowag.assignment.database

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val SHARED_PREFERENCES_NAME = "MyPrefs"
        private const val SHARED_PREFERENCES_INTERVAL = "interval"
    }

    fun setInterval(interval: Long) {
        val editor = sharedPreferences.edit()
        editor.putLong(SHARED_PREFERENCES_INTERVAL, interval)
        editor.apply()
    }

    fun getInterval(): Long {
        return sharedPreferences.getLong(SHARED_PREFERENCES_INTERVAL, 60000)
    }
}