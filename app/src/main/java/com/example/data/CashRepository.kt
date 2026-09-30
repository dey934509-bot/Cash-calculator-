package com.example.data

import kotlinx.coroutines.flow.Flow

class CashRepository(private val dao: CashTallyDao) {
    val allTallies: Flow<List<CashTallyEntity>> = dao.getAllTallies()

    suspend fun insertTally(tally: CashTallyEntity): Long {
        return dao.insertTally(tally)
    }

    suspend fun deleteTally(id: Long) {
        dao.deleteTallyById(id)
    }

    suspend fun clearAll() {
        dao.clearAllTallies()
    }
}
