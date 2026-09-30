package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CashTallyDao {
    @Query("SELECT * FROM cash_tallies ORDER BY timestamp DESC")
    fun getAllTallies(): Flow<List<CashTallyEntity>>

    @Query("SELECT * FROM cash_tallies WHERE id = :id LIMIT 1")
    suspend fun getTallyById(id: Long): CashTallyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTally(tally: CashTallyEntity): Long

    @Query("DELETE FROM cash_tallies WHERE id = :id")
    suspend fun deleteTallyById(id: Long)

    @Query("DELETE FROM cash_tallies")
    suspend fun clearAllTallies()
}
