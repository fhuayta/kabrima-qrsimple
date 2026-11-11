package qrsimple.franz.com.qrsimple.core.util

import android.content.Context
import qrsimple.franz.com.qrsimple.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateTimeFormatter {

    fun formatRelative(context: Context, timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        if (diff < 60_000L) {
            return context.getString(R.string.time_just_now)
        }

        val minutes = diff / 60_000L
        if (minutes < 60L) {
            return context.getString(R.string.time_minutes_ago, minutes)
        }

        val itemCalendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        val nowCalendar = Calendar.getInstance().apply { timeInMillis = now }

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeStr = timeFormat.format(Date(timestamp))

        val isToday = itemCalendar.get(Calendar.YEAR) == nowCalendar.get(Calendar.YEAR) &&
                itemCalendar.get(Calendar.DAY_OF_YEAR) == nowCalendar.get(Calendar.DAY_OF_YEAR)

        if (isToday) {
            return context.getString(R.string.time_today, timeStr)
        }

        val isYesterday = itemCalendar.get(Calendar.YEAR) == nowCalendar.get(Calendar.YEAR) &&
                itemCalendar.get(Calendar.DAY_OF_YEAR) == nowCalendar.get(Calendar.DAY_OF_YEAR) - 1

        if (isYesterday) {
            return context.getString(R.string.time_yesterday, timeStr)
        }

        val dateFormat = SimpleDateFormat("d MMM yyyy, HH:mm", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }

    fun formatRelative(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        if (diff < 60_000L) {
            return "Justo ahora"
        }

        val minutes = diff / 60_000L
        if (minutes < 60L) {
            return "Hace $minutes min"
        }

        val itemCalendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        val nowCalendar = Calendar.getInstance().apply { timeInMillis = now }

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeStr = timeFormat.format(Date(timestamp))

        val isToday = itemCalendar.get(Calendar.YEAR) == nowCalendar.get(Calendar.YEAR) &&
                itemCalendar.get(Calendar.DAY_OF_YEAR) == nowCalendar.get(Calendar.DAY_OF_YEAR)

        if (isToday) {
            return "Hoy, $timeStr"
        }

        val isYesterday = itemCalendar.get(Calendar.YEAR) == nowCalendar.get(Calendar.YEAR) &&
                itemCalendar.get(Calendar.DAY_OF_YEAR) == nowCalendar.get(Calendar.DAY_OF_YEAR) - 1

        if (isYesterday) {
            return "Ayer, $timeStr"
        }

        val dateFormat = SimpleDateFormat("d MMM yyyy, HH:mm", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }
}
