package feature.habits.data

import org.jetbrains.compose.resources.StringResource
import tech.mobiledeveloper.jethabit.resources.Res
import tech.mobiledeveloper.jethabit.resources.tracker_measurement_cal
import tech.mobiledeveloper.jethabit.resources.tracker_measurement_cm
import tech.mobiledeveloper.jethabit.resources.tracker_measurement_g
import tech.mobiledeveloper.jethabit.resources.tracker_measurement_kg
import tech.mobiledeveloper.jethabit.resources.tracker_measurement_km

enum class Measurement(val stringRes: StringResource) {
    KILOGRAMS(Res.string.tracker_measurement_kg),
    KILOMETERS(Res.string.tracker_measurement_km),
    GRAMS(Res.string.tracker_measurement_g),
    CALORIES(Res.string.tracker_measurement_cal),
    CENTIMETERS(Res.string.tracker_measurement_cm)
} 