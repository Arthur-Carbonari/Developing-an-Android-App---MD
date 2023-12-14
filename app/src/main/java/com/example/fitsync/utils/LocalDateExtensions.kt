package com.example.fitsync.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDate.getWeekId(): String {
    val weekOfYear = this.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
    return "${this.year}-W${weekOfYear.toString().padStart(2, '0')}"
}
