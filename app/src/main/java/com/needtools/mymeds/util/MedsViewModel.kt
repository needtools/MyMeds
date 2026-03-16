package com.needtools.mymeds.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.needtools.mymeds.db.Pill
import com.needtools.mymeds.db.PillDao
import com.needtools.mymeds.db.PillIntake
import com.needtools.mymeds.db.PillIntakeDao
import com.needtools.mymeds.db.PillWithStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine

class MedsViewModel(
    private val pillDao: PillDao,
    private val intakeDao: PillIntakeDao,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _refreshTrigger = MutableStateFlow(System.currentTimeMillis())

    init {
        cleanOldHistory()
        viewModelScope.launch {
            while (true) {
                delay(60000)
                _refreshTrigger.value = System.currentTimeMillis()
            }
        }
    }

    val allPills: Flow<List<Pill>> = pillDao.getAllPills()


    private fun cleanOldHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            val days = settingsManager.getHistoryDays()
            val threshold = System.currentTimeMillis() - (days.toLong() * 24 * 60 * 60 * 1000)
            intakeDao.deleteOldIntakes(threshold)
        }
    }

    fun savePill(pill: Pill) {
        viewModelScope.launch(Dispatchers.IO) {
            val maxNumber = pillDao.getMaxNumber() ?: -1
            pillDao.insert(pill.copy(numberInList = maxNumber + 1))
        }
    }

    fun getIntakeHistory(pillId: Int): Flow<List<PillIntake>> {
        return intakeDao.getIntakesForPill(pillId)
    }

    fun getPillById(pillId: Int): Flow<Pill?> {
        return pillDao.getPillById(pillId)
    }

    fun updatePill(pill: Pill) {
        viewModelScope.launch {
            pillDao.update(pill)
        }
    }

    fun deletePill(pill: Pill) {
        viewModelScope.launch {
            pillDao.delete(pill)
        }
    }

    fun movePillUp(pill: Pill) {
        viewModelScope.launch(Dispatchers.IO) {
            val allPills = pillDao.getAllPillsList()
            val currentIndex = allPills.indexOfFirst { it.id == pill.id }

            if (currentIndex > 0) {
                val currentPillInDb = allPills[currentIndex]
                val prevPill = allPills[currentIndex - 1]

                val currentPos = currentPillInDb.numberInList
                val prevPos = prevPill.numberInList

                val newCurrentPos = if (currentPos == prevPos) currentPos - 1 else prevPos

                pillDao.update(currentPillInDb.copy(numberInList = newCurrentPos))
                pillDao.update(prevPill.copy(numberInList = currentPos))
            }
        }
    }

    fun movePillDown(pill: Pill) {
        viewModelScope.launch {
            val allPills = pillDao.getAllPillsList()
            val currentIndex = allPills.indexOfFirst { it.id == pill.id }

            if (currentIndex != -1 && currentIndex < allPills.size - 1) {
                val nextPill = allPills[currentIndex + 1]

                val currentPos = pill.numberInList
                val nextPos = nextPill.numberInList

                pillDao.update(pill.copy(numberInList = nextPos))
                pillDao.update(nextPill.copy(numberInList = currentPos))
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)

    val pillsWithStatus: Flow<List<PillWithStatus>> = _refreshTrigger.flatMapLatest {tick ->
        pillDao.getAllPills().flatMapLatest { pills ->
            val start = DateTimeUtils.getStartOfDay()
            val end = DateTimeUtils.getEndOfDay()

            val intakeFlows: List<Flow<PillWithStatus>> = pills.map { pill ->
                intakeDao.getIntakesForPillToday(pill.id, start, end).map { intakes ->
                    PillWithStatus(pill, intakes, lastUpdate = tick)
                }
            }

            if (intakeFlows.isEmpty()) flowOf(emptyList())
            else combine(intakeFlows) { it.toList() }
        }
    }

    fun recordIntake(pillId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            intakeDao.insert(PillIntake(pillId = pillId, intakeTime = System.currentTimeMillis()))
        }
    }

}