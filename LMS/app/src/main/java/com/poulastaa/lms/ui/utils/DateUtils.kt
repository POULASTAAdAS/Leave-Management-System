package com.poulastaa.lms.ui.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    @RequiresApi(Build.VERSION_CODES.O)
    fun convertMillisToLocalDate(millis: Long): LocalDate {
        return Instant
            .ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertMillisToLocalDateWithFormatter(
        date: LocalDate,
        dateTimeFormatter: DateTimeFormatter
    ): LocalDate { //Convert the date to a long in millis using a dateformmater
        val dateInMillis = LocalDate.parse(date.format(dateTimeFormatter), dateTimeFormatter)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        //Convert the millis to a localDate object
        return Instant
            .ofEpochMilli(dateInMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun dateToString(date: LocalDate): String {
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
        val dateInMillis = convertMillisToLocalDateWithFormatter(date, dateFormatter)
        return dateFormatter.format(dateInMillis)
    }

    fun calculateExperience(inputDate: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val date = LocalDate.parse(inputDate, formatter)
            val today = LocalDate.now()

            val period = Period.between(date, today)

            "${period.years} Y/${period.months} M/${period.days} D"
        } else {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date: Date = formatter.parse(inputDate) ?: return ""
            val calDate = Calendar.getInstance()
            calDate.time = date

            val today = Calendar.getInstance()

            val years = today.get(Calendar.YEAR) - calDate.get(Calendar.YEAR)
            val months = today.get(Calendar.MONTH) - calDate.get(Calendar.MONTH)

            // Adjust for negative days and months
            var adjustedYears = years
            var adjustedMonths = months

            if (adjustedMonths < 0) {
                adjustedYears -= 1
                adjustedMonths += 12
            }

            "$adjustedYears Y/$adjustedMonths M"
        }
    }
}