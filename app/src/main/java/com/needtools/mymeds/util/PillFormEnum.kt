package com.needtools.mymeds.util

import androidx.annotation.StringRes
import com.needtools.mymeds.R

enum class PillFormEnum(@StringRes val resId: Int) {
    TABLET(R.string.form_tablet),
    CAPSULE(R.string.form_capsule),
    SOLUTION(R.string.form_solution),
    INJECTION(R.string.form_injection),
    OINTMENT(R.string.form_ointment),
    SYRUP(R.string.form_syrup),
    SUPPOSITORY(R.string.form_suppository),
    POWDER(R.string.form_powder),
    SPRAY(R.string.form_spray),
    OTHER(R.string.other)//!!!!!!!!!!! MUST BE LAST IN THIS ENUM
}