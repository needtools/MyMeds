package com.needtools.mymeds.util

import androidx.annotation.StringRes
import com.needtools.mymeds.R

enum class IntakeEnum(@StringRes val labelResId: Int) {
    BEFORE_MEALS(R.string.intake_before_meals),
    WITH_MEALS(R.string.intake_with_meals),
    AFTER_MEALS(R.string.intake_after_meals),
    IN_THE_MORNING(R.string.intake_morning),
    IN_THE_EVENING(R.string.intake_evening),
    BEFORE_BEDTIME(R.string.intake_before_bedtime),
    OTHER(R.string.other)//!!!!!!!!!!! MUST BE LAST IN THIS ENUM
}