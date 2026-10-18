package au.edu.jcu.assessment.utilityapp.ui.weather

import au.edu.jcu.assessment.utilityapp.domain.model.TemperatureUnit
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

/** Rounds to a whole degree for at-a-glance reading, e.g. 21.4 → "21°C". */
fun formatTemperature(value: Double, unit: TemperatureUnit): String =
    "${value.roundToInt()}${unit.symbol}"

/** e.g. 2026-10-18T10:15 → "10:15". */
fun LocalDateTime.formatTime(): String = format(timeFormatter)
