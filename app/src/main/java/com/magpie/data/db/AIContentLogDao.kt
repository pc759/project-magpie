package com.magpie.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AIContentLogDao {
    @Insert
    suspend fun insertLog(log: AIContentLog): Long

    @Query("SELECT * FROM ai_content_logs ORDER BY timestamp DESC")
    suspend fun getAllLogs(): List<AIContentLog>

    @Query("SELECT * FROM ai_content_logs WHERE location = :location ORDER BY timestamp DESC")
    suspend fun getLogsByLocation(location: String): List<AIContentLog>

    @Query("SELECT * FROM ai_content_logs WHERE userReported = 1 ORDER BY timestamp DESC")
    suspend fun getReportedContent(): List<AIContentLog>

    @Update
    suspend fun updateLog(log: AIContentLog)
}
